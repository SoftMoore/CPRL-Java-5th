   CALL _main
   HALT
_main:
   PROC 8
   LDLADDR 8
   LDCINT 3
   STOREW
   LDCSTR "Day number for var wed is "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTINT
   PUTEOL
   LDCSTR "Day number for Friday is  "
   PUTSTR
   LDCINT 5
   PUTINT
   PUTEOL
   LDLADDR 12
   LDCINT 0
   STOREW
L0:
   LDLADDR 12
   LOADW
   LDCINT 6
   BG L1
   LDCSTR "day number = "
   PUTSTR
   LDLADDR 12
   LOADW
   PUTINT
   PUTEOL
   LDLADDR 12
   LDLADDR 12
   LOADW
   INC
   STOREW
   BR L0
L1:
   RET 0
