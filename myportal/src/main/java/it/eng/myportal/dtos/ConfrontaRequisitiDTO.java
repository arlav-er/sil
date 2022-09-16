/**
 * 
 */
package it.eng.myportal.dtos;

import it.eng.myportal.beans.offertelavoro.VaCvContainerDTO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * TODO il metodo getVaLivelloLingua e getCvLivelloLingua vengono richiamati nell'xhtml e non va bene che eseguano delle operazioni!
 * 
 * @author Enrico D'Angelo
 * 
 */
public class ConfrontaRequisitiDTO {


	
	private static final String INDIFFERENTE = "Indifferente";
	private static final String NO = "No";
	private static final String SI = "Sì";
	private static final String Y = "Y";
	
	private static final String AUTOMUNITO = "Automunito";
	private static final String MOTOMUNITO = "Motomunito";
	private static final String TRASFERTE = "Trasferte";
	private static final String SEPARATOR = ":";
	private static final String PARLATA = "parlata";
	private static final String SCRITTA = "scritta";
	private static final String LETTA = "letta";
	private VaVisualizzaDTO vaVisualizzaDTO;
	private CvVisualizzaDTO cvVisualizzaDTO;
	private VaAltreInfoDTO vaAltreInfoDTO;
	private CvAltreInfoDTO cvAltreInfoDTO;
	private ArrayList<String> keyAltreInfo;
	private Map<String, VaCvContainerDTO<VaPatenteDTO, CvPatenteDTO>> mapPatenti;
	private Map<String, VaCvContainerDTO<VaPatentinoDTO, CvPatentinoDTO>> mapPatentini;
	private Map<String, VaCvContainerDTO<VaAlboDTO, CvAlboDTO>> mapAlbo;
	private Map<String, VaCvContainerDTO<VaAgevolazioneDTO, CvAgevolazioneDTO>> mapAgevolazioni;
	private Map<String, VaCvContainerDTO<VaIstruzioneDTO, CvIstruzioneDTO>> mapIstruzione;
	private Map<String, VaCvContainerDTO<VaLinguaDTO, CvLinguaDTO>> mapLingua;
	private ArrayList<String> dummyList;

	public ConfrontaRequisitiDTO(VaVisualizzaDTO vaVisualizzaDTO, CvVisualizzaDTO cvVisualizzaDTO) {
		this.vaVisualizzaDTO = vaVisualizzaDTO;
		this.cvVisualizzaDTO = cvVisualizzaDTO;

		vaAltreInfoDTO = vaVisualizzaDTO.getVaAltreInfoDTO();
		cvAltreInfoDTO = cvVisualizzaDTO.getCvAltreInfoDTO();
		mapPatenti = popolaMap(vaVisualizzaDTO.getListaVaPatenteDTO(), cvVisualizzaDTO.getListaCvPatentiDTO());
		mapPatentini = popolaMap(vaVisualizzaDTO.getListaVaPatentinoDTO(), cvVisualizzaDTO.getListaCvPatentiniDTO());
		mapAlbo = popolaMap(vaVisualizzaDTO.getListaVaAlboDTO(), cvVisualizzaDTO.getListaCvAlboDTO());
		mapAgevolazioni = popolaMap(vaVisualizzaDTO.getListaVaAgevolazioneDTO(),
				cvVisualizzaDTO.getListaCvAgevolazioni());
		mapIstruzione = popolaMap(vaVisualizzaDTO.getListaVaIstruzioneDTO(), cvVisualizzaDTO.getListaCvIstruzioneDTO());
		mapLingua = popolaMap(vaVisualizzaDTO.getListaVaLinguaDTO(), cvVisualizzaDTO.getListaCvLinguaDTO());
		
		popolaChiavi();
	}

	private <VA extends IHasUniqueValue, CV extends IHasUniqueValue> Map<String, VaCvContainerDTO<VA, CV>> popolaMap(
			List<VA> vaList, List<CV> cvList) {
		Map<String, VaCvContainerDTO<VA, CV>> map = new TreeMap<String, VaCvContainerDTO<VA, CV>>();

		if (cvList != null) {
			for (CV cvEl : cvList) {
				VaCvContainerDTO<VA, CV> dtoCont = new VaCvContainerDTO<VA, CV>();
				dtoCont.setCvDto(cvEl);
				map.put(cvEl.getUniqueValue(), dtoCont);
			}
		}
		if (vaList != null) {
			for (VA vaEl : vaList) {
				VaCvContainerDTO<VA, CV> dtoCont = map.get(vaEl.getUniqueValue());
				if (dtoCont == null) {
					dtoCont = new VaCvContainerDTO<VA, CV>();
				}
				dtoCont.setVaDto(vaEl);
				map.put(vaEl.getUniqueValue(), dtoCont);
			}
		}

		return map;
	}

	private void popolaChiavi() {
		// queste sono casi particolari, per le altre mappe basta chiamere
		// keyset() sull amappa
		ArrayList<String> keySet = new ArrayList<String>(mapLingua.keySet());
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next();
			VaCvContainerDTO<VaLinguaDTO, CvLinguaDTO> cont = mapLingua.get(key);
			mapLingua.put(key + SEPARATOR + LETTA, cont);
			mapLingua.put(key + SEPARATOR + SCRITTA, cont);
			mapLingua.put(key + SEPARATOR + PARLATA, cont);
		}

		keyAltreInfo = new ArrayList<String>();
		keyAltreInfo.add(TRASFERTE);
		keyAltreInfo.add(AUTOMUNITO);
		keyAltreInfo.add(MOTOMUNITO);
		
		dummyList = new ArrayList<String>();
		dummyList.add("dummy");
	}

	public VaVisualizzaDTO getVaVisualizzaDTO() {
		return vaVisualizzaDTO;
	}

	public void setVaVisualizzaDTO(VaVisualizzaDTO vaVisualizzaDTO) {
		this.vaVisualizzaDTO = vaVisualizzaDTO;
	}

	public CvVisualizzaDTO getCvVisualizzaDTO() {
		return cvVisualizzaDTO;
	}

	public void setCvVisualizzaDTO(CvVisualizzaDTO cvVisualizzaDTO) {
		this.cvVisualizzaDTO = cvVisualizzaDTO;
	}

	public VaAltreInfoDTO getVaAltreInfoDTO() {
		return vaAltreInfoDTO;
	}

	public void setVaAltreInfoDTO(VaAltreInfoDTO vaAltreInfoDTO) {
		this.vaAltreInfoDTO = vaAltreInfoDTO;
	}

	public CvAltreInfoDTO getCvAltreInfoDTO() {
		return cvAltreInfoDTO;
	}

	public void setCvAltreInfoDTO(CvAltreInfoDTO cvAltreInfoDTO) {
		this.cvAltreInfoDTO = cvAltreInfoDTO;
	}

	public ArrayList<String> getKeyAltreInfo() {
		return keyAltreInfo;
	}

	public List<String> getKeyAnagrafica() {
		return dummyList;
	}

	public void setKeyAltreInfo(ArrayList<String> keyAltreInfo) {
		this.keyAltreInfo = keyAltreInfo;
	}

	public List<String> getKeyPatente() {
		List<String> list = new ArrayList<String>(mapPatenti.keySet());
		return list;
	}

	public List<String> getKeyPatentino() {
		List<String> list = new ArrayList<String>(mapPatentini.keySet());
		return list;
	}

	public List<String> getKeyAlbo() {
		List<String> list = new ArrayList<String>(mapAlbo.keySet());
		return list;
	}

	public List<String> getKeyAgevolazione() {
		List<String> list = new ArrayList<String>(mapAgevolazioni.keySet());
		return list;
	}

	public List<String> getKeyIstruzione() {
		List<String> list = new ArrayList<String>(mapIstruzione.keySet());
		return list;
	}

	public List<String> getKeyLingua() {
		List<String> list = new ArrayList<String>(mapLingua.keySet());
		return list;
	}

	private VaCvContainerDTO<VaLinguaDTO, CvLinguaDTO> getContainer(String key) {
		String effKey = key;
		if (key.endsWith(LETTA) || key.endsWith(SCRITTA) || key.endsWith(PARLATA)) {
			effKey = key.replace(SEPARATOR + ".+", "");
		}

		VaCvContainerDTO<VaLinguaDTO, CvLinguaDTO> cont = mapLingua.get(effKey);
		return cont;
	}

	public VaLinguaDTO getVaLingua(String key) {
		VaCvContainerDTO<VaLinguaDTO, CvLinguaDTO> cont = getContainer(key);
		return cont.getVaDto();
	}

	public AbstractLinguaDTO getCvLingua(String key) {
		VaCvContainerDTO<VaLinguaDTO, CvLinguaDTO> cont = getContainer(key);
		return cont.getCvDto();
	}

	public Map<String, VaCvContainerDTO<VaPatenteDTO, CvPatenteDTO>> getMapPatenti() {
		return mapPatenti;
	}

	public void setMapPatenti(Map<String, VaCvContainerDTO<VaPatenteDTO, CvPatenteDTO>> mapPatenti) {
		this.mapPatenti = mapPatenti;
	}

	public Map<String, VaCvContainerDTO<VaPatentinoDTO, CvPatentinoDTO>> getMapPatentini() {
		return mapPatentini;
	}

	public void setMapPatentini(Map<String, VaCvContainerDTO<VaPatentinoDTO, CvPatentinoDTO>> mapPatentini) {
		this.mapPatentini = mapPatentini;
	}

	public Map<String, VaCvContainerDTO<VaAlboDTO, CvAlboDTO>> getMapAlbo() {
		return mapAlbo;
	}

	public void setMapAlbo(Map<String, VaCvContainerDTO<VaAlboDTO, CvAlboDTO>> mapAlbo) {
		this.mapAlbo = mapAlbo;
	}

	public Map<String, VaCvContainerDTO<VaAgevolazioneDTO, CvAgevolazioneDTO>> getMapAgevolazioni() {
		return mapAgevolazioni;
	}

	public void setMapAgevolazioni(Map<String, VaCvContainerDTO<VaAgevolazioneDTO, CvAgevolazioneDTO>> mapAgevolazioni) {
		this.mapAgevolazioni = mapAgevolazioni;
	}

	public Map<String, VaCvContainerDTO<VaIstruzioneDTO, CvIstruzioneDTO>> getMapIstruzione() {
		return mapIstruzione;
	}

	public void setMapIstruzione(Map<String, VaCvContainerDTO<VaIstruzioneDTO, CvIstruzioneDTO>> mapIstruzione) {
		this.mapIstruzione = mapIstruzione;
	}

	public Map<String, VaCvContainerDTO<VaLinguaDTO, CvLinguaDTO>> getMapLingua() {
		return mapLingua;
	}

	public void setMapLingua(Map<String, VaCvContainerDTO<VaLinguaDTO, CvLinguaDTO>> mapLingua) {
		this.mapLingua = mapLingua;
	}

	public String getVaLivelloLingua(String key, VaLinguaDTO vaLingua) {
		return getLivelloLingua(key, vaLingua);
	}

	public String getCvLivelloLingua(String key, AbstractLinguaDTO cvLingua) {
		return getLivelloLingua(key, cvLingua);
	}
	
	protected String getLivelloLingua(String key, AbstractLinguaDTO lingua) {
		if ((key.endsWith(LETTA) || key.endsWith(SCRITTA) || key.endsWith(PARLATA)) && lingua != null) {
			if (key.endsWith(LETTA)) {
				return lingua.getStrGradoLinguaLetto();
			}
			if (key.endsWith(SCRITTA)) {
				return lingua.getStrGradoLinguaScritto();
			}
			if (key.endsWith(PARLATA)) {
				return lingua.getStrGradoLinguaParlato();
			}
		}
		return "";	
	}

	public String getVaAltreInfoDescrizione(String key, VaAltreInfoDTO vaAltreInfoDTO) {
		if (vaAltreInfoDTO == null) {
			return "";
		}

		if (key.equalsIgnoreCase(TRASFERTE)) {
			return (Y.equalsIgnoreCase(vaAltreInfoDTO.getFuorisede())) ? SI : INDIFFERENTE;
		}
		if (key.equalsIgnoreCase(MOTOMUNITO)) {
			return (Y.equalsIgnoreCase(vaAltreInfoDTO.getMotomunito())) ? SI : INDIFFERENTE;
		}
		if (key.equalsIgnoreCase(AUTOMUNITO)) {
			return (Y.equalsIgnoreCase(vaAltreInfoDTO.getAutomunito())) ? SI : INDIFFERENTE;
		}
		return "";
	}

	public String getCvAltreInfoDescrizione(String key, CvAltreInfoDTO cvAltreInfoDTO) {
		if (cvAltreInfoDTO == null) {
			return "";
		}

		if (key.equalsIgnoreCase(TRASFERTE)) {
			DeTrasfertaDTO deTrasfertaDTO = cvAltreInfoDTO.getTipoTrasferta();
			if (deTrasfertaDTO != null) {
				return deTrasfertaDTO.getDescrizione();
			} else {
				return "";
			}
		}
		if (key.equalsIgnoreCase(MOTOMUNITO)) {
			return (cvAltreInfoDTO.getMotomunito()) ? SI : NO;
		}
		if (key.equalsIgnoreCase(AUTOMUNITO)) {
			return (cvAltreInfoDTO.getAutomunito()) ? SI : NO;
		}
		return "";
	}

	public String getNomeLingua(String key) {
		VaCvContainerDTO<VaLinguaDTO, CvLinguaDTO> vaCvContainerDTO = mapLingua.get(key);
		String nomeLingua = (vaCvContainerDTO.getCvDto() != null) ? vaCvContainerDTO.getCvDto().getStrLingua()
				: vaCvContainerDTO.getVaDto().getStrLingua();
		return nomeLingua;
	}

	public String getNomeAltreInfo(String key) {
		if (key.equalsIgnoreCase(TRASFERTE)) {
			return "Disponibile a trasferte";
		}
		if (key.equalsIgnoreCase(MOTOMUNITO)) {
			return MOTOMUNITO;
		}
		if (key.equalsIgnoreCase(AUTOMUNITO)) {
			return AUTOMUNITO;
		}
		return "";
	}

	public String getContestoLingua(String key) {
		if (key.endsWith(LETTA)) {
			return "Letta";
		}
		if (key.endsWith(SCRITTA)) {
			return "Scritta";
		}
		if (key.endsWith(PARLATA)) {
			return "Parlata";
		}
		return "";
	}
}
