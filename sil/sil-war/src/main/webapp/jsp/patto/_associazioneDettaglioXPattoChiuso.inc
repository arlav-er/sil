<%--


<%@ include file="../patto/_associazioneDettaglioXPatto.inc" %>


VARIABILI DA DICHIARARE (E VALORIZZARE) NELLA JSP CHE ESEGUE LA INCLUDE (copia e incolla)
//////////////////////////////////////////////////////////////////////////
    //
    // va valorizzata con la chiave del record visualizzato della tabella gestita dalla pagina
    // **********************   SOLO NELLA PAGINA DI DETTAGLIO   *****************    
    PRG_TAB_DA_ASSOCIARE=prgTabTuaPagina.toString(); //  
    //////////////////////////////////////////////    
    // String PRG_TAB_DA_ASSOCIARE=null; // dichiarazione **** non piu' necessario *****
    String cdnLavoratore;
    boolean readOnlyStr = valore fornito dallo attributo pageattribs true=pagina in sola lettura|false pagina con accesso alle funzioni di 
                            aggiornamento e modifica
    String COD_LST_TAB="PR_MAN|.....";
    SourceBean row=null; // row = tuoSourceBeanRow;
/////////////////////////////////////////////////////////////////////////
--%>
	<%
	String PRG_PATTO_LAVORATORE=null;
    String STATO_ATTO=null;
    String DATA_STIPULA=null;
    String TIPOLOGIA=null; 
    String PRG_LAV_PATTO_SCELTA=null;
    String DATPROTOCOLLOINF=null;
    String STR_DESCRIZIONE_PATTO=null;
    String codStatoPattoLavoratore = null;
    String datFine = null;
    String motivoFine = null, datOraProtocollo=null, numProtocollo = null;
	try {
		PRG_PATTO_LAVORATORE=((BigDecimal)row.getAttribute("PRGPATTOLAVORATORE")).toString();
		PRG_LAV_PATTO_SCELTA=((BigDecimal)row.getAttribute("PRGLAVPATTOSCELTA")).toString();
		STATO_ATTO=(String) row.getAttribute("STATOATTO");
		DATA_STIPULA=(String) row.getAttribute("datstipula");
		TIPOLOGIA=(String) row.getAttribute("FLGPATTO297");
		STR_DESCRIZIONE_PATTO=(String) row.getAttribute("strDescrizionePatto");
		if (STR_DESCRIZIONE_PATTO!=null && !STR_DESCRIZIONE_PATTO.equals("")) {
			TIPOLOGIA=STR_DESCRIZIONE_PATTO;	
		}
		else {
			if (TIPOLOGIA!=null) {
				TIPOLOGIA=TIPOLOGIA.equalsIgnoreCase("S")?"Patto 150":"Accordo Generico";
			}	
		}
		DATPROTOCOLLOINF=(String) row.getAttribute("datprotocollo");
		codStatoPattoLavoratore = (String)row.getAttribute("codStatoPattoLavoratore");
		datFine = (String)row.getAttribute("datFine");
		motivoFine = (String)row.getAttribute("motivoFine");
		datOraProtocollo = (String)row.getAttribute("datOraProtocollo");
		numProtocollo = Utils.notNull(row.getAttribute("numProtocollo"));
	}catch (NullPointerException npe) {}
	// gestione visualizzazione sezione
	boolean legatoAlPatto = PRG_PATTO_LAVORATORE!=null;
	String imgSelSezionePatto = legatoAlPatto?"../../img/aperto.gif":"../../img/chiuso.gif";
	String displaySPatto = legatoAlPatto? "":"none";
	%>
<%@include file="_controllaStatoAtto.inc"%>
<table   id="sezione_patto" width="100%" border=0>
    <tr>
        <td colspan="4">    
            <table class='sezione' cellspacing=0 cellpadding=0>
				<tr>					
					<td  width=18>&nbsp;
<%--                    	<img id='I_SEL_S_PATTO' src='<%=imgSelSezionePatto%>' onclick='cambia(this, document.getElementById("T_S_PATTO"))'>
--%>
</td>
					<td class="sezione_titolo">Legame con il Patto 150/Accordo generico</td>
					<% if (!readOnlyStr) { %>
					<td class="sezione_pulsante">
						<a  href='#' alt='Legame con patto' onclick='cercaIlPatto(<%=cdnLavoratore%>)'>
							<img src='../../img/binocolo.gif'></a>
					</td>
					<td class="sezione_pulsante">
						<a  href='#' alt='Cancella' onclick='resetPatto()'><img src='../../img/del.gif'></a>
					</td>
					<% } %>
				</tr>
			</table>
		</td>
    </tr>    
    <tr>
    	<td colspan=4 align="center">
        	<TABLE id='T_S_PATTO' style='width:100%;display:<%=displaySPatto%>'>     
        	<script>initSezioni( new Sezione(document.getElementById('T_S_PATTO'),
        									 document.getElementById('I_SEL_S_PATTO'),
        									 <%=legatoAlPatto%>)
        						);
        	</script>
        		<tr>        		
	        		<td class="etichetta2">Tipologia</td>        
	        		<td class=campo2>
	            		<af:textBox type="text"
	                        name="TipoPatto"
	                        value="<%= it.eng.sil.util.Utils.notNull(TIPOLOGIA)%>"
	                        classNameBase="input"
	                        validateOnPost="true" 
	                        required="false"
	                        readonly="true"
	                        size="17"
	                        maxlength="17"/>
	                 
					</td>   
					<td class="etichetta2" style="width:190" nowrap>Data stipula</td>
			        <td class=campo2>
			            <af:textBox type="date"
			                        validateOnPost="true"
			                        readonly="true"
			                        required="false"
			                        classNameBase="input"
			                        name="DATSTIPULA"
			                        value="<%= it.eng.sil.util.Utils.notNull(DATA_STIPULA)%>"
			                        size="11"
			                        maxlength="10"/>
			        </td>   
			    </tr>
			    <tr>
			        <td class="etichetta2">Stato atto</td> 
			        <td class=campo2>
			            <af:textBox type="text"
			                        name="StatoPatto"
			                        value="<%=it.eng.sil.util.Utils.notNull(STATO_ATTO)%>"
			                        validateOnPost="true"
			                        classNameBase="input"
			                        required="false"
			                        readonly="true"
			                        size="30"
			                        maxlength="27"/>
			        </td>
			        <td class="etichetta2" >Data Prot. INF.</td>        
	        		<td class=campo2>
	            		<af:textBox type="text"
	                        name="datProtocolloInf"
	                        value="<%= it.eng.sil.util.Utils.notNull(DATPROTOCOLLOINF)%>"
	                        classNameBase="input"
	                        validateOnPost="true" 
	                        required="false"
	                        readonly="true"
	                        size="17"
	                        maxlength="17"/>	                 
					</td>
			    </tr>
			    <%if (datFine!=null) {%>
			    <tr>
			        <td class="etichetta2">Data fine</td> 
			        <td class=campo2>
			            <af:textBox type="text"
			                        name="dataFine"
			                        value="<%=it.eng.sil.util.Utils.notNull(datFine)%>"
			                        validateOnPost="true"
			                        classNameBase="input"
			                        required="false"
			                        readonly="true"
			                        size="30"
			                        maxlength="27"/>
			        </td>
			        <td class="etichetta2" >Motivo fine</td> 
	        		<td class=campo2>
	            		<af:textBox type="text"
	                        name="motivoFine"
	                        value="<%= it.eng.sil.util.Utils.notNull(motivoFine)%>"
	                        classNameBase="input"
	                        validateOnPost="true" 
	                        required="false"
	                        readonly="true"
	                        size="50"/>	                 
					</td>
			    </tr>
			    <%}%>
			    <tr>
			        <td class="etichetta2">Num. protocollo</td> 
			        <td class=campo2>
			            <af:textBox type="text"
			                        name="numProtocollo"
			                        value="<%=it.eng.sil.util.Utils.notNull(numProtocollo)%>"
			                        validateOnPost="true"
			                        classNameBase="input"
			                        required="false"
			                        readonly="true"
			                        size="30"
			                        maxlength="27"/>
			        </td>
			        <td class="etichetta2" >Data protocollo</td> 
	        		<td class=campo2>
	            		<af:textBox type="text"
	                        name="dataOraProtocollo"
	                        value="<%= it.eng.sil.util.Utils.notNull(datOraProtocollo)%>"
	                        classNameBase="input"
	                        required="false"
	                        readonly="true"
	                        size="18"/>	                 
					</td>
			    </tr>
			    <tr><td colspan=4>&nbsp;</td></tr>
			    <tr>
			        <td colspan="4" align="center"> 
			            <input type="hidden" name="PRG_TAB"  value="<%= it.eng.sil.util.Utils.notNull(PRG_TAB_DA_ASSOCIARE) %>">
			            <input type="hidden" name="PRG_PATTO_LAVORATORE"  value="<%= it.eng.sil.util.Utils.notNull(PRG_PATTO_LAVORATORE)%>">
			            <script>_prgPatto='<%= it.eng.sil.util.Utils.notNull(PRG_PATTO_LAVORATORE)%>'</script>
			            <input type="hidden" name="COD_LST_TAB" value="<%= COD_LST_TAB%>">
			            <input type="hidden" name="operazioneColPatto" value="0">
			            <input type="hidden" name="PRG_LAV_PATTO_SCELTA" value="<%= it.eng.sil.util.Utils.notNull(PRG_LAV_PATTO_SCELTA) %>">
			            <input type="hidden" name="COD_STATO_PATTO_LAVORATORE" value="<%= codStatoPattoLavoratore %>">
			        </td>
			    </tr>
	 	   </table>
	    </td>
	</tr>
</table>