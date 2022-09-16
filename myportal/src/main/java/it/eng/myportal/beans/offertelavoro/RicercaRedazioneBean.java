package it.eng.myportal.beans.offertelavoro;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.PageEvent;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.AutoCompleteBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.VacancyDaRedazioneDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.DeEvasioneRich;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeEvasioneRichHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.helpers.RedazioneSearchParams;
import it.eng.myportal.utils.ConstantsSingleton;


/**
 * BackingBean della pagina che xpermette di cercare tra le offerte di lavoro
 *
 */
@ManagedBean
@ViewScoped
public class RicercaRedazioneBean extends AbstractBaseBean implements Serializable  {
	private static Log log = LogFactory.getLog(RicercaRedazioneBean.class);

	private List<VaDatiVacancy> risultato;
	private VacancyDaRedazioneDTO vacancySelected;

	@ManagedProperty(value = "#{autoCompleteBean}")
	private AutoCompleteBean autoCompleteBean;

	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;
	@EJB
	DeProvenienzaHome deProvenienzaHome;
	@EJB
	DeEvasioneRichHome deEvasioneRichHome;

	RestoreParameters ret;
	PageEvent event;
	RedazioneSearchParams redazioneSearchParams;
	private RicercaVacancyModel ricercaVacancyModel;

	private int dataGridFirstPage = 0;
	private boolean loadFiltersRecords = false;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		ret = super.generateRestoreParams();
		secureMeProvincia();
		retrieveData();

	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		ret = super.generateRestoreParams();
		if(redazioneSearchParams != null){
			ret.put("data", redazioneSearchParams);
		}
		return ret;
	}

	public boolean chekRER() {
		boolean check = false;
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			check = true;
		}
		return check;
	}

	public void secureMeProvincia() {
		log.warn("Attenzione Controllo accesso:utente Provincia");
		if (!session.isProvincia()) {
			redirectHome();
		}
	}

	protected RedazioneSearchParams retrieveData() {

		if (beanParamsSess != null) {
			if(beanParamsSess.containsKey("data")){
				Object object = beanParamsSess.get("data");
				redazioneSearchParams = (RedazioneSearchParams) object;
				setRedazioneSearchParams(redazioneSearchParams);
				if(beanParamsSess.containsKey("pageEvent")){
					Object pageNumber = beanParamsSess.get("pageEvent");
					event = (PageEvent)pageNumber;
				}
				searchParams();
			}
			log.debug("dati recuperati dalla sessione.");

		}else{
			redazioneSearchParams = new RedazioneSearchParams();
			risultato = new ArrayList<VaDatiVacancy>();

		}
		return redazioneSearchParams;
	}


	public void search() {
		log.info("Ricerca Offerte di lavoro e costruzione del Model - RicercaVacancyModel.");
		generateRestoreParams();
		putParamsIntoSession();
		setDataGridFirstPage(0);
		ricercaVacancyModel = new RicercaVacancyModel(redazioneSearchParams, rvRicercaVacancyHome);
	}

	public void searchParams() {
		ricercaVacancyModel = new RicercaVacancyModel(redazioneSearchParams, rvRicercaVacancyHome);
		if(event != null) {
			this.setDataGridFirstPage(((DataTable) event.getSource()).getFirst());
		}
	}

	protected RestoreParameters generatePageParams(PageEvent event) {
		ret.put("pageEvent", event);
		putParamsIntoSession();
		return ret;
	}

	public void onDataGridPageChange(PageEvent event) {
		this.setDataGridFirstPage(((DataTable) event.getSource()).getFirst());
		generatePageParams(event);
	}

	public PageEvent getEvent() {
		return event;
	}

	public void setEvent(PageEvent event) {
		this.event = event;
	}

	public RedazioneSearchParams getRedazioneSearchParams() {
		return redazioneSearchParams;
	}

	public void setRedazioneSearchParams(RedazioneSearchParams redazioneSearchParams) {
		this.redazioneSearchParams = redazioneSearchParams;
	}

	public AutoCompleteBean getAutoCompleteBean() {
		return autoCompleteBean;
	}

	public void setAutoCompleteBean(AutoCompleteBean autoCompleteBean) {
		this.autoCompleteBean = autoCompleteBean;
	}

	public List<VaDatiVacancy> getRisultato() {
		return risultato;
	}

	public void setRisultato(List<VaDatiVacancy> risultato) {
		this.risultato = risultato;
	}

	public VacancyDaRedazioneDTO getVacancySelected() {
		return vacancySelected;
	}

	public void setVacancySelected(VacancyDaRedazioneDTO vacancySelected) {
		this.vacancySelected = vacancySelected;
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

	public RicercaVacancyModel getRicercaVacancyModel() {
		return ricercaVacancyModel;
	}

	public void setRicercaVacancyModel(RicercaVacancyModel ricercaVacancyModel) {
		this.ricercaVacancyModel = ricercaVacancyModel;
	}

	public void onRowSelect(SelectEvent event) {
		String base = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(
					base + "/faces/secure/azienda/vacancies/view_pf.xhtml?id="
							+ vacancySelected.getIdVaDatiVacancy());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Date getCurrentDate() {
		return new Date();
	}
	
	public List<DeProvenienza> findDeProvenienzaFiltered(){
		return deProvenienzaHome.findByCodMyPortalAndSil();
	}
	
	public List<DeEvasioneRich> findByCodEvasione(){
		List<DeEvasioneRich> filteredList = deEvasioneRichHome
				.findByCodEvasione(ConstantsSingleton.Evasione.PUBB_ANONIMA,
									ConstantsSingleton.Evasione.PUBB_ANONIMA_PRESELEZIONE,
									ConstantsSingleton.Evasione.PUBB_PALESE,
									ConstantsSingleton.Evasione.PUBB_PALESE_PRESELEZIONE);
		return filteredList;
	}
	
}