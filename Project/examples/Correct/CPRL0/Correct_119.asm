   CALL _main
   HALT
_main:
   LDCSTR "character with \\n: "
   PUTSTR
   LDCCH '\n'
   PUTCH
   LDCSTR "remaining text."
   PUTSTR
   PUTEOL
   LDCSTR "character with \\t: "
   PUTSTR
   LDCCH '\t'
   PUTCH
   LDCSTR "remaining text."
   PUTSTR
   PUTEOL
   RET 0
