# Copyright (C) 2011, 2012 jOVAL.org.  All rights reserved.
# This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

TOP=../..

include $(TOP)/common.mk

CLASSPATH="$(CLASSLIB)$(CLN)$(LIB)$(CLN)$(WSMAN_LIB)$(CLN)$(SRC)"
RUNTIMECP="$(CLASSLIB)$(CLN)$(LIB)$(CLN)$(WSMAN_LIB)$(CLN)$(WSMV_LIB)"

include classes.mk

CLASS_FILES:=$(foreach class, $(CLASSES), $(BUILD)/$(subst .,/,$(class)).class)
PACKAGES=$(sort $(basename $(CLASSES)))
PACKAGEDIRS=$(subst .,/,$(PACKAGES))

all: $(WSMV_LIB)

$(WSMV_LIB): classes resources
	$(JAR) cvf $@ -C $(BUILD)/ .

clean:
	rm -rf $(BUILD)
	rm -f $(WSMV_LIB)

dist-clean: clean
	rm -rf $(DIST)

dist: all
	mkdir -p $(DIST)
	cp -R $(RSRC)/bin/$(PLATFORM)/* $(DIST)
	cp -R $(RSRC)/licenses $(DIST)
	cp -R $(RSRC)/lib $(DIST)
	cp $(WSMV_LIB) $(DIST)/lib
	cp $(WSMAN_LIB) $(DIST)/lib

resources:
	rm -f $(BUILD)/*.properties
	cp $(RSRC)/*.properties $(BUILD)

classes: classdirs $(CLASS_FILES)

classdirs: $(foreach pkg, $(PACKAGEDIRS), $(BUILD)/$(pkg)/)

$(BUILD)/%.class: $(SRC)/%.java
	$(JAVAC) $(JAVACFLAGS) -d $(BUILD) -classpath $(CLASSPATH) $<

$(BUILD)/%/:
	mkdir -p $(subst PKG,,$@)

test:
#	$(JRE) -classpath $(RUNTIMECP) jwsmv.winrs.Client -r:family-pc -u:Administrator -p:password -noe cmd.exe
	$(JRE) -classpath $(RUNTIMECP) jwsmv.cim.Client tardis joval password
