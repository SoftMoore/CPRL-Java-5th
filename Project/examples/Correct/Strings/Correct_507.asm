   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   ALLOC 4
   CALL _readName
   STOREW
   LDCSTR "Your name is "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCSTR "."
   PUTSTR
   PUTEOL
   RET 0
_readName:
   PROC 4
   LDCSTR "What is your name?  "
   PUTSTR
   LDLADDR 8
   GETSTR
   LDCSTR "You entered "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCSTR "."
   PUTSTR
   PUTEOL
   LDLADDR -4
   LDLADDR 8
   LOADW
   STOREW
   RET 0
