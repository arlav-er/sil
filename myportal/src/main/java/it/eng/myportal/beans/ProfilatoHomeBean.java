package it.eng.myportal.beans;

import it.eng.myportal.dtos.CertificatoreInfoDTO;
import it.eng.myportal.dtos.ConsulenteInfoDTO;
import it.eng.myportal.dtos.CoordinatoreInfoDTO;
import it.eng.myportal.dtos.PatronatoDTO;
import it.eng.myportal.dtos.ProvinciaDTO;
import it.eng.myportal.dtos.RegioneDTO;
import it.eng.myportal.dtos.SoggettoPubblicoDTO;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.utils.OracleObfuscation;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.codec.binary.Hex;

/**
 * BackingBean dell'HomePage dell'utente.
 * 
 * @author Turro
 * 
 */
@ManagedBean
@ViewScoped
public class ProfilatoHomeBean extends AbstractHomepageBean {

	CoordinatoreInfoDTO coordinatore;

	ConsulenteInfoDTO consulente;

	CertificatoreInfoDTO certificatore;

	SoggettoPubblicoDTO soggetto;

	ProvinciaDTO provincia;

	RegioneDTO regione;

	PatronatoDTO patronato;

	private String cartella;

	private String tokenSARE;
	private String urlSARE;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		try {
			if (session.isCoordinatore()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				coordinatore = session.getConnectedCoordinatore();
				this.cartella = "coordinatore";

			} else if (session.isConsulente()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				consulente = session.getConnectedConsulente();
				this.cartella = "consulente";

			} else if (session.isCertificatore()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				certificatore = session.getConnectedCertificatore();
				this.cartella = "certificatore";

			} else if (session.isSoggettoPubblico()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				soggetto = session.getConnectedSoggetto();
				this.cartella = "soggetto_pubblico";

			} else if (session.isRegione()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				regione = session.getConnectedRegione();
				this.cartella = "regione";

			} else if (session.isPatronato()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				patronato = session.getConnectedPatronato();
				this.cartella = "patronato";

			} else if (session.isProvincia()) {
				session.getParams().clear();
				provincia = session.getConnectedProvincia();
				this.cartella = "provincia";

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

				if (provincia.getProvincia().getId() != null) {
					String endPoint = wsEndpointHome.getSareAddress(provincia.getProvincia().getId());
					// 'https://saretest.regione.emilia-romagna.it/WSSareUtenti/SareUtenti.asmx'
					String[] arrs = endPoint.split("/WsSareMyPortal");

					tokenSARE = userCript;
					urlSARE = arrs[0] + "/secure/accessi/accessFromTorre38.asp";
					log.debug("ACCESSO SARE" + tokenSARE);
				} else {
					tokenSARE = "#";
					urlSARE = "";
				}

				log.debug("Costruito il Bean per Home Page Provincia!");
			} else {
				addErrorMessage("authorized.is_not");
				redirectHome();
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	public CoordinatoreInfoDTO getCoordinatore() {
		return coordinatore;
	}

	public void setCoordinatore(CoordinatoreInfoDTO coordinatore) {
		this.coordinatore = coordinatore;
	}

	public ProvinciaDTO getProvincia() {
		return provincia;
	}

	public void setProvincia(ProvinciaDTO provincia) {
		this.provincia = provincia;
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

	public String getCartella() {
		return cartella;
	}

	public void setCartella(String cartella) {
		this.cartella = cartella;
	}

	public ConsulenteInfoDTO getConsulente() {
		return consulente;
	}

	public void setConsulente(ConsulenteInfoDTO consulente) {
		this.consulente = consulente;
	}

	public CertificatoreInfoDTO getCertificatore() {
		return certificatore;
	}

	public void setCertificatore(CertificatoreInfoDTO certificatore) {
		this.certificatore = certificatore;
	}

	public RegioneDTO getRegione() {
		return regione;
	}

	public void setRegione(RegioneDTO regione) {
		this.regione = regione;
	}

	public PatronatoDTO getPatronato() {
		return patronato;
	}

	public void setPatronato(PatronatoDTO patronato) {
		this.patronato = patronato;
	}

	public SoggettoPubblicoDTO getSoggetto() {
		return soggetto;
	}

	public void setSoggetto(SoggettoPubblicoDTO soggetto) {
		this.soggetto = soggetto;
	}

}
