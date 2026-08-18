   CALL _main
   HALT
_main:
   PROC 8
   LDLADDR 8
   LDCSTR "John"
   STOREW
   LDCSTR "Name1 is "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCCH '.'
   PUTCH
   PUTEOL
   LDCSTR "Hello, "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCCH '.'
   PUTCH
   PUTEOL
   LDLADDR 8
   LDCSTR "Joan"
   STOREW
   LDCSTR "Name1 changed to "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCCH '.'
   PUTCH
   PUTEOL
   LDCSTR "Hello, "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCCH '.'
   PUTCH
   PUTEOL
   LDLADDR 12
   LDCSTR "Barbara"
   STOREW
   LDCSTR "Name2 is "
   PUTSTR
   LDLADDR 12
   LOADW
   PUTSTR
   LDCCH '.'
   PUTCH
   PUTEOL
   LDCSTR "Hello, "
   PUTSTR
   LDLADDR 12
   LOADW
   PUTSTR
   LDCCH '.'
   PUTCH
   PUTEOL
   RET 0
