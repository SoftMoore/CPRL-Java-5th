   PROGRAM 4
   CALL _main
   HALT
_main:
   LDCSTR "Enter value for x: "
   PUTSTR
   LDGADDR 0
   GETINT
   LDGADDR 0
   LOADW
   LDCINT 5
   BG L0
   LDCB 1
   BR L1
L0:
   LDCB 0
L1:
   BYTE2INT
   PUTINT
   RET 0
