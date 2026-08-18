   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCINT 12
   STOREW
   ALLOC 4
   LDLADDR 8
   LOADW
   CALL _inc
   PUTINT
   PUTEOL
   RET 0
_inc:
   LDLADDR -8
   LDLADDR -4
   LOADW
   LDCINT 1
   ADD
   STOREW
   RET 4
