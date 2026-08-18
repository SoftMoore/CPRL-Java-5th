@echo off

rem
rem make the cvm executable
rem (run in a VS deveoper command prompt)
rem

cl /Fecprl /0x /std:c17 cvm.c opcode.c
