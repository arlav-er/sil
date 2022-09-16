package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * Questa classe restituisce la query per la ricerca di movimenti di Apprendistato/Tirocinio
 * 
 * @author Landi
 *
 */

public class EstrazioneEntePromotore implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EstrazioneEntePromotore.class.getName());

	private static final String SELECT_SQL_BASE = "SELECT "
			+ "AM_MOVIMENTO.PRGMOVIMENTO, SUBSTR (AM_MOVIMENTO.CODTIPOMOV, 1, 1) CODTIPOMOVVISUAL, "
			+ "AM_MOVIMENTO.CDNLAVORATORE, "
			+ "TO_CHAR(AM_MOVIMENTO.DATFINEMOVEFFETTIVA,'DD/MM/YYYY') DATFINEMOVEFFETTIVA, "
			+ "TO_CHAR(AM_MOVIMENTO.DATINIZIOMOV,'DD/MM/YYYY') DATINIZIOMOV, " + "AN_UNITA_AZIENDA.PRGAZIENDA, "
			+ "AN_UNITA_AZIENDA.PRGUNITA, "
			+ "AN_LAVORATORE.STRCOGNOME || ' ' || AN_LAVORATORE.STRNOME COGNOMENOMELAV, AN_LAVORATORE.STRCODICEFISCALE CODFISCLAV, "
			+ "INITCAP(AN_AZIENDA.STRRAGIONESOCIALE) AS STRRAGIONESOCIALE, " + "AN_AZIENDA.STRCODICEFISCALE, "
			+ "AN_UNITA_AZIENDA.STRINDIRIZZO, "
			+ "AN_UNITA_AZIENDA.STRINDIRIZZO || ', ' || DE_COMUNE.STRDENOMINAZIONE || '(' || RTRIM(PROV.STRISTAT) || ')' INDIRAZIENDA, "
			+ "DE_COMUNE.CODCOM, " + "AM_MOVIMENTO.CODTIPOMOV ," + "AM_MOVIMENTO.CODMVCESSAZIONE ,"
			+ "AM_MOVIMENTO.CODMONOTEMPO, " + "AM_MOVIMENTO.CODTIPOCONTRATTO CODTIPOASS, "
			+ "AN_AZIENDA.STRCODICEFISCALE STRCODFISCPROMOTORETIR, "
			+ "INITCAP(DE_COMUNE.STRDENOMINAZIONE) AS COMUNE_AZ "
			+ "FROM AM_MOVIMENTO, AN_UNITA_AZIENDA, AN_AZIENDA, AN_LAVORATORE, DE_COMUNE, DE_PROVINCIA PROV "
			+ "WHERE (AM_MOVIMENTO.CODSTATOATTO = 'PR') "
			+ "AND (AM_MOVIMENTO.PRGAZIENDA = AN_UNITA_AZIENDA.PRGAZIENDA) "
			+ "AND (AM_MOVIMENTO.PRGUNITA = AN_UNITA_AZIENDA.PRGUNITA) "
			+ "AND (AN_UNITA_AZIENDA.PRGAZIENDA = AN_AZIENDA.PRGAZIENDA) "
			+ "AND (AM_MOVIMENTO.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE) "
			+ "AND (AN_UNITA_AZIENDA.CODCOM = DE_COMUNE.CODCOM) "
			+ "AND (DE_COMUNE.CODPROVINCIA = PROV.CODPROVINCIA (+) ) "
			+ "AND (AM_MOVIMENTO.CODTIPOMOV <> 'CES') AND (AM_MOVIMENTO.CODTIPOCONTRATTO IN ('C.01.00', 'C.04.00', 'A.03.08', 'A.03.09', 'A.03.10')) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		query_totale.append("AND (AM_MOVIMENTO.CDNLAVORATORE = ");
		query_totale.append(cdnLavoratore);
		query_totale.append(")");

		query_totale.append(" ORDER BY AM_MOVIMENTO.DATINIZIOMOV ASC, AM_MOVIMENTO.PRGMOVIMENTO ASC");

		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}
