#
# variables set by `configure':
#

CONFIGFILE = ../../Makefile.inc
include $(CONFIGFILE)

#
# Local definitions
#
O=$(OMNETPP_OUT_DIR)/$(CONFIGNAME)/src/utils
L=$(OMNETPP_LIB_DIR)

.PHONY : all clean
#
# Rules
#
all: $(CONFIGFILE)
	$(Q)echo "#!/bin/sh" >$(OMNETPP_BIN_DIR)/opp_configfilepath && echo '(cd $$(dirname $$0)/.. && echo $$(pwd)/Makefile.inc)' >>$(OMNETPP_BIN_DIR)/opp_configfilepath
	$(Q)chmod +x $(OMNETPP_BIN_DIR)/opp_configfilepath 

clean:
	$(qecho) Cleaning utils
	$(Q)rm -rf $O
	$(Q)cd $(OMNETPP_BIN_DIR) && rm -f omnest omnetpp opp_ide opp_configfilepath opp_charttool opp_featuretool opp_shlib_postprocess opp_makemake opp_runall opp_neddoc opp_test opp_fingerprinttest

-include $(OBJS:%=%.d)

