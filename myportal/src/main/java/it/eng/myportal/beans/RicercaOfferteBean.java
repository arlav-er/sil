package it.eng.myportal.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import it.eng.myportal.dtos.AziendaSessionDTO;
import it.eng.myportal.dtos.OfferteMiniDTO;
import it.eng.myportal.dtos.RicercaOfferteDTO;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.enums.TipoRicercaOfferte;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean
@ViewScoped
public class RicercaOfferteBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = 350167739197076788L;

	private static final String PARAMETRI_RICERCA = "parametriRicerca";

	private boolean ricercaEseguita;

	private static Log log = LogFactory.getLog(RicercaOfferteBean.class);

	@EJB
	private VaDatiVacancyHome vaDatiVacancyHome;
	@EJB
	private DeProvenienzaHome deProvenienzaHome;

	private List<OfferteMiniDTO> risultato;

	private RicercaOfferteDTO parametriRicerca;

	private List<SelectItem> tipiRicerca;
	private List<SelectItem> tipoStato;
	private List<DeProvenienza> provenienza;

	// private List<VaDatiVacancyDTO> vacancies;
	private Integer selectedVacancyId;
	private String selectedVacancyDescrizione;
	private String fullCopyVacancyText;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		log.debug("Costruito il Bean per Informazioni Offerte!");
		try {
			if (session.isAzienda()) {
				if (parametriRicerca == null) {
					parametriRicerca = new RicercaOfferteDTO();
					if(beanParamsSess != null) {
						if (beanParamsSess.containsKey(PARAMETRI_RICERCA)) {
							parametriRicerca = (RicercaOfferteDTO) beanParamsSess.get(PARAMETRI_RICERCA);
							search();
						}
					}
				}

				if (risultato == null) {
					risultato = new ArrayList<OfferteMiniDTO>();
				}
				provenienza =deProvenienzaHome.findAll();
				tipiRicerca = TipoRicercaOfferte.asSelectItems();
				if (ConstantsSingleton.COD_REGIONE != ConstantsSingleton.COD_REGIONE_TRENTO) {
					for (ListIterator<SelectItem> iter = tipiRicerca.listIterator(); iter.hasNext();) {
						SelectItem current = iter.next();
						if (TipoRicercaOfferte.LAV.getLabel().equals(current.getLabel())
								|| TipoRicercaOfferte.PUB.getLabel().equals(current.getLabel())
								|| TipoRicercaOfferte.ARC.getLabel().equals(current.getLabel())) {
							iter.remove();
						}
					}
				}
				
				if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
					tipoStato = CodStatoVacancyEnum.asSelectItems();
					for (ListIterator<SelectItem> iter = tipoStato.listIterator(); iter.hasNext();) {
						SelectItem current = iter.next();
						if (!CodStatoVacancyEnum.LAV.getDescrizione().equals(current.getLabel())
								&& !CodStatoVacancyEnum.PUB.getDescrizione().equals(current.getLabel())
								&& !CodStatoVacancyEnum.ARC.getDescrizione().equals(current.getLabel())) {
							iter.remove();
						}
					}
				}
				log.debug("Costruito il Bean per cercare le offerte.");
			} else {
				log.warn("Tentativo di accedere alla ricerca delle offerte fallita.");
				redirectHome();
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	public void search() {
		risultato = new ArrayList<OfferteMiniDTO>();

		putParamsIntoSession();
		AziendaSessionDTO azienda = session.getConnectedAzienda();

		risultato = vaDatiVacancyHome.getListaOfferte(parametriRicerca, azienda.getIdAziendaInfo());

		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {		
			risultato.addAll(vaDatiVacancyHome.getListaOffertePalesi(parametriRicerca, azienda.getIdAziendaInfo()));
		/* altra query che estre pure le palesi con DFD e stare attenti nella lista finale che per queste offerte che hanno provenienza SIL  
		 * non devono essere eliminate ma devono solo essere copiate , visualizzate con la lista delle candidature
		 * */
		}
		ricercaEseguita = true;
	}

	public List<OfferteMiniDTO> getRisultato() {
		return risultato;
	}

	public void setRisultato(List<OfferteMiniDTO> results) {
		this.risultato = results;
	}

	public RicercaOfferteDTO getParametriRicerca() {
		return parametriRicerca;
	}

	public void setParametriRicerca(RicercaOfferteDTO parametriRicerca) {
		this.parametriRicerca = parametriRicerca;
	}

	public List<SelectItem> getTipiRicerca() {
		return tipiRicerca;
	}

	public void setTipiRicerca(List<SelectItem> tipiRicerca) {
		this.tipiRicerca = tipiRicerca;
	}
	
	public List<SelectItem> getTipoStato() {
		return tipoStato;
	}

	public void setTipoStato(List<SelectItem> tipoStato) {
		this.tipoStato = tipoStato;
	}

	public List<DeProvenienza> getProvenienza() {
		return provenienza;
	}

	public void setProvenienza(List<DeProvenienza> provenienza) {
		this.provenienza = provenienza;
	}

	public boolean isRicercaEseguita() {
		return ricercaEseguita;
	}

	public void setRicercaEseguita(boolean ricercaEseguita) {
		this.ricercaEseguita = ricercaEseguita;
	}

	public void deleteVacancy() {
		try {
			Map<String, String> map = getRequestParameterMap();
			Integer deleteId = null;

			for (Entry<String, String> entry : map.entrySet()) {
				if (entry.getKey().endsWith("id_delete")) {
					deleteId = Integer.parseInt(entry.getValue());
				}
			}

			if (deleteId != null) {
				if (vaDatiVacancyHome.isProprietary(deleteId, getSession().getPrincipalId())) {
					vaDatiVacancyHome.removeCascadeById(deleteId, session.getPrincipalId());
					addInfoMessage("vacancy.deleted");
					search();
				} else {
					addErrorMessage("vacancy.wrongPermission");
				}
			}
		} catch (EJBException e) {
			addErrorMessage("vacancy.error_deleting", e);
		}
	}

	public void copyVacancyNew() {
		try {

			if (selectedVacancyId != null && fullCopyVacancyText != null) {
				if (vaDatiVacancyHome.isProprietary(selectedVacancyId, getSession().getPrincipalId())) {
					vaDatiVacancyHome.copyById(getSession().getPrincipalId(), selectedVacancyId, fullCopyVacancyText);

					addInfoMessage("vacancy.copied");
					search();
				} else {
					addErrorMessage("vacancy.wrongPermission");
				}
			}
		} catch (EJBException e) {
			addErrorMessage("vacancy.error_copying", e);
		}
	}

	public void deleteVacancyNew() {
		try {
			if (selectedVacancyId != null) {
				if (vaDatiVacancyHome.isProprietary(selectedVacancyId, getSession().getPrincipalId())) {
					vaDatiVacancyHome.removeCascadeById(selectedVacancyId, session.getPrincipalId());
					addInfoMessage("vacancy.deleted");
					search();
				} else {
					addErrorMessage("vacancy.wrongPermission");
				}
			}
		} catch (EJBException e) {
			addErrorMessage("vacancy.error_deleting", e);
		}
	}

	public void initCopyVacancy(Integer id, String copyDescrizione) {
		this.selectedVacancyId = id;
		this.selectedVacancyDescrizione = copyDescrizione;
		this.openVacancyCopyModal();
	}

	/**
	 * Per quanto riguarda invece le vacancy con provenenza myportal, una vacancy può essere modificata solo in
	 * determinate condizioni, che sono le stesse usate per il pulsante di modifica, quindi: lo stato delle vacancy deve
	 * essere LAV o PUB e la vacancy non deve essere scaduta, quindi dt_scadenza_pubblicazione >= sysdate.
	 * 
	 * Solo se queste condizioni sono vere, in caso di vacancy con provenienza my portal, bisogna mostrare in questa
	 * lista l'icona di modifica
	 * 
	 * @param offerta
	 * @return
	 */
	public boolean renderedEdit(OfferteMiniDTO offerta) {
		return vaDatiVacancyHome.isModificaRendered(offerta.getIdVaDatiVacancy(), getSession());
	}
	
	public boolean renderedCopia(OfferteMiniDTO offerta) {

		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			if (!offerta.getFlgIdo()) {
				return false;
			}
		}

		return true;
	}

	public Integer getSelectedVacancyId() {
		return selectedVacancyId;
	}

	public void setSelectedVacancyId(Integer selectedVacancyId) {
		this.selectedVacancyId = selectedVacancyId;
	}

	public String getSelectedVacancyDescrizione() {
		return selectedVacancyDescrizione;
	}

	public void setSelectedVacancyDescrizione(String selectedVacancyDescrizione) {
		this.selectedVacancyDescrizione = selectedVacancyDescrizione;
	}

	public String getFullCopyVacancyText() {
		return fullCopyVacancyText;
	}

	public void setFullCopyVacancyText(String fullCopyVacanzaText) {
		this.fullCopyVacancyText = fullCopyVacanzaText;
	}

	public void openVacancyCopyModal() {
		fullCopyVacancyText = selectedVacancyDescrizione;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('vacancies_copyWV').show();");
	}

	public void openD() {
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('vacancies_copyWV').hide();");
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(PARAMETRI_RICERCA, parametriRicerca);
		return ret;
	}

	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
			parametriRicerca = (RicercaOfferteDTO) restoreParams.get(PARAMETRI_RICERCA);
			search();
	};
}