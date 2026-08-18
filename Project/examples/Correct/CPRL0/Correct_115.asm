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
   LDCSTR "x + y = "
   PUTSTR
   LDGADDR 0
   LOADW
   LDGADDR 4
   LOADW
   ADD
   PUTINT
   PUTEOL
   RET 0
