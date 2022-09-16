package it.eng.sil.module.condizionalita.rdc;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

public class RicercaAzioniCondizionalita implements IDynamicStatementProvider {

	private static final String QUERY = " select  to_char(coll.DATCOLLOQUIO, 'dd/mm/yyyy') as dataInizioProg, "
			+ "       to_char(coll.DATFINEPROGRAMMA, 'dd/mm/yyyy') as dataFineProg, "
			+ "        az.STRDESCRIZIONE as descrAttivita, " + "        MN_YG_EVENTO.STRDESCRIZIONE as descrEsito, "
			+ "        ser.STRDESCRIZIONE as descrProgramma, " + "        azr.STRDESCRIZIONE as descrPrestazione, "
			+ "        to_char(perc.dateffettiva, 'dd/mm/yyyy') as dataEffettiva, "
			+ "        to_char(perc.DATAVVIOAZIONE, 'dd/mm/yyyy') as dataAvvio, "
			+ "        to_char(cond.DATINVIO, 'dd/mm/yyyy') || ' - ' || DE_CONDIZIONALITA.STRDESCRIZIONE as descrEvento, "
			+ "        coll.PRGCOLLOQUIO, perc.PRGPERCORSO " + " FROM or_colloquio coll "
			+ " INNER JOIN OR_PERCORSO_CONCORDATO perc on (coll.PRGCOLLOQUIO = perc.PRGCOLLOQUIO) "
			+ " INNER JOIN DE_AZIONE az ON (az.PRGAZIONI = perc.PRGAZIONI) "
			+ " INNER JOIN DE_AZIONE_RAGG azr ON (az.PRGAZIONERAGG = azr.PRGAZIONIRAGG) "
			+ " LEFT OUTER JOIN de_esito es ON (es.CODESITO  = perc.CODESITO) "
			+ " INNER JOIN MN_YG_EVENTO on (es.CODEVENTOMIN = MN_YG_EVENTO.CODEVENTO) "
			+ " INNER JOIN de_servizio ser ON (ser.CODSERVIZIO = coll.CODSERVIZIO) "
			+ " LEFT OUTER JOIN AM_CONDIZIONALITA cond on (cond.PRGCOLLOQUIO = coll.PRGCOLLOQUIO and cond.PRGPERCORSO = perc.PRGPERCORSO) "
			+ " LEFT OUTER JOIN DE_CONDIZIONALITA ON (DE_CONDIZIONALITA.CODEVENTO = cond.CODEVENTOCOND) "
			+ " where coll.PRGCOLLOQUIO = $PRGCOLL$ ";

	private static final String QUERY_CONDIZ = " select  to_char(coll.DATCOLLOQUIO, 'dd/mm/yyyy') as dataInizioProg, "
			+ "       to_char(coll.DATFINEPROGRAMMA, 'dd/mm/yyyy') as dataFineProg, "
			+ "        az.STRDESCRIZIONE as descrAttivita, " + "        MN_YG_EVENTO.STRDESCRIZIONE as descrEsito, "
			+ "        ser.STRDESCRIZIONE as descrProgramma, " + "        azr.STRDESCRIZIONE as descrPrestazione, "
			+ "        to_char(perc.dateffettiva, 'dd/mm/yyyy') as dataEffettiva, "
			+ "        to_char(perc.DATAVVIOAZIONE, 'dd/mm/yyyy') as dataAvvio, "
			+ "        to_char(cond.DATINVIO, 'dd/mm/yyyy') || ' - ' || DE_CONDIZIONALITA.STRDESCRIZIONE as descrEvento, "
			+ "        coll.PRGCOLLOQUIO, perc.PRGPERCORSO " + " FROM or_colloquio coll "
			+ " INNER JOIN OR_PERCORSO_CONCORDATO perc on (coll.PRGCOLLOQUIO = perc.PRGCOLLOQUIO) "
			+ " INNER JOIN DE_AZIONE az ON (az.PRGAZIONI = perc.PRGAZIONI) "
			+ " INNER JOIN DE_AZIONE_RAGG azr ON (az.PRGAZIONERAGG = azr.PRGAZIONIRAGG) "
			+ " LEFT OUTER JOIN DE_ESITO es ON (es.CODESITO  = perc.CODESITO) "
			+ " INNER JOIN MN_YG_EVENTO on (es.CODEVENTOMIN = MN_YG_EVENTO.CODEVENTO) "
			+ " INNER JOIN de_servizio ser ON (ser.CODSERVIZIO = coll.CODSERVIZIO) "
			+ " INNER JOIN AM_CONDIZIONALITA cond on (cond.PRGCOLLOQUIO = coll.PRGCOLLOQUIO and cond.PRGPERCORSO = perc.PRGPERCORSO) "
			+ " INNER JOIN DE_CONDIZIONALITA ON (DE_CONDIZIONALITA.CODEVENTO = cond.CODEVENTOCOND) "
			+ " where coll.PRGCOLLOQUIO = $PRGCOLL$ ";

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String prgColloquio = SourceBeanUtils.getAttrStrNotNull(req, "PRGCOLLOQUIO");

		String negativi = SourceBeanUtils.getAttrStrNotNull(req, "NEGATIVI");

		String condizionalita = SourceBeanUtils.getAttrStrNotNull(req, "CONDIZIONALITA");

		StringBuffer queryBuffer = new StringBuffer(QUERY);

		if (StringUtils.isFilledNoBlank(condizionalita)) {
			queryBuffer = new StringBuffer(QUERY_CONDIZ);
		}

		String queryStr = queryBuffer.toString().replaceAll("\\$PRGCOLL\\$", prgColloquio);

		queryBuffer = new StringBuffer(queryStr);

		if (StringUtils.isFilledNoBlank(negativi)) {
			queryBuffer.append(" AND (NVL(es.FLGNEGATIVO, 'N') = 'S') ");
		}

		return queryBuffer.toString();
	}

}
