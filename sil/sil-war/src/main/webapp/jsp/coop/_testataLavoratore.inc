<%
out.print(StyleUtils.roundTopTable(true));

SourceBean _cache = (SourceBean)sessionContainer.getAttribute(it.eng.sil.module.coop.GetDatiPersonali.SCHEDA_LAVORATORE_COOP_ID);
SourceBean _testata = (SourceBean)_cache.getAttribute("TESTATA");
String _codiceFiscale      = Utils.notNull(_testata.getAttribute("strCodiceFiscale"));
String _cognome            = Utils.notNull(_testata.getAttribute("strCognome"));
String _nome               = Utils.notNull(_testata.getAttribute("strNome"));
String _dataNascita        = Utils.notNull(_testata.getAttribute("dataNascita"));
String _comuneNascita      = Utils.notNull(_testata.getAttribute("comNas"));
String _codProvinciaMaster = Utils.notNull(_testata.getAttribute("codProvinciaMaster"));
String _provinciaMaster    = Utils.notNull(_testata.getAttribute("provinciaMaster"));
String _tipoMaster         = Utils.notNull(_testata.getAttribute("tipoMaster"));

%>
<p class="info_lav">
		Lavoratore&nbsp;<strong><%=_cognome%>&nbsp;<%=_nome%></strong>
		codice&nbsp;fiscale&nbsp;<strong><%=_codiceFiscale%></strong>
		data&nbsp;di&nbsp;nascita&nbsp;<strong><%=_dataNascita%></strong>
		comune&nbsp;di&nbsp;nascita&nbsp;<strong><%=_comuneNascita%></strong>
		<br>
	</p>
<p class="info_lav">
		Provincia master&nbsp;<strong><%=_provinciaMaster%></strong>
		tipo&nbsp;master&nbsp;<strong><%=_tipoMaster%></strong>
		<br>
	</p>
	<br>
	<p class="info_lav">Informazioni presenti:</p>
<%
Vector sezioniConDati = _cache.getAttributeAsVector("TESTATA.PARAGRAFO");
for (int i=0;i<sezioniConDati.size();i++) {
	SourceBean _paragrafo = (SourceBean)sezioniConDati.get(i);
	String _sezione = (String)_paragrafo.getAttribute("nome");
	Vector _sezioni = _paragrafo.getAttributeAsVector("sezioni");
	out.print("<p class='info_lav'>&nbsp;&nbsp;&nbsp;&nbsp;<b>"+_sezione+"</b>: ");
	for (int j=0;j<_sezioni.size();j++) {
		out.print(_sezioni.get(j));
		if (j+1<_sezioni.size()) out.println(", ");
	}
	out.println("</p>");
}
// controllo se si sono verificati errori nel polo remoto.
Vector erroriAttributes = _cache.getContainedSourceBeanAttributes("ERRORS");
if (erroriAttributes.size()>0) {
	out.print("<p class='info_lav' style='color:red'>");
	out.print("Si sono verificati errori nella lettura dei dati nel polo remoto");
	Vector moduliInErrore = _cache.getAttributeAsVector("TESTATA.ERRORI.ERRORE");
	if (moduliInErrore.size()>0) out.print(" nelle sezioni:</p><p  class='info_lav' style='color: red'>");

	for (int i=0;i<moduliInErrore.size();i++) {
		SourceBean errore = (SourceBean)moduliInErrore.get(i);
		out.print(errore.getAttribute("generato_da") + " ("+errore.getAttribute("descrizione") + ")");
		if (i+1<moduliInErrore.size()) out.println(", ");
	}
	out.print("</p>");	
}
out.print(StyleUtils.roundBottomTable(true));

%>
