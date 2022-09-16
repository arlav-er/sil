
<%
    //Controllo se in sessione ho il CodCpi di destinazione (cdnTipoGruppo dell'utente == 1)
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String codCpiUser = user.getCodRif();
	boolean codCpiUserNotFound = false;
	if(cdnTipoGruppo != 1) {
		codCpiUserNotFound = true;
	}   
%>


<table  class="main">
<!-- GESTIONE CIG -->
	<tr>
		<td colspan="3">
			<table class='sezione2' cellspacing=0 cellpadding=0 >
	            <tr>
	                <td  width=18><img id='IMG_S_GEST_CIG' src='../../img/chiuso.gif' onclick='cambia(this, document.getElementById("S_GEST_CIG"))'></td>
	                <td  class='titolo_sezione'>Altre iscrizioni</td>    				
	                <td align='right' width='30'></td>
	            </tr>
	        </table>
	    </td>
	</tr>             
	<tr>
		<td colspan="3">
			<TABLE class="main" id='S_GEST_CIG' style='display:none' >  
        		<script>initSezioni(new Sezione(document.getElementById('S_GEST_CIG'),document.getElementById('IMG_S_GEST_CIG'),false));</script>				
				<tr>
				    <td valign="top"><af:list moduleName="M_LIST_GEST_CIG_LAV1" skipNavigationButton="1" /></td>    
				     <td class="colonna1"></td>   
				    <td valign="top"><af:list moduleName="M_LIST_GEST_CIG_LAV2" skipNavigationButton="1" /></td>    
				</tr>			           	
			</table>
		</td>
	</tr>

</table>

