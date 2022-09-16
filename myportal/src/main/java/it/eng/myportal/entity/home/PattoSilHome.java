package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.PattoSilDTO;
import it.eng.myportal.entity.PattoSil;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.enums.StatoAccettazionePattoEnum;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.ws.pattoonline.PattoType;

@Stateless
public class PattoSilHome extends AbstractUpdatableHome<PattoSil, PattoSilDTO> {

	@EJB
	private DeProvinciaHome deProvinciaHome;
	@EJB
	private PfPrincipalHome PfPrincipalHome;
	@EJB
	private WsEndpointHome wsEndpointHome;

	@Override
	public PattoSil findById(Integer id) {
		return findById(PattoSil.class, id);
	}

	public List<PattoSil> findByIdPfPrincipal(Integer idPfPrincipal) {
		return entityManager.createNamedQuery("findPattoSilByIdPrincipal", PattoSil.class)
				.setParameter("idPfPrincipal", idPfPrincipal).getResultList();
	}

	@Override
	public PattoSilDTO toDTO(PattoSil entity) {
		if (entity == null) {
			return null;
		}
		PattoSilDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdPattoSil());
		dto.setCodFis(entity.getCodFis());
		dto.setDtmIns(entity.getDtmIns());
		dto.setDtmMod(entity.getDtmIns());
		dto.setPdfPatto(entity.getPdfPatto());
		dto.setTipoAccettazione(entity.getTipoAccettazione());
		dto.setTsAccettazione(entity.getTsAccettazione());
		dto.setTsInvio(entity.getTsInvio());
		dto.setFlgPresaVisione(entity.getFlgPresaVisione());
		dto.setDtPatto(entity.getDtPatto());
		dto.setCodStatoAccettazione(entity.getCodStatoAccettazione());

		if (entity.getDeProvincia() != null) {
			DeProvincia deProvincia = deProvinciaHome.findById(entity.getDeProvincia().getCodProvincia());
			dto.setDeProvinciaDTO(deProvinciaHome.toDTO(deProvincia));
		}
		if (entity.getPfPrincipal() != null) {
			PfPrincipal pfPrincipal = pfPrincipalHome.findById(entity.getPfPrincipal().getIdPfPrincipal());
			dto.setPfPrincipalDTO(pfPrincipalHome.toDTO(pfPrincipal));
		}
		return dto;
	}

	@Override
	public PattoSil fromDTO(PattoSilDTO dto) {
		if (dto == null) {
			return null;
		}
		PattoSil entity = super.fromDTO(dto);
		entity.setIdPattoSil(dto.getId());
		entity.setCodFis(dto.getCodFis());
		entity.setTsInvio(dto.getTsInvio());
		entity.setTsAccettazione(dto.getTsAccettazione());
		entity.setPdfPatto(dto.getPdfPatto());
		entity.setTipoAccettazione(dto.getTipoAccettazione());
		entity.setCodStatoAccettazione(dto.getCodStatoAccettazione());
		entity.setDtPatto(dto.getDtPatto());
		entity.setFlgPresaVisione(dto.getFlgPresaVisione());

		entity.setPfPrincipal(pfPrincipalHome.fromDTO(dto.getPfPrincipalDTO()));
		entity.setDeProvincia(deProvinciaHome.fromDTO(dto.getDeProvinciaDTO()));
		return entity;
	}

	public void savePatto(PfPrincipal targetCittadino, byte[] pdfPatto, PattoType patto) {
		Date now = new Date();
		PattoSil transientInstance = new PattoSil();
		transientInstance.setPfPrincipal(targetCittadino);
		transientInstance.setPdfPatto(pdfPatto);
		transientInstance.setCodFis(patto.getCodiceFiscale().getValue());
		transientInstance.setCodStatoAccettazione(StatoAccettazionePattoEnum.D);
		transientInstance.setDeProvincia(deProvinciaHome.findById(patto.getCodProvinciaProv()));
		try {
			transientInstance.setDtPatto(Utils.gregorianDateToDate(patto.getDataPatto()));
		} catch (Exception e) {
			log.error("GRAVE: formato data errato " + patto.getDataPatto());
		}
		transientInstance.setTsInvio(new Date());
		transientInstance.setNumProtocollo(patto.getNumProtocollo());
		transientInstance.setNumAnnoProtocollo(patto.getAnnoProtocollo().intValue());

		transientInstance.setDtmIns(now);
		transientInstance.setDtmMod(now);
		transientInstance.setPfPrincipalIns(targetCittadino);
		transientInstance.setPfPrincipalMod(targetCittadino);

		persist(transientInstance);
		log.info("Persistito Patto da SIL ");

	}

	public void hasBeenSeen(PattoSil in) {
		in.setFlgPresaVisione(true);
		merge(in);
	}

	public PattoSil hasBeenSigned(PattoSil in, it.eng.myportal.entity.TipoAccettazioneEnum tipoAccettazione) {
		in.setCodStatoAccettazione(StatoAccettazionePattoEnum.A);
		in.setTipoAccettazione(tipoAccettazione);
		in.setTsAccettazione(new Date());
		return merge(in);
	}
	/**
	 * metti a scaduto se trascorso max scadenza
	 * 
	 * @param patto
	 * @return
	 */
	public PattoSil checkScadenza(PattoSil patto) {
		if (new Date().after(patto.getDataMaxAccettazione())
				&& StatoAccettazionePattoEnum.D.equals(patto.getCodStatoAccettazione())) {
			patto.setCodStatoAccettazione(StatoAccettazionePattoEnum.S);
			return merge(patto);
		}
		return patto;
	}

	public PattoSil findPatto(PfPrincipal targetCittadino, PattoType patto) {
		PattoSil ptSil = null;
		String hql = "from PattoSil pt  " + " WHERE pt.codFis = :codFis " + "   and pt.dtPatto = :dtPatto "
				+ "   and pt.deProvincia.codProvincia = :codProvincia " + "   and pt.numProtocollo = :numProtocollo "
				+ "   and pt.numAnnoProtocollo = :numAnnoProtocollo ";
		TypedQuery<PattoSil> query = entityManager.createQuery(hql, PattoSil.class);
		query.setParameter("codFis", patto.getCodiceFiscale().getValue());
		query.setParameter("dtPatto", patto.getDataPatto().toGregorianCalendar().getTime());
		query.setParameter("codProvincia", patto.getCodProvinciaProv());
		query.setParameter("numProtocollo", patto.getNumProtocollo());
		query.setParameter("numAnnoProtocollo", new Integer(patto.getAnnoProtocollo().intValue()));

		try {
			ptSil = (PattoSil) query.getSingleResult();
			return ptSil;
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
}
