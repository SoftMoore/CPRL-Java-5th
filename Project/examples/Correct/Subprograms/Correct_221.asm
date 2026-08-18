   CALL _main
   HALT
_main:
   LDCCH 'A'
   CALL _ord
   ALLOC 2
   LDCINT 65
   CALL _chr
   PUTCH
   PUTEOL
   RET 0
_ord:
   LDCINT 70
   PUTINT
   PUTEOL
   RET 2
_chr:
   LDLADDR -6
   LDLADDR -4
   LOADW
   INT2CHAR
   STORE2B
   RET4
