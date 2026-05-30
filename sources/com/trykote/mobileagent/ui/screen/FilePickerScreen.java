package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.net.InlineImageCache;
import com.trykote.mobileagent.protocol.xmpp.XmppHttpUpload;
import com.trykote.mobileagent.protocol.xmpp.XmppProtocol;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.util.IOUtils;
import com.trykote.mobileagent.util.RemoteLogger;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Browses the device filesystem via JSR-75 FileConnection.
 *
 * Two modes:
 *   currentUrl == null  -> root list (FileSystemRegistry.listRoots)
 *   currentUrl != null  -> directory contents; ends with '/'
 *
 * On file select: schedules an AsyncTask that reads the file and hands it
 * to XmppHttpUpload.requestUpload.
 */
public final class FilePickerScreen extends ScreenView {

    private static final String FILE_PREFIX = "file:///";
    private static final String UP_LABEL = "..";
    private static final int MAX_INLINE_FILE_BYTES = 512 * 1024;

    private static XmppProtocol pendingAccount;
    private static String pendingTargetJid;

    private String currentUrl;

    public FilePickerScreen() {
        super(ScreenManager.TYPE_FULLSCREEN, ScreenId.UNUSED_135);
    }

    public FilePickerScreen(int screenId) {
        super(ScreenManager.TYPE_FULLSCREEN, screenId);
    }

    /**
     * Stage an upload target. Caller is responsible for actually opening
     * the screen (return ScreenId.UNUSED_135 from a menu handler). Returns
     * true if state was set; false if upload service isn't usable.
     */
    public static boolean prepare(XmppProtocol account, String targetJid) {
        if (account == null || account.getHttpUpload() == null) {
            NotificationHelper.showNotification("HTTP upload не готов");
            return false;
        }
        if (account.getHttpUpload().getState() != XmppHttpUpload.STATE_READY) {
            NotificationHelper.showNotification("Сервис загрузки недоступен");
            return false;
        }
        pendingAccount = account;
        pendingTargetJid = targetJid;
        RemoteLogger.info("PICK", "prepare: account=" + account.login + " target=" + targetJid);
        return true;
    }

    public void buildContent() {
        if (currentUrl == null) {
            captureCurrentContext();
        }
        configureHeader(0, currentUrl == null ? "Выберите файл" : currentUrl);
        if (currentUrl == null) {
            populateRoots();
        } else {
            addUpEntry();
            populateCurrentDir();
        }
        configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
    }

    /** Resolve pending account/target from the current chat context if not already set. */
    private static void captureCurrentContext() {
        if (pendingAccount != null && pendingTargetJid != null) {
            return;
        }
        Contact c = AppState.getCurrentContact();
        if (c == null || !(c.account instanceof XmppProtocol)) {
            RemoteLogger.warn("PICK", "captureCurrentContext: no XMPP contact");
            return;
        }
        XmppProtocol acc = (XmppProtocol) c.account;
        XmppHttpUpload upl = acc.getHttpUpload();
        if (upl == null || upl.getState() != XmppHttpUpload.STATE_READY) {
            RemoteLogger.warn("PICK", "captureCurrentContext: upload not ready, state="
                + (upl == null ? "null" : String.valueOf(upl.getState())));
            return;
        }
        pendingAccount = acc;
        pendingTargetJid = c.getIdentifier();
        RemoteLogger.info("PICK", "context captured: account=" + acc.login + " target=" + pendingTargetJid);
    }

    private void populateRoots() {
        try {
            Enumeration roots = FileSystemRegistry.listRoots();
            int count = 0;
            while (roots.hasMoreElements()) {
                String root = (String) roots.nextElement();
                String absolute = FILE_PREFIX + root;
                addEntry(root, absolute);
                count++;
            }
            RemoteLogger.info("PICK", "roots: " + count);
        } catch (Throwable t) {
            RemoteLogger.error("PICK", "listRoots failed", t);
        }
    }

    private void addUpEntry() {
        addEntry(UP_LABEL, UP_LABEL);
    }

    private void populateCurrentDir() {
        FileConnection fc = null;
        try {
            fc = (FileConnection) Connector.open(currentUrl, Connector.READ);
            Enumeration entries = fc.list();
            Vector dirs = new Vector();
            Vector files = new Vector();
            while (entries.hasMoreElements()) {
                String name = (String) entries.nextElement();
                if (name.endsWith("/")) {
                    dirs.addElement(name);
                } else {
                    files.addElement(name);
                }
            }
            addEntries(dirs, true);
            addEntries(files, false);
            RemoteLogger.info("PICK", "dir " + currentUrl + ": " + dirs.size() + "d / " + files.size() + "f");
        } catch (Throwable t) {
            RemoteLogger.error("PICK", "list " + currentUrl + " failed", t);
            NotificationHelper.showNotification("Не удалось открыть папку");
        } finally {
            closeQuiet(fc);
        }
    }

    private void addEntries(Vector names, boolean asDir) {
        for (int i = 0; i < names.size(); i++) {
            String name = (String) names.elementAt(i);
            String label = asDir ? "[" + name + "]" : name;
            addEntry(label, currentUrl + name);
        }
    }

    private void addEntry(String label, String data) {
        MenuItem item = MenuItem.createWithWidth(label, 0)
            .setIcon(MenuItem.NO_ICON)
            .setLabel(label);
        item.data = data;
        addItem(item);
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        if (data == null) {
            return ScreenId.NONE;
        }
        String value = (String) data;
        if (UP_LABEL.equals(value)) {
            navigateUp();
            rebuild();
            return ScreenId.NONE;
        }
        if (value.endsWith("/")) {
            currentUrl = value;
            rebuild();
            return ScreenId.NONE;
        }
        startUploadForFile(value);
        closePickerAndParentMenu();
        return -1;
    }

    public int onSelect(MenuItem item, String title, int selectedOption, Object data, Object headerData) {
        return onItemSelected(item, title, selectedOption, data);
    }

    /**
     * Closes the file picker plus the menu it was opened from, so the upload status
     * bar isn't overlaid by the now-irrelevant action menu.
     */
    private static void closePickerAndParentMenu() {
        ScreenBuilder.onScreenClosed();
        ListView next = ScreenManager.getCurrentScreen();
        if (next == null) {
            return;
        }
        int type = next.screenType;
        if (type == ScreenManager.TYPE_DIALOG_CENTER
                || type == ScreenManager.TYPE_DIALOG_BOTTOM
                || type == ScreenManager.TYPE_DIALOG_CORNER
                || type == ScreenManager.TYPE_DIALOG_LOW
                || type == ScreenManager.TYPE_POPUP) {
            ScreenBuilder.onScreenClosed();
        }
    }

    private void navigateUp() {
        if (currentUrl == null) {
            return;
        }
        String trimmed = currentUrl.substring(0, currentUrl.length() - 1);
        int lastSlash = trimmed.lastIndexOf('/');
        if (lastSlash < FILE_PREFIX.length() - 1) {
            currentUrl = null;
        } else {
            currentUrl = trimmed.substring(0, lastSlash + 1);
        }
    }

    private void rebuild() {
        this.menuItems.removeAllElements();
        this.selectedIndex = -1;
        this.scrollOffset = 0;
        buildContent();
    }

    private void startUploadForFile(String fileUrl) {
        XmppProtocol account = pendingAccount;
        String target = pendingTargetJid;
        pendingAccount = null;
        pendingTargetJid = null;
        if (account == null || target == null) {
            RemoteLogger.warn("PICK", "no pending account/target on file select");
            return;
        }
        new AsyncTask(AsyncTaskId.FILE_PICKER_READ, new Object[]{account, target, fileUrl});
    }

    /** Runs on AsyncTask thread. Reads file from FS and triggers slot request. */
    public static void readAndUpload(Object[] args) {
        XmppProtocol account = (XmppProtocol) args[0];
        String targetJid = (String) args[1];
        String fileUrl = (String) args[2];

        FileConnection fc = null;
        InputStream in = null;
        try {
            fc = (FileConnection) Connector.open(fileUrl, Connector.READ);
            long size = fc.fileSize();
            if (size > MAX_INLINE_FILE_BYTES) {
                RemoteLogger.warn("PICK", "file too big: " + size + " > " + MAX_INLINE_FILE_BYTES);
                NotificationHelper.showNotification("Файл слишком большой");
                return;
            }
            long serverMax = account.getHttpUpload().getMaxFileSize();
            if (serverMax > 0 && size > serverMax) {
                RemoteLogger.warn("PICK", "file > server max: " + size + " > " + serverMax);
                NotificationHelper.showNotification("Сервер не принимает такой размер");
                return;
            }
            byte[] data = new byte[(int) size];
            in = fc.openInputStream();
            int total = 0;
            while (total < data.length) {
                int read = in.read(data, total, data.length - total);
                if (read < 0) break;
                total += read;
            }
            if (total < data.length) {
                RemoteLogger.warn("PICK", "short read: " + total + " of " + data.length);
                return;
            }
            String name = fc.getName();
            String contentType = guessContentType(name);
            RemoteLogger.info("PICK", "read " + total + " bytes from " + name + " ct=" + contentType);
            account.getHttpUpload().requestUpload(targetJid, name, data, contentType);
        } catch (OutOfMemoryError oom) {
            int freed = InlineImageCache.clearCache();
            RemoteLogger.error("PICK", "OOM reading " + fileUrl + ", evicted " + freed + " cached: " + oom);
            NotificationHelper.showNotification(freed > 0
                    ? "Недостаточно памяти, кеш очищен — попробуйте снова"
                    : "Файл не помещается в память");
        } catch (IOException ioe) {
            RemoteLogger.error("PICK", "IO error reading " + fileUrl + ": " + ioe);
            NotificationHelper.showNotification("Не удалось прочитать файл");
        } catch (SecurityException se) {
            RemoteLogger.error("PICK", "security error " + fileUrl + ": " + se);
            NotificationHelper.showNotification("Нет доступа к файлу");
        } catch (Throwable t) {
            RemoteLogger.error("PICK", "readAndUpload failed", t);
            NotificationHelper.showNotification("Ошибка чтения файла");
        } finally {
            IOUtils.closeInput(in);
            closeQuiet(fc);
        }
    }

    private static void closeQuiet(FileConnection fc) {
        if (fc != null) {
            try {
                fc.close();
            } catch (Throwable ignored) {
            }
        }
    }

    static String guessContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".txt")) return "text/plain";
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".mp3")) return "audio/mpeg";
        if (lower.endsWith(".amr")) return "audio/amr";
        if (lower.endsWith(".3gp")) return "video/3gpp";
        return "application/octet-stream";
    }
}
