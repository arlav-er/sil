<table  class="main">
<!-- GESTIONE CIG -->
	<tr>
		<td colspan="3">
			<table class='sezione2' cellspacing=0 cellpadding=0 >
	            <tr>
	                <td  width=18><img id='IMG_S_GEST_NEET' src='../../img/chiuso.gif' onclick='cambia(this, document.getElementById("S_GEST_NEET"))'></td>
	                <td  class='titolo_sezione'>DICHIARAZIONI NEET</td>    				
	                <td align='right' width='30'></td>
	            </tr>
	        </table>
	    </td>
	</tr>             
	<tr>
		<td colspan="3">
			<TABLE class="main" id='S_GEST_NEET' style='display:none' >  
        		<script>initSezioni(new Sezione(document.getElementById('S_GEST_NEET'),document.getElementById('IMG_S_GEST_NEET'),false));</script>				
				<tr>
				               
				    <td valign="top"><af:list moduleName="M_DichNeetAccorpaLav1"  skipNavigationButton="1" /></td>    
                    <td class="colonna1"></td> 
                    <td valign="top"><af:list moduleName="M_DichNeetAccorpaLav2"  skipNavigationButton="1" /></td>    
                    
				</tr>			           	
			</table>
		</td>
	</tr>

</table>