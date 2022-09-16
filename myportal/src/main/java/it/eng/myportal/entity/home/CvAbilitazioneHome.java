package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.CvAbilitazioniDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeAlbo;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.DePatentino;
import it.eng.myportal.entity.decodifiche.sil.DeAlboSil;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.decodifiche.sil.DePatentinoSil;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;
import it.eng.myportal.entity.home.local.ICvAbilitazioneHome;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home per la gestione della tab ABILITAZIONI della curriculum
 * 
 * @author Enrico D'Angelo
 */
@Stateless
public class CvAbilitazioneHome implements ICvAbilitazioneHome {

	/**
	 * Logger di classe. Utilizzare questo log per scrivere messaggi di errore, info e debug.
	 */
	protected static Log log = LogFactory.getLog(CvAbilitazioneHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	DeAlboHome deAlboHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	@EJB
	DePatentinoSilHome dePatentinoSilHome;

	@EJB
	DeAlboSilHome deAlboSilHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	private CvPatenteHome cvPatenteHome;

	@EJB
	private CvAlboHome cvAlboHome;

	@EJB
	private CvPatentinoHome cvPatentinoHome;

	@Override
	public CvAbilitazioniDTO persistDTO(CvAbilitazioniDTO data, Integer idPrincipalIns) {
		Date now = new Date();
		CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(data.getIdCvDatiPersonali());
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipalIns);

		persistPatenti(data, pfPrincipal, cvDatiPersonali, now);
		persistPatentini(data, pfPrincipal, cvDatiPersonali, now);
		persistAlbi(data, pfPrincipal, cvDatiPersonali, now);
		cvDatiPersonali.setNoteAbilitazioni(data.getNoteAbilitazioni());
		cvDatiPersonaliHome.merge(cvDatiPersonali);
		return toDTO(cvDatiPersonali);
	}

	@Override
	public CvAbilitazioniDTO mergeDTO(CvAbilitazioniDTO data, Integer idPrincipalMod) {
		Date now = new Date();
		CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(data.getIdCvDatiPersonali());
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipalMod);

		mergePatenti(data, pfPrincipal, cvDatiPersonali, now);
		mergePatentini(data, pfPrincipal, cvDatiPersonali, now);
		mergeAlbi(data, pfPrincipal, cvDatiPersonali, now);
		cvDatiPersonali.setNoteAbilitazioni(data.getNoteAbilitazioni());
		cvDatiPersonaliHome.merge(cvDatiPersonali);

		return toDTO(cvDatiPersonali);
	}

	/**
	 * Effettua l'aggiornamento di tutte le patenti
	 * 
	 * @param data
	 *            VaAbilitazioniDTO
	 * @param pfPrincipal
	 *            PfPrincipal
	 * @param cvDatiPersonali
	 *            cvDatiPersonali
	 * @param now
	 *            Data
	 */
	private void mergePatenti(CvAbilitazioniDTO data, PfPrincipal pfPrincipal, CvDatiPersonali cvDatiPersonali, Date now) {
		Set<CvPatente> vecchiePatenti = cvDatiPersonali.getCvPatentes();

		// cancella le vecchie patenti
		if (vecchiePatenti != null && vecchiePatenti.size() > 0) {
			for (CvPatente cvPatente : vecchiePatenti) {
				cvPatenteHome.remove(cvPatente);
			}
		}

		// inserisci le nuove patenti
		persistPatenti(data, pfPrincipal, cvDatiPersonali, now);
	}

	/**
	 * Effettua la persist di tutte le patenti
	 * 
	 * @param data
	 *            VaAbilitazioniDTO
	 * @param pfPrincipal
	 *            PfPrincipal
	 * @param vaDatiVacancy
	 *            VaDatiVacancy
	 * @param now
	 *            Data
	 */
	private void persistPatenti(CvAbilitazioniDTO data, PfPrincipal pfPrincipal, CvDatiPersonali cvDatiPersonali,
			Date now) {
		List<IDecode> nuovePatenti = data.getListaPatenti();
		Set<CvPatente> insertPatenti = new HashSet<CvPatente>();

		// inserisci le nuove patenti
		if (nuovePatenti != null && nuovePatenti.size() > 0) {

			for (int i = 0; i < nuovePatenti.size(); i++) {
				IDecode iDecode = nuovePatenti.get(i);
				CvPatente cvPatente = new CvPatente(null, pfPrincipal, pfPrincipal, cvDatiPersonali, now, now);
				if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(cvDatiPersonali.getOpzTipoDecodifiche())) {
					DePatenteSil dePatenteSil = dePatenteSilHome.findById(iDecode.getId());
					cvPatente.setDePatenteSil(dePatenteSil);
				} else {
					DePatente dePatente = dePatenteHome.findById(iDecode.getId());
					cvPatente.setDePatente(dePatente);
				}
				cvPatenteHome.persist(cvPatente);
				insertPatenti.add(cvPatente);
			}
		}
		cvDatiPersonali.setCvPatentes(insertPatenti);
	}

	/**
	 * Effettua l'aggiornamento di tutte i patentini
	 * 
	 * @param data
	 *            VaAbilitazioniDTO
	 * @param pfPrincipal
	 *            PfPrincipal
	 * @param vaDatiVacancy
	 *            VaDatiVacancy
	 * @param now
	 *            Data
	 */
	private void mergePatentini(CvAbilitazioniDTO data, PfPrincipal pfPrincipal, CvDatiPersonali cvDatiPersonali,
			Date now) {
		Set<CvPatentino> vecchiPatentini = cvDatiPersonali.getCvPatentinos();

		// cancella i vecchi Patentini
		if (vecchiPatentini != null && vecchiPatentini.size() > 0) {
			for (CvPatentino cvPatentino : vecchiPatentini) {
				cvPatentinoHome.remove(cvPatentino);
			}
		}

		// inserisci i nuovi Patentini
		persistPatentini(data, pfPrincipal, cvDatiPersonali, now);
	}

	/**
	 * Effettua la persist di tutte i patentini
	 * 
	 * @param data
	 *            VaAbilitazioniDTO
	 * @param pfPrincipal
	 *            PfPrincipal
	 * @param vaDatiVacancy
	 *            VaDatiVacancy
	 * @param now
	 *            Data
	 */
	private void persistPatentini(CvAbilitazioniDTO data, PfPrincipal pfPrincipal, CvDatiPersonali cvDatiPersonali,
			Date now) {
		List<IDecode> nuovePatentini = data.getListaPatentini();
		Set<CvPatentino> insertPatentini = new HashSet<CvPatentino>();

		// inserisci i nuovi Patentini
		if (nuovePatentini != null && nuovePatentini.size() > 0) {
			for (int i = 0; i < nuovePatentini.size(); i++) {
				IDecode iDecode = nuovePatentini.get(i);
				CvPatentino cvPatentino = new CvPatentino(null, pfPrincipal, pfPrincipal, cvDatiPersonali, now, now);

				if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(cvDatiPersonali.getOpzTipoDecodifiche())) {
					DePatentinoSil dePatentinoSil = dePatentinoSilHome.findById(iDecode.getId());
					cvPatentino.setDePatentinoSil(dePatentinoSil);
				} else {
					DePatentino dePatentino = dePatentinoHome.findById(iDecode.getId());
					cvPatentino.setDePatentino(dePatentino);
				}

				cvPatentinoHome.persist(cvPatentino);
				insertPatentini.add(cvPatentino);
			}
		}
		cvDatiPersonali.setCvPatentinos(insertPatentini);
	}

	/**
	 * Effettua l'aggiornamento di tutti gli albi
	 * 
	 * @param data
	 *            VaAbilitazioniDTO
	 * @param pfPrincipal
	 *            PfPrincipal
	 * @param vaDatiVacancy
	 *            VaDatiVacancy
	 * @param now
	 *            Data
	 */
	private void mergeAlbi(CvAbilitazioniDTO data, PfPrincipal pfPrincipal, CvDatiPersonali cvDatiPersonali, Date now) {
		Set<CvAlbo> vecchiAlbi = cvDatiPersonali.getCvAlbos();

		// cancella i vecchi albi
		if (vecchiAlbi != null && vecchiAlbi.size() > 0) {
			for (CvAlbo cvAlbo : vecchiAlbi) {
				cvAlboHome.remove(cvAlbo);
			}
		}

		// inserisci i nuovi albi
		persistAlbi(data, pfPrincipal, cvDatiPersonali, now);
	}

	/**
	 * Effettua la persist di tutti gli albi
	 * 
	 * @param data
	 *            VaAbilitazioniDTO
	 * @param pfPrincipal
	 *            PfPrincipal
	 * @param vaDatiVacancy
	 *            VaDatiVacancy
	 * @param now
	 *            Data
	 */
	private void persistAlbi(CvAbilitazioniDTO data, PfPrincipal pfPrincipal, CvDatiPersonali cvDatiPersonali, Date now) {
		List<IDecode> nuoviAlbi = data.getListaAlbi();
		Set<CvAlbo> insertAlbi = new HashSet<CvAlbo>();

		// inserisci i nuovi albi
		if (nuoviAlbi != null && nuoviAlbi.size() > 0) {
			for (int i = 0; i < nuoviAlbi.size(); i++) {
				IDecode iDecode = nuoviAlbi.get(i);
				CvAlbo cvAlbo = new CvAlbo(null, pfPrincipal, pfPrincipal, cvDatiPersonali, now, now);
				if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(cvDatiPersonali.getOpzTipoDecodifiche())) {
					DeAlboSil deAlboSil = deAlboSilHome.findById(iDecode.getId());
					cvAlbo.setDeAlboSil(deAlboSil);
				} else {
					DeAlbo deAlbo = deAlboHome.findById(iDecode.getId());
					cvAlbo.setDeAlbo(deAlbo);
				}
				cvAlboHome.persist(cvAlbo);
				insertAlbi.add(cvAlbo);
			}
		}
		cvDatiPersonali.setCvAlbos(insertAlbi);
	}

	@Override
	public CvAbilitazioniDTO findDTOByCurriculumId(int curriculumId) {
		CvDatiPersonali curriculum = cvDatiPersonaliHome.findById(curriculumId);
		return toDTO(curriculum);
	}

	private CvAbilitazioniDTO toDTO(CvDatiPersonali curriculum) {
		CvAbilitazioniDTO dto = new CvAbilitazioniDTO();
		dto.setIdCvDatiPersonali(curriculum.getIdCvDatiPersonali());

		// patenti
		Set<CvPatente> patentes = curriculum.getCvPatentes();
		if (patentes != null) {
			for (CvPatente cvPatente : patentes) {
				if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(curriculum.getOpzTipoDecodifiche())) {
					dto.getListaPatenti().add(dePatenteSilHome.toDTO(cvPatente.getDePatenteSil()));
				} else {
					dto.getListaPatenti().add(dePatenteHome.toDTO(cvPatente.getDePatente()));
				}
			}
		}

		// patentini
		Set<CvPatentino> patentinos = curriculum.getCvPatentinos();
		if (patentinos != null) {
			for (CvPatentino cvPatentino : patentinos) {
				if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(curriculum.getOpzTipoDecodifiche())) {
					dto.getListaPatentini().add(dePatentinoSilHome.toDTO(cvPatentino.getDePatentinoSil()));
				} else {
					dto.getListaPatentini().add(dePatentinoHome.toDTO(cvPatentino.getDePatentino()));
				}
			}
		}

		// albi
		Set<CvAlbo> albos = curriculum.getCvAlbos();
		if (albos != null) {
			for (CvAlbo cvAlbo : albos) {
				if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(curriculum.getOpzTipoDecodifiche())) {
					dto.getListaAlbi().add(deAlboSilHome.toDTO(cvAlbo.getDeAlboSil()));
				} else {
					dto.getListaAlbi().add(deAlboHome.toDTO(cvAlbo.getDeAlbo()));
				}
			}
		}

		dto.setNoteAbilitazioni(curriculum.getNoteAbilitazioni());
		return dto;
	}

	@Override
	public void removeById(Integer idCurriculum, Integer idPfPrincipalMod) {

		CvDatiPersonali curriculum = cvDatiPersonaliHome.findById(idCurriculum);
		Date now = new Date();
		curriculum.setDtmMod(now);
		curriculum.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
		cvDatiPersonaliHome.merge(curriculum);

		Set<CvPatente> patenti = curriculum.getCvPatentes();
		for (Iterator<CvPatente> iterator = patenti.iterator(); iterator.hasNext();) {
			CvPatente cvPatente = iterator.next();
			entityManager.remove(cvPatente);
		}

		Set<CvPatentino> patentino = curriculum.getCvPatentinos();
		for (Iterator<CvPatentino> iterator = patentino.iterator(); iterator.hasNext();) {
			CvPatentino cvPatentino = iterator.next();
			entityManager.remove(cvPatentino);
		}

		Set<CvAlbo> albo = curriculum.getCvAlbos();
		for (Iterator<CvAlbo> iterator = albo.iterator(); iterator.hasNext();) {
			CvAlbo cvAlbo = iterator.next();
			entityManager.remove(cvAlbo);
		}
	}

}
