   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCSTR "John"
   STOREW
   LDCSTR "Name is "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCCH '.'
   PUTCH
   PUTEOL
   LDLADDR 8
   CALL _changeName
   LDCSTR "Name changed to "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCCH '.'
   PUTCH
   PUTEOL
   RET 0
_changeName:
   LDLADDR -4
   LOADW
   LDCSTR "Johnny"
   STOREW
   RET 4
