package it.eng.myportal.entity.home;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.dtos.AgenziaDTO;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.AziendaMiniDTO;
import it.eng.myportal.dtos.AziendaSessionDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeTipoAbilitatoDTO;
import it.eng.myportal.dtos.DeTipoDelegatoDTO;
import it.eng.myportal.dtos.DeTipoUtenteSareDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.RicercaAziendaDTO;
import it.eng.myportal.dtos.SedeDTO;
import it.eng.myportal.dtos.SoggettoDTO;
import it.eng.myportal.dtos.UtenteSAREDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.AziendaInfoRettifica;
import it.eng.myportal.entity.AziendaInfo_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.entity.SvAziendaInfo;
import it.eng.myportal.entity.SvAziendaInfo_;
import it.eng.myportal.entity.decodifiche.DeAutorizzazioneSare;
import it.eng.myportal.entity.decodifiche.DeAutorizzazioneSare_;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeComune_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.DeTipoAbilitato;
import it.eng.myportal.entity.decodifiche.DeTipoDelegato;
import it.eng.myportal.entity.decodifiche.DeTipoUtenteSare;
import it.eng.myportal.entity.decodifiche.DeTipoUtenteSare_;
import it.eng.myportal.entity.ejb.NotificationBuilder;
import it.eng.myportal.entity.ejb.UtenteAziendaSARE;
import it.eng.myportal.entity.home.decodifiche.DeAutorizzazioneSareHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoAbilitatoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoDelegatoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoUtenteSareHome;
import it.eng.myportal.enums.SoftwareSAREUtilizzato;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * Home object for domain model class Azienda.
 * 
 * @see it.eng.myportal.entity.Azienda
 * @author Rodi A.
 */
@Stateless
public class AziendaInfoHome extends AbstractUpdatableHome<AziendaInfo, AziendaInfoDTO> {

	private static final String NESSUNA_MODIFICA = "-1";

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeTipoAbilitatoHome deTipoAbilitatoHome;

	@EJB
	DeTipoDelegatoHome deTipoDelegatoHome;

	@EJB
	DeTipoUtenteSareHome deTipoUtenteSareHome;

	@EJB
	DeAutorizzazioneSareHome deAutorizzazioneSareHome;

	@EJB
	AziendaInfoRettificaHome aziendaInfoRettificaHome;

	@EJB
	SvAziendaInfoHome svAziendaInfoHome;

	@EJB
	UtenteAziendaSARE utenteAziendaSARE;

	@EJB
	ProvinciaHome provinciaHome;

	@EJB
	NotificationBuilder notificationBuilder;

	@EJB
	private DeRegioneHome deRegioneHome;

	@EJB
	private PtScrivaniaHome ptScrivaniaHome;

	@EJB
	private PtPortletHome ptPortletHome;

	public AziendaInfo findByUserName(String name) {
		PfPrincipal pf = pfPrincipalHome.findAbilitatoByUsername(name);
		if (pf == null || pf.getAziendaInfo() == null)
			return null;

		return pf.getAziendaInfo();
	}

	public AziendaInfo findById(Integer id) {
		AziendaInfo obj = findById(AziendaInfo.class, id);
		return obj;
	}

	public AziendaSessionDTO findSessionDTOByUserName(String name) {
		return toSessionDTO(findByUserName(name));
	}

	public AziendaSessionDTO findSessionDTOById(Integer id) {
		return toSessionDTO(findById(id));
	}

	public AziendaSessionDTO toSessionDTO(AziendaInfo aziendaInfo) {
		if (aziendaInfo == null)
			return null;
		AziendaSessionDTO dto = new AziendaSessionDTO();
		popolaDTO(dto, aziendaInfo);
		dto.setId(aziendaInfo.getIdPfPrincipal());
		dto.setCapSede(aziendaInfo.getCapSede());
		dto.setCognomeReferente(aziendaInfo.getCognomeRic());
		dto.setComune(deComuneHome.toDTO(aziendaInfo.getDeComuneSede()));
		dto.setEmailReferente(aziendaInfo.getEmailReferente());
		dto.setFaxSede(aziendaInfo.getFaxSede());
		dto.setIndirizzoSede(aziendaInfo.getIndirizzoSede());
		dto.setNomeReferente(aziendaInfo.getNomeRic());
		dto.setRagioneSociale(aziendaInfo.getRagioneSociale());
		dto.setStileSelezionato(aziendaInfo.getPfPrincipal().getStileSelezionato());
		dto.setTelefonoSede(aziendaInfo.getTelefonoSede());
		SvAziendaInfo svAzienda = aziendaInfo.getPfPrincipal().getSvAziendaInfo();
		if (svAzienda != null) {
			dto.setIdVetrina(svAzienda.getIdPfPrincipal());
		}
		dto.setProvincia(deProvinciaHome.toDTO(aziendaInfo.getDeProvincia()));

		// verifica richiesta accesso al SARE
		Boolean flgAbilitatoSare = aziendaInfo.getPfPrincipal().getFlagAbilitatoSare();
		dto.setAbilitatoSare(flgAbilitatoSare);

		DeAutorizzazioneSare deAutorizzazione = aziendaInfo.getDeAutorizzazioneSare();
		dto.setConfermatoSare(flgAbilitatoSare && (deAutorizzazione != null)
				&& "4".equals(deAutorizzazione.getCodAutorizzazioneSare()));

		return dto;
	}

	@Override
	public AziendaInfoDTO toDTO(AziendaInfo entity) {
		if (entity == null)
			return null;
		AziendaInfoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdPfPrincipal());
		dto.setLogo(entity.getLogo());
		if (entity.getDeComuneRichiedente() != null) {
			dto.setComune(deComuneHome.toDTO(entity.getDeComuneRichiedente()));
		}
		dto.setFlagAgenziaEstera(Boolean.TRUE.equals(entity.getFlagAgenziaEstera()));

		dto.setSoftwareSAREUtilizzato(SoftwareSAREUtilizzato.getInstance(entity.getSwCreazioneCo()));
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		if (entity.getDeProvincia() != null) {
			dto.setProvinciaRiferimento(deProvinciaHome.toDTO(entity.getDeProvincia()));
		}
		// richiedente
		dto.setNomeRic(entity.getNomeRic());
		dto.setCognomeRic(entity.getCognomeRic());
		if (entity.getDeComuneNascitaRic() != null) {
			dto.setComuneNascitaRic(deComuneHome.toDTO(entity.getDeComuneNascitaRic()));
		} else {
			dto.setComuneNascitaRic(new DeComuneDTO());
		}

		dto.setDataNascitaRic(entity.getDtDataNascitaRic());
		dto.setIndirizzoRic(entity.getIndirizzoRic());
		dto.setCapRic(entity.getCapRic());

		dto.setMittenteSare(entity.getMittenteSare());

		// Referente
		dto.setReferenteSare(entity.getReferenteSare());
		dto.setEmailReferente(entity.getEmailReferente());
		dto.setNomeReferente(entity.getNomeRic());
		dto.setCognomeReferente(entity.getCognomeRic());
		dto.setTelefonoReferente(entity.getTelefonoReferente());
		dto.setCellReferente(entity.getCellReferente());

		// Sede (operativa)
		SedeDTO sedeOperativa = new SedeDTO();
		sedeOperativa.setComune(deComuneHome.toDTO(entity.getDeComuneSede()));
		sedeOperativa.setCap(entity.getCapSede());
		sedeOperativa.setFax(entity.getFaxSede());
		sedeOperativa.setIndirizzo(entity.getIndirizzoSede());
		sedeOperativa.setTelefono(entity.getTelefonoSede());
		dto.setSedeOperativa(sedeOperativa);

		dto.setRagioneSociale(entity.getRagioneSociale());

		// SedeLegale

		SedeDTO sedeLegale = new SedeDTO();
		DeComune deComuneByCodComSedeLegale = entity.getDeComuneSedeLegale();
		if (deComuneByCodComSedeLegale != null) {
			sedeLegale.setComune(deComuneHome.toDTO(deComuneByCodComSedeLegale));
		}
		sedeLegale.setCap(entity.getCapSedeLegale());
		sedeLegale.setFax(entity.getFaxSedeLegale());
		sedeLegale.setIndirizzo(entity.getIndirizzoSedeLegale());
		sedeLegale.setTelefono(entity.getTelefonoSedeLegale());
		dto.setSedeLegale(sedeLegale);
		dto.setPartitaIva(entity.getPartitaIva());

		DeTipoAbilitato deTipoAbilitato = entity.getDeTipoAbilitato();
		if (deTipoAbilitato != null) {
			dto.setTipoAbilitato(deTipoAbilitatoHome.toDTO(deTipoAbilitato));
		}
		DeTipoDelegato deTipoDelegato = entity.getDeTipoDelegato();
		if (deTipoDelegato != null) {
			dto.setTipoDelegato(deTipoDelegatoHome.toDTO(deTipoDelegato));
		}
		DeComune deComuneByCodComIscrizione = entity.getDeComuneIscrizione();

		/**
		 * AGENZIA SOMMINISTRAZIONE
		 */
		AgenziaDTO agenzia = new AgenziaDTO();
		agenzia.setEstera(entity.getFlagAgenziaEstera());
		agenzia.setNumeroProvvedimento(entity.getIscrProvvedNumero());
		agenzia.setDataProvvedimento(entity.getDtIscrProvvedData());

		if (deComuneByCodComIscrizione != null) {
			agenzia.setComune(deComuneHome.toDTO(deComuneByCodComIscrizione));
		}
		agenzia.setNumeroIscrizione(entity.getIscrNumero());
		agenzia.setDataIscrizione(entity.getDtIscrData());

		dto.setAgenzia(agenzia);

		/**
		 * Soggetto Abilitato
		 */

		SoggettoDTO soggettoAbilitato = new SoggettoDTO();
		soggettoAbilitato.setOrdineIscrizione(entity.getIscrOrdine());
		if (deComuneByCodComIscrizione != null) {
			soggettoAbilitato.setLuogoIscrizione(deComuneHome.toDTO(entity.getDeComuneIscrizione()));
		}
		soggettoAbilitato.setDataIscrizione(entity.getDtIscrData());
		soggettoAbilitato.setNumeroIscrizione(entity.getIscrNumero());
		dto.setSoggettoAbilitato(soggettoAbilitato);

		PfPrincipal pfPrincipal = entity.getPfPrincipal();
		SvAziendaInfo svAzienda = pfPrincipal.getSvAziendaInfo();
		if (svAzienda != null) {
			dto.setIdVetrina(svAzienda.getIdPfPrincipal());
		}

		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setPartitaIva(entity.getPartitaIva());

		DeTipoUtenteSare deTipoUtenteSare = entity.getDeTipoUtenteSare();
		if (deTipoUtenteSare != null) {
			dto.setCodTipoUtenteSare(deTipoUtenteSare.getCodTipoUtenteSare());
		}

		DeAutorizzazioneSare deAutorizzazioneSare = entity.getDeAutorizzazioneSare();
		if (deAutorizzazioneSare != null) {
			dto.setCodAutorizzazioneSare(deAutorizzazioneSare.getCodAutorizzazioneSare());
			dto.setDescAutorizzazioneSare(deAutorizzazioneSare.getDescrizione());
		}
		dto.setDomanda(pfPrincipal.getDomanda());
		dto.setRisposta(pfPrincipal.getRisposta());
		dto.setEmail(pfPrincipal.getEmail());

		// verifica richiesta accesso al SARE
		Boolean flgAbilitatoSare = pfPrincipal.getFlagAbilitatoSare();
		dto.setAbilitatoSare(flgAbilitatoSare);

		DeAutorizzazioneSare deAutorizzazione = entity.getDeAutorizzazioneSare();
		dto.setConfermatoSare(flgAbilitatoSare && (deAutorizzazione != null)
				&& "4".equals(deAutorizzazione.getCodAutorizzazioneSare()));
		dto.setFlagValida(entity.getFlagValida());

		if (dto.getTipoDelegato() != null && dto.getTipoDelegato().getId() != null
				&& dto.getTipoDelegato().getId().equals(ConstantsSingleton.DeTipoDelegato.PROMOTORE_TIROCINI)) {
			dto.setFlagAbilitatoDichiarazioneNeet(true);
		} else {
			dto.setFlagAbilitatoDichiarazioneNeet(false);
		}

		return dto;
	}

	@Override
	public AziendaInfo fromDTO(AziendaInfoDTO dto) {
		if (dto == null)
			return null;
		AziendaInfo entity = super.fromDTO(dto);
		if (dto.getProvinciaRiferimento() != null && dto.getProvinciaRiferimento().getId() != null) {
			entity.setDeProvincia(deProvinciaHome.findById(dto.getProvinciaRiferimento().getId()));
		}

		if (dto.getComune() != null && dto.getComune().getId() != null)
			entity.setDeComuneRichiedente(deComuneHome.findById(dto.getComune().getId()));
		entity.setIdPfPrincipal(dto.getId());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getId()));
		entity.setLogo(dto.getLogo());
		entity.setPartitaIva(dto.getPartitaIva());

		entity.setFlagAgenziaEstera(dto.getFlagAgenziaEstera());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		if (dto.getSoftwareSAREUtilizzato() != null) {
			entity.setSwCreazioneCo(dto.getSoftwareSAREUtilizzato().toString());
		}

		// richiedente
		entity.setNomeRic(dto.getNomeRic());
		entity.setCognomeRic(dto.getCognomeRic());
		if (dto.getComuneNascitaRic() != null && dto.getComuneNascitaRic().getId() != null) {
			entity.setDeComuneNascitaRic(deComuneHome.findById(dto.getComuneNascitaRic().getId()));
		}
		// entity.setDeComuneByCodComNascitaRic(deComuneHome.findById(dto.getComuneNascitaRic().getId()));
		entity.setDtDataNascitaRic(dto.getDataNascitaRic());
		entity.setIndirizzoRic(dto.getIndirizzoRic());
		entity.setCapRic(dto.getCapRic());

		String codTipoUtenteSare = dto.getCodTipoUtenteSare();
		if (StringUtils.isNotBlank(codTipoUtenteSare)) {
			entity.setDeTipoUtenteSare(deTipoUtenteSareHome.findById(codTipoUtenteSare));
		}

		String codAutorizzazioneSare = dto.getCodAutorizzazioneSare();
		if (StringUtils.isNotBlank(codAutorizzazioneSare)) {
			entity.setDeAutorizzazioneSare(deAutorizzazioneSareHome.findById(codAutorizzazioneSare));
		}

		// Referente
		entity.setMittenteSare(dto.getMittenteSare());
		entity.setReferenteSare(dto.getReferenteSare());
		entity.setEmailReferente(dto.getEmailReferente());
		entity.setTelefonoReferente(dto.getTelefonoReferente());
		entity.setCellReferente(dto.getCellReferente());

		// Sede (operativa)
		DeComuneDTO comuneSede = dto.getSedeOperativa().getComune();
		if (comuneSede != null) {
			entity.setDeComuneSede(deComuneHome.findById(comuneSede.getId()));
		}
		entity.setCapSede(dto.getSedeOperativa().getCap());
		entity.setFaxSede(dto.getSedeOperativa().getFax());
		entity.setIndirizzoSede(dto.getSedeOperativa().getIndirizzo());
		entity.setRagioneSociale(dto.getRagioneSociale());
		entity.setTelefonoSede(dto.getSedeOperativa().getTelefono());

		// SedeLegale
		DeComuneDTO comuneSedeLegale = dto.getSedeLegale().getComune();
		if (comuneSedeLegale != null && comuneSedeLegale.getId() != null) {
			entity.setDeComuneSedeLegale(deComuneHome.findById(comuneSedeLegale.getId()));
		}
		entity.setCapSedeLegale(dto.getSedeLegale().getCap());
		entity.setIndirizzoSedeLegale(dto.getSedeLegale().getIndirizzo());
		entity.setTelefonoSedeLegale(dto.getSedeLegale().getTelefono());
		entity.setFaxSedeLegale(dto.getSedeLegale().getFax());

		entity.setEmailReferente(dto.getEmailReferente());
		Integer idVetrina = dto.getIdVetrina();
		if (idVetrina != null) {
			entity.getPfPrincipal().setSvAziendaInfo(svAziendaInfoHome.findById(idVetrina));
		}

		addTipoAbilitato(dto, entity);
		DeTipoDelegatoDTO deTipoDelegatoDTO = dto.getTipoDelegato();
		if (deTipoDelegatoDTO != null && deTipoDelegatoDTO.getId() != null) {
			entity.setDeTipoDelegato(deTipoDelegatoHome.findById(deTipoDelegatoDTO.getId()));
		}

		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setPartitaIva(dto.getPartitaIva());

		entity.setFlagValida(dto.getFlagValida());

		return entity;
	}

	/**
	 * @param dto
	 * @param entity
	 */
	protected void addTipoAbilitato(AziendaInfoDTO dto, AziendaInfo entity) {
		DeTipoAbilitatoDTO deTipoAbilitatoDTO = dto.getTipoAbilitato();
		if (deTipoAbilitatoDTO != null && deTipoAbilitatoDTO.getId() != null) {
			entity.setDeTipoAbilitato(deTipoAbilitatoHome.findById(deTipoAbilitatoDTO.getId()));

			/**
			 * AGENZIA SOMMINISTRAZIONE
			 */
			if (ConstantsSingleton.DeTipoAbilitato.AGENZIA_SOMMINISTRAZIONE.equals(deTipoAbilitatoDTO.getId())) {
				AgenziaDTO agenzia = dto.getAgenzia();
				addAgenziaInfo(entity, agenzia);
			}
			/**
			 * Soggetto Abilitato
			 */
			else if (ConstantsSingleton.DeTipoAbilitato.SOGGETTO_ABILITATO.equals(deTipoAbilitatoDTO.getId())) {
				SoggettoDTO soggettoAbilitato = dto.getSoggettoAbilitato();
				addSoggettoAbilitatoInfo(entity, soggettoAbilitato);

			}
		}
	}

	/**
	 * Aggiunge all'entity le informazioni sull'agenzia di somministrazione
	 * 
	 * @param entity
	 * @param agenzia
	 * @return
	 */
	public void addAgenziaInfo(AziendaInfo entity, AgenziaDTO agenzia) {
		entity.setFlagAgenziaEstera(agenzia.getEstera());
		entity.setIscrProvvedNumero(agenzia.getNumeroProvvedimento());
		entity.setDtIscrProvvedData(agenzia.getDataProvvedimento());

		if (agenzia.getComune() != null && agenzia.getComune().getId() != null) {
			entity.setDeComuneIscrizione(deComuneHome.findById(agenzia.getComune().getId()));
		}
		entity.setIscrNumero(agenzia.getNumeroIscrizione());
		entity.setDtIscrData(agenzia.getDataIscrizione());
	}

	/**
	 * Aggiunge all'entity le informazioni sul so
	 * 
	 * @param entity
	 * @param agenzia
	 * @return
	 */
	public void addSoggettoAbilitatoInfo(AziendaInfo entity, SoggettoDTO soggettoAbilitato) {
		entity.setIscrOrdine(soggettoAbilitato.getOrdineIscrizione());
		DeComuneDTO luogoIscrizione = soggettoAbilitato.getLuogoIscrizione();
		if (luogoIscrizione != null && luogoIscrizione.getId() != null) {
			entity.setDeComuneIscrizione(deComuneHome.findById(luogoIscrizione.getId()));
		}
		entity.setIscrNumero(soggettoAbilitato.getNumeroIscrizione());
		entity.setDtIscrData(soggettoAbilitato.getDataIscrizione());
	}

	public AziendaInfoDTO findDTOByCodiceFiscale(String cf) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AziendaInfo> query = qb.createQuery(AziendaInfo.class);
		Root<AziendaInfo> aziendaInfo = query.from(AziendaInfo.class);
		query.where(qb.equal(aziendaInfo.get(AziendaInfo_.codiceFiscale), cf));
		/* eseguo */
		TypedQuery<AziendaInfo> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		/* e prendo i risultati */
		List<AziendaInfo> instance = q.getResultList();
		if (instance.isEmpty())
			return null;
		return toDTO(instance.get(0));
	}

	public AziendaInfo findByCodiceFiscale(String cf) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AziendaInfo> query = qb.createQuery(AziendaInfo.class);
		Root<AziendaInfo> aziendaInfo = query.from(AziendaInfo.class);
		query.where(qb.equal(aziendaInfo.get(AziendaInfo_.codiceFiscale), cf));
		/* eseguo */
		TypedQuery<AziendaInfo> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		;
		/* e prendo i risultati */
		List<AziendaInfo> instance = q.getResultList();
		if (instance.isEmpty())
			return null;
		return instance.get(0);
	}

	public List<AziendaInfo> findListByCodiceFiscale(String cf) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AziendaInfo> query = qb.createQuery(AziendaInfo.class);
		Root<AziendaInfo> aziendaInfo = query.from(AziendaInfo.class);
		query.where(qb.equal(aziendaInfo.get(AziendaInfo_.codiceFiscale), cf));
		/* eseguo */
		TypedQuery<AziendaInfo> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		/* e prendo i risultati */
		List<AziendaInfo> instance = q.getResultList();

		return instance;
	}

	/**
	 * Query che ricerca tutte le aziende che corrispondono ai parametri passati in input - sono tutti opzionali.
	 * 
	 * @param userParam
	 *            - parametro di ricerca in username|codiceFiscale|ragioneSociale
	 * @param requestStatusParam
	 *            - parametro di ricerca nello status
	 * @param idProvincia
	 *            - provincia su cui l'azienda ha chiesto competenza SARE
	 * @param numPagina
	 *            - paginazione lato server - disabilitata
	 * @param dimPagina
	 *            - paginazione lato server - disabilitata
	 * @return
	 */
	public List<UtenteSAREDTO> cercaUtentiSARE(RicercaAziendaDTO params, int numPagina, int dimPagina) {
		List<UtenteSAREDTO> result = new ArrayList<UtenteSAREDTO>();

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = qb.createTupleQuery();
		Root<AziendaInfo> aziendaInfo = query.from(AziendaInfo.class);
		Join<AziendaInfo, DeProvincia> deProv = aziendaInfo.join(AziendaInfo_.deProvincia);
		Join<AziendaInfo, DeTipoUtenteSare> deTipoUtSare = aziendaInfo.join(AziendaInfo_.deTipoUtenteSare,
				JoinType.LEFT);
		Join<AziendaInfo, DeAutorizzazioneSare> deAutorizzazioneSare = aziendaInfo
				.join(AziendaInfo_.deAutorizzazioneSare, JoinType.LEFT);
		Join<AziendaInfo, PfPrincipal> pfPrincipal = aziendaInfo.join(AziendaInfo_.pfPrincipal);

		Path<Integer> idPfPrincipalAziendaPath = aziendaInfo.get(AziendaInfo_.idPfPrincipal);
		Path<Integer> idPfPrincipalPath = pfPrincipal.get(PfPrincipal_.idPfPrincipal);
		Path<String> codiceFiscalePath = aziendaInfo.get(AziendaInfo_.codiceFiscale);
		Path<String> ragioneSocialePath = aziendaInfo.get(AziendaInfo_.ragioneSociale);
		Path<String> usernamePath = pfPrincipal.get(PfPrincipal_.username);
		Path<String> emailRegistrazionePath = pfPrincipal.get(PfPrincipal_.email);
		Path<String> nomeRefPath = aziendaInfo.get(AziendaInfo_.referenteSare);
		Path<String> telefonoRefPath = aziendaInfo.get(AziendaInfo_.telefonoReferente);
		Path<String> emailRefPath = aziendaInfo.get(AziendaInfo_.emailReferente);

		Path<String> codAutorizzazioneSare = deAutorizzazioneSare.get(DeAutorizzazioneSare_.codAutorizzazioneSare);
		Path<String> autorizzazioneSareDescrizione = deAutorizzazioneSare.get(DeAutorizzazioneSare_.descrizione);
		Path<String> codTipoUtenteSare = deTipoUtSare.get(DeTipoUtenteSare_.codTipoUtenteSare);
		Path<String> descTipoUtenteSare = deTipoUtSare.get(DeTipoUtenteSare_.descrizione);

		Predicate userPredicate = null;
		Predicate requestStatusPredicate = null;

		List<Predicate> lPred = new ArrayList<Predicate>();
		Predicate autSareNotNull = qb.isNotNull(deAutorizzazioneSare.get(DeAutorizzazioneSare_.codAutorizzazioneSare));
		lPred.add(autSareNotNull);

		if (StringUtils.isNotBlank(params.getUtente())) {
			Predicate codiceFiscalePredicate = qb.equal(qb.upper(aziendaInfo.get(AziendaInfo_.codiceFiscale)),
					params.getUtente().toUpperCase());
			Predicate ragioneSocialePredicate = qb.like(qb.upper(aziendaInfo.get(AziendaInfo_.ragioneSociale)),
					"%" + params.getUtente().toUpperCase() + "%");
			Predicate usernamePredicate = qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.username)),
					params.getUtente().toUpperCase());

			// questo predicato va aggiunto solo se la stringa "userParam"
			// contiene un numero
			Predicate idAziendaInfoPredicate = null;
			try {
				BigDecimal userParamBigDecimal = new BigDecimal(params.getUtente());
				idAziendaInfoPredicate = qb.equal(aziendaInfo.get(AziendaInfo_.idPfPrincipal), userParamBigDecimal);
			} catch (NumberFormatException e) {
				// sto facendo la ricerca per nome, tutto ok
				// blblblbl
			}

			if (idAziendaInfoPredicate != null) {
				userPredicate = qb.or(codiceFiscalePredicate, ragioneSocialePredicate, usernamePredicate,
						idAziendaInfoPredicate);
			} else {
				userPredicate = qb.or(codiceFiscalePredicate, ragioneSocialePredicate, usernamePredicate);
			}

			lPred.add(userPredicate);
		}

		if (StringUtils.isNotBlank(params.getStatoRichiesta())) {
			requestStatusPredicate = qb.equal(deAutorizzazioneSare.get(DeAutorizzazioneSare_.codAutorizzazioneSare),
					params.getStatoRichiesta());
			lPred.add(requestStatusPredicate);
		}
		if (StringUtils.isNotBlank(params.getIdProvincia())) {
			requestStatusPredicate = qb.equal(deProv.get(DeProvincia_.codProvincia), params.getIdProvincia());
			lPred.add(requestStatusPredicate);
		} // se voglio solo le aziende con delle rettifiche, metti in join con
			// la tabella azienda_info_rettifica
		if (params.getConRettifica()) {
			Join<PfPrincipal, AziendaInfoRettifica> rettifica = pfPrincipal.join(PfPrincipal_.aziendaInfoRettifica);
		}

		query.multiselect(idPfPrincipalAziendaPath, idPfPrincipalPath, codiceFiscalePath, ragioneSocialePath,
				usernamePath, emailRegistrazionePath, nomeRefPath, telefonoRefPath, emailRefPath, codTipoUtenteSare,
				descTipoUtenteSare, codAutorizzazioneSare, autorizzazioneSareDescrizione);
		if (!lPred.isEmpty()) {
			query.where(qb.and(lPred.toArray(new Predicate[0])));
		}

		/* eseguo */
		TypedQuery<Tuple> q = entityManager.createQuery(query);
		// int firstResult = numPagina * dimPagina;
		// Limiti i risultati / pagino
		// q.setFirstResult(firstResult);
		// q.setMaxResults(dimPagina);
		// Paginazione fatta lato client
		/* e prendo i risultati */
		List<Tuple> queryResult = q.getResultList();

		for (Tuple tuple : queryResult) {
			UtenteSAREDTO utenteSAREDTO = new UtenteSAREDTO(tuple.get(idPfPrincipalAziendaPath),
					tuple.get(idPfPrincipalPath), tuple.get(codiceFiscalePath), tuple.get(ragioneSocialePath),
					tuple.get(usernamePath), tuple.get(emailRegistrazionePath), tuple.get(nomeRefPath),
					tuple.get(telefonoRefPath), tuple.get(emailRefPath), tuple.get(codAutorizzazioneSare),
					tuple.get(autorizzazioneSareDescrizione), tuple.get(codTipoUtenteSare),
					tuple.get(descTipoUtenteSare));
			result.add(utenteSAREDTO);
		}

		return result;
	}

	public String cercaUtentiSARECSV(RicercaAziendaDTO params, int numPagina, int dimPagina) {
		List<UtenteSAREDTO> list = cercaUtentiSARE(params, numPagina, dimPagina);

		String FIELD_DELIMITER = "\"";
		String FIELD_SEPARATOR = ";";
		String LINE_SEPARATOR = "\n";
		StringBuilder csvBuilder = new StringBuilder();

		/* riga dei titoli */
		csvBuilder.append(FIELD_DELIMITER + "Codice Fiscale" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Ragione sociale" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Telefono referente SARE" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "E-mail referente SARE" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Referente SARE" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Autorizzazione" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Tipo utente" + FIELD_DELIMITER + LINE_SEPARATOR);

		/* dati */
		for (UtenteSAREDTO utenteSAREDTO : list) {
			if (utenteSAREDTO.getCodiceFiscale() != null) {
				csvBuilder
						.append(FIELD_DELIMITER + utenteSAREDTO.getCodiceFiscale() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteSAREDTO.getRagioneSociale() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSAREDTO.getRagioneSociale().replace("\"", "\"\"")
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteSAREDTO.getTelefonoReferente() != null) {
				csvBuilder.append(
						FIELD_DELIMITER + utenteSAREDTO.getTelefonoReferente() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteSAREDTO.getEmailReferente() != null) {
				csvBuilder.append(
						FIELD_DELIMITER + utenteSAREDTO.getEmailReferente() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteSAREDTO.getNomeReferente() != null) {
				csvBuilder
						.append(FIELD_DELIMITER + utenteSAREDTO.getNomeReferente() + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteSAREDTO.getAutorizzazioneSAREDescrizione() != null) {
				csvBuilder.append(FIELD_DELIMITER + utenteSAREDTO.getAutorizzazioneSAREDescrizione() + FIELD_DELIMITER
						+ FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (utenteSAREDTO.getDescTipoUtenteSare() != null) {
				csvBuilder.append(
						FIELD_DELIMITER + utenteSAREDTO.getDescTipoUtenteSare() + FIELD_DELIMITER + LINE_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + LINE_SEPARATOR);
			}
		}

		String csv = csvBuilder.toString();
		return csv;
	}

	public AziendaInfo findByCFAndMailReferente(String cf, String emailReferente) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AziendaInfo> query = qb.createQuery(AziendaInfo.class);
		Root<AziendaInfo> aziendaInfo = query.from(AziendaInfo.class);
		query.where(qb.equal(aziendaInfo.get(AziendaInfo_.codiceFiscale), cf));
		query.where(qb.equal(aziendaInfo.get(AziendaInfo_.emailReferente), emailReferente));
		/* eseguo */
		TypedQuery<AziendaInfo> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		;
		/* e prendo i risultati */
		List<AziendaInfo> instance = q.getResultList();
		if (instance.isEmpty())
			return null;
		return instance.get(0);
	}
	
	public AziendaInfo findByCFAndMail(String cf, String emailRegistrazione) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AziendaInfo> query = qb.createQuery(AziendaInfo.class);
		Root<AziendaInfo> aziendaInfo = query.from(AziendaInfo.class);
		Join<AziendaInfo, PfPrincipal> pfPrincipal = aziendaInfo.join(AziendaInfo_.pfPrincipal, JoinType.INNER);
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(aziendaInfo.get(AziendaInfo_.codiceFiscale), cf));
		whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.email), emailRegistrazione));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		/* eseguo */
		TypedQuery<AziendaInfo> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		;
		/* e prendo i risultati */
		List<AziendaInfo> instance = q.getResultList();
		if (instance.isEmpty())
			return null;
		return instance.get(0);
	}

	@Override
	public AziendaInfoDTO persistDTO(AziendaInfoDTO data, Integer idPrincipalIns) {

		PfPrincipal principal = pfPrincipalHome.findById(idPrincipalIns);
		principal.setDomanda(data.getDomanda());
		principal.setRisposta(data.getRisposta());
		principal.setEmail(data.getEmail());

		pfPrincipalHome.merge(principal);

		AziendaInfoDTO ret = super.persistDTO(data, idPrincipalIns);
		return ret;
	}

	@Override
	public AziendaInfoDTO mergeDTO(AziendaInfoDTO data, Integer idPrincipalMod) {
		return mergeDTO(data, false, idPrincipalMod);
	}

	public AziendaInfoDTO mergeDTO(AziendaInfoDTO data, boolean deleteRettifica, Integer idPrincipalMod) {
		// modifico i dati dell'utente azienda
		AziendaInfo aziendaInfo = findById(data.getId());
		PfPrincipal pfPrincipal = aziendaInfo.getPfPrincipal();

		pfPrincipal.setDomanda(data.getDomanda());
		pfPrincipal.setRisposta(data.getRisposta());
		pfPrincipal.setEmail(data.getEmail());
		pfPrincipal.setNome(data.getNomeRic());
		pfPrincipal.setCognome(data.getCognomeRic());

		pfPrincipal = pfPrincipalHome.merge(pfPrincipal);
		AziendaInfoDTO temp = super.mergeDTO(data, idPrincipalMod);

		temp.setDomanda(pfPrincipal.getDomanda());
		temp.setRisposta(pfPrincipal.getRisposta());
		temp.setEmail(pfPrincipal.getEmail());

		if (deleteRettifica) {
			AziendaInfoRettifica rettifica = aziendaInfoRettificaHome.findById(data.getId());
			if (rettifica != null) {
				aziendaInfoRettificaHome.remove(rettifica);
			}
		}

		// se è la provincia ad effettuare la modifica dei dati allora invio una
		// notifica all'azienda
		if (pfPrincipalHome.isProvincia(idPrincipalMod)) {

			// riporto le modifiche sul SARE
			PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.toDTO(pfPrincipal);

			String esitoWs = utenteAziendaSARE.modificaUtenteSare(pfPrincipalDTO, temp);
			log.info("SARE: rettifica azienda " + pfPrincipalDTO.getUsername() + " con esito=" + esitoWs);

			PfPrincipal princProvincia = pfPrincipalHome.findById(idPrincipalMod);
			Provincia prov = princProvincia.getProvinciasForIdPfPrincipal().iterator().next();

			entityManager.flush();
			entityManager.refresh(pfPrincipal);

			Set<MsgMessaggioDTO> n = notificationBuilder.buildNotificationsAziendaInfoModified(prov, aziendaInfo);
			notificationBuilder.sendNotification(n);
		}

		return temp;
	}

	public void salvaLogo(Integer idAziendaInfo, byte[] logo) {
		AziendaInfo aziendaInfo = findById(idAziendaInfo);

		aziendaInfo.setLogo(logo);

		merge(aziendaInfo);
	}

	/**
	 * Modifica atomica dei dati, compreso contatto SARE
	 * 
	 * @param azienda
	 *            DTO azienda
	 * @param aziendaInfo
	 *            DTO azienda info
	 * @param princMod
	 *            autore modifica
	 * @param sareUserExistingAssorm
	 * @param sareAccessRequired
	 * @return DTO aggiornato
	 * 
	 * 
	 * @since 0.7.5 prima mandava in errore l'autenticazione forte in seguito a registrazione debole
	 * @author pegoraro
	 */
	public AziendaInfoDTO transactedUpdate(AziendaInfoDTO aziendaInfo, Integer princMod, boolean sareUserExistingAssorm,
			boolean sareAccessRequired) {
		PfPrincipalDTO pfSare = pfPrincipalHome.findDTOById(princMod);

		// se l'azienda ha chiesta l'autorizzazione all'accesso SARE setto lo
		// stato richiesta come NUOVA
		if (sareAccessRequired) {
			// l'utente ha richiesto l'abilitazione all'accesso al SARE
			pfSare.setFlagAbilitatoSare(true);
			pfPrincipalHome.mergeDTO(pfSare, princMod);

			// Se il codice richiesta a SARE è nullo significa che è la prima
			// volta che si fa richiesta
			if (aziendaInfo.getCodAutorizzazioneSare() == null) {
				// 0 e' nuova richiesta, vedi de_autorizzazione_sare
				aziendaInfo.setCodAutorizzazioneSare("0");
				// R e' mittente SARE, vedi de_tipo_utente_sare
				aziendaInfo.setCodTipoUtenteSare("R");
			}

		}

		aziendaInfo = mergeDTO(aziendaInfo, princMod);

		// forza l'esecuzione di tutte le query prima di procedere alla chiamata
		// verso SARE
		entityManager.flush();

		// nel caso l'azienda richiese il primo accesso
		if (sareAccessRequired) {
			if (sareUserExistingAssorm) {
				// chiamata al SARE per registrare l'utente azienda

				String esitoWs = utenteAziendaSARE.modificaUtenteSare(pfSare, aziendaInfo);
				log.info("SARE: modifica registrazione azienda " + pfSare.getUsername() + " con esito=" + esitoWs);
			} else {
				// chiamata al SARE per registrare l'utente azienda

				String esitoWs = utenteAziendaSARE.registraUtenteSare(pfSare, aziendaInfo);
				log.info("SARE: registrazione azienda " + pfSare.getUsername() + " con esito=" + esitoWs);
			}

		}

		return aziendaInfo;
	}

	/**
	 * Aggiorna l'abilitazione al SARE e/o il tipo utente delle aziende passate come input
	 * 
	 * @param daAggiornare
	 *            lista delle aziende da aggiornare
	 */
	public void aggiornaUtentiSARE(List<UtenteSAREDTO> daAggiornare, Integer idPfPrincipalMod) {
		for (UtenteSAREDTO utenteSARE : daAggiornare) {
			AziendaInfo aziendaInfo = findById(utenteSARE.getIdAziendaInfo());
			aziendaInfo.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
			if (!NESSUNA_MODIFICA.equalsIgnoreCase(utenteSARE.getModificaAutorizzazioneSARE())) {
				// aziendaInfo.setRequestStatus(utenteSARE.getModificaAutorizzazioneSARE());
				aziendaInfo.setDeAutorizzazioneSare(
						deAutorizzazioneSareHome.findById(utenteSARE.getModificaAutorizzazioneSARE()));
				merge(aziendaInfo);

				AziendaInfoDTO aziendaInfoDTO = toDTO(aziendaInfo);
				// ABILITAZIONE SARE
				String esitoWs = utenteAziendaSARE.abilitaUtenteSare(utenteSARE, aziendaInfoDTO);
				log.info("SARE: abilitazione azienda " + utenteSARE.getUsername() + " con esito=" + esitoWs);

			}
			if (!NESSUNA_MODIFICA.equalsIgnoreCase(utenteSARE.getModificaCodTipoUtenteSare())) {
				aziendaInfo
						.setDeTipoUtenteSare(deTipoUtenteSareHome.findById(utenteSARE.getModificaCodTipoUtenteSare()));
				merge(aziendaInfo);

				AziendaInfoDTO aziendaInfoDTO = toDTO(aziendaInfo);
				// ABILITAZIONE SARE
				// String esitoWs =
				// utenteAziendaSARE.modificaTipoUtenteSare(utenteSARE,
				// aziendaInfoDTO);
				// DAVIDE 14/12/2012
				// viene inviata sempre una modifica dati con l'aggiunta del
				// tipo utente
				// questo per far in modo di cancellare sul SARE i dti che non
				// servono
				Integer idPfPrincipal = utenteSARE.getIdPfPrincipal();
				PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOById(idPfPrincipal);
				AziendaSessionDTO aziendaDTO = findSessionDTOByUserName(utenteSARE.getUsername());

				String esitoWs = utenteAziendaSARE.modificaUtenteSare(pfPrincipalDTO, aziendaInfoDTO);
				log.info("SARE: abilitazione/modifica azienda " + utenteSARE.getUsername() + " con esito=" + esitoWs);
			}
		}
	}

	/**
	 * Aggiorna i dati dell'utente modificati dal CPI nella pagina amministrativa deghli utenti SARE. I campi
	 * modificabili sono: pf_principal_info.email, azienda_info.referente_sare, azienda_info.telefono_referente e
	 * azienda_info.email_referente
	 * 
	 * @param utenteSARE
	 *            nuovi dati da aggiornare per l'utente SARE
	 */
	public void aggiornaUtenteSARE(UtenteSAREDTO utenteSARE, Integer idPfPrincipalMod) {
		// aggiorno la tabella azienda_info
		AziendaInfo aziendaInfo = findById(utenteSARE.getIdAziendaInfo());
		aziendaInfo.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
		aziendaInfo.setReferenteSare(utenteSARE.getNomeReferente());
		aziendaInfo.setEmailReferente(utenteSARE.getEmailReferente());
		aziendaInfo.setTelefonoReferente(utenteSARE.getTelefonoReferente());
		merge(aziendaInfo);

		// aggiorno la tabella pf_principal_info
		Integer idPfPrincipal = utenteSARE.getIdPfPrincipal();
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
		pfPrincipal.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
		pfPrincipal.setEmail(utenteSARE.getEmailRegistrazione());
		pfPrincipalHome.merge(pfPrincipal);

		// invio modifica a SARE
		PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOById(idPfPrincipal);

		AziendaSessionDTO aziendaDTO = findSessionDTOByUserName(utenteSARE.getUsername());
		AziendaInfoDTO aziendaInfoDTO = findDTOById(utenteSARE.getIdAziendaInfo());

		String esitoWs = utenteAziendaSARE.modificaUtenteSare(pfPrincipalDTO, aziendaInfoDTO);
		log.info("SARE: modifica dati referente SARE " + utenteSARE.getUsername() + " con esito=" + esitoWs);
	}

	/**
	 * Fa una query per trovare una lista di aziende basandosi sui parametri di ricerca. Supporta la paginazione tramite
	 * gli ultimi due parametri.
	 */
	public List<AziendaMiniDTO> findMiniDTOByFilter(String name, String where, String whereCod,
			List<Integer> entiAccreditatiList, Boolean entiAccreditati, boolean provincia, int maxResults,
			int startResultsFrom) {

		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AziendaMiniDTO> query = cb.createQuery(AziendaMiniDTO.class);

		// Creo la clausola FROM della query.
		Root<AziendaInfo> aziendaInfo = query.from(AziendaInfo.class);
		Join<AziendaInfo, DeComune> deComune = aziendaInfo.join(AziendaInfo_.deComuneSede, JoinType.LEFT);
		Join<DeComune, DeProvincia> deProvincia = deComune.join(DeComune_.deProvincia, JoinType.LEFT);
		Join<AziendaInfo, PfPrincipal> pfPrincipal = aziendaInfo.join(AziendaInfo_.pfPrincipal, JoinType.LEFT);
		Join<PfPrincipal, SvAziendaInfo> svAziendaInfo = pfPrincipal.join(PfPrincipal_.svAziendaInfo, JoinType.LEFT);

		// Creo la clausola SELECT della query
		query.select(cb.construct(AziendaMiniDTO.class, aziendaInfo.get(AziendaInfo_.idPfPrincipal),
				aziendaInfo.get(AziendaInfo_.ragioneSociale), deComune.get(DeComune_.denominazione)));

		// Creo la clausola WHERE della query
		Subquery<Long> subquery = query.subquery(Long.class);
		List<Predicate> whereConditions = findByFilterCreateWhereConditions(name, where, whereCod, entiAccreditatiList,
				entiAccreditati, provincia, subquery, aziendaInfo, deComune, deProvincia, pfPrincipal, svAziendaInfo);

		// Aggiungo la clausola ORDER BY: ordino il risultato per nome dell'azienda.
		query.orderBy(cb.asc(aziendaInfo.get(AziendaInfo_.ragioneSociale)));

		// Costruisco la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<AziendaMiniDTO> tquery = entityManager.createQuery(query);

		// Aggiungo la paginazione.
		if (startResultsFrom != 0)
			tquery.setFirstResult(startResultsFrom);
		if (maxResults != 0)
			tquery.setMaxResults(maxResults);

		// Eseguo la query e restituisco il risultato
		return tquery.getResultList();
	}

	/**
	 * Fa una query per scoprire quante aziende corrispondono ai parametri di ricerca specificati. Serve per
	 * inizializzare la paginazione.
	 */
	public Long findCountByFilter(String name, String where, String whereCod, List<Integer> entiAccreditatiList,
			Boolean entiAccreditati, boolean provincia) {
		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		// Creo la clausola FROM della query.
		Root<AziendaInfo> aziendaInfo = query.from(AziendaInfo.class);
		Join<AziendaInfo, DeComune> deComune = aziendaInfo.join(AziendaInfo_.deComuneSede, JoinType.LEFT);
		Join<DeComune, DeProvincia> deProvincia = deComune.join(DeComune_.deProvincia, JoinType.LEFT);
		Join<AziendaInfo, PfPrincipal> pfPrincipal = aziendaInfo.join(AziendaInfo_.pfPrincipal, JoinType.LEFT);
		Join<PfPrincipal, SvAziendaInfo> svAziendaInfo = pfPrincipal.join(PfPrincipal_.svAziendaInfo, JoinType.LEFT);

		// Creo la clausola SELECT della query
		query.select(cb.count(aziendaInfo));

		// Creo la clausola WHERE della query
		Subquery<Long> subquery = query.subquery(Long.class);
		List<Predicate> whereConditions = this.findByFilterCreateWhereConditions(name, where, whereCod,
				entiAccreditatiList, entiAccreditati, provincia, subquery, aziendaInfo, deComune, deProvincia,
				pfPrincipal, svAziendaInfo);

		// Costruisco la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> tquery = entityManager.createQuery(query);

		// Eseguo la query e restituisco il risultato
		return tquery.getSingleResult();
	}

	/**
	 * Funzione privata che costruisce la clausola WHERE delle query costruite dalle funzioni "findMiniDTOByFilter" e
	 * "findCountByFilter". Serve ad evitare duplicazione codice.
	 */
	private List<Predicate> findByFilterCreateWhereConditions(String name, String where, String whereCod,
			List<Integer> entiAccreditatiList, Boolean entiAccreditati, boolean provincia, Subquery<Long> subquery,
			Root<AziendaInfo> aziendaInfo, Join<AziendaInfo, DeComune> deComune,
			Join<DeComune, DeProvincia> deProvincia, Join<AziendaInfo, PfPrincipal> pfPrincipal,
			Join<PfPrincipal, SvAziendaInfo> svAziendaInfo) {
		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// Clausola 1: nome. (E' possibile cercare più di una parola)
		if (StringUtils.isNotBlank(name)) {
			String[] parole = name.split(" ");
			for (String parola : parole) {
				// Due possibilità: la prima parola inizia con NAME, o una parola che non è la prima inizia con NAME.
				Predicate nomeCondizione = cb.or(
						cb.like(cb.upper(aziendaInfo.get(AziendaInfo_.ragioneSociale)), parola.toUpperCase() + "%"),
						cb.like(cb.upper(aziendaInfo.get(AziendaInfo_.ragioneSociale)),
								"% " + parola.toUpperCase() + "%"));
				whereConditions.add(nomeCondizione);
			}
		}

		// Clausola 2: provincia
		if (provincia && StringUtils.isNotBlank(whereCod)) {
			Predicate provinciaCondizione = cb.equal(deProvincia.get(DeProvincia_.codProvincia), whereCod);
			whereConditions.add(provinciaCondizione);
		}

		// Clausola 3: comune
		if (!provincia && StringUtils.isNotBlank(whereCod)) {
			Predicate comuneCondizione = cb.equal(deComune.get(DeComune_.codCom), whereCod);
			whereConditions.add(comuneCondizione);
		}

		// Clausola 4: deve avere almeno una vetrina (uso una subquery).
		Root<SvAziendaInfo> svAziendaInfoSub = subquery.from(SvAziendaInfo.class);
		subquery.select(cb.count(svAziendaInfoSub));
		subquery.where(cb.equal(svAziendaInfoSub.get(SvAziendaInfo_.idPfPrincipal),
				svAziendaInfo.get(SvAziendaInfo_.idPfPrincipal)));
		Predicate vetrinaCondizione = cb.greaterThanOrEqualTo(subquery, new Long(1));
		whereConditions.add(vetrinaCondizione);

		// Clausola 5: filtro sugli enti accreditati (SI / NO / null)
		if (entiAccreditati != null && entiAccreditatiList != null) {
			Predicate isEnteAccreditatoPredicate = aziendaInfo.get(AziendaInfo_.idPfPrincipal).in(entiAccreditatiList);
			if (entiAccreditati) {
				whereConditions.add(isEnteAccreditatoPredicate);
			} else {
				whereConditions.add(cb.not(isEnteAccreditatoPredicate));
			}
		}

		return whereConditions;
	}

	public DeProvincia getProvinciaRiferimento(AziendaInfo aziendaInfo) {
		PfPrincipal pfPrincipal = aziendaInfo.getPfPrincipal();
		if (pfPrincipal.getFlagAbilitatoSare()) {
			return aziendaInfo.getDeProvincia();
		} else {
			DeProvincia provinciaSede = aziendaInfo.getDeComuneSede().getDeProvincia();
			DeRegione regioneSede = provinciaSede.getDeRegione();
			if (ConstantsSingleton.COD_REGIONE.equals(regioneSede.getCodRegione())) {
				return provinciaSede;
			} else {
				DeRegione regioneApp = deRegioneHome.findById(ConstantsSingleton.COD_REGIONE.toString());
				return deRegioneHome.getCapoluogo(regioneApp);
			}
		}
	}

	public AziendaInfo register(RegisterAziendaDTO register, boolean pwdScaduta) {
		// l'utente che registra quello nuovo è l'amministratore
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();
		// crea il principal
		PfPrincipal principalAzienda = pfPrincipalHome.registerAzienda(register, administrator, now, pwdScaduta);

		// DATI ACCOUNT E DATI RICHIEDENTE
		AziendaInfo aziendaInfo = new AziendaInfo();
		aziendaInfo.setIdPfPrincipal(principalAzienda.getIdPfPrincipal());
		aziendaInfo.setPfPrincipal(principalAzienda);
		aziendaInfo.setEmailRic(register.getEmail());

		/**
		 * Dati soggetto che richiede la registrazione per conto azienda
		 */
		aziendaInfo.setNomeRic(register.getNomeRic());
		aziendaInfo.setCognomeRic(register.getCognomeRic());
		if (register.getDataNascitaRic() != null) {
			aziendaInfo.setDtDataNascitaRic(register.getDataNascitaRic());
		}
		if (register.getComuneNascitaRic().getId() != null) {
			aziendaInfo.setDeComuneNascitaRic(deComuneHome.findById(register.getComuneNascitaRic().getId()));
		}
		aziendaInfo.setIndirizzoRic(register.getIndirizzo());
		aziendaInfo.setCapRic(register.getCap());

		// DATI AZIENDA
		aziendaInfo.setCodiceFiscale(register.getCodiceFiscale().toUpperCase());
		aziendaInfo.setRagioneSociale(register.getRagioneSociale());

		// SEDE OPERATIVA
		SedeDTO sedeOperativa = register.getSedeOperativa();
		aziendaInfo.setIndirizzoSede(sedeOperativa.getIndirizzo());
		aziendaInfo.setEmailSede(register.getEmail());
		if (sedeOperativa.getCap() != null) {
			aziendaInfo.setCapSede(sedeOperativa.getCap());
		}
		if (sedeOperativa.getComune().getId() != null) {
			aziendaInfo.setDeComuneSede(deComuneHome.findById(sedeOperativa.getComune().getId()));
		}
		if (sedeOperativa.getTelefono() != null) {
			aziendaInfo.setTelefonoSede(sedeOperativa.getTelefono());
		}
		if (sedeOperativa.getFax() != null) {
			aziendaInfo.setFaxSede(sedeOperativa.getFax());
		}
		if (sedeOperativa.getTelefono() != null) {
			aziendaInfo.setTelefonoSedeLegale(sedeOperativa.getTelefono());
		}
		if (sedeOperativa.getFax() != null) {
			aziendaInfo.setFaxSedeLegale(sedeOperativa.getFax());
		}
		aziendaInfo.setFlagAgenziaEstera(false); // normalmente

		// richesto accesso SARE -> metto anche i dati relativi
		if (register.getRichiestaAccessoSARE()) {
			popolaDatiAccessoSARE(register, aziendaInfo, "0");
		}

		aziendaInfo.setPfPrincipalIns(administrator);
		aziendaInfo.setPfPrincipalMod(administrator);
		aziendaInfo.setDtmMod(now);
		aziendaInfo.setDtmIns(now);
		aziendaInfo.setFlagValida(register.isFlgValida());
		persist(aziendaInfo);

		List<PtPortlet> portlets = null;
		portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.AZIENDA);

		if (portlets != null) {
			for (int i = 0; i < portlets.size(); i++) {
				PtPortlet iesimaPortlet = portlets.get(i);

				PtScrivania iesimaScrivania = new PtScrivania();
				iesimaScrivania.setPfPrincipal(principalAzienda);
				iesimaScrivania.setFlagRidotta(false);
				iesimaScrivania.setFlagVisualizza(true);
				iesimaScrivania.setPtPortlet(iesimaPortlet);
				iesimaScrivania.setDtmIns(new Date());
				iesimaScrivania.setDtmMod(new Date());
				iesimaScrivania.setPfPrincipalIns(aziendaInfo.getPfPrincipalIns());
				iesimaScrivania.setPfPrincipalMod(aziendaInfo.getPfPrincipalMod());

				iesimaScrivania.setPosizione(i % 5);
				iesimaScrivania.setOptColonna((i % 2 == 0) ? "L" : "R");

				ptScrivaniaHome.persist(iesimaScrivania);

				ptScrivaniaHome.findPortletsScrivania(aziendaInfo.getIdPfPrincipal());
			}
		}

		if (register.getRichiestaAccessoSARE()) {
			// chiamata al SARE per registrare l'utente azienda
			PfPrincipalDTO pfSare = pfPrincipalHome.findDTOById(principalAzienda.getIdPfPrincipal());
			AziendaSessionDTO azSARE = findSessionDTOById(aziendaInfo.getIdPfPrincipal());
			AziendaInfoDTO azInfoSARE = findDTOById(azSARE.getId());

			String esitoWs = utenteAziendaSARE.registraUtenteSare(pfSare, azInfoSARE);
			log.info("SARE: registrazione azienda " + principalAzienda.getUsername() + " con esito=" + esitoWs);
			// TODO - parsare la risposta del ws
			// azInfoSARE.setCodAutorizzazioneSare(esitoWs);
			// aziendaInfoHome.persistDTO(azInfoSARE,
			// administrator.getIdPfPrincipal());
		}

		return aziendaInfo;
	}

	private void popolaDatiAccessoSARE(RegisterAziendaDTO register, AziendaInfo aziendaInfo,
			String codAutorizzazioneSare) {
		// RICHIESTA SARE IN STATO NUOVA - DA APPROVARE
		// in fase di registrazione il tipo utente SARE è sempre quello MITTENTE
		// SARE con CODICE = R
		aziendaInfo.setDeTipoUtenteSare(deTipoUtenteSareHome.findById("R"));
		// aziendaInfo.setRequestStatus(reqStatusIn);
		if (codAutorizzazioneSare == null)
			codAutorizzazioneSare = "0";
		aziendaInfo.setDeAutorizzazioneSare(deAutorizzazioneSareHome.findById(codAutorizzazioneSare));

		aziendaInfo.setDeProvincia(deProvinciaHome.findById(register.getProvincia().getId()));
		aziendaInfo.setMittenteSare(register.getMittenteSare());
		aziendaInfo.setSwCreazioneCo(register.getSoftwareSAREUtilizzato().toString());
		if (register.getComune() != null && register.getComune().getId() != null)
			aziendaInfo.setDeComuneRichiedente(deComuneHome.findById(register.getComune().getId()));
		if (register.getTipoRichiedente() != null)
			aziendaInfo.setDeTipoAbilitato(deTipoAbilitatoHome.findById(register.getTipoRichiedente()));
		if (register.getTipoDelegato() != null)
			aziendaInfo.setDeTipoDelegato(deTipoDelegatoHome.findById(register.getTipoDelegato()));

		// Ulteriori dati aziendali
		aziendaInfo.setPartitaIva(register.getPartitaIva());
		aziendaInfo.setReferenteSare(register.getReferenteSare());
		aziendaInfo.setTelefonoReferente(register.getTelefono());
		aziendaInfo.setEmailReferente(register.getEmailReferenteSare());

		// SEDE LEGALE
		SedeDTO sedeLegale = register.getSedeLegale();
		if (sedeLegale != null) {
			aziendaInfo.setIndirizzoSedeLegale(sedeLegale.getIndirizzo());
			aziendaInfo.setCapSedeLegale(sedeLegale.getCap());
			if (sedeLegale.getComune() != null && sedeLegale.getComune().getId() != null) {
				aziendaInfo.setDeComuneSedeLegale(deComuneHome.findById(sedeLegale.getComune().getId()));
			}
			aziendaInfo.setTelefonoSedeLegale(sedeLegale.getTelefono());
			aziendaInfo.setFaxSedeLegale(sedeLegale.getFax());
		}

		// AGENZIA DI SOMMINISTRAZIONE
		if (ConstantsSingleton.DeTipoAbilitato.AGENZIA_SOMMINISTRAZIONE.equals(register.getTipoRichiedente())) {
			AgenziaDTO agenzia = register.getAgenzia();
			if (agenzia != null) {
				aziendaInfo.setFlagAgenziaEstera(agenzia.getEstera());
				aziendaInfo.setIscrProvvedNumero(agenzia.getNumeroProvvedimento());
				aziendaInfo.setDtIscrProvvedData(agenzia.getDataProvvedimento());
				if (agenzia.getComune() != null && agenzia.getComune().getId() != null) {
					aziendaInfo.setDeComuneIscrizione(deComuneHome.findById(agenzia.getComune().getId()));
				}
				aziendaInfo.setIscrNumero(agenzia.getNumeroIscrizione());
				aziendaInfo.setDtIscrData(agenzia.getDataIscrizione());
			}
		}
		// SOGGETTO ABILITATO
		else if (ConstantsSingleton.DeTipoAbilitato.SOGGETTO_ABILITATO.equals(register.getTipoRichiedente())) {
			SoggettoDTO soggetto = register.getSoggettoAbilitato();
			aziendaInfo.setIscrOrdine(soggetto.getOrdineIscrizione());

			if (soggetto.getLuogoIscrizione() != null && soggetto.getLuogoIscrizione().getId() != null) {
				aziendaInfo.setDeComuneIscrizione(deComuneHome.findById(soggetto.getLuogoIscrizione().getId()));
			}

			aziendaInfo.setIscrNumero(soggetto.getNumeroIscrizione());
			aziendaInfo.setDtIscrData(soggetto.getDataIscrizione());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.myportal.entity.home.IPfPrincipalHome#confirm(it.eng.myportal. dtos.RegisterUtenteDTO)
	 */
	public Integer confirm(RegisterAziendaDTO data) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(data.getUsername());
		if (pfPrincipal == null)
			return ConstantsSingleton.Register.USER_NOT_FOUND;
		AziendaInfo aziendaInfo = pfPrincipal.getAziendaInfo();
		if (aziendaInfo == null)
			return ConstantsSingleton.Register.USER_NOT_FOUND;
		if (pfPrincipal.getFlagAbilitato())
			return ConstantsSingleton.Register.USER_ALREADY_ACTIVATED;
		if (data.getActivateToken() == null || !data.getActivateToken().equals(pfPrincipal.getConfirmationToken())) {
			return ConstantsSingleton.Register.WRONG_TOKEN;
		}

		if (data.getEmail() == null || !data.getEmail().equals(pfPrincipal.getEmail())) {
			return ConstantsSingleton.Register.WRONG_EMAIL;
		}
		pfPrincipal.setFlagAbilitato(true); // non è ancora attivo
		// TODO conferma campi
		pfPrincipal.setDtEmailPassword(new Date());
		pfPrincipal.setDtEmailVerification(new Date());
		pfPrincipalHome.persist(pfPrincipal);
		return ConstantsSingleton.Register.USER_ACTIVATED;
	}

	public void registerImportSare(RegisterAziendaDTO register) {
		// l'utente che registra quello nuovo è l'amministratore
		PfPrincipal administrator = pfPrincipalHome.findById(0);
		Date now = new Date();
		// crea il principal
		PfPrincipal principalAzienda = pfPrincipalHome.registerFromSareImport(register, administrator, now);

		// DATI ACCOUNT E DATI RICHIEDENTE
		AziendaInfo aziendaInfo = new AziendaInfo();

		/**
		 * Dati soggetto che richiede la registrazione per conto azienda
		 */
		aziendaInfo.setNomeRic(register.getNomeRic());
		aziendaInfo.setCognomeRic(register.getCognomeRic());
		if (register.getDataNascitaRic() != null) {
			aziendaInfo.setDtDataNascitaRic(register.getDataNascitaRic());
		}
		if (register.getComuneNascitaRic() != null && register.getComuneNascitaRic().getId() != null) {
			aziendaInfo.setDeComuneNascitaRic(deComuneHome.findById(register.getComuneNascitaRic().getId()));
		}
		aziendaInfo.setIndirizzoRic(register.getIndirizzo());
		aziendaInfo.setCapRic(register.getCap());

		// DATI AZIENDA
		aziendaInfo.setCodiceFiscale(register.getCodiceFiscale());
		aziendaInfo.setRagioneSociale(register.getRagioneSociale());

		// SEDE OPERATIVA
		SedeDTO sedeOperativa = register.getSedeOperativa();
		aziendaInfo.setIndirizzoSede(sedeOperativa.getIndirizzo());
		if (sedeOperativa.getCap() != null) {
			aziendaInfo.setCapSede(sedeOperativa.getCap());
		}
		if (sedeOperativa.getComune() != null && sedeOperativa.getComune().getId() != null) {
			aziendaInfo.setDeComuneSede(deComuneHome.findById(sedeOperativa.getComune().getId()));
		}
		if (sedeOperativa.getTelefono() != null) {
			aziendaInfo.setTelefonoSede(sedeOperativa.getTelefono());
		}
		if (sedeOperativa.getFax() != null) {
			aziendaInfo.setFaxSede(sedeOperativa.getFax());
		}
		if (sedeOperativa.getTelefono() != null) {
			aziendaInfo.setTelefonoSedeLegale(sedeOperativa.getTelefono());
		}
		if (sedeOperativa.getFax() != null) {
			aziendaInfo.setFaxSedeLegale(sedeOperativa.getFax());
		}
		aziendaInfo.setFlagAgenziaEstera(false); // normalmente

		// richesto accesso SARE -> metto anche i dati relativi
		if (register.getRichiestaAccessoSARE()) {
			popolaDatiAccessoSARE(register, aziendaInfo, ((RegisterAziendaDTO) register).getStrRequestStatus());
		}

		DeTipoUtenteSareDTO tipoUtenteSareDTO = ((RegisterAziendaDTO) register).getDeTipoUtenteSare();
		if (tipoUtenteSareDTO != null) {
			aziendaInfo.setDeTipoUtenteSare(deTipoUtenteSareHome.findById(tipoUtenteSareDTO.getId()));
		}

		aziendaInfo.setPfPrincipalIns(administrator);
		aziendaInfo.setPfPrincipalMod(administrator);
		aziendaInfo.setDtmMod(now);
		aziendaInfo.setDtmIns(now);
		persist(aziendaInfo);

		List<PtPortlet> portlets = null;
		portlets = ptPortletHome.findByCodRuoloPortale(ConstantsSingleton.DeRuoloPortale.AZIENDA);

		if (portlets != null) {
			for (int i = 0; i < portlets.size(); i++) {
				PtPortlet iesimaPortlet = portlets.get(i);

				PtScrivania iesimaScrivania = new PtScrivania();
				iesimaScrivania.setPfPrincipal(aziendaInfo.getPfPrincipal());
				iesimaScrivania.setFlagRidotta(false);
				iesimaScrivania.setFlagVisualizza(true);
				iesimaScrivania.setPtPortlet(iesimaPortlet);
				iesimaScrivania.setDtmIns(new Date());
				iesimaScrivania.setDtmMod(new Date());
				iesimaScrivania.setPfPrincipalIns(aziendaInfo.getPfPrincipalIns());
				iesimaScrivania.setPfPrincipalMod(aziendaInfo.getPfPrincipalMod());

				iesimaScrivania.setPosizione(i % 5);
				iesimaScrivania.setOptColonna((i % 2 == 0) ? "L" : "R");

				ptScrivaniaHome.persist(iesimaScrivania);

				ptScrivaniaHome.findPortletsScrivania(aziendaInfo.getIdPfPrincipal());
			}
		}
	}

	public List<AziendaSessionDTO> findSessionDTOByFilter(String name, String where) {

		String basicQuery = " SELECT info.* " + " FROM mycas.azienda_info info "
				+ " inner join de_comune com ON info.cod_com_sede = com.cod_com "
				+ " inner join sv_azienda_info sv ON info.id_pf_principal = sv.id_pf_principal " + "WHERE  1 = 1 ";

		StringBuilder sb = new StringBuilder(basicQuery);
		List<String> paramlist = new ArrayList<String>();
		if (StringUtils.isNotBlank(name)) {
			sb.append(" and to_tsvector(info.ragione_sociale) @@ to_tsquery(?) ");
			String queryName = splitStringForTSQuery(name);
			paramlist.add(queryName);
		}
		if (StringUtils.isNotBlank(where)) {
			sb.append(" and to_tsvector(com.denominazione) @@ to_tsquery(?) ");
			String queryWhere = splitStringForTSQuery(where);
			paramlist.add(queryWhere);
		}

		Query nq = entityManager.createNativeQuery(sb.toString(), AziendaInfo.class);
		int paramIdx = 1;
		for (String param : paramlist) {
			nq.setParameter(paramIdx++, param);
		}
		List<AziendaInfo> l = nq.getResultList();
		List<AziendaSessionDTO> lDto = new ArrayList<AziendaSessionDTO>();
		if (l != null)
			for (AziendaInfo az : l) {
				lDto.add(toSessionDTO(az));
			}

		// log.debug("recupero azienda con username: " + name);

		return lDto;
	}
	
	public AziendaInfo findByIdPfPrincipal(Integer idPfPrincipal) {
		AziendaInfo aziendaInfo = entityManager.createNamedQuery("findByPfPrincipal", AziendaInfo.class)
				.setParameter("idPfPrincipal", idPfPrincipal)
				.getSingleResult();
		return aziendaInfo;
	}

}
