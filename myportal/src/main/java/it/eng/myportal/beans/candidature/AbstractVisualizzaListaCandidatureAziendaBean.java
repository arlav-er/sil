package it.eng.myportal.beans.candidature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AcVisualizzaCandidaturaDTO;
import it.eng.myportal.dtos.DeIdoneitaCandidaturaDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.entity.CodValutazioneEnum;
import it.eng.myportal.entity.decodifiche.DeIdoneitaCandidatura;
import it.eng.myportal.entity.home.decodifiche.DeIdoneitaCandidaturaHome;

/**
 * BackingBean della pagina di visualizzazione dell'elenco delle candidature
 * 
 * @author Enrico D'Angelo
 * 
 */
public abstract class AbstractVisualizzaListaCandidatureAziendaBean extends
		AbstractBaseBean {
	/**
	 * Elenco delle candidature, filtrate secondo i criteri indicati, relative
	 * ad una data vacancy
	 */
	private List<AcVisualizzaCandidaturaDTO> candidature;
	/**
	 * Elenco delle candidature relative ad una data vacancy
	 */
	private List<AcVisualizzaCandidaturaDTO> allCandidature;

	@EJB
	private DeIdoneitaCandidaturaHome deIdoneitaCandidaturaHome;

	private List<DeIdoneitaCandidatura> livelliValutazione;
	private List<DeIdoneitaCandidatura> livelliValutazioneSelezionati;
	
	private List<SelectItem> livelliValutazioneNoIdo;
	private List<IDecode> livelliValutazioneSelezionatiNoIdo;

	private List<String> livelliValutazioneComplessiva;
	private List<String> livelliValutazioneComplessivaSelezionati;
	private String tipoValutazioneComplessiva;
	
	private int id;

	public List<DeIdoneitaCandidatura> getLivelliValutazione() {
		return livelliValutazione;
	}

	public void setLivelliValutazione(List<DeIdoneitaCandidatura> livelliValutazione) {
		this.livelliValutazione = livelliValutazione;
	}

	public List<DeIdoneitaCandidatura> getLivelliValutazioneSelezionati() {
		return livelliValutazioneSelezionati;
	}

	public void setLivelliValutazioneSelezionati(
			List<DeIdoneitaCandidatura> livelliValutazioneSelezionati) {
		this.livelliValutazioneSelezionati = livelliValutazioneSelezionati;
	}
	private int numTotalCandidature;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			if (session.isAzienda()) {
				Map<String, String> map = FacesContext.getCurrentInstance()
						.getExternalContext().getRequestParameterMap();
				/*
				 * l'id passato come parametro dipende dal bean concreto che
				 * implementa questo bean astratto, puo' trattarsi dell'id di
				 * una vacancy o dell'id di un'azienda
				 */
				String idString = map.get("id");
				id = Integer.parseInt(idString);

				allCandidature = getListaCandidature(id);
				candidature = allCandidature;

				livelliValutazione = deIdoneitaCandidaturaHome.findAll();
				livelliValutazioneSelezionati = new ArrayList<DeIdoneitaCandidatura>();
				setLivelliValutazioneComplessiva(popolaValutazioneComplessiva());
				livelliValutazioneComplessivaSelezionati = new ArrayList<String>();
				log.debug("Costruito il Bean per la visualizzazione dell'elenco delle candidature!");
				CheckSessione();
				numTotalCandidature = 0;
				if (allCandidature != null && !allCandidature.isEmpty()) {
					setNumTotalCandidature(allCandidature.size());
				}
				
				if(!utils.isRER()) {
					livelliValutazioneNoIdo = deIdoneitaCandidaturaHome.getListItems(false, "descrizione");
					livelliValutazioneSelezionatiNoIdo = new ArrayList<IDecode>();					
				}

			} else {
				allCandidature = candidature = null;
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		} catch (NumberFormatException nfe) {
			addErrorMessage("data.conversion_error", nfe);
			redirectHome();
		}
	}
	
	public List<String> popolaValutazioneComplessiva() {
		List<String> retStr = new ArrayList<String>();
		CodValutazioneEnum[] codiciValutazione = CodValutazioneEnum.values();
		for (CodValutazioneEnum codValutazioneEnum : codiciValutazione) {
			retStr.add(decodeValutazioneEnum(codValutazioneEnum.getValue()));
		}
		return retStr;
	}

	/**
	 * Metodo astratto che recupera la lista di candidature da visualizzare.
	 * Guardare nei bean concreti per le implementazioni.
	 * 
	 * @param id vacancyID o aziendaInfoID
	 * @return lista delle candidature da visualizzare
	 */
	protected abstract List<AcVisualizzaCandidaturaDTO> getListaCandidature(
			Integer id);

	/**
	 * Filtra le candidature da visualizzare in base ai filtri definiti
	 */
	public void filtraCandidature() {
		
		
		if (((livelliValutazioneSelezionati == null)
				|| (livelliValutazioneSelezionati.size() == 0)) && (tipoValutazioneComplessiva == null || ( tipoValutazioneComplessiva != null && tipoValutazioneComplessiva.isEmpty())) ) { // entrambi i filtri sono a null
			candidature = allCandidature;
		} else {
			candidature = new ArrayList<AcVisualizzaCandidaturaDTO>();
			
			if (((livelliValutazioneSelezionati != null) && (livelliValutazioneSelezionati.size() != 0))) {
				/* valutare anche l'altro fltro */
				
               // candidature = new ArrayList<AcVisualizzaCandidaturaDTO>();
            	for (AcVisualizzaCandidaturaDTO candidatura : allCandidature) {
					DeIdoneitaCandidaturaDTO dtoMerda = candidatura.getDeIdoneitaCandidaturaDTO();
					DeIdoneitaCandidatura deIdo = deIdoneitaCandidaturaHome.fromDTO(dtoMerda);
					
					if (livelliValutazioneSelezionati.contains(deIdo)) {
					  if((tipoValutazioneComplessiva != null && tipoValutazioneComplessiva.isEmpty()) ||tipoValutazioneComplessiva == null) {
						    candidature.add(candidatura);
					  }else {
						 if(candidatura.getCodValutazioneAcCandidatura().getValue()
							.equalsIgnoreCase(tipoValutazioneComplessiva)) {
							candidature.add(candidatura); 
						 }						  
					  }					
					}
				}
              
			} else { // csao in cui i livelli non sono stati selezionati 
				if ((tipoValutazioneComplessiva != null && !tipoValutazioneComplessiva.isEmpty())) {
					for (AcVisualizzaCandidaturaDTO candidatura : allCandidature) {
						if (candidatura.getCodValutazioneAcCandidatura().getValue()
								.equalsIgnoreCase(tipoValutazioneComplessiva))
							candidature.add(candidatura);
					}
				}
			}
		}
	}
	
	public void filtraCandidatureNoIdo() {
		/*
		 * Se non ci sono filtri selezionati visualizzo tutte le candidature
		 */
		if ((livelliValutazioneSelezionatiNoIdo == null)
				|| (livelliValutazioneSelezionatiNoIdo.size() == 0)) {
			candidature = allCandidature;
		} else {
			candidature = new ArrayList<AcVisualizzaCandidaturaDTO>();

			for (AcVisualizzaCandidaturaDTO candidatura : allCandidature) {

				/*
				 * Se una candidatura rispetta almeno uno dei filtri definiti
				 * allora la aggiungo nell'elenco di quelle visualizzate
				 */
				for (IDecode filtro : livelliValutazioneSelezionatiNoIdo) {
					if (candidatura.getDeIdoneitaCandidaturaDTO().getId()
							.equals(filtro.getId())) {
						candidature.add(candidatura);
					}
				}
			}
		}
	}

	public void CheckSessione(){}

	public void filtraCandidatureNewVersion() {

		if (((livelliValutazioneSelezionati == null) || (livelliValutazioneSelezionati.size() == 0))
				&& (livelliValutazioneComplessivaSelezionati == null
						|| (livelliValutazioneComplessivaSelezionati.size() == 0))) { // entrambi i filtri sono a null
			candidature = allCandidature;
		} else {
			candidature = new ArrayList<AcVisualizzaCandidaturaDTO>();

			if (((livelliValutazioneSelezionati != null) && (livelliValutazioneSelezionati.size() != 0))) {
				/* valutare anche l'altro fltro */

				for (AcVisualizzaCandidaturaDTO candidatura : allCandidature) {
					DeIdoneitaCandidaturaDTO dtoMerda = candidatura.getDeIdoneitaCandidaturaDTO();
					DeIdoneitaCandidatura deIdo = deIdoneitaCandidaturaHome.fromDTO(dtoMerda);

					if (livelliValutazioneSelezionati.contains(deIdo)) {

						if (livelliValutazioneComplessivaSelezionati == null
								|| (livelliValutazioneComplessivaSelezionati.size() == 0)) {
							candidature.add(candidatura);
						} else {
							if ((livelliValutazioneComplessivaSelezionati != null
									&& livelliValutazioneComplessivaSelezionati.size() != 0)) {
								String codCorrenteStr = "";
								String codCorrenteStrDec = "";
								if (candidatura.getCodValutazioneAcCandidatura() != null) {
									codCorrenteStr = candidatura.getCodValutazioneAcCandidatura().getValue();
									codCorrenteStrDec = decodeValutazioneEnum(codCorrenteStr);

									if (livelliValutazioneComplessivaSelezionati.contains(codCorrenteStrDec)) {
										candidature.add(candidatura);
									}
								}
							}
						}
					}
				}

			} else { // csao in cui i livelli non sono stati selezionati

				if ((livelliValutazioneComplessivaSelezionati != null
						&& livelliValutazioneComplessivaSelezionati.size() != 0)) {

					for (AcVisualizzaCandidaturaDTO candidatura : allCandidature) {

						String codCorrenteStr = "";
						String codCorrenteStrDec = "";
						if (candidatura.getCodValutazioneAcCandidatura() != null) {
							codCorrenteStr = candidatura.getCodValutazioneAcCandidatura().getValue();
							codCorrenteStrDec = decodeValutazioneEnum(codCorrenteStr);

							if (livelliValutazioneComplessivaSelezionati.contains(codCorrenteStrDec)) {
								candidature.add(candidatura);
							}
						}
					}
				}
			}
		}
	}

      public String decodeValutazioneEnum(String daDecod) {
    	  String codCorrenteStrDec = "";
    	  if(daDecod != null) {
			switch (daDecod) {
			case "L0":
    			codCorrenteStrDec = "Non idoneo";
    			break;
    		case "L1":
    			codCorrenteStrDec = "1";
    			break;
    		case "L2":
    			codCorrenteStrDec = "2";
    			break;
    		case "L3":
    			codCorrenteStrDec = "3";
    			break;
    		case "L4":
    			codCorrenteStrDec = "4";
    			break;
    		default:
    			break;
    		}
    	  }
    	  return codCorrenteStrDec;
      }
	
	public List<AcVisualizzaCandidaturaDTO> getCandidature() {
		return candidature;
	}

	public void setCandidature(List<AcVisualizzaCandidaturaDTO> candidature) {
		this.candidature = candidature;
	}

	public int getNumTotalCandidature() {
		return numTotalCandidature;
	}

	public void setNumTotalCandidature(int numTotalCandidature) {
		this.numTotalCandidature = numTotalCandidature;
	}
	
	public String getTipoValutazioneComplessiva() {
		return tipoValutazioneComplessiva;
	}

	public void setTipoValutazioneComplessiva(String tipoValutazioneComplessiva) {
		this.tipoValutazioneComplessiva = tipoValutazioneComplessiva;
	}

	public List<String> getLivelliValutazioneComplessiva() {
		return livelliValutazioneComplessiva;
	}

	public void setLivelliValutazioneComplessiva(List<String> livelliValutazioneComplessiva) {
		this.livelliValutazioneComplessiva = livelliValutazioneComplessiva;
	}


	
	public List<String> getLivelliValutazioneComplessivaSelezionati() {
		return livelliValutazioneComplessivaSelezionati;
	}

	public void setLivelliValutazioneComplessivaSelezionati(List<String> livelliValutazioneComplessivaSelezionati) {
		this.livelliValutazioneComplessivaSelezionati = livelliValutazioneComplessivaSelezionati;
	}
	
	
	public List<IDecode> getLivelliValutazioneSelezionatiNoIdo() {
		return livelliValutazioneSelezionatiNoIdo;
	}

	public void setLivelliValutazioneSelezionatiNoIdo(
			List<IDecode> livelliValutazioneSelezionatiNoIdo) {
		this.livelliValutazioneSelezionatiNoIdo = livelliValutazioneSelezionatiNoIdo;
	}
	
	public List<SelectItem> getLivelliValutazioneNoIdo() {
		return livelliValutazioneNoIdo;
	}

	public void setLivelliValutazioneNoIdo(List<SelectItem> livelliValutazioneNoIdo) {
		this.livelliValutazioneNoIdo = livelliValutazioneNoIdo;
	}
}
