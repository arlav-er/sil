package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.RegisterSoggettoPubblicoDTO;
import it.eng.myportal.dtos.SoggettoPubblicoDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.entity.SoggettoPubblico;
import it.eng.myportal.entity.SoggettoPubblicoImport;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

@Stateless
public class SoggettoPubblicoHome extends AbstractUpdatableHome<SoggettoPubblico, SoggettoPubblicoDTO> {

	@EJB
	PtScrivaniaHome ptScrivaniaHome;

	@EJB
	PtPortletHome ptPortletHome;

	public SoggettoPubblico findByUserName(String name) {
		PfPrincipal pf = pfPrincipalHome.findAbilitatoByUsername(name);
		if (pf == null || pf.getSoggettoPubbsForIdPfPrincipal().size() == 0)
			return null;

		return pf.getSoggettoPubbsForIdPfPrincipal().iterator().next();
	}

	public SoggettoPubblico findById(Integer id) {
		SoggettoPubblico obj = findById(SoggettoPubblico.class, id);
		return obj;
	}

	@Override
	public SoggettoPubblicoDTO toDTO(SoggettoPubblico entity) {
		if (entity == null)
			return null;
		SoggettoPubblicoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdSoggettoPubblico());
		PfPrincipal pfPrincipal = entity.getPfPrincipal();
		dto.setIdPfPrincipal(pfPrincipal.getIdPfPrincipal());

		// richiedente
		dto.setNome(entity.getNome());
		dto.setCognome(entity.getCognome());
		dto.setStileSelezionato(entity.getPfPrincipal().getStileSelezionato());
		dto.setEmail(entity.getEmail());
		dto.setInizio(entity.getDtmInizio());
		dto.setFine(entity.getDtmFine());
		dto.setRagioneSociale(entity.getRagioneSociale());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setTelefono(entity.getTelefono());

		return dto;
	}

	@Override
	public SoggettoPubblico fromDTO(SoggettoPubblicoDTO dto) {
		if (dto == null)
			return null;
		SoggettoPubblico entity = super.fromDTO(dto);

		entity.setidSoggettoPubblico(dto.getId());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));
		entity.setNome(dto.getNome());
		entity.setCognome(dto.getCognome());
		entity.setEmail(dto.getEmail());
		entity.setDtmInizio(dto.getInizio());
		entity.setDtmFine(dto.getFine());

		entity.setRagioneSociale(dto.getRagioneSociale());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setTelefono(dto.getTelefono());

		return entity;
	}

	public SoggettoPubblicoDTO findDTOByUserName(String name) {
		return toDTO(findByUserName(name));
	}

	public void register(RegisterSoggettoPubblicoDTO register, Boolean abilitatoSare) {
		// l'utente che registra quello nuovo è l'amministratore
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();

		// crea il principal
		PfPrincipal principalSoggetto = pfPrincipalHome.registerSoggettoPubblico(register, administrator, now,
				abilitatoSare, true);

		SoggettoPubblico sogg = new SoggettoPubblico();
		sogg.setPfPrincipal(principalSoggetto);
		sogg.setPfPrincipalIns(administrator);
		sogg.setPfPrincipalMod(administrator);
		sogg.setNome(register.getNome());
		sogg.setCognome(register.getCognome());
		sogg.setEmail(register.getEmail());

		sogg.setRagioneSociale(register.getRagioneSociale());
		sogg.setCodiceFiscale(register.getCodiceFiscale());
		sogg.setTelefono(register.getTelefono());

		sogg.setDtmIns(new Date());
		sogg.setDtmMod(new Date());

		sogg.setDtmInizio(register.getDtmInizio());
		sogg.setDtmFine(register.getDtmFine());

		List<PtPortlet> portlets = null;
		portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.SOGGETTOPUBBLICO);

		if (portlets != null) {
			for (int i = 0; i < portlets.size(); i++) {
				PtPortlet iesimaPortlet = portlets.get(i);

				PtScrivania iesimaScrivania = new PtScrivania();
				iesimaScrivania.setPfPrincipal(principalSoggetto);
				iesimaScrivania.setFlagRidotta(false);
				iesimaScrivania.setFlagVisualizza(true);
				iesimaScrivania.setPtPortlet(iesimaPortlet);
				iesimaScrivania.setDtmIns(new Date());
				iesimaScrivania.setDtmMod(new Date());
				iesimaScrivania.setPfPrincipalIns(principalSoggetto);
				iesimaScrivania.setPfPrincipalMod(principalSoggetto);

				iesimaScrivania.setPosizione(i % 5);
				iesimaScrivania.setOptColonna((i % 2 == 0) ? "L" : "R");

				ptScrivaniaHome.persist(iesimaScrivania);

				// ptScrivaniaHome.findPortletsScrivania(principalCoordinatore.getIdPfPrincipal());
			}
		}

		persist(sogg);

	}

	public List<SoggettoPubblicoImport> findAllImport() {
		TypedQuery<SoggettoPubblicoImport> q = entityManager.createNamedQuery("findAllSoggetti",
				SoggettoPubblicoImport.class);
		return q.getResultList();
	}

	public RegisterSoggettoPubblicoDTO portingSoggettoPubblicoRER(SoggettoPubblicoImport soggImport) {

		boolean checkUsername = true;
		boolean checkMail = true;
		RegisterSoggettoPubblicoDTO soggetto = new RegisterSoggettoPubblicoDTO();
		// certificatore_info
		soggetto.setNome(soggImport.getNome());
		soggetto.setCognome(soggImport.getCognome());
		soggetto.setCodiceFiscale(soggImport.getStrCodiceFiscale());
		soggetto.setRagioneSociale(StringUtils.abbreviate(soggImport.getStrRagioneSociale(), 90));
		soggetto.setTelefono(soggImport.getTelefono());

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.YEAR, 1);

		soggetto.setDtmInizio(new Date());
		soggetto.setDtmFine(c.getTime());

		// pf_principal
		soggetto.setDomanda("Il mio CF?");
		soggetto.setRisposta(soggImport.getStrCodiceFiscale());
		soggetto.setAcceptInformativa(true);
		soggetto.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));

		// controllo esistenza CF
		PfPrincipal utenteSameUsername = pfPrincipalHome.findByUsername(soggImport.getStrCodiceFiscale());
		if (utenteSameUsername != null) {
			checkUsername = false;
		}

		// controllo esistenza EMAIL
		List<PfPrincipal> utentiSameMail = pfPrincipalHome.findByEmail(soggImport.getEmail());
		if (!utentiSameMail.isEmpty()) {
			checkMail = false;
		}

		if (checkMail && checkUsername) {
			soggetto.setUsername(soggImport.getStrCodiceFiscale());
			String password = Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH);
			soggetto.setPassword(password);
			soggetto.setPasswordConfirm(password);
			soggetto.setEmail(soggImport.getEmail());

			register(soggetto, false);

			return soggetto;
		} else {
			log.error("PORTING CERTIFICATORI: utente " + soggetto.getCodiceFiscale()
					+ " non inserito. email duplicata=" + checkMail + " o username duplicato=" + checkUsername);
			return null;
		}

	}

}
