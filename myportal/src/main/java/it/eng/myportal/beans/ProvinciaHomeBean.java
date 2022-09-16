package it.eng.myportal.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.codec.binary.Hex;

import it.eng.myportal.dtos.ProvinciaDTO;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.OracleObfuscation;

/**
 * BackingBean dell'HomePage della Provincia.<br/>
 * 
 * <br/>
 * <b>Elenco restrizioni di sicurezza</b>
 * <ul>
 * <li>L'utente connesso deve essere del CPI della provincia.</li>
 * </ul>
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class ProvinciaHomeBean extends AbstractHomepageBean {

	private String tokenSARE;
	private String urlSARE;
	private String urlNuovoSARE;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			if (session.isProvincia()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				ProvinciaDTO provincia = session.getConnectedProvincia();

				// token per il collegamento
				// username|password|dd/mm/yyyy
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String dataFormattata = dateFormat.format(new Date());
				// recupero la password non cryptata passata alla form di login
				String passwordCript = pfPrincipalHome.findAbilitatoByUsername(session.getUsername()).getPassWord();
				String nuovaPassword = session.getNuovaPassword();
				String passwordNoCript = "";
				// TODO MYCAS non mi passa la password
				/*
				 * if (nuovaPassword != null && !("").equalsIgnoreCase(nuovaPassword)) { passwordNoCript =
				 * nuovaPassword; } else { Object passwordNoCriptObj = SecurityContextAssociation.getCredential();
				 * passwordNoCript = passwordNoCriptObj.toString(); }
				 */
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

				try {
					String endPoint = wsEndpointHome.getSareAddress(provincia.getProvincia().getId());
					// 'https://saretest.regione.emilia-romagna.it/WSSareUtenti/SareUtenti.asmx'
					String[] arrs = endPoint.split("/WsSareMyPortal");

					tokenSARE = userCript;
					urlSARE = arrs[0] + "/secure/accessi/accessFromTorre38.asp";
					log.debug("ACCESSO VECCHIO SARE" + tokenSARE);
				} catch (Exception e) {
					tokenSARE = "#";
					urlSARE = "";
				}
				//URL nuovo SARE regionale, unica
				urlNuovoSARE = wsEndpointHome.getNuovoSareAddress();

				log.debug("Costruito il Bean per Home Page Provincia!");
			} else {
				addErrorMessage("provincia.is_not");
				redirectHome();
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	/**
	 * @return the tokenSARE
	 */
	public String getTokenSARE() {
		return tokenSARE;
	}

	/**
	 * @param tokenSARE
	 *            the tokenSARE to set
	 */
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

	@Deprecated
	public boolean canShowSareNew() {
		String codProv = session.getConnectedProvincia().getProvincia().getId();
		String codAttivitaPf = "ACCEDISARENEW_" + codProv;
		boolean check = isNuovoSareInstallato() && isAbilitato(codAttivitaPf, TipoAbilitazione.VISIBILE);

		return check;
	}
	
	public boolean isProfilatoNuovoSare() {
		return isNuovoSareInstallato() && session.isProfilatoSare();
	}

	public boolean isNuovoSareInstallato() {
		return checkAbilitazioneSistemaVisibile(ConstantsSingleton.Sistema.Sare.CODSISTEMA);
	}

}
