@echo off
set ACTIVEMQ_HOME="/afs/cad.njit.edu/courses/ccs/f21/cs/656/cmb/d17/amq"
set ACTIVEMQ_BASE="/afs/cad.njit.edu/courses/ccs/f21/cs/656/cmb/d17/S3"

set PARAM=%1
:getParam
shift
if "%1"=="" goto end
set PARAM=%PARAM% %1
goto getParam
:end

%ACTIVEMQ_HOME%/bin/activemq %PARAM%