   CALL _main
   HALT
_main:
   LDCB 0
   BYTE2INT
   LDCB 1
   BYTE2INT
   BGE L2
   LDCSTR "false < true"
   PUTSTR
   PUTEOL
   BR L3
L2:
   LDCSTR "false >= true"
   PUTSTR
   PUTEOL
L3:
   LDCCH 'a'
   CHAR2INT
   LDCCH 'b'
   CHAR2INT
   BGE L6
   LDCSTR "'a' < 'b'"
   PUTSTR
   PUTEOL
   BR L7
L6:
   LDCSTR "'a' >= 'b'"
   PUTSTR
   PUTEOL
L7:
   RET 0
