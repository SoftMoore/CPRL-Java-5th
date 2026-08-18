   CALL _main
   HALT
_main:
   PROC 4
L0:
   LDCSTR "Enter an integer: "
   PUTSTR
   LDLADDR 8
   GETINT
   LDCSTR "abs("
   PUTSTR
   LDLADDR 8
   LOADW
   PUTINT
   LDCSTR ") = "
   PUTSTR
   ALLOC 4
   LDLADDR 8
   LOADW
   CALL _abs
   PUTINT
   PUTEOL
   LDLADDR 8
   LOADW
   LDCINT 0
   BE L1
   BR L0
L1:
   RET 0
_abs:
   LDLADDR -8
   LDLADDR -4
   LOADW
   LDCINT 0
   BL L6
   LDLADDR -4
   LOADW
   BR L7
L6:
   LDLADDR -4
   LOADW
   NEG
L7:
   STOREW
   RET 4
