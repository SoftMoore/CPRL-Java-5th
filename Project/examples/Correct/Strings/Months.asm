   PROGRAM 48
   LDGADDR 0
   LDCSTR "January"
   LDCSTR "February"
   LDCSTR "March"
   LDCSTR "April"
   LDCSTR "May"
   LDCSTR "June"
   LDCSTR "July"
   LDCSTR "August"
   LDCSTR "September"
   LDCSTR "October"
   LDCSTR "November"
   LDCSTR "December"
   STORE 48
   CALL _main
   HALT
_main:
   PROC 4
   LDCSTR "The months are as follows:"
   PUTSTR
   PUTEOL
   LDLADDR 8
   LDCINT 0
   STOREW
L0:
   LDLADDR 8
   LOADW
   LDCINT 11
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
