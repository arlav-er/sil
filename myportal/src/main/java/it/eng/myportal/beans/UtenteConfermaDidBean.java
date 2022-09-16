package it.eng.myportal.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.DidConfermaPrenotaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.DidConfermaPrenota;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.home.DidConfermaPrenotaHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;

@ManagedBean
@ViewScoped
public class UtenteConfermaDidBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(UtenteConfermaDidBean.class);

	@EJB
	DidConfermaPrenotaHome didConfermaPrenotaHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	TsGetOpzioniEJB tsGetOpzioniEJB;

	private boolean esisteConferma;
	private boolean abilitaButtonConferma;
	private UtenteCompletoDTO utente;
	private String strDataConferma;
	private Date dtFineConfermaDid;

	@PostConstruct
	public void postConstruct() {
		try {
			if (!session.isUtente()) {
				log.warn("Tentativo di accedere alla conferma did non dalla sezione cittadino.");
				redirectHome();
				return;
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			super.postConstruct();

			Date currentDate = new Date();

			//StConfigurazione config = stConfigurazioneHome.findById(new Integer("1"));
			dtFineConfermaDid = tsGetOpzioniEJB.getDtFineConfermaDid();

			if (dtFineConfermaDid == null || !currentDate.before(dtFineConfermaDid)) {
				setAbilitaButtonConferma(false);
			} else {
				setAbilitaButtonConferma(true);
			}

			utente = utenteInfoHome.findDTOCompletoByUsername(session.getUsername());
			DidConfermaPrenota confermaUtente = didConfermaPrenotaHome.findByIdPfPrincipal(utente.getId());
			if (confermaUtente != null) {
				esisteConferma = true;
				Date dtConferma = confermaUtente.getDtConferma();
				strDataConferma = dateFormat.format(dtConferma);

			} else {
				esisteConferma = false;
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	public void insertConferma() {
		try {
			// controlli
			UtenteInfo user = utenteInfoHome.findByUsername(session.getUsername());
			if (user.getCodiceFiscale() != null) {
				DeComune comDom = user.getDeComuneDomicilio();
				if (comDom != null) {
					DeComune comuneDomicilio = deComuneHome.findById(comDom.getCodCom());
					String idProvDom = comuneDomicilio.getDeProvincia().getCodProvincia();
					// DeProvincia provDom =
					// deProvinciaHome.findById(idProvDom);
					if (("55").equals(idProvDom)) {
						DidConfermaPrenotaDTO confermaDTO = new DidConfermaPrenotaDTO();
						confermaDTO.setCodiceFiscale(user.getCodiceFiscale());
						confermaDTO.setComuneDomicilio(deComuneHome.findDTOById(comuneDomicilio.getCodCom()));
						confermaDTO.setDtConferma(new Date());
						confermaDTO.setPrincipal(pfPrincipalHome.findDTOById(user.getIdPfPrincipal()));

						confermaDTO.setIdPrincipalIns(0);
						confermaDTO.setIdPrincipalMod(0);
						confermaDTO.setDtmIns(new Date());
						confermaDTO.setDtmMod(new Date());

						didConfermaPrenotaHome.persistDTO(confermaDTO, session.getPrincipalId());

						addInfoMessage("data.created");

						redirectHome();
					} else {
						// 2) comune in provincia di TERNI
						addErrorMessage("conferma.comdom-no-terni");
						redirectHome();
					}
				} else {
					addErrorMessage("conferma.comdom-assente");
					redirectHome();
				}
			} else {
				// 1) esistenza CF
				addErrorMessage("conferma.cf-assente");
				redirectHome();
			}
		} catch (EJBException e) {
			gestisciErrore(e, "data.error_saving");
		}
	}

	public boolean isEsisteConferma() {
		return esisteConferma;
	}

	public void setEsisteConferma(boolean esisteConferma) {
		this.esisteConferma = esisteConferma;
	}

	public UtenteCompletoDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteCompletoDTO utente) {
		this.utente = utente;
	}

	public String getStrDataConferma() {
		return strDataConferma;
	}

	public void setStrDataConferma(String strDataConferma) {
		this.strDataConferma = strDataConferma;
	}

	public Date getDtFineConfermaDid() {
		return dtFineConfermaDid;
	}

	public void setDtFineConfermaDid(Date dtFineConfermaDid) {
		this.dtFineConfermaDid = dtFineConfermaDid;
	}

	public boolean isAbilitaButtonConferma() {
		return abilitaButtonConferma;
	}

	public void setAbilitaButtonConferma(boolean abilitaButtonConferma) {
		this.abilitaButtonConferma = abilitaButtonConferma;
	}

}
