package it.eng.sil.cig.bean;

import static it.eng.sil.cig.bean.CigConst.CONC_PARZIALE;
import static it.eng.sil.cig.bean.CigConst.CONC_SI;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.utils.Converter;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * 
 * @author Esposito,Rodi
 *
 */
public class CILavoratoreBean {
	private String prglavoratore;
	private String prgaccordo;
	private String strcodicefiscale;
	private String strcognome;
	private String strnome;
	private String strsesso;
	private String datassunzione;
	private String codcomnascita;
	private String datnascita;
	private String strcell;
	private String straltrotel;
	private String codlineaaz;
	private String codqualificasrq;
	private String codcomres;
	private String strindres;
	private String codcomdom;
	private String strinddom;
	private String codcittadinanza;
	private String codtitolo;
	private String prgazienda;
	private String prgunita;
	private String strnota;
	private String cdnutins;
	private String dtmins;
	private String cdnutmod;
	private String dtmmod;
	private String numklolavoratore = "0";
	private String codmonotipocompetenza;
	private String codTipoAccordo;
	private String codComCompetenza;
	private String datInizioCig;
	private String datFineCig;
	private String codComSede;

	/* 27/05 campi aggiunti per mobilità in deroga */
	// data di licenziamento
	private String datLicenziamento;
	// diritto alla disoccupazione ordinaria
	private String flgDirittoDO;
	// motivo per cui il lavoratore non ha diritto alla disoccupazione ordinaria
	private String codMotivoNotDO;

	private boolean esisteInAnag = false;
	private boolean competente = false;

	private Vector<CILavAccordoBean> listaLavAccordo;

	private String cdnlavoratore;

	private TransactionQueryExecutor tex;

	private static final Logger _logger = Logger.getLogger(CILavoratoreBean.class.getName());

	public CILavoratoreBean(SourceBean sb, Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex)
			throws EMFInternalError, ParseException {
		String strCodFiscLav = (String) sb.getCharacters("DATILAVORATORE.STRCODICEFISCALE");
		_logger.info("Inizio la costruzione del bean lavoratore " + strCodFiscLav);
		String strCognome = (String) sb.getCharacters("DATILAVORATORE.STRCOGNOME");
		String strNome = (String) sb.getCharacters("DATILAVORATORE.STRNOME");
		String strSesso = (String) sb.getCharacters("DATILAVORATORE.STRSESSO");
		String datAssunzione = (String) sb.getCharacters("DATILAVORATORE.DATASSUNZIONE");
		String codLineaAz = (String) sb.getCharacters("DATILAVORATORE.CODLINEAAZ");

		String datLicenziamento = (String) sb.getCharacters("DATILAVORATORE.DATLICENZIAMENTO");
		String flgDirittoDO = (String) sb.getCharacters("DATILAVORATORE.FLGDIRITTODO");
		String codMotivoNotDO = (String) sb.getCharacters("DATILAVORATORE.CODMOTIVONOTDO");

		if (datLicenziamento != null)
			this.datLicenziamento = Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", datLicenziamento);
		this.flgDirittoDO = flgDirittoDO;
		this.codMotivoNotDO = codMotivoNotDO;

		if (strCognome == null)
			throw new IllegalArgumentException("Il strcognome non puo' essere null.");
		if (strNome == null)
			throw new IllegalArgumentException("Il strnome non puo' essere null.");
		if (strCodFiscLav == null)
			throw new IllegalArgumentException("Il strcodicefiscale non puo' essere null.");
		if (strSesso == null)
			throw new IllegalArgumentException("Il strsesso non puo' essere null.");
		if (datAssunzione == null)
			throw new IllegalArgumentException("Il datassunzione non puo' essere null.");
		if (codLineaAz == null)
			throw new IllegalArgumentException("Il codlineaaz non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
		//
		this.strcognome = strCognome;
		this.strnome = strNome;
		this.strcodicefiscale = strCodFiscLav;
		this.strsesso = strSesso;
		this.datassunzione = Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", datAssunzione);
		this.codlineaaz = codLineaAz;
		this.cdnutins = cdnUtIns.toString();
		this.cdnutmod = cdnUtMod.toString();
		this.listaLavAccordo = new Vector<CILavAccordoBean>();
		this.tex = tex;
		this.esisteInAnag = checkEsisteInAnag();

		this.setCodcomnascita((String) sb.getCharacters("DATILAVORATORE.CODCOMNASCITA"));
		this.setDatnascita(Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy",
				(String) sb.getCharacters("DATILAVORATORE.DATNASCITA")));
		this.setStrcell((String) sb.getCharacters("DATILAVORATORE.STRCELL"));
		this.setStraltrotel((String) sb.getCharacters("DATILAVORATORE.STRALTROTEL"));
		this.setCodqualificasrq((String) sb.getCharacters("DATILAVORATORE.CODQUALIFICASRQ"));
		this.setCodcomres((String) sb.getCharacters("DATILAVORATORE.CODCOMRES"));
		this.setStrindres((String) sb.getCharacters("DATILAVORATORE.STRINDRES"));
		this.setCodcomdom((String) sb.getCharacters("DATILAVORATORE.CODCOMDOM"));
		this.setStrinddom((String) sb.getCharacters("DATILAVORATORE.STRINDDOM"));
		this.setCodcittadinanza((String) sb.getCharacters("DATILAVORATORE.CODCITTADINANZA"));
		this.setCodtitolo((String) sb.getCharacters("DATILAVORATORE.CODTITOLO"));
		this.setStrnota((String) sb.getCharacters("DATILAVORATORE.STRNOTA"));
	}

	/**
	 * Verifica se esiste già un altro lavoratore con lo stesso codice fiscale in AN_LAVORATORE
	 * 
	 * @return <code>true</code> se il lavoratore esiste già, false altrimenti.
	 * @throws EMFInternalError
	 */
	private boolean checkEsisteInAnag() throws EMFInternalError {
		Object pLavoratore[] = new Object[1];
		pLavoratore[0] = this.strcodicefiscale;

		SourceBean res = (SourceBean) tex.executeQuery("CHECK_ESISTE_LAV_ANAG", pLavoratore, "SELECT");

		if (res.containsAttribute("ROW.CDNLAVORATORE")) {
			this.cdnlavoratore = res.getAttribute("ROW.CDNLAVORATORE").toString();
			return true;
		} else
			return false;
	}

	/**
	 * Calcola data inizio e data fine cig per il lavoratore. Se non si riesce a calcolare la data fine, il lavoratore
	 * non è stato accolto. Questa funzione viene richiamata dopo la costruzione del lavoratore e prima di eseguire
	 * operazioni su di lui. In caso di rettifica o aggiornamento, datInizio e datFine devono essere stati valorizzati.
	 * 
	 * @param numConcessione
	 *            numero di concessione della domanda
	 * @param datConcessione
	 *            data di concessione della domanda
	 * @param codTipoConcessione
	 *            codice tipo concessione della domanda
	 * @param codtipoaccordo
	 *            codice tipo accordo della domanda
	 * @throws ParseException
	 *             nel caso in cui qualche data non sia ben formata (dd/MM/yyyy)
	 */
	public void calcolaDateCIG(String numConcessione, String datConcessione, String codTipoConcessione,
			String codtipoaccordo) throws ParseException {
		DateFormat formatter;
		formatter = new SimpleDateFormat("dd/MM/yyyy");

		Date datInizio = null;
		Date datFine = null;

		// true se la domanda è stata concessa
		boolean concessa = numConcessione != null && !numConcessione.equals("") && datConcessione != null
				&& !datConcessione.equals("") && codTipoConcessione != null
				&& (codTipoConcessione.equals(CONC_SI) || codTipoConcessione.equals(CONC_PARZIALE));

		// true se si tratta di una domanda di mobilità in deroga
		boolean mobilitainderoga = CigConst.AM_MOBILITA.equals(codtipoaccordo);

		int i = 0;

		for (; i < listaLavAccordo.size(); i++) {
			CILavAccordoBean lavAccordo = listaLavAccordo.get(i);
			if (concessa && !mobilitainderoga) {
				if (lavAccordo.getFlgaccolto().equals("S")) {
					datInizio = (Date) formatter.parse(lavAccordo.getDatiniziocigs());
					datFine = (Date) formatter.parse(lavAccordo.getDatfinecigs());
					break;
				}
			} else {
				datInizio = (Date) formatter.parse(lavAccordo.getDatiniziocigs());
				datFine = (Date) formatter.parse(lavAccordo.getDatfinecigs());
				break;
			}
		}

		if (datInizio == null || datFine == null) {
			// non c'è nessun accordo che soddisfa i criteri, in questo caso non possiamo calcolare le date
			this.setDatInizioCig(null);
			this.setDatFineCig(null);
			_logger.info("Nessun accordo soddisfa i criteri. Data inizio e data fine cig per il lavoratore "
					+ strcodicefiscale + " non calcolate.");
			return;
		}

		i++;

		for (; i < listaLavAccordo.size(); i++) {
			CILavAccordoBean lavAccordo = listaLavAccordo.get(i);
			Date tmpDatInizio = (Date) formatter.parse(lavAccordo.getDatiniziocigs());
			Date tmpDatFine = (Date) formatter.parse(lavAccordo.getDatfinecigs());
			if (concessa && !mobilitainderoga) {
				if (tmpDatInizio.before(datInizio) && lavAccordo.getFlgaccolto().equals("S")) {
					datInizio = tmpDatInizio;
				}
				if (tmpDatFine.after(datFine) && lavAccordo.getFlgaccolto().equals("S")) {
					datFine = tmpDatFine;
				}
			} else {
				if (tmpDatInizio.before(datInizio)) {
					datInizio = tmpDatInizio;
				}
				if (tmpDatFine.after(datFine)) {
					datFine = tmpDatFine;
				}
			}
		}

		this.setDatInizioCig(formatter.format(datInizio));
		this.setDatFineCig(formatter.format(datFine));
		_logger.info("Data inizio e data fine cig per il lavoratore " + strcodicefiscale + " calcolate correttamente.");
	}

	/**
	 * Calcola se il lavoratore è competente o meno per la provincia
	 * 
	 * @throws EMFInternalError
	 */
	public void calcolaCompetenza() throws EMFInternalError {
		Object pLavoratore[] = new Object[1];
		pLavoratore[0] = this.cdnlavoratore;
		SourceBean res = null;

		// se esiste già in anagrafica, verifico il comune di domicilio
		if (this.esisteInAnag) {
			res = (SourceBean) tex.executeQuery("CI_IS_DOM_ANAG_IN_PROV", pLavoratore, "SELECT");
			this.codComCompetenza = (String) res.getAttribute("ROW.CODCOMDOM");
			this.competente = ((String) res.getAttribute("ROW.FLAG")).equals("TRUE");
			if (this.competente) {
				this.codmonotipocompetenza = "A";
				return;
			}
		}

		pLavoratore = new Object[1];

		if (!this.competente) {

			// controllo se il comune di domicilio appartiene alla provincia
			pLavoratore[0] = this.codcomdom;
			boolean flagRegDom = false;
			res = (SourceBean) tex.executeQuery("CI_IS_DOM_IN_PROV", pLavoratore, "SELECT");

			if (StringUtils.getAttributeStrNotNull(res, "ROW.FLAGDOM").equals("TRUE")) {
				this.competente = true;
				this.codComCompetenza = this.codcomdom;
				this.codmonotipocompetenza = "D";
				return;
			} else if (StringUtils.getAttributeStrNotNull(res, "ROW.FLAGREGDOM").equals("TRUE")) {
				flagRegDom = true;
			}

			// controllo se il comune di residenza appartiene alla provincia
			boolean flagRegRes = false;
			if (!this.competente && !flagRegDom) {
				pLavoratore[0] = this.codcomres;
				res = (SourceBean) tex.executeQuery("CI_IS_RES_IN_PROV", pLavoratore, "SELECT");

				if (StringUtils.getAttributeStrNotNull(res, "ROW.FLAGRES").equals("TRUE")) {
					this.competente = true;
					this.codComCompetenza = this.codcomres;
					this.codmonotipocompetenza = "R";
					return;
				} else if (StringUtils.getAttributeStrNotNull(res, "ROW.FLAGREGRES").equals("TRUE")) {
					flagRegRes = true;
				}

				// infine controllo se il comune della sede è all'interno della provincia
				if (!this.competente && !flagRegDom && !flagRegRes) {
					pLavoratore[0] = this.codComSede;
					res = (SourceBean) tex.executeQuery("CI_IS_SEDE_IN_PROV", pLavoratore, "SELECT");

					if (StringUtils.getAttributeStrNotNull(res, "ROW.FLAGSEDE").equals("TRUE")) {
						this.competente = true;
						this.codComCompetenza = this.codComSede;
						this.codmonotipocompetenza = "S";
						return;
					}
				}
			}
		}
	}

	public String getCodmonotipocompetenza() {
		return codmonotipocompetenza;
	}

	/**
	 * Inserisce il lavoratore all'interno della tabella CI_LAVORATORE in maniera ottimistica. Non controllo prima i
	 * codici di decodifica ma aspetto che si schianti.
	 * 
	 * @throws EMFInternalError
	 */
	public void insert() throws EMFInternalError {
		Object pLavoratore[] = new Object[29];
		Object nextPrgLavoratore = getNextPrgLavoratore();

		pLavoratore[0] = nextPrgLavoratore;
		pLavoratore[1] = cdnlavoratore;
		pLavoratore[2] = prgaccordo;
		pLavoratore[3] = strcodicefiscale;
		pLavoratore[4] = strcognome;
		pLavoratore[5] = strnome;
		pLavoratore[6] = strsesso;
		pLavoratore[7] = datassunzione;
		pLavoratore[8] = codcomnascita;
		pLavoratore[9] = datnascita;
		pLavoratore[10] = strcell;
		pLavoratore[11] = straltrotel;
		pLavoratore[12] = codlineaaz;
		pLavoratore[13] = codqualificasrq;
		pLavoratore[14] = codcomres;
		pLavoratore[15] = strindres;
		pLavoratore[16] = codcomdom;
		pLavoratore[17] = strinddom;
		pLavoratore[18] = codcittadinanza;
		pLavoratore[19] = codtitolo;
		pLavoratore[20] = prgazienda;
		pLavoratore[21] = prgunita;
		pLavoratore[22] = strnota;
		pLavoratore[23] = cdnutins;
		pLavoratore[24] = cdnutmod;
		pLavoratore[25] = numklolavoratore;

		pLavoratore[26] = this.datLicenziamento;
		pLavoratore[27] = this.flgDirittoDO;
		pLavoratore[28] = this.codMotivoNotDO;

		tex.executeQuery("INSERT_CI_LAVORATORE", pLavoratore, "INSERT");
		// solo se l'inserimento va a buon fine setto il prglavoratore con cui e' stato registrato il record
		setPrglavoratore(nextPrgLavoratore.toString());
	}

	public void insertAnagrafica() throws Exception {
		Object nextCdnLavoratore = getNextCdnLavoratore();

		_logger.info("Inserisco il lavoratore " + getStrcodicefiscale() + " in anagrafica.");

		SourceBean req = new SourceBean("SERVICE_REQUEST");
		RequestContextIFace rcIFace = null;

		SourceBean res = new SourceBean("SERVICE_RESPONSE");
		RequestContainer rc = new RequestContainer();
		RequestContainer.setRequestContainer(rc);
		ResponseContainer rs = new ResponseContainer();
		rs.setErrorHandler(new EMFErrorHandler());
		SessionContainer session = new SessionContainer(false);

		User user = new User(new BigDecimal(cdnutins).intValue(), "", "", "");

		SourceBean row = (SourceBean) tex.executeQuery("GET_CODCPICAPOLUOGO", new Object[] {}, "SELECT");

		user.setCodRif(StringUtils.getAttributeStrNotNull(row, "ROW.RESULT"));

		session.setAttribute("@@USER@@", user);
		rc.setSessionContainer(session);
		rcIFace = new DefaultRequestContext(rc, rs);

		req.setAttribute("CDNLAVORATORE", nextCdnLavoratore);
		req.setAttribute("STRCODICEFISCALE", (strcodicefiscale == null) ? "" : strcodicefiscale);
		req.setAttribute("STRCOGNOME", (strcognome == null) ? "" : strcognome);
		req.setAttribute("STRNOME", (strnome == null) ? "" : strnome);
		req.setAttribute("STRSESSO", (strsesso == null) ? "" : strsesso);
		req.setAttribute("DATNASC", (datnascita == null) ? "" : datnascita);
		req.setAttribute("CODCOMNAS", (codcomnascita == null) ? "" : codcomnascita);
		req.setAttribute("CODCITTADINANZAHID", (codcittadinanza == null) ? "" : codcittadinanza);
		req.setAttribute("CODCOMRES", (codcomres == null) ? "" : codcomres);
		req.setAttribute("STRINDIRIZZORES", (strindres == null) ? "" : strindres);
		req.setAttribute("CODCOMDOM", (codcomdom == null) ? "" : codcomdom);
		req.setAttribute("STRINDIRIZZODOM", (strinddom == null) ? "" : strinddom);
		req.setAttribute("STRTELALTRO", (straltrotel == null) ? "" : straltrotel);
		req.setAttribute("STRCELL", (strcell == null) ? "" : strcell);
		req.setAttribute("FLGCFOK", "S");

		Object[] codcomDomOb = new Object[1];
		codcomDomOb[0] = codcomdom;
		SourceBean rowCpiLav = (SourceBean) tex.executeQuery("TRA_GET_CODCPI", codcomDomOb, "SELECT");

		req.setAttribute("CODCPI", StringUtils.getAttributeStrNotNull(rowCpiLav, "ROW.RESULT"));

		ModuleIFace modulo = ModuleFactory.getModule("M_InsertLavoratoreAnagIndirizzi");
		((RequestContextIFace) modulo).setRequestContext(rcIFace);
		((AbstractSimpleModule) modulo).enableTransactions(tex);
		rc.setServiceRequest(req);

		modulo.service(req, res);
		if (!rs.getErrorHandler().isOK())
			throw new Exception("Impossibile inserire il lavoratore nell' Anagrafica.");

		// solo se l'inserimento va a buon fine setto il cdnlavoratore con cui e' stato registrato il record
		setCdnlavoratore(nextCdnLavoratore.toString());
	}

	/**
	 * Inserisce il lavoratore all'interno dell'Indice Regionale
	 * 
	 * @throws Exception
	 */
	public void insertLavoratore_IR() throws Exception {

		_logger.info("Inserisco il lavoratore " + getStrcodicefiscale() + " in Indice Regionale.");

		SourceBean req = new SourceBean("SERVICE_REQUEST");
		RequestContextIFace rcIFace = null;

		SourceBean res = new SourceBean("SERVICE_RESPONSE");
		RequestContainer rc = new RequestContainer();
		RequestContainer.setRequestContainer(rc);
		ResponseContainer rs = new ResponseContainer();
		rs.setErrorHandler(new EMFErrorHandler());
		SessionContainer session = new SessionContainer(false);

		User user = new User(new BigDecimal(cdnutins).intValue(), "", "", "");

		SourceBean row = (SourceBean) tex.executeQuery("GET_CODCPICAPOLUOGO", new Object[] {}, "SELECT");

		user.setCodRif(StringUtils.getAttributeStrNotNull(row, "ROW.RESULT"));

		session.setAttribute("@@USER@@", user);
		rc.setSessionContainer(session);

		rcIFace = new DefaultRequestContext(rc, rs);

		req.setAttribute("CDNLAVORATORE", cdnlavoratore);
		req.setAttribute("STRCODICEFISCALE", (strcodicefiscale == null) ? "" : strcodicefiscale);
		req.setAttribute("STRCOGNOME", (strcognome == null) ? "" : strcognome);
		req.setAttribute("STRNOME", (strnome == null) ? "" : strnome);
		req.setAttribute("DATNASC", (datnascita == null) ? "" : datnascita);
		req.setAttribute("CODCOMNAS", (codcomnascita == null) ? "" : codcomnascita);

		ModuleIFace modulo = ModuleFactory.getModule("M_COOP_RibadisciPutLavoratoreIR");
		((RequestContextIFace) modulo).setRequestContext(rcIFace);
		((AbstractSimpleModule) modulo).enableTransactions(tex);
		rc.setServiceRequest(req);

		modulo.service(req, res);
		if (!rs.getErrorHandler().isOK())
			throw new Exception("Impossibile inserire il lavoratore nell' Indice Regionale.");

	}

	/**
	 * Inserisce il lavoratore nella tabella AM_ALTRA_ISCR
	 * 
	 * @param codStatoIscr
	 *            tipo di iscrizione
	 * @param codAccordo
	 *            codice accordo. Necessario per la verifica che non esistano altre domande aperte con lo stesso codice.
	 * @throws EMFInternalError
	 */
	public void insertAmAltraIscr(String codStatoIscr, String codAccordo) throws EMFInternalError {
		Object pVerifica[] = new Object[7];

		String datInizio = this.getDatInizioCig();
		String datFine = this.getDatFineCig();

		pVerifica[0] = this.cdnlavoratore;
		pVerifica[1] = datInizio;
		pVerifica[2] = datFine;
		pVerifica[3] = datInizio;
		pVerifica[4] = datFine;
		pVerifica[5] = datInizio;
		pVerifica[6] = datFine;

		SourceBean row;
		BigDecimal num;
		// verifico se ci sono periodi di cig che si intersecano, in tal caso esco
		/*
		 * 240310 condizione non più necessaria row = (SourceBean)tex.executeQuery("CHECK_ISCR_INTER_DATE", pVerifica,
		 * "SELECT"); num = new BigDecimal(row.getAttribute("ROW.NUMISCR").toString()); if(num.intValue()>0) return;
		 */

		// verifico che non esista più di una cig aperta per lo stesso lavoratore, stessa azienda e stesso codAccordo
		// 070910 modificata la query per considerare lo stesso codAccordo.
		row = (SourceBean) tex.executeQuery("CHECK_ALTRE_ISCR_APERTE",
				new Object[] { this.cdnlavoratore, this.getPrgazienda(), codAccordo }, "SELECT");
		num = new BigDecimal(row.getAttribute("ROW.ISCRAPERTA").toString());
		if (num.intValue() > 1) {
			_logger.info(
					"Attenzione: l'iscrizione non è stata inserita perché esiste più di una cig aperta per lo stesso lavoratore, la stessa azienda e che fa riferimento alla domanda "
							+ codAccordo + ".");
			return;
		}

		Object pLavoratore[] = new Object[20];
		Object nextPrgAltraIscr = getNextPrgAltraIscr();

		pLavoratore[0] = nextPrgAltraIscr; // PRGALTRAISCR
		pLavoratore[1] = this.cdnlavoratore; // CDNLAVORATORE
		pLavoratore[2] = this.prgazienda; // PRGAZIENDA
		pLavoratore[3] = this.prgunita; // PRGUNITA
		pLavoratore[4] = this.prgaccordo; // PRGACCORDO
		pLavoratore[5] = this.codComCompetenza;
		pLavoratore[6] = this.codmonotipocompetenza;
		pLavoratore[7] = null;
		pLavoratore[8] = codStatoIscr;
		pLavoratore[9] = this.codTipoAccordo; // CODTIPOISCR
		pLavoratore[10] = null; // DATCHIUSURAISCR
		pLavoratore[11] = this.datInizioCig; // DATCOMPETENZA
		pLavoratore[12] = this.datFineCig; // DATFINE
		pLavoratore[13] = this.datInizioCig; // DATINIZIO
		pLavoratore[14] = "0"; // NUMKLOALTRAISCR
		pLavoratore[15] = cdnutins; // CDNUTINS
		pLavoratore[16] = cdnutmod; // CDNUTMOD

		pLavoratore[17] = this.datLicenziamento;
		pLavoratore[18] = this.flgDirittoDO;
		pLavoratore[19] = this.codMotivoNotDO;

		tex.executeQuery("INSERT_AM_ALTRA_ISCR", pLavoratore, "INSERT");

		_logger.info("Inserita iscrizione " + nextPrgAltraIscr + " in AM_ALTRA_ISCR.");
	}

	public void updateAmAltraIscr(BigDecimal prgAltraIscrComp, BigDecimal numkloAltraIscrComp, String prgAccordo)
			throws EMFInternalError {
		Object pAltraIscr[] = new Object[7];

		{
			int j = 0;
			pAltraIscr[j++] = prgAccordo; // LEGO L'ISCRIZIONE ALLA DOMANDA
			pAltraIscr[j++] = cdnutmod;
			pAltraIscr[j++] = this.datInizioCig; // DATINIZIO
			pAltraIscr[j++] = this.datFineCig; // DATFINE
			pAltraIscr[j++] = this.codTipoAccordo; // TIPOISCR
			pAltraIscr[j++] = numkloAltraIscrComp; // gli passo NUMKLOK
			pAltraIscr[j++] = prgAltraIscrComp;
		}

		tex.executeQuery("UPDATE_AM_ALTRA_ISCR", pAltraIscr, "UPDATE");

		_logger.info("Aggiornata iscrizione " + prgAltraIscrComp + " in AM_ALTRA_ISCR.");
	}

	public BigDecimal[] cercaIscrizioneCompatibile(int ggTolleranza) throws EMFInternalError {
		Object pIscrizione[] = new Object[6];
		BigDecimal[] ret = new BigDecimal[2];

		pIscrizione[0] = this.cdnlavoratore;
		pIscrizione[1] = this.prgazienda;
		pIscrizione[2] = this.datInizioCig;
		pIscrizione[3] = ggTolleranza;
		pIscrizione[4] = this.datFineCig;
		pIscrizione[5] = ggTolleranza;

		SourceBean row = (SourceBean) tex.executeQuery("GET_ISCR_COMPATIBILE", pIscrizione, "SELECT");

		BigDecimal prgAltraIscr = (BigDecimal) row.getAttribute("ROW.PRGALTRAISCR");
		BigDecimal numkloAltraIscr = (BigDecimal) row.getAttribute("ROW.NUMKLOALTRAISCR");

		if (prgAltraIscr == null)
			return null;

		_logger.info("Trovata iscrizione compatibile! " + prgAltraIscr + ". Periodo: " + datInizioCig + " - "
				+ datFineCig + ". numlock: " + numkloAltraIscr);

		ret[0] = prgAltraIscr;
		ret[1] = numkloAltraIscr;
		return ret;
	}

	public void updateCellulare() throws EMFInternalError {
		// se mi è arrivato un numero di cellulare e non è presente su DB
		if (strcell != null && !hasACellPhoneNumber()) {
			_logger.info("Aggiorno il cellulare del lavoratore " + strcodicefiscale);
			Object pUpdateCell[] = new Object[3];
			{
				int j = 0;
				pUpdateCell[j++] = strcell;
				pUpdateCell[j++] = cdnutmod; //
				pUpdateCell[j++] = cdnlavoratore; //
			}
			tex.executeQuery("UPDATE_CELL_NUM", pUpdateCell, "UPDATE");
		} else {
			_logger.info("Non aggiorno il cellulare del lavoratore " + strcodicefiscale);
		}

	}

	private boolean hasACellPhoneNumber() throws EMFInternalError {
		Object pLavoratore[] = new Object[1];
		pLavoratore[0] = this.cdnlavoratore;

		SourceBean res = (SourceBean) tex.executeQuery("CHECK_ESISTE_CELL_NUM", pLavoratore, "SELECT");

		String number = (String) res.getAttribute("ROW.STRCELL");
		boolean hasNumber = (number != null && !"".equals(number.trim()));
		return (hasNumber);
	}

	private BigDecimal getNextPrgAltraIscr() throws EMFInternalError {
		String query = "select s_am_altra_iscr.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella am_altra_iscr");
		return nextval;
	}

	private BigDecimal getNextCdnLavoratore() throws EMFInternalError {

		String query = "select s_an_lavoratore.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella an_lavoratore");
		return nextval;
	}

	private BigDecimal getNextPrgLavoratore() throws EMFInternalError {

		String query = "select s_ci_lavoratore.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella ci_lavoratore");
		return nextval;
	}

	public void addLavAccordo(CILavAccordoBean lavAcc) {
		listaLavAccordo.addElement(lavAcc);
	}

	public String getPrglavoratore() {
		return prglavoratore;
	}

	public void setPrglavoratore(String prglavoratore) {
		this.prglavoratore = prglavoratore;
	}

	public String getPrgaccordo() {
		return prgaccordo;
	}

	public void setPrgaccordo(String prgaccordo) {
		this.prgaccordo = prgaccordo;
	}

	public String getStrcodicefiscale() {
		return strcodicefiscale;
	}

	public void setStrcodicefiscale(String strcodicefiscale) {
		this.strcodicefiscale = strcodicefiscale;
	}

	public String getStrcognome() {
		return strcognome;
	}

	public void setStrcognome(String strcognome) {
		this.strcognome = strcognome;
	}

	public String getStrnome() {
		return strnome;
	}

	public void setStrnome(String strnome) {
		this.strnome = strnome;
	}

	public String getStrsesso() {
		return strsesso;
	}

	public void setStrsesso(String strsesso) {
		this.strsesso = strsesso;
	}

	public String getDatassunzione() {
		return datassunzione;
	}

	public void setDatassunzione(String datassunzione) {
		this.datassunzione = datassunzione;
	}

	public String getCodcomnascita() {
		return codcomnascita;
	}

	public void setCodcomnascita(String codcomnascita) {
		this.codcomnascita = codcomnascita;
	}

	public String getDatnascita() {
		return datnascita;
	}

	public void setDatnascita(String datnascita) {
		this.datnascita = datnascita;
	}

	public String getStrcell() {
		return strcell;
	}

	public void setStrcell(String strcell) {
		this.strcell = strcell;
	}

	public String getStraltrotel() {
		return straltrotel;
	}

	public void setStraltrotel(String straltrotel) {
		this.straltrotel = straltrotel;
	}

	public String getCodlineaaz() {
		return codlineaaz;
	}

	public void setCodlineaaz(String codlineaaz) {
		this.codlineaaz = codlineaaz;
	}

	public String getCodqualificasrq() {
		return codqualificasrq;
	}

	public void setCodqualificasrq(String codqualificasrq) {
		this.codqualificasrq = codqualificasrq;
	}

	public String getCodcomres() {
		return codcomres;
	}

	public void setCodcomres(String codcomres) {
		this.codcomres = codcomres;
	}

	public String getStrindres() {
		return strindres;
	}

	public void setStrindres(String strindres) {
		this.strindres = strindres;
	}

	public String getCodcomdom() {
		return codcomdom;
	}

	public void setCodcomdom(String codcomdom) {
		this.codcomdom = codcomdom;
	}

	public String getStrinddom() {
		return strinddom;
	}

	public void setStrinddom(String strinddom) {
		this.strinddom = strinddom;
	}

	public String getCodcittadinanza() {
		return codcittadinanza;
	}

	public void setCodcittadinanza(String codcittadinanza) {
		this.codcittadinanza = codcittadinanza;
	}

	public String getCodtitolo() {
		return codtitolo;
	}

	public void setCodtitolo(String codtitolo) {
		this.codtitolo = codtitolo;
	}

	public String getPrgazienda() {
		return prgazienda;
	}

	public void setPrgazienda(String prgazienda) {
		this.prgazienda = prgazienda;
	}

	public String getPrgunita() {
		return prgunita;
	}

	public void setPrgunita(String prgunita) {
		this.prgunita = prgunita;
	}

	public String getStrnota() {
		return strnota;
	}

	public void setStrnota(String strnota) {
		this.strnota = strnota;
	}

	public String getCdnutins() {
		return cdnutins;
	}

	public void setCdnutins(String cdnutins) {
		this.cdnutins = cdnutins;
	}

	public String getDtmins() {
		return dtmins;
	}

	public void setDtmins(String dtmins) {
		this.dtmins = dtmins;
	}

	public String getCdnutmod() {
		return cdnutmod;
	}

	public void setCdnutmod(String cdnutmod) {
		this.cdnutmod = cdnutmod;
	}

	public String getDtmmod() {
		return dtmmod;
	}

	public void setDtmmod(String dtmmod) {
		this.dtmmod = dtmmod;
	}

	public String getNumklolavoratore() {
		return numklolavoratore;
	}

	public void setNumklolavoratore(String numklolavoratore) {
		this.numklolavoratore = numklolavoratore;
	}

	public TransactionQueryExecutor getTex() {
		return tex;
	}

	public void setTex(TransactionQueryExecutor tex) {
		this.tex = tex;
	}

	public String getCdnlavoratore() {
		return cdnlavoratore;
	}

	public void setCdnlavoratore(String cdnlavoratore) {
		this.cdnlavoratore = cdnlavoratore;
	}

	public String getCodTipoAccordo() {
		return codTipoAccordo;
	}

	public void setCodTipoAccordo(String codTipoAccordo) {
		this.codTipoAccordo = codTipoAccordo;
	}

	public String getDatInizioCig() {
		return datInizioCig;
	}

	public void setDatInizioCig(String datInizioCig) {
		this.datInizioCig = datInizioCig;
	}

	public String getDatFineCig() {
		return datFineCig;
	}

	public void setDatFineCig(String datFineCig) {
		this.datFineCig = datFineCig;
	}

	public Vector<CILavAccordoBean> getListaLavAccordo() {
		return listaLavAccordo;
	}

	public void setListaLavAccordo(Vector<CILavAccordoBean> listaLavAccordo) {
		this.listaLavAccordo = listaLavAccordo;
	}

	public boolean isEsisteInAnag() {
		return esisteInAnag;
	}

	public void setEsisteInAnag(boolean esisteInAnag) {
		this.esisteInAnag = esisteInAnag;
	}

	/**
	 * Restituisce <code>true</code> se il lavoratore è competente per la provincia
	 * 
	 * @return
	 */
	public boolean isCompetente() {
		return competente;
	}

	public void setCompetente(boolean competente) {
		this.competente = competente;
	}

	public String getCodComSede() {
		return codComSede;
	}

	public void setCodComSede(String codComSede) {
		this.codComSede = codComSede;
	}

	public String getCodMotivoNotDO() {
		return codMotivoNotDO;
	}

	public void setCodMotivoNotDO(String codMotivoNotDO) {
		this.codMotivoNotDO = codMotivoNotDO;
	}

	public String getDatLicenziamento() {
		return datLicenziamento;
	}

	public String getFlgDirittoDO() {
		return flgDirittoDO;
	}

}
