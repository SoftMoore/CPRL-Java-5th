   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCINT 0
   STOREW
L0:
   LDLADDR 8
   LOADW
   LDCINT 6
   BG L1
   ALLOC 1
   LDLADDR 8
   LOADW
   CALL _isWeekDay
   BZ L2
   LDCSTR "day "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTINT
   LDCSTR " is a weekday"
   PUTSTR
   PUTEOL
   BR L3
L2:
   LDCSTR "day "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTINT
   LDCSTR " is not a weekday"
   PUTSTR
   PUTEOL
L3:
   LDLADDR 8
   LDLADDR 8
   LOADW
   INC
   STOREW
   BR L0
L1:
   RET 0
_isWeekDay:
   LDLADDR -5
   LDLADDR -4
   LOADW
   LDCINT 1
   BGE L8
   LDCB 0
   BR L9
L8:
   LDLADDR -4
   LOADW
   LDCINT 5
   BG L6
   LDCB 1
   BR L7
L6:
   LDCB 0
L7:
L9:
   STOREB
   RET 4
