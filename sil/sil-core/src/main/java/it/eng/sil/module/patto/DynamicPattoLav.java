package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;

public class DynamicPattoLav implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = " distinct lav.strcodicefiscale," + " lav.strcognome,"
			+ " lav.strnome, " + " patto.prgpattolavoratore," + " patto.codcpi, " + " patto.cdnlavoratore, "
			+ " patto.prgdichdisponibilita," + " patto.DATSTIPULA,"
			+ " to_char(patto.datstipula, 'dd/mm/yyyy') as STRDATSTIPULA,  "
			+ " decode( DE_STATO_ATTO.codstatoatto, 'SO','Sospesa', " + "      'RE','Respinta', "
			+ " 	   'AC','Accolta', " + " 	   'IA','In attesa', " + " 	   'FD','Fine decor.', "
			+ " 	   'AS','Chiuso', " + " 	   'AO','Accolto', " + " 	   'PR','Protocollato', "
			+ " 	   'PA','Attesa prot.', " + " 	   'FI','Firmato', " + " 	   'DF','Attesa firma', "
			+ " 	   'PP','Prepatto', " + " 	   'SS','Sospeso' " + " ) as codstatoatto, " + " patto.prgstatooccupaz,"
			+ " patto.flgcomunicazesiti, " + " patto.codmotivofineatto, " + " patto.codservizio, "
			+ " to_char(patto.datscadconferma, 'dd/mm/yyyy') as DATSCADCONFERMA,   "
			// + " patto.datfine, "
			+ " patto.strnote," + " patto.cdnutins," + " patto.dtmins, " + " patto.cdnutmod," + " patto.dtmmod, "
			+ " patto.numklopattolavoratore," + " patto.flgpatto297," + " de_patto.codtipopatto,"
			+ " to_char(patto.datfine, 'dd/mm/yyyy') as DATFINE,   "
			+ " PG_UTILS.TRUNC_DESC(mot.STRDESCRIZIONE, 39, '...') as MOTIVOFINEATTO ";
	// + " mot.STRDESCRIZIONE as MOTIVOFINEATTO ";

	private static final String FROM_SQL_BASE = "   an_lavoratore lav, am_patto_lavoratore patto,"
			+ "   am_elenco_anagrafico ea," + "   an_lav_storia_inf_coll  coll," + "   an_lav_storia_inf inf,"
			+ "   DE_STATO_ATTO, " + " DE_TIPO_PATTO de_patto, " + " DE_MOTIVO_FINE_ATTO mot ";
	private static final String WHERE_SQL_BASE = "  lav.cdnlavoratore = patto.cdnlavoratore"
			// + " AND patto.datFine is null"
			+ "  AND lav.CDNLAVORATORE = ea.CDNLAVORATORE" + "  AND ea.prgElencoAnagrafico = coll.strChiaveTabella"
			+ "  AND coll.codLstTab = 'EA' " + "  AND coll.prgLavStoriaInf = inf.prgLavStoriaInf"
			+ "  AND inf.datFine is null " + "  AND patto.CODSTATOATTO = DE_STATO_ATTO.CODSTATOATTO "
			+ "  AND patto.codtipopatto = de_patto.codtipopatto ";

	// + " AND patto.codmotivofineatto = mot.codmotivofineatto (+)";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		SourceBean req = requestContainer.getServiceRequest();

		String nome = (String) req.getAttribute("NOME");
		String cognome = (String) req.getAttribute("COGNOME");
		String datInizioDa = (String) req.getAttribute("DATINIZIODA");
		String datInizioA = (String) req.getAttribute("DATINIZIOA");
		String scadConfDa = (String) req.getAttribute("SCADCONFDA");
		String scadConfA = (String) req.getAttribute("SCADCONFA");

		String cf = (String) req.getAttribute("CF");
		String strFlgPatto297 = (String) req.getAttribute("FLGPATTO297");
		String codCodificaPatto = (String) req.getAttribute("codCodificaPatto");
		String codTipoPatto = (String) req.getAttribute("codTipoPatto");
		String codCpi = (String) req.getAttribute("codCpi");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");

		String attiAperti = StringUtils.getAttributeStrNotNull(req, "AttiAperti");
		String pattiOnLine = StringUtils.getAttributeStrNotNull(req, "PattiOnLine");
		String codAccettazionePtOnLine = StringUtils.getAttributeStrNotNull(req, "codAccOnLine");
		String dataFineAttoDa = StringUtils.getAttributeStrNotNull(req, "dataFineAttoDa");
		String dataFineAttoA = StringUtils.getAttributeStrNotNull(req, "dataFineAttoA");
		String motivoFine = StringUtils.getAttributeStrNotNull(req, "MotivoFine");

		// String attPrec =(String) req.getAttribute("ATT_PREC");
		DynamicStatementUtils dsu = new DynamicStatementUtils();

		dsu.addSelect(SELECT_SQL_BASE);
		dsu.addFrom(FROM_SQL_BASE);
		dsu.addWhere(WHERE_SQL_BASE);

		// ricerca degli atti aperti, chiusi o entrambi
		if (attiAperti.equals("Si")) {
			dsu.addWhere("patto.codmotivofineatto = mot.codmotivofineatto (+)");
			dsu.addWhere("patto.datFine is null");
		}

		if (attiAperti.equals("No")) {
			dsu.addWhere("patto.codmotivofineatto = mot.codmotivofineatto (+)");
			dsu.addWhereIfFilledStr("patto.codmotivofineatto", motivoFine);
			dsu.addWhere("patto.datFine is not null ");
			dsu.addWhereIfFilledDateBetween("patto.datFine", dataFineAttoDa, dataFineAttoA);
		}

		if (attiAperti.equals("")) {
			dsu.addWhere("patto.codmotivofineatto = mot.codmotivofineatto (+)");
			String addWhereTemp = "";
			// atti aperti Si
			addWhereTemp = addWhereTemp + " (patto.datFine is null ";
			// atti aperti No
			if (!dataFineAttoDa.equals("")) {
				addWhereTemp = addWhereTemp + " or (trunc(patto.datFine) >= TO_DATE('" + dataFineAttoDa
						+ "','DD/MM/YYYY') ";
			}
			if (!dataFineAttoA.equals("")) {
				if (!dataFineAttoDa.equals("")) {
					addWhereTemp = addWhereTemp + " and trunc(patto.datFine) <= TO_DATE('" + dataFineAttoA
							+ "','DD/MM/YYYY') ";
				} else {
					addWhereTemp = addWhereTemp + " or (trunc(patto.datFine) <= TO_DATE('" + dataFineAttoA
							+ "','DD/MM/YYYY') ";
				}
			}
			if (dataFineAttoA.equals("") && dataFineAttoDa.equals("")) {
				addWhereTemp = addWhereTemp + " or (patto.datFine is not null ";
			}
			if (!motivoFine.equals("")) {
				addWhereTemp = addWhereTemp + " and patto.codmotivofineatto = '" + motivoFine + "'";
			}
			addWhereTemp = addWhereTemp + ") ) ";
			dsu.addWhere(addWhereTemp);
		}

		// ricerca dei patti stipulati online
		if (pattiOnLine != null && pattiOnLine.equals("Si")) {
			dsu.addWhere(" nvl(patto.FLGPATTOONLINE , 'N') = 'S' ");

			if (codAccettazionePtOnLine != null && StringUtils.isFilledNoBlank(codAccettazionePtOnLine)) {
				dsu.addWhere(" patto.CODMONOACCETTAZIONE  = '" + codAccettazionePtOnLine.toUpperCase() + "'");
			}

		}

		if (pattiOnLine != null && pattiOnLine.equals("No")) {
			dsu.addWhere(" nvl(patto.FLGPATTOONLINE, 'N') = 'N' ");
		}

		if (tipoRic.equalsIgnoreCase("esatta")) {
			dsu.addWhereIfFilledStrUpper("strnome", nome);
			dsu.addWhereIfFilledStrUpper("strcognome", cognome);
			dsu.addWhereIfFilledStrUpper("strCodiceFiscale", cf);
		} else {
			dsu.addWhereIfFilledStrLikeUpper("strnome", nome, DynamicStatementUtils.DO_LIKE_INIZIA);
			dsu.addWhereIfFilledStrLikeUpper("strcognome", cognome, DynamicStatementUtils.DO_LIKE_INIZIA);
			dsu.addWhereIfFilledStrLikeUpper("strCodiceFiscale", cf, DynamicStatementUtils.DO_LIKE_INIZIA);

		} // else

		dsu.addWhereIfFilledDateBetween("patto.datStipula", datInizioDa, datInizioA);
		dsu.addWhereIfFilledDateBetween("patto.datScadConferma", scadConfDa, scadConfA);

		// if ( (attPrec!=null) && (!attPrec.equals("")) )
		// {
		// buf.append(" AND upper(tipo_att_lav.strDescrizione) = upper('" +
		// attPrec + "')");
		// }

		if (!codCodificaPatto.equals("")) {
			dsu.addWhereIfFilledStr("nvl(patto.codcodificapatto, 'PT297')", codCodificaPatto);
		}

		dsu.addWhereIfFilledStr("patto.flgpatto297", strFlgPatto297);
		dsu.addWhereIfFilledStr("patto.codtipopatto", codTipoPatto);

		if (codCpi != null && !codCpi.equals("")) {
			dsu.addWhere("inf.codCpiTit = '" + codCpi + "' ");
			dsu.addWhere("inf.codMonoTipoCpi = 'C' ");
		}

		// dsu.addWhere("DECODE(inf.DATFINE,NULL,'S','N')='S'");
		dsu.addOrder("patto.DATSTIPULA desc");
		dsu.addOrder("lav.strcognome");
		dsu.addOrder("lav.strnome");

		String query = dsu.getStatement();
		return query;
	}
}
