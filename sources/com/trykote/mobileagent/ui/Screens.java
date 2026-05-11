package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.handler.ScreenHandler;

/**
 * Screen factory methods.
 * Generated from config.json — do not edit manually.
 */
public final class Screens {
    private Screens() {}

    /** MAP_VIEW */
    public static Screen mapView(ScreenHandler handler) {
        Screen s = new Screen(1, 6, handler);
        s.configureHeader(-1, 1038);
        s.configureSoftKeys(1062, 20, 328, 0, 0);
        return s;
    }

    /** MAP_VIEW_ALT */
    public static Screen mapViewAlt(ScreenHandler handler) {
        Screen s = new Screen(1, 6, handler);
        s.configureHeader(-1, 1038);
        s.addLabelSeparator(332); // В вашем телефоне недостаточно оперативной памят...
        s.configureSoftKeys(0, 0, 1050, 4, 0);
        return s;
    }

    /** VIEW_MODE */
    public static Screen viewMode(ScreenHandler handler) {
        Screen s = new Screen(10, 91, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(311, 334, 0);
        s.addActionById(312, 335, 1);
        s.addActionById(363, 344, 120); // Водные процедуры
        s.addConditionalIf(1418, 2, 310, 336); // UIKeys.FLAG_ONLINE_CUSTOM_OFF
        s.addConditionalIf(1419, 3, 310, 337); // UIKeys.FLAG_ONLINE_CUSTOM_ON
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** GPS_SETTINGS */
    public static Screen gpsSettings(ScreenHandler handler) {
        Screen s = new Screen(3, 20, handler);
        s.configureHeader(0, 1038);
        s.addActionById(361, 340, 0);
        s.addActionById(303, 339, 100); // Сообщать о дорожной обстановке от имени:
        s.addConditionalIf(276, 1, 365, 348); // MapKeys.FLAG_MAP_VIEW_ACTIVE
        s.addConditionalIf(277, 2, 6, 347); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.addConditionalIf(1422, 3, 310, 341); // MapKeys.FLAG_GPS_NO_MAP
        s.addConditionalIf(1423, 4, 310, 342); // MapKeys.FLAG_GPS_WITH_MAP
        s.addActionById(230, 345, 174); // Сплю
        s.addActionById(313, 343, 91); // Сообщать о пробках
        s.addConditionalIf(277, 119, 229, 346); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.addActionById(12, 500, 131); // Кино
        s.addActionById(10, 1049, 5); // Эта операция доступна только при отсутствии сое...
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** REGION_SELECTOR — Выбор карты */
    public static Screen regionSelector(ScreenHandler handler) {
        Screen s = new Screen(2, 97, handler);
        s.configureHeader(315, 383);
        s.showCheckboxes = true;
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** MAP_POINTS — Поиск */
    public static Screen mapPoints(ScreenHandler handler) {
        Screen s = new Screen(0, 100, handler);
        s.configureHeader(303, 1061);
        s.addTextInput(350, 255, 424, 0, 1248);
        s.configureSoftKeys(1061, 199, 1050, 12, 199);
        return s;
    }

    /** MAP_OVERLAY */
    public static Screen mapOverlay(ScreenHandler handler) {
        Screen s = new Screen(2, 101, handler);
        s.configureHeader(303, 1038);
        s.configureSoftKeys(1048, 6, 1050, 12, 199);
        return s;
    }

    /** MAP_TOOLTIP — Ссылка на карту */
    public static Screen mapTooltip(ScreenHandler handler) {
        Screen s = new Screen(0, 110, handler);
        s.configureHeader(308, 353);
        s.addTextInput(371, 63, 424, 0, 1249);
        s.configureSoftKeys(1060, 111, 1050, 12, 0);
        return s;
    }

    /** PEOPLE_NEARBY — Кому отправить */
    public static Screen peopleNearby(ScreenHandler handler) {
        Screen s = new Screen(0, 111, handler);
        s.configureHeader(308, 378);
        s.configureSoftKeys(1060, 6, 1050, 12, 6);
        return s;
    }

    /** XMPP_MAP_CONTEXT */
    public static Screen xmppMapContext(ScreenHandler handler) {
        Screen s = new Screen(11, 113, handler);
        s.configureHeader(303, 1038);
        s.addConditionalIf(1432, 0, 0, 367);
        s.addConditionalIf(1436, 1, 220, 503);
        s.addConditionalIf(1429, 2, 17, 716); // Эта учетная запись подключена или находится в с...
        s.addConditionalIf(1433, 3, 7, 552); // Эта учетная запись не подключена к серверу.
        s.addConditionalIf(1431, 4, 6, 368); // Эта операция доступна только при соединении с с...
        s.addConditionalIf(1430, 5, 6, 368); // Эта операция доступна только при отсутствии сое...
        s.addConditionalIf(1428, 6, 361, 330); // Имя не может быть пустым.
        s.addConditionalIf(1428, 7, 369, 365); // Заполните текст запроса авторизации.
        s.addConditionalIf(1427, 8, 9, 364); // Группа не пуста. Удаление невозможно.
        s.addConditionalIf(1426, 9, 9, 718); // Операция недопустима над специальными группами.
        s.addConditionalIf(1574, 10, 215, 359); // UIKeys.FLAG_ROUTE_POINT_VISIBLE
        s.addConditionalIf(1575, 11, 224, 360); // UIKeys.FLAG_ROUTE_POINT_HIDDEN
        s.addConditionalIf(1546, 12, 34, 362); // MapKeys.FLAG_CHAT_HAS_ITEMS
        s.addConditionalIf(1424, 13, 34, 363); // Местоположение не определено
        s.addConditionalIf(277, 14, 228, 357); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.addConditionalIf(277, 15, 229, 358); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.addConditionalIf(1434, 16, 223, 361); // , запись в блог, 
        s.addConditionalUnless(1425, 17, 362, 374); // Псевдоним, Имя, Фамилия, E-mail, ... (12 total)
        s.addConditionalIf(1437, 18, 361, 366); // Город: 
        s.addConditionalIf(1435, 19, 308, 355); // Страница: 
        s.addConditionalIf(1425, 20, 34, 356); // Возраст: 
        s.addActionById(311, 334, 21); // Примечания: 
        s.addActionById(312, 335, 22); // Клиент: 
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** SAVE_LOCATION — Сохранить закладку */
    public static Screen saveLocation(ScreenHandler handler) {
        Screen s = new Screen(0, 114, handler);
        s.configureHeader(360, 373);
        s.addTextInput(371, 63, 424, 0, 1250);
        s.configureSoftKeys(1053, 6, 1050, 12, 6);
        return s;
    }

    /** MAP_ROUTE */
    public static Screen mapRoute(ScreenHandler handler) {
        Screen s = new Screen(2, 116, handler);
        s.configureHeader(303, 1038);
        s.configureSoftKeys(1048, 118, 1050, 12, 199);
        return s;
    }

    /** MAP_STATUS */
    public static Screen mapStatus(ScreenHandler handler) {
        Screen s = new Screen(10, 117, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(362, 374, 114); // Выбрать на карте
        s.addActionById(360, 1251, 120); // Водные процедуры
        s.addConditionalUnless(253, 199, 360, 376); // MapKeys.FLAG_GPS_ACTIVE
        s.addConditionalIf(253, 199, 360, 377); // MapKeys.FLAG_GPS_ACTIVE
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** SEND_TO_CONTACT — Кому отправить */
    public static Screen sendToContact(ScreenHandler handler) {
        Screen s = new Screen(0, 118, handler);
        s.configureHeader(16, 378);
        s.configureSoftKeys(1060, 6, 1050, 12, 6);
        return s;
    }

    /** CHAT_ROOM_OPTIONS */
    public static Screen chatRoomOptions(ScreenHandler handler) {
        Screen s = new Screen(10, 119, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(228, 357, 0);
        s.addActionById(229, 358, 1);
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** MAP_ROUTE_SELECT — Мои закладки */
    public static Screen mapRouteSelect(ScreenHandler handler) {
        Screen s = new Screen(0, 120, handler);
        s.configureHeader(360, 344);
        s.configureSoftKeys(1059, 199, 1050, 12, 199);
        return s;
    }

    /** CHAT_LIST_OPTIONS */
    public static Screen chatListOptions(ScreenHandler handler) {
        Screen s = new Screen(3, 121, handler);
        s.configureHeader(0, 1038);
        s.addActionById(308, 379, 6); // Имя не может быть пустым.
        s.addActionById(16, 1060, 118); // -нет-
        s.addActionById(34, 719, 120); // Водные процедуры
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** MMP_ACCOUNT_SELECT — Настройки */
    public static Screen mmpAccountSelect(ScreenHandler handler) {
        Screen s = new Screen(0, 129, handler);
        s.configureHeader(12, 500);
        s.addTextSeparator(393); // Устройство GPS:
        s.addPassword(239);
        s.addDropdown(381, 384, 44); // Включать встроенный GPS:
        s.addDropdown(382, 385, 45); // Выключать встроенный GPS через:
        s.addCheckbox(391, 255); // Корректировать GPS координаты
        s.addCheckbox(392, 238); // Предупреждать о маневрах
        s.addCheckbox(386, 47); // Сообщать о пробках
        s.addTextSeparator(394); // Хранение данных:
        s.addCheckbox(389, 256); // Использовать карты на флэш-диске
        s.addCheckbox(388, 257); // Сохранять загружаемые карты на флэш-диск
        s.addLabelSeparator(387); // Использовать карты из каталога:
        s.addLogin(390, 233); // */
        s.addTextSeparator(759); // Прочее:
        s.addDropdown(395, 1252, 1438); // Сообщать о дорожной обстановке от имени:
        s.addCheckbox(663, 285); // Автоматически обновлять мое местоположение (Cel...
        s.configureSoftKeys(1053, 12, 1050, 12, 199);
        return s;
    }

    /** PEOPLE_SEARCH */
    public static Screen peopleSearch(ScreenHandler handler) {
        Screen s = new Screen(10, 131, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(315, 398, 97); // Предупреждать о маневрах
        s.addConditionalIf(1439, 6, 219, 400); // Имя не может быть пустым.
        s.addConditionalIf(1440, 6, 226, 401); // Имя не может быть пустым.
        s.addActionById(34, 402, 6); // Имя не может быть пустым.
        s.addActionById(12, 399, 129); // Музыка
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** SAVED_LOCATIONS */
    public static Screen savedLocations(ScreenHandler handler) {
        Screen s = new Screen(2, 153, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.configureSoftKeys(1048, 154, 1050, 12, 154);
        return s;
    }

    /** SHARE_LOCATION — Комментарий */
    public static Screen shareLocation(ScreenHandler handler) {
        Screen s = new Screen(0, 154, handler);
        s.configureHeader(303, 405);
        s.addTextInput(405, 255, 424, 0, 1254);
        s.configureSoftKeys(1060, 6, 1050, 6, 6);
        return s;
    }

    /** GROUP_MANAGEMENT_ALT — Выбор */
    public static Screen groupManagementAlt(ScreenHandler handler) {
        Screen s = new Screen(0, 155, handler);
        s.configureHeader(230, 406);
        s.configureSoftKeys(1053, 6, 1050, 6, 0);
        return s;
    }

    /** MAP_SEARCH */
    public static Screen mapSearch(ScreenHandler handler) {
        Screen s = new Screen(11, 158, handler);
        s.configureHeader(0, 1038);
        s.addLabelSeparator(0);
        s.addConditionalIf(1441, 0, 361, 409);
        s.addActionById(308, 410, 6); // Имя не может быть пустым.
        s.addActionById(303, 1061, 100); // Сообщать о дорожной обстановке от имени:
        s.addActionById(360, 1251, 120); // Водные процедуры
        s.configureSoftKeys(1048, 199, 1050, 6, 199);
        return s;
    }

    /** WIFI_ACCOUNT_LIST */
    public static Screen wifiAccountList(ScreenHandler handler) {
        Screen s = new Screen(2, 159, handler);
        s.configureHeader(0, 1038);
        s.addLabelSeparator(412); // Сообщать о точках:
        s.addActionById(23, 413, 153); // В поисках...
        s.addLabelSeparator(414); // От имени учетной записи:
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** MAILBOX_OPTIONS */
    public static Screen mailboxOptions(ScreenHandler handler) {
        Screen s = new Screen(2, 167, handler);
        s.configureHeader(0, 1038);
        s.addActionById(308, 657, 0);
        s.addActionById(303, 659, 1);
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** FORM_LIST */
    public static Screen formList(ScreenHandler handler) {
        Screen s = new Screen(2, 170, handler);
        s.configureHeader(0, 1038);
        s.configureSoftKeys(1048, 113, 1050, 12, 113);
        return s;
    }

    /** MRIM_ACCOUNT_SELECT */
    public static Screen mrimAccountSelect(ScreenHandler handler) {
        Screen s = new Screen(2, 172, handler);
        s.configureHeader(0, 1038);
        s.addLabelSeparator(415); // Выберите учетную запись:
        s.configureSoftKeys(1048, 173, 1050, 6, 173);
        return s;
    }

    /** MAP_OPTIONS */
    public static Screen mapOptions(ScreenHandler handler) {
        Screen s = new Screen(10, 174, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addConditionalIf(276, 175, 6, 369); // MapKeys.FLAG_MAP_VIEW_ACTIVE
        s.addConditionalIf(277, 155, 230, 338); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** NEARBY_SETTINGS — Люди на карте */
    public static Screen nearbySettings(ScreenHandler handler) {
        Screen s = new Screen(0, 175, handler);
        s.configureHeader(12, 369);
        s.addCheckbox(417, 278); // Я
        s.addCheckbox(418, 279); // Мои контакты
        s.addCheckbox(548, 280); // Все
        s.configureSoftKeys(1053, 6, 1050, 12, 6);
        return s;
    }

    /** CONTACT_POPUP — Контакты */
    public static Screen contactPopup(ScreenHandler handler) {
        Screen s = new Screen(0, 176, handler);
        s.configureHeader(6, 1047);
        s.configureSoftKeys(1059, 199, 1055, 12, 199);
        return s;
    }

    /** SEARCH_ENTRY */
    public static Screen searchEntry(ScreenHandler handler) {
        Screen s = new Screen(3, 177, handler);
        s.configureHeader(0, 1038);
        s.addActionById(220, 503, 102); // Список карт еще не был загружен
        s.addActionById(17, 716, 0);
        s.addActionById(7, 419, 1);
        s.addConditionalIf(1445, 2, 6, 420); // UIKeys.FLAG_PHONE_HAS_NEXT
        s.addConditionalIf(1446, 3, 6, 421); // UIKeys.FLAG_PHONE_HAS_PREV
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** EDIT_SCREEN */
    public static Screen editScreen(ScreenHandler handler) {
        Screen s = new Screen(2, 178, handler);
        s.configureHeader(-1, 1038);
        s.addLabelSeparator(423); // Выберите режим использования:
        s.addActionById(6, 347, 0);
        s.addActionById(365, 348, 1);
        s.configureSoftKeys(1048, 6, 1055, 4, 6);
        return s;
    }

    /** STATUS_PREVIEW — Действия */
    public static Screen statusPreview(ScreenHandler handler) {
        Screen s = new Screen(0, 84, handler);
        s.configureHeader(9, 1059);
        s.addConditionalIf(1456, 40, -1, 1060); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1461, 93, -1, 471); // Здравствуйте, 
        s.addActionById(-1, 536, 123); // Пиво
        s.addActionById(-1, 472, 95); // С уважением
        s.addConditionalIf(1460, 63, -1, 473); // UIKeys.FLAG_RESOURCE_LOADING
        s.addConditionalIf(1456, 63, -1, 474); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1456, 94, -1, 475); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1456, 63, -1, 478); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addActionById(-1, 477, 40); // Отдалить [#]
        s.configureSoftKeys(1048, 199, 1050, 63, 199);
        return s;
    }

    /** CHOICE_DIALOG */
    public static Screen choiceDialog(ScreenHandler handler) {
        Screen s = new Screen(2, 32, handler);
        s.configureHeader(0, 1038);
        s.configureSoftKeys(1048, 12, 1050, 12, 12);
        return s;
    }

    /** ACCOUNT_LIST */
    public static Screen accountList(ScreenHandler handler) {
        Screen s = new Screen(3, 1, handler);
        s.configureHeader(0, 1038);
        s.addConditionalIf(1462, 15, 305, 495); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addConditionalUnless(1462, 15, 305, 495); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addConditionalIf(1463, 3, 306, 498); // SessionKeys.FLAG_HAS_MRIM_ACCOUNTS
        s.addConditionalUnless(1463, 3, 306, 498); // SessionKeys.FLAG_HAS_MRIM_ACCOUNTS
        s.addConditionalIf(1464, 152, 365, 509); // SessionKeys.FLAG_HAS_MRIM_ACCOUNTS_2
        s.addConditionalUnless(1465, 152, 365, 509); // SessionKeys.FLAG_HAS_XMPP_ACCOUNTS
        s.addActionById(22, 1047, 5); // Эта операция доступна только при отсутствии сое...
        s.addActionById(156, 459216, 146); // Засыпаю
        s.addActionById(8, 500, 7); // Заполните текст запроса авторизации.
        s.addActionById(9, 718, 132); // Веселюсь
        s.addActionById(10, 1049, 10); // Контакт и так в этой группе.
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** KEY_MAPPING */
    public static Screen keyMapping(ScreenHandler handler) {
        Screen s = new Screen(10, 132, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(28, 497, 8); // Группа не пуста. Удаление невозможно.
        s.addActionById(5, 502, 137); // Сплю
        s.addConditionalIf(1543, 4, 15, 501); // UIKeys.FLAG_KNOWN_DEVICE
        s.addActionById(9, 546, 9); // Операция недопустима над специальными группами.
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** ABOUT — Мобильный агент */
    public static Screen about(ScreenHandler handler) {
        Screen s = new Screen(5, 9, handler);
        s.configureHeader(9, 510);
        s.addSeparator(512, 1288); // Версия:
        s.addSeparator(853022, 1180715);
        s.addSeparator(513, 1287); // Сборка:
        s.addSeparator(514, 1377341); // Официальный сайт:
        s.addSeparator(515, 1376); // Платформа:
        s.addSeparator(516, 1377); // Модель:
        s.addSeparator(517, 1284); // Память (всего):
        s.addSeparator(518, 1285); // Память (свободно):
        s.configureSoftKeys(507, 57, 1050, 12, 0);
        return s;
    }

    /** BLOG_POST — Микроблог */
    public static Screen blogPost(ScreenHandler handler) {
        Screen s = new Screen(0, 147, handler);
        s.configureHeader(2, 520);
        s.addTextInput(521, 500, 424, 0, 1286);
        s.addCheckbox(1284, 1468);
        s.configureSoftKeys(1060, 4, 1055, 12, 0);
        return s;
    }

    /** MESSAGE_INPUT — Микроблог */
    public static Screen messageInput(ScreenHandler handler) {
        Screen s = new Screen(0, 115, handler);
        s.configureHeader(2, 520);
        s.addSeparator(1284, 1287);
        s.addTextInput(526, 500, 424, 0, 1286);
        s.addCheckbox(527, 1507); // сделать моим статусом
        s.configureSoftKeys(1060, 40, 1055, 12, 0);
        return s;
    }

    /** GROUP_MANAGEMENT */
    public static Screen groupManagement(ScreenHandler handler) {
        Screen s = new Screen(10, 146, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addConditionalIf(1462, 89, 376, 529); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addConditionalUnless(1462, 89, 376, 529); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addActionById(16, 1044, 36); // Здравствуйте. Я нашел вас на карте в Mail.Ru Аг...
        s.addActionById(264, 1045, 6); // Имя не может быть пустым.
        s.addConditionalIf(1462, 147, 2, 528); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addConditionalUnless(1462, 147, 2, 528); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CONTACT_LIST_TEMPLATE */
    public static Screen contactListTemplate(ScreenHandler handler) {
        Screen s = new Screen(1, 4, handler);
        s.configureHeader(-1, 1038);
        s.addActionById(1, 0, 0); // Тестовый параметр
        s.configureSoftKeys(1062, 1, 1059, 199, 199);
        return s;
    }

    /** MULTI_ACCOUNT_LIST */
    public static Screen multiAccountList(ScreenHandler handler) {
        Screen s = new Screen(10, 25, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** MESSAGE_SUMMARY */
    public static Screen messageSummary(ScreenHandler handler) {
        Screen s = new Screen(9, 40, handler);
        s.configureHeader(0, 1290);
        s.configureSoftKeys(1059, 92, 1050, 12, 0);
        return s;
    }

    /** SERVER_ADDRESS — Шаблоны сообщений */
    public static Screen serverAddress(ScreenHandler handler) {
        Screen s = new Screen(0, 95, handler);
        s.configureHeader(32, 543);
        s.configureSoftKeys(1048, 63, 1050, 12, 63);
        return s;
    }

    /** PHONE_INPUT — Шаблоны сообщений */
    public static Screen phoneInput(ScreenHandler handler) {
        Screen s = new Screen(0, 94, handler);
        s.configureHeader(32, 543);
        s.configureSoftKeys(1052, 12, 1050, 12, 12);
        return s;
    }

    /** EMOTICON_PICKER — Смайлики */
    public static Screen emoticonPicker(ScreenHandler handler) {
        Screen s = new Screen(6, 93, handler);
        s.configureHeader(46, 471);
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** MESSAGE_DETAIL — Вставить текст из архива */
    public static Screen messageDetail(ScreenHandler handler) {
        Screen s = new Screen(0, 123, handler);
        s.configureHeader(32, 536);
        s.configureSoftKeys(1048, 63, 1050, 12, 63);
        return s;
    }

    /** MAP_MENU */
    public static Screen mapMenu(ScreenHandler handler) {
        Screen s = new Screen(10, 7, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(11, 537, 25); //  лет
        s.addActionById(12, 538, 26); //  год
        s.addActionById(6, 542, 29); // Слушает: 
        s.addActionById(13, 539, 28); // не определен
        s.addActionById(14, 540, 27); //  года
        s.addActionById(32, 543, 33); // Навигация
        s.addActionById(16, 1044, 56); // Я в пробке. Буду через   мин.
        s.addConditionalUnless(1545, 129, 264, 1045); // Музыка
        s.addActionById(29, 541, 14); // Нельзя отправлять пустое сообщение.
        s.addActionById(309, 544, 50); // Как твои дела?
        s.addConditionalIf(1538, 140, 236, 1009); // UIKeys.FLAG_ADVANCED_FEATURES
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CONTACT_GROUPS */
    public static Screen contactGroups(ScreenHandler handler) {
        Screen s = new Screen(10, 47, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(8, 547, 76); // Название:
        s.addActionById(28, 497, 8); // Группа не пуста. Удаление невозможно.
        s.addActionById(34, 719, 77); // Введите название закладки
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** ACCOUNT_SWITCHER */
    public static Screen accountSwitcher(ScreenHandler handler) {
        Screen s = new Screen(10, 15, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(11, 548, 4); // Эта операция доступна только при соединении с с...
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** ACCOUNTS_MENU */
    public static Screen accountsMenu(ScreenHandler handler) {
        Screen s = new Screen(10, 5, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(7, 552, 0);
        s.addActionById(27, 703, 0);
        s.addActionById(232, 553, 0);
        s.addActionById(35, 670, 0);
        s.addActionById(22, 700, 124); // Думаю
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** XMPP_CONTEXT_MENU — Учетная запись Mail.Ru */
    public static Screen xmppContextMenu(ScreenHandler handler) {
        Screen s = new Screen(0, 76, handler);
        s.configureHeader(156, 557);
        s.addTextInput(673, 63, 425, 0, 1292);
        s.addDropdown(674, 694, 1474); // Домен:
        s.addTextInput(556, 63, 425, 327680, 1293);
        s.configureSoftKeys(1053, 25, 1055, 12, 0);
        return s;
    }

    /** XMPP_LOGIN — Учетная запись ICQ */
    public static Screen xmppLogin(ScreenHandler handler) {
        Screen s = new Screen(0, 76, handler);
        s.configureHeader(256, 558);
        s.addLogin(12, 0);
        s.addDropdown(674, 694, 1474);
        s.configureSoftKeys(1053, 25, 1055, 12, 0);
        return s;
    }

    /** PRIVACY_SETTINGS — Шаблоны сообщений */
    public static Screen privacySettings(ScreenHandler handler) {
        Screen s = new Screen(0, 33, handler);
        s.configureHeader(32, 543);
        s.addTextInput(1038, 255, 424, 0, 48);
        s.addTextInput(1038, 255, 424, 0, 49);
        s.addTextInput(1038, 255, 424, 0, 50);
        s.addTextInput(1038, 255, 424, 0, 51);
        s.addTextInput(1038, 255, 424, 0, 52);
        s.addTextInput(1038, 255, 424, 0, 53);
        s.addTextInput(1038, 255, 424, 0, 54);
        s.addTextInput(1038, 255, 424, 0, 55);
        s.addTextInput(1038, 255, 424, 0, 56);
        s.addTextInput(1038, 255, 424, 0, 57);
        s.addTextInput(1038, 255, 424, 0, 58);
        s.addTextInput(1038, 255, 424, 0, 59);
        s.addTextInput(1038, 255, 424, 0, 60);
        s.addTextInput(1038, 255, 424, 0, 61);
        s.addTextInput(1038, 255, 424, 0, 62);
        s.configureSoftKeys(1053, 12, 1050, 12, 0);
        return s;
    }

    /** THEME_SETTINGS — Интерфейс */
    public static Screen themeSettings(ScreenHandler handler) {
        Screen s = new Screen(0, 26, handler);
        s.configureHeader(12, 538);
        s.addCheckbox(561, 71); // Полный экран
        s.addCheckbox(562, 65); // Софт-кнопки наоборот
        s.addCheckbox(563, 67); // Почта в закладках
        s.addCheckbox(564, 68); // Карты в закладках
        s.addDropdown(565, 566, 73); // Размер шрифта:
        s.addDropdown(560, 567, 72); // Цветовая схема:
        s.addCheckbox(568, 70); // Использовать курсив
        s.addCheckbox(572, 66); // Затемнение экрана
        s.addDropdown(569, 570, 74); // Язык по умолчанию:
        s.addDropdown(573, 574, 246); // Коррекция времени:
        s.addCheckbox(571, 69); // Подтверждать выход
        s.configureSoftKeys(1053, 4, 1050, 12, 0);
        return s;
    }

    /** SOUND_SETTINGS — Уведомления */
    public static Screen soundSettings(ScreenHandler handler) {
        Screen s = new Screen(0, 28, handler);
        s.configureHeader(13, 539);
        s.addDropdown(581, 591, 75); // Новое сообщение:
        s.addCheckbox(589, 76); // Вибрация
        s.addDropdown(582, 591, 77); // Контакт в онлайне:
        s.addCheckbox(589, 78); // Вибрация
        s.addDropdown(583, 591, 79); // Новое письмо:
        s.addCheckbox(589, 80); // Вибрация
        s.addDropdown(584, 591, 81); // Запрос авторизации:
        s.addCheckbox(589, 82); // Вибрация
        s.addDropdown(585, 591, 83); // Отправка сообщения:
        s.addCheckbox(589, 84); // Вибрация
        s.addDropdown(587, 591, 240); // Новый микроблог:
        s.addCheckbox(589, 241); // Вибрация
        s.addDropdown(586, 591, 85); // Ошибка:
        s.addCheckbox(589, 86); // Вибрация
        s.addCheckbox(588, 87); // Управление громкостью
        s.addNumericInput(590, 3, 1262, 88, 0, 100, 50);
        s.addCheckbox(592, 89); // Тихий режим
        s.configureSoftKeys(1053, 12, 1050, 12, 0);
        return s;
    }

    /** CONTACT_SETTINGS — Почта */
    public static Screen contactSettings(ScreenHandler handler) {
        Screen s = new Screen(0, 56, handler);
        s.configureHeader(16, 1044);
        s.addTextSeparator(595); // Уведомления о новой почте:
        s.addCheckbox(596, 90); // Иконка
        s.addCheckbox(597, 91); // Всплывающее окно
        s.addTextSeparator(598); // Отправка и просмотр:
        s.addCheckbox(599, 92); // Вставлять приветствие
        s.addTextInput(601, 255, 424, 0, 93);
        s.addCheckbox(600, 94); // Вставлять подпись
        s.addTextInput(602, 255, 424, 0, 95);
        s.addCheckbox(603, 96); // Цитировать при ответе
        s.addNumericInput(604, 3, 1262, 97, 5, 999, 10);
        s.configureSoftKeys(1053, 12, 1050, 12, 0);
        return s;
    }

    /** MULTI_ACCOUNT_SETTINGS — Список контактов */
    public static Screen multiAccountSettings(ScreenHandler handler) {
        Screen s = new Screen(0, 29, handler);
        s.configureHeader(6, 542);
        s.addTextSeparator(620); // Учетные записи:
        s.addCheckbox(621, 243); // В отдельных закладках
        s.addCheckbox(624, 245); // Строка состояния
        s.addTextSeparator(605); // Контакты:
        s.addCheckbox(606, 98); // Только онлайн-контакты
        s.addDropdown(619, 622, 242); // Столбцы:
        s.addTextSeparator(607); // Группы:
        s.addCheckbox(608, 99); // Использовать
        s.addCheckbox(609, 100); // Объединять одноименные
        s.addCheckbox(610, 101); // Показывать пустые
        s.addTextSeparator(611); // Диалог:
        s.addNumericInput(612, 3, 1262, 102, 5, 999, 20);
        s.addCheckbox(623, 244); // Принимать микроблоги
        s.addCheckbox(618, 106); // Автотранслит SMS
        s.addTextSeparator(613); // Антиспам:
        s.addCheckbox(614, 103); // Принимать от временных
        s.addTextSeparator(615); // Приватность:
        s.addCheckbox(616, 104); // Отправлять 'Я пишу'
        s.addCheckbox(617, 105); // Скрывать платформу
        s.configureSoftKeys(1053, 4, 1050, 12, 0);
        return s;
    }

    /** CHAT_VIEW_MODE — Настройка сети */
    public static Screen chatViewMode(ScreenHandler handler) {
        Screen s = new Screen(0, 50, handler);
        s.configureHeader(309, 544);
        s.addCheckbox(625, 112); // Асинхронная передача
        s.configureSoftKeys(1053, 12, 1050, 12, 0);
        return s;
    }

    /** TRAFFIC_COST — Тарификация */
    public static Screen trafficCost(ScreenHandler handler) {
        Screen s = new Screen(0, 14, handler);
        s.configureHeader(29, 541);
        s.addTextInput(626, 10, 1262, 0, 1286);
        s.addNumericInput(627, 10, 1262, 114, 1, 1024, 1024);
        s.addTextInput(628, 10, 424, 0, 117);
        s.configureSoftKeys(1053, 12, 1050, 12, 0);
        return s;
    }

    /** NOTIFICATION_SETTINGS — Горячие клавиши */
    public static Screen notificationSettings(ScreenHandler handler) {
        Screen s = new Screen(0, 27, handler);
        s.configureHeader(14, 540);
        s.addDropdown(629, 641, 205); // *:
        s.addDropdown(630, 641, 206); // #:
        s.addDropdown(631, 641, 207); // 0:
        s.addDropdown(632, 641, 208); // 1:
        s.addDropdown(633, 641, 209); // 2:
        s.addDropdown(634, 641, 210); // 3:
        s.addDropdown(635, 641, 211); // 4:
        s.addDropdown(636, 641, 212); // 5:
        s.addDropdown(637, 641, 213); // 6:
        s.addDropdown(638, 641, 214); // 7:
        s.addDropdown(639, 641, 215); // 8:
        s.addDropdown(640, 641, 216); // 9:
        s.configureSoftKeys(1053, 12, 1050, 12, 0);
        return s;
    }

    /** EXT_SETTINGS */
    public static Screen extSettings(ScreenHandler handler) {
        Screen s = new Screen(2, 151, handler);
        s.configureHeader(303, 1038);
        s.showCheckboxes = true;
        s.addActionById(364, 880, 0);
        s.addActionById(365, 884, 1);
        s.addActionById(366, 882, 2); // Эта учетная запись подключена или находится в с...
        s.addActionById(367, 881, 3); // Эта учетная запись не подключена к серверу.
        s.addActionById(369, 656, 4); // Эта операция доступна только при соединении с с...
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** MAP_VIEW_SETTINGS */
    public static Screen mapViewSettings(ScreenHandler handler) {
        Screen s = new Screen(10, 152, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(361, 666, 6); // Имя не может быть пустым.
        s.addActionById(308, 668, 162); // Я пришелец
        s.addActionById(369, 665, 151); // Курю
        s.addActionById(8, 500, 129); // Музыка
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** NOTIFICATION_DIALOG */
    public static Screen notificationDialog(ScreenHandler handler) {
        Screen s = new Screen(8, 112, handler);
        s.configureHeader(0, 1038);
        s.addLabelSeparator(1294);
        s.configureSoftKeys(0, 0, 669, 12, 12);
        return s;
    }

    /** CREATE_GROUP — Создать группу */
    public static Screen createGroup(ScreenHandler handler) {
        Screen s = new Screen(0, 69, handler);
        s.configureHeader(35, 670);
        s.addTextInput(671, 63, 424, 0, 1295);
        s.configureSoftKeys(1053, 4, 1050, 12, 0);
        return s;
    }

    /** REGION_CHOICE — Добавить контакт */
    public static Screen regionChoice(ScreenHandler handler) {
        Screen s = new Screen(0, 21, handler);
        s.configureHeader(7, 552);
        s.addTextInput(673, 63, 425, 0, 1296);
        s.addDropdown(674, 694, 1480); // Домен:
        s.addDropdown(679, 680, 1481); // Пол:
        s.addNumericInput(681, 3, 1262, 1482, 1, 999, -1);
        s.addNumericInput(682, 3, 1262, 1483, 1, 999, -1);
        s.addDropdown(688, 685, 1484); // Знак Зодиака:
        s.addDropdown(689, 1300, 1485); // Страна:
        s.addDropdown(690, 684, 1486); // Регион:
        s.addDropdown(691, 684, 1487); // Город:
        s.addTextInput(675, 63, 424, 0, 1297);
        s.addTextInput(676, 63, 424, 0, 1298);
        s.addTextInput(677, 63, 424, 0, 1299);
        s.addDropdown(693, 686, 1488); // Месяц рождения:
        s.addDropdown(692, 687, 1489); // День рождения:
        s.addCheckbox(678, 1490); // Искать только подключенных
        s.configureSoftKeys(1061, 44, 1050, 12, 0);
        return s;
    }

    /** XMPP_LOGIN_ALT — Учетная запись Jabber */
    public static Screen xmppLoginAlt(ScreenHandler handler) {
        Screen s = new Screen(0, 76, handler);
        s.configureHeader(383, 697);
        s.addLogin(12, 0);
        s.addDropdown(674, 696, 1474); // Домен:
        s.addDropdown(674, 694, 1474);
        s.addDropdown(691, 684, 1487);
        s.configureSoftKeys(1053, 25, 1055, 12, 0);
        return s;
    }

    /** XMPP_LOGIN_ALT2 — Учетная запись ВКонтакте */
    public static Screen xmppLoginAlt2(ScreenHandler handler) {
        Screen s = new Screen(0, 76, handler);
        s.configureHeader(387, 698);
        s.addLogin(12, 0);
        s.addDropdown(674, 694, 1474);
        s.addDropdown(691, 684, 1487);
        s.configureSoftKeys(1053, 25, 1055, 12, 0);
        return s;
    }

    /** EMPTY_SCREEN — Списки видимости */
    public static Screen emptyScreen(ScreenHandler handler) {
        Screen s = new Screen(10, 124, handler);
        s.configureHeader(22, 700);
        s.showCheckboxes = true;
        s.addActionById(17, 763, 125); // Когда я ем...
        s.addActionById(19, 764, 126); // Телевизор
        s.addActionById(20, 701, 127); // На встрече
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CONTACT_EDITOR — Имя и телефоны */
    public static Screen contactEditor(ScreenHandler handler) {
        Screen s = new Screen(0, 19, handler);
        s.configureHeader(18, 702);
        s.addTextInput(704, 63, 424, 0, 1302);
        s.configureSoftKeys(1053, 4, 1050, 12, 0);
        return s;
    }

    /** ADD_MRIM_CONTACT — Добавить телефонный контакт */
    public static Screen addMrimContact(ScreenHandler handler) {
        Screen s = new Screen(0, 22, handler);
        s.configureHeader(27, 703);
        s.addTextSeparator(12);
        s.addActionById(3, 1303, 424);
        s.addActionById(3, 1304, 424);
        s.configureSoftKeys(1053, 4, 1050, 12, 0);
        return s;
    }

    /** RENAME_GROUP — Переименовать */
    public static Screen renameGroup(ScreenHandler handler) {
        Screen s = new Screen(0, 70, handler);
        s.configureHeader(35, 721);
        s.addTextInput(671, 63, 424, 0, 1306);
        s.configureSoftKeys(1053, 4, 1050, 12, 0);
        return s;
    }

    /** ADD_CONTACT — Добавить контакт */
    public static Screen addContact(ScreenHandler handler) {
        Screen s = new Screen(0, 21, handler);
        s.configureHeader(256, 552);
        s.addNumericInput(263250, 10, 1262, 1491, 1, 2147483647, -1);
        s.addTextInput(675, 63, 424, 0, 1307);
        s.addTextInput(676, 63, 424, 0, 1308);
        s.addTextInput(677, 63, 424, 0, 1309);
        s.addTextInput(394334, 63, 425, 0, 1310);
        s.addTextInput(328804, 63, 424, 0, 1311);
        s.addTextInput(525398, 63, 424, 0, 1312);
        s.addCheckbox(678, 1492); // Искать только подключенных
        s.configureSoftKeys(1061, 44, 1050, 12, 0);
        return s;
    }

    /** PHONE_GROUPS — Отправить SMS */
    public static Screen phoneGroups(ScreenHandler handler) {
        Screen s = new Screen(0, 65, handler);
        s.configureHeader(27, 711);
        s.addDropdown(712, 1313, 1493); // Телефон:
        s.addTextInput(870, 160, 424, 0, 1279);
        s.configureSoftKeys(1059, 87, 1050, 12, 0);
        return s;
    }

    /** CHAT_STATUS */
    public static Screen chatStatus(ScreenHandler handler) {
        Screen s = new Screen(3, 87, handler);
        s.configureHeader(0, 1038);
        s.addConditionalIf(1456, 40, -1, 1060); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1456, 65, -1, 476); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addActionById(-1, 472, 99); // Хранение данных:
        s.addConditionalIf(1460, 65, -1, 473); // UIKeys.FLAG_RESOURCE_LOADING
        s.addConditionalIf(1456, 65, -1, 474); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1456, 98, -1, 475); // UIKeys.FLAG_STATUS_TEXT_SET
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CONTACT_GROUP_MENU */
    public static Screen contactGroupMenu(ScreenHandler handler) {
        Screen s = new Screen(4, 30, handler);
        s.configureHeader(0, 1038);
        s.addActionById(35, 721, 70); // Видимость
        s.addActionById(34, 719, 71); // Указать что я здесь
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CONTACT_ACTIONS_MENU */
    public static Screen contactActionsMenu(ScreenHandler handler) {
        Screen s = new Screen(4, 0, handler);
        s.configureHeader(0, 1038);
        s.addConditionalIf(3707, 40, 21, 715); // ContactKeys.FLAG_CONTACT_MENU_MODE
        s.addConditionalUnless(1496, 63, 0, 714); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1501, 6, 365, 722); // ContactKeys.SCREEN_FLAGS_END
        s.addConditionalIf(1497, 166, 376, 725); // ContactKeys.FLAG_CONTACT_IS_USER
        s.addConditionalIf(1496, 65, 27, 711); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1497, 40, 33, 717); // ContactKeys.FLAG_CONTACT_IS_USER
        s.addConditionalUnless(1496, 85, 17, 716); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1498, 66, 23, 1051); // ContactKeys.FLAG_CONTACT_IS_ONLINE
        s.addConditionalIf(1499, 66, 23, 720); // ContactKeys.FLAG_CONTACT_HAS_UNREAD
        s.addConditionalIf(1504, 35, 314, 726); // UIKeys.FLAG_XMPP_CAN_EDIT
        s.addActionById(18, 547, 19); // Страница: 
        s.addConditionalUnless(1496, 64, 9, 718); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1496, 128, 34, 766); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1496, 71, 19, 719); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CONTACT_MENU */
    public static Screen contactMenu(ScreenHandler handler) {
        Screen s = new Screen(4, 0, handler);
        s.configureHeader(0, 1038);
        s.addConditionalIf(12, 199, 14, 7);
        s.addActionById(0, 714, 63); // Маршрут сюда
        s.addConditionalIf(1503, 166, 376, 725); // Водные процедуры
        s.addActionById(33, 717, 40); // Отдалить [#]
        s.addActionById(6, 724, 150); // Любовь
        s.addActionById(7, 774, 144); // PSP
        s.addActionById(726, 0, 0);
        s.addActionById(34, 766, 128); // Кофе
        s.addActionById(20, 767, 11); // Пустой пароль недопустим.
        s.addActionById(19, 723, 71); // Указать что я здесь
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CONTACT_INFO_VIEW_SCREEN — Анкета */
    public static Screen contactInfoViewScreen(ScreenHandler handler) {
        Screen s = new Screen(5, 96, handler);
        s.configureHeader(17, 716);
        s.configureSoftKeys(503, 0, 1050, 12, 0);
        return s;
    }

    /** CAPTCHA — Фото */
    public static Screen captcha(ScreenHandler handler) {
        Screen s = new Screen(5, 106, handler);
        s.configureHeader(17, 503);
        s.configureSoftKeys(0, 0, 1050, 12, 0);
        return s;
    }

    /** PROFILE_LIST — Обновление */
    public static Screen profileList(ScreenHandler handler) {
        Screen s = new Screen(5, 59, handler);
        s.configureHeader(36, 507);
        s.addSeparator(732, 1284); // Доступна новая версия Мобильного Агента
        s.addSeparator(733, 1285); // Загрузить новую версию можно, открыв в wap-брау...
        s.addLabelSeparator(734); // Произвести попытку загрузки?
        s.configureSoftKeys(1056, 12, 1057, 12, 12);
        return s;
    }

    /** SEARCH_RESULT_LIST — Результаты поиска */
    public static Screen searchResultList(ScreenHandler handler) {
        Screen s = new Screen(0, 73, handler);
        s.configureHeader(17, 735);
        s.configureSoftKeys(1051, 66, 1050, 12, 103);
        return s;
    }

    /** CONTACT_INFO_DETAIL_SCREEN — Анкета */
    public static Screen contactInfoDetailScreen(ScreenHandler handler) {
        Screen s = new Screen(5, 103, handler);
        s.configureHeader(17, 716);
        s.configureSoftKeys(503, 0, 1050, 12, 0);
        return s;
    }

    /** CONTACT_ADD_SCREEN — Запрос авторизации */
    public static Screen contactAddScreen(ScreenHandler handler) {
        Screen s = new Screen(0, 66, handler);
        s.configureHeader(23, 737);
        s.configureSoftKeys(1058, 4, 1050, 12, 0);
        return s;
    }

    /** CONTACT_LIST_SCREEN — Запрос авторизации */
    public static Screen contactListScreen(ScreenHandler handler) {
        Screen s = new Screen(0, 66, handler);
        s.configureHeader(23, 737);
        s.addSeparator(263250, 1320);
        s.addActionById(738, 1320, 1);
        s.addTextSeparator(424);
        s.configureSoftKeys(1058, 4, 1050, 12, 0);
        return s;
    }

    /** ADD_CONTACT_FORM — Добавить контакт */
    public static Screen addContactForm(ScreenHandler handler) {
        Screen s = new Screen(0, 21, handler);
        s.configureHeader(383, 552);
        s.addTextInput(744, 63, 425, 0, 1296);
        s.addDropdown(691, 684, 1487);
        s.addTextSeparator(424);
        s.configureSoftKeys(1051, 4, 1050, 12, 0);
        return s;
    }

    /** SETTINGS_MENU */
    public static Screen settingsMenu(ScreenHandler handler) {
        Screen s = new Screen(10, 8, handler);
        s.configureHeader(28, 1038);
        s.showCheckboxes = true;
        s.addActionById(28, 745, 0);
        s.addActionById(28, 746, 1);
        s.addActionById(28, 747, 2); // Эта учетная запись подключена или находится в с...
        s.addActionById(28, 748, 3); // Эта учетная запись не подключена к серверу.
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** TRAFFIC_STATS */
    public static Screen trafficStats(ScreenHandler handler) {
        Screen s = new Screen(0, 34, handler);
        s.configureSoftKeys(508, 34, 1050, 12, 0);
        return s;
    }

    /** SETTINGS_MAIN */
    public static Screen settingsMain(ScreenHandler handler) {
        Screen s = new Screen(5, 2, handler);
        s.configureHeader(156, 459216);
        s.addLabelSeparator(1016); // Добро пожаловать в Мобильный Агент! Сейчас буде...
        s.configureSoftKeys(1017, 157, 0, 157, 157);
        return s;
    }

    /** DELETE_CONTACT_LIST — Игнорировать */
    public static Screen deleteContactList(ScreenHandler handler) {
        Screen s = new Screen(0, 127, handler);
        s.configureHeader(20, 767);
        s.configureSoftKeys(719, 4, 1050, 12, 0);
        return s;
    }

    /** UNBLOCK_CONTACT_LIST — Я всегда невидим для... */
    public static Screen unblockContactList(ScreenHandler handler) {
        Screen s = new Screen(0, 126, handler);
        s.configureHeader(19, 764);
        s.configureSoftKeys(719, 4, 1050, 12, 0);
        return s;
    }

    /** BLOCK_CONTACT_LIST — Я всегда видим для... */
    public static Screen blockContactList(ScreenHandler handler) {
        Screen s = new Screen(0, 125, handler);
        s.configureHeader(17, 763);
        s.configureSoftKeys(719, 4, 1050, 12, 0);
        return s;
    }

    /** PHONE_INPUT_ALT — Шаблоны сообщений */
    public static Screen phoneInputAlt(ScreenHandler handler) {
        Screen s = new Screen(0, 98, handler);
        s.configureHeader(32, 543);
        s.configureSoftKeys(1052, 12, 1050, 12, 12);
        return s;
    }

    /** URL_OPEN — Шаблоны сообщений */
    public static Screen urlOpen(ScreenHandler handler) {
        Screen s = new Screen(0, 99, handler);
        s.configureHeader(32, 543);
        s.configureSoftKeys(1048, 65, 1050, 12, 65);
        return s;
    }

    /** ACCOUNT_SWITCH_OPTIONS */
    public static Screen accountSwitchOptions(ScreenHandler handler) {
        Screen s = new Screen(10, 64, handler);
        s.configureHeader(9, 1038);
        s.showCheckboxes = true;
        s.addConditionalUnless(1496, 0, 0, 763); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalUnless(1496, 1, 0, 764); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalUnless(1496, 86, 35, 765); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addActionById(34, 766, 128); // Кофе
        s.addConditionalUnless(1496, 11, 20, 767); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addActionById(19, 719, 71); // Указать что я здесь
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CREATE_CHAT_ROOM — Начать конференцию */
    public static Screen createChatRoom(ScreenHandler handler) {
        Screen s = new Screen(0, 143, handler);
        s.configureHeader(232, 553);
        s.addTextInput(869, 255, 424, 0, 1292);
        s.addCheckbox(769, 2722); // Приглашаю только я
        s.addTextSeparator(770); // Пригласить:
        s.configureSoftKeys(1053, 4, 1050, 12, 0);
        return s;
    }

    /** GROUP_MEMBERS — Список участников */
    public static Screen groupMembers(ScreenHandler handler) {
        Screen s = new Screen(0, 142, handler);
        s.configureHeader(6, 724);
        s.configureSoftKeys(1051, 199, 1050, 12, 145);
        return s;
    }

    /** EDIT_MEMBERS — Добавить в конференцию */
    public static Screen editMembers(ScreenHandler handler) {
        Screen s = new Screen(0, 144, handler);
        s.configureHeader(7, 774);
        s.configureSoftKeys(1051, 40, 1050, 12, 0);
        return s;
    }

    /** CHAT_OPTIONS */
    public static Screen chatOptions(ScreenHandler handler) {
        Screen s = new Screen(10, 166, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addConditionalIf(1497, 65, 27, 776); // ContactKeys.FLAG_CONTACT_IS_USER
        s.addConditionalIf(1497, 54, 238, 777); // ContactKeys.FLAG_CONTACT_IS_USER
        s.addConditionalIf(1500, 135, 221, 778); // Учусь
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** COLOR_PICKER */
    public static Screen colorPicker(ScreenHandler handler) {
        Screen s = new Screen(10, 104, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(156, 642, 0);
        s.addActionById(159, 643, 1);
        s.addActionById(157, 644, 2); // Эта учетная запись подключена или находится в с...
        s.addActionById(160, 645, 3); // Эта учетная запись не подключена к серверу.
        s.addActionById(158, 646, 4); // Эта операция доступна только при соединении с с...
        s.addActionById(155, 647, 5); // Эта операция доступна только при отсутствии сое...
        s.configureSoftKeys(1048, 4, 1050, 12, 4);
        return s;
    }

    /** GROUP_MOVE */
    public static Screen groupMove(ScreenHandler handler) {
        Screen s = new Screen(10, 86, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CONTACT_INFO_EDITOR — Список видимости */
    public static Screen contactInfoEditor(ScreenHandler handler) {
        Screen s = new Screen(0, 156, handler);
        s.configureHeader(310, 779);
        s.configureSoftKeys(1053, 151, 1050, 151, 151);
        return s;
    }

    /** PROFILE_EDIT */
    public static Screen profileEdit(ScreenHandler handler) {
        Screen s = new Screen(8, 160, handler);
        s.configureHeader(-1, 1038);
        s.addLabelSeparator(1337);
        s.configureSoftKeys(785, 171, 1052, 151, 171);
        return s;
    }

    /** CHAT_DETAIL */
    public static Screen chatDetail(ScreenHandler handler) {
        Screen s = new Screen(10, 162, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(308, 657, 6); // Имя не может быть пустым.
        s.addActionById(303, 659, 100); // Сообщать о дорожной обстановке от имени:
        s.addActionById(360, 1251, 120); // Водные процедуры
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** MAIL_ACCOUNT_LIST */
    public static Screen mailAccountList(ScreenHandler handler) {
        Screen s = new Screen(2, 169, handler);
        s.configureHeader(303, 1038);
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CHAT_ROOM_CONFIG — Описание статуса */
    public static Screen chatRoomConfig(ScreenHandler handler) {
        Screen s = new Screen(0, 49, handler);
        s.configureHeader(0, 795);
        s.addTextInput(0, 63, 424, 0, 0);
        s.configureSoftKeys(1053, 0, 1050, 12, 0);
        return s;
    }

    /** FILE_SELECTOR — Выберите файл */
    public static Screen fileSelector(ScreenHandler handler) {
        Screen s = new Screen(0, 135, handler);
        s.configureHeader(221, 797);
        s.addLogin(796, 1284); // *
        s.configureSoftKeys(1060, 141, 1050, 12, 0);
        return s;
    }

    /** PHOTO_SELECTOR_ALT — Фото */
    public static Screen photoSelectorAlt(ScreenHandler handler) {
        Screen s = new Screen(5, 133, handler);
        s.configureHeader(220, 503);
        s.addLabelSeparator(1344);
        s.configureSoftKeys(0, 0, 1055, 12, 0);
        return s;
    }

    /** MAIN_SCREEN — Блокировка */
    public static Screen mainScreen(ScreenHandler handler) {
        Screen s = new Screen(5, 137, handler);
        s.configureHeader(5, 502);
        s.addLabelSeparator(803); // Клавиатура заблокирована.
        s.configureSoftKeys(0, 0, 804, 0, 0);
        return s;
    }

    /** ACCOUNT_SETUP — Настройка учетной записи */
    public static Screen accountSetup(ScreenHandler handler) {
        Screen s = new Screen(0, 157, handler);
        s.configureHeader(156, 805);
        s.addActionById(-1, 806, 105); // У меня есть учетная запись mail.ru
        s.addActionById(-1, 807, 165); // Регистрация новой учетной записи
        s.configureSoftKeys(1048, 199, 1055, 4, 199);
        return s;
    }

    /** REGISTRATION_FORM — Учетная запись Mail.Ru */
    public static Screen registrationForm(ScreenHandler handler) {
        Screen s = new Screen(0, 164, handler);
        s.configureHeader(156, 557);
        s.addLogin(12, 0);
        s.addDropdown(674, 694, 1474);
        s.addTextInput(808, 63, 425, 327680, 1284);
        s.addDropdown(809, 810, 4305); // Секретный вопрос:
        s.addTextInput(811, 255, 424, 0, 1287);
        s.addTextInput(812, 255, 424, 0, 1288);
        s.addDropdown(693, 686, 1488);
        s.addNumericInput(813, 4, 425, 1491, 1, 2100, -1);
        s.addDropdown(674, 694, 1480);
        s.addImage(1341);
        s.addNumericInput(814, 6, 425, 1480, -1, 999999, -1);
        s.configureSoftKeys(1053, 12, 1055, 12, 0);
        return s;
    }

    /** REGISTRATION */
    public static Screen registration(ScreenHandler handler) {
        Screen s = new Screen(10, 16, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(-1, 815, 76); // Название:
        s.addActionById(-1, 816, 165); // Сошел с ума
        s.configureSoftKeys(1048, 199, 1055, 12, 199);
        return s;
    }

    /** ERROR_ALERT */
    public static Screen errorAlert(ScreenHandler handler) {
        Screen s = new Screen(8, 0, handler);
        s.configureHeader(0, 1038);
        s.addLabelSeparator(1344);
        s.configureSoftKeys(1056, 0, 1057, 12, 0);
        return s;
    }

    /** CONFIRM_DIALOG */
    public static Screen confirmDialog(ScreenHandler handler) {
        Screen s = new Screen(7, 0, handler);
        s.configureSoftKeys(0, 0, 1055, 12, 0);
        return s;
    }

    /** GENERIC_LIST */
    public static Screen genericList(ScreenHandler handler) {
        Screen s = new Screen(1, 36, handler);
        s.configureHeader(-1, 1038);
        s.configureSoftKeys(1062, 88, 1048, 37, 37);
        return s;
    }

    /** INPUT_FORM — Папки */
    public static Screen inputForm(ScreenHandler handler) {
        Screen s = new Screen(0, 38, handler);
        s.configureHeader(35, 834);
        s.configureSoftKeys(1059, 80, 1050, 12, 41);
        return s;
    }

    /** CONTACT_DETAILS */
    public static Screen contactDetails(ScreenHandler handler) {
        Screen s = new Screen(0, 43, handler);
        s.configureHeader(0, 1038);
        s.configureSoftKeys(1059, 51, 1050, 12, 48);
        return s;
    }

    /** MESSAGE_PREVIEW */
    public static Screen messagePreview(ScreenHandler handler) {
        Screen s = new Screen(5, 52, handler);
        s.configureHeader(240, 1284);
        s.addTextSeparator(1285);
        s.addLabelSeparator(1286);
        s.configureSoftKeys(1059, 53, 1050, 43, 0);
        return s;
    }

    /** COMPOSE_RECIPIENTS */
    public static Screen composeRecipients(ScreenHandler handler) {
        Screen s = new Screen(3, 53, handler);
        s.configureHeader(0, 1038);
        s.addActionById(213, 839, 54); // Ok, договорились.
        s.addActionById(214, 840, 54); // Ok, договорились.
        s.addActionById(216, 841, 54); // Ok, договорились.
        s.addActionById(243, 842, 58); // Спасибо!
        s.addActionById(239, 843, 90); // никогда, 5 мин, 15 мин, 30 мин
        s.addActionById(218, 844, 60); // Буду ждать тебя 
        s.addActionById(217, 845, 61); // До свидания!
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** CHAT_ROOM_CONTEXT */
    public static Screen chatRoomContext(ScreenHandler handler) {
        Screen s = new Screen(3, 51, handler);
        s.configureHeader(0, 1038);
        s.addConditionalIf(1517, 37, 243, 851); // ChatKeys.SCREEN_FLAGS_START
        s.addActionById(238, 846, 54); // Ok, договорились.
        s.addConditionalIf(1518, 43, 24, 847); // ChatKeys.FLAG_MSG_READ_SELECTED
        s.addConditionalIf(1519, 43, 25, 848); // ChatKeys.FLAG_MSG_UNREAD_SELECTED
        s.addConditionalIf(1520, 67, 25, 1347); // ChatKeys.FLAG_CHATROOM_HAS_MEMBERS
        s.addConditionalIf(1521, 62, 240, 850); // ChatKeys.FLAG_IS_CHATROOM
        s.addConditionalIf(1517, 68, 227, 1061); // ChatKeys.SCREEN_FLAGS_START
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** SOFTKEY_MENU */
    public static Screen softkeyMenu(ScreenHandler handler) {
        Screen s = new Screen(10, 67, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(237, 853, 72); // Написать сообщение
        s.addActionById(225, 854, 72); // Написать сообщение
        s.addActionById(24, 852, 43); // Дорожные обьекты
        s.addActionById(239, 843, 90); // никогда, 5 мин, 15 мин, 30 мин
        s.addActionById(218, 844, 60); // Буду ждать тебя 
        s.addActionById(217, 845, 61); // До свидания!
        s.configureSoftKeys(1048, 199, 1055, 12, 199);
        return s;
    }

    /** MAIL_MENU */
    public static Screen mailMenu(ScreenHandler handler) {
        Screen s = new Screen(10, 62, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(213, 839, 48); // Привет!
        s.addActionById(214, 840, 48); // Привет!
        s.addActionById(216, 841, 48); // Привет!
        s.addConditionalIf(1522, 72, 237, 855); // ChatKeys.FLAG_MSG_UNREAD
        s.addConditionalIf(1523, 72, 225, 856); // ChatKeys.SCREEN_FLAGS_END
        s.addActionById(239, 843, 90); // никогда, 5 мин, 15 мин, 30 мин
        s.addActionById(218, 844, 60); // Буду ждать тебя 
        s.addActionById(217, 845, 61); // До свидания!
        s.configureSoftKeys(1048, 199, 1055, 12, 199);
        return s;
    }

    /** INPUT_DIALOG */
    public static Screen inputDialog(ScreenHandler handler) {
        Screen s = new Screen(10, 60, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(218, 859, 78); // Сохранить закладку
        s.addActionById(218, 860, 78); // Сохранить закладку
        s.configureSoftKeys(1048, 199, 1055, 12, 199);
        return s;
    }

    /** GROUP_SELECTOR */
    public static Screen groupSelector(ScreenHandler handler) {
        Screen s = new Screen(10, 58, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(243, 864, 0);
        s.addActionById(243, 865, 1);
        s.configureSoftKeys(1048, 199, 1055, 12, 199);
        return s;
    }

    /** NOTIFICATION_OPTIONS */
    public static Screen notificationOptions(ScreenHandler handler) {
        Screen s = new Screen(3, 80, handler);
        s.configureHeader(0, 1038);
        s.addActionById(243, 851, 37); // В вашем телефоне недостаточно оперативной памят...
        s.addActionById(238, 846, 54); // Ok, договорились.
        s.addActionById(227, 1061, 68); // Спрятать
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** SEARCH_RESULTS — Поиск */
    public static Screen searchResults(ScreenHandler handler) {
        Screen s = new Screen(0, 68, handler);
        s.configureHeader(227, 1061);
        s.addTextInput(867, 255, 424, 0, 1348);
        s.addTextInput(868, 255, 424, 0, 1349);
        s.addTextInput(869, 255, 424, 0, 1350);
        s.addTextInput(870, 255, 424, 0, 1351);
        s.addCheckbox(871, 1526); // Искать во всех папках
        s.configureSoftKeys(1061, 81, 1050, 12, 0);
        return s;
    }

    /** COMPOSE_MESSAGE — Написать письмо */
    public static Screen composeMessage(ScreenHandler handler) {
        Screen s = new Screen(0, 54, handler);
        s.configureHeader(238, 875);
        s.addTextInput(868, 255, 425, 0, 1352);
        s.addTextInput(869, 255, 424, 0, 1353);
        s.addTextSeparator(870); // Сообщение:
        s.addTextInput(1038, 10000, 424, 0, 1354);
        s.configureSoftKeys(1060, 82, 1050, 79, 0);
        return s;
    }

    /** THEME_OPTIONS */
    public static Screen themeOptions(ScreenHandler handler) {
        Screen s = new Screen(3, 88, handler);
        s.configureHeader(156, 1038);
        s.addActionById(8, 500, 56); // Я в пробке. Буду через   мин.
        s.addActionById(10, 1049, 10);
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** DIALOG_SCREEN */
    public static Screen dialogScreen(ScreenHandler handler) {
        Screen s = new Screen(10, 90, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.configureSoftKeys(1048, 42, 1050, 12, 42);
        return s;
    }

    /** VERSION_SELECT */
    public static Screen versionSelect(ScreenHandler handler) {
        Screen s = new Screen(10, 109, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(370, 880, 1);
        s.addActionById(372, 882, 3); // Эта учетная запись не подключена к серверу.
        s.addActionById(373, 883, 4); // Эта операция доступна только при соединении с с...
        s.addActionById(374, 884, 5); // Эта операция доступна только при отсутствии сое...
        s.addActionById(371, 881, 2); // Эта учетная запись подключена или находится в с...
        s.configureSoftKeys(1048, 3, 1050, 12, 3);
        return s;
    }

    /** VCARD_ACTIONS */
    public static Screen vcardActions(ScreenHandler handler) {
        Screen s = new Screen(3, 130, handler);
        s.configureHeader(0, 1038);
        s.addActionById(-1, 885, 0);
        s.addActionById(-1, 886, 1);
        s.addActionById(-1, 887, 2); // Эта учетная запись подключена или находится в с...
        s.configureSoftKeys(1048, 199, 1050, 12, 199);
        return s;
    }

    /** FORM_SETTINGS — Подсветка */
    public static Screen formSettings(ScreenHandler handler) {
        Screen s = new Screen(0, 140, handler);
        s.configureHeader(236, 1009);
        s.addCheckbox(1010, 268); // Управлять подсветкой
        s.addCheckbox(1011, 269); // Выключать при блокировке
        s.addCheckbox(1012, 270); // Включать при событиях
        s.addDropdown(1013, 1015, 271); // Выключать:
        s.addCheckbox(1014, 272); // Не выключать при навигации
        s.configureSoftKeys(1053, 12, 1050, 12, 0);
        return s;
    }

    /** INVITE_TOS_SCREEN — Внимание! */
    public static Screen inviteTosScreen(ScreenHandler handler) {
        Screen s = new Screen(0, 138, handler);
        s.configureHeader(156, 1018);
        s.addLabelSeparator(1019); // Вы используете несертифицированную версию Мобил...
        s.addCheckbox(1020, 273); // Больше не показывать
        s.configureSoftKeys(1017, 12, 1021, 12, 12);
        return s;
    }

    /** ASYNC_CONFIRM_SCREEN */
    public static Screen asyncConfirmScreen(ScreenHandler handler) {
        Screen s = new Screen(2, 149, handler);
        s.configureHeader(0, 1038);
        s.configureSoftKeys(1048, 12, 1050, 12, 12);
        return s;
    }

    /** WIFI_NETWORKS — Рекомендовать */
    public static Screen wifiNetworks(ScreenHandler handler) {
        Screen s = new Screen(0, 168, handler);
        s.configureHeader(376, 529);
        s.addTextInput(712, 31, 1262, 3, 1303);
        s.configureSoftKeys(1060, 199, 1050, 12, 199);
        return s;
    }

    /** CONNECTION_SETTINGS */
    public static Screen connectionSettings(ScreenHandler handler) {
        Screen s = new Screen(10, 35, handler);
        s.configureHeader(0, 1038);
        s.showCheckboxes = true;
        s.addActionById(314, 1034, 0);
        s.addActionById(314, 1035, 1);
        s.addActionById(314, 719, 2); // Эта учетная запись подключена или находится в с...
        s.configureSoftKeys(1048, 4, 1050, 12, 4);
        return s;
    }

}
