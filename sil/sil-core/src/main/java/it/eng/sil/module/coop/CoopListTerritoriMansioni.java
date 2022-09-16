package it.eng.sil.module.coop;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class CoopListTerritoriMansioni extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CoopListTerritoriMansioni.class.getName());

	public void service(SourceBean request, SourceBean response) {
		String pool = getPool();
		// Apro la query che restituisce l'elenco delle mansioni e ne memorizzo
		// il risultato nel
		// SourceBean mansioni
		boolean noInfo = true;
		setSectionQuerySelect("QUERY_GET_MANSIONI_LAV");
		SourceBean sbMansioni = doSelect(request, response, false);
		java.util.Vector vettMansioni = sbMansioni.getAttributeAsVector("ROW");
		try {
			if (request.getAttribute("PRGMANSIONE") != null) {
				while (request.containsAttribute("PRGMANSIONE"))
					request.delAttribute("PRGMANSIONE");
			}
			request.setAttribute("PRGMANSIONE", "");

			for (int i = 0; i < vettMansioni.size(); i++) {
				SourceBean sbMansione = (SourceBean) vettMansioni.get(i);
				request.updAttribute("PRGMANSIONE", sbMansione.getAttribute("PRGMANSIONE"));

				setSectionQuerySelect("QUERY_GET_COMUNI_MANSIONE");
				SourceBean sbComuniMansione = doSelect(request, response, false);
				sbMansione.setAttribute("COMUNI", sbComuniMansione);
				noInfo = (noInfo) && sbComuniMansione.getAttribute("row") != null ? false : noInfo;

				setSectionQuerySelect("QUERY_GET_PROVINCE_MANSIONE");
				SourceBean sbProvinceMansione = doSelect(request, response, false);
				sbMansione.setAttribute("PROVINCE", sbProvinceMansione);
				noInfo = (noInfo) && sbProvinceMansione.getAttribute("row") != null ? false : noInfo;

				setSectionQuerySelect("QUERY_GET_REGIONI_MANSIONE");
				SourceBean sbRegioniMansione = doSelect(request, response, false);
				sbMansione.setAttribute("REGIONI", sbRegioniMansione);
				noInfo = (noInfo) && sbRegioniMansione.getAttribute("row") != null ? false : noInfo;

				setSectionQuerySelect("QUERY_GET_STATI_MANSIONE");
				SourceBean sbStatiMansione = doSelect(request, response, false);
				sbMansione.setAttribute("STATI", sbStatiMansione);
				noInfo = (noInfo) && sbStatiMansione.getAttribute("row") != null ? false : noInfo;
				sbMansione.delAttribute("prgmansione");
			}
		} catch (Exception ex) {
			_logger.error("sil.module.presel.ListTerritoriMansioni" + "::Errore in setAttribute" + ex.getMessage());

			ex.printStackTrace();
		}

		try {
			if (noInfo)
				response.setAttribute("MANSIONI", new SourceBean("ROWS"));
			else {
				response.setAttribute("MANSIONI", sbMansioni);
			}
		} catch (Exception ex) {
			_logger.error("sil.module.presel.ListTerritoriMansioni" + "::Errore in setAttribute" + ex.getMessage());

			ex.printStackTrace();
		}
	}
}

/*
 * public void service(SourceBean request, SourceBean response) throws Exception { _logger.debug(
 * this.getClass().getName() + "::select() CALLED...");
 * 
 * 
 * DBAccess dbaccess = new DBAccess(); DataConnection conn = dbaccess.getConnection(Values.DB_SIL_DATI); SQLCommand
 * selectCommand = null; DataResult dr = null;
 * 
 * String stmt = " SELECT XMLELEMENT (\"MANSIONI\",XMLAGG(XMLELEMENT (\"MANSIONE\", XMLATTRIBUTES "
 * +" (DE_MAN.STRDESCRIZIONE, MAN.PRGMANSIONE), " +" (SELECT XMLELEMENT
 * (\"COMUNI\", XMLAGG(XMLELEMENT(\"COMUNE\",XMLATTRIBUTES " +" (COM.PRGDISCOMUNE, DE_COM.STRDENOMINAZIONE)))) " +" FROM
 * PR_DIS_COMUNE COM, DE_COMUNE DE_COM " +" WHERE " +" COM.CODCOM = DE_COM.CODCOM " +" AND COM.PRGMANSIONE =
 * MAN.PRGMANSIONE ), " +" (SELECT XMLELEMENT (\"PROVINCE\", XMLAGG(XMLELEMENT(\"PROVINCIA\",XMLATTRIBUTES " +"
 * (PRO.CODPROVINCIA, DE_PRO.STRDENOMINAZIONE)))) " +" FROM PR_DIS_PROVINCIA PRO, DE_PROVINCIA DE_PRO " +" WHERE " +"
 * PRO.CODPROVINCIA = DE_PRO.CODPROVINCIA " +" AND PRO.PRGMANSIONE = MAN.PRGMANSIONE ), " +" (SELECT XMLELEMENT
 * (\"REGIONI\", XMLAGG(XMLELEMENT(\"REGIONE\",XMLATTRIBUTES " +" (REG.PRGDISREGIONE, DE_REG.STRDENOMINAZIONE)))) " +"
 * FROM PR_DIS_REGIONE REG, DE_REGIONE DE_REG " +" WHERE " +" REG.CODREGIONE = DE_REG.CODREGIONE " +" AND
 * REG.PRGMANSIONE = MAN.PRGMANSIONE ), " +" (SELECT XMLELEMENT (\"STATI\", XMLAGG(XMLELEMENT(\"STATO\",XMLATTRIBUTES "
 * +" (STA.PRGDISSTATO, DE_STA.STRDENOMINAZIONE)))) " +" FROM PR_DIS_STATO STA, DE_COMUNE DE_STA " +" WHERE " +"
 * STA.CODCOM = DE_STA.CODCOM " +" AND STA.PRGMANSIONE = MAN.PRGMANSIONE )))) " +" FROM PR_MANSIONE MAN, DE_MANSIONE
 * DE_MAN " +" WHERE MAN.CODMANSIONE = DE_MAN.CODMANSIONE " +" AND MAN.FLGDISPONIBILE = 'S' ";
 * 
 * selectCommand = conn.createSelectCommand(stmt);
 * 
 * ArrayList inputParameters = new ArrayList(1);
 * 
 * inputParameters.add(conn.createDataField("cdnLavoratore", Types.BIGINT, response.getAttribute("CDNLAVORATORE")));
 * 
 * dr = selectCommand.execute();
 * 
 * ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
 * 
 * DataField df = sdr.getDataRow().getColumn("MANSIONI"); oracle.sql.OPAQUE resultBlob = (oracle.sql.OPAQUE )
 * df.getObjectValue();
 * 
 * 
 * XMLType xmlType = XMLType.createXML(resultBlob);
 * 
 * response.setAttribute("MANSIONI",xmlType.getStringVal());
 * 
 * //System.out.println(xmlType.getStringVal());
 * 
 * com.engiweb.framework.dbaccess.Utils.releaseResources(conn, selectCommand, dr); }
 */