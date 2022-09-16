package it.eng.sil.module.condizionalita.rdc;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

public class RicercaProgrammiAzioniCondizionalita implements IDynamicStatementProvider {

	private static final String QUERY = "SELECT ser.strdescrizione  AS descrProgramma,  "
			+ "					  TO_CHAR(coll.DATCOLLOQUIO, 'dd/mm/yyyy')     AS dataInizioProg,  "
			+ "					  TO_CHAR(coll.DATFINEPROGRAMMA, 'dd/mm/yyyy') AS dataFineProg,  "
			+ "					  coll.PRGCOLLOQUIO,  " + "					  tabNegativo.countNeg,  "
			+ "					  tabCondizionalita.counCond  " + "					 FROM or_colloquio coll  "
			+ "					 INNER JOIN de_servizio ser  "
			+ "					 ON (ser.CODSERVIZIO                  = coll.CODSERVIZIO  "
			+ "					 AND (NVL(ser.FLGCONDIZIONALITA, 'N') = 'S')) , " + "         (     "
			+ "              SELECT NVL(COUNT(es.CODESITO),0) AS countNeg ,coll.PRGCOLLOQUIO "
			+ "             FROM or_colloquio coll  "
			+ "              INNER JOIN de_servizio ser   ON (ser.CODSERVIZIO  = coll.CODSERVIZIO  "
			+ "                   AND (NVL(ser.FLGCONDIZIONALITA, 'N') = 'S'))  "
			+ "              INNER JOIN or_percorso_concordato perc  "
			+ "              ON ( perc.PRGCOLLOQUIO = coll.PRGCOLLOQUIO)  "
			+ "              left outer JOIN de_esito es ON (es.CODESITO  = perc.CODESITO  AND (NVL(es.FLGNEGATIVO, 'N') = 'S'))  "
			+ "              WHERE coll.cdnlavoratore      = $CDNLAV$ " + "              group by coll.PRGCOLLOQUIO "
			+ "		 ) tabNegativo,  " + "         ( "
			+ "              SELECT NVL(COUNT(cond.PRGCONDIZIONALITA),0) AS counCond , coll.PRGCOLLOQUIO  "
			+ "              FROM or_colloquio coll               " + "               INNER JOIN de_servizio ser  "
			+ "              ON (ser.CODSERVIZIO                  = coll.CODSERVIZIO  "
			+ "              AND (NVL(ser.FLGCONDIZIONALITA, 'N') = 'S'))  "
			+ "               left outer JOIN  AM_CONDIZIONALITA cond   "
			+ "              ON (coll.PRGCOLLOQUIO = cond.PRGCOLLOQUIO  )  "
			+ "              WHERE coll.cdnlavoratore = $CDNLAV$ "
			+ "              group by coll.PRGCOLLOQUIO 				  " + "          ) tabCondizionalita  "
			+ "					 WHERE coll.cdnlavoratore =$CDNLAV$  "
			+ "           and tabCondizionalita.PRGCOLLOQUIO = coll.PRGCOLLOQUIO "
			+ "            and tabNegativo.PRGCOLLOQUIO = coll.PRGCOLLOQUIO          ";

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(req, "cdnLavoratore");

		String dataInizioProgramma = SourceBeanUtils.getAttrStrNotNull(req, "dataInizioProg");

		String negativi = SourceBeanUtils.getAttrStrNotNull(req, "NEGATIVI");

		String aperti = SourceBeanUtils.getAttrStrNotNull(req, "APERTI");

		String condizionalita = SourceBeanUtils.getAttrStrNotNull(req, "CONDIZIONALITA");

		StringBuffer queryBuffer = new StringBuffer(QUERY);

		String queryStr = queryBuffer.toString().replaceAll("\\$CDNLAV\\$", cdnLavoratore);

		queryBuffer = new StringBuffer(queryStr);

		if (StringUtils.isFilledNoBlank(dataInizioProgramma)) {
			queryBuffer.append(" AND trunc(coll.DATCOLLOQUIO) >= ");
			queryBuffer.append("to_date('" + dataInizioProgramma + "','dd/mm/yyyy')");
		}

		if (StringUtils.isFilledNoBlank(aperti)) {
			queryBuffer.append(" AND coll.DATFINEPROGRAMMA is null ");
		}
		if (StringUtils.isFilledNoBlank(condizionalita)) {
			queryBuffer.append(" AND counCond > 0 ");
		}
		if (StringUtils.isFilledNoBlank(negativi)) {
			queryBuffer.append(" AND countNeg > 0 ");
		}

		return queryBuffer.toString();
	}

}
