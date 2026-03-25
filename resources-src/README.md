[English](#english) | [Русский](#русский)

---

## English

# Resource Sources

This directory contains human-readable resource sources for MobileAgent 3.9.
During build (`make jar` or `make resources`), these files are converted into
the obfuscated format expected by the application and placed into `build/resources/`.

### Files

| File | Description |
|------|-------------|
| `config.json` | Main configuration: object pool (including packed strings) + int pool |
| `cities.xml` | City database in UTF-8 with human-readable tags |
| `xmpp_data.bin` | XMPP protocol data (binary, stored as-is) |
| `images/` | PNG images with descriptive names |
| `images/mapping.json` | Maps obfuscated filenames to descriptive names |
| `META-INF/MANIFEST.MF` | JAR manifest |

### Build Pipeline

```
resources-src/                      build/resources/
  config.json        --cfg_tool.py---->  cfg          (binary config)
  cities.xml         --pack_cities.sh->  b            (CP1251 + obfuscated tags)
  images/*.png       --pack_resources.sh-> *.png      (renamed per mapping.json)
  xmpp_data.bin      --pack_resources.sh-> a          (copied as-is)
  META-INF/MANIFEST.MF                   (copied to JAR)
```

### Tools

| Tool | Purpose |
|------|---------|
| `tools/cfg_tool.py --dump <cfg> <dir>` | Binary cfg → config.json |
| `tools/cfg_tool.py --pack <dir> <cfg>` | config.json → binary cfg |
| `tools/cfg_tool.py --verify <cfg> <dir>` | Round-trip verification (dump → pack → compare) |
| `tools/cfg_tool.py --gen-java <dir> <java>` | Generate `PackedStringKeys.java` from config.json |
| `tools/pack_cities.sh <out_dir>` | Convert cities.xml (UTF-8 → CP1251, rename tags) |
| `tools/pack_resources.sh <out_dir>` | Copy and rename images per mapping.json |

---

### File Schemas

#### config.json

```jsonc
{
  "format": "mobileagent-cfg-v1",    // DO NOT change
  "objectPool": [                      // ordered array; indices must be contiguous
    // Integer entry:
    { "index": 0, "type": "int", "value": 42 },

    // String entry (decoded from CP1251):
    { "index": 43, "type": "string", "value": "Настройка учетной записи" },

    // Binary entry (non-text byte array, base64-encoded):
    { "index": 296, "type": "bytes", "value": "AEBBQlBY..." },

    // Null entry:
    { "index": 222, "type": "null" },

    // Packed strings (null-separated segments + named sub-string references):
    {
      "index": 295,
      "type": "packed_strings",
      "entries": [
        {"value": "statisticsq=data/adddata/get_phone_info"},
        {"bytes": "AQIDBAUGBwgJ..."},
        {"value": "http://mobile.mail.ru/"}
      ],
      "names": [
        {"name": "URL_STATS", "offset": 0, "length": 39},
        {"name": "TAG_A", "offset": 2, "length": 1}
      ]
    }
  ],
  "intPool": [0, 0, 15, ...]          // flat array of integers
}
```

| Field | Editable? | Notes |
|-------|-----------|-------|
| `format` | No | Must be `"mobileagent-cfg-v1"` |
| `objectPool[].index` | No | Sequential index, must match position in array |
| `objectPool[].type` | No | Determined by dump heuristic; changing type breaks pack |
| `objectPool[].value` | **Yes** | Edit freely for `string` and `int` entries |
| `intPool[]` | **Yes** | Integer values, edit freely |

**Object types explained:**

| Type | Stored as | Description |
|------|-----------|-------------|
| `int` | JSON number | Integer value |
| `string` | JSON string | CP1251 string, shown as Unicode. Encoded back to CP1251 on pack |
| `bytes` | base64 string | Raw binary (MIDI, hashes, null-delimited arrays). Not human-readable |
| `null` | (no value) | Null entry. In binary format, encoded as the CP1251 string `"null"` |
| `packed_strings` | entries + names | Packed strings blob stored as readable segments (see below) |

**Packed strings entry format:**

The `packed_strings` object contains:
- `entries[]` — null-separated segments of the blob. Each is either:
  - `{"value": "text"}` — CP1251-encodable text string
  - `{"bytes": "base64..."}` — raw binary data
  - `{"value": ""}` — empty segment (represents consecutive null bytes)
- `names[]` — named sub-string references for `PackedStringKeys.java`. Each has:
  - `name` — Java constant name (`UPPER_SNAKE_CASE`)
  - `offset` — byte offset within the reconstructed blob
  - `length` — byte length of the sub-string

Entries are joined with null bytes on pack to reconstruct the original blob.
Names reference arbitrary sub-strings within the blob (not necessarily aligned
to entry boundaries) and are used solely for `--gen-java` code generation.

| Field | Editable? | Notes |
|-------|-----------|-------|
| `entries[].value` | **Yes** | Edit text content freely |
| `entries[].bytes` | Caution | Base64-encoded binary; edit only if you know what you're doing |
| `names[].name` | **Yes** | Add/edit to define a Java constant in `PackedStringKeys.java` |
| `names[].offset` | Caution | Byte offset in blob; must match actual content position |
| `names[].length` | Caution | Byte length; must match actual content |

#### images/mapping.json

```jsonc
{
  "a.png": "sprite_messaging_contacts.png",   // key = runtime name, value = source name
  "b.png": "sprite_settings_profile.png",
  "icon.png": "icon.png",                     // may be identity mapping
  "splash.png": "splash.png"
}
```

| Field | Editable? | Notes |
|-------|-----------|-------|
| Keys (e.g. `"a.png"`) | **Yes** | Obfuscated filename used at runtime. Must match what Java code expects in `Image.createImage()` calls |
| Values (e.g. `"sprite_messaging_contacts.png"`) | **Yes** | Human-readable source filename. The PNG with this name must exist in `images/` |

To add a new image: place the PNG in `images/`, add a mapping entry, and
reference the obfuscated name in Java.

#### cities.xml

```xml
<countries>
    <country i="24" n="Россия">
        <region i="25" n="Москва">
            <city i="1734">Зеленоград</city>
        </region>
    </country>
</countries>
```

| Field | Editable? | Notes |
|-------|-----------|-------|
| Tag names (`country`, `region`, `city`) | No | Renamed to single-letter tags during pack (`c`, `r`, `i`) |
| Attribute `i` (ID) | **Yes** | Numeric city/region/country ID |
| Attribute `n` (name) | **Yes** | Region/country display name |
| City text content | **Yes** | City display name |

The file is UTF-8. During pack, `pack_cities.sh` converts it to CP1251 and
renames tags to single-letter equivalents.

---

### How to Add and Use Resources

#### Editing string values in config.json

Entries of type `string` can be edited directly — write the value as
plain Unicode text. `cfg_tool.py` encodes it to CP1251 on `--pack`.

In Java, entries are accessed by pool index via `AppState`:

```java
// Defined in core/StateKeys.java:
public static final int STR_MY_LABEL = 500;

// Read:
String label = AppState.getString(StateKeys.STR_MY_LABEL);

// Write at runtime:
AppState.setString(StateKeys.STR_MY_LABEL, "new value");
```

Main `AppState` accessors:

| Method | Description |
|--------|-------------|
| `getString(int)` / `setString(int, String)` | String values |
| `getInt(int)` / `setInt(int, int)` | Integer values |
| `getBool(int)` / `setBool(int, boolean)` | Boolean (stored as int 0/1) |
| `getLong(int)` / `setLong(int, long)` | Long (stored as two consecutive ints) |
| `getBytes(int)` | Raw byte array |
| `getImage(int)` | Image object |
| `clearIndex(int)` | Reset to null |

Each pool index should have a named constant in `core/StateKeys.java`.

#### Adding images

1. Place the PNG in `resources-src/images/` with a descriptive name.
2. Add a mapping entry in `images/mapping.json`:
   ```json
   "ad.png": "my_new_icon.png"
   ```
3. In Java, load by the obfuscated name:
   ```java
   Image icon = Image.createImage("/ad.png");
   ```

#### Naming packed string constants

Packed strings are short string literals (tag names, URL fragments, status codes)
stored compactly in a single binary blob. Each is referenced by an integer ID
encoding its offset and length: `id = (length << 16) | offset`.

To give a sub-string a Java constant name, add an entry to the `names` array
in the `packed_strings` object in `config.json`, then regenerate:
```bash
tools/cfg_tool.py --gen-java resources-src/ sources/.../core/PackedStringKeys.java
```

Constants appear in `core/PackedStringKeys.java` and are used with
`StringUtils.matchesKey()` for comparisons and `ByteBuffer.writeCompressed()`
for protocol encoding:

```java
if (StringUtils.matchesKey(PackedStringKeys.TAG_STATUS, tagName)) { ... }

ByteBuffer buf = new ByteBuffer()
    .writeCompressed(PackedStringKeys.URL_PROFILE_PHOTO)
    .writeRawString(userId);
```

#### Using ScreenId

Screen identifiers are defined in `core/ScreenId.java` and used in handler
dispatch (switch/case) and navigation:

```java
case ScreenId.MY_NEW_SCREEN:
    // build screen content
    return;

// Navigation:
return ScreenId.MY_NEW_SCREEN;
```

---

## Русский

# Исходники ресурсов

Этот каталог содержит ресурсы MobileAgent 3.9 в человекочитаемом формате.
При сборке (`make jar` или `make resources`) файлы конвертируются в обфусцированный
формат, ожидаемый приложением, и помещаются в `build/resources/`.

### Файлы

| Файл | Описание |
|------|----------|
| `config.json` | Основная конфигурация: пул объектов (включая упакованные строки) + пул int |
| `cities.xml` | База городов в UTF-8 с читаемыми тегами |
| `xmpp_data.bin` | Данные протокола XMPP (бинарные, хранятся как есть) |
| `images/` | PNG-изображения с описательными именами |
| `images/mapping.json` | Маппинг обфусцированных имён в описательные |
| `META-INF/MANIFEST.MF` | Манифест JAR |

### Конвейер сборки

```
resources-src/                      build/resources/
  config.json        --cfg_tool.py---->  cfg          (бинарный конфиг)
  cities.xml         --pack_cities.sh->  b            (CP1251 + обфусцированные теги)
  images/*.png       --pack_resources.sh-> *.png      (переименованы по mapping.json)
  xmpp_data.bin      --pack_resources.sh-> a          (копируется как есть)
  META-INF/MANIFEST.MF                   (копируется в JAR)
```

### Инструменты

| Инструмент | Назначение |
|------------|------------|
| `tools/cfg_tool.py --dump <cfg> <dir>` | Бинарный cfg → config.json |
| `tools/cfg_tool.py --pack <dir> <cfg>` | config.json → бинарный cfg |
| `tools/cfg_tool.py --verify <cfg> <dir>` | Round-trip проверка (dump → pack → сравнение) |
| `tools/cfg_tool.py --gen-java <dir> <java>` | Генерация `PackedStringKeys.java` из config.json |
| `tools/pack_cities.sh <out_dir>` | Конвертация cities.xml (UTF-8 → CP1251, переименование тегов) |
| `tools/pack_resources.sh <out_dir>` | Копирование и переименование изображений по mapping.json |

---

### Схемы файлов

#### config.json

```jsonc
{
  "format": "mobileagent-cfg-v1",    // НЕ менять
  "objectPool": [                      // упорядоченный массив; индексы непрерывные
    // Целое число:
    { "index": 0, "type": "int", "value": 42 },

    // Строка (декодированная из CP1251):
    { "index": 43, "type": "string", "value": "Настройка учетной записи" },

    // Бинарные данные (не текст, base64):
    { "index": 296, "type": "bytes", "value": "AEBBQlBY..." },

    // Null-запись:
    { "index": 222, "type": "null" },

    // Упакованные строки (null-разделённые сегменты + именованные ссылки):
    {
      "index": 295,
      "type": "packed_strings",
      "entries": [
        {"value": "statisticsq=data/adddata/get_phone_info"},
        {"bytes": "AQIDBAUGBwgJ..."},
        {"value": "http://mobile.mail.ru/"}
      ],
      "names": [
        {"name": "URL_STATS", "offset": 0, "length": 39},
        {"name": "TAG_A", "offset": 2, "length": 1}
      ]
    }
  ],
  "intPool": [0, 0, 15, ...]          // плоский массив целых чисел
}
```

| Поле | Можно менять? | Примечания |
|------|---------------|------------|
| `format` | Нет | Должно быть `"mobileagent-cfg-v1"` |
| `objectPool[].index` | Нет | Последовательный индекс, должен совпадать с позицией в массиве |
| `objectPool[].type` | Нет | Определяется эвристикой при дампе; смена типа ломает упаковку |
| `objectPool[].value` | **Да** | Свободно редактируйте для `string` и `int` |
| `intPool[]` | **Да** | Целые числа, редактируйте свободно |

**Типы объектов:**

| Тип | Хранение | Описание |
|-----|----------|----------|
| `int` | JSON-число | Целое число |
| `string` | JSON-строка | CP1251-строка, показана как Unicode. При упаковке кодируется обратно в CP1251 |
| `bytes` | base64-строка | Бинарные данные (MIDI, хеши, массивы с null-разделителями). Не человекочитаемые |
| `null` | (нет значения) | Null-запись. В бинарном формате кодируется CP1251-строкой `"null"` |
| `packed_strings` | entries + names | Упакованные строки в читаемом виде (подробнее ниже) |

**Формат packed_strings:**

Объект `packed_strings` содержит:
- `entries[]` — null-разделённые сегменты blob'а. Каждый — один из:
  - `{"value": "текст"}` — текстовая CP1251-строка
  - `{"bytes": "base64..."}` — бинарные данные
  - `{"value": ""}` — пустой сегмент (двойной null-байт)
- `names[]` — именованные ссылки на подстроки для `PackedStringKeys.java`. Каждая содержит:
  - `name` — имя Java-константы (`UPPER_SNAKE_CASE`)
  - `offset` — смещение в байтах внутри собранного blob'а
  - `length` — длина подстроки в байтах

При упаковке entries соединяются null-байтами для восстановления blob'а.
Names ссылаются на произвольные подстроки внутри blob'а (не обязательно
совпадающие с границами entries) и используются только для `--gen-java`.

| Поле | Можно менять? | Примечания |
|------|---------------|------------|
| `entries[].value` | **Да** | Текстовое содержимое, редактируйте свободно |
| `entries[].bytes` | Осторожно | Base64-кодированные бинарные данные; редактируйте только если знаете что делаете |
| `names[].name` | **Да** | Добавьте/измените для Java-константы в `PackedStringKeys.java` |
| `names[].offset` | Осторожно | Смещение в blob'е; должно соответствовать позиции в содержимом |
| `names[].length` | Осторожно | Длина; должна соответствовать содержимому |

#### images/mapping.json

```jsonc
{
  "a.png": "sprite_messaging_contacts.png",   // ключ = имя в рантайме, значение = имя исходника
  "b.png": "sprite_settings_profile.png",
  "icon.png": "icon.png",                     // может быть тождественным
  "splash.png": "splash.png"
}
```

| Поле | Можно менять? | Примечания |
|------|---------------|------------|
| Ключи (напр. `"a.png"`) | **Да** | Обфусцированное имя файла в рантайме. Должно совпадать с тем, что ожидает Java-код в вызовах `Image.createImage()` |
| Значения (напр. `"sprite_messaging_contacts.png"`) | **Да** | Человекочитаемое имя исходника. PNG с таким именем должен существовать в `images/` |

Чтобы добавить изображение: положите PNG в `images/`, добавьте маппинг,
ссылайтесь на обфусцированное имя в Java.

#### cities.xml

```xml
<countries>
    <country i="24" n="Россия">
        <region i="25" n="Москва">
            <city i="1734">Зеленоград</city>
        </region>
    </country>
</countries>
```

| Поле | Можно менять? | Примечания |
|------|---------------|------------|
| Имена тегов (`country`, `region`, `city`) | Нет | При упаковке переименовываются в однобуквенные (`c`, `r`, `i`) |
| Атрибут `i` (ID) | **Да** | Числовой ID города/региона/страны |
| Атрибут `n` (название) | **Да** | Отображаемое название региона/страны |
| Текст внутри `<city>` | **Да** | Отображаемое название города |

Файл в UTF-8. При упаковке `pack_cities.sh` конвертирует его в CP1251 и
переименовывает теги в однобуквенные.

---

### Как добавлять и использовать ресурсы

#### Редактирование строковых значений в config.json

Записи типа `string` можно редактировать прямо в файле — пишите
значение обычным Unicode-текстом. `cfg_tool.py` закодирует его в CP1251
при `--pack`.

В Java записи доступны по индексу пула через `AppState`:

```java
// Определено в core/StateKeys.java:
public static final int STR_MY_LABEL = 500;

// Чтение:
String label = AppState.getString(StateKeys.STR_MY_LABEL);

// Запись в рантайме:
AppState.setString(StateKeys.STR_MY_LABEL, "новое значение");
```

Основные методы `AppState`:

| Метод | Описание |
|-------|----------|
| `getString(int)` / `setString(int, String)` | Строковые значения |
| `getInt(int)` / `setInt(int, int)` | Целые числа |
| `getBool(int)` / `setBool(int, boolean)` | Булевы (хранятся как int 0/1) |
| `getLong(int)` / `setLong(int, long)` | Long (хранится как два последовательных int) |
| `getBytes(int)` | Байтовый массив |
| `getImage(int)` | Объект Image |
| `clearIndex(int)` | Сброс в null |

Для каждого индекса пула должна быть именованная константа в `core/StateKeys.java`.

#### Добавление изображений

1. Поместите PNG в `resources-src/images/` с описательным именем.
2. Добавьте маппинг в `images/mapping.json`:
   ```json
   "ad.png": "my_new_icon.png"
   ```
3. В Java загружайте по обфусцированному имени:
   ```java
   Image icon = Image.createImage("/ad.png");
   ```

#### Именование констант упакованных строк

Упакованные строки — короткие строковые литералы (имена тегов, фрагменты URL,
коды статусов), компактно хранящиеся в одном бинарном blob'е. Каждая адресуется
целочисленным ID, кодирующим смещение и длину: `id = (length << 16) | offset`.

Чтобы дать подстроке имя Java-константы, добавьте запись в массив `names`
объекта `packed_strings` в `config.json`, затем перегенерируйте:
```bash
tools/cfg_tool.py --gen-java resources-src/ sources/.../core/PackedStringKeys.java
```

Константы появляются в `core/PackedStringKeys.java` и используются с
`StringUtils.matchesKey()` для сравнений и `ByteBuffer.writeCompressed()`
для протокольного кодирования:

```java
if (StringUtils.matchesKey(PackedStringKeys.TAG_STATUS, tagName)) { ... }

ByteBuffer buf = new ByteBuffer()
    .writeCompressed(PackedStringKeys.URL_PROFILE_PHOTO)
    .writeRawString(userId);
```

#### Использование ScreenId

Идентификаторы экранов определены в `core/ScreenId.java` и используются
в диспетчеризации обработчиков (switch/case) и навигации:

```java
case ScreenId.MY_NEW_SCREEN:
    // построение содержимого экрана
    return;

// Навигация:
return ScreenId.MY_NEW_SCREEN;
```
