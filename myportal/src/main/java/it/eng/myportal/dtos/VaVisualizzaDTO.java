package it.eng.myportal.dtos;

import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;

/**
 * DTO contenente i dati per visualizzare la vacancy.
 * 
 * @author Rodi A.
 * 
 */
public class VaVisualizzaDTO implements IHasPrimaryKey<Integer> {
	private static final long serialVersionUID = -3823863383747083340L;

	private String ragioneSociale;
	private VaDatiVacancyDTO vaDatiVacancyDTO;
	private VaAltreInfoDTO vaAltreInfoDTO;
	private VaInformaticaDTO vaInformaticaDTO;
	private VaCompetenzeTrasvDTO vaCompetenzeTrasvDTO;
	private VaContattoDTO vaContattoPrincDTO;
	private VaContattoDTO vaContattoAltDTO;
	private VaRetribuzioneDTO vaRetribuzioneDTO;
	private VaEsperienzeDTO vaEsperienzeDTO;
	private VaPubblicazioneDTO vaPubblicazioneDTO;
	private List<VaIstruzioneDTO> listaVaIstruzioneDTO;
	private List<VaLinguaDTO> listaVaLinguaDTO;
	private List<VaContrattoDTO> listaVaContrattoDTO;
	private List<VaTurnoDTO> listaVaTurnoDTO;
	private List<VaOrarioDTO> listaVaOrarioDTO;
	private List<VaPatenteDTO> listaVaPatenteDTO;
	private List<VaPatentinoDTO> listaVaPatentinoDTO;
	private List<VaAlboDTO> listaVaAlboDTO;
	private List<VaAgevolazioneDTO> listaVaAgevolazioneDTO;
	private List<AcVisualizzaCandidaturaDTO> listAcCandidaturaDTO;
	private Integer id;
	private Integer idSvAziendaInfo;
	/**
	 * Variabile impostata a true se l'utente può visualizzare i dati di contatto della vacancy, impostata a false non
	 * gli è permesso.
	 */
	private boolean mostraContattoPrincipale;
	/**
	 * A seconda se la vacancy è intermediata o meno potrà vedere o il contatto principale o quello alternativo
	 */
	private boolean mostraContattoAlternativo;

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<VaIstruzioneDTO> getListaVaIstruzioneDTO() {
		return listaVaIstruzioneDTO;
	}

	public void setListaVaIstruzioneDTO(List<VaIstruzioneDTO> listaVaIstruzioneDTO) {
		this.listaVaIstruzioneDTO = listaVaIstruzioneDTO;
	}

	public List<VaLinguaDTO> getListaVaLinguaDTO() {
		return listaVaLinguaDTO;
	}

	public void setListaVaLinguaDTO(List<VaLinguaDTO> listaVaLinguaDTO) {
		this.listaVaLinguaDTO = listaVaLinguaDTO;
	}

	public VaDatiVacancyDTO getVaDatiVacancyDTO() {
		return vaDatiVacancyDTO;
	}

	public void setVaDatiVacancyDTO(VaDatiVacancyDTO vaDatiVacancyDTO) {
		this.vaDatiVacancyDTO = vaDatiVacancyDTO;
	}

	/**
	 * @return the listAcCandidaturaDTO
	 */
	public List<AcVisualizzaCandidaturaDTO> getListAcCandidaturaDTO() {
		return listAcCandidaturaDTO;
	}

	/**
	 * @param listAcCandidaturaDTO
	 *            the listAcCandidaturaDTO to set
	 */
	public void setListAcCandidaturaDTO(List<AcVisualizzaCandidaturaDTO> listAcCandidaturaDTO) {
		this.listAcCandidaturaDTO = listAcCandidaturaDTO;
	}

	public VaAltreInfoDTO getVaAltreInfoDTO() {
		return vaAltreInfoDTO;
	}

	public void setVaAltreInfoDTO(VaAltreInfoDTO vaAltreInfoDTO) {
		this.vaAltreInfoDTO = vaAltreInfoDTO;
	}

	public VaInformaticaDTO getVaInformaticaDTO() {
		return vaInformaticaDTO;
	}

	public void setVaInformaticaDTO(VaInformaticaDTO vaInformaticaDTO) {
		this.vaInformaticaDTO = vaInformaticaDTO;
	}

	public VaCompetenzeTrasvDTO getVaCompetenzeTrasvDTO() {
		return vaCompetenzeTrasvDTO;
	}

	public void setVaCompetenzeTrasvDTO(VaCompetenzeTrasvDTO vaCompetenzeTrasvDTO) {
		this.vaCompetenzeTrasvDTO = vaCompetenzeTrasvDTO;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof VaVisualizzaDTO))
			return false;
		Integer otherId = ((VaVisualizzaDTO) o).getId();
		if (otherId != null && this.getId() != null)
			return otherId.intValue() == this.getId().intValue();
		else
			return false;
	}

	@Override
	public int hashCode() {
		if (this.getId() != null)
			return this.getId() % ConstantsSingleton.DEFAULT_BUCKET_SIZE;
		else
			return 0;
	}

	public List<VaContrattoDTO> getListaVaContrattoDTO() {
		return listaVaContrattoDTO;
	}

	public void setListaVaContrattoDTO(List<VaContrattoDTO> listaVaContrattoDTO) {
		this.listaVaContrattoDTO = listaVaContrattoDTO;
	}

	public List<VaTurnoDTO> getListaVaTurnoDTO() {
		return listaVaTurnoDTO;
	}

	public void setListaVaTurnoDTO(List<VaTurnoDTO> listaVaTurnoDTO) {
		this.listaVaTurnoDTO = listaVaTurnoDTO;
	}

	public List<VaOrarioDTO> getListaVaOrarioDTO() {
		return listaVaOrarioDTO;
	}

	public void setListaVaOrarioDTO(List<VaOrarioDTO> listaVaOrarioDTO) {
		this.listaVaOrarioDTO = listaVaOrarioDTO;
	}

	public VaRetribuzioneDTO getVaRetribuzioneDTO() {
		return vaRetribuzioneDTO;
	}

	public void setVaRetribuzioneDTO(VaRetribuzioneDTO vaRetribuzioneDTO) {
		this.vaRetribuzioneDTO = vaRetribuzioneDTO;
	}

	public List<VaPatenteDTO> getListaVaPatenteDTO() {
		return listaVaPatenteDTO;
	}

	public void setListaVaPatenteDTO(List<VaPatenteDTO> listaVaPatenteDTO) {
		this.listaVaPatenteDTO = listaVaPatenteDTO;
	}

	public List<VaPatentinoDTO> getListaVaPatentinoDTO() {
		return listaVaPatentinoDTO;
	}

	public void setListaVaPatentinoDTO(List<VaPatentinoDTO> listaVaPatentinoDTO) {
		this.listaVaPatentinoDTO = listaVaPatentinoDTO;
	}

	public List<VaAlboDTO> getListaVaAlboDTO() {
		return listaVaAlboDTO;
	}

	public void setListaVaAlboDTO(List<VaAlboDTO> listaVaAlboDTO) {
		this.listaVaAlboDTO = listaVaAlboDTO;
	}

	public List<VaAgevolazioneDTO> getListaVaAgevolazioneDTO() {
		return listaVaAgevolazioneDTO;
	}

	public void setListaVaAgevolazioneDTO(List<VaAgevolazioneDTO> listaVaAgevolazioneDTO) {
		this.listaVaAgevolazioneDTO = listaVaAgevolazioneDTO;
	}

	public VaContattoDTO getVaContattoPrincDTO() {
		return vaContattoPrincDTO;
	}

	public void setVaContattoPrincDTO(VaContattoDTO vaContattoPrincDTO) {
		this.vaContattoPrincDTO = vaContattoPrincDTO;
	}

	public VaContattoDTO getVaContattoAltDTO() {
		return vaContattoAltDTO;
	}

	public void setVaContattoAltDTO(VaContattoDTO vaContattoAltDTO) {
		this.vaContattoAltDTO = vaContattoAltDTO;
	}

	public Integer getIdSvAziendaInfo() {
		return idSvAziendaInfo;
	}

	public void setIdSvAziendaInfo(Integer idSvAziendaInfo) {
		this.idSvAziendaInfo = idSvAziendaInfo;
	}

	/**
	 * Determina se la vacancy proviene da clic lavoro o meno
	 * 
	 * @return
	 */
	public boolean isFromClicLavoro() {
		return getVaDatiVacancyDTO().isFromClicLavoro();
	}

	public boolean isMostraContattoPrincipale() {
		return mostraContattoPrincipale;
	}

	public void setMostraContattoPrincipale(boolean mostraContatto) {
		this.mostraContattoPrincipale = mostraContatto;
	}

	public boolean isMostraContattoAlternativo() {
		return mostraContattoAlternativo;
	}

	public void setMostraContattoAlternativo(boolean mostraContattoAlternativo) {
		this.mostraContattoAlternativo = mostraContattoAlternativo;
	}

	public boolean isIntermediata() {
		return getVaDatiVacancyDTO().isIntermediata();
	}

	public void setIntermediata(boolean b) {
		getVaDatiVacancyDTO().setIntermediata(b);
	}

	public VaEsperienzeDTO getVaEsperienzeDTO() {
		return vaEsperienzeDTO;
	}

	public void setVaEsperienzeDTO(VaEsperienzeDTO vaEsperienzeDTO) {
		this.vaEsperienzeDTO = vaEsperienzeDTO;
	}

	public VaPubblicazioneDTO getVaPubblicazioneDTO() {
		return vaPubblicazioneDTO;
	}

	public void setVaPubblicazioneDTO(VaPubblicazioneDTO vaPubblicazioneDTO) {
		this.vaPubblicazioneDTO = vaPubblicazioneDTO;
	}

}
