   CALL _main
   HALT
_main:
   PROC 6
   LDCCH 'A'
   LDCINT 65
   CALL _check
   LDCCH '3'
   LDCINT 51
   CALL _check
   LDCCH 'π'
   LDCINT 960
   CALL _check
   LDCCH '€'
   LDCINT 8364
   CALL _check
   LDCCH '\n'
   LDCINT 10
   CALL _check
   RET 0
_check:
   PROC 6
   LDCSTR "Char "
   PUTSTR
   LDLADDR -6
   LOAD2B
   PUTCH
   LDCSTR " converted to Integer = "
   PUTSTR
   ALLOC 4
   LDLADDR -6
   LOAD2B
   CALL _ord
   PUTINT
   LDCCH '.'
   PUTCH
   PUTEOL
   LDCSTR "Integer "
   PUTSTR
   LDLADDR -4
   LOADW
   PUTINT
   LDCSTR " converted to Char = "
   PUTSTR
   ALLOC 2
   LDLADDR -4
   LOADW
   CALL _chr
   PUTCH
   LDCCH '.'
   PUTCH
   PUTEOL
   LDLADDR 10
   ALLOC 4
   LDLADDR -6
   LOAD2B
   CALL _ord
   STOREW
   LDLADDR 8
   ALLOC 2
   LDLADDR 10
   LOADW
   CALL _chr
   STORE2B
   LDLADDR -6
   LOAD2B
   LDLADDR 8
   LOAD2B
   CALL _checkEqualChars
   PUTEOL
   RET 6
_checkEqualChars:
   LDLADDR -4
   LOAD2B
   CHAR2INT
   LDLADDR -2
   LOAD2B
   CHAR2INT
   BE L2
   LDCSTR "*** Error : c = "
   PUTSTR
   LDLADDR -4
   LOAD2B
   PUTCH
   LDCSTR ", c2 = "
   PUTSTR
   LDLADDR -2
   LOAD2B
   PUTCH
   PUTEOL
L2:
   RET 4
_ord:
   LDLADDR -6
   LDLADDR -2
   LOAD2B
   CHAR2INT
   STOREW
   RET 2
_chr:
   LDLADDR -6
   LDLADDR -4
   LOADW
   INT2CHAR
   STORE2B
   RET4
