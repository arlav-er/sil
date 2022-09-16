package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * Effettua la ricerca dinamica di un movimento dato:
 * <p>
 * - o il codice fiscale del lavoratore <br>
 * - o il nome del lavoratore<br>
 * - o il cognome del lavoratore<br>
 * - o la ragione sociale della azienda<br>
 * - o la partita iva della azienda<br>
 * - o il codice fiscale della azienda<br>
 * - o la data di inizio del movimento<br>
 * - o la data di comunicazione<br>
 * - o il tipo movimento<br>
 * - o il tipo avviamento<br>
 * - o il tipo rapporto<br>
 * - o la normativa<br>
 * - o la qualifica<br>
 * - o il CCNL<br>
 * - o nessuna delle precedenti (restituisce TUTTO)<br>
 * <p>
 * può effettuare la ricerca sia sulla tabella AM_MOVIMENTO, sia sulla AM_MOVIMENTO_APPOGGIO, a seconda del parametro
 * CONTEXT indicato nella ServiceRequest, se il parametro non è specificato o vale "gestisci" verrà effettuata la
 * ricerca sulla AM_MOVIMENTO, se il parametro vale "valida" verrà effettuata la ricerca sulla AM_MOVIMENTO_APPOGGIO.
 * 
 * @author Paolo Roccetti
 * 
 */
public class DynRicercaMovimenti implements IDynamicStatementProvider {
	private static final String DATA_EDILIZIA_LEGGE = "12/08/2006";
	private static final String DATA_FINE_EDILIZIA_LEGGE = "31/12/2006";
	private static final String DATA_LEGGE_FINANZIARIA_2006 = "01/01/2007";
	private static final String CODATECOEDILIZIA = "45";

	private String SELECT_SQL_BASE_GESTISCI = " MOV.prgMovimento PRGMOV, " + " MOV.prgMovimentoPrec PRGMOVIMENTOPREC, "
			+ " MOV.prgMovimentoSucc PRGMOVIMENTOSUCC, " + " LAV.cdnLavoratore CDNLAV, " + " AZ.prgAzienda PRGAZ, "
			+ " UAZ.prgUnita PRGUAZ, " + " TO_CHAR(MOV.datComunicaz, 'DD/MM/YYYY') DatComunicaz, "
			+ " TO_CHAR(MOV.datInizioMov, 'DD/MM/YYYY') DATAMOV, " + " SUBSTR(MOV.codTipoMov, 1, 1 ) codTipoMov, "
			+ " SUBSTR(MOV.codTipoMov, 1, 1 ) || decode(mov.CODTIPOMOV, 'AVV', '', decode(nvl(mov.prgmovimentoprec,'0'),'0','<br>&lt;--','&nbsp;&nbsp;&nbsp;')) codTipoMovVisual, "
			+ " MOV.codComunicazione CODCOMUNICAZIONE, " + " MOV.CODTIPOCONTRATTO CODTIPOASS, "
			+ " MOV.CODTIPOCONTRATTO || '<br>' || MOV.CODMVCESSAZIONE CODASSCESVISUAL, "
			+ " MOV.CODMONOTIPOFINE CODMONOTIPOFINE, " + " MOV.codMonoTempo CODMONOTEMPO, "
			+ " MOV.CODSTATOATTO, MOV.CODMOTANNULLAMENTO, "
			+ " TO_CHAR(MOV.DATFINEMOVEFFETTIVA, 'DD/MM/YYYY') DATFINEMOVEFFETTIVA, "
			+ " decode(MOV.codTipoMov, 'CES','',TO_CHAR(MOV.DATFINEMOVEFFETTIVA, 'DD/MM/YYYY')) DATFINEMOVEFFETTIVAVIS, "
			+ " MOV.CODMVCESSAZIONE, " + " MOV.DECRETRIBUZIONEMEN, MOV.DECRETRIBUZIONEMENSANATA, "
			+ " decode(mov.CODTIPOMOV,'CES','', " + " 'R.' || MOV.DECRETRIBUZIONEMEN || "
			+ " decode(nvl(MOV.CODTIPODICH,'0'),'0','','<br>S.' || MOV.DECRETRIBUZIONEMENSANATA) ) MOVRETRIBUZIONE,"
			+ " TMOV.strDescrizione DESCRMOV, " + " LAV.strCognome || ' ' || LAV.strNome COGNOMENOMELAV, "
			+ " LAV.strCodiceFiscale CODFISCLAV, " + " AZ.strRagioneSociale RAGSOCAZ, "
			+ " UAZ.strIndirizzo || ', ' || COM.strDenominazione || '(' || RTRIM(PROV.strIstat) || ')' IndirAzienda, "
			+ " COM.strDenominazione COMAZ, "
			+ " PROV.strDenominazione PROVAZ , DE_TIPO_CONTRATTO.CODMONOTIPO CODMONOTIPOASS, "
			+ " (CASE WHEN (MOV.CODTIPOMOV <> 'CES' AND DE_TIPO_CONTRATTO.NUMRETRIBUZIONEPROSP IS NOT NULL AND MOV.CODTIPODICH IS NOT NULL "
			+ " AND MOV.DECRETRIBUZIONEANN IS NOT NULL AND MOV.DECRETRIBUZIONEMENSANATA IS NOT NULL) "
			+ " THEN TO_CHAR((MOV.DECRETRIBUZIONEMENSANATA * 12 * DE_TIPO_CONTRATTO.NUMRETRIBUZIONEPROSP)) "
			+ " WHEN (MOV.CODTIPOMOV <> 'CES' AND DE_TIPO_CONTRATTO.NUMRETRIBUZIONEPROSP IS NOT NULL AND MOV.CODTIPODICH IS NULL AND MOV.DECRETRIBUZIONEANN IS NOT NULL) "
			+ " THEN TO_CHAR((MOV.DECRETRIBUZIONEANN * DE_TIPO_CONTRATTO.NUMRETRIBUZIONEPROSP)) " + " ELSE NULL"
			+ " END) RETRIBUZIONEPROSPETTICA, " + " decode(MOV.STRNOTE,NULL,'','S') as NOTEMODIF, "
			+ " MOV.CODTIPODOCEX, MOV.STRNUMDOCEX,"
			+ " MOV.CODMOTIVOPERMSOGGEX,TO_CHAR(MOV.DATSCADENZA, 'DD/MM/YYYY') DATSCADENZA";

	private String SELECT_SQL_BASE_VALIDA = " SELECT "
			+ " MOV.prgMovimentoApp PRGMOVAPP, MOV.prgMovimentoApp, MOV.codTipoComunic, MOV.codComunicazione CODCOMUNICAZIONE, TCOM.STRDESCRIZIONE DESCRTIPOCOMUNICAZ, "
			+ " TO_CHAR(MOV.datComunicaz, 'DD/MM/YYYY') DatComunicaz, "
			+ " TO_CHAR(MOV.datInizioMov, 'DD/MM/YYYY') DATAMOV, "
			+ " CASE WHEN (MOV.DATINIZIOMOV is not null) THEN MOV.DATINIZIOMOV ELSE MOV.DATFINEMOV END DATASORT1, "
			+ " SUBSTR(MOV.codTipoMov, 1, 1 ) codTipoMov, " + " MOV.CODTIPOCONTRATTO CODTIPOASS, "
			+ " MOV.codMonoTempo CODMONOTEMPO, " + " MOV.STRCOGNOME COGNOMELAV, " + " MOV.STRNOME NOMELAV, "
			+ " MOV.STRCODICEFISCALE CODFISCLAV, " + " MOV.STRAZRAGIONESOCIALE RAGSOCAZ, "
			+ " MOV.STRUAINDIRIZZO || ', ' || COM.strDenominazione || '(' || RTRIM(PROV.strIstat) || ')' IndirAzienda, "
			+ " MOV.CODTIPODOCEX, MOV.STRNUMDOCEX,"
			+ " MOV.CODMOTIVOPERMSOGGEX, TO_CHAR(MOV.DATSCADENZA, 'DD/MM/YYYY') DATSCADENZA, " + " MOV.CODTIPOMIS ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String dataMovDa = (String) req.getAttribute("datmovimentoda");
		String dataMovA = (String) req.getAttribute("datmovimentoa");
		String dataComDa = (String) req.getAttribute("datcomunicazioneda");
		String dataComA = (String) req.getAttribute("datcomunicazionea");
		String tipoMovimento = (String) req.getAttribute("tipoMovimento");
		String codTipoComunic = (String) req.getAttribute("codTipoComunic");

		String codTipoAss = (String) req.getAttribute("codTipoAss");

		String codMonoTempo = (String) req.getAttribute("codMonoTempo");
		String tipoRapporto = (String) req.getAttribute("tipoRapporto");
		String normativa = (String) req.getAttribute("normativa");
		String codMansione = (String) req.getAttribute("CODMANSIONE");
		String ccnl = (String) req.getAttribute("codCCNL");
		String codAgevolazione = (String) req.getAttribute("codAgevolazione");

		String nome = (String) req.getAttribute("nome");
		String cognome = (String) req.getAttribute("cognome");
		String cflavoratore = (String) req.getAttribute("codiceFiscaleLavoratore");
		String ragsocaz = (String) req.getAttribute("ragioneSociale");
		String piva = (String) req.getAttribute("pIva");
		String cfazienda = (String) req.getAttribute("codFiscaleAzienda");
		String codTipoAzienda = (String) req.getAttribute("codTipoAzienda");

		String cpiAzienda = StringUtils.getAttributeStrNotNull(req, "CodCPI");

		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");
		String prgAzienda = (String) req.getAttribute("PRGAZIENDA");
		String prgUnita = (String) req.getAttribute("PRGUNITA");
		String onlyOpenMov = (String) req.getAttribute("onlyOpenMov");
		String codCpiLav = (String) req.getAttribute("CodCPILav");
		String codStatoAtto = (String) req.getAttribute("CODSTATOATTO");
		String codProvenienza = (String) req.getAttribute("CODMONOMOVDICH");
		String numGgTraMovComunicaz = (String) req.getAttribute("numGgTraMovComunicaz");
		String prgAziendaUt = (String) req.getAttribute("PRGAZIENDAUT");
		String prgUnitaUt = (String) req.getAttribute("PRGUNITAUT");
		String codMotAnnullamento = (String) req.getAttribute("CODMOTANNULLAMENTO");
		String codCpiAz = (String) req.getAttribute("CodCPIAz");
		String insertBy = (String) req.getAttribute("insertBy");
		// Giovanni D'Auria 02/02/2005 begin
		// codice del motivo della cessazione
		String codMotCessazione = (String) req.getAttribute("motivoCessazione");
		String referente = (String) req.getAttribute("referente");

		String codComunicazione = (String) req.getAttribute("codComunicazione");
		String codComunicazionePrec = (String) req.getAttribute("codComunicazionePrec");
		/**
		 * Aggiunto flag legge 68 in ricerca movimenti. se non sei in validazione viene assegnata dal parametro in
		 * request flg68, se in validazione dal parametro in request flglegge68 Pablo 30/03/2011
		 */
		String flgLegge68 = StringUtils.getAttributeStrNotNull(req, "flgl68"); // prima della mia modifica era assegnata
																				// a "" PAblo
		// 23/11/2007 savino: ricerca per art.13
		String ricercaPerArt13 = (String) req.getAttribute("RicXArt13");
		String missioni = (String) req.getAttribute("missioni");
		boolean ricercaMovL68 = !flgLegge68.equals("");

		if (missioni == null)
			missioni = "";

		// [START] Gestione agenzie di somministrazione estere
		String flgAzEstera = (String) req.getAttribute("flgAzEstera");
		String strCfAzEstera = (String) req.getAttribute("strCfAzEstera");
		String strRagSocAzEstera = (String) req.getAttribute("strRagSocAzEstera");
		// [END]

		String SELECT_SQL_BASE_GESTISCI_FROM = " FROM AM_MOVIMENTO MOV,DE_MV_TIPO_MOV TMOV,AN_UNITA_AZIENDA UAZ,"
				+ " AN_AZIENDA AZ, AN_LAVORATORE LAV, DE_COMUNE COM, " + " DE_PROVINCIA PROV , DE_TIPO_CONTRATTO ";

		if ((codComunicazione != null) && (!codComunicazione.equals("")) && missioni.equals("on")) {
			SELECT_SQL_BASE_GESTISCI_FROM += " ,AM_MOVIMENTO_MISSIONE MIS ";

		}

		// Utilizzata solo nella ricerca dei movimenti (contesto <> validazione)
		String SELECT_SQL_BASE_GESTISCI_WHERE = "WHERE MOV.codTipoMov = TMOV.codTipoMov AND MOV.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+) ";

		if (ricercaMovL68)
			SELECT_SQL_BASE_GESTISCI_WHERE += " AND MOV.FLGLEGGE68 = 'S' ";

		if ((codComunicazione != null) && (!codComunicazione.equals("")) && missioni.equals("on")) {
			SELECT_SQL_BASE_GESTISCI_WHERE += " AND MOV.PRGMOVIMENTO = MIS.PRGMOVIMENTO ";
		}
		// Cerco il parametro del contesto:
		StringBuffer buf_totale;
		String context = StringUtils.getAttributeStrNotNull(req, "CONTEXT");
		if (context.equals("valida")) {
			SELECT_SQL_BASE_VALIDA += " FROM AM_MOVIMENTO_APPOGGIO MOV, ";
		} else if (context.equals("validaArchivio")) {
			SELECT_SQL_BASE_VALIDA += " FROM AM_MOV_APP_ARCHIVIO MOV, ";
		}

		SELECT_SQL_BASE_VALIDA += " DE_COMUNE COM, DE_PROVINCIA PROV, DE_TIPO_COMUNICAZIONE TCOM "
				+ " WHERE MOV.CODUACOM = COM.codCom (+) AND COM.codProvincia = PROV.codProvincia (+) AND MOV.CODTIPOCOMUNIC = TCOM.CODTIPOCOMUNICAZIONE(+)"
				+ " AND NVL(MOV.FLGASSDACESS, 'N') != 'S' ";

		if ((codComunicazione != null) && (!codComunicazione.equals("")) && missioni.equals("on")) {
			SELECT_SQL_BASE_GESTISCI = " SELECT distinct " + SELECT_SQL_BASE_GESTISCI;
		} else {
			SELECT_SQL_BASE_GESTISCI = " SELECT " + SELECT_SQL_BASE_GESTISCI;
		}

		// query_totale viene impostata come query nella ricerca dei movimenti (contesto <> validazione)
		String query_totale = SELECT_SQL_BASE_GESTISCI + SELECT_SQL_BASE_GESTISCI_FROM + SELECT_SQL_BASE_GESTISCI_WHERE;
		if (context.startsWith("valida")) {
			buf_totale = new StringBuffer(SELECT_SQL_BASE_VALIDA);
		} else {
			buf_totale = new StringBuffer(query_totale);
		}

		StringBuffer buf = new StringBuffer();

		if (!context.startsWith("valida")) {
			// Inserisco la Join con la tabella dei lavoratori
			buf.append(" AND MOV.cdnLavoratore = LAV.cdnLavoratore ");
			// Inserisco la Join con la tabella delle aziende
			buf.append(" AND MOV.prgAzienda = AZ.prgAzienda ");
			// Inserisco la Join con la tabella delle Unita Aziende
			// e con quelle dei comuni e province
			buf.append(" AND MOV.prgAzienda = UAZ.prgAzienda AND MOV.prgUnita = UAZ.prgUnita "
					+ " AND UAZ.codCom = COM.codCom AND COM.codProvincia = PROV.codProvincia (+) ");
		}

		// Inserisco nella buf la condizione sulla data di inizio se esiste
		if ((dataMovDa != null) && (!dataMovDa.equals(""))) {
			buf.append(" AND MOV.datInizioMov >= to_date('" + dataMovDa + "', 'DD/MM/YYYY') ");
		}
		if ((dataMovA != null) && (!dataMovA.equals(""))) {
			buf.append(" AND MOV.datInizioMov <= to_date('" + dataMovA + "', 'DD/MM/YYYY') ");
		}

		// Inserisco nella buf la condizione sulla data di comunicazione se esiste
		if ((dataComDa != null) && (!dataComDa.equals(""))) {
			buf.append(" AND MOV.datComunicaz >= to_date('" + dataComDa + "', 'DD/MM/YYYY') ");
		}
		if ((dataComA != null) && (!dataComA.equals(""))) {
			buf.append(" AND MOV.datComunicaz <= to_date('" + dataComA + "', 'DD/MM/YYYY') ");
		}

		if ((codComunicazione != null) && (!codComunicazione.equals("")) && missioni.equals("on")) {
			buf.append(" AND MIS.CODCOMUNICAZIONE = '" + codComunicazione + "'");
		}

		if ((codComunicazione != null) && (!codComunicazione.equals("")) && missioni.equals("")) {
			buf.append(" AND MOV.CODCOMUNICAZIONE = '" + codComunicazione + "'");
		}

		if ((codComunicazionePrec != null) && (!codComunicazionePrec.equals(""))) {
			buf.append(" AND MOV.CODCOMUNICAZIONEPREC = '" + codComunicazionePrec + "'");
		}

		// ************************* Giovanni D'Auria 02/02/2005 begin **********************
		if ((tipoMovimento != null) && (!tipoMovimento.equals(""))) {
			buf.append(" AND MOV.codTipoMov = '" + tipoMovimento + "' ");
			if (tipoMovimento.equalsIgnoreCase("CES")) {
				if (codMotCessazione != null && !codMotCessazione.equals("")) {
					buf.append(" AND MOV.CODMVCESSAZIONE= '" + codMotCessazione + "' ");
				}
			}
		}
		// ************************* Giovanni D'Auria 02/02/2005 end **********************

		// Solo movimenti aperti
		if ((onlyOpenMov != null) && onlyOpenMov.equalsIgnoreCase("S")) {
			buf.append(" AND PRGMOVIMENTOSUCC IS NULL AND MOV.CODTIPOMOV != 'CES' "
					+ "AND (DATFINEMOVEFFETTIVA IS NULL OR DATFINEMOVEFFETTIVA >= SYSDATE)");
		}

		// Movimenti in ritardo
		if (!context.startsWith("valida")) {
			if ((numGgTraMovComunicaz != null) && numGgTraMovComunicaz.equalsIgnoreCase("S")) {
				SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
				String encryptKey = (String) sessione.getAttribute("_ENCRYPTER_KEY_");
				// CONTROLLI AVV E CODMOTANNULLAMENTO = "URG"
				buf.append(
						" AND ( (MOV.CODTIPOMOV = 'AVV' AND (MOV.CODMOTANNULLAMENTO IN ('URG','MAG')) AND NUMGGTRAMOVCOMUNICAZIONE > 5) "
								+
								// CONTROLLI PER L'EDILIZIA
								" OR (TRUNC(DATCOMUNICAZ) >= TRUNC(DATINIZIOMOV) AND TRUNC(MOV.DATINIZIOMOV) >= TO_DATE('"
								+ DATA_EDILIZIA_LEGGE + "','DD/MM/YYYY') AND TRUNC(MOV.DATINIZIOMOV) <= TO_DATE('"
								+ DATA_FINE_EDILIZIA_LEGGE + "','DD/MM/YYYY') "
								+ " AND MOV.CODTIPOMOV = 'AVV' AND (NVL(MOV.CODMOTANNULLAMENTO, ' ') NOT IN ('URG','MAG')) AND LENGTH(UAZ.CODATECO) >= 2 AND SUBSTR(UAZ.CODATECO,1,2) = '"
								+ CODATECOEDILIZIA + "') " +
								// CONTROLLI AVV PER LE AGENZIE DIVERSE DA QUELLE DI SOMMINISTRAZIONE
								" OR (TRUNC(DATCOMUNICAZ) >= TRUNC(DATINIZIOMOV) AND TRUNC(MOV.DATINIZIOMOV) >= TO_DATE('"
								+ DATA_LEGGE_FINANZIARIA_2006
								+ "','DD/MM/YYYY') AND MOV.CODTIPOMOV = 'AVV' AND NVL(MOV.CODMOTANNULLAMENTO, ' ') NOT IN ('URG','MAG') AND AZ.CODTIPOAZIENDA <> 'INT') "
								+
								// CONTROLLI CES PER LAVORATORI IN COLLOCAMENTO MIRATO
								" OR (TRUNC(MOV.DATINIZIOMOV) >= TO_DATE('" + DATA_LEGGE_FINANZIARIA_2006
								+ "','DD/MM/YYYY') AND MOV.CODTIPOMOV = 'CES' AND NUMGGTRAMOVCOMUNICAZIONE > 10 AND (SELECT COUNT(*) FROM AM_CM_ISCR, AM_DOCUMENTO DOC, AM_DOCUMENTO_COLL COLL "
								+ "WHERE AM_CM_ISCR.PRGCMISCR = COLL.STRCHIAVETABELLA AND COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO AND DOC.CODTIPODOCUMENTO = 'L68' AND DOC.CODSTATOATTO = 'PR' AND DECRYPT(AM_CM_ISCR.CDNLAVORATORE, '"
								+ encryptKey
								+ "') = MOV.CDNLAVORATORE AND DOC.CDNLAVORATORE = MOV.CDNLAVORATORE) >= 1) " +
								// CONTROLLI PER AGENZIE DI SOMMINISTRAZIONE (AVV, PRO/TRA, CES)
								" OR (TRUNC(DATCOMUNICAZ) > TRUNC(ADD_MONTHS(TO_DATE('20' || TO_CHAR(DATINIZIOMOV,'MM') || TO_CHAR(DATINIZIOMOV,'YYYY') , 'DD/MM/YYYY'),1)) "
								+ " AND (MOV.CODTIPOMOV <> 'AVV' OR (NVL(MOV.CODMOTANNULLAMENTO, ' ') NOT IN ('URG','MAG')) ) AND TRUNC(MOV.DATINIZIOMOV) >= TO_DATE('"
								+ DATA_LEGGE_FINANZIARIA_2006 + "','DD/MM/YYYY') AND AZ.CODTIPOAZIENDA = 'INT') " +
								// CONTROLLI CES PER AGENZIE DIVERSE DA QUELLE DI SOMMINISTRAZIONE
								" OR (TRUNC(MOV.DATINIZIOMOV) >= TO_DATE('" + DATA_LEGGE_FINANZIARIA_2006
								+ "','DD/MM/YYYY') AND MOV.CODTIPOMOV = 'CES' AND AZ.CODTIPOAZIENDA <> 'INT' AND NUMGGTRAMOVCOMUNICAZIONE > 5) "
								+
								// CONTROLLI PRO/TRA PER LE AGENZIE DIVERSE DA QUELLE DI SOMMINISTRAZIONE
								" OR (TRUNC(MOV.DATINIZIOMOV) >= TO_DATE('" + DATA_LEGGE_FINANZIARIA_2006
								+ "','DD/MM/YYYY') AND MOV.CODTIPOMOV IN ('PRO', 'TRA') AND AZ.CODTIPOAZIENDA <> 'INT' AND NUMGGTRAMOVCOMUNICAZIONE > 5) "
								+
								// CONTROLLI PER MOVIMENTI CON DATA INIZIO PRECEDENTE AL 01/01/2007
								" OR (TRUNC(MOV.DATINIZIOMOV) < TO_DATE('" + DATA_LEGGE_FINANZIARIA_2006
								+ "','DD/MM/YYYY') "
								+ " AND (MOV.CODTIPOMOV <> 'AVV' OR (NVL (mov.codmotannullamento, ' ') NOT IN ('URG','MAG')) ) AND NUMGGTRAMOVCOMUNICAZIONE > (Select NUMGGPRIMARITARDOMOV from ts_generale)) )");
				buf.append(" AND CODMONOMOVDICH = 'O'");
			}
		}

		// CodCPI
		if ((codCpiLav != null) && (!codCpiLav.equals(""))) {
			buf.append(" AND MOV.CODCPILAV = '" + codCpiLav + "'");
		}

		// Tipo Avviamento
		if ((codTipoAss != null) && (!codTipoAss.equals(""))) {
			buf.append(" AND MOV.codTipoContratto = '" + codTipoAss + "'");
		}

		// codStatoAtto
		if ((codStatoAtto != null) && (!codStatoAtto.equals(""))) {
			buf.append(" AND MOV.codStatoAtto = '" + codStatoAtto + "'");
		}

		// codMotAnnullamento
		if ((codMotAnnullamento != null) && (!codMotAnnullamento.equals(""))) {
			buf.append(" AND MOV.codMotAnnullamento = '" + codMotAnnullamento + "'");
		}

		// codProvenienza
		if ((codProvenienza != null) && (!codProvenienza.equals(""))) {
			buf.append(" AND MOV.CODMONOMOVDICH = '" + codProvenienza + "'");
		}

		// codMonoTempo
		if ((codMonoTempo != null) && (!codMonoTempo.equals(""))) {
			buf.append(" AND MOV.codMonoTempo = '" + codMonoTempo + "' ");
		}

		// tipoRapporto
		if ((tipoRapporto != null) && (!tipoRapporto.equals(""))) {
			buf.append(" AND MOV.codContratto = '" + tipoRapporto + "' ");
		}

		// normativa
		if ((normativa != null) && (!normativa.equals(""))) {
			buf.append(" AND MOV.codNormativa = '" + normativa + "' ");
		}

		// codMansione
		if ((codMansione != null) && (!codMansione.equals(""))) {
			buf.append(" AND MOV.codMansione = '" + codMansione + "' ");
		}

		// ccnl
		if ((ccnl != null) && (!ccnl.equals(""))) {
			buf.append(" AND MOV.codCcnl = '" + ccnl + "' ");
		}

		// codAgevolazione
		if ((codAgevolazione != null) && (!codAgevolazione.equals(""))) {
			buf.append(" AND MOV.codAgevolazione = '" + codAgevolazione + "' ");
		}

		// [START] Gestione agenzie di somministrazione estere
		if ((flgAzEstera != null) && (!flgAzEstera.equals(""))) {
			buf.append(" AND MOV.flgAzEstera = '" + flgAzEstera + "' ");
		}
		if ((strCfAzEstera != null) && (!strCfAzEstera.equals(""))) {
			strCfAzEstera = strCfAzEstera.toUpperCase();
			buf.append(" AND upper(MOV.strCfAzEstera) = '" + strCfAzEstera + "' ");
		}
		if ((strRagSocAzEstera != null) && (!strRagSocAzEstera.equals(""))) {
			strRagSocAzEstera = StringUtils.replace(strRagSocAzEstera, "'", "''");
			strRagSocAzEstera = strRagSocAzEstera.toUpperCase();
			buf.append(" AND upper(MOV.strRagSocAzEstera) like '" + strRagSocAzEstera + "%'");
		}
		// [END]

		// Se non sto validando inserisco le altre join
		if (!context.startsWith("valida")) {
			if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) {
				buf.append(" AND MOV.cdnLavoratore = " + cdnLavoratore);
			}

			if ((prgAzienda != null) && (!prgAzienda.equals("")) && (prgUnita != null) && (!prgUnita.equals(""))) {
				buf.append(" AND MOV.prgAzienda = " + prgAzienda + " AND MOV.prgUnita = " + prgUnita);
			}

			if ((prgAziendaUt != null) && (!prgAziendaUt.equals("")) && (prgUnitaUt != null)
					&& (!prgUnitaUt.equals(""))) {
				buf.append(" AND MOV.PRGAZIENDAUTILIZ = " + prgAziendaUt + " AND MOV.PRGUNITAUTILIZ = " + prgUnitaUt);
			}

			if ((codCpiAz != null) && (!codCpiAz.equals(""))) {
				buf.append(" AND COM.CODCPI = '" + codCpiAz + "'");
			}

			if ((insertBy != null) && (!insertBy.equals(""))) {
				buf.append(" AND MOV.CODMONOPROV = '" + insertBy + "'");
			}

			if ((referente != null) && (!referente.equals(""))) {
				referente = StringUtils.replace(referente, "'", "''");
				referente = referente.toUpperCase();
				buf.append(" AND upper(MOV.STRREFERENTE) like '" + referente + "%'");
			}

			// 23/11/2007 savino: ricerca art. 13
			if ((ricercaPerArt13 != null) && (ricercaPerArt13.equals("CESSATI") || ricercaPerArt13.equals("VALIDI"))) {
				buf_totale.append(", CM_MOV_L68_ART13 ART13 ");
				if ("CESSATI".equals(ricercaPerArt13)) {
					buf.append(
							" AND MOV.CODTIPOMOV='AVV' AND ART13.DATFINESGRAVIO IS NOT NULL AND ART13.DATFINESGRAVIO > nvl(pg_anagrafica_professionale_rp.GETDATAFINEMOVEFFFORRP( MOV.PRGMOVIMENTO ), MOV.DATFINEMOVEFFETTIVA) ");
					buf.append(" AND MOV.PRGMOVIMENTO = ART13.PRGMOVIMENTO ");
				} else if ("VALIDI".equals(ricercaPerArt13)) {
					buf.append(" AND (MOV.CODAGEVOLAZIONE = '66' OR MOV.CODAGEVOLAZIONE = '67' ) ");

				}

			}

			if (missioni.equals("")) {
				buf.append(" ORDER BY MOV.datInizioMov DESC, " + " decode(MOV.codTipoMov, 'AVV', 1, " + " 'PRO', 2, "
						+ " 'TRA', 3, " + " 'CES', 4) DESC, "
						+ "codTipoMov, MOV.dtmins DESC, COGNOMENOMELAV, RAGSOCAZ, IndirAzienda");
			}
		}
		// Se sto validando inserisco le altre condizioni senza le Join su Aziende, Lavoratori ecc...
		else {
			flgLegge68 = StringUtils.getAttributeStrNotNull(req, "FLGLEGGE68");

			if ((cflavoratore != null) && (!cflavoratore.equals(""))) {
				cflavoratore = cflavoratore.toUpperCase();
				buf.append(" AND upper(MOV.strCodiceFiscale) like '" + cflavoratore + "%'");
			}

			if ((cognome != null) && (!cognome.equals(""))) {
				cognome = StringUtils.replace(cognome, "'", "''");
				cognome = cognome.toUpperCase();
				buf.append(" AND upper(MOV.strCognome) like '" + cognome + "%'");
			}
			if ((nome != null) && (!nome.equals(""))) {
				nome = StringUtils.replace(nome, "'", "''");
				nome = nome.toUpperCase();
				buf.append(" AND upper(MOV.strNome) like '" + nome + "%'");
			}
			if ((ragsocaz != null) && (!ragsocaz.equals(""))) {
				ragsocaz = StringUtils.replace(ragsocaz, "'", "''");
				ragsocaz = ragsocaz.toUpperCase();
				buf.append(" AND upper(MOV.STRAZRAGIONESOCIALE) like '" + ragsocaz + "%'");
			}

			if ((piva != null) && (!piva.equals(""))) {
				piva = piva.toUpperCase();
				buf.append(" AND upper(MOV.STRAZPARTITAIVA) like '" + piva + "%'");
			}

			if ((cfazienda != null) && (!cfazienda.equals(""))) {
				cfazienda = cfazienda.toUpperCase();
				buf.append(" AND upper(MOV.STRAZCODICEFISCALE) like '" + cfazienda + "%'");
			}
			if ((codTipoAzienda != null) && (!codTipoAzienda.equals(""))) {
				codTipoAzienda = codTipoAzienda.toUpperCase();
				buf.append(" AND upper(MOV.CODAZTIPOAZIENDA) like '" + codTipoAzienda + "%'");
			}
			if ((referente != null) && (!referente.equals(""))) {
				referente = StringUtils.replace(referente, "'", "''");
				referente = referente.toUpperCase();
				buf.append(" AND upper(MOV.STRREFERENTE) like '" + referente + "%'");
			}
			// Filtro sul cpi competente per l'azienda D'Auria Giovanni 09/05/2005
			if (!cpiAzienda.equals("")) {
				buf.append(" AND COM.CODCPI = '" + cpiAzienda + "' ");
			}

			// Filtro su flgLegge68
			if (flgLegge68.equals("L")) {
				buf.append(" AND MOV.FLGLEGGE68 = 'S' ");
			} else {
				if (flgLegge68.equals("N")) {
					buf.append(" AND NVL(MOV.FLGLEGGE68, 'N') <> 'S' ");
				}
			}
			if ((codTipoComunic != null) && (!codTipoComunic.equals(""))) {
				codTipoComunic = codTipoComunic.toUpperCase();
				buf.append(" AND upper(MOV.CODTIPOCOMUNIC) like '" + codTipoComunic + "%'");
			}
			// Ordinamento query
			buf.append(" ORDER BY " + " decode(MOV.codTipoMov, 'AVV', 1, " + " 'TRA', 2, " + " 'PRO', 3, "
					+ " 'CES', 4) ASC, " + "codTipoMov, decode(MOV.codTipoComunic, '01', 1, '03', 2, 3) ASC, "
					+ "decode(MOV.CODTIPOCONTRATTO, 'C.01.00', 1, 2) ASC, "
					+ "DATASORT1 DESC, CODFISCLAV, RAGSOCAZ, IndirAzienda");
		}
		buf_totale.append(buf.toString());
		return buf_totale.toString();

	}
}
