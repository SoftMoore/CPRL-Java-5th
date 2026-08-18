   PROGRAM 4
   CALL _main
   HALT
_main:
L0:
   LDCSTR "Enter an integer (0 to exit): "
   PUTSTR
   LDGADDR 0
   GETINT
   LDGADDR 0
   LOADW
   LDCINT 0
   BGE L8
   LDGADDR 0
   LOADW
   PUTINT
   LDCSTR " is negative"
   PUTSTR
   PUTEOL
   BR L9
L8:
   LDGADDR 0
   LOADW
   LDCINT 0
   BNE L6
   LDGADDR 0
   LOADW
   PUTINT
   LDCSTR " is zero"
   PUTSTR
   PUTEOL
   BR L7
L6:
   LDGADDR 0
   LOADW
   PUTINT
   LDCSTR " is positive"
   PUTSTR
   PUTEOL
L7:
L9:
   LDGADDR 0
   LOADW
   LDCINT 0
   BE L1
   BR L0
L1:
   RET 0
