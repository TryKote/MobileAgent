LIBS    = libs
CP      = $(LIBS)/cldcapi11.jar:$(LIBS)/midpapi20.jar:$(LIBS)/jsr75.jar:$(LIBS)/nokiaui.jar
SRC     = sources
BUILD   = build
JAR     = MobileAgent_3.9.jar

SOURCES = $(shell find $(SRC) -name '*.java')

.PHONY: all compile jar clean

all: jar

compile: $(BUILD)/.compiled

$(BUILD)/.compiled: $(SOURCES)
	@mkdir -p $(BUILD)/classes
	javac --release 8 -classpath "$(CP)" -d $(BUILD)/classes -encoding UTF-8 $(SOURCES)
	@touch $@

jar: $(BUILD)/$(JAR)

$(BUILD)/$(JAR): $(BUILD)/.compiled
	@mkdir -p $(BUILD)/jar
	cp -r $(BUILD)/classes/* $(BUILD)/jar/
	cp -r resources/* $(BUILD)/jar/
	rm -rf $(BUILD)/jar/META-INF
	mkdir -p $(BUILD)/jar/META-INF
	cp resources/META-INF/MANIFEST.MF $(BUILD)/jar/META-INF/
	cd $(BUILD)/jar && jar cfm ../$(JAR) META-INF/MANIFEST.MF .
	@echo "Built: $(BUILD)/$(JAR)"

clean:
	rm -rf $(BUILD)
