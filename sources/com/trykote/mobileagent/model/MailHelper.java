package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Vector;

public final class MailHelper {

    // Mail action codes
    public static final int ACTION_CLOSE_AND_OPEN = 48;
    public static final int ACTION_COMPOSE_REPLY = 54;

    // Chat view mode values
    public static final int VIEW_MODE_LIST = 1;
    public static final int VIEW_MODE_DETAIL = 2;

    // Character code for '@' used in email address validation
    private static final char CHAR_AT = '@';

    public static final void writeAddressPairs(Vector vector, ByteBuffer buf) {
        int count = Utils.vectorSize(vector);
        buf.writeIntLE(count);
        for (int i = 0; i < count; i++) {
            String[] pair = (String[]) vector.elementAt(i);
            buf.writeStringUTF16(pair[0]).writeStringUTF16(pair[1]);
        }
    }

    public static final Vector readAddressPairs(ByteBuffer buf) {
        Vector results = ObjectPool.newVector();
        int count = buf.readInt();
        for (int i = count - 1; i >= 0; i--) {
            results.addElement(new String[]{buf.readUTF8Str((String) null), buf.readUTF8Str((String) null)});
        }
        return results;
    }

    public static final Vector copyAddressList(Vector vector) {
        Vector results = ObjectPool.newVector();
        for (int i = 0; i < Utils.vectorSize(vector); i++) {
            results.addElement(vector.elementAt(i));
        }
        return results;
    }

    public static final Vector mergeAddressLists(Vector target, Vector source) {
        if (source != null) {
            Enumeration elements = source.elements();
            while (elements.hasMoreElements()) {
                addUniqueAddress(target, (String[]) elements.nextElement());
            }
        }
        return target;
    }

    public static final Vector getFirstAddress(Vector vector) {
        Vector results = ObjectPool.newVector();
        if (Utils.vectorSize(vector) > 0) {
            results.addElement(vector.elementAt(0));
        }
        return results;
    }

    public static final Vector addUniqueAddress(Vector vector, String[] addressPair) {
        String address = addressPair[0];
        if (address.indexOf(CHAR_AT) != -1) {
            boolean alreadyExists = false;
            for (int j = Utils.vectorSize(vector) - 1; j >= 0; j--) {
                if (StringUtils.equals(address, ((String[]) vector.elementAt(j))[0])) {
                    alreadyExists = true;
                }
            }
            if (!alreadyExists) {
                vector.addElement(addressPair);
            }
        }
        return vector;
    }

    public static final Vector parseAddressHeader(String addresses, String names) {
        Vector results = ObjectPool.newVector();
        Vector decodedNames = splitCommaSeparated(Conversation.decodeHtmlSpecial(names));
        Vector rawAddresses = splitCommaSeparated(addresses);
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
        boolean collecting = true;
        int i = 0;
        while (i <= length) {
            char ch = i < length ? str.charAt(i) : ',';
            if (!collecting) {
                collecting = true;
            } else if (ch == ',') {
                results.addElement(ObjectPool.toString(sb, false));
                collecting = false;
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
            if (ch != ';' && ch != ',' && ch != ' ') {
                sb.append(ch);
            } else if (sb.length() > 0) {
                String address = ObjectPool.toString(sb, false);
                results.addElement(new String[]{address, address});
            }
            i++;
        }
        ObjectPool.toStringAndRelease(sb);
        return results;
    }

    public static final void setMailAction(int action, int actionType) {
        RuntimeState.setXmppAction(action);
        RuntimeState.setXmppActionType(actionType);
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
        String messageId = RuntimeState.getMessageId();
        ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).chatRoomManager.findById(ChatState.getChatRoomId());
        Message message = chatRoom.getMessage(messageId);
        boolean wasUnread = message.hasFlag(Message.FLAG_UNREAD);
        Object jsonPayload = ApiClient.getJsonPayload();
        Object attachmentsList = JsonParser.getValueByInt(jsonPayload, 722874);
        int size = ((Vector) attachmentsList).size();
        Object[] attachments = new Object[size];
        for (int i = size - 1; i >= 0; i--) {
            Object attachmentObj = JsonParser.getVectorElement(attachmentsList, i);
            attachments[i] = new String[]{JsonParser.getStringByInt(attachmentObj, Message.ATTACHMENT_KEY_FIRST), JsonParser.getStringByInt(attachmentObj, Message.ATTACHMENT_KEY_FIRST + 1), JsonParser.getStringByInt(attachmentObj, Message.ATTACHMENT_KEY_FIRST + 2), JsonParser.getStringByInt(attachmentObj, Message.ATTACHMENT_KEY_FIRST + 3), JsonParser.getStringByInt(attachmentObj, Message.ATTACHMENT_KEY_FIRST + 4), JsonParser.getStringByInt(attachmentObj, Message.ATTACHMENT_KEY_LAST)};
        }
        message.attachments = attachments;
        String rawBody = (String) JsonParser.getValueByInt(jsonPayload, 919493);
        if (rawBody == null) {
            bodyText = AppState.emptyStr;
        } else {
            StringBuffer sb = ObjectPool.newStringBuffer();
            int length = rawBody.length();
            int pos = 0;
            while (pos < length) {
                char ch = rawBody.charAt(pos);
                sb.append(ch);
                if (ch == ' ') {
                    while (pos + 1 < length && rawBody.charAt(pos + 1) == ' ') {
                        pos++;
                    }
                }
                if (ch == '\n') {
                    while (pos + 1 < length && rawBody.charAt(pos + 1) == '\n') {
                        pos++;
                    }
                }
                pos++;
            }
            bodyText = ObjectPool.toStringAndRelease(sb);
        }
        message.body = bodyText;
        if (wasUnread) {
            message.setFlag(Message.FLAG_UNREAD, false);
            chatRoom.decrementUnread();
        }
        return handleMailRedirect();
    }

    private static final int handleMailRedirect() {
        int action = RuntimeState.getXmppAction();
        if (action == ACTION_COMPOSE_REPLY) {
            Message message = ((MrimAccount) AppState.getAccount()).chatRoomManager.findById(ChatState.getChatRoomId()).getMessage(RuntimeState.getMessageId());
            Vector toList = message.getToList();
            Vector ccList = message.getCcList();
            getFirstRecipient(toList);
            String subject = message.getSubject();
            String body = message.body;
            String replyPrefix = ResourceAccessor.str(PackedStringKeys.PREFIX_REPLY);
            String fwdPrefix = ResourceAccessor.str(PackedStringKeys.PREFIX_FORWARD);
            String quotedBody = new StringBuffer().append(ResourceAccessor.str(StringResKeys.STR_SEARCH_QUERY_PREFIX)).append(Utils.quoteText(body)).toString();
            switch (RuntimeState.getXmppActionType()) {
                case 0:
                    composeEmail(getFirstAddress(toList), new StringBuffer().append(replyPrefix).append(subject).toString(), quotedBody);
                    break;
                case 1:
                    composeEmail(mergeAddressLists(copyAddressList(toList), ccList), new StringBuffer().append(replyPrefix).append(subject).toString(), quotedBody);
                    break;
                case 2:
                    composeEmail(ObjectPool.newVector(), new StringBuffer().append(fwdPrefix).append(subject).toString(), quotedBody);
                    break;
                case 3:
                    composeEmail(copyAddressList(ccList), subject, body);
                    break;
            }
        }
        return action;
    }

    public static final int handleMailMenuAction(String str, int actionCode) {
        String messageId = RuntimeState.getMessageId();
        wrapInVector(messageId);
        int chatRoomId = ChatState.getChatRoomId();
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Message message = account.chatRoomManager.findById(chatRoomId).getMessage(messageId);
        String subject = message.getSubject();
        Vector toList = message.getToList();
        Vector ccList = message.getCcList();
        getFirstRecipient(toList);
        boolean needsAuth = SettingsState.isAuthRequired();
        String replyPrefix = ResourceAccessor.str(PackedStringKeys.PREFIX_REPLY);
        String forwardPrefix = ResourceAccessor.str(PackedStringKeys.PREFIX_FORWARD);
        String body = AppState.emptyStr;
        if (actionCode == ACTION_CLOSE_AND_OPEN) {
            ScreenBuilder.onScreenClosed();
            ScreenBuilder.onScreenClosed();
        }
        if (StringUtils.matchesKey(839, str)) {
            if (!needsAuth) {
                return composeEmail(getFirstAddress(toList), new StringBuffer().append(replyPrefix).append(subject).toString(), body);
            }
            setMailAction(ACTION_COMPOSE_REPLY, 0);
            return 0;
        }
        if (StringUtils.matchesKey(840, str)) {
            if (!needsAuth) {
                return composeEmail(mergeAddressLists(copyAddressList(toList), ccList), new StringBuffer().append(replyPrefix).append(subject).toString(), body);
            }
            setMailAction(ACTION_COMPOSE_REPLY, 1);
            return 0;
        }
        if (StringUtils.matchesKey(841, str)) {
            if (!needsAuth) {
                return composeEmail(ObjectPool.newVector(), new StringBuffer().append(forwardPrefix).append(subject).toString(), body);
            }
            setMailAction(ACTION_COMPOSE_REPLY, 2);
            return 0;
        }
        if (StringUtils.matchesKey(855, str)) {
            ChatState.setChatViewMode(VIEW_MODE_DETAIL);
            return 0;
        }
        if (StringUtils.matchesKey(856, str)) {
            ChatState.setChatViewMode(VIEW_MODE_LIST);
            return 0;
        }
        if (!StringUtils.matchesKey(845, str)) {
            return 0;
        }
        ChatState.setActiveChatRoomId(account.chatRoomManager.findDefault().id);
        return 0;
    }

    public static final int handleMailForwardAction(String str) {
        String messageId = RuntimeState.getMessageId();
        int chatRoomId = ChatState.getChatRoomId();
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Message message = account.chatRoomManager.findById(chatRoomId).getMessage(messageId);
        Vector toList = message.getToList();
        Vector ccList = message.getCcList();
        String subject = message.getSubject();
        String replyPrefix = ResourceAccessor.str(PackedStringKeys.PREFIX_REPLY);
        String forwardPrefix = ResourceAccessor.str(PackedStringKeys.PREFIX_FORWARD);
        String currentLogin = ((MrimAccount) AppState.getAccount()).login;
        wrapInVector(messageId);
        if (StringUtils.matchesKey(839, str)) {
            ScreenBuilder.onScreenClosed();
            composeEmail(getFirstAddress(toList), StringUtils.concat(replyPrefix, subject), Utils.quoteText(message.body));
            return 0;
        }
        if (!StringUtils.matchesKey(840, str)) {
            if (StringUtils.matchesKey(841, str)) {
                ScreenBuilder.onScreenClosed();
                composeEmail(ObjectPool.newVector(), StringUtils.concat(forwardPrefix, subject), Utils.quoteText(message.body));
                return 0;
            }
            if (!StringUtils.matchesKey(845, str)) {
                return 0;
            }
            ChatState.setActiveChatRoomId(account.chatRoomManager.findDefault().id);
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        Vector mergedRecipients = mergeAddressLists(copyAddressList(ccList), toList);
        for (int j = Utils.vectorSize(mergedRecipients) - 1; j >= 0; j--) {
            Object entry = mergedRecipients.elementAt(j);
            if (StringUtils.equals(currentLogin, ((String[]) entry)[0])) {
                mergedRecipients.removeElement(entry);
                break;
            }
        }
        composeEmail(mergedRecipients, StringUtils.concat(replyPrefix, subject), Utils.quoteText(message.body));
        return 0;
    }

    private static void wrapInVector(String str) {
        Vector items = ObjectPool.newVector();
        items.addElement(str);
        IOUtils.setSelectedItems(items);
    }

    public static String[] createAddressPair(String address1, String address2) {
        return new String[]{address1, address2};
    }

    public static int composeEmail(Vector recipients, String subject, String bodyText) {
        StringBuffer recipientsSb = ObjectPool.newStringBuffer();
        String empty = AppState.emptyStr;
        String separator = ObjectPool.unpackChars(8236);
        int i = 0;
        while (i < Utils.vectorSize(recipients)) {
            recipientsSb.append(i > 0 ? separator : empty).append(((String[]) recipients.elementAt(i))[0]);
            i++;
        }
        RuntimeState.setMsgExtra2(ObjectPool.toStringAndRelease(recipientsSb));
        RuntimeState.setMsgExtra3(Utils.defaultStr(subject));
        String emptyStr = AppState.emptyStr;
        RuntimeState.setTrafficStatusText(ObjectPool.newStringBuffer().append(SettingsState.isTrafficInfoEnabled() ? ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_TRAFFIC_INFO_YES)).append('\n')) : emptyStr).append(SettingsState.getTrafficInfoType() != 0 ? ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_TRAFFIC_INFO_NO)).append('\n')) : emptyStr).append(Utils.defaultStr(bodyText)).append(ResourceAccessor.str(StringResKeys.STR_TRAFFIC_LABEL)));
        return ScreenId.COMPOSE_MESSAGE;
    }
}
