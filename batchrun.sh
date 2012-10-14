#!/bin/sh
# simple wrapper script to do batch runs ...e.g.,
#     /users/rlr/RePast/Demos-3/heatBugs2/bin/batchrun.sh nB=100 T=400
# this is primarily intended to use be used as the programName in
# drone control files.  See
#    /users/rlr/RePast/Demos-3/heatBugs2/Readme.txt
#

# This first is the path to the location of all the parts of the project,
# eg, where the src/  classes/ bin/ dirs are located.
# CHANGE THIS after copy/move the project ==>
PROJECTDIR=C:\RepastSimphony-2.0\work_workspace\Spanglish

PACKAGENAME=spanglish


# the parameters below only occassionally need to be changed
BATCHMODELNAME=BatchModel

# add extra user libraries that should be included here
USERDEFINEDLIBS=

########################################################################
########################################################################
# PROBABLY NO CHANGES BELOW THIS

# the params below should be changed when versions of java
# and different libraries are upgraded so they point to the
# correct paths of java and libraries
case $(uname) in
   Darwin)
      JAVADIR="/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home"
      ;;
   Linux)
      JAVADIR=/appl64/jdk1.6.0_11

      ;;
   *)
      JAVADIR=/appl/jdk1.6.0_10
      ;;
esac 


CSCS530LIBDIR=C:\RepastSimphony-2.0\work_workspace

JSCLIBDIR=

# the actual run command 
$JAVADIR/bin/java $JAVAPAR -cp $USERDEFINEDLIBS:$PROJECTDIR/bin:$CSCS530LIBDIR/cscs530.jar $PACKAGENAME.$BATCHMODELNAME $*
