@echo off

rem -- Lipsion
color 1f
:menu
echo   ________________________________________________________________
echo  ^|                                                                ^|
echo  ^|                     Maven  -  �������                         ^|
echo  ^|                                                                ^|
echo  ^|  0 - eclipse:clean clean       1 - clean package -D...skip=true^|
echo  ^|  2 - eclipse:e.. -Ddown..      3 - package -D...skip=true      ^|
echo  ^|      -Dwtp..                                                   ^|
echo  ^|  4 - ���벿���                5 - mvn jetty:run               ^|
echo  ^|  6 - mvn deploy                7 - mvn install                 ^|
echo  ^|  8 - �޸�maven���̰汾��                                       ^|
echo  ^|________________________________________________________________^|
:input
set /p input=-^> ��ѡ��: 

if "%input%"== "0" goto clean
if "%input%"== "1" goto clean-package
if "%input%"== "2" goto eclipse
if "%input%"== "3" goto package
if "%input%"== "4" goto deploy-zip
if "%input%"== "5" goto jetty-run
if "%input%"== "6" goto deploy
if "%input%"== "7" goto install
if "%input%"== "8" goto change-version
goto end

:clean
echo  # �������̱��� #
mvn eclipse:clean clean&&pause&&cls

:clean-package
echo  # �������벢��� #
mvn clean package -U -Dmaven.test.skip=true &&pause&&cls

:eclipse
echo  # ת��Eclipse���� #
mvn eclipse:clean eclipse:eclipse -DdownloadSources=true &&pause&&cls

:package
echo  # ��� #
mvn package -U -Dmaven.test.skip=true &&pause&&cls

:change-version
echo  #  ��������Ҫ�����İ汾 #
set /p newVersion=
echo # ���̰汾%newVersion% #
call mvn clean versions:set -DnewVersion=%newVersion%
echo # �����Ƿ�������ģ�鶼�����汾�ɹ� �°汾Ϊ%newVersion% #
echo # ��ʼɾ��pom.xml.versionsBackup�ļ� #
del  /s  pom.xml.versionsBackup
pause&&cls

:deploy-zip
cls && call deploy.bat
:jetty-run
rem start mvn jetty:run
for /d %%d in (*) do (
  rem if exist %%d\POM.xml set web_dir=%%d
  if %%d:~-5%=="web" goto run-mvn-jetty
)

:run-mvn-jetty
cd %%d&start mvn jetty:run

:deploy
echo  # ���� #
mvn deploy -Dmaven.test.skip=true &&pause&&cls

:install
echo  # ��װ���زֿ� #
mvn install -Dmaven.test.skip=true &&pause&&cls

rem for /d %%d in (*) do (
rem  if exist %%d\POM.xml set dao_dir=%%d
rem )
rem echo ��ǰĿ¼��:%dao_dir%
rem &&pause 
:end
echo ����
prompt
popd