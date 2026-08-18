   CALL _main
   HALT
_main:
   PROC 4
   LDLADDR 8
   LDCSTR "Marie"
   STOREW
   LDCSTR "The length of \""
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCSTR "\" is "
   PUTSTR
   LDLADDR 8
   LOADW
   LOADW
   PUTINT
   LDCCH '.'
   PUTCH
   PUTEOL
   LDCSTR "The chars in "
   PUTSTR
   LDLADDR 8
   LOADW
   PUTSTR
   LDCSTR " are "
   PUTSTR
   LDLADDR 8
   LOADW
   CALL _writeChars
   LDCCH '.'
   PUTCH
   PUTEOL
   RET 0
_writeChars:
   PROC 4
   LDLADDR 8
   LDCINT 0
   STOREW
L0:
   LDLADDR 8
   LOADW
   LDLADDR -4
   LOADW
   LOADW
   LDCINT 1
   SUB
   BG L1
   LDCCH '\''
   PUTCH
   LDLADDR -4
   LOADW
   LDCINT 4
   ADD
   LDLADDR 8
   LOADW
   LDCINT 2
   MUL
   ADD
   LOAD2B
   PUTCH
   LDCCH '\''
   PUTCH
   LDLADDR 8
   LOADW
   LDLADDR -4
   LOADW
   LOADW
   LDCINT 1
   SUB
   BGE L4
   LDCCH ' '
   PUTCH
L4:
   LDLADDR 8
   LDLADDR 8
   LOADW
   INC
   STOREW
   BR L0
L1:
   RET 4
