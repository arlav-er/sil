package it.eng.myportal.beans.curriculum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.component.datagrid.DataGrid;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.PageEvent;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.AutoCompleteBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.PrimoContattoDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.ejb.ClicLavoroPrimoContattoEjb;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.helpers.CurriculumSearchParams;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean
@ViewScoped
/**
 * Bean ricerca CV utilizzato sia da azienda che redazione
 * @author Ale
 *
 */
public class CercaCurriculumPfBean extends AbstractBaseBean implements Serializable {

	private static final long serialVersionUID = 5537670668031203617L;

	protected static Log log = LogFactory.getLog(CercaCurriculumPfBean.class);

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	ClicLavoroPrimoContattoEjb primoContattoEjb;

	@ManagedProperty(value = "#{autoCompleteBean}")
	private AutoCompleteBean autoCompleteBean;

	// Contatto da parte dell'azienda che visualizza il CV
	private PrimoContattoDTO primoContatto;

	private RicercaCvAziendaModel curriculumModel;
	private CurriculumSearchParams curriculumSearchParams;

	private int dataGridFirstPage = 0;


	private boolean loadFiltersRecords = false;

	// used in contattaLavoratore modal
	private CvDatiPersonali selectedDatiPersonali;
	RestoreParameters ret;
	PageEvent event;

	/**
	 * Inizializza i parametri del bean.
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		ret = super.generateRestoreParams();
		secureMeAzienda();
		resetPrimoContatto();
		retrieveData();
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		ret.put("data", curriculumSearchParams);
		return ret;
	}

	protected RestoreParameters generatePageParams(PageEvent event) {
		ret.put("pageEvent", event);
		putParamsIntoSession();
		return ret;
	}


	protected CurriculumSearchParams retrieveData() {

		if (beanParamsSess != null) {
			if(beanParamsSess.containsKey("data")){
				Object object = beanParamsSess.get("data");
				curriculumSearchParams = (CurriculumSearchParams)object;
				if(beanParamsSess.containsKey("pageEvent")){
					Object pageNumber = beanParamsSess.get("pageEvent");
					event = (PageEvent)pageNumber;
				}
				searchParams();
			}
			log.debug("dati recuperati dalla sessione.");

		}else{
			curriculumSearchParams = new CurriculumSearchParams();
		}
		return curriculumSearchParams;
	}

	public void secureMeAzienda() {
		log.warn("Attenzione sto accedendo come azienda oppure come utente provincia");
		if (!session.isAzienda() && !session.isProvincia()) {
			redirectHome();
		}
	}

	/**
	 * Effettua la ricerca. Calcola il numero totale di elementi, inizializza la paginazione e carica la prima pagina
	 * del risultato.
	 */
	public void search() {
		// Salvo i parametri di ricerca per poter tornare indietro
		generateRestoreParams();
		putParamsIntoSession();
		// Creo l'handler della paginazione
		setDataGridFirstPage(0);
		curriculumModel = new RicercaCvAziendaModel(curriculumSearchParams, cvDatiPersonaliHome);
		
	}
	public void searchParams() {
		curriculumModel = new RicercaCvAziendaModel(curriculumSearchParams, cvDatiPersonaliHome);
		if(event != null) {
			this.setDataGridFirstPage(((DataGrid) event.getSource()).getFirst());
		}
	}

	/**
	 * Eh lo so, la vita va cosi. Ore per togliere i DTO e poi ti accorgi che le many to one sono dei <Set>, che JSF non
	 * vuole
	 * 
	 * helper method
	 * 
	 * @param set
	 * @return
	 */
	public <T> List<T> getListFromSet(Set<T> set) {
		if (set == null || set.isEmpty())
			return new ArrayList<T>();
		return new ArrayList<T>(set);
	}

	/**
	 * "Pulisce" i parametri del bean riguardo al contattare i lavoratori.
	 */
	private void resetPrimoContatto() {
		primoContatto = new PrimoContattoDTO();
		primoContatto.setIdPfPrincipalAzienda(getSession().getPrincipalId());
	}

	/**
	 * Invia un messaggio di contatto al proprietario di un curriculum.
	 */
	public void inviaPrimoContatto() {

		if (primoContatto == null || primoContatto.getMessaggio().isEmpty()) {
			log.warn("NON invio un messaggio vuoto, abortisco");
			return;
		}

		try {
			primoContatto.setIdCvDatiPersonali(selectedDatiPersonali.getIdCvDatiPersonali());
			primoContattoEjb.sendPrimoContatto(primoContatto);
			resetPrimoContatto();
			RequestContext.getCurrentInstance().addCallbackParam("inviaSuccess", true);
			// addInfoMessage("messaggio.primo_contatto_ok");

		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	public void onDataGridPageChange(PageEvent event) {
		this.setDataGridFirstPage(((DataGrid) event.getSource()).getFirst());
		generatePageParams(event);
	}

	public boolean chekRER() {
		boolean check = false;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			check = true;
		}
		return check;
	}

	/*public void titoliDiStudioNodeSelected(org.primefaces.event.NodeSelectEvent event) {
		curriculumSearchParams.setDeTitolo((DeTitolo) event.getTreeNode().getData());
		RequestContext.getCurrentInstance().execute("PF('titoliDiStudioWV').selectionComplete();");
	}*/

	public String formatDateIntoISO(Date date) {
		if (date == null) {
			return null;
		}
		return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(date);
	}

	public PrimoContattoDTO getPrimoContatto() {
		return primoContatto;
	}

	public void setPrimoContatto(PrimoContattoDTO primoContatto) {
		this.primoContatto = primoContatto;
	}

	public RicercaCvAziendaModel getCurriculumModel() {
		return curriculumModel;
	}

	public void setCurriculumModel(RicercaCvAziendaModel curriculumModel) {
		this.curriculumModel = curriculumModel;
	}

	public AutoCompleteBean getAutoCompleteBean() {
		return autoCompleteBean;
	}

	public void setAutoCompleteBean(AutoCompleteBean autoCompleteBean) {
		this.autoCompleteBean = autoCompleteBean;
	}

	public CurriculumSearchParams getCurriculumSearchParams() {
		return curriculumSearchParams;
	}

	public void setCurriculumSearchParams(CurriculumSearchParams curriculumSearchParams) {
		this.curriculumSearchParams = curriculumSearchParams;
	}

	public int getDataGridFirstPage() {
		return dataGridFirstPage;
	}

	public void setDataGridFirstPage(int dataGridFirstPage) {
		this.dataGridFirstPage = dataGridFirstPage;
	}

	public boolean isLoadFiltersRecords() {
		return loadFiltersRecords;
	}

	public void setLoadFiltersRecords(boolean loadFiltersRecords) {
		this.loadFiltersRecords = loadFiltersRecords;
	}

	public CvDatiPersonali getSelectedDatiPersonali() {
		return selectedDatiPersonali;
	}

	public void setSelectedDatiPersonali(CvDatiPersonali selectedDatiPersonali) {
		this.selectedDatiPersonali = selectedDatiPersonali;
	}

}
