package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.LogUtils;
import it.eng.sil.Values;
import it.eng.sil.security.PageAttribs;

/**
 * Classe per il recupero dati dell'azienda
 */
public class InfoAzienda {

	String statement = "SELECT AZ.STRRAGIONESOCIALE AS RAGIONESOCIALE, " + " AZ.STRCODICEFISCALE AS CODICEFISCALE, "
			+ " AZ.STRPARTITAIVA AS PIVA, " + " U.STRINDIRIZZO || ', ' || U.STRLOCALITA AS INDIRIZZO, "
			+ " C.STRDENOMINAZIONE AS DENOMINAZIONE, " + " U.CODCCNL AS CCNLAZ, "
			+ " NATGIUR.STRDESCRIZIONE DESCRNATGIURAZ,  " + " U.STRTEL STRTELAZ, " + " U.STRFAX STRFAXAZ, "
			+ " C.STRCAP STRCAPAZ, " + " DECCNL.STRDESCRIZIONE AS DESCRCCNL, " + " AZ.CODTIPOAZIENDA AS CODTIPOAZ, "
			+ " TIPOAZ.STRDESCRIZIONE AS DESCRTIPOAZ, " + " AZ.STRNUMALBOINTERINALI AS STRNUMALBOINT, "
			+ " U.STRNUMREGISTROCOMMITT AS STRNUMREGCOMM, " + " U.CODATECO CODATECO, "
			+ " U.STRCFAZESTERA AS CODFISCAZESTERA, U.STRRAGSOCAZESTERA AS RAGSOCAZESTERA, "
			+ " ATT.STRDESCRIZIONE DESCRATECO, " + " AZ.CODNATGIURIDICA CODNATGIURIDICA, " + " AZ.FLGDATIOK FLGDATIOK, "
			+ " ST.CODCPITIT CODCPI" + " FROM " + " AN_AZIENDA AZ, " + " AN_UNITA_AZIENDA U, " + " DE_COMUNE C, "
			+ " DE_CONTRATTO_COLLETTIVO DECCNL, " + " DE_TIPO_AZIENDA TIPOAZ, " + " DE_ATTIVITA ATT, "
			+ " DE_NAT_GIURIDICA NATGIUR, " + " AN_UA_STORIA_INF ST" + " WHERE " + " U.PRGAZIENDA = ST.PRGAZIENDA AND "
			+ " U.PRGUNITA = ST.PRGUNITA AND " + " AZ.PRGAZIENDA = U.PRGAZIENDA AND "
			+ " U.CODCCNL = DECCNL.CODCCNL (+) AND " + " AZ.CODTIPOAZIENDA = TIPOAZ.CODTIPOAZIENDA AND "
			+ " C.CODCOM = U.CODCOM AND " + " U.CODATECO = ATT.CODATECO AND "
			+ " AZ.CODNATGIURIDICA = NATGIUR.CODNATGIURIDICA (+) ";

	Object prgAzienda = null;

	Object prgUnita = null;
	Object prgRichiesta = null;

	String annoRichiesta = "";
	BigDecimal numStorico = null;

	String ragioneSociale = "&nbsp;&nbsp;";
	String codiceFiscale = "&nbsp;&nbsp;";
	String pIva = "&nbsp;&nbsp;";
	String indirizzo = "&nbsp;&nbsp;";
	String comune = "&nbsp;&nbsp;";
	// Campi sotto aggiunti da Paolo Roccetti per il recupero dei dati nel
	// dettaglio generale del movimento
	String codiceCCNL = "";
	String descrCCNL = "";
	String tipoAz = "";
	String telAz = "";
	String faxAz = "";
	String capAz = "";
	String descrTipoAz = "";
	String numAlboInter = "";
	String numRegComm = "";
	String codAtecoAz = "";
	String descrAtecoAz = "";
	String descrNatGiurAz = "";
	String codNatGiurAz = "";
	String strFlgDatiOk = "";
	String codCpi = "";
	String codFisSommEstera = "";
	String ragSocSommEstera = "";

	String infoCorrentiPage = "AmstrInfoCorrentiPage";

	private boolean existInfo = true;
	private int maxLenStatoOcc = 50;
	private PageAttribs pageAttribs;
	private List sezioni;

	// flag che indica se il link verrà aperto con una popUp
	public static boolean PopUp_NO = false;
	public static boolean PopUp_YES = true;

	// Var destinate ad usi futuri quando verra implementata la profilatura; i
	// nomi sono puramente inventati
	public static int LAV_GENERICO = 1;
	public static int LAV_METAL = 2;

	/**
	 * 
	 * @param prgAzienda
	 * @param prgUnita
	 * @param prgRichiesta
	 */
	public InfoAzienda(Object prgAzienda, Object prgUnita, Object prgRichiesta) {
		this.prgAzienda = prgAzienda;
		this.prgUnita = prgUnita;
		this.prgRichiesta = prgRichiesta;
		init();
	}

	/**
	 * @DEPRECATED
	 * @param prgAzienda
	 * @param prgUnita
	 */
	public InfoAzienda(Object prgAzienda, Object prgUnita) {
		this.prgAzienda = prgAzienda;
		this.prgUnita = prgUnita;
		this.prgRichiesta = null;
		init();
	}

	/**
	 * Costruttore.
	 * 
	 * @param prgAzienda
	 * @param prgUnita
	 */
	private void init() {
		// this.prgAzienda = prgAzienda;
		// this.prgUnita = prgUnita;

		SourceBean datiAzienda = null;
		if (prgAzienda != null && prgUnita != null)
			datiAzienda = getInfoAzienda();

		if (datiAzienda != null) {
			ragioneSociale = datiAzienda.containsAttribute("ragioneSociale")
					? (String) datiAzienda.getAttribute("ragioneSociale")
					: "&nbsp;&nbsp;";
			codiceFiscale = datiAzienda.containsAttribute("codiceFiscale")
					? (String) datiAzienda.getAttribute("codiceFiscale")
					: "&nbsp;&nbsp;";
			pIva = datiAzienda.containsAttribute("pIva") ? (String) datiAzienda.getAttribute("pIva") : "&nbsp;&nbsp;";
			indirizzo = datiAzienda.containsAttribute("indirizzo") ? (String) datiAzienda.getAttribute("indirizzo")
					: "&nbsp;&nbsp;";
			comune = datiAzienda.containsAttribute("denominazione") ? (String) datiAzienda.getAttribute("denominazione")
					: "&nbsp;&nbsp;";
			// Campi sotto aggiunti da Paolo Roccetti per il recupero dei dati
			// nel dettaglio generale del movimento
			codiceCCNL = datiAzienda.containsAttribute("CCNLAZ") ? (String) datiAzienda.getAttribute("CCNLAZ") : "";
			descrCCNL = datiAzienda.containsAttribute("DESCRCCNL") ? (String) datiAzienda.getAttribute("DESCRCCNL")
					: "";
			tipoAz = datiAzienda.containsAttribute("CODTIPOAZ") ? (String) datiAzienda.getAttribute("CODTIPOAZ") : "";
			telAz = datiAzienda.containsAttribute("STRTELAZ") ? (String) datiAzienda.getAttribute("STRTELAZ") : "";
			faxAz = datiAzienda.containsAttribute("STRFAXAZ") ? (String) datiAzienda.getAttribute("STRFAXAZ") : "";
			capAz = datiAzienda.containsAttribute("STRCAPAZ") ? (String) datiAzienda.getAttribute("STRCAPAZ") : "";
			descrTipoAz = datiAzienda.containsAttribute("DESCRTIPOAZ")
					? (String) datiAzienda.getAttribute("DESCRTIPOAZ")
					: "";
			numAlboInter = datiAzienda.containsAttribute("STRNUMALBOINT")
					? (String) datiAzienda.getAttribute("STRNUMALBOINT")
					: "";
			numRegComm = datiAzienda.containsAttribute("STRNUMREGCOMM")
					? (String) datiAzienda.getAttribute("STRNUMREGCOMM")
					: "";
			codAtecoAz = datiAzienda.containsAttribute("CODATECO") ? (String) datiAzienda.getAttribute("CODATECO") : "";
			descrAtecoAz = datiAzienda.containsAttribute("DESCRATECO") ? (String) datiAzienda.getAttribute("DESCRATECO")
					: "";
			descrNatGiurAz = datiAzienda.containsAttribute("DESCRNATGIURAZ")
					? (String) datiAzienda.getAttribute("DESCRNATGIURAZ")
					: "";
			codNatGiurAz = datiAzienda.containsAttribute("CODNATGIURIDICA")
					? (String) datiAzienda.getAttribute("CODNATGIURIDICA")
					: "";
			strFlgDatiOk = datiAzienda.containsAttribute("FLGDATIOK") ? (String) datiAzienda.getAttribute("FLGDATIOK")
					: "";
			codCpi = datiAzienda.containsAttribute("CODCPI") ? (String) datiAzienda.getAttribute("CODCPI") : "";

			codFisSommEstera = datiAzienda.containsAttribute("CODFISCAZESTERA")
					? (String) datiAzienda.getAttribute("CODFISCAZESTERA")
					: "";
			ragSocSommEstera = datiAzienda.containsAttribute("RAGSOCAZESTERA")
					? (String) datiAzienda.getAttribute("RAGSOCAZESTERA")
					: "";

		} else {
			this.existInfo = false;
		}
	}

	/**
	 * Ritorna un SourceBean con Ragione Sociale, cCodice Fiscale, Partita IVA ed indirizzo relative all'unita
	 * dell'azienda, prelevate attraverso la query al DB chiamata GET_InfUnitaAzienda
	 * 
	 * @param prgAzienda
	 *            progressivo azienda
	 * @param prgUnita
	 *            progressivo unita
	 * @return SourceBean contenente informazioni relative l'unita e l'azienda
	 */
	private SourceBean getInfoAzienda() {
		SourceBean riga = null;
		// Map result = new HashMap();

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String stat = statement + " AND U.PRGAZIENDA = " + this.prgAzienda + " AND U.PRGUNITA = " + this.prgUnita;

			sqlCommand = dataConnection.createSelectCommand(stat);

			// List inputParameter = new ArrayList();
			// inputParameter.add(dataConnection.createDataField("",
			// Types.NUMERIC, prgAzienda));
			// inputParameter.add(dataConnection.createDataField("",
			// Types.NUMERIC, prgUnita));

			dataResult = sqlCommand.execute();

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");
			if (righe.size() == 1) {
				riga = (SourceBean) righe.get(0);
			} else {
				LogUtils.logError("getInfoAzienda", "L'azienda con codice " + prgAzienda
						+ " non è stata trovata nel DB\n oppure sono state trovate più occorenze", this);
			}
		} // try
		catch (com.engiweb.framework.error.EMFInternalError ex) {
			LogUtils.logError("getInfoAzienda", "Internal Error", ex, this);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
		return riga;
	} // getInfoAzienda(_)

	/**
	 * 
	 */
	public String getRagioneSociale() {
		return this.ragioneSociale;
	}

	/**
	 * 
	 */
	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	/**
	 * 
	 */
	public String getPIva() {
		return this.pIva;
	}

	/**
	 * 
	 */
	public String getIndirizzo() {
		return this.indirizzo;
	}

	/**
	 * 
	 */
	public String getComune() {
		return this.comune;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getCCNL() {
		return this.codiceCCNL;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getDescrCCNL() {
		return this.descrCCNL;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getTipoAz() {
		return this.tipoAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getTelAz() {
		return this.telAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getFaxAz() {
		return this.faxAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getCapAz() {
		return this.capAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getDescrTipoAz() {
		return this.descrTipoAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getNumAlboInter() {
		return this.numAlboInter;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getNumRegComm() {
		return this.numRegComm;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getCodAtecoAz() {
		return this.codAtecoAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getDescrAtecoAz() {
		return this.descrAtecoAz;
	}

	/**
	 * @author Paolo Roccetti
	 */
	public String getDescrNatGiurAz() {
		return this.descrNatGiurAz;
	}

	/**
	 * @author Giuseppe De Simone
	 */
	public String getCodNatGiurAz() {
		return this.codNatGiurAz;
	}

	/**
	 * @author Giuseppe De Simone
	 */
	public String getFlgDatiOk() {
		return this.strFlgDatiOk;
	}

	/**
	 * @return
	 */
	public Object getPrgRichiesta() {
		return prgRichiesta;
	}

	/**
	 * @param object
	 */
	public void setPrgRichiesta(Object object) {
		prgRichiesta = object;
	}

	/**
	 * Codice CPI dell'azienda
	 */
	public String getCodCpi() {
		return this.codCpi;
	}

	public String getCFSommEstera() {
		return codFisSommEstera;
	}

	public void setCFSommEstera(String cf) {
		codFisSommEstera = cf;
	}

	public String getRagSocSommEstera() {
		return ragSocSommEstera;
	}

	public void setRagSocSommEstera(String ragSoc) {
		ragSocSommEstera = ragSoc;
	}

} // InfCorrentiAzienda
