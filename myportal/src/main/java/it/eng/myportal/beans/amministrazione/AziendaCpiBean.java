package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.RegisterProvinciaDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.dtos.SedeDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.ProvinciaHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.utils.Utils;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class AziendaCpiBean extends AbstractBaseBean implements Serializable {

	@EJB
	DeCpiHome deCpiHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	ProvinciaHome provinciaHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String EMILIA_ROMAGNA = "8";
	private final String UMBRIA = "10";

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
	}
	
	public void createAllEmiliaRomagna() {
		createAll(EMILIA_ROMAGNA);
	}

	public void createAllUmbria() {
		createAll(UMBRIA);
	}

	private void createAll(String codRegione) {
		int num = 0;
		List<DeCpiDTO> cpi = deCpiHome.findDTOByCodRegione(codRegione);
		for (Iterator<DeCpiDTO> iterator = cpi.iterator(); iterator.hasNext();) {
			DeCpiDTO deCpiDTO = (DeCpiDTO) iterator.next();

			PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOByUsername(deCpiDTO.getId());
			if (pfPrincipalDTO == null) {

				String mail = deCpiDTO.getEmail();
				if (mail == null || ("").equals(mail)) {
					mail = "cpi@cpi.it";
				}

				RegisterAziendaDTO ret = new RegisterAziendaDTO();
				ret.setAcceptInformativa(true);
				ret.setRichiestaAccessoSARE(false);
				ret.setFlgAbilitato(true);
				ret.setActivateToken(null);
				ret.setAttivo(true);
				// ret.setCap(deCpiDTO.getCap()==null?"0":deCpiDTO.getCap());
				// ret.setIndirizzo(deCpiDTO.getIndirizzo()==null?"-":deCpiDTO.getIndirizzo());
				ret.setCodiceFiscale(deCpiDTO.getId());
				ret.setCognomeRic(deCpiDTO.getDescrizione());
				ret.setNomeRic(deCpiDTO.getId());

				DeComuneDTO com = deComuneHome.findDTOById(deCpiDTO.getCodCom()); //
				ret.setComune(com);

				ret.setDomanda("Qual e' il tuo codice cpi?");
				ret.setEmail(mail);
				ret.setEmailConfirm(mail);

				ret.setPassword(deCpiDTO.getId());
				ret.setPasswordConfirm(deCpiDTO.getId());

				ret.setRagioneSociale("CPI DI " + deCpiDTO.getDescrizione());

				ret.setRisposta(deCpiDTO.getId());

				// SEDE OPERATIVA
				SedeDTO sedeOperativa = new SedeDTO();
				sedeOperativa.setCap(deCpiDTO.getCap() == null ? "0" : deCpiDTO.getCap());
				DeComuneDTO comOp = deComuneHome.findDTOById(deCpiDTO.getCodCom());
				sedeOperativa.setComune(comOp);
				sedeOperativa.setFax(deCpiDTO.getFax());
				sedeOperativa.setIndirizzo(deCpiDTO.getIndirizzo() == null ? "-" : deCpiDTO.getIndirizzo());
				sedeOperativa.setTelefono(deCpiDTO.getTel());
				ret.setSedeOperativa(sedeOperativa);

				ret.setUsername(deCpiDTO.getId());
				ret.setFlgAbilitato(true);
				ret.setAttivo(true);
				ret.setDtScadenzaPassword(new Date());

				aziendaInfoHome.register(ret, false);

				num = num + 1;
			}

		}
		FacesMessage doneMessage = new FacesMessage();
		String msg = "Inserite " + num + " azienda CPI";
		doneMessage.setDetail(msg);
		FacesContext.getCurrentInstance().addMessage("insert_azcpi", doneMessage);
	}

	public void createUtentiProvincia() {
		int num = 0;
		List<DeProvinciaDTO> provs = deProvinciaHome.findByRegione("8");
		for (Iterator<DeProvinciaDTO> iterator = provs.iterator(); iterator.hasNext();) {
			DeProvinciaDTO provincia = (DeProvinciaDTO) iterator.next();
			// DeComune com =
			// deComuneHome.findByDenominazione(provincia.getDescrizione());
			String targa = provincia.getTarga();

			PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOByUsername("redazione" + targa.toLowerCase());
			if (pfPrincipalDTO == null) {

				RegisterProvinciaDTO ret = new RegisterProvinciaDTO();
				ret.setProvincia(provincia);

				ret.setAcceptInformativa(true);
				ret.setAttivo(true);
				// ret.setCap(com.getCap());
				ret.setCodiceFiscale("-");
				ret.setCognome(targa);
				// ret.setComune(deComuneHome.toDTO(com));
				ret.setDomanda("Qual è la targa della tua provincia?");
				ret.setEmail("mail@mail.it");
				ret.setEmailConfirm("mail@mail.it");
				ret.setIndirizzo("-");
				ret.setNome("PROVINCIA DI " + provincia.getDescrizione());
				ret.setPassword("Redaz99" + targa.toLowerCase());
				ret.setPasswordConfirm("Redaz99" + targa.toLowerCase());
				ret.setRisposta(targa);
				ret.setTelefono("0");
				ret.setUsername("redazione" + targa.toLowerCase());

				provinciaHome.register(ret, false);
				num = num + 1;
			}

		}
		FacesMessage doneMessage = new FacesMessage();
		String msg = "Inserite " + num + " azienda CPI";
		doneMessage.setDetail(msg);
		FacesContext.getCurrentInstance().addMessage("insert_azcpi", doneMessage);
	}

	public void createUtenteProvinciaClicLavoro() {
		int num = 0;

		PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOByUsername("cliclavoro");
		if (pfPrincipalDTO == null) {
			DeProvinciaDTO provincia = deProvinciaHome.findDTOById("37");
			RegisterProvinciaDTO ret = new RegisterProvinciaDTO();
			ret.setProvincia(provincia);

			ret.setAcceptInformativa(true);
			ret.setAttivo(true);
			// ret.setCap(com.getCap());
			ret.setCodiceFiscale("-");
			ret.setCognome("CLICLAVORO");
			// ret.setComune(deComuneHome.toDTO(com));
			ret.setDomanda("Il mio username?");
			ret.setEmail("mail@mail.it");
			ret.setEmailConfirm("mail@mail.it");
			ret.setIndirizzo("-");
			ret.setNome("PROVINCIA");
			ret.setPassword(Utils.SHA1.encrypt("clicL4voro"));
			ret.setPasswordConfirm(Utils.SHA1.encrypt("clicL4voro"));
			ret.setRisposta("cliclavoro");
			ret.setTelefono("0");
			ret.setUsername("cliclavoro");

			provinciaHome.register(ret, false);
			num = num + 1;
		}

		FacesMessage doneMessage = new FacesMessage();
		String msg = "Inserito utente provincia cliclavoro";
		doneMessage.setDetail(msg);
		FacesContext.getCurrentInstance().addMessage("insert_ut_cliclavoro", doneMessage);
	}

	public void createUtenteAziendaClicLavoro() {
		// aziendaEng
		RegisterAziendaDTO ret = new RegisterAziendaDTO();
		ret.setAcceptInformativa(true);
		ret.setRichiestaAccessoSARE(false);
		ret.setFlgAbilitato(true);
		ret.setActivateToken(null);
		ret.setAttivo(true);
		ret.setCodiceFiscale("00000000000");
		ret.setCognomeRic("CLICLAVORO");
		ret.setNomeRic("AZIENDA");
		DeComuneDTO com = deComuneHome.findDTOById("H501"); //
		ret.setComune(com);
		ret.setDomanda("Qual e' il tuo username?");
		ret.setEmail("mail@mail.it");
		ret.setEmailConfirm("mail@mail.it");
		ret.setPassword("clicL4voro");
		ret.setPasswordConfirm("clicL4voro");
		ret.setRagioneSociale("Azienda CLICLAVORO");
		ret.setRisposta("azienda_cliclavoro");
		// SEDE OPERATIVA
		SedeDTO sedeOperativa = new SedeDTO();
		sedeOperativa.setCap("0");
		DeComuneDTO comOp = deComuneHome.findDTOById("H501");
		sedeOperativa.setComune(comOp);
		sedeOperativa.setFax("0");
		sedeOperativa.setIndirizzo("via");
		sedeOperativa.setTelefono("0");
		ret.setSedeOperativa(sedeOperativa);
		ret.setUsername("az_cliclavoro");
		ret.setFlgAbilitato(true);
		ret.setAttivo(true);
		ret.setDtScadenzaPassword(new Date());
		aziendaInfoHome.register(ret, false);

		FacesMessage doneMessage = new FacesMessage();
		String msg = "Inserito utente azienda cliclavoro";
		doneMessage.setDetail(msg);
		FacesContext.getCurrentInstance().addMessage("insert_az_cliclavoro", doneMessage);
	}

	/**
	 * TODO che fa sto metodo?
	 */
	public void createUtentiEng() {

		// String[] utentiEng = new String[3];
		// utentiEng[0] = "utenteEng";
		// utentiEng[1] = "aziendaEng";
		// utentiEng[2] = "provinciaEng";

		// aziendaEng
		RegisterAziendaDTO ret = new RegisterAziendaDTO();
		ret.setAcceptInformativa(true);
		ret.setRichiestaAccessoSARE(false);
		ret.setFlgAbilitato(true);
		ret.setActivateToken(null);
		ret.setAttivo(true);
		ret.setCodiceFiscale("00000000000");
		ret.setCognomeRic("ENG");
		ret.setNomeRic("AZIENDA");
		DeComuneDTO com = deComuneHome.findDTOById("A944"); //
		ret.setComune(com);
		ret.setDomanda("Qual e' il tuo username?");
		ret.setEmail("doncotic@eng.it");
		ret.setEmailConfirm("doncotic@eng.it");
		ret.setPassword("aziendaEng");
		ret.setPasswordConfirm("aziendaEng");
		ret.setRagioneSociale("Azienda test ENG");
		ret.setRisposta("aziendaEng");
		// SEDE OPERATIVA
		SedeDTO sedeOperativa = new SedeDTO();
		sedeOperativa.setCap("0");
		DeComuneDTO comOp = deComuneHome.findDTOById("A944");
		sedeOperativa.setComune(comOp);
		sedeOperativa.setFax("0");
		sedeOperativa.setIndirizzo("via calzolerie");
		sedeOperativa.setTelefono("0");
		ret.setSedeOperativa(sedeOperativa);
		ret.setUsername("aziendaEng");
		ret.setFlgAbilitato(true);
		ret.setAttivo(true);
		ret.setDtScadenzaPassword(new Date());
		aziendaInfoHome.register(ret, false);

		// provinciaEng
		RegisterProvinciaDTO retProv = new RegisterProvinciaDTO();
		retProv.setProvincia(deProvinciaHome.findDTOById("37"));
		retProv.setAcceptInformativa(true);
		retProv.setAttivo(true);
		retProv.setCodiceFiscale("-");
		retProv.setCognome("ENG");
		retProv.setDomanda("Qual è il tuo username?");
		retProv.setEmail("doncotic@eng.it");
		retProv.setEmailConfirm("doncotic@eng.it");
		retProv.setIndirizzo("-");
		retProv.setNome("Provincia test");
		retProv.setPassword("provinciaEng");
		retProv.setPasswordConfirm("provinciaEng");
		retProv.setRisposta("provinciaEng");
		retProv.setTelefono("0");
		retProv.setUsername("provinciaEng");
		provinciaHome.register(retProv, true);

		// utenteEng
		RegisterUtenteDTO retUt = new RegisterUtenteDTO();
		retUt.setAcceptInformativa(true);
		retUt.setActivateToken(null);
		retUt.setAttivo(true);
		retUt.setAutenticazioneForte(false);
		retUt.setCap("0");
		retUt.setCognome("ENG");
		retUt.setNome("LAVORATORE");
		retUt.setComune(deComuneHome.findDTOById("A944"));
		retUt.setDomanda("Qual è il tuo username?");
		retUt.setRisposta("utenteEng");
		retUt.setDomicilio(deComuneHome.findDTOById("A944"));
		retUt.setProvincia(deProvinciaHome.findDTOById("37"));
		retUt.setEmail("doncotic@eng.it");
		retUt.setEmailConfirm("doncotic@eng.it");
		retUt.setPassword("utenteEng");
		retUt.setPasswordConfirm("utenteEng");
		retUt.setUsername("utenteEng");
		utenteInfoHome.register(retUt, false);

		FacesMessage doneMessage = new FacesMessage();
		String msg = "Inserite utenti ENG";
		doneMessage.setDetail(msg);
		FacesContext.getCurrentInstance().addMessage("insert_azcpi", doneMessage);
	}

}
