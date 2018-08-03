       identification division.
       program-id. sub-cob.
       environment division.
       configuration section.
       data division.
       working-storage section.,

           01 temp-string pic x(20) based.
           01 pchar usage pointer.
           01 output-string pic x(20).

       linkage section.

           01 string-from-java pic x(20).
           01 cobol-string pic x(22).

       procedure division.

           sub-cob section.
           goback.

           entry "proc-cob" using
           by reference string-from-java
           by reference cobol-string.


           set pchar to address of string-from-java.

           set address of temp-string to pchar.
           string temp-string delimited by x"00" into output-string.

           move FUNCTION UPPER-CASE(string-from-java) to cobol-string.
           string cobol-string delimited by x"00" into cobol-string.
           goback.