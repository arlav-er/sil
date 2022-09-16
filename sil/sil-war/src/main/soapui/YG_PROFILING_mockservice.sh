#!/bin/bash 
#/usr/local/bin/YG_PROFILING_mockservice.sh
#


function d_start ( ) 
{ 
	echo  "YG-PROFILING mockservice: starting service (port:18084)" 
	/home/rvitaliano-cons/soapui_stubs/SoapUI-5.3.0/bin/mockservicerunner.sh -Djava.awt.headless=true -m "YG_Profiling_Binding MockService" "/home/rvitaliano-cons/soapui_stubs/soapui_projects/TEST-YG-PROFILING-soapui-project.xml" -p 18084 >> /home/rvitaliano-cons/soapui_stubs/soapui_logs/YG_PROFILING_mockservice.log 2>&1 &
	echo  "YG-PROFILING mockservice: started" 
}
 
function d_stop ( ) 
{ 
	echo  "YG-PROFILING mockservice: stopping service" 
	ps -ef | grep TEST-YG-PROFILING-soapui-project | grep -v grep | awk '{print $2}' | xargs kill 
        echo  "YG-PROFILING mockservice: service stopped" 
 }
 
function d_status ( ) 
{ 
	echo  "YG-PROFILING mockservice: checking for process PID"
        PID=`ps -ef | grep TEST-YG-PROFILING-soapui-project | grep -v grep | awk '{print $2}'` 
        if [[ "" !=  "$PID" ]]; then
           echo "Process PID is"
           echo "$PID"
        else
           echo "Process is not running"
        fi  
}
 

 
# Management instructions of the service 
case  $1  in 
	"start")
		d_start
		;; 
	"stop")
		d_stop
		;; 
	"reload")
		d_stop
		sleep  1
		d_start
		;; 
	"status")
		d_status
		;; 
	* ) 
	echo  "Usage: $0 {start | stop | reload | status}" 
	exit  1 
	;; 
esac
 
exit  0

