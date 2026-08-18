   CALL _main
   HALT
_main:
   CALL _reverse
   PUTEOL
   RET 0
_reverse:
   PROC 2
   LDLADDR 8
   GETCH
   LDLADDR 8
   LOAD2B
   CHAR2INT
   LDCCH 'E'
   CHAR2INT
   BE L2
   CALL _reverse
L2:
   LDLADDR 8
   LOAD2B
   PUTCH
   RET 0
