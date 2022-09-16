package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.dtos.VaAbilitazioniDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaAlbo;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaPatente;
import it.eng.myportal.entity.VaPatentino;
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
import it.eng.myportal.entity.home.local.IVaAbilitazioneHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home per la gestione della tab ABILITAZIONI della vacancy
 * 
 * @author Rodi A.
 */
@Stateless
public class VaAbilitazioneHome implements IVaAbilitazioneHome {

	/**
	 * Logger di classe. Utilizzare questo log per scrivere messaggi di errore, info e debug.
	 */
	protected static Log log = LogFactory.getLog(VaAbilitazioneHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

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
	private VaPatenteHome vaPatenteHome;

	@EJB
	private VaAlboHome vaAlboHome;

	@EJB
	private VaPatentinoHome vaPatentinoHome;

	@Override
	public VaAbilitazioniDTO persistDTO(VaAbilitazioniDTO data, Integer idPrincipalIns) {
		Date now = new Date();
		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(data.getIdVaDatiVacancy());
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipalIns);

		persistPatenti(data, pfPrincipal, vaDatiVacancy, now);
		persistPatentini(data, pfPrincipal, vaDatiVacancy, now);
		persistAlbi(data, pfPrincipal, vaDatiVacancy, now);

		vaDatiVacancy.setDtmMod(now);
		vaDatiVacancy.setPfPrincipalMod(pfPrincipal);
		vaDatiVacancy.setNoteAbilitazioni(data.getNoteAbilitazioni());
		vaDatiVacancyHome.merge(vaDatiVacancy);

		return toDTO(vaDatiVacancy);
	}

	@Override
	public VaAbilitazioniDTO mergeDTO(VaAbilitazioniDTO data, Integer idPrincipalMod) {
		Date now = new Date();
		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(data.getIdVaDatiVacancy());
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipalMod);

		mergePatenti(data, pfPrincipal, vaDatiVacancy, now);
		mergePatentini(data, pfPrincipal, vaDatiVacancy, now);
		mergeAlbi(data, pfPrincipal, vaDatiVacancy, now);

		vaDatiVacancy.setDtmMod(now);
		vaDatiVacancy.setPfPrincipalMod(pfPrincipal);
		vaDatiVacancy.setNoteAbilitazioni(data.getNoteAbilitazioni());
		vaDatiVacancyHome.merge(vaDatiVacancy);

		return toDTO(vaDatiVacancy);
	}

	/**
	 * Effettua l'aggiornamento di tutte le patenti
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
	public void mergePatenti(VaAbilitazioniDTO data, PfPrincipal pfPrincipal, VaDatiVacancy vaDatiVacancy, Date now) {
		Set<VaPatente> vecchiePatenti = vaDatiVacancy.getVaPatentes();

		// cancella le vecchie patenti
		if (vecchiePatenti != null && vecchiePatenti.size() > 0) {
			for (VaPatente vaPatente : vecchiePatenti) {
				vaPatenteHome.remove(vaPatente);

			}
		}

		// inserisci le nuove patenti
		persistPatenti(data, pfPrincipal, vaDatiVacancy, now);
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
	public void persistPatenti(VaAbilitazioniDTO data, PfPrincipal pfPrincipal, VaDatiVacancy vaDatiVacancy, Date now) {
		List<IDecode> nuovePatenti = data.getListaPatenti();
		Set<VaPatente> insertPatenti = new HashSet<VaPatente>();

		// inserisci le nuove patenti
		if (nuovePatenti != null && nuovePatenti.size() > 0) {
			for (IDecode iDecode : nuovePatenti) {
				VaPatente vaPatente = new VaPatente(null, pfPrincipal, pfPrincipal, null, vaDatiVacancy, now, now);
				if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vaDatiVacancy.getOpzTipoDecodifiche())) {
					DePatenteSil dePatenteSil = dePatenteSilHome.findById(iDecode.getId());
					vaPatente.setDePatenteSil(dePatenteSil);
				} else {
					DePatente dePatente = dePatenteHome.findById(iDecode.getId());
					vaPatente.setDePatente(dePatente);
				}
				vaPatenteHome.persist(vaPatente);
				insertPatenti.add(vaPatente);
			}
		}
		vaDatiVacancy.setVaPatentes(insertPatenti);
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
	public void mergePatentini(VaAbilitazioniDTO data, PfPrincipal pfPrincipal, VaDatiVacancy vaDatiVacancy, Date now) {
		Set<VaPatentino> vecchiPatentini = vaDatiVacancy.getVaPatentinos();

		// cancella i vecchi Patentini
		if (vecchiPatentini != null && vecchiPatentini.size() > 0) {
			for (VaPatentino vaPatentino : vecchiPatentini) {
				vaPatentinoHome.remove(vaPatentino);

			}
		}

		// inserisci i nuovi Patentini
		persistPatentini(data, pfPrincipal, vaDatiVacancy, now);
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
	public void persistPatentini(VaAbilitazioniDTO data, PfPrincipal pfPrincipal, VaDatiVacancy vaDatiVacancy, Date now) {
		List<IDecode> nuovePatentini = data.getListaPatentini();
		Set<VaPatentino> insertPatentini = new HashSet<VaPatentino>();

		// inserisci i nuovi Patentini
		if (nuovePatentini != null && nuovePatentini.size() > 0) {
			for (IDecode iDecode : nuovePatentini) {
				VaPatentino vaPatentino = new VaPatentino(null, pfPrincipal, pfPrincipal, null, vaDatiVacancy, now, now);
				if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vaDatiVacancy.getOpzTipoDecodifiche())) {
					DePatentinoSil dePatentinoSil = dePatentinoSilHome.findById(iDecode.getId());
					vaPatentino.setDePatentinoSil(dePatentinoSil);
				} else {
					DePatentino dePatentino = dePatentinoHome.findById(iDecode.getId());
					vaPatentino.setDePatentino(dePatentino);
				}
				vaPatentinoHome.persist(vaPatentino);
				insertPatentini.add(vaPatentino);
			}
		}
		vaDatiVacancy.setVaPatentinos(insertPatentini);
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
	public void mergeAlbi(VaAbilitazioniDTO data, PfPrincipal pfPrincipal, VaDatiVacancy vaDatiVacancy, Date now) {
		List<VaAlbo> vecchiAlbi = vaDatiVacancy.getVaAlbos();

		// cancella i vecchi albi
		if (vecchiAlbi != null && vecchiAlbi.size() > 0) {
			for (VaAlbo vaAlbo : vecchiAlbi) {
				vaAlboHome.remove(vaAlbo);
			}
		}

		// inserisci i nuovi albi
		persistAlbi(data, pfPrincipal, vaDatiVacancy, now);
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
	public void persistAlbi(VaAbilitazioniDTO data, PfPrincipal pfPrincipal, VaDatiVacancy vaDatiVacancy, Date now) {
		List<IDecode> nuoviAlbi = data.getListaAlbi();
		List<VaAlbo> insertAlbi = new ArrayList<VaAlbo>();

		// inserisci i nuovi albi
		if (nuoviAlbi != null && nuoviAlbi.size() > 0) {
			for (IDecode iDecode : nuoviAlbi) {
				VaAlbo vaAlbo = new VaAlbo(null, pfPrincipal, pfPrincipal, null, vaDatiVacancy, now, now);
				if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vaDatiVacancy.getOpzTipoDecodifiche())) {
					DeAlboSil deAlboSil = deAlboSilHome.findById(iDecode.getId());
					vaAlbo.setDeAlboSil(deAlboSil);
				} else {
					DeAlbo deAlbo = deAlboHome.findById(iDecode.getId());
					vaAlbo.setDeAlbo(deAlbo);
				}

				vaAlboHome.persist(vaAlbo);
				insertAlbi.add(vaAlbo);
			}
		}
		vaDatiVacancy.setVaAlbos(insertAlbi);
	}

	@Override
	public VaAbilitazioniDTO findDTOByVacancyId(int vacancyId) {
		VaDatiVacancy vacancy = vaDatiVacancyHome.findById(vacancyId);
		return toDTO(vacancy);
	}

	private VaAbilitazioniDTO toDTO(VaDatiVacancy vacancy) {
		VaAbilitazioniDTO dto = new VaAbilitazioniDTO();
		dto.setIdVaDatiVacancy(vacancy.getIdVaDatiVacancy());
		boolean usaDecodificheSil = VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vacancy.getOpzTipoDecodifiche());

		// patenti
		Set<VaPatente> patentes = vacancy.getVaPatentes();
		if (patentes != null) {
			for (VaPatente vaPatente : patentes) {
				if (usaDecodificheSil) {
					dto.getListaPatenti().add(dePatenteSilHome.toDTO(vaPatente.getDePatenteSil()));
				} else {
					dto.getListaPatenti().add(dePatenteHome.toDTO(vaPatente.getDePatente()));
				}
			}
		}

		// patentini
		Set<VaPatentino> patentinos = vacancy.getVaPatentinos();
		if (patentinos != null) {
			for (VaPatentino vaPatentino : patentinos) {
				if (usaDecodificheSil) {
					dto.getListaPatentini().add(dePatentinoSilHome.toDTO(vaPatentino.getDePatentinoSil()));
				} else {
					dto.getListaPatentini().add(dePatentinoHome.toDTO(vaPatentino.getDePatentino()));
				}
			}
		}

		// albi
		List<VaAlbo> albos = vacancy.getVaAlbos();
		if (albos != null) {
			for (VaAlbo vaAlbo : albos) {
				if (usaDecodificheSil) {
					dto.getListaAlbi().add(deAlboSilHome.toDTO(vaAlbo.getDeAlboSil()));
				} else {
					dto.getListaAlbi().add(deAlboHome.toDTO(vaAlbo.getDeAlbo()));
				}
			}
		}
		dto.setNoteAbilitazioni(vacancy.getNoteAbilitazioni());
		return dto;
	}

	@Override
	public void removeById(Integer deleteId, Integer idPfPrincipalMod) {
		VaDatiVacancy dati = vaDatiVacancyHome.findById(deleteId);
		dati.setDtmMod(new Date());
		dati.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
		dati.setNoteAbilitazioni(null);
		vaDatiVacancyHome.merge(dati);

		Set<VaPatente> patenti = dati.getVaPatentes();
		for (VaPatente vaPatente : patenti) {
			entityManager.remove(vaPatente);
		}

		Set<VaPatentino> patentino = dati.getVaPatentinos();
		for (VaPatentino vaPatentino : patentino) {
			entityManager.remove(vaPatentino);
		}

		List<VaAlbo> albo = dati.getVaAlbos();
		for (VaAlbo vaAlbo : albo) {
			entityManager.remove(vaAlbo);
		}
	}
}
