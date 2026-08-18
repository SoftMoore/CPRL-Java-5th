   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCINT 2
   STOREW
   LDCSTR "Day.Tue = "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTINT
   PUTEOL
   LDCSTR "Day.Day = "
   PUTSTR
   LDCINT 7
   PUTINT
   PUTEOL
   RET 0
