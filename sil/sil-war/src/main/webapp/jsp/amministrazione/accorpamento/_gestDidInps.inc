
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
	                <td  width=18><img id='IMG_S_GEST_DIDINPS' src='../../img/chiuso.gif' onclick='cambia(this, document.getElementById("S_GEST_DIDINPS"))'></td>
	                <td  class='titolo_sezione'>DID INPS</td>    				
	                <td align='right' width='30'></td>
	            </tr>
	        </table>
	    </td>
	</tr>             
	<tr>
		<td colspan="3">
			<TABLE class="main" id='S_GEST_DIDINPS' style='display:none' >  
        		<script>initSezioni(new Sezione(document.getElementById('S_GEST_DIDINPS'),document.getElementById('IMG_S_GEST_DIDINPS'),false));</script>				
				<tr>
				               
				    <td valign="top"><af:list moduleName="M_DidInpsAccorpaLav1"  skipNavigationButton="1" /></td>    
                    <td class="colonna1"></td> 
                    <td valign="top"><af:list moduleName="M_DidInpsAccorpaLav2"  skipNavigationButton="1" /></td>    
                    
				</tr>			           	
			</table>
		</td>
	</tr>
	<af:textBox type="hidden" name="cdnLavoratore" value="" />

</table>

