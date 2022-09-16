package it.eng.myportal.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.codec.binary.Hex;

import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.AziendaSessionDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.SvAziendaInfoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.SvAziendaInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.OracleObfuscation;

/**
 * BackingBean dell'HomePage dell'azienda.<br/>
 * <br/>
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere un'azienda.</li>
 * </ul>
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class AziendaHomeBean extends AbstractHomepageBean {

	private AziendaSessionDTO azienda;
	private String tokenSARE;
	private String urlSARE;
	private String urlNuovoSARE;
	private String urlMyStage;
	private AziendaInfoDTO aziendaInfoDTO;
	private boolean promotore;

	/**
	 * Elenco delle vacancies create dall'utente
	 */
	// TODO: Rifattorizzare la seguente variabile in firstNRowsVacancyDTO
	private List<VaDatiVacancyDTO> vacancies;

	// private int numeroVacancies;

	@EJB
	private VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	private SvAziendaInfoHome svAziendaHome;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	/**
	 * Variabile a 'true' se l'azienda ha già costruito una sua vetrina
	 */
	private SvAziendaInfoDTO vetrina;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			if (session.isAzienda()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				azienda = session.getConnectedAzienda();
				promotore = false;

				fillVacancyListWithFirstNRowsVacancyDirettePalesi();

				vetrina = svAziendaHome.findDTOByIdPfPrincipal(azienda.getId());

				// token per il collegamento
				// username|password|dd/mm/yyyy
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String dataFormattata = dateFormat.format(new Date());
				String passwordCript = pfPrincipalHome.findAbilitatoByUsername(session.getUsername()).getPassWord();
				// recupero la password non cryptata passata alla form di login

				String token = session.getUsername() + "|" + passwordCript + "|" + dataFormattata;
				// String token = "provanc|provanc|"+dataFormattata;
				StringBuilder padded = new StringBuilder(token);
				while ((padded.length() % 8) > 0) {
					padded.append(" ");
				}

				// cripto il token da passare al SARE
				OracleObfuscation desObf = new OracleObfuscation("TODOTODO");
				byte[] encrypted = desObf.encrypt((padded.toString()).getBytes());
				// Encrypt
				String userCript = new String(Hex.encodeHex(encrypted));
				aziendaInfoDTO = aziendaInfoHome.findDTOById(azienda.getIdAziendaInfo());
				if (aziendaInfoDTO.getProvinciaRiferimento().getId() != null) {
					try {
						String endPoint = wsEndpointHome
								.getSareAddress(aziendaInfoDTO.getProvinciaRiferimento().getId());
						// 'https://saretest.regione.emilia-romagna.it/WSSareUtenti/SareUtenti.asmx'
						String[] arrs = endPoint.split("/WsSareMyPortal");

						// tokenSARE = "t="+userCript;
						tokenSARE = userCript;
						urlSARE = arrs[0] + "/secure/accessi/accessFromTorre38.asp";
						log.debug("ACCESSO VECCHIO SARE:" + urlSARE + tokenSARE);
					} catch (Exception e) {
						tokenSARE = "#";
						urlSARE = "";
					}
				} else {
					tokenSARE = "#";
					urlSARE = "";
				}

				try {
					// URL nuovo SARE regionale, unica
					urlNuovoSARE = wsEndpointHome.getNuovoSareAddress();
				} catch (Exception e) {
					urlNuovoSARE = "";
				}

				urlMyStage = session.getUrlMyStage();

				if (aziendaInfoDTO.getTipoDelegato() != null) {
					String idTipoDelegato = aziendaInfoDTO.getTipoDelegato().getId();
					if (idTipoDelegato != null && ("010").equalsIgnoreCase(idTipoDelegato)) {
						promotore = true;
					}
				}

			} else {
				addErrorMessage("azienda.is_not");
				redirectHome();
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	private void fillVacancyListWithFirstNRowsVacancyDirettePalesi() {
		vacancies = new ArrayList<VaDatiVacancyDTO>();
		List<VaDatiVacancyDTO> firstNVacanciesDirette = new ArrayList<VaDatiVacancyDTO>();
		List<VaDatiVacancyDTO> firstNVvacanciesIndirettePalesi = new ArrayList<VaDatiVacancyDTO>();

		firstNVacanciesDirette = vaDatiVacancyHome.findFirstNRowVacanciesByIdPfPrincipalAzienda(azienda.getId(),
				session.getPrincipalId(), false, false, ConstantsSingleton.MAX_PORTLET_VACANCIES,
				UtilsBean.isStaticRER());
		firstNVvacanciesIndirettePalesi = vaDatiVacancyHome.findFirstNRowVacanciesByIdPfPrincipalAzienda(
				azienda.getId(), session.getPrincipalId(), false, true, ConstantsSingleton.MAX_PORTLET_VACANCIES,
				UtilsBean.isStaticRER());

		vacancies.addAll(firstNVacanciesDirette);
		vacancies.addAll(firstNVvacanciesIndirettePalesi);
	}

	public List<VaDatiVacancyDTO> getVacancies() {
		return vacancies;
	}

	public void setVacancies(List<VaDatiVacancyDTO> vacancies) {
		this.vacancies = vacancies;
	}

	public int getNumeroVacancies() {
		return vacancies != null ? vacancies.size() : 0;
	}

	public AziendaInfoDTO getAziendaInfoDTO() {
		return aziendaInfoDTO;
	}

	public void setAziendaInfoDTO(AziendaInfoDTO aziendaInfoDTO) {
		this.aziendaInfoDTO = aziendaInfoDTO;
	}

	public boolean canCopyVacancy(VaDatiVacancy toCopy) {
		boolean proprietary = vaDatiVacancyHome.isProprietary(toCopy.getIdVaDatiVacancy(),
				getSession().getPrincipalId());

		if (!proprietary)
			return false;

		if (toCopy.getFlagIdo()) {
			return true;
		} else {
			return true;
		}
	}

	public void copyVacancy() {
		try {
			Map<String, String> map = getRequestParameterMap();
			Integer copyId = null;
			String copyDescrizione = null;

			for (Entry<String, String> entry : map.entrySet()) {
				if (entry.getKey().endsWith("descrizione_copia")) {
					copyDescrizione = entry.getValue();
				} else if (entry.getKey().endsWith("id_copia")) {
					copyId = Integer.parseInt(entry.getValue());
				}
			}

			if (copyId != null && copyDescrizione != null) {
				if (vaDatiVacancyHome.isProprietary(copyId, getSession().getPrincipalId())) {
					vaDatiVacancyHome.copyById(getSession().getPrincipalId(), copyId, copyDescrizione);

					fillVacancyListWithFirstNRowsVacancyDirettePalesi();

					// messaggio di copia riuscita
					addInfoMessage("vacancy.copied");
				} else {
					addErrorMessage("vacancy.wrongPermission");
				}
			}
		} catch (EJBException e) {
			gestisciErrore(e, "vacancy.error_copying");
		}
	}

	public boolean canDeleteVacancy(VaDatiVacancy toDelete) {
		boolean proprietary = vaDatiVacancyHome.isProprietary(toDelete.getIdVaDatiVacancy(),
				getSession().getPrincipalId());

		if (!proprietary)// se non e` tua, ciao
			return false;

		if (toDelete.getFlagIdo()) {// nuovo IDO deve essere in lavorazione
			return CodStatoVacancyEnum.LAV.equals(toDelete.getCodStatoVacancyEnum());
		} else if ((CodStatoVacancyEnum.LAV.equals(toDelete.getCodStatoVacancyEnum())
				|| CodStatoVacancyEnum.PUB.equals(toDelete.getCodStatoVacancyEnum()))) {
			return true;
		}

		log.warn("Attenzione: non si dispone delle autorizzazioni per eseguire la cancellazione");

		return false;
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
					vaDatiVacancyHome.deleteVacancy(deleteId, session.getPrincipalId());

					fillVacancyListWithFirstNRowsVacancyDirettePalesi();

					// messaggio di eliminazione riuscita
					addInfoMessage("vacancy.deleted");
				} else {
					addErrorMessage("vacancy.wrongPermission");
				}
			}
		} catch (EJBException e) {
			// TODO valutare gestiscierrore()
			addErrorMessage("vacancy.error_deleting", e);
		}
	}

	/**
	 * Solo per la VDA, disabilitiamo la portlet "vacancies" per le aziende non abilitate dal CPI (flagValida = false)
	 */
	public boolean isVacanciesAbilitate() {
		if ((ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_VDA) && !aziendaInfoDTO.getFlagValida()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Solo per la VDA, disabilitiamo la portlet "vetrina" per le aziende non abilitate dal CPI (flagValida = false)
	 */
	public boolean isVetrinaAbilitata() {
		if ((ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_VDA) && !aziendaInfoDTO.getFlagValida()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean chekRER() {
		boolean check = false;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			check = true;
		}
		return check;
	}

	/**
	 * Mostro il pulsante per accedere al nuovo SARE solo se questo è abilitato nella mia provincia
	 */
	@Deprecated
	public boolean canShowSareNew() {
		boolean check = false;
		DeProvinciaDTO provDTO = session.getConnectedAzienda().getProvincia();
		if (provDTO != null) {
			String codProv = provDTO.getId();
			String codAttivitaPf = "ACCEDISARENEW_" + codProv;
			check = isNuovoSareInstallato() && isAbilitato(codAttivitaPf, TipoAbilitazione.VISIBILE);
		}
		return check;
	}

	public boolean isProfilatoNuovoSare() {
		return isNuovoSareInstallato() && session.isProfilatoSare();
	}

	public SvAziendaInfoHome getSvAziendaHome() {
		return svAziendaHome;
	}

	public void setSvAziendaHome(SvAziendaInfoHome svAziendaHome) {
		this.svAziendaHome = svAziendaHome;
	}

	public SvAziendaInfoDTO getVetrina() {
		return vetrina;
	}

	public void setVetrina(SvAziendaInfoDTO vetrina) {
		this.vetrina = vetrina;
	}

	public String getTokenSARE() {
		return tokenSARE;
	}

	public void setTokenSARE(String tokenSARE) {
		this.tokenSARE = tokenSARE;
	}

	public String getUrlSARE() {
		return urlSARE;
	}

	public void setUrlSARE(String urlSARE) {
		this.urlSARE = urlSARE;
	}

	public String getUrlNuovoSARE() {
		return urlNuovoSARE;
	}

	public void setUrlNuovoSARE(String urlNuovoSARE) {
		this.urlNuovoSARE = urlNuovoSARE;
	}

	public boolean isNuovoSareInstallato() {
		return checkAbilitazioneSistemaVisibile(ConstantsSingleton.Sistema.Sare.CODSISTEMA);
	}

	public String getUrlMyStage() {
		return urlMyStage;
	}

	public void setUrlMyStage(String urlMyStage) {
		this.urlMyStage = urlMyStage;
	}

	public boolean getPromotore() {
		return promotore;
	}

	public void setPromotore(boolean promotore) {
		this.promotore = promotore;
	}

	public boolean isEnteAccreditato() {
		return session.isEnteAccreditato();
	}

}
