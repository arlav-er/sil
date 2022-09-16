#!/bin/bash 
#/usr/local/bin/YG_ADESIONE_mockservice.sh 
#


function d_start ( ) 
{ 
	echo  "YG-ADESIONE mockservice: starting service (port:18081)" 
	/home/sil/SoapUI-5.3.0/bin/mockservicerunner.sh -Djava.awt.headless=true -m "YGservizicoapWSSoapBinding MockService" "/home/sil/soapuiprojects/TEST-YG-ADESIONE-soapui-project.xml" -p 18081 >> /home/sil/soapuilogs/YG_ADESIONE_mockservice.log 2>&1 &
	echo  "YG-ADESIONE mockservice: started" 
}
 
function d_stop ( ) 
{ 
	echo  "YG-ADESIONE mockservice: stopping service" 
	ps -ef | grep TEST-YG-ADESIONE-soapui-project | grep -v grep | awk '{print $2}' | xargs kill 
        echo  "YG-ADESIONE mockservice: service stopped" 
 }
 
function d_status ( ) 
{ 
	echo  "YG-ADESIONE mockservice: checking for process PID"
        PID=`ps -ef | grep TEST-YG-ADESIONE-soapui-project | grep -v grep | awk '{print $2}'` 
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

