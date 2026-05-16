package com.trykote.mobileagent.protocol.mrim;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;

import java.util.Vector;

final class MrimResponseHandler {

    // Group ID unit (1 << 24) - each group occupies one 24-bit slot
    private static final int GROUP_ID_UNIT = 16777216;

    // Contact status flags for created contacts
    private static final int FLAG_PHONE_CONTACT = 1048576;
    private static final int FLAG_AUTH_PENDING = 64;
    private static final int FLAG_PHONE_NUMBER = 128;

    // Initial status code for phone contacts
    private static final int STATUS_PHONE = 103;

    // Auth response: request acknowledged
    private static final int AUTH_RESPONSE_ACK = 32769;

    // Error code: duplicate contact
    private static final int ERROR_DUPLICATE_CONTACT = 5;

    private final MrimAccount account;

    MrimResponseHandler(MrimAccount account) {
        this.account = account;
    }

    void dispatch(ByteBuffer buf, int i) {
        Object[] objArr = null;
        int resultCode = buf.readInt();
        Vector vector = this.account.extras;
        int size = -1;
        for (int si = vector.size() - 1; si >= 0; si--) {
            objArr = (Object[]) vector.elementAt(si);
            if (((Integer) objArr[0]).intValue() == i) {
                size = si;
                break;
            }
        }
        if (size < 0) {
            return;
        }
        switch (((Integer) objArr[1]).intValue()) {
            case MrimAccount.RESP_MODIFY_CONTACT:
                if (resultCode != 0) {
                    EventDispatcher.postRenameError(objArr, resultCode);
                    break;
                } else {
                    ((MrimContact) objArr[2]).updateDisplayNameAndGroups((String) objArr[3], (String) objArr[4]);
                    break;
                }
            case MrimAccount.RESP_RENAME_GROUP:
                if (resultCode != 0) {
                    EventDispatcher.postRenameError(objArr, resultCode);
                    break;
                } else {
                    ((MrimContactGroup) objArr[2]).setNameIfChanged((String) objArr[3]);
                    break;
                }
            case MrimAccount.RESP_DELETE_CONTACT:
                if (resultCode != 0) {
                    EventDispatcher.postDeleteError(objArr, resultCode);
                    break;
                } else {
                    this.account.removeContact((Contact) objArr[2], true);
                    break;
                }
            case MrimAccount.RESP_DELETE_GROUP:
                handleDeleteGroupResponse(resultCode, objArr);
                break;
            case MrimAccount.RESP_ADD_GROUP:
                if (resultCode != 0) {
                    EventDispatcher.postAddGroupError(objArr, resultCode);
                    break;
                } else {
                    this.account.groups.addElement(new MrimContactGroup(this.account, this.account.findAvailableGroupId(), ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    break;
                }
            case MrimAccount.RESP_ADD_PHONE_CONTACT:
                handleAddPhoneContactResponse(resultCode, objArr, buf);
                break;
            case MrimAccount.RESP_XMPP_SERVICE:
                if (resultCode != 1) {
                    EventDispatcher.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_XMPP_SERVICE_MSG)).append(objArr[2]).append(ResourceAccessor.str(StringResKeys.STR_MESSAGE_SEPARATOR)).append(resultCode)));
                    break;
                }
                break;
            case MrimAccount.RESP_SINGLE_CONTACT:
                MrimContactParser.createSingleContact(this.account, resultCode, buf);
                break;
            case MrimAccount.RESP_CONTACT_INFO:
                MrimContactParser.parseContactInfoResponse(this.account, resultCode, buf);
                break;
            case MrimAccount.RESP_ADD_CONTACT:
                handleAddContactResponse(resultCode, objArr, buf);
                break;
            case MrimAccount.RESP_AUTH:
                handleAuthResponseResult(resultCode, objArr);
                break;
            case MrimAccount.RESP_MOVE_FLAG:
                if (resultCode != 0) {
                    EventDispatcher.postRenameError(objArr, resultCode);
                    break;
                } else {
                    ((MrimContact) objArr[2]).statusFlags = ((Integer) objArr[3]).intValue();
                    break;
                }
            case MrimAccount.RESP_MOVE_TO_GROUP:
                handleMoveToGroupResponse(resultCode, objArr);
                break;
            case MrimAccount.RESP_UPDATE_NAME:
                MrimContactParser.updateContactName(this.account, resultCode, buf);
                break;
            case MrimAccount.RESP_ADD_PHONE:
                handleAddPhoneResponse(resultCode, objArr, buf);
                break;
            case MrimAccount.RESP_ADD_TO_GROUP:
                MrimContactParser.addContactToGroup(this.account, resultCode, buf);
                break;
            case MrimAccount.RESP_AUTH_RESPONSE:
                this.account.handleAuthResponse(resultCode, objArr, buf);
                break;
        }
        vector.removeElementAt(size);
    }

    private void handleDeleteGroupResponse(int resultCode, Object[] objArr) {
        if (resultCode != 0) {
            EventDispatcher.postDeleteError(objArr, resultCode);
            return;
        }
        MrimContactGroup mrimGroup = (MrimContactGroup) objArr[2];
        int groupIndex = mrimGroup.groupId >> 24;
        for (int gi = this.account.groups.size() - 1; gi >= 0; gi--) {
            MrimContactGroup mrimGroup2 = (MrimContactGroup) this.account.getGroup(gi);
            if ((mrimGroup2.groupId >> 24) > groupIndex) {
                mrimGroup2.groupId -= GROUP_ID_UNIT;
            }
        }
        this.account.removeGroup((ContactGroup) mrimGroup);
    }

    private void handleAddPhoneContactResponse(int resultCode, Object[] objArr, ByteBuffer buf) {
        if (resultCode != 0) {
            EventDispatcher.postAddGroupError(objArr, resultCode);
            return;
        }
        MrimContactGroup mrimGroup = (MrimContactGroup) objArr[4];
        int contactId = buf.readInt();
        String statusStr = ResourceAccessor.str(StringResKeys.STR_PHONE_SUFFIX);
        String str = (String) objArr[2];
        String str2 = (String) objArr[3];
        String str3 = AppState.emptyStr;
        mrimGroup.addContact((Object) new MrimContact(this.account, contactId, FLAG_PHONE_CONTACT, STATUS_PHONE, statusStr, str, 0, 1, str2, str3, str3));
    }

    private void handleAddContactResponse(int resultCode, Object[] objArr, ByteBuffer buf) {
        if (resultCode != 0) {
            if (resultCode != ERROR_DUPLICATE_CONTACT) {
                EventDispatcher.postAddGroupError(objArr, resultCode);
            }
            return;
        }
        MrimContactGroup mrimGroup = (MrimContactGroup) objArr[4];
        int contactId = buf.readInt();
        int flags = ((Integer) objArr[5]).intValue();
        int serverId = mrimGroup.serverId;
        String str = (String) objArr[2];
        String str2 = (String) objArr[3];
        String str3 = AppState.emptyStr;
        mrimGroup.addContact((Object) new MrimContact(this.account, contactId, flags, serverId, str, str2, 1, 0, str3, str3, str3));
    }

    private void handleAuthResponseResult(int resultCode, Object[] objArr) {
        MrimContact mrimContact = (MrimContact) objArr[2];
        switch (resultCode) {
            case 0:
                mrimContact.updateMessageFlag(((Long) objArr[3]).longValue(), FLAG_AUTH_PENDING);
                break;
            case AUTH_RESPONSE_ACK:
                if (mrimContact.isSystem()) {
                    EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_AUTH_GRANTED));
                    break;
                }
            default:
                EventDispatcher.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_AUTH_REQUEST)).append(objArr[2]).append(ResourceAccessor.str(StringResKeys.STR_MESSAGE_SEPARATOR)).append(resultCode)));
                break;
        }
    }

    private void handleMoveToGroupResponse(int resultCode, Object[] objArr) {
        if (resultCode != 0) {
            EventDispatcher.postRenameError(objArr, resultCode);
            return;
        }
        MrimContact mrimContact = (MrimContact) objArr[2];
        MrimContactGroup mrimGroup = (MrimContactGroup) objArr[3];
        mrimContact.groupId = mrimGroup.serverId;
        for (int gi = this.account.groups.size() - 1; gi >= 0; gi--) {
            this.account.getGroup(gi).removeElement(mrimContact);
        }
        mrimGroup.addContact((Object) mrimContact);
    }

    private void handleAddPhoneResponse(int resultCode, Object[] objArr, ByteBuffer buf) {
        if (resultCode != 0) {
            EventDispatcher.postAddGroupError(objArr, resultCode);
            return;
        }
        MrimContactGroup defaultGroup = this.account.getFirstContactGroup();
        int contactId = buf.readInt();
        int serverId = defaultGroup.serverId;
        String contactName = buf.readWideStr();
        String str = (String) objArr[2];
        String str2 = AppState.emptyStr;
        defaultGroup.addContact(new MrimContact(this.account, contactId, FLAG_PHONE_NUMBER, serverId, contactName, str, 0, 1, str2, str2, str2));
    }
}
