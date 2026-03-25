package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.*;
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
            addUniqueAddress(results, AppController.createAddressPair((String) rawAddresses.elementAt(i), (String) decodedNames.elementAt(i)));
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
        AppState.setInt(StateKeys.INT_XMPP_ACTION, i);
        AppState.setInt(StateKeys.INT_XMPP_ACTION_TYPE, i2);
    }

    public static final int processMailResponse() {
        String bodyText;
        Object[] asyncResult = IOUtils.pollAsyncResult();
        if (asyncResult == null) {
            return handleMailRedirect();
        }
        Object[] responseData = ApiClient.getAsyncResult(asyncResult);
        if (responseData == null) {
            return 0;
        }
        int validationResult = IOUtils.validateJsonResponse(responseData);
        if (validationResult != 0) {
            return validationResult;
        }
        String messageId = AppState.getString(StateKeys.SLOT_MESSAGE_ID);
        ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID));
        Message message = chatRoom.getMessage(messageId);
        boolean wasUnread = message.hasFlag(4);
        Object jsonPayload = IOUtils.getJsonPayload();
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
            bodyText = AppState.emptyStr;
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
        int action = AppState.getInt(StateKeys.INT_XMPP_ACTION);
        if (action == 54) {
            Message message = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID)).getMessage(AppState.getString(StateKeys.SLOT_MESSAGE_ID));
            Vector toList = message.getToList();
            Vector ccList = message.getCcList();
            getFirstRecipient(toList);
            String subject = message.getSubject();
            String str = message.body;
            String replyPrefix = AppState.getString(StateKeys.STR_RES_HTTPS_PREFIX);
            String fwdPrefix = AppState.getString(StateKeys.STR_RES_HTTP_PREFIX);
            String string = new StringBuffer().append(AppState.getString(StateKeys.STR_SEARCH_QUERY_PREFIX)).append(Utils.quoteText(str)).toString();
            switch (AppState.getInt(StateKeys.INT_XMPP_ACTION_TYPE)) {
                case 0:
                    ResourceManager.composeEmail(getFirstAddress(toList), new StringBuffer().append(replyPrefix).append(subject).toString(), string);
                    break;
                case 1:
                    ResourceManager.composeEmail(mergeAddressLists(copyAddressList(toList), ccList), new StringBuffer().append(replyPrefix).append(subject).toString(), string);
                    break;
                case 2:
                    ResourceManager.composeEmail(ObjectPool.newVector(), new StringBuffer().append(fwdPrefix).append(subject).toString(), string);
                    break;
                case 3:
                    ResourceManager.composeEmail(copyAddressList(ccList), subject, str);
                    break;
            }
        }
        return action;
    }
}
