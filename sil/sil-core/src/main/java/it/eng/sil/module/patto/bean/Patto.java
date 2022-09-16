/*
 * Created on Oct 14, 2005
 */
package it.eng.sil.module.patto.bean;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

/**
 * @author savino
 * 
 */
public class Patto {


	private SourceBean source;

	public Patto() throws Exception {
		this(null);
	}

	public Patto(SourceBean s) throws Exception {
		this.source = (s == null) ? defaultBean() : s;
	}

	public String getCdnLavoratore() {
		return Utils.notNull(source.getAttribute("CDNLAVORATORE"));
	}

	public String getStrCognome() {
		return Utils.notNull(source.getAttribute("strCognome"));
	}

	public String getStrNome() {
		return Utils.notNull(source.getAttribute("strNome"));
	}

	public String getPrgDichDisponibilita() {
		return Utils.notNull(source.getAttribute("PRGDICHDISPONIBILITA"));
	}

	public String getDatStipula() {
		return Utils.notNull(source.getAttribute("DATSTIPULA"));
	}
	
	public String getDatStipulaOrig() {
		return Utils.notNull(source.getAttribute("DATSTIPULAORIG"));
	}

	public String getCodStatoAtto() {
		return Utils.notNull(source.getAttribute("CODSTATOATTO"));
	}

	public String getPrgStatoOccupaz() {
		return Utils.notNull(source.getAttribute("PRGSTATOOCCUPAZ"));
	}

	public String getFlgComunicazEsiti() {
		return Utils.notNull(source.getAttribute("FLGCOMUNICAZESITI"));
	}

	public String getFlgPatto297() {
		return Utils.notNull(source.getAttribute("FLGPATTO297"));
	}

	public boolean isPatto297() {
		return getFlgPatto297().equals("S");
	}

	public boolean isAccordoGenerico() {
		return getFlgPatto297().equals("N");
	}

	public String getPrgPattoLavoratore() {
		return Utils.notNull(source.getAttribute("PRGPATTOLAVORATORE"));
	}

	public String getDatScadConferma() {
		return Utils.notNull(source.getAttribute("DATSCADCONFERMA"));
	}

	public String getStrNote() {
		return Utils.notNull(source.getAttribute("STRNOTE"));
	}

	public String getCodMotivoFineAtto() {
		return Utils.notNull(source.getAttribute("CODMOTIVOFINEATTO"));
	}

	public String getDatFine() {
		return Utils.notNull(source.getAttribute("DATFINE"));
	}

	public String getCdnUtIns() {
		return Utils.notNull(source.getAttribute("CDNUTINS"));
	}

	public String getDtmIns() {
		return Utils.notNull(source.getAttribute("DTMINSORA"));
	}

	public String getCdnUtMod() {
		return Utils.notNull(source.getAttribute("CDNUTMOD"));
	}

	public String getDtmMod() {
		return Utils.notNull(source.getAttribute("DTMMODORA"));
	}

	public String getNumKlockPattoLavoratore() {
		return Utils.notNull(source.getAttribute("NUMKLOPATTOLAVORATORE"));
	}

	public String getCodCpi() {
		return Utils.notNull(source.getAttribute("CODCPI"));
	}

	public String getDescCpi() {
		return Utils.notNull(source.getAttribute("descCPI"));
	}

	public String getCodice() {
		return Utils.notNull(source.getAttribute("CODICE"));
	}

	// public String getCognIns() { return
	// Utils.notNull(source.getAttribute("CognIns"));}
	// public String getNomIns() { return
	// Utils.notNull(source.getAttribute("NomIns"));}
	// public String getCognMod() { return
	// Utils.notNull(source.getAttribute("CognMod"));}
	// public String getNomMod() { return
	// Utils.notNull(source.getAttribute("NomMod"));}
	public String getDatDichiarazione() {
		return Utils.notNull(source.getAttribute("DATDICHIARAZIONE"));
	}

	public String getDatInizio() {
		return Utils.notNull(source.getAttribute("DATINIZIO"));
	}

	public String getDescrizioneStato() {
		return Utils.notNull(source.getAttribute("DESCRIZIONESTATO"));
	}

	public String getCodTipoPatto() {
		return Utils.notNull(source.getAttribute("CODTIPOPATTO"));
	}

	public String getCodServizio() {
		return Utils.notNull(source.getAttribute("CODSERVIZIO"));
	}

	public String getCodStatoAttoDid() {
		return Utils.notNull(source.getAttribute("codStatoAttoDid"));
	}
	
	//campi assegno ricollocazione
	public String getImportoAr(){
		return Utils.notNull(source.getAttribute("importoar"));
	}
	
	public String getDatNaspi(){
		return Utils.notNull(source.getAttribute("datNaspi"));
	}
	
	public String getNoteElemAttivazione(){
		return Utils.notNull(source.getAttribute("strnoteattivazione"));
	}

	public String getDataRiferimento150() {
		return Utils.notNull(source.getAttribute("DATRIFERIMENTO150"));
	}
	
	public String getIndiceSvantaggio150() {
		return  Utils.notNull(source.getAttribute("NUMINDICESVANTAGGIO150"));
	}
	
	/**
	 * @return il numero di lock successivo, se esiste, oppure stringa vuota
	 *         altrimenti
	 */
	public String getNextKlock() {
		if (getNumKlockPattoLavoratore().equals(""))
			return "";
		String ret = "";
		try {
			BigDecimal klock = (BigDecimal) this.source.getAttribute("NUMKLOPATTOLAVORATORE");
			ret = String.valueOf(klock.intValue() + 1);
		} catch (Throwable t) {
		}
		return ret;
	}

	public String getDescrizioneStatoTagliato(int max) {
		if (getDescrizioneStato() != null) {
			int size = getDescrizioneStato().length() + 3;
			if (size > max)
				return getDescrizioneStato().substring(0, max) + "...";
		}
		return getDescrizioneStato();
	}

	// se lo stato del patto lo richiede si prendono le infomazioni correnti
	public void setInfoCorrenti(InfoCorrenti i) {
		try {
			// se il lavoratore e' in mobilita' o ha un accordo la query di
			// dettaglio del patto
			// potrebbe non estrarre l'informazione sull'elenco anagrafico
			if (getDatInizio().equals(""))
				setDatInizioElAnag(i.getDatInizio());
			if (getCodStatoAtto().equals("PR"))
				return;
			if (getCodStatoAtto().equals("PP"))
				;
			if (getPrgStatoOccupaz().equals(""))
				setPrgStatoOccupaz(i.getPrgStatoOccupaz());
			if (getDescrizioneStato().equals(""))
				setDescrizioneStatoOccupaz(i.getDescrizioneStato());
			if (getCodCpi().equals(""))
				setCodCpiTit(i.getCodCpiTit());
			if (getDescCpi().equals(""))
				setDescCpiTit(i.getDescCpi());
			if ((exist() && isPatto297() && getCodStatoAttoDid().equals("")) || !exist()) {
				setDatDichiarazione(i.getDatDichiarazione());
				setCodStatoAttoDid(i.getCodStatoAttoDid());
				setPrgDichDisponibilita(i.getPrgDichDisponibilita());
			}
		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean exist() {
		return !getPrgPattoLavoratore().equals("");
	}

	/**
	 * @param s
	 */
	private void setPrgDichDisponibilita(String s) throws SourceBeanException {
		this.source.setAttribute("PRGDICHDISPONIBILITA", s);
	}

	/**
	 * @param s
	 *            cod stato atto did valida o legata al patto
	 */
	private void setCodStatoAttoDid(String s) throws SourceBeanException {
		this.source.setAttribute("codStatoAttoDid", s);
	}

	/**
	 * @param s
	 *            descrizione cpi titolare
	 */
	private void setDescCpiTit(String s) throws SourceBeanException {
		this.source.setAttribute("descCPI", s);
	}

	/**
	 * @param s
	 *            cod cpi titolare
	 */
	private void setCodCpiTit(String s) throws SourceBeanException {
		this.source.setAttribute("CODCPI", s);
	}

	/**
	 * @param s
	 *            stato occupazionale legato al patto o aperto al momento del
	 *            caricamento della pagina
	 */
	private void setDescrizioneStatoOccupaz(String s) throws SourceBeanException {
		this.source.setAttribute("DESCRIZIONESTATO", s);
	}

	/**
	 * @param s
	 *            la data inizio elenco anagrafico
	 */
	private void setDatInizioElAnag(String s) throws SourceBeanException {
		this.source.setAttribute("DATINIZIO", s);
	}

	public String getDataInizioElencoanAnafico() {
		return getDatInizio();
	}

	/**
	 * @param string
	 */
	private void setDatDichiarazione(String s) throws SourceBeanException {
		this.source.setAttribute("DATDICHIARAZIONE", s);

	}

	/**
	 * @param string
	 */
	private void setPrgStatoOccupaz(String s) throws SourceBeanException {
		this.source.setAttribute("PRGSTATOOCCUPAZ", s);
	}

	// obiettiviDelPattoModify = flag_insert || !CODSTATOATTO.equals("PR");
	private SourceBean defaultBean() {
		SourceBean s = null;
		try {
			s = new SourceBean("PATTO297_EMPTY");
			s.setAttribute("DATSTIPULA", DateUtils.getNow());

		} catch (Exception e) {
		}
		return s;

	}

	public void setInfoScadenzaPatto(SourceBean numGg) {
		int giorno, mese, anno;
		// Gestione dei numGG per la scad del patto
		// SourceBean numGg =
		// (SourceBean)serviceResponse.getAttribute("M_GetNumGG.ROWS.ROW");
		String numGScadP = numGg.getAttribute("NUMGGSCADPATTO").toString();
		giorno = Integer.parseInt(getDatStipula().substring(0, 2), 10);
		mese = Integer.parseInt(getDatStipula().substring(3, 5), 10);
		anno = Integer.parseInt(getDatStipula().substring(6, 10), 10);
		GregorianCalendar dataDich = new GregorianCalendar(anno, (mese - 1), giorno);
		dataDich.set(Calendar.DATE, (giorno + Integer.parseInt(numGScadP)));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			setDatScadenzaConferma(df.format(dataDich.getTime()));
		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param string
	 */
	private void setDatScadenzaConferma(String s) throws SourceBeanException {
		this.source.setAttribute("DATSCADCONFERMA", s);

	}
	
	public String getDataRiferimento() {
		return Utils.notNull(source.getAttribute("DATRIFERIMENTO"));
	}
	
	public BigDecimal getIndiceSvantaggio() {
		return (BigDecimal) source.getAttribute("NUMINDICESVANTAGGIO");
	}
	
	public BigDecimal getIndiceSvantaggio2() {
		return (BigDecimal) source.getAttribute("NUMINDICESVANTAGGIO2");
	}
	
	public String getIndiceSvantaggioVecchio() {
		return Utils.notNull(source.getAttribute("STRINDICESVANTAGGIOOLD"));
	}
	
	public String getCodificaPatto() {
		return Utils.notNull(source.getAttribute("CODCODIFICAPATTO"));
	}
	
	public String getCodVchProfiling() {
		return Utils.notNull(source.getAttribute("codVCHProfiling"));
	}
	
	public BigDecimal getDecDoteProcessoAssegnato() {
		return (BigDecimal) source.getAttribute("decDoteProcessoAssegnato");
	}
	
	public BigDecimal getDecDoteProcessoResidua() {
		return (BigDecimal) source.getAttribute("decDoteProcessoResidua");
	}
	
	public BigDecimal getDecDoteRisultatoAssegnato() {
		return (BigDecimal) source.getAttribute("decDoteRisultatoAssegnato");
	}
	
	public BigDecimal getDecDoteRisultatoResidua() {
		return (BigDecimal) source.getAttribute("decDoteRisultatoResidua");
	}
	
	public BigDecimal getPrgLavoratoreProfilo() {
		return (BigDecimal) source.getAttribute("prglavoratoreprofilo");
	}
	
	public void controllaChiusuraAccordoGenerico(Object cdnLavoratore, RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		Vector accordiGen = DBLoad.getAccordiGenerici(cdnLavoratore, transExec);
		for (int i = 0; i < accordiGen.size(); i++) {
			SourceBean accordo = (SourceBean) accordiGen.get(i);
			String dataStipula = (String) accordo.getAttribute(PattoBean.DB_DAT_INIZIO);
			String dataFine = (String) accordo.getAttribute(PattoBean.DB_DAT_FINE);
			String codStatoAtto = (String) accordo.getAttribute(PattoBean.DB_COD_STATO_ATTO);
			if ( (codStatoAtto != null) && (codStatoAtto.equalsIgnoreCase("PR") || codStatoAtto.equalsIgnoreCase("PP")) ) {
				if (dataFine == null || dataFine.equals("")) {
					DBStore.chiudiAccordoNo297(accordo, dataStipula, Properties.CHIUSURA_AUTOMATICA_ACCORDO, requestContainer, transExec);
				}
			}
		}
	}
	
	public void controllaAperturaAccordoGenerico(Object cdnLavoratore, RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		boolean accordoAperto = false;
		Vector accordiGen = DBLoad.getAccordiGenerici(cdnLavoratore, transExec);
		for (int i = 0; i < accordiGen.size(); i++) {
			SourceBean accordo = (SourceBean) accordiGen.get(i);
			String dataFine = (String) accordo.getAttribute(PattoBean.DB_DAT_FINE);
			String codStatoAtto = (String) accordo.getAttribute(PattoBean.DB_COD_STATO_ATTO);
			String codMotivoFineAtto = (String) accordo.getAttribute(PattoBean.COD_MOTIVO_FINE_ATTO);
			if ( (codStatoAtto != null) && (codStatoAtto.equalsIgnoreCase("PR") || codStatoAtto.equalsIgnoreCase("PP")) ) {
				if (dataFine != null && !dataFine.equals("")) {
					//accordo chiuso
					if (codMotivoFineAtto != null && codMotivoFineAtto.equalsIgnoreCase(Properties.CHIUSURA_AUTOMATICA_ACCORDO)) {
						if (!accordoAperto) {
							DBStore.apriAccordoNo297(accordo, requestContainer, transExec);
							accordoAperto = true;
							//gestione adeguamento patto ANP
							String codTipoAccordo = accordo.getAttribute("codtipopatto")!=null?accordo.getAttribute("codtipopatto").toString():"";
							if (!codTipoAccordo.equalsIgnoreCase(PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {
								DBStore.trasformaPattoAccordoANP(accordo, requestContainer, transExec);
							}
						}
					}
				}
				else {
					//accordo aperto
					if (!accordoAperto) {
						accordoAperto = true;
						//gestione adeguamento patto ANP
						String codTipoAccordo = accordo.getAttribute("codtipopatto")!=null?accordo.getAttribute("codtipopatto").toString():"";
						if (!codTipoAccordo.equalsIgnoreCase(PattoBean.DB_ADESIONE_NUOVO_PROGRAMMA)) {
							DBStore.trasformaPattoAccordoANP(accordo, requestContainer, transExec);
						}
					}
					else {
						String dataStipula = (String) accordo.getAttribute(PattoBean.DB_DAT_INIZIO);
						DBStore.chiudiAccordoNo297(accordo, dataStipula, Properties.CHIUSURA_AUTOMATICA_ACCORDO, requestContainer, transExec);
					}
				}
			}
		}
	}
	/* Soggetto Accreditato */
	public String getCodiceSedeEnte() {
		return Utils.notNull(source.getAttribute("codSedeEnte"));
	}
	public String getCodiceFiscaleEnte() {
		return Utils.notNull(source.getAttribute("cfEnte"));
	}
	public String getRagioneSocialeEnte() {
		return Utils.notNull(source.getAttribute("ragSocEnte"));
	}
	public String getIndirizzoEnte() {
		return Utils.notNull(source.getAttribute("indirizzoEnte"));
	}
	public String getStrTelEnte() {
		return Utils.notNull(source.getAttribute("strTelEnte"));
	}	
	public String getComuneEnte() {
		return Utils.notNull(source.getAttribute("comuneEnte"));
	}
	public String getNoteEnte() {
		return Utils.notNull(source.getAttribute("Strnotaente"));
	}
	public String getStrNumProfiling() {
		return Utils.notNull(source.getAttribute("NUMPROFILING"));
	}
	public String getStrProfiling() {
		return Utils.notNull(source.getAttribute("STRPROFILING"));
	}
	public String getSiglaProvinciaEnte() {
		return Utils.notNull(source.getAttribute("Strtarga"));
	}
	/** PATTO ON LINE **/
	public String getCodiceAbilitazioneServizi() {
		return Utils.notNull(source.getAttribute("STRCODABIPORTALE"));
	}
	public String getDataInvioAccettazioneServizi() {
		return Utils.notNull(source.getAttribute("DTMINVIOPORTALE"));
	}
	public String getDataAccettazioneServizi() {
		return Utils.notNull(source.getAttribute("DTMACCETTAZIONE"));
	}
	public String getStatoPattoOnLine() {
		return Utils.notNull(source.getAttribute("CODMONOACCETTAZIONE"));
	}
	public String getTipoAccettazionePattoOnLine() {
		return Utils.notNull(source.getAttribute("STRTIPOACCETTAZIONE"));
	}
	public String getFlagPattoOnLine() {
		return Utils.notNull(source.getAttribute("FLGPATTOONLINE"));
	}
	public String getDataUltimaStampa(){
		return Utils.notNull(source.getAttribute("DTMULTIMASTAMPA"));
	}
	public String getFlagReinvioPattoOnLine() {
		return Utils.notNull(source.getAttribute("FLGREINVIOPTONLINE"));
	}
	
	public Integer riapriPatto(DataConnection conn, BigDecimal prgPattoLav, int forzatura, BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand("{ call ? := PG_UTILS_DID_PATTO.RIAPRIPATTO(?,?,?) }");
			
			int paramIndex = 0;
			ArrayList parameters = new ArrayList(4);
			
			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);
			
			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgPatto", Types.BIGINT,  prgPattoLav));
			command.setAsInputParameters(paramIndex++);
			
			parameters.add(conn.createDataField("cdnUt", Types.BIGINT,  cdnParUtente));
			command.setAsInputParameters(paramIndex++);
			
			parameters.add(conn.createDataField("forzaRiapertura", Types.INTEGER, forzatura));
			command.setAsInputParameters(paramIndex++);
			
			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			Integer esito = (Integer)df.getObjectValue();
			
			return esito;
		}
		catch (Exception ex) {
			return -1;
		}
		finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(null, command, dr);
		}
	}
	
}