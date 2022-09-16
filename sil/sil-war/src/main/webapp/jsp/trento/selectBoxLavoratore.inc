<% String [] aliasLav = new String [] {	"CapDomicilio", 
										"CapResidenza", 
										"Cittadinanza",
										"CodiceFiscale",
										"CognomeLavoratore",										
										"ComuneDomicilio",
										"ComuneNascita",
										"ProvinciaNascita",	
										"ComuneResidenza",
										"Cellulare",
										"Email",
										"DataDid",
										"CategoriaCM",
										"DataNascita",
										"IndirizzoDomicilio",
										"IndirizzoResidenza",
										"LocalitaDomicilio",
										"LocalitaResidenza",
										"Nazione",
										"NomeLavoratore",
										"ProvinciaDomicilio",
										"ProvinciaResidenza",
										"SecondaCittadinanza",
										"SecondaNazionalita",
										"Sesso",
										"TelefonoResidenza",
										"TelefonoDomicilio",
										"DataAppOrientamPAI",
										"OraAppOrientamPAI",
										"DataAppPAT",
										"OraAppPAT",
										"DataIscrCM",
										"StatoOccupazionaleCorrente",
										"MesiAnzianitaCorrente",
										"GiorniAnzianitaCorrente",
										"DataTrasferimentoCpi",
										"DescrizioneTrasferimentoCpi",
										"DataAnazianitaL68",
										"NumeroIscrizione"
										//"DataAppPAI",
										//"OraAppPAI"
										};

%>
<p class="titolo"><b>Dominio dati Lavoratore</b></p>
<select class="dropzone" id="tagAlias" size=10 ondblclick="onDblClick(this.value)">
	<%for (int i=0;i<aliasLav.length;i++) { %>
	<option draggable="true"
		ondragstart="event.dataTransfer.setData('text/plain','@<%= aliasLav[i]%>')"
		value="@<%= aliasLav[i]%>"><%= aliasLav[i]%></option>
	<%} %>
</select>
