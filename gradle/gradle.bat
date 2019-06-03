set project_path=%1
set task=%2

start /wait /b cmd.exe /c ".\content\%project_path%\gradlew.bat -b .\content\%project_path%\build.gradle %task%" > NUL 2> NUL

exit 0
