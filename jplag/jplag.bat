if not exist ".\module_data\jplag" mkdir ".\module_data\jplag"

set module_dir=%1
set run_dir=%2
set language=%3
set exclude=%4

start /wait java -jar %module_dir%/jplag-2.11.9-SNAPSHOT-jar-with-dependencies.jar ./content -s -S content/%run_dir% -x %exclude% -l %language% -o ./module_data/jplag/parser_log.txt -r ./module_data/jplag/results

exit
