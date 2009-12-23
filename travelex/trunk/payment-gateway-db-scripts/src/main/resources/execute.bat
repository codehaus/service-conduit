@echo off
echo COMMAND USAGE - execute.bat username password
sqlplus %1/%2@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=xe))) @batch.sql