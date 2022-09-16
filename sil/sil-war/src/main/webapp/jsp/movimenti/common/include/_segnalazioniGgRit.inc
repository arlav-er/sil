<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<%
  SourceBean sbTmp = (SourceBean)serviceResponse.getAttribute("M_GETDATI_TSGEN.ROWS.ROW");
  BigDecimal rangePrCom = null;
  String codRegione = "";
  String dataTirocinio = "";
  String dataLavoroAgr = "";
  boolean flgLavAgr = false; 
  Calendar datoggi = Calendar.getInstance();
  String giornoDB = Integer.toString(datoggi.get(5));
  String meseDB = Integer.toString(datoggi.get(2)+1);
  String annoDB = Integer.toString(datoggi.get(1));
  
  if (giornoDB.length() == 1) { giornoDB = '0' + giornoDB;}
  if (meseDB.length() == 1) { meseDB = '0' + meseDB;}
  
  String dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;
  
  if (sbTmp!=null){
  	rangePrCom = (BigDecimal)sbTmp.getAttribute("NUMGGPRIMARITARDOMOV");
  	if(rangePrCom == null) rangePrCom = new BigDecimal(0);
  	out.println("<script language=\"javascript\"> var varRange = \"" + rangePrCom.toString() + "\"; </script>");
  	codRegione = sbTmp.containsAttribute("CODREGIONE")?sbTmp.getAttribute("CODREGIONE").toString():"";
  	out.println("<script language=\"javascript\"> var codRegioneProv = \"" + codRegione + "\"; </script>");
  	dataTirocinio = sbTmp.containsAttribute("DATTIROCINIO")?sbTmp.getAttribute("DATTIROCINIO").toString():"";
  	out.println("<script language=\"javascript\"> var dataTirocinio = \"" + dataTirocinio + "\"; </script>");
  	out.println("<script language=\"javascript\"> var dataOdierna = \"" + dataOdierna + "\"; </script>");
  	dataLavoroAgr = sbTmp.containsAttribute("DATAGRICOLTURA")?sbTmp.getAttribute("DATAGRICOLTURA").toString():"";
  	out.println("<script language=\"javascript\"> var dataLavAgric = \"" + dataLavoroAgr + "\"; </script>");
  	
  	if( !dataLavoroAgr.equals("") && DateUtils.compare(dataOdierna,dataLavoroAgr) > 0 ) 
  		flgLavAgr = true;
}%> 