#!/bin/sh                                                                                           
function print_usage(){                                                                             
  echo "Usage: deploy   COMMAND"                                                                    
  echo "   where  COMMAND is one of:"                                                               
  echo "  -d     will update the project and restart dcscheduler  "
  echo "  -b     update and build the project  "                                                    
  echo "  -u     update the project from svn "                                                      
  echo "  -r     roolback the pre-build   "                                                         
  echo "  -c     replace the config file "                                                          
  echo "  -s     stop"                                                                                           
}                                                                                                   
function rollbackf(){                                                                               
  echo "rollback"                                                                                   
  rm -rf /usr/local/wonhigh/dc/dc-scheduler/                                                    
  mv /usr/local/wonhigh/dc/dc-scheduler.back  /usr/local/wonhigh/dc/dc-scheduler             
}                                                                                                   
function bulid(){                                                                                   
  echo "=================build start ==================================="                           
  mvn clean compile package -Dmaven.test.skip=true                                                  
  echo "=================build end ==================================="                             
}                                                                                                   
function update(){                                                                                  
  echo "=================update start ==================================="                          
  svn update                                                                                        
  echo "=================update start ==================================="                          
}   

function stop(){
pid=`ps -ef |grep scheduler|grep 4100|awk '{print $2}'`
kill -9 $pid
}

                                                                                                
function deploy(){                                                                                  
   update                                                                                           
   bulid                                                                                            
  #dcclient -stop                                                                                   
  fzip=`ls ./dc-scheduler-web/target/dc-scheduler-web-*.zip`                                             
  if [ -f $fzip ]                                                                                   
   then                                                                                             
    #stop scheduler                                                                                  
    stop                                                                                
    #backup dc-scheduler                                                                               
    rm -rf /usr/local/wonhigh/dc/dc-scheduler.back                                             
    mv /usr/local/wonhigh/dc/dc-scheduler   /usr/local/wonhigh/dc/dc-scheduler.back     
    unzip $fzip dc-scheduler/* -d /usr/local/wonhigh/dc                                   
    #start dc-client                                                                                
    echo "==========================start the dcscheduler ========================================"    
    /usr/local/tomcat-dc-scheduler-4100/bin/startup.sh                                                                                
                                                                           
    jps                                                                                             
   else                                                                                             
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!build project filed ==================="                        
    exit                                                                                            
  fi                                                                                                
}                                                                                                   
                                                                                                    
                                                                                                    
if [ $# = 0 ]                                                                                       
 then                                                                                               
 print_usage                                                                                        
 exit                                                                                               
fi                                                                                                  
COMMAND=$1                                                                                          
                                                                                                    
                                                                                                    
case $COMMAND in                                                                                    
  -h)                                                                                               
  print_usage                                                                                       
  ;;                                                                                                
  -d)                                                                                               
  deploy                                                                                            
  ;;                                                                                                
  roolback|-r)                                                                                      
  rollbackf                                                                                         
  ;;                                                                                                
  -b)                                                                                               
  bulid                                                                                             
  ;;                                                                                                
   -u)                                                                                              
  update                                                                                            
  ;;                                                                                                
   -b)                                                                                              
  bulid                                                                                             
  ;; 
     -s)                                                                                              
  stop                                                                                             
  ;;                                                                                                
esac                                                                                                