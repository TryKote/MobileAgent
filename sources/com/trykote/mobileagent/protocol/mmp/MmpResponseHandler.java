package com.trykote.mobileagent.protocol.mmp;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.RegistrationKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.model.ContactInfo;
import com.trykote.mobileagent.protocol.ProtocolFactory;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.Utils;

import java.util.Enumeration;
import java.util.Vector;

final class MmpResponseHandler {

    // Response type IDs (queue entry command codes)
    static final int RESP_RENAME_CONTACT = 0;
    static final int RESP_RENAME_GROUP = 1;
    static final int RESP_DELETE_GROUP = 2;
    static final int RESP_SYNC_GROUP_ORDER = 3;
    static final int RESP_ADD_GROUP = 4;
    static final int RESP_DELETE_CONTACT = 5;
    static final int RESP_CONTACT_LIST = 6;
    static final int RESP_CONTACT_INFO = 7;
    static final int RESP_HISTORY = 8;
    static final int RESP_SEARCH = 9;
    static final int RESP_MOVE_PHASE1 = 10;
    static final int RESP_MOVE_PHASE2 = 11;
    static final int RESP_MOVE_PHASE3 = 12;
    static final int RESP_MOVE_PHASE4 = 13;
    static final int RESP_ADD_CONTACT_PHASE1 = 14;
    static final int RESP_ADD_CONTACT_PHASE2 = 15;
    static final int RESP_ADD_CONTACT_PHASE3 = 16;
    static final int RESP_AUTH_STATUS = 17;
    static final int RESP_UPDATE_PERMISSIONS = 18;
    static final int RESP_REMOVE_PERMISSIONS = 19;
    static final int RESP_CONFIG_UPDATE = 20;
    static final int RESP_NEW_CONTACT_SEARCH = 21;

    // Contact list entry types
    private static final int ENTRY_TYPE_CONTACT = 0;
    static final int ENTRY_TYPE_GROUP = 1;
    private static final int ENTRY_TYPE_CONTACT_ID_MAP = 2;
    private static final int ENTRY_TYPE_GROUP_ID_MAP = 3;
    private static final int ENTRY_TYPE_SETTINGS = 4;
    private static final int ENTRY_TYPE_ADDITIONAL_DATA = 14;

    // Server response sub-types
    private static final int SERVER_RESPONSE_MARKER = 2010;
    private static final int SUBTYPE_USER_BASIC = 200;
    private static final int SUBTYPE_USER_EXTENDED = 220;
    private static final int SUBTYPE_USER_ABOUT = 230;
    private static final int SUBTYPE_SEARCH_RESULT = 420;
    private static final int SUBTYPE_SEARCH_LAST = 430;
    private static final int SUBTYPE_HISTORY_ENTRY = 65;
    private static final int SUBTYPE_HISTORY_END = 66;

    // Search/history markers
    private static final int SEARCH_HISTORY_MARKER = 60;
    private static final int SEARCH_HISTORY_FULL_MARKER = 62;
    private static final int SEARCH_FIELD_COUNT = 10;

    // Offline message filter
    private static final int OFFLINE_MSG_FILTERED = 1004;

    // Date/time constants for timestamp calculation
    private static final int EPOCH_YEAR = 1970;
    private static final int LEAP_CYCLE_START = 1968;
    private static final int CENTURY_YEAR = 2000;
    private static final int DAYS_IN_YEAR = 365;
    private static final int FEB_DAYS_NORMAL = 28;
    private static final int FEB_DAYS_LEAP = 29;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MILLIS_PER_SECOND = 1000;

    private final MmpProtocol account;

    MmpResponseHandler(MmpProtocol account) {
        this.account = account;
    }

    void dispatch(ByteBuffer buf, int seqNum, int responseFlags) {
        Object[] cmdEntry = null;
        Vector queue = this.account.extras;
        int queueIndex = -1;
        for (int scanIndex = queue.size() - 1; scanIndex >= 0; scanIndex--) {
            cmdEntry = (Object[]) queue.elementAt(scanIndex);
            if (((Integer) cmdEntry[0]).intValue() == seqNum) {
                queueIndex = scanIndex;
                break;
            }
        }
        if (queueIndex < 0) {
            return;
        }
        boolean shouldRemove = true;
        switch (((Integer) cmdEntry[1]).intValue()) {
            case RESP_RENAME_CONTACT:
                shouldRemove = handleSimpleResponse(buf, cmdEntry, true);
                break;
            case RESP_RENAME_GROUP:
                shouldRemove = handleSimpleGroupResponse(buf, cmdEntry);
                break;
            case RESP_DELETE_GROUP:
                shouldRemove = handleDeleteGroupResponse(buf, cmdEntry);
                break;
            case RESP_SYNC_GROUP_ORDER:
                shouldRemove = handleSyncGroupOrderResponse(buf);
                break;
            case RESP_ADD_GROUP:
                shouldRemove = handleAddGroupResponse(buf, cmdEntry);
                break;
            case RESP_DELETE_CONTACT:
                shouldRemove = handleDeleteContactResponse(buf, cmdEntry);
                break;
            case RESP_CONTACT_LIST:
                shouldRemove = handleContactListResponse(buf, responseFlags);
                // fall through — preserved from original bytecode
            case RESP_CONTACT_INFO:
                shouldRemove = handleContactInfoResponse(buf, responseFlags, cmdEntry);
                break;
            case RESP_HISTORY:
                shouldRemove = handleHistoryResponse(buf);
                break;
            case RESP_SEARCH:
                shouldRemove = handleSearchResponse(buf);
                break;
            case RESP_MOVE_PHASE1:
                shouldRemove = handleMovePhase1(buf, cmdEntry);
                break;
            case RESP_MOVE_PHASE2:
                shouldRemove = handleMovePhase2(buf, cmdEntry);
                break;
            case RESP_MOVE_PHASE3:
                shouldRemove = handleMovePhase3(buf, cmdEntry);
                break;
            case RESP_MOVE_PHASE4:
                shouldRemove = handleMovePhase4(buf, cmdEntry);
                break;
            case RESP_ADD_CONTACT_PHASE1:
                shouldRemove = handleAddContactPhase1(buf, cmdEntry);
                break;
            case RESP_ADD_CONTACT_PHASE2:
                shouldRemove = handleAddContactPhase2(buf, cmdEntry);
                break;
            case RESP_ADD_CONTACT_PHASE3:
                shouldRemove = handleAddContactPhase3(buf, cmdEntry);
                break;
            case RESP_AUTH_STATUS:
                this.account.lastError = this.account.configFlags & MmpProtocol.MASK_LOW_16;
                shouldRemove = false;
                break;
            case RESP_UPDATE_PERMISSIONS:
                shouldRemove = handleUpdatePermissionsResponse(buf, cmdEntry);
                break;
            case RESP_REMOVE_PERMISSIONS:
                shouldRemove = handleRemovePermissionsResponse(buf, cmdEntry);
                break;
            case RESP_CONFIG_UPDATE:
                int configStatus = buf.readShortBE();
                if (configStatus != 0) {
                    EventDispatcher.postOperationError(configStatus);
                }
                break;
            case RESP_NEW_CONTACT_SEARCH:
                shouldRemove = handleNewContactSearchResponse(buf);
                break;
        }
        if (shouldRemove) {
            queue.removeElementAt(queueIndex);
        }
    }

    private boolean handleSimpleResponse(ByteBuffer buf, Object[] cmdEntry, boolean isContact) {
        int status = buf.readShortBE();
        if (status == 0) {
            if (isContact) {
                ((MmpContact) cmdEntry[2]).setDisplayName((String) cmdEntry[3]);
            }
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleSimpleGroupResponse(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            ((MmpContactGroup) cmdEntry[2]).setNameIfChanged((String) cmdEntry[3]);
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleDeleteGroupResponse(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            this.account.removeGroup((ContactGroup) cmdEntry[2]);
            this.account.trySendData(this.account.createSyncGroupsCmd());
        } else {
            EventDispatcher.postDeleteError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleSyncGroupOrderResponse(ByteBuffer buf) {
        int status = buf.readShortBE();
        if (status == 0) {
            this.account.trySendData(this.account.createSyncContactsCmd());
        } else {
            EventDispatcher.postOperationError(status);
        }
        return true;
    }

    private boolean handleAddGroupResponse(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            this.account.addGroup(new MmpContactGroup(this.account, ((Integer) cmdEntry[3]).intValue(), (String) cmdEntry[2]));
            this.account.trySendData(this.account.createSyncGroupsCmd());
        } else {
            EventDispatcher.postAddGroupError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleDeleteContactResponse(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            this.account.removeContact((Contact) cmdEntry[2], true);
            this.account.trySendData(this.account.createSyncContactsCmd());
        } else {
            EventDispatcher.postDeleteError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleContactListResponse(ByteBuffer buf, int responseFlags) {
        boolean isLastChunk = (responseFlags & 1) == 0;
        buf.skip(1);
        Vector results = parseContactListEntries(buf);
        for (int resultIndex = results.size() - 1; resultIndex >= 0; resultIndex--) {
            MmpContact parsedContact = (MmpContact) results.elementAt(resultIndex);
            int contactGroupId = parsedContact.onlineSemaphore;
            MmpContactGroup group = null;
            for (int groupIndex = this.account.groups.size() - 1; groupIndex >= 0; groupIndex--) {
                MmpContactGroup candidateGroup = (MmpContactGroup) this.account.getGroup(groupIndex);
                if (candidateGroup.groupId == contactGroupId) {
                    group = candidateGroup;
                    break;
                }
            }
            if (group != null) {
                group.addContact((Object) parsedContact);
            }
        }
        if (isLastChunk) {
            finalizeContactList();
        }
        ObjectPool.releaseVector(results);
        return isLastChunk;
    }

    private Vector parseContactListEntries(ByteBuffer buf) {
        Vector results = ObjectPool.newVector();
        int contactCount = buf.readShortBE();
        for (int entryIndex = 0; entryIndex < contactCount; entryIndex++) {
            String name = buf.readVarLenStr();
            int groupId = buf.readShortBE();
            int contactId = buf.readShortBE();
            int entryType = buf.readShortBE();
            int dataRemaining = buf.readShortBE();
            switch (entryType) {
                case ENTRY_TYPE_CONTACT:
                    String displayName = name;
                    boolean needsAuth = false;
                    while (dataRemaining > 0) {
                        int attrType = buf.readShortBE();
                        int dataLen = buf.peekShortBE(0);
                        if (attrType == MmpProtocol.TAG_DISPLAY_NAME) {
                            displayName = buf.readVarLenStr();
                        } else {
                            if (attrType == MmpProtocol.TAG_AUTHORIZATION_FLAG) {
                                needsAuth = true;
                            }
                            buf.skip(dataLen + 2);
                        }
                        dataRemaining -= dataLen + 4;
                    }
                    results.addElement(new MmpContact(this.account, contactId, groupId, name, displayName, needsAuth));
                    continue;
                case ENTRY_TYPE_GROUP:
                    if (groupId != 0) {
                        this.account.groups.addElement(new MmpContactGroup(this.account, groupId, name));
                    }
                    buf.skip(dataRemaining);
                    continue;
                case ENTRY_TYPE_CONTACT_ID_MAP:
                    this.account.contactsByIdMap.put(name, ObjectPool.integerOf(contactId));
                    buf.skip(dataRemaining);
                    continue;
                case ENTRY_TYPE_GROUP_ID_MAP:
                    this.account.contactGroupsMap.put(name, ObjectPool.integerOf(contactId));
                    buf.skip(dataRemaining);
                    continue;
                case ENTRY_TYPE_SETTINGS:
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                default:
                    buf.skip(dataRemaining);
                    continue;
                case ENTRY_TYPE_ADDITIONAL_DATA:
                    this.account.additionalDataMap.put(name, ObjectPool.integerOf(contactId));
                    buf.skip(dataRemaining);
                    continue;
            }
            while (dataRemaining > 0) {
                if (buf.readShortBE() == MmpProtocol.TAG_SEQUENCE_MARKER) {
                    this.account.groupSequenceId = contactId;
                }
                int entryLen = buf.readShortBE();
                buf.skip(entryLen);
                dataRemaining -= entryLen + 4;
            }
        }
        this.account.contactListIndex = contactCount;
        return results;
    }

    private void finalizeContactList() {
        this.account.sendData(ProtocolFactory.createMmpCommand(this.account, MmpCommand.CONTACT_LIST_ACK, (ByteBuffer) null));
        int savedSequenceId = this.account.groupSequenceId;
        if (savedSequenceId != 0) {
            this.account.sendData(this.account.sendContactListRequest(savedSequenceId));
        }
        this.account.sendData(ProtocolFactory.createMmpCommand(this.account, MmpCommand.SET_CAPABILITIES, new ByteBuffer().writeCharBytes(StringPool.get(PackedStringKeys.MMP_TRANSFER_HEADER))));
        this.account.sendData(this.account.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this.account, MmpCommand.SEARCH, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(this.account.serverId).writeShortLE(SEARCH_HISTORY_MARKER).writeShortBE(0)), ObjectPool.integerOf(RESP_HISTORY)}));
        applyPermissionMaps();
        this.account.contactsByIdMap.clear();
        this.account.contactGroupsMap.clear();
        this.account.additionalDataMap.clear();
        if (this.account.groups.size() == 0) {
            this.account.sendData(this.account.sendAddGroupCommand(StringPool.get(PackedStringKeys.XMPP_GROUP_GENERAL)));
        }
        this.account.progress = MmpProtocol.PROGRESS_CONNECTED;
        this.account.msgCount = MmpProtocol.PROGRESS_CONNECTED;
    }

    private void applyPermissionMaps() {
        Enumeration elements = this.account.contactMap.elements();
        while (elements.hasMoreElements()) {
            MmpContact contact = (MmpContact) elements.nextElement();
            Object deleteId = this.account.contactsByIdMap.get(contact.identifier);
            if (deleteId != null) {
                contact.canDelete = ((Integer) deleteId).intValue();
            }
            Object blockId = this.account.contactGroupsMap.get(contact.identifier);
            if (blockId != null) {
                contact.canBlock = ((Integer) blockId).intValue();
            }
            Object unblockId = this.account.additionalDataMap.get(contact.identifier);
            if (unblockId != null) {
                contact.canUnblock = ((Integer) unblockId).intValue();
            }
        }
    }

    private boolean handleContactInfoResponse(ByteBuffer buf, int responseFlags, Object[] cmdEntry) {
        buf.skip(10);
        if (buf.readShortLE() == SERVER_RESPONSE_MARKER) {
            buf.readShortLE();
            int subType = buf.readShortLE();
            buf.readByte();
            ContactInfo info = (ContactInfo) AppState.getObject(RegistrationKeys.SLOT_REG_PARAM_2);
            if (info == null) {
                info = ContactInfo.createAccountInfo(this.account);
            }
            switch (subType) {
                case SUBTYPE_USER_BASIC:
                    String nickName = buf.readPascalStr();
                    ContactInfo basicInfo = info.setDisplayName(nickName).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr()).setCustomField1(buf.readPascalStr());
                    buf.readPascalStr();
                    basicInfo.setJobTitle(buf.readPascalStr()).setCustomField2(buf.readPascalStr()).setCustomField3(buf.readPascalStr()).setCustomField4(buf.readPascalStr());
                    if (this.account.serverId == ((Integer) cmdEntry[2]).intValue()) {
                        this.account.setDisplayName(nickName);
                    }
                    break;
                case SUBTYPE_USER_EXTENDED:
                    ContactInfo extInfo = info.setAge(buf.readShortLE()).setMaritalStatus(buf.readByte()).setCustomField6(buf.readPascalStr());
                    int birthYear = buf.readShortLE();
                    byte birthDay = buf.readByte();
                    byte birthMonth = buf.readByte();
                    if (birthMonth >= 0) {
                        extInfo.setCompany(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Utils.zeroPad(birthMonth + 1)).append('/').append(Utils.zeroPad(birthDay)).append('/').append(birthYear)));
                    }
                    break;
                case SUBTYPE_USER_ABOUT:
                    info.setCustomField5(buf.readPascalStr());
                    break;
            }
            boolean isInfoComplete = (responseFlags & 1) == 0;
            if (isInfoComplete) {
                RegistrationState.setParam1(AppState.getObject(RegistrationKeys.SLOT_REG_PARAM_2));
                RegistrationState.clearParam2();
            }
            return isInfoComplete;
        }
        return true;
    }

    private boolean handleHistoryResponse(ByteBuffer buf) {
        int prevReserved = this.account.reserved1;
        this.account.reserved1 = prevReserved + 1;
        if (prevReserved != 0) {
            SessionState.setMrimDataLoaded(0);
        }
        buf.skip(10);
        int historySubType = buf.readShortLE();
        buf.readShortLE();
        switch (historySubType) {
            case SUBTYPE_HISTORY_ENTRY:
                int senderId = buf.readInt();
                int year = buf.readShortLE();
                byte month = buf.readByte();
                byte dayOfMonth = buf.readByte();
                byte hour = buf.readByte();
                byte minute = buf.readByte();
                byte febDays = (year % 4 != 0 || year == CENTURY_YEAR) ? (byte) FEB_DAYS_NORMAL : (byte) FEB_DAYS_LEAP;
                int totalDays = (((((year - EPOCH_YEAR) * DAYS_IN_YEAR) + ((year - LEAP_CYCLE_START) / 4)) + dayOfMonth) + FEB_DAYS_NORMAL) - febDays;
                if (year >= CENTURY_YEAR) {
                    totalDays--;
                }
                byte[] monthDays = AppState.getBytes(StringResKeys.RES_MONTH_DAYS);
                int monthIndex = 0;
                while (monthIndex < month - 1) {
                    totalDays += monthIndex == 1 ? febDays : monthDays[monthIndex];
                    monthIndex++;
                }
                long timestampMs = MILLIS_PER_SECOND * ((SECONDS_PER_DAY * totalDays) + (hour * SECONDS_PER_HOUR) + (minute * SECONDS_PER_MINUTE));
                buf.readShortBE();
                if (senderId != OFFLINE_MSG_FILTERED) {
                    this.account.onMessage(Integer.toString(senderId), timestampMs, buf.readModifiedStr());
                }
                return false;
            case SUBTYPE_HISTORY_END:
                SessionState.setMrimDataLoaded(1);
                this.account.trySendData(ProtocolFactory.createMmpCommand(this.account, MmpCommand.SEARCH, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(this.account.serverId).writeShortLE(SEARCH_HISTORY_FULL_MARKER).writeShortBE(0)));
                return false;
            default:
                return false;
        }
    }

    private boolean handleSearchResponse(ByteBuffer buf) {
        Vector searchResults = RegistrationState.getParam3();
        buf.skip(10);
        if (buf.readShortLE() != SERVER_RESPONSE_MARKER) {
            return true;
        }
        buf.readShortLE();
        int searchSubType = buf.readShortLE();
        if ((SUBTYPE_SEARCH_RESULT == searchSubType || SUBTYPE_SEARCH_LAST == searchSubType) && buf.readByte() == SEARCH_FIELD_COUNT) {
            buf.readShortBE();
            ContactInfo searchResult = ContactInfo.createAccountInfo(this.account).setMmpContactId(buf.readInt()).setDisplayName(buf.readPascalStr()).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr());
            buf.readByte();
            searchResults.addElement(searchResult.setMmpTypeId(buf.readShortLE()).setMaritalStatus(buf.readByte()).setAge(buf.readShortLE()));
        }
        if (searchSubType == SUBTYPE_SEARCH_LAST) {
            RegistrationState.setParam4(RegistrationState.getParam3());
            RegistrationState.clearParam3();
            return true;
        }
        return false;
    }

    private boolean handleMovePhase1(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            MmpContact movedContact = (MmpContact) cmdEntry[2];
            MmpContactGroup srcGroup = (MmpContactGroup) cmdEntry[3];
            this.account.trySendData(this.account.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this.account, MmpCommand.MODIFY_CONTACT, srcGroup.createUpdatePacket(srcGroup.name, movedContact.userId, -1)), ObjectPool.integerOf(RESP_MOVE_PHASE2), movedContact, srcGroup, cmdEntry[4]}));
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleMovePhase2(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            MmpContact movedContact = (MmpContact) cmdEntry[2];
            MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
            this.account.trySendData(this.account.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this.account, MmpCommand.ADD_CONTACT, movedContact.encodeContactUpdate(4, movedContact.displayName, destGroup.groupId)), ObjectPool.integerOf(RESP_MOVE_PHASE3), movedContact, cmdEntry[3], destGroup}));
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleMovePhase3(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            MmpContact movedContact = (MmpContact) cmdEntry[2];
            MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
            this.account.trySendData(this.account.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this.account, MmpCommand.MODIFY_CONTACT, destGroup.createUpdatePacket(destGroup.name, -1, movedContact.userId)), ObjectPool.integerOf(RESP_MOVE_PHASE4), movedContact, cmdEntry[3], destGroup}));
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleMovePhase4(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            MmpContactGroup srcGroup = (MmpContactGroup) cmdEntry[3];
            MmpContact movedContact = (MmpContact) cmdEntry[2];
            srcGroup.removeElement(movedContact);
            MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
            destGroup.addContact((Object) movedContact);
            movedContact.onlineSemaphore = destGroup.groupId;
            this.account.trySendData(this.account.createSyncContactsCmd());
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleAddContactPhase1(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
            this.account.trySendData(this.account.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this.account, MmpCommand.MODIFY_CONTACT, destGroup.createUpdatePacket(destGroup.name, -1, ((Integer) cmdEntry[5]).intValue())), ObjectPool.integerOf(RESP_ADD_CONTACT_PHASE2), cmdEntry[2], cmdEntry[3], destGroup, cmdEntry[5], cmdEntry[6]}));
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleAddContactPhase2(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
            MmpContact newContact = new MmpContact(this.account, ((Integer) cmdEntry[5]).intValue(), destGroup.groupId, (String) cmdEntry[2], (String) cmdEntry[3], true);
            destGroup.addContact((Object) newContact);
            this.account.trySendData(this.account.createSyncContactsCmd());
            this.account.trySendData(this.account.createGetContactsCmd());
            this.account.trySendData(this.account.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this.account, MmpCommand.MODIFY_CONTACT, newContact.encodeContactUpdate(5, newContact.displayName, newContact.onlineSemaphore)), ObjectPool.integerOf(RESP_ADD_CONTACT_PHASE3), cmdEntry[2], cmdEntry[3], cmdEntry[4], cmdEntry[5], cmdEntry[6], newContact}));
            this.account.trySendData(this.account.createSyncContactsCmd());
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleAddContactPhase3(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            this.account.trySendData(this.account.createSyncContactsCmd());
            this.account.trySendData(MmpMessageParser.createSendMessageCmd(this.account, (MmpContact) cmdEntry[7], (String) cmdEntry[6]));
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleUpdatePermissionsResponse(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            ((MmpContact) cmdEntry[2]).updatePermissionFlags(((Integer) cmdEntry[3]).intValue(), ((Integer) cmdEntry[4]).intValue());
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleRemovePermissionsResponse(ByteBuffer buf, Object[] cmdEntry) {
        int status = buf.readShortBE();
        if (status == 0) {
            ((MmpContact) cmdEntry[2]).updatePermissionFlags(((Integer) cmdEntry[3]).intValue(), 0);
        } else {
            EventDispatcher.postRenameError(cmdEntry, status);
        }
        return true;
    }

    private boolean handleNewContactSearchResponse(ByteBuffer buf) {
        buf.skip(10);
        if (buf.readShortLE() != SERVER_RESPONSE_MARKER) {
            return false;
        }
        buf.readShortLE();
        int searchSubType = buf.readShortLE();
        if ((SUBTYPE_SEARCH_RESULT == searchSubType || SUBTYPE_SEARCH_LAST == searchSubType) && buf.readByte() == SEARCH_FIELD_COUNT) {
            buf.readShortBE();
            ContactInfo searchResult = ContactInfo.createAccountInfo(this.account).setMmpContactId(buf.readInt()).setDisplayName(buf.readPascalStr()).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr());
            buf.readByte();
            searchResult.setMmpTypeId(buf.readShortLE()).setMaritalStatus(buf.readByte()).setAge(buf.readShortLE());
            MmpContact foundContact = (MmpContact) this.account.contactMap.get(searchResult.getString(60));
            if (foundContact != null) {
                foundContact.setDisplayName(searchResult.getDisplayNameOrId());
            }
        }
        return searchSubType == SUBTYPE_SEARCH_LAST;
    }
}
