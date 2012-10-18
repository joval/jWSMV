# Copyright (C) 2011 jOVAL.org.  All rights reserved.
# This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

include $(TOP)/customize.mk

Default: all

PLATFORM=unknown
ifeq (Windows, $(findstring Windows,$(OS)))
    PLATFORM=win
    CLN=;
    JAVACFLAGS=-Xlint:unchecked
else
    OS=$(shell uname)
    CLN=:
    JAVACFLAGS=-Xlint:unchecked -XDignore.symbol.file=true -Xbootclasspath/p:$(JAXB_HOME)/lib/jaxb-api.jar:$(JAXB_HOME)/lib/jaxb-impl.jar
endif

ifeq (Linux, $(findstring Linux,$(OS)))
    PLATFORM=linux
endif

NULL:=
SPACE:=$(NULL) # end of the line
SHELL=/bin/sh
CWD=$(shell pwd)

ifeq (x86, $(ARCH))
    JRE=$(JRE32_HOME)/bin/java
else
    JRE=$(JRE64_HOME)/bin/java
endif

JAVA=$(JAVA_HOME)/bin/java
JAVA_VERSION=1.6
ifeq (1.7, $(findstring 1.7,`$(JAVA) -version`))
    JAVA_VERSION=1.7
endif

JAXB_HOME=$(TOP)/tools/jaxb-ri-2.2.6
XJC=$(JAVA) -jar $(JAXB_HOME)/lib/jaxb-xjc.jar
XJCFLAGS=-enableIntrospection
JAVAC=$(JAVA_HOME)/bin/javac
JAR=$(JAVA_HOME)/bin/jar
CLASSLIB=$(JAVA_HOME)/jre/lib/rt.jar
BUILD=build
DIST=dist
RSRC=rsrc
DOCS=docs/api
SRC=$(TOP)/src
COMPONENTS=$(TOP)/components
LIBDIR=$(RSRC)/lib
LIB=$(subst $(SPACE),$(CLN),$(filter %.jar %.zip, $(wildcard $(LIBDIR)/*)))

WSMAN=$(COMPONENTS)/ws-man
WSMAN_LIB=$(WSMAN)/ws-man.jar
WSMV=$(COMPONENTS)/winrs
WSMV_LIB=$(WSMV)/jWSMV.jar
