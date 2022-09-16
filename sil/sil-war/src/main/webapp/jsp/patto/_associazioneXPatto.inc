<%--


<%@ include file="../patto/_associazioneDettaglioXPatto.inc" %>


VARIABILI DA DICHIARARE (E VALORIZZARE) NELLA JSP CHE ESEGUE LA INCLUDE (copia e incolla)
//////////////////////////////////////////////////////////////////////////    
    String COD_LST_TAB="PR_MAR|AM_IND_T|.....";    
    String cdnLavoratore;
    boolean readOnlyStr = valore fornito dallo attributo pageattribs true=pagina in sola lettura|false pagina con accesso alle funzioni di 
                            aggiornamento e modifica
/////////////////////////////////////////////////////////////////////////
--%>

<table   id="sezione_patto" width="100%" border=0>
    <tr>
        <td colspan="4">
        <%--
            <div class="sezione">Legame con il Patto 150/Accordo generico &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;
            <% if (!readOnlyStr) { %>
            <a href="#"  onClick="javascript:cercaIlPatto(<%=cdnLavoratore%>)"><img src="../../img/binocolo.gif" alt="Cerca"/></a>        
        	<a href="#"  onClick="javascript:resetPatto()"><img src="../../img/del.gif" alt="Cancella"/></a>
            <%}%>
        	</div>
        --%>
        	<table class='sezione' cellspacing=0 cellpadding=0>
				<tr>										
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
    <tr><td colspan=4>&nbsp;</td></tr>
    <tr>
        <td class="etichetta2">Tipologia&nbsp;</td>        
        <td class=campo2 colspan=3>
            <af:textBox type="text" name="TipoPatto" value="" validateOnPost="true" 
                 required="false" readonly="true" size="17" maxlength="17"/>
                 
        </td>      
    </tr>
    <tr>
        <td class="etichetta2" style="width:190">Data stipula&nbsp;</td>
        <td class=campo2>
            <af:textBox type="date" validateOnPost="true" readonly="true"
                    required="false" name="DATSTIPULA" value="" size="11" maxlength="10"/>
        </td>
        <td class="etichetta2">Stato&nbsp;atto&nbsp;</td> 
        <td class=campo2>
            <af:textBox type="text" name="StatoPatto" value="" validateOnPost="true"
                 required="false" readonly="true" size="28" maxlength="27"/>
        </td>
    </tr>
    <tr><td colspan=4>&nbsp;</td></tr>
    <tr>
        <td colspan="4" align="center"> 
            <!--input type="hidden" name="PRG_TAB"  value=""-->
            <input type="hidden" name="PRG_PATTO_LAVORATORE"  value="">
            <script>_prgPatto=''</script>
            <input type="hidden" name="COD_LST_TAB" value="<%= COD_LST_TAB%>">
            <input type="hidden" name="operazioneColPatto" value="0">
            <input type="hidden" name="PRG_LAV_PATTO_SCELTA" value="">
        </td>
    </tr>
</table>