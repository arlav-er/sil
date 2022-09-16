<%
  String codiceMinSAP1 = "";
  String codiceMinSAP2 = "";
  String dtInvioMinSAP1 = "";
  String dtInvioMinSAP2 = "";
  String statoMinSAP1 = "";
  String statoMinSAP2 = "";
  String dtInizioStatoMin1 = "";
  String dtInizioStatoMin2 = "";
  
  Vector vectInfoSAPLav1 = serviceResponse.getAttributeAsVector("M_SAPAccorpaLav1.ROWS.ROW");
  Vector vectInfoSAPLav2 = serviceResponse.getAttributeAsVector("M_SAPAccorpaLav2.ROWS.ROW");
  
  if ((vectInfoSAPLav1 != null) && (vectInfoSAPLav1.size() > 0)) {
  	SourceBean infoSAPLav1 = (SourceBean) vectInfoSAPLav1.get(0);
  	codiceMinSAP1 = SourceBeanUtils.getAttrStrNotNull(infoSAPLav1,"CODMINSAP");
  	dtInvioMinSAP1 = SourceBeanUtils.getAttrStrNotNull(infoSAPLav1,"DATAINVIOMIN");
  	statoMinSAP1 = SourceBeanUtils.getAttrStrNotNull(infoSAPLav1,"STRDESCRIZIONE");
  	dtInizioStatoMin1 = SourceBeanUtils.getAttrStrNotNull(infoSAPLav1,"DATINIZIOVAL");
  }
  
  if ((vectInfoSAPLav2 != null) && (vectInfoSAPLav2.size() > 0)) {
  	SourceBean infoSAPLav2 = (SourceBean) vectInfoSAPLav2.get(0);
  	codiceMinSAP2 = SourceBeanUtils.getAttrStrNotNull(infoSAPLav2,"CODMINSAP");
  	dtInvioMinSAP2 = SourceBeanUtils.getAttrStrNotNull(infoSAPLav2,"DATAINVIOMIN");
  	statoMinSAP2 = SourceBeanUtils.getAttrStrNotNull(infoSAPLav2,"STRDESCRIZIONE");
  	dtInizioStatoMin2 = SourceBeanUtils.getAttrStrNotNull(infoSAPLav2,"DATINIZIOVAL");
  }
  
%>
<table  class="main">
<!-- GESTIONE SAP -->
	<tr>
		<td colspan="3">
			<table class='sezione2' cellspacing=0 cellpadding=0 >
	            <tr>
	                <td  width=18><img id='IMG_S_GEST_SAP' src='../../img/chiuso.gif' onclick='cambia(this, document.getElementById("S_GEST_SAP"))'></td>
	                <td  class='titolo_sezione'>SAP</td>    				
	                <td align='right' width='30'></td>
	            </tr>
	        </table>
	    </td>
	</tr>             
	<tr>
		<td colspan="3">
			<TABLE class="main" id='S_GEST_SAP' style='display:none' >  
        		<script>initSezioni(new Sezione(document.getElementById('S_GEST_SAP'),document.getElementById('IMG_S_GEST_SAP'),false));</script>				
				<tr>
				    <td class="colonna1">Codice SAP Ministeriale</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="codiceMinSAP1" value="<%=codiceMinSAP1%>" size="40" readonly="<%= String.valueOf(!canModify) %>" /><br/></td>    
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="codiceMinSAP2" value="<%=codiceMinSAP2%>" size="40" readonly="<%= String.valueOf(!canModify) %>" /><br/></td>    
				</tr>
				<tr>
				    <td class="colonna1">Data Invio SAP al Ministero</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="dtInvioMinSAP1" value="<%=dtInvioMinSAP1%>" size="40" readonly="<%= String.valueOf(!canModify) %>" /></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="dtInvioMinSAP2" value="<%=dtInvioMinSAP2%>" size="40" readonly="<%= String.valueOf(!canModify) %>" /></td>
				</tr>	
				<tr>
				    <td class="colonna1">Stato</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="statoMinSAP1" value="<%=statoMinSAP1%>" size="40" readonly="<%= String.valueOf(!canModify) %>" /></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="statoMinSAP2" value="<%=statoMinSAP2%>" size="40" readonly="<%= String.valueOf(!canModify) %>" /></td>
				</tr>
				<tr>
				    <td class="colonna1">Data inizio Stato</td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="dtInizioStatoMin1" value="<%=dtInizioStatoMin1%>" size="40" readonly="<%= String.valueOf(!canModify) %>" /></td>
				    <td class="colonna2"><af:textBox classNameBase="input"  type="text" name="dtInizioStatoMin2" value="<%=dtInizioStatoMin2%>" size="40" readonly="<%= String.valueOf(!canModify) %>" /></td>
				</tr>		           	
			</table>
		</td>
	</tr>

</table>