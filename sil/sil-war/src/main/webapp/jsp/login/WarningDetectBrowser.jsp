<%@ page contentType="text/html;charset=utf-8"%>
<%@ page session="false"%>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*,
                it.eng.sil.security.User" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<html lang="ita">
<head>
	<title>SIL - Sistema Informativo del Lavoro</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<script language="JavaScript" src="../../js/browser_detect.js"></script>

<script language="JavaScript">
	
			var okBrowser=true;
			var okDim = true;
			var browserDesc="";
			var browserMsg="";
					
			okBrowser= 	is_ie6up   ||
	    				is_nav7up ||
			        	is_fx || 
			        	is_moz;
			      
		
			if(screen.width<1024||screen.height<768){
				okDim= false;
			}


		if (!okBrowser){
			if (is_ie){
				// famiglia IE
				if (is_ie5_5){
					browserDesc="Internet Explorer 5.5"
				}else if (is_ie5){
					browserDesc="Internet Explorer"
				}else{
					browserDesc="Internet Explorer ver. 4 o precedente"
				}
				
				browserMsg="<tr><td>La versione del <b>browser</b> rilevata<br>(" + browserDesc + ")<br> " +
							"non è abbastanza recente per garantire un completo funzionamento dell'applicazione.</td></tr>";

				browserMsg += "<tr><td>Si consiglia di aggiornare il browser <br>ad una versione piu' recente<br>(almeno la versione 6.0)</td></tr>";
				
				
			}else if (is_nav){
				// famiglia netscape
				browserDesc="Netscape"

				browserMsg="<tr><td>La versione del <b>browser</b> rilevata<br>(" + browserDesc + ")<br> " +
							"non è abbastanza recente per garantire un completo funzionamento dell'applicazione.</td></tr>";

				browserMsg += "<tr><td>Si consiglia di aggiornare il browser <br>ad una versione piu' recente</td></tr>";


			}else if (is_opera){
				// famiglia netscape
				browserDesc="Opera"

				browserMsg="<tr><td>La versione del <b>browser rilevato</b><br>(" + browserDesc + ")<br> " +
							"non rientra fra quelli supportati.</td></tr>";

				browserMsg += "<tr><td>Si consiglia di utilizzare uno dei browser supportati (Internet Explorer 6+, Mozilla 1.7+ o FireFox 1.0+</td></tr>";



			}
			else{ 
				browserDesc=agt; 
				browserMsg="<tr><td>La versione del <b>browser rilevato</b><br>(" + browserDesc + ")<br> " +
							"non rientra fra quelli supportati.</td></tr>";

				browserMsg += "<tr><td>Si consiglia di utilizzare uno dei browser supportati (Internet Explorer 6+, Mozilla 1.7+ o FireFox 1.0+</td></tr>";

			}
		} // end browser	
	
	
	
	if (!okDim){
	
			dimMsg="<tr><td>La <b>risoluzione video</b> impostata (" +screen.width + " x " + screen.height + ") " +
							"non è adeguata per l'uso ottimale dell'applicazione.</td></tr>";

			dimMsg+="<tr><td>Si consiglia di aumentare la risoluzione video ad almeno 1024 x 768 pixel.</td></tr>";
	
	}
	
	

	
	</script>

</head>

<body class="gestione">
<p>&nbsp;</p>

<p class="titolo">Avviso</p>

<table class="main">
	<tbody>
		<tr>
			<td><img border="0" src="../../img/warning_bd.gif"></td>
			<td></td>
			<td>
			<table class="main">
				<tbody>
					<tr>
						<td><script>
								if (!okBrowser){
									document.write(browserMsg);
								}
							</script></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>



					<tr>
						<td><script>
								if (!okDim){
									document.write(dimMsg);
								}
							</script></td>
					</tr>



				</tbody>
			</table>
			</td>
		</tr>


		<tr>
			<td colspan="3"></td>
		</tr>
		<tr>
			<td colspan="3"><input class="pulsante" type="button" name="Chiudi"
				value="Chiudi" onclick="window.close();"></td>
		</tr>
	</tbody>
</table>
</body>
</html>
