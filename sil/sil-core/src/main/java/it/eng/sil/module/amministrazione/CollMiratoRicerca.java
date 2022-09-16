package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.Utils;

public class CollMiratoRicerca implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CollMiratoRicerca.class.getName());
	private String className = this.getClass().getName();

	private String SELECT_SQL_BASE = "        AN_LAVORATORE.STRCOGNOME COGNOME," + "        AN_LAVORATORE.STRNOME NOME,"
			+ "        AN_LAVORATORE.STRCODICEFISCALE CF," + "        I.PRGCMISCR, " + "        I.CDNLAVORATORE, "
			+ "        I.CODCMTIPOISCR, " + "        DE_CM_TIPO_ISCR.STRDESCRIZIONE AS DESCRIZIONEISCR, "
			+ "        to_char(I.DATDATAINIZIO,'DD/MM/YYYY') DATINIZIO, "
			+ "        to_char(I.DATDATAFINE,'DD/MM/YYYY') DATFINE, "
			+ "        I.CODTIPOINVALIDITA,  PR.STRDENOMINAZIONE as PROVINCIA_ISCR, "
			+ "        DE_CM_TIPO_INVALIDITA.STRDESCRIZIONE AS DESCRIZIONEINV, " + "        I.NUMPERCINVALIDITA, "
			+ "        I.NUMKLOCMISCR ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();
		DynamicStatementUtils dynamicStatementUt = new DynamicStatementUtils();

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		// Recupero i dati dalla request
		String nome = (String) req.getAttribute("nome");
		String cognome = (String) req.getAttribute("cognome");
		String cf = (String) req.getAttribute("CF");
		String tipoIscr = (String) req.getAttribute("codCMTipoIscr");
		String CODSTATOATTO = "";
		if (Sottosistema.CM.isOn()) {
			CODSTATOATTO = (String) req.getAttribute("CODSTATOATTO");
		}
		String tipoInv = (String) req.getAttribute("codCMTipoInvalidita");
		String percInv = (String) req.getAttribute("numPercInvalildita");
		String tipoRic = StringUtils.getAttributeStrNotNull(req, "tipoRicerca");
		String codGradoOcc = StringUtils.getAttributeStrNotNull(req, "codGradoOcc");
		String codCpi = Utils.notNull((String) req.getAttribute("codCpi"));
		String dataInizioDa = Utils.notNull((String) req.getAttribute("datinizioda"));
		String dataInizioA = Utils.notNull((String) req.getAttribute("datinizioa"));
		String COD_PROVINCIA_ISCR = Utils.notNull((String) req.getAttribute("PROVINCIA_ISCR"));

		if (Sottosistema.CM.isOn()) {
			SELECT_SQL_BASE = SELECT_SQL_BASE + ", DOC.CODSTATOATTO, ATTO.STRDESCRIZIONE STATOATTO ";
		}

		// Preparo la query
		dynamicStatementUt.addSelect(SELECT_SQL_BASE);

		dynamicStatementUt.addFrom("AM_CM_ISCR I");
		dynamicStatementUt.addFrom("DE_CM_TIPO_ISCR");
		dynamicStatementUt.addFrom("DE_CM_TIPO_INVALIDITA");
		dynamicStatementUt.addFrom("AN_LAVORATORE");
		dynamicStatementUt.addFrom("an_lav_storia_inf inf");
		dynamicStatementUt.addFrom("DE_PROVINCIA PR");
		if (Sottosistema.CM.isOff()) {
			dynamicStatementUt.addFrom("an_lav_storia_inf_coll coll");
			dynamicStatementUt.addFrom("am_elenco_anagrafico ea");
		}
		if (Sottosistema.CM.isOn()) {
			dynamicStatementUt.addFrom("AM_DOCUMENTO_COLL DOC_COLL");
			dynamicStatementUt.addFrom("AM_DOCUMENTO DOC");
			dynamicStatementUt.addFrom("DE_STATO_ATTO ATTO");
		}

		if (!codGradoOcc.equals("")) {
			dynamicStatementUt.addFrom("PR_DISPO_L68 DIS");
			dynamicStatementUt.addWhere("dis.cdnlavoratore = inf.CDNLAVORATORE");
			dynamicStatementUt.addWhere("DIS.CODGRADOOCC = '" + codGradoOcc + "'");
		}

		dynamicStatementUt.addWhere("  AN_LAVORATORE.CDNLAVORATORE = DECRYPT(I.CDNLAVORATORE, '" + encryptKey + "') ");
		dynamicStatementUt.addWhere("  I.CODCMTIPOISCR     = DE_CM_TIPO_ISCR.CODCMTIPOISCR(+) ");
		dynamicStatementUt.addWhere("  I.CODTIPOINVALIDITA = DE_CM_TIPO_INVALIDITA.CODTIPOINVALIDITA(+) ");
		dynamicStatementUt.addWhere("  I.DATDATAFINE is null ");
		dynamicStatementUt.addWhere("  inf.cdnlavoratore = AN_LAVORATORE.CDNLAVORATORE ");
		dynamicStatementUt.addWhere("  I.CODPROVINCIA = PR.CODPROVINCIA ");
		dynamicStatementUt.addWhere("  inf.datFine is null ");
		if (Sottosistema.CM.isOff()) {
			dynamicStatementUt.addWhere("  inf.prgLavStoriaInf = coll.prgLavStoriaInf ");
			dynamicStatementUt.addWhere("  coll.codlsttab = 'EA' ");
			dynamicStatementUt.addWhere("  coll.strChiaveTabella = ea.prgElencoAnagrafico ");
			dynamicStatementUt.addWhere("  ea.datCan is null ");
		}
		if (Sottosistema.CM.isOn()) {
			dynamicStatementUt.addWhere("  I.PRGCMISCR = DOC_COLL.STRCHIAVETABELLA ");
			dynamicStatementUt.addWhere("  DOC_COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO ");
			dynamicStatementUt.addWhere("  DOC.CODTIPODOCUMENTO = 'L68' ");
			dynamicStatementUt.addWhere("  DOC.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE ");
			dynamicStatementUt.addWhere("  DOC.CODSTATOATTO = ATTO.CODSTATOATTO ");

		}

		if ((codCpi != null) && (!codCpi.trim().equals(""))) {
			dynamicStatementUt.addWhereIfFilledStr("inf.codCpiTit", codCpi);
			dynamicStatementUt.addWhereIfFilledStr("inf.codMonoTipoCpi", "C");
		}
		if (tipoRic.equalsIgnoreCase("esatta")) {
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				dynamicStatementUt.addWhereIfFilledStr("upper(strnome)", nome.toUpperCase());
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				cognome = StringUtils.replace(cognome, "'", "''");
				dynamicStatementUt.addWhereIfFilledStr("upper(strcognome)", cognome.toUpperCase());
			}

			if ((cf != null) && (!cf.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStr("upper(strCodiceFiscale)", cf.toUpperCase());
			}

		} else {
			if ((nome != null) && (!nome.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("strnome", nome, DynamicStatementUtils.DO_LIKE_INIZIA);
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("strcognome", cognome,
						DynamicStatementUtils.DO_LIKE_INIZIA);
			}

			if ((cf != null) && (!cf.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrLikeUpper("strCodiceFiscale", cf,
						DynamicStatementUtils.DO_LIKE_INIZIA);
			}
		}
		if ((tipoIscr != null) && (!tipoIscr.equals(""))) {
			dynamicStatementUt.addWhereIfFilledStrUpper("I.CODCMTIPOISCR", tipoIscr);
		}

		if (Sottosistema.CM.isOn()) {
			if ((CODSTATOATTO != null) && (!CODSTATOATTO.equals(""))) {
				dynamicStatementUt.addWhereIfFilledStrUpper("DOC.CODSTATOATTO", CODSTATOATTO);
			}
		}

		if ((tipoInv != null) && (!tipoInv.equals(""))) {
			dynamicStatementUt.addWhereIfFilledStrUpper("I.CODTIPOINVALIDITA", tipoInv);
		}

		if ((percInv != null) && (!percInv.equals(""))) {
			dynamicStatementUt.addWhereIfFilledStrUpper("I.NUMPERCINVALIDITA", percInv);
		}
		if ((COD_PROVINCIA_ISCR != null) && (!COD_PROVINCIA_ISCR.equals(""))) {
			dynamicStatementUt.addWhereIfFilledStr("upper(I.CODPROVINCIA)", COD_PROVINCIA_ISCR.toUpperCase());
		}
		dynamicStatementUt.addWhereIfFilledDateBetween("I.DATDATAINIZIO", dataInizioDa, dataInizioA);

		dynamicStatementUt.addOrder("I.DATDATAINIZIO DESC");
		dynamicStatementUt.addOrder("cognome");

		_logger.debug(className + "::Stringa di ricerca:" + dynamicStatementUt.toString());

		return (dynamicStatementUt.getStatement());
	}

} // class CollMiratoRicerca