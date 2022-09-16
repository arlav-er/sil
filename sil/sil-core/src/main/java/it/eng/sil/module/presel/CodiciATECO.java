package it.eng.sil.module.presel;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

public class CodiciATECO {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CodiciATECO.class.getName());

	public CodiciATECO() {
	}

	/**
	 * Crea l'albero dei codici Ateco a partire da una query XSQL e da un trasformer xsl. Evita annidamenti di query sul
	 * DB che creano problemi di connessioni e cursori
	 * </p>
	 * 
	 * @author Roccetti
	 */
	public static String getCodici(String codPadre) {

		// Query xsql
		String xsqlquery = "SELECT	XMLELEMENT(\"ROOT\", ( " + " SELECT XMLAGG(XMLELEMENT(\"attivita\", "
				+ " XMLATTRIBUTES(ATT.CODATECO AS \"codAteco\", "
				+ " DECODE(att.codatecodot,NULL,\'\',att.codatecodot||\' - \')||PG_UTILS.TRUNC_DESC(att.strdescrizione,65, \'...\' ) "
				+ "|| DECODE (SYSDATE, GREATEST(SYSDATE, att.DATINIZIOVAL, att.DATFINEVAL), "
				+ " \' (scaduto)\',  LEAST(SYSDATE, att.DATINIZIOVAL, att.DATFINEVAL), "
				+ " \' (scaduto)\', \'\') AS \"strDescrizione\", " + " ATT.CODPADRE AS \"codPadre\", "
				+ " nvl(PG_UTILS.TRUNC_DESC(ATTPADRE.strdescrizione,66, \'...\' ),att.strdescrizione) "
				+ "|| DECODE (SYSDATE, GREATEST(SYSDATE, ATTPADRE.DATINIZIOVAL, ATTPADRE.DATFINEVAL), "
				+ " \' (scaduto)\',  LEAST(SYSDATE, ATTPADRE.DATINIZIOVAL, ATTPADRE.DATFINEVAL), "
				+ " \' (scaduto)\', \'\')  AS \"desTipoAteco\", "
				+ " ATT.CDNLIVELLO AS \"cdnLivello\", ATT.CODATECODOT AS \"codAtecoDot\"))) "
				+ " FROM DE_ATTIVITA ATT, DE_ATTIVITA ATTPADRE " + " WHERE ATT.CODPADRE = ATTPADRE.CODATECO (+) "
				+ " START WITH ATT.CODATECO = '" + codPadre + "'  " + " CONNECT BY PRIOR ATT.CODATECO = ATT.CODPADRE) "
				+ " ).getClobVal() AS ATTXML FROM DUAL ";

		DataConnection dcSil = null;
		SQLCommand cmd = null;
		DataResult dr = null;
		StringWriter result = new StringWriter();
		try {
			// Apro la connessione
			dcSil = DataConnectionManager.getInstance().getConnection("SIL_DATI");
			// dichiaro il comando
			cmd = dcSil.createSelectCommand(xsqlquery);
			// eseguo il comando con il parametro impostato
			dr = cmd.execute();
			// Risultati
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();

			DataField df = sdr.getDataRow().getColumn("ATTXML");
			String xml = df.getStringValue();

			// Tracing dei risultati
			_logger.debug(xml);

			// Formattazione risultati attraverso un transformer

			// Creo il nome del file di configurazione per il transformer
			String filename = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsl"
					+ File.separator + "presel" + File.separator + "AtecoQueryXSQL.xsl";
			File f = new File(filename);

			// Creo il transformer
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(f));
			// Trasformo il risultato
			transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(result));

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "sil.anag.presel.CodiciATECO"
					+ "::ERRORE DI CONNESSIONE::Non è possibile recuperare i dati " + e.getMessage(), e);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dcSil, cmd, dr);
		}
		return result.toString();
	}
}
