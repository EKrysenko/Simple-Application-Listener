identification division.
program-id. sub-cob.
environment division.
configuration section.
data division.
working-storage section.
01 temp-string pic x(20) based.
01 pchar usage pointer.
01 my-string pic x(20).
linkage section.
01 string-from-java pic x(20).
procedure division.
sub-cob section.
goback.
entry "proc-cob" using by reference string-from-java.
display "entry proc-cob".
set pchar to address of string-from-java.
display "pchar set".
set address of temp-string to pchar.
display "temp-string set"
string temp-string delimited by x"00" into my-string.
display "my-string set".
display my-string.
goback.
