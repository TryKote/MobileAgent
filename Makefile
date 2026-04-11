LIBS    = libs
CP      = $(LIBS)/cldcapi11.jar:$(LIBS)/midpapi20.jar:$(LIBS)/jsr75.jar:$(LIBS)/nokiaui.jar:$(LIBS)/wma.jar
SRC     = sources
BUILD   = build
JAR     = TK_MobileAgent_3.9.jar
JAVAC8  = /usr/lib/jvm/java-8-openjdk/bin/javac
PROGUARD = tools/proguard.jar

RESOURCES_SRC = resources-src
RESOURCES_OUT = $(BUILD)/resources
GEN_DIR  = $(BUILD)/generated/com/trykote/mobileagent/util
GEN_CFG  = config/remote_logger.cfg
GEN_SRC  = $(SRC)/com/trykote/mobileagent/util/RemoteLoggerConfig.java
TEST_CFG = config/test_account.cfg
TEST_SRC = $(SRC)/com/trykote/mobileagent/util/TestConfig.java
SOURCES  = $(shell find $(SRC) -name '*.java' ! -name 'RemoteLoggerConfig.java' ! -name 'TestConfig.java')

# Editor
EDITOR_SRC  = editor/src
EDITOR_BUILD = $(BUILD)/editor
JAVAFX_DIR  = /usr/lib/jvm/java-21-openjdk/lib
JAVAFX_CP   = $(JAVAFX_DIR)/javafx.base.jar:$(JAVAFX_DIR)/javafx.controls.jar:$(JAVAFX_DIR)/javafx.fxml.jar:$(JAVAFX_DIR)/javafx.graphics.jar
JAVAFX_MODS = javafx.controls,javafx.fxml
EDITOR_SOURCES = $(shell find $(EDITOR_SRC) -name '*.java')

.PHONY: all compile jar clean resources screen-defs palette-keys gen-keys editor

all: jar

compile: $(BUILD)/.compiled

# Resource generation from human-readable sources
resources: $(BUILD)/.resources

$(BUILD)/.resources: $(RESOURCES_SRC)/config.json \
                     $(RESOURCES_SRC)/cities.xml $(RESOURCES_SRC)/images/mapping.json \
                     $(RESOURCES_SRC)/blowfish_constants.bin
	@mkdir -p $(RESOURCES_OUT)
	python3 tools/cfg_tool.py --serialize $(RESOURCES_SRC) $(RESOURCES_OUT)/cfg
	tools/pack_cities.sh $(RESOURCES_OUT)
	tools/pack_resources.sh $(RESOURCES_OUT)
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
	python3 tools/patch_stringbuilder.py $(BUILD)/classes
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
	python3 tools/patch_class_version.py $(BUILD)/preverified 45.3
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

screen-defs:
	python3 tools/cfg_tool.py --gen-screens $(RESOURCES_SRC) $(SRC)/com/trykote/mobileagent/core/ScreenDef.java

palette-keys:
	python3 tools/cfg_tool.py --gen-palette $(RESOURCES_SRC) $(SRC)/com/trykote/mobileagent/core/PaletteKeys.java

gen-keys:
	python3 tools/cfg_tool.py --gen-keys $(RESOURCES_SRC) $(SRC)/com/trykote/mobileagent/core/

editor: $(BUILD)/.editor
	java -Dglass.gtk.uiScale=2.0 --module-path $(JAVAFX_CP) --add-modules $(JAVAFX_MODS) -cp $(EDITOR_BUILD) com.trykote.editor.EditorApp $(RESOURCES_SRC)

$(BUILD)/.editor: $(EDITOR_SOURCES)
	@mkdir -p $(EDITOR_BUILD)
	javac --module-path $(JAVAFX_CP) --add-modules $(JAVAFX_MODS) -d $(EDITOR_BUILD) -encoding UTF-8 $(EDITOR_SOURCES)
	@touch $@

clean:
	rm -rf $(BUILD)
