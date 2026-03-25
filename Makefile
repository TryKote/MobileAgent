LIBS    = libs
CP      = $(LIBS)/cldcapi11.jar:$(LIBS)/midpapi20.jar:$(LIBS)/jsr75.jar:$(LIBS)/nokiaui.jar:$(LIBS)/wma.jar
SRC     = sources
BUILD   = build
JAR     = TK_MobileAgent_3.9.jar

RESOURCES_SRC = resources-src
RESOURCES_OUT = $(BUILD)/resources
GEN_DIR  = $(BUILD)/generated/com/trykote/mobileagent/util
GEN_CFG  = config/remote_logger.cfg
GEN_SRC  = $(SRC)/com/trykote/mobileagent/util/RemoteLoggerConfig.java
SOURCES  = $(shell find $(SRC) -name '*.java' ! -name 'RemoteLoggerConfig.java')

.PHONY: all compile jar clean resources screen-defs

all: jar

compile: $(BUILD)/.compiled

# Resource generation from human-readable sources
resources: $(BUILD)/.resources

$(BUILD)/.resources: $(RESOURCES_SRC)/config.json \
                     $(RESOURCES_SRC)/cities.xml $(RESOURCES_SRC)/images/mapping.json \
                     $(RESOURCES_SRC)/xmpp_data.bin
	@mkdir -p $(RESOURCES_OUT)
	python3 tools/cfg_tool.py --pack $(RESOURCES_SRC) $(RESOURCES_OUT)/cfg
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

$(BUILD)/.compiled: $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java
	@mkdir -p $(BUILD)/classes
	javac --release 8 -classpath "$(CP)" -d $(BUILD)/classes -encoding UTF-8 $(SOURCES) $(GEN_DIR)/RemoteLoggerConfig.java
	@touch $@

jar: $(BUILD)/$(JAR)

$(BUILD)/$(JAR): $(BUILD)/.compiled $(BUILD)/.resources
	@mkdir -p $(BUILD)/jar
	cp -r $(RESOURCES_OUT)/* $(BUILD)/jar/
	cp -r $(BUILD)/classes/* $(BUILD)/jar/
	rm -rf $(BUILD)/jar/META-INF
	mkdir -p $(BUILD)/jar/META-INF
	cp $(RESOURCES_SRC)/META-INF/MANIFEST.MF $(BUILD)/jar/META-INF/
	cd $(BUILD)/jar && jar cfm ../$(JAR) META-INF/MANIFEST.MF .
	@echo "Built: $(BUILD)/$(JAR)"

screen-defs:
	python3 tools/cfg_tool.py --gen-screens $(RESOURCES_SRC) $(SRC)/com/trykote/mobileagent/core/ScreenDef.java

clean:
	rm -rf $(BUILD)
