package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Vector;

public final class MailHelper {

    public static final void writeAddressPairs(Vector vector, ByteBuffer buf) {
        int count = Utils.vectorSize(vector);
        buf.writeIntLE(count);
        for (int i = 0; i < count; i++) {
            String[] strArr = (String[]) vector.elementAt(i);
            buf.writeStringUTF16(strArr[0]).writeStringUTF16(strArr[1]);
        }
    }

    public static final Vector readAddressPairs(ByteBuffer buf) {
        Vector results = ObjectPool.newVector();
        int resultCode = buf.readInt();
        while (true) {
            resultCode--;
            if (resultCode < 0) {
                return results;
            }
            results.addElement(new String[]{buf.readUTF8Str((String) null), buf.readUTF8Str((String) null)});
        }
    }

    public static final Vector copyAddressList(Vector vector) {
        Vector results = ObjectPool.newVector();
        for (int i = 0; i < Utils.vectorSize(vector); i++) {
            results.addElement(vector.elementAt(i));
        }
        return results;
    }

    public static final Vector mergeAddressLists(Vector vector, Vector vector2) {
        if (vector2 != null) {
            Enumeration elements = vector2.elements();
            while (elements.hasMoreElements()) {
                addUniqueAddress(vector, (String[]) elements.nextElement());
            }
        }
        return vector;
    }

    public static final Vector getFirstAddress(Vector vector) {
        Vector results = ObjectPool.newVector();
        if (Utils.vectorSize(vector) > 0) {
            results.addElement(vector.elementAt(0));
        }
        return results;
    }

    public static final Vector addUniqueAddress(Vector vector, String[] strArr) {
        String str = strArr[0];
        if (str.indexOf(64) != -1) {
            boolean z = false;
            int count = Utils.vectorSize(vector);
            while (true) {
                count--;
                if (count < 0) {
                    break;
                }
                if (StringUtils.equals(str, ((String[]) vector.elementAt(count))[0])) {
                    z = true;
                }
            }
            if (!z) {
                vector.addElement(strArr);
            }
        }
        return vector;
    }

    public static final Vector parseAddressHeader(String str, String str2) {
        Vector results = ObjectPool.newVector();
        Vector decodedNames = splitCommaSeparated(Conversation.decodeHtmlSpecial(str2));
        Vector rawAddresses = splitCommaSeparated(str);
        for (int i = 0; i < Utils.vectorSize(rawAddresses); i++) {
            addUniqueAddress(results, createAddressPair((String) rawAddresses.elementAt(i), (String) decodedNames.elementAt(i)));
        }
        ObjectPool.releaseVector(decodedNames);
        ObjectPool.releaseVector(rawAddresses);
        return results;
    }

    public static final String[] getFirstRecipient(Vector vector) {
        if (Utils.vectorSize(vector) > 0) {
            return (String[]) vector.elementAt(0);
        }
        return null;
    }

    private static final Vector splitCommaSeparated(String str) {
        Vector results = ObjectPool.newVector();
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str.length();
        boolean z = true;
        int i = 0;
        while (i <= length) {
            char ch = i < length ? str.charAt(i) : ',';
            if (!z) {
                z = true;
            } else if (ch == ',') {
                results.addElement(ObjectPool.toString(sb, false));
                z = false;
            } else {
                sb.append(ch);
            }
            i++;
        }
        ObjectPool.toStringAndRelease(sb);
        return results;
    }

    public static final Vector parseRecipientList(String str) {
        Vector results = ObjectPool.newVector();
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str.length();
        int i = 0;
        while (i <= length) {
            char ch = i == length ? ';' : str.charAt(i);
            char c = ch;
            if (ch != ';' && c != ',' && c != ' ') {
                sb.append(c);
            } else if (sb.length() > 0) {
                String address = ObjectPool.toString(sb, false);
                results.addElement(new String[]{address, address});
            }
            i++;
        }
        ObjectPool.toStringAndRelease(sb);
        return results;
    }

    public static final void setMailAction(int i, int i2) {
        Storage.state().setInt(RuntimeKeys.INT_XMPP_ACTION, i);
        Storage.state().setInt(RuntimeKeys.INT_XMPP_ACTION_TYPE, i2);
    }

    public static final int processMailResponse() {
        String bodyText;
        Object[] asyncResult = ApiClient.pollAsyncResult();
        if (asyncResult == null) {
            return handleMailRedirect();
        }
        Object[] responseData = ApiClient.getAsyncResult(asyncResult);
        if (responseData == null) {
            return 0;
        }
        int validationResult = ApiClient.validateJsonResponse(responseData);
        if (validationResult != 0) {
            return validationResult;
        }
        String messageId = Storage.state().getString(RuntimeKeys.SLOT_MESSAGE_ID);
        ChatRoom chatRoom = ((MrimAccount) Storage.state().getAccount()).chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_CHATROOM_ID));
        Message message = chatRoom.getMessage(messageId);
        boolean wasUnread = message.hasFlag(4);
        Object jsonPayload = ApiClient.getJsonPayload();
        Object attachmentsList = JsonParser.getValueByInt(jsonPayload, 722874);
        int size = ((Vector) attachmentsList).size();
        int i = size;
        Object[] objArr = new Object[size];
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            Object attachmentObj = JsonParser.getVectorElement(attachmentsList, i);
            objArr[i] = new String[]{JsonParser.getStringByInt(attachmentObj, 1227), JsonParser.getStringByInt(attachmentObj, 1228), JsonParser.getStringByInt(attachmentObj, 1229), JsonParser.getStringByInt(attachmentObj, 1230), JsonParser.getStringByInt(attachmentObj, 1231), JsonParser.getStringByInt(attachmentObj, 1232)};
        }
        message.attachments = objArr;
        String str = (String) JsonParser.getValueByInt(jsonPayload, 919493);
        if (str == null) {
            bodyText = Storage.emptyStr;
        } else {
            StringBuffer sb = ObjectPool.newStringBuffer();
            int length = str.length();
            int i2 = 0;
            while (i2 < length) {
                char ch = str.charAt(i2);
                sb.append(ch);
                if (ch == ' ') {
                    while (i2 + 1 < length && str.charAt(i2 + 1) == ' ') {
                        i2++;
                    }
                }
                if (ch == '\n') {
                    while (i2 + 1 < length && str.charAt(i2 + 1) == '\n') {
                        i2++;
                    }
                }
                i2++;
            }
            bodyText = ObjectPool.toStringAndRelease(sb);
        }
        message.body = bodyText;
        if (wasUnread) {
            message.setFlag(4, false);
            chatRoom.decrementUnread();
        }
        return handleMailRedirect();
    }

    private static final int handleMailRedirect() {
        int action = Storage.state().getInt(RuntimeKeys.INT_XMPP_ACTION);
        if (action == 54) {
            Message message = ((MrimAccount) Storage.state().getAccount()).chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_CHATROOM_ID)).getMessage(Storage.state().getString(RuntimeKeys.SLOT_MESSAGE_ID));
            Vector toList = message.getToList();
            Vector ccList = message.getCcList();
            getFirstRecipient(toList);
            String subject = message.getSubject();
            String str = message.body;
            String replyPrefix = Storage.resources().getString(PackedStringKeys.PREFIX_REPLY);
            String fwdPrefix = Storage.resources().getString(PackedStringKeys.PREFIX_FORWARD);
            String string = new StringBuffer().append(Storage.resources().getString(StringResKeys.STR_SEARCH_QUERY_PREFIX)).append(Utils.quoteText(str)).toString();
            switch (Storage.state().getInt(RuntimeKeys.INT_XMPP_ACTION_TYPE)) {
                case 0:
                    composeEmail(getFirstAddress(toList), new StringBuffer().append(replyPrefix).append(subject).toString(), string);
                    break;
                case 1:
                    composeEmail(mergeAddressLists(copyAddressList(toList), ccList), new StringBuffer().append(replyPrefix).append(subject).toString(), string);
                    break;
                case 2:
                    composeEmail(ObjectPool.newVector(), new StringBuffer().append(fwdPrefix).append(subject).toString(), string);
                    break;
                case 3:
                    composeEmail(copyAddressList(ccList), subject, str);
                    break;
            }
        }
        return action;
    }

    /* renamed from: a */
    public static final int handleMailMenuAction(String str, int i) {
        String messageId = Storage.state().getString(RuntimeKeys.SLOT_MESSAGE_ID);
        wrapInVector(messageId);
        int chatRoomId = Storage.state().getInt(ChatKeys.INT_CHATROOM_ID);
        MrimAccount account = (MrimAccount) Storage.state().getAccount();
        Message message = account.chatRoomManager.findById(chatRoomId).getMessage(messageId);
        String subject = message.getSubject();
        Vector toList = message.getToList();
        Vector ccList = message.getCcList();
        getFirstRecipient(toList);
        boolean needsAuth = Storage.state().getBool(SettingsKeys.SETTING_AUTH_REQUIRED);
        String replyPrefix = Storage.resources().getString(PackedStringKeys.PREFIX_REPLY);
        String forwardPrefix = Storage.resources().getString(PackedStringKeys.PREFIX_FORWARD);
        String body = Storage.emptyStr;
        if (i == 48) {
            ScreenBuilder.onScreenClosed();
            ScreenBuilder.onScreenClosed();
        }
        if (StringUtils.matchesKey(839, str)) {
            if (!needsAuth) {
                return composeEmail(getFirstAddress(toList), new StringBuffer().append(replyPrefix).append(subject).toString(), body);
            }
            setMailAction(54, 0);
            return 0;
        }
        if (StringUtils.matchesKey(840, str)) {
            if (!needsAuth) {
                return composeEmail(mergeAddressLists(copyAddressList(toList), ccList), new StringBuffer().append(replyPrefix).append(subject).toString(), body);
            }
            setMailAction(54, 1);
            return 0;
        }
        if (StringUtils.matchesKey(841, str)) {
            if (!needsAuth) {
                return composeEmail(ObjectPool.newVector(), new StringBuffer().append(forwardPrefix).append(subject).toString(), body);
            }
            setMailAction(54, 2);
            return 0;
        }
        if (StringUtils.matchesKey(855, str)) {
            Storage.state().setInt(ChatKeys.INT_CHAT_VIEW_MODE, 2);
            return 0;
        }
        if (StringUtils.matchesKey(856, str)) {
            Storage.state().setInt(ChatKeys.INT_CHAT_VIEW_MODE, 1);
            return 0;
        }
        if (!StringUtils.matchesKey(845, str)) {
            return 0;
        }
        Storage.state().setInt(ChatKeys.INT_ACTIVE_CHATROOM_ID, account.chatRoomManager.findDefault().id);
        return 0;
    }

    /* renamed from: a */
    public static final int handleMailForwardAction(String str) {
        String strM584b = Storage.state().getString(RuntimeKeys.SLOT_MESSAGE_ID);
        int iM586d = Storage.state().getInt(ChatKeys.INT_CHATROOM_ID);
        MrimAccount account = (MrimAccount) Storage.state().getAccount();
        Message message = account.chatRoomManager.findById(iM586d).getMessage(strM584b);
        Vector toList = message.getToList();
        Vector ccList = message.getCcList();
        String subject = message.getSubject();
        String strM584b2 = Storage.resources().getString(PackedStringKeys.PREFIX_REPLY);
        String strM584b3 = Storage.resources().getString(PackedStringKeys.PREFIX_FORWARD);
        String str2 = ((MrimAccount) Storage.state().getAccount()).login;
        wrapInVector(strM584b);
        if (StringUtils.matchesKey(839, str)) {
            ScreenBuilder.onScreenClosed();
            composeEmail(getFirstAddress(toList), StringUtils.concat(strM584b2, subject), Utils.quoteText(message.body));
            return 0;
        }
        if (!StringUtils.matchesKey(840, str)) {
            if (StringUtils.matchesKey(841, str)) {
                ScreenBuilder.onScreenClosed();
                composeEmail(ObjectPool.newVector(), StringUtils.concat(strM584b3, subject), Utils.quoteText(message.body));
                return 0;
            }
            if (!StringUtils.matchesKey(845, str)) {
                return 0;
            }
            Storage.state().setInt(ChatKeys.INT_ACTIVE_CHATROOM_ID, account.chatRoomManager.findDefault().id);
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        Vector vectorM865a = mergeAddressLists(copyAddressList(ccList), toList);
        int iM541c = Utils.vectorSize(vectorM865a);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                break;
            }
            Object objElementAt = vectorM865a.elementAt(iM541c);
            if (StringUtils.equals(str2, ((String[]) objElementAt)[0])) {
                vectorM865a.removeElement(objElementAt);
                break;
            }
        }
        composeEmail(vectorM865a, StringUtils.concat(strM584b2, subject), Utils.quoteText(message.body));
        return 0;
    }

    /* renamed from: g */
    private static void wrapInVector(String str) {
        Vector vectorM1213g = ObjectPool.newVector();
        vectorM1213g.addElement(str);
        IOUtils.setSelectedItems(vectorM1213g);
    }

    public static String[] createAddressPair(String address1, String address2) {
        return new String[]{address1, address2};
    }

    public static int composeEmail(Vector recipients, String subject, String bodyText) {
        StringBuffer recipientsSb = ObjectPool.newStringBuffer();
        String empty = Storage.emptyStr;
        String separator = ObjectPool.unpackChars(8236);
        int i = 0;
        while (i < Utils.vectorSize(recipients)) {
            recipientsSb.append(i > 0 ? separator : empty).append(((String[]) recipients.elementAt(i))[0]);
            i++;
        }
        Storage.state().setObject(RuntimeKeys.SLOT_MSG_EXTRA_2, (Object) ObjectPool.toStringAndRelease(recipientsSb));
        Storage.state().setObject(RuntimeKeys.SLOT_MSG_EXTRA_3, (Object) Utils.defaultStr(subject));
        String empty2 = Storage.emptyStr;
        Storage.state().setFromBuffer(RuntimeKeys.SLOT_TRAFFIC_STATUS_TEXT, ObjectPool.newStringBuffer().append(Storage.state().getBool(SettingsKeys.SETTING_TRAFFIC_INFO_ENABLED) ? ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_TRAFFIC_INFO_YES)).append('\n')) : empty2).append(Storage.state().getBool(SettingsKeys.SETTING_TRAFFIC_INFO_TYPE) ? ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_TRAFFIC_INFO_NO)).append('\n')) : empty2).append(Utils.defaultStr(bodyText)).append(Storage.resources().getString(StringResKeys.STR_TRAFFIC_LABEL)));
        return ScreenId.COMPOSE_MESSAGE;
    }
}
