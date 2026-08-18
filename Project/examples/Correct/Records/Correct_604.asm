   PROGRAM 104
   LDGADDR 0
   LDCSTR "invalid"
   LDCINT 0
   LDCSTR "January"
   LDCINT 31
   LDCSTR "February"
   LDCINT 29
   LDCSTR "March"
   LDCINT 31
   LDCSTR "April"
   LDCINT 30
   LDCSTR "May"
   LDCINT 31
   LDCSTR "June"
   LDCINT 30
   LDCSTR "July"
   LDCINT 31
   LDCSTR "August"
   LDCINT 31
   LDCSTR "September"
   LDCINT 30
   LDCSTR "October"
   LDCINT 31
   LDCSTR "November"
   LDCINT 30
   LDCSTR "December"
   LDCINT 31
   STORE 104
   CALL _main
   HALT
_writelnMonth:
   LDCSTR "Month "
   PUTSTR
   LDLADDR -8
   LOADW
   PUTSTR
   LDCSTR " has a maximum of "
   PUTSTR
   LDLADDR -8
   LDCINT 4
   ADD
   LOADW
   PUTINT
   LDCSTR " days."
   PUTSTR
   PUTEOL
   RET 8
_main:
   PROC 4
   LDLADDR 8
   LDCINT 1
   STOREW
L2:
   LDLADDR 8
   LOADW
   LDCINT 12
   BG L3
   LDGADDR 0
   LDLADDR 8
   LOADW
   LDCINT 8
   MUL
   ADD
   LOAD 8
   CALL _writelnMonth
   LDLADDR 8
   LDLADDR 8
   LOADW
   LDCINT 1
   ADD
   STOREW
   BR L2
L3:
   RET 0
