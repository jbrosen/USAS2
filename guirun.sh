#!/bin/sh
# simple wrapper script to do gui runs ...e.g.,
#     bin/guirun.sh D=3  

# This first is the path to the location of all the parts of the project,
# eg, where the src/  classes/ bin/ dirs are located.
# CHANGE THIS after copy/move the project ==>
PROJECTDIR=C:\RepastSimphony-2.0\work_workspace\Spanglish



# the parameters below only occassionally need to be changed
PACKAGENAME=spanglish
GUIMODELNAME=GUIModel


# add extra user libraries that should be included here
USERDEFINEDLIBS=

# add a run time parameter to java here
JAVAPAR=

########################################################################
########################################################################
# PROBABLY NO CHANGES BELOW THIS

# the params below should be changed when versions of java
# and different libraries are upgraded so they point to the
# correct paths of java and libraries

# java path -- linux or mac
C:\Users\Jacob\Documents\jre-7u5-windows-x64


CSCS530LIBDIR=C:\RepastSimphony-2.0\work_workspace

# the actual run command 
$JAVADIR\bin\java $JAVAPAR -cp $USERDEFINEDLIBS:$PROJECTDIR\bin:$CSCS530LIBDIR\bcscs530.jar  $PACKAGENAME.$GUIMODELNAME $*
