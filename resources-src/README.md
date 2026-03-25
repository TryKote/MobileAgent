[English](#english) | [Русский](#русский)

---

## English

# Resource Sources

This directory contains human-readable resource sources for MobileAgent 3.9.
During build (`make jar` or `make resources`), these files are converted into
the binary format expected by the application and placed into `build/resources/`.

## Files

| File | Description |
|------|-------------|
| `config.json` | Main configuration: object pool + screen definitions |
| `cities.xml` | City database in UTF-8 with human-readable tags |
| `blowfish_constants.bin` | Blowfish S-boxes and P-array (standard pi-derived constants, 1042 ints) |
| `images/` | PNG images with descriptive names |
| `images/mapping.json` | Maps runtime filenames to descriptive source names |
| `META-INF/MANIFEST.MF` | JAR manifest |

## Build Pipeline

```
resources-src/                      build/resources/
  config.json        --cfg_tool.py---->  cfg          (binary config)
  cities.xml         --pack_cities.sh->  b            (CP1251 + obfuscated tags)
  images/*.png       --pack_resources.sh-> *.png      (renamed per mapping.json)
  blowfish_constants.bin --pack_resources.sh-> a       (copied as-is)
  META-INF/MANIFEST.MF                   (copied to JAR)
```

## Tools

| Tool | Purpose |
|------|---------|
| `tools/cfg_tool.py --deserialize <cfg> <dir>` | Deserialize binary cfg to config.json |
| `tools/cfg_tool.py --serialize <dir> <cfg>` | Serialize config.json to binary cfg |
| `tools/cfg_tool.py --round-trip <cfg> <dir>` | Round-trip verify |
| `tools/cfg_tool.py --gen-java <dir> <java>` | Generate `PackedStringKeys.java` |
| `tools/cfg_tool.py --gen-screens <dir> <java>` | Generate `ScreenDef.java` |
| `tools/pack_cities.sh <out_dir>` | Convert cities.xml (UTF-8 -> CP1251) |
| `tools/pack_resources.sh <out_dir>` | Copy and rename images per mapping.json |

---

# config.json

The main configuration file. Format version: `mobileagent-cfg-v2`.

It has two top-level sections:

```jsonc
{
  "format": "mobileagent-cfg-v2",
  "objectPool": [ ... ],   // data storage (strings, ints, binaries)
  "screens": [ ... ]        // UI screen definitions
}
```

## objectPool

An ordered array of data entries. Each entry has an `index` (must match its
position in the array) and a `type`. The application accesses entries by index
at runtime through `AppState.getString(index)`, `AppState.getInt(index)`, etc.

### Entry types

| Type | JSON format | Description |
|------|-------------|-------------|
| `int` | `{"index": 0, "type": "int", "value": 42}` | Integer value |
| `string` | `{"index": 43, "type": "string", "value": "Hello"}` | Text string (stored as CP1251 in binary) |
| `bytes` | `{"index": 296, "type": "bytes", "value": "base64..."}` | Raw binary data |
| `string_list` | `{"index": 694, "type": "string_list", "value": ["a", "b"]}` | Null-separated CP1251 string list (dropdown choices, etc.) |
| `null` | `{"index": 222, "type": "null"}` | Empty slot |
| `packed_strings` | *(see below)* | Compact string blob with named references |

**What can you edit?**
- `value` of `string` and `int` entries: freely
- `value` of `bytes` entries: only if you know what's inside
- `index` and `type`: never change these

### packed_strings

A single blob containing many short strings (URL fragments, tag names, status
codes) packed together. Instead of storing each as a separate pool entry,
they're concatenated with null-byte separators into one binary blob.

```jsonc
{
  "index": 295,
  "type": "packed_strings",
  "entries": [
    {"value": "statisticsq=data/add"},    // text segment
    {"bytes": "AQIDBAUGBwgJ..."},          // binary segment
    {"value": "http://mobile.mail.ru/"}    // text segment
  ],
  "names": [
    {"name": "URL_STATS", "offset": 0, "length": 20},
    {"name": "TAG_A", "offset": 2, "length": 1}
  ]
}
```

- `entries[]` are null-separated segments. On pack, they're joined with `\0` bytes.
- `names[]` define Java constants for `PackedStringKeys.java`. Each name points
  to a substring within the blob by `offset` and `length` (in bytes).
  The runtime ID is `(length << 16) | offset`.

---

## screens

This is the most important section for UI work. It defines **136 screen
definitions** — the structure of every screen, dialog, menu, and popup in the
application. The runtime code reads these definitions and builds the UI from them.

### How it works (the big picture)

1. Each screen definition is a block of integers in the binary `cfg` file.
2. `config.json` represents these blocks as structured JSON objects.
3. `cfg_tool.py --serialize` converts JSON back to binary integers.
4. At runtime, `ScreenManager.createScreen(offset)` reads integers starting
   at `offset` and builds a `Screen` object with menu items.
5. Screen handlers (in `ui/handler/`) call `createScreen(ScreenDef.SOME_SCREEN)`
   and then show the result.

```
config.json "screens" array
        |
        | cfg_tool.py --serialize
        v
    cfg binary (intPool)
        |
        | ScreenManager.createScreen(offset)
        v
    Screen object (runtime UI)
```

### Screen definition structure

Each screen is a JSON object:

```jsonc
{
  "name": "GPS_SETTINGS",          // unique name (becomes a ScreenDef constant)
  "title": 1038,                   // objectPool index for title string
  "title_": "Карта",               // (comment) human-readable title text
  "screenId": 20,                  // ScreenId constant for handler dispatch
  "screenId_": "GPS_SETTINGS",     // (comment) ScreenId constant name
  "type": "dialog_bottom",         // screen type (see table below)
  "checkboxes": true,              // (optional) show radio-button markers
  "headerMode": 0,                 // header icon code (see below)
  "leftSoftKey": {                 // left soft key (phone button)
    "label": 1048,                 //   objectPool index for label text
    "cmd": 199,                    //   command ID sent on press
    "label_": "Выбрать"            //   (comment) label text
  },
  "rightSoftKey": {                // right soft key
    "label": 1050,
    "cmd": 12,
    "label_": "Назад"
  },
  "extraCmd": 199,                 // command ID for "select" action (Enter key)
  "items": [ ... ],                // list of UI elements (see item types below)
  "trailingData": [23, 1462, ...]  // (rare) raw ints shared with other screens
}
```

**Fields ending with `_` (like `title_`, `label_`, `screenId_`) are comments.**
They are ignored during packing and exist only for human readability.
`cfg_tool.py --deserialize` generates them automatically from objectPool values.

### Screen types

The `type` field determines how the screen is displayed:

| Type | Description |
|------|-------------|
| `fullscreen` | Full-screen with header and scrolling |
| `fullscreen_alt` | Full-screen, alternate style |
| `fullscreen_noscroll` | Full-screen without scrolling |
| `fullscreen_noscroll_alt` | Full-screen without scrolling, alternate |
| `dialog_center` | Dialog box centered on screen |
| `dialog_bottom` | Dialog anchored to bottom |
| `dialog_corner` | Dialog anchored to corner |
| `dialog_low` | Dialog in lower area |
| `popup` | Popup menu overlay |
| `toast` | Brief notification toast |
| `toast_center` | Centered toast notification |
| `map` | Map view |
| `map_alt` | Map view, alternate |

### headerMode (header icon)

The `headerMode` field is the **icon code** for the small 16x16 icon displayed
in the screen header, next to the title text. The app stores all icons as
sprites in a single sprite sheet; the icon code selects which sprite to draw.

| Value | Meaning |
|-------|---------|
| `0` | Icon at sprite code 0 (default/generic icon) |
| `1`..`354`+ | Specific icon from the sprite sheet |
| `4294967295` | No icon (this is -1 as unsigned 32-bit; no header icon is drawn) |

The header (with icon and title) is only rendered for **fullscreen** and **map**
screen types. For dialog, popup, and toast types the header is not shown at all,
so the `headerMode` value is irrelevant (but still stored in the definition).

### Soft keys

Mobile phones had two physical buttons below the screen — the left and right
soft keys. Each soft key has:

- `label` — objectPool index pointing to the button text (e.g., "Menu", "Back")
- `cmd` — a command ID. When the user presses the button, this command is sent
  to the application's event handler.
- `label_` — (comment) the actual text, for readability

If `label` is `0`, the soft key is hidden.

`extraCmd` is the command triggered by pressing the center/Enter key.

### Item types

The `items` array defines the content of the screen. Each item has a `type`
field and type-specific properties. Here are all 13 item types:

#### `action` — Menu item / button

The most common type. A tappable menu entry with an icon and a label.

```jsonc
{"type": "action", "label": 100, "icon": 303, "cmd": 339}
// label: objectPool index for text
// icon:  objectPool index for icon image
// cmd:   command ID sent when selected
```

Optional: `"style": "text"` — renders as a text-style action (no icon background).

**Dynamic variant** — label is determined at runtime by a condition key:

```jsonc
{"type": "action", "extra": 1473, "condKey": 21, "icon": 7, "cmd": 552}
// extra:   runtime data reference
// condKey: AppState key that determines the label
```

#### `separator` — Info row with two columns

Displays a label on the left and a value on the right. Not interactive.

```jsonc
{"type": "separator", "label": 512, "sublabel": 1288, "label_": "Version:"}
// label:    objectPool index for left text
// sublabel: objectPool index for right text (or packed string ID)
```

#### `checkbox` — Toggle switch

A boolean setting that the user can flip on/off.

```jsonc
{"type": "checkbox", "label": 391, "stateKey": 255, "label_": "Correct GPS coords"}
// label:    objectPool index for description text
// stateKey: AppState key where the boolean value is stored (0/1)
```

#### `dropdown` — Selection list

A setting with multiple predefined options.

```jsonc
{"type": "dropdown", "label": 382, "choices": 385, "indexKey": 45,
 "label_": "Turn off GPS after:"}
// label:    objectPool index for description text
// choices:  objectPool index for the list of option strings
// indexKey: AppState key where the selected index is stored
```

#### `text_separator` — Section header

A non-interactive text label that separates groups of items.

```jsonc
{"type": "text_separator", "label": 393, "label_": "GPS Device:"}
```

#### `label_separator` — Static text block

Similar to `text_separator`, but rendered as a block of text (like a paragraph).

```jsonc
{"type": "label_separator", "label": 332,
 "label_": "Not enough RAM for map display..."}
```

#### `text_input` — Text field

An editable text field. Has two variants depending on `validation`:

**Standard (validation != 2):**
```jsonc
{"type": "text_input", "dataKey": 350, "inputType": 255,
 "hint": 424, "validation": 0, "valueKey": 1248}
// dataKey:    objectPool index for field label
// inputType:  keyboard constraint flags
// hint:       objectPool index for placeholder text
// validation: validation mode (0 = none, 1 = required)
// valueKey:   AppState key where text value is stored
```

**Numeric with range (validation == 2):**
```jsonc
{"type": "text_input", "dataKey": 814, "inputType": 6,
 "hint": 425, "validation": 2,
 "min": 0, "max": 9999, "default": 100, "stateKey": 1350}
// min/max:    allowed numeric range
// default:   default value
// stateKey:  AppState key where the number is stored
```

#### `login` — Login field

A special text field for usernames/logins.

```jsonc
{"type": "login", "label": 390, "value": 233}
// label: objectPool index for field label
// value: objectPool index for current value
```

#### `password` — Password field

A masked text field for passwords.

```jsonc
{"type": "password", "value": 239}
// value: objectPool index for current value
```

#### `image` — Image display

Displays an image from the object pool.

```jsonc
{"type": "image", "poolIndex": 1341}
// poolIndex: objectPool index containing the image data
```

#### `redirect` — Include items from another screen

Inserts items from another screen definition at this point. Used to share
common item groups between screens without duplicating them.

```jsonc
{"type": "redirect", "targetOffset": 2787}
// targetOffset: intPool offset of the screen whose items to include
```

#### `conditional_if` — Conditional action (show if true)

Like `action`, but only visible when an AppState flag is set to a truthy value.

```jsonc
{"type": "conditional_if", "condKey": 276, "label": 1, "icon": 365, "cmd": 348}
// condKey: AppState key to check; item is shown only if value != 0
```

#### `conditional_unless` — Conditional action (show if false)

The opposite of `conditional_if` — shown only when the flag is zero/false.

```jsonc
{"type": "conditional_unless", "condKey": 1462, "label": 147, "icon": 2, "cmd": 528}
```

Both conditional types also support `"style": "text"`.

### Integer references

Most numeric fields in screen definitions are **not literal values** — they are
references to the objectPool. For example:

- `"title": 1038` means "get the title string from `objectPool[1038]`"
- `"label": 391` means "get the label text from `objectPool[391]`"
- `"icon": 303` means "get the icon image from `objectPool[303]`"

The `cmd` fields are literal command IDs (not pool references).
`stateKey`, `condKey`, `indexKey`, `valueKey`, `dataKey` are AppState key indices.

### trailingData

Some screens have a `trailingData` array — raw integers that come after the
screen's items in the binary. These are typically shared data fragments
referenced by `redirect` items from other screens. Don't edit these unless
you understand the cross-references.

---

## How to create a new screen

### Step 1: Define the screen in config.json

Add a new object to the `screens` array. Place it in alphabetical order
or near related screens.

```jsonc
{
  "name": "MY_NEW_SCREEN",
  "title": 500,
  "screenId": 170,
  "type": "dialog_center",
  "checkboxes": true,
  "headerMode": 0,
  "leftSoftKey": {"label": 1048, "cmd": 199},
  "rightSoftKey": {"label": 1050, "cmd": 12},
  "extraCmd": 199,
  "items": [
    {"type": "action", "label": 501, "icon": 303, "cmd": 600},
    {"type": "action", "label": 502, "icon": 304, "cmd": 601}
  ]
}
```

- `name` must be unique and in `UPPER_SNAKE_CASE`.
- `screenId` should be a ScreenId constant (or a new one).
- `title`, `label`, `icon` values must be valid objectPool indices.
- `cmd` values are command IDs you'll handle in Java.

### Step 2: Register the screen offset

Add your screen to the `KNOWN_SCREENS` list in `tools/cfg_tool.py` so the
tool knows how to parse it. Then regenerate constants:

```bash
# Rebuild binary and regenerate ScreenDef.java
make resources
make screen-defs
```

This creates a `ScreenDef.MY_NEW_SCREEN` constant with the correct offset.

### Step 3: Add a ScreenId (if needed)

If your screen needs a new screen ID, add a constant to
`core/ScreenId.java`:

```java
public static final int MY_NEW_SCREEN = 170;
```

### Step 4: Show the screen from a handler

In the appropriate screen handler (e.g., `ui/handler/SettingsHandler.java`),
add a case to build and show your screen:

```java
case ScreenId.MY_NEW_SCREEN:
    ScreenManager.showScreen(
        ScreenManager.createScreen(ScreenDef.MY_NEW_SCREEN));
    return;
```

### Step 5: Handle commands

In `AppController` (or the relevant handler), add cases for your command IDs
(600, 601 in the example) to define what happens when the user selects items.

### Step 6: Build and test

```bash
make resources    # pack config.json -> binary cfg
make compile      # compile Java
make jar          # build JAR
```

---

## Other resources

### images/mapping.json

Maps runtime filenames to human-readable source filenames:

```jsonc
{
  "a.png": "sprite_messaging_contacts.png",
  "icon.png": "icon.png"
}
```

To add an image: place PNG in `images/`, add a mapping entry, reference the
runtime name in Java via `Image.createImage("/a.png")`.

### cities.xml

City database in UTF-8. Converted to CP1251 with obfuscated tags on pack.

```xml
<countries>
    <country i="24" n="Russia">
        <region i="25" n="Moscow">
            <city i="1734">Zelenograd</city>
        </region>
    </country>
</countries>
```

---

## Русский

# Исходники ресурсов

Этот каталог содержит ресурсы MobileAgent 3.9 в человекочитаемом формате.
При сборке (`make jar` или `make resources`) файлы конвертируются в бинарный
формат и помещаются в `build/resources/`.

## Файлы

| Файл | Описание |
|------|----------|
| `config.json` | Основная конфигурация: пул объектов + определения экранов |
| `cities.xml` | База городов в UTF-8 с читаемыми тегами |
| `blowfish_constants.bin` | S-box'ы и P-array Blowfish (стандартные константы из π, 1042 int) |
| `images/` | PNG-изображения с описательными именами |
| `images/mapping.json` | Маппинг рантайм-имён в описательные |
| `META-INF/MANIFEST.MF` | Манифест JAR |

## Конвейер сборки

```
resources-src/                      build/resources/
  config.json        --cfg_tool.py---->  cfg          (бинарный конфиг)
  cities.xml         --pack_cities.sh->  b            (CP1251 + обфусцированные теги)
  images/*.png       --pack_resources.sh-> *.png      (переименованы по mapping.json)
  blowfish_constants.bin --pack_resources.sh-> a       (копируется как есть)
  META-INF/MANIFEST.MF                   (копируется в JAR)
```

## Инструменты

| Инструмент | Назначение |
|------------|------------|
| `tools/cfg_tool.py --deserialize <cfg> <dir>` | Десериализация бинарного cfg в config.json |
| `tools/cfg_tool.py --serialize <dir> <cfg>` | Сериализация config.json в бинарный cfg |
| `tools/cfg_tool.py --round-trip <cfg> <dir>` | Round-trip верификация |
| `tools/cfg_tool.py --gen-java <dir> <java>` | Генерация `PackedStringKeys.java` |
| `tools/cfg_tool.py --gen-screens <dir> <java>` | Генерация `ScreenDef.java` |
| `tools/pack_cities.sh <out_dir>` | Конвертация cities.xml (UTF-8 -> CP1251) |
| `tools/pack_resources.sh <out_dir>` | Копирование и переименование изображений |

---

# config.json

Основной конфигурационный файл. Версия формата: `mobileagent-cfg-v2`.

Два раздела верхнего уровня:

```jsonc
{
  "format": "mobileagent-cfg-v2",
  "objectPool": [ ... ],   // хранилище данных (строки, числа, бинарные)
  "screens": [ ... ]        // определения экранов UI
}
```

## objectPool

Упорядоченный массив записей данных. У каждой записи есть `index` (должен
совпадать с позицией в массиве) и `type`. Приложение обращается к записям
по индексу через `AppState.getString(index)`, `AppState.getInt(index)` и т.д.

### Типы записей

| Тип | Формат JSON | Описание |
|-----|-------------|----------|
| `int` | `{"index": 0, "type": "int", "value": 42}` | Целое число |
| `string` | `{"index": 43, "type": "string", "value": "Привет"}` | Текстовая строка (в бинарном файле хранится как CP1251) |
| `bytes` | `{"index": 296, "type": "bytes", "value": "base64..."}` | Бинарные данные |
| `string_list` | `{"index": 694, "type": "string_list", "value": ["a", "b"]}` | Список строк CP1251 через null-байт (варианты dropdown и т.п.) |
| `null` | `{"index": 222, "type": "null"}` | Пустой слот |
| `packed_strings` | *(см. ниже)* | Компактный блоб строк с именованными ссылками |

**Что можно редактировать?**
- `value` у записей `string` и `int`: свободно
- `value` у записей `bytes`: только если знаете, что внутри
- `index` и `type`: никогда не меняйте

### packed_strings

Единый блоб, содержащий множество коротких строк (фрагменты URL, имена тегов,
коды статусов), упакованных вместе. Вместо хранения каждой строки отдельной
записью пула, они склеены null-байтами в один бинарный блоб.

```jsonc
{
  "index": 295,
  "type": "packed_strings",
  "entries": [
    {"value": "statisticsq=data/add"},    // текстовый сегмент
    {"bytes": "AQIDBAUGBwgJ..."},          // бинарный сегмент
    {"value": "http://mobile.mail.ru/"}    // текстовый сегмент
  ],
  "names": [
    {"name": "URL_STATS", "offset": 0, "length": 20},
    {"name": "TAG_A", "offset": 2, "length": 1}
  ]
}
```

- `entries[]` — сегменты, разделённые null-байтами. При упаковке склеиваются через `\0`.
- `names[]` — именованные ссылки на подстроки для `PackedStringKeys.java`.
  Каждое имя указывает на подстроку в блобе по `offset` и `length` (в байтах).
  Рантайм-ID: `(length << 16) | offset`.

---

## screens

Это самый важный раздел для работы с UI. Он содержит **136 определений
экранов** — структуру каждого экрана, диалога, меню и всплывающего окна
в приложении. Рантайм-код читает эти определения и строит из них UI.

### Как это работает (общая картина)

1. Каждое определение экрана — это блок целых чисел в бинарном файле `cfg`.
2. `config.json` представляет эти блоки как структурированные JSON-объекты.
3. `cfg_tool.py --serialize` конвертирует JSON обратно в бинарные числа.
4. В рантайме `ScreenManager.createScreen(offset)` читает числа начиная
   с `offset` и строит объект `Screen` с элементами меню.
5. Обработчики экранов (в `ui/handler/`) вызывают
   `createScreen(ScreenDef.SOME_SCREEN)` и показывают результат.

```
config.json массив "screens"
        |
        | cfg_tool.py --serialize
        v
    cfg бинарный (intPool)
        |
        | ScreenManager.createScreen(offset)
        v
    Объект Screen (рантайм UI)
```

### Структура определения экрана

Каждый экран — это JSON-объект:

```jsonc
{
  "name": "GPS_SETTINGS",          // уникальное имя (становится константой ScreenDef)
  "title": 1038,                   // индекс objectPool для строки заголовка
  "title_": "Карта",               // (комментарий) текст заголовка
  "screenId": 20,                  // константа ScreenId для диспетчеризации обработчика
  "screenId_": "GPS_SETTINGS",     // (комментарий) имя константы ScreenId
  "type": "dialog_bottom",         // тип экрана (см. таблицу ниже)
  "checkboxes": true,              // (опционально) показывать радио-кнопки
  "headerMode": 0,                 // код иконки заголовка (см. ниже)
  "leftSoftKey": {                 // левая софт-клавиша (кнопка телефона)
    "label": 1048,                 //   индекс objectPool для текста кнопки
    "cmd": 199,                    //   ID команды при нажатии
    "label_": "Выбрать"            //   (комментарий) текст кнопки
  },
  "rightSoftKey": {                // правая софт-клавиша
    "label": 1050,
    "cmd": 12,
    "label_": "Назад"
  },
  "extraCmd": 199,                 // команда для действия "выбор" (Enter)
  "items": [ ... ],                // список элементов UI (см. типы ниже)
  "trailingData": [23, 1462, ...]  // (редко) сырые числа, общие с другими экранами
}
```

**Поля, заканчивающиеся на `_` (как `title_`, `label_`, `screenId_`) — это
комментарии.** Они игнорируются при упаковке и существуют только для
читабельности. `cfg_tool.py --deserialize` генерирует их автоматически из значений
objectPool.

### Типы экранов

Поле `type` определяет, как экран отображается:

| Тип | Описание |
|-----|----------|
| `fullscreen` | Полноэкранный с заголовком и прокруткой |
| `fullscreen_alt` | Полноэкранный, альтернативный стиль |
| `fullscreen_noscroll` | Полноэкранный без прокрутки |
| `fullscreen_noscroll_alt` | Полноэкранный без прокрутки, альтернативный |
| `dialog_center` | Диалог по центру экрана |
| `dialog_bottom` | Диалог внизу экрана |
| `dialog_corner` | Диалог в углу |
| `dialog_low` | Диалог в нижней части |
| `popup` | Всплывающее меню |
| `toast` | Кратковременное уведомление |
| `toast_center` | Уведомление по центру |
| `map` | Карта |
| `map_alt` | Карта, альтернативный |

### headerMode (иконка заголовка)

Поле `headerMode` — это **код иконки** для маленькой 16x16 иконки, которая
рисуется в заголовке экрана рядом с текстом. Все иконки приложения хранятся
как спрайты в одном спрайт-листе; код иконки определяет, какой спрайт
отрисовать.

| Значение | Смысл |
|----------|-------|
| `0` | Иконка с кодом 0 (стандартная/общая) |
| `1`..`354`+ | Конкретная иконка из спрайт-листа |
| `4294967295` | Без иконки (это -1 как беззнаковое 32-бит; иконка не рисуется) |

Заголовок (с иконкой и текстом) отображается только для **полноэкранных** и
**карточных** типов экрана. Для диалогов, попапов и тостов заголовок не
показывается вообще, поэтому значение `headerMode` не влияет ни на что
(но всё равно хранится в определении).

### Софт-клавиши

У мобильных телефонов было две физические кнопки под экраном — левая и
правая софт-клавиши. У каждой:

- `label` — индекс objectPool, указывающий на текст кнопки ("Меню", "Назад")
- `cmd` — ID команды. При нажатии кнопки эта команда отправляется
  обработчику событий приложения.
- `label_` — (комментарий) сам текст, для читабельности

Если `label` равен `0`, софт-клавиша скрыта.

`extraCmd` — команда, срабатывающая при нажатии центральной кнопки / Enter.

### Типы элементов

Массив `items` определяет содержимое экрана. У каждого элемента есть поле
`type` и свойства, зависящие от типа. Вот все 13 типов:

#### `action` — Пункт меню / кнопка

Самый частый тип. Нажимаемый пункт меню с иконкой и текстом.

```jsonc
{"type": "action", "label": 100, "icon": 303, "cmd": 339}
// label: индекс objectPool для текста
// icon:  индекс objectPool для иконки
// cmd:   ID команды при выборе
```

Опционально: `"style": "text"` — текстовый стиль (без фона иконки).

**Динамический вариант** — текст определяется в рантайме:

```jsonc
{"type": "action", "extra": 1473, "condKey": 21, "icon": 7, "cmd": 552}
// extra:   ссылка на рантайм-данные
// condKey: ключ AppState, определяющий текст
```

#### `separator` — Информационная строка с двумя колонками

Показывает метку слева и значение справа. Не интерактивный.

```jsonc
{"type": "separator", "label": 512, "sublabel": 1288, "label_": "Версия:"}
```

#### `checkbox` — Переключатель

Булева настройка, которую пользователь включает/выключает.

```jsonc
{"type": "checkbox", "label": 391, "stateKey": 255, "label_": "Корректировать GPS"}
// stateKey: ключ AppState, где хранится значение (0/1)
```

#### `dropdown` — Список выбора

Настройка с несколькими предопределёнными вариантами.

```jsonc
{"type": "dropdown", "label": 382, "choices": 385, "indexKey": 45,
 "label_": "Выключать GPS через:"}
// choices:  индекс objectPool со списком вариантов
// indexKey: ключ AppState, где хранится выбранный индекс
```

#### `text_separator` — Заголовок секции

Неинтерактивная текстовая метка, разделяющая группы элементов.

```jsonc
{"type": "text_separator", "label": 393, "label_": "Устройство GPS:"}
```

#### `label_separator` — Блок статического текста

Похож на `text_separator`, но отрисовывается как блок текста (абзац).

```jsonc
{"type": "label_separator", "label": 332,
 "label_": "Недостаточно памяти для карт..."}
```

#### `text_input` — Текстовое поле

Редактируемое текстовое поле. Два варианта в зависимости от `validation`:

**Стандартный (validation != 2):**
```jsonc
{"type": "text_input", "dataKey": 350, "inputType": 255,
 "hint": 424, "validation": 0, "valueKey": 1248}
// dataKey:    индекс objectPool для метки поля
// inputType:  флаги ограничений клавиатуры
// hint:       индекс objectPool для плейсхолдера
// validation: режим валидации (0 = нет, 1 = обязательное)
// valueKey:   ключ AppState, где хранится текст
```

**Числовой с диапазоном (validation == 2):**
```jsonc
{"type": "text_input", "dataKey": 814, "inputType": 6,
 "hint": 425, "validation": 2,
 "min": 0, "max": 9999, "default": 100, "stateKey": 1350}
// min/max:   допустимый числовой диапазон
// default:  значение по умолчанию
// stateKey: ключ AppState, где хранится число
```

#### `login` — Поле логина

Специальное текстовое поле для логинов.

```jsonc
{"type": "login", "label": 390, "value": 233}
```

#### `password` — Поле пароля

Замаскированное текстовое поле для паролей.

```jsonc
{"type": "password", "value": 239}
```

#### `image` — Отображение изображения

Показывает изображение из пула объектов.

```jsonc
{"type": "image", "poolIndex": 1341}
```

#### `redirect` — Вставка элементов из другого экрана

Вставляет элементы из другого определения экрана в текущее место. Используется
для переиспользования общих групп элементов без дублирования.

```jsonc
{"type": "redirect", "targetOffset": 2787}
// targetOffset: смещение intPool экрана, чьи элементы вставляются
```

#### `conditional_if` — Условное действие (показать если истина)

Как `action`, но видимо только когда флаг AppState установлен в ненулевое значение.

```jsonc
{"type": "conditional_if", "condKey": 276, "label": 1, "icon": 365, "cmd": 348}
// condKey: ключ AppState для проверки; элемент виден только если значение != 0
```

#### `conditional_unless` — Условное действие (показать если ложь)

Противоположность `conditional_if` — виден только когда флаг равен нулю.

```jsonc
{"type": "conditional_unless", "condKey": 1462, "label": 147, "icon": 2, "cmd": 528}
```

Оба условных типа также поддерживают `"style": "text"`.

### Целочисленные ссылки

Большинство числовых полей в определениях экранов — **не литеральные значения**,
а ссылки на objectPool. Например:

- `"title": 1038` означает "взять строку заголовка из `objectPool[1038]`"
- `"label": 391` означает "взять текст метки из `objectPool[391]`"
- `"icon": 303` означает "взять иконку из `objectPool[303]`"

Поля `cmd` — это литеральные ID команд (не ссылки на пул).
`stateKey`, `condKey`, `indexKey`, `valueKey`, `dataKey` — это индексы ключей AppState.

### trailingData

У некоторых экранов есть массив `trailingData` — сырые целые числа, идущие
после элементов экрана в бинарном файле. Обычно это общие фрагменты данных,
на которые ссылаются `redirect`-элементы из других экранов. Не редактируйте
их, если не понимаете перекрёстные ссылки.

---

## Как создать новый экран

### Шаг 1: Определите экран в config.json

Добавьте новый объект в массив `screens`:

```jsonc
{
  "name": "MY_NEW_SCREEN",
  "title": 500,
  "screenId": 170,
  "type": "dialog_center",
  "checkboxes": true,
  "headerMode": 0,
  "leftSoftKey": {"label": 1048, "cmd": 199},
  "rightSoftKey": {"label": 1050, "cmd": 12},
  "extraCmd": 199,
  "items": [
    {"type": "action", "label": 501, "icon": 303, "cmd": 600},
    {"type": "action", "label": 502, "icon": 304, "cmd": 601}
  ]
}
```

- `name` должно быть уникальным и в `UPPER_SNAKE_CASE`.
- `screenId` — константа ScreenId (или новая).
- `title`, `label`, `icon` — валидные индексы objectPool.
- `cmd` — ID команд, которые вы обработаете в Java.

### Шаг 2: Зарегистрируйте смещение экрана

Добавьте экран в список `KNOWN_SCREENS` в `tools/cfg_tool.py`, чтобы
инструмент мог его распарсить. Затем перегенерируйте константы:

```bash
# Пересобрать бинарный файл и перегенерировать ScreenDef.java
make resources
make screen-defs
```

Это создаст константу `ScreenDef.MY_NEW_SCREEN` с правильным смещением.

### Шаг 3: Добавьте ScreenId (если нужно)

Если экрану нужен новый ID, добавьте константу в `core/ScreenId.java`:

```java
public static final int MY_NEW_SCREEN = 170;
```

### Шаг 4: Покажите экран из обработчика

В подходящем обработчике (например, `ui/handler/SettingsHandler.java`)
добавьте кейс для построения и показа экрана:

```java
case ScreenId.MY_NEW_SCREEN:
    ScreenManager.showScreen(
        ScreenManager.createScreen(ScreenDef.MY_NEW_SCREEN));
    return;
```

### Шаг 5: Обработайте команды

В `AppController` (или соответствующем обработчике) добавьте кейсы для ваших
ID команд (600, 601 в примере), чтобы определить, что происходит при выборе
элементов.

### Шаг 6: Соберите и протестируйте

```bash
make resources    # упаковать config.json -> бинарный cfg
make compile      # скомпилировать Java
make jar          # собрать JAR
```

---

## Другие ресурсы

### images/mapping.json

Маппинг рантайм-имён файлов в человекочитаемые исходные имена:

```jsonc
{
  "a.png": "sprite_messaging_contacts.png",
  "icon.png": "icon.png"
}
```

Чтобы добавить изображение: положите PNG в `images/`, добавьте маппинг,
используйте рантайм-имя в Java через `Image.createImage("/a.png")`.

### cities.xml

База городов в UTF-8. При упаковке конвертируется в CP1251 с обфусцированными тегами.

```xml
<countries>
    <country i="24" n="Россия">
        <region i="25" n="Москва">
            <city i="1734">Зеленоград</city>
        </region>
    </country>
</countries>
```
