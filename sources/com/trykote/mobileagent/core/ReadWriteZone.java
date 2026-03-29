package com.trykote.mobileagent.core;

import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.mrim.MrimContact;

import javax.microedition.lcdui.Image;
import java.util.Vector;

/**
 * Read-write access to the AppState pool (runtime state, flags, counters).
 * Delegates all operations to AppState.
 */
public final class ReadWriteZone extends StorageZone {

    // --- Reads ---

    public String getString(int key) {
        return AppState.getString(key);
    }

    public int getInt(int key) {
        return AppState.getInt(key);
    }

    public boolean getBool(int key) {
        return AppState.getBool(key);
    }

    public long getLong(int key) {
        return AppState.getLong(key);
    }

    public byte[] getBytes(int key) {
        return AppState.getBytes(key);
    }

    public Vector getVector(int key) {
        return AppState.getVector(key);
    }

    public Object getObject(int key) {
        return AppState.pool[key];
    }

    // --- Writes ---

    public void setString(int key, String value) {
        AppState.setString(key, value);
    }

    public void setInt(int key, int value) {
        AppState.setInt(key, value);
    }

    public boolean setBool(int key, boolean value) {
        return AppState.setBool(key, value);
    }

    public void setLong(int key, long value) {
        AppState.setLong(key, value);
    }

    public Object setObject(int key, Object value) {
        return AppState.setObject(key, value);
    }

    public void addInt(int key, int value) {
        AppState.addInt(key, value);
    }

    public boolean toggleBool(int key) {
        return AppState.toggleBool(key);
    }

    public int getAndClearInt(int key) {
        return AppState.getAndClearInt(key);
    }

    public void clearIndex(int key) {
        AppState.clearIndex(key);
    }

    public void clearRange(int from, int to) {
        AppState.clearRange(from, to);
    }

    public void resetToEmpty(int key) {
        AppState.resetToEmpty(key);
    }

    public void setFromBuffer(int key, StringBuffer sb) {
        AppState.setFromBuffer(key, sb);
    }

    public void setFromPool(int key, int sourceKey) {
        AppState.setFromPool(key, sourceKey);
    }

    public void setStringIndirect(int key, String value) {
        AppState.setStringIndirect(key, value);
    }

    public void setIntIndirect(int key, int value) {
        AppState.setIntIndirect(key, value);
    }

    public Object[] getObjectArray(int key) {
        return AppState.getObjectArray(key);
    }

    public Image getImage(int key) {
        return AppState.getImage(key);
    }

    // --- Convenience typed accessors ---

    public Account getAccount() {
        return AppState.getAccount();
    }

    public void setAccount(Object account) {
        AppState.setAccount(account);
    }

    public Contact getCurrentContact() {
        return AppState.getCurrentContact();
    }

    public MrimContact getCurrentMrimContact() {
        return AppState.getCurrentMrimContact();
    }

    public com.trykote.mobileagent.model.ContactGroup getCurrentGroup() {
        return AppState.getCurrentGroup();
    }

    public void setCurrentEntity(Object entity) {
        AppState.setCurrentEntity(entity);
    }

    public com.trykote.mobileagent.ui.MainCanvas getCanvas() {
        return AppState.getCanvas();
    }

    public com.trykote.mobileagent.core.Midlet getMidlet() {
        return AppState.getMidlet();
    }

    public com.trykote.mobileagent.ui.GraphicsContext getGfxContext(int index) {
        return AppState.getGfxContext(index);
    }

    public String getAppProperty(int key) {
        return AppState.getAppProperty(key);
    }

    public int getHeight() {
        return AppState.getHeight();
    }

    public void setDimensions(int width, int height) {
        AppState.setDimensions(width, height);
    }

    public javax.microedition.lcdui.Font getFont() {
        return AppState.getFont();
    }

    public int getIntOffset(int index) {
        return AppState.getIntOffset(index);
    }

    public java.util.Calendar getCalendar() {
        return AppState.getCalendar();
    }

    public int getDateCode() {
        return AppState.getDateCode();
    }

    public String getEllipsis() {
        return AppState.getEllipsis();
    }

    public void setScreen(Object screen) {
        AppState.setScreen(screen);
    }

    public boolean hasMemory() {
        return AppState.hasMemory();
    }

    public void updateTime() {
        AppState.updateTime();
    }

    public void saveDelta(boolean chunked) {
        AppState.saveDelta(chunked);
    }
}
