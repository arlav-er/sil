
<%@page import="it.eng.sil.module.voucher.Properties"%>
<%out.print(htmlStreamTop);%>
<af:form name="frmAzione" action="AdapterHTTP" method="POST" >
	<input type="hidden" name="PAGE" value="TDAMainPage">
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
	<input type="hidden" name="PRGVOUCHER" value="<%=prgVoucher%>"/>
	<input type="hidden" name="CODSTATOVOUCHER" value="<%=codStatoVch%>"/>
	<input type="hidden" name="CFENTE" value="<%=cfEnteAccreditato%>"/>
	<input type="hidden" name="OPERAZIONETDA" value=""/>
	<input type="hidden" name="NUMKLOVOUCHER" value="<%=strNumkloVoucher%>"/>


<p align="center">
	<table class="main">
	<tr>
  		<td class="etichetta">Obiettivo Misura&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="obiettivoAz" title="Obiettivo Misura"
			                        value="<%=descRaggAzione%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="100"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">Servizio/Azione&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="azionedesc" title="Servizio/Azione"
			                        value="<%=descAzione%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="100"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">Soggetto Accreditato attivato&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="cfenteattivato" title="Soggetto Accreditato attivato"
			                        value="<%=cfEnteAccreditato%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="30"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">Sede Soggetto Accreditato attivato&nbsp;</td>
  		<td class="campo">
  		  <af:textBox type="text" name="sedeenteattivato" title="Sede Soggetto Accreditato Attivato"
			                        value="<%=sedeEnteAccreditato%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="100"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">CPI&nbsp;</td>
  		<td class="campo">
  		  <af:textBox type="text" name="cpiCompTit" title="CPI"
			                        value="<%=cpiCompTit%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="100"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta" nowrap>Codice Attivazione&nbsp;</td>
  		<td>
	  		<table class="main" style="width: 50%">
			<tr>
  				<td class="campo">
			  		<af:textBox type="text" name="codattivazione" title="Codice Attivazione"
				                        value="<%=codAttivazione%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="21"/>
				</td>
				<td class="etichetta" nowrap>Stato Titolo&nbsp;</td>
				<td class="campo">
					<af:textBox type="text" name="statotitolo" title="Stato Titolo"
	                        value="<%=descStatoVch%>"
	                        classNameBase="input"
	                        readonly="true"
	                        size="10"/>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	<%if (codStatoVch.equalsIgnoreCase(StatoEnum.ANNULLATO.getCodice())) {%>
		<tr>
  		<td class="etichetta" nowrap>Motivo annullamento&nbsp;</td>
  		<td class="campo">
			<af:textBox type="text" name="descAnnullVch" title="Modivo annullamento"
		                        value="<%=descAnnullVch%>"
		                        classNameBase="input"
		                        readonly="true"
		                        size="30"/>&nbsp;&nbsp;&nbsp;
	     </td>
	</tr>
	<%}%>
	
	<tr>
  		<td class="etichetta" nowrap>Lavoratore&nbsp;</td>
  		<td class="campo">
			<af:textBox type="text" name="cognomelav" title="Lavoratore"
		                        value='<%=nomeLav+" "+cognomeLav%>'
		                        classNameBase="input"
		                        readonly="true"
		                        size="20"/>&nbsp;&nbsp;&nbsp;
	     </td>
	</tr>
	<tr>
  		<td class="etichetta" nowrap>CF&nbsp;</td>
  		<td class="campo">
			  <af:textBox type="text" name="cfLav" title="Codice Fiscale"
				                        value="<%=cfLav%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="20"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Assegnato il&nbsp;</td>
  		<td>
	  		<table class="main" style="width: 50%">
			<tr>
				<td class="campo">
			  		<af:textBox type="date" name="datassegnato" title="Assegnato il"
				                        value="<%=datAssegnazione%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="11" maxlength="10"/>
				</td>
				<td class="etichetta" nowrap>Attivato il&nbsp;</td>
	  			<td class="campo">
			  		<af:textBox type="date" name="dataattivato" title="Attivato il"
				                        value="<%=datAttivazione%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="11" maxlength="10"/>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	<tr>
  		<td class="etichetta" nowrap>Valore massimo EURO&nbsp;</td>
  		<td class="campo">
			  <af:textBox type="text" name="decValMassimo" title="Valore massimo EURO"
				                        value="<%=decValTotaleStr%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="10"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta" nowrap>Concludere entro&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="date" name="datmaxconclusione" title="Concludere entro"
			                        value="<%=datMaxChiusura%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="11" maxlength="10"/>
		</td>
	</tr>
	<%if (codTipoServizio.equalsIgnoreCase(Properties.SERVIZIO_A_RISULTATO)) {%>
	<tr>
  		<td class="etichetta" nowrap>Risultato raggiunto&nbsp;</td>
  		<td>
	  		<table class="main" style="width: 50%">
			<tr> 
  				<td class="campo">
  					<af:comboBox classNameBase="input" name="codVchTipoRisultato" moduleName="M_GetTipoRisultatoTDA" onChange="fieldChanged();"
            			addBlank="true" selectedValue="<%=codVchTipoRisultato%>" title="Risultato raggiunto" disabled="<%=readonly%>" />  
				</td>
			</tr>
			</table>
		</td>
	</tr>
	<%}%>
	<tr>
		<td class="etichetta" nowrap>Concluso il&nbsp;</td>
  		<td>
	  		<table class="main" style="width: 50%">
			<tr>
				<td class="campo" nowrap>
			  		<af:textBox type="date" name="datconclusione" 
			  						title="Data fine erogazione"
			                        value="<%=datChiusura%>"
			                        classNameBase="input"
			                        validateOnPost="true"
			                        readonly="<%=readonly%>"
			                        onKeyUp="fieldChanged()"
			                        size="11" maxlength="10"/>
			 
				</td>
				<td class="etichetta" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;Rimborso Previsto EURO&nbsp;</td>
	  			<td class="campo">
			  		<af:textBox type="text" name="decValSpeso" title="Rimborso Previsto EURO"
			                        value="<%=decValSpesoStr%>" onKeyUp="fieldChanged();"
			                        classNameBase="input"
			                        readonly="true"
			                        size="10"/>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	<%if (codStatoVch.equalsIgnoreCase(StatoEnum.CONCLUSO.getCodice())) {
		if (canAggiornaPagamenti) {%>
			<tr>
				<td class="etichetta" nowrap>Stato Pagamento&nbsp;</td>
		  		<td>
			  		<table class="main" style="width: 50%">
					<tr>
						<td class="campo" nowrap>
					  		<af:comboBox classNameBase="input" name="codVchStatoPagamento" moduleName="M_GetStatiPagamentiVoucher"
	            				addBlank="true" selectedValue="<%=codVchStatoPagamento%>" title="Stato Pagamento" onChange="fieldChanged();"/>
						</td>
						<td class="etichetta" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;Importo Pagato EURO&nbsp;</td>
			  			<td class="campo">
					  		<af:textBox type="number" name="decpagato" title="Importo Pagato EURO"
					                        value="<%=decSpesoPagamentoStr%>" onKeyUp="fieldChanged();"
					                        classNameBase="input"
					                        size="10"/>
						</td>
					</tr>
					</table>
				</td>
			</tr>
		<%} else {%>
			<tr>
				<td class="etichetta" nowrap>Stato Pagamento&nbsp;</td>
		  		<td>
			  		<table class="main" style="width: 50%">
					<tr>
						<td class="campo" nowrap>
					  		<af:comboBox classNameBase="input" name="codVchStatoPagamento" moduleName="M_GetStatiPagamentiVoucher"
	            				addBlank="true" selectedValue="<%=codVchStatoPagamento%>" title="Stato Pagamento" disabled="true"/>
						</td>
						<td class="etichetta" nowrap>&nbsp;&nbsp;&nbsp;&nbsp;Importo Pagato EURO&nbsp;</td>
			  			<td class="campo">
					  		<af:textBox type="number" name="decpagato" title="Importo Pagato EURO"
					                        value="<%=decSpesoPagamentoStr%>"
					                        classNameBase="input" readonly="true"
					                        size="10"/>
						</td>
					</tr>
					</table>
				</td>
			</tr>
		<%}
	}%>
	
	<tr>
		<td class="etichetta" nowrap>IBAN/Numero Fattura&nbsp;</td>
		<td class="campo">
		<af:textBox type="text" name="codiceIBAN" title="IBAN/Numero Fattura" maxlength="27" readonly="<%=String.valueOf(!canGestioneIban)%>"
			value="<%=codiceIBAN%>" onKeyUp="fieldChanged();" classNameBase="input" size="30"/>
		</td>
	</tr>
	
	</table>
	
	<br>
    <table>
	<tr>
	<%if (canGestioneIban) {%>
		<td align="left">
			<input class="pulsante" type = "button" name="BTNAGGIORNAIBAN" value="Aggiorna IBAN" onClick="checkAggiornaIban();"/>
		</td>
	<%}%>
	<%if (canConcludi) {%>
		<td align="left">
			<input class="pulsante" type = "button" name="BTNCONCLUDI" value="Concludi" onClick="checkConcludi();"/>
		</td>
	<%} else {
		if (canRiapriVoucher && codCpiUtenteCollegato.equalsIgnoreCase(codCpiTitolareVch)) {
			if (codVchStatoPagamento.equalsIgnoreCase(Properties.STATO_PAGAMENTO_IN_ATTESA)) {%>
				<td align="left">
					<input class="pulsante" type = "button" name="BTNRIAPRI" value="Riapri" onClick="checkRiapri();"/>
				</td>	
			<%}
		}
		if (canAggiornaPagamenti) {%>
			<td align="left">
				<input class="pulsante" type = "button" name="BTNAGGIORNAPAG" value="Aggiorna Pagamenti" onClick="checkAggiornaPagamenti();"/>
			</td>
		<%}
		if (canStampaVoucher) {%>
			<td align="left">
			<input class="pulsante" type="button" name="stampaVch" value="Stampa attestazione" onclick="stampaTDAVoucher('<%=prgVoucher%>')"/>
			</td>
		<%}
	}%>
	<td align="center">
		<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
	</td>
	</tr>
	</table>
</af:form>
<%out.print(htmlStreamBottom);%>