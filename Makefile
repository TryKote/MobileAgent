LIBS    = libs
CP      = $(LIBS)/cldcapi11.jar:$(LIBS)/midpapi20.jar:$(LIBS)/jsr75.jar:$(LIBS)/nokiaui.jar:$(LIBS)/wma.jar:$(LIBS)/bouncycastle-j2me.jar
SRC     = sources
BUILD   = build
JAR     = TK_MobileAgent_3.9.jar
JAVAC8  = /usr/lib/jvm/java-8-openjdk/bin/javac
PROGUARD = tools/proguard.jar
EDITOR  = cd editor && mvn -q exec:java -Dexec.mainClass="com.trykote.editor.Main"

# Release
RELEASE_JAR   = MobileAgent.jar
RELEASE_JAD   = MobileAgent.jad
JADSIGN_SRC   = tools/JadSign.java
JADSIGN_DIR   = $(BUILD)/jadsign
SIGN_KEYSTORE = certs/signer.jks
SIGN_PASSWORD = changeit
SIGN_ALIAS    = signer
CA_CERT_DER   = certs/trykote_ca.cer
JAD_FIELDS    = MIDlet-1|MIDlet-Name|MIDlet-Version|MIDlet-Vendor|MIDlet-Description|MIDlet-Permissions|MicroEdition-Configuration|MicroEdition-Profile

RESOURCES_SRC = resources-src
RESOURCES_OUT = $(BUILD)/resources
GEN_DIR  = $(BUILD)/generated/com/trykote/mobileagent/util
GEN_CFG  = config/remote_logger.cfg
GEN_SRC  = $(SRC)/com/trykote/mobileagent/util/RemoteLoggerConfig.java
TEST_CFG = config/test_account.cfg
TEST_SRC = $(SRC)/com/trykote/mobileagent/util/TestConfig.java
SOURCES  = $(shell find $(SRC) -name '*.java' ! -name 'RemoteLoggerConfig.java' ! -name 'TestConfig.java')

# Editor (Maven project in editor/)

.PHONY: all compile jar optimized-jar debug-jar clean clean-rms resources screen-defs palette-keys gen-keys gen-screens gen-string-pool gen-palette gen-all editor editor-build jadsign unsigned-release signed-release

all: jar

# --- Compilation (fast: javac + patch-stringbuilder only) ---

compile: $(BUILD)/.compiled

$(BUILD)/.compiled: $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java $(GEN_DIR)/TestConfig.java
	@mkdir -p $(BUILD)/classes
	$(JAVAC8) -source 1.5 -target 1.5 -Xlint:-options -classpath "$(CP)" -d $(BUILD)/classes -encoding UTF-8 $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java $(GEN_DIR)/TestConfig.java
	@cd $(BUILD)/classes && unzip -qo ../../$(LIBS)/bouncycastle-j2me.jar 'org/*'
	cd editor && mvn -q compile
	$(EDITOR) -Dexec.args="--patch-stringbuilder ../$(BUILD)/classes"
	@# CLDC 1.1 compatibility: detect autoboxing (Integer.valueOf etc.) that javac silently emits
	@bad=$$(grep -rl 'java/lang/Integer.valueOf\|java/lang/Long.valueOf\|java/lang/Short.valueOf\|java/lang/Byte.valueOf\|java/lang/Boolean.valueOf\|java/lang/Character.valueOf' $(BUILD)/classes/ 2>/dev/null); \
	if [ -n "$$bad" ]; then echo "ERROR: autoboxing detected (forbidden on CLDC 1.1):"; echo "$$bad"; exit 1; fi
	@touch $@

# --- JAR (preverify only, no optimization/obfuscation) ---

jar: $(BUILD)/$(JAR)

$(BUILD)/.preverified: $(BUILD)/.compiled
	@rm -rf $(BUILD)/preverified
	java -jar $(PROGUARD) \
		-injars $(BUILD)/classes \
		-outjars $(BUILD)/preverified \
		-libraryjars $(LIBS)/cldcapi11.jar \
		-libraryjars $(LIBS)/midpapi20.jar \
		-libraryjars $(LIBS)/jsr75.jar \
		-libraryjars $(LIBS)/nokiaui.jar \
		-libraryjars $(LIBS)/wma.jar \
		-include proguard-preverify.cfg
	$(EDITOR) -Dexec.args="--patch-version ../$(BUILD)/preverified 45.3"
	@touch $@

$(BUILD)/$(JAR): $(BUILD)/.preverified $(BUILD)/.resources
	@mkdir -p $(BUILD)/jar
	cp -r $(RESOURCES_OUT)/* $(BUILD)/jar/
	cp -r $(BUILD)/preverified/* $(BUILD)/jar/
	rm -rf $(BUILD)/jar/META-INF
	mkdir -p $(BUILD)/jar/META-INF
	cp $(RESOURCES_SRC)/META-INF/MANIFEST.MF $(BUILD)/jar/META-INF/
	cd $(BUILD)/jar && jar cfm ../$(JAR) META-INF/MANIFEST.MF .
	@echo "Built: $(BUILD)/$(JAR)"

# --- Optimized JAR (full ProGuard: preverify + optimize + obfuscate + shrink) ---

optimized-jar: $(BUILD)/optimized-$(JAR)

$(BUILD)/.optimized: $(BUILD)/.compiled
	@rm -rf $(BUILD)/optimized
	@cd $(BUILD)/classes && unzip -qo ../../$(LIBS)/bouncycastle-j2me.jar 'java/*'
	java -jar $(PROGUARD) \
		-injars $(BUILD)/classes \
		-outjars $(BUILD)/optimized \
		-libraryjars $(LIBS)/cldcapi11.jar \
		-libraryjars $(LIBS)/midpapi20.jar \
		-libraryjars $(LIBS)/jsr75.jar \
		-libraryjars $(LIBS)/nokiaui.jar \
		-libraryjars $(LIBS)/wma.jar \
		-include proguard.cfg
	$(EDITOR) -Dexec.args="--patch-version ../$(BUILD)/optimized 45.3"
	@touch $@

$(BUILD)/optimized-$(JAR): $(BUILD)/.optimized $(BUILD)/.resources
	@mkdir -p $(BUILD)/optimized-jar
	cp -r $(RESOURCES_OUT)/* $(BUILD)/optimized-jar/
	cp -r $(BUILD)/optimized/* $(BUILD)/optimized-jar/
	rm -rf $(BUILD)/optimized-jar/META-INF
	mkdir -p $(BUILD)/optimized-jar/META-INF
	cp $(RESOURCES_SRC)/META-INF/MANIFEST.MF $(BUILD)/optimized-jar/META-INF/
	cd $(BUILD)/optimized-jar && jar cfm ../optimized-$(JAR) META-INF/MANIFEST.MF .
	@echo "Built: $(BUILD)/optimized-$(JAR)"

# --- Debug JAR (no ProGuard, keeps debug info + line numbers) ---

debug-jar: $(BUILD)/debug-$(JAR)

$(BUILD)/.debug-compiled: $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java $(GEN_DIR)/TestConfig.java
	@mkdir -p $(BUILD)/classes
	$(JAVAC8) -g -source 1.5 -target 1.5 -Xlint:-options -classpath "$(CP)" -d $(BUILD)/classes -encoding UTF-8 $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java $(GEN_DIR)/TestConfig.java
	@cd $(BUILD)/classes && unzip -qo ../../$(LIBS)/bouncycastle-j2me.jar 'org/*'
	@touch $@

$(BUILD)/debug-$(JAR): $(BUILD)/.debug-compiled $(BUILD)/.resources
	$(EDITOR) -Dexec.args="--patch-version ../$(BUILD)/classes 45.3"
	@mkdir -p $(BUILD)/debug-jar
	cp -r $(RESOURCES_OUT)/* $(BUILD)/debug-jar/
	cp -r $(BUILD)/classes/* $(BUILD)/debug-jar/
	@# KEmulator's CustomClassLoader refuses to load classes in java.* packages.
	@# BC's java.* shims are needed on real J2ME (CLDC lacks SecureRandom/BigInteger)
	@# but break on full-JVM emulators — strip them so the bootstrap loader is used.
	rm -rf $(BUILD)/debug-jar/java
	rm -rf $(BUILD)/debug-jar/META-INF
	mkdir -p $(BUILD)/debug-jar/META-INF
	cp $(RESOURCES_SRC)/META-INF/MANIFEST.MF $(BUILD)/debug-jar/META-INF/
	cd $(BUILD)/debug-jar && jar cfm ../debug-$(JAR) META-INF/MANIFEST.MF .
	@echo "Built: $(BUILD)/debug-$(JAR)"

# --- Resources ---

resources: $(BUILD)/.resources

$(BUILD)/.resources: $(RESOURCES_SRC)/cities.xml $(RESOURCES_SRC)/images/mapping.json \
                     $(RESOURCES_SRC)/blowfish_constants.bin
	@mkdir -p $(RESOURCES_OUT)
	cd editor && mvn -q compile
	tools/pack_cities.sh $(RESOURCES_OUT)
	$(EDITOR) -Dexec.args="--pack-resources ../$(RESOURCES_SRC) ../$(RESOURCES_OUT)"
	@touch $@

# --- Generated config files ---

$(GEN_DIR)/RemoteLoggerConfig.java: $(GEN_SRC) $(GEN_CFG)
	@mkdir -p $(GEN_DIR)
	@awk -F= 'BEGIN { lvl="0" } \
	           FNR==NR { if ($$1=="enabled") e=($$2=="true"||$$2=="1")?"true":"false"; \
	                      if ($$1=="host") h=$$2; \
	                      if ($$1=="port") p=$$2; \
	                      if ($$1=="level") { \
	                          if ($$2=="trace")        lvl="0"; \
	                          else if ($$2=="debug")   lvl="1"; \
	                          else if ($$2=="info")    lvl="2"; \
	                          else if ($$2=="warning") lvl="3"; \
	                          else if ($$2=="error")   lvl="4"; \
	                      } \
	                      next } \
	           /@@REMOTE_LOGGER_ENABLED@@/ { sub(/= [^;]+;/, "= "e";") } \
	           /@@REMOTE_LOGGER_HOST@@/    { sub(/= "[^"]*";/, "= \""h"\";") } \
	           /@@REMOTE_LOGGER_PORT@@/    { sub(/= [^;]+;/, "= "p";") } \
	           /@@REMOTE_LOGGER_LEVEL@@/   { sub(/= [^;]+;/, "= "lvl";") } \
	           { print }' $(GEN_CFG) $(GEN_SRC) > $@

$(GEN_DIR)/TestConfig.java: $(TEST_SRC) $(TEST_CFG)
	@mkdir -p $(GEN_DIR)
	@awk -F= 'FNR==NR { if ($$1=="enabled") e=($$2=="true"||$$2=="1")?"true":"false"; \
	                      if ($$1=="login") l=$$2; \
	                      if ($$1=="password") pw=$$2; \
	                      if ($$1=="type") t=$$2; next } \
	           /@@TEST_ACCOUNT_ENABLED@@/ { sub(/= [^;]+;/, "= "e";") } \
	           /@@TEST_ACCOUNT_LOGIN@@/   { sub(/= "[^"]*";/, "= \""l"\";") } \
	           /@@TEST_ACCOUNT_PASSWORD@@/{ sub(/= "[^"]*";/, "= \""pw"\";") } \
	           /@@TEST_ACCOUNT_TYPE@@/    { sub(/= [^;]+;/, "= "t";") } \
	           { print }' $(TEST_CFG) $(TEST_SRC) > $@

# --- Code generation targets ---

screen-defs: editor-build
	$(EDITOR) -Dexec.args="--gen-screens ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/core/ScreenDef.java"

palette-keys: editor-build
	$(EDITOR) -Dexec.args="--gen-palette ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/key/PaletteKeys.java"

gen-keys: editor-build
	$(EDITOR) -Dexec.args="--gen-keys ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/key"

gen-screens: editor-build
	$(EDITOR) -Dexec.args="--gen-screen-factory ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/ui"

gen-string-pool: editor-build
	$(EDITOR) -Dexec.args="--gen-string-pool ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/core/StringPool.java"

gen-palette: editor-build
	$(EDITOR) -Dexec.args="--gen-palette-class ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/ui/Palette.java"

gen-pool-init: editor-build
	$(EDITOR) -Dexec.args="--gen-pool-init ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/core/PoolInit.java"

gen-all: gen-screens gen-string-pool gen-palette palette-keys gen-keys gen-pool-init ## Regenerate all generated Java files

editor-build:
	cd editor && mvn -q compile

editor: editor-build
	cd editor && mvn -q javafx:run

# --- JadSign tool ---

jadsign: $(JADSIGN_DIR)/.compiled

$(JADSIGN_DIR)/.compiled: $(JADSIGN_SRC)
	@mkdir -p $(JADSIGN_DIR)
	javac -d $(JADSIGN_DIR) $(JADSIGN_SRC)
	@touch $@

# --- Release builds ---
# Usage:
#   make unsigned-release                        # optimized JAR + unsigned JAD
#   make signed-release                          # optimized JAR + signed JAD
#   make signed-release SUBSTITUTE_TEST_DATA=1   # same, with test account from config/test_account.cfg

MANIFEST = $(RESOURCES_SRC)/META-INF/MANIFEST.MF

define release-build
@if [ -z "$(SUBSTITUTE_TEST_DATA)" ]; then \
	echo "Test account: disabled (pass SUBSTITUTE_TEST_DATA=1 to enable)"; \
	mkdir -p $(GEN_DIR); \
	cp $(TEST_SRC) $(GEN_DIR)/TestConfig.java; \
else \
	echo "Test account: enabled from $(TEST_CFG)"; \
	rm -f $(GEN_DIR)/TestConfig.java; \
fi
@rm -f $(BUILD)/.compiled $(BUILD)/.optimized
@$(MAKE) --no-print-directory optimized-jar
@cp $(BUILD)/optimized-$(JAR) $(BUILD)/$(RELEASE_JAR)
@JAR_SIZE=$$(stat -c%s "$(BUILD)/$(RELEASE_JAR)"); \
{ grep -E '^($(JAD_FIELDS)):' $(MANIFEST) | tr -d '\r'; \
  echo "MIDlet-Jar-URL: $(RELEASE_JAR)"; \
  echo "MIDlet-Jar-Size: $$JAR_SIZE"; \
} > $(BUILD)/$(RELEASE_JAD)
endef

unsigned-release:
	$(release-build)
	@echo "Release: $(BUILD)/$(RELEASE_JAR) + $(BUILD)/$(RELEASE_JAD)"

signed-release: $(JADSIGN_DIR)/.compiled
	$(release-build)
	@java -cp $(JADSIGN_DIR) JadSign $(SIGN_KEYSTORE) $(SIGN_PASSWORD) $(SIGN_ALIAS) \
		$(BUILD)/$(RELEASE_JAR) $(BUILD)/$(RELEASE_JAD)
	@CA_B64=$$(openssl x509 -inform DER -in $(CA_CERT_DER) -outform PEM | grep -v '^-----' | tr -d '\n'); \
	echo "MIDlet-Certificate-1-2: $$CA_B64" >> $(BUILD)/$(RELEASE_JAD)
	@echo "Signed release: $(BUILD)/$(RELEASE_JAR) + $(BUILD)/$(RELEASE_JAD)"

# --- Cleanup ---

clean:
	rm -rf $(BUILD)

clean-rms:
	rm -rf rms/*
	rm -rf ~/.local/share/KEmulator/rms/*
