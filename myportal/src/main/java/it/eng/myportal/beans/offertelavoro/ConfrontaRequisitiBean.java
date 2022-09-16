package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AbstractLinguaDTO;
import it.eng.myportal.dtos.ConfrontaRequisitiDTO;
import it.eng.myportal.dtos.CvAltreInfoDTO;
import it.eng.myportal.dtos.CvVisualizzaDTO;
import it.eng.myportal.dtos.DeTrasfertaDTO;
import it.eng.myportal.dtos.VaAltreInfoDTO;
import it.eng.myportal.dtos.VaLinguaDTO;
import it.eng.myportal.dtos.VaVisualizzaDTO;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

/**
 * BackingBean della pagina che permette di confrontare requisiti tra le offerte
 * di lavoro ed il cv selezionato
 * 
 * TODO è possbile averequalche commento all'interno di questa classe? Non si
 * capisce granchè. correggere l'errore di ciclocomplessità elevata.
 * 
 * TODO tutta la logica di costruzione della pagina è effettuata nel bean
 * utilizzando i DTO, bisogna spostare tutta la logica negli EJB e lavorare con
 * le entity
 * 
 * 
 * @author Girotti
 * 
 */
@ManagedBean
@ViewScoped
public class ConfrontaRequisitiBean extends AbstractBaseBean implements Serializable {

	private static final String Y = "Y";
	/**
	 * 
	 */
	private static final long serialVersionUID = 8087101704466479493L;
	private static final String IMG_WARNING = "/resources/images/warning.png";
	private static final String IMG_OK = "/resources/images/ok.png";
	private static final String IMG_NO = "/resources/images/no.png";
	private static final String MOTOMUNITO = "Motomunito";
	private static final String AUTOMUNITO = "Automunito";
	private static final String TRASFERTE = "Trasferte";
	private static final String PARLATA = "parlata";
	private static final String SCRITTA = "scritta";
	private static final String LETTA = "letta";
	private static final String LINGUA = "lingua";

	private transient ConfrontaRequisitiDTO data;
	private VaVisualizzaDTO vaVisualizzaDTO;
	private CvVisualizzaDTO cvVisualizzaDTO;

	@EJB
	transient CvDatiPersonaliHome cvDatiPersonaliHome;
	
	@EJB
	transient VaDatiVacancyHome vaDatiVacancyHome;

	/**
	 * recupera l'elenco delle Vacancy e dei Template dal DB ed inizializza il
	 * Bean
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		retrieveData();
		log.debug("Costruito il Bean per visualizzare l'elenco dei requisiti delle Vacancy vs il curricula dell'utente.");
	}

	protected void retrieveData() {
		Map<String, String> map = getRequestParameterMap();
		try {
			String idVaDatiVacancyString = map.get("idVacancy");
			String idCvDatiPersonaliString = map.get("idCurricula");
			if (StringUtils.isNotBlank(idCvDatiPersonaliString) && StringUtils.isNotBlank(idVaDatiVacancyString)) {
				Integer idVaDatiVacancy = Integer.parseInt(idVaDatiVacancyString);
				Integer idCvDatiPersonali = Integer.parseInt(idCvDatiPersonaliString);
				Integer idPfPrincipal = session.getPrincipalId();

				vaVisualizzaDTO = vaDatiVacancyHome.findVisualizzaDTOById(idVaDatiVacancy, idPfPrincipal);
				cvVisualizzaDTO = cvDatiPersonaliHome.findVisualizzaDTOById(idCvDatiPersonali, idPfPrincipal);

				data = new ConfrontaRequisitiDTO(vaVisualizzaDTO, cvVisualizzaDTO);
			} else {
				throw new RuntimeException("Id non corretto");
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("Id non corretto");
		}
	}

	public ConfrontaRequisitiDTO getData() {
		return data;
	}

	public void setData(ConfrontaRequisitiDTO data) {
		this.data = data;
	}

	public String getImgAltreInfo(String key, CvAltreInfoDTO cvAltreInfo, VaAltreInfoDTO vaAltreInfo) {
		if (vaAltreInfo == null) {
			return IMG_WARNING;
		}
		if (cvAltreInfo == null) {
			return IMG_NO;
		}

		if (AUTOMUNITO.equalsIgnoreCase(key)) {
			if (cvAltreInfo.getAutomunito()) {
				return IMG_OK;
			} else {
				// cv NON AUTOMUNITO
				if (Y.equalsIgnoreCase(vaAltreInfo.getAutomunito())) {
					// è RICHIESTO -> NO
					return IMG_NO;
				} else {
					// non è RICHIESTO -> OK
					return IMG_OK;
				}
			}
		}
		if (MOTOMUNITO.equalsIgnoreCase(key)) {
			if (cvAltreInfo.getMotomunito()) {
				return IMG_OK;
			} else {
				// cv NON MOTOMUNITO
				if (Y.equalsIgnoreCase(vaAltreInfo.getMotomunito())) {
					// è RICHIESTO -> NO
					return IMG_NO;
				} else {
					// non è RICHIESTO -> OK
					return IMG_OK;
				}
			}
		}

		if (TRASFERTE.equalsIgnoreCase(key)) {
			DeTrasfertaDTO cvTipoTrasferta = cvAltreInfo.getTipoTrasferta();
			
			boolean isVaTrasfReq = "S".equalsIgnoreCase(vaAltreInfo.getFuorisede());
			// trasferta non richiesta - metto ok
			if (!isVaTrasfReq) {
				return IMG_OK;
			}

			// trasferta richiesta - cv non specificato - metto KO
			if ((cvTipoTrasferta == null || StringUtils.isBlank(cvTipoTrasferta.getId()))) {
				return IMG_NO;
			}
			
			try {
				int trasfCv = Integer.parseInt(cvTipoTrasferta.getId(), 10);
				// 2 - Ambito Provinciale è il minimo livello di trasferta possibile
				// se isVaTrasfReq=true -> Risponde NO se nel cv è specificato No Trasferte 
				int trasfVa = (isVaTrasfReq) ? 2 : 0;
				if (trasfCv < trasfVa) {
					return IMG_NO;
				} else {
					return IMG_OK;
				}
			} catch (Exception e) {
				// può accadere se l'oggetto trasferta è nullo o ha un id nullo
				return IMG_NO;
			}
		}

		return "";
	}

	public String getImgLingua(String key, AbstractLinguaDTO cvLingua, VaLinguaDTO vaLingua) {
		Map<String, String> mapImg = getImmaginiConfrontoLingua(cvLingua, vaLingua);

		if (key.endsWith(LETTA)) {
			return mapImg.get(LETTA);
		}
		if (key.endsWith(SCRITTA)) {
			return mapImg.get(SCRITTA);
		}
		if (key.endsWith(PARLATA)) {
			return mapImg.get(PARLATA);
		}

		return mapImg.get(LINGUA);
	}

	private Map<String, String> getImmaginiConfrontoLingua(AbstractLinguaDTO cvLingua, VaLinguaDTO vaLingua) {
		Map<String, String> map = new HashMap<String, String>();

		if (cvLingua == null && vaLingua != null) {
			map.put(LINGUA, IMG_NO);
			map.put(LETTA, IMG_NO);
			map.put(SCRITTA, IMG_NO);
			map.put(PARLATA, IMG_NO);
		}

		if (cvLingua != null && vaLingua == null) {

			map.put(LINGUA, IMG_WARNING);
			map.put(LETTA, IMG_WARNING);
			map.put(SCRITTA, IMG_WARNING);
			map.put(PARLATA, IMG_WARNING);
		}

		if (cvLingua != null && vaLingua != null) {

			map.put(LINGUA, IMG_OK);
			// nel cv è madrelingua - ok
			if (cvLingua.isMadrelingua()) {
				map.put(LETTA, IMG_OK);
				map.put(SCRITTA, IMG_OK);
				map.put(PARLATA, IMG_OK);
				// nella vacancy è madrelingua e nel cv no - warning? no?
			} else if (vaLingua.isMadrelingua()) {
				map.put(LETTA, IMG_WARNING);
				map.put(SCRITTA, IMG_WARNING);
				map.put(PARLATA, IMG_WARNING);
				// nessuno madrelingua -> confronto i livelli
			} else {

				confrontoLivelliLingua(cvLingua, vaLingua, map, IMG_OK, IMG_NO);
			}
		}
		return map;
	}

	private void confrontoLivelliLingua(AbstractLinguaDTO cvLingua, VaLinguaDTO vaLingua, Map<String, String> map,
			String ok, String no) {
		int cvGradoLetto = cvLingua.getNumGradoLinguaLetto();
		int cvGradoScritto = cvLingua.getNumGradoLinguaScritto();
		int cvGradoParlato = cvLingua.getNumGradoLinguaParlato();

		int vaGradoLetto = vaLingua.getNumGradoLinguaLetto();
		int vaGradoScritto = vaLingua.getNumGradoLinguaScritto();
		int vaGradoParlato = vaLingua.getNumGradoLinguaParlato();

		if (cvGradoLetto >= vaGradoLetto) {
			map.put(LETTA, ok);

		} else {
			map.put(LETTA, no);
		}

		if (cvGradoScritto >= vaGradoScritto) {
			map.put(SCRITTA, ok);

		} else {
			map.put(SCRITTA, no);
		}

		if (cvGradoParlato >= vaGradoParlato) {
			map.put(PARLATA, ok);
		} else {
			map.put(PARLATA, no);
		}
	}
}
