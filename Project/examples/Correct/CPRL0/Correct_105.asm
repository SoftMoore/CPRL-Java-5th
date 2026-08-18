   PROGRAM 8
   CALL _main
   HALT
_main:
   LDCSTR "Enter value for x: "
   PUTSTR
   LDGADDR 0
   GETINT
   LDCSTR "Enter value for y: "
   PUTSTR
   LDGADDR 4
   GETINT
   LDCSTR "x = "
   PUTSTR
   LDGADDR 0
   LOADW
   PUTINT
   PUTEOL
   LDCSTR "y = "
   PUTSTR
   LDGADDR 4
   LOADW
   PUTINT
   PUTEOL
   LDGADDR 0
   LOADW
   LDGADDR 4
   LOADW
   BG L2
   LDCSTR "x <= y"
   PUTSTR
   PUTEOL
   BR L3
L2:
   LDCSTR "x > y"
   PUTSTR
   PUTEOL
L3:
   RET 0
