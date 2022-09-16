package it.eng.myportal.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.dtos.PtScrivaniaDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.PtPortletHome;
import it.eng.myportal.entity.home.PtScrivaniaHome;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;
import it.eng.myportal.servlet.FacesUtil;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * BackingBean dell'Autenticazione per ogni tipo di utente esegue il redirect alle home page relative ai singoli utenti
 * verifica se si ha la password scaduta
 * 
 * 
 */
@ManagedBean
@RequestScoped
public class AuthenticateHomeBean extends AbstractBaseBean {

	@EJB
	transient PfPrincipalHome pfPrincipalHome;

	@EJB
	PtScrivaniaHome ptScrivaniaHome;

	@EJB
	PtPortletHome ptPortletHome;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	private String outcome;

	

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {

			String contesto = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			Date dataScadenza = pfPrincipalHome.getDataScadenza(session.getUsername());

			Date now = new Date();
			if (now.before(dataScadenza)) {
				String home = getHomeBySession();

				// 3/12/2014 rimosso da MyAccount
				// inserisco le portlet per l'utente!!!!
				PfPrincipal pfPrincipal = pfPrincipalHome.findAbilitatoByUsername(session.getUsername());
				List<PtScrivaniaDTO> listaPortletScrivaniaUtente = ptScrivaniaHome.findPortletsScrivania(pfPrincipal
						.getIdPfPrincipal());

				if (listaPortletScrivaniaUtente.size() == 0) {
					if (pfPrincipal != null) {
						List<PtPortlet> portlets = null;

						portlets = ptPortletHome.findByCodRuoloPortale(pfPrincipal.getDeRuoloPortale()
								.getCodRuoloPortale());
						if (portlets != null) {
							for (int i = 0; i < portlets.size(); i++) {
								PtPortlet iesimaPortlet = portlets.get(i);

								// Alcune portlet NON devono essere aggiunte automaticamente.
								// (Esempio: le portlet di AssistER)
								if (!ConstantsSingleton.PtPortlet.PORTLET_NASCOSTE.contains(iesimaPortlet.getNome())) {
									PtScrivania iesimaScrivania = new PtScrivania();
									iesimaScrivania.setPfPrincipal(pfPrincipal);
									iesimaScrivania.setFlagRidotta(false);
									iesimaScrivania.setFlagVisualizza(true);
									iesimaScrivania.setPtPortlet(iesimaPortlet);
									iesimaScrivania.setDtmIns(new Date());
									iesimaScrivania.setDtmMod(new Date());
									iesimaScrivania.setPfPrincipalIns(pfPrincipal);
									iesimaScrivania.setPfPrincipalMod(pfPrincipal);

									if (("_portlet_yg").equalsIgnoreCase(iesimaPortlet.getNome())) {
										iesimaScrivania.setPosizione(0);
										iesimaScrivania.setOptColonna("L");
									} else {
										iesimaScrivania.setPosizione((i + 1) % 5);
										iesimaScrivania.setOptColonna(((i + 1) % 2 == 0) ? "L" : "R");
									}

									ptScrivaniaHome.persist(iesimaScrivania);
								}
							}
						}
						log.info("Portlets inserite correttamente");
					}
				}

				if (StringUtils.isBlank(home)) {
					log.error("Errore durante la costruzione del bean " + getClass().getSimpleName()
							+ ". Nessun utente o azienda collegato");
					setOutcome(contesto + ConstantsSingleton.BASE_URL);
				} else {
					setOutcome(contesto + home);
				}

			} else {
				SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
				// PASSWORD SCADUTA
				// home intermedia per modificarla
				log.info("La password dell'utente" + session.getUsername() + " è scaduta il "
						+ formatter1.format(dataScadenza) + " e deve essere aggiornata.");
				// setOutcome("/MyPortal/faces/secure/auth/cambioPassword.xhtml");
				// TODO modifica url per CAS

				try {
					FacesUtil.getResponse().sendRedirect(ConstantsSingleton.MYACCOUNT_URL + "/secure/changePassword");
				} catch (Exception e) {
					log.error("Errore durante la redirect verso changePassword: " + e.toString());
				}
			}

		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectPublicIndex();
		}
	}

	/**
	 * @return the passwordConfirm
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * @param passwordConfirm
	 *            the passwordConfirm to set
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

}
