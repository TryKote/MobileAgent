[English](#english) | [Русский](#русский)

---

## English

# MIDlet Signing for Nokia S40

This guide covers the complete process of signing MobileAgent for Nokia S40 phones (tested on Nokia 6303i, S40 6th Edition).

Signed MIDlets run in the "trusted third party" security domain, which allows raw socket connections to ports ≤ 1024 (including HTTPS port 443).

## Prerequisites

- **OpenSSL** (any modern version)
- **Java JDK 8+** (keytool, java)
- **faketime** (`sudo pacman -S libfaketime` on Manjaro)
- **gammu** (`sudo pacman -S gammu` on Manjaro) — for Bluetooth filesystem access
- **NokiCert 0.3.2** — `certs/NokiCert/nokicert-0.3.2/nokicert-0.3.2-bin/`
- **JadSign tool** — compiled from `/tmp/JadSign.java` (see below)
- Nokia phone paired via Bluetooth

## Overview

Nokia S40 requires a **two-level certificate chain**:

```
TryKote CA (root, self-signed)        ← installed on phone via NokiCert
    └── MobileAgent Signer            ← signs the JAR, included in JAD
```

A single self-signed certificate does NOT work — the phone rejects it even if installed in the trust store.

## Step 1: Generate Certificate Chain (one-time)

All commands run from `certs/` directory.

### 1.1 CA Root Certificate

```bash
# Generate CA private key (1024-bit RSA — Nokia doesn't support larger)
openssl genrsa -out ca_key.pem 1024

# Create self-signed CA certificate (SHA-1 only — Nokia doesn't support SHA-256)
# Backdated to 2005 for compatibility with phones that have old system dates
faketime '2005-01-01' openssl req -new -x509 -sha1 \
  -key ca_key.pem -days 10950 \
  -subj "/CN=TryKote CA/O=TryKote/C=RU" \
  -extensions v3_ca \
  -config <(printf '[req]\ndistinguished_name=dn\nx509_extensions=v3_ca\n[dn]\n[v3_ca]\nbasicConstraints=critical,CA:true\nsubjectKeyIdentifier=hash\n') \
  -outform DER -out trykote_ca.cer
```

**Important constraints:**
- **RSA 1024-bit** — Nokia S40 may not support 2048-bit keys
- **SHA-1** — Nokia S40 does not support SHA-256 for MIDlet signature verification
- **CA:true** in X.509 v3 Basic Constraints — required for Nokia to accept it as a root CA
- **Backdated** — avoids "certificate date" errors if phone clock is reset

### 1.2 Signing Certificate

```bash
# Generate signing private key
openssl genrsa -out sign_key.pem 1024

# Create Certificate Signing Request
openssl req -new -sha1 -key sign_key.pem \
  -subj "/CN=MobileAgent Signer/O=TryKote/C=RU" \
  -out sign.csr

# Convert CA cert to PEM (needed for signing)
openssl x509 -inform DER -in trykote_ca.cer -outform PEM -out trykote_ca.pem

# Sign the signing certificate with our CA (backdated to 2009)
faketime '2009-06-01' openssl x509 -req -sha1 \
  -in sign.csr -CA trykote_ca.pem -CAkey ca_key.pem \
  -CAcreateserial -days 7300 \
  -outform DER -out sign_cert.cer
```

### 1.3 Create Java KeyStore for JAD signing

```bash
# Convert signing cert to PEM
openssl x509 -inform DER -in sign_cert.cer -outform PEM -out sign_cert.pem

# Create PKCS#12 keystore
openssl pkcs12 -export -in sign_cert.pem -inkey sign_key.pem \
  -name signer -passout pass:changeit -out signer.p12

# Convert to JKS
keytool -importkeystore \
  -srckeystore signer.p12 -srcstoretype pkcs12 -srcstorepass changeit -srcalias signer \
  -destkeystore signer.jks -deststoretype jks -deststorepass changeit \
  -destalias signer -destkeypass changeit
```

### 1.4 Verify certificates

```bash
echo "=== CA ===" && openssl x509 -inform DER -in trykote_ca.cer -noout -subject -dates
echo "=== Signer ===" && openssl x509 -inform DER -in sign_cert.cer -noout -subject -issuer -dates
```

Expected output:
```
=== CA ===
subject=CN=TryKote CA, O=TryKote, C=RU
notBefore=Dec 31 19:00:00 2004 GMT
notAfter=Dec 24 19:00:00 2034 GMT
=== Signer ===
subject=CN=MobileAgent Signer, O=TryKote, C=RU
issuer=CN=TryKote CA, O=TryKote, C=RU
notBefore=May 31 18:00:00 2009 GMT
notAfter=May 26 18:00:00 2029 GMT
```

## Step 2: Install CA Certificate on Phone (one-time per phone)

### 2.1 Configure gammu

Create `~/.gammurc`:
```ini
[gammu]
device = 20:D6:07:35:95:4B
connection = bluephonet
```

Replace the Bluetooth MAC with your phone's address (`bluetoothctl devices`).

### 2.2 Install via NokiCert

**Phone date must be within the CA certificate validity range (2005–2034) during installation.**

```bash
cd certs/NokiCert/nokicert-0.3.2/nokicert-0.3.2-bin
sh NokiCert.sh
```

In the GUI:
1. Enter phone Bluetooth address (without colons) and channel 15
2. Click **Select certificate** → choose `certs/trykote_ca.cer`
3. Check **Apps. signing**
4. Click **Install certificate**
5. Wait for "Status: certificate installed"

### 2.3 Verify installation

```bash
gammu getfolderlisting "d:/predefhiddenfolder/certificates/auth" | grep -i trykote
```

Should show a file like `TryKote CA`.

## Step 3: Build the JAR

```bash
# Clean build + optimized JAR (includes ProGuard shrinking + obfuscation)
rm -rf build
make compile
make optimized-jar
```

The optimized JAR:
- Renames BC's `java.*` classes (forbidden on J2ME) via ProGuard obfuscation
- Shrinks unused Bouncy Castle classes
- Preverifies for CLDC

Output: `build/optimized-TK_MobileAgent_3.9.jar`

## Step 4: Create and Sign the JAD

### 4.1 Compile JadSign (one-time)

```bash
cat > /tmp/JadSign.java << 'JAVA'
import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.Base64;

public class JadSign {
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.err.println("Usage: JadSign <keystore.jks> <password> <alias> <jarfile> <jadfile>");
            System.exit(1);
        }
        String ksFile = args[0], ksPass = args[1], alias = args[2], jarFile = args[3], jadFile = args[4];

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(ksFile), ksPass.toCharArray());

        PrivateKey key = (PrivateKey) ks.getKey(alias, ksPass.toCharArray());
        java.security.cert.Certificate cert = ks.getCertificate(alias);

        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(key);
        FileInputStream jarIn = new FileInputStream(jarFile);
        byte[] buf = new byte[8192];
        int n;
        while ((n = jarIn.read(buf)) > 0) sig.update(buf, 0, n);
        jarIn.close();
        String sigB64 = Base64.getEncoder().encodeToString(sig.sign());
        String certB64 = Base64.getEncoder().encodeToString(cert.getEncoded());

        StringBuilder jad = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(jadFile));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("MIDlet-Certificate-") && !line.startsWith("MIDlet-Jar-RSA-SHA1"))
                jad.append(line).append("\n");
        }
        reader.close();

        jad.append("MIDlet-Certificate-1-1: ").append(certB64).append("\n");
        jad.append("MIDlet-Jar-RSA-SHA1: ").append(sigB64).append("\n");

        FileWriter writer = new FileWriter(jadFile);
        writer.write(jad.toString());
        writer.close();

        System.out.println("Signed. Sig=" + sigB64.length() + " chars, Cert=" + certB64.length() + " chars");
    }
}
JAVA
javac /tmp/JadSign.java -d /tmp
```

### 4.2 Create unsigned JAD

The JAD **must match** the MANIFEST.MF inside the JAR. Critical fields that must be identical:
- `MIDlet-1` (name, icon, class — must be exact match)
- `MIDlet-Name`
- `MIDlet-Version`
- `MIDlet-Vendor`
- `MicroEdition-Configuration` (CLDC-1.0 if that's what MANIFEST.MF says)
- `MicroEdition-Profile`

Check MANIFEST.MF first:
```bash
unzip -p build/optimized-TK_MobileAgent_3.9.jar META-INF/MANIFEST.MF
```

Create JAD matching MANIFEST.MF:
```bash
JAR=build/optimized-TK_MobileAgent_3.9.jar
JAR_SIZE=$(stat -c%s "$JAR")

cat > build/MobileAgent.jad << JADEOF
MIDlet-1: TryKoteAgent,/icon.png,com.trykote.mobileagent.core.Midlet
MIDlet-Name: MobileAgent by TryKote
MIDlet-Version: 3.9.07
MIDlet-Vendor: TryKote
MIDlet-Description: MobileAgent by TryKote
MIDlet-Jar-URL: MobileAgent.jar
MIDlet-Jar-Size: ${JAR_SIZE}
MicroEdition-Configuration: CLDC-1.0
MicroEdition-Profile: MIDP-2.0
JADEOF
```

### 4.3 Sign the JAD

```bash
# Add signing certificate + signature
java -cp /tmp JadSign certs/signer.jks changeit signer "$JAR" build/MobileAgent.jad

# Add CA certificate to the chain
CA_B64=$(openssl x509 -inform DER -in certs/trykote_ca.cer -outform PEM | grep -v '^-----' | tr -d '\n')
echo "MIDlet-Certificate-1-2: ${CA_B64}" >> build/MobileAgent.jad
```

### 4.4 Prepare files for phone

```bash
cp build/optimized-TK_MobileAgent_3.9.jar build/MobileAgent.jar
```

Copy both `build/MobileAgent.jar` and `build/MobileAgent.jad` to the phone's SD card (same folder).

## Step 5: Install on Phone

1. **Set phone date** within signing cert validity (2009–2029)
2. Open file manager on phone
3. Navigate to `MobileAgent.jad` on the SD card
4. Phone reads JAD, downloads JAR (from same folder), verifies signature
5. Install

## Troubleshooting

### "Недопустимое приложение" (Invalid application)
- JAD attributes don't match MANIFEST.MF — check `MIDlet-1`, `MIDlet-Name`, `MIDlet-Version`, `MIDlet-Vendor`, `MicroEdition-Configuration` are identical
- `MIDlet-Jar-Size` doesn't match actual JAR file size

### "Сертификат отсутствует на телефоне или SIM карте" (Certificate not found)
- CA certificate not installed on phone — run NokiCert
- Installed without "Apps. signing" flag — reinstall with the checkbox

### "Срок действия сертификата истёк" (Certificate expired)
- Phone date is outside certificate validity range
- Set phone date between 2009 and 2029

### Certificate chain requirements
- **Two-level chain required** — self-signed certificates are rejected even if installed in the trust store
- Certificate-1-1 = signing cert (issued by CA)
- Certificate-1-2 = CA root cert (self-signed, installed on phone)

### Crypto constraints for Nokia S40
- **RSA 1024-bit only** — larger keys may not work
- **SHA-1 only** — SHA-256 is not supported for MIDlet verification
- **No line folding needed** in JAD — Nokia accepts long lines despite the MANIFEST spec

## File Reference

```
certs/
  ca_key.pem              # CA private key (KEEP SECRET)
  trykote_ca.cer          # CA certificate DER (install on phone)
  trykote_ca.pem          # CA certificate PEM
  sign_key.pem            # Signing private key (KEEP SECRET)
  sign_cert.cer           # Signing certificate DER
  sign_cert.pem           # Signing certificate PEM
  signer.jks              # Java KeyStore for JadSign
  signer.p12              # PKCS#12 keystore (intermediate)

build/
  optimized-TK_MobileAgent_3.9.jar  # ProGuard-processed JAR
  MobileAgent.jar                     # Copy of above (renamed for JAD)
  MobileAgent.jad                     # Signed JAD descriptor
```

---

## Русский

# Подпись MIDlet для Nokia S40

Полное руководство по подписи MobileAgent для телефонов Nokia S40 (протестировано на Nokia 6303i, S40 6th Edition).

Подписанные MIDlet-приложения работают в домене безопасности "trusted third party", что разрешает сокетные подключения к портам ≤ 1024 (включая HTTPS порт 443).

## Необходимое ПО

- **OpenSSL** (любая современная версия)
- **Java JDK 8+** (keytool, java)
- **faketime** (`sudo pacman -S libfaketime` на Manjaro)
- **gammu** (`sudo pacman -S gammu` на Manjaro) — для доступа к файловой системе по Bluetooth
- **NokiCert 0.3.2** — `certs/NokiCert/nokicert-0.3.2/nokicert-0.3.2-bin/`
- **JadSign** — компилируется из `/tmp/JadSign.java` (см. ниже)
- Телефон Nokia, сопряжённый по Bluetooth

## Обзор

Nokia S40 требует **двухуровневую цепочку сертификатов**:

```
TryKote CA (корневой, самоподписанный)   ← устанавливается на телефон через NokiCert
    └── MobileAgent Signer               ← подписывает JAR, включается в JAD
```

Одиночный самоподписанный сертификат НЕ работает — телефон отклоняет его, даже если он установлен в хранилище доверенных.

## Шаг 1: Генерация цепочки сертификатов (однократно)

Все команды выполняются из директории `certs/`.

### 1.1 Корневой CA-сертификат

```bash
# Генерация приватного ключа CA (RSA 1024 бит — Nokia не поддерживает больше)
openssl genrsa -out ca_key.pem 1024

# Создание самоподписанного CA-сертификата (только SHA-1 — Nokia не поддерживает SHA-256)
# Дата сдвинута в прошлое для совместимости с телефонами, у которых сброшены часы
faketime '2005-01-01' openssl req -new -x509 -sha1 \
  -key ca_key.pem -days 10950 \
  -subj "/CN=TryKote CA/O=TryKote/C=RU" \
  -extensions v3_ca \
  -config <(printf '[req]\ndistinguished_name=dn\nx509_extensions=v3_ca\n[dn]\n[v3_ca]\nbasicConstraints=critical,CA:true\nsubjectKeyIdentifier=hash\n') \
  -outform DER -out trykote_ca.cer
```

**Важные ограничения:**
- **RSA 1024 бит** — Nokia S40 может не поддерживать 2048-битные ключи
- **SHA-1** — Nokia S40 не поддерживает SHA-256 для верификации подписей MIDlet
- **CA:true** в X.509 v3 Basic Constraints — обязательно для принятия Nokia как корневого CA
- **Бэкдейт** — предотвращает ошибки дат при сбросе часов телефона

### 1.2 Сертификат подписи

```bash
# Генерация приватного ключа подписи
openssl genrsa -out sign_key.pem 1024

# Создание запроса на подпись сертификата (CSR)
openssl req -new -sha1 -key sign_key.pem \
  -subj "/CN=MobileAgent Signer/O=TryKote/C=RU" \
  -out sign.csr

# Конвертация CA-сертификата в PEM (необходимо для подписи)
openssl x509 -inform DER -in trykote_ca.cer -outform PEM -out trykote_ca.pem

# Подпись сертификата нашим CA (дата сдвинута в 2009)
faketime '2009-06-01' openssl x509 -req -sha1 \
  -in sign.csr -CA trykote_ca.pem -CAkey ca_key.pem \
  -CAcreateserial -days 7300 \
  -outform DER -out sign_cert.cer
```

### 1.3 Создание Java KeyStore для подписи JAD

```bash
# Конвертация сертификата подписи в PEM
openssl x509 -inform DER -in sign_cert.cer -outform PEM -out sign_cert.pem

# Создание PKCS#12 хранилища
openssl pkcs12 -export -in sign_cert.pem -inkey sign_key.pem \
  -name signer -passout pass:changeit -out signer.p12

# Конвертация в JKS
keytool -importkeystore \
  -srckeystore signer.p12 -srcstoretype pkcs12 -srcstorepass changeit -srcalias signer \
  -destkeystore signer.jks -deststoretype jks -deststorepass changeit \
  -destalias signer -destkeypass changeit
```

### 1.4 Проверка сертификатов

```bash
echo "=== CA ===" && openssl x509 -inform DER -in trykote_ca.cer -noout -subject -dates
echo "=== Signer ===" && openssl x509 -inform DER -in sign_cert.cer -noout -subject -issuer -dates
```

Ожидаемый вывод:
```
=== CA ===
subject=CN=TryKote CA, O=TryKote, C=RU
notBefore=Dec 31 19:00:00 2004 GMT
notAfter=Dec 24 19:00:00 2034 GMT
=== Signer ===
subject=CN=MobileAgent Signer, O=TryKote, C=RU
issuer=CN=TryKote CA, O=TryKote, C=RU
notBefore=May 31 18:00:00 2009 GMT
notAfter=May 26 18:00:00 2029 GMT
```

## Шаг 2: Установка CA-сертификата на телефон (однократно для каждого телефона)

### 2.1 Настройка gammu

Создайте `~/.gammurc`:
```ini
[gammu]
device = 20:D6:07:35:95:4B
connection = bluephonet
```

Замените Bluetooth MAC на адрес вашего телефона (`bluetoothctl devices`).

### 2.2 Установка через NokiCert

**Дата на телефоне должна быть в пределах действия CA-сертификата (2005–2034) во время установки.**

```bash
cd certs/NokiCert/nokicert-0.3.2/nokicert-0.3.2-bin
sh NokiCert.sh
```

В GUI:
1. Введите Bluetooth-адрес телефона (без двоеточий) и канал 15
2. Нажмите **Select certificate** → выберите `certs/trykote_ca.cer`
3. Поставьте галку **Apps. signing**
4. Нажмите **Install certificate**
5. Дождитесь "Status: certificate installed"

### 2.3 Проверка установки

```bash
gammu getfolderlisting "d:/predefhiddenfolder/certificates/auth" | grep -i trykote
```

Должен показать файл `TryKote CA`.

## Шаг 3: Сборка JAR

```bash
# Чистая сборка + оптимизированный JAR (ProGuard: shrinking + obfuscation)
rm -rf build
make compile
make optimized-jar
```

Оптимизированный JAR:
- Переименовывает `java.*`-классы Bouncy Castle (запрещены в J2ME) через обфускацию ProGuard
- Удаляет неиспользуемые классы Bouncy Castle
- Выполняет preverification для CLDC

Результат: `build/optimized-TK_MobileAgent_3.9.jar`

## Шаг 4: Создание и подпись JAD

### 4.1 Компиляция JadSign (однократно)

```bash
cat > /tmp/JadSign.java << 'JAVA'
import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.Base64;

public class JadSign {
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.err.println("Usage: JadSign <keystore.jks> <password> <alias> <jarfile> <jadfile>");
            System.exit(1);
        }
        String ksFile = args[0], ksPass = args[1], alias = args[2], jarFile = args[3], jadFile = args[4];

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(ksFile), ksPass.toCharArray());

        PrivateKey key = (PrivateKey) ks.getKey(alias, ksPass.toCharArray());
        java.security.cert.Certificate cert = ks.getCertificate(alias);

        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(key);
        FileInputStream jarIn = new FileInputStream(jarFile);
        byte[] buf = new byte[8192];
        int n;
        while ((n = jarIn.read(buf)) > 0) sig.update(buf, 0, n);
        jarIn.close();
        String sigB64 = Base64.getEncoder().encodeToString(sig.sign());
        String certB64 = Base64.getEncoder().encodeToString(cert.getEncoded());

        StringBuilder jad = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(jadFile));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("MIDlet-Certificate-") && !line.startsWith("MIDlet-Jar-RSA-SHA1"))
                jad.append(line).append("\n");
        }
        reader.close();

        jad.append("MIDlet-Certificate-1-1: ").append(certB64).append("\n");
        jad.append("MIDlet-Jar-RSA-SHA1: ").append(sigB64).append("\n");

        FileWriter writer = new FileWriter(jadFile);
        writer.write(jad.toString());
        writer.close();

        System.out.println("Signed. Sig=" + sigB64.length() + " chars, Cert=" + certB64.length() + " chars");
    }
}
JAVA
javac /tmp/JadSign.java -d /tmp
```

### 4.2 Создание неподписанного JAD

JAD **должен совпадать** с MANIFEST.MF внутри JAR. Критичные поля, которые должны быть идентичны:
- `MIDlet-1` (имя, иконка, класс — точное совпадение)
- `MIDlet-Name`
- `MIDlet-Version`
- `MIDlet-Vendor`
- `MicroEdition-Configuration` (CLDC-1.0, если так в MANIFEST.MF)
- `MicroEdition-Profile`

Сначала проверьте MANIFEST.MF:
```bash
unzip -p build/optimized-TK_MobileAgent_3.9.jar META-INF/MANIFEST.MF
```

Создайте JAD, соответствующий MANIFEST.MF:
```bash
JAR=build/optimized-TK_MobileAgent_3.9.jar
JAR_SIZE=$(stat -c%s "$JAR")

cat > build/MobileAgent.jad << JADEOF
MIDlet-1: TryKoteAgent,/icon.png,com.trykote.mobileagent.core.Midlet
MIDlet-Name: MobileAgent by TryKote
MIDlet-Version: 3.9.07
MIDlet-Vendor: TryKote
MIDlet-Description: MobileAgent by TryKote
MIDlet-Jar-URL: MobileAgent.jar
MIDlet-Jar-Size: ${JAR_SIZE}
MicroEdition-Configuration: CLDC-1.0
MicroEdition-Profile: MIDP-2.0
JADEOF
```

### 4.3 Подпись JAD

```bash
# Добавить сертификат подписи + подпись
java -cp /tmp JadSign certs/signer.jks changeit signer "$JAR" build/MobileAgent.jad

# Добавить CA-сертификат в цепочку
CA_B64=$(openssl x509 -inform DER -in certs/trykote_ca.cer -outform PEM | grep -v '^-----' | tr -d '\n')
echo "MIDlet-Certificate-1-2: ${CA_B64}" >> build/MobileAgent.jad
```

### 4.4 Подготовка файлов для телефона

```bash
cp build/optimized-TK_MobileAgent_3.9.jar build/MobileAgent.jar
```

Скопируйте оба файла `build/MobileAgent.jar` и `build/MobileAgent.jad` на SD-карту телефона (в одну папку).

## Шаг 5: Установка на телефон

1. **Установите дату на телефоне** в пределах действия сертификата подписи (2009–2029)
2. Откройте файловый менеджер на телефоне
3. Перейдите к `MobileAgent.jad` на SD-карте
4. Телефон прочитает JAD, загрузит JAR (из той же папки), проверит подпись
5. Установите приложение

## Устранение неполадок

### "Недопустимое приложение" (Invalid application)
- Атрибуты JAD не совпадают с MANIFEST.MF — проверьте идентичность `MIDlet-1`, `MIDlet-Name`, `MIDlet-Version`, `MIDlet-Vendor`, `MicroEdition-Configuration`
- `MIDlet-Jar-Size` не совпадает с реальным размером JAR-файла

### "Сертификат отсутствует на телефоне или SIM карте" (Certificate not found)
- CA-сертификат не установлен на телефоне — запустите NokiCert
- Установлен без галки "Apps. signing" — переустановите с галкой

### "Срок действия сертификата истёк" (Certificate expired)
- Дата на телефоне вне диапазона действия сертификата
- Установите дату телефона между 2009 и 2029

### Требования к цепочке сертификатов
- **Обязательна двухуровневая цепочка** — самоподписанные сертификаты отклоняются, даже если установлены в хранилище доверенных
- Certificate-1-1 = сертификат подписи (выданный CA)
- Certificate-1-2 = корневой CA-сертификат (самоподписанный, установленный на телефоне)

### Криптографические ограничения Nokia S40
- **Только RSA 1024 бит** — больший размер ключа может не работать
- **Только SHA-1** — SHA-256 не поддерживается для верификации подписей MIDlet
- **Перенос строк в JAD не нужен** — Nokia принимает длинные строки, несмотря на спецификацию MANIFEST

## Справочник файлов

```
certs/
  ca_key.pem              # Приватный ключ CA (ХРАНИТЬ В СЕКРЕТЕ)
  trykote_ca.cer          # CA-сертификат DER (устанавливается на телефон)
  trykote_ca.pem          # CA-сертификат PEM
  sign_key.pem            # Приватный ключ подписи (ХРАНИТЬ В СЕКРЕТЕ)
  sign_cert.cer           # Сертификат подписи DER
  sign_cert.pem           # Сертификат подписи PEM
  signer.jks              # Java KeyStore для JadSign
  signer.p12              # PKCS#12 хранилище (промежуточное)

build/
  optimized-TK_MobileAgent_3.9.jar  # JAR после ProGuard
  MobileAgent.jar                     # Копия (переименованная для JAD)
  MobileAgent.jad                     # Подписанный JAD-дескриптор
```
