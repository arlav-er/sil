package it.eng.myportal.beans;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeRegioneDTO;
import it.eng.myportal.dtos.GenericDecodeDTO;
import it.eng.myportal.entity.decodifiche.DeAlbo;
import it.eng.myportal.entity.decodifiche.DeAreaFormazione;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.DePatentino;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeQualificaSrq;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.decodifiche.sil.DeAlboSil;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.decodifiche.sil.DePatentinoSil;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;
import it.eng.myportal.entity.home.decodifiche.DeBpMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneSolrHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeQualificaSrqHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.entity.home.decodifiche.nodto.DeAreaFormazioneHome;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Nuova classe per l'autocomplete su primefaces
 * 
 * @author pegoraro
 *
 */
@ManagedBean
@ViewScoped
public class AutoCompleteBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 535974785348770963L;

	protected static Log log = LogFactory.getLog(AutoCompleteBean.class);

	@EJB
	transient DeComuneSolrHome deComuneSolrHome;

	@EJB
	transient DeRegioneHome deRegioneHome;

	@EJB
	DeMansioneHome mansHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeMansioneMinHome mansMinHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	DePatentinoSilHome dePatentinoSilHome;

	@EJB
	DeAlboHome deAlboHome;

	@EJB
	DeAlboSilHome deAlboSilHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DeComuneHome comuneEjb;

	@EJB
	DeProvinciaHome provinciaEjb;

	@EJB
	DeTitoloHome titoloEjb;

	@EJB
	DeAreaFormazioneHome deAreaFormazioneHome;

	@EJB
	DeQualificaSrqHome qualificaSrqHome;

	@EJB
	private DeBpMansioneHome deBpMansione;

	@PostConstruct
	public void postConstruct() {

	}

	public AutoCompleteBean() {

	}

	public List<DeComune> completeComune(String startsWith) {
		log.info("Called completecomune: " + startsWith);
		return comuneEjb.findComuneAutocomplete(startsWith);
	}

	public List<DeProvincia> completeProvincia(String startsWith) {
		log.info("Called completeProvincia: " + startsWith);
		return provinciaEjb.findProvinceStartingWith(startsWith);
	}

	public List<DeMansione> completeMansione(String startsWith) {
		log.info("Called completemansione: " + startsWith);
		return mansHome.findMansioniStartingWith(startsWith);
	}

	public List<DeBpMansione> completeBpMansione(String startsWith) {
		log.info("Called completemansione: " + startsWith);
		return deBpMansione.findBySuggestionNoDTO(startsWith);
	}

	public String completeMansioneGroupBy(DeMansione deMansione) {
		if (deMansione != null) {
			if (deMansione.getPadre() != null) {
				return deMansione.getPadre().getDescrizione();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * Autocomplete della mansione ministeriale
	 * 
	 * @param startsWith
	 *            codMansioneMin
	 * @return
	 */
	public List<DeMansioneMin> completeMansioneMinisteriale(String startsWith) {
		log.info("Called completeMansione (ministeriale): " + startsWith);
		return mansMinHome.findMansioniStartingWith(startsWith);
	}

	/**
	 * Autocomplete per mansione Sil
	 * 
	 * @param startsWith
	 *            DE_MANSIONE.CODMANSIONE
	 * @return
	 */
	public List<DeMansioneMin> completeMansioneMinisterialeByCodMansione(String startsWith) {

		log.info("Called completeMansione (ministeriale): " + startsWith);
		return mansMinHome.findMansioniMinByCodMansione(startsWith);
	}

	public List<DeTitolo> completeTitolo(String startsWith) {
		log.info("Called completetitolo w/ key: " + startsWith);
		return titoloEjb.findBySuggestFigli(startsWith);
	}

	public String completeTitoloGroupBy(DeTitolo deTitolo) {
		if (deTitolo != null) {
			if (deTitolo.getPadre() != null) {
				return deTitolo.getPadre().getDescrizioneParlante();
			} else {
				return deTitolo.getDescrizioneParlante();
			}
		} else {
			return "";
		}
	}

	public List<DePatente> completePatente(String word) {
		return dePatenteHome.findBySuggestionNoDto(word);
	}

	public List<DePatenteSil> completePatenteSil(String word) {
		return dePatenteSilHome.findBySuggestionNoDto(word);
	}

	public List<DePatentino> completePatentino(String word) {
		return dePatentinoHome.findBySuggestionNoDto(word);
	}

	public List<DePatentinoSil> completePatentinoSil(String word) {
		return dePatentinoSilHome.findBySuggestionNoDto(word);
	}

	public List<DeAlbo> completeAlbo(String word) {
		return deAlboHome.findBySuggestionNoDto(word);
	}

	public List<DeAlboSil> completeAlboSil(String word) {
		return deAlboSilHome.findBySuggestionNoDto(word);
	}

	public List<DeContrattoSil> completeContrattoSil(String word) {
		return deContrattoSilHome.findBySuggestionNoDto(word);
	}

	public List<DeContrattoSil> completeContrattoSilFlagIdoTrue(String word) {
		return deContrattoSilHome.findBySuggestionFlagIdoTrueNoDto(word);
	}

	public List<DeOrarioSil> completeOrarioSil(String word) {
		return deOrarioSilHome.findBySuggestionNoDto(word);
	}

	public List<DeOrarioSil> completeOrarioSilFlagIdoTrue(String word) {
		return deOrarioSilHome.findBySuggestionFlagIdoTrueNoDto(word);
	}

	public List<DeAreaFormazione> completeDeAreaFormazione(String word) {
		return deAreaFormazioneHome.findBySuggestionNoDto(word);
	}

	public List<DeLingua> completeLingua(String word) {
		List<DeLingua> results = deLinguaHome.findBySuggest(word);
		return results;
	}

	public List<DeQualificaSrq> completeQualificaSrq(String startsWith) {
		log.info("Called completemansione: " + startsWith);
		return qualificaSrqHome.findBySuggestionNoDto(startsWith);
	}

	public String completeQualificaSrqGroupBy(DeQualificaSrq deQualificaSrq) {
		if (deQualificaSrq != null) {
			if (deQualificaSrq.getCodPadre() != null) {
				return qualificaSrqHome.findById(deQualificaSrq.getCodPadre()).getDescrizione();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * ATTENZIONE!!!!
	 * 
	 * ricerca offerte da SOLR il codice decodifica è la stessa descrizione in quanto su SOLR viene effettuato la
	 * ricerca sul TESTO e non sul CODICE
	 * 
	 * @param query
	 * @return
	 * 
	 *         pegoraro: lo depreco perchè non ci sono riferimenti
	 */
	@Deprecated
	public List<GenericDecodeDTO> completeComuniSolrAndRegione(String query) {
		List<DeComuneDTO> deComuneList = deComuneSolrHome.findBySuggestion(query);
		List<DeRegioneDTO> deRegioneList = deRegioneHome.findBySuggestionInitCap(query);
		List<GenericDecodeDTO> genericDecodeDTOList = new ArrayList<GenericDecodeDTO>();

		for (Iterator<DeComuneDTO> iterator = deComuneList.iterator(); iterator.hasNext();) {
			DeComuneDTO deComuneDTO = (DeComuneDTO) iterator.next();
			GenericDecodeDTO genericDecodDTO = new GenericDecodeDTO();
			genericDecodDTO.setDescrizione(deComuneDTO.getDescrizione());
			genericDecodDTO.setId(deComuneDTO.getDescrizione());
			genericDecodeDTOList.add(genericDecodDTO);
		}

		for (Iterator<DeRegioneDTO> iterator = deRegioneList.iterator(); iterator.hasNext();) {
			DeRegioneDTO deRegioneDTO = (DeRegioneDTO) iterator.next();
			GenericDecodeDTO genericDecodDTO = new GenericDecodeDTO();
			genericDecodDTO.setDescrizione(deRegioneDTO.getDescrizione());
			genericDecodDTO.setId(deRegioneDTO.getDescrizione());
			genericDecodeDTOList.add(genericDecodDTO);
		}

		return genericDecodeDTOList;
	}

}
