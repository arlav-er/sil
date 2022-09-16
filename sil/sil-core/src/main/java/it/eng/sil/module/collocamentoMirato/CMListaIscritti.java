package it.eng.sil.module.collocamentoMirato;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.Utils;

public class CMListaIscritti implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMListaIscritti.class.getName());
	private String className = this.getClass().getName();

	private String SELECT_SQL_BASE = "        AN_LAVORATORE.STRCOGNOME COGNOME," + "        AN_LAVORATORE.STRNOME NOME,"
			+ "        AN_LAVORATORE.STRCODICEFISCALE CF," + "        I.PRGCMISCR, " + "        I.CDNLAVORATORE, "
			+ "        AN_LAVORATORE.CDNLAVORATORE CDNLAV, " + "        I.CODCMTIPOISCR, "
			+ "        DE_CM_TIPO_ISCR.STRDESCRIZIONE AS DESCRIZIONEISCR, "
			+ "        to_char(I.DATDATAINIZIO,'DD/MM/YYYY') DATINIZIO, "
			+ "        to_char(I.DATDATAFINE,'DD/MM/YYYY') DATFINE, "
			+ "        I.CODTIPOINVALIDITA,  PR.STRDENOMINAZIONE as PROVINCIA_ISCR, "
			+ "        DE_CM_TIPO_INVALIDITA.STRDESCRIZIONE AS DESCRIZIONEINV, "
			+ "        I.NUMPERCINVALIDITA, DE_CITTADINANZA.STRDESCRIZIONE AS DESCCITTADINANZA, "
			+ "        I.NUMKLOCMISCR, DOC.CODSTATOATTO, ATTO.STRDESCRIZIONE STATOATTO ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();
		DynamicStatementUtils dynamicStatementUt = new DynamicStatementUtils();

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		String tipoIscr = (Utils.notNull((String) req.getAttribute("codCMTipoIscr"))).trim();
		String CODSTATOATTO = (Utils.notNull((String) req.getAttribute("CODSTATOATTO"))).trim();
		String tipoInv = (Utils.notNull((String) req.getAttribute("codCMTipoInvalidita"))).trim();
		String percInvDa = (Utils.notNull((String) req.getAttribute("numPercInvalilditaDa"))).trim();
		String percInvA = (Utils.notNull((String) req.getAttribute("numPercInvalilditaA"))).trim();
		String dataInizioDa = (Utils.notNull((String) req.getAttribute("datinizioda"))).trim();
		String dataInizioA = (Utils.notNull((String) req.getAttribute("datinizioa"))).trim();
		String strNazione = (Utils.notNull((String) req.getAttribute("codCittadinanza"))).trim();
		String codTitolo = (Utils.notNull((String) req.getAttribute("codTitolo"))).trim();
		String codComuneDispo = (Utils.notNull((String) req.getAttribute("codComune"))).trim();
		String codComuniDispo = (Utils.notNull((String) req.getAttribute("INSIEMEDICOMUNI"))).trim();
		String provDispo = (Utils.notNull((String) req.getAttribute("CODPROVINCIA"))).trim();
		String codProfilo = (Utils.notNull((String) req.getAttribute("CODPROFILO"))).trim();
		String COD_PROVINCIA_ISCR = Utils.notNull((String) req.getAttribute("PROVINCIA_ISCR"));

		// Preparo la query
		if (req.containsAttribute("stampa")) {
			SELECT_SQL_BASE = SELECT_SQL_BASE
					+ ", DE_TITOLO.STRDESCRIZIONE DESCTITOLOSTUDIO, DE_SUGGER_INSLAV.STRDESCRIZIONE DESCPROFILO ";
		}
		dynamicStatementUt.addSelect(SELECT_SQL_BASE);
		dynamicStatementUt.addFrom("AM_CM_ISCR I");
		dynamicStatementUt.addFrom("DE_CM_TIPO_ISCR");
		dynamicStatementUt.addFrom("DE_CM_TIPO_INVALIDITA");
		dynamicStatementUt.addFrom("AN_LAVORATORE");
		dynamicStatementUt.addFrom("AM_DOCUMENTO_COLL DOC_COLL");
		dynamicStatementUt.addFrom("AM_DOCUMENTO DOC");
		dynamicStatementUt.addFrom("DE_STATO_ATTO ATTO");
		dynamicStatementUt.addFrom("DE_CITTADINANZA");
		dynamicStatementUt.addFrom("DE_PROVINCIA PR");

		if (req.containsAttribute("stampa")) {
			if ((codTitolo != null) && (!codTitolo.equals(""))) {
				dynamicStatementUt.addFrom("(select max(st2.codtitolo) maxTitolo, st2.cdnlavoratore from pr_studio st1 "
						+ " inner join pr_studio st2 on st1.cdnlavoratore = st2.cdnlavoratore "
						+ " where st1.codtitolo = '" + codTitolo + "' group by st2.cdnlavoratore) TABMAXTITOLO");
			} else {
				dynamicStatementUt
						.addFrom("(select max(st1.codtitolo) maxTitolo, lav.cdnlavoratore from an_lavoratore lav "
								+ " left join pr_studio st1 on (lav.cdnlavoratore = st1.cdnlavoratore) "
								+ " group by lav.cdnlavoratore) TABMAXTITOLO");
			}
			dynamicStatementUt.addFrom("DE_TITOLO");
		} else {
			if ((codTitolo != null) && (!codTitolo.equals(""))) {
				dynamicStatementUt.addFrom("PR_STUDIO");
			}
		}

		if (req.containsAttribute("stampa")) {
			dynamicStatementUt.addFrom("CM_DIAGNOSI_FUNZIONALE");
			dynamicStatementUt.addFrom("DE_SUGGER_INSLAV");
		} else {
			if (codProfilo != null && !codProfilo.equals("")) {
				dynamicStatementUt.addFrom("CM_DIAGNOSI_FUNZIONALE");
			}
		}

		dynamicStatementUt.addWhere("  AN_LAVORATORE.CDNLAVORATORE = DECRYPT(I.CDNLAVORATORE, '" + encryptKey + "') ");
		dynamicStatementUt.addWhere("  I.CODCMTIPOISCR     = DE_CM_TIPO_ISCR.CODCMTIPOISCR(+) ");
		dynamicStatementUt.addWhere("  I.CODTIPOINVALIDITA = DE_CM_TIPO_INVALIDITA.CODTIPOINVALIDITA(+) ");
		dynamicStatementUt.addWhere("  I.DATDATAFINE is null ");
		dynamicStatementUt.addWhere("  I.PRGCMISCR = DOC_COLL.STRCHIAVETABELLA ");
		dynamicStatementUt.addWhere("  DOC_COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO ");
		dynamicStatementUt.addWhere("  DOC.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE ");
		dynamicStatementUt.addWhere("  DOC.CODTIPODOCUMENTO = 'L68' ");
		dynamicStatementUt.addWhere("  DOC.CODSTATOATTO = ATTO.CODSTATOATTO ");
		dynamicStatementUt.addWhere("  AN_LAVORATORE.CODCITTADINANZA = DE_CITTADINANZA.CODCITTADINANZA ");
		dynamicStatementUt.addWhere("  I.CODPROVINCIA = PR.CODPROVINCIA ");

		if (req.containsAttribute("stampa")) {
			if ((codTitolo != null) && (!codTitolo.equals(""))) {
				dynamicStatementUt.addWhere("  TABMAXTITOLO.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE ");
				dynamicStatementUt.addWhere("  TABMAXTITOLO.maxTitolo = DE_TITOLO.CODTITOLO ");

			} else {
				dynamicStatementUt.addWhere("  TABMAXTITOLO.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE (+)");
				dynamicStatementUt.addWhere("  TABMAXTITOLO.maxTitolo = DE_TITOLO.CODTITOLO (+)");
			}
		} else {
			if ((codTitolo != null) && (!codTitolo.equals(""))) {
				dynamicStatementUt.addWhere("  PR_STUDIO.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE ");
			}
		}

		if (req.containsAttribute("stampa")) {
			dynamicStatementUt.addWhere("  DECRYPT(I.CDNLAVORATORE, '" + encryptKey
					+ "') =  DECRYPT(CM_DIAGNOSI_FUNZIONALE.CDNLAVORATORE (+), '" + encryptKey + "')");
			dynamicStatementUt
					.addWhere("  CM_DIAGNOSI_FUNZIONALE.CODSUGGERIMENTO = DE_SUGGER_INSLAV.CODSUGGERIMENTO (+)");
		} else {
			if (codProfilo != null && !codProfilo.equals("")) {
				dynamicStatementUt
						.addWhere("  AN_LAVORATORE.CDNLAVORATORE = DECRYPT(CM_DIAGNOSI_FUNZIONALE.CDNLAVORATORE, '"
								+ encryptKey + "') ");
			}

			if (codProfilo != null && !codProfilo.equals("")) {
				dynamicStatementUt.addWhereIfFilledStrUpper("CM_DIAGNOSI_FUNZIONALE.CODSUGGERIMENTO", codProfilo);
			}
		}

		if (codComuneDispo != null && !codComuneDispo.equals("")) {
			dynamicStatementUt.addWhere(" AN_LAVORATORE.CDNLAVORATORE IN "
					+ " (SELECT PR_MANSIONE.CDNLAVORATORE FROM PR_MANSIONE, PR_DIS_COMUNE "
					+ " WHERE PR_MANSIONE.PRGMANSIONE = PR_DIS_COMUNE.PRGMANSIONE AND PR_DIS_COMUNE.CODCOM = '"
					+ codComuneDispo + "') ");
		} else {
			if (codComuniDispo != null && !codComuniDispo.equals("")) {
				Vector vettComuni = StringUtils.split(codComuniDispo, ",");
				int sizeComuni = vettComuni.size();
				String listaComuni = "";
				for (int i = 0; i < sizeComuni; i++) {
					String comuneCurr = (String) vettComuni.get(i);
					if (listaComuni.equals("")) {
						listaComuni = "('" + comuneCurr + "'";
					} else {
						listaComuni = listaComuni + ", '" + comuneCurr + "'";
					}
				}
				listaComuni = listaComuni + ")";
				dynamicStatementUt.addWhere(" AN_LAVORATORE.CDNLAVORATORE IN "
						+ " (SELECT PR_MANSIONE.CDNLAVORATORE FROM PR_MANSIONE, PR_DIS_COMUNE "
						+ " WHERE PR_MANSIONE.PRGMANSIONE = PR_DIS_COMUNE.PRGMANSIONE AND PR_DIS_COMUNE.CODCOM IN "
						+ listaComuni + ") ");
			} else {
				if (provDispo != null && !provDispo.equals("")) {
					dynamicStatementUt.addWhere(" AN_LAVORATORE.CDNLAVORATORE IN "
							+ " (SELECT PR_MANSIONE.CDNLAVORATORE FROM PR_MANSIONE, PR_DIS_PROVINCIA "
							+ " WHERE PR_MANSIONE.PRGMANSIONE = PR_DIS_PROVINCIA.PRGMANSIONE AND PR_DIS_PROVINCIA.CODPROVINCIA = '"
							+ provDispo + "') ");
				}
			}
		}

		if ((tipoIscr != null) && (!tipoIscr.equals(""))) {
			dynamicStatementUt.addWhereIfFilledStrUpper("I.CODCMTIPOISCR", tipoIscr);
		}

		if ((CODSTATOATTO != null) && (!CODSTATOATTO.equals(""))) {
			dynamicStatementUt.addWhereIfFilledStrUpper("DOC.CODSTATOATTO", CODSTATOATTO);
		}

		if ((tipoInv != null) && (!tipoInv.equals(""))) {
			dynamicStatementUt.addWhereIfFilledStrUpper("I.CODTIPOINVALIDITA", tipoInv);
		}

		if ((strNazione != null) && (!strNazione.equals(""))) {
			dynamicStatementUt.addWhereIfFilledStrUpper("AN_LAVORATORE.CODCITTADINANZA", strNazione);
		}

		if (!req.containsAttribute("stampa")) {
			if ((codTitolo != null) && (!codTitolo.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrUpper("PR_STUDIO.CODTITOLO", codTitolo);
			}
		}
		if ((COD_PROVINCIA_ISCR != null) && (!COD_PROVINCIA_ISCR.equals(""))) {
			dynamicStatementUt.addWhereIfFilledStr("upper(I.CODPROVINCIA)", COD_PROVINCIA_ISCR.toUpperCase());
		}
		dynamicStatementUt.addWhereIfFilledNumBetween("I.NUMPERCINVALIDITA", percInvDa, percInvA);
		dynamicStatementUt.addWhereIfFilledDateBetween("I.DATDATAINIZIO", dataInizioDa, dataInizioA);

		dynamicStatementUt.addOrder("I.DATDATAINIZIO DESC");
		dynamicStatementUt.addOrder("cognome");

		_logger.debug(className + "::Stringa di ricerca:" + dynamicStatementUt.toString());

		return (dynamicStatementUt.getStatement());
	}

}