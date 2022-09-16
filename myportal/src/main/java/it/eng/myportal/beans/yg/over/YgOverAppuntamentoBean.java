package it.eng.myportal.beans.yg.over;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.yg.SlotComparator;
import it.eng.myportal.dtos.AgAppuntamentoDTO;
import it.eng.myportal.dtos.AppuntamentoDTO;
import it.eng.myportal.dtos.DeAmbienteSilDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.YgGaranziaOverDTO;
import it.eng.myportal.entity.home.AgAppuntamentoHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.YgGaranziaOverHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbienteSilHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.rest.yg.PrenotaAppuntamento;
import it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.xml.datatype.XMLGregorianCalendar;

@ManagedBean
@ViewScoped
public class YgOverAppuntamentoBean extends AbstractBaseBean {

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeAmbienteSilHome deAmbienteSilHome;

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	@EJB
	private PrenotaAppuntamento prenotaAppuntamentoHome;

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	YgGaranziaOverHome ygGaranziaOverHome;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	private YgGaranziaOverDTO adesione;
	private AppuntamentoDTO appuntamento;
	private List<DeCpiDTO> cpiList;
	private List<DeAmbienteSilDTO> sportelloDistaccatoList;

	private Date selectedDataDa;
	private String selectedMattinaPomeriggio;
	private DeCpiDTO selectedCpi;
	private DeAmbienteSilDTO selectedSportello;

	private boolean showSlotDisponibili;
	private Date dataPrecedente;
	private List<Risposta.ElencoDisponibilita.DatiAppuntamento> slotDisponibili;
	private Boolean[] slotCheckbox;
	private BigInteger idSlotSelezionato;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (!session.isUtente()) {
			log.warn("Tentativo di accedere alla sezione Garanzia Giovani Over da utente non cittadino.");
			redirectHome();
			return;
		}

		// Carico la adesione a Garanzia Over dell'utente.
		adesione = ygGaranziaOverHome.findByPfPrincipal(session.getPrincipalId());
		if (adesione == null || adesione.getDtAdesione() == null) {
			log.warn("Tentativo di accedere alla sezione Appuntamento Garanzia Giovani Over senza un'adesione.");
			redirectHome();
			return;
		}

		// Se è già stato preso un appuntamento, carico anche l'appuntamento.
		if (adesione.getFlgPresoInCarico()) {
			AgAppuntamentoDTO agAppuntamento = agAppuntamentoHome.findDTOAppuntamentoGaranziaOver(
					session.getPrincipalId(), adesione.getId());
			appuntamento = agAppuntamentoHome.findAppuntamentoDTObyIdAgAppuntamento(agAppuntamento.getId());
		}

		// Carico la lista di CPI per la provincia dell'adesione a Umbriattiva Adulti.
		cpiList = deCpiHome.findDTOValidiByProvincia(adesione.getDeProvincia().getId());
		selectedCpi = adesione.getDeCpiAdesione();
		onSelectCPI();
		selectedMattinaPomeriggio = "I";
	}

	/**
	 * Questo metodo fa il redirect alla home se l'utente non può vedere questa pagina.
	 */
	public void checkViewPage() {
		if (!session.isUtente()) {
			log.warn("Tentativo di accedere alla sezione Umbriattiva Adulti da utente non cittadino.");
			redirectHome();
			return;
		}

		// Per il momento questa sezione è abilitata SOLO per il portale Umbria
		if (ConstantsSingleton.COD_REGIONE != ConstantsSingleton.COD_REGIONE_UMBRIA) {
			log.warn("Tentativo di accedere alla sezione Umbriattiva Adulti su un Portale non Umbria.");
			redirectHome();
			return;
		}
	}

	/**
	 * Restituisce TRUE se l'utente ha già un appuntamento presso il CPI.
	 */
	public boolean isAppuntamentoPreso() {
		return adesione.getFlgPresoInCarico();
	}

	/**
	 * Aggiorna la lista degli sportelli distaccati in base al CPI attualmente selezionato.
	 */
	public void onSelectCPI() {
		if (selectedCpi != null && selectedCpi.getId() != null) {
			sportelloDistaccatoList = deAmbienteSilHome.findDTOByCodCpi(selectedCpi.getId());
		} else {
			sportelloDistaccatoList = new ArrayList<DeAmbienteSilDTO>();
		}
	}

	/**
	 * Restituisce la data attuale (Serve a limitare il componente che sceglie la data).
	 */
	public Date getTodayDate() {
		return new Date();
	}

	/**
	 * Prepara i dati degli slot disponibili ricevuti dal WS per la visualizzazione
	 */
	public void cercaSlot() {
		// Un valore di "indifferente" viene tradotto con null per il WS.
		String mattinaPomeriggioWs = selectedMattinaPomeriggio.equals("I") ? null : selectedMattinaPomeriggio;

		// Questo è per evitare NullPointerException se non è selezionato uno sportello.
		Integer idSelectedSportello = selectedSportello == null ? null : selectedSportello.getId();

		// Aggiorno il CPI in cui cercare slot, nel caso l'utente ne abbia selezionato un altro.
		adesione.setDeCpiAdesione(selectedCpi);

		// Chiamo il WS per ottenere la lista di slot disponibili per un singolo giorno, il primo che
		// abbia almeno uno slot disponibile dopo la data selezionata.
		slotDisponibili = agAppuntamentoHome.disponibilitaAppuntamentoOver(session.getPrincipalId(), adesione.getId(),
				selectedDataDa, null, mattinaPomeriggioWs, idSelectedSportello, adesione.getDeCpiAdesione().getId());

		// Ordino le liste di slot per orario
		SlotComparator slotComparator = new SlotComparator();
		Collections.sort(slotDisponibili, slotComparator);

		// Faccio in modo che la lista venga visualizzata e inizializzo il front-end
		showSlotDisponibili = true;
		idSlotSelezionato = null;
		slotCheckbox = new Boolean[slotDisponibili.size()];
	}

	/**
	 * Metodo chiamato quando l'utente clicca sul pulsante "cerca disponibilità nei giorni seguenti".
	 */
	public void cercaGiorniSeguenti() {
		// Setto la "data da" della ricerca dal giorno successivo a quello degli slot visualizzati.
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(convertXMLCalendarToDate(slotDisponibili.get(0).getDataAppuntamento()));
		currentDate.add(Calendar.DATE, 1);
		selectedDataDa = currentDate.getTime();

		cercaSlot();
	}

	/**
	 * Metodo chiamato quando l'utente decide di prenotare effettivamente un appuntamento.
	 */
	public void prenotaAppuntamento() {
		if (idSlotSelezionato == null)
			return;
		PrenotaAppuntamento.RispostaFissaAppuntamwento rispostaFissaAppuntamento = prenotaAppuntamentoHome
				.fissaAppuntamentoGaranziaOver(session.getPrincipalId(), adesione.getId(), idSlotSelezionato, adesione
						.getDeCpiAdesione().getId());

		// Se la prenotazione è andata a buon fine, aggiorno i dati e mando una e-mail.
		if (rispostaFissaAppuntamento.getRisposta().getEsito().getCodice().equals("00")) {
			appuntamento = agAppuntamentoHome.findAppuntamentoDTObyIdAgAppuntamento(rispostaFissaAppuntamento
					.getIdAgAppuntamento());
			adesione = ygGaranziaOverHome.findByPfPrincipal(session.getPrincipalId());
			mandaMailAppuntamento(adesione, appuntamento);
		} else {
			addErrorMessage("data.error_updating");
		}
	}

	private void mandaMailAppuntamento(YgGaranziaOverDTO adesione, AppuntamentoDTO appuntamento) {
		EmailDTO confirmRegistrationEmail = EmailDTO.buildAppuntamentoGaranziaOverEmail(adesione.getPfPrincipal(),
				appuntamento);
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, confirmRegistrationEmail);
		session.refreshSession();
	}

	/**
	 * Metodo che serve a convertire l'XMLGregorianCalendar (restituito dal WS) in un oggetto Date che JSF è in grado di
	 * comprendere e mostrare.
	 */
	public Date convertXMLCalendarToDate(XMLGregorianCalendar cal) {
		return cal.toGregorianCalendar().getTime();
	}

	/**
	 * Metodo chiamato quando l'utente clicca su una checkbox per selezionare uno slot. Setta tutte le altre checkbox a
	 * FALSE e memorizza quale slot è selezionato.
	 */
	public void onSlotSelected(Integer selectedSlot) {
		idSlotSelezionato = slotDisponibili.get(selectedSlot).getIdentificativoSlot();
		for (int i = 0; i < slotCheckbox.length; i++) {
			if (i != selectedSlot)
				slotCheckbox[i] = false;
		}
	}

	public YgGaranziaOverDTO getAdesione() {
		return adesione;
	}

	public void setAdesione(YgGaranziaOverDTO adesione) {
		this.adesione = adesione;
	}

	public List<DeCpiDTO> getCpiList() {
		return cpiList;
	}

	public void setCpiList(List<DeCpiDTO> cpiList) {
		this.cpiList = cpiList;
	}

	public List<DeAmbienteSilDTO> getSportelloDistaccatoList() {
		return sportelloDistaccatoList;
	}

	public void setSportelloDistaccatoList(List<DeAmbienteSilDTO> sportelloDistaccatoList) {
		this.sportelloDistaccatoList = sportelloDistaccatoList;
	}

	public Date getSelectedDataDa() {
		return selectedDataDa;
	}

	public void setSelectedDataDa(Date selectedDataDa) {
		this.selectedDataDa = selectedDataDa;
	}

	public String getSelectedMattinaPomeriggio() {
		return selectedMattinaPomeriggio;
	}

	public void setSelectedMattinaPomeriggio(String selectedMattinaPomeriggio) {
		this.selectedMattinaPomeriggio = selectedMattinaPomeriggio;
	}

	public DeCpiDTO getSelectedCpi() {
		return selectedCpi;
	}

	public void setSelectedCpi(DeCpiDTO selectedCpi) {
		this.selectedCpi = selectedCpi;
	}

	public DeAmbienteSilDTO getSelectedSportello() {
		return selectedSportello;
	}

	public void setSelectedSportello(DeAmbienteSilDTO selectedSportello) {
		this.selectedSportello = selectedSportello;
	}

	public Date getDataPrecedente() {
		return dataPrecedente;
	}

	public void setDataPrecedente(Date dataPrecedente) {
		this.dataPrecedente = dataPrecedente;
	}

	public List<Risposta.ElencoDisponibilita.DatiAppuntamento> getSlotDisponibili() {
		return slotDisponibili;
	}

	public void setSlotDisponibili(List<Risposta.ElencoDisponibilita.DatiAppuntamento> slotDisponibili) {
		this.slotDisponibili = slotDisponibili;
	}

	public boolean isShowSlotDisponibili() {
		return showSlotDisponibili;
	}

	public void setShowSlotDisponibili(boolean showSlotDisponibili) {
		this.showSlotDisponibili = showSlotDisponibili;
	}

	public Boolean[] getSlotCheckbox() {
		return slotCheckbox;
	}

	public void setSlotCheckbox(Boolean[] slotCheckbox) {
		this.slotCheckbox = slotCheckbox;
	}

	public BigInteger getIdSlotSelezionato() {
		return idSlotSelezionato;
	}

	public void setIdSlotSelezionato(BigInteger idSlotSelezionato) {
		this.idSlotSelezionato = idSlotSelezionato;
	}

	public AppuntamentoDTO getAppuntamento() {
		return appuntamento;
	}

	public void setAppuntamento(AppuntamentoDTO appuntamento) {
		this.appuntamento = appuntamento;
	}
}
