<p class="sezione">
<ul>   
<%
if (("1").equals(err_percEsonero)) {
%>
	<li><%= MessageBundle.getMessage(Integer.toString(MessageCodes.CollocamentoMirato.ERROR_NO_PERC_ESONERO))%></li>
<%
}
%> 
<%
if (("1").equals(err_percCompensazione)) {
%>
	<li><%= MessageBundle.getMessage(Integer.toString(MessageCodes.CollocamentoMirato.ERROR_NO_COMPENSAZIONE_TERRITORIALE))%></li>
<%
}
%> 
<%
if (("1").equals(err_numCompInProv)) {
%>
	<li>L'autocompensazione territoriale non può essere riferita alla stessa provincia del prospetto</li>
<%
}
%> 
<%
if (("1").equals(err_numCompArt18)) {
%>
	<li><%= MessageBundle.getMessage(Integer.toString(MessageCodes.CollocamentoMirato.ERROR_COERENZA_CATEGORIA_COMP_TERR_ART18))%></li>
<%
}
%> 
<%
if (("1").equals(err_numCompRiduz)) {
%>
	<li>L'autocompensazione non può essere in riduzione</li>
<%
}
%> 
<%
if (("1").equals(err_numCompensEcc)) {
%>
	<li><%= MessageBundle.getMessage(Integer.toString(MessageCodes.CollocamentoMirato.ERROR_COERENZA_CATEGORIA_COMP_TERR))%></li>
<%
}
%> 
<%
if (("1").equals(err_dataConsegnaProspetto)) {
%>
	<li><%= MessageBundle.getMessage(Integer.toString(MessageCodes.CollocamentoMirato.ERROR_DATA_CONSEGNA_OBBLIGATORIA))%></li>
<%
}
%> 
<%
if (("1").equals(err_dataProspetto)) {
%>
	<li><%= MessageBundle.getMessage(Integer.toString(MessageCodes.CollocamentoMirato.ERROR_DATA_PROSPETTO_OBBLIGATORIA))%></li>
<%
}
%>
<%
if (("1").equals(err_dataRiferimento)) {
%>
	<li><%= MessageBundle.getMessage(Integer.toString(MessageCodes.CollocamentoMirato.ERROR_DATA_RIFERIMENTO_OBBLIGATORIA))%></li>
<%
}
%>
<%
if (("1").equals(err_checkProspStoriciz)) {
%>
	<li><%= MessageBundle.getMessage(Integer.toString(MessageCodes.CollocamentoMirato.ERROR_PROSPETTO_STORICIZ_DUPLICATO))%></li>
<%
}
%>
<%
if (("1").equals(checkScopertura)) {
%>
	<li>Warning: i dati del prospetto indicano una scopertura da parte dell'azienda.</li>
<%
}
if (("1").equals(checkExistForza)) {
%>
	<li>Errore: controllare i lavoratori in forza, esistono lavoratori dopo la data "situazione al" inserita.</li>
<%
}
if (("1").equals(checkSospensione)) {
%>
	<li>Warning: l'azienda risulta in sospensione.</li>
<%
}
%> 
    
</ul>
</p>
