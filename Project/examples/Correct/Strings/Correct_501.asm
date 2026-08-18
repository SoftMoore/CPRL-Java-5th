   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCSTR "John"
   STOREW
   LDCSTR "Hello, "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCSTR "."
   PUTSTR
   PUTEOL
   RET 0
