   CALL _main
   HALT
_getName:
   LDLADDR -4
   LDCSTR "Gaby"
   STOREW
   RET 0
_main:
   PROC 4
   LDLADDR 8
   ALLOC 4
   CALL _getName
   STOREW
   LDLADDR 8
   LOADW
   PUTSTR
   PUTEOL
   RET 0
