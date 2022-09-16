package it.eng.myportal.beans.yg;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.YgAdesioneHome;
import it.eng.myportal.youthGuarantee.bean.RisultatoInvioAdesione;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class YgRipristinoAdesioneBean extends AbstractBaseBean {

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	private Integer idYgAdesione;
	private YgAdesioneDTO ygAdesioneDTO;
	private boolean codiceSapValido = true;

	public void checkViewPage() {
		boolean checkView = true;

		// modifica del 08/05/2014
		// non è più necessario essere abilitati
		// ai servizi amministrativi per
		// poter accedere alla parte di YG

		// boolean checkView = getSession().isAbilitato("_portlet_yg_ricerca",
		// TipoAbilitazione.VISIBILE);

		if (!checkView) {
			getExternalContext().setResponseStatus(404);
			getFacesContext().responseComplete();
			redirectHome();
			return;
		}

		if (!session.isProvincia()) {
			log.warn("Tentativo di accedere alla ricerca adesioni non dalla sezione provincia.");
			redirectHome();
			return;
		}
	}

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			Map<String, String> map = getRequestParameterMap();					
			for (Entry<String, String> entry : map.entrySet()) {
				if (entry.getKey().endsWith("idYgAdesione")) {
					idYgAdesione = Integer.parseInt(entry.getValue());
					break;
				}
			}
			
			//idYgAdesione = Integer.parseInt(map.get("idYgAdesione"));
			log.info("Ripristino adesione id="+ idYgAdesione );
			
			ygAdesioneDTO = ygAdesioneHome.findDTOById(idYgAdesione);

			if (ygAdesioneHome.isAdesioneFromCliclavoro(ygAdesioneDTO.getIdentificativoSap())) {
				codiceSapValido = ygAdesioneHome.getNuovoIdentificativoSAP(ygAdesioneDTO);
			}
		} catch (Exception e) {
			redirectGrave("generic.manipulation_error");
		}
	}

	/**
	 * Esegue il ripristino dell'adesione, cioe' reinvia un'adesione con gli
	 * stessi dati.
	 */
	public String ripristinoAdesione() {
		try {
			log.info("Ripristino adesione ripristinoAdesione() id="+ ygAdesioneDTO.getId() );
			log.info("Ripristino adesione ripristinoAdesione() codicesap="+ ygAdesioneDTO.getIdentificativoSap() );
			log.info("Ripristino adesione ripristinoAdesione() id utente="+ session.getPrincipalId() );
			
			RisultatoInvioAdesione risultatoInvioAdesione = ygAdesioneHome.ripristinaAdesione(ygAdesioneDTO.getId(),
					ygAdesioneDTO.getIdentificativoSap(), session.getPrincipalId());
			getFacesContext().getExternalContext().getRequestMap()
					.put("risultatoInvioAdesione", risultatoInvioAdesione);
		} catch (Exception e) {
			addErrorMessage("generic.servizio_non_disponibile");
			return "";
		}

		return "esito_ripristino_adesione.xhtml";
	}

	public YgAdesioneDTO getYgAdesioneDTO() {
		return ygAdesioneDTO;
	}

	public void setYgAdesioneDTO(YgAdesioneDTO ygAdesioneDTO) {
		this.ygAdesioneDTO = ygAdesioneDTO;
	}

	public boolean isCodiceSapValido() {
		return codiceSapValido;
	}

	public void setCodiceSapValido(boolean codiceSapValido) {
		this.codiceSapValido = codiceSapValido;
	}

}
