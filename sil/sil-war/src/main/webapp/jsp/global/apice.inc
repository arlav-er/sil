<%
class Deapostrofa{
 	public String esegui(String str){
    String ret=new String();

 		for(int i = 0; i < str.length(); i++){
 		  if(str.charAt(i)=='\''){
        ret = ret + "''";
 		  }else{
 		    ret=ret + str.charAt(i);
 		  }
 		}
    return ret;
 	}
}
%>