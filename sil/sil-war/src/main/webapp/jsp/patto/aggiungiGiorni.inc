<%
	int giorno,mese,anno;
	//Gestione dei numGG per la scad del patto
	SourceBean numGg = (SourceBean)serviceResponse.getAttribute("M_GetNumGG.ROWS.ROW");
	String numGScadP = numGg.getAttribute("NUMGGSCADPATTO").toString();
	giorno = Integer.parseInt(DATSTIPULA.substring(0,2),10);
	mese = Integer.parseInt(DATSTIPULA.substring(3,5),10);
	anno = Integer.parseInt(DATSTIPULA.substring(6,10),10);
	GregorianCalendar dataDich = new GregorianCalendar(anno,(mese-1),giorno);
	dataDich.set(Calendar.DATE,(giorno+Integer.parseInt(numGScadP)));

  DATSCADCONFERMA = df.format(dataDich.getTime());
%>