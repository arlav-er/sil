<%  
	BigDecimal prgAzienda=null;
  BigDecimal prgUnita=null;
	SourceBean rigaTestata = null;
	SourceBean contTestata = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA.ROWS.ROW");
	if (contTestata != null) {
	  prgAzienda = (BigDecimal) contTestata.getAttribute("PRGAZIENDA");
    prgUnita = (BigDecimal) contTestata.getAttribute("PRGUNITA");
	}
	String prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
%>