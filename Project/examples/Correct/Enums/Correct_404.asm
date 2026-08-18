   PROGRAM 28
   LDGADDR 0
   LDCSTR "Sunday"
   LDCSTR "Monday"
   LDCSTR "Tuesday"
   LDCSTR "Wednesday"
   LDCSTR "Thursday"
   LDCSTR "Friday"
   LDCSTR "Saturday"
   STORE 28
   CALL _main
   HALT
_main:
   PROC 4
   LDCSTR "Days of the week:"
   PUTSTR
   PUTEOL
   LDLADDR 8
   LDCINT 0
   STOREW
L0:
   LDLADDR 8
   LOADW
   LDCINT 6
   BG L1
   LDGADDR 0
   LDLADDR 8
   LOADW
   LDCINT 4
   MUL
   ADD
   LOADW
   PUTSTR
   PUTEOL
   LDLADDR 8
   LDLADDR 8
   LOADW
   INC
   STOREW
   BR L0
L1:
   RET 0
