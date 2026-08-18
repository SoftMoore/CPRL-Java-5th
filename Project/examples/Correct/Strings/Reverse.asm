   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCSTR "12345"
   STOREW
   LDCSTR "Writing \""
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCSTR "\" in reverse order is \""
   PUTSTR
   LDLADDR 8
   LOADW
   CALL _writeReversed
   LDCSTR "\""
   PUTSTR
   LDCCH '.'
   PUTCH
   PUTEOL
   RET 0
_writeReversed:
   PROC 8
   LDLADDR 8
   LDLADDR -4
   LOADW
   LOADW
   LDCINT 1
   SUB
   STOREW
   LDLADDR 12
   LDCINT 0
   STOREW
L0:
   LDLADDR 12
   LOADW
   LDLADDR 8
   LOADW
   BG L1
   LDLADDR -4
   LOADW
   LDCINT 4
   ADD
   LDLADDR 8
   LOADW
   LDLADDR 12
   LOADW
   SUB
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
   RET 4
