<table  class="main">
	<tr>
		<td colspan="3">
			<table class='sezione2' cellspacing=0 cellpadding=0 >
	        	<tr>
	            	<td  width=18><img id='imgColl' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("SezColl"))'></td>
	                <td  class='titolo_sezione'>Colloquio</td>    				
	                <td align='right' width='30'></td>
	            </tr>
	        </table>
	    </td>
	</tr>
	<tr>
		<td colspan="3">
			<table class="main" id='SezColl' style="display:'' " >  
        		<script>initSezioni(new Sezione(document.getElementById('SezColl'),document.getElementById('imgColl'),false));</script>				
				<tr>
				    <td class="colonna1">Data Colloquio</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="datColloquio" value="<%=datColloquio1%>" size="15" maxlength="15" readonly="true"/></td>    
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="datColloquio" value="<%=datColloquio2%>" size="15" maxlength="15" readonly="true"/></td>    
				</tr>
				<tr>
				    <td class="colonna1">Data Chiusura</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="datchiusura" value="<%=datchiusura1%>" size="15" maxlength="15" readonly="true"/></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="datchiusura" value="<%=datchiusura2%>" size="15" maxlength="15" readonly="true"/></td>
				</tr>
				<tr>
				    <td class="colonna1">Motivo chiusura</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="motChiusura" value="<%=motChiusura1%>" size="40" maxlength="80" readonly="true"/></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="motChiusura" value="<%=motChiusura2%>" size="40" maxlength="80" readonly="true"/></td>
				</tr>
				<tr>
    				<td class="colonna1">Note</td>
    				<td class="colonna2"><af:textArea classNameBase="textarea"  cols="30" rows="4" name="strNote" value="<%=strNote1%>" maxlength="1000"  readonly="true"/></td>
    				<td class="colonna2"><af:textArea classNameBase="textarea"  cols="30" rows="4" name="strNote" value="<%=strNote2%>" maxlength="1000"  readonly="true"/></td>    
				</tr>
			</table>
		</td>
	</tr>
</table>