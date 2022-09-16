  <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

  <html lang="ita">
  <head>
    <title>Login</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/" />
    <script language="Javascript">

    var hWindow = null;
    
    function  apriLookupErrAut(msg, msgError){
        var f,t,feat;
        f="../../servlet/fv/AdapterHTTP?PAGE=loginNOPage&msg=" + msg+"&msgError="+msgError;
        t="AppSil";
        feat="width=200,height=250,screenX=0,screenY=2,left=300,top=200,resizable=no,toolbar=no,menubar=no,titlebar=no,alwaysRaised=yes,status=no";
        hWindow=open(f,t,feat);
        return(false);
    }

      function  apriAppl(page){
          var f,t,feat, h,l;
          f="../../servlet/fv/AdapterHTTP?PAGE=" + page;
          t="AppSil";
          h=screen.availHeight-50;
          l=screen.availWidth-10;
          
          feat="width=" + l + ",height=" + h + ",screenX=0,screenY=0,left=0,top=0,resizable=yes,toolbar=no,menubar=no,titlebar=no,alwaysRaised=yes,status=yes";
          hWindow=open(f,t,feat);
          hWindow.focus();
          return(false);
      }

      function ctrlWindow(){
        if ( (hWindow!=null) && (!hWindow.closed)){
          if (confirm("Questa operazione comporta l'uscita dalla sessione precedente.\r\n\Continuare? ")){
            hWindow.close();
            return true;
          } else{
           return false;
          }
        }
        return true;
      }
      function  apriLoginConvenzione(){
          var f,t,feat, h,l;
          f="../../jsp/login/homeConvenzione.jsp";
          t="_top";
          feat="false";
          hWindow=open(f,t,feat);
          hWindow.focus();
          return(false);
      }      	   

    </script>
  </head>  

  <body class="menu" onLoad="<%=onLoadJs%>">
  <af:form name="form" action="../../servlet/fv/AdapterHTTP" method="POST" onSubmit="ctrlWindow()">
    <af:textBox name="PAGE" type="hidden" value="eseguiLoginPage" /> 

    <table align="center" width="100%" height="50%" bgcolor="#000080" cellspacing=0 cellpadding=0 border=0 class="def">
    
    <tr>
      <td colspan="6" class="autenticazione">
		&nbsp;
      </td>	
    </tr>
    
    <tr><td class="autenticazione">Login</td>
      <td class="autenticazione">
      <af:textBox size="10" title="Login" maxlength="18" name="USERNAME" value="<%=username%>" 
        required="true" />
      </td>
      
		<td class="autenticazione">Password</td>
      <td class="autenticazione">
      <af:textBox  type="password" title="Password" size="10" maxlength="20" name="PASSWORD" value="" required="true" />
      </td>
      <td class="autenticazione" colspan="2">
	      &nbsp;&nbsp;<input type="image" src="../../img/hp/enter.gif" border="0" value="ENTRA">
      </td>
    </tr>
	<tr>
      <td colspan="6" class="autenticazione">
      	<% if (checkConvenzione) { %>
			<a href="#" onclick="apriLoginConvenzione()" style="text-decoration: none;">Accesso utenti convenzionati</a>
		<% } %>
      </td>
	</tr>
	<tr>
      <td colspan="6" class="autenticazione">
		&nbsp;
      </td>
	</tr> 
    </table>

  </td></tr>

  </table>

  </af:form>
 
          
  </body>
  </html>

