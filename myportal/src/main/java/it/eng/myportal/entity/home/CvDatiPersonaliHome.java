package it.eng.myportal.entity.home;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import it.eng.myportal.dtos.AziendaSessionDTO;
import it.eng.myportal.dtos.CvAgevolazioneDTO;
import it.eng.myportal.dtos.CvAlboDTO;
import it.eng.myportal.dtos.CvAllegatoDTO;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.CvFilterDTO;
import it.eng.myportal.dtos.CvPatenteDTO;
import it.eng.myportal.dtos.CvPatentinoDTO;
import it.eng.myportal.dtos.CvVisualizzaDTO;
import it.eng.myportal.dtos.DeAmbitoDiffusioneDTO;
import it.eng.myportal.dtos.DeMotivoChiusuraDTO;
import it.eng.myportal.dtos.DeProvenienzaDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.dtos.GenericDecodeDTO;
import it.eng.myportal.dtos.RicercaCVaziendaDTO;
import it.eng.myportal.dtos.RicercaCVprovinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AcCandidatura_;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.CvAgevolazione;
import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvAllegato;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvAltreInfo_;
import it.eng.myportal.entity.CvCandidaturaCl;
import it.eng.myportal.entity.CvCompetenzeTrasv;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvDatiPersonali_;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.CvEsperienzeProf_;
import it.eng.myportal.entity.CvFormazione;
import it.eng.myportal.entity.CvInformatica;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.CvIstruzione_;
import it.eng.myportal.entity.CvLingua;
import it.eng.myportal.entity.CvLingua_;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.CvProfDesiderate;
import it.eng.myportal.entity.IEntity;
import it.eng.myportal.entity.MsgContatto;
import it.eng.myportal.entity.MsgContatto_;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.MsgMessaggio_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Poi;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.UtenteInfo_;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaDatiVacancy_;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.decodifiche.DeBpMansione_;
import it.eng.myportal.entity.decodifiche.DeCittadinanza;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeComune_;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeLingua_;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeMansione_;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.decodifiche.DeProvenienza_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeTipoMessaggio;
import it.eng.myportal.entity.decodifiche.DeTipoMessaggio_;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.DeTitolo_;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoChiusuraHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoComunicazioneClHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;
import it.eng.myportal.entity.home.local.ICvAbilitazioneHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.helpers.CurriculumSearchParams;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.AzioneCliclavoro;
import it.eng.myportal.utils.Utils;

/**
 * Home per CvDatiPersonali.
 *
 * @see it.eng.myportal.entity.CvDatiPersonali
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class CvDatiPersonaliHome extends AbstractUpdatableHome<CvDatiPersonali, CvDatiPersonaliDTO>
		implements InoDTOejb<CvDatiPersonali> {

	private static final int FIELD_INCREMENT = 3;
	private static final int LIST_INCREMENT = 10;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	CvAltreInfoHome cvAltreInfoHome;

	@EJB
	CvCompetenzeTrasvHome cvCompetenzeTrasvHome;

	@EJB
	CvEsperienzeProfHome cvEsperienzeProfHome;

	@EJB
	CvFormazioneHome cvFormazioneHome;

	@EJB
	CvInformaticaHome cvInformaticaHome;

	@EJB
	CvIstruzioneHome cvIstruzioneHome;

	@EJB
	CvLinguaHome cvLinguaHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	CvPatenteHome cvPatenteHome;

	@EJB
	CvAlboHome cvAlboHome;

	@EJB
	CvPatentinoHome cvPatentinoHome;

	@EJB
	CvAgevolazioneHome cvAgevolazioneHome;

	@EJB
	ICvAbilitazioneHome cvAbilitazioneHome;

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	DeAmbitoDiffusioneHome deAmbitoDiffusioneHome;

	@EJB
	DeMotivoChiusuraHome deMotivoChiusuraHome;

	@EJB
	CvProfDesiderateHome cvProfDesiderateHome;

	@EJB
	CvCandidaturaClHome cvCandidaturaClHome;

	@EJB
	DeTipoComunicazioneClHome deTipoComunicazioneClHome;

	@EJB
	PoiHome poiHome;

	@EJB
	CvAllegatoHome cvAllegatoHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	DeProvenienzaHome deProvenienzaHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	DeTrasfertaHome deTrasfertaHome;

	private Set<CvLingua> setL;

	public CvDatiPersonali findById(Integer id) {
		return findById(CvDatiPersonali.class, id);
	}

	@Override
	public CvDatiPersonaliDTO toDTO(CvDatiPersonali entity) {
		if (entity == null)
			return null;
		CvDatiPersonaliDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvDatiPersonali());
		dto.setDtNascita(entity.getDtNascita());
		dto.setCodComDomicilio(entity.getDeComuneDomicilio().getCodCom());
		dto.setStrComDomicilio(deComuneHome.toDTO(entity.getDeComuneDomicilio()).getDescrizione());
		dto.setCodComNascita(entity.getDeComuneNascita().getCodCom());
		dto.setStrComNascita(deComuneHome.toDTO(entity.getDeComuneNascita()).getDescrizione());
		dto.setEmail(entity.getEmail());
		dto.setFax(entity.getFax());
		dto.setIdPfPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		dto.setIndirizzoDomicilio(entity.getIndirizzoDomicilio());
		dto.setTel1(entity.getTel1());
		dto.setTel2(entity.getTel2());
		dto.setProvenienzaCurriculum(deProvenienzaHome.toDTO(entity.getProvenienzaCurriculum()));
		dto.setDescrizione(entity.getDescrizione());
		dto.setFlagInviato(entity.getFlagInviato());
		dto.setFromMyPortal(entity.isFromMyPortal());
		dto.setFromCliclavoro(entity.isFromClicLavoro());
		dto.setSincronizzatoCliclavoro(entity.isSincronizzatoCliclavoro());
		dto.setRicercabile(entity.isRicercabile());
		List<CvAllegatoDTO> l = cvAllegatoHome.findLightDtoByCurriculumId(entity.getIdCvDatiPersonali());
		if (l != null && !l.isEmpty()) {
			CvAllegatoDTO cva = l.get(0);
			dto.setIdCvAllegato(cva.getId());
			dto.setFilename(cva.getFilename());
		}

		Poi poiDomicilio = entity.getPoi();
		if (poiDomicilio != null) {
			dto.setIdPoiDomicilio(poiDomicilio.getIdPoi());
		}
		dto.setReferenziatoInMsg(entity.getMsgMessaggios().size() > 0);
		dto.setDeAmbitoDiffusione(deAmbitoDiffusioneHome.toDTO(entity.getDeAmbitoDiffusione()));
		dto.setMotivoChiusura(deMotivoChiusuraHome.toDTO(entity.getDeMotivoChiusura()));
		dto.setDtScadenza(entity.getDtScadenza());
		dto.setIntermediario(entity.getIntermediario());
		dto.setVisibilita(entity.getVisibilita());
		PfPrincipal pfPrincipalPalese = entity.getPfPrincipalPalese();
		if (pfPrincipalPalese != null) {
			dto.setIdPfPrincipalPalese(pfPrincipalPalese.getIdPfPrincipal());
		} else {
			dto.setIdPfPrincipalPalese(null);
		}
		dto.setIntermediario(entity.getIntermediario());
		dto.setDenominazioneIntermediario(entity.getDenominazioneIntermediario());
		dto.setFlagEliminato(entity.getFlagEliminato());

		dto.setCodComunicazioneSil(entity.getCodComunicazioneSil());
		dto.setIntermediato(entity.isIntermediato());
		dto.setOpzTipoDecodifiche(entity.getOpzTipoDecodifiche());
		dto.setTitolo(entity.getTitolo());
		dto.setDescrizioneCv(entity.getDescrizioneCv());
		dto.setValido(entity.isValido());

		dto.setFlgIdo(entity.getFlagIdo());

		return dto;
	}

	@Override
	public CvDatiPersonali fromDTO(CvDatiPersonaliDTO dto) {
		CvDatiPersonali entity = null;
		if (dto != null) {
			// TODO 100 e' il limite della colonna sul DB
			if (dto.getDescrizione().length() > 100) {
				throw new MyPortalException("data.error.copying.longname");
			} else {
				entity = super.fromDTO(dto);

				entity.setIdCvDatiPersonali(dto.getId());
				entity.setDtNascita(dto.getDtNascita());
				entity.setDeComuneDomicilio(deComuneHome.findById(dto.getCodComDomicilio()));
				entity.setDeComuneNascita(deComuneHome.findById(dto.getCodComNascita()));
				PfPrincipal pfPrincipal = pfPrincipalHome.findById(dto.getIdPfPrincipal());
				entity.setPfPrincipal(pfPrincipal);
				entity.setEmail(dto.getEmail());
				entity.setFax(dto.getFax());
				entity.setIndirizzoDomicilio(dto.getIndirizzoDomicilio());
				entity.setTel1(dto.getTel1());
				entity.setTel2(dto.getTel2());
				entity.setDescrizione(dto.getDescrizione());
				entity.setFlagInviato(dto.getFlagInviato());
				entity.setDtScadenza(dto.getDtScadenza());
				DeProvenienzaDTO provenienzaCurriculumDTO = dto.getProvenienzaCurriculum();
				if (provenienzaCurriculumDTO != null) {
					entity.setProvenienzaCurriculum(deProvenienzaHome.findById(provenienzaCurriculumDTO.getId()));
				}

				entity.setIntermediario(dto.getIntermediario());
				entity.setVisibilita(dto.getVisibilita());
				entity.setFlagIdo(dto.getFlgIdo());
				entity.setRicercabile(dto.isRicercabile());

				DeAmbitoDiffusioneDTO ambitoDiffusione = dto.getDeAmbitoDiffusione();
				if (ambitoDiffusione == null || ambitoDiffusione.getId() == null) {
					entity.setDeAmbitoDiffusione(
							deAmbitoDiffusioneHome.findById(ConstantsSingleton.DeAmbitoDiffusione.REGIONALE));
				} else {
					entity.setDeAmbitoDiffusione(deAmbitoDiffusioneHome.findById(dto.getDeAmbitoDiffusione().getId()));
				}
				DeMotivoChiusuraDTO motivoChiusura = dto.getMotivoChiusura();
				if (motivoChiusura == null || motivoChiusura.getId() == null) {

				} else {
					entity.setDeMotivoChiusura(deMotivoChiusuraHome.findById(dto.getMotivoChiusura().getId()));
				}

				Integer idPoiDomicilio = dto.getIdPoiDomicilio();
				if (idPoiDomicilio != null) {
					entity.setPoi(poiHome.findById(idPoiDomicilio));
				}

				Integer idPfPrincipalPalese = dto.getIdPfPrincipalPalese();
				if (idPfPrincipalPalese != null) {
					entity.setPfPrincipalPalese(pfPrincipalHome.findById(idPfPrincipalPalese));
				} else {
					entity.setPfPrincipalPalese(null);
				}

				entity.setIntermediario(dto.getIntermediario());
				entity.setDenominazioneIntermediario(dto.getDenominazioneIntermediario());
				entity.setFlagEliminato(dto.getFlagEliminato());
				entity.setCodComunicazioneSil(dto.getCodComunicazioneSil());
				entity.setOpzTipoDecodifiche(dto.getOpzTipoDecodifiche());
				entity.setTitolo(dto.getTitolo());
				entity.setDescrizioneCv(dto.getDescrizioneCv());
			}

		}
		return entity;
	}

	/**
	 * Elimina un Curriculum. Se il CV e' sincronizzato con cliclavoro e non si trova nello stato CI (chiusura inviata)
	 * allora viene inviata una comunicazione di chiusura e il CV viene nascosto all'utente per essere eliminato da DB
	 * in seguito, altrimenti viene eliminato immediatamente
	 *
	 * @param idCvDatiPersonali
	 * @param idPfPrincipalMod
	 */
	public void deleteCurriculum(Integer idCvDatiPersonali, Integer idPfPrincipalMod) {
		CvCandidaturaCl cvCandidaturaCl = cvCandidaturaClHome.findById(idCvDatiPersonali);

		if (cvCandidaturaCl != null) {
			if (cvCandidaturaClHome.isChiusuraInviata(cvCandidaturaCl)
					|| cvCandidaturaClHome.isPrimoInvioNonInviato(cvCandidaturaCl)) {
				/*
				 * deve ancora essere inviata la comunicazione di sincronizzazione del CV con cliclavoro, oppure ne e'
				 * gia' stata comunicata la chiusura con successo, quindi posso eliminare il CV
				 */
				removeCascadeById(idCvDatiPersonali, idPfPrincipalMod);
			} else {
				/*
				 * il CV e' sincronizzato con cliclavoro, setto il flag_eliminato e invio la chiusura della
				 * sincronizzazione con cliclavoro
				 */
				CvDatiPersonali cvDatiPersonali = findById(idCvDatiPersonali);
				cvDatiPersonali.setFlagEliminato(true);
				if (!cvCandidaturaClHome.isChiusuraNonInviata(cvCandidaturaCl)) {
					/*
					 * solo se i dati di invio a cliclavoro sono in uno stato diverso da CA o CE invio una comunicazione
					 * di chiusura, altrimenti significa che l'ho gia' fatto
					 */
					cvCandidaturaClHome.updateCandidatura(idPfPrincipalMod, idCvDatiPersonali, cvCandidaturaCl,
							ConstantsSingleton.AzioneCliclavoro.CHIUSURA);
				}
				merge(cvDatiPersonali);
			}
		} else {
			/*
			 * il CV non e' sincronizzato con cliclavoro quindi lo posso eliminare direttamente
			 */
			removeCascadeById(idCvDatiPersonali, idPfPrincipalMod);
		}
	}

	/**
	 * Elimina fisicamente un CV da DB e tutte le entry in altre tabelle in cui e' referenziato. Usare deleteCurriculum
	 * per una gestione piu' ad alto livello
	 */
	@Override
	public void removeCascadeById(Integer id, Integer idPfPrincipalMod) {
		CvDatiPersonali entity = findById(id);

		/* elimino i riferimenti in cv_abilita_vis_contatto_lav */
		entity.setContactInfoViewableBy(new HashSet<PfPrincipal>());
		entityManager.merge(entity);

		CvAltreInfo cvAltreInfo = entity.getCvAltreInfo();
		if (cvAltreInfo != null) {
			cvAltreInfoHome.remove(cvAltreInfo);
		}
		Set<CvCompetenzeTrasv> compTrasv = entity.getCvCompetenzeTrasvs();
		for (CvCompetenzeTrasv cvCompetenzeTrasv : compTrasv) {
			cvCompetenzeTrasvHome.remove(cvCompetenzeTrasv);
		}
		Set<CvEsperienzeProf> esps = entity.getCvEsperienzeProfs();
		for (CvEsperienzeProf cvEsperienzeProf : esps) {
			cvEsperienzeProfHome.remove(cvEsperienzeProf);
		}
		Set<CvFormazione> formaziones = entity.getCvFormaziones();
		for (CvFormazione cvFormazione : formaziones) {
			cvFormazioneHome.remove(cvFormazione);
		}
		CvInformatica cvInformatica = entity.getCvInformatica();
		if (cvInformatica != null) {
			cvInformaticaHome.remove(cvInformatica);
		}
		Set<CvIstruzione> istruziones = entity.getCvIstruziones();
		for (CvIstruzione cvIstruzione : istruziones) {
			cvIstruzioneHome.remove(cvIstruzione);
		}
		Set<CvLingua> linguas = entity.getCvLinguas();
		for (CvLingua cvLingua : linguas) {
			cvLinguaHome.remove(cvLingua);
		}
		Set<AcCandidatura> candidature = entity.getAcCandidaturas();
		for (AcCandidatura acCandidatura : candidature) {
			acCandidaturaHome.remove(acCandidatura);
		}
		Set<CvPatente> patentes = entity.getCvPatentes();
		for (CvPatente cvPatente : patentes) {
			cvPatenteHome.remove(cvPatente);
		}
		Set<CvAlbo> albos = entity.getCvAlbos();
		for (CvAlbo cvAlbo : albos) {
			cvAlboHome.remove(cvAlbo);
		}
		Set<CvPatentino> patentinos = entity.getCvPatentinos();
		for (CvPatentino cvPatentino : patentinos) {
			cvPatentinoHome.remove(cvPatentino);
		}
		Set<CvAgevolazione> agevolaziones = entity.getCvAgevolaziones();
		for (CvAgevolazione cvAgevolazione : agevolaziones) {
			cvAgevolazioneHome.remove(cvAgevolazione);
		}
		Set<CvProfDesiderate> profDesiderates = entity.getCvProfDesiderates();
		for (CvProfDesiderate cvProfDesiderate : profDesiderates) {
			cvProfDesiderateHome.remove(cvProfDesiderate);
		}

		/* elimino i dati di invio a cliclavoro, se presenti */
		CvCandidaturaCl cvCandidaturaCl = cvCandidaturaClHome.findById(id);
		if (cvCandidaturaCl != null) {
			cvCandidaturaClHome.remove(cvCandidaturaCl);
		}

		cvAllegatoHome.removeByCurriculumId(id);

		removeReferencesFromMessages(entity, id);

		removeById(id, idPfPrincipalMod);
	}

	/**
	 * Controlla se il CV e' referenziato da qualche messaggio e in caso ne elimina i riferimenti.
	 *
	 * @param entity
	 * @param idCvDatiPersonali
	 */
	private void removeReferencesFromMessages(CvDatiPersonali entity, Integer idCvDatiPersonali) {
		Set<MsgMessaggio> msgMessaggios = entity.getMsgMessaggios();
		for (MsgMessaggio msgMessaggio : msgMessaggios) {
			Set<CvDatiPersonali> curricula = msgMessaggio.getCurricula();
			for (CvDatiPersonali curriculum : curricula) {
				if (curriculum.getIdCvDatiPersonali().equals(idCvDatiPersonali)) {
					msgMessaggioHome.removeCurriculumReference(msgMessaggio.getIdMsgMessaggio(), idCvDatiPersonali);
				}
			}
		}

		Set<MsgContatto> msgContattos = entity.getMsgContattos();
		for (MsgContatto msgContatto : msgContattos) {
			msgContatto.setCvDatiPersonali(null);
			entityManager.merge(msgContatto);
		}
	}

	/**
	 * Questa funzione crea una subquery che dev'essere compresa nella ricerca dei CV per gli utenti AZIENDA. Faccio un
	 * giochetto coi parametri per fare in modo che possa essere utilizzata sia dalla query vera e propria che da quella
	 * count.
	 */
	private Subquery<Date> creaSubQuery(CriteriaQuery query, CriteriaQuery<Long> queryCount,
			Integer idPfPrincipalAzienda) {

		// Costruisco la "base" della subquery.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Date> subquery = null;
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		if (query != null)
			subquery = query.subquery(Date.class);
		else
			subquery = queryCount.subquery(Date.class);

		// Costruisco la clausola FROM della subquery.
		Root<AcCandidatura> acCandidatura = subquery.from(AcCandidatura.class);
		Join<AcCandidatura, MsgContatto> msgContatto = acCandidatura.join(AcCandidatura_.msgContattos, JoinType.LEFT);
		Join<MsgContatto, MsgMessaggio> msgMessaggio = msgContatto.join(MsgContatto_.msgMessaggio);
		Join<MsgMessaggio, DeTipoMessaggio> deTipoMess = msgMessaggio.join(MsgMessaggio_.deTipoMessaggio);
		Join<AcCandidatura, PfPrincipal> pfPrincipal = acCandidatura.join(AcCandidatura_.pfPrincipal);
		Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = acCandidatura.join(AcCandidatura_.vaDatiVacancy);

		// Seleziono il max della data di Inserimento ...
		subquery.select(cb.greatest(acCandidatura.get(AcCandidatura_.dtmIns)));

		// ... delle candidature a questa azienda ...
		Predicate predAzInfo = cb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipalAzienda);
		whereConditions.add(predAzInfo);

		// ... per offerte di lavoro non ancora scadute...
		Predicate predDataScad = cb.greaterThan(vaDatiVacancy.get(VaDatiVacancy_.dtScadenzaPubblicazione), new Date());
		whereConditions.add(predDataScad);

		// ... dei messaggi di primo contatto.
		Predicate predTipoMsg = cb.equal(deTipoMess.get(DeTipoMessaggio_.codTipoMessaggio), "03");
		whereConditions.add(predTipoMsg);

		// Restituisco la subquery completa.
		subquery.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		return subquery;
	}

	private Subquery<Integer> creaSubQueryNewVersion(CriteriaQuery query, CriteriaQuery<Long> queryCount,
			String professione) {

		// Costruisco la "base" della subquery.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Integer> subquery = null;
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		if (query != null)
			subquery = query.subquery(Integer.class);
		/*
		 * else subquery = queryCount.subquery(Integer.class);
		 */
		// Costruisco la clausola FROM della subquery.
		Root<CvDatiPersonali> cvDatiPersonali = subquery.from(CvDatiPersonali.class);
		Join<CvDatiPersonali, CvEsperienzeProf> esperienzeProfessionali = cvDatiPersonali
				.join(CvDatiPersonali_.cvEsperienzeProfs);
		Predicate predGruppoProf = cb.like(cb.upper(esperienzeProfessionali.get(CvEsperienzeProf_.professione)),
				"%" + professione.toUpperCase() + "%");

		whereConditions.add(predGruppoProf);

		subquery.select(cb.max(cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali)));
		subquery.groupBy(cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali));

		// Restituisco la subquery completa.
		subquery.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		return subquery;
	}

	/**
	 * Restituisce tutti i curriculum di un utente in ordine di data ultima Modifica (dal più recento al più vecchio)
	 *
	 * @param idPfPrincipal
	 * @return la lista dei suoi cv
	 */
	public List<CvDatiPersonali> findByIdPfPrincipal(Integer idPfPrincipal) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvDatiPersonali> query = cb.createQuery(CvDatiPersonali.class);
		Root<CvDatiPersonali> f = query.from(CvDatiPersonali.class);
		Join<CvDatiPersonali, PfPrincipal> cvJoinPrincipal = f.join(CvDatiPersonali_.pfPrincipal);
		Predicate equalPredicate = cb.equal(cvJoinPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		query.where(equalPredicate);
		query.orderBy(cb.desc(f.get(CvDatiPersonali_.dtmMod)));
		return entityManager.createQuery(query).getResultList();
	}

	/**
	 * Metodo che restituisce la lista dei CV live appartenenti ad un utente. Il metodo viene utilizzato nella
	 * visualizzazione dei CV nella homepage dell'utente.
	 *
	 * @param idPfPrincipal
	 * @return
	 */
	public List<CvDatiPersonaliDTO> findAllCurriculaLiveDTO(Integer idPfPrincipal) {
		List<CvDatiPersonaliDTO> result = new ArrayList<CvDatiPersonaliDTO>();
		List<CvDatiPersonali> list = null;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			list = this.findAllCurriculaLiveFlgIdo(idPfPrincipal);
		} else {
			list = this.findAllCurriculaLive(idPfPrincipal);
		}
		for (CvDatiPersonali cvDatiPersonali : list) {
			result.add(this.toDTO(cvDatiPersonali));
		}
		return result;
	}

	/**
	 * Metodo che restituisce la lista dei CV live appartenenti ad un utente. Il metodo viene utilizzato nella
	 * visualizzazione dei CV nella homepage dell'utente.
	 *
	 * @param idPfPrincipal
	 * @return
	 */
	public List<CvDatiPersonali> findAllCurriculaLive(Integer idPfPrincipal) {
		TypedQuery<CvDatiPersonali> query = entityManager.createNamedQuery("findCvLive", CvDatiPersonali.class);
		query = query.setParameter("idPrincipal", idPfPrincipal);
		List<CvDatiPersonali> list = query.getResultList();

		return list;
	}

	public List<CvDatiPersonali> findAllCurriculaLiveFlgIdo(Integer idPfPrincipal) {
		TypedQuery<CvDatiPersonali> query = entityManager.createNamedQuery("findCvLiveFlgIdo", CvDatiPersonali.class);
		query = query.setParameter("idPrincipal", idPfPrincipal);
		List<CvDatiPersonali> list = query.getResultList();

		return list;
	}

	/**
	 * Restituisce tutti i CV intermediati appartenenti all'utente, esclusi quelli con provenienza MINISTERO e scaduti.
	 * Il metodo viene utilizzato nella visualizzazione dei CV nella homepage dell'utente.
	 *
	 * @param idPfPrincipal
	 * @return
	 */
	public List<CvDatiPersonaliDTO> findAllCurriculaIntermediatiNoMinScadutiDTO(Integer idPfPrincipal) {
		List<CvDatiPersonaliDTO> result = new ArrayList<CvDatiPersonaliDTO>();
		List<CvDatiPersonali> list = null;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			list = findAllCurriculaIntermediatiNoMinScadutiFlgIdo(idPfPrincipal);
		} else {
			list = findAllCurriculaIntermediatiNoMinScaduti(idPfPrincipal);
		}
		for (CvDatiPersonali cvDatiPersonali : list) {
			result.add(this.toDTO(cvDatiPersonali));
		}
		return result;
	}

	/**
	 * Restituisce tutti i CV intermediati appartenenti all'utente, esclusi quelli con provenienza MINISTERO e scaduti.
	 * Il metodo viene utilizzato nella visualizzazione dei CV nella homepage dell'utente.
	 *
	 * @param idPfPrincipal
	 * @return
	 */
	public List<CvDatiPersonali> findAllCurriculaIntermediatiNoMinScaduti(Integer idPfPrincipal) {
		TypedQuery<CvDatiPersonali> query = entityManager.createNamedQuery("findCvCliclavoroNonScaduti",
				CvDatiPersonali.class);
		query = query.setParameter("idPrincipal", idPfPrincipal);
		List<CvDatiPersonali> list = query.getResultList();

		return list;
	}

	public List<CvDatiPersonali> findAllCurriculaIntermediatiNoMinScadutiFlgIdo(Integer idPfPrincipal) {
		TypedQuery<CvDatiPersonali> query = entityManager.createNamedQuery("findCvCliclavoroNonScadutiFlgIdo",
				CvDatiPersonali.class);
		query = query.setParameter("idPrincipal", idPfPrincipal);
		List<CvDatiPersonali> list = query.getResultList();

		return list;
	}

	/**
	 * Metodo che restituisce la lista dei CV live appartenenti ad un utente NON SCADUTI.
	 *
	 * @param idPfPrincipal
	 * @return
	 */
	public List<CvDatiPersonaliDTO> getAllCurriculaLiveNoScadDTO(Integer idPfPrincipal) {
		List<CvDatiPersonaliDTO> result = new ArrayList<CvDatiPersonaliDTO>();

		List<CvDatiPersonali> list = getAllCurriculaLiveNoScad(idPfPrincipal);

		for (CvDatiPersonali cvDatiPersonali : list) {
			result.add(this.toDTO(cvDatiPersonali));
		}
		return result;
	}

	public List<CvDatiPersonaliDTO> getAllCurriculaLiveNoScadDTOFlagIdo(Integer idPfPrincipal) {
		List<CvDatiPersonaliDTO> result = new ArrayList<CvDatiPersonaliDTO>();

		List<CvDatiPersonali> list = getAllCurriculaLiveNoScadFlagIdo(idPfPrincipal);

		for (CvDatiPersonali cvDatiPersonali : list) {
			result.add(this.toDTO(cvDatiPersonali));
		}
		return result;
	}

	/**
	 * Metodo che restituisce la lista dei CV live appartenenti ad un utente NON SCADUTI. Il metodo viene utilizzato
	 * anche dall'App per recuperare i CV utilizzabili per una candidatura.
	 *
	 * @param idPfPrincipal
	 * @return
	 */
	public List<CvDatiPersonali> getAllCurriculaLiveNoScad(Integer idPfPrincipal) {

		TypedQuery<CvDatiPersonali> query = entityManager.createNamedQuery("findCvLiveNoScad", CvDatiPersonali.class);
		query = query.setParameter("idPrincipal", idPfPrincipal);
		List<CvDatiPersonali> list = query.getResultList();

		return list;
	}

	/* non scaduti con flagido = true */
	public List<CvDatiPersonali> getAllCurriculaLiveNoScadFlagIdo(Integer idPfPrincipal) {

		TypedQuery<CvDatiPersonali> query = entityManager.createNamedQuery("findCvLiveNoScadFlagIdo",
				CvDatiPersonali.class);
		query = query.setParameter("idPrincipal", idPfPrincipal);
		List<CvDatiPersonali> list = query.getResultList();

		return list;
	}

	/**
	 * Metodo che restituisce la lista dei CV intermediati (palesi) di un utente NON SCADUTI.
	 *
	 * @param idPfPrincipal
	 * @return
	 */
	public List<CvDatiPersonaliDTO> getAllCurriculaIntermediatiNoScadDTO(Integer idPfPrincipal) {
		List<CvDatiPersonaliDTO> result = new ArrayList<CvDatiPersonaliDTO>();

		List<CvDatiPersonali> list = getAllCurriculaIntermediatiNoScad(idPfPrincipal);

		for (CvDatiPersonali cvDatiPersonali : list) {
			result.add(this.toDTO(cvDatiPersonali));
		}
		return result;
	}

	/**
	 * Metodo che restituisce la lista dei CV intermediati (palesi) di un utente NON SCADUTI. Il metodo viene utilizzato
	 * anche dall'App per recuperare i CV utilizzabili per una candidatura.
	 *
	 * @param idPfPrincipal
	 * @return
	 */
	public List<CvDatiPersonali> getAllCurriculaIntermediatiNoScad(Integer idPfPrincipal) {

		TypedQuery<CvDatiPersonali> query = entityManager.createNamedQuery("findCvIntermediatiNoScad",
				CvDatiPersonali.class);
		query = query.setParameter("idPrincipal", idPfPrincipal);
		List<CvDatiPersonali> list = query.getResultList();

		return list;
	}

	/**
	 * Restituisce i DTO di tutti i curriculum di un utente in ordine di data ultima Modifica (dal più recento al più
	 * vecchio)
	 *
	 * @param idPfPrincipal
	 * @return la lista dei suoi cv
	 */
	public List<CvDatiPersonaliDTO> findDTOByIdPfPrincipal(Integer idPfPrincipal) {
		List<CvDatiPersonaliDTO> result = new ArrayList<CvDatiPersonaliDTO>();

		List<CvDatiPersonali> list = findByIdPfPrincipal(idPfPrincipal);

		for (CvDatiPersonali cvDatiPersonali : list) {
			CvDatiPersonaliDTO cvDatiPersonaliDTO = toDTO(cvDatiPersonali);
			result.add(cvDatiPersonaliDTO);
		}

		return result;
	}

	/**
	 * @param descrizione
	 *            - dei cv da restituire
	 * @param caseSensitive
	 *            - false -> case insensitive
	 * @return
	 */
	public List<CvDatiPersonali> findByDescrizione(Integer idPfPrincipal, String descrizione, boolean caseSensitive) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvDatiPersonali> query = cb.createQuery(CvDatiPersonali.class);

		Root<CvDatiPersonali> f = query.from(CvDatiPersonali.class);
		Join<CvDatiPersonali, PfPrincipal> cvJoinPrincipal = f.join(CvDatiPersonali_.pfPrincipal);
		Predicate principalPredicate = cb.equal(cvJoinPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		Expression<Boolean> descPredicate = getExpFilterByDescrizione(descrizione, caseSensitive, cb, f);
		query.where(cb.and(principalPredicate, descPredicate));
		query.orderBy(cb.desc(f.get(CvDatiPersonali_.dtmMod)));
		return entityManager.createQuery(query).getResultList();
	}

	private Expression<Boolean> getExpFilterByDescrizione(String descrizione, boolean caseSensitive, CriteriaBuilder cb,
			Root<CvDatiPersonali> f) {
		Path<String> pDescrizione = f.get(CvDatiPersonali_.descrizione);
		Expression<Boolean> equalPredicate;
		if (caseSensitive) {
			equalPredicate = cb.equal(pDescrizione, descrizione);
		} else {
			Expression<String> upper = cb.upper(pDescrizione);
			equalPredicate = cb.equal(upper, StringUtils.upperCase(descrizione));
		}
		return equalPredicate;
	}

	/**
	 * Restituisce il numero di curriculum di un utente con una descrizione (case sensitive o meno)
	 *
	 * @param idPfPrincipal
	 *            - id utente
	 * @param descrizione
	 *            - dei cv da contare
	 * @param caseSensitive
	 *            - false -> case insensitive
	 * @return
	 */
	public long getNumCvByDescrizione(Integer idPfPrincipal, String descrizione, boolean caseSensitive) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		Root<CvDatiPersonali> f = c.from(CvDatiPersonali.class);
		Expression<Boolean> descPredicate = getExpFilterByDescrizione(descrizione, caseSensitive, cb, f);
		Join<CvDatiPersonali, PfPrincipal> cvJoinPrincipal = f.join(CvDatiPersonali_.pfPrincipal);
		Predicate principalPredicate = cb.equal(cvJoinPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		c = c.select(cb.count(f));
		c = c.where(cb.and(principalPredicate, descPredicate));
		Long singleResult = entityManager.createQuery(c).getSingleResult();
		long count = singleResult.longValue();
		return count;
	}

	/**
	 * Fa una query per scoprire il numero di curriculum posseduti da un certo utente.
	 */
	public long getNumCvByIdPfPrincipal(Integer idPfPrincipal) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		Root<CvDatiPersonali> f = c.from(CvDatiPersonali.class);
		Join<CvDatiPersonali, PfPrincipal> cvJoinPrincipal = f.join(CvDatiPersonali_.pfPrincipal);
		Predicate equalPredicate = cb.equal(cvJoinPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		c = c.select(cb.count(f));
		c = c.where(equalPredicate);
		Long singleResult = entityManager.createQuery(c).getSingleResult();
		long count = singleResult.longValue();
		return count;
	}

	/**
	 * Copia e salva un curriculum su DB.
	 *
	 * @param pfPrincipalId
	 *            del cv da copiare
	 * @param curriculumId
	 *            id del curriculum da copiare
	 * @param descrizione
	 *            descrizione del nuovo cv
	 * @param perCandidatura
	 *            determina se impostare il flag 'inviato' nella copia.
	 *
	 * @return il DTO del nuovo cv
	 */
	public CvDatiPersonaliDTO copyById(Integer pfPrincipalId, Integer curriculumId, String descrizione,
			Boolean perCandidatura) {
		return toDTO(copyByIdNoDTO(pfPrincipalId, curriculumId, descrizione, perCandidatura));
	}

	public CvDatiPersonaliDTO copyById(Integer pfPrincipalId, Integer curriculumId, String descrizione) {
		return copyById(pfPrincipalId, curriculumId, descrizione, Boolean.FALSE);
	}

	/**
	 * Interfaccia per copia senza DTO introdotto con passaggio a PF per evitrare la migrazione delle sezioni
	 * 'incompativili' semplicemente non le copiamo
	 *
	 * @since 1.24.4
	 *
	 * @param pfPrincipalId
	 * @param curriculumId
	 * @param descrizione
	 * @param perCandidatura
	 * @return
	 */
	public CvDatiPersonali copyLightNoDTO(Integer pfPrincipalId, Integer curriculumId, String descrizione,
			Boolean perCandidatura) {
		Date now = new Date();

		//

		CvDatiPersonali cvDatiPersonaliCopiato = new CvDatiPersonali();

		// TODO 100 e' il limite della colonna sul DB
		if (descrizione.length() > 100) {
			throw new MyPortalException("data.error.copying.longname");
		}
		CvDatiPersonali cvDatiPersonaliOrig = findById(curriculumId);
		if (cvDatiPersonaliOrig == null) {
			throw new MyPortalException("data.error.copying");
		}

		Set<CvAgevolazione> agevolaziones = cvDatiPersonaliOrig.getCvAgevolaziones();
		Set<CvAlbo> albos = cvDatiPersonaliOrig.getCvAlbos();
		CvAltreInfo cvAltreInfo = cvDatiPersonaliOrig.getCvAltreInfo();
		Set<CvCompetenzeTrasv> compTrasv = cvDatiPersonaliOrig.getCvCompetenzeTrasvs();
		Set<CvEsperienzeProf> esps = cvDatiPersonaliOrig.getCvEsperienzeProfs();
		Set<CvFormazione> formaziones = cvDatiPersonaliOrig.getCvFormaziones();
		CvInformatica cvInformatica = cvDatiPersonaliOrig.getCvInformatica();
		Set<CvIstruzione> istruziones = cvDatiPersonaliOrig.getCvIstruziones();
		Set<CvLingua> linguas = cvDatiPersonaliOrig.getCvLinguas();
		Set<CvPatente> patentes = cvDatiPersonaliOrig.getCvPatentes();
		Set<CvPatentino> patentinos = cvDatiPersonaliOrig.getCvPatentinos();
		Set<CvProfDesiderate> cvProfDesiderates = cvDatiPersonaliOrig.getCvProfDesiderates();
		Poi poi = cvDatiPersonaliOrig.getPoi();

		/* prima copio tutti i dati come nel CV originale */
		cvDatiPersonaliCopiato.setPfPrincipal(cvDatiPersonaliOrig.getPfPrincipal());
		cvDatiPersonaliCopiato.setRicercabile(cvDatiPersonaliOrig.isRicercabile());
		cvDatiPersonaliCopiato.setDeAmbitoDiffusione(
				deAmbitoDiffusioneHome.findById(ConstantsSingleton.DeAmbitoDiffusione.REGIONALE));
		cvDatiPersonaliCopiato.setProvenienzaCurriculum(cvDatiPersonaliOrig.getProvenienzaCurriculum());
		cvDatiPersonaliCopiato.setPfPrincipalPalese(cvDatiPersonaliOrig.getPfPrincipalPalese());
		cvDatiPersonaliCopiato.setDtScadenza(cvDatiPersonaliOrig.getDtScadenza());
		cvDatiPersonaliCopiato.setIntermediario(cvDatiPersonaliOrig.getIntermediario());
		cvDatiPersonaliCopiato.setVisibilita(cvDatiPersonaliOrig.getVisibilita());
		cvDatiPersonaliCopiato.setDenominazioneIntermediario(cvDatiPersonaliOrig.getDenominazioneIntermediario());
		// e' comunque impossibile che venga fatta la copia di un CV con il flag a true
		cvDatiPersonaliCopiato.setFlagEliminato(false);
		cvDatiPersonaliCopiato.setFlagIdo(true);// nuovo ido per forza
		cvDatiPersonaliCopiato.setNoteAbilitazioni(cvDatiPersonaliOrig.getNoteAbilitazioni());

		/* modifico i dati originali nei casi particolari */
		if (perCandidatura) {
			/* copia per una candidatura, setto il flag_inviato */
			cvDatiPersonaliCopiato.setFlagInviato(true);
			if (cvDatiPersonaliOrig.isIntermediato() && cvDatiPersonaliOrig.getPfPrincipalPalese() != null) {
				cvDatiPersonaliCopiato.setPfPrincipal(cvDatiPersonaliOrig.getPfPrincipalPalese());
			}
			cvDatiPersonaliCopiato.setPfPrincipalPalese(cvDatiPersonaliOrig.getPfPrincipalPalese());
			if (!cvDatiPersonaliOrig.isFromClicLavoro()) {
				cvDatiPersonaliCopiato.setProvenienzaCurriculum(
						deProvenienzaHome.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
			}
			cvDatiPersonaliCopiato.setVisibilita(true);
			cvDatiPersonaliCopiato.setDtScadenza(null);
			cvDatiPersonaliCopiato.setIntermediario(null);
			cvDatiPersonaliCopiato.setDenominazioneIntermediario(null);
		} else {
			/* copia volontaria */

			cvDatiPersonaliCopiato.setFlagInviato(false);

			/*
			 * copia volontaria di un CV proveniente da SIL o Cliclavoro, il nuovo CV viene associato all'utente in
			 * sessione ed annullo tutte le informazioni relative all'intermediazione e sincronizzazione con cliclavoro
			 */
			cvDatiPersonaliCopiato.setPfPrincipal(pfPrincipalHome.findById(pfPrincipalId));
			cvDatiPersonaliCopiato.setProvenienzaCurriculum(
					deProvenienzaHome.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
			cvDatiPersonaliCopiato.setPfPrincipalPalese(cvDatiPersonaliOrig.getPfPrincipalPalese());
			cvDatiPersonaliCopiato.setDtScadenza(null);
			cvDatiPersonaliCopiato.setIntermediario(null);
			cvDatiPersonaliCopiato.setVisibilita(true);
			cvDatiPersonaliCopiato.setDenominazioneIntermediario(null);
		}

		cvDatiPersonaliCopiato.setIndirizzoDomicilio(cvDatiPersonaliOrig.getIndirizzoDomicilio());
		cvDatiPersonaliCopiato.setDeComuneDomicilio(cvDatiPersonaliOrig.getDeComuneDomicilio());
		cvDatiPersonaliCopiato.setEmail(cvDatiPersonaliOrig.getEmail());
		cvDatiPersonaliCopiato.setTel1(cvDatiPersonaliOrig.getTel1());
		cvDatiPersonaliCopiato.setTel2(cvDatiPersonaliOrig.getTel2());
		cvDatiPersonaliCopiato.setFax(cvDatiPersonaliOrig.getFax());
		cvDatiPersonaliCopiato.setDeComuneNascita(cvDatiPersonaliOrig.getDeComuneNascita());
		cvDatiPersonaliCopiato.setDtNascita(cvDatiPersonaliOrig.getDtNascita());
		cvDatiPersonaliCopiato.setDescrizione(descrizione);
		cvDatiPersonaliCopiato.setDeMotivoChiusura(cvDatiPersonaliOrig.getDeMotivoChiusura());
		cvDatiPersonaliCopiato.setTitolo(cvDatiPersonaliOrig.getTitolo());
		cvDatiPersonaliCopiato.setDescrizioneCv(cvDatiPersonaliOrig.getDescrizioneCv());
		cvDatiPersonaliCopiato.setOpzTipoDecodifiche(cvDatiPersonaliOrig.getOpzTipoDecodifiche());
		cvDatiPersonaliCopiato.setIndirizzoProfilo(cvDatiPersonaliOrig.getIndirizzoProfilo());
		cvDatiPersonaliCopiato.setRiferimentiMessaggistica(cvDatiPersonaliOrig.getRiferimentiMessaggistica());
		cvDatiPersonaliCopiato.setFoto(cvDatiPersonaliOrig.getFoto());

		cvDatiPersonaliCopiato.setAcCandidaturas(new HashSet<AcCandidatura>(0));

		if (poi != null) {
			Poi copyPoi = poiHome.copyById(pfPrincipalId, poi, cvDatiPersonaliOrig);
			cvDatiPersonaliCopiato.setPoi(copyPoi);
		}

		cvDatiPersonaliCopiato.setDtmIns(now);
		cvDatiPersonaliCopiato.setDtmMod(now);
		cvDatiPersonaliCopiato.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
		cvDatiPersonaliCopiato.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));

		persist(cvDatiPersonaliCopiato);
		log.debug("Id nuovo curriculum creato byCopy: " + cvDatiPersonaliCopiato.getIdCvDatiPersonali());

		CvAllegato cvAllegatoOrg = cvAllegatoHome.findEntityByCurriculumId(cvDatiPersonaliOrig.getIdCvDatiPersonali());
		if (cvAllegatoOrg != null) {
			CvAllegato cvAllegato = new CvAllegato();
			cvAllegato.setCvDatiPersonali(cvDatiPersonaliCopiato);
			cvAllegato.setContenuto(cvAllegatoOrg.getContenuto());
			cvAllegato.setFilename(cvAllegatoOrg.getFilename());
			cvAllegato.setFilename(cvAllegatoOrg.getFilename());
			cvAllegato.setDtmIns(now);
			cvAllegato.setDtmMod(now);
			cvAllegato.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvAllegato.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));

			cvAllegatoHome.persist(cvAllegato);
		}

		copyCollection(pfPrincipalId, cvAgevolazioneHome, agevolaziones, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvAlboHome, albos, cvDatiPersonaliCopiato);
		if (cvAltreInfo != null) {
			cvAltreInfoHome.copyById(pfPrincipalId, cvAltreInfo, cvDatiPersonaliCopiato);
		}
		copyCollection(pfPrincipalId, cvCompetenzeTrasvHome, compTrasv, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvEsperienzeProfHome, esps, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvFormazioneHome, formaziones, cvDatiPersonaliCopiato);
		if (cvInformatica != null) {
			cvInformaticaHome.copyById(pfPrincipalId, cvInformatica, cvDatiPersonaliCopiato);
		}
		copyCollection(pfPrincipalId, cvIstruzioneHome, istruziones, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvLinguaHome, linguas, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvPatenteHome, patentes, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvPatentinoHome, patentinos, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvProfDesiderateHome, cvProfDesiderates, cvDatiPersonaliCopiato);

		return cvDatiPersonaliCopiato;
	}

	/**
	 * Interfaccia per copia senza DTO introdotto con passaggio a PF, ma modifiche minime
	 *
	 * @since 1.24.4
	 *
	 * @param pfPrincipalId
	 * @param curriculumId
	 * @param descrizione
	 * @param perCandidatura
	 * @return
	 */
	public CvDatiPersonali copyByIdNoDTO(Integer pfPrincipalId, Integer curriculumId, String descrizione,
			Boolean perCandidatura) {
		Date now = new Date();

		//

		CvDatiPersonali cvDatiPersonaliCopiato = new CvDatiPersonali();

		// TODO 100 e' il limite della colonna sul DB
		if (descrizione.length() > 100) {
			throw new MyPortalException("data.error.copying.longname");
		}
		CvDatiPersonali cvDatiPersonaliOrig = findById(curriculumId);
		if (cvDatiPersonaliOrig == null) {
			throw new MyPortalException("data.error.copying");
		}

		Set<CvAgevolazione> agevolaziones = cvDatiPersonaliOrig.getCvAgevolaziones();
		Set<CvAlbo> albos = cvDatiPersonaliOrig.getCvAlbos();
		CvAltreInfo cvAltreInfo = cvDatiPersonaliOrig.getCvAltreInfo();
		Set<CvCompetenzeTrasv> compTrasv = cvDatiPersonaliOrig.getCvCompetenzeTrasvs();
		Set<CvEsperienzeProf> esps = cvDatiPersonaliOrig.getCvEsperienzeProfs();
		Set<CvFormazione> formaziones = cvDatiPersonaliOrig.getCvFormaziones();
		CvInformatica cvInformatica = cvDatiPersonaliOrig.getCvInformatica();
		Set<CvIstruzione> istruziones = cvDatiPersonaliOrig.getCvIstruziones();
		Set<CvLingua> linguas = cvDatiPersonaliOrig.getCvLinguas();
		Set<CvPatente> patentes = cvDatiPersonaliOrig.getCvPatentes();
		Set<CvPatentino> patentinos = cvDatiPersonaliOrig.getCvPatentinos();
		Set<CvProfDesiderate> cvProfDesiderates = cvDatiPersonaliOrig.getCvProfDesiderates();
		Poi poi = cvDatiPersonaliOrig.getPoi();

		/* prima copio tutti i dati come nel CV originale */
		cvDatiPersonaliCopiato.setPfPrincipal(cvDatiPersonaliOrig.getPfPrincipal());
		cvDatiPersonaliCopiato.setRicercabile(cvDatiPersonaliOrig.isRicercabile());
		cvDatiPersonaliCopiato.setDeAmbitoDiffusione(
				deAmbitoDiffusioneHome.findById(ConstantsSingleton.DeAmbitoDiffusione.REGIONALE));
		cvDatiPersonaliCopiato.setProvenienzaCurriculum(cvDatiPersonaliOrig.getProvenienzaCurriculum());
		cvDatiPersonaliCopiato.setPfPrincipalPalese(cvDatiPersonaliOrig.getPfPrincipalPalese());
		cvDatiPersonaliCopiato.setDtScadenza(cvDatiPersonaliOrig.getDtScadenza());
		cvDatiPersonaliCopiato.setIntermediario(cvDatiPersonaliOrig.getIntermediario());
		cvDatiPersonaliCopiato.setVisibilita(cvDatiPersonaliOrig.getVisibilita());
		cvDatiPersonaliCopiato.setDenominazioneIntermediario(cvDatiPersonaliOrig.getDenominazioneIntermediario());
		// e' comunque impossibile che venga fatta la copia di un CV con il flag a true
		cvDatiPersonaliCopiato.setFlagEliminato(false);
		cvDatiPersonaliCopiato.setFlagIdo(cvDatiPersonaliOrig.getFlagIdo());
		cvDatiPersonaliCopiato.setNoteAbilitazioni(cvDatiPersonaliOrig.getNoteAbilitazioni());
		cvDatiPersonaliCopiato.setCodiceFiscale(cvDatiPersonaliOrig.getCodiceFiscale());

		cvDatiPersonaliCopiato.setFlagTrattamentoDati(cvDatiPersonaliOrig.getFlagTrattamentoDati());

		/* modifico i dati originali nei casi particolari */
		if (perCandidatura) {
			/* copia per una candidatura, setto il flag_inviato */
			cvDatiPersonaliCopiato.setFlagInviato(true);
			if (cvDatiPersonaliOrig.isIntermediato() && cvDatiPersonaliOrig.getPfPrincipalPalese() != null) {
				cvDatiPersonaliCopiato.setPfPrincipal(cvDatiPersonaliOrig.getPfPrincipalPalese());
			}
			cvDatiPersonaliCopiato.setPfPrincipalPalese(cvDatiPersonaliOrig.getPfPrincipalPalese());
			if (!cvDatiPersonaliOrig.isFromClicLavoro()) {
				cvDatiPersonaliCopiato.setProvenienzaCurriculum(
						deProvenienzaHome.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
			}
			cvDatiPersonaliCopiato.setVisibilita(true);
			cvDatiPersonaliCopiato.setDtScadenza(null);
			cvDatiPersonaliCopiato.setIntermediario(null);
			cvDatiPersonaliCopiato.setDenominazioneIntermediario(null);
		} else {
			/* copia volontaria */

			cvDatiPersonaliCopiato.setFlagInviato(false);

			/*
			 * copia volontaria di un CV proveniente da SIL o Cliclavoro, il nuovo CV viene associato all'utente in
			 * sessione ed annullo tutte le informazioni relative all'intermediazione e sincronizzazione con cliclavoro
			 */
			cvDatiPersonaliCopiato.setPfPrincipal(pfPrincipalHome.findById(pfPrincipalId));
			cvDatiPersonaliCopiato.setProvenienzaCurriculum(
					deProvenienzaHome.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
			cvDatiPersonaliCopiato.setPfPrincipalPalese(cvDatiPersonaliOrig.getPfPrincipalPalese());
			// cvDatiPersonaliCopiato.setDtScadenza(null);
			cvDatiPersonaliCopiato.setIntermediario(null);
			cvDatiPersonaliCopiato.setVisibilita(true);
			cvDatiPersonaliCopiato.setDenominazioneIntermediario(null);

			/* in PAT 60 giorni */
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_TRENTO) {
				cvDatiPersonaliCopiato.setDtScadenza(DateUtils.addDays(now, 60));
			} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				cvDatiPersonaliCopiato.setDtScadenza(DateUtils.addDays(now, 180));
			} else {
				cvDatiPersonaliCopiato.setDtScadenza(DateUtils.addDays(now, 30));
			}

		}

		cvDatiPersonaliCopiato.setIndirizzoDomicilio(cvDatiPersonaliOrig.getIndirizzoDomicilio());
		cvDatiPersonaliCopiato.setDeComuneDomicilio(cvDatiPersonaliOrig.getDeComuneDomicilio());
		cvDatiPersonaliCopiato.setEmail(cvDatiPersonaliOrig.getEmail());
		cvDatiPersonaliCopiato.setTel1(cvDatiPersonaliOrig.getTel1());
		cvDatiPersonaliCopiato.setTel2(cvDatiPersonaliOrig.getTel2());
		cvDatiPersonaliCopiato.setFax(cvDatiPersonaliOrig.getFax());
		cvDatiPersonaliCopiato.setDeComuneNascita(cvDatiPersonaliOrig.getDeComuneNascita());
		cvDatiPersonaliCopiato.setDtNascita(cvDatiPersonaliOrig.getDtNascita());
		cvDatiPersonaliCopiato.setDescrizione(descrizione);
		cvDatiPersonaliCopiato.setDeMotivoChiusura(cvDatiPersonaliOrig.getDeMotivoChiusura());
		cvDatiPersonaliCopiato.setTitolo(cvDatiPersonaliOrig.getTitolo());
		cvDatiPersonaliCopiato.setDescrizioneCv(cvDatiPersonaliOrig.getDescrizioneCv());
		cvDatiPersonaliCopiato.setOpzTipoDecodifiche(cvDatiPersonaliOrig.getOpzTipoDecodifiche());
		cvDatiPersonaliCopiato.setIndirizzoProfilo(cvDatiPersonaliOrig.getIndirizzoProfilo());
		cvDatiPersonaliCopiato.setRiferimentiMessaggistica(cvDatiPersonaliOrig.getRiferimentiMessaggistica());
		cvDatiPersonaliCopiato.setFoto(cvDatiPersonaliOrig.getFoto());

		cvDatiPersonaliCopiato.setAcCandidaturas(new HashSet<AcCandidatura>(0));

		if (poi != null) {
			Poi copyPoi = poiHome.copyById(pfPrincipalId, poi, cvDatiPersonaliOrig);
			cvDatiPersonaliCopiato.setPoi(copyPoi);
		}

		cvDatiPersonaliCopiato.setDtmIns(now);
		cvDatiPersonaliCopiato.setDtmMod(now);
		cvDatiPersonaliCopiato.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
		cvDatiPersonaliCopiato.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));

		persist(cvDatiPersonaliCopiato);
		log.debug("Id nuovo curriculum creato byCopy: " + cvDatiPersonaliCopiato.getIdCvDatiPersonali());

		CvAllegato cvAllegatoOrg = cvAllegatoHome.findEntityByCurriculumId(cvDatiPersonaliOrig.getIdCvDatiPersonali());
		if (cvAllegatoOrg != null) {
			CvAllegato cvAllegato = new CvAllegato();
			cvAllegato.setCvDatiPersonali(cvDatiPersonaliCopiato);
			cvAllegato.setContenuto(cvAllegatoOrg.getContenuto());
			cvAllegato.setFilename(cvAllegatoOrg.getFilename());
			cvAllegato.setFilename(cvAllegatoOrg.getFilename());
			cvAllegato.setDtmIns(now);
			cvAllegato.setDtmMod(now);
			cvAllegato.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvAllegato.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));

			cvAllegatoHome.persist(cvAllegato);
		}

		copyCollection(pfPrincipalId, cvAgevolazioneHome, agevolaziones, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvAlboHome, albos, cvDatiPersonaliCopiato);
		if (cvAltreInfo != null) {
			cvAltreInfoHome.copyById(pfPrincipalId, cvAltreInfo, cvDatiPersonaliCopiato);
		}
		copyCollection(pfPrincipalId, cvCompetenzeTrasvHome, compTrasv, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvEsperienzeProfHome, esps, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvFormazioneHome, formaziones, cvDatiPersonaliCopiato);
		if (cvInformatica != null) {
			cvInformaticaHome.copyById(pfPrincipalId, cvInformatica, cvDatiPersonaliCopiato);
		}
		copyCollection(pfPrincipalId, cvIstruzioneHome, istruziones, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvLinguaHome, linguas, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvPatenteHome, patentes, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvPatentinoHome, patentinos, cvDatiPersonaliCopiato);
		copyCollection(pfPrincipalId, cvProfDesiderateHome, cvProfDesiderates, cvDatiPersonaliCopiato);

		return cvDatiPersonaliCopiato;
	}

	/**
	 * Esegue la copia di una collection di elementi
	 *
	 * @param pfPrincipalId
	 *            utente che esegue la copia
	 * @param home
	 *            la Home incaricata di eseguire la copia
	 * @param collection
	 *            la collezione da copiare
	 * @param cvDatiPersonali
	 *            il curriculum a cui collegare la collection copiata
	 */
	private <Entity extends IEntity> void copyCollection(Integer pfPrincipalId,
			AbstractCurriculumEntityListHome<Entity, ?> home, Set<Entity> collection, CvDatiPersonali cvDatiPersonali) {
		if (collection != null) {
			for (Entity entity : collection) {
				home.copyById(pfPrincipalId, entity, cvDatiPersonali);
			}
		}
	}

	/**
	 * Esegue la copia di una collection di elementi
	 *
	 * @param pfPrincipalId
	 *            utente che esegue la copia
	 * @param home
	 *            la Home incaricata di eseguire la copia
	 * @param collection
	 *            la collezione da copiare
	 * @param cvDatiPersonali
	 *            il curriculum a cui collegare la collection copiata
	 */
	private <Entity extends IEntity> void copyCollection(Integer pfPrincipalId,
			AbstractCurriculumEntityHome<Entity, ?> home, Set<Entity> collection, CvDatiPersonali cvDatiPersonali) {
		if (collection != null) {
			for (Entity entity : collection) {
				home.copyById(pfPrincipalId, entity, cvDatiPersonali);
			}
		}
	}

	public CvVisualizzaDTO findVisualizzaDTOById(int curriculumId, Integer pfPrincipalId) {
		CvDatiPersonali cvDatiPersonali = findById(curriculumId);
		CvDatiPersonaliDTO cvDatiPersonaliDTO = toDTO(cvDatiPersonali);
		PfPrincipal pfPrincipalRequest = pfPrincipalHome.findById(pfPrincipalId);
		CvVisualizzaDTO cvVisualizzaDTO = new CvVisualizzaDTO();

		if (checkViewPermissions(cvDatiPersonali, pfPrincipalRequest)) {
			cvVisualizzaDTO.setCvDatiPersonaliDTO(cvDatiPersonaliDTO);
			PfPrincipal pfPrincipal = pfPrincipalHome.findById(cvDatiPersonaliDTO.getIdPfPrincipal());
			cvVisualizzaDTO.setId(curriculumId);

			/* nome e cognome da visualizzare */
			Integer idPfPrincipalPalese = cvDatiPersonaliDTO.getIdPfPrincipalPalese();
			if (idPfPrincipalPalese != null) {
				PfPrincipal pfPrincipalPalese = pfPrincipalHome.findById(idPfPrincipalPalese);
				cvVisualizzaDTO.setNome(pfPrincipalPalese.getNome());
				cvVisualizzaDTO.setCognome(pfPrincipalPalese.getCognome());
			} else {
				cvVisualizzaDTO.setNome(pfPrincipal.getNome());
				cvVisualizzaDTO.setCognome(pfPrincipal.getCognome());
			}

			cvVisualizzaDTO.setListaCvIstruzioneDTO(cvIstruzioneHome.findDTOByCurriculumId(curriculumId));
			cvVisualizzaDTO.setListaCvFormazioneDTO(cvFormazioneHome.findDTOByCurriculumId(curriculumId));
			cvVisualizzaDTO
					.setListaCvEsperienzeProfessionaliDTO(cvEsperienzeProfHome.findDTOByCurriculumId(curriculumId));
			cvVisualizzaDTO.setListaCvLinguaDTO(cvLinguaHome.findDTOByCurriculumId(curriculumId));
			cvVisualizzaDTO.setCvInformaticaDTO(cvInformaticaHome.findDTOByCurriculumId(curriculumId));
			cvVisualizzaDTO.setCvAbilitazioniDTO(cvAbilitazioneHome.findDTOByCurriculumId(curriculumId));
			cvVisualizzaDTO.setCvCompetenzeTrasvDTO(cvCompetenzeTrasvHome.findDTOByCurriculumId(curriculumId));
			cvVisualizzaDTO.setCvAltreInfoDTO(cvAltreInfoHome.findDTOByCurriculumId(curriculumId));
			List<CvPatenteDTO> cvPatenti = cvPatenteHome.findDTOByCurriculumId(curriculumId);
			cvVisualizzaDTO.setListaCvPatentiDTO(cvPatenti);
			List<CvPatentinoDTO> cvPatentini = cvPatentinoHome.findDTOByCurriculumId(curriculumId);
			cvVisualizzaDTO.setListaCvPatentiniDTO(cvPatentini);
			List<CvAlboDTO> cvAlbo = cvAlboHome.findDTOByCurriculumId(curriculumId);
			cvVisualizzaDTO.setListaCvAlboDTO(cvAlbo);
			List<CvAgevolazioneDTO> cvAgevolazioni = cvAgevolazioneHome.findDTOByCurriculumId(curriculumId);
			cvVisualizzaDTO.setListaCvAgevolazioni(cvAgevolazioni);
			cvVisualizzaDTO.setListCvProfDesiderataDTO(cvProfDesiderateHome.findDTOByCurriculumId(curriculumId));

			/* informazioni di sincronizzazione con cliclavoro */
			cvVisualizzaDTO.setSincronizzatoCliclavoro(cvDatiPersonali.isSincronizzatoCliclavoro());

			cvVisualizzaDTO.setTestataVisible(isTestataVisibile(cvDatiPersonali, pfPrincipalRequest));
		}

		return cvVisualizzaDTO;
	}

	private boolean checkViewPermissions(CvDatiPersonali cvDatiPersonali, PfPrincipal pfPrincipalRequest) {
		boolean result = false;
		/*
		 * se è un utente che richiede il CV, deve esserne il proprietario
		 */
		if (pfPrincipalRequest.isUtente()) {
			if (!cvDatiPersonali.isProprietario(pfPrincipalRequest.getIdPfPrincipal())) {
				throw new MyPortalException("curriculum.insufficient_permission");
			} else {
				result = true;
			}
		}
		/* se è un'azienda che richiede CV, deve esserle stato inviato */
		else if (pfPrincipalRequest.isAzienda()) {
			AziendaInfo aziendaInfo = pfPrincipalRequest.getAziendaInfo();
			if (!canAziendaViewCurriculum(aziendaInfo.getIdPfPrincipal(), cvDatiPersonali.getIdCvDatiPersonali())) {
				throw new MyPortalException("curriculum.insufficient_permission");
			} else {
				result = true;
			}
		}
		/*
		 * se e' una provincia che richiede il CV deve essere competente per il lavoratore.
		 */
		else if (pfPrincipalRequest.isProvincia()) {
			// clic lavoro vede tutti i cv
			if (pfPrincipalRequest.isClicLavoro()) {
				result = true;
			} else {
				Provincia provincia = pfPrincipalRequest.getProvinciasForIdPfPrincipal().iterator().next();
				boolean isProvCompetente = false;
				boolean isProvCompetenteByPalese = false;
				Integer idPfPrincipal = cvDatiPersonali.getPfPrincipal().getIdPfPrincipal();
				PfPrincipal pfPrincPalese = cvDatiPersonali.getPfPrincipalPalese();

				// Non dovrebbe mai capitare, cmq controlliamo
				if (idPfPrincipal != null) {
					isProvCompetente = utenteInfoHome.isProvinciaCompetente(provincia, idPfPrincipal);
				}
				if (pfPrincPalese != null) {
					Integer idPfPrincipalPalese = pfPrincPalese.getIdPfPrincipal();
					isProvCompetenteByPalese = utenteInfoHome.isProvinciaCompetente(provincia, idPfPrincipalPalese);
				}

				boolean isFromSILCompetente = (ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC
						+ provincia.getDeProvincia().getTarga())
								.equalsIgnoreCase(cvDatiPersonali.getProvenienzaCurriculum().getCodProvenienza());

				if (isProvCompetente || isProvCompetenteByPalese || isFromSILCompetente
						|| cvDatiPersonali.isFromClicLavoro()) {
					result = true;
				} else {
					throw new MyPortalException("curriculum.no_competence");
				}
				result = true;
			}
		}

		return result;
	}

	/**
	 * Determina se un'azienda può visualizzare il CV in base al fatto se questo le è stato inviato o meno. TODO al
	 * momento l'azieda può visualizzare tutti i CV
	 *
	 * @param pfPrincipalRequest
	 * @param curriculumId
	 * @return
	 */
	private boolean canAziendaViewCurriculum(Integer idAziendaInfo, Integer curriculumId) {
		/*
		 * CriteriaBuilder cb = entityManager.getCriteriaBuilder(); CriteriaQuery<AcCandidatura> c =
		 * cb.createQuery(AcCandidatura.class); Root<AcCandidatura> acCandidatura = c.from(AcCandidatura.class);
		 * Join<AcCandidatura, CvDatiPersonali> cvDatiPersonali = acCandidatura.join(AcCandidatura_.cvDatiPersonali);
		 *
		 * Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = acCandidatura.join(AcCandidatura_.vaDatiVacancy,
		 * JoinType.LEFT); Join<VaDatiVacancy, VaDatiAziendaInfo> vaDatiAziendaInfo =
		 * vaDatiVacancy.join(VaDatiVacancy_.vaDatiAziendaInfo, JoinType.LEFT); Join<VaDatiAziendaInfo, AziendaInfo>
		 * aziendaInfo = vaDatiAziendaInfo.join(VaDatiAziendaInfo_.aziendaInfo, JoinType.LEFT); Join<VaDatiAziendaInfo,
		 * AziendaInfo> aziendaInfoPalese = vaDatiAziendaInfo.join( VaDatiAziendaInfo_.aziendaInfoPalese,
		 * JoinType.LEFT);
		 *
		 * Predicate idAziendaInfoCondition = cb.equal(aziendaInfo.get(AziendaInfo_.idAziendaInfo), idAziendaInfo);
		 * Predicate idAziendaInfoPaleseCondition = cb.equal(aziendaInfoPalese.get(AziendaInfo_.idAziendaInfo),
		 * idAziendaInfo); Predicate aziendaCondition = cb.or(idAziendaInfoCondition, idAziendaInfoPaleseCondition);
		 * Predicate cvCondition = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali), curriculumId);
		 *
		 * c = c.where(cb.or(aziendaCondition, cvCondition)); List<AcCandidatura> list =
		 * entityManager.createQuery(c).getResultList(); return list.size() > 0;
		 */
		return true;
	}

	/**
	 * Restituisce l'ultimo cv dell'utente e lancia un'eccezione se l'utente non ha alcun CV.
	 *
	 * @param idUtente
	 * @return l'ultimo cv dell'utente
	 */
	public CvDatiPersonaliDTO findLastCurriculumByIdPfPrincipal(Integer idPfPrincipal) {
		List<CvDatiPersonali> curricula = findByIdPfPrincipal(idPfPrincipal);
		if (curricula.size() > 0)
			return toDTO(curricula.get(0));
		else
			throw new MyPortalException("data.error_loading");
	}

	/**
	 * Il metodo restituisce true se e' necessario comunicare a cliclavoro i dati del CV passato come parametro. Un CV
	 * va comunicato a cliclavoro se: 1) e' gia' sincronizzato con cliclavoro, 2) se la modifica corrente comporta la
	 * sincronizzazione con cliclavoro, 3) se lo stato di invio dell'ultima comunicazione con cliclavoro si trova in
	 * stato CE
	 *
	 * @param codStatoInvioCl
	 * @param codAmbitoDiffusionePrecedente
	 * @param codAmbitoDiffusioneNuovo
	 * @return
	 */
	public boolean needsMergeCliclavoro(Integer idCvDatiPersonali, String codAmbitoDiffusionePrecedente,
			String codAmbitoDiffusioneNuovo) {
		CvCandidaturaCl cvCandidaturaCl = cvCandidaturaClHome.findById(idCvDatiPersonali);
		String codStatoInvioCl = "";
		if (cvCandidaturaCl != null) {
			codStatoInvioCl = cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl();
		}

		boolean giaSincronizzato = deAmbitoDiffusioneHome.isSincronizzatoClicLavoro(codAmbitoDiffusionePrecedente);
		boolean nuovaSincronizzazione = deAmbitoDiffusioneHome.isSincronizzatoClicLavoro(codAmbitoDiffusioneNuovo);
		boolean erroreChiusura = deStatoInvioClHome.isErroreChiusura(codStatoInvioCl);

		return giaSincronizzato || nuovaSincronizzazione || erroreChiusura;
	}

	public CvDatiPersonaliDTO mergeClicLavoro(Integer idPfPrincipal, Integer idCvDatiPersonali) {
		if (idCvDatiPersonali == null) {
			// ERRORE
			throw new MyPortalException("curriculum.error_updating");
		}
		String codAmbitoDiffusione = findById(idCvDatiPersonali).getDeAmbitoDiffusione().getCodAmbitoDiffusione();

		/*
		 * questo metodo viene chiamato dai bean delle tab, percio' l'ambito di diffusione del CV non puo' essere
		 * cambiato
		 */
		return mergeClicLavoro(idPfPrincipal, idCvDatiPersonali, codAmbitoDiffusione, codAmbitoDiffusione);
	}

	public CvDatiPersonaliDTO mergeClicLavoro(Integer idPfPrincipal, Integer idCvDatiPersonali,
			String codAmbitoDiffusionePrecedente, String codAmbitoDiffusioneNuovo) {
		if (idCvDatiPersonali == null) {
			throw new MyPortalException("curriculum.error_loading");
		}

		CvDatiPersonali cvDatiPersonali = findById(idCvDatiPersonali);
		CvCandidaturaCl cvCandidaturaCl = cvCandidaturaClHome.findById(idCvDatiPersonali);
		/*
		 * indica se e' la prima volta che il CV viene sincronizzato su cliclavoro
		 */
		boolean primaApertura;
		if (cvCandidaturaCl == null) {
			primaApertura = true;
		} else {
			primaApertura = false;
		}

		if (deAmbitoDiffusioneHome.isSincronizzatoClicLavoro(codAmbitoDiffusionePrecedente)
				|| deAmbitoDiffusioneHome.isSincronizzatoClicLavoro(codAmbitoDiffusioneNuovo)) {
			/*
			 * controllo dei vincoli per la sincronizzazione con cliclavoro, se falliscono viene lanciata un'eccezione e
			 * la transazione fa rollback
			 */
			checkErrorsClicLavoro(idCvDatiPersonali);
		}

		if (codAmbitoDiffusionePrecedente.equals(codAmbitoDiffusioneNuovo)) {
			/* l'ambito di diffusione non e' cambiato */
			if (deAmbitoDiffusioneHome.isSincronizzatoClicLavoro(codAmbitoDiffusionePrecedente)) {
				/* sono sincronizzato con cliclavoro */
				/* mando una comunicazione di rettifica */
				cvCandidaturaCl = cvCandidaturaClHome.updateCandidatura(idPfPrincipal, idCvDatiPersonali,
						cvCandidaturaCl, AzioneCliclavoro.MODIFICA_CV);
			} else {
				if (!primaApertura && deStatoInvioClHome
						.isErroreChiusura(cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl())) {
					/* ripristino lo stato in CA */
					cvCandidaturaCl = cvCandidaturaClHome.updateCandidatura(idPfPrincipal, idCvDatiPersonali,
							cvCandidaturaCl, AzioneCliclavoro.MODIFICA_CV);
				} else {
					/* non sono sincronizzato con cliclavoro, non faccio nulla */
					return toDTO(cvDatiPersonali);
				}
			}
		} else {
			/* l'ambito di diffusione e' cambiato */
			if (deAmbitoDiffusioneHome.isSincronizzatoClicLavoro(codAmbitoDiffusionePrecedente)) {
				/* sono sincronizzato con cliclavoro */
				if (deAmbitoDiffusioneHome.isSincronizzatoClicLavoro(codAmbitoDiffusioneNuovo)) {
					/* mando una comunicazione di rettifica */
					cvCandidaturaCl = cvCandidaturaClHome.updateCandidatura(idPfPrincipal, idCvDatiPersonali,
							cvCandidaturaCl, AzioneCliclavoro.MODIFICA_CV);
				} else {
					/* devo chiudere la sincronizzazione con cliclavoro */
					if (!primaApertura && deStatoInvioClHome
							.giaComunicatoCliclavoro(cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl())) {
						/* mando un acomunicazione di chiusura */
						cvCandidaturaCl = cvCandidaturaClHome.updateCandidatura(idPfPrincipal, idCvDatiPersonali,
								cvCandidaturaCl, AzioneCliclavoro.CHIUSURA);
					} else {
						/* non ho ancora comunicato l'apertura, la elimino da DB */
						cvCandidaturaClHome.remove(cvCandidaturaCl);
						cvCandidaturaCl = null;
					}
				}
			} else {
				/* mi devo sincronizzare con cliclavoro */
				if (primaApertura) {
					/* mando una comunicazione di apertura */
					cvCandidaturaCl = cvCandidaturaClHome.updateCandidatura(idPfPrincipal, idCvDatiPersonali,
							cvCandidaturaCl, AzioneCliclavoro.SINCRONIZZO);
				} else {
					if (cvCandidaturaClHome.isChiusuraInviata(cvCandidaturaCl)) {
						/* ripristino una comunicazione di apertura */
						cvCandidaturaCl = cvCandidaturaClHome.updateCandidatura(idPfPrincipal, idCvDatiPersonali,
								cvCandidaturaCl, AzioneCliclavoro.SINCRONIZZO);
					} else {
						/* ripristino una comunicazione di rettifica */
						cvCandidaturaCl = cvCandidaturaClHome.updateCandidatura(idPfPrincipal, idCvDatiPersonali,
								cvCandidaturaCl, AzioneCliclavoro.SINCRONIZZO);
					}
				}
			}

			/* aggiorno l'ambito di diffusione del CV */
			cvDatiPersonali.setDeAmbitoDiffusione(deAmbitoDiffusioneHome.findById(codAmbitoDiffusioneNuovo));

			/*
			 * se non ho eliminato la riga dal DB procedo con l'inserimento o l'aggiornamento
			 */
			if (cvCandidaturaCl != null) {
				if (primaApertura) {
					cvCandidaturaClHome.persist(cvCandidaturaCl);
				} else {
					cvCandidaturaClHome.merge(cvCandidaturaCl);
				}
			}
		}
		return toDTO(cvDatiPersonali);
	}

	/**
	 * Verifica se la candidatura può essere inviata a clicLavoro
	 *
	 *
	 * @param idCvDatiPersonali
	 */
	public void checkErrorsClicLavoro(Integer idCvDatiPersonali) {
		CvDatiPersonali statoPrecedente = findById(idCvDatiPersonali);

		ArrayList<String> warningMessages = new ArrayList<String>();
		String errorMessage = "Attenzione: per poter attivare la trasmissione del CV al sistema nazionale Cliclavoro è necessario ";
		String profDesiderateErrorMessage = " procedere all'inserimento di almeno una professione desiderata";
		String campiErrorMessage = " valorizzare i campi Codice Fiscale e Cittadinanza nella sezione Profilo";
		boolean isErrore = false;
		int numProfDesiderate = statoPrecedente.getCvProfDesiderates().size();
		String codiceFiscale = statoPrecedente.getPfPrincipal().getUtenteInfo().getCodiceFiscale();
		DeCittadinanza deCittadinanza = statoPrecedente.getPfPrincipal().getUtenteInfo().getDeCittadinanza();
		// verifica se c'è almeno una professione desiderata
		if (numProfDesiderate == 0) {
			errorMessage += profDesiderateErrorMessage;
			if (codiceFiscale == null || codiceFiscale.isEmpty() || deCittadinanza == null) {
				errorMessage += " e" + campiErrorMessage;
			}
			errorMessage += ".";
			isErrore = true;
		}
		// altrimenti verifica se codice fiscale e cittadinanza sono valorizzati
		else {
			if (codiceFiscale == null || deCittadinanza == null) {
				errorMessage += campiErrorMessage + ".";
				isErrore = true;
			}
		}

		if (isErrore) { // caso A1: errori presenti
			log.debug("Errori di validazione CV + " + idCvDatiPersonali + " per clic lavoro");
			warningMessages.add(errorMessage);
			throw new MyPortalException("Errore Clic Lavoro", true, warningMessages);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.eng.myportal.entity.home.AbstractUpdatableHome#persistDTO(it.eng.myportal .dtos.IHasPrimaryKey,
	 * java.lang.Integer)
	 */
	@Override
	public CvDatiPersonaliDTO persistDTO(CvDatiPersonaliDTO data, Integer idPrincipalIns) {
		Integer idPrincipal = data.getIdPfPrincipal();
		String _filename = data.getFilename();
		String _fileNameTmp = data.getFileNameTmp();
		boolean isAllegatoPresent = StringUtils.isNotBlank(_filename) && StringUtils.isNotBlank(_fileNameTmp);
		CvDatiPersonaliDTO datap = super.persistDTO(data, idPrincipalIns);

		// gestione allegato
		if (isAllegatoPresent) {
			// esiste un allegato - lo salvo con fk verso il cv
			CvAllegatoDTO cva = new CvAllegatoDTO();
			cva.setIdCvDatiPersonali(datap.getId());
			cva.setFilename(_filename);
			byte[] contenuto;
			try {
				contenuto = Utils.fileToByte(ConstantsSingleton.TMP_DIR + File.separator + _fileNameTmp);
			} catch (FileNotFoundException e) {
				throw new MyPortalException("curriculum.allegato.error");
			} catch (IOException e) {
				throw new MyPortalException("curriculum.allegato.error");
			}
			cva.setContenuto(contenuto);
			cvAllegatoHome.persistDTO(cva, idPrincipal);
		}
		return datap;
	}

	public CvDatiPersonaliDTO newPersistDTO(CvDatiPersonaliDTO data, Integer idPrincipalIns, byte[] allegato) {
		Integer idPrincipal = data.getIdPfPrincipal();
		String _filename = data.getFilename();
		CvDatiPersonaliDTO datap = super.persistDTO(data, idPrincipalIns);
		// gestione allegato
		if (allegato != null) {
			// esiste un allegato - lo salvo con fk verso il cv
			CvAllegatoDTO cva = new CvAllegatoDTO();
			cva.setIdCvDatiPersonali(datap.getId());
			cva.setFilename(_filename);
			cva.setContenuto(allegato);
			cvAllegatoHome.persistDTO(cva, idPrincipal);
		}
		return datap;
	}

	/**
	 * Fa una query per selezionare i curriculum che corrispondono a dei parametri di ricerca. NB: Deve essere usata da
	 * utenti di tipo AZIENDA.
	 *
	 * @param filter
	 *            I parametri di ricerca.
	 * @return Una pagina del risultato.
	 */
	public List<RicercaCVaziendaDTO> ricercaCVazienda(CvFilterDTO filter, Integer idAziendaInfo,
			Integer idPfPrincipal) {

		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RicercaCVaziendaDTO> query = cb.createQuery(RicercaCVaziendaDTO.class);

		// Costruisco la clausola FROM della query, facendo i join delle tabelle necessarie.
		Root<CvDatiPersonali> cvDatiPersonali = query.from(CvDatiPersonali.class);
		Join<CvDatiPersonali, DeComune> deComuneDomicilio = cvDatiPersonali.join(CvDatiPersonali_.deComuneDomicilio);
		Join<DeComune, DeProvincia> deProvinciaDomicilio = deComuneDomicilio.join(DeComune_.deProvincia);
		Join<CvDatiPersonali, AcCandidatura> acCandidatura = cvDatiPersonali.join(CvDatiPersonali_.acCandidaturas,
				JoinType.LEFT);
		Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = acCandidatura.join(AcCandidatura_.vaDatiVacancy,
				JoinType.LEFT);
		Join<AcCandidatura, MsgContatto> msgContatto = acCandidatura.join(AcCandidatura_.msgContattos, JoinType.LEFT);
		Join<MsgContatto, MsgMessaggio> msgMessaggio = msgContatto.join(MsgContatto_.msgMessaggio, JoinType.LEFT);
		Join<CvDatiPersonali, MsgContatto> msgContattoAz = cvDatiPersonali.join(CvDatiPersonali_.msgContattos,
				JoinType.LEFT);
		Join<MsgContatto, MsgMessaggio> msgMessaggioAz = msgContattoAz.join(MsgContatto_.msgMessaggio, JoinType.LEFT);
		Join<MsgMessaggio, PfPrincipal> pfPrincipalMsgAz = msgMessaggioAz.join(MsgMessaggio_.pfPrincipalFrom,
				JoinType.LEFT);
		Join<MsgMessaggio, PfPrincipal> pfPrincipalTo = msgMessaggio.join(MsgMessaggio_.pfPrincipalTo, JoinType.LEFT);

		// Creo la clausola WHERE della query.
		List<Predicate> whereConditions = createWhereConditionsAzienda(filter, idAziendaInfo, idPfPrincipal,
				cvDatiPersonali, acCandidatura, msgContatto, msgMessaggio, pfPrincipalTo);

		// Aggiungo un'ulteriore predicato alla clausola WHERE:
		// se esiste più di un messaggio di contatto, prendo l'ultimo.
		// (Uso una subquery per ottenere questo risultato).
		Subquery<Date> subquery = creaSubQuery(query, null, idAziendaInfo);
		Predicate predDataMax = cb.or(cb.equal(acCandidatura.get(AcCandidatura_.dtmIns), subquery),
				cb.isNull(acCandidatura.get(AcCandidatura_.dtmIns)));
		whereConditions.add(predDataMax);

		// Creo la clausola SELECT della query, specificando i campi che mi servono.
		query.select(cb.construct(RicercaCVaziendaDTO.class, cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali),
				cvDatiPersonali.get(CvDatiPersonali_.descrizione),
				cb.concat(deComuneDomicilio.get(DeComune_.denominazione),
						cb.concat(" (", cb.concat(deProvinciaDomicilio.get(DeProvincia_.targa), ")"))),
				cvDatiPersonali.get(CvDatiPersonali_.dtmMod), vaDatiVacancy.get(VaDatiVacancy_.idVaDatiVacancy),
				vaDatiVacancy.get(VaDatiVacancy_.attivitaPrincipale), msgMessaggio.get(MsgMessaggio_.idMsgMessaggio),
				cb.selectCase()
						.when(cb.equal(pfPrincipalMsgAz.get(PfPrincipal_.idPfPrincipal), idPfPrincipal),
								msgMessaggioAz.get(MsgMessaggio_.dtmIns))
						.otherwise(cb.nullLiteral(Date.class)),
				cb.selectCase()
						.when(cb.equal(pfPrincipalMsgAz.get(PfPrincipal_.idPfPrincipal), idPfPrincipal),
								msgMessaggioAz.get(MsgMessaggio_.idMsgMessaggio))
						.otherwise(cb.nullLiteral(Integer.class))))
				.distinct(true);

		// Inserisco la clausola WHERE nella query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		// Aggiungo una clausola ORDER BY per ordinare i risultati per data di aggiornamento.
		query.orderBy(cb.desc(cvDatiPersonali.get(CvDatiPersonali_.dtmMod)),
				cb.asc(cvDatiPersonali.get(CvDatiPersonali_.descrizione)));

		// Paginazione : seleziono solo un certo sottoinsieme di risultati.
		TypedQuery<RicercaCVaziendaDTO> tquery = entityManager.createQuery(query);

		if (filter.getStartResultsFrom() > 0) {
			tquery.setFirstResult(filter.getStartResultsFrom());
		}

		if (filter.getMaxResults() > 0) {
			tquery.setMaxResults(filter.getMaxResults());
		}

		// Costruisco ed eseguo la query.
		List<RicercaCVaziendaDTO> data = tquery.getResultList();

		// FUNZIONE COMMENTATA: Interferisce con la paginazione.
		// TODO: Da vedere in futuro esattamente come fare.
		// data = rimuoviDuplicati(data);

		// Restituisco i dati.
		return data;

	}

	public List<CvDatiPersonali> ricercaCVaziendaLazyModelNewVersion(CurriculumSearchParams filter) {
		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvDatiPersonali> query = cb.createQuery(CvDatiPersonali.class);
		Root<CvDatiPersonali> cvDatiPersonali = query.from(CvDatiPersonali.class);
		// Creo la clausola WHERE della query.
		List<Predicate> whereConditions = createWhereConditionsAziendaLazyNewVersion(filter, cvDatiPersonali, query);
		if (filter.getProfessione() != null) {
			Subquery<Integer> subquery = creaSubQueryNewVersion(query, null, filter.getProfessione());
			Predicate predIdMax = cb.in(cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali)).value(subquery);
			whereConditions.add(predIdMax);
		}
		// Inserisco la clausola WHERE nella query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		// Aggiungo una clausola ORDER BY per ordinare i risultati per data di
		// aggiornamento.
		query.orderBy(cb.desc(cvDatiPersonali.get(CvDatiPersonali_.dtmMod)),
				cb.asc(cvDatiPersonali.get(CvDatiPersonali_.descrizione)));

		// Paginazione : seleziono solo un certo sottoinsieme di risultati.
		TypedQuery<CvDatiPersonali> tquery = entityManager.createQuery(query);

		if (filter.getStartFrom() > 0) {
			tquery.setFirstResult(filter.getStartFrom());
		}

		if (filter.getChunkSize() > 0) {
			tquery.setMaxResults(filter.getChunkSize());
		}

		// Costruisco ed eseguo la query.
		List<CvDatiPersonali> data = tquery.getResultList();

		// lazy load su chunk
		for (CvDatiPersonali cvDatiPersonali2 : data) {
			setL = cvDatiPersonali2.getCvLinguas();
			if (setL.size() > 0) {
				for (CvLingua cvLin : setL) {
					cvLin.getDeLingua().getDenominazione();
				}
			}
			Set<CvIstruzione> setIstr = cvDatiPersonali2.getCvIstruziones();
			if (setIstr.size() > 0) {
				for (CvIstruzione cvLin : setIstr) {
					cvLin.getDeTitolo().getDescrizione();
				}
			}
		}

		return data;

	}

	public List<CvDatiPersonali> ricercaCVaziendaLazyModel(CurriculumSearchParams filter,
			AziendaSessionDTO aziendaSessionDTO) {
		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvDatiPersonali> query = cb.createQuery(CvDatiPersonali.class);

		// Costruisco la clausola FROM della query, facendo i join delle tabelle necessarie.
		Root<CvDatiPersonali> cvDatiPersonali = query.from(CvDatiPersonali.class);
		// Join<CvDatiPersonali, DeComune> deComuneDomicilio = cvDatiPersonali.join(CvDatiPersonali_.deComuneDomicilio);
		// Join<DeComune, DeProvincia> deProvinciaDomicilio = deComuneDomicilio.join(DeComune_.deProvincia);
		Join<CvDatiPersonali, AcCandidatura> acCandidatura = cvDatiPersonali.join(CvDatiPersonali_.acCandidaturas,
				JoinType.LEFT);
		// TRoppo pesanti, stackoverflow
		// Fetch<CvDatiPersonali, CvLingua> cvLingue = cvDatiPersonali.fetch(CvDatiPersonali_.cvLinguas,
		// JoinType.LEFT);
		// Fetch<CvDatiPersonali, PfPrincipal> pfPrincCandidato= cvDatiPersonali.fetch(CvDatiPersonali_.pfPrincipal,
		// JoinType.INNER);
		Join<AcCandidatura, PfPrincipal> acCandidaturapfPrincipal = acCandidatura.join(AcCandidatura_.pfPrincipal,
				JoinType.LEFT);
		// Join<AcCandidatura, VaDatiVacancy> vaDatiVacancy = acCandidatura.join(AcCandidatura_.vaDatiVacancy,
		// JoinType.LEFT);
		Join<AcCandidatura, MsgContatto> msgContatto = acCandidatura.join(AcCandidatura_.msgContattos, JoinType.LEFT);
		Join<MsgContatto, MsgMessaggio> msgMessaggio = msgContatto.join(MsgContatto_.msgMessaggio, JoinType.LEFT);
		Join<CvDatiPersonali, MsgContatto> msgContattoAz = cvDatiPersonali.join(CvDatiPersonali_.msgContattos,
				JoinType.LEFT);
		Join<MsgContatto, MsgMessaggio> msgMessaggioAz = msgContattoAz.join(MsgContatto_.msgMessaggio, JoinType.LEFT);
		Join<MsgMessaggio, PfPrincipal> pfPrincipalMsgAz = msgMessaggioAz.join(MsgMessaggio_.pfPrincipalFrom,
				JoinType.LEFT);
		Join<MsgMessaggio, PfPrincipal> pfPrincipalTo = msgMessaggio.join(MsgMessaggio_.pfPrincipalTo, JoinType.LEFT);

		// Creo la clausola WHERE della query.
		List<Predicate> whereConditions = createWhereConditionsAziendaLazy(filter, aziendaSessionDTO.getId(),
				cvDatiPersonali, acCandidatura, msgContatto, msgMessaggio, pfPrincipalTo, acCandidaturapfPrincipal);

		// Aggiungo un'ulteriore predicato alla clausola WHERE:
		// se esiste più di un messaggio di contatto, prendo l'ultimo.
		// (Uso una subquery per ottenere questo risultato).

		Subquery<Date> subquery = creaSubQuery(query, null, aziendaSessionDTO.getId());
		Predicate predDataMax = cb.or(cb.equal(acCandidatura.get(AcCandidatura_.dtmIns), subquery),
				cb.isNull(acCandidatura.get(AcCandidatura_.dtmIns)));
		whereConditions.add(predDataMax);

		// Creo la clausola SELECT della query, specificando i campi che mi servono.
		/*
		 * query.select(cb.construct(RicercaCVaziendaDTO.class, cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali),
		 * cvDatiPersonali.get(CvDatiPersonali_.descrizione), cb.concat(deComuneDomicilio.get(DeComune_.denominazione),
		 * cb.concat(" (", cb.concat(deProvinciaDomicilio.get(DeProvincia_.targa), ")"))),
		 * cvDatiPersonali.get(CvDatiPersonali_.dtmMod), vaDatiVacancy.get(VaDatiVacancy_.idVaDatiVacancy),
		 * vaDatiVacancy.get(VaDatiVacancy_.attivitaPrincipale), msgMessaggio.get(MsgMessaggio_.idMsgMessaggio),
		 * cb.selectCase() .when(cb.equal(pfPrincipalMsgAz.get(PfPrincipal_.idPfPrincipal), idPfPrincipalCandidato),
		 * msgMessaggioAz.get(MsgMessaggio_.dtmIns)) .otherwise(cb.nullLiteral(Date.class)), cb.selectCase()
		 * .when(cb.equal(pfPrincipalMsgAz.get(PfPrincipal_.idPfPrincipal), idPfPrincipalCandidato),
		 * msgMessaggioAz.get(MsgMessaggio_.idMsgMessaggio)) .otherwise(cb.nullLiteral(Integer.class))))
		 * .distinct(true);
		 */

		// Inserisco la clausola WHERE nella query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		// Aggiungo una clausola ORDER BY per ordinare i risultati per data di aggiornamento.
		query.orderBy(cb.desc(cvDatiPersonali.get(CvDatiPersonali_.dtmMod)),
				cb.asc(cvDatiPersonali.get(CvDatiPersonali_.descrizione)));

		// Paginazione : seleziono solo un certo sottoinsieme di risultati.
		TypedQuery<CvDatiPersonali> tquery = entityManager.createQuery(query);

		if (filter.getStartFrom() > 0) {
			tquery.setFirstResult(filter.getStartFrom());
		}

		if (filter.getChunkSize() > 0) {
			tquery.setMaxResults(filter.getChunkSize());
		}

		// Costruisco ed eseguo la query.
		List<CvDatiPersonali> data = tquery.getResultList();

		// lazy load su chunk
		for (CvDatiPersonali cvDatiPersonali2 : data) {
			setL = cvDatiPersonali2.getCvLinguas();
			if (setL.size() > 0) {
				for (CvLingua cvLin : setL) {
					cvLin.getDeLingua().getDenominazione();
				}
			}
			Set<CvIstruzione> setIstr = cvDatiPersonali2.getCvIstruziones();
			if (setIstr.size() > 0) {
				for (CvIstruzione cvLin : setIstr) {
					cvLin.getDeTitolo().getDescrizione();
				}
			}
		}

		return data;
	}

	public Long ricercaCVaziendaLazyModelCount(CurriculumSearchParams curriculumSearchParams,
			AziendaSessionDTO aziendaSessionDTO) {
		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		// Costruisco la clausola FROM della query, facendo i join delle tabelle necessarie.
		Root<CvDatiPersonali> cvDatiPersonali = query.from(CvDatiPersonali.class);
		Join<CvDatiPersonali, AcCandidatura> acCandidatura = cvDatiPersonali.join(CvDatiPersonali_.acCandidaturas,
				JoinType.LEFT);
		Join<AcCandidatura, PfPrincipal> acCandidaturapfPrincipal = acCandidatura.join(AcCandidatura_.pfPrincipal,
				JoinType.LEFT);
		Join<AcCandidatura, MsgContatto> msgContatto = acCandidatura.join(AcCandidatura_.msgContattos, JoinType.LEFT);
		Join<MsgContatto, MsgMessaggio> msgMessaggio = msgContatto.join(MsgContatto_.msgMessaggio, JoinType.LEFT);
		Join<MsgMessaggio, PfPrincipal> pfPrincipalTo = msgMessaggio.join(MsgMessaggio_.pfPrincipalTo, JoinType.LEFT);

		// Creo la clausola SELECT della query, specificando i campi che mi servono.
		query.select(cb.countDistinct(cvDatiPersonali));

		// Creo la clausola WHERE della query.
		List<Predicate> whereConditions = createWhereConditionsAziendaLazy(curriculumSearchParams,
				aziendaSessionDTO.getId(), cvDatiPersonali, acCandidatura, msgContatto, msgMessaggio, pfPrincipalTo,
				acCandidaturapfPrincipal);

		// Aggiungo un'ulteriore predicato alla clausola WHERE:
		// se esiste più di un messaggio di contatto, prendo l'ultimo.
		// (Uso una subquery per ottenere questo risultato).
		Subquery<Date> subquery = creaSubQuery(null, query, aziendaSessionDTO.getId());
		Predicate predDataMax = cb.or(cb.equal(acCandidatura.get(AcCandidatura_.dtmIns), subquery),
				cb.isNull(acCandidatura.get(AcCandidatura_.dtmIns)));
		whereConditions.add(predDataMax);

		// Costruisco ed eseguo la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> tquery = entityManager.createQuery(query);
		Long result = tquery.getSingleResult();
		return result;
	}

	public Long ricercaCVaziendaLazyModelCountNewVersion(CurriculumSearchParams curriculumSearchParams) {
		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		// Costruisco la clausola FROM della query, facendo i join delle tabelle necessarie.
		Root<CvDatiPersonali> cvDatiPersonali = query.from(CvDatiPersonali.class);

		// Creo la clausola SELECT della query, specificando i campi che mi servono.
		query.select(cb.countDistinct(cvDatiPersonali));

		// Creo la clausola WHERE della query.
		List<Predicate> whereConditions = createWhereConditionsAziendaLazyCountNewVersion(curriculumSearchParams,
				cvDatiPersonali, query);
		if (curriculumSearchParams.getProfessione() != null) {
			Subquery<Integer> subquery = creaSubQueryNewVersion(query, null, curriculumSearchParams.getProfessione());
			Predicate predIdMax = cb.in(cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali)).value(subquery);
			whereConditions.add(predIdMax);
		}

		// Costruisco ed eseguo la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> tquery = entityManager.createQuery(query);
		Long result = tquery.getSingleResult();
		return result;
	}

	/**
	 * Crea la clausola WHERE per le query ricercaCV e findCountByFilter per gli utenti di tipo AZIENDA.
	 */
	private List<Predicate> createWhereConditionsAziendaLazy(CurriculumSearchParams filter, Integer idAziendaInfo,
			Root<CvDatiPersonali> cvDatiPersonali, Join<CvDatiPersonali, AcCandidatura> acCandidatura,
			Join<AcCandidatura, MsgContatto> msgContatto, Join<MsgContatto, MsgMessaggio> msgMessaggio,
			Join<MsgMessaggio, PfPrincipal> pfPrincipalTo, Join<AcCandidatura, PfPrincipal> pfPrincipalCand) {

		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// 1: elimino i curriculum scaduti e non ricercabili.
		Predicate p = cb.or(cb.greaterThanOrEqualTo(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza), new Date()),
				cb.isNull(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza)));
		whereConditions.add(p);

		whereConditions.add(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.ricercabile), true));

		// 2: prendo solo CV non inviati a candidature oppure inviati con messaggio
		p = cb.or(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.FALSE),
				cb.and(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.TRUE),
						cb.isNotNull(msgMessaggio.get(MsgMessaggio_.idMsgMessaggio)),
						cb.equal(pfPrincipalTo.get(PfPrincipal_.idPfPrincipal),
								pfPrincipalCand.get(PfPrincipal_.idPfPrincipal))));
		whereConditions.add(p);

		// 3: prendo solo CV non in attesa di eliminazione
		p = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagEliminato), Boolean.FALSE);
		whereConditions.add(p);

		Predicate pido = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagIdo), Boolean.TRUE);
		whereConditions.add(pido);

		// 4: filtro per titolo di studio
		if (filter.getDeTitolo() != null) {

			Join<CvDatiPersonali, CvIstruzione> j = cvDatiPersonali.join(CvDatiPersonali_.cvIstruziones);
			Join<CvIstruzione, DeTitolo> tit = j.join(CvIstruzione_.deTitolo);

			Predicate predicate = cb.equal(tit.get(DeTitolo_.codTitolo), filter.getDeTitolo().getCodTitolo());

			whereConditions.add(predicate);
		}

		// 5: filtro per lingua
		if (filter.getDeLingua() != null && !filter.getDeLingua().isEmpty()) {
			for (DeLingua deLinguaDTO : filter.getDeLingua()) {
				Join<CvDatiPersonali, CvLingua> j = cvDatiPersonali.join(CvDatiPersonali_.cvLinguas);
				Join<CvLingua, DeLingua> ling = j.join(CvLingua_.deLingua);
				p = cb.equal(ling.get(DeLingua_.codLingua), deLinguaDTO.getCodLingua());
				whereConditions.add(p);
			}
		}

		// 6: filtro per comune di domicilio

		if (filter.getComuneDomicilio() != null && filter.getComuneDomicilio().getCodCom() != null) {
			Join<CvDatiPersonali, DeComune> j = cvDatiPersonali.join(CvDatiPersonali_.deComuneDomicilio);
			p = cb.equal(j.get(DeComune_.codCom), filter.getComuneDomicilio().getCodCom());
			whereConditions.add(p);
		}

		// 8: filtro per lavoratori automuniti e/o motomuniti
		Predicate orMezzo = null;

		if (filter.getAutomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			orMezzo = cb.equal(j.get(CvAltreInfo_.flagAutomunito), true);
		}

		if (filter.getMotomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			if (orMezzo == null) {
				orMezzo = cb.equal(j.get(CvAltreInfo_.flagMotomunito), true);
			} else {
				orMezzo = cb.or(orMezzo, cb.equal(j.get(CvAltreInfo_.flagMotomunito), true));
			}
		}

		if (orMezzo != null)
			whereConditions.add(orMezzo);

		// 11 : filtro per gruppo professionale
		if (filter.getProfessione() != null) {
			Join<CvDatiPersonali, CvEsperienzeProf> esperienzeProfessionali = cvDatiPersonali
					.join(CvDatiPersonali_.cvEsperienzeProfs);
			Join<CvEsperienzeProf, DeBpMansione> deMansione = esperienzeProfessionali
					.join(CvEsperienzeProf_.deBpMansione);
			Predicate predGruppoProf = cb.like(deMansione.get(DeBpMansione_.codMansione),
					"%" + filter.getProfessione() + "%");
			whereConditions.add(predGruppoProf);
		}

		// Restituisco i predicati
		return whereConditions;
	}

	private List<Predicate> createWhereConditionsAziendaLazyNewVersion(CurriculumSearchParams filter,
			Root<CvDatiPersonali> cvDatiPersonali, CriteriaQuery<CvDatiPersonali> query) {

		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// 1: elimino i curriculum scaduti e non ricercabili.
		Predicate p = cb.or(cb.greaterThanOrEqualTo(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza), new Date()),
				cb.isNull(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza)));
		whereConditions.add(p);

		whereConditions.add(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.ricercabile), true));

		// 2: prendo solo CV non inviati a candidature oppure inviati con messaggio
		/*
		 * p = cb.or(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.FALSE),
		 * cb.and(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.TRUE),
		 * cb.isNotNull(msgMessaggio.get(MsgMessaggio_.idMsgMessaggio)),
		 * cb.equal(pfPrincipalTo.get(PfPrincipal_.idPfPrincipal), pfPrincipalCand.get(PfPrincipal_.idPfPrincipal))));
		 * whereConditions.add(p);
		 */
		p = cb.and(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.FALSE));
		whereConditions.add(p);

		p = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.opzTipoDecodifiche), new String("S"));
		whereConditions.add(p);

		// 3: prendo solo CV non in attesa di eliminazione
		p = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagEliminato), Boolean.FALSE);
		whereConditions.add(p);

		Predicate pido = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagIdo), Boolean.TRUE);
		whereConditions.add(pido);

		// 4: filtro per titolo di studio
		if (filter.getTitolo() != null) {

			// Join<CvDatiPersonali, CvIstruzione> j = cvDatiPersonali.join(CvDatiPersonali_.cvIstruziones);
			// Join<CvIstruzione, DeTitolo> tit = j.join(CvIstruzione_.deTitolo);

			// Predicate predicate = cb.equal(tit.get(DeTitolo_.codTitolo), filter.getDeTitolo().getCodTitolo());
			Join<CvDatiPersonali, CvIstruzione> istruzione = cvDatiPersonali.join(CvDatiPersonali_.cvIstruziones);
			Predicate predTitoli = cb.like(cb.upper(istruzione.get(CvIstruzione_.specifica)),
					"%" + filter.getTitolo().toUpperCase() + "%");

			whereConditions.add(predTitoli);

			// whereConditions.add(predicate);
		}

		// 5: filtro per lingua
		if (filter.getDeLingua() != null && !filter.getDeLingua().isEmpty()) {
			for (DeLingua deLinguaDTO : filter.getDeLingua()) {
				Join<CvDatiPersonali, CvLingua> j = cvDatiPersonali.join(CvDatiPersonali_.cvLinguas);
				Join<CvLingua, DeLingua> ling = j.join(CvLingua_.deLingua);
				p = cb.equal(ling.get(DeLingua_.codLingua), deLinguaDTO.getCodLingua());
				whereConditions.add(p);
			}
		}

		// 6: filtro per comune di domicilio

		if (filter.getComuneDomicilio() != null && filter.getComuneDomicilio().getCodCom() != null) {
			Join<CvDatiPersonali, DeComune> j = cvDatiPersonali.join(CvDatiPersonali_.deComuneDomicilio);
			p = cb.equal(j.get(DeComune_.codCom), filter.getComuneDomicilio().getCodCom());
			whereConditions.add(p);
		}

		// 8: filtro per lavoratori automuniti e/o motomuniti
		Predicate orMezzo = null;

		if (filter.getAutomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			orMezzo = cb.equal(j.get(CvAltreInfo_.flagAutomunito), true);
		}

		if (filter.getMotomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			if (orMezzo == null) {
				orMezzo = cb.equal(j.get(CvAltreInfo_.flagMotomunito), true);
			} else {
				orMezzo = cb.or(orMezzo, cb.equal(j.get(CvAltreInfo_.flagMotomunito), true));
			}
		}

		if (orMezzo != null)
			whereConditions.add(orMezzo);

		// 11 : filtro per gruppo professionale
		/*
		 * if (filter.getProfessione() != null) { Join<CvDatiPersonali, CvEsperienzeProf> esperienzeProfessionali =
		 * cvDatiPersonali .join(CvDatiPersonali_.cvEsperienzeProfs); Join<CvEsperienzeProf, DeBpMansione> deMansione =
		 * esperienzeProfessionali .join(CvEsperienzeProf_.deBpMansione); Predicate predGruppoProf =
		 * cb.like(cb.upper(esperienzeProfessionali.get(CvEsperienzeProf_.professione)), "%" +
		 * filter.getProfessione().toUpperCase() + "%");
		 * 
		 * whereConditions.add(predGruppoProf); }
		 */
		// todo inserire subquery
		/*
		 * commento per il momento if (filter.getProfessione() != null) { Subquery<String> subquery =
		 * creaSubQueryNewVersion(query, null,filter.getProfessione(),
		 * cvDatiPersonali.get(CvDatiPersonali_.descrizione));
		 * whereConditions.add(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali), subquery)); }
		 */
		// Restituisco i predicati
		return whereConditions;
	}

	private List<Predicate> createWhereConditionsAziendaLazyCountNewVersion(CurriculumSearchParams filter,
			Root<CvDatiPersonali> cvDatiPersonali, CriteriaQuery<Long> query) {

		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// 1: elimino i curriculum scaduti e non ricercabili.
		Predicate p = cb.or(cb.greaterThanOrEqualTo(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza), new Date()),
				cb.isNull(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza)));
		whereConditions.add(p);

		whereConditions.add(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.ricercabile), true));

		// 2: prendo solo CV non inviati a candidature oppure inviati con messaggio
		/*
		 * p = cb.or(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.FALSE),
		 * cb.and(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.TRUE),
		 * cb.isNotNull(msgMessaggio.get(MsgMessaggio_.idMsgMessaggio)),
		 * cb.equal(pfPrincipalTo.get(PfPrincipal_.idPfPrincipal), pfPrincipalCand.get(PfPrincipal_.idPfPrincipal))));
		 * whereConditions.add(p);
		 */

		p = cb.and(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.FALSE));
		whereConditions.add(p);

		// 3: prendo solo CV non in attesa di eliminazione
		p = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagEliminato), Boolean.FALSE);
		whereConditions.add(p);

		p = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.opzTipoDecodifiche), new String("S"));
		whereConditions.add(p);

		Predicate pido = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagIdo), Boolean.TRUE);
		whereConditions.add(pido);

		// 4: filtro per titolo di studio
		/*
		 * if (filter.getgetDeTitolo() != null) {
		 * 
		 * Join<CvDatiPersonali, CvIstruzione> j = cvDatiPersonali.join(CvDatiPersonali_.cvIstruziones);
		 * Join<CvIstruzione, DeTitolo> tit = j.join(CvIstruzione_.deTitolo);
		 * 
		 * Predicate predicate = cb.equal(tit.get(DeTitolo_.codTitolo), filter.getDeTitolo().getCodTitolo());
		 * 
		 * whereConditions.add(predicate); }
		 */

		// 4: filtro per titolo di studio
		if (filter.getTitolo() != null) {
			Join<CvDatiPersonali, CvIstruzione> istruzione = cvDatiPersonali.join(CvDatiPersonali_.cvIstruziones);
			Predicate predTitoli = cb.like(cb.upper(istruzione.get(CvIstruzione_.specifica)),
					"%" + filter.getTitolo().toUpperCase() + "%");

			whereConditions.add(predTitoli);
		}

		// 5: filtro per lingua
		if (filter.getDeLingua() != null && !filter.getDeLingua().isEmpty()) {
			for (DeLingua deLinguaDTO : filter.getDeLingua()) {
				Join<CvDatiPersonali, CvLingua> j = cvDatiPersonali.join(CvDatiPersonali_.cvLinguas);
				Join<CvLingua, DeLingua> ling = j.join(CvLingua_.deLingua);
				p = cb.equal(ling.get(DeLingua_.codLingua), deLinguaDTO.getCodLingua());
				whereConditions.add(p);
			}
		}

		// 6: filtro per comune di domicilio

		if (filter.getComuneDomicilio() != null && filter.getComuneDomicilio().getCodCom() != null) {
			Join<CvDatiPersonali, DeComune> j = cvDatiPersonali.join(CvDatiPersonali_.deComuneDomicilio);
			p = cb.equal(j.get(DeComune_.codCom), filter.getComuneDomicilio().getCodCom());
			whereConditions.add(p);
		}

		// 8: filtro per lavoratori automuniti e/o motomuniti
		Predicate orMezzo = null;

		if (filter.getAutomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			orMezzo = cb.equal(j.get(CvAltreInfo_.flagAutomunito), true);
		}

		if (filter.getMotomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			if (orMezzo == null) {
				orMezzo = cb.equal(j.get(CvAltreInfo_.flagMotomunito), true);
			} else {
				orMezzo = cb.or(orMezzo, cb.equal(j.get(CvAltreInfo_.flagMotomunito), true));
			}
		}

		if (orMezzo != null)
			whereConditions.add(orMezzo);

		// 11 : filtro per gruppo professionale
		/*
		 * if (filter.getProfessione() != null) { Join<CvDatiPersonali, CvEsperienzeProf> esperienzeProfessionali =
		 * cvDatiPersonali .join(CvDatiPersonali_.cvEsperienzeProfs); Join<CvEsperienzeProf, DeBpMansione> deMansione =
		 * esperienzeProfessionali .join(CvEsperienzeProf_.deBpMansione); Predicate predGruppoProf =
		 * cb.like(cb.upper(esperienzeProfessionali.get(CvEsperienzeProf_.professione)), "%" +
		 * filter.getProfessione().toUpperCase() + "%");
		 * 
		 * whereConditions.add(predGruppoProf); }
		 */
		// todo inserire subquery
		/*
		 * commentato if (filter.getProfessione() != null) { Subquery<String> subquery = creaSubQueryNewVersion(query,
		 * null,filter.getProfessione(), cvDatiPersonali.get(CvDatiPersonali_.descrizione));
		 * whereConditions.add(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali), subquery)); }
		 */
		// Restituisco i predicati
		return whereConditions;
	}

	/**
	 * Questa funzione effettua una query sul database per recuperare una lista di curriculum; può essere chiamata dagli
	 * utenti di tipo "provincia".
	 *
	 * @param filter
	 *            I parametri della ricerca da effettuare.
	 * @param provincia
	 *            Un DTO contenente i dati della provincia che effettua la ricerca.
	 * @param isCliclavoro
	 *            Mettere "true" se si vogliono visualizzare solo i curriculum di cliclavoro.
	 * @return Una lista di DTO contenenti i dati dei curriculum recuperati.
	 */
	public List<RicercaCVprovinciaDTO> ricercaCVprovincia(CvFilterDTO filter, DeProvinciaDTO provincia,
			boolean isCliclavoro) {

		// Costruisco una CriteriaQuery per la ricerca.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RicercaCVprovinciaDTO> query = cb.createQuery(RicercaCVprovinciaDTO.class);

		// Costruisco la clausola FROM della query, facendo join con le tabelle necessarie.
		Root<CvDatiPersonali> cvDatiPersonali = query.from(CvDatiPersonali.class);
		Join<CvDatiPersonali, DeProvenienza> deProvenienza = cvDatiPersonali
				.join(CvDatiPersonali_.provenienzaCurriculum);
		Join<CvDatiPersonali, DeComune> deComuneDomicilio = cvDatiPersonali.join(CvDatiPersonali_.deComuneDomicilio);
		Join<DeComune, DeProvincia> deProvinciaDomicilio = deComuneDomicilio.join(DeComune_.deProvincia);
		Join<CvDatiPersonali, PfPrincipal> pfPrincipal = cvDatiPersonali.join(CvDatiPersonali_.pfPrincipal);
		Join<PfPrincipal, UtenteInfo> utenteInfo = pfPrincipal.join(PfPrincipal_.utenteInfo, JoinType.LEFT);
		Join<UtenteInfo, DeProvincia> deProvincia = utenteInfo.join(UtenteInfo_.deProvincia, JoinType.LEFT);
		Join<CvDatiPersonali, PfPrincipal> pfPrincipalPalese = cvDatiPersonali.join(CvDatiPersonali_.pfPrincipalPalese,
				JoinType.LEFT);
		Join<PfPrincipal, UtenteInfo> utenteInfoPalese = pfPrincipalPalese.join(PfPrincipal_.utenteInfo, JoinType.LEFT);
		Join<UtenteInfo, DeProvincia> deProvinciaPalese = utenteInfoPalese.join(UtenteInfo_.deProvincia, JoinType.LEFT);

		// Specifico i campi che voglio recuperare per ogni curriculum.
		query.select(cb.construct(RicercaCVprovinciaDTO.class,
				cb.coalesce(pfPrincipalPalese.get(PfPrincipal_.nome), pfPrincipal.get(PfPrincipal_.nome)),
				cb.coalesce(pfPrincipalPalese.get(PfPrincipal_.cognome), pfPrincipal.get(PfPrincipal_.cognome)),
				pfPrincipal.get(PfPrincipal_.nome), pfPrincipal.get(PfPrincipal_.cognome),
				utenteInfoPalese.get(UtenteInfo_.codiceFiscale), utenteInfo.get(UtenteInfo_.codiceFiscale),
				cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali),
				cvDatiPersonali.get(CvDatiPersonali_.descrizione), cvDatiPersonali.get(CvDatiPersonali_.dtmIns),
				cb.concat(deComuneDomicilio.get(DeComune_.denominazione),
						cb.concat(" (", cb.concat(deProvinciaDomicilio.get(DeProvincia_.targa), ")"))),
				deProvenienza.get(DeProvenienza_.descrizione)));

		// Ora specifico le varie clausole "WHERE" della query.
		List<Predicate> whereConditions = this.createWhereConditionsProvincia(filter, provincia, isCliclavoro,
				cvDatiPersonali, deProvenienza, deComuneDomicilio, deProvinciaDomicilio, pfPrincipal, utenteInfo,
				deProvincia, pfPrincipalPalese, utenteInfoPalese, deProvinciaPalese);
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		// Aggiungo una ORDER BY per ordinare i risultati per cognome, poi per nome ed
		// infine per data di inserimento.
		query.orderBy(
				cb.asc(cb.coalesce(pfPrincipalPalese.get(PfPrincipal_.cognome), pfPrincipal.get(PfPrincipal_.cognome))),
				cb.asc(cb.coalesce(pfPrincipalPalese.get(PfPrincipal_.nome), pfPrincipal.get(PfPrincipal_.nome))),
				cb.desc(cvDatiPersonali.get(CvDatiPersonali_.dtmIns)));

		// Limito il numero di risultati per la paginazione.
		TypedQuery<RicercaCVprovinciaDTO> tquery = entityManager.createQuery(query);

		if (filter.getStartResultsFrom() > 0) {
			tquery.setFirstResult(filter.getStartResultsFrom());
		}

		if (filter.getMaxResults() > 0) {
			tquery.setMaxResults(filter.getMaxResults());
		}

		// Eseguo la query e restituisco il risultato
		List<RicercaCVprovinciaDTO> data = tquery.getResultList();
		return data;
	}

	/**
	 * recuperoi il cv importato dal SIL associato al codice comunicazione passato
	 *
	 * @param codComunicazioneSil
	 * @return
	 */
	public CvDatiPersonali findCvByCodComunicazioneSil(String codComunicazioneSil) {
		try {
			CvDatiPersonali curriculum = entityManager
					.createNamedQuery("findCvByCodComunicazioneSil", CvDatiPersonali.class)
					.setParameter("codComunicazioneSil", codComunicazioneSil).getSingleResult();
			if (curriculum != null)
				return curriculum;
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	public CvDatiPersonali findCvByUtPaleseFromSIL(Integer idPrincipal) {
		try {
			List<CvDatiPersonali> curriculums = entityManager
					.createNamedQuery("findCvByUtPaleseFromSIL", CvDatiPersonali.class)
					.setParameter("idPrincipal", idPrincipal).getResultList();
			if (curriculums != null && !curriculums.isEmpty()) {
				return curriculums.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public CvDatiPersonali findByCodComunicazione(String codiceCurriculum) {
		List<CvDatiPersonali> curriculums = entityManager
				.createNamedQuery("findCvByCodComunicazione", CvDatiPersonali.class)
				.setParameter("codComunicazione", codiceCurriculum).getResultList();
		if (curriculums != null && !curriculums.isEmpty()) {
			return curriculums.get(0);
		} else {
			return null;
		}
	}

	/**
	 * @param cvDatiPersonaliDTO
	 * @param idPfPrincipal
	 * @return
	 */
	public boolean isTestataVisibile(Integer idCvDatiPersonali, Integer idPfPrincipal) {
		return isTestataVisibile(findById(idCvDatiPersonali), pfPrincipalHome.findById(idPfPrincipal));
	}

	public boolean isTestataVisibile(CvDatiPersonali cvDatiPersonali, PfPrincipal pfPrincipalRequest) {
		boolean result = true;
		/*
		 * visibilita' della testata:
		 *
		 * se il CV proviene da cliclavoro allora il CV deve avere visibilita' Y e l'utente in questione deve aver avuto
		 * esito positivo dopo un messaggio di primo contatto con il proprietario del CV (utente palese del CV), fa
		 * eccezione l'utente cui si riferisce il CV che puo' sempre vederla.
		 *
		 * in tutti gli altri casi e' visibile.
		 *
		 *
		 * 21/05/2013 - Davide Se il cv non è intermediato, la visibilità dipende solo dal contatto e non dal flag
		 * visibilità
		 */
		/* se non son utente e il CV viene da cliclavoro controllo */
		List<AcCandidatura> listaCandidature = acCandidaturaHome.findAllByIdCvDatiPersonaliIdAziendaInfo(
				cvDatiPersonali.getIdCvDatiPersonali(), pfPrincipalRequest.getIdPfPrincipal());
		if (!pfPrincipalRequest.isUtente() && cvDatiPersonali.isFromClicLavoro()) {
			if (!cvDatiPersonali.isIntermediato()) {
				// Lista aziende a cui ho risposto si per una richiesta di primo contatto
				if (!cvDatiPersonali.getContactInfoViewableBy().contains(pfPrincipalRequest)
						&& (listaCandidature == null || listaCandidature.isEmpty())) {
					/*
					 * visibilita' = Y, ma non ho avuto un esito positivo ad un primo contatto, non visibile
					 */
					result = false;
				}
			} else {
				if (cvDatiPersonali.getVisibilita()) {
					if (!cvDatiPersonali.getContactInfoViewableBy().contains(pfPrincipalRequest)) {
						/*
						 * visibilita' = Y, ma non ho avuto un esito positivo ad un primo contatto, non visibile
						 */
						result = false;
					}
				} else {
					/* visibilita' = N, non visibile */
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * Conta il numero di curriculum che corrispondono ai parametri di ricerca. Usare per ottenere il numero totale di
	 * elementi per la paginazione. NB: Deve essere chiamato da utenti di tipo PROVINCIA.
	 *
	 * @param filter
	 *            I parametri della ricerca.
	 * @param provincia
	 *            Un DTO contenente i dati della provincia che effettua la ricerca.
	 * @param isCliclavoro
	 *            Settarlo a TRUE per vedere solo i curriculum provenienti da cliclavoro.
	 * @return
	 */
	public Long findCountByFilterProvincia(CvFilterDTO filter, DeProvinciaDTO provincia, boolean isCliclavoro) {

		// Costruisco una CriteriaQuery per la ricerca.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		// Costruisco la clausola FROM della query, facendo join con le tabelle necessarie.
		Root<CvDatiPersonali> cvDatiPersonali = query.from(CvDatiPersonali.class);
		Join<CvDatiPersonali, DeProvenienza> deProvenienza = cvDatiPersonali
				.join(CvDatiPersonali_.provenienzaCurriculum);
		Join<CvDatiPersonali, DeComune> deComuneDomicilio = cvDatiPersonali.join(CvDatiPersonali_.deComuneDomicilio);
		Join<DeComune, DeProvincia> deProvinciaDomicilio = deComuneDomicilio.join(DeComune_.deProvincia);
		Join<CvDatiPersonali, PfPrincipal> pfPrincipal = cvDatiPersonali.join(CvDatiPersonali_.pfPrincipal);
		Join<PfPrincipal, UtenteInfo> utenteInfo = pfPrincipal.join(PfPrincipal_.utenteInfo, JoinType.LEFT);
		Join<UtenteInfo, DeProvincia> deProvincia = utenteInfo.join(UtenteInfo_.deProvincia, JoinType.LEFT);
		Join<CvDatiPersonali, PfPrincipal> pfPrincipalPalese = cvDatiPersonali.join(CvDatiPersonali_.pfPrincipalPalese,
				JoinType.LEFT);
		Join<PfPrincipal, UtenteInfo> utenteInfoPalese = pfPrincipalPalese.join(PfPrincipal_.utenteInfo, JoinType.LEFT);
		Join<UtenteInfo, DeProvincia> deProvinciaPalese = utenteInfoPalese.join(UtenteInfo_.deProvincia, JoinType.LEFT);

		// Specifico che voglio fare una COUNT dei risultati.
		query.select(cb.countDistinct(cvDatiPersonali));

		// Costruisco ed aggiungo la clausola WHERE della query.
		List<Predicate> whereConditions = this.createWhereConditionsProvincia(filter, provincia, isCliclavoro,
				cvDatiPersonali, deProvenienza, deComuneDomicilio, deProvinciaDomicilio, pfPrincipal, utenteInfo,
				deProvincia, pfPrincipalPalese, utenteInfoPalese, deProvinciaPalese);
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		// Eseguo la query e restituisco il risultato
		TypedQuery<Long> tquery = entityManager.createQuery(query);
		return tquery.getSingleResult();
	}

	/**
	 * Questo metodo fa una query per contare il numero di curriculum che corrispondono ai parametri di ricerca. Da
	 * usare per ottenere il numero totale di elementi per la paginazione. NB: Deve essere chiamato da utenti di tipo
	 * AZIENDA.
	 *
	 * @param filter
	 *            I parametri di ricerca.
	 * @param idAziendaInfo
	 *            L'ID dell'azienda che fa la ricerca.
	 * @param idPfPrincipal
	 *            L'ID dell'azienda che fa la ricerca.
	 */
	public Long findCountByFilterAzienda(CvFilterDTO filter, Integer idAziendaInfo, Integer idPfPrincipal) {
		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		// Costruisco la clausola FROM della query, facendo i join delle tabelle necessarie.
		Root<CvDatiPersonali> cvDatiPersonali = query.from(CvDatiPersonali.class);
		Join<CvDatiPersonali, AcCandidatura> acCandidatura = cvDatiPersonali.join(CvDatiPersonali_.acCandidaturas,
				JoinType.LEFT);
		Join<AcCandidatura, MsgContatto> msgContatto = acCandidatura.join(AcCandidatura_.msgContattos, JoinType.LEFT);
		Join<MsgContatto, MsgMessaggio> msgMessaggio = msgContatto.join(MsgContatto_.msgMessaggio, JoinType.LEFT);
		Join<MsgMessaggio, PfPrincipal> pfPrincipalTo = msgMessaggio.join(MsgMessaggio_.pfPrincipalTo, JoinType.LEFT);

		// Creo la clausola SELECT della query, specificando i campi che mi servono.
		query.select(cb.countDistinct(cvDatiPersonali));

		// Creo la clausola WHERE della query.
		List<Predicate> whereConditions = createWhereConditionsAzienda(filter, idAziendaInfo, idPfPrincipal,
				cvDatiPersonali, acCandidatura, msgContatto, msgMessaggio, pfPrincipalTo);

		// Aggiungo un'ulteriore predicato alla clausola WHERE:
		// se esiste più di un messaggio di contatto, prendo l'ultimo.
		// (Uso una subquery per ottenere questo risultato).
		Subquery<Date> subquery = creaSubQuery(null, query, idAziendaInfo);
		Predicate predDataMax = cb.or(cb.equal(acCandidatura.get(AcCandidatura_.dtmIns), subquery),
				cb.isNull(acCandidatura.get(AcCandidatura_.dtmIns)));
		whereConditions.add(predDataMax);

		// Costruisco ed eseguo la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> tquery = entityManager.createQuery(query);
		Long result = tquery.getSingleResult();
		return result;
	}

	/**
	 * Costruisce la clausola WHERE delle query utilizzate nelle funzioni findCountByFilter e ricercaCvprovincia. Serve
	 * ad evitare duplicazione del codice. Bisogna passare a questa funzione la Root e tutti i vari Join che compongono
	 * la clausola FROM della query. NB: Funzione per le query di utenti di tipo PROVINCIA.
	 */
	private List<Predicate> createWhereConditionsProvincia(CvFilterDTO filter, DeProvinciaDTO provincia,
			boolean isCliclavoro, Root<CvDatiPersonali> cvDatiPersonali,
			Join<CvDatiPersonali, DeProvenienza> deProvenienza, Join<CvDatiPersonali, DeComune> deComuneDomicilio,
			Join<DeComune, DeProvincia> deProvinciaDomicilio, Join<CvDatiPersonali, PfPrincipal> pfPrincipal,
			Join<PfPrincipal, UtenteInfo> utenteInfo, Join<UtenteInfo, DeProvincia> deProvincia,
			Join<CvDatiPersonali, PfPrincipal> pfPrincipalPalese, Join<PfPrincipal, UtenteInfo> utenteInfoPalese,
			Join<UtenteInfo, DeProvincia> deProvinciaPalese) {

		// Inizializzo il query builder e la lista di predicati.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// 1: Controllare la provenienza dei curriculum.
		Predicate predCompMinistero = null;
		Predicate provinciaCompetentePred = null;

		if (isCliclavoro) {
			// Se "isClicLavoro" è a TRUE, allora seleziono solo quelli provenienti dal MINISTERO.
			provinciaCompetentePred = cb.equal(deProvenienza.get(DeProvenienza_.codProvenienza),
					ConstantsSingleton.DeProvenienza.COD_MINISTERO);
		} else {
			// Se "isClicLavoro" è a FALSE, seleziono i curriculum visibili dalla mia provincia.
			predCompMinistero = cb.and(
					cb.equal(deProvenienza.get(DeProvenienza_.codProvenienza),
							ConstantsSingleton.DeProvenienza.COD_MINISTERO),
					cb.equal(deProvinciaPalese.get(DeProvincia_.codProvincia), provincia.getId()));
			provinciaCompetentePred = cb.or(cb.equal(deProvincia.get(DeProvincia_.codProvincia), provincia.getId()),
					cb.equal(deProvenienza.get(DeProvenienza_.codProvenienza),
							ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC + provincia.getTarga()),
					predCompMinistero);
		}

		whereConditions.add(provinciaCompetentePred);

		// 2 : Scartare i curriculum non ricercabili, scaduti, in attesa di eliminazione o inviati.
		whereConditions.add(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.ricercabile), true)); // Non ricercabile.

		Predicate p = cb.or(
				// Scaduto.
				cb.greaterThanOrEqualTo(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza), new Date()),
				cb.isNull(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza)));
		whereConditions.add(p);

		p = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagEliminato), Boolean.FALSE); // Eliminato.
		whereConditions.add(p);

		p = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.FALSE); // Inviato.
		whereConditions.add(p);

		// 3 : Filtrare per titolo di studio.
		if (filter.getTitoli() != null && !filter.getTitoli().isEmpty()) {

			Join<CvDatiPersonali, CvIstruzione> j = cvDatiPersonali.join(CvDatiPersonali_.cvIstruziones);
			Join<CvIstruzione, DeTitolo> tit = j.join(CvIstruzione_.deTitolo);
			Iterator<DeTitoloDTO> it = filter.getTitoli().iterator();
			p = cb.equal(tit.get(DeTitolo_.codTitolo), it.next().getId());
			while (it.hasNext()) {
				Predicate predicate = cb.equal(tit.get(DeTitolo_.codTitolo), it.next().getId());
				p = cb.or(p, predicate);

			}
			whereConditions.add(p);
		}

		// 4 : Filtrare per lingua.

		// NON ATTIVO: FILTRO "OR"
		/*
		 * if (filter.getLingue() != null && !filter.getLingue().isEmpty()) { Join<CvDatiPersonali, CvLingua> j =
		 * cvDatiPersonali .join(CvDatiPersonali_.cvLinguas); Join<CvLingua, DeLingua> ling =
		 * j.join(CvLingua_.deLingua);
		 *
		 * Iterator<GenericDecodeDTO> it = filter.getLingue().iterator(); p = cb.equal(ling.get(DeLingua_.codLingua),
		 * it.next().getId());
		 *
		 * while(it.hasNext()) { Predicate predicate = cb.equal(ling.get(DeLingua_.codLingua), it.next().getId()); p =
		 * cb.or(p, predicate); } whereConditions.add(p); }
		 */

		// FILTRO AND
		if (filter.getLingue() != null && !filter.getLingue().isEmpty()) {
			for (GenericDecodeDTO deLinguaDTO : filter.getLingue()) {
				Join<CvDatiPersonali, CvLingua> j = cvDatiPersonali.join(CvDatiPersonali_.cvLinguas);
				Join<CvLingua, DeLingua> ling = j.join(CvLingua_.deLingua);
				p = cb.equal(ling.get(DeLingua_.codLingua), deLinguaDTO.getId());
				whereConditions.add(p);
			}
		}

		// 5 : Filtrare per comune di domicilio.
		if (filter.getComune() != null && filter.getComune().getId() != null) {
			Join<CvDatiPersonali, DeComune> j = cvDatiPersonali.join(CvDatiPersonali_.deComuneDomicilio);
			p = cb.equal(j.get(DeComune_.codCom), filter.getComune().getId());
			whereConditions.add(p);
		}

		// 6 : Filtrare per disponibilità a trasferte.
		if (filter.isTrasferte()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			p = cb.notEqual(j.get(CvAltreInfo_.deTrasferta),
					deTrasfertaHome.findById(ConstantsSingleton.DeTrasferta.NO_TRASFERTE));
			whereConditions.add(p);
		}

		// 7 : Filtrare per "automunito" e/o "motomunito".
		Predicate orMezzo = null;
		if (filter.isAutomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			orMezzo = cb.equal(j.get(CvAltreInfo_.flagAutomunito), true);
		}

		if (filter.isMotomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			if (orMezzo == null) {
				orMezzo = cb.equal(j.get(CvAltreInfo_.flagMotomunito), true);
			} else {
				orMezzo = cb.or(orMezzo, cb.equal(j.get(CvAltreInfo_.flagMotomunito), true));
			}

		}

		if (orMezzo != null)
			whereConditions.add(orMezzo);

		// 8 : Filtrare per nome e cognome.
		if (filter.getNome() != null || filter.getCognome() != null) {
			Predicate predNome = null;
			Predicate predNomePalese = null;
			Predicate predCognome = null;
			Predicate predCognomePalese = null;

			if (filter.getNome() != null) {
				predNome = cb.like(cb.upper(pfPrincipal.get(PfPrincipal_.nome)),
						"%" + filter.getNome().toUpperCase() + "%");
				predNomePalese = cb.like(cb.upper(pfPrincipalPalese.get(PfPrincipal_.nome)),
						"%" + filter.getNome().toUpperCase() + "%");

			}
			if (filter.getCognome() != null) {
				predCognome = cb.like(cb.upper(pfPrincipal.get(PfPrincipal_.cognome)),
						"%" + filter.getCognome().toUpperCase() + "%");
				predCognomePalese = cb.like(cb.upper(pfPrincipalPalese.get(PfPrincipal_.cognome)),
						"%" + filter.getCognome().toUpperCase() + "%");

			}
			Predicate andProprietario = predNome;
			if (predCognome != null && andProprietario == null)
				andProprietario = predCognome;
			if (predCognome != null && andProprietario != null)
				andProprietario = cb.and(andProprietario, predCognome);
			Predicate andPalese = predNomePalese;
			if (predCognomePalese != null && andPalese == null)
				andPalese = predCognomePalese;
			if (predCognomePalese != null && andPalese != null)
				andPalese = cb.and(andPalese, predCognomePalese);
			Predicate orNomeCognome = andPalese;
			if (andProprietario != null && orNomeCognome == null)
				orNomeCognome = andProprietario;
			if (andProprietario != null && orNomeCognome != null)
				orNomeCognome = cb.or(orNomeCognome, andProprietario);
			whereConditions.add(orNomeCognome);
		}

		// 9 : Filtrare per codice fiscale.
		if (filter.getCf() != null && !filter.getCf().isEmpty()) {
			Predicate predCF = cb.equal(cb.upper(utenteInfo.get(UtenteInfo_.codiceFiscale)),
					filter.getCf().toUpperCase());
			Predicate predCFpalese = cb.equal(cb.upper(utenteInfoPalese.get(UtenteInfo_.codiceFiscale)),
					filter.getCf().toUpperCase());

			predCF = cb.or(predCF, predCFpalese);
			whereConditions.add(predCF);

		}

		// 10 : Filtrare per gruppo professionale.
		if (filter.getGruppoProf() != null && filter.getGruppoProf().getId() != null) {
			Join<CvDatiPersonali, CvEsperienzeProf> esperienzeProfessionali = cvDatiPersonali
					.join(CvDatiPersonali_.cvEsperienzeProfs);
			Join<CvEsperienzeProf, DeMansione> deMansione = esperienzeProfessionali.join(CvEsperienzeProf_.deMansione);
			Predicate predGruppoProf = cb.equal(deMansione.get(DeMansione_.codMansione),
					filter.getGruppoProf().getId());
			whereConditions.add(predGruppoProf);
		}

		return whereConditions;
	}

	/**
	 * Crea la clausola WHERE per le query ricercaCV e findCountByFilter per gli utenti di tipo AZIENDA.
	 */
	private List<Predicate> createWhereConditionsAzienda(CvFilterDTO filter, Integer idAziendaInfo,
			Integer idPfPrincipal, Root<CvDatiPersonali> cvDatiPersonali,
			Join<CvDatiPersonali, AcCandidatura> acCandidatura, Join<AcCandidatura, MsgContatto> msgContatto,
			Join<MsgContatto, MsgMessaggio> msgMessaggio, Join<MsgMessaggio, PfPrincipal> pfPrincipalTo) {

		// Costruisco la query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// ROBA VECCHIA, COMMENTATA?
		// Predicate predMessaggioAz =
		// cb.or(cb.equal(msgMessaggioAz.get(MsgMessaggio_.pfPrincipalFrom),
		// idPfPrincipal),
		// cb.isNull(msgMessaggioAz.get(MsgMessaggio_.pfPrincipalFrom)));
		// whereConditions.add(predMessaggioAz);

		// 1: elimino i curriculum scaduti e non ricercabili.
		Predicate p = cb.or(cb.greaterThanOrEqualTo(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza), new Date()),
				cb.isNull(cvDatiPersonali.get(CvDatiPersonali_.dtScadenza)));
		whereConditions.add(p);

		whereConditions.add(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.ricercabile), true));

		// 2: prendo solo CV non inviati a candidature oppure inviati con messaggio
		p = cb.or(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.FALSE),
				cb.and(cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagInviato), Boolean.TRUE),
						cb.isNotNull(msgMessaggio.get(MsgMessaggio_.idMsgMessaggio)),
						cb.equal(pfPrincipalTo.get(PfPrincipal_.idPfPrincipal), idPfPrincipal)));
		whereConditions.add(p);

		// 3: prendo solo CV non in attesa di eliminazione
		p = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.flagEliminato), Boolean.FALSE);
		whereConditions.add(p);

		// 4: filtro per titolo di studio
		if (filter.getTitoli() != null && !filter.getTitoli().isEmpty()) {

			Join<CvDatiPersonali, CvIstruzione> j = cvDatiPersonali.join(CvDatiPersonali_.cvIstruziones);
			Join<CvIstruzione, DeTitolo> tit = j.join(CvIstruzione_.deTitolo);
			Iterator<DeTitoloDTO> it = filter.getTitoli().iterator();
			p = cb.equal(tit.get(DeTitolo_.codTitolo), it.next().getId());

			while (it.hasNext()) {
				Predicate predicate = cb.equal(tit.get(DeTitolo_.codTitolo), it.next().getId());
				p = cb.or(p, predicate);
			}
			whereConditions.add(p);
		}

		// 5: filtro per lingua
		if (filter.getLingue() != null && !filter.getLingue().isEmpty()) {
			for (GenericDecodeDTO deLinguaDTO : filter.getLingue()) {
				Join<CvDatiPersonali, CvLingua> j = cvDatiPersonali.join(CvDatiPersonali_.cvLinguas);
				Join<CvLingua, DeLingua> ling = j.join(CvLingua_.deLingua);
				p = cb.equal(ling.get(DeLingua_.codLingua), deLinguaDTO.getId());
				whereConditions.add(p);
			}
		}

		// 6: filtro per comune di domicilio
		if (filter.getComune() != null && filter.getComune().getId() != null) {
			Join<CvDatiPersonali, DeComune> j = cvDatiPersonali.join(CvDatiPersonali_.deComuneDomicilio);
			p = cb.equal(j.get(DeComune_.codCom), filter.getComune().getId());
			whereConditions.add(p);
		}

		// 7: filtro per disponibilità a trasferte
		if (filter.isTrasferte()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			p = cb.notEqual(j.get(CvAltreInfo_.deTrasferta),
					deTrasfertaHome.findById(ConstantsSingleton.DeTrasferta.NO_TRASFERTE));
			whereConditions.add(p);
		}

		// 8: filtro per lavoratori automuniti e/o motomuniti
		Predicate orMezzo = null;

		if (filter.isAutomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			orMezzo = cb.equal(j.get(CvAltreInfo_.flagAutomunito), true);
		}

		if (filter.isMotomunito()) {
			Join<CvDatiPersonali, CvAltreInfo> j = cvDatiPersonali.join(CvDatiPersonali_.cvAltreInfo);
			if (orMezzo == null) {
				orMezzo = cb.equal(j.get(CvAltreInfo_.flagMotomunito), true);
			} else {
				orMezzo = cb.or(orMezzo, cb.equal(j.get(CvAltreInfo_.flagMotomunito), true));
			}
		}

		if (orMezzo != null)
			whereConditions.add(orMezzo);

		// 9: filtro per nome e cognome
		Join<CvDatiPersonali, PfPrincipal> principal = cvDatiPersonali.join(CvDatiPersonali_.pfPrincipal);
		Join<CvDatiPersonali, PfPrincipal> principalPalese = cvDatiPersonali.join(CvDatiPersonali_.pfPrincipalPalese,
				JoinType.LEFT);
		if (filter.getNome() != null || filter.getCognome() != null) {
			Predicate predNome = null;
			Predicate predNomePalese = null;
			Predicate predCognome = null;
			Predicate predCognomePalese = null;

			if (filter.getNome() != null) {
				predNome = cb.like(cb.upper(principal.get(PfPrincipal_.nome)),
						"%" + filter.getNome().toUpperCase() + "%");
				predNomePalese = cb.like(cb.upper(principalPalese.get(PfPrincipal_.nome)),
						"%" + filter.getNome().toUpperCase() + "%");
			}

			if (filter.getCognome() != null) {
				predCognome = cb.like(cb.upper(principal.get(PfPrincipal_.cognome)),
						"%" + filter.getCognome().toUpperCase() + "%");
				predCognomePalese = cb.like(cb.upper(principalPalese.get(PfPrincipal_.cognome)),
						"%" + filter.getCognome().toUpperCase() + "%");
			}

			Predicate andProprietario = predNome;
			if (predCognome != null && andProprietario == null)
				andProprietario = predCognome;
			if (predCognome != null && andProprietario != null)
				andProprietario = cb.and(andProprietario, predCognome);

			Predicate andPalese = predNomePalese;
			if (predCognomePalese != null && andPalese == null)
				andPalese = predCognomePalese;
			if (predCognomePalese != null && andPalese != null)
				andPalese = cb.and(andPalese, predCognomePalese);

			Predicate orNomeCognome = andPalese;
			if (andProprietario != null && orNomeCognome == null)
				orNomeCognome = andProprietario;
			if (andProprietario != null && orNomeCognome != null)
				orNomeCognome = cb.or(orNomeCognome, andProprietario);

			whereConditions.add(orNomeCognome);
		}

		// 10 : filtro per codice fiscale
		if (filter.getCf() != null && !filter.getCf().isEmpty()) {
			Join<CvDatiPersonali, PfPrincipal> j = cvDatiPersonali.join(CvDatiPersonali_.pfPrincipal);
			Join<PfPrincipal, UtenteInfo> utenteInfo = j.join(PfPrincipal_.utenteInfo);
			Predicate predCF = cb.equal(cb.upper(utenteInfo.get(UtenteInfo_.codiceFiscale)),
					filter.getCf().toUpperCase());
			whereConditions.add(predCF);
		}

		// 11 : filtro per gruppo professionale
		if (filter.getGruppoProf() != null && filter.getGruppoProf().getId() != null) {
			Join<CvDatiPersonali, CvEsperienzeProf> esperienzeProfessionali = cvDatiPersonali
					.join(CvDatiPersonali_.cvEsperienzeProfs);
			Join<CvEsperienzeProf, DeMansione> deMansione = esperienzeProfessionali.join(CvEsperienzeProf_.deMansione);
			Predicate predGruppoProf = cb.equal(deMansione.get(DeMansione_.codMansione),
					filter.getGruppoProf().getId());
			whereConditions.add(predGruppoProf);
		}

		// Restituisco i predicati
		return whereConditions;
	}

	/**
	 * Questa funzione viene chiamata alla fine della ricerca dei curriculum da parte dell'utente AZIENDA. Dovrebbe fare
	 * in modo che nel risultato ci sia solo un curriculum per ciascun utente, inserendoci però eventuali riferimenti a
	 * messaggi (anche se i messaggi in realtà si riferiscono ad altri curriculum). TODO: Capire se/come/quando chiamare
	 * questa funzione.
	 */
	private List<RicercaCVaziendaDTO> rimuoviDuplicati(List<RicercaCVaziendaDTO> data) {
		// Creo una mappa, e dispongo i curriculum in questa mappa basandosi sull'ID del loro proprietario.
		Map<Integer, RicercaCVaziendaDTO> mappa = new HashMap<Integer, RicercaCVaziendaDTO>();
		for (RicercaCVaziendaDTO dto : data) {
			if (!mappa.containsKey(dto.getId())) {
				mappa.put(dto.getId(), dto);
			} else {
				// Se nella mappa c'è già un curriculum di quell'utente...
				RicercaCVaziendaDTO inMappa = mappa.get(dto.getId());

				// Se il nuovo curriculum ha un messaggio ed il vecchio no, aggiungo l'id del messaggio
				// al curriculum già presente nella mappa.
				if (dto.getIdMsgMessaggio() != null && inMappa.getIdMsgMessaggio() == null)
					inMappa.setIdMsgMessaggio(dto.getIdMsgMessaggio());

				// Se il nuovo curriculum ha un messaggio aziendale ed il vecchio no, aggiungo l'id e la data
				// del messaggio aziendale al curriculum già presente nella mappa.
				if (dto.getIdMsgMessaggioAz() != null && inMappa.getIdMsgMessaggioAz() == null)
					inMappa.setIdMsgMessaggioAz(dto.getIdMsgMessaggioAz());
				if (dto.getDataMessaggioAz() != null && inMappa.getDataMessaggioAz() == null)
					inMappa.setDataMessaggioAz(dto.getDataMessaggioAz());
			}
		}

		// Creo un nuovo array di curriculum, inserendoci SOLO un curriculum per ciascun utente.
		List<RicercaCVaziendaDTO> out = new ArrayList<RicercaCVaziendaDTO>();
		Set<Integer> keySet = mappa.keySet();
		for (Integer key : keySet) {
			out.add(mappa.get(key));
		}
		return out;
	}

	/**
	 * Controllo titolo e descrizione del CV per evitare che contengano dati personali.
	 */
	public String checkTitoloCv(CvDatiPersonaliDTO cv, UtenteCompletoDTO utente) {
		String result = "";

		if (cv.getTitolo() != null) {
			String titolo = cv.getTitolo().trim().toUpperCase();
			result = checkCampoCvPerInfoPersonali(titolo, "Il titolo", cv, utente);
			if (result != null && !result.isEmpty()) {
				return result;
			}
		}

		if (cv.getDescrizioneCv() != null) {
			String descrizione = cv.getDescrizioneCv().trim().toUpperCase();
			result = checkCampoCvPerInfoPersonali(descrizione, "La descrizione", cv, utente);
			if (result != null && !result.isEmpty()) {
				return result;
			}
		}

		// Se è andato tutto bene, restituisco stringa vuota.
		return "";
	}

	/**
	 * Controlla che all'interno di uno dei campi del CV non compaiano riferimenti ai dati personali del suo
	 * proprietario (nome, cognome, CF, data di nascita, telefono o email). Restituisco un messaggio di errore se
	 * qualcosa non va bene, o stringa vuota se va tutto bene.
	 */
	private String checkCampoCvPerInfoPersonali(String campo, String nomeCampo, CvDatiPersonaliDTO cv,
			UtenteCompletoDTO utente) {
		if (campo.contains(utente.getUtenteDTO().getPfPrincipalDTO().getNome().trim().toUpperCase())) {
			return nomeCampo + " del curriculum non può contenere il tuo nome";
		}

		if (campo.contains(utente.getUtenteDTO().getPfPrincipalDTO().getCognome().trim().toUpperCase())) {
			return nomeCampo + " del curriculum non può contenere il tuo cognome";
		}

		if (utente.getUtenteInfo().getCodiceFiscale() != null
				&& campo.contains(utente.getUtenteInfo().getCodiceFiscale().trim().toUpperCase())) {
			return nomeCampo + " del curriculum non può contenere il tuo codice fiscale";
		}

		if (cv.getDtNascita() != null) {
			DateFormat dtNascitaFormat = new SimpleDateFormat("dd/MM/yyyy");
			String dtNascitaString = dtNascitaFormat.format(cv.getDtNascita());
			if (campo.contains(dtNascitaString)) {
				return nomeCampo + " del curriculum non può contenere la tua data di nascita";
			}
		}

		if (cv.getTel1() != null && !cv.getTel1().trim().isEmpty()) {
			if (campo.contains(cv.getTel1().trim().toUpperCase())) {
				return nomeCampo + " del curriculum non può contenere il tuo numero di telefono";
			}
		}

		if (cv.getTel2() != null && !cv.getTel2().trim().isEmpty()) {
			if (campo.contains(cv.getTel2().trim().toUpperCase())) {
				return nomeCampo + " del curriculum non può contenere il tuo numero di telefono";
			}
		}

		if (cv.getEmail() != null && !cv.getEmail().trim().isEmpty()) {
			if (campo.contains(cv.getEmail().trim().toUpperCase())) {
				return nomeCampo + " del curriculum non può contenere il tuo indirizzo email";
			}
		}

		// Se è andato tutto bene, ritorno un messaggio vuoto.
		return "";
	}

	@Override
	public CvDatiPersonali merge(CvDatiPersonali entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);

		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public CvDatiPersonali persist(CvDatiPersonali entity, Integer actingUser) {
		// persistenza che aggira i DTO

		PfPrincipal usr = pfPrincipalHome.findById(actingUser);

		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();// forza insert, per avere ID
		return entity;
	}

	public int getCompletedPercentage(CvDatiPersonali cvDatiPersonali) {
		int ret = 0;
		CvDatiPersonali dbProgress = cvDatiPersonali;
		try {
			dbProgress = findById(cvDatiPersonali.getIdCvDatiPersonali());
		} catch (Exception e) {
			return ret;
		}
		ret += LIST_INCREMENT; // base for saving it

		// main lists
		ret += dbProgress.getCvFormaziones().isEmpty() ? 0 : LIST_INCREMENT;
		ret += dbProgress.getCvLinguas().isEmpty() ? 0 : LIST_INCREMENT;
		ret += dbProgress.getCvIstruziones().isEmpty() ? 0 : LIST_INCREMENT;
		ret += dbProgress.getCvEsperienzeProfs().isEmpty() ? 0 : LIST_INCREMENT;
		ret += dbProgress.getCvProfDesiderates().isEmpty() ? 0 : LIST_INCREMENT;
		ret += dbProgress.getCvAgevolaziones().isEmpty() ? 0 : LIST_INCREMENT;

		// minor lists
		ret += dbProgress.getCvPatentes().isEmpty() ? 0 : FIELD_INCREMENT;
		ret += dbProgress.getCvPatentinos().isEmpty() ? 0 : FIELD_INCREMENT;
		ret += dbProgress.getCvAlbos().isEmpty() ? 0 : FIELD_INCREMENT;

		if (!dbProgress.getCvCompetenzeTrasvs().isEmpty()) {
			CvCompetenzeTrasv tUnico = dbProgress.getCvCompetenzeTrasvs().iterator().next();
			ret += Utils.isStringEmpty(tUnico.getCapCompAltre()) ? 0 : FIELD_INCREMENT;
			ret += Utils.isStringEmpty(tUnico.getCapCompOrganizzative()) ? 0 : FIELD_INCREMENT;
			ret += Utils.isStringEmpty(tUnico.getCapCompRelInterpersonali()) ? 0 : FIELD_INCREMENT;
			ret += Utils.isStringEmpty(tUnico.getCapCompTecniche()) ? 0 : FIELD_INCREMENT;
		}

		if (dbProgress.getCvInformatica() != null) {
			ret += Utils.isStringEmpty(dbProgress.getCvInformatica().getCertificazione()) ? 0 : FIELD_INCREMENT;
			ret += Utils.isStringEmpty(dbProgress.getCvInformatica().getDescrizione()) ? 0 : FIELD_INCREMENT;
		}

		if (dbProgress.getCvAltreInfo() != null) {
			ret += Utils.isStringEmpty(dbProgress.getCvAltreInfo().getLimitazioni()) ? 0 : FIELD_INCREMENT;
			ret += Utils.isStringEmpty(dbProgress.getCvAltreInfo().getUlterioriInfo()) ? 0 : FIELD_INCREMENT;
			ret += dbProgress.getCvAltreInfo().getDeTrasferta() != null ? 0 : FIELD_INCREMENT;
			// ret += dbProgress.getCvAltreInfo().getFlagTrattamentoDati() ? FIELD_INCREMENT : 0;
		}

		// fields
		ret += Utils.isStringEmpty(dbProgress.getDescrizioneCv()) ? 0 : FIELD_INCREMENT;
		ret += Utils.isStringEmpty(dbProgress.getTitolo()) ? 0 : FIELD_INCREMENT;
		ret += Utils.isStringEmpty(dbProgress.getIndirizzoDomicilio()) ? 0 : FIELD_INCREMENT;
		ret += Utils.isStringEmpty(dbProgress.getEmail()) ? 0 : FIELD_INCREMENT;
		ret += Utils.isStringEmpty(dbProgress.getTel1()) ? 0 : FIELD_INCREMENT;
		ret += Utils.isStringEmpty(dbProgress.getTel2()) ? 0 : FIELD_INCREMENT;
		ret += Utils.isStringEmpty(dbProgress.getRiferimentiMessaggistica()) ? 0 : FIELD_INCREMENT;
		ret += Utils.isStringEmpty(dbProgress.getIndirizzoProfilo()) ? 0 : FIELD_INCREMENT;
		ret += Utils.isStringEmpty(dbProgress.getNoteAbilitazioni()) ? 0 : FIELD_INCREMENT;
		ret += dbProgress.getDtNascita() == null ? 0 : FIELD_INCREMENT;

		if (ret > 100)
			ret = 100;
		return ret;
	}

	
	public List<CvDatiPersonali> findAllCurriculaInScadenzaFlgIdo(Integer numGGCVScadenza) {
		// Data corrente
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int n_giorni = numGGCVScadenza.intValue();
		c.add(Calendar.DATE, -n_giorni);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date currentDatePlus5 = c.getTime();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvDatiPersonali> query = cb.createQuery(CvDatiPersonali.class);
		Root<CvDatiPersonali> root = query.from(CvDatiPersonali.class);

		// FROM
		Join<CvDatiPersonali, PfPrincipal> joinPfPrincipal = root.join(CvDatiPersonali_.pfPrincipal);
		Join<CvDatiPersonali, DeProvenienza> joinDeProvenienza = root.join(CvDatiPersonali_.provenienzaCurriculum);

		// WHERE
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		Expression<Calendar> dateTruncExpr = cb
				.function("date_trunc", Calendar.class, cb.literal("day"), root.get(CvDatiPersonali_.dtScadenza))
				.as(Calendar.class);

		whereConditions.add(cb.equal(dateTruncExpr, currentDatePlus5));
		whereConditions.add(cb.equal(root.get(CvDatiPersonali_.flagIdo), true));
		whereConditions.add(cb.equal(root.get(CvDatiPersonali_.flagInviato), false));
		whereConditions.add(cb.equal(root.get(CvDatiPersonali_.flagEliminato), false));
		whereConditions.add(cb.equal(joinDeProvenienza.get(DeProvenienza_.codProvenienza),
				ConstantsSingleton.DeProvenienza.COD_MYPORTAL));

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<CvDatiPersonali> typedQuery = entityManager.createQuery(query);
		List<CvDatiPersonali> results = typedQuery.getResultList();

		return results;
	}
	
}
