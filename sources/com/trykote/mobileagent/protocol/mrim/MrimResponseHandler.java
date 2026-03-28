package com.trykote.mobileagent.protocol.mrim;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

final class MrimResponseHandler {

    private final MrimAccount account;

    MrimResponseHandler(MrimAccount account) {
        this.account = account;
    }

    void dispatch(ByteBuffer buf, int i) {
        Object[] objArr;
        int resultCode = buf.readInt();
        Vector vector = this.account.extras;
        int size = vector.size();
        do {
            size--;
            if (size < 0) {
                return;
            } else {
                objArr = (Object[]) vector.elementAt(size);
            }
        } while (((Integer) objArr[0]).intValue() != i);
        switch (((Integer) objArr[1]).intValue()) {
            case MrimAccount.RESP_MODIFY_CONTACT:
                if (resultCode != 0) {
                    IOUtils.postRenameError(objArr, resultCode);
                    break;
                } else {
                    ((MrimContact) objArr[2]).updateDisplayNameAndGroups((String) objArr[3], (String) objArr[4]);
                    break;
                }
            case MrimAccount.RESP_RENAME_GROUP:
                if (resultCode != 0) {
                    IOUtils.postRenameError(objArr, resultCode);
                    break;
                } else {
                    ((MrimContactGroup) objArr[2]).setNameIfChanged((String) objArr[3]);
                    break;
                }
            case MrimAccount.RESP_DELETE_CONTACT:
                if (resultCode != 0) {
                    IOUtils.postDeleteError(objArr, resultCode);
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
                    IOUtils.postAddGroupError(objArr, resultCode);
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
                    IOUtils.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_XMPP_SERVICE_MSG)).append(objArr[2]).append(AppState.getString(StateKeys.STR_MESSAGE_SEPARATOR)).append(resultCode)));
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
                    IOUtils.postRenameError(objArr, resultCode);
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
                ResourceManager.handleAuthResponse(this.account, resultCode, objArr, buf);
                break;
        }
        vector.removeElementAt(size);
    }

    private void handleDeleteGroupResponse(int resultCode, Object[] objArr) {
        if (resultCode != 0) {
            IOUtils.postDeleteError(objArr, resultCode);
            return;
        }
        MrimContactGroup mrimGroup = (MrimContactGroup) objArr[2];
        int groupIndex = mrimGroup.groupId >> 24;
        int size2 = this.account.groups.size();
        while (true) {
            size2--;
            if (size2 < 0) {
                this.account.removeGroup((ContactGroup) mrimGroup);
                return;
            }
            MrimContactGroup mrimGroup2 = (MrimContactGroup) this.account.getGroup(size2);
            if ((mrimGroup2.groupId >> 24) > groupIndex) {
                mrimGroup2.groupId -= 16777216;
            }
        }
    }

    private void handleAddPhoneContactResponse(int resultCode, Object[] objArr, ByteBuffer buf) {
        if (resultCode != 0) {
            IOUtils.postAddGroupError(objArr, resultCode);
            return;
        }
        MrimContactGroup mrimGroup = (MrimContactGroup) objArr[4];
        int contactId = buf.readInt();
        String statusStr = AppState.getString(StateKeys.STR_PHONE_SUFFIX);
        String str = (String) objArr[2];
        String str2 = (String) objArr[3];
        String str3 = AppState.emptyStr;
        mrimGroup.addContact((Object) new MrimContact(this.account, contactId, 1048576, 103, statusStr, str, 0, 1, str2, str3, str3));
    }

    private void handleAddContactResponse(int resultCode, Object[] objArr, ByteBuffer buf) {
        if (resultCode != 0) {
            if (resultCode != 5) {
                IOUtils.postAddGroupError(objArr, resultCode);
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
                mrimContact.updateMessageFlag(((Long) objArr[3]).longValue(), 64);
                break;
            case 32769:
                if (mrimContact.isSystem()) {
                    IOUtils.postNotification(AppState.getString(StateKeys.STR_AUTH_GRANTED));
                    break;
                }
            default:
                IOUtils.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_AUTH_REQUEST)).append(objArr[2]).append(AppState.getString(StateKeys.STR_MESSAGE_SEPARATOR)).append(resultCode)));
                break;
        }
    }

    private void handleMoveToGroupResponse(int resultCode, Object[] objArr) {
        if (resultCode != 0) {
            IOUtils.postRenameError(objArr, resultCode);
            return;
        }
        MrimContact mrimContact = (MrimContact) objArr[2];
        MrimContactGroup mrimGroup = (MrimContactGroup) objArr[3];
        mrimContact.groupId = mrimGroup.serverId;
        int size = this.account.groups.size();
        while (true) {
            size--;
            if (size < 0) {
                mrimGroup.addContact((Object) mrimContact);
                return;
            }
            this.account.getGroup(size).removeElement(mrimContact);
        }
    }

    private void handleAddPhoneResponse(int resultCode, Object[] objArr, ByteBuffer buf) {
        if (resultCode != 0) {
            IOUtils.postAddGroupError(objArr, resultCode);
            return;
        }
        MrimContactGroup defaultGroup = this.account.getFirstContactGroup();
        int contactId = buf.readInt();
        int serverId = defaultGroup.serverId;
        String contactName = buf.readWideStr();
        String str = (String) objArr[2];
        String str2 = AppState.emptyStr;
        defaultGroup.addContact(new MrimContact(this.account, contactId, 128, serverId, contactName, str, 0, 1, str2, str2, str2));
    }
}
