[English](#english) | [Русский](#русский)

---

## English

# Mail.ru Agent (MobileAgent 3.9) — Decompiled Source

Decompilation of the J2ME messenger Mail.ru Agent (`MobileAgent_3.9.jar`) using jadx.

### Structure

- `sources/p000/` — 56 Java files (package `p000`, names deobfuscated by jadx)
- `resources/` — resources from the original JAR (icons, configs, original .class files)
- `libs/` — J2ME libraries required for compilation

### Build

```bash
make          # compile + package into JAR
make compile  # compile only
make clean    # clean build artifacts
```

### Platform

- MIDP 2.0 / CLDC 1.1
- Nokia UI API, JSR-75 (FileConnection)

---

## Русский

# Mail.ru Agent (MobileAgent 3.9) — Декомпилированный исходный код

Декомпиляция J2ME мессенджера Mail.ru Agent (`MobileAgent_3.9.jar`) через jadx.

### Структура

- `sources/p000/` — 56 Java-файлов (пакет `p000`, имена деобфусцированы jadx)
- `resources/` — ресурсы из оригинального JAR (иконки, конфиги, оригинальные .class)
- `libs/` — библиотеки J2ME, необходимые для компиляции

### Сборка

```bash
make          # компиляция + упаковка в JAR
make compile  # только компиляция
make clean    # очистка
```

### Платформа

- MIDP 2.0 / CLDC 1.1
- Nokia UI API, JSR-75 (FileConnection)

