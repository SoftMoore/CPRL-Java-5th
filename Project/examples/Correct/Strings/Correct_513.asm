   CALL _main
   HALT
_main:
   PROC 8
   LDLADDR 8
   LDCSTR "Jane"
   STOREW
   LDLADDR 12
   LDCSTR "Doe"
   STOREW
   LDLADDR 8
   LDCSTR " "
   CALL _writeConcatenatedStrings
   LDLADDR 12
   LOADW
   CALL _writeStringChars
   PUTEOL
   LDCSTR "Jane Doe"
   PUTSTR
   PUTEOL
   RET 0
_writeConcatenatedStrings:
   LDLADDR -8
   LOADW
   LOADW
   CALL _writeStringChars
   LDLADDR -4
   LOADW
   PUTSTR
   RET 8
_writeStringChars:
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
   LDLADDR 8
   LDLADDR 8
   LOADW
   INC
   STOREW
   BR L0
L1:
   RET 4
