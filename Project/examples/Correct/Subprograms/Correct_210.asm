   CALL _main
   HALT
_main:
   PROC 8
L0:
   LDCSTR "Enter value for a (0 to exit):  "
   PUTSTR
   LDLADDR 8
   GETINT
   LDCSTR "a = "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTINT
   PUTEOL
   LDLADDR 8
   LOADW
   LDCINT 0
   BE L1
   LDCSTR "Enter value for b:  "
   PUTSTR
   LDLADDR 12
   GETINT
   LDCSTR "b = "
   PUTSTR
   LDLADDR 12
   LOADW
   PUTINT
   PUTEOL
   PUTEOL
   LDCSTR "lessThan("
   PUTSTR
   LDLADDR 8
   LOADW
   PUTINT
   LDCSTR ", "
   PUTSTR
   LDLADDR 12
   LOADW
   PUTINT
   LDCSTR ") = "
   PUTSTR
   ALLOC 1
   LDLADDR 8
   LOADW
   LDLADDR 12
   LOADW
   CALL _lessThan
   CALL _writeBoolean
   PUTEOL
   PUTEOL
   BR L0
L1:
   LDCSTR "Done."
   PUTSTR
   PUTEOL
   RET 0
_lessThan:
   LDLADDR -9
   LDLADDR -8
   LOADW
   LDLADDR -4
   LOADW
   BGE L4
   LDCB 1
   BR L5
L4:
   LDCB 0
L5:
   STOREB
   RET 8
_writeBoolean:
   LDLADDR -1
   LOADB
   BZ L6
   LDCSTR "true"
   PUTSTR
   BR L7
L6:
   LDCSTR "false"
   PUTSTR
L7:
   RET 1
