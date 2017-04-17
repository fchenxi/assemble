@echo off

rem -- Lipsion
color 1f
:menu
echo   ________________________________________________________________
echo  ^|                                                                ^|
echo  ^|                     Maven  -  控制面板                         ^|
echo  ^|                                                                ^|
echo  ^|  0 - eclipse:clean clean       1 - clean package -D...skip=true^|
echo  ^|  2 - eclipse:e.. -Ddown..      3 - package -D...skip=true      ^|
echo  ^|      -Dwtp..                                                   ^|
echo  ^|  4 - 编译部署包                5 - mvn jetty:run               ^|
echo  ^|  6 - mvn deploy                7 - mvn install                 ^|
echo  ^|  8 - 修改maven工程版本号                                       ^|
echo  ^|________________________________________________________________^|
:input
set /p input=-^> 请选择: 

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
echo  # 消除工程编译 #
mvn eclipse:clean clean&&pause&&cls

:clean-package
echo  # 消除编译并打包 #
mvn clean package -U -Dmaven.test.skip=true &&pause&&cls

:eclipse
echo  # 转成Eclipse工程 #
mvn eclipse:clean eclipse:eclipse -DdownloadSources=true &&pause&&cls

:package
echo  # 打包 #
mvn package -U -Dmaven.test.skip=true &&pause&&cls

:change-version
echo  #  请输入需要升级的版本 #
set /p newVersion=
echo # 工程版本%newVersion% #
call mvn clean versions:set -DnewVersion=%newVersion%
echo # 请检查是否所有子模块都升级版本成功 新版本为%newVersion% #
echo # 开始删除pom.xml.versionsBackup文件 #
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
echo  # 发布 #
mvn deploy -Dmaven.test.skip=true &&pause&&cls

:install
echo  # 安装本地仓库 #
mvn install -Dmaven.test.skip=true &&pause&&cls

rem for /d %%d in (*) do (
rem  if exist %%d\POM.xml set dao_dir=%%d
rem )
rem echo 当前目录是:%dao_dir%
rem &&pause 
:end
echo 结束
prompt
popd