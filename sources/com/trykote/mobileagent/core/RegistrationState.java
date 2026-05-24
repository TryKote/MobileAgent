package com.trykote.mobileagent.core;

import com.trykote.mobileagent.key.RegistrationKeys;

import java.util.Vector;

/**
 * Typed facade for registration/search state in AppState.
 * Delegates to AppState — zero runtime overhead.
 */
public final class RegistrationState extends AppState {
    private RegistrationState() {}

    public static final RegistrationState INSTANCE = new RegistrationState();

    // --- Registration flow ---

    public static boolean isRegistrationDone() {
        return getBool(RegistrationKeys.FLAG_REGISTRATION_DONE);
    }

    public static void setRegistrationDone(boolean done) {
        setInt(RegistrationKeys.FLAG_REGISTRATION_DONE, done ? 1 : 0);
    }

    public static Object[] getRegistrationData() {
        return getObjectArray(RegistrationKeys.OBJ_REGISTRATION_DATA);
    }

    public static void setRegistrationData(Object data) {
        setObject(RegistrationKeys.OBJ_REGISTRATION_DATA, data);
    }

    public static void clearRegistrationData() {
        clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
    }

    public static boolean isRegSmsMode() {
        return getBool(RegistrationKeys.FLAG_REG_SMS_MODE);
    }

    public static void setRegSmsMode(boolean mode) {
        setInt(RegistrationKeys.FLAG_REG_SMS_MODE, mode ? 1 : 0);
    }

    public static boolean isUseSslTls() {
        return getBool(RegistrationKeys.FLAG_USE_SSL_TLS);
    }

    public static void setUseSslTls(boolean useSsl) {
        setInt(RegistrationKeys.FLAG_USE_SSL_TLS, useSsl ? 1 : 0);
    }

    public static int getRegDomainIndex() {
        return getInt(RegistrationKeys.INT_REG_DOMAIN_INDEX);
    }

    public static void setRegDomainIndex(int index) {
        setInt(RegistrationKeys.INT_REG_DOMAIN_INDEX, index);
    }

    // --- Credentials ---

    public static String getPassword() {
        return getString(RegistrationKeys.SLOT_PASSWORD);
    }

    public static void setPassword(Object password) {
        setObject(RegistrationKeys.SLOT_PASSWORD, password);
    }

    public static void clearPassword() {
        clearIndex(RegistrationKeys.SLOT_PASSWORD);
    }

    public static String getDeviceId() {
        return getString(RegistrationKeys.SLOT_DEVICE_ID);
    }

    public static void setDeviceId(Object deviceId) {
        setObject(RegistrationKeys.SLOT_DEVICE_ID, deviceId);
    }

    // --- Personal info ---

    public static String getFirstName() {
        return getString(RegistrationKeys.SLOT_FIRST_NAME);
    }

    public static void setFirstName(Object name) {
        setObject(RegistrationKeys.SLOT_FIRST_NAME, name);
    }

    public static String getLastName() {
        return getString(RegistrationKeys.SLOT_LAST_NAME);
    }

    public static void setLastName(Object name) {
        setObject(RegistrationKeys.SLOT_LAST_NAME, name);
    }

    // --- Search query & results ---

    public static String getSearchQuery() {
        return getString(RegistrationKeys.SLOT_SEARCH_QUERY);
    }

    public static void clearSearchQuery() {
        clearIndex(RegistrationKeys.SLOT_SEARCH_QUERY);
    }

    public static Object getSearchResult() {
        return getPoolObject(RegistrationKeys.OBJ_SEARCH_RESULT);
    }

    public static void setSearchResult(Object result) {
        setObject(RegistrationKeys.OBJ_SEARCH_RESULT, result);
    }

    public static String getSearchResultName() {
        return getString(RegistrationKeys.SLOT_SEARCH_RESULT);
    }

    public static void setSearchResultName(Object name) {
        setObject(RegistrationKeys.SLOT_SEARCH_RESULT, name);
    }

    public static void clearSearchResultName() {
        clearIndex(RegistrationKeys.SLOT_SEARCH_RESULT);
    }

    public static int getSearchResultCount() {
        return getInt(RegistrationKeys.COUNTER_SEARCH_RESULTS);
    }

    public static void addSearchResultCount(int delta) {
        addInt(RegistrationKeys.COUNTER_SEARCH_RESULTS, delta);
    }

    // --- Search form fields (1-6) ---

    public static String getSearchField(int index) {
        return getString(RegistrationKeys.SLOT_SEARCH_FIELD_1 + index - 1);
    }

    public static void clearSearchFields() {
        for (int i = RegistrationKeys.SLOT_SEARCH_FIELD_1; i <= RegistrationKeys.SLOT_SEARCH_FIELD_6; i++) {
            clearIndex(i);
        }
    }

    public static void setSearchLabel(StringBuffer buf) {
        setFromBuffer(RegistrationKeys.SLOT_SEARCH_LABEL_1, buf);
    }

    public static void clearSearchLabel() {
        clearIndex(RegistrationKeys.SLOT_SEARCH_LABEL_1);
    }

    // --- Search parameters ---

    public static int getSearchGender() {
        return getInt(RegistrationKeys.INT_SEARCH_GENDER);
    }

    public static void setSearchGender(int gender) {
        setInt(RegistrationKeys.INT_SEARCH_GENDER, gender);
    }

    public static int getSearchAge() {
        return getInt(RegistrationKeys.INT_SEARCH_AGE);
    }

    public static void setSearchAge(int age) {
        setInt(RegistrationKeys.INT_SEARCH_AGE, age);
    }

    public static int getSearchCountry() {
        return getInt(RegistrationKeys.INT_SEARCH_COUNTRY);
    }

    public static void setSearchCountry(int country) {
        setInt(RegistrationKeys.INT_SEARCH_COUNTRY, country);
    }

    public static int getSearchRegion() {
        return getInt(RegistrationKeys.INT_SEARCH_REGION);
    }

    public static void setSearchRegion(int region) {
        setInt(RegistrationKeys.INT_SEARCH_REGION, region);
    }

    public static int getSearchCity() {
        return getInt(RegistrationKeys.INT_SEARCH_CITY);
    }

    public static void setSearchCity(int city) {
        setInt(RegistrationKeys.INT_SEARCH_CITY, city);
    }

    public static int getSearchParam1() {
        return getInt(RegistrationKeys.INT_SEARCH_PARAM_1);
    }

    public static void setSearchParam1(int value) {
        setInt(RegistrationKeys.INT_SEARCH_PARAM_1, value);
    }

    public static int getSearchParam2() {
        return getInt(RegistrationKeys.INT_SEARCH_PARAM_2);
    }

    public static void setSearchParam2(int value) {
        setInt(RegistrationKeys.INT_SEARCH_PARAM_2, value);
    }

    public static int getSearchParam3() {
        return getInt(RegistrationKeys.INT_SEARCH_PARAM_3);
    }

    public static void setSearchParam3(int value) {
        setInt(RegistrationKeys.INT_SEARCH_PARAM_3, value);
    }

    public static boolean isSearchOnlineOnly() {
        return getBool(RegistrationKeys.FLAG_SEARCH_ONLINE_ONLY);
    }

    public static void setSearchOnlineOnly(boolean onlineOnly) {
        setInt(RegistrationKeys.FLAG_SEARCH_ONLINE_ONLY, onlineOnly ? 1 : 0);
    }

    // --- Region/country data ---

    public static int getRegionCode() {
        return getInt(RegistrationKeys.INT_REGION_CODE);
    }

    public static void setRegionCode(int code) {
        setInt(RegistrationKeys.INT_REGION_CODE, code);
    }

    public static int getCountryCode() {
        return getInt(RegistrationKeys.INT_COUNTRY_CODE);
    }

    public static void setCountryCode(int code) {
        setInt(RegistrationKeys.INT_COUNTRY_CODE, code);
    }

    public static Vector getRegionData() {
        return getVector(RegistrationKeys.SLOT_REG_FIELD_2);
    }

    public static void setRegionData(Object data) {
        setObject(RegistrationKeys.SLOT_REG_FIELD_2, data);
    }

    public static void setRegionNames(StringBuffer buf) {
        setFromBuffer(RegistrationKeys.SLOT_REG_FIELD_1, buf);
    }

    // --- General-purpose temp slots (SLOT_REG_PARAM_1..4) ---

    public static Object getParam1() {
        return getPoolObject(RegistrationKeys.SLOT_REG_PARAM_1);
    }

    public static void setParam1(Object value) {
        setObject(RegistrationKeys.SLOT_REG_PARAM_1, value);
    }

    public static void clearParam1() {
        clearIndex(RegistrationKeys.SLOT_REG_PARAM_1);
    }

    public static Object[] getParam2() {
        return getObjectArray(RegistrationKeys.SLOT_REG_PARAM_2);
    }

    public static void setParam2(Object value) {
        setObject(RegistrationKeys.SLOT_REG_PARAM_2, value);
    }

    public static void clearParam2() {
        clearIndex(RegistrationKeys.SLOT_REG_PARAM_2);
    }

    public static Vector getParam3() {
        return getVector(RegistrationKeys.SLOT_REG_PARAM_3);
    }

    public static void setParam3(Object value) {
        setObject(RegistrationKeys.SLOT_REG_PARAM_3, value);
    }

    public static void clearParam3() {
        clearIndex(RegistrationKeys.SLOT_REG_PARAM_3);
    }

    public static Object getParam4() {
        return getPoolObject(RegistrationKeys.SLOT_REG_PARAM_4);
    }

    public static void setParam4(Object value) {
        setObject(RegistrationKeys.SLOT_REG_PARAM_4, value);
    }

    public static void clearParam4() {
        clearIndex(RegistrationKeys.SLOT_REG_PARAM_4);
    }

    // --- Bulk resets ---

    public static void resetSearchParams() {
        setInt(RegistrationKeys.INT_REGION_CODE, 0);
        setInt(RegistrationKeys.INT_COUNTRY_CODE, 0);
        setInt(RegistrationKeys.INT_SEARCH_PARAM_1, -1);
        setInt(RegistrationKeys.INT_SEARCH_PARAM_2, -1);
        setInt(RegistrationKeys.INT_SEARCH_PARAM_3, 0);
        setInt(RegistrationKeys.INT_SEARCH_COUNTRY, 0);
        setInt(RegistrationKeys.INT_SEARCH_REGION, 0);
        setInt(RegistrationKeys.INT_SEARCH_CITY, 0);
        setInt(RegistrationKeys.INT_SEARCH_AGE, 0);
        setInt(RegistrationKeys.INT_SEARCH_GENDER, 0);
        setInt(RegistrationKeys.FLAG_SEARCH_ONLINE_ONLY, 0);
    }
}
