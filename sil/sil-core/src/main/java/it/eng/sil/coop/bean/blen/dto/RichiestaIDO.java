package it.eng.sil.coop.bean.blen.dto;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.bean.blen.constant.ServiziConstant;
import it.eng.sil.coop.bean.blen.input.ricerca.AltreInformazioni;
import it.eng.sil.coop.bean.blen.input.ricerca.CondizioniOfferte;
import it.eng.sil.coop.bean.blen.input.ricerca.DurataRichiesta;
import it.eng.sil.coop.bean.blen.input.ricerca.IstruzioneFormazione;
import it.eng.sil.coop.bean.blen.input.ricerca.Lingua;
import it.eng.sil.coop.bean.blen.input.ricerca.ProfiloRichiesto;
import it.eng.sil.coop.bean.blen.input.ricerca.Richiesta;
import it.eng.sil.coop.bean.blen.input.ricerca.SiNo;
import it.eng.sil.coop.bean.blen.input.ricerca.Titolostudio;
import it.eng.sil.coop.utils.UtilityCodifiche;

public class RichiestaIDO {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RichiestaIDO.class.getName());

	private ProfiloRichiesto profilo;
	private IstruzioneFormazione formazione;
	private CondizioniOfferte condizioneOfferte;
	private DurataRichiesta durataRich;
	private DatiAzienda azienda;
	private AltreInformazioni altreInformazioni;

	public RichiestaIDO(Richiesta richiesta, AltreInformazioni altreInformazioni, DatiAzienda aziendaR) {
		setProfiloRichiesto(richiesta.getProfiloRichiesto());
		setIstruzioneRichiesta(richiesta.getIstruzioneFormazione());
		setCondizioniOfferte(richiesta.getCondizioniOfferte());
		setDurataRichiesta(richiesta.getDurataRichiesta());
		setDatiAzienda(aziendaR);
		setAltreInformazioni(altreInformazioni);
	}

	public BigDecimal insertRichiesta(String strDataRichiesta, BigDecimal prgAz, BigDecimal prgUnAz,
			TransactionQueryExecutor transExec) throws EMFInternalError {
		BigDecimal newPrgRichiestaAz = insertDoRIchiesta(strDataRichiesta, prgAz, prgUnAz, transExec);

		BigDecimal prgAlternativa = new BigDecimal(1);

		updateDoAlternativa(newPrgRichiestaAz, prgAlternativa, transExec);

		updateDoEvasione(newPrgRichiestaAz, transExec);

		insertDoMansione(newPrgRichiestaAz, prgAlternativa, transExec);

		insertDoOrario(newPrgRichiestaAz, transExec);

		if (getIstruzioneRichiesta().getTitolostudio() != null
				&& getIstruzioneRichiesta().getTitolostudio().size() > 0) {
			for (Titolostudio titolo : getIstruzioneRichiesta().getTitolostudio()) {
				insertDoStudio(newPrgRichiestaAz, prgAlternativa, titolo, transExec);
			}
		}

		if (getIstruzioneRichiesta().getLingua() != null && getIstruzioneRichiesta().getLingua().size() > 0) {
			for (Lingua lingua : getIstruzioneRichiesta().getLingua()) {
				insertDoLingua(newPrgRichiestaAz, prgAlternativa, lingua, transExec);
			}
		}

		String codAbilitazioneAlbo = getIstruzioneRichiesta().getIdalbo();
		if (codAbilitazioneAlbo != null && !codAbilitazioneAlbo.equals("")) {
			insertDoAbilitazione(newPrgRichiestaAz, codAbilitazioneAlbo, transExec);
		}

		if (getIstruzioneRichiesta().getIdpatenteguida() != null
				&& getIstruzioneRichiesta().getIdpatenteguida().size() > 0) {
			for (String patenteGuida : getIstruzioneRichiesta().getIdpatenteguida()) {
				insertDoAbilitazione(newPrgRichiestaAz, patenteGuida, transExec);
			}
		}

		if (getIstruzioneRichiesta().getIdpatentino() != null && getIstruzioneRichiesta().getIdpatentino().size() > 0) {
			for (String patentino : getIstruzioneRichiesta().getIdpatentino()) {
				insertDoAbilitazione(newPrgRichiestaAz, patentino, transExec);
			}
		}

		insertDoComune(newPrgRichiestaAz, transExec);

		insertDoContratto(newPrgRichiestaAz, prgAlternativa, transExec);

		insertDoInfo(transExec, newPrgRichiestaAz, prgAlternativa,
				getIstruzioneRichiesta().getConoscenzeinformatiche());

		return newPrgRichiestaAz;
	}

	private void insertDoInfo(TransactionQueryExecutor transExec, BigDecimal prgRichiestaAz, BigDecimal prgAlternativa,
			String conoscenzeInformatiche) throws EMFInternalError {

		if (conoscenzeInformatiche == null) {
			return;
		}

		String[] conoscenzaInformatica = UtilityCodifiche.getConoscenzaInformatica(transExec, conoscenzeInformatiche);

		if (conoscenzaInformatica != null) {

			String codTipoInfo = conoscenzaInformatica[0];
			String codDettInfo = conoscenzaInformatica[1];

			Object[] inputParameters = new Object[8];

			inputParameters[0] = prgRichiestaAz;// PRGRICHIESTAAZ
			inputParameters[1] = prgAlternativa; // PRGALTERNATIVA
			inputParameters[2] = codTipoInfo; // CODTIPOINFO
			inputParameters[3] = codDettInfo;// CODICE
			inputParameters[4] = ServiziConstant.CONOSCENZA_INFORMATICA_CODIFICA_INESISTENTE;// CDNGRADO
			inputParameters[5] = null;// FLGINDISPENSABILE
			inputParameters[6] = ServiziConstant.UTENTE_BLEN;// CDNUTINS
			inputParameters[7] = ServiziConstant.UTENTE_BLEN;// CDNUTMOD

			executeInsert(transExec, "InsertInfoRichiesta", inputParameters);
		}

	}

	private BigDecimal insertDoRIchiesta(String strDataRichiesta, BigDecimal prgAz, BigDecimal prgUnAz,
			TransactionQueryExecutor transExec) throws EMFInternalError {
		BigDecimal newPrgRichiestaAz;
		int annoRichiesta;
		try {
			annoRichiesta = DateUtils.getAnno(strDataRichiesta);
		} catch (Exception ex) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"Errore nell'estrazione dell'anno richiesta: " + ex.getLocalizedMessage());
		}
		Object[] inputParameters = new Object[49];
		newPrgRichiestaAz = getNewPrgRichiestaAz(transExec);
		inputParameters[0] = newPrgRichiestaAz; // "prgRichiestaAz"
		inputParameters[1] = prgAz; // "prgAzienda"
		inputParameters[2] = prgUnAz; // "prgUnita"
		inputParameters[3] = UtilityCodifiche.getCPIFromComune(transExec,
				getDatiAzienda().getDatiContatto().getIdcomune()); // "codCpi associato al comune del datore di lavoro"
		inputParameters[4] = annoRichiesta; // "numAnno"
		inputParameters[5] = getNumeroRichiestaAnno(transExec, annoRichiesta); // "numRichiesta"
		inputParameters[6] = new BigDecimal(0); // "numStorico"
		inputParameters[7] = strDataRichiesta; // "datRichiesta"
		inputParameters[8] = null; // "flgArt16"
		inputParameters[9] = getDataScadenza(getDurataRichiesta().getDatascadenza()); // "datScadenza"
		inputParameters[10] = getProfiloRichiesto().getNumerolavoratori(); // "numProfRichiesti"
		inputParameters[11] = null; // "strLocalita"
		inputParameters[12] = null; // "prgSpi"
		inputParameters[13] = null; // "strCognomeRiferimento"
		inputParameters[14] = null; // "strNomeRiferimento"
		inputParameters[15] = null; // "strTelRiferimento"
		inputParameters[16] = null; // "strFaxRiferimento"
		inputParameters[17] = getDatiAzienda().getDatiContatto().getEmail(); // "strEmailRiferimento"
		inputParameters[18] = null; // "flgAutomunito"
		inputParameters[19] = null; // "flgMilite"
		inputParameters[20] = null; // "codTrasferta"
		inputParameters[21] = null; // "flgFuoriSede"
		inputParameters[22] = null; // "txtNoteOperatore"
		inputParameters[23] = null; // "flgMotomunito"
		inputParameters[24] = null; // "flgVittoAlloggio"
		inputParameters[25] = null; // "flgVitto"
		inputParameters[26] = null; // "flgTurismo"
		inputParameters[27] = null; // "strSesso"
		inputParameters[28] = null; // "codMotGenere"
		inputParameters[29] = null; // "strMotivSesso"
		inputParameters[30] = null; // "codArea"
		inputParameters[31] = ServiziConstant.UTENTE_BLEN; // "_CDUT_"
		inputParameters[32] = ServiziConstant.UTENTE_BLEN; // "_CDUT_"
		inputParameters[33] = null; // "cdnGruppo"
		inputParameters[34] = null; // "numPostoAS"
		inputParameters[35] = null; // "numPostoLSU"
		inputParameters[36] = null; // "numPostoMilitare"
		inputParameters[37] = null; // "numPostoMB"
		inputParameters[38] = null; // "flgRiusoGraduatoria"
		inputParameters[39] = null; // "datChiamata"
		inputParameters[40] = null; // "tipoLSU"
		inputParameters[41] = null; // "FLGSVANTAGGIATI"
		inputParameters[42] = null; // "STRMOTSVANTAGGIATI"
		inputParameters[43] = null; // "DATVERIFICASVAN"
		inputParameters[44] = null; // "FLGDISNONISCR"
		inputParameters[45] = null; // "STRMOTNONISCR"
		inputParameters[46] = null; // "DATVERIFICADIS"
		inputParameters[47] = getCondizioniOfferte().getIdtipologiacontratto(); // "codrapportolav"
		inputParameters[48] = (getAltreInformazioni().getNO().equals(SiNo.SI) ? "S" : "N"); // "flgNullaOsta"

		executeInsert(transExec, "INSERT_DO_RICHIESTA_AZ_BLEN", inputParameters);

		return newPrgRichiestaAz;
	}

	private BigDecimal getNewPrgRichiestaAz(TransactionQueryExecutor transExec) throws EMFInternalError {
		SourceBean res = (SourceBean) transExec.executeQuery("SELECT_DO_RICHIESTA_AZ_SEQUENCE", null, "SELECT");
		BigDecimal prgRichiestaAz = res.containsAttribute("ROW") ? (BigDecimal) res.getAttribute("ROW.prgRichiestaAz")
				: (BigDecimal) res.getAttribute("prgRichiestaAz");
		return prgRichiestaAz;
	}

	private BigDecimal getNewPrgMansione(TransactionQueryExecutor transExec) throws EMFInternalError {
		SourceBean res = (SourceBean) transExec.executeQuery("SELECT_DO_MANSIONE_SEQUENCE", null, "SELECT");
		BigDecimal prgMansione = res.containsAttribute("ROW") ? (BigDecimal) res.getAttribute("ROW.prgMansione")
				: (BigDecimal) res.getAttribute("prgMansione");
		return prgMansione;
	}

	private BigDecimal getNumeroRichiestaAnno(TransactionQueryExecutor transExec, int anno) throws EMFInternalError {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = anno;
		SourceBean res = (SourceBean) transExec.executeQuery("SELECT_DO_NUMERO_RICHIESTA", inputParameters, "SELECT");
		BigDecimal beanprgNumeroRichiesta = res.containsAttribute("ROW")
				? (BigDecimal) res.getAttribute("ROW.NUMERORICHIESTA")
				: (BigDecimal) res.getAttribute("NUMERORICHIESTA");
		return beanprgNumeroRichiesta;
	}

	private String getDataScadenza(XMLGregorianCalendar dataScadenza) throws EMFInternalError {

		GregorianCalendar maxScadenza = new GregorianCalendar();
		maxScadenza.add(GregorianCalendar.DAY_OF_YEAR, 60);
		
		XMLGregorianCalendar maxScadenzaXMLGC = null;
		try {
			maxScadenzaXMLGC = DatatypeFactory.newInstance().newXMLGregorianCalendar( maxScadenza );
		} catch (DatatypeConfigurationException e) {
			_logger.error("Errore nella determinazione della maxScadenza", e);
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nella determinazione della maxScadenza: " + e.getLocalizedMessage());
		}

		if (dataScadenza == null || dataScadenza.compare(maxScadenzaXMLGC.normalize()) == DatatypeConstants.GREATER) {
			dataScadenza = maxScadenzaXMLGC.normalize();
		}

		String data = dataScadenza.getDay() + "/" + dataScadenza.getMonth() + "/" + dataScadenza.getYear();

		return data;

	}

	private void updateDoAlternativa(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa,
			TransactionQueryExecutor transExec) throws EMFInternalError {
		Object[] inputParameters = null;
		String flgEsperienza = null;
		inputParameters = new Object[11];
		if (getProfiloRichiesto().getEsperienzarichiesta() != null) {
			flgEsperienza = getProfiloRichiesto().getEsperienzarichiesta().name();
			if (flgEsperienza != null && flgEsperienza.length() > 0) {
				flgEsperienza = flgEsperienza.substring(0, 1);
			}
		}
		inputParameters[0] = flgEsperienza; // "flgEsperienza"
		inputParameters[1] = null; // "numAnniEsperienza"
		inputParameters[2] = null; // "numda"
		inputParameters[3] = null; // "numa"
		inputParameters[4] = null; // "codMotEta"
		inputParameters[5] = null; // "strMotiveta"
		inputParameters[6] = null; // "flgFormazioneprof"
		inputParameters[7] = null; // "strNote"
		inputParameters[8] = ServiziConstant.UTENTE_BLEN; // "cdnUtmod"
		inputParameters[9] = prgRichiestaAz; // "prgRichiestaAz"
		inputParameters[10] = prgAlternativa; // "prgAlternativa"

		executeUpdate(transExec, "SAVE_IDO_ETAESPERIENZA", inputParameters);
	}

	private void updateDoEvasione(BigDecimal prgRichiestaAz, TransactionQueryExecutor transExec)
			throws EMFInternalError {
		Object[] inputParameters = null;
		inputParameters = new Object[9];
		inputParameters[0] = ServiziConstant.MO_EVASIONE_SELEZIONE; // "codEvasione"
		inputParameters[1] = ServiziConstant.STATO_EVASIONE_INSERITA; // "cdnStatoRich"
		inputParameters[2] = null; // "STRMOTVOCHIUSURA"
		inputParameters[3] = null; // "DATCHIUSURA"
		inputParameters[4] = ServiziConstant.STATO_RICHIESTA_COMPLETA; // "CODMONOSTATORICH"
		inputParameters[5] = ServiziConstant.UTENTE_BLEN; // "CDNUTMOD"
		inputParameters[6] = null; // "CODMOTIVOCHIUSURARICH"
		inputParameters[7] = null; // "CODESITORICHIESTA"
		inputParameters[8] = prgRichiestaAz; // "prgRichiestaAz"

		executeUpdate(transExec, "IDO_SAVESTATORICHAZ", inputParameters);
	}
	/*
	 * <STATEMENT name="InsertOrarioRichiesta" query="INSERT INTO DO_ORARIO ( PRGORARIO, -CODORARIO, -PRGRICHIESTAAZ,
	 * -CDNUTINS, DTMINS, -CDNUTMOD, DTMMOD, -FLGINVIOCL) VALUES (S_DO_ORARIO.nextval, ?, ?, ?, sysdate, ?, sysdate,
	 * ?)"/>
	 */

	private void insertDoOrario(BigDecimal prgRichiestaAz, TransactionQueryExecutor transExec) throws EMFInternalError {
		Object[] inputParameters = null;
		String idmodalitalavoro = null;
		String codOrario = null;
		idmodalitalavoro = getCondizioniOfferte().getIdmodalitalavoro();
		if (idmodalitalavoro != null && idmodalitalavoro.length() > 0) {
			codOrario = UtilityCodifiche.getCodOrarioSil(transExec, idmodalitalavoro);
			if (codOrario == null || codOrario.length() == 0) {
				_logger.warn("Errore nel recupero del codice orario lavoro a partire da id modalita lavoro");
				return;
			}
			if (codOrario != null && codOrario.length() > 0) {
				inputParameters = new Object[5];
				inputParameters[0] = codOrario; // CODORARIO
				inputParameters[1] = prgRichiestaAz; // PRGRICHIESTAAZ
				inputParameters[2] = ServiziConstant.UTENTE_BLEN; // CDNUTINS
				inputParameters[3] = ServiziConstant.UTENTE_BLEN; // CDNUTMOD
				inputParameters[4] = ServiziConstant.SI; // FLGINVIOCL

				executeInsert(transExec, "InsertOrarioRichiesta", inputParameters);
			}
		}
	}

	private void insertDoMansione(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa,
			TransactionQueryExecutor transExec) throws EMFInternalError {
		Object[] inputParameters = null;
		String codMansione = null;
		codMansione = getProfiloRichiesto().getIdprofessione();
		if (codMansione != null && codMansione.length() > 0) {
			codMansione = UtilityCodifiche.getMansioneSil(transExec, codMansione);
			if (codMansione == null || codMansione.length() == 0) {
				_logger.warn("Errore nel recupero della mansione");
				return;
			}
			if (codMansione != null && codMansione.length() > 0) {
				inputParameters = new Object[9];
				inputParameters[0] = getNewPrgMansione(transExec); // "prgMansione"
				inputParameters[1] = prgRichiestaAz; // "prgRichiestaAz"
				inputParameters[2] = prgAlternativa; // "prgAlternativa"
				inputParameters[3] = codMansione; // "codMansione"
				inputParameters[4] = null; // "flgPubblica"
				inputParameters[5] = null; // "codQualifica"
				inputParameters[6] = ServiziConstant.UTENTE_BLEN; // "cdnUtins"
				inputParameters[7] = ServiziConstant.UTENTE_BLEN; // "cdnUtmod"
				inputParameters[8] = ServiziConstant.SI; // "FLGINVIOCL"

				executeInsert(transExec, "INSERT_IDO_MANSIONE", inputParameters);
			}
		}
	}

	private void insertDoStudio(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa, Titolostudio titolo,
			TransactionQueryExecutor transExec) throws EMFInternalError {
		Object[] inputParameters = null;
		String codTitolo = titolo.getIdtitolostudio();
		if (codTitolo != null && codTitolo.length() > 0) {
			codTitolo = UtilityCodifiche.getTitoloStudioSil(transExec, codTitolo);
			if (codTitolo == null || codTitolo.length() == 0) {
				_logger.warn("Errore nel recupero del titolo studio");
				return;
			}
			if (codTitolo != null && codTitolo.length() > 0) {
				inputParameters = new Object[8];
				inputParameters[0] = prgRichiestaAz; // "prgRichiestaAz"
				inputParameters[1] = prgAlternativa; // "prgAlternativa"
				inputParameters[2] = codTitolo; // "CODTITOLO"
				inputParameters[3] = titolo.getDescrizionestudio(); // "STRSPECIFICA"
				inputParameters[4] = null; // "FLGCONSEGUITO"
				inputParameters[5] = null; // "FLGINDISPENSABILE"
				inputParameters[6] = ServiziConstant.UTENTE_BLEN; // "cdnUtins"
				inputParameters[7] = ServiziConstant.UTENTE_BLEN; // "cdnUtmod"

				executeInsert(transExec, "InsertStudioRichiesta", inputParameters);
			}
		}
	}

	private void insertDoLingua(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa, Lingua lingua,
			TransactionQueryExecutor transExec) throws EMFInternalError {
		Object[] inputParameters = null;
		HashMap<String, BigDecimal> gradoConoscenza = null;
		gradoConoscenza = new HashMap<String, BigDecimal>();

		String codLingua = lingua.getIdlingua();
		String livelloParlato = lingua.getIdlivelloparlato();
		String livelloScritto = lingua.getIdlivelloscritto();
		String livelloLetto = lingua.getIdlivelloletto();

		BigDecimal codiceLivelloParlato = null;
		BigDecimal codiceLivelloScritto = null;
		BigDecimal codiceLivelloLetto = null;

		if (codLingua != null && codLingua.length() > 0) {

			if (livelloParlato != null && livelloParlato.length() > 0) {
				if (gradoConoscenza.containsKey(livelloParlato)) {
					codiceLivelloParlato = gradoConoscenza.get(livelloParlato);
				} else {
					codiceLivelloParlato = UtilityCodifiche.getLivelloConoscenzaLinSil(transExec, livelloParlato);
					if (codiceLivelloParlato != null) {
						gradoConoscenza.put(livelloParlato, codiceLivelloParlato);
					}
				}
			}

			if (livelloScritto != null && livelloScritto.length() > 0) {
				if (gradoConoscenza.containsKey(livelloScritto)) {
					codiceLivelloScritto = gradoConoscenza.get(livelloScritto);
				} else {
					codiceLivelloScritto = UtilityCodifiche.getLivelloConoscenzaLinSil(transExec, livelloScritto);
					if (codiceLivelloScritto != null) {
						gradoConoscenza.put(livelloScritto, codiceLivelloScritto);
					}
				}
			}

			if (livelloLetto != null && livelloLetto.length() > 0) {
				if (gradoConoscenza.containsKey(livelloLetto)) {
					codiceLivelloLetto = gradoConoscenza.get(livelloLetto);
				} else {
					codiceLivelloLetto = UtilityCodifiche.getLivelloConoscenzaLinSil(transExec, livelloLetto);
					if (codiceLivelloLetto != null) {
						gradoConoscenza.put(livelloLetto, codiceLivelloLetto);
					}
				}
			}

			if (codiceLivelloLetto == null || codiceLivelloScritto == null || codiceLivelloParlato == null) {
				_logger.warn("Attenzione: codiceLivelloLetto (" + livelloLetto + ") o codiceLivelloScritto ("
						+ livelloScritto + ") o codiceLivelloParlato (" + livelloParlato
						+ ") non trovato, si scarta la lingua.");
				return;
			}

			inputParameters = new Object[9];
			inputParameters[0] = prgRichiestaAz; // "prgRichiestaAz"
			inputParameters[1] = prgAlternativa; // "prgAlternativa"
			inputParameters[2] = codLingua; // "CODLINGUA"
			inputParameters[3] = codiceLivelloLetto; // "CDNGRADOLETTO"
			inputParameters[4] = codiceLivelloScritto; // "CDNGRADOSCRITTO"
			inputParameters[5] = codiceLivelloParlato; // "CDNGRADOPARLATO"
			inputParameters[6] = null; // "FLGINDISPENSABILE
			inputParameters[7] = ServiziConstant.UTENTE_BLEN; // "cdnUtins"
			inputParameters[8] = ServiziConstant.UTENTE_BLEN; // "cdnUtmod"

			executeInsert(transExec, "InsertLinguaRichiesta", inputParameters);

		}

	}

	private void insertDoAbilitazione(BigDecimal prgRichiestaAz, String codAbilitazione,
			TransactionQueryExecutor transExec) throws EMFInternalError {

		String codAbilitazioneGen = UtilityCodifiche.getAbilitazioneSil(transExec, codAbilitazione);

		if (codAbilitazioneGen == null) {
			// nessun mapping lato SIL per questa abilitazione, non la si inserisce (si scarta)
			_logger.warn("Errore nel recupero del cod abilitazione sil");
			return;
		}

		Object[] inputParameters = null;
		inputParameters = new Object[6];
		inputParameters[0] = codAbilitazioneGen; // "CODABILITAZIONEGEN"
		inputParameters[1] = prgRichiestaAz; // "PRGRICHIESTAAZ"
		inputParameters[2] = null; // "FLGINDISPENSABILE"
		inputParameters[3] = ServiziConstant.UTENTE_BLEN; // "CDNUTINS"
		inputParameters[4] = ServiziConstant.UTENTE_BLEN; // "CDNUTMOD"
		inputParameters[5] = ServiziConstant.SI; // "FLGINVIOCL"

		executeInsert(transExec, "INSERT_ABIL_RICH", inputParameters);

	}

	private void insertDoComune(BigDecimal prgRichiestaAz, TransactionQueryExecutor transExec) throws EMFInternalError {
		String codComune = getCondizioniOfferte().getIdcomune();
		if (codComune != null && codComune.length() > 0) {

			if (isNuovoComune(codComune, prgRichiestaAz, transExec)) {

				// pulisce i flag invio cl degli eventuali comuni precedentemente inseriti

				Object[] inputParameters = null;

				inputParameters = new Object[2];
				inputParameters[0] = ServiziConstant.UTENTE_BLEN; // "cdnUtmod"
				inputParameters[1] = prgRichiestaAz; // "PRGRICHIESTAAZ"

				executeUpdate(transExec, "UPDATE_FLGCL_COMUNE", inputParameters);

				// inserisce il nuovo comune

				inputParameters = null;

				inputParameters = new Object[5];
				inputParameters[0] = getCondizioniOfferte().getIdcomune(); // "CODCOM"
				inputParameters[1] = prgRichiestaAz; // "PRGRICHIESTAAZ"
				inputParameters[2] = ServiziConstant.UTENTE_BLEN; // "cdnUtins"
				inputParameters[3] = ServiziConstant.UTENTE_BLEN; // "cdnUtmod"
				inputParameters[4] = ServiziConstant.SI; // "FLGINVIOCL"

				executeInsert(transExec, "InsertComuneRichiesta", inputParameters);

			}
		}
	}

	private boolean isNuovoComune(String codComune, BigDecimal prgRichiestaAz, TransactionQueryExecutor transExec)
			throws EMFInternalError {
		Object[] inputParameters = new Object[2];
		inputParameters[0] = prgRichiestaAz; // "CODCOM"
		inputParameters[1] = codComune; // "PRGRICHIESTAAZ"
		int num = 0;
		SourceBean res = (SourceBean) transExec.executeQuery("SELECT_COUNT_COM_RICHIESTA", inputParameters, "SELECT");
		if (res != null) {
			BigDecimal numComRichiesta = res.containsAttribute("ROW") ? (BigDecimal) res.getAttribute("ROW.NUM")
					: (BigDecimal) res.getAttribute("NUM");
			num = numComRichiesta.intValue();
		}
		return num == 0;
	}

	private void insertDoContratto(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa,
			TransactionQueryExecutor transExec) throws EMFInternalError {
		Object[] inputParameters = null;
		String codContratto = null;
		String tipoContratto = null;
		codContratto = getCondizioniOfferte().getIdtipologiacontratto();
		if (codContratto != null && codContratto.length() > 0) {
			Vector tipologiaRapporto = UtilityCodifiche.getCodiceContratto(transExec, codContratto);
			int sizeRapporti = tipologiaRapporto.size();
			for (int i = 0; i < sizeRapporti; i++) {
				SourceBean rapp = (SourceBean) tipologiaRapporto.get(i);
				tipoContratto = rapp.containsAttribute("CODCONTRATTO") ? rapp.getAttribute("CODCONTRATTO").toString()
						: "";
				if (!tipoContratto.equals("")) {
					inputParameters = new Object[6];
					inputParameters[0] = tipoContratto; // "CODCONTRATTO"
					inputParameters[1] = prgRichiestaAz; // "prgRichiestaAz"
					inputParameters[2] = prgAlternativa; // "prgAlternativa"
					inputParameters[3] = ServiziConstant.UTENTE_BLEN; // "cdnUtins"
					inputParameters[4] = ServiziConstant.UTENTE_BLEN; // "cdnUtmod"
					inputParameters[5] = ServiziConstant.SI; // "FLGINVIOCL"

					executeInsert(transExec, "InsertContrattiRichiesta", inputParameters);

				}
			}
		}
	}

	public ProfiloRichiesto getProfiloRichiesto() {
		return profilo;
	}

	public void setProfiloRichiesto(ProfiloRichiesto value) {
		this.profilo = value;
	}

	public IstruzioneFormazione getIstruzioneRichiesta() {
		return formazione;
	}

	public void setIstruzioneRichiesta(IstruzioneFormazione value) {
		this.formazione = value;
	}

	public CondizioniOfferte getCondizioniOfferte() {
		return condizioneOfferte;
	}

	public void setCondizioniOfferte(CondizioniOfferte value) {
		this.condizioneOfferte = value;
	}

	public DurataRichiesta getDurataRichiesta() {
		return durataRich;
	}

	public void setDurataRichiesta(DurataRichiesta value) {
		this.durataRich = value;
	}

	public DatiAzienda getDatiAzienda() {
		return azienda;
	}

	public void setDatiAzienda(DatiAzienda value) {
		this.azienda = value;
	}

	public AltreInformazioni getAltreInformazioni() {
		return altreInformazioni;
	}

	public void setAltreInformazioni(AltreInformazioni altreInformazioni) {
		this.altreInformazioni = altreInformazioni;
	}

	private static boolean isInsertFailed(Object objRes) {
		return objRes == null || !(objRes instanceof Boolean && ((Boolean) objRes).booleanValue() == true);
	}

	private static void executeInsert(TransactionQueryExecutor transExec, String statementName,
			Object[] inputParameters) throws EMFInternalError {
		Object objRes = transExec.executeQuery(statementName, inputParameters, "INSERT");

		if (isInsertFailed(objRes)) {
			_logger.error("Inserimento fallito: " + statementName);
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Inserimento fallito: " + statementName);
		}
	}

	private static void executeUpdate(TransactionQueryExecutor transExec, String statementName,
			Object[] inputParameters) throws EMFInternalError {
		Object objRes = transExec.executeQuery(statementName, inputParameters, "UPDATE");

		if (isInsertFailed(objRes)) {
			_logger.error("Aggiornamento fallito: " + statementName);
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Aggiornamento fallito: " + statementName);
		}
	}

}
