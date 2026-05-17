package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.AppState;

/**
 * Screen factory methods.
 * Generated from config.json — do not edit manually.
 */
public final class Screens {
    private Screens() {}

    /** MAP_VIEW */
    public static Screen mapView() {
        Screen s = new Screen(1, 6);
        s.configureHeader(-1, "");
        s.configureSoftKeys("Меню", 20, "Навигация", 0, 0);
        return s;
    }

    /** MAP_VIEW_ALT */
    public static Screen mapViewAlt() {
        Screen s = new Screen(1, 6);
        s.configureHeader(-1, "");
        s.addLabelSeparator("В вашем телефоне недостаточно оперативной памяти для корректной работы карт"); // В вашем телефоне недостаточно оперативной памят...
        s.configureSoftKeys(null, 0, "Назад", 4, 0);
        return s;
    }

    /** VIEW_MODE */
    public static Screen viewMode() {
        Screen s = new Screen(10, 91);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(311, "Приблизить [*]", 0);
        s.addAction(312, "Отдалить [#]", 1);
        s.addAction(363, "Мои закладки", 120); // Водные процедуры
        s.addConditionalIf(1418, "Показать панель пробок", 310, 2); // UIKeys.FLAG_ONLINE_CUSTOM_OFF
        s.addConditionalIf(1419, "Скрыть панель пробок", 310, 3); // UIKeys.FLAG_ONLINE_CUSTOM_ON
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** GPS_SETTINGS */
    public static Screen gpsSettings() {
        Screen s = new Screen(3, 20);
        s.configureHeader(0, "");
        s.addAction(361, "Найти меня", 0);
        s.addAction(303, "Поиск по адресу", 100); // Сообщать о дорожной обстановке от имени:
        s.addConditionalIf(276, "Автомобильный режим", 365, 1); // MapKeys.FLAG_MAP_VIEW_ACTIVE
        s.addConditionalIf(277, "Социальный режим", 6, 2); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.addConditionalIf(1422, "Показать пробки", 310, 3); // MapKeys.FLAG_GPS_NO_MAP
        s.addConditionalIf(1423, "Скрыть пробки", 310, 4); // MapKeys.FLAG_GPS_WITH_MAP
        s.addAction(230, "Отображать на карте", 174); // Сплю
        s.addAction(313, "Навигация по карте", 91); // Сообщать о пробках
        s.addConditionalIf(277, "Маршруты", 229, 119); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.addAction(12, "Настройки", 131); // Кино
        s.addAction(10, "Выход", 5); // Эта операция доступна только при отсутствии сое...
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** REGION_SELECTOR — Выбор карты */
    public static Screen regionSelector() {
        Screen s = new Screen(2, 97);
        s.configureHeader(315, "Выбор карты");
        s.showCheckboxes = true;
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** MAP_POINTS — Поиск */
    public static Screen mapPoints() {
        Screen s = new Screen(0, 100);
        s.configureHeader(303, "Поиск");
        s.addTextInput("Адрес:", 255, AppState.getString(424), 0, 1248);
        s.configureSoftKeys("Поиск", 199, "Назад", 12, 199);
        return s;
    }

    /** MAP_OVERLAY */
    public static Screen mapOverlay() {
        Screen s = new Screen(2, 101);
        s.configureHeader(303, "");
        s.configureSoftKeys("Выбрать", 6, "Назад", 12, 199);
        return s;
    }

    /** MAP_TOOLTIP — Ссылка на карту */
    public static Screen mapTooltip() {
        Screen s = new Screen(0, 110);
        s.configureHeader(308, "Ссылка на карту");
        s.addTextInput("Название:", 63, AppState.getString(424), 0, 1249);
        s.configureSoftKeys("Отправить", 111, "Назад", 12, 0);
        return s;
    }

    /** PEOPLE_NEARBY — Кому отправить */
    public static Screen peopleNearby() {
        Screen s = new Screen(0, 111);
        s.configureHeader(308, "Кому отправить");
        s.configureSoftKeys("Отправить", 6, "Назад", 12, 6);
        return s;
    }

    /** XMPP_MAP_CONTEXT */
    public static Screen xmppMapContext() {
        Screen s = new Screen(11, 113);
        s.configureHeader(303, "");
        s.addConditionalIf(1432, "Написать сообщение", 0, 0);
        s.addConditionalIf(1436, "Фото", 220, 1);
        s.addConditionalIf(1429, "Анкета", 17, 2); // Эта учетная запись подключена или находится в с...
        s.addConditionalIf(1433, "Добавить контакт", 7, 3); // Эта учетная запись не подключена к серверу.
        s.addConditionalIf(1431, "Показать контакты", 6, 4); // Эта операция доступна только при соединении с с...
        s.addConditionalIf(1430, "Показать контакты", 6, 5); // Эта операция доступна только при отсутствии сое...
        s.addConditionalIf(1428, "Уточнить", 361, 6); // Имя не может быть пустым.
        s.addConditionalIf(1428, "Видимость", 369, 7); // Заполните текст запроса авторизации.
        s.addConditionalIf(1427, "Список объектов", 9, 8); // Группа не пуста. Удаление невозможно.
        s.addConditionalIf(1426, "Дополнительно", 9, 9); // Операция недопустима над специальными группами.
        s.addConditionalIf(1574, "Проехать здесь", 215, 10); // UIKeys.FLAG_ROUTE_POINT_VISIBLE
        s.addConditionalIf(1575, "Удалить точку из маршрута", 224, 11); // UIKeys.FLAG_ROUTE_POINT_HIDDEN
        s.addConditionalIf(1546, "Удалить маршрут", 34, 12); // MapKeys.FLAG_CHAT_HAS_ITEMS
        s.addConditionalIf(1424, "Спрятать", 34, 13); // Местоположение не определено
        s.addConditionalIf(277, "Маршрут отсюда", 228, 14); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.addConditionalIf(277, "Маршрут сюда", 229, 15); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.addConditionalIf(1434, "Сообщить о точке", 223, 16); // , запись в блог, 
        s.addConditionalUnless(1425, "Добавить в закладки", 362, 17); // Псевдоним, Имя, Фамилия, E-mail, ... (12 total)
        s.addConditionalIf(1437, "Указать что я здесь", 361, 18); // Город: 
        s.addConditionalIf(1435, "Отправить ссылку на точку", 308, 19); // Страница: 
        s.addConditionalIf(1425, "Удалить закладку", 34, 20); // Возраст: 
        s.addAction(311, "Приблизить [*]", 21); // Примечания: 
        s.addAction(312, "Отдалить [#]", 22); // Клиент: 
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** SAVE_LOCATION — Сохранить закладку */
    public static Screen saveLocation() {
        Screen s = new Screen(0, 114);
        s.configureHeader(360, "Сохранить закладку");
        s.addTextInput("Название:", 63, AppState.getString(424), 0, 1250);
        s.configureSoftKeys("Сохранить", 6, "Назад", 12, 6);
        return s;
    }

    /** MAP_ROUTE */
    public static Screen mapRoute() {
        Screen s = new Screen(2, 116);
        s.configureHeader(303, "");
        s.configureSoftKeys("Выбрать", 118, "Назад", 12, 199);
        return s;
    }

    /** MAP_STATUS */
    public static Screen mapStatus() {
        Screen s = new Screen(10, 117);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(362, "Добавить в закладки", 114); // Выбрать на карте
        s.addAction(360, AppState.getString(1251), 120); // Водные процедуры
        s.addConditionalUnless(253, "Показывать закладки", 360, 199); // MapKeys.FLAG_GPS_ACTIVE
        s.addConditionalIf(253, "Скрывать закладки", 360, 199); // MapKeys.FLAG_GPS_ACTIVE
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** SEND_TO_CONTACT — Кому отправить */
    public static Screen sendToContact() {
        Screen s = new Screen(0, 118);
        s.configureHeader(16, "Кому отправить");
        s.configureSoftKeys("Отправить", 6, "Назад", 12, 6);
        return s;
    }

    /** CHAT_ROOM_OPTIONS */
    public static Screen chatRoomOptions() {
        Screen s = new Screen(10, 119);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(228, "Маршрут отсюда", 0);
        s.addAction(229, "Маршрут сюда", 1);
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** MAP_ROUTE_SELECT — Мои закладки */
    public static Screen mapRouteSelect() {
        Screen s = new Screen(0, 120);
        s.configureHeader(360, "Мои закладки");
        s.configureSoftKeys("Действия", 199, "Назад", 12, 199);
        return s;
    }

    /** CHAT_LIST_OPTIONS */
    public static Screen chatListOptions() {
        Screen s = new Screen(3, 121);
        s.configureHeader(0, "");
        s.addAction(308, "Перейти", 6); // Имя не может быть пустым.
        s.addAction(16, "Отправить", 118); // -нет-
        s.addAction(34, "Удалить", 120); // Водные процедуры
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** MMP_ACCOUNT_SELECT — Настройки */
    public static Screen mmpAccountSelect() {
        Screen s = new Screen(0, 129);
        s.configureHeader(12, "Настройки");
        s.addTextSeparator("Устройство GPS:"); // Устройство GPS:
        s.addPassword(239);
        s.addDropdown("Включать встроенный GPS:", 384, 44); // Включать встроенный GPS:
        s.addDropdown("Выключать встроенный GPS через:", 385, 45); // Выключать встроенный GPS через:
        s.addCheckbox("Корректировать GPS координаты", 255); // Корректировать GPS координаты
        s.addCheckbox("Предупреждать о маневрах", 238); // Предупреждать о маневрах
        s.addCheckbox("Сообщать о пробках", 47); // Сообщать о пробках
        s.addTextSeparator("Хранение данных:"); // Хранение данных:
        s.addCheckbox("Использовать карты на флэш-диске", 256); // Использовать карты на флэш-диске
        s.addCheckbox("Сохранять загружаемые карты на флэш-диск", 257); // Сохранять загружаемые карты на флэш-диск
        s.addLabelSeparator("Использовать карты из каталога:"); // Использовать карты из каталога:
        s.addLogin("*/", 233); // */
        s.addTextSeparator("Прочее:"); // Прочее:
        s.addDropdown("Сообщать о дорожной обстановке от имени:", 1252, 1438); // Сообщать о дорожной обстановке от имени:
        s.addCheckbox("Автоматически обновлять мое местоположение (CellID, GPS):", 285); // Автоматически обновлять мое местоположение (Cel...
        s.configureSoftKeys("Сохранить", 12, "Назад", 12, 199);
        return s;
    }

    /** PEOPLE_SEARCH */
    public static Screen peopleSearch() {
        Screen s = new Screen(10, 131);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(315, "Выбор города", 97); // Предупреждать о маневрах
        s.addConditionalIf(1439, "Подключить GPS", 219, 6); // Имя не может быть пустым.
        s.addConditionalIf(1440, "Отключить GPS", 226, 6); // Имя не может быть пустым.
        s.addAction(34, "Очистить карту", 6); // Имя не может быть пустым.
        s.addAction(12, "Общие", 129); // Музыка
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** SAVED_LOCATIONS */
    public static Screen savedLocations() {
        Screen s = new Screen(2, 153);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.configureSoftKeys("Выбрать", 154, "Назад", 12, 154);
        return s;
    }

    /** SHARE_LOCATION — Комментарий */
    public static Screen shareLocation() {
        Screen s = new Screen(0, 154);
        s.configureHeader(303, "Комментарий");
        s.addTextInput("Комментарий", 255, AppState.getString(424), 0, 1254);
        s.configureSoftKeys("Отправить", 6, "Назад", 6, 6);
        return s;
    }

    /** GROUP_MANAGEMENT_ALT — Выбор */
    public static Screen groupManagementAlt() {
        Screen s = new Screen(0, 155);
        s.configureHeader(230, "Выбор");
        s.configureSoftKeys("Сохранить", 6, "Назад", 6, 0);
        return s;
    }

    /** MAP_SEARCH */
    public static Screen mapSearch() {
        Screen s = new Screen(11, 158);
        s.configureHeader(0, "");
        s.addLabelSeparator(null);
        s.addConditionalIf(1441, "Из моего местоположения", 361, 0);
        s.addAction(308, "Выбрать на карте", 6); // Имя не может быть пустым.
        s.addAction(303, "Поиск", 100); // Сообщать о дорожной обстановке от имени:
        s.addAction(360, AppState.getString(1251), 120); // Водные процедуры
        s.configureSoftKeys("Выбрать", 199, "Назад", 6, 199);
        return s;
    }

    /** WIFI_ACCOUNT_LIST */
    public static Screen wifiAccountList() {
        Screen s = new Screen(2, 159);
        s.configureHeader(0, "");
        s.addLabelSeparator("Сообщать о точках:"); // Сообщать о точках:
        s.addAction(23, "анонимно", 153); // В поисках...
        s.addLabelSeparator("От имени учетной записи:"); // От имени учетной записи:
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** MAILBOX_OPTIONS */
    public static Screen mailboxOptions() {
        Screen s = new Screen(2, 167);
        s.configureHeader(0, "");
        s.addAction(308, "Указать на карте", 0);
        s.addAction(303, "Поиск по названию", 1);
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** FORM_LIST */
    public static Screen formList() {
        Screen s = new Screen(2, 170);
        s.configureHeader(0, "");
        s.configureSoftKeys("Выбрать", 113, "Назад", 12, 113);
        return s;
    }

    /** MRIM_ACCOUNT_SELECT */
    public static Screen mrimAccountSelect() {
        Screen s = new Screen(2, 172);
        s.configureHeader(0, "");
        s.addLabelSeparator("Выберите учетную запись:"); // Выберите учетную запись:
        s.configureSoftKeys("Выбрать", 173, "Назад", 6, 173);
        return s;
    }

    /** MAP_OPTIONS */
    public static Screen mapOptions() {
        Screen s = new Screen(10, 174);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addConditionalIf(276, "Люди на карте", 6, 175); // MapKeys.FLAG_MAP_VIEW_ACTIVE
        s.addConditionalIf(277, "Дорожные обьекты", 230, 155); // ContactKeys.FLAG_CONTACT_LIST_ACTIVE
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** NEARBY_SETTINGS — Люди на карте */
    public static Screen nearbySettings() {
        Screen s = new Screen(0, 175);
        s.configureHeader(12, "Люди на карте");
        s.addCheckbox("Я", 278); // Я
        s.addCheckbox("Мои контакты", 279); // Мои контакты
        s.addCheckbox("Все", 280); // Все
        s.configureSoftKeys("Сохранить", 6, "Назад", 12, 6);
        return s;
    }

    /** CONTACT_POPUP — Контакты */
    public static Screen contactPopup() {
        Screen s = new Screen(0, 176);
        s.configureHeader(6, "Контакты");
        s.configureSoftKeys("Действия", 199, "Отмена", 12, 199);
        return s;
    }

    /** SEARCH_ENTRY */
    public static Screen searchEntry() {
        Screen s = new Screen(3, 177);
        s.configureHeader(0, "");
        s.addAction(220, "Фото", 102); // Список карт еще не был загружен
        s.addAction(17, "Анкета", 0);
        s.addAction(7, "Добавить пользователя", 1);
        s.addConditionalIf(1445, "Следующие 10", 6, 2); // UIKeys.FLAG_PHONE_HAS_NEXT
        s.addConditionalIf(1446, "Предыдущие 10", 6, 3); // UIKeys.FLAG_PHONE_HAS_PREV
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** EDIT_SCREEN */
    public static Screen editScreen() {
        Screen s = new Screen(2, 178);
        s.configureHeader(-1, "");
        s.addLabelSeparator("Выберите режим использования:"); // Выберите режим использования:
        s.addAction(6, "Социальный режим", 0);
        s.addAction(365, "Автомобильный режим", 1);
        s.configureSoftKeys("Выбрать", 6, "Отмена", 4, 6);
        return s;
    }

    /** STATUS_PREVIEW — Действия */
    public static Screen statusPreview() {
        Screen s = new Screen(0, 84);
        s.configureHeader(9, "Действия");
        s.addConditionalIf(1456, "Отправить", -1, 40); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1461, "Смайлики", -1, 93); // Здравствуйте, 
        s.addAction(-1, "Вставить текст из архива", 123); // Пиво
        s.addAction(-1, "Вставить шаблон", 95); // С уважением
        s.addConditionalIf(1460, "Вставить текст из буфера", -1, 63); // UIKeys.FLAG_RESOURCE_LOADING
        s.addConditionalIf(1456, "Копировать в буфер", -1, 63); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1456, "Задать как шаблон", -1, 94); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1456, "Ретранслит", -1, 63); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addAction(-1, "Отменить сообщение", 40); // Отдалить [#]
        s.configureSoftKeys("Выбрать", 199, "Назад", 63, 199);
        return s;
    }

    /** CHOICE_DIALOG */
    public static Screen choiceDialog() {
        Screen s = new Screen(2, 32);
        s.configureHeader(0, "");
        s.configureSoftKeys("Выбрать", 12, "Назад", 12, 12);
        return s;
    }

    /** ACCOUNT_LIST */
    public static Screen accountList() {
        Screen s = new Screen(3, 1);
        s.configureHeader(0, "");
        s.addConditionalIf(1462, "Подкл./ Откл.", 305, 15); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addConditionalUnless(1462, "Подкл./ Откл.", 305, 15); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addConditionalIf(1463, "Статус", 306, 3); // SessionKeys.FLAG_HAS_MRIM_ACCOUNTS
        s.addConditionalUnless(1463, "Статус", 306, 3); // SessionKeys.FLAG_HAS_MRIM_ACCOUNTS
        s.addConditionalIf(1464, "Местоположение", 365, 152); // SessionKeys.FLAG_HAS_MRIM_ACCOUNTS_2
        s.addConditionalUnless(1465, "Местоположение", 365, 152); // SessionKeys.FLAG_HAS_XMPP_ACCOUNTS
        s.addAction(22, "Контакты", 5); // Эта операция доступна только при отсутствии сое...
        s.addAction(156, "Mail.Ru", 146); // Засыпаю
        s.addAction(8, "Настройки", 7); // Заполните текст запроса авторизации.
        s.addAction(9, "Дополнительно", 132); // Веселюсь
        s.addAction(10, "Выход", 10); // Контакт и так в этой группе.
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** KEY_MAPPING */
    public static Screen keyMapping() {
        Screen s = new Screen(10, 132);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(28, "Трафик", 8); // Группа не пуста. Удаление невозможно.
        s.addAction(5, "Блокировка", 137); // Сплю
        s.addConditionalIf(1543, "Свернуть", 15, 4); // UIKeys.FLAG_KNOWN_DEVICE
        s.addAction(9, "О программе", 9); // Операция недопустима над специальными группами.
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** ABOUT — Мобильный агент */
    public static Screen about() {
        Screen s = new Screen(5, 9);
        s.configureHeader(9, "Мобильный агент");
        s.addSeparator("Версия:", AppState.getString(1288)); // Версия:
        s.addSeparator("Copyright (c)", "Mail.Ru, 2006-2010");
        s.addSeparator("Сборка:", AppState.getString(1287)); // Сборка:
        s.addSeparator("Официальный сайт:", "http://agent.mail.ru/"); // Официальный сайт:
        s.addSeparator("Платформа:", AppState.getString(1376)); // Платформа:
        s.addSeparator("Модель:", AppState.getString(1377)); // Модель:
        s.addSeparator("Память (всего):", AppState.getString(1284)); // Память (всего):
        s.addSeparator("Память (свободно):", AppState.getString(1285)); // Память (свободно):
        s.configureSoftKeys("Обновление", 57, "Назад", 12, 0);
        return s;
    }

    /** BLOG_POST — Микроблог */
    public static Screen blogPost() {
        Screen s = new Screen(0, 147);
        s.configureHeader(2, "Микроблог");
        s.addTextInput("Сказать:", 500, AppState.getString(424), 0, 1286);
        s.addCheckbox(AppState.getString(1284), 1468);
        s.configureSoftKeys("Отправить", 4, "Отмена", 12, 0);
        return s;
    }

    /** MESSAGE_INPUT — Микроблог */
    public static Screen messageInput() {
        Screen s = new Screen(0, 115);
        s.configureHeader(2, "Микроблог");
        s.addSeparator(AppState.getString(1284), AppState.getString(1287));
        s.addTextInput("Ответить:", 500, AppState.getString(424), 0, 1286);
        s.addCheckbox("сделать моим статусом", 1507); // сделать моим статусом
        s.configureSoftKeys("Отправить", 40, "Отмена", 12, 0);
        return s;
    }

    /** GROUP_MANAGEMENT */
    public static Screen groupManagement() {
        Screen s = new Screen(10, 146);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addConditionalIf(1462, "Рекомендовать", 376, 89); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addConditionalUnless(1462, "Рекомендовать", 376, 89); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addAction(16, "Почта", 36); // Здравствуйте. Я нашел вас на карте в Mail.Ru Аг...
        s.addAction(264, "Карты", 6); // Имя не может быть пустым.
        s.addConditionalIf(1462, "Сказать", 2, 147); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.addConditionalUnless(1462, "Сказать", 2, 147); // SessionKeys.FLAG_HAS_MULTIPLE_MRIM
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CONTACT_LIST_TEMPLATE */
    public static Screen contactListTemplate() {
        Screen s = new Screen(1, 4);
        s.configureHeader(-1, "");
        s.configureSoftKeys("Меню", 1, "Действия", 199, 199);
        return s;
    }

    /** MULTI_ACCOUNT_LIST */
    public static Screen multiAccountList() {
        Screen s = new Screen(10, 25);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** MESSAGE_SUMMARY */
    public static Screen messageSummary() {
        Screen s = new Screen(9, 40);
        s.configureHeader(0, AppState.getString(1290));
        s.configureSoftKeys("Действия", 92, "Назад", 12, 0);
        return s;
    }

    /** SERVER_ADDRESS — Шаблоны сообщений */
    public static Screen serverAddress() {
        Screen s = new Screen(0, 95);
        s.configureHeader(32, "Шаблоны сообщений");
        s.configureSoftKeys("Выбрать", 63, "Назад", 12, 63);
        return s;
    }

    /** PHONE_INPUT — Шаблоны сообщений */
    public static Screen phoneInput() {
        Screen s = new Screen(0, 94);
        s.configureHeader(32, "Шаблоны сообщений");
        s.configureSoftKeys("Изменить", 12, "Назад", 12, 12);
        return s;
    }

    /** EMOTICON_PICKER — Смайлики */
    public static Screen emoticonPicker() {
        Screen s = new Screen(6, 93);
        s.configureHeader(46, "Смайлики");
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** MESSAGE_DETAIL — Вставить текст из архива */
    public static Screen messageDetail() {
        Screen s = new Screen(0, 123);
        s.configureHeader(32, "Вставить текст из архива");
        s.configureSoftKeys("Выбрать", 63, "Назад", 12, 63);
        return s;
    }

    /** MAP_MENU */
    public static Screen mapMenu() {
        Screen s = new Screen(10, 7);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(11, "Учетные записи", 25); //  лет
        s.addAction(12, "Интерфейс", 26); //  год
        s.addAction(6, "Список контактов", 29); // Слушает: 
        s.addAction(13, "Уведомления", 28); // не определен
        s.addAction(14, "Горячие клавиши", 27); //  года
        s.addAction(32, "Шаблоны сообщений", 33); // Навигация
        s.addAction(16, "Почта", 56); // Я в пробке. Буду через   мин.
        s.addConditionalUnless(1545, "Карты", 264, 129); // Музыка
        s.addAction(29, "Тарификация", 14); // Нельзя отправлять пустое сообщение.
        s.addAction(309, "Настройка сети", 50); // Как твои дела?
        s.addConditionalIf(1538, "Подсветка", 236, 140); // UIKeys.FLAG_ADVANCED_FEATURES
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CONTACT_GROUPS */
    public static Screen contactGroups() {
        Screen s = new Screen(10, 47);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(8, "Редактировать", 76); // Название:
        s.addAction(28, "Трафик", 8); // Группа не пуста. Удаление невозможно.
        s.addAction(34, "Удалить", 77); // Введите название закладки
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** ACCOUNT_SWITCHER */
    public static Screen accountSwitcher() {
        Screen s = new Screen(10, 15);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(11, "Все", 4); // Эта операция доступна только при соединении с с...
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** ACCOUNTS_MENU */
    public static Screen accountsMenu() {
        Screen s = new Screen(10, 5);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(7, "Добавить контакт", 0);
        s.addAction(27, "Добавить телефонный контакт", 0);
        s.addAction(232, "Начать конференцию", 0);
        s.addAction(35, "Создать группу", 0);
        s.addAction(22, "Списки видимости", 124); // Думаю
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** XMPP_CONTEXT_MENU — Учетная запись Mail.Ru */
    public static Screen xmppContextMenu() {
        Screen s = new Screen(0, 76);
        s.configureHeader(156, "Учетная запись Mail.Ru");
        s.addTextInput("Логин:", 63, "UCB_BASIC_LATIN", 0, 1292);
        s.addDropdown("Домен:", 694, 1474); // Домен:
        s.addTextInput("Пароль:", 63, "UCB_BASIC_LATIN", 327680, 1293);
        s.configureSoftKeys("Сохранить", 25, "Отмена", 12, 0);
        return s;
    }

    /** XMPP_LOGIN — Учетная запись ICQ */
    public static Screen xmppLogin() {
        Screen s = new Screen(0, 76);
        s.configureHeader(256, "Учетная запись ICQ");
        s.addLogin(AppState.getString(12), 0);
        s.addDropdown("Домен:", 694, 1474);
        s.configureSoftKeys("Сохранить", 25, "Отмена", 12, 0);
        return s;
    }

    /** PRIVACY_SETTINGS — Шаблоны сообщений */
    public static Screen privacySettings() {
        Screen s = new Screen(0, 33);
        s.configureHeader(32, "Шаблоны сообщений");
        s.addTextInput("", 255, AppState.getString(424), 0, 48);
        s.addTextInput("", 255, AppState.getString(424), 0, 49);
        s.addTextInput("", 255, AppState.getString(424), 0, 50);
        s.addTextInput("", 255, AppState.getString(424), 0, 51);
        s.addTextInput("", 255, AppState.getString(424), 0, 52);
        s.addTextInput("", 255, AppState.getString(424), 0, 53);
        s.addTextInput("", 255, AppState.getString(424), 0, 54);
        s.addTextInput("", 255, AppState.getString(424), 0, 55);
        s.addTextInput("", 255, AppState.getString(424), 0, 56);
        s.addTextInput("", 255, AppState.getString(424), 0, 57);
        s.addTextInput("", 255, AppState.getString(424), 0, 58);
        s.addTextInput("", 255, AppState.getString(424), 0, 59);
        s.addTextInput("", 255, AppState.getString(424), 0, 60);
        s.addTextInput("", 255, AppState.getString(424), 0, 61);
        s.addTextInput("", 255, AppState.getString(424), 0, 62);
        s.configureSoftKeys("Сохранить", 12, "Назад", 12, 0);
        return s;
    }

    /** THEME_SETTINGS — Интерфейс */
    public static Screen themeSettings() {
        Screen s = new Screen(0, 26);
        s.configureHeader(12, "Интерфейс");
        s.addCheckbox("Полный экран", 71); // Полный экран
        s.addCheckbox("Софт-кнопки наоборот", 65); // Софт-кнопки наоборот
        s.addCheckbox("Почта в закладках", 67); // Почта в закладках
        s.addCheckbox("Карты в закладках", 68); // Карты в закладках
        s.addDropdown("Размер шрифта:", 566, 73); // Размер шрифта:
        s.addDropdown("Цветовая схема:", 567, 72); // Цветовая схема:
        s.addCheckbox("Использовать курсив", 70); // Использовать курсив
        s.addCheckbox("Затемнение экрана", 66); // Затемнение экрана
        s.addDropdown("Язык по умолчанию:", 570, 74); // Язык по умолчанию:
        s.addDropdown("Коррекция времени:", 574, 246); // Коррекция времени:
        s.addCheckbox("Подтверждать выход", 69); // Подтверждать выход
        s.configureSoftKeys("Сохранить", 4, "Назад", 12, 0);
        return s;
    }

    /** SOUND_SETTINGS — Уведомления */
    public static Screen soundSettings() {
        Screen s = new Screen(0, 28);
        s.configureHeader(13, "Уведомления");
        s.addDropdown("Новое сообщение:", 591, 75); // Новое сообщение:
        s.addCheckbox("Вибрация", 76); // Вибрация
        s.addDropdown("Контакт в онлайне:", 591, 77); // Контакт в онлайне:
        s.addCheckbox("Вибрация", 78); // Вибрация
        s.addDropdown("Новое письмо:", 591, 79); // Новое письмо:
        s.addCheckbox("Вибрация", 80); // Вибрация
        s.addDropdown("Запрос авторизации:", 591, 81); // Запрос авторизации:
        s.addCheckbox("Вибрация", 82); // Вибрация
        s.addDropdown("Отправка сообщения:", 591, 83); // Отправка сообщения:
        s.addCheckbox("Вибрация", 84); // Вибрация
        s.addDropdown("Новый микроблог:", 591, 240); // Новый микроблог:
        s.addCheckbox("Вибрация", 241); // Вибрация
        s.addDropdown("Ошибка:", 591, 85); // Ошибка:
        s.addCheckbox("Вибрация", 86); // Вибрация
        s.addCheckbox("Управление громкостью", 87); // Управление громкостью
        s.addNumericInput("Уровень громкости (0-100):", 3, AppState.getString(1262), 88, 0, 100, 50);
        s.addCheckbox("Тихий режим", 89); // Тихий режим
        s.configureSoftKeys("Сохранить", 12, "Назад", 12, 0);
        return s;
    }

    /** CONTACT_SETTINGS — Почта */
    public static Screen contactSettings() {
        Screen s = new Screen(0, 56);
        s.configureHeader(16, "Почта");
        s.addTextSeparator("Уведомления о новой почте:"); // Уведомления о новой почте:
        s.addCheckbox("Иконка", 90); // Иконка
        s.addCheckbox("Всплывающее окно", 91); // Всплывающее окно
        s.addTextSeparator("Отправка и просмотр:"); // Отправка и просмотр:
        s.addCheckbox("Вставлять приветствие", 92); // Вставлять приветствие
        s.addTextInput("Приветствие:", 255, AppState.getString(424), 0, 93);
        s.addCheckbox("Вставлять подпись", 94); // Вставлять подпись
        s.addTextInput("Подпись:", 255, AppState.getString(424), 0, 95);
        s.addCheckbox("Цитировать при ответе", 96); // Цитировать при ответе
        s.addNumericInput("Загружать по:", 3, AppState.getString(1262), 97, 5, 999, 10);
        s.configureSoftKeys("Сохранить", 12, "Назад", 12, 0);
        return s;
    }

    /** MULTI_ACCOUNT_SETTINGS — Список контактов */
    public static Screen multiAccountSettings() {
        Screen s = new Screen(0, 29);
        s.configureHeader(6, "Список контактов");
        s.addTextSeparator("Учетные записи:"); // Учетные записи:
        s.addCheckbox("В отдельных закладках", 243); // В отдельных закладках
        s.addCheckbox("Строка состояния", 245); // Строка состояния
        s.addTextSeparator("Контакты:"); // Контакты:
        s.addCheckbox("Только онлайн-контакты", 98); // Только онлайн-контакты
        s.addDropdown("Столбцы:", 622, 242); // Столбцы:
        s.addTextSeparator("Группы:"); // Группы:
        s.addCheckbox("Использовать", 99); // Использовать
        s.addCheckbox("Объединять одноименные", 100); // Объединять одноименные
        s.addCheckbox("Показывать пустые", 101); // Показывать пустые
        s.addTextSeparator("Диалог:"); // Диалог:
        s.addNumericInput("Размер архива:", 3, AppState.getString(1262), 102, 5, 999, 20);
        s.addCheckbox("Принимать микроблоги", 244); // Принимать микроблоги
        s.addCheckbox("Автотранслит SMS", 106); // Автотранслит SMS
        s.addTextSeparator("Антиспам:"); // Антиспам:
        s.addCheckbox("Принимать от временных", 103); // Принимать от временных
        s.addTextSeparator("Приватность:"); // Приватность:
        s.addCheckbox("Отправлять 'Я пишу'", 104); // Отправлять 'Я пишу'
        s.addCheckbox("Скрывать платформу", 105); // Скрывать платформу
        s.configureSoftKeys("Сохранить", 4, "Назад", 12, 0);
        return s;
    }

    /** CHAT_VIEW_MODE — Настройка сети */
    public static Screen chatViewMode() {
        Screen s = new Screen(0, 50);
        s.configureHeader(309, "Настройка сети");
        s.addCheckbox("Асинхронная передача", 112); // Асинхронная передача
        s.configureSoftKeys("Сохранить", 12, "Назад", 12, 0);
        return s;
    }

    /** TRAFFIC_COST — Тарификация */
    public static Screen trafficCost() {
        Screen s = new Screen(0, 14);
        s.configureHeader(29, "Тарификация");
        s.addTextInput("Цена трафика (за 1 Мб):", 10, AppState.getString(1262), 0, 1286);
        s.addNumericInput("Округление до (Кб):", 10, AppState.getString(1262), 114, 1, 1024, 1024);
        s.addTextInput("Валюта:", 10, AppState.getString(424), 0, 117);
        s.configureSoftKeys("Сохранить", 12, "Назад", 12, 0);
        return s;
    }

    /** NOTIFICATION_SETTINGS — Горячие клавиши */
    public static Screen notificationSettings() {
        Screen s = new Screen(0, 27);
        s.configureHeader(14, "Горячие клавиши");
        s.addDropdown("*:", 641, 205); // *:
        s.addDropdown("#:", 641, 206); // #:
        s.addDropdown("0:", 641, 207); // 0:
        s.addDropdown("1:", 641, 208); // 1:
        s.addDropdown("2:", 641, 209); // 2:
        s.addDropdown("3:", 641, 210); // 3:
        s.addDropdown("4:", 641, 211); // 4:
        s.addDropdown("5:", 641, 212); // 5:
        s.addDropdown("6:", 641, 213); // 6:
        s.addDropdown("7:", 641, 214); // 7:
        s.addDropdown("8:", 641, 215); // 8:
        s.addDropdown("9:", 641, 216); // 9:
        s.configureSoftKeys("Сохранить", 12, "Назад", 12, 0);
        return s;
    }

    /** EXT_SETTINGS */
    public static Screen extSettings() {
        Screen s = new Screen(2, 151);
        s.configureHeader(303, "");
        s.showCheckboxes = true;
        s.addAction(364, "Видимый для всех", 0);
        s.addAction(365, "Видимый для списка контактов", 1);
        s.addAction(366, "Видимый для списка видящих", 2); // Эта учетная запись подключена или находится в с...
        s.addAction(367, "Невидимый для всех", 3); // Эта учетная запись не подключена к серверу.
        s.addAction(369, "Задать список видящих", 4); // Эта операция доступна только при соединении с с...
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** MAP_VIEW_SETTINGS */
    public static Screen mapViewSettings() {
        Screen s = new Screen(10, 152);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(361, "Показать где я", 6); // Имя не может быть пустым.
        s.addAction(308, "Указать где я", 162); // Я пришелец
        s.addAction(369, "Установить видимость", 151); // Курю
        s.addAction(8, "Настройки", 129); // Музыка
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** NOTIFICATION_DIALOG */
    public static Screen notificationDialog() {
        Screen s = new Screen(8, 112);
        s.configureHeader(0, "");
        s.addLabelSeparator(AppState.getString(1294));
        s.configureSoftKeys(null, 0, "Назад", 12, 12);
        return s;
    }

    /** CREATE_GROUP — Создать группу */
    public static Screen createGroup() {
        Screen s = new Screen(0, 69);
        s.configureHeader(35, "Создать группу");
        s.addTextInput("Название группы:", 63, AppState.getString(424), 0, 1295);
        s.configureSoftKeys("Сохранить", 4, "Назад", 12, 0);
        return s;
    }

    /** REGION_CHOICE — Добавить контакт */
    public static Screen regionChoice() {
        Screen s = new Screen(0, 21);
        s.configureHeader(7, "Добавить контакт");
        s.addTextInput("Логин:", 63, "UCB_BASIC_LATIN", 0, 1296);
        s.addDropdown("Домен:", 694, 1480); // Домен:
        s.addDropdown("Пол:", 680, 1481); // Пол:
        s.addNumericInput("Мин. возраст:", 3, AppState.getString(1262), 1482, 1, 999, -1);
        s.addNumericInput("Макс. возраст:", 3, AppState.getString(1262), 1483, 1, 999, -1);
        s.addDropdown("Знак Зодиака:", 685, 1484); // Знак Зодиака:
        s.addDropdown("Страна:", 1300, 1485); // Страна:
        s.addDropdown("Регион:", 684, 1486); // Регион:
        s.addDropdown("Город:", 684, 1487); // Город:
        s.addTextInput("Псевдоним:", 63, AppState.getString(424), 0, 1297);
        s.addTextInput("Имя:", 63, AppState.getString(424), 0, 1298);
        s.addTextInput("Фамилия:", 63, AppState.getString(424), 0, 1299);
        s.addDropdown("Месяц рождения:", 686, 1488); // Месяц рождения:
        s.addDropdown("День рождения:", 687, 1489); // День рождения:
        s.addCheckbox("Искать только подключенных", 1490); // Искать только подключенных
        s.configureSoftKeys("Поиск", 44, "Назад", 12, 0);
        return s;
    }

    /** XMPP_LOGIN_ALT — Учетная запись Jabber */
    public static Screen xmppLoginAlt() {
        Screen s = new Screen(0, 76);
        s.configureHeader(383, "Учетная запись Jabber");
        s.addTextInput("Логин:", 63, "UCB_BASIC_LATIN", 0, 1292);
        s.addTextInput("Пароль:", 63, "UCB_BASIC_LATIN", 327680, 1293);
        s.configureSoftKeys("Сохранить", 25, "Отмена", 12, 0);
        return s;
    }

    /** XMPP_LOGIN_ALT2 — Учетная запись ВКонтакте */
    public static Screen xmppLoginAlt2() {
        Screen s = new Screen(0, 76);
        s.configureHeader(387, "Учетная запись ВКонтакте");
        s.addLogin(AppState.getString(12), 0);
        s.addDropdown("Домен:", 694, 1474);
        s.addDropdown("Город:", 684, 1487);
        s.configureSoftKeys("Сохранить", 25, "Отмена", 12, 0);
        return s;
    }

    /** EMPTY_SCREEN — Списки видимости */
    public static Screen emptyScreen() {
        Screen s = new Screen(10, 124);
        s.configureHeader(22, "Списки видимости");
        s.showCheckboxes = true;
        s.addAction(17, "Я всегда видим для...", 125); // Когда я ем...
        s.addAction(19, "Я всегда невидим для...", 126); // Телевизор
        s.addAction(20, "Игнорируемые", 127); // На встрече
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CONTACT_EDITOR — Имя и телефоны */
    public static Screen contactEditor() {
        Screen s = new Screen(0, 19);
        s.configureHeader(18, "Имя и телефоны");
        s.addTextInput("Имя контакта:", 63, AppState.getString(424), 0, 1302);
        s.configureSoftKeys("Сохранить", 4, "Назад", 12, 0);
        return s;
    }

    /** ADD_MRIM_CONTACT — Добавить телефонный контакт */
    public static Screen addMrimContact() {
        Screen s = new Screen(0, 22);
        s.configureHeader(27, "Добавить телефонный контакт");
        s.addTextSeparator(AppState.getString(12));
        s.addAction(3, AppState.getString(1303), 424);
        s.addAction(3, AppState.getString(1304), 424);
        s.configureSoftKeys("Сохранить", 4, "Назад", 12, 0);
        return s;
    }

    /** RENAME_GROUP — Переименовать */
    public static Screen renameGroup() {
        Screen s = new Screen(0, 70);
        s.configureHeader(35, "Переименовать");
        s.addTextInput("Название группы:", 63, AppState.getString(424), 0, 1306);
        s.configureSoftKeys("Сохранить", 4, "Назад", 12, 0);
        return s;
    }

    /** ADD_CONTACT — Добавить контакт */
    public static Screen addContact() {
        Screen s = new Screen(0, 21);
        s.configureHeader(256, "Добавить контакт");
        s.addNumericInput("UIN:", 10, AppState.getString(1262), 1491, 1, 2147483647, -1);
        s.addTextInput("Псевдоним:", 63, AppState.getString(424), 0, 1307);
        s.addTextInput("Имя:", 63, AppState.getString(424), 0, 1308);
        s.addTextInput("Фамилия:", 63, AppState.getString(424), 0, 1309);
        s.addTextInput("Email:", 63, "UCB_BASIC_LATIN", 0, 1310);
        s.addTextInput("City:", 63, AppState.getString(424), 0, 1311);
        s.addTextInput("Keyword:", 63, AppState.getString(424), 0, 1312);
        s.addCheckbox("Искать только подключенных", 1492); // Искать только подключенных
        s.configureSoftKeys("Поиск", 44, "Назад", 12, 0);
        return s;
    }

    /** PHONE_GROUPS — Отправить SMS */
    public static Screen phoneGroups() {
        Screen s = new Screen(0, 65);
        s.configureHeader(27, "Отправить SMS");
        s.addDropdown("Телефон:", 1313, 1493); // Телефон:
        s.addTextInput("Сообщение:", 160, AppState.getString(424), 0, 1279);
        s.configureSoftKeys("Действия", 87, "Назад", 12, 0);
        return s;
    }

    /** CHAT_STATUS */
    public static Screen chatStatus() {
        Screen s = new Screen(3, 87);
        s.configureHeader(0, "");
        s.addConditionalIf(1456, "Отправить", -1, 40); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1456, "Транслитерация", -1, 65); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addAction(-1, "Вставить шаблон", 99); // Хранение данных:
        s.addConditionalIf(1460, "Вставить текст из буфера", -1, 65); // UIKeys.FLAG_RESOURCE_LOADING
        s.addConditionalIf(1456, "Копировать в буфер", -1, 65); // UIKeys.FLAG_STATUS_TEXT_SET
        s.addConditionalIf(1456, "Задать как шаблон", -1, 98); // UIKeys.FLAG_STATUS_TEXT_SET
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CONTACT_GROUP_MENU */
    public static Screen contactGroupMenu() {
        Screen s = new Screen(4, 30);
        s.configureHeader(0, "");
        s.addAction(35, "Переименовать", 70); // Видимость
        s.addAction(34, "Удалить", 71); // Указать что я здесь
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CONTACT_ACTIONS_MENU */
    public static Screen contactActionsMenu() {
        Screen s = new Screen(4, 0);
        s.configureHeader(0, "");
        s.addConditionalIf(3707, "Архив сообщений", 21, 40); // ContactKeys.FLAG_CONTACT_MENU_MODE
        s.addConditionalUnless(1496, "Новое сообщение", 0, 63); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1501, "Показать на карте", 365, 6); // ContactKeys.SCREEN_FLAGS_END
        s.addConditionalIf(1497, "Отправить", 376, 166); // ContactKeys.FLAG_CONTACT_IS_USER
        s.addConditionalIf(1496, "Отправить SMS", 27, 65); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1497, "Разбудить собеседника", 33, 40); // ContactKeys.FLAG_CONTACT_IS_USER
        s.addConditionalUnless(1496, "Анкета", 17, 85); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1498, "Добавить", 23, 66); // ContactKeys.FLAG_CONTACT_IS_ONLINE
        s.addConditionalIf(1499, "Перезапросить авторизацию", 23, 66); // ContactKeys.FLAG_CONTACT_HAS_UNREAD
        s.addConditionalIf(1504, "Подписка", 314, 35); // UIKeys.FLAG_XMPP_CAN_EDIT
        s.addAction(18, "Редактировать", 19); // Страница: 
        s.addConditionalUnless(1496, "Дополнительно", 9, 64); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1496, "Очистить архив сообщений", 34, 128); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalIf(1496, "Удалить", 19, 71); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CONTACT_MENU */
    public static Screen contactMenu() {
        Screen s = new Screen(4, 0);
        s.configureHeader(0, "");
        s.addConditionalIf(12, AppState.getString(7), 14, 199);
        s.addAction(0, "Новое сообщение", 63); // Маршрут сюда
        s.addConditionalIf(1503, "Отправить", 376, 166); // Водные процедуры
        s.addAction(33, "Разбудить собеседника", 40); // Отдалить [#]
        s.addAction(6, "Список участников", 150); // Любовь
        s.addAction(7, "Добавить в конференцию", 144); // PSP
        s.addAction(726, null, 0);
        s.addAction(34, "Очистить архив сообщений", 128); // Кофе
        s.addAction(20, "Игнорировать", 11); // Пустой пароль недопустим.
        s.addAction(19, "Выйти из конференции", 71); // Указать что я здесь
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CONTACT_INFO_VIEW_SCREEN — Анкета */
    public static Screen contactInfoViewScreen() {
        Screen s = new Screen(5, 96);
        s.configureHeader(17, "Анкета");
        s.configureSoftKeys("Фото", 0, "Назад", 12, 0);
        return s;
    }

    /** CAPTCHA — Фото */
    public static Screen captcha() {
        Screen s = new Screen(5, 106);
        s.configureHeader(17, "Фото");
        s.configureSoftKeys(null, 0, "Назад", 12, 0);
        return s;
    }

    /** PROFILE_LIST — Обновление */
    public static Screen profileList() {
        Screen s = new Screen(5, 59);
        s.configureHeader(36, "Обновление");
        s.addSeparator("Доступна новая версия Мобильного Агента", AppState.getString(1284)); // Доступна новая версия Мобильного Агента
        s.addSeparator("Загрузить новую версию можно, открыв в wap-браузере телефона следующую ссылку:", AppState.getString(1285)); // Загрузить новую версию можно, открыв в wap-брау...
        s.addLabelSeparator("Произвести попытку загрузки?"); // Произвести попытку загрузки?
        s.configureSoftKeys("Да", 12, "Нет", 12, 12);
        return s;
    }

    /** SEARCH_RESULT_LIST — Результаты поиска */
    public static Screen searchResultList() {
        Screen s = new Screen(0, 73);
        s.configureHeader(17, "Результаты поиска");
        s.configureSoftKeys("Добавить", 66, "Назад", 12, 103);
        return s;
    }

    /** CONTACT_INFO_DETAIL_SCREEN — Анкета */
    public static Screen contactInfoDetailScreen() {
        Screen s = new Screen(5, 103);
        s.configureHeader(17, "Анкета");
        s.configureSoftKeys("Фото", 0, "Назад", 12, 0);
        return s;
    }

    /** CONTACT_ADD_SCREEN — Запрос авторизации */
    public static Screen contactAddScreen() {
        Screen s = new Screen(0, 66);
        s.configureHeader(23, "Запрос авторизации");
        s.configureSoftKeys("Дальше", 4, "Назад", 12, 0);
        return s;
    }

    /** CONTACT_LIST_SCREEN — Запрос авторизации */
    public static Screen contactListScreen() {
        Screen s = new Screen(0, 66);
        s.configureHeader(23, "Запрос авторизации");
        s.addSeparator("UIN:", AppState.getString(1320));
        s.addAction(738, AppState.getString(1320), 1);
        s.addTextSeparator(AppState.getString(424));
        s.configureSoftKeys("Дальше", 4, "Назад", 12, 0);
        return s;
    }

    /** ADD_CONTACT_FORM — Добавить контакт */
    public static Screen addContactForm() {
        Screen s = new Screen(0, 21);
        s.configureHeader(383, "Добавить контакт");
        s.addTextInput("jid:", 63, "UCB_BASIC_LATIN", 0, 1296);
        s.addDropdown("Город:", 684, 1487);
        s.addTextSeparator(AppState.getString(424));
        s.configureSoftKeys("Добавить", 4, "Назад", 12, 0);
        return s;
    }

    /** SETTINGS_MENU */
    public static Screen settingsMenu() {
        Screen s = new Screen(10, 8);
        s.configureHeader(28, "");
        s.showCheckboxes = true;
        s.addAction(28, "За сессию", 0);
        s.addAction(28, "Сегодня", 1);
        s.addAction(28, "За месяц", 2); // Эта учетная запись подключена или находится в с...
        s.addAction(28, "Всего", 3); // Эта учетная запись не подключена к серверу.
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** TRAFFIC_STATS */
    public static Screen trafficStats() {
        Screen s = new Screen(0, 34);
        s.configureSoftKeys("Сброс", 34, "Назад", 12, 0);
        return s;
    }

    /** SETTINGS_MAIN */
    public static Screen settingsMain() {
        Screen s = new Screen(5, 2);
        s.configureHeader(156, "Mail.Ru");
        s.addLabelSeparator("Добро пожаловать в Мобильный Агент!\nСейчас будет произведена настройка учетных записей."); // Добро пожаловать в Мобильный Агент! Сейчас буде...
        s.configureSoftKeys("Продолжить", 157, null, 157, 157);
        return s;
    }

    /** DELETE_CONTACT_LIST — Игнорировать */
    public static Screen deleteContactList() {
        Screen s = new Screen(0, 127);
        s.configureHeader(20, "Игнорировать");
        s.configureSoftKeys("Удалить", 4, "Назад", 12, 0);
        return s;
    }

    /** UNBLOCK_CONTACT_LIST — Я всегда невидим для... */
    public static Screen unblockContactList() {
        Screen s = new Screen(0, 126);
        s.configureHeader(19, "Я всегда невидим для...");
        s.configureSoftKeys("Удалить", 4, "Назад", 12, 0);
        return s;
    }

    /** BLOCK_CONTACT_LIST — Я всегда видим для... */
    public static Screen blockContactList() {
        Screen s = new Screen(0, 125);
        s.configureHeader(17, "Я всегда видим для...");
        s.configureSoftKeys("Удалить", 4, "Назад", 12, 0);
        return s;
    }

    /** PHONE_INPUT_ALT — Шаблоны сообщений */
    public static Screen phoneInputAlt() {
        Screen s = new Screen(0, 98);
        s.configureHeader(32, "Шаблоны сообщений");
        s.configureSoftKeys("Изменить", 12, "Назад", 12, 12);
        return s;
    }

    /** URL_OPEN — Шаблоны сообщений */
    public static Screen urlOpen() {
        Screen s = new Screen(0, 99);
        s.configureHeader(32, "Шаблоны сообщений");
        s.configureSoftKeys("Выбрать", 65, "Назад", 12, 65);
        return s;
    }

    /** ACCOUNT_SWITCH_OPTIONS */
    public static Screen accountSwitchOptions() {
        Screen s = new Screen(10, 64);
        s.configureHeader(9, "");
        s.showCheckboxes = true;
        s.addConditionalUnless(1496, "Я всегда видим для...", 0, 0); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalUnless(1496, "Я всегда невидим для...", 0, 1); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addConditionalUnless(1496, "Переместить в группу", 35, 86); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addAction(34, "Очистить архив сообщений", 128); // Кофе
        s.addConditionalUnless(1496, "Игнорировать", 20, 11); // ContactKeys.FLAG_CONTACT_IS_GROUP
        s.addAction(19, "Удалить", 71); // Указать что я здесь
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CREATE_CHAT_ROOM — Начать конференцию */
    public static Screen createChatRoom() {
        Screen s = new Screen(0, 143);
        s.configureHeader(232, "Начать конференцию");
        s.addTextInput("Тема:", 255, AppState.getString(424), 0, 1292);
        s.addCheckbox("Приглашаю только я", 2722); // Приглашаю только я
        s.addTextSeparator("Пригласить:"); // Пригласить:
        s.configureSoftKeys("Сохранить", 4, "Назад", 12, 0);
        return s;
    }

    /** GROUP_MEMBERS — Список участников */
    public static Screen groupMembers() {
        Screen s = new Screen(0, 142);
        s.configureHeader(6, "Список участников");
        s.configureSoftKeys("Добавить", 199, "Назад", 12, 145);
        return s;
    }

    /** EDIT_MEMBERS — Добавить в конференцию */
    public static Screen editMembers() {
        Screen s = new Screen(0, 144);
        s.configureHeader(7, "Добавить в конференцию");
        s.configureSoftKeys("Добавить", 40, "Назад", 12, 0);
        return s;
    }

    /** CHAT_OPTIONS */
    public static Screen chatOptions() {
        Screen s = new Screen(10, 166);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addConditionalIf(1497, "SMS ", 27, 65); // ContactKeys.FLAG_CONTACT_IS_USER
        s.addConditionalIf(1497, "письмо", 238, 54); // ContactKeys.FLAG_CONTACT_IS_USER
        s.addConditionalIf(1500, "файл", 221, 135); // Учусь
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** COLOR_PICKER */
    public static Screen colorPicker() {
        Screen s = new Screen(10, 104);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(156, "Онлайн", 0);
        s.addAction(159, "Готов поболтать", 1);
        s.addAction(157, "Отсутствую", 2); // Эта учетная запись подключена или находится в с...
        s.addAction(160, "Не беспокоить", 3); // Эта учетная запись не подключена к серверу.
        s.addAction(158, "Невидим", 4); // Эта операция доступна только при соединении с с...
        s.addAction(155, "Отключен", 5); // Эта операция доступна только при отсутствии сое...
        s.configureSoftKeys("Выбрать", 4, "Назад", 12, 4);
        return s;
    }

    /** GROUP_MOVE */
    public static Screen groupMove() {
        Screen s = new Screen(10, 86);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CONTACT_INFO_EDITOR — Список видимости */
    public static Screen contactInfoEditor() {
        Screen s = new Screen(0, 156);
        s.configureHeader(310, "Список видимости");
        s.configureSoftKeys("Сохранить", 151, "Назад", 151, 151);
        return s;
    }

    /** PROFILE_EDIT */
    public static Screen profileEdit() {
        Screen s = new Screen(8, 160);
        s.configureHeader(-1, "");
        s.addLabelSeparator(AppState.getString(1337));
        s.configureSoftKeys("OK", 171, "Изменить", 151, 171);
        return s;
    }

    /** CHAT_DETAIL */
    public static Screen chatDetail() {
        Screen s = new Screen(10, 162);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(308, "Указать на карте", 6); // Имя не может быть пустым.
        s.addAction(303, "Поиск по названию", 100); // Сообщать о дорожной обстановке от имени:
        s.addAction(360, AppState.getString(1251), 120); // Водные процедуры
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** MAIL_ACCOUNT_LIST */
    public static Screen mailAccountList() {
        Screen s = new Screen(2, 169);
        s.configureHeader(303, "");
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CHAT_ROOM_CONFIG — Описание статуса */
    public static Screen chatRoomConfig() {
        Screen s = new Screen(0, 49);
        s.configureHeader(0, "Описание статуса");
        s.addTextInput(null, 63, AppState.getString(424), 0, 0);
        s.configureSoftKeys("Сохранить", 0, "Назад", 12, 0);
        return s;
    }

    /** FILE_SELECTOR — Выберите файл */
    public static Screen fileSelector() {
        Screen s = new Screen(0, 135);
        s.configureHeader(221, "Выберите файл");
        s.addLogin("*", 1284); // *
        s.configureSoftKeys("Отправить", 141, "Назад", 12, 0);
        return s;
    }

    /** PHOTO_SELECTOR_ALT — Фото */
    public static Screen photoSelectorAlt() {
        Screen s = new Screen(5, 133);
        s.configureHeader(220, "Фото");
        s.addLabelSeparator(AppState.getString(1344));
        s.configureSoftKeys(null, 0, "Отмена", 12, 0);
        return s;
    }

    /** MAIN_SCREEN — Блокировка */
    public static Screen mainScreen() {
        Screen s = new Screen(5, 137);
        s.configureHeader(5, "Блокировка");
        s.addLabelSeparator("Клавиатура заблокирована."); // Клавиатура заблокирована.
        s.configureSoftKeys(null, 0, "Разблок.", 0, 0);
        return s;
    }

    /** ACCOUNT_SETUP — Настройка учетной записи */
    public static Screen accountSetup() {
        Screen s = new Screen(0, 157);
        s.configureHeader(383, "Настройка учетной записи");
        s.addAction(-1, "Учетная запись Jabber", 76); // XMPP_LOGIN
        s.configureSoftKeys("Выбрать", 199, "Отмена", 4, 199);
        return s;
    }

    /** REGISTRATION_FORM — Учетная запись Mail.Ru */
    public static Screen registrationForm() {
        Screen s = new Screen(0, 164);
        s.configureHeader(156, "Учетная запись Mail.Ru");
        s.addLogin(AppState.getString(12), 0);
        s.addDropdown("Домен:", 694, 1474);
        s.addTextInput("Повторите пароль:", 63, "UCB_BASIC_LATIN", 327680, 1284);
        s.addDropdown("Секретный вопрос:", 810, 4305); // Секретный вопрос:
        s.addTextInput("Свой вопрос:", 255, AppState.getString(424), 0, 1287);
        s.addTextInput("Ответ:", 255, AppState.getString(424), 0, 1288);
        s.addDropdown("Месяц рождения:", 686, 1488);
        s.addNumericInput("Год:", 4, "UCB_BASIC_LATIN", 1491, 1, 2100, -1);
        s.addDropdown("Домен:", 694, 1480);
        s.addImage(1341);
        s.addNumericInput("Число на картинке:", 6, "UCB_BASIC_LATIN", 1480, -1, 999999, -1);
        s.configureSoftKeys("Сохранить", 12, "Отмена", 12, 0);
        return s;
    }

    /** REGISTRATION */
    public static Screen registration() {
        Screen s = new Screen(10, 16);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(-1, "Добавить имеющуюся", 76); // Название:
        s.addAction(-1, "Создать новую", 165); // Сошел с ума
        s.configureSoftKeys("Выбрать", 199, "Отмена", 12, 199);
        return s;
    }

    /** ERROR_ALERT */
    public static Screen errorAlert() {
        Screen s = new Screen(8, 0);
        s.configureHeader(0, "");
        s.addLabelSeparator(AppState.getString(1344));
        s.configureSoftKeys("Да", 0, "Нет", 12, 0);
        return s;
    }

    /** CONFIRM_DIALOG */
    public static Screen confirmDialog() {
        Screen s = new Screen(7, 0);
        s.configureSoftKeys(null, 0, "Отмена", 12, 0);
        return s;
    }

    /** GENERIC_LIST */
    public static Screen genericList() {
        Screen s = new Screen(1, 36);
        s.configureHeader(-1, "");
        s.configureSoftKeys("Меню", 88, "Выбрать", 37, 37);
        return s;
    }

    /** INPUT_FORM — Папки */
    public static Screen inputForm() {
        Screen s = new Screen(0, 38);
        s.configureHeader(35, "Папки");
        s.configureSoftKeys("Действия", 80, "Назад", 12, 41);
        return s;
    }

    /** CONTACT_DETAILS */
    public static Screen contactDetails() {
        Screen s = new Screen(0, 43);
        s.configureHeader(0, "");
        s.configureSoftKeys("Действия", 51, "Назад", 12, 48);
        return s;
    }

    /** MESSAGE_PREVIEW */
    public static Screen messagePreview() {
        Screen s = new Screen(5, 52);
        s.configureHeader(240, AppState.getString(1284));
        s.addTextSeparator(AppState.getString(1285));
        s.addLabelSeparator(AppState.getString(1286));
        s.configureSoftKeys("Действия", 53, "Назад", 43, 0);
        return s;
    }

    /** COMPOSE_RECIPIENTS */
    public static Screen composeRecipients() {
        Screen s = new Screen(3, 53);
        s.configureHeader(0, "");
        s.addAction(213, "Ответить", 54); // Ok, договорились.
        s.addAction(214, "Ответить всем", 54); // Ok, договорились.
        s.addAction(216, "Переслать", 54); // Ok, договорились.
        s.addAction(243, "Кодировка", 58); // Спасибо!
        s.addAction(239, "Перенести...", 90); // никогда, 5 мин, 15 мин, 30 мин
        s.addAction(218, "Пожаловаться на спам", 60); // Буду ждать тебя 
        s.addAction(217, "Удалить в корзину", 61); // До свидания!
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** CHAT_ROOM_CONTEXT */
    public static Screen chatRoomContext() {
        Screen s = new Screen(3, 51);
        s.configureHeader(0, "");
        s.addConditionalIf(1517, "Обновить", 243, 37); // ChatKeys.SCREEN_FLAGS_START
        s.addAction(238, "Новое письмо", 54); // Ok, договорились.
        s.addConditionalIf(1518, "Снять отметку", 24, 43); // ChatKeys.FLAG_MSG_READ_SELECTED
        s.addConditionalIf(1519, "Пометить", 25, 43); // ChatKeys.FLAG_MSG_UNREAD_SELECTED
        s.addConditionalIf(1520, AppState.getString(1347), 25, 67); // ChatKeys.FLAG_CHATROOM_HAS_MEMBERS
        s.addConditionalIf(1521, "Письмо", 240, 62); // ChatKeys.FLAG_IS_CHATROOM
        s.addConditionalIf(1517, "Поиск", 227, 68); // ChatKeys.SCREEN_FLAGS_START
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** SOFTKEY_MENU */
    public static Screen softkeyMenu() {
        Screen s = new Screen(10, 67);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(237, "Пометить как прочитанные", 72); // Написать сообщение
        s.addAction(225, "Пометить как непрочитанные", 72); // Написать сообщение
        s.addAction(24, "Снять отметки", 43); // Дорожные обьекты
        s.addAction(239, "Перенести...", 90); // никогда, 5 мин, 15 мин, 30 мин
        s.addAction(218, "Пожаловаться на спам", 60); // Буду ждать тебя 
        s.addAction(217, "Удалить в корзину", 61); // До свидания!
        s.configureSoftKeys("Выбрать", 199, "Отмена", 12, 199);
        return s;
    }

    /** MAIL_MENU */
    public static Screen mailMenu() {
        Screen s = new Screen(10, 62);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(213, "Ответить", 48); // Привет!
        s.addAction(214, "Ответить всем", 48); // Привет!
        s.addAction(216, "Переслать", 48); // Привет!
        s.addConditionalIf(1522, "Пометить как прочитанное", 237, 72); // ChatKeys.FLAG_MSG_UNREAD
        s.addConditionalIf(1523, "Пометить как непрочитанное", 225, 72); // ChatKeys.SCREEN_FLAGS_END
        s.addAction(239, "Перенести...", 90); // никогда, 5 мин, 15 мин, 30 мин
        s.addAction(218, "Пожаловаться на спам", 60); // Буду ждать тебя 
        s.addAction(217, "Удалить в корзину", 61); // До свидания!
        s.configureSoftKeys("Выбрать", 199, "Отмена", 12, 199);
        return s;
    }

    /** INPUT_DIALOG */
    public static Screen inputDialog() {
        Screen s = new Screen(10, 60);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(218, "Пожаловаться и удалить", 78); // Сохранить закладку
        s.addAction(218, "Пожаловаться и оставить", 78); // Сохранить закладку
        s.configureSoftKeys("Выбрать", 199, "Отмена", 12, 199);
        return s;
    }

    /** GROUP_SELECTOR */
    public static Screen groupSelector() {
        Screen s = new Screen(10, 58);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(243, "koi -> win", 0);
        s.addAction(243, "win -> koi", 1);
        s.configureSoftKeys("Выбрать", 199, "Отмена", 12, 199);
        return s;
    }

    /** NOTIFICATION_OPTIONS */
    public static Screen notificationOptions() {
        Screen s = new Screen(3, 80);
        s.configureHeader(0, "");
        s.addAction(243, "Обновить", 37); // В вашем телефоне недостаточно оперативной памят...
        s.addAction(238, "Новое письмо", 54); // Ok, договорились.
        s.addAction(227, "Поиск", 68); // Спрятать
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** SEARCH_RESULTS — Поиск */
    public static Screen searchResults() {
        Screen s = new Screen(0, 68);
        s.configureHeader(227, "Поиск");
        s.addTextInput("От:", 255, AppState.getString(424), 0, 1348);
        s.addTextInput("Кому:", 255, AppState.getString(424), 0, 1349);
        s.addTextInput("Тема:", 255, AppState.getString(424), 0, 1350);
        s.addTextInput("Сообщение:", 255, AppState.getString(424), 0, 1351);
        s.addCheckbox("Искать во всех папках", 1526); // Искать во всех папках
        s.configureSoftKeys("Поиск", 81, "Назад", 12, 0);
        return s;
    }

    /** COMPOSE_MESSAGE — Написать письмо */
    public static Screen composeMessage() {
        Screen s = new Screen(0, 54);
        s.configureHeader(238, "Написать письмо");
        s.addTextInput("Кому:", 255, "UCB_BASIC_LATIN", 0, 1352);
        s.addTextInput("Тема:", 255, AppState.getString(424), 0, 1353);
        s.addTextSeparator("Сообщение:"); // Сообщение:
        s.addTextInput("", 10000, AppState.getString(424), 0, 1354);
        s.configureSoftKeys("Отправить", 82, "Назад", 79, 0);
        return s;
    }

    /** THEME_OPTIONS */
    public static Screen themeOptions() {
        Screen s = new Screen(3, 88);
        s.configureHeader(156, "");
        s.addAction(8, "Настройки", 56); // Я в пробке. Буду через   мин.
        s.addAction(10, "Выход", 10);
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** DIALOG_SCREEN */
    public static Screen dialogScreen() {
        Screen s = new Screen(10, 90);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.configureSoftKeys("Выбрать", 42, "Назад", 12, 42);
        return s;
    }

    /** VERSION_SELECT */
    public static Screen versionSelect() {
        Screen s = new Screen(10, 109);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(370, "Видимый для всех", 1);
        s.addAction(372, "Видимый для списка видящих", 3); // Эта учетная запись не подключена к серверу.
        s.addAction(373, "Видимый для всех кроме списка невидящих", 4); // Эта операция доступна только при соединении с с...
        s.addAction(374, "Видимый для списка контактов", 5); // Эта операция доступна только при отсутствии сое...
        s.addAction(371, "Невидимый для всех", 2); // Эта учетная запись подключена или находится в с...
        s.configureSoftKeys("Выбрать", 3, "Назад", 12, 3);
        return s;
    }

    /** VCARD_ACTIONS */
    public static Screen vcardActions() {
        Screen s = new Screen(3, 130);
        s.configureHeader(0, "");
        s.addAction(-1, "Большое", 0);
        s.addAction(-1, "Среднее", 1);
        s.addAction(-1, "Маленькое", 2); // Эта учетная запись подключена или находится в с...
        s.configureSoftKeys("Выбрать", 199, "Назад", 12, 199);
        return s;
    }

    /** FORM_SETTINGS — Подсветка */
    public static Screen formSettings() {
        Screen s = new Screen(0, 140);
        s.configureHeader(236, "Подсветка");
        s.addCheckbox("Управлять подсветкой", 268); // Управлять подсветкой
        s.addCheckbox("Выключать при блокировке", 269); // Выключать при блокировке
        s.addCheckbox("Включать при событиях", 270); // Включать при событиях
        s.addDropdown("Выключать:", 1015, 271); // Выключать:
        s.addCheckbox("Не выключать при навигации", 272); // Не выключать при навигации
        s.configureSoftKeys("Сохранить", 12, "Назад", 12, 0);
        return s;
    }

    /** INVITE_TOS_SCREEN — Внимание! */
    public static Screen inviteTosScreen() {
        Screen s = new Screen(0, 138);
        s.configureHeader(156, "Внимание!");
        s.addLabelSeparator("Вы используете несертифицированную версию Мобильного Агента, часть функций будет недоступна. Чтобы начать использовать все функции программы, установите ее с сайта m.mail.ru"); // Вы используете несертифицированную версию Мобил...
        s.addCheckbox("Больше не показывать", 273); // Больше не показывать
        s.configureSoftKeys("Продолжить", 12, "Установить", 12, 12);
        return s;
    }

    /** ASYNC_CONFIRM_SCREEN */
    public static Screen asyncConfirmScreen() {
        Screen s = new Screen(2, 149);
        s.configureHeader(0, "");
        s.configureSoftKeys("Выбрать", 12, "Назад", 12, 12);
        return s;
    }

    /** WIFI_NETWORKS — Рекомендовать */
    public static Screen wifiNetworks() {
        Screen s = new Screen(0, 168);
        s.configureHeader(376, "Рекомендовать");
        s.addTextInput("Телефон:", 31, AppState.getString(1262), 3, 1303);
        s.configureSoftKeys("Отправить", 199, "Назад", 12, 199);
        return s;
    }

    /** CONNECTION_SETTINGS */
    public static Screen connectionSettings() {
        Screen s = new Screen(10, 35);
        s.configureHeader(0, "");
        s.showCheckboxes = true;
        s.addAction(314, "Запросить", 0);
        s.addAction(314, "Разрешить", 1);
        s.addAction(314, "Удалить", 2); // Эта учетная запись подключена или находится в с...
        s.configureSoftKeys("Выбрать", 4, "Назад", 12, 4);
        return s;
    }

}
