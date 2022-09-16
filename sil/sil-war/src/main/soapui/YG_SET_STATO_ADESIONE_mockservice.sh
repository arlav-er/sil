#!/bin/bash 
#/usr/local/bin/YG_SET_STATO_ADESIONE_mockservice.sh
#


function d_start ( ) 
{ 
	echo  "YG-SET-STATO-ADESIONE mockservice: starting service (port:18083)" 
	/home/sil/SoapUI-5.3.0/bin/mockservicerunner.sh -Djava.awt.headless=true -m "servizicoapWSSoapBinding MockService" "/home/sil/soapuiprojects/TEST-YG-SET-STATO-ADESIONE-soapui-project.xml" -p 18083 >> /home/sil/soapuilogs/YG_SET_STATO_ADESIONE_mockservice.log 2>&1 &
	echo  "YG-SET-STATO-ADESIONE mockservice: started" 
}
 
function d_stop ( ) 
{ 
	echo  "YG-SET-STATO-ADESIONE mockservice: stopping service" 
	ps -ef | grep TEST-YG-SET-STATO-ADESIONE-soapui-project | grep -v grep | awk '{print $2}' | xargs kill 
        echo  "YG-SET-STATO-ADESIONE mockservice: service stopped" 
 }
 
function d_status ( ) 
{ 
	echo  "YG-SET-STATO-ADESIONE mockservice: checking for process PID"
        PID=`ps -ef | grep TEST-YG-SET-STATO-ADESIONE-soapui-project | grep -v grep | awk '{print $2}'` 
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

