package it.eng.myportal.entity.home;

import static it.eng.myportal.utils.ConstantsSingleton.INDIFFERENTE;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import it.eng.myportal.beans.UtilsBean;
import it.eng.myportal.dtos.DeTrasfertaDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.dtos.VaAltreInfoDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaAgevolazione;
import it.eng.myportal.entity.VaAltreInfo;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.DeAgevolazione;
import it.eng.myportal.entity.decodifiche.DeGenere;
import it.eng.myportal.entity.decodifiche.DeMotivoGenereSil;
import it.eng.myportal.entity.decodifiche.DeTrasferta;
import it.eng.myportal.entity.decodifiche.sil.DeAgevolazioneSil;
import it.eng.myportal.entity.decodifiche.sil.DeAreaSil;
import it.eng.myportal.entity.home.decodifiche.DeAbilitazioneGenHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneSilHome;
import it.eng.myportal.entity.home.decodifiche.DeAreaSilHome;
import it.eng.myportal.entity.home.decodifiche.DeGenereHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoGenereSilHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeRetribuzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;

/**
 * Home object for domain model class VaAltreInfo.
 * 
 * @see it.eng.myportal.entity.VaAltreInfo
 * @author Rodi A. iescone
 */
@Stateless
@LocalBean
public class VaAltreInfoHome extends AbstractVacancyEntityHome<VaAltreInfo, VaAltreInfoDTO> implements InoDTOejb<VaAltreInfo> {

	@EJB
	DeAbilitazioneGenHome deAbilitazioneGenHome;

	@EJB
	DeAgevolazioneHome deAgevolazioneHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeRetribuzioneHome vaDeRetribuzioneHome;

	@EJB
	VaAgevolazioneHome vaAgevolazioneHome;

	@EJB
	DeTrasfertaHome deTrasfertaHome;

	@EJB
	DeGenereHome deGenereHome;

	@EJB
	DeMotivoGenereSilHome deMotivoGenereSilHome;

	@EJB
	DeAreaSilHome deAreaSilHome;

	@EJB
	DeAgevolazioneSilHome deAgevolazioneSilHome;

	public VaAltreInfo findById(Integer id) {
		return findById(VaAltreInfo.class, id);
	}

	@Override
	public VaAltreInfo findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaAltreInfo();
	}

	@Override
	public VaAltreInfoDTO toDTO(VaAltreInfo entity) {
		if (entity == null)
			return null;
		VaAltreInfoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaDatiVacancy());
		dto.setIdVaDatiVacancy(entity.getVaDatiVacancy().getIdVaDatiVacancy());

		dto.setFuorisede(entity.getOpzFuorisede());
		if(UtilsBean.isStaticRER()) {
			dto.setTrasferta(entity.getOpzTrasferta());
		}
		
		dto.setAutomunito(entity.getOpzAutomunito());
		dto.setMotomunito(entity.getOpzMotomunito());
		dto.setVitto(entity.getOpzVitto());
		dto.setAlloggio(entity.getOpzAlloggio());

		DeTrasferta trasf = entity.getVaDatiVacancy().getDeTrasferta();
		if (trasf != null)
			dto.setTipoTrasferta(deTrasfertaHome.toDTO(trasf));

		Set<VaAgevolazione> agevolaziones = vaAgevolazioneHome.findByVacancyId(entity.getIdVaDatiVacancy());
		for (VaAgevolazione vaAgevolazione : agevolaziones) {
			if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(entity.getVaDatiVacancy().getOpzTipoDecodifiche())) {
				dto.getListaAgevolazioniDTO().add(deAgevolazioneSilHome.toDTO(vaAgevolazione.getDeAgevolazioneSil()));
			} else {
				dto.getListaAgevolazioniDTO().add(deAgevolazioneHome.toDTO(vaAgevolazione.getDeAgevolazione()));
			}
		}

		dto.setUlterioriRequisiti(entity.getUlterioriRequisiti());

		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(entity.getVaDatiVacancy().getIdVaDatiVacancy());
		dto.setOpzNullaOsta(vaDatiVacancy.getOpzNullaOsta());

		// Campi aggiunti per Trento (aprile 2017)
		dto.setOpzMilite(entity.getOpzMilite());
		dto.setNotaMotivoGenere(entity.getNotaMotivoGenere());
		dto.setLocalita(entity.getLocalita());

		if (entity.getDeGenere() != null) {
			dto.setDeGenere(deGenereHome.toDTO(entity.getDeGenere()));
		}

		if (entity.getDeMotivoGenereSil() != null) {
			dto.setDeMotivoGenereSil(deMotivoGenereSilHome.toDTO(entity.getDeMotivoGenereSil()));
		}

		if (entity.getDeAreaSil() != null) {
			dto.setDeAreaSil(deAreaSilHome.toDTO(entity.getDeAreaSil()));
		}

		return dto;
	}

	@Override
	public VaAltreInfo fromDTO(VaAltreInfoDTO dto) {
		if (dto == null)
			return null;
		VaAltreInfo entity = super.fromDTO(dto);
		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy());
		vaDatiVacancy.setOpzNullaOsta(dto.getOpzNullaOsta());
		vaDatiVacancy = vaDatiVacancyHome.merge(vaDatiVacancy);

		entity.setVaDatiVacancy(vaDatiVacancy);
		entity.setIdVaDatiVacancy(dto.getIdVaDatiVacancy());

		String fuorisede = dto.getFuorisede();
		entity.setOpzFuorisede(INDIFFERENTE.equals(fuorisede) ? null : fuorisede);
		
		if (UtilsBean.isStaticRER()) {
			String trasferta = dto.getTrasferta();
			entity.setOpzTrasferta(INDIFFERENTE.equals(trasferta) ? null : trasferta);
		}
		
		String automunito = dto.getAutomunito();
		entity.setOpzAutomunito(INDIFFERENTE.equals(automunito) ? null : automunito);

		String motomunito = dto.getMotomunito();
		entity.setOpzMotomunito(INDIFFERENTE.equals(motomunito) ? null : motomunito);

		String vitto = dto.getVitto();
		entity.setOpzVitto(INDIFFERENTE.equals(vitto) ? null : vitto);

		String alloggio = dto.getAlloggio();
		entity.setOpzAlloggio(INDIFFERENTE.equals(alloggio) ? null : alloggio);

		entity.setUlterioriRequisiti(dto.getUlterioriRequisiti());

		// Campi aggiunti per Trento (aprile 2017)
		entity.setOpzMilite(dto.getOpzMilite());
		entity.setNotaMotivoGenere(dto.getNotaMotivoGenere());
		entity.setLocalita(dto.getLocalita());

		if (dto.getDeGenere() != null && dto.getDeGenere().getId() != null) {
			DeGenere deGenereEntity = deGenereHome.findById(dto.getDeGenere().getId());
			entity.setDeGenere(deGenereEntity);
		}

		if (dto.getDeMotivoGenereSil() != null && dto.getDeMotivoGenereSil().getId() != null) {
			DeMotivoGenereSil deMotivoGenereSilEntity = deMotivoGenereSilHome.findById(dto.getDeMotivoGenereSil()
					.getId());
			entity.setDeMotivoGenereSil(deMotivoGenereSilEntity);
		}

		if (dto.getDeAreaSil() != null && dto.getDeAreaSil().getId() != null) {
			DeAreaSil deAreaSilEntity = deAreaSilHome.findById(dto.getDeAreaSil().getId());
			entity.setDeAreaSil(deAreaSilEntity);
		}

		return entity;
	}

	public VaAltreInfoDTO mergeDTO(VaAltreInfoDTO data, Integer idPrincipalMod) {
		updateAgevolazioni(data, idPrincipalMod);
		return super.mergeDTO(data, idPrincipalMod);
	}

	/**
	 * Inserisce le agevolazioni collegate alla vacancy
	 * 
	 * @param data
	 * @param idPrincipalMod
	 */
	private void updateAgevolazioni(VaAltreInfoDTO data, Integer idPrincipalMod) {
		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(data.getIdVaDatiVacancy());
		Set<VaAgevolazione> vecchieAgevolazioni = vaDatiVacancy.getVaAgevolaziones();

		// cancella le vecchie agevolazioni
		if (vecchieAgevolazioni != null && vecchieAgevolazioni.size() > 0) {
			for (VaAgevolazione vaAgevolazione : vecchieAgevolazioni) {
				vaAgevolazioneHome.remove(vaAgevolazione);
			}
		}

		// inserisci le nuove agevolazioni
		persistAgevolazioni(data, idPrincipalMod);
	}

	@Override
	public VaAltreInfoDTO persistDTO(VaAltreInfoDTO data, Integer idPrincipalIns) {
		persistAgevolazioni(data, idPrincipalIns);
		return super.persistDTO(data, idPrincipalIns);
	}

	/**
	 * Rende persistenti le modifiche apportate alle agevolazioni collegate alla vacancy
	 * 
	 * @param data
	 * @param idPrincipalMod
	 */
	private void persistAgevolazioni(VaAltreInfoDTO data, Integer idPrincipal) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipal);
		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(data.getIdVaDatiVacancy());
		List<IDecode> nuoveAgevolazioni = data.getListaAgevolazioniDTO();
		Set<VaAgevolazione> insertAgevolazioni = new HashSet<VaAgevolazione>();

		// inserisci le agevolazioni
		if (nuoveAgevolazioni != null && nuoveAgevolazioni.size() > 0) {
			for (IDecode iDecode : nuoveAgevolazioni) {
				VaAgevolazione agevolazione = new VaAgevolazione(null, pfPrincipal, pfPrincipal, vaDatiVacancy, null,
						new Date(), new Date());
				if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vaDatiVacancy.getOpzTipoDecodifiche())) {
					DeAgevolazioneSil deAgevolazioneSil = deAgevolazioneSilHome.findById(iDecode.getId());
					agevolazione.setDeAgevolazioneSil(deAgevolazioneSil);
				} else {
					DeAgevolazione deAgevolazione = deAgevolazioneHome.findById(iDecode.getId());
					agevolazione.setDeAgevolazione(deAgevolazione);
				}
				vaAgevolazioneHome.persist(agevolazione);
				insertAgevolazioni.add(agevolazione);
			}
		}
		vaDatiVacancy.setVaAgevolaziones(insertAgevolazioni);
	}

	/**
	 * Inserisce le agevolazioni collegate alla vacancy
	 * 
	 * @param data
	 * @param idPrincipalMod
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateTipoTrasferta(VaAltreInfoDTO data, Integer idPrincipalMod) {
		// PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipalMod);
		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(data.getIdVaDatiVacancy());
		// DeTrasferta oldTrasf = vaDatiVacancy.getDeTrasferta();
		DeTrasfertaDTO newTrasf = data.getTipoTrasferta();
		if (newTrasf == null || newTrasf.getId() == null) {
			vaDatiVacancy.setDeTrasferta(null);
		} else {
			vaDatiVacancy.setDeTrasferta(deTrasfertaHome.findById(newTrasf.getId()));
		}
		vaDatiVacancyHome.persist(vaDatiVacancy);

	}

	public void copyById(Integer pfPrincipalId, VaAltreInfo vaAltreInfo, VaDatiVacancy vaDatiVacancy) {
		if (vaAltreInfo == null) {
			String msg = "Impossibile trovare l'entity corrispondente a vaAltreInfo con id "
					+ vaDatiVacancy.getIdVaDatiVacancy();
			log.error(msg);
			return;
		}
		Date now = new Date();
		entityManager.detach(vaAltreInfo);
		vaAltreInfo.setVaDatiVacancy(vaDatiVacancy);
		vaAltreInfo.setDtmIns(now);
		vaAltreInfo.setDtmMod(now);
		vaAltreInfo.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
		vaAltreInfo.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
		persist(vaAltreInfo);
	}

	@Override
	public void removeById(Integer deleteId, Integer idPfPrincipalMod) {

		VaDatiVacancy dati = vaDatiVacancyHome.findById(deleteId);
		dati.setDtmMod(new Date());
		dati.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
		vaDatiVacancyHome.merge(dati);

		VaAltreInfo altreInfo = dati.getVaAltreInfo();
		if (altreInfo != null) {
			entityManager.remove(altreInfo);
		}

		Set<VaAgevolazione> agev = dati.getVaAgevolaziones();
		for (Iterator<VaAgevolazione> iterator = agev.iterator(); iterator.hasNext();) {
			VaAgevolazione vaAge = iterator.next();
			entityManager.remove(vaAge);
		}

	}
 

	@Override
	public VaAltreInfo merge(VaAltreInfo entity, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public VaAltreInfo persist(VaAltreInfo entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}
}
