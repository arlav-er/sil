package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.CvProfDesiderateDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeRegioneDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvDatiPersonali_;
import it.eng.myportal.entity.CvProfDesiderate;
import it.eng.myportal.entity.CvProfDesiderate_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.decodifiche.DeOrario;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.decodifiche.sil.DeTurnoSil;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoSilHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Home object for domain model class CvProfDesiderate.
 * 
 * @see it.eng.myportal.entity.CvProfDesiderate
 * @author Turrini
 */
@Stateless
@LocalBean
public class CvProfDesiderateHome extends AbstractCurriculumEntityListHome<CvProfDesiderate, CvProfDesiderateDTO>
		implements InoDTOejb<CvProfDesiderate> {

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	CvProfDesiderateHome cvProfDesiderateHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	DeTurnoSilHome deTurnoSilHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeRegioneHome deRegioneHome;

	@EJB
	DeTrasfertaHome deTrasfertaHome;

	/**
	 * Restituisce l'entity CvProfDesiderate a partire dall'id
	 * 
	 * @param id Integer
	 * @return CvProfDesiderate
	 */
	public CvProfDesiderate findById(Integer id) {
		return findById(CvProfDesiderate.class, id);
	}

	public List<CvProfDesiderate> findProperByCurriculumId(Integer curriculumId) {
		List<CvProfDesiderate> professioniList = entityManager
				.createNamedQuery("CvProfessioniFindByCvId", CvProfDesiderate.class)
				.setHint("org.hibernate.cacheable", true).setParameter("cvId", curriculumId).getResultList();
		return professioniList;
	}

	@Override
	public CvProfDesiderateDTO toDTO(CvProfDesiderate entity) {
		if (entity == null)
			return null;
		CvProfDesiderateDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvProfessioniDesiderate());
		dto.setIdCvDatiPersonali(entity.getCvDatiPersonali().getIdCvDatiPersonali());

		if (entity.getDeMansioneMin() != null) {
			DeMansioneMinDTO deMansioneMinDTO = deMansioneMinHome
					.findDTOById(entity.getDeMansioneMin().getCodMansioneMin());
			dto.setDeMansioneMin(deMansioneMinDTO);

			DeMansioneDTO deMansDto = deMansioneHome
					.toDTO(entity.getDeMansioneMin().getDeMansioneSil().getDeMansione());
			dto.setDeMansione(deMansDto);
		}

		dto.setDescrizioneProfessione(entity.getDescrizioneProfessione());
		dto.setFlagDispTrasferte(entity.getFlagDispTrasferte());
		dto.setFlagDispMezzoProprio(entity.getFlagDispMezzoProprio());
		dto.setFlagEspSettore(entity.getFlagEspSettore());
		dto.setBreveDescrProfessione(entity.getBreveDescrProfessione());
		dto.setNoteComuni(entity.getNoteComuni());
		dto.setFlagDispMezziPubblici(entity.getFlagDispMezziPubblici());
		dto.setFlagPendolarismo(entity.getFlagPendolarismo());
		dto.setNumOrePercorrenza(entity.getNumOrePercorrenza());
		dto.setFlagMobilitaSett(entity.getFlagMobilitaSett());
		dto.setNoteDisponibilita(entity.getNoteDisponibilita());

		dto.setDispTerritorialeComuni(entity.getDispTerritorialeComuni());
		dto.setDispTerritorialeProvince(entity.getDispTerritorialeProvince());
		dto.setDispTerritorialeRegioni(entity.getDispTerritorialeRegioni());

		if (entity.getDeTrasferta() != null) {
			dto.setDeTrasferta(deTrasfertaHome.toDTO(entity.getDeTrasferta()));
		}

		// modalita lavoro
		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
			for (DeOrarioSil deOrarioSil : entity.getDeOrarioSils()) {
				dto.getListaOrario().add(deOrarioSilHome.toDTO(deOrarioSil));
			}
		} else {
			for (DeOrario deOrario : entity.getDeOrarios()) {
				dto.getListaOrario().add(deOrarioHome.toDTO(deOrario));
			}
		}

		// rapporti lavoro
		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
			for (DeContrattoSil deContrattoSil : entity.getDeContrattoSils()) {
				dto.getListaRapportoLavoro().add(deContrattoSilHome.toDTO(deContrattoSil));
			}
		} else {
			for (DeContratto deContratto : entity.getDeContrattos()) {
				dto.getListaRapportoLavoro().add(deContrattoHome.toDTO(deContratto));
			}
		}

		// turni
		if (entity.getDeTurnoSils() != null) {
			for (DeTurnoSil deTurnoSil : entity.getDeTurnoSils()) {
				dto.getListaTurno().add(deTurnoSilHome.toDTO(deTurnoSil));
			}
		}

		// Comuni, province, regioni, stati
		if (entity.getDeComuneList() != null) {
			for (DeComune comune : entity.getDeComuneList()) {
				dto.getListaComuni().add(deComuneHome.toDTO(comune));
			}
		}
		if (entity.getDeProvinciaList() != null) {
			for (DeProvincia provincia : entity.getDeProvinciaList()) {
				dto.getListaProvince().add(deProvinciaHome.toDTO(provincia));
			}
		}
		if (entity.getDeRegioneList() != null) {
			for (DeRegione regione : entity.getDeRegioneList()) {
				dto.getListaRegioni().add(deRegioneHome.toDTO(regione));
			}
		}
		if (entity.getDeStatoList() != null) {
			for (DeComune stato : entity.getDeStatoList()) {
				dto.getListaStati().add(deComuneHome.toDTO(stato));
			}
		}

		return dto;
	}

	@Override
	public CvProfDesiderate fromDTO(CvProfDesiderateDTO dto) {

		if (dto == null)
			return null;
		CvProfDesiderate entity = super.fromDTO(dto);

		entity.setIdCvProfessioniDesiderate(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));

		entity.setDeMansioneMin(deMansioneMinHome.findById(dto.getDeMansioneMin().getId()));

		entity.setDescrizioneProfessione(dto.getDescrizioneProfessione());
		entity.setFlagEspSettore(dto.getFlagEspSettore());
		entity.setBreveDescrProfessione(dto.getBreveDescrProfessione());
		entity.setFlagDispTrasferte(dto.getFlagDispTrasferte());
		entity.setFlagDispMezzoProprio(dto.getFlagDispMezzoProprio());
		entity.setNoteComuni(dto.getNoteComuni());
		entity.setFlagDispMezziPubblici(dto.getFlagDispMezziPubblici());
		entity.setFlagPendolarismo(dto.getFlagPendolarismo());
		entity.setNumOrePercorrenza(dto.getNumOrePercorrenza());
		entity.setFlagMobilitaSett(dto.getFlagMobilitaSett());
		entity.setNoteDisponibilita(dto.getNoteDisponibilita());

		entity.setDispTerritorialeComuni(dto.getDispTerritorialeComuni());
		entity.setDispTerritorialeProvince(dto.getDispTerritorialeProvince());
		entity.setDispTerritorialeRegioni(dto.getDispTerritorialeRegioni());

		if (dto.getDeTrasferta() != null && dto.getDeTrasferta().getId() != null) {
			entity.setDeTrasferta(deTrasfertaHome.findById(dto.getDeTrasferta().getId()));
		}

		List<IDecode> listaOrario = dto.getListaOrario();
		if (listaOrario != null) {
			if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
				Set<DeOrarioSil> deOrarioSils = new HashSet<DeOrarioSil>();
				for (IDecode modalita : listaOrario) {
					deOrarioSils.add(deOrarioSilHome.findById(modalita.getId()));
				}
				entity.setDeOrarioSils(deOrarioSils);
			} else {
				Set<DeOrario> deOrarios = new HashSet<DeOrario>();
				for (IDecode modalita : listaOrario) {
					deOrarios.add(deOrarioHome.findById(modalita.getId()));
				}
				entity.setDeOrarios(deOrarios);
			}
		}

		List<IDecode> listaRapporto = dto.getListaRapportoLavoro();
		if (listaRapporto != null) {
			if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
				Set<DeContrattoSil> deContrattoSils = new HashSet<DeContrattoSil>();
				for (IDecode rapporto : listaRapporto) {
					deContrattoSils.add(deContrattoSilHome.findById(rapporto.getId()));
				}
				entity.setDeContrattoSils(deContrattoSils);
			} else {
				Set<DeContratto> deContrattos = new HashSet<DeContratto>();
				for (IDecode rapporto : listaRapporto) {
					deContrattos.add(deContrattoHome.findById(rapporto.getId()));
				}
				entity.setDeContrattos(deContrattos);
			}
		}

		// Lista turni: solo nuovo flusso
		if (dto.getListaTurno() != null && !dto.getListaTurno().isEmpty()) {
			Set<DeTurnoSil> deTurnoSils = new HashSet<DeTurnoSil>();
			for (IDecode turno : dto.getListaTurno()) {
				deTurnoSils.add(deTurnoSilHome.findById(turno.getId()));
			}
			entity.setDeTurnoSils(deTurnoSils);
		}

		// Comuni, province, regioni, stati
		if (dto.getListaComuni() != null && !dto.getListaComuni().isEmpty()) {
			Set<DeComune> deComuneSet = new HashSet<DeComune>();
			for (DeComuneDTO comune : dto.getListaComuni()) {
				deComuneSet.add(deComuneHome.findById(comune.getId()));
			}
			entity.setDeComuneList(deComuneSet);
		}

		if (dto.getListaProvince() != null && !dto.getListaProvince().isEmpty()) {
			Set<DeProvincia> deProvinciaSet = new HashSet<DeProvincia>();
			for (DeProvinciaDTO provincia : dto.getListaProvince()) {
				deProvinciaSet.add(deProvinciaHome.findById(provincia.getId()));
			}
			entity.setDeProvinciaList(deProvinciaSet);
		}

		if (dto.getListaRegioni() != null && !dto.getListaRegioni().isEmpty()) {
			Set<DeRegione> deRegioneSet = new HashSet<DeRegione>();
			for (DeRegioneDTO regione : dto.getListaRegioni()) {
				deRegioneSet.add(deRegioneHome.findById(regione.getId()));
			}
			entity.setDeRegioneList(deRegioneSet);
		}

		if (dto.getListaStati() != null && !dto.getListaStati().isEmpty()) {
			Set<DeComune> deStatoSet = new HashSet<DeComune>();
			for (DeComuneDTO stato : dto.getListaStati()) {
				deStatoSet.add(deComuneHome.findById(stato.getId()));
			}
			entity.setDeStatoList(deStatoSet);
		}

		return entity;
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param curriculumId int
	 * @return Set<CvProfDesiderate>
	 */
	@Override
	protected Set<CvProfDesiderate> findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvProfDesiderates();
	}

	public void copyById(Integer pfPrincipalId, CvProfDesiderate cvProfDesiderate, CvDatiPersonali cvDatiPersonali) {
		if (cvProfDesiderate != null) {
			Date now = new Date();
			entityManager.detach(cvProfDesiderate);
			cvProfDesiderate.setIdCvProfessioniDesiderate(null);
			cvProfDesiderate.setCvDatiPersonali(cvDatiPersonali);
			cvProfDesiderate.setDtmIns(now);
			cvProfDesiderate.setDtmMod(now);
			cvProfDesiderate.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvProfDesiderate.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));

			// Converto i dati di disponibilità regionale comuni, se presenti, in una
			// stringa
			if (cvProfDesiderate.getDeComuneList() != null && !cvProfDesiderate.getDeComuneList().isEmpty()) {
				String dispComuniString = cvProfDesiderate.getDispTerritorialeComuni() == null ? ""
						: cvProfDesiderate.getDispTerritorialeComuni();
				for (DeComune comuneDisp : cvProfDesiderate.getDeComuneList()) {
					dispComuniString = dispComuniString + comuneDisp.getStrDenominazioneConTarga() + ", ";
				}
				dispComuniString = dispComuniString.substring(0, dispComuniString.length() - 2); // Tolgo l'ultima
																									// virgola
				cvProfDesiderate.setDispTerritorialeComuni(dispComuniString);
				cvProfDesiderate.setDeComuneList(new HashSet<DeComune>());
			}

			// Converto i dati di disponibilità regionale province, se presenti, in una
			// stringa
			if (cvProfDesiderate.getDeProvinciaList() != null && !cvProfDesiderate.getDeProvinciaList().isEmpty()) {
				String dispProvinceString = cvProfDesiderate.getDispTerritorialeProvince() == null ? ""
						: cvProfDesiderate.getDispTerritorialeProvince();
				for (DeProvincia provinciaDisp : cvProfDesiderate.getDeProvinciaList()) {
					dispProvinceString = dispProvinceString + provinciaDisp.getDenominazione() + ", ";
				}
				dispProvinceString = dispProvinceString.substring(0, dispProvinceString.length() - 2);
				cvProfDesiderate.setDispTerritorialeProvince(dispProvinceString);
				cvProfDesiderate.setDeProvinciaList(new HashSet<DeProvincia>());
			}

			// Converto i dati di disponibilità regionale regioni, se presenti, in una
			// stringa
			if (cvProfDesiderate.getDeRegioneList() != null && !cvProfDesiderate.getDeRegioneList().isEmpty()) {
				String dispRegioniString = cvProfDesiderate.getDispTerritorialeRegioni() == null ? ""
						: cvProfDesiderate.getDispTerritorialeRegioni();
				for (DeRegione regioneDisp : cvProfDesiderate.getDeRegioneList()) {
					dispRegioniString = dispRegioniString + regioneDisp.getDenominazione() + ", ";
				}
				dispRegioniString = dispRegioniString.substring(0, dispRegioniString.length() - 2);
				cvProfDesiderate.setDispTerritorialeRegioni(dispRegioniString);
				cvProfDesiderate.setDeRegioneList(new HashSet<DeRegione>());
			}

			persist(cvProfDesiderate);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_professione_desiderata associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	public int countProfDesiderateByCurriculumId(int curriculumId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		Root<CvProfDesiderate> f = c.from(CvProfDesiderate.class);
		Join<CvProfDesiderate, CvDatiPersonali> j = f.join(CvProfDesiderate_.cvDatiPersonali);
		Predicate principalPredicate = cb.equal(j.get(CvDatiPersonali_.idCvDatiPersonali), curriculumId);
		c = c.select(cb.count(f));
		c = c.where(principalPredicate);
		Long singleResult = entityManager.createQuery(c).getSingleResult();
		return singleResult.intValue();
	}

	public List<CvProfDesiderateDTO> massivePersistByListaDeMansMinDTO(CvProfDesiderateDTO data,
			List<CvProfDesiderateDTO> list, Integer principalId) {
		List<DeMansioneMinDTO> lMans = data.getListaDeMansioneMin();
		Integer curriculumId = data.getIdCvDatiPersonali();
		// CvProfDesiderateDTO cvProfDes = null;
		CvProfDesiderateDTO dto = null;
		Set<String> sMansPresent = new HashSet<String>();
		if (list != null) {
			for (CvProfDesiderateDTO cvProfDes : list) {
				sMansPresent.add(cvProfDes.getDeMansioneMin().getId());
			}
		}
		List<CvProfDesiderateDTO> lista = new ArrayList<CvProfDesiderateDTO>();
		if (lMans != null)
			for (DeMansioneMinDTO deManMin : lMans) {
				if (sMansPresent.contains(deManMin.getId())) {
					continue;
				}
				dto = new CvProfDesiderateDTO();

				dto.setId(null);
				dto.setIdCvDatiPersonali(curriculumId);

				dto.setDeMansioneMin(deManMin);

				dto.setDescrizioneProfessione(data.getDescrizioneProfessione());
				dto.setFlagDispTrasferte(data.getFlagDispTrasferte());
				dto.setFlagDispMezzoProprio(data.getFlagDispMezzoProprio());
				dto.setFlagEspSettore(data.getFlagEspSettore());
				dto.setBreveDescrProfessione(data.getBreveDescrProfessione());
				dto.setDeTrasferta(data.getDeTrasferta());

				// modalita lavoro
				List<IDecode> listaOrario = data.getListaOrario();
				dto.setListaOrario(listaOrario);

				// rapporti lavoro
				List<IDecode> listaRapportoLavoro = data.getListaRapportoLavoro();
				dto.setListaRapportoLavoro(listaRapportoLavoro);

				// turni
				List<IDecode> listaTurno = data.getListaTurno();
				dto.setListaTurno(listaTurno);

				// Disponibilità territoriali
				dto.setListaComuni(data.getListaComuni());
				dto.setListaProvince(data.getListaProvince());
				dto.setListaRegioni(data.getListaRegioni());
				dto.setListaStati(data.getListaStati());
				dto.setDispTerritorialeComuni(data.getDispTerritorialeComuni());
				dto.setDispTerritorialeProvince(data.getDispTerritorialeProvince());
				dto.setDispTerritorialeRegioni(data.getDispTerritorialeRegioni());

				dto.setDtmMod(new Date());
				dto = persistDTO(dto, principalId);
				lista.add(dto);
			}
		return lista;
	}

	@Override
	public CvProfDesiderate persist(CvProfDesiderate entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}

	@Override
	public void remove(CvProfDesiderate entity) {
		CvProfDesiderate entityCvProfessione = findById(entity.getIdCvProfessioniDesiderate());
		super.remove(entityCvProfessione);
	}

	@Override
	public CvProfDesiderate merge(CvProfDesiderate in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

}
