package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;

/**
 * Effettua la ricerca dinamica sullo scadenziario
 * 
 * @author Giovanni Landi
 * 
 */
public class DynStatementRicercaScadenze implements IDynamicStatementProvider {
	private static final int SCADENZA_ORGANIZZATIVA_CONTATTI_LAVORATORI = 1;
	private static final int SCADENZA_ORGANIZZATIVA_CONTATTI_AZIENDE = 2;
	private static final int SCADENZA_ORGANIZZATIVA_VALIDITA_SCHEDA_LAV = 3;
	private static final int SCADENZA_ORGANIZZATIVA_VALIDITA_PERMESSO_SOGG = 4;
	private static final int CLIC_LAVORO_LISTA_CANDIDATURE_INVIATE = 5;

	private static final int SCADENZA_AMMINISTRATIVA_DATE_PERCORSI_LAV = 101;
	private static final int SCADENZA_AMMINISTRATIVA_COLLOQUIO_LAV_POR = 102;
	private static final int SCADENZA_AMMINISTRATIVA_STIPULA_PATTO_LAV = 103;
	private static final int SCADENZA_VALIDITA_PATTO = 104;

	private static final int SEGNALAZIONE_LAV_APP_CAMBIO_STATO_OCCUPAZ = 1001;
	private static final int SEGNALAZIONE_LAV_SENZA_DISPONIBILITA_TERR = 1002;
	private static final int SEGNALAZIONE_LAV_ESCLUSI_ROSA = 1003;
	private static final int SEGNALAZIONE_LAV_PRONTI_NO_MANSIONI = 1004;

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();
		String SELECT_SQL_BASE = "";
		String FROM = null;
		String WHERE = null;
		String ORDER = null;

		String strMotivoContatto = "";
		String data_dal = req.containsAttribute("DATADAL") ? req.getAttribute("DATADAL").toString() : "";
		String data_al = req.containsAttribute("DATAAL") ? req.getAttribute("DATAAL").toString() : "";
		String strCodScadenza = req.containsAttribute("SCADENZIARIO") ? req.getAttribute("SCADENZIARIO").toString()
				: "";
		String codCpi = req.containsAttribute("CODCPI") ? req.getAttribute("CODCPI").toString() : "";
		String strFiltra = req.containsAttribute("FILTRALISTA") ? req.getAttribute("FILTRALISTA").toString() : "";

		String codificaPatto = req.containsAttribute("TIPOLOGIAPATTO") ? req.getAttribute("TIPOLOGIAPATTO").toString()
				: "";

		int nCodScadenza = 0;
		if (strCodScadenza.compareTo("ORG1") == 0) {
			nCodScadenza = SCADENZA_ORGANIZZATIVA_CONTATTI_LAVORATORI;
		} else if (strCodScadenza.compareTo("ORG2") == 0) {
			nCodScadenza = SCADENZA_ORGANIZZATIVA_CONTATTI_AZIENDE;
		} else if (strCodScadenza.compareTo("ORG3") == 0) {
			nCodScadenza = SCADENZA_ORGANIZZATIVA_VALIDITA_SCHEDA_LAV;
		} else if (strCodScadenza.compareTo("ORG4") == 0) {
			nCodScadenza = SCADENZA_ORGANIZZATIVA_VALIDITA_PERMESSO_SOGG;
		} else if (strCodScadenza.compareTo("AMM1") == 0) {
			nCodScadenza = SCADENZA_AMMINISTRATIVA_DATE_PERCORSI_LAV;
		} else if (strCodScadenza.compareTo("AMM2") == 0) {
			nCodScadenza = SCADENZA_AMMINISTRATIVA_COLLOQUIO_LAV_POR;
		} else if (strCodScadenza.compareTo("AMM3") == 0) {
			nCodScadenza = SCADENZA_AMMINISTRATIVA_STIPULA_PATTO_LAV;
		} else if (strCodScadenza.compareTo("AMM4") == 0) {
			nCodScadenza = SCADENZA_VALIDITA_PATTO;
		} else if (strCodScadenza.compareTo("VER1") == 0) {
			nCodScadenza = SEGNALAZIONE_LAV_APP_CAMBIO_STATO_OCCUPAZ;
		} else if (strCodScadenza.compareTo("VER2") == 0) {
			nCodScadenza = SEGNALAZIONE_LAV_SENZA_DISPONIBILITA_TERR;
		} else if (strCodScadenza.compareTo("VER3") == 0) {
			nCodScadenza = SEGNALAZIONE_LAV_ESCLUSI_ROSA;
		} else if (strCodScadenza.compareTo("VER4") == 0) {
			nCodScadenza = SEGNALAZIONE_LAV_PRONTI_NO_MANSIONI;
		} else if (strCodScadenza.compareTo("VER5") == 0) {
			nCodScadenza = CLIC_LAVORO_LISTA_CANDIDATURE_INVIATE;
		}

		switch (nCodScadenza) {

		case SCADENZA_ORGANIZZATIVA_CONTATTI_LAVORATORI:
			strMotivoContatto = req.containsAttribute("MOTIVO_CONTATTO")
					? req.getAttribute("MOTIVO_CONTATTO").toString()
					: "";
			SELECT_SQL_BASE = "select an.CDNLAVORATORE, " + "an.STRCOGNOME, " + "an.STRNOME, " + "an.STRCODICEFISCALE, "
					+ "to_char(DATNASC,'dd/mm/yyyy') as DATNASC, "
					+ "to_char(DATCONTATTO,'dd/mm/yyyy') as STRDATCONTATTO, "
					+ "  nvl(an. strindirizzodom,'&nbsp;') as strindirizzodom, "
					+ "to_char(DATENTROIL,'dd/mm/yyyy') as STRDATENTROIL , " + "contatto1.prgcontatto, "

					+ "de.strdenominazione  as STRcittadomi "

					+ "from an_lavoratore an, ag_contatto contatto1,  de_comune de "
					+ "where contatto1.CDNLAVORATORE = an.CDNLAVORATORE " + "and  an.codcomdom = de.codcom "

					+ "AND contatto1.CODCPICONTATTO = '" + codCpi + "' " + "AND contatto1.DATENTROIL is not null";

			if ((data_dal != null) && (!data_dal.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND contatto1.DATENTROIL >= to_date('" + data_dal
						+ "','dd/mm/yyyy')";
			}

			if ((data_al != null) && (!data_al.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND contatto1.DATENTROIL <= to_date('" + data_al
						+ "','dd/mm/yyyy')";
			}

			if (((data_dal == null) || (data_dal.equals(""))) && ((data_al == null) || (data_al.equals("")))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND contatto1.DATENTROIL >= trunc(sysdate)";
			}

			if ((strMotivoContatto != null) && (!strMotivoContatto.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND contatto1.PRGMOTCONTATTO = " + strMotivoContatto;
			}

			if (strFiltra.compareTo("1") == 0) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists (select contatto2.DATCONTATTO, "
						+ "contatto2.PRGCONTATTO from ag_contatto contatto2 " + "where CONTATTO2.CODCPICONTATTO = '"
						+ codCpi + "'";

				SELECT_SQL_BASE = SELECT_SQL_BASE
						+ " AND to_date(to_char(contatto2.DATCONTATTO,'dd/mm/yyyy') || contatto2.STRORACONTATTO, 'dd/mm/yyyy hh24:mi') > "
						+ "to_date(to_char(contatto1.DATCONTATTO,'dd/mm/yyyy') || contatto1.STRORACONTATTO, 'dd/mm/yyyy hh24:mi') "
						+ "AND contatto1.PRGCONTATTO <> contatto2.PRGCONTATTO "
						+ "AND CONTATTO1.CDNLAVORATORE = CONTATTO2.CDNLAVORATORE)";
			}
			SELECT_SQL_BASE = SELECT_SQL_BASE + " order by DATENTROIL asc";
			break;

		case SCADENZA_ORGANIZZATIVA_CONTATTI_AZIENDE:
			strMotivoContatto = req.containsAttribute("MOTIVO_CONTATTO")
					? req.getAttribute("MOTIVO_CONTATTO").toString()
					: "";
			SELECT_SQL_BASE = "select de_tipo_azienda.strdescrizione as descrizionetipo, "
					+ "AN_AZIENDA.strcodicefiscale as codicefiscale, " + "AN_AZIENDA.prgazienda as codazienda, "
					+ "AN_UNITA_AZIENDA.prgunita as codunitaazienda, " + "AN_AZIENDA.strpartitaiva as piva, "
					+ "AN_AZIENDA.strragionesociale as ragionesociale, "
					+ "AN_UNITA_AZIENDA.strindirizzo as indirizzo, "
					+ "to_char(DATCONTATTO,'dd/mm/yyyy') as STRDATCONTATTO, "
					+ "to_char(DATENTROIL,'dd/mm/yyyy') as  STRDATENTROIL, " + "ag_contatto.prgcontatto, "
					+ "de_comune.strdenominazione as comune " + "from de_tipo_azienda, AN_UNITA_AZIENDA "
					+ "inner join AN_AZIENDA on (an_unita_azienda.prgazienda = an_azienda.prgazienda) "
					+ "inner join de_comune on (an_unita_azienda.codcom = de_comune.codcom) "
					+ "inner join ag_contatto on (an_unita_azienda.prgazienda = ag_contatto.prgazienda and an_unita_azienda.prgunita = ag_contatto.prgunita) "
					+ "where an_azienda.codtipoazienda = de_tipo_azienda.codtipoazienda "
					+ "AND ag_contatto.CODCPICONTATTO = '" + codCpi + "' " + "AND ag_contatto.DATENTROIL is not null";

			if ((data_dal != null) && (!data_dal.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND ag_contatto.DATENTROIL >= to_date('" + data_dal
						+ "','dd/mm/yyyy')";
			}

			if ((data_al != null) && (!data_al.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND ag_contatto.DATENTROIL <= to_date('" + data_al
						+ "','dd/mm/yyyy')";
			}

			if (((data_dal == null) || (data_dal.equals(""))) && ((data_al == null) || (data_al.equals("")))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND ag_contatto.DATENTROIL >= trunc(sysdate)";
			}

			if ((strMotivoContatto != null) && (!strMotivoContatto.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND ag_contatto.PRGMOTCONTATTO = " + strMotivoContatto;
			}

			if (strFiltra.compareTo("1") == 0) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists (select contatto2.DATCONTATTO, "
						+ "contatto2.PRGCONTATTO from ag_contatto contatto2 " + "where CONTATTO2.CODCPICONTATTO = '"
						+ codCpi + "'";

				SELECT_SQL_BASE = SELECT_SQL_BASE
						+ " AND to_date(to_char(contatto2.DATCONTATTO,'dd/mm/yyyy') || contatto2.STRORACONTATTO, 'dd/mm/yyyy hh24:mi') > "
						+ "to_date(to_char(ag_contatto.DATCONTATTO,'dd/mm/yyyy') || ag_contatto.STRORACONTATTO, 'dd/mm/yyyy hh24:mi') "
						+ "AND ag_contatto.PRGCONTATTO <> contatto2.PRGCONTATTO "
						+ "AND ag_contatto.prgazienda = CONTATTO2.prgazienda "
						+ "and ag_contatto.prgunita = CONTATTO2.prgunita)";

			}
			SELECT_SQL_BASE = SELECT_SQL_BASE + " order by DATENTROIL asc";
			break;

		case SCADENZA_ORGANIZZATIVA_VALIDITA_SCHEDA_LAV:
			String strCodTipoValidita = (String) req.getAttribute("CODTIPOVALIDITA");

			String strStatoValidaCurr = req.containsAttribute("statoValCV") ? req.getAttribute("statoValCV").toString()
					: "";

			String strCpiAppoggio = req.containsAttribute("CodCpiApp") ? req.getAttribute("CodCpiApp").toString() : "";

			SELECT_SQL_BASE = "select an_lavoratore.CDNLAVORATORE, " + "an_lavoratore.STRCOGNOME, "
					+ "an_lavoratore.STRNOME, " + "an_lavoratore.STRCODICEFISCALE, "
					+ "to_char(DATNASC,'dd/mm/yyyy') as DATNASC, "
					+ "to_char(DATFINECURR,'dd/mm/yyyy') as STRDATFINECURR " + "from an_lavoratore "
					+ "inner join pr_validita on (an_lavoratore.CDNLAVORATORE = pr_validita.CDNLAVORATORE) ";

			if (!strCpiAppoggio.equals("")) {
				if ((codCpi != null) && (codCpi.compareTo("") != 0)) {
					SELECT_SQL_BASE = SELECT_SQL_BASE
							+ " inner join an_lav_storia_inf on (an_lavoratore.cdnLavoratore= an_lav_storia_inf.cdnLavoratore and DECODE(an_lav_storia_inf.DATFINE, NULL, 'S','N') = 'S') ";
				}
			}

			SELECT_SQL_BASE = SELECT_SQL_BASE + " where pr_validita.DATFINECURR is not null ";

			if ((strCodTipoValidita != null) && (strCodTipoValidita.compareTo("") != 0)) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND  pr_validita.CODTIPOVALIDITA = '" + strCodTipoValidita + "'";
			}

			if ((strStatoValidaCurr != null) && (strStatoValidaCurr.compareTo("") != 0)) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND  pr_validita.CODSTATOLAV = '" + strStatoValidaCurr + "'";
			}

			if (!strCpiAppoggio.equals("")) {
				if ((codCpi != null) && (codCpi.compareTo("") != 0)) {
					SELECT_SQL_BASE = SELECT_SQL_BASE + " AND an_lav_storia_inf.CODCPITIT = '" + codCpi + "'"
							+ " AND (an_lav_storia_inf.CODMONOTIPOCPI = 'T' OR an_lav_storia_inf.CODMONOTIPOCPI = 'C')";
				}
			}

			if ((data_dal != null) && (!data_dal.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND trunc(pr_validita.datfinecurr) >= to_date('" + data_dal
						+ "','dd/mm/yyyy')";
			}

			if ((data_al != null) && (!data_al.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND trunc(pr_validita.datfinecurr) <= to_date('" + data_al
						+ "','dd/mm/yyyy')";
			}

			if (((data_dal == null) || (data_dal.equals(""))) && ((data_al == null) || (data_al.equals("")))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND pr_validita.DATFINECURR <= trunc(sysdate)";
			}

			if (strFiltra.compareTo("1") == 0) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists "
						+ "(select ag_lavoratore.cdnlavoratore from ag_lavoratore "
						+ "inner join ag_agenda on (ag_lavoratore.prgappuntamento = ag_agenda.prgappuntamento ) ";

				if (!strCpiAppoggio.equals("")) {
					if ((codCpi != null) && (codCpi.compareTo("") != 0)) {
						SELECT_SQL_BASE = SELECT_SQL_BASE + "and ag_lavoratore.CODCPI = ag_agenda.CODCPI ";
					}
				}
				/*
				 * + "where ag_agenda.CODCPI = '" + codCpi + "' and "
				 */
				SELECT_SQL_BASE = SELECT_SQL_BASE
						+ "where ag_lavoratore.cdnlavoratore = an_lavoratore.cdnlavoratore and "
						+ "ag_agenda.DTMDATAORA < pr_validita.DATFINECURR and "
						+ "ag_agenda.DTMDATAORA >= trunc(sysdate) ";

				if (!strCpiAppoggio.equals("")) {
					if ((codCpi != null) && (codCpi.compareTo("") != 0)) {
						SELECT_SQL_BASE = SELECT_SQL_BASE + " AND ag_agenda.CODCPI = '" + codCpi + "'";
					}
				}

				SELECT_SQL_BASE = SELECT_SQL_BASE + " ) ";
			}

			SELECT_SQL_BASE = SELECT_SQL_BASE + " order by DATFINECURR asc";
			break;

		case SCADENZA_ORGANIZZATIVA_VALIDITA_PERMESSO_SOGG:
			SELECT_SQL_BASE = "select an_lavoratore.CDNLAVORATORE, " + "an_lavoratore.STRCOGNOME, "
					+ "an_lavoratore.STRNOME, " + "an_lavoratore.STRCODICEFISCALE, "
					+ "to_char(DATNASC,'dd/mm/yyyy') as DATNASC, "
					+ "to_char(max(DATSCADENZA),'dd/mm/yyyy') as DATSCADENZA " + "from an_lavoratore "
					+ "inner join AM_EX_PERM_SOGG on (an_lavoratore.CDNLAVORATORE = AM_EX_PERM_SOGG.CDNLAVORATORE) "
					+ "where nvl(AM_EX_PERM_SOGG.CODSTATUS, '1') in ('1', '3') "
					+ "AND AM_EX_PERM_SOGG.DATSCADENZA is not null AND AM_EX_PERM_SOGG.DATFINE is null ";

			if ((data_dal != null) && (!data_dal.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND AM_EX_PERM_SOGG.DATSCADENZA >= to_date('" + data_dal
						+ "','dd/mm/yyyy')";
			}

			if ((data_al != null) && (!data_al.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND AM_EX_PERM_SOGG.DATSCADENZA <= to_date('" + data_al
						+ "','dd/mm/yyyy')";
			}

			if (((data_dal == null) || (data_dal.equals(""))) && ((data_al == null) || (data_al.equals("")))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND AM_EX_PERM_SOGG.DATSCADENZA <= trunc(sysdate)";
			}

			// Filtro sulla Competenza in base al parametro CODCPI
			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND EXISTS (" + "select an_lav_storia_inf.cdnLavoratore "
					+ "from an_lav_storia_inf " + "where an_lav_storia_inf.cdnLavoratore = an_lavoratore.cdnLavoratore "
					+ "and an_lav_storia_inf.datFine is null " + "and an_lav_storia_inf.codCpiTit='" + codCpi + "' "
					+ "and (an_lav_storia_inf.codMonoTipoCpi='T' or an_lav_storia_inf.codMonoTipoCpi='C')" + ") ";

			if (strFiltra.compareTo("1") == 0) {
				// filtra per contatti
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists "
						+ "(select ag_contatto.cdnlavoratore from ag_contatto " + "where ag_contatto.CODCPICONTATTO = '"
						+ codCpi + "' and " + "ag_contatto.cdnlavoratore = an_lavoratore.CDNLAVORATORE and "
						+ "ag_contatto.DATCONTATTO is not null and "
						+ "ag_contatto.DATCONTATTO < AM_EX_PERM_SOGG.DATSCADENZA and "
						+ "ag_contatto.DATCONTATTO > AM_EX_PERM_SOGG.DATRICHIESTA)";
			} else {
				if (strFiltra.compareTo("2") == 0) {
					// filtra per appuntamenti
					SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists "
							+ "(select ag_lavoratore.cdnlavoratore from ag_lavoratore "
							+ "inner join ag_agenda on (ag_lavoratore.prgappuntamento = ag_agenda.prgappuntamento "
							+ "and ag_lavoratore.CODCPI = ag_agenda.CODCPI) " + "where ag_agenda.CODCPI = '" + codCpi
							+ "' " + "and ag_lavoratore.cdnlavoratore = an_lavoratore.CDNLAVORATORE "
							+ "and ag_agenda.DTMDATAORA < AM_EX_PERM_SOGG.DATSCADENZA and "
							+ "ag_agenda.DTMDATAORA > trunc(sysdate))";
				}
			}
			SELECT_SQL_BASE = SELECT_SQL_BASE + " group by an_lavoratore.CDNLAVORATORE, "
					+ "an_lavoratore.STRCOGNOME, an_lavoratore.STRNOME, " + "an_lavoratore.STRCODICEFISCALE, DATNASC";
			SELECT_SQL_BASE = SELECT_SQL_BASE + " ORDER BY TO_DATE(DATSCADENZA,'dd/mm/yyyy') desc";
			break;

		case SCADENZA_AMMINISTRATIVA_DATE_PERCORSI_LAV:
			SELECT_SQL_BASE = "select an_lavoratore.CDNLAVORATORE, " + "an_lavoratore.STRCOGNOME, "
					+ "an_lavoratore.STRNOME, " + "an_lavoratore.STRCODICEFISCALE, "
					+ "to_char(DATNASC,'dd/mm/yyyy') as DATNASC, "
					+ "to_char(min(DATSTIMATA),'dd/mm/yyyy') as STRDATSTIMATA, " + "DE_AZIONE.STRDESCRIZIONE, "
					+ "OR_COLLOQUIO.PRGCOLLOQUIO, OR_COLLOQUIO.DATCOLLOQUIO "
					+ "from OR_PERCORSO_CONCORDATO, OR_COLLOQUIO, AN_LAVORATORE, DE_AZIONE ";

			if (!codificaPatto.equals("")) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + ", AM_PATTO_LAVORATORE, AM_LAV_PATTO_SCELTA "
						+ "where OR_PERCORSO_CONCORDATO.PRGCOLLOQUIO = OR_COLLOQUIO.PRGCOLLOQUIO "
						+ "AND OR_COLLOQUIO.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE "
						+ "AND OR_PERCORSO_CONCORDATO.PRGAZIONI = DE_AZIONE.PRGAZIONI " + "AND OR_COLLOQUIO.CODCPI = '"
						+ codCpi + "' " + "AND OR_PERCORSO_CONCORDATO.DATSTIMATA is not null "
						+ "AND OR_PERCORSO_CONCORDATO.CODESITORENDICONT = 'P' "
						+ "AND AM_PATTO_LAVORATORE.DATFINE IS NULL "
						+ "AND AM_PATTO_LAVORATORE.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE "
						+ "AND AM_PATTO_LAVORATORE.CODCPI = '" + codCpi + "' ";
			} else {
				SELECT_SQL_BASE = SELECT_SQL_BASE
						+ " where OR_PERCORSO_CONCORDATO.PRGCOLLOQUIO = OR_COLLOQUIO.PRGCOLLOQUIO "
						+ "AND OR_COLLOQUIO.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE "
						+ "AND OR_PERCORSO_CONCORDATO.PRGAZIONI = DE_AZIONE.PRGAZIONI " + "AND OR_COLLOQUIO.CODCPI = '"
						+ codCpi + "' " + "AND OR_PERCORSO_CONCORDATO.DATSTIMATA is not null "
						+ "AND OR_PERCORSO_CONCORDATO.CODESITORENDICONT = 'P' ";
			}

			if (!codificaPatto.equals("")) {
				SELECT_SQL_BASE = SELECT_SQL_BASE
						+ "AND AM_PATTO_LAVORATORE.PRGPATTOLAVORATORE = AM_LAV_PATTO_SCELTA.PRGPATTOLAVORATORE "
						+ "AND to_number(AM_LAV_PATTO_SCELTA.STRCHIAVETABELLA) = OR_PERCORSO_CONCORDATO.PRGPERCORSO "
						+ "AND AM_LAV_PATTO_SCELTA.CODLSTTAB ='OR_PER' "
						+ "AND NVL(AM_PATTO_LAVORATORE.CODCODIFICAPATTO, 'PT297') = '" + codificaPatto + "'";
			}

			if ((data_dal != null) && (!data_dal.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND OR_PERCORSO_CONCORDATO.DATSTIMATA >= to_date('" + data_dal
						+ "','dd/mm/yyyy')";
			}

			if ((data_al != null) && (!data_al.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND OR_PERCORSO_CONCORDATO.DATSTIMATA <= to_date('" + data_al
						+ "','dd/mm/yyyy')";
			}

			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists "
					+ "(select or_coll.prgcolloquio from OR_COLLOQUIO or_coll "
					+ "where or_coll.cdnlavoratore = an_lavoratore.CDNLAVORATORE and " + "or_coll.CODCPI = '" + codCpi
					+ "' " + "and or_coll.datcolloquio > OR_COLLOQUIO.DATCOLLOQUIO "
					+ "and or_coll.prgcolloquio <> OR_COLLOQUIO.prgcolloquio)";

			if (strFiltra.compareTo("1") == 0) {
				// filtra per appuntamenti
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists "
						+ "(select ag_lavoratore.cdnlavoratore from ag_lavoratore "
						+ "inner join ag_agenda on (ag_lavoratore.prgappuntamento = ag_agenda.prgappuntamento "
						+ "and ag_lavoratore.CODCPI = ag_agenda.CODCPI) "
						+ "where ag_lavoratore.cdnlavoratore = OR_COLLOQUIO.cdnlavoratore " + "and ag_agenda.codcpi = '"
						+ codCpi + "' " + "and ag_agenda.DTMDATAORA < OR_PERCORSO_CONCORDATO.DATSTIMATA and "
						+ "ag_agenda.DTMDATAORA >= trunc(sysdate))";
			}

			SELECT_SQL_BASE = SELECT_SQL_BASE + " group by an_lavoratore.CDNLAVORATORE, " + "an_lavoratore.STRCOGNOME, "
					+ "an_lavoratore.STRNOME, " + "an_lavoratore.STRCODICEFISCALE, "
					+ "DATNASC, DE_AZIONE.STRDESCRIZIONE, " + "OR_COLLOQUIO.PRGCOLLOQUIO, OR_COLLOQUIO.DATCOLLOQUIO ";

			SELECT_SQL_BASE = SELECT_SQL_BASE + " order by min(DATSTIMATA) asc";

			break;

		case SCADENZA_AMMINISTRATIVA_COLLOQUIO_LAV_POR:
			SELECT_SQL_BASE = "select AN_LAV1.CDNLAVORATORE, " + "AN_LAV1.STRCOGNOME, " + "AN_LAV1.STRNOME, "
					+ "AN_LAV1.STRCODICEFISCALE, " + "to_char(DATNASC,'dd/mm/yyyy') as DATNASC, "
					+ "to_char(DATSCADCONFERMA,'dd/mm/yyyy') as SCADENZA "
					+ "from AM_ELENCO_ANAGRAFICO, AN_LAV_STORIA_INF, AN_LAVORATORE AN_LAV1, "
					+ "AN_LAV_STORIA_INF_COLL, AM_DICH_DISPONIBILITA DISPONIBILITA1 "
					+ "where DISPONIBILITA1.PRGELENCOANAGRAFICO = AM_ELENCO_ANAGRAFICO.PRGELENCOANAGRAFICO "
					+ "AND AM_ELENCO_ANAGRAFICO.CDNLAVORATORE = AN_LAV1.CDNLAVORATORE "
					+ "AND AN_LAV1.CDNLAVORATORE = AN_LAV_STORIA_INF.CDNLAVORATORE "
					+ "AND AN_LAV_STORIA_INF.PRGLAVSTORIAINF = AN_LAV_STORIA_INF_COLL.PRGLAVSTORIAINF "
					+ "AND AN_LAV_STORIA_INF_COLL.STRCHIAVETABELLA = AM_ELENCO_ANAGRAFICO.PRGELENCOANAGRAFICO "
					+ "AND AN_LAV_STORIA_INF_COLL.CODLSTTAB = 'EA' " + "AND AN_LAV_STORIA_INF.codCpiTit = '" + codCpi
					+ "' " + "AND DISPONIBILITA1.DATSCADCONFERMA IS NOT NULL "
					+ "AND AM_ELENCO_ANAGRAFICO.DATCAN IS NULL " + "AND DISPONIBILITA1.DATFINE IS NULL";

			if ((data_dal != null) && (!data_dal.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND DISPONIBILITA1.DATSCADCONFERMA >= to_date('" + data_dal
						+ "','dd/mm/yyyy')";
			}

			if ((data_al != null) && (!data_al.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND DISPONIBILITA1.DATSCADCONFERMA <= to_date('" + data_al
						+ "','dd/mm/yyyy')";
			}

			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists (select AM_PATTO_LAVORATORE.CDNLAVORATORE "
					+ "from AM_PATTO_LAVORATORE  " + "WHERE AM_PATTO_LAVORATORE.CODCPI = '" + codCpi + "' "
					+ "AND AM_PATTO_LAVORATORE.PRGDICHDISPONIBILITA = DISPONIBILITA1.PRGDICHDISPONIBILITA "
					+ "AND AM_PATTO_LAVORATORE.CDNLAVORATORE = AN_LAV1.CDNLAVORATORE)";

			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists (select OR_COLLOQUIO.CDNLAVORATORE "
					+ "from OR_COLLOQUIO " + "WHERE OR_COLLOQUIO.CODCPI = '" + codCpi + "' "
					+ "AND OR_COLLOQUIO.CDNLAVORATORE = AN_LAV1.CDNLAVORATORE "
					+ "AND OR_COLLOQUIO.DATCOLLOQUIO BETWEEN DISPONIBILITA1.DATDICHIARAZIONE "
					+ "AND DISPONIBILITA1.DATSCADCONFERMA)";

			if (strFiltra.compareTo("1") == 0) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists "
						+ "(select AG_LAVORATORE.CDNLAVORATORE, MAX(ag_agenda.DTMDATAORA) from ag_lavoratore "
						+ "inner join ag_agenda on (ag_lavoratore.prgappuntamento = ag_agenda.prgappuntamento "
						+ "and ag_lavoratore.CODCPI = ag_agenda.CODCPI) "
						+ "inner join de_stato_appuntamento on (ag_agenda.codstatoappuntamento = de_stato_appuntamento.codstatoappuntamento) "
						+ "where ag_agenda.CODCPI = '" + codCpi + "' and "
						+ "ag_lavoratore.cdnlavoratore = AN_LAV1.CDNLAVORATORE and "
						+ "de_stato_appuntamento.flgattivo = 'S' and "
						+ "ag_agenda.DTMDATAORA BETWEEN DISPONIBILITA1.DATDICHIARAZIONE "
						+ "AND DISPONIBILITA1.DATSCADCONFERMA AND " + "ag_agenda.DTMDATAORA >= trunc(sysdate) "
						+ "AND ag_agenda.codesitoappunt is null " + "GROUP BY AG_LAVORATORE.CDNLAVORATORE)";
			}
			SELECT_SQL_BASE = SELECT_SQL_BASE + " order by DATSCADCONFERMA asc";
			break;

		case SCADENZA_AMMINISTRATIVA_STIPULA_PATTO_LAV:
			SELECT_SQL_BASE = "select AN_LAV1.CDNLAVORATORE, " + "AN_LAV1.STRCOGNOME, " + "AN_LAV1.STRNOME, "
					+ "AN_LAV1.STRCODICEFISCALE, " + "to_char(DATNASC,'dd/mm/yyyy') as DATNASC, "
					+ "to_char(DATSCADEROGAZSERVIZI,'dd/mm/yyyy') as SCADENZA "
					+ "from AM_ELENCO_ANAGRAFICO, AN_LAV_STORIA_INF, AN_LAVORATORE AN_LAV1, "
					+ "AN_LAV_STORIA_INF_COLL, AM_DICH_DISPONIBILITA DISPONIBILITA1 "
					+ "where DISPONIBILITA1.PRGELENCOANAGRAFICO = AM_ELENCO_ANAGRAFICO.PRGELENCOANAGRAFICO "
					+ "AND AM_ELENCO_ANAGRAFICO.CDNLAVORATORE = AN_LAV1.CDNLAVORATORE "
					+ "AND AN_LAV1.CDNLAVORATORE = AN_LAV_STORIA_INF.CDNLAVORATORE "
					+ "AND AN_LAV_STORIA_INF.PRGLAVSTORIAINF = AN_LAV_STORIA_INF_COLL.PRGLAVSTORIAINF "
					+ "AND AN_LAV_STORIA_INF_COLL.STRCHIAVETABELLA = AM_ELENCO_ANAGRAFICO.PRGELENCOANAGRAFICO "
					+ "AND AN_LAV_STORIA_INF_COLL.CODLSTTAB = 'EA' " + "AND AN_LAV_STORIA_INF.codCpiTit = '" + codCpi
					+ "' " + "AND DISPONIBILITA1.DATSCADEROGAZSERVIZI IS NOT NULL "
					+ "AND AM_ELENCO_ANAGRAFICO.DATCAN IS NULL " + "AND DISPONIBILITA1.DATFINE IS NULL";

			if ((data_dal != null) && (!data_dal.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND DISPONIBILITA1.DATSCADEROGAZSERVIZI >= to_date('" + data_dal
						+ "','dd/mm/yyyy')";
			}

			if ((data_al != null) && (!data_al.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND DISPONIBILITA1.DATSCADEROGAZSERVIZI <= to_date('" + data_al
						+ "','dd/mm/yyyy')";
			}

			SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists (select AM_PATTO_LAVORATORE.CDNLAVORATORE "
					+ "from AM_PATTO_LAVORATORE " + "WHERE AM_PATTO_LAVORATORE.CODCPI = '" + codCpi + "' "
					+ "AND AM_PATTO_LAVORATORE.PRGDICHDISPONIBILITA = DISPONIBILITA1.PRGDICHDISPONIBILITA "
					+ "AND AM_PATTO_LAVORATORE.CDNLAVORATORE = AN_LAV1.CDNLAVORATORE)";

			if (strFiltra.compareTo("1") == 0) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists "
						+ "(select AG_LAVORATORE.CDNLAVORATORE, MAX(ag_agenda.DTMDATAORA) from ag_lavoratore "
						+ "inner join ag_agenda on (ag_lavoratore.prgappuntamento = ag_agenda.prgappuntamento "
						+ "and ag_lavoratore.CODCPI = ag_agenda.CODCPI) "
						+ "inner join de_stato_appuntamento on (ag_agenda.codstatoappuntamento = de_stato_appuntamento.codstatoappuntamento), "
						+ "OR_COLLOQUIO " + "where ag_agenda.CODCPI = '" + codCpi + "' and "
						+ "ag_lavoratore.cdnlavoratore = AN_LAV1.CDNLAVORATORE and "
						+ "de_stato_appuntamento.flgattivo = 'S' and " + "ag_agenda.DTMDATAORA BETWEEN " + "CASE "
						+ "WHEN exists (select MAX(OR_COLLOQUIO.DATCOLLOQUIO) " + "from OR_COLLOQUIO "
						+ "WHERE OR_COLLOQUIO.CODCPI = '" + codCpi + "' "
						+ "AND OR_COLLOQUIO.CDNLAVORATORE = AN_LAV1.CDNLAVORATORE "
						+ "AND OR_COLLOQUIO.DATCOLLOQUIO BETWEEN DISPONIBILITA1.DATDICHIARAZIONE "
						+ "AND DISPONIBILITA1.DATSCADEROGAZSERVIZI) THEN OR_COLLOQUIO.DATCOLLOQUIO "
						+ "ELSE DISPONIBILITA1.DATDICHIARAZIONE " + "END "
						+ "AND DISPONIBILITA1.DATSCADEROGAZSERVIZI AND " + "ag_agenda.DTMDATAORA >= trunc(sysdate) "
						+ "AND ag_agenda.codesitoappunt is null " + "GROUP BY AG_LAVORATORE.CDNLAVORATORE)";
			}
			SELECT_SQL_BASE = SELECT_SQL_BASE + " order by DATSCADEROGAZSERVIZI asc";
			break;

		case SCADENZA_VALIDITA_PATTO:
			SELECT_SQL_BASE = "select AN_LAV1.CDNLAVORATORE, " + "AN_LAV1.STRCOGNOME, " + "AN_LAV1.STRNOME, "
					+ "AN_LAV1.STRCODICEFISCALE, " + "to_char(DATNASC,'dd/mm/yyyy') as DATNASC, "
					+ "to_char(DATSCADCONFERMA,'dd/mm/yyyy') as SCADENZA " + "from AN_LAVORATORE AN_LAV1, "
					+ "AM_PATTO_LAVORATORE AM_PATTO1 " + "where AM_PATTO1.CDNLAVORATORE = AN_LAV1.CDNLAVORATORE "
					+ "AND AM_PATTO1.CODCPI = '" + codCpi + "' "
					+ "AND AM_PATTO1.DATSCADCONFERMA is not null AND AM_PATTO1.DATFINE IS NULL";

			if (!codificaPatto.equals("")) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND NVL(AM_PATTO1.CODCODIFICAPATTO, 'PT297') = '" + codificaPatto
						+ "'";
			}

			if ((data_dal != null) && (!data_dal.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND AM_PATTO1.DATSCADCONFERMA >= to_date('" + data_dal
						+ "','dd/mm/yyyy')";
			}

			if ((data_al != null) && (!data_al.equals(""))) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND AM_PATTO1.DATSCADCONFERMA <= to_date('" + data_al
						+ "','dd/mm/yyyy')";
			}
			if (strFiltra.compareTo("1") == 0) {
				SELECT_SQL_BASE = SELECT_SQL_BASE + " AND not exists "
						+ "(select AG_LAVORATORE.CDNLAVORATORE, MAX(ag_agenda.DTMDATAORA) from ag_lavoratore, "
						+ "ag_agenda, am_patto_lavoratore, de_stato_appuntamento, am_lav_patto_scelta "
						+ "where ag_lavoratore.prgappuntamento = ag_agenda.prgappuntamento "
						+ "and ag_lavoratore.CODCPI = ag_agenda.CODCPI "
						+ "and ag_lavoratore.cdnlavoratore = am_patto_lavoratore.cdnlavoratore "
						+ "and am_patto_lavoratore.prgpattolavoratore = am_lav_patto_scelta.prgpattolavoratore "
						+ "and ag_lavoratore.CODCPI = am_patto_lavoratore.CODCPI "
						+ "and ag_agenda.codstatoappuntamento = de_stato_appuntamento.codstatoappuntamento "
						+ "and ag_agenda.CODCPI = '" + codCpi + "' and "
						+ "ag_lavoratore.cdnlavoratore = AN_LAV1.CDNLAVORATORE and "
						+ "de_stato_appuntamento.flgattivo = 'S' and "
						+ "am_lav_patto_scelta.strchiavetabella = AG_LAVORATORE.CDNLAVORATORE and "
						+ "am_lav_patto_scelta.codlsttab = 'AG_LAV' and am_lav_patto_scelta.prgpattolavoratore = AM_PATTO1.prgpattolavoratore AND "
						+ "ag_agenda.DTMDATAORA >= trunc(sysdate) " + "AND ag_agenda.codesitoappunt is null "
						+ "GROUP BY AG_LAVORATORE.CDNLAVORATORE)";
			}

			SELECT_SQL_BASE = SELECT_SQL_BASE + " order by AM_PATTO1.DATSCADCONFERMA asc";
			break;

		case SEGNALAZIONE_LAV_APP_CAMBIO_STATO_OCCUPAZ:

			SELECT_SQL_BASE = "select " + "	   AN_LAV1.CDNLAVORATORE, " + "	   AN_LAV1.STRCOGNOME, "
					+ "	   AN_LAV1.STRNOME, " + "	   AN_LAV1.STRCODICEFISCALE, "
					+ "	   to_char(DATNASC,'dd/mm/yyyy') as DATNASC, "
					+ "	   to_char(MIN(DTMDATAORA),'dd/mm/yyyy') as DATAAPPUNTAMENTO, "
					+ "	   DE_STATO_OCCUPAZ.STRDESCRIZIONE as  STRDESCSTATOOCC " + " from AN_LAVORATORE AN_LAV1, "
					// + " AN_LAV_STORIA_INF LAV_STO, "
					+ "	 AG_AGENDA AG_AG1, " + "  DE_STATO_APPUNTAMENTO AG_ST, " + "	 AG_LAVORATORE AG_LAV1, "
					+ "	 AM_STATO_OCCUPAZ, " + "	 DE_STATO_OCCUPAZ "
					+ " where AG_AG1.PRGAPPUNTAMENTO = AG_LAV1.PRGAPPUNTAMENTO "
					+ " AND AG_AG1.CODCPI = AG_LAV1.CODCPI "
					// + " AND LAV_STO.CDNLAVORATORE=AN_LAV1.CDNLAVORATORE "
					// + " AND LAV_STO.CODCPITIT='"
					// + codCpi
					// + "'"
					+ " AND AG_AG1.CODCPI = '" + codCpi + "' "
					+ " AND (AG_AG1.CODSTATOAPPUNTAMENTO=AG_ST.CODSTATOAPPUNTAMENTO) AND AG_ST.flgAttivo = 'S' "
					+ " AND AG_LAV1.CDNLAVORATORE =  AN_LAV1.CDNLAVORATORE "
					+ " AND AM_STATO_OCCUPAZ.CODSTATOOCCUPAZ = DE_STATO_OCCUPAZ.CODSTATOOCCUPAZ "
					+ " AND AM_STATO_OCCUPAZ.CDNLAVORATORE =  AN_LAV1.CDNLAVORATORE "
					+ " AND AM_STATO_OCCUPAZ.DATFINE IS NULL "
					// + " AND trunc(am_stato_occupaz.datinizio) >= trunc(ag_ag1.dtmins) "
					+ " AND AG_AG1.DTMDATAORA >= trunc(sysdate) "
					+ " AND trunc(AM_STATO_OCCUPAZ.DATINIZIO) BETWEEN trunc(AG_AG1.DTMINS) AND trunc(AG_AG1.DTMDATAORA)";

			if (!data_dal.equals("") && data_al.equals("")) { // se data_dal non è nulla, ma data_al lo è
				SELECT_SQL_BASE += " AND AG_AG1.DTMDATAORA >= TO_DATE('" + data_dal + "', 'DD/MM/YYYY') "
						+ " AND trunc(AM_STATO_OCCUPAZ.DATINIZIO) BETWEEN trunc(AG_AG1.DTMINS) AND trunc(sysdate) ";
			} else if (data_dal.equals("") && !data_al.equals("")) { // se data_dal è nulla, ma data_al non lo è
				SELECT_SQL_BASE += " AND AG_AG1.DTMDATAORA <= TO_DATE('" + data_al + "', 'DD/MM/YYYY') "
						+ " AND trunc(AM_STATO_OCCUPAZ.DATINIZIO) BETWEEN trunc(AG_AG1.DTMINS) AND TO_DATE('" + data_al
						+ "', 'DD/MM/YYYY') ";
			} else if (!data_dal.equals("") && !data_al.equals("")) { // se nessuna delle due è nulla
				SELECT_SQL_BASE += " AND AG_AG1.DTMDATAORA BETWEEN TO_DATE('" + data_dal
						+ "', 'DD/MM/YYYY') AND TO_DATE('" + data_al + "', 'DD/MM/YYYY') "
						+ " AND trunc(AM_STATO_OCCUPAZ.DATINIZIO) BETWEEN trunc(AG_AG1.DTMINS) AND TO_DATE('" + data_al
						+ "', 'DD/MM/YYYY') ";

			}
			// se sono entrambe nulle non ci sono clausole aggiuntive

			/*
			 * codice originario senza criteri di selezione di date SELECT_SQL_BASE+=
			 * "	  AND TRUNC(AG_AG1.DTMDATAORA) >= trunc(sysdate) "
			 * +" AND trunc(AM_STATO_OCCUPAZ.DATINIZIO) BETWEEN trunc(AG_AG1.DTMINS) AND TRUNC(SYSDATE) ";
			 */

			SELECT_SQL_BASE += "GROUP BY AN_LAV1.CDNLAVORATORE, STRCOGNOME, STRNOME, "
					+ "STRCODICEFISCALE, DATNASC, DE_STATO_OCCUPAZ.STRDESCRIZIONE";

			break;
		// PABLO 20.06.2011 INIZIO
		case CLIC_LAVORO_LISTA_CANDIDATURE_INVIATE:
			String dataInvio_al = req.containsAttribute("DATAINVIO_AL") ? req.getAttribute("DATAINVIO_AL").toString()
					: "";
			String dataInvio_dal = req.containsAttribute("DATAINVIO_DAL") ? req.getAttribute("DATAINVIO_DAL").toString()
					: "";
			String dataScadenza_al = req.containsAttribute("DATASCADENZA_AL")
					? req.getAttribute("DATASCADENZA_AL").toString()
					: "";
			String dataScadenza_dal = req.containsAttribute("DATASCADENZA_DAL")
					? req.getAttribute("DATASCADENZA_DAL").toString()
					: "";
			String codCPI = req.containsAttribute("CODCPI") ? req.getAttribute("CODCPI").toString() : "";
			String codAmbitoDiff = req.containsAttribute("COD_AMBITO_DIFFUSIONE")
					? req.getAttribute("COD_AMBITO_DIFFUSIONE").toString()
					: "";

			SELECT_SQL_BASE = " distinct(AN_LAV1.CDNLAVORATORE),  " + " AN_LAV1.STRCOGNOME, " + " AN_LAV1.STRNOME, "
					+ " AN_LAV1.STRCODICEFISCALE, " + " to_char(AN_LAV1.DATNASC,'dd/mm/yyyy') as DATNASC ";

			DynamicStatementUtils ut = new DynamicStatementUtils();

			ut.addSelect(SELECT_SQL_BASE);
			ut.addFrom("AN_LAVORATORE AN_LAV1, CL_CANDIDATURA CANDI ");
			ut.addWhere("CANDI.CDNLAVORATORE = AN_LAV1.CDNLAVORATORE ");
			if (codAmbitoDiff != null && !codAmbitoDiff.equals("")) {
				ut.addFrom("CL_AMBITO_CANDIDATURA AMBI_CANDI ");
				ut.addWhere("AMBI_CANDI.PRGCANDIDATURA = CANDI.PRGCANDIDATURA ");
				ut.addWhere("AMBI_CANDI.CODAMBITODIFFUSIONE = ('" + codAmbitoDiff + "') ");
			}

			if (codCPI != null && !codCPI.equals("")) {
				ut.addFrom("AN_LAV_STORIA_INF ALSI");
				ut.addWhere("ALSI.CDNLAVORATORE = AN_LAV1.CDNLAVORATORE ");
				ut.addWhere("ALSI.DATFINE IS NULL AND ALSI.CODCPITIT = '" + codCPI + "' ");
			}

			// FILTRO PER DATA DI INVIO
			if (!dataInvio_al.equals("")) {
				if (!dataInvio_dal.equals(""))
					ut.addWhere("CANDI.DATINVIO BETWEEN TO_DATE('" + dataInvio_dal + "','dd/mm/yyyy') AND TO_DATE('"
							+ dataInvio_al + "','dd/mm/yyyy') ");
				else
					ut.addWhere("CANDI.DATINVIO <= TO_DATE('" + dataInvio_al + "','dd/mm/yyyy') ");
			} else if (!dataInvio_dal.equals(""))
				ut.addWhere("CANDI.DATINVIO >= TO_DATE('" + dataInvio_dal + "','dd/mm/yyyy') ");

			// FILTRO PRE DATA DI SCADENZA, BEST BEFORE...
			if (!dataScadenza_al.equals("")) {
				if (!dataScadenza_dal.equals(""))
					ut.addWhere("CANDI.DATSCADENZA BETWEEN TO_DATE('" + dataScadenza_dal
							+ "','dd/mm/yyyy') AND TO_DATE('" + dataScadenza_al + "','dd/mm/yyyy') ");
				else
					ut.addWhere("CANDI.DATSCADENZA <= TO_DATE('" + dataScadenza_al + "','dd/mm/yyyy')");
			} else if (!dataScadenza_dal.equals(""))
				ut.addWhere("CANDI.DATSCADENZA >= TO_DATE('" + dataScadenza_dal + "','dd/mm/yyyy')");

			String statementDebug = ut.getStatement();
			return statementDebug;

		// FINE

		case SEGNALAZIONE_LAV_SENZA_DISPONIBILITA_TERR:

			FROM = null;
			WHERE = null;

			String valCV = req.getAttribute("OpzioneCV").toString();
			String statoVal = req.getAttribute("statoValCV").toString();

			SELECT_SQL_BASE = "select AN_LAV1.CDNLAVORATORE,  " + " AN_LAV1.STRCOGNOME, " + " AN_LAV1.STRNOME, "
					+ " AN_LAV1.STRCODICEFISCALE, " + " to_char(AN_LAV1.DATNASC,'dd/mm/yyyy') as DATNASC ";

			FROM = " from AN_LAVORATORE AN_LAV1, AN_LAV_STORIA_INF LAVSTO ";
			WHERE = " WHERE AN_LAV1.CDNLAVORATORE=LAVSTO.CDNLAVORATORE ";

			// ricerca su validità CV
			if (valCV.equals("1")) { // ricerco solo se ho specificato la ricerca per validità controllando le date di
										// inizio e fine
				FROM += " , PR_VALIDITA VAL ";
				WHERE += " AND an_lav1.cdnLavoratore=val.cdnLavoratore AND ";
				WHERE += " trunc(val.datfinecurr) >= trunc(sysdate) AND trunc(val.datiniziocurr) <= trunc(sysdate) ";
				// controllo se è stato specificato uno stato di validità
				if (!statoVal.equals("")) {
					WHERE += " AND VAL.CODSTATOLAV='" + statoVal + "' ";

				}
			}

			// ricerca su CPI titolare
			if (!codCpi.equals("")) {
				WHERE += " AND LAVSTO.CODCPITIT='" + codCpi + "' AND LAVSTO.CODMONOTIPOCPI IN ('C','T') ";
			}

			/*
			 * WHERE+=" and an_lav1.CDNLAVORATORE  in ( " +" 			select mans.cdnLavoratore "
			 * +"          from pr_mansione mans, " +"               pr_dis_comune DISCOM,"
			 * +"               pr_dis_provincia DISPROV, " +"               pr_dis_regione DISREG, "
			 * +"               pr_dis_stato DISSTATO " +"          where mans.flgDisponibile IN ('S', 'P', 'L') "
			 * +"          AND MANS.prgmansione = DISCOM.prgmansione (+) "
			 * +"          AND MANS.prgmansione = DISPROV.prgmansione (+) "
			 * +"          AND MANS.prgmansione = DISREG.prgmansione (+) "
			 * +"          AND MANS.prgmansione = DISSTATO.prgmansione (+) "
			 * +"          and nvl(DISCOM.prgdiscomune, 0) = 0 " +"          and nvl(DISPROV.prgdisprovincia, 0) = 0 "
			 * +"			and nvl(DISREG.prgdisregione, 0) = 0 " +"      	and nvl(DISSTATO.prgdisstato,0) = 0 )"
			 * +"			AND LAVSTO.DATFINE IS NULL";
			 */

			WHERE += " and an_lav1.CDNLAVORATORE  in ( " + " 			select mans.cdnLavoratore "
					+ "          from pr_mansione mans " + "          where mans.flgDisponibile IN ('S', 'P', 'L') "
					+ "           AND not exists (select 1 from PR_DIS_STATO  WHERE PRGMANSIONE = MANS.PRGMANSIONE)"
					+ " 			 AND not exists (select 1 from PR_DIS_REGIONE  WHERE PRGMANSIONE = MANS.PRGMANSIONE)"
					+ " 			 AND not exists (select 1 from PR_DIS_COMUNE  WHERE PRGMANSIONE = MANS.PRGMANSIONE)"
					+ " 			 AND not exists (select 1 from PR_DIS_PROVINCIA  WHERE PRGMANSIONE = MANS.PRGMANSIONE) "
					+ "		)	AND LAVSTO.DATFINE IS NULL";

			SELECT_SQL_BASE += FROM + WHERE;

			break;

		case SEGNALAZIONE_LAV_ESCLUSI_ROSA:

			FROM = null;
			WHERE = null;

			SELECT_SQL_BASE = " SELECT DISTINCT " + "LAV.CDNLAVORATORE,  " + "LAV.STRCOGNOME,  " + "LAV.STRNOME,  "
					+ "LAV.STRCODICEFISCALE,  " + "TO_CHAR(LAV.DATNASC,'dd/mm/yyyy') DATNASC, "
					+ "TO_CHAR((SELECT COUNT(*)  " + "         FROM DO_NOMINATIVO NOM  "
					+ "			WHERE NOM.CDNLAVORATORE = LAV.CDNLAVORATORE "
					+ " 		AND NOM.DTMCANC >= DID.DATDICHIARAZIONE) " + ") NUMESCLUSIONI ";

			FROM = "FROM AN_LAVORATORE LAV, " + "AM_DICH_DISPONIBILITA DID, " + "AM_ELENCO_ANAGRAFICO EA ";

			WHERE = " WHERE LAV.CDNLAVORATORE = EA.CDNLAVORATORE "
					+ " AND EA.PRGELENCOANAGRAFICO = DID.PRGELENCOANAGRAFICO "
					+ " AND NVL(TO_CHAR(EA.DATCAN), 'null') = 'null' " + " AND LAV.CDNLAVORATORE IN ( "
					+ "							   SELECT DISTINCT CDNLAVORATORE "
					+ "							   FROM DO_NOMINATIVO NOM "
					+ "							   WHERE CODTIPOCANC IS NOT NULL  "
					+ "		   				   and NOM.dtmcanc>=did.datDichiarazione)";

			// controllo date
			if (!data_dal.equals("")) { // se data_dal non è nulla
				WHERE += " AND DID.DATDICHIARAZIONE >= TO_DATE('" + data_dal + "', 'DD/MM/YYYY') ";
			}

			if (!data_al.equals("")) { // se data_dal non è nulla
				WHERE += " AND DID.DATFINE <= TO_DATE('" + data_al + "', 'DD/MM/YYYY') ";
			} // se sono entrambe nulle non ci sono clausole aggiuntive

			// controllo CPI
			if (!codCpi.equals("")) {
				FROM += ", AN_LAV_STORIA_INF LAVSTO";

				WHERE += " AND LAV.CDNLAVORATORE=LAVSTO.CDNLAVORATORE " + "  AND LAVSTO.CODCPITIT='" + codCpi + "' "
						+ "  AND LAVSTO.CODMONOTIPOCPI='C' ";
			}

			ORDER = " ORDER BY NUMESCLUSIONI DESC ";

			SELECT_SQL_BASE += FROM + WHERE + ORDER;

			break;

		case SEGNALAZIONE_LAV_PRONTI_NO_MANSIONI:
			SELECT_SQL_BASE = "SELECT   " + "   LAV.CDNLAVORATORE,   " + "   LAV.STRCOGNOME, " + "   LAV.STRNOME, "
					+ "   LAV.STRCODICEFISCALE, " + "   CPI.STRDESCRIZIONE CPITIT, "
					+ "   TO_CHAR(VAL.DATINIZIOCURR, 'DD/MM/YYYY') DATINIZIOCURR, "
					+ "   TO_CHAR(VAL.DATFINECURR, 'DD/MM/YYYY') DATFINECURR ";

			FROM = "FROM AN_LAVORATORE LAV, " + "     AN_LAV_STORIA_INF STO, " + "     PR_VALIDITA VAL, "
					+ "     DE_CPI CPI ";

			WHERE = "WHERE LAV.CDNLAVORATORE = STO.CDNLAVORATORE " + "AND CPI.CODCPI=STO.CODCPITIT "
					+ "AND LAV.CDNLAVORATORE = VAL.CDNLAVORATORE " + "AND DECODE(STO.DATFINE,NULL,'S','N')  = 'S' "
					+ "AND VAL.CODTIPOVALIDITA='DL' " + "AND VAL.CODSTATOLAV='COM' "
					+ "AND trunc(SYSDATE) BETWEEN trunc(VAL.DATINIZIOCURR) AND trunc(VAL.DATFINECURR) "
					+ "AND NOT EXISTS (SELECT MAN.CDNLAVORATORE" + "                 FROM PR_MANSIONE MAN "
					+ "          		WHERE ( MAN.FLGDISPONIBILE IN (\'S\', \'P\', \'L\') OR MAN.FLGDISPONIBILE IS NULL ) "
					+ "         		AND MAN.CDNLAVORATORE = LAV.CDNLAVORATORE) ";

			// controllo CPI
			if (!codCpi.equals("")) {
				WHERE += "  AND STO.CODCPITIT='" + codCpi + "' ";
			}

			SELECT_SQL_BASE += FROM + WHERE;
			break;
		}

		return SELECT_SQL_BASE;
	}
}