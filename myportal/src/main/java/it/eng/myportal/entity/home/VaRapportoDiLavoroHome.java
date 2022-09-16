package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.dtos.VaRapportoDiLavoroDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaContratto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaOrario;
import it.eng.myportal.entity.VaPubblicazione;
import it.eng.myportal.entity.VaRetribuzione;
import it.eng.myportal.entity.VaTurno;
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.decodifiche.DeOrario;
import it.eng.myportal.entity.decodifiche.DeRetribuzione;
import it.eng.myportal.entity.decodifiche.DeTurno;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.decodifiche.sil.DeTurnoSil;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DeRetribuzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoSilHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home per la gestione della atb RAPPORTO DI LAVORO della vacancy
 * 
 * @author Rodi A.
 */
@Stateless(name = "VaRapportoDiLavoroHome")
@LocalBean
public class VaRapportoDiLavoroHome implements IVacancyEntityHome<VaRapportoDiLavoroDTO> {

	/**
	 * Logger di classe. Utilizzare questo log per scrivere messaggi di errore, info e debug.
	 */
	protected static Log log = LogFactory.getLog(VaRapportoDiLavoroHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	VaRetribuzioneHome vaRetribuzioneHome;

	@EJB
	VaPatentinoHome vaPatentinoHome;

	@EJB
	DeRetribuzioneHome deRetribuzioneHome;

	@EJB
	DeTurnoHome deTurnoHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	private VaTurnoHome vaTurnoHome;

	@EJB
	private VaOrarioHome vaOrarioHome;

	@EJB
	private VaContrattoHome vaContrattoHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	DeTurnoSilHome deTurnoSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	VaPubblicazioneHome vaPubblicazioneHome;

	@Override
	public VaRapportoDiLavoroDTO persistDTO(VaRapportoDiLavoroDTO data, Integer idPrincipalIns) {

		return data; // TODO
	}

	@Override
	public VaRapportoDiLavoroDTO mergeDTO(VaRapportoDiLavoroDTO data, Integer idPrincipalMod) {
		Date now = new Date();
		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(data.getIdVaDatiVacancy());
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipalMod);

		// AGGIORNAMENTO RETRIBUZIONE
		if (data.getRetribuzione().getId() != null) {
			DeRetribuzione deRetribuzione = deRetribuzioneHome.findById(data.getRetribuzione().getId());
			VaRetribuzione vaRetribuzione = vaRetribuzioneHome.findByVacancyId(data.getIdVaDatiVacancy());
			if (vaRetribuzione == null) {
				log.debug("Creazione di VaRetribuzione per la vacancy con id " + vaDatiVacancy.getIdVaDatiVacancy());
				vaRetribuzione = new VaRetribuzione(pfPrincipal, pfPrincipal, vaDatiVacancy, deRetribuzione, now, now);
				vaRetribuzione.setIdVaDatiVacancy(vaDatiVacancy.getIdVaDatiVacancy());
				vaRetribuzioneHome.persist(vaRetribuzione);
			} else {
				log.debug("Aggiornamento di VaRetribuzione per la vacancy con id " + vaDatiVacancy.getIdVaDatiVacancy());
				vaRetribuzione.setDtmMod(now);
				vaRetribuzione.setPfPrincipalMod(pfPrincipal);
				vaRetribuzione.setDeRetribuzione(deRetribuzione);
				vaRetribuzioneHome.merge(vaRetribuzione);
			}
			vaDatiVacancy.setVaRetribuzione(vaRetribuzione);
		}
		mergeTipologiaContratto(data, pfPrincipal, vaDatiVacancy, now);
		mergeTurni(data, pfPrincipal, vaDatiVacancy, now);
		mergeOrari(data, pfPrincipal, vaDatiVacancy, now);

		// AGGIORNAMENTO CAMPI SU VA_PUBBLICAZIONE
		if (vaDatiVacancy.getVaPubblicazione() == null) {
			VaPubblicazione nuovaPubblicazione = new VaPubblicazione();
			nuovaPubblicazione.setContratto(data.getInfoContratti());
			nuovaPubblicazione.setOrario(data.getInfoOrariTurni());
			nuovaPubblicazione.setDtmIns(now);
			nuovaPubblicazione.setDtmMod(now);
			nuovaPubblicazione.setPfPrincipalIns(pfPrincipal);
			nuovaPubblicazione.setPfPrincipalMod(pfPrincipal);
			nuovaPubblicazione.setVaDatiVacancy(vaDatiVacancy);
			vaPubblicazioneHome.persist(nuovaPubblicazione);
		} else {
			vaDatiVacancy.getVaPubblicazione().setContratto(data.getInfoContratti());
			vaDatiVacancy.getVaPubblicazione().setOrario(data.getInfoOrariTurni());
			vaDatiVacancy.getVaPubblicazione().setDtmMod(now);
			vaDatiVacancy.getVaPubblicazione().setPfPrincipalMod(pfPrincipal);
			vaPubblicazioneHome.merge(vaDatiVacancy.getVaPubblicazione());
		}

		// MERGE OGGETTO PRINCIPALE
		vaDatiVacancy.setDtmMod(now);
		vaDatiVacancy.setPfPrincipalMod(pfPrincipal);
		vaDatiVacancyHome.merge(vaDatiVacancy);

		return toDTO(vaDatiVacancy);
	}

	/**
	 * ===AGGIORNAMENTO TIPOLOGIE DI CONTRATTO===
	 */
	public void mergeTipologiaContratto(VaRapportoDiLavoroDTO data, PfPrincipal pfPrincipal,
			VaDatiVacancy vaDatiVacancy, Date now) {
		List<VaContratto> vecchieTipologiaDiContratto = vaDatiVacancy.getVaContrattos();
		List<IDecode> nuoveTipologieContratto = data.getTipologieContratto();
		List<VaContratto> insertTipologieContratto = new ArrayList<VaContratto>();
		// cancella i vecchi turni
		if (vecchieTipologiaDiContratto != null && vecchieTipologiaDiContratto.size() > 0) {
			for (VaContratto vaTurno : vecchieTipologiaDiContratto) {
				vaContrattoHome.remove(vaTurno);
			}
		}
		// inserisci i nuovi turni
		if (nuoveTipologieContratto != null && nuoveTipologieContratto.size() > 0) {
			for (IDecode iDecode : nuoveTipologieContratto) {
				VaContratto vaContratto = new VaContratto();
				vaContratto.setVaDatiVacancy(vaDatiVacancy);
				vaContratto.setDtmIns(now);
				vaContratto.setDtmMod(now);
				vaContratto.setPfPrincipalIns(pfPrincipal);
				vaContratto.setPfPrincipalMod(pfPrincipal);
				if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vaDatiVacancy.getOpzTipoDecodifiche())) {
					DeContrattoSil deContrattoSil = deContrattoSilHome.findById(iDecode.getId());
					vaContratto.setDeContrattoSil(deContrattoSil);
				} else {
					DeContratto deContratto = deContrattoHome.findById(iDecode.getId());
					vaContratto.setDeContratto(deContratto);
				}
				vaContrattoHome.persist(vaContratto);
				insertTipologieContratto.add(vaContratto);
			}
		}
		vaDatiVacancy.setVaContrattos(insertTipologieContratto);
	}

	/**
	 * 
	 * ===AGGIORNAMENTO TURNI===
	 * 
	 */
	public void mergeTurni(VaRapportoDiLavoroDTO data, PfPrincipal pfPrincipal, VaDatiVacancy vaDatiVacancy, Date now) {
		Set<VaTurno> vecchiTurni = vaDatiVacancy.getVaTurnos();
		List<IDecode> nuoviTurni = data.getTurni();
		Set<VaTurno> insertTurni = new HashSet<VaTurno>();
		// cancella i vecchi turni
		if (vecchiTurni != null && vecchiTurni.size() > 0) {
			for (VaTurno vaTurno : vecchiTurni) {
				vaTurnoHome.remove(vaTurno);
			}
		}
		// inserisci i nuovi turni
		if (nuoviTurni != null && nuoviTurni.size() > 0) {
			for (IDecode iDecode : nuoviTurni) {
				VaTurno turno = new VaTurno(null, pfPrincipal, pfPrincipal, null, vaDatiVacancy, now, now);
				if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vaDatiVacancy.getOpzTipoDecodifiche())) {
					DeTurnoSil deTurnoSil = deTurnoSilHome.findById(iDecode.getId());
					turno.setDeTurnoSil(deTurnoSil);
				} else {
					DeTurno deTurno = deTurnoHome.findById(iDecode.getId());
					turno.setDeTurno(deTurno);
				}
				vaTurnoHome.persist(turno);
				insertTurni.add(turno);
			}
		}
		vaDatiVacancy.setVaTurnos(insertTurni);
	}

	/**
	 * 
	 * ===AGGIORNAMENTO ORARI===
	 * 
	 */
	public void mergeOrari(VaRapportoDiLavoroDTO data, PfPrincipal pfPrincipal, VaDatiVacancy vaDatiVacancy, Date now) {
		Set<VaOrario> vecchiOrari = vaDatiVacancy.getVaOrarios();
		List<IDecode> nuoviOrari = data.getOrari();
		Set<VaOrario> insertOrari = new HashSet<VaOrario>();
		// cancella i vecchi orari
		if (vecchiOrari != null && vecchiOrari.size() > 0) {
			for (VaOrario vaTurno : vecchiOrari) {
				vaOrarioHome.remove(vaTurno);
			}
		}
		// inserisci i nuovi orari
		if (nuoviOrari != null && nuoviOrari.size() > 0) {
			for (IDecode iDecode : nuoviOrari) {
				VaOrario orario = new VaOrario(null, pfPrincipal, pfPrincipal, null, vaDatiVacancy, now, now);
				if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vaDatiVacancy.getOpzTipoDecodifiche())) {
					DeOrarioSil deOrarioSil = deOrarioSilHome.findById(iDecode.getId());
					orario.setDeOrarioSil(deOrarioSil);
				} else {
					DeOrario deOrario = deOrarioHome.findById(iDecode.getId());
					orario.setDeOrario(deOrario);
				}
				vaOrarioHome.persist(orario);
				insertOrari.add(orario);
			}
		}
		vaDatiVacancy.setVaOrarios(insertOrari);
	}

	@Override
	public VaRapportoDiLavoroDTO findDTOByVacancyId(int vacancyId) {
		VaDatiVacancy vacancy = vaDatiVacancyHome.findById(vacancyId);
		return toDTO(vacancy);
	}

	private VaRapportoDiLavoroDTO toDTO(VaDatiVacancy vacancy) {
		VaRapportoDiLavoroDTO dto = new VaRapportoDiLavoroDTO();
		dto.setIdVaDatiVacancy(vacancy.getIdVaDatiVacancy());

		// la retribuzione
		VaRetribuzione retribuzione = vacancy.getVaRetribuzione();
		if (retribuzione != null)
			dto.setRetribuzione(deRetribuzioneHome.toDTO(retribuzione.getDeRetribuzione()));

		// le tipologie di contratto
		List<VaContratto> contrattos = vacancy.getVaContrattos();
		if (contrattos != null) {
			Collections.sort(contrattos, new Comparator<VaContratto>() {

				@Override
				public int compare(VaContratto o1, VaContratto o2) {
					return o1.getDtmIns().compareTo(o2.getDtmIns());
				}

			});

			for (VaContratto vaContratto : contrattos) {
				if (vaContratto.getDeContratto() != null) {
					dto.getTipologieContratto().add(deContrattoHome.toDTO(vaContratto.getDeContratto()));
				} else if (vaContratto.getDeContrattoSil() != null) {
					dto.getTipologieContratto().add(deContrattoSilHome.toDTO(vaContratto.getDeContrattoSil()));
				}
			}
		}

		// i turni
		Set<VaTurno> turnos = vacancy.getVaTurnos();
		if (turnos != null) {
			for (VaTurno vaTurno : turnos) {
				if (vaTurno.getDeTurno() != null) {
					dto.getTurni().add(deTurnoHome.toDTO(vaTurno.getDeTurno()));
				} else if (vaTurno.getDeTurnoSil() != null) {
					dto.getTurni().add(deTurnoSilHome.toDTO(vaTurno.getDeTurnoSil()));
				}
			}
		}

		// gli orari
		Set<VaOrario> orarios = vacancy.getVaOrarios();
		if (orarios != null) {
			for (VaOrario vaOrario : orarios) {
				if (vaOrario.getDeOrario() != null) {
					dto.getOrari().add(deOrarioHome.toDTO(vaOrario.getDeOrario()));
				} else if (vaOrario.getDeOrarioSil() != null) {
					dto.getOrari().add(deOrarioSilHome.toDTO(vaOrario.getDeOrarioSil()));
				}
			}
		}

		// I dati presi da VaPubblicazione
		if (vacancy.getVaPubblicazione() != null) {
			dto.setInfoContratti(vacancy.getVaPubblicazione().getContratto());
			dto.setInfoOrariTurni(vacancy.getVaPubblicazione().getOrario());
		}

		return dto;
	}

	@Override
	public void removeById(Integer deleteId, Integer idPfPrincipalMod) {
		VaDatiVacancy dati = vaDatiVacancyHome.findById(deleteId);
		dati.setDtmMod(new Date());
		dati.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
		vaDatiVacancyHome.merge(dati);

		VaRetribuzione retribuzione = dati.getVaRetribuzione();
		if (retribuzione != null) {
			entityManager.remove(retribuzione);
		}

		List<VaContratto> contrattos = dati.getVaContrattos();
		for (Iterator<VaContratto> iterator = contrattos.iterator(); iterator.hasNext();) {
			VaContratto contr = iterator.next();
			entityManager.remove(contr);
		}

		Set<VaTurno> turnos = dati.getVaTurnos();
		for (Iterator<VaTurno> iterator = turnos.iterator(); iterator.hasNext();) {
			VaTurno turno = iterator.next();
			entityManager.remove(turno);
		}

		Set<VaOrario> orarios = dati.getVaOrarios();
		for (Iterator<VaOrario> iterator = orarios.iterator(); iterator.hasNext();) {
			VaOrario orario = iterator.next();
			entityManager.remove(orario);
		}
	}

}
