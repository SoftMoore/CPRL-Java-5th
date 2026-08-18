   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCSTR "                                                   "
   STOREW
   LDLADDR 8
   LDCSTR "before tab \t after tab"
   STOREW
   LDLADDR 8
   LOADW
   PUTSTR
   PUTEOL
   LDCSTR "length of testString = "
   PUTSTR
   LDLADDR 8
   LOADW
   LOADW
   PUTINT
   PUTEOL
   PUTEOL
   LDLADDR 8
   LDCSTR "before carriage return \r after carriage return"
   STOREW
   LDLADDR 8
   LOADW
   PUTSTR
   PUTEOL
   LDCSTR "length of testString = "
   PUTSTR
   LDLADDR 8
   LOADW
   LOADW
   PUTINT
   PUTEOL
   PUTEOL
   LDLADDR 8
   LDCSTR "before newline \n after newline"
   STOREW
   LDLADDR 8
   LOADW
   PUTSTR
   PUTEOL
   LDCSTR "length of testString = "
   PUTSTR
   LDLADDR 8
   LOADW
   LOADW
   PUTINT
   PUTEOL
   PUTEOL
   LDLADDR 8
   LDCSTR "before single quote \' after single quote"
   STOREW
   LDLADDR 8
   LOADW
   PUTSTR
   PUTEOL
   LDCSTR "length of testString = "
   PUTSTR
   LDLADDR 8
   LOADW
   LOADW
   PUTINT
   PUTEOL
   PUTEOL
   LDLADDR 8
   LDCSTR "before double quote \" after double quote"
   STOREW
   LDLADDR 8
   LOADW
   PUTSTR
   PUTEOL
   LDCSTR "length of testString = "
   PUTSTR
   LDLADDR 8
   LOADW
   LOADW
   PUTINT
   PUTEOL
   PUTEOL
   LDLADDR 8
   LDCSTR "before backslash \\ after backslash"
   STOREW
   LDLADDR 8
   LOADW
   PUTSTR
   PUTEOL
   LDCSTR "length of testString = "
   PUTSTR
   LDLADDR 8
   LOADW
   LOADW
   PUTINT
   PUTEOL
   PUTEOL
   RET 0
