package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.processors.Warning;

public class StatoOccupazionaleBean extends StatoOccupazionaleBase implements Cloneable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StatoOccupazionaleBean.class.getName());
	/**
	 * Altro
	 */
	public static final int C = 0;
	/**
	 * Disoccupato
	 */
	public static final int A21 = 1;
	/**
	 * Inoccupato
	 */
	public static final int A22 = 2;
	/**
	 * Occupato
	 */
	public static final int B = 3;
	/**
	 * Occupato in cerca di altra occupazione
	 */
	public static final int A1 = 4;
	/**
	 * Precario con att. lav. che non sospende lo stato di disoc.
	 */
	public static final int A212 = 5;
	/**
	 * Persona con attività formativa o lavorativa senza contratto
	 */
	public static final int A213 = 6;
	/**
	 * In sospensione della anzianità ex art. 4 D.L. 181/2000
	 */
	public static final int B1 = 7;
	/**
	 * Inoccupato con attività lavorativa o formativa senza contratto
	 */
	public static final int A223 = 8;
	/**
	 * Segnalato dalle agenzie di mediazione
	 */
	public static final int E = 9;
	/**
	 * Proveniente dal flusso scolastico
	 */
	public static final int F = 10;
	/**
	 * Segnalato dalle imprese di lavoro temporaneo
	 */
	public static final int D = 11;
	/**
	 * Cessato dall impiego
	 */
	public static final int C0 = 12;
	/**
	 * Decaduto dallo stato di disoccupazione
	 */
	public static final int C1 = 13;
	/**
	 * Decaduto per mancata disponibilità al lavoro ex. D.L. 181
	 */
	public static final int C11 = 14;
	/**
	 * Decaduto per mancata presentazione al colloquio orien.
	 */
	public static final int C12 = 15;
	/**
	 * Decaduto per mancata adesione proposta congrua
	 */
	public static final int C13 = 16;
	/**
	 * Sospeso per contrazione d'attività
	 */
	public static final int A0 = 17;
	/**
	 * decaduto per rifiuto politica attiva
	 */
	public static final int C14 = 18;
	/**
	 * Altro con codice inesistente
	 */
	public static final int NT = 19;
	/**
	 * In Mobilità occupato
	 */
	public static final int B2 = 20;
	/**
	 * Occupato a rischio disoccupazione
	 */
	public static final int B3 = 21;

	/**
	 * Raggruppamenti associati agli stati occupazionali introdotti in precedenza
	 */
	public static final int RAGG_O = 0;
	public static final int RAGG_D = 1;
	public static final int RAGG_I = 2;
	public static final int RAGG_A = 3;

	public static final String COD_RAGG_O = "O";
	public static final String COD_RAGG_A = "A";
	public static final String COD_RAGG_I = "I";
	public static final String COD_RAGG_D = "D";

	public static final String[] deStatoOccupaz = { "C", // Altro
			"A21", // Disoccupato
			"A22", // Inoccupato
			"B", // Occupato
			"A1", // Occupato in cerca di altra occupazione
			"A212", // Precario con att. lav. che non sospende lo stato di
					// disoc.
			"A213", // Persona con attività formativa o lavorativa senza
					// contratto
			"B1", // In sospensione della anzianità ex art. 4 D.L. 181/2000
			"A223", // Inoccupato con attività lavorativa o formativa senza
					// contratto
			"E", // Segnalato dalle agenzie di mediazione
			"F", // Proveniente dal flusso scolastico
			"D", // Segnalato dalle imprese di lavoro temporaneo
			"C0", // Cessato (non rientrato)
			"C1", // Decaduto dallo stato di disoccupazione
			"C11", // Decaduto per mancata disponibilità al lavoro ex. D.L. 181
			"C12", // Decaduto per mancata presentazione al colloquio orien.
			"C13", // Decaduto per mancata adesione proposta congrua
			"A0", // Sospeso per contrazione d'attività
			"C14", // decaduto per rifiuto politica attiva
			"NT", // Altro con codice inesistente
			"B2", // In mobilità
			"B3" // Occupato a rischio disoccupazione
	};

	public static final int[] deStatoOccRagg = { RAGG_A, // "C", // Altro
			RAGG_D, // "A21", // Disoccupato
			RAGG_I, // "A22", // Inoccupato
			RAGG_O, // "B", // Occupato
			RAGG_O, // "A1", // Occupato in cerca di altra occupazione
			RAGG_D, // "A212", // Precario con att. lav. che non sospende lo
					// stato di disoc.
			RAGG_D, // "A213", // Persona con attività formativa o lavorativa
					// senza contratto
			RAGG_D, // "B1", // In sospensione della anzianità ex art. 4 D.L.
					// 181/2000
			RAGG_I, // "A223", // Inoccupato con attività lavorativa o formativa
					// senza contratto
			RAGG_A, // "E", // Segnalato dalle agenzie di mediazione
			RAGG_A, // "F", // Proveniente dal flusso scolastico
			RAGG_A, // "D", // Segnalato dalle imprese di lavoro temporaneo
			RAGG_A, // "C0", // Cessato (non rientrato)
			RAGG_A, // "C1", // Decaduto dallo stato di disoccupazione
			RAGG_A, // "C11", // Decaduto per mancata disponibilità al lavoro
					// ex. D.L. 181
			RAGG_A, // "C12", // Decaduto per mancata presentazione al colloquio
					// orien.
			RAGG_A, // "C13", // Decaduto per mancata adesione proposta congrua
			RAGG_O, // "A0", // Sospeso per contrazione d'attività
			RAGG_A, // "C14", // decaduto per rifiuto politica attiva
			RAGG_A, // "NT", // codice inesistente
			RAGG_D, // "B2", // in mobilità
			RAGG_D // "B3", // Occupato a rischio disoccupazione
	};

	public final static String[] mapStatoOccRagg = { COD_RAGG_O, // RAGG_O
			COD_RAGG_D, // RAGG_D
			COD_RAGG_I, // RAGG_I
			COD_RAGG_A // RAGG_A
	};

	/**
	 * associa ad uno stato di occupazione iniziale quello finale in base alle 3 situazioni che si possono presentare
	 * [0]: il reddito del lavoratore e' minore del limite ([REDDITO_BASSO]) [1]: il reddito del lavoratore e' maggiore
	 * del limite ma appartiene a quella categoria particolare (TD <8 mesi) ([REDDITO_ALTO_CAT_SPECIALE]) [2]: il
	 * reddito del lavoratore e' maggiore del limite e non appartiene alla categoria 'particolare' ([REDDITO_ALTO]) [3]:
	 * (MOV < 15 GIORNI)
	 */
	public static final int[][] deCambioStato = { { B, B, B, C }, // "C",
																	// Altro
			{ A212, B1, B, A21 }, // "A21", Disoccupato
			{ A212, B1, B, A21 }, // "A22", Inoccupato
			{ A21, B, B, B }, // "B", Occupato
			{ A21, A21, A21, A1 }, // "A1", Occupato in cerca di altra
									// occupazione
			{ A212, B1, B, A212 }, // "A212",Precario con att. lav. che non
									// sospende lo stato di disoc.
			{ A212, B1, B, A213 }, // "A213",Persona con attività formativa o
									// lavorativa senza contratto
			{ B1, B1, B, B1 }, // "B1", In sospensione della anzianità ex art.
								// 4 D.L. 181/2000
			{ A212, B1, B, A21 }, // "A223",Inoccupato con attività lavorativa
									// o formativa senza contratto
			{ B, B, B, E }, // "E", Segnalato dalle agenzie di mediazione
			{ B, B, B, F }, // "F", Proveniente dal flusso scolastico
			{ B, B, B, D }, // "D", Segnalato dalle imprese di lavoro temporaneo
			{ B, B, B, C0 }, // "C0", Cessato (non rientrato)
			{ B, B, B, C1 }, // "C1", Decaduto dallo stato di disoccupazione
			{ B, B, B, C11 }, // "C11", Decaduto per mancata disponibilità al
								// lavoro ex. D.L. 181
			{ B, B, B, C12 }, // "C12", Decaduto per mancata presentazione al
								// colloquio orien.
			{ B, B, B, C13 }, // "C13", Decaduto per mancata adesione proposta
								// congrua
			{ B, B, B, A0 }, // "A0", Sospeso per contrazione d'attività
			{ B, B, B, C14 }, // "C14", Decaduto per rifiuto politica attiva
			{}, { A212, B1, B, B2 }, // "B2", Mobilita:Occupato
			{ A212, B1, B, B3 } // "B3", Occupato a rischio disoccupazione
	};

	private String[] deDescrizioni = { "Altro", "Disoccupato", "Inoccupato", "Occupato",
			"Occupato in cerca di altra occupazione", "Precario con att. lav. che non sospende lo stato di disoc.",
			"Persona con attività formativa o lavorativa senza contratto", "In sospensione d'anzianità",
			"Inoccupato con attività lavorativa o formativa senza contratto", "Segnalato dalle agenzie di mediazione",
			"Proveniente dal flusso scolastico", "Segnalato dalle imprese di lavoro temporaneo",
			"Cessato (non rientrato)", "Decaduto dallo stato di disoccupazione",
			"Decaduto per mancata disponibilità al lavoro ex. D.L. 181",
			"Decaduto per mancata presentazione al colloquio orien.", "Decaduto per mancata adesione proposta congrua",
			"Sospeso per contrazione d'attività", "Decaduto per rifiuto politica attiva", "Codice inesistente",
			"In mobilità", "Occupato a rischio disoccupazione" };

	private String[] deDescrizioniRagg = { "Occupato", "Disoccupato", "Inoccupato", "Altro" };

	/**
	 * Limite reddito su cui calcolare lo stato occupazionale. Viene aggiornato al limite superiore (es. se arriva un
	 * movimento dovuto ad un lavoro autonomo a cui corrisponse un limite superiore). P.S.: il calcolo deve tenere conto
	 * del caso peggiore (limite piu' alto).
	 */
	private double limiteReddito;
	/**
	 * codifica interna dello stato occupazionale
	 */
	private int statoOccupazionale = C;
	/**
	 * codifica interna dello stato occupazionale di raggruppamento
	 */
	private int statoOccupazionaleDiRaggruppamento = RAGG_A;

	private int giorniDiLavoro = 0;
	private double reddito;
	private double redditoAnnoSuccessivo = 0;
	private boolean changed = false;
	String msg = null;
	/**
	 * Codice numerico dello stato in cui si trova l'oggetto. 0: nessun errore | -1: errore | 1: movimento di
	 * dimissioni: <b>Per ora non e' usato.</b>
	 */
	private int err;
	private int statoOccupazionaleIniziale = STATO_OCC_INIZIALE_DEFAULT;
	private List movimenti;
	private List statiOccupazionali;
	private DidBean did;
	private List patti;
	private StatoOccupazionaleBean statoOccupazionaleBack;
	private StatoOccupazionaleBean statoOccupazionaleNext;

	/**
	 * Metodi di supporto della classe
	 * 
	 * @param row
	 * @throws Exception
	 */
	private BigDecimal toBigDecimal(Object obj) {
		if (obj instanceof BigDecimal)
			return (BigDecimal) obj;
		else
			return new BigDecimal((String) obj);
	}

	/**
	 * costruttori della classe
	 * 
	 * @param sb
	 * @throws Exception
	 */

	public StatoOccupazionaleBean() throws SourceBeanException {
		super();
	}

	public StatoOccupazionaleBean(double limiteReddito) throws SourceBeanException {
		super();
		this.limiteReddito = limiteReddito;
	}

	public StatoOccupazionaleBean(SourceBean row) throws Exception {
		super(row);
		if (row == null)
			throw new NullPointerException();
		String PREFIX = row.containsAttribute("ROW") ? "ROW." : "";
		if (row.getAttribute(PREFIX + "codStatoOccupaz") != null)
			setStatoOccupazionale((String) row.getAttribute(PREFIX + "codStatoOccupaz"));
	}

	public StatoOccupazionaleBean(SourceBean newSO, SourceBean newDid, List newPatti, List statiOccs) throws Exception {
		super(newSO);
		if (newSO == null)
			throw new NullPointerException();
		String PREFIX = newSO.containsAttribute("ROW") ? "ROW." : "";
		setStatoOccupazionale((String) newSO.getAttribute(PREFIX + "codStatoOccupaz"));
		movimenti = new ArrayList();
		if (newDid != null)
			did = new DidBean(newDid);
		if (newPatti != null)
			patti = newPatti;
		this.statiOccupazionali = statiOccs;
	}

	public StatoOccupazionaleBean(StatoOccupazionaleBean newSO, StatoOccupazionaleBean sOccPrec) throws Exception {
		super(newSO);
		this.statoOccupazionale = newSO.getStatoOccupazionaleInizialeC();
		setStatoOccupazionale(newSO.getStatoOccupaz());
		setStatoOccupazionaleBack(sOccPrec);
	}

	public boolean isProtocollato() {
		String codStatoAtto = this.containsAttribute(StatoOccupazionaleBean.DB_COD_STATO_ATTO)
				? (String) this.getAttribute(StatoOccupazionaleBean.DB_COD_STATO_ATTO)
				: "";
		if (codStatoAtto.toUpperCase().equals("PR"))
			return true;
		else
			return false;
	}

	public boolean isDataFutura() throws Exception {
		String dataInizio = getDataInizio();
		String oggi = DateUtils.getNow();
		int resultCompare = DateUtils.compare(dataInizio, oggi);
		if (resultCompare > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isPrecNormativa(String dataRifNormativa) throws Exception {
		String dataInizio = getDataInizio();
		int resultCompare = DateUtils.compare(dataInizio, dataRifNormativa);
		if (resultCompare < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Restituisce il codice interno in formato numerico associato al codice stato occupazionale presente nella tabella
	 * de_stato_occupaz
	 */
	public static int encode(String cod) throws Exception {
		int i = 0;
		for (; i < deStatoOccupaz.length; i++) {
			if (deStatoOccupaz[i].equalsIgnoreCase(cod)) {
				break;
			}
		}
		if (i >= deStatoOccupaz.length) {
			throw new Exception("stato occupazionale inesistente: " + cod);
		}
		return i;
	}

	/**
	 * restituisce il codice in formato numerico dello stato occupazionale di raggruppamento associato al codice della
	 * tabella de_stato_occupaz
	 */
	public static int encodeRagg(String r) throws Exception {
		int i = 0;
		for (; i < mapStatoOccRagg.length; i++) {
			if (mapStatoOccRagg[i].equals(r)) {
				break;
			}
		}
		if (i >= mapStatoOccRagg.length) {
			throw new Exception("stato occupazionale di raggruppamento inesistente");
		}
		return i;
	}

	/**
	 * restituisce il codice dello stato occupazionale della tabella de_stato_occupaz associato al codice interno in
	 * formato numerico
	 */
	public static String decode(int codificaInterna) {
		return deStatoOccupaz[codificaInterna];
	}

	/**
	 * restituisce il codice presente in de_stato_occupaz associato alla sua codifica interna
	 */
	public static String decodeRagg(int r) throws Exception {
		return mapStatoOccRagg[r];
	}

	public void setStatoOccupazionale(int codStatoOcc) {
		this.statoOccupazionaleIniziale = this.statoOccupazionale;
		this.statoOccupazionale = codStatoOcc;
		setStatoOccupazionaleRagg(deStatoOccRagg[codStatoOcc]);
	}

	public void setStatoOccupazionale(String codStatoOcc) throws Exception {
		this.statoOccupazionaleIniziale = this.statoOccupazionale;
		this.statoOccupazionale = encode(codStatoOcc);
		if (codStatoOcc != null) {
			setStatoOccupazionaleRagg(deStatoOccRagg[this.statoOccupazionale]);
		}
	}

	public void setStatoOccupazionaleRagg(String newStOccRagg) throws Exception {
		this.statoOccupazionaleDiRaggruppamento = encodeRagg(newStOccRagg);
	}

	public void setStatoOccupazionaleRagg(int newStOccRagg) {
		this.statoOccupazionaleDiRaggruppamento = newStOccRagg;
	}

	public int getStatoOccupazionale() {
		return this.statoOccupazionale;
	}

	public String getStatoOccupaz() {
		return decode(getStatoOccupazionale());
	}

	public int getStatoOccupazionaleRagg() {
		return this.statoOccupazionaleDiRaggruppamento;
	}

	/**
	 * Restituisce il codice della tabella de_stato_occupaz associato al codice interno
	 */
	public String getStatoOccupazRagg() {
		return mapStatoOccRagg[this.statoOccupazionaleDiRaggruppamento];
	}

	public BigDecimal getPrgStatoOccupaz() {
		if (this.containsAttribute("row"))
			return (BigDecimal) this.getAttribute("row.prgstatooccupaz");
		else
			return (BigDecimal) this.getAttribute("prgstatooccupaz");
	}

	public void addMovimento(MovimentoBean m) {
		movimenti.add(m);
	}

	public String toString() {
		String s = "StatoOccupazionaleBean ";
		s += " prgStOcc=" + getPrgStatoOccupaz() + ", codStOcc=" + this.getAttribute("codStatoOccupaz") + ", datInizio="
				+ getDataInizio() + ", datFine=" + (getDataFine() == null ? "" : getDataFine());
		return s;
	}

	public void setStatoOccupazionaleBack(StatoOccupazionaleBean statoOccupazionaleBack) {
		this.statoOccupazionaleBack = statoOccupazionaleBack;
	}

	public void setStatoOccupazionaleNext(StatoOccupazionaleBean statoOccupazionaleNext) {
		this.statoOccupazionaleNext = statoOccupazionaleNext;
	}

	public StatoOccupazionaleBean getStatoOccupazionaleNext() {
		return this.statoOccupazionaleNext;
	}

	public StatoOccupazionaleBean getStatoOccupazionaleBack() {
		return statoOccupazionaleBack;
	}

	public DidBean getDIDCollegata() {
		return this.did;
	}

	public boolean collegatoAllaDid() {
		return this.did != null;
	}

	public List getPatti() {
		return patti;
	}

	public void setPatti(List o) {
		patti = o;
	}

	public void setErr(int code) {
		this.err = code;
	}

	public int getErr() {
		return this.err;
	}

	public double getReddito() {
		return this.reddito;
	}

	public double getRedditoAnnoSuccessivo() {
		return this.redditoAnnoSuccessivo;
	}

	public void setLimiteReddito(double limiteReddito) {
		this.limiteReddito = limiteReddito;
	}

	public double getLimiteReddito() {
		return this.limiteReddito;
	}

	/**
	 * 
	 * @return true se lo stato occupazionale e' cambiato, false altrimenti
	 */
	public boolean ischanged() {
		return this.changed;
	}

	public void setChanged(boolean b) {
		this.changed = b;
	}

	/**
	 * E' lo stato occupazionale in cui si trovava l'oggetto prima di settare il nuovo stato.
	 */
	public String getStatoOccupazionaleIniziale() {
		if (this.statoOccupazionaleIniziale >= 0) {
			return decode(this.statoOccupazionaleIniziale);
		} else {
			return "";
		}
	}

	/**
	 * Ritorna lo stato occupazionale, in formato numerico interno, precedente all'impostazione del nuovo stato
	 */
	public int getStatoOccupazionaleInizialeC() {
		return this.statoOccupazionaleIniziale;
	}

	/**
	 * Calcola ("aggiorna") il reddito del lavoratore
	 */
	public void aggiorna(int ggLavorati, double retribuzione) {
		reddito += ((retribuzione * ggLavorati) / 30);
		this.giorniDiLavoro += ggLavorati;
	}

	/**
	 * Calcola ("aggiorna") il reddito dell' anno successivo del lavoratore
	 */
	public void aggiornaAnnoSuccessivo(int ggLavorati, double retribuzione) {
		redditoAnnoSuccessivo += ((retribuzione * ggLavorati) / 30);
	}

	public Object clone() {
		Object obj = null;
		try {
			obj = new StatoOccupazionaleBean(this, getStatoOccupazionaleBack());
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "impossibile clonare StatoOccupazionaleBean", e);

		}
		return obj;
	}

	public boolean equals(Object obj) {
		boolean ret = false;
		if ((obj != null) && obj instanceof StatoOccupazionaleBean) {
			StatoOccupazionaleBean so = (StatoOccupazionaleBean) obj;
			ret = so.getStatoOccupazionale() == getStatoOccupazionale();
		}
		return ret;
	}

	public boolean compare(StatoOccupazionaleBean so) {
		try {
			boolean a = getProgressivoDB().equals(so.getProgressivoDB());
			if (!a) {
				_logger.debug("compare: progressivi diversi:" + getProgressivoDB().toString() + " - "
						+ so.getProgressivoDB().toString());

			}

			boolean b = getCdnLavoratore().equals(so.getCdnLavoratore());
			if (!b) {
				_logger.debug("compare: cdnLavoratore diversi");

			}

			boolean c = getStatoOccupazionale() == so.getStatoOccupazionale();
			if (!c) {
				_logger.debug("compare: stati occupazionali diversi:" + getStatoOccupazionale() + " - "
						+ so.getStatoOccupazionale());

			}

			boolean d = ((getDataInizio() == so.getDataInizio()) || ((getDataInizio() != null)
					&& (so.getDataInizio() != null) && getDataInizio().equals(so.getDataInizio())));
			if (!d) {
				_logger.debug("compare: data inizio diverse:" + getDataInizio() + " - " + so.getDataInizio());

			}

			boolean e = ((getDataFine() == so.getDataFine()) || ((getDataFine() != null) && (so.getDataFine() != null)
					&& getDataFine().equals(so.getDataFine())));
			if (!e) {
				_logger.debug("compare: data fine diverse:" + getDataFine() + " - " + so.getDataFine());

			}

			boolean f = ((getDataAnzianita() == so.getDataAnzianita()) || ((getDataAnzianita() != null)
					&& (so.getDataAnzianita() != null) && getDataAnzianita().equals(so.getDataAnzianita())));
			if (!f) {
				_logger.debug("compare: data anzianita' diverse:" + getDataAnzianita() + " - " + so.getDataAnzianita());

			}

			boolean g = ((getDataRichiestaRevisione() == so.getDataRichiestaRevisione())
					|| ((getDataRichiestaRevisione() != null) && (so.getDataRichiestaRevisione() != null)
							&& getDataRichiestaRevisione().equals(so.getDataRichiestaRevisione())));
			if (!g) {
				_logger.debug("compare: data richiesta revisione diverse:" + getDataRichiestaRevisione() + " - "
						+ so.getDataRichiestaRevisione());

			}

			boolean h = ((getDataRicorsoGiurisdizionale() == so.getDataRicorsoGiurisdizionale())
					|| ((getDataRicorsoGiurisdizionale() != null) && (so.getDataRicorsoGiurisdizionale() != null)
							&& getDataRicorsoGiurisdizionale().equals(so.getDataRicorsoGiurisdizionale())));
			if (!h) {
				_logger.debug("compare: data ricorso giurisdizionale diverse:" + getDataRicorsoGiurisdizionale() + " - "
						+ so.getDataRicorsoGiurisdizionale());

			}

			return a && b && c && d && f && g && h;
		} catch (NullPointerException npe) {
			return false;
		}
	}

	public static void setStatoOccupazionaleAssociatoAlMotivoFineAtto(SourceBean request, String codLstTab,
			String dataRif) throws Exception {
		Object cdnLav = request.getAttribute("CDNLAVORATORE");
		String flgPensionato = "";
		StatoOccupazionaleBean soDaChiudere = null;
		if (cdnLav != null) {
			soDaChiudere = DBLoad.getStatoOccupazionale(cdnLav);
			if ((soDaChiudere != null) && (soDaChiudere.getPensionato() != null))
				flgPensionato = soDaChiudere.getPensionato();
			if (flgPensionato != null)
				request.setAttribute("FLGPENSIONATO", flgPensionato);
		}
		String codMotivoFineAtto = (String) request.getAttribute("codMotivoFineAtto");
		SourceBean row = DBLoad.getStatoOccAssociatoAlMotivoFineAtto(codMotivoFineAtto, codLstTab, dataRif);
		if (row.containsAttribute("codStatoOccupaz")) {
			String codStatoOccupaz = (String) row.getAttribute("codStatoOccupaz");
			String flgImpattiAmm = (String) row.getAttribute("FLGIMPATTIAMM");
			if (flgImpattiAmm != null) {
				String noteMotivoFineAtto = (String) row.getAttribute("strDescrizione");
				String noteStatoOccupaz = "Chiusura dichiarazione di immediata disponibilità per il seguente motivo: "
						+ noteMotivoFineAtto;
				request.setAttribute("strNoteStatoOccupaz", noteStatoOccupaz);
				request.updAttribute("codStatoOccupaz", codStatoOccupaz);
			}
		}
	}

	public static String getStatoOccupazionaleAssociatoAlMotivoDineAtto(String codMotivoFineAtto, String dataRif)
			throws Exception {
		String codStatoOccupaz = null;
		String codLstTab = "AM_DIC_D";
		SourceBean row = DBLoad.getStatoOccAssociatoAlMotivoFineAtto(codMotivoFineAtto, codLstTab, dataRif);
		if (row.containsAttribute("codStatoOccupaz")) {
			codStatoOccupaz = (String) row.getAttribute("codStatoOccupaz");
			String flgImpattiAmm = (String) row.getAttribute("FLGIMPATTIAMM");
			if (flgImpattiAmm != null) {
				String noteMotivoFineAtto = (String) row.getAttribute("strDescrizione");
				String noteStatoOccupaz = "Chiusura dichiarazione di immediata disponibilità per il seguente motivo: "
						+ noteMotivoFineAtto;
			}
		}
		return codStatoOccupaz;
	}

	/**
	 * imposta il nuovo stato occupazionale in base a quello di partenza (presente nell'istanza ) ed alla situazione in
	 * cui si e' verificato
	 * 
	 * @param caso
	 *            puo' valere REDDITO_BASSO | REDDITO_ALTO_CAT_SPECIALE | REDDITO_ALTO
	 */
	public void calcolaNuovoStato(int caso) {
		int nuovoStato = deCambioStato[this.statoOccupazionale][caso];
		if (nuovoStato != this.statoOccupazionale) {
			setStatoOccupazionale(nuovoStato);
			setChanged(true);
		}
	}

	public static StatoOccupazionaleBean creaStatoOccupazionaleIniziale(SourceBean request) throws Exception {
		String PREFIX = "";
		if (request.containsAttribute("ROW"))
			PREFIX = "ROW.";
		request.updAttribute(PREFIX + "codStatoOccupaz", StatoOccupazionaleBean.decode(StatoOccupazionaleBean.C));
		request.updAttribute(PREFIX + "codStatoOccupazRagg", StatoOccupazionaleBean.COD_RAGG_A);
		StatoOccupazionaleBean so = new StatoOccupazionaleBean(request);
		return so;
	}

	public void setRedditoAlto() {
		this.reddito = Double.MAX_VALUE;
		this.redditoAnnoSuccessivo = Double.MAX_VALUE;
		setLimiteReddito(0);
	}

	public String msgNuovoStato() {
		if (msg != null)
			return msg;
		try {
			String vecchio = null;
			if (this.statoOccupazionaleIniziale >= 0) {
				vecchio = getDescrizione(this.statoOccupazionaleIniziale);
				// String vecchioRagg = getDescrizioneRagg(deStatoOccRagg[this.statoOccupazionaleIniziale]);
				// vecchio = vecchioRagg + ":" + vecchio;
			} else
				vecchio = "non presente";
			String nuovo = getDescrizione(this.statoOccupazionale);
			// String nuovoRagg = getDescrizioneRagg(this.statoOccupazionaleDiRaggruppamento);
			// nuovo = nuovoRagg + ":" + nuovo;
			msg = vecchio + " -> " + nuovo;
		} catch (Exception e) {
			msg = "Errore nella determinazione dello stato occupazionale";
		}
		return msg;
	}

	public String msgNuovoStato(SourceBean soccApertoPrec) {
		String prec = null;
		int stoccPrec = -1;
		try {
			if (soccApertoPrec != null && soccApertoPrec.getAttribute("codStatoOccupaz") != null)
				stoccPrec = StatoOccupazionaleBean.encode((String) soccApertoPrec.getAttribute("codStatoOccupaz"));
			if (stoccPrec >= 0) {
				prec = getDescrizione(stoccPrec);
				// String vecchioRagg = getDescrizioneRagg(deStatoOccRagg[stoccPrec]);
				// prec = vecchioRagg + ":" + prec;
			} else
				prec = "non presente";
			String nuovo = getDescrizione(this.statoOccupazionale);
			// String nuovoRagg = getDescrizioneRagg(this.statoOccupazionaleDiRaggruppamento);
			// nuovo = nuovoRagg + ":" + nuovo;
			msg = prec + " -> " + nuovo;
		} catch (Exception e) {
			msg = "Errore nella determinazione dello stato occupazionale";
		}
		return msg;
	}

	public String msgNuovoStato(StatoOccupazionaleBean soccApertoPrec) {
		String prec = null;
		int stoccPrec = -1;
		try {
			if (soccApertoPrec != null && soccApertoPrec.getStatoOccupazionale() != -1)
				stoccPrec = soccApertoPrec.getStatoOccupazionale();
			if (stoccPrec >= 0) {
				prec = getDescrizione(stoccPrec);
				// String vecchioRagg = getDescrizioneRagg(deStatoOccRagg[stoccPrec]);
				// prec = vecchioRagg + ":" + prec;
			} else
				prec = "non presente";
			String nuovo = getDescrizione(this.statoOccupazionale);
			// String nuovoRagg = getDescrizioneRagg(this.statoOccupazionaleDiRaggruppamento);
			// nuovo = nuovoRagg + ":" + nuovo;
			msg = prec + " -> " + nuovo;
		} catch (Exception e) {
			msg = "Errore nella determinazione dello stato occupazionale";
		}
		return msg;
	}

	public void setMsgNuovoStato(String m) {
		this.msg = m;
	}

	public String getDescrizione(int sOcc) throws Exception {
		return deDescrizioni[sOcc];
	}

	public String getDescrizioneRagg(int sOccRagg) throws Exception {
		return deDescrizioniRagg[sOccRagg];
	}

	public String getDescrizioneCompleta() throws Exception {
		String nuovo = getDescrizione(this.statoOccupazionale);
		// String nuovoRagg = getDescrizioneRagg(this.statoOccupazionaleDiRaggruppamento);
		// nuovo = nuovoRagg + ":" + nuovo;
		return nuovo;
	}

	public static StatoOccupazionaleBean creaStatoOccupazionaleBeanIniziale(String dataRif, Object cdnLavoratore,
			StatoOccupazionaleBean stAperto, List dids, List rowsMobilita, List movimenti,
			it.eng.afExt.utils.TransactionQueryExecutor txExecutor) throws Exception {
		SourceBean did = null;
		SourceBean mobilita = null;
		String dataInizioSo = null;
		UtilsConfig utility = new UtilsConfig("AM_297");
		String data297 = utility.getValoreConfigurazione();
		if (data297.equals("")) {
			data297 = EventoAmministrativo.DATA_NORMATIVA_DEFAULT;
		}

		if (DateUtils.compare(dataRif, data297) <= 0) {
			dataRif = data297;
			dataInizioSo = dataRif;
		} else {
			// Viene creato il nuovo stato occupazionale a partire dal giorno
			// precedente
			dataRif = DateUtils.giornoPrecedente(dataRif);
			dataInizioSo = dataRif;
		}
		boolean mobilitaPresente = false;
		boolean didPresente = false;
		boolean didValidaGiornoChiusura = false;

		SourceBean sbGenerale = DBLoad.getInfoGenerali();
		String dat150 = sbGenerale.getAttribute("DAT150") != null ? sbGenerale.getAttribute("DAT150").toString() : null;
		boolean gestioneDecreto150 = (dat150 != null && !dat150.equals("") && DateUtils.compare(dataRif, dat150) >= 0);
		boolean iscrittoCM = false;
		Vector vettCM = DBLoad.getAllDisabiliCollocamentoMirato(cdnLavoratore, txExecutor);
		if (vettCM != null && vettCM.size() > 0) {
			iscrittoCM = Controlli.inCollocamentoMiratoAllaData(vettCM, dataRif);
		}

		for (int i = 0; i < rowsMobilita.size(); i++) {
			Object o = rowsMobilita.get(i);
			mobilita = (SourceBean) o;
			String dataInizioMob = (String) mobilita.getAttribute(MobilitaBean.DB_DAT_INIZIO);
			String dataFineMob = (String) mobilita.getAttribute(MobilitaBean.DB_DAT_FINE);
			if (DateUtils.compare(dataRif, dataInizioMob) >= 0 && (dataFineMob == null || dataFineMob.equals("")
					|| DateUtils.compare(dataRif, dataFineMob) <= 0)) {
				mobilitaPresente = true;
			}
		}
		for (int i = 0; i < dids.size(); i++) {
			Object o = dids.get(i);
			if (o instanceof DidBean)
				did = ((DidBean) o).getSource();
			else
				did = (SourceBean) o;
			didValidaGiornoChiusura = false;
			String dataDichiarazione = (String) did.getAttribute("datDichiarazione");
			String dataFine = (String) did.getAttribute("datFine");
			String motivoFineAtto = "";
			// Se la did è chiusa per avviamento, il giorno di chiusura della
			// did il lavoratore è in 150
			// Se la did non è chiusa per avviamento, il giorno di chiusura
			// della did il lavoratore è decaduto
			if (dataFine != null && !dataFine.equals("")) {
				motivoFineAtto = did.containsAttribute("codMotivoFineAtto")
						? did.getAttribute("codMotivoFineAtto").toString()
						: "";
				if (motivoFineAtto.equalsIgnoreCase("AV")) {
					didValidaGiornoChiusura = (DateUtils.compare(dataRif, dataFine) <= 0);
				} else {
					didValidaGiornoChiusura = (DateUtils.compare(dataRif, dataFine) < 0);
				}
			}
			if (DateUtils.compare(dataRif, dataDichiarazione) >= 0
					&& (dataFine == null || dataFine.equals("") || didValidaGiornoChiusura)) {
				didPresente = true;
			}
		}
		boolean movimentiPresenti = false;
		boolean movimentiPrecedenti = false;
		if (movimenti == null) {
			movimenti = DBLoad.getMovimentiLavoratore(cdnLavoratore, txExecutor);
			movimenti = MovimentoBean.gestisciTuttiPeriodiIntermittenti(movimenti, txExecutor);
		}
		for (int i = 0; i < movimenti.size(); i++) {
			Object o = movimenti.get(i);
			SourceBean m = null;
			if (o instanceof MovimentoBean)
				m = ((MovimentoBean) o).getSource();
			else if (o instanceof DidBean)
				continue;
			else if (o instanceof MobilitaBean || o instanceof ChiusuraMobilitaBean)
				continue;
			else
				m = (SourceBean) o;
			String dataInizioMov = (String) m.getAttribute(MovimentoBean.DB_DATA_INIZIO);
			String dataFineMov = (String) m.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
			if (m.containsAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS)
					&& m.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_ASS).toString().equals("T")) {
				continue;
			}

			if (!m.getAttribute(MovimentoBean.DB_COD_MOVIMENTO).toString().equals("CES")) {
				if (DateUtils.compare(dataRif, dataInizioMov) >= 0 && (dataFineMov == null || dataFineMov.equals("")
						|| DateUtils.compare(dataRif, dataFineMov) <= 0)) {
					movimentiPresenti = true;
				}
				if (DateUtils.compare(dataRif, dataInizioMov) > 0)
					movimentiPrecedenti = true;
			}
		}
		String codStatoOccupaz = null;
		String codStatoOccupazRagg = null;
		if (mobilitaPresente) {
			if (movimentiPresenti) {
				codStatoOccupaz = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.B2);
				codStatoOccupazRagg = "D";
			} else {
				codStatoOccupaz = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.A21);
				codStatoOccupazRagg = "D";
			}
		} else {
			if (movimentiPresenti) {
				if (didPresente) {
					if (gestioneDecreto150 && !iscrittoCM) {
						codStatoOccupaz = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.B1);
					} else {
						codStatoOccupaz = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.A212);
					}
					codStatoOccupazRagg = "D";
				} else {
					codStatoOccupaz = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.B);
					codStatoOccupazRagg = "O";
				}
			} else {
				if (didPresente) {
					if (movimentiPrecedenti) {
						codStatoOccupaz = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.A21);
						codStatoOccupazRagg = "D";

					} else {
						codStatoOccupaz = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.A22);
						codStatoOccupazRagg = "I";
					}
				} else {
					if (movimentiPrecedenti) {
						codStatoOccupaz = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.C0);
						codStatoOccupazRagg = "A";

					} else {
						codStatoOccupaz = StatoOccupazionaleBean.decode(StatoOccupazionaleBean.C);
						codStatoOccupazRagg = "A";
					}
				}
			}
		}
		String dataCalcoloAnzianita = null;
		String dataInizio = null;
		String dataCalcoloMesiSosp = null;
		String dataAnzianita = null;
		String numMesiSosp = null;
		String flgPensionato = null;
		String codMonoCalcoloAnzianitaPrec297 = null;
		String numAnzianitaPrec297 = null;
		if (stAperto == null || stAperto.getDataInizio() == null
				|| stAperto.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_O
				|| stAperto.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_A) {
			// lo stato occupazionale manca del tutto
			// vado a leggere an_lav_storia_inf
			if (didPresente) {
				SourceBean inf = DBLoad
						.getLavStoriaInfAperta(cdnLavoratore instanceof String ? new BigDecimal((String) cdnLavoratore)
								: (BigDecimal) cdnLavoratore);
				dataCalcoloAnzianita = (String) inf.getAttribute("datCalcoloAnzianita");
				dataCalcoloMesiSosp = (String) inf.getAttribute("datCalcoloMesiSosp");
				dataAnzianita = (String) inf.getAttribute("datanzianitadisoc");
				codMonoCalcoloAnzianitaPrec297 = (String) inf.getAttribute("codMonoCalcoloAnzianitaPrec297");
				Object o = inf.getAttribute("numAnzianitaPrec297");
				if (o != null)
					numAnzianitaPrec297 = (String) ((BigDecimal) o).toString();
				o = inf.getAttribute("numMesiSosp");
				if (o != null)
					numMesiSosp = ((BigDecimal) o).toString();
			}
			if (stAperto != null && stAperto.getPensionato() != null)
				flgPensionato = stAperto.getPensionato();
		} else {
			dataCalcoloAnzianita = stAperto.getDataCalcoloAnzianita();
			dataCalcoloMesiSosp = stAperto.getDataCalcoloMesiSosp();
			numMesiSosp = stAperto.getNumMesiSosp();
			dataAnzianita = stAperto.getDataAnzianita();
			codMonoCalcoloAnzianitaPrec297 = stAperto.getCodMonoCalcoloAnzianitaPrec297();
			numAnzianitaPrec297 = stAperto.getNumAnzianitaPrec297();
			flgPensionato = stAperto.getPensionato();
		}
		SourceBean ultimo = null;

		ultimo = new SourceBean("ROW");
		ultimo.setAttribute("datInizio", dataInizioSo);
		ultimo.setAttribute("CDNLAVORATORE", cdnLavoratore);
		ultimo.setAttribute("codStatoOccupaz", codStatoOccupaz);
		ultimo.setAttribute("prgStatoOccupaz", new BigDecimal(-1));
		ultimo.setAttribute("codStatoOccupazRagg", codStatoOccupazRagg);
		if (flgPensionato != null)
			ultimo.setAttribute("flgPensionato", flgPensionato);
		if (dataCalcoloAnzianita != null)
			ultimo.setAttribute("datCalcoloAnzianita", dataCalcoloAnzianita);
		if (dataCalcoloMesiSosp != null)
			ultimo.setAttribute("datCalcoloMesiSosp", dataCalcoloMesiSosp);
		if (dataAnzianita != null)
			ultimo.setAttribute("datanzianitadisoc", dataAnzianita);
		if (codMonoCalcoloAnzianitaPrec297 != null)
			ultimo.setAttribute("codMonoCalcoloAnzianitaPrec297", codMonoCalcoloAnzianitaPrec297);
		if (numAnzianitaPrec297 != null)
			ultimo.setAttribute("numAnzianitaPrec297", numAnzianitaPrec297);
		if (numMesiSosp != null)
			ultimo.setAttribute("numMesiSosp", numMesiSosp);
		StatoOccupazionaleBean statoOccCorrente = new StatoOccupazionaleBean(ultimo, null, null, null);
		statoOccCorrente.setCodMonoProvenienza("M");
		return statoOccCorrente;
	}

	/**
	 * @param cdnLavoratore
	 * @param transactionQueryExecutor
	 * @return
	 */
	public static List getStatiOccupazionali(Object cdnLavoratore, TransactionQueryExecutor transactionQueryExecutor)
			throws Exception {

		SourceBean row = null;
		Object params[] = new Object[2];
		params[0] = cdnLavoratore;
		params[1] = "01/01/1900";

		row = (SourceBean) transactionQueryExecutor.executeQuery("GET_STATI_OCCUPAZIONALI", params, "SELECT");

		if (row == null)
			throw new Exception("impossibile estrarre gli stati occupazionali");
		return (new ArrayList(row.getAttributeAsVector("ROW")));

	}

	public static void normalizzaStatiOccupazionali(Vector statiOccupazionali, Vector listStOccDaCancellare,
			Vector statiOccupazionaliFinali) {
		SourceBean sb = null;
		SourceBean sbCancella = null;
		BigDecimal prg = null;
		boolean trovato = false;
		BigDecimal prgCancella = null;
		for (int i = 0; i < statiOccupazionali.size(); i++) {
			trovato = false;
			sb = (SourceBean) statiOccupazionali.get(i);
			prg = (BigDecimal) sb.getAttribute("prgStatoOccupaz");
			for (int j = 0; j < listStOccDaCancellare.size(); j++) {
				sbCancella = (SourceBean) listStOccDaCancellare.get(j);
				prgCancella = (BigDecimal) sbCancella.getAttribute("prgStatoOccupaz");
				if (prg.equals(prgCancella)) {
					trovato = true;
					break;
				}
			}
			if (!trovato) {
				statiOccupazionaliFinali.add(sb);
			}
		}
	}

	public static void addWarning(int code, RequestContainer requestContainer) throws Exception {
		ArrayList warnings = null;
		if (requestContainer.getServiceRequest()
				.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE))
			warnings = (ArrayList) requestContainer.getServiceRequest()
					.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
		else {
			warnings = new ArrayList();
			requestContainer.getServiceRequest().setAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE,
					warnings);
		}
		boolean esisteWarning = false;
		Warning objWarning = null;
		for (int i = 0; i < warnings.size(); i++) {
			Object objList = warnings.get(i);
			if (objList instanceof Warning) {
				objWarning = (Warning) objList;
				if (objWarning != null && objWarning.getCode() == code) {
					esisteWarning = true;
					break;
				}
			}

		}
		if (!esisteWarning) {
			warnings.add(new Warning(code, ""));
		}
	}

}