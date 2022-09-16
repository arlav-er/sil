<table id="sezione_formazione" width="100%" border=0>

    <tr>
        <td colspan="4">    
            <table class='sezione' cellspacing=0 cellpadding=0>
				<tr>					
					<td  width=18>
                   	<img id='I_SEL_S_VOUCHER' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("T_S_VOUCHER"))'>
                   	</td>					
					<td class="sezione_titolo">Voucher</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr>
    	<td colspan=4 align="center">
        	<table id="T_S_VOUCHER" style="width:100%;">
        		<tr>
	        		<td class="etichetta" nowrap>Data Assegnazione&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data Assegnazione" classNameBase="input" readonly="true" type="text"
			                    name="dtmDataAssVoucher" required="false" value="<%=dataAssegnazioneVCH%>" size="12"/>
					</td>
					<td class="etichetta" nowrap>Utente Assegnazione&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Utente Assegnazione" classNameBase="input" readonly="true" type="text"
			                    name="utenteVoucher" required="false" value="<%=utenteAssVoucher%>" size="30"/>
					</td>
					<td class="etichetta" nowrap>Codice di attivazione&nbsp;</td>        
	        		<td class="campo">
	        			<af:textBox title="Codice di attivazione" classNameBase="input" readonly="true" type="text"
			                    name="numAttVoucher" required="false" value="<%=codAttivazione%>" size="21"/>
					</td>
				</tr>
        		<tr>
	        		<td class="etichetta" nowrap>Data Attivazione&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data Attivazione" classNameBase="input" readonly="true" type="text"
			                    name="dtmDataAttVoucher" required="false" value="<%=dataAttivazioneVCH%>" size="12"/>
					</td>
					<td class="etichetta" nowrap>Utente Attivazione&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Utente Attivazione" classNameBase="input" readonly="true" type="text"
			                    name="utenteVoucher" required="false" value="<%=utenteAttVoucher%>" size="30"/>
					</td>
					<td class="etichetta" nowrap>Stato&nbsp;</td>        
	        		<td class="campo">
	        			<af:textBox title="Stato voucher" classNameBase="input" readonly="true" type="text"
			                    name="descStatoVoucher" required="false" value="<%=descStatoVoucher%>" size="10"/>
					</td>
				</tr>
				<tr>
	        		<td class="etichetta" nowrap>Cf ente&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Cf ente" classNameBase="input" readonly="true" type="text"
			                    name="cfEnteAttVch" required="false" value="<%=strcfenteaccreditato%>" size="12"/>
					</td>
					<td class="etichetta" nowrap>Codice sede&nbsp;</td>
					<td class=campo>
	        			 <af:textBox title="Codice sede" classNameBase="input" readonly="true" type="text"
			                    name="codSedeEnteAttVch" required="false" value="<%=codsede%>" size="12"/>
					</td>
					<td colspan="2">&nbsp;
					</td>
				</tr>
				<tr>
					<td class="etichetta" nowrap>Sede&nbsp;</td>        
	        		<td colspan="5" class=campo>
	        			 <af:textBox title="Sede" classNameBase="input" readonly="true" type="text"
			                    name="sedeEnteAttVch" required="false" value="<%=sedeEnteAccreditato%>" size="100"/>
					</td>
				</tr>
				<tr>
	        		<td class="etichetta" nowrap>Data massima attivazione&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data massima attivazione" classNameBase="input" readonly="true" type="text"
			                    name="dtmDataMaxAttVoucher" required="false" value="<%=datMaxAttivazione%>" size="12"/>
					</td>
					<td class="etichetta" nowrap>Data massima erogazione&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data massima erogazione" classNameBase="input" readonly="true" type="text"
			                    name="dtmDataMaxChiVoucher" required="false" value="<%=datMaxErogazione%>" size="12"/>
					</td>
					<td colspan="2">&nbsp;
					</td>
				</tr>
				<tr>
	        		<td class="etichetta" nowrap>Data Chiusura&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Data Chiusura" classNameBase="input" readonly="true" type="text"
			                    name="dtmDataChiVoucher" required="false" value="<%=dataChiusuraVCH%>" size="12"/>
					</td>
					<td class="etichetta" nowrap>Utente Chiusura&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="Utente Chiusura" classNameBase="input" readonly="true" type="text"
			                    name="utenteVoucher" required="false" value="<%=utenteChiVoucher%>" size="30"/>
					</td>
					<td class="etichetta" nowrap>Valore Massimo EURO&nbsp;</td>        
	        		<td class="campo">
	        			<af:textBox title="Valore Massimo EURO" classNameBase="input" readonly="true" type="number"
			                    name="decValMassimo" required="false" value="<%=strdecValTotaleVch%>" size="10"/>
					</td>
				</tr>
				<%if (codStatoVoucher.equalsIgnoreCase(StatoEnum.CONCLUSO.getCodice())) {%>
					<tr>
						<td class="etichetta" colspan=4>&nbsp;</td>
						<td class="etichetta" nowrap>Speso EURO&nbsp;</td>        
		        		<td class="campo">
		        			<af:textBox title="Speso EURO" classNameBase="input" readonly="true" type="number"
				                    name="decValSpeso" required="false" value="<%=strdecValSpesoVch%>" size="10"/>
						</td>
		        	</tr>
		        <%}%>
				<tr>
	        		<td class="etichetta" nowrap>TDA soggetti in CM o svantaggiati&nbsp;</td>        
	        		<td class=campo>
	        			 <af:textBox title="TDA soggetti in CM o svantaggiati" classNameBase="input" readonly="true" type="text"
			                    name="flagCmVch" required="false" value="<%=strFlagCM%>" size="12"/>
					</td>
				</tr>
	        </table>
	    </td>
	</tr>
</table>
