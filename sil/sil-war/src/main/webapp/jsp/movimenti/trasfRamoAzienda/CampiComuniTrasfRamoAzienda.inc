<%
	//Campi comuni a tutte le linguette delle pagine per il trasferimento
	//del ramo aziendale, tranne per i lavoratori selezionati, che vengono
	//mantenuti in sessione perchè potrebbero essere centinaia...
	String PRGAZIENDAPROVENIENZA = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAPROVENIENZA");
    String PRGUNITAPROVENIENZA = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAPROVENIENZA");
    String PRGAZIENDADESTINAZIONE = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDADESTINAZIONE");
    String PRGUNITADESTINAZIONE = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITADESTINAZIONE");  
    String FILTROCFLAV = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTROCFLAV");  
    String FILTRONOMELAV = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRONOMELAV");  
    String FILTROCOGNOMELAV = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTROCOGNOMELAV");  
    String FILTRODATAINIZIOASS = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRODATAINIZIOASS");  
    String FILTRODATAFINEASS = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTRODATAFINEASS");  
    String FILTROCODTIPOASS = StringUtils.getAttributeStrNotNull(serviceRequest, "FILTROCODTIPOASS");  
    String CODMONOMOVDICH = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOMOVDICH");
    String DATCOMUNICAZ = StringUtils.getAttributeStrNotNull(serviceRequest, "DATCOMUNICAZ");
    String DATTRASFERIMENTO = StringUtils.getAttributeStrNotNull(serviceRequest, "DATTRASFERIMENTO");
    String NUMGGTRAMOVCOMUNICAZIONE = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMGGTRAMOVCOMUNICAZIONE");
    String CODTIPOTRASF = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOTRASF");
    String dataFineAffittoRamo = StringUtils.getAttributeStrNotNull(serviceRequest, "DATFINEAFFITTORAMO");
%>
	<input type="hidden" name=PRGAZIENDAPROVENIENZA value="<%=PRGAZIENDAPROVENIENZA%>"/>			
	<input type="hidden" name=PRGUNITAPROVENIENZA value="<%=PRGUNITAPROVENIENZA%>"/>			
	<input type="hidden" name=PRGAZIENDADESTINAZIONE value="<%=PRGAZIENDADESTINAZIONE%>"/>			
	<input type="hidden" name=PRGUNITADESTINAZIONE value="<%=PRGUNITADESTINAZIONE%>"/>			
	<input type="hidden" name=FILTROCFLAV value="<%=FILTROCFLAV%>"/>			
	<input type="hidden" name=FILTRONOMELAV value="<%=FILTRONOMELAV%>"/>			
	<input type="hidden" name=FILTROCOGNOMELAV value="<%=FILTROCOGNOMELAV%>"/>			
	<input type="hidden" name=FILTRODATAINIZIOASS value="<%=FILTRODATAINIZIOASS%>"/>			
	<input type="hidden" name=FILTRODATAFINEASS value="<%=FILTRODATAFINEASS%>"/>			
	<input type="hidden" name=FILTROCODTIPOASS value="<%=FILTROCODTIPOASS%>"/>			
	<input type="hidden" name=CODMONOMOVDICH value="<%=CODMONOMOVDICH%>"/>			
	<input type="hidden" name=DATCOMUNICAZ value="<%=DATCOMUNICAZ%>"/>			
	<input type="hidden" name=DATTRASFERIMENTO value="<%=DATTRASFERIMENTO%>"/>			
	<input type="hidden" name=NUMGGTRAMOVCOMUNICAZIONE value="<%=NUMGGTRAMOVCOMUNICAZIONE%>"/>			
	<input type="hidden" name=CODTIPOTRASF value="<%=CODTIPOTRASF%>"/>
	<input type="hidden" name=DATFINEAFFITTORAMO value="<%=dataFineAffittoRamo%>"/>