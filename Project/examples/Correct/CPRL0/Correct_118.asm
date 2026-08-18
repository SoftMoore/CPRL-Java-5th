   CALL _main
   HALT
_main:
   LDCSTR "string with \\n: \nremaining text."
   PUTSTR
   PUTEOL
   LDCSTR "string with \\t: \tremaining text."
   PUTSTR
   PUTEOL
   RET 0
