package com.trykote.mobileagent.protocol.xmpp;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.net.HttpsClient;
import com.trykote.mobileagent.net.InlineImageCache;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;
import com.trykote.mobileagent.util.XmlElement;

import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.util.Vector;

/**
 * XEP-0363 HTTP File Upload client.
 *
 * Lifecycle:
 *   IDLE -> start() -> DISCO_ITEMS_SENT
 *        -> disco#items result -> probe each service via disco#info
 *        -> READY (serviceJid + maxFileSize known) | UNAVAILABLE
 *
 * Upload:
 *   READY -> requestUpload() -> SLOT_REQUESTED
 *         -> slot result -> UPLOADING (AsyncTask does HTTPS PUT)
 *         -> back to READY; <message> with OOB URL sent
 */
public final class XmppHttpUpload implements HttpsClient.ProgressListener {

    private static final long PROGRESS_REPAINT_THROTTLE_MS = 250L;

    public static final int STATE_IDLE = 0;
    public static final int STATE_DISCO_ITEMS_SENT = 1;
    public static final int STATE_DISCO_INFO_SENT = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_UNAVAILABLE = 4;
    public static final int STATE_SLOT_REQUESTED = 5;
    public static final int STATE_UPLOADING = 6;

    public static final String NS_DISCO_ITEMS = "http://jabber.org/protocol/disco#items";
    public static final String NS_DISCO_INFO = "http://jabber.org/protocol/disco#info";
    public static final String NS_HTTP_UPLOAD = "urn:xmpp:http:upload:0";
    public static final String NS_DATA_FORM = "jabber:x:data";
    public static final String NS_OOB = "jabber:x:oob";

    private static final String ID_DISCO_ITEMS = "upl_items";
    private static final String ID_DISCO_INFO_PREFIX = "upl_info_";
    private static final String ID_SLOT = "upl_slot";

    private final XmppProtocol owner;
    private int state = STATE_IDLE;
    private String serviceJid;
    private long maxFileSize = -1;
    private Vector pendingInfoJids;
    private int currentInfoIndex;
    private String currentInfoJid;

    private String pendingPutUrl;
    private String pendingGetUrl;
    private String[] pendingPutHeaders;
    private byte[] pendingBody;
    private String pendingContentType;
    private String pendingTargetJid;
    private String pendingFilename;

    private volatile int uploadBytesSent;
    private volatile int uploadBytesTotal;
    private volatile boolean uploadActive;
    private long lastProgressRepaintMs;

    public XmppHttpUpload(XmppProtocol owner) {
        this.owner = owner;
    }

    public int getState() {
        return state;
    }

    public String getServiceJid() {
        return serviceJid;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public int getUploadBytesSent() {
        return uploadBytesSent;
    }

    public int getUploadBytesTotal() {
        return uploadBytesTotal;
    }

    public boolean isUploadInProgress() {
        return uploadActive;
    }

    public String getUploadStatusText() {
        if (!uploadActive) {
            return null;
        }
        int total = uploadBytesTotal;
        int sent = uploadBytesSent;
        if (total <= 0) {
            return "Запрос загрузки...";
        }
        if (sent <= 0) {
            return "Соединение...";
        }
        if (sent >= total) {
            return "Завершение...";
        }
        int pct = (int) ((long) sent * 100 / total);
        return "Отправка " + pct + "%";
    }

    public void onUploadProgress(int sent, int total) {
        uploadBytesSent = sent;
        uploadBytesTotal = total;
        long now = System.currentTimeMillis();
        if (sent == total || now - lastProgressRepaintMs >= PROGRESS_REPAINT_THROTTLE_MS) {
            lastProgressRepaintMs = now;
            AppController.needsRepaint = true;
        }
    }

    /** Issued once per session, after roster is received. */
    public void start(String serverDomain) {
        if (state != STATE_IDLE) {
            return;
        }
        RemoteLogger.info("UPL", "discovery: querying disco#items on " + serverDomain);
        XmlElement iq = newIq("get", serverDomain, ID_DISCO_ITEMS);
        XmlElement query = iq.addTextChild("query", null);
        query.setStringAttr("xmlns", NS_DISCO_ITEMS);
        owner.sendRaw(iq);
        state = STATE_DISCO_ITEMS_SENT;
    }

    /** Called from XmppProtocol.handleIq for every incoming iq. Returns true if consumed. */
    public boolean handleIq(XmlElement iq) {
        String id = iq.getIntAttribute(PackedStringKeys.ATTR_ID);
        if (id == null) {
            return false;
        }
        String type = iq.getNameAttr();
        if (ID_DISCO_ITEMS.equals(id)) {
            handleDiscoItemsResult(iq, type);
            return true;
        }
        if (id.startsWith(ID_DISCO_INFO_PREFIX)) {
            handleDiscoInfoResult(iq, type);
            return true;
        }
        if (ID_SLOT.equals(id)) {
            handleSlotResult(iq, type);
            return true;
        }
        return false;
    }

    private void handleDiscoItemsResult(XmlElement iq, String type) {
        if (!"result".equals(type)) {
            RemoteLogger.warn("UPL", "disco#items failed: type=" + type);
            state = STATE_UNAVAILABLE;
            return;
        }
        XmlElement query = findChildByXmlns(iq, "query", NS_DISCO_ITEMS);
        if (query == null) {
            state = STATE_UNAVAILABLE;
            return;
        }
        pendingInfoJids = new Vector();
        for (int i = 0; i < Utils.vectorSize(query.children); i++) {
            XmlElement child = query.getChildAt(i);
            if ("item".equals(child.tagName)) {
                String jid = child.getStringAttr("jid");
                if (jid != null) {
                    pendingInfoJids.addElement(jid);
                }
            }
        }
        RemoteLogger.info("UPL", "disco#items: " + pendingInfoJids.size() + " services");
        currentInfoIndex = 0;
        probeNextService();
    }

    private void probeNextService() {
        if (pendingInfoJids == null || currentInfoIndex >= pendingInfoJids.size()) {
            RemoteLogger.info("UPL", "no upload service found among " + (pendingInfoJids == null ? 0 : pendingInfoJids.size()) + " items");
            state = STATE_UNAVAILABLE;
            pendingInfoJids = null;
            return;
        }
        currentInfoJid = (String) pendingInfoJids.elementAt(currentInfoIndex);
        String id = ID_DISCO_INFO_PREFIX + currentInfoIndex;
        RemoteLogger.debug("UPL", "disco#info: probing " + currentInfoJid + " id=" + id);
        XmlElement iq = newIq("get", currentInfoJid, id);
        XmlElement query = iq.addTextChild("query", null);
        query.setStringAttr("xmlns", NS_DISCO_INFO);
        owner.sendRaw(iq);
        state = STATE_DISCO_INFO_SENT;
    }

    private void handleDiscoInfoResult(XmlElement iq, String type) {
        if (!"result".equals(type)) {
            RemoteLogger.debug("UPL", "disco#info error on " + currentInfoJid + " type=" + type);
            currentInfoIndex++;
            probeNextService();
            return;
        }
        XmlElement query = findChildByXmlns(iq, "query", NS_DISCO_INFO);
        if (query == null) {
            currentInfoIndex++;
            probeNextService();
            return;
        }
        boolean supportsUpload = false;
        for (int i = 0; i < Utils.vectorSize(query.children); i++) {
            XmlElement child = query.getChildAt(i);
            if ("feature".equals(child.tagName) && NS_HTTP_UPLOAD.equals(child.getStringAttr("var"))) {
                supportsUpload = true;
                break;
            }
        }
        if (!supportsUpload) {
            currentInfoIndex++;
            probeNextService();
            return;
        }
        long max = extractMaxFileSize(query);
        serviceJid = currentInfoJid;
        maxFileSize = max;
        state = STATE_READY;
        pendingInfoJids = null;
        currentInfoJid = null;
        RemoteLogger.info("UPL", "READY: service=" + serviceJid + " maxFileSize=" + maxFileSize);
    }

    private long extractMaxFileSize(XmlElement query) {
        // Look for: <x xmlns='jabber:x:data'><field var='max-file-size'><value>N</value></field></x>
        for (int i = 0; i < Utils.vectorSize(query.children); i++) {
            XmlElement child = query.getChildAt(i);
            if (!"x".equals(child.tagName)) {
                continue;
            }
            if (!NS_DATA_FORM.equals(child.getStringAttr("xmlns"))) {
                continue;
            }
            for (int j = 0; j < Utils.vectorSize(child.children); j++) {
                XmlElement field = child.getChildAt(j);
                if (!"field".equals(field.tagName)) {
                    continue;
                }
                if (!"max-file-size".equals(field.getStringAttr("var"))) {
                    continue;
                }
                XmlElement value = field.findByRawName("value");
                if (value != null && value.textContent != null) {
                    try {
                        return Long.parseLong(StringUtils.fromBuffer(value.textContent));
                    } catch (Throwable t) {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Initiates upload. Caller must check state == STATE_READY and size <= maxFileSize.
     * On success, sends a chat <message> with OOB URL to targetJid.
     */
    public void requestUpload(String targetJid, String filename, byte[] data, String contentType) {
        if (state != STATE_READY) {
            RemoteLogger.warn("UPL", "requestUpload called in state=" + state);
            return;
        }
        if (maxFileSize > 0 && data.length > maxFileSize) {
            RemoteLogger.warn("UPL", "file too large: " + data.length + " > " + maxFileSize);
            return;
        }
        pendingTargetJid = targetJid;
        pendingFilename = filename;
        pendingBody = data;
        pendingContentType = contentType;

        RemoteLogger.info("UPL", "slot request: filename=" + filename + " size=" + data.length + " ct=" + contentType);
        XmlElement iq = newIq("get", serviceJid, ID_SLOT);
        XmlElement request = iq.addTextChild("request", null);
        request.setStringAttr("xmlns", NS_HTTP_UPLOAD);
        request.setStringAttr("filename", filename);
        request.setStringAttr("size", String.valueOf(data.length));
        request.setStringAttr("content-type", contentType);
        owner.sendRaw(iq);
        state = STATE_SLOT_REQUESTED;
        uploadBytesSent = 0;
        uploadBytesTotal = 0;
        uploadActive = true;
        AppController.needsRepaint = true;
    }

    private void handleSlotResult(XmlElement iq, String type) {
        if (!"result".equals(type)) {
            RemoteLogger.warn("UPL", "slot request failed: type=" + type);
            EventDispatcher.postNotification("Сервер отклонил загрузку");
            clearPending();
            state = STATE_READY;
            finishUpload();
            return;
        }
        XmlElement slot = findChildByXmlns(iq, "slot", NS_HTTP_UPLOAD);
        if (slot == null) {
            EventDispatcher.postNotification("Сервер не выдал слот загрузки");
            clearPending();
            state = STATE_READY;
            finishUpload();
            return;
        }
        XmlElement put = slot.findByRawName("put");
        XmlElement get = slot.findByRawName("get");
        if (put == null || get == null) {
            EventDispatcher.postNotification("Некорректный ответ сервера");
            clearPending();
            state = STATE_READY;
            finishUpload();
            return;
        }
        pendingPutUrl = put.getStringAttr("url");
        pendingGetUrl = get.getStringAttr("url");
        pendingPutHeaders = collectHeaders(put);
        RemoteLogger.info("UPL", "slot OK: put=" + truncate(pendingPutUrl) + " get=" + truncate(pendingGetUrl) + " headers=" + (pendingPutHeaders == null ? 0 : pendingPutHeaders.length / 2));

        state = STATE_UPLOADING;
        new AsyncTask(AsyncTaskId.XMPP_HTTP_UPLOAD, this);
    }

    private String[] collectHeaders(XmlElement put) {
        // Per XEP-0363: only Authorization, Cookie, Expires are allowed.
        Vector pairs = new Vector();
        for (int i = 0; i < Utils.vectorSize(put.children); i++) {
            XmlElement child = put.getChildAt(i);
            if (!"header".equals(child.tagName)) {
                continue;
            }
            String name = child.getStringAttr("name");
            if (name == null) {
                continue;
            }
            if (!"Authorization".equalsIgnoreCase(name)
                    && !"Cookie".equalsIgnoreCase(name)
                    && !"Expires".equalsIgnoreCase(name)) {
                continue;
            }
            String value = StringUtils.fromBuffer(child.textContent);
            if (value == null) {
                value = "";
            }
            // Newlines must be stripped per spec.
            value = stripNewlines(value);
            pairs.addElement(name);
            pairs.addElement(value);
        }
        if (pairs.size() == 0) {
            return null;
        }
        String[] arr = new String[pairs.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (String) pairs.elementAt(i);
        }
        return arr;
    }

    private String stripNewlines(String value) {
        StringBuffer sb = new StringBuffer(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c != '\r' && c != '\n') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Runs on AsyncTask thread. Performs HTTPS PUT and, on success, sends the OOB message. */
    public void performUpload() {
        String url = pendingPutUrl;
        byte[] body = pendingBody;
        String contentType = pendingContentType;
        String[] headers = pendingPutHeaders;
        String getUrl = pendingGetUrl;
        String target = pendingTargetJid;

        uploadBytesSent = 0;
        uploadBytesTotal = body.length;
        lastProgressRepaintMs = 0L;
        AppController.needsRepaint = true;

        HttpsClient http = null;
        int responseCode = -1;
        Throwable error = null;
        try {
            RemoteLogger.info("UPL", "PUT " + truncate(url) + " body=" + body.length);
            http = new HttpsClient(url, "PUT", body, contentType, headers, this);
            responseCode = http.getResponseCode();
        } catch (Throwable t) {
            error = t;
            RemoteLogger.error("UPL", "PUT failed", t);
        } finally {
            if (http != null) {
                http.close();
            }
        }
        clearPending();
        state = STATE_READY;
        finishUpload();

        if (error == null && responseCode >= 200 && responseCode < 300) {
            RemoteLogger.info("UPL", "PUT OK code=" + responseCode + ", sending OOB message");
            tryPrecacheImage(body, contentType, getUrl);
            sendOobMessage(target, getUrl);
        } else {
            RemoteLogger.warn("UPL", "upload failed code=" + responseCode + " err=" + error);
            EventDispatcher.postNotification(describeUploadFailure(responseCode, error));
        }
    }

    private String describeUploadFailure(int responseCode, Throwable error) {
        if (error instanceof OutOfMemoryError) {
            int freed = InlineImageCache.clearCache();
            return freed > 0
                    ? "Недостаточно памяти, кеш очищен — попробуйте снова"
                    : "Недостаточно памяти для загрузки";
        }
        if (error instanceof IOException) {
            return "Нет связи с сервером";
        }
        if (error != null) {
            return "Ошибка загрузки файла";
        }
        if (responseCode > 0) {
            return "Сервер вернул HTTP " + responseCode;
        }
        return "Сервер недоступен";
    }

    private void tryPrecacheImage(byte[] data, String contentType, String url) {
        if (contentType == null || !contentType.startsWith("image/")) {
            return;
        }
        if (!InlineImageCache.isImageUrl(url)) {
            return;
        }
        try {
            Image image = Image.createImage(data, 0, data.length);
            image = InlineImageCache.scaleToFit(image);
            InlineImageCache.putImage(url, image);
            RemoteLogger.info("UPL", "precached image " + image.getWidth() + "x" + image.getHeight());
        } catch (OutOfMemoryError oom) {
            int freed = InlineImageCache.clearCache();
            RemoteLogger.warn("UPL", "precache OOM, evicted " + freed + " cached images");
        } catch (Throwable t) {
            RemoteLogger.warn("UPL", "precache failed: " + t);
        }
    }

    private void sendOobMessage(String targetJid, String url) {
        XmlElement message = XmlElement.createFromState(PackedStringKeys.TAG_MESSAGE);
        message.setAttrValue(PackedStringKeys.ATTR_TO, targetJid);
        message.addNameAttr(PackedStringKeys.XMPP_TYPE_CHAT);
        message.addChild(XmlElement.createFromState(PackedStringKeys.TAG_BODY).appendText((Object) url));
        XmlElement oob = message.addTextChild("x", null);
        oob.setStringAttr("xmlns", NS_OOB);
        oob.addTextChild("url", url);
        owner.sendRaw(message);

        Contact contact = owner.getContact((Object) targetJid);
        if (contact != null) {
            contact.appendOutgoingMessage(url);
        } else {
            RemoteLogger.warn("UPL", "no local contact for " + targetJid + " — message not appended");
        }
    }

    private void finishUpload() {
        uploadBytesSent = 0;
        uploadBytesTotal = 0;
        uploadActive = false;
        AppController.needsRepaint = true;
    }

    private void clearPending() {
        pendingBody = null;
        pendingContentType = null;
        pendingFilename = null;
        pendingPutUrl = null;
        pendingGetUrl = null;
        pendingPutHeaders = null;
        pendingTargetJid = null;
    }

    // --- Helpers ---

    private XmlElement newIq(String type, String to, String id) {
        XmlElement iq = XmlElement.createFromState(PackedStringKeys.XMPP_IQ);
        iq.setAttrValue(PackedStringKeys.ATTR_TO, to);
        iq.setAttrValue(PackedStringKeys.ATTR_ID, id);
        iq.setStringAttr(StringPool.get(PackedStringKeys.ATTR_TYPE), type);
        return iq;
    }

    private XmlElement findChildByXmlns(XmlElement parent, String tag, String ns) {
        for (int i = 0; i < Utils.vectorSize(parent.children); i++) {
            XmlElement child = parent.getChildAt(i);
            if (tag.equals(child.tagName) && ns.equals(child.getStringAttr("xmlns"))) {
                return child;
            }
        }
        return null;
    }

    private static String truncate(String s) {
        if (s == null) return "null";
        return s.length() > 80 ? s.substring(0, 80) + "..." : s;
    }
}
