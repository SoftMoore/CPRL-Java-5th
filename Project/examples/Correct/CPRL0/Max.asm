   PROGRAM 8
   CALL _main
   HALT
_main:
L0:
   LDCSTR "Enter two integer values.  Enter two zeros to exit."
   PUTSTR
   PUTEOL
   LDGADDR 0
   GETINT
   LDGADDR 4
   GETINT
   LDGADDR 0
   LOADW
   LDCINT 0
   BE L6
   LDCB 0
   BR L7
L6:
   LDGADDR 4
   LOADW
   LDCINT 0
   BNE L4
   LDCB 1
   BR L5
L4:
   LDCB 0
L5:
L7:
   BNZ L1
   LDCSTR "max of the two values is "
   PUTSTR
   LDGADDR 0
   LOADW
   LDGADDR 4
   LOADW
   BLE L10
   LDGADDR 0
   LOADW
   BR L11
L10:
   LDGADDR 4
   LOADW
L11:
   PUTINT
   PUTEOL
   BR L0
L1:
   RET 0
