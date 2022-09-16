package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.CertificatoreInfoDTO;
import it.eng.myportal.dtos.RegisterCertificatoreDTO;
import it.eng.myportal.entity.CertificatoreImport;
import it.eng.myportal.entity.CertificatoreInfo;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
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
public class CertificatoreInfoHome extends AbstractUpdatableHome<CertificatoreInfo, CertificatoreInfoDTO> {

	@EJB
	PtScrivaniaHome ptScrivaniaHome;

	@EJB
	PtPortletHome ptPortletHome;

	public CertificatoreInfo findByUserName(String name) {
		PfPrincipal pf = pfPrincipalHome.findAbilitatoByUsername(name);
		if (pf == null || pf.getCertificatoreInfo() == null) {
			return null;
		}
		return pf.getCertificatoreInfo();
	}

	public CertificatoreInfo findById(Integer id) {
		CertificatoreInfo obj = findById(CertificatoreInfo.class, id);
		return obj;
	}

	@Override
	public CertificatoreInfoDTO toDTO(CertificatoreInfo entity) {
		if (entity == null)
			return null;
		CertificatoreInfoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdPfPrincipal());
		dto.setPrincipal(pfPrincipalHome.toDTO(entity.getPfPrincipal()));

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
	public CertificatoreInfo fromDTO(CertificatoreInfoDTO dto) {
		if (dto == null)
			return null;
		CertificatoreInfo entity = super.fromDTO(dto);

		entity.setIdPfPrincipal(dto.getId());

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

	public CertificatoreInfoDTO findDTOByUserName(String name) {
		return toDTO(findByUserName(name));
	}

	public void register(RegisterCertificatoreDTO register, Boolean abilitatoSare) {
		// l'utente che registra quello nuovo è l'amministratore
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();

		// crea il principal
		PfPrincipal principalCertificatore = pfPrincipalHome.registerCertificatore(register, administrator, now,
				abilitatoSare, true);

		CertificatoreInfo cert = new CertificatoreInfo();
		cert.setIdPfPrincipal(principalCertificatore.getIdPfPrincipal());
		cert.setPfPrincipal(principalCertificatore);
		cert.setPfPrincipalIns(administrator);
		cert.setPfPrincipalMod(administrator);
		cert.setNome(register.getNome());
		cert.setCognome(register.getCognome());
		cert.setEmail(register.getEmail());

		cert.setRagioneSociale(register.getRagioneSociale());
		cert.setCodiceFiscale(register.getCodiceFiscale());
		cert.setTelefono(register.getTelefono());

		cert.setDtmIns(new Date());
		cert.setDtmMod(new Date());

		cert.setDtmInizio(register.getDtmInizio());
		cert.setDtmFine(register.getDtmFine());

		List<PtPortlet> portlets = null;
		portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.CERTIFIC);

		if (portlets != null) {
			for (int i = 0; i < portlets.size(); i++) {
				PtPortlet iesimaPortlet = portlets.get(i);

				PtScrivania iesimaScrivania = new PtScrivania();
				iesimaScrivania.setPfPrincipal(principalCertificatore);
				iesimaScrivania.setFlagRidotta(false);
				iesimaScrivania.setFlagVisualizza(true);
				iesimaScrivania.setPtPortlet(iesimaPortlet);
				iesimaScrivania.setDtmIns(new Date());
				iesimaScrivania.setDtmMod(new Date());
				iesimaScrivania.setPfPrincipalIns(principalCertificatore);
				iesimaScrivania.setPfPrincipalMod(principalCertificatore);

				iesimaScrivania.setPosizione(i % 5);
				iesimaScrivania.setOptColonna((i % 2 == 0) ? "L" : "R");

				ptScrivaniaHome.persist(iesimaScrivania);

				// ptScrivaniaHome.findPortletsScrivania(principalCoordinatore.getIdPfPrincipal());
			}
		}

		persist(cert);

	}

	public RegisterCertificatoreDTO portingCertificatoriRER(CertificatoreImport certificatoreImport) {

		boolean checkUsername = true;
		boolean checkMail = true;
		RegisterCertificatoreDTO certificatore = new RegisterCertificatoreDTO();
		// certificatore_info
		certificatore.setNome(certificatoreImport.getNome());
		certificatore.setCognome(certificatoreImport.getCognome());
		certificatore.setCodiceFiscale(certificatoreImport.getCodiceFiscale());
		certificatore.setRagioneSociale(StringUtils.abbreviate(certificatoreImport.getRagioneSociale(), 90));
		certificatore.setTelefono(certificatoreImport.getTelefono());

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.YEAR, 1);

		certificatore.setDtmInizio(new Date());
		certificatore.setDtmFine(c.getTime());

		// pf_principal
		certificatore.setDomanda("Il mio CF?");
		certificatore.setRisposta(certificatoreImport.getCodiceFiscale());
		certificatore.setAcceptInformativa(true);
		certificatore.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));

		// controllo esistenza CF
		PfPrincipal utenteSameUsername = pfPrincipalHome.findByUsername(certificatoreImport.getCodiceFiscale());
		if (utenteSameUsername != null) {
			checkUsername = false;
		}

		// controllo esistenza EMAIL
		List<PfPrincipal> utentiSameMail = pfPrincipalHome.findByEmail(certificatoreImport.getEmail());
		if (!utentiSameMail.isEmpty()) {
			checkMail = false;
		}

		if (checkMail && checkUsername) {
			certificatore.setUsername(certificatoreImport.getCodiceFiscale());
			String password = Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH);
			certificatore.setPassword(password);
			certificatore.setPasswordConfirm(password);
			certificatore.setEmail(certificatoreImport.getEmail());

			register(certificatore, false);

			return certificatore;
		} else {
			log.error("PORTING CERTIFICATORI: utente " + certificatoreImport.getCodiceFiscale()
					+ " non inserito. email duplicata=" + checkMail + " o username duplicato=" + checkUsername);
			return null;
		}

	}

	public List<CertificatoreImport> findAllImport() {
		TypedQuery<CertificatoreImport> q = entityManager.createNamedQuery("findAllCerificatori",
				CertificatoreImport.class);
		return q.getResultList();
	}

}
