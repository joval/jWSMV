#!/bin/sh
# Copyright (C) 2011 jOVAL.org.  All rights reserved.
# This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt
#
export INSTALL_DIR=`dirname ${0}`
export LIB=${INSTALL_DIR}/lib
if [ "x${JAVA_HOME}" == x ]; then
    echo "JAVA_HOME environment variable must be set to run winrs."
else
    ${JAVA_HOME}/bin/java -cp "${LIB}/*" jwsmv.winrs.Client "$@"
fi
