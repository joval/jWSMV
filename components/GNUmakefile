# Copyright (C) 2011 jOVAL.org.  All rights reserved.
# This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

all: _winrs

rebuild:
	@$(MAKE) --keep-going --directory=winrs all

clean:
	@$(MAKE) --keep-going --directory=ws-man clean
	@$(MAKE) --keep-going --directory=winrs dist-clean

_winrs: _ws-man
	@$(MAKE) --keep-going --directory=winrs dist

_ws-man:
	@$(MAKE) --keep-going --directory=ws-man all
