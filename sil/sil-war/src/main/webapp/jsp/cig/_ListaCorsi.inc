<table  class="main">
	<tr>
		<td colspan="3">
			<table class='sezione2' cellspacing=0 cellpadding=0 >
	            <tr>
	                <td  width=18><img id='IMG_LISTA_CORSI' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("LISTA_CORSI"))'></td>
	                <td  class='titolo_sezione'>Corsi</td>    				
	                <td align='right' width='30'></td>
	            </tr>
	        </table>
	    </td>
	</tr>             
	<tr>
		<td colspan="3">
			<TABLE class="main" id='LISTA_CORSI' style="display:'' " >  
        		<script>initSezioni(new Sezione(document.getElementById('LISTA_CORSI'),document.getElementById('IMG_LISTA_CORSI'),false));</script>				
				<tr>
				    <td class="colonna1"></td>            
				    <td valign="top"><af:list moduleName="M_LIST_CORSI_ISCR1" skipNavigationButton="1" /></td>
				    <td>&nbsp;</td> 
				    <td valign="top"><af:list moduleName="M_LIST_CORSI_ISCR2" skipNavigationButton="1" /></td>    
				</tr>			           	
			</table>
		</td>
	</tr>

</table>

