   CALL _main
   HALT
_main:
   PROC 8
   LDLADDR 8
   LDCSTR "John"
   STOREW
   LDCSTR "firstName = "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   PUTEOL
   LDCSTR "firstName.length = "
   PUTSTR
   LDLADDR 8
   LOADW
   LOADW
   PUTINT
   PUTEOL
   LDCSTR "firstName[2] = "
   PUTSTR
   LDLADDR 8
   LOADW
   LDCINT 4
   ADD
   LDCINT 2
   LDCINT 2
   MUL
   ADD
   LOAD2B
   PUTCH
   PUTEOL
   LDCSTR "firstName written one character at a time: "
   PUTSTR
   LDLADDR 12
   LDCINT 0
   STOREW
L0:
   LDLADDR 12
   LOADW
   LDLADDR 8
   LOADW
   LOADW
   LDCINT 1
   SUB
   BG L1
   LDLADDR 8
   LOADW
   LDCINT 4
   ADD
   LDLADDR 12
   LOADW
   LDCINT 2
   MUL
   ADD
   LOAD2B
   PUTCH
   LDLADDR 12
   LDLADDR 12
   LOADW
   INC
   STOREW
   BR L0
L1:
   PUTEOL
   RET 0
