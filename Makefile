LIBS    = libs
CP      = $(LIBS)/cldcapi11.jar:$(LIBS)/midpapi20.jar:$(LIBS)/jsr75.jar:$(LIBS)/nokiaui.jar:$(LIBS)/wma.jar
SRC     = sources
BUILD   = build
JAR     = TK_MobileAgent_3.9.jar
JAVAC8  = /usr/lib/jvm/java-8-openjdk/bin/javac
PROGUARD = tools/proguard.jar
EDITOR  = cd editor && mvn -q exec:java -Dexec.mainClass="com.trykote.editor.Main"

RESOURCES_SRC = resources-src
RESOURCES_OUT = $(BUILD)/resources
GEN_DIR  = $(BUILD)/generated/com/trykote/mobileagent/util
GEN_CFG  = config/remote_logger.cfg
GEN_SRC  = $(SRC)/com/trykote/mobileagent/util/RemoteLoggerConfig.java
TEST_CFG = config/test_account.cfg
TEST_SRC = $(SRC)/com/trykote/mobileagent/util/TestConfig.java
SOURCES  = $(shell find $(SRC) -name '*.java' ! -name 'RemoteLoggerConfig.java' ! -name 'TestConfig.java')

# Editor (Maven project in editor/)

.PHONY: all compile jar clean resources screen-defs palette-keys gen-keys editor editor-build

all: jar

compile: $(BUILD)/.compiled

# Resource generation from human-readable sources
resources: $(BUILD)/.resources

$(BUILD)/.resources: $(RESOURCES_SRC)/config.json \
                     $(RESOURCES_SRC)/cities.xml $(RESOURCES_SRC)/images/mapping.json \
                     $(RESOURCES_SRC)/blowfish_constants.bin
	@mkdir -p $(RESOURCES_OUT)
	cd editor && mvn -q compile
	$(EDITOR) -Dexec.args="--gen-screens ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/core/ScreenDef.java"
	$(EDITOR) -Dexec.args="--gen-palette ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/core/PaletteKeys.java"
	$(EDITOR) -Dexec.args="--serialize ../$(RESOURCES_SRC) ../$(RESOURCES_OUT)/cfg"
	tools/pack_cities.sh $(RESOURCES_OUT)
	$(EDITOR) -Dexec.args="--pack-resources ../$(RESOURCES_SRC) ../$(RESOURCES_OUT)"
	@touch $@

$(GEN_DIR)/RemoteLoggerConfig.java: $(GEN_SRC) $(GEN_CFG)
	@mkdir -p $(GEN_DIR)
	@awk -F= 'FNR==NR { if ($$1=="enabled") e=($$2=="true"||$$2=="1")?"true":"false"; \
	                      if ($$1=="host") h=$$2; \
	                      if ($$1=="port") p=$$2; next } \
	           /@@REMOTE_LOGGER_ENABLED@@/ { sub(/= [^;]+;/, "= "e";") } \
	           /@@REMOTE_LOGGER_HOST@@/    { sub(/= "[^"]*";/, "= \""h"\";") } \
	           /@@REMOTE_LOGGER_PORT@@/    { sub(/= [^;]+;/, "= "p";") } \
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

$(BUILD)/.compiled: $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java $(GEN_DIR)/TestConfig.java
	@mkdir -p $(BUILD)/classes
	$(JAVAC8) -source 1.5 -target 1.5 -Xlint:-options -classpath "$(CP)" -d $(BUILD)/classes -encoding UTF-8 $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java $(GEN_DIR)/TestConfig.java
	cd editor && mvn -q compile
	$(EDITOR) -Dexec.args="--patch-stringbuilder ../$(BUILD)/classes"
	@rm -rf $(BUILD)/preverified
	java -jar $(PROGUARD) \
		-injars $(BUILD)/classes \
		-outjars $(BUILD)/preverified \
		-libraryjars $(LIBS)/cldcapi11.jar \
		-libraryjars $(LIBS)/midpapi20.jar \
		-libraryjars $(LIBS)/jsr75.jar \
		-libraryjars $(LIBS)/nokiaui.jar \
		-libraryjars $(LIBS)/wma.jar \
		-microedition \
		-dontshrink \
		-dontobfuscate \
		-dontoptimize \
		-dontwarn
	$(EDITOR) -Dexec.args="--patch-version ../$(BUILD)/preverified 45.3"
	@touch $@

jar: $(BUILD)/$(JAR)

$(BUILD)/$(JAR): $(BUILD)/.compiled $(BUILD)/.resources
	@mkdir -p $(BUILD)/jar
	cp -r $(RESOURCES_OUT)/* $(BUILD)/jar/
	cp -r $(BUILD)/preverified/* $(BUILD)/jar/
	rm -rf $(BUILD)/jar/META-INF
	mkdir -p $(BUILD)/jar/META-INF
	cp $(RESOURCES_SRC)/META-INF/MANIFEST.MF $(BUILD)/jar/META-INF/
	cd $(BUILD)/jar && jar cfm ../$(JAR) META-INF/MANIFEST.MF .
	@echo "Built: $(BUILD)/$(JAR)"

# Debug JAR: skips ProGuard/preverify/patch, keeps line numbers and debug info
debug-jar: $(BUILD)/.debug-compiled $(BUILD)/.resources
	@mkdir -p $(BUILD)/debug-jar
	cp -r $(RESOURCES_OUT)/* $(BUILD)/debug-jar/
	cp -r $(BUILD)/classes/* $(BUILD)/debug-jar/
	rm -rf $(BUILD)/debug-jar/META-INF
	mkdir -p $(BUILD)/debug-jar/META-INF
	cp $(RESOURCES_SRC)/META-INF/MANIFEST.MF $(BUILD)/debug-jar/META-INF/
	cd $(BUILD)/debug-jar && jar cfm ../debug-$(JAR) META-INF/MANIFEST.MF .
	@echo "Built: $(BUILD)/debug-$(JAR)"

$(BUILD)/.debug-compiled: $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java $(GEN_DIR)/TestConfig.java
	@mkdir -p $(BUILD)/classes
	$(JAVAC8) -g -source 1.5 -target 1.5 -Xlint:-options -classpath "$(CP)" -d $(BUILD)/classes -encoding UTF-8 $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java $(GEN_DIR)/TestConfig.java
	@touch $@

screen-defs: editor-build
	$(EDITOR) -Dexec.args="--gen-screens ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/core/ScreenDef.java"

palette-keys: editor-build
	$(EDITOR) -Dexec.args="--gen-palette ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/core/PaletteKeys.java"

gen-keys: editor-build
	$(EDITOR) -Dexec.args="--gen-keys ../$(RESOURCES_SRC) ../$(SRC)/com/trykote/mobileagent/core"

editor-build:
	cd editor && mvn -q compile

editor: editor-build
	cd editor && mvn -q javafx:run

clean:
	rm -rf $(BUILD)
