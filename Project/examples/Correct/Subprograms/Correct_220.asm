   CALL _main
   HALT
_main:
   ALLOC 4
   LDCCH 'A'
   CALL _ord
   PUTINT
   PUTEOL
   ALLOC 2
   LDCINT 65
   CALL _chr
   PUTCH
   PUTEOL
   RET 0
_ord:
   LDLADDR -6
   LDCINT 70
   STOREW
   RET 2
_chr:
   LDLADDR -6
   LDLADDR -4
   LOADW
   INT2CHAR
   STORE2B
   RET4
