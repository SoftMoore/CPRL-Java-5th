   CALL _main
   HALT
_main:
   PROC 8
   LDLADDR 12
   LDCSTR "Hello, "
   STOREW
   LDCSTR "What is your name?  "
   PUTSTR
   LDLADDR 8
   GETSTR
   LDLADDR 12
   LOADW
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCSTR "."
   PUTSTR
   PUTEOL
   RET 0
