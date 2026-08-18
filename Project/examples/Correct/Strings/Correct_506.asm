   CALL _main
   HALT
_main:
   PROC 4
   LDCSTR "Enter a name: "
   PUTSTR
   LDLADDR 8
   GETSTR
   LDLADDR 8
   LOADW
   CALL _writeName
   PUTEOL
   RET 0
_writeName:
   LDCSTR "Hello, "
   PUTSTR
   LDLADDR -4
   LOADW
   PUTSTR
   LDCSTR "."
   PUTSTR
   RET 4
