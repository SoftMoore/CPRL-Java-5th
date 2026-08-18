@echo off

rem
rem Run CPRL Virtual Machine interpreter on a single ".obj" file
rem

rem set config environment variables locally
setlocal
call cprl_config.cmd

set CLASSPATH=%COMPILER_PROJECT_PATH%
java -ea -cp "%CLASSPATH%" cvm.Cprl %1

rem restore settings
endlocal
