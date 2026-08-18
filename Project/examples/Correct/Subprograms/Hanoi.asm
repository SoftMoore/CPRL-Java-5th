   CALL _main
   HALT
_main:
   PROC 4
   LDCSTR "Enter number of disks to be moved:  "
   PUTSTR
   LDLADDR 8
   GETINT
   LDLADDR 8
   LOADW
   LDCCH 'A'
   LDCCH 'B'
   LDCCH 'C'
   CALL _move
   RET 0
_move:
   LDLADDR -10
   LOADW
   LDCINT 0
   BLE L2
   LDLADDR -10
   LOADW
   LDCINT 1
   SUB
   LDLADDR -6
   LOAD2B
   LDLADDR -2
   LOAD2B
   LDLADDR -4
   LOAD2B
   CALL _move
   LDCSTR "Move a disk from "
   PUTSTR
   LDLADDR -6
   LOAD2B
   PUTCH
   LDCSTR " to "
   PUTSTR
   LDLADDR -4
   LOAD2B
   PUTCH
   PUTEOL
   LDLADDR -10
   LOADW
   LDCINT 1
   SUB
   LDLADDR -2
   LOAD2B
   LDLADDR -4
   LOAD2B
   LDLADDR -6
   LOAD2B
   CALL _move
L2:
   RET 10
