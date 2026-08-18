   CALL _main
   HALT
_main:
   PROC 4
   LDCSTR "Enter lines (\":q\" to quit): "
   PUTSTR
   PUTEOL
L0:
   LDLADDR 8
   GETSTR
   ALLOC 1
   LDLADDR 8
   LOADW
   CALL _shouldExit
   BNZ L1
   LDLADDR 8
   LOADW
   PUTSTR
   PUTEOL
   BR L0
L1:
   RET 0
_shouldExit:
   LDLADDR -5
   LDLADDR -4
   LOADW
   LOADW
   LDCINT 2
   BE L6
   LDCB 0
   BR L7
L6:
   LDLADDR -4
   LOADW
   LDCINT 4
   ADD
   LDCINT 0
   LDCINT 2
   MUL
   ADD
   LOAD2B
   CHAR2INT
   LDCCH ':'
   CHAR2INT
   BNE L4
   LDCB 1
   BR L5
L4:
   LDCB 0
L5:
L7:
   BNZ L10
   LDCB 0
   BR L11
L10:
   LDLADDR -4
   LOADW
   LDCINT 4
   ADD
   LDCINT 1
   LDCINT 2
   MUL
   ADD
   LOAD2B
   CHAR2INT
   LDCCH 'q'
   CHAR2INT
   BNE L8
   LDCB 1
   BR L9
L8:
   LDCB 0
L9:
L11:
   STOREB
   RET 4
