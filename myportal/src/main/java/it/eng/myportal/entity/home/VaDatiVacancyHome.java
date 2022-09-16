package it.eng.myportal.entity.home;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.eng.myportal.beans.SessionBean;
import it.eng.myportal.beans.UtilsBean;
import it.eng.myportal.beans.vacancies.TestataVacancyBean;
// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.CvFilterDTO;
import it.eng.myportal.dtos.DeAttivitaMinDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.DeMotivoChiusuraDTO;
import it.eng.myportal.dtos.DeProvenienzaDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.NotificaInLavVacancyDTO;
import it.eng.myportal.dtos.NotificaScadenzaVacancyDTO;
import it.eng.myportal.dtos.OfferteMiniDTO;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.RicercaOfferteDTO;
import it.eng.myportal.dtos.SedeDTO;
import it.eng.myportal.dtos.SvAziendaInfoDTO;
import it.eng.myportal.dtos.VaAltreInfoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.dtos.VaVisualizzaDTO;
import it.eng.myportal.entity.AbstractEntity;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.CvLingua;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.IEntity;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Poi;
import it.eng.myportal.entity.SvAziendaInfo;
import it.eng.myportal.entity.VaAgevolazione;
import it.eng.myportal.entity.VaAlbo;
import it.eng.myportal.entity.VaAlbo_;
import it.eng.myportal.entity.VaAltreInfo;
import it.eng.myportal.entity.VaCompetenzeTrasv;
import it.eng.myportal.entity.VaContatto;
import it.eng.myportal.entity.VaContratto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaDatiVacancy_;
import it.eng.myportal.entity.VaEsperienze;
import it.eng.myportal.entity.VaInformatica;
import it.eng.myportal.entity.VaIstruzione;
import it.eng.myportal.entity.VaIstruzione_;
import it.eng.myportal.entity.VaLingua;
import it.eng.myportal.entity.VaLingua_;
import it.eng.myportal.entity.VaOrario;
import it.eng.myportal.entity.VaPatente;
import it.eng.myportal.entity.VaPatente_;
import it.eng.myportal.entity.VaPatentino;
import it.eng.myportal.entity.VaPatentino_;
import it.eng.myportal.entity.VaPubblicazione;
import it.eng.myportal.entity.VaRetribuzione;
import it.eng.myportal.entity.VaTurno;
import it.eng.myportal.entity.VaVacancyCl;
import it.eng.myportal.entity.decodifiche.DeAlbo;
import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeEvasioneRich;
import it.eng.myportal.entity.decodifiche.DeEvasioneRich_;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.DePatentino;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.decodifiche.DeProvenienza_;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.DeTrasferta;
import it.eng.myportal.entity.decodifiche.min.DeAttivitaMin;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.decodifiche.sil.DeAttivitaSil;
import it.eng.myportal.entity.decodifiche.sil.DeMansioneSil;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.ejb.ErrorsSingleton;
import it.eng.myportal.entity.ejb.NotificationBuilder;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneSilHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;
import it.eng.myportal.entity.home.decodifiche.DeAreaSilHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaSilHome;
import it.eng.myportal.entity.home.decodifiche.DeBpMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeEvasioneRichHome;
import it.eng.myportal.entity.home.decodifiche.DeGenereHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinSilHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneSilHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoChiusuraHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoEtaSilHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoGenereSilHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeSvTemplateHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoSilHome;
import it.eng.myportal.entity.home.local.IVaContattoHome;
import it.eng.myportal.enums.TipoRicercaOfferte;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.AzioneCliclavoro;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.URL;
import it.eng.myportal.utils.Utils;

/**
 * Home object for domain model class VaDatiVacancy.
 * 
 * @see it.eng.myportal.entity.VaDatiVacancy
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaDatiVacancyHome extends AbstractUpdatableHome<VaDatiVacancy, VaDatiVacancyDTO>
		implements InoDTOejb<VaDatiVacancy> {

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@EJB
	DeSvTemplateHome deSvTemplateHome;

	@EJB
	protected ErrorsSingleton errorsBean;

	@EJB
	NotificationBuilder notificationBuilder;

	@EJB
	private DeProvenienzaHome deProvenienzaHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeTrasfertaHome deTrasfertaHome;

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	DeGradoLinHome deGradoLinHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DeAlboHome deAlboHome;

	@EJB
	DeProvenienzaHome deProvenienzaVacancyHome;

	@EJB
	VaAltreInfoHome vaAltreInfoHome;

	@EJB
	VaCompetenzeTrasvHome vaCompetenzeTrasvHome;

	@EJB
	VaInformaticaHome vaInformaticaHome;

	@EJB
	VaIstruzioneHome vaIstruzioneHome;

	@EJB
	VaLinguaHome vaLinguaHome;

	@EJB
	IVaContattoHome vaContattoHome;

	@EJB
	VaAlboHome vaAlboHome;

	@EJB
	VaAgevolazioneHome vaAgevolazioneHome;

	@EJB
	VaContrattoHome vaContrattoHome;

	@EJB
	VaOrarioHome vaOrarioHome;

	@EJB
	VaPatenteHome vaPatenteHome;

	@EJB
	VaPatentinoHome vaPatentinoHome;

	@EJB
	VaTurnoHome vaTurnoHome;

	@EJB
	VaRetribuzioneHome vaRetribuzioneHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	DeMotivoChiusuraHome motivoChiusuraHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	DeAttivitaMinHome deAttivitaMinHome;

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	VaVacancyClHome vaVacancyClHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeEvasioneRichHome deEvasioneRichHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	PoiHome poiHome;

	@EJB
	SvAziendaInfoHome svAziendaInfoHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeAgevolazioneHome deAgevolazioneHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeMansioneSilHome deMansioneSilHome;

	@EJB
	DeBpMansioneHome deBpMansioneHome;

	@EJB
	DeAttivitaSilHome deAttivitaSilHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	TsGetOpzioniEJB tsGetOpzioniEJB;

	@EJB
	DeGenereHome deGenereHome;

	@EJB
	DeMotivoGenereSilHome deMotivoGenereSilHome;

	@EJB
	DeAreaSilHome deAreaSilHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	@EJB
	DeAlboSilHome deAlboSilHome;

	@EJB
	DePatentinoSilHome dePatentinoSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DeTurnoSilHome deTurnoSilHome;

	@EJB
	DeTurnoHome deTurnoHome;

	@EJB
	DeMotivoEtaSilHome deMotivoEtaSilHome;

	@EJB
	DeGradoLinSilHome deGradoLinSilHome;

	@EJB
	DeAgevolazioneSilHome deAgevolazioneSilHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	VaPubblicazioneHome vaPubblicazioneHome;

	@EJB
	VaEsperienzeHome vaEsperienzeHome;

	public VaDatiVacancy findById(Integer id) {
		return findById(VaDatiVacancy.class, id);
	}

	// Seleziono le vacancies di questa azienda (dirette o indirette palesi, a seconda del parametro).
	private Predicate filterVacancyByDirettePalesi(CriteriaBuilder cb, Root<VaDatiVacancy> vaDatiVacancy,
			int idPfPrincipalAzienda, boolean falseDiretteTruePalesi) {
		if (falseDiretteTruePalesi) {
			Join<VaDatiVacancy, PfPrincipal> aziendaInfoPalese = vaDatiVacancy.join(VaDatiVacancy_.pfPrincipalPalese);
			return cb.equal(aziendaInfoPalese.get(PfPrincipal_.idPfPrincipal), idPfPrincipalAzienda);
		} else {
			Join<VaDatiVacancy, PfPrincipal> aziendaInfo = vaDatiVacancy.join(VaDatiVacancy_.pfPrincipal);
			return cb.equal(aziendaInfo.get(PfPrincipal_.idPfPrincipal), idPfPrincipalAzienda);
		}
	}

	private Predicate filterVacancyByDFD(CriteriaBuilder cb, Root<VaDatiVacancy> vaDatiVacancy) {
		Join<VaDatiVacancy, DeEvasioneRich> codEvasione = vaDatiVacancy.join(VaDatiVacancy_.deEvasioneRich);
		return cb.equal(codEvasione.get(DeEvasioneRich_.codEvasione), ConstantsSingleton.Evasione.PUBB_PALESE);
	}

	// Escludo dalla selezione le vacancies eliminate.
	private Predicate filterVacancyByNonEliminate(CriteriaBuilder cb, Root<VaDatiVacancy> vaDatiVacancy) {
		return cb.equal(vaDatiVacancy.get(VaDatiVacancy_.flagEliminata), Boolean.FALSE);
	}

	// Seleziono solo le vacancies aventi stato Pubblicato
	private Predicate filterVacancyByCodStatoPubblicato(CriteriaBuilder cb, Root<VaDatiVacancy> vaDatiVacancy) {
		// da gen 2019 solo vacancy pubblicate (in ER = tutte)
		return cb.equal(vaDatiVacancy.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.PUB);
	}

	private Predicate filterVacancyByCodStatoLavorazione(CriteriaBuilder cb, Root<VaDatiVacancy> vaDatiVacancy) {
		return cb.equal(vaDatiVacancy.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.LAV);
	}

	private List<Predicate> vacancyFiltersList(CriteriaBuilder cb, Root<VaDatiVacancy> vaDatiVacancy,
			int idPfPrincipalAzienda, Integer idPfPrincipal, boolean scadute, boolean falseDiretteTruePalesi) {
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions
				.add(filterVacancyByDirettePalesi(cb, vaDatiVacancy, idPfPrincipalAzienda, falseDiretteTruePalesi));
		if (!scadute) {
			whereConditions.add(cb.greaterThan(vaDatiVacancy.get(VaDatiVacancy_.dtScadenzaPubblicazione), new Date()));
		}
		whereConditions.add(filterVacancyByNonEliminate(cb, vaDatiVacancy));
		whereConditions.add(filterVacancyByCodStatoPubblicato(cb, vaDatiVacancy));
		return whereConditions;
	}

	private List<Predicate> vacancyFiltersListForRER(CriteriaBuilder cb, Root<VaDatiVacancy> vaDatiVacancy,
			int idPfPrincipalAzienda, Integer idPfPrincipal, boolean scadute, boolean falseDiretteTruePalesi) {
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions
				.add(filterVacancyByDirettePalesi(cb, vaDatiVacancy, idPfPrincipalAzienda, falseDiretteTruePalesi));
		// estriamo solo se flgido = true
		whereConditions.add(cb.equal(vaDatiVacancy.get(VaDatiVacancy_.flagIdo), Boolean.TRUE));
		if (falseDiretteTruePalesi) { // solo per la vacancy dove id_pf_principal_palese = idPfPrincipal in sessione
			whereConditions.add(filterVacancyByDFD(cb, vaDatiVacancy));
		}
		if (!scadute) {
			Predicate scadenzaPubblicazioneCheck = cb.or(
					cb.greaterThan(vaDatiVacancy.get(VaDatiVacancy_.dtScadenzaPubblicazione), new Date()),
					cb.isNull(vaDatiVacancy.get(VaDatiVacancy_.dtScadenzaPubblicazione)));
			whereConditions.add(scadenzaPubblicazioneCheck);
		}
		whereConditions.add(filterVacancyByNonEliminate(cb, vaDatiVacancy));
		Predicate lavOrPub = cb.or(filterVacancyByCodStatoPubblicato(cb, vaDatiVacancy),
				filterVacancyByCodStatoLavorazione(cb, vaDatiVacancy));
		whereConditions.add(lavOrPub);
		return whereConditions;
	}

	private List<VaDatiVacancyDTO> fillAndGetVacancyDTO(List<VaDatiVacancy> vacancies, Integer idPfPrincipal) {
		List<VaDatiVacancyDTO> vacanciesDTO = new ArrayList<VaDatiVacancyDTO>();
		for (VaDatiVacancy entity : vacancies) {
			VaDatiVacancyDTO dto = toDTO(entity);
			// se sto cercando le vacancy palesi significa che l'idAziendaInfo
			// per cui sto ricercando non e' quello dell'azienda proprietaria
			// della vacancy.
			dto.setProprietary(isProprietary(entity, idPfPrincipal));
			dto.setAziendaPrincipale(isAziendaPrincipale(entity, idPfPrincipal));

			Long candidatureCount = acCandidaturaHome.findCandidatureCountByVacancyId(entity.getIdVaDatiVacancy());
			dto.setNumCandidati(candidatureCount.intValue());

			vacanciesDTO.add(dto);
		}
		return vacanciesDTO;
	}

	/**
	 * Restituisce tutte le offerte di lavoro effettuate da un'azienda. Tramite il parametro "palese" si sceglie se
	 * recuperare le vacancy dirette oppure le vacancy indirette palesi.
	 * 
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda per cui si vogliono recuperare le vacancy
	 * @param scadute
	 *            true se si vogliono recuperare anche le vacancy scadute
	 * @param palese
	 *            true se si vogliono recuperare le vacancy indirete palesi, false se si vogliono recuperare le vacancy
	 *            dirette
	 * @return
	 */
	public List<VaDatiVacancyDTO> findFirstNRowVacanciesByIdPfPrincipalAzienda(int idPfPrincipalAzienda,
			Integer idPfPrincipal, boolean scadute, boolean falseDiretteTruePalesi, int firstNRowVacancies,
			boolean isRER) {
		log.debug("getting Vacancies from AziendaInfo: " + idPfPrincipalAzienda);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<VaDatiVacancy> cq = cb.createQuery(VaDatiVacancy.class);
		Root<VaDatiVacancy> vaDatiVacancy = cq.from(VaDatiVacancy.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		if (isRER) {
			whereConditions = vacancyFiltersListForRER(cb, vaDatiVacancy, idPfPrincipalAzienda, idPfPrincipal, scadute,
					falseDiretteTruePalesi);
		} else {
			whereConditions = vacancyFiltersList(cb, vaDatiVacancy, idPfPrincipalAzienda, idPfPrincipal, scadute,
					falseDiretteTruePalesi);
		}
		cq.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		cq.orderBy(cb.desc(vaDatiVacancy.get(VaDatiVacancy_.dtScadenzaPubblicazione)));
		TypedQuery<VaDatiVacancy> typedQuery = entityManager.createQuery(cq);
		// Nella lista delle offerte di lavoro, situtata nella Home, vengono visualizzate soltanto le prime n vacancy
		typedQuery.setMaxResults(firstNRowVacancies);
		List<VaDatiVacancy> vacancies = typedQuery.getResultList();
		List<VaDatiVacancyDTO> vacanciesDTO = fillAndGetVacancyDTO(vacancies, idPfPrincipal);
		return vacanciesDTO;
	}

	/**
	 * Restituisce tutte le offerte di lavoro effettuate da un'azienda. Tramite il parametro "palese" si sceglie se
	 * recuperare le vacancy dirette oppure le vacancy indirette palesi.
	 * 
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda per cui si vogliono recuperare le vacancy
	 * @param scadute
	 *            true se si vogliono recuperare anche le vacancy scadute
	 * @param palese
	 *            true se si vogliono recuperare le vacancy indirete palesi, false se si vogliono recuperare le vacancy
	 *            dirette
	 * @return
	 */
	public List<VaDatiVacancyDTO> findLazyVacanciesByIdPfPrincipalAzienda(int startingAt, int maxperPage,
			int idPfPrincipalAzienda, Integer idPfPrincipal, boolean scadute, boolean falseDiretteTruePalesi,
			boolean filterByDFD) {
		log.debug("getting Vacancies from AziendaInfo: " + idPfPrincipalAzienda);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<VaDatiVacancy> cq = cb.createQuery(VaDatiVacancy.class);
		Root<VaDatiVacancy> vaDatiVacancy = cq.from(VaDatiVacancy.class);
		List<Predicate> whereConditions = vacancyFiltersList(cb, vaDatiVacancy, idPfPrincipalAzienda, idPfPrincipal,
				scadute, falseDiretteTruePalesi);
		if (filterByDFD) {
			whereConditions.add(filterVacancyByDFD(cb, vaDatiVacancy));
		}
		cq.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		cq.orderBy(cb.desc(vaDatiVacancy.get(VaDatiVacancy_.dtScadenzaPubblicazione)));
		TypedQuery<VaDatiVacancy> typedQuery = entityManager.createQuery(cq);
		typedQuery.setFirstResult(startingAt).setMaxResults(maxperPage);
		List<VaDatiVacancy> vacancies = typedQuery.getResultList();
		List<VaDatiVacancyDTO> vacanciesDTO = fillAndGetVacancyDTO(vacancies, idPfPrincipal);
		return vacanciesDTO;
	}

	public Long countVacanciesByIdPfPrincipalAzienda(int idPfPrincipalAzienda, Integer idPfPrincipal, boolean scadute,
			boolean falseDiretteTruePalesi, boolean filterByDFD) {
		log.debug("getting Vacancies from AziendaInfo: " + idPfPrincipalAzienda);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<VaDatiVacancy> vaDatiVacancy = cq.from(VaDatiVacancy.class);
		cq.select(cb.count(vaDatiVacancy.get(VaDatiVacancy_.idVaDatiVacancy)));
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions = vacancyFiltersList(cb, vaDatiVacancy, idPfPrincipalAzienda, idPfPrincipal, scadute,
				falseDiretteTruePalesi);
		if (filterByDFD) {
			whereConditions.add(filterVacancyByDFD(cb, vaDatiVacancy));
		}
		cq.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> typedQuery = entityManager.createQuery(cq);
		return typedQuery.getSingleResult();
	}

	public List<OfferteMiniDTO> getListaOfferte(RicercaOfferteDTO param, Integer idPfPrincipalAzienda) {
		log.debug("getting Vacancies from AziendaInfo: " + idPfPrincipalAzienda);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<VaDatiVacancy> c = cb.createQuery(VaDatiVacancy.class);
		Root<VaDatiVacancy> f = c.from(VaDatiVacancy.class);
		Join<VaDatiVacancy, PfPrincipal> aziendaInfo = f.join(VaDatiVacancy_.pfPrincipal);
		Join<VaDatiVacancy, DeProvenienza> deProvVacancy = f.join(VaDatiVacancy_.deProvenienzaVacancy, JoinType.LEFT);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		whereConditions.add(cb.equal(aziendaInfo.get(PfPrincipal_.idPfPrincipal), idPfPrincipalAzienda));

		Integer num_anno;
		Integer num_Richiesta;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date nowDate = null;
		try {
			nowDate = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String numAnno = param.getAnno();
		if (StringUtils.isNotBlank(numAnno)) {
			num_anno = Integer.parseInt(numAnno);
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.numAnno), num_anno));
		}

		String stato = param.getStato();
		if (StringUtils.isNotBlank(stato)) {
			whereConditions
					.add(cb.equal(f.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.valueOf(stato)));
		}

		String numRichiesta = param.getNumeroRichiesta();
		if (StringUtils.isNotBlank(numRichiesta)) {
			num_Richiesta = Integer.parseInt(numRichiesta);
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.numRichiesta), num_Richiesta));
		}

		// String codProvenienza = param.getCodProvenienza();
		if (param.getDeProvenienza() != null) {
			String codProvenienza = param.getDeProvenienza().getCodProvenienza();
			whereConditions.add(cb.equal(deProvVacancy.get(DeProvenienza_.codProvenienza), codProvenienza));
		}

		String attivitaPrincipale = param.getAttivita();
		if (StringUtils.isNotBlank(attivitaPrincipale)) {
			attivitaPrincipale = attivitaPrincipale.trim().toUpperCase();
			whereConditions
					.add(cb.like(cb.upper(f.get(VaDatiVacancy_.attivitaPrincipale)), "%" + attivitaPrincipale + "%"));
		}

		if (param.getTipo().equals(TipoRicercaOfferte.NO)) {
			whereConditions.add(cb.lessThan(f.get(VaDatiVacancy_.dtScadenzaPubblicazione), new Date()));
		} else if (param.getTipo().equals(TipoRicercaOfferte.SI)) {
			{
				whereConditions.add(cb.greaterThanOrEqualTo(f.get(VaDatiVacancy_.dtScadenzaPubblicazione), nowDate));
			}
		} else if (param.getTipo().equals(TipoRicercaOfferte.LAV)) {
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.LAV));
		}

		else if (param.getTipo().equals(TipoRicercaOfferte.ARC)) {
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.ARC));
		}

		else if (param.getTipo().equals(TipoRicercaOfferte.PUB)) {
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.PUB));
		}

		if (UtilsBean.isStaticRER()) {
			String cfOrRagSocAzienda = param.getCfOrRagSocAzienda();
			if (StringUtils.isNotBlank(cfOrRagSocAzienda)) {
				cfOrRagSocAzienda = cfOrRagSocAzienda.trim().toUpperCase();
				Predicate cf = cb.like(cb.upper(f.get(VaDatiVacancy_.codiceFiscale)), "%" + cfOrRagSocAzienda + "%");
				Predicate ragsociale = cb.like(cb.upper(f.get(VaDatiVacancy_.ragioneSociale)),
						"%" + cfOrRagSocAzienda + "%");
				Predicate tot = cb.or(cf, ragsociale);
				whereConditions.add(tot);
			}

			String nomeOperatore = param.getNomeOperatore();
			if (StringUtils.isNotBlank(nomeOperatore)) {
				nomeOperatore = nomeOperatore.trim().toUpperCase();
				whereConditions.add(cb.like(cb.upper(f.get(VaDatiVacancy_.nomeOperatore)), "%" + nomeOperatore + "%"));
			}

			String cognomeOperatore = param.getCognomeOperatore();
			if (StringUtils.isNotBlank(cognomeOperatore)) {
				cognomeOperatore = cognomeOperatore.trim().toUpperCase();
				whereConditions
						.add(cb.like(cb.upper(f.get(VaDatiVacancy_.cognomeOperatore)), "%" + cognomeOperatore + "%"));
			}
		}

		c.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<VaDatiVacancy> typedQuery = entityManager.createQuery(c);

		List<VaDatiVacancy> lista = typedQuery.getResultList();
		List<OfferteMiniDTO> listaDTO = new ArrayList<OfferteMiniDTO>();

		for (VaDatiVacancy vaDatiVacancy : lista) {
			OfferteMiniDTO dto = new OfferteMiniDTO();
			dto.setRiferimento(vaDatiVacancy.getNumRichiesta() + " - " + vaDatiVacancy.getNumAnno() + " - "
					+ vaDatiVacancy.getDeProvenienzaVacancy().getDescrizione());
			dto.setAttivita(vaDatiVacancy.getAttivitaPrincipale());
			dto.setDtmIns(vaDatiVacancy.getDtmIns());
			dto.setFlgIdo(vaDatiVacancy.getFlagIdo());
			dto.setCodProvenienza(vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza());
			dto.setDtScadenzaPubblicazione(vaDatiVacancy.getDtScadenzaPubblicazione());
			dto.setIdVaDatiVacancy(vaDatiVacancy.getIdVaDatiVacancy());
			dto.setStatoVacancy(vaDatiVacancy.getCodStatoVacancyEnum());
			// SOLO per le vacancy "nuove" (da maggio 2017 per trento, nelle altre regioni chissà) :
			// Se la vacancy è proveniente da SIL, non è modificabile.
			dto.setNonModificabile(false);
			String operatore = "";
			if (UtilsBean.isStaticRER()) {
				dto.setCfOrRagSocAzienda(vaDatiVacancy.getRagioneSociale());
				if (StringUtils.isNotBlank(vaDatiVacancy.getCognomeOperatore())) {
					operatore = vaDatiVacancy.getCognomeOperatore();
				}
				if (StringUtils.isNotBlank(vaDatiVacancy.getNomeOperatore())) {
					operatore = StringUtils.isNotBlank(operatore) ? operatore + " " + vaDatiVacancy.getNomeOperatore()
							: vaDatiVacancy.getNomeOperatore();
				}
				dto.setOperatore(operatore);
			}

			if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vaDatiVacancy.getOpzTipoDecodifiche())
					&& vaDatiVacancy.getDeProvenienzaVacancy() != null) {
				String codProvenienzaSil = vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza();
				// La vacancy è proveniente da un SIL se il suo codProvenienzaSil è una stringa che inizia con 'SIL'
				if (codProvenienzaSil != null && codProvenienzaSil.length() >= 3
						&& "SIL".equals(codProvenienzaSil.substring(0, 3))) {
					dto.setNonModificabile(true);
				}
			}

			listaDTO.add(dto);
		}

		return listaDTO;
	}

	public List<OfferteMiniDTO> getListaOffertePalesi(RicercaOfferteDTO param, Integer idPfPrincipalAzienda) {
		log.debug("getting Vacancies from AziendaInfo: " + idPfPrincipalAzienda);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<VaDatiVacancy> c = cb.createQuery(VaDatiVacancy.class);
		Root<VaDatiVacancy> f = c.from(VaDatiVacancy.class);

		Join<VaDatiVacancy, DeProvenienza> deProvVacancy = f.join(VaDatiVacancy_.deProvenienzaVacancy, JoinType.LEFT);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(filterVacancyByDirettePalesi(cb, f, idPfPrincipalAzienda, true));

		whereConditions.add(filterVacancyByDFD(cb, f));

		Integer num_anno;
		Integer num_Richiesta;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date nowDate = null;
		try {
			nowDate = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String numAnno = param.getAnno();
		if (StringUtils.isNotBlank(numAnno)) {
			num_anno = Integer.parseInt(numAnno);
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.numAnno), num_anno));
		}

		String stato = param.getStato();
		if (StringUtils.isNotBlank(stato)) {
			whereConditions
					.add(cb.equal(f.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.valueOf(stato)));
		}

		String numRichiesta = param.getNumeroRichiesta();
		if (StringUtils.isNotBlank(numRichiesta)) {
			num_Richiesta = Integer.parseInt(numRichiesta);
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.numRichiesta), num_Richiesta));
		}

		// String codProvenienza = param.getCodProvenienza();
		// if (StringUtils.isNotBlank(codProvenienza)) {
		// whereConditions.add(cb.equal(deProvVacancy.get(DeProvenienza_.codProvenienza), codProvenienza));
		// }

		if (param.getDeProvenienza() != null) {
			String codProvenienza = param.getDeProvenienza().getCodProvenienza();
			whereConditions.add(cb.equal(deProvVacancy.get(DeProvenienza_.codProvenienza), codProvenienza));
		}

		String attivitaPrincipale = param.getAttivita();
		if (StringUtils.isNotBlank(attivitaPrincipale)) {
			attivitaPrincipale = attivitaPrincipale.trim().toUpperCase();
			whereConditions
					.add(cb.like(cb.upper(f.get(VaDatiVacancy_.attivitaPrincipale)), "%" + attivitaPrincipale + "%"));
		}

		if (param.getTipo().equals(TipoRicercaOfferte.NO)) {
			whereConditions.add(cb.lessThan(f.get(VaDatiVacancy_.dtScadenzaPubblicazione), new Date()));
		} else if (param.getTipo().equals(TipoRicercaOfferte.SI)) {
			{
				whereConditions.add(cb.greaterThanOrEqualTo(f.get(VaDatiVacancy_.dtScadenzaPubblicazione), nowDate));
			}
		} else if (param.getTipo().equals(TipoRicercaOfferte.LAV)) {
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.LAV));
		}

		else if (param.getTipo().equals(TipoRicercaOfferte.ARC)) {
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.ARC));
		}

		else if (param.getTipo().equals(TipoRicercaOfferte.PUB)) {
			whereConditions.add(cb.equal(f.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.PUB));
		}
		if (UtilsBean.isStaticRER()) {
			String cfOrRagSocAzienda = param.getCfOrRagSocAzienda();
			if (StringUtils.isNotBlank(cfOrRagSocAzienda)) {
				cfOrRagSocAzienda = cfOrRagSocAzienda.trim().toUpperCase();
				Predicate cf = cb.like(cb.upper(f.get(VaDatiVacancy_.codiceFiscale)), "%" + cfOrRagSocAzienda + "%");
				Predicate ragsociale = cb.like(cb.upper(f.get(VaDatiVacancy_.ragioneSociale)),
						"%" + cfOrRagSocAzienda + "%");
				Predicate tot = cb.or(cf, ragsociale);
				whereConditions.add(tot);
			}
			String nomeOperatore = param.getNomeOperatore();
			if (StringUtils.isNotBlank(nomeOperatore)) {
				nomeOperatore = nomeOperatore.trim().toUpperCase();
				whereConditions.add(cb.like(cb.upper(f.get(VaDatiVacancy_.nomeOperatore)), "%" + nomeOperatore + "%"));
			}

			String cognomeOperatore = param.getCognomeOperatore();
			if (StringUtils.isNotBlank(cognomeOperatore)) {
				cognomeOperatore = cognomeOperatore.trim().toUpperCase();
				whereConditions
						.add(cb.like(cb.upper(f.get(VaDatiVacancy_.cognomeOperatore)), "%" + cognomeOperatore + "%"));
			}

		}

		c.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<VaDatiVacancy> typedQuery = entityManager.createQuery(c);

		List<VaDatiVacancy> lista = typedQuery.getResultList();
		List<OfferteMiniDTO> listaDTO = new ArrayList<OfferteMiniDTO>();
		for (VaDatiVacancy vaDatiVacancy : lista) {
			OfferteMiniDTO dto = new OfferteMiniDTO();
			dto.setRiferimento(vaDatiVacancy.getNumRichiesta() + " - " + vaDatiVacancy.getNumAnno() + " - "
					+ vaDatiVacancy.getDeProvenienzaVacancy().getDescrizione());
			dto.setAttivita(vaDatiVacancy.getAttivitaPrincipale());
			dto.setDtmIns(vaDatiVacancy.getDtmIns());
			dto.setFlgIdo(vaDatiVacancy.getFlagIdo());
			dto.setCodProvenienza(vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza());
			dto.setDtScadenzaPubblicazione(vaDatiVacancy.getDtScadenzaPubblicazione());
			dto.setIdVaDatiVacancy(vaDatiVacancy.getIdVaDatiVacancy());
			dto.setStatoVacancy(vaDatiVacancy.getCodStatoVacancyEnum());
			// SOLO per le vacancy "nuove" (da maggio 2017 per trento, nelle altre regioni chissà) :
			// Se la vacancy è proveniente da SIL, non è modificabile.
			dto.setNonModificabile(false);
			String operatore = "";
			if (UtilsBean.isStaticRER()) {
				dto.setCfOrRagSocAzienda(vaDatiVacancy.getRagioneSociale());
				if (StringUtils.isNotBlank(vaDatiVacancy.getCognomeOperatore())) {
					operatore = vaDatiVacancy.getCognomeOperatore();
				}
				if (StringUtils.isNotBlank(vaDatiVacancy.getNomeOperatore())) {
					operatore = StringUtils.isNotBlank(operatore) ? operatore + " " + vaDatiVacancy.getNomeOperatore()
							: vaDatiVacancy.getNomeOperatore();
				}
				dto.setOperatore(operatore);
			}

			if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(vaDatiVacancy.getOpzTipoDecodifiche())
					&& vaDatiVacancy.getDeProvenienzaVacancy() != null) {
				String codProvenienzaSil = vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza();
				// La vacancy è proveniente da un SIL se il suo codProvenienzaSil è una stringa che inizia con 'SIL'
				if (codProvenienzaSil != null && codProvenienzaSil.length() >= 3
						&& "SIL".equals(codProvenienzaSil.substring(0, 3))) {
					dto.setNonModificabile(true);
				}
			}

			listaDTO.add(dto);
		}

		return listaDTO;
	}

	/**
	 * Restituisce true se il principal passato in input e' proprietario della vacancy. Un principal e' proprietario
	 * della vacancy se l'id_azienda_info corrispondente e' uguale ad id_azienda_info o ad id_azienda_info_palese
	 * collegati alla vacancy in va_dati_azienda_info.
	 * 
	 * @param vacancyId
	 * @param idPfPrincipal
	 * @return
	 */
	public Boolean isProprietary(Integer vacancyId, Integer idPfPrincipal) {
		// se vacancyId e' null significa che sto inserendo una nuova vacancy
		if (vacancyId == null) {
			return true;
		}
		VaDatiVacancy vaDatiVacancy = findById(vacancyId);

		return isProprietary(vaDatiVacancy, idPfPrincipal);
	}

	public Boolean isProprietary(VaDatiVacancy vaDatiVacancy, Integer idPrincipalIns) {
		// se vacancyId e' null significa che sto inserendo una nuova vacancy
		if (vaDatiVacancy == null) {
			return true;
		}
		Integer idPfPrincipalAzienda = vaDatiVacancy.getPfPrincipal().getIdPfPrincipal();
		PfPrincipal pfPrincipalAziendaPalese = vaDatiVacancy.getPfPrincipalPalese();
		Integer idAziendaInfoPaleseData = null;
		if (pfPrincipalAziendaPalese != null) {
			idAziendaInfoPaleseData = pfPrincipalAziendaPalese.getIdPfPrincipal();
		}

		PfPrincipal pfPrincipalIns = pfPrincipalHome.findById(idPrincipalIns);

		if (pfPrincipalIns.isAzienda()) {

			if (idPfPrincipalAzienda != null && idPrincipalIns != null) {
				if (idPfPrincipalAzienda.equals(idPrincipalIns)
						|| (idAziendaInfoPaleseData != null && idAziendaInfoPaleseData.equals(idPrincipalIns))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Restituisce true se l'azienda corrispondente all'idPfPrincipal passato e' collegata alla vacancy passata come
	 * azienda principale.
	 * 
	 * @param vaDatiVacancy
	 * @param idPfPrincipal
	 * @return
	 */
	public boolean isAziendaPrincipale(VaDatiVacancy vaDatiVacancy, Integer idPfPrincipal) {
		return vaDatiVacancy.getPfPrincipal().getIdPfPrincipal().equals(idPfPrincipal);
	}

	@Override
	public VaDatiVacancyDTO persistDTO(VaDatiVacancyDTO data, Integer idPrincipalIns) {
		if (isProprietary(data.getId(), idPrincipalIns)) {

			SvAziendaInfoDTO svdto = svAziendaInfoHome.findDTOByIdPfPrincipal(data.getIdPfPrincipalAzienda());

			if (svdto == null) {

				creaVetrinaMinimale(data, idPrincipalIns);
			}

			return super.persistDTO(data, idPrincipalIns);
		} else {
			throw new MyPortalException("vacancy.wrongPermission");
		}
	}

	@Override
	public void persist(VaDatiVacancy data) {
		Poi poi = poiHome.createOrUpdatePoi(data);

		data.setPoi(poi);
		super.persist(data);
	};

	@Override
	public VaDatiVacancy merge(VaDatiVacancy detachedInstance) {
		Poi poi = poiHome.createOrUpdatePoi(detachedInstance);

		detachedInstance.setPoi(poi);
		/*
		 * Se si tratta di una vacancy proveninete da SIL devo memorizzare il numero di riferimento corrente nei campi:
		 * num_anno_sil, num_richiesta_sil e cod_provenienza_vacancy_sil
		 */
		if (detachedInstance.isFromSIL()) {
			detachedInstance.setNumAnnoSIL(detachedInstance.getNumAnno());
			detachedInstance.setNumRichiestaSIL(detachedInstance.getNumRichiesta());
			detachedInstance.setDeProvenienzaVacancySIL(detachedInstance.getDeProvenienzaVacancy());

			// nuovo riferimento della vacancy
			Integer numAnno = Calendar.getInstance().get(Calendar.YEAR);
			detachedInstance.setNumAnno(numAnno);
			detachedInstance
					.setNumRichiesta(getNextNumRichiesta(numAnno, ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
			detachedInstance.setDeProvenienzaVacancy(
					deProvenienzaVacancyHome.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
		}

		return super.merge(detachedInstance);
	}

	/**
	 * Utilizzato SOLo per la rigenerazione dei POI
	 * 
	 * @param detachedInstance
	 * @return
	 */

	public VaDatiVacancy secureMerge(VaDatiVacancy detachedInstance) {
		Poi poi = poiHome.createOrUpdatePoi(detachedInstance);
		detachedInstance.setPoi(poi);
		return super.merge(detachedInstance);
	}

	@Override
	public VaDatiVacancyDTO mergeDTO(VaDatiVacancyDTO data, Integer idPrincipalIns) {
		if (isProprietary(data.getId(), idPrincipalIns)) {
			return super.mergeDTO(data, idPrincipalIns);
		} else {
			throw new MyPortalException("vacancy.wrongPermission");
		}
	}

	@Override
	public VaDatiVacancyDTO toDTO(VaDatiVacancy entity) {
		if (entity == null)
			return null;
		VaDatiVacancyDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaDatiVacancy());
		dto.setAnno(entity.getNumAnno());
		dto.setNumeroRichiesta(entity.getNumRichiesta());
		DeProvenienza deProvenienzaVacancy = entity.getDeProvenienzaVacancy();
		dto.setDeProvenienzaVacancyDTO(deProvenienzaVacancyHome.toDTO(deProvenienzaVacancy));

		// numero di riferimento su SIL (se presente)
		dto.setAnnoSIL(entity.getNumAnnoSIL());
		dto.setNumeroRichiestaSIL(entity.getNumRichiestaSIL());
		DeProvenienza deProvenienzaVacancySIL = entity.getDeProvenienzaVacancySIL();
		if (deProvenienzaVacancySIL != null) {
			dto.setDeProvenienzaVacancySILDTO(deProvenienzaVacancyHome.toDTO(deProvenienzaVacancySIL));
		}

		dto.setAttivitaPrincipale(entity.getAttivitaPrincipale());
		dto.setDescrizione(entity.getAttivitaDescrizioneEstesa());
		DeMansione deMansione = entity.getDeMansione();
		dto.setCodMansione(deMansione.getCodMansione());
		dto.setStrMansione(deMansione.getDescrizione());
		if (entity.getDeAttivita() != null) {
			DeAttivita deAttivita = entity.getDeAttivita();
			dto.setCodAteco(deAttivita.getCodAteco());
			dto.setStrAteco(deAttivita.getDescrizione());
		}
		dto.setNonModificabile(entity.isNonModificabile());
		dto.setNoteAbilitazioni(entity.getNoteAbilitazioni());
		dto.setDataPubblicazione(entity.getDtPubblicazione());
		dto.setScadenzaPubblicazione(entity.getDtScadenzaPubblicazione());
		dto.setNumeroLavoratoriRicercati(entity.getNumLavRicercati());
		dto.setAnonima(entity.getFlagAnonima());
		DeTrasferta deTrasferta = entity.getDeTrasferta();
		if (deTrasferta != null) {
			dto.setCodTrasferta(deTrasferta.getCodTrasferta());
			dto.setStrTrasferta(deTrasferta.getDescrizione());
		}
		dto.setPubblicabile(entity.getOpzPubblicabile());
		String esperienzaRichiesta = StringUtils.defaultString(entity.getOpzEsperienze(),
				ConstantsSingleton.INDIFFERENTE);
		dto.setEsperienzaRichiesta(esperienzaRichiesta);

		VaContatto contattoPrinc = entity.getVaContattoPrinc();
		if (contattoPrinc != null) {
			dto.setIdVaContattoPrinc(contattoPrinc.getIdVaContatto());
		}
		VaContatto contattoAlt = entity.getVaContattoAlt();
		if (contattoAlt != null) {
			dto.setIdVaContattoAlt(contattoAlt.getIdVaContatto());
		}
		dto.setDataAggiornamento(entity.getDtmMod());

		dto.setFlagInvioCl(entity.getFlagInvioCl());
		dto.setSincronizzataCliclavoro(entity.getFlagInvioCl());
		dto.setDtScadenza(entity.getDtScadenza());
		dto.setVisibilita(entity.getVisibilita());

		dto.setMotivoChiusura(motivoChiusuraHome.toDTO(entity.getMotivoChiusura()));
		if (entity.getAttivitaMin() != null) {
			dto.setAttivitaMin(deAttivitaMinHome.toDTO(entity.getAttivitaMin()));
		} else {
			dto.setAttivitaMin(new DeAttivitaMinDTO());
		}
		if (entity.getMansioneMin() != null) {
			dto.setMansioneMin(deMansioneMinHome.toDTO(entity.getMansioneMin()));
		} else {
			dto.setMansioneMin(new DeMansioneMinDTO());
		}

		dto.setIntermediario(entity.getIntermediario());
		dto.setDenominazioneIntermediario(entity.getDenominazioneIntermediario());
		dto.setFlagEliminata(entity.getFlagEliminata());

		dto.setFromMyPortal(entity.isFromMyPortal());
		dto.setFromSIL(entity.isFromSIL());
		dto.setFromClicLavoro(entity.isFromClicLavoro());
		dto.setOriginariaSIL(entity.isOriginariaSIL());
		dto.setIntermediata(entity.isIntermediata());
		dto.setCodComunicazioneSil(entity.getCodComunicazioneSil());

		dto.setIdPfPrincipalAzienda(entity.getPfPrincipal().getIdPfPrincipal());

		// AziendaInfoPalese
		PfPrincipal aziendaInfoPalese = entity.getPfPrincipalPalese();
		if (aziendaInfoPalese != null) {
			dto.setIdPfPrincipalAziendaPalese(aziendaInfoPalese.getIdPfPrincipal());
		}

		dto.setIndirizzoLavoro(entity.getIndirizzoLavoro());
		dto.setCodComLavoro(entity.getDeComune().getCodCom());
		dto.setStrComLavoro(deComuneHome.toDTO(entity.getDeComune()).getDescrizione());

		Poi poi = entity.getPoi();
		if (poi != null) {
			dto.setIdPoi(poi.getIdPoi());
		}

		dto.setRagioneSociale(ottieniRagioneSociale(dto.getIdPfPrincipalAzienda(), dto.getIdPfPrincipalAziendaPalese(),
				dto.getAnonima()));

		dto.setOpzNullaOsta(entity.getOpzNullaOsta());
		dto.setNotaOperatore(entity.getNotaOperatore());
		dto.setOpzTipoDecodifiche(entity.getOpzTipoDecodifiche());
		dto.setFlgPacchettoCresco(entity.getFlgPacchettoCresco());
		dto.setFlgCandidatura(entity.getFlgCandidatura());
		dto.setCodStatoVacancyEnum(entity.getCodStatoVacancyEnum());
		if (entity.getFlagIdo() == null) {
			if (UtilsBean.isStaticRER()) {
				entity.setFlagIdo(Boolean.TRUE);
			} else {
				entity.setFlagIdo(Boolean.FALSE);
			}
		}
		dto.setFlgIdo(entity.getFlagIdo());
		if (entity.getDeBpMansione() != null) {
			dto.setCodMansioneIstat(entity.getDeBpMansione().getCodMansione());
			dto.setDescrCodMansioneIstat(entity.getDeBpMansione().getDescrizione());
		}
		dto.setAziendaRagSociale(entity.isRagioneSocAziendaPalese());

		return dto;
	}

	@Override
	public VaDatiVacancy fromDTO(VaDatiVacancyDTO dto) {
		if (dto == null)
			return null;
		VaDatiVacancy entity = super.fromDTO(dto);

		/*
		 * genero un nuovo numero di riferimento per la vacancy se non e' gia' presetne
		 */
		Integer numAnno = dto.getAnno();
		Integer numRichiesta = dto.getNumeroRichiesta();
		DeProvenienzaDTO deProvenienzaVacancyDTO = dto.getDeProvenienzaVacancyDTO();
		if (numAnno == null && numRichiesta == null && deProvenienzaVacancyDTO == null) {
			deProvenienzaVacancyDTO = deProvenienzaVacancyHome
					.findDTOById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL);
			numAnno = Calendar.getInstance().get(Calendar.YEAR);
			numRichiesta = getNextNumRichiesta(numAnno, deProvenienzaVacancyDTO.getId());
		}
		entity.setNumAnno(numAnno);
		entity.setNumRichiesta(numRichiesta);
		entity.setDeProvenienzaVacancy(deProvenienzaVacancyHome.fromDTO(deProvenienzaVacancyDTO));
		entity.setNoteAbilitazioni(dto.getNoteAbilitazioni());
		// numero di riferimento su SIL (se presente)
		entity.setNumAnnoSIL(dto.getAnnoSIL());
		entity.setNumRichiestaSIL(dto.getNumeroRichiestaSIL());
		DeProvenienzaDTO deProvenienzaVacancySIL = dto.getDeProvenienzaVacancySILDTO();
		if (deProvenienzaVacancySIL != null) {
			entity.setDeProvenienzaVacancySIL(deProvenienzaVacancyHome.fromDTO(deProvenienzaVacancySIL));
		}

		entity.setAttivitaPrincipale(dto.getAttivitaPrincipale());
		entity.setAttivitaDescrizioneEstesa(dto.getDescrizione());
		entity.setDeMansione(deMansioneHome.findById(dto.getCodMansione()));
		if (dto.getCodAteco() != null) {
			entity.setDeAttivita(deAttivitaHome.findById(dto.getCodAteco()));
		} else {
			entity.setDeAttivita(null);
		}
		entity.setDtPubblicazione(dto.getDataPubblicazione());
		entity.setDtScadenzaPubblicazione(dto.getScadenzaPubblicazione());
		entity.setNumLavRicercati(dto.getNumeroLavoratoriRicercati());
		entity.setFlagAnonima(dto.getAnonima() == null ? false : dto.getAnonima());
		String codTrasferta = dto.getCodTrasferta();
		if (codTrasferta != null) {
			entity.setDeTrasferta(deTrasfertaHome.findById(codTrasferta));
		}
		entity.setOpzPubblicabile(dto.getPubblicabile());
		String esperienzaRichiesta = dto.getEsperienzaRichiesta();
		if (ConstantsSingleton.INDIFFERENTE.equalsIgnoreCase(esperienzaRichiesta)) {
			esperienzaRichiesta = null;
		}
		entity.setOpzEsperienze(esperienzaRichiesta);

		entity.setIdVaDatiVacancy(dto.getId());

		Integer idVaContattoPrinc = dto.getIdVaContattoPrinc();
		if (idVaContattoPrinc != null) {
			entity.setVaContattoPrinc(vaContattoHome.findById(idVaContattoPrinc));
		}
		Integer idVaContattoAlt = dto.getIdVaContattoAlt();
		if (idVaContattoAlt != null) {
			entity.setVaContattoAlt(vaContattoHome.findById(idVaContattoAlt));
		}

		entity.setFlagInvioCl(dto.getFlagInvioCl());
		entity.setDtScadenza(dto.getDtScadenza());
		entity.setVisibilita(dto.getVisibilita());

		DeMotivoChiusuraDTO motivoChiusura = dto.getMotivoChiusura();
		if (motivoChiusura == null || motivoChiusura.getId() == null) {

		} else {
			entity.setMotivoChiusura(motivoChiusuraHome.findById(dto.getMotivoChiusura().getId()));
		}
		DeAttivitaMinDTO deAttivitaMinDTO = dto.getAttivitaMin();
		if (deAttivitaMinDTO != null) {
			String codDeAttivitaMin = deAttivitaMinDTO.getId();
			if (codDeAttivitaMin != null && !codDeAttivitaMin.isEmpty()) {
				entity.setAttivitaMin(deAttivitaMinHome.findById(dto.getAttivitaMin().getId()));
			} else {
				entity.setAttivitaMin(null);
			}
		}
		DeMansioneMinDTO deMansioneMinDTO = dto.getMansioneMin();
		if (deMansioneMinDTO != null) {
			String codDeMansioneMin = deMansioneMinDTO.getId();
			if (codDeMansioneMin != null && !codDeMansioneMin.isEmpty()) {
				entity.setMansioneMin(deMansioneMinHome.findById(dto.getMansioneMin().getId()));
			} else {
				entity.setMansioneMin(null);
			}
		}

		entity.setIntermediario(dto.getIntermediario());
		entity.setDenominazioneIntermediario(dto.getDenominazioneIntermediario());
		entity.setFlagEliminata(dto.getFlagEliminata());
		entity.setCodComunicazioneSil(dto.getCodComunicazioneSil());

		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipalAzienda()));

		// AziendaInfoPalese
		Integer idAziendaInfoPalese = dto.getIdPfPrincipalAziendaPalese();
		if (idAziendaInfoPalese != null) {
			entity.setPfPrincipalPalese(pfPrincipalHome.findById(idAziendaInfoPalese));
		}

		entity.setIndirizzoLavoro(dto.getIndirizzoLavoro());
		entity.setDeComune(deComuneHome.findById(dto.getCodComLavoro()));

		Integer idPoi = dto.getIdPoi();
		if (idPoi != null) {
			entity.setPoi(poiHome.findById(dto.getIdPoi()));
		}

		entity.setOpzNullaOsta(dto.getOpzNullaOsta());
		entity.setNotaOperatore(dto.getNotaOperatore());
		entity.setOpzTipoDecodifiche(dto.getOpzTipoDecodifiche());
		entity.setFlgPacchettoCresco(dto.getFlgPacchettoCresco());
		entity.setFlgCandidatura(dto.getFlgCandidatura());
		entity.setCodStatoVacancyEnum(dto.getCodStatoVacancyEnum());

		return entity;
	}

	public synchronized Integer getNextNumRichiesta(int numAnno, String codProvenienzaVacancy) {
		Integer maxNumRichiesta = getMaxNumRichiesta(numAnno, codProvenienzaVacancy);
		return maxNumRichiesta + 1;
	}

	private Integer getMaxNumRichiesta(int numAnno, String codProvenienzaVacancy) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> c = cb.createQuery(Integer.class);
		Root<VaDatiVacancy> f = c.from(VaDatiVacancy.class);
		Join<VaDatiVacancy, DeProvenienza> va2DeProv = f.join(VaDatiVacancy_.deProvenienzaVacancy);
		Predicate equalPredicateCodProv = cb.equal(va2DeProv.get(DeProvenienza_.codProvenienza), codProvenienzaVacancy);
		Predicate equalPredicateNumAnno = cb.equal(f.get(VaDatiVacancy_.numAnno), numAnno);
		Path<Integer> path = f.get(VaDatiVacancy_.numRichiesta);
		Expression<Integer> obj = cb.max(path);
		c = c.select(obj);
		c = c.where(equalPredicateCodProv, equalPredicateNumAnno);
		TypedQuery<Integer> typedQuery = entityManager.createQuery(c);
		Integer singleResult = typedQuery.getSingleResult();

		// e' il primo numRichiesta
		if (singleResult == null) {
			singleResult = 0;
		}

		return singleResult;
	}

	public void deleteVacancy(Integer idVaDatiVacancy, Integer idPfPrincipalMod) {
		VaVacancyCl vaVacancyCl = vaVacancyClHome.findById(idVaDatiVacancy);

		if (vaVacancyCl != null) {
			if (vaVacancyClHome.isChiusuraInviata(vaVacancyCl) || vaVacancyClHome.isPrimoInvioNonInviato(vaVacancyCl)) {
				/*
				 * deve ancora essere inviata la comunicazione di sincronizzazione del CV con cliclavoro, oppure ne e'
				 * gia' stata comunicata la chiusura con successo, quindi posso eliminare il CV
				 */
				removeCascadeById(idVaDatiVacancy, idPfPrincipalMod);
			} else {
				/*
				 * il CV e' sincronizzato con cliclavoro, setto il flag_eliminato e invio la chiusura della
				 * sincronizzazione con cliclavoro
				 */
				VaDatiVacancy vaDatiVacancy = findById(idVaDatiVacancy);
				vaDatiVacancy.setFlagEliminata(true);
				if (!vaVacancyClHome.isChiusuraNonInviata(vaVacancyCl)) {
					/*
					 * solo se i dati di invio a cliclavoro sono in uno stato diverso da CA o CE invio una comunicazione
					 * di chiusura, altrimenti significa che l'ho gia' fatto
					 */
					vaVacancyClHome.updateVacancy(idPfPrincipalMod, idVaDatiVacancy, vaVacancyCl,
							ConstantsSingleton.AzioneCliclavoro.CHIUSURA);
				}
				merge(vaDatiVacancy);

				// elimino la vacancy anche dagli indici SOLR
				Integer id = vaDatiVacancy.getIdVaDatiVacancy();
				deleteVacancySolr(id);
			}
		} else {
			/*
			 * il CV non e' sincronizzato con cliclavoro quindi lo posso eliminare direttamente
			 */
			removeCascadeById(idVaDatiVacancy, idPfPrincipalMod);
		}
	}

	@Override
	public void removeCascadeById(Integer id, Integer idPfPrincipalMod) {
		VaDatiVacancy entity = findById(id);

		/* elimino i riferimenti in va_abilita_vis_contatto_az */
		entity.setContactInfoViewableBy(null);
		entityManager.merge(entity);

		VaAltreInfo info = entity.getVaAltreInfo();
		if (info != null)
			vaAltreInfoHome.remove(info);

		List<VaAlbo> albos = entity.getVaAlbos();
		for (VaAlbo vaAlbo : albos) {
			vaAlboHome.remove(vaAlbo);
		}
		Set<VaAgevolazione> agevolaziones = entity.getVaAgevolaziones();
		for (VaAgevolazione vaAgevolazione : agevolaziones) {
			vaAgevolazioneHome.remove(vaAgevolazione);
		}
		VaCompetenzeTrasv compTrasv = entity.getVaCompetenzeTrasv();
		if (compTrasv != null)
			vaCompetenzeTrasvHome.remove(compTrasv);

		VaInformatica informatica = entity.getVaInformatica();
		if (informatica != null)
			vaInformaticaHome.remove(informatica);

		List<VaContratto> contrattos = entity.getVaContrattos();
		for (VaContratto vaContratto : contrattos) {
			vaContrattoHome.remove(vaContratto);
		}

		Set<VaIstruzione> istruziones = entity.getVaIstruziones();
		for (VaIstruzione vaIstruzione : istruziones) {
			vaIstruzioneHome.remove(vaIstruzione);
		}
		Set<VaLingua> linguas = entity.getVaLinguas();
		for (VaLingua vaLingua : linguas) {
			vaLinguaHome.remove(vaLingua);
		}
		Set<VaOrario> orarios = entity.getVaOrarios();
		for (VaOrario vaOrario : orarios) {
			vaOrarioHome.remove(vaOrario);
		}
		Set<VaPatente> patentes = entity.getVaPatentes();
		for (VaPatente vaPatente : patentes) {
			vaPatenteHome.remove(vaPatente);
		}
		Set<VaPatentino> patentinos = entity.getVaPatentinos();
		for (VaPatentino vaPatentino : patentinos) {
			vaPatentinoHome.remove(vaPatentino);
		}
		Set<VaTurno> turnos = entity.getVaTurnos();
		for (VaTurno vaTurno : turnos) {
			vaTurnoHome.remove(vaTurno);
		}
		VaRetribuzione vaRetribuzione = entity.getVaRetribuzione();
		if (vaRetribuzione != null)
			vaRetribuzioneHome.remove(vaRetribuzione);

		Set<AcCandidatura> candidature = entity.getAcCandidaturas();
		for (AcCandidatura acCandidatura : candidature) {
			acCandidaturaHome.remove(acCandidatura);
		}

		/* elimino i dati di invio a cliclavoro, se presenti */
		VaVacancyCl vaVacancyCl = vaVacancyClHome.findById(id);
		if (vaVacancyCl != null) {
			vaVacancyClHome.remove(vaVacancyCl);
		}

		List<VaPubblicazione> pubblicazioneList = vaPubblicazioneHome.findVaPubblicazioneByVaDatiVacancy(id);
		for (VaPubblicazione vaPubblicazione : pubblicazioneList) {
			vaPubblicazioneHome.removeById(vaPubblicazione.getIdVaPubblicazione(), idPfPrincipalMod);
		}

		VaEsperienze vaEsperienze = vaEsperienzeHome.findByVacancyId(id);
		if (vaEsperienze != null)
			vaEsperienzeHome.removeById(vaEsperienze.getIdVaEsperienze(), idPfPrincipalMod);

		VaContatto contattoPrinc = entity.getVaContattoPrinc();
		if (contattoPrinc != null)
			vaContattoHome.remove(contattoPrinc);

		VaContatto contattoAlt = entity.getVaContattoAlt();
		if (contattoAlt != null)
			vaContattoHome.remove(contattoAlt);

		removeById(id, idPfPrincipalMod);

		entityManager.flush();

		// elimino la vacancy anche dagli indici SOLR
		// non mi va di toccarla, ma e` sbagliata qua NDpegoraro
		deleteVacancySolr(id);
	}

	public List<VaDatiVacancyDTO> findDTOByCurriculumId(int curriculumId) {
		CvDatiPersonali curriculum = cvDatiPersonaliHome.findById(curriculumId);
		if (curriculum == null) {
			return null;
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<VaDatiVacancy> query = cb.createQuery(VaDatiVacancy.class);
		query.distinct(true);
		Root<VaDatiVacancy> root = query.from(VaDatiVacancy.class);
		Set<CvLingua> cvLinguas = curriculum.getCvLinguas();
		Set<CvIstruzione> cvIstruziones = curriculum.getCvIstruziones();
		Set<CvPatente> cvPatentes = curriculum.getCvPatentes();
		Set<CvPatentino> cvPatentinos = curriculum.getCvPatentinos();
		Set<CvAlbo> cvAlbos = curriculum.getCvAlbos();
		Set<CvEsperienzeProf> cvEsperienzeProfs = curriculum.getCvEsperienzeProfs();

		List<Predicate> whereConditions = getListPredicateVacancyVsCurriculumMatch(root, cvLinguas, cvIstruziones,
				cvPatentes, cvPatentinos, cvAlbos, cvEsperienzeProfs, cb);

		Predicate dataScadenzaFutura = cb.greaterThan(root.get(VaDatiVacancy_.dtScadenzaPubblicazione),
				DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		Predicate filtersByCv = cb.or(whereConditions.toArray(new Predicate[whereConditions.size()]));
		query.where(filtersByCv, dataScadenzaFutura);

		Order order = cb.desc(root.get(VaDatiVacancy_.dtPubblicazione));
		query = query.orderBy(order);
		TypedQuery<VaDatiVacancy> q = entityManager.createQuery(query);
		q.setMaxResults(100);
		// q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<VaDatiVacancy> list = q.getResultList();
		List<VaDatiVacancyDTO> listDto = new ArrayList<VaDatiVacancyDTO>();
		if (list != null)
			for (VaDatiVacancy vaDatiVacancy : list) {
				listDto.add(toDTO(vaDatiVacancy));
			}

		return listDto;
	}

	public long countVacancyCompatibiliByCurriculumId(int curriculumId) {
		CvDatiPersonali curriculum = cvDatiPersonaliHome.findById(curriculumId);
		if (curriculum == null) {
			return 0;
		}
		Set<CvLingua> cvLinguas = curriculum.getCvLinguas();
		Set<CvIstruzione> cvIstruziones = curriculum.getCvIstruziones();
		Set<CvPatente> cvPatentes = curriculum.getCvPatentes();
		Set<CvPatentino> cvPatentinos = curriculum.getCvPatentinos();
		Set<CvAlbo> cvAlbos = curriculum.getCvAlbos();
		Set<CvEsperienzeProf> cvEsperienzeProfs = curriculum.getCvEsperienzeProfs();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		// CriteriaQuery<VaDatiVacancy> query =
		// cb.createQuery(VaDatiVacancy.class);
		// query.distinct(true);
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		Root<VaDatiVacancy> root;

		root = query.from(VaDatiVacancy.class);
		List<Predicate> whereConditions = getListPredicateVacancyVsCurriculumMatch(root, cvLinguas, cvIstruziones,
				cvPatentes, cvPatentinos, cvAlbos, cvEsperienzeProfs, cb);

		Predicate filtersByCv = cb.or(whereConditions.toArray(new Predicate[whereConditions.size()]));
		Predicate dataScadenzaFutura = cb.greaterThan(root.get(VaDatiVacancy_.dtScadenzaPubblicazione),
				DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		query.where(filtersByCv, dataScadenzaFutura);
		// query.where(cb.greaterThan(root.get(VaDatiVacancy_.dtScadenzaPubblicazione),
		// DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
		query = query.select(cb.countDistinct(root));

		TypedQuery<Long> q = entityManager.createQuery(query);
		Long singleResult = q.getSingleResult();
		long count = singleResult.longValue();

		return count;
	}

	private List<Predicate> getListPredicateVacancyVsCurriculumMatch(Root<VaDatiVacancy> root, Set<CvLingua> cvLinguas,
			Set<CvIstruzione> cvIstruziones, Set<CvPatente> cvPatentes, Set<CvPatentino> cvPatentinos,
			Set<CvAlbo> cvAlbos, Set<CvEsperienzeProf> cvEsperienzeProfs, CriteriaBuilder cb) {
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		In<DeLingua> inLingua = getInPredicateLingua(root, cvLinguas, cb);
		if (inLingua != null) {
			whereConditions.add(inLingua);
		}

		In<DeTitolo> inIstruzione = getInPredicateIstruzione(root, cvIstruziones, cb);
		if (inIstruzione != null) {
			whereConditions.add(inIstruzione);
		}
		if ((cvPatentes != null) && (!cvPatentes.isEmpty())) {
			Join<VaDatiVacancy, VaPatente> join = root.join(VaDatiVacancy_.vaPatentes);
			Path<DePatente> dePath = join.get(VaPatente_.dePatente);
			In<DePatente> inPredicate = cb.in(dePath);

			for (CvPatente cvPatente : cvPatentes) {
				inPredicate.value(cvPatente.getDePatente());
			}
			whereConditions.add(inPredicate);
		}

		if ((cvPatentinos != null) && (!cvPatentinos.isEmpty())) {
			Join<VaDatiVacancy, VaPatentino> join = root.join(VaDatiVacancy_.vaPatentinos);

			Path<DePatentino> dePath = join.get(VaPatentino_.dePatentino);
			In<DePatentino> inPredicate = cb.in(dePath);

			for (CvPatentino cvPatentino : cvPatentinos) {
				inPredicate.value(cvPatentino.getDePatentino());
			}
			whereConditions.add(inPredicate);
		}

		if ((cvAlbos != null) && (!cvAlbos.isEmpty())) {
			Join<VaDatiVacancy, VaAlbo> join = root.join(VaDatiVacancy_.vaAlbos);

			Path<DeAlbo> dePath = join.get(VaAlbo_.deAlbo);
			In<DeAlbo> inPredicate = cb.in(dePath);

			for (CvAlbo cvAlbo : cvAlbos) {
				inPredicate.value(cvAlbo.getDeAlbo());
			}
			whereConditions.add(inPredicate);
		}

		if ((cvEsperienzeProfs != null) && (!cvEsperienzeProfs.isEmpty())) {
			Path<DeMansione> dePath = root.get(VaDatiVacancy_.deMansione);
			In<DeMansione> inPredicate = cb.in(dePath);

			for (CvEsperienzeProf cvEsperienzeProf : cvEsperienzeProfs) {
				inPredicate.value(cvEsperienzeProf.getDeMansione());
			}
			whereConditions.add(inPredicate);
		}
		return whereConditions;
	}

	private In<DeTitolo> getInPredicateIstruzione(Root<VaDatiVacancy> root, Set<CvIstruzione> cvIstruziones,
			CriteriaBuilder cb) {
		In<DeTitolo> inPredicate = null;
		if ((cvIstruziones != null) && (!cvIstruziones.isEmpty())) {
			Join<VaDatiVacancy, VaIstruzione> join = root.join(VaDatiVacancy_.vaIstruziones);
			Path<DeTitolo> dePath = join.get(VaIstruzione_.deTitolo);
			inPredicate = cb.in(dePath);

			for (CvIstruzione cvIstruzione : cvIstruziones) {
				inPredicate.value(cvIstruzione.getDeTitolo());
			}
		}
		return inPredicate;
	}

	private In<DeLingua> getInPredicateLingua(Root<VaDatiVacancy> root, Set<CvLingua> cvLinguas, CriteriaBuilder cb) {
		In<DeLingua> inPredicate = null;
		if ((cvLinguas != null) && (!cvLinguas.isEmpty())) {
			Join<VaDatiVacancy, VaLingua> join = root.join(VaDatiVacancy_.vaLinguas);
			Path<DeLingua> deLinguaPath = join.get(VaLingua_.deLingua);
			inPredicate = cb.in(deLinguaPath);
			for (CvLingua cvLingua : cvLinguas) {
				inPredicate.value(cvLingua.getDeLingua());
			}
		}
		return inPredicate;
	}

	/**
	 * Restituisce l'offerta di lavoro di un'azienda in base all'alternate key, se esiste.
	 * 
	 * @param numAnno
	 * @param numRichiesta
	 * @param codProvenienzaVacancy
	 * @return
	 */
	public VaDatiVacancy findVacancyByAlternateKey(int numAnno, int numRichiesta, String codProvenienzaVacancy) {
		log.debug("getting Vacancy from AlternateKey  numAnno:" + numAnno + ", numRichiesta:" + numRichiesta
				+ ", codProvenienzaVacancy:" + codProvenienzaVacancy);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<VaDatiVacancy> c = cb.createQuery(VaDatiVacancy.class);
		Root<VaDatiVacancy> f = c.from(VaDatiVacancy.class);

		Join<VaDatiVacancy, DeProvenienza> j = f.join(VaDatiVacancy_.deProvenienzaVacancy);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(cb.equal(f.get(VaDatiVacancy_.numAnno), numAnno));
		whereConditions.add(cb.equal(f.get(VaDatiVacancy_.numRichiesta), numRichiesta));
		whereConditions.add(cb.equal(j.get(DeProvenienza_.codProvenienza), codProvenienzaVacancy));

		c.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<VaDatiVacancy> typedQuery = entityManager.createQuery(c);
		List<VaDatiVacancy> res = typedQuery.getResultList();
		if (res == null) {
			return null;
		}
		if (res.isEmpty()) {
			return null;
		}
		VaDatiVacancy vaDatiVacancy = res.get(0);
		log.debug("Vacancy from AlternateKey:" + vaDatiVacancy + ", numVacancy:" + res.size());
		return vaDatiVacancy;
	}

	/**
	 * Restituisce l'offerta di lavoro di un'azienda in base al numero di riferimento SIL passato come parametro
	 * 
	 * @param numAnno
	 * @param numRichiesta
	 * @param codProvenienzaVacancy
	 * @return
	 */
	public VaDatiVacancy findVacancyModificataByRiferimentoSIL(Integer numAnnoSIL, Integer numRichiestaSIL,
			String codProvenienzaVacancySIL) {
		log.debug("getting Vacancy from riferimento SIL numAnnoSIL:" + numAnnoSIL + ", numRichiestaSIL:"
				+ numRichiestaSIL + ", codProvenienzaVacancySIL:" + codProvenienzaVacancySIL);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<VaDatiVacancy> c = cb.createQuery(VaDatiVacancy.class);
		Root<VaDatiVacancy> vaDatiVacancy = c.from(VaDatiVacancy.class);

		Join<VaDatiVacancy, DeProvenienza> deProvenienzaVacancy = vaDatiVacancy
				.join(VaDatiVacancy_.deProvenienzaVacancySIL);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(cb.equal(vaDatiVacancy.get(VaDatiVacancy_.numAnnoSIL), numAnnoSIL));
		whereConditions.add(cb.equal(vaDatiVacancy.get(VaDatiVacancy_.numRichiestaSIL), numRichiestaSIL));
		whereConditions
				.add(cb.equal(deProvenienzaVacancy.get(DeProvenienza_.codProvenienza), codProvenienzaVacancySIL));

		c.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<VaDatiVacancy> typedQuery = entityManager.createQuery(c);
		List<VaDatiVacancy> res = typedQuery.getResultList();
		if (res == null) {
			return null;
		}
		if (res.isEmpty()) {
			return null;
		}
		VaDatiVacancy result = res.get(0);
		log.debug("Vacancy from riferimento SIL: " + result + ", numVacancy: " + res.size());
		return result;
	}

	/**
	 * Restituisce true se la vacancy e' sincronizzata con cliclavoro, false altrimenti
	 * 
	 * @param idVaDatiVacancy
	 * @return
	 */
	public boolean isSincronizzatoClicLavoro(Integer idVaDatiVacancy) {
		VaDatiVacancy vaDatiVacancy = findById(idVaDatiVacancy);
		boolean result = vaDatiVacancy.getFlagInvioCl();

		return result;
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
	public boolean needsMergeCliclavoro(Integer idVaDatiVacancy, boolean flagInvioClPrecedente,
			boolean flagInvioClNuovo) {
		VaVacancyCl vaVacancyCl = vaVacancyClHome.findById(idVaDatiVacancy);
		String codStatoInvioCl = "";
		if (vaVacancyCl != null) {
			codStatoInvioCl = vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl();
		}

		boolean giaSincronizzato = flagInvioClPrecedente;
		boolean nuovaSincronizzazione = flagInvioClNuovo;
		boolean erroreChiusura = deStatoInvioClHome.isErroreChiusura(codStatoInvioCl);

		return giaSincronizzato || nuovaSincronizzazione || erroreChiusura;
	}

	/**
	 * Controlla che siano sempre presenti le invarianti di cliclavoro
	 * 
	 * @param idVaDatiVacancy
	 */
	public void checkErrorsClicLavoro(Integer idVaDatiVacancy) {
		VaDatiVacancy vaDatiVacancy = findById(idVaDatiVacancy);
		PfPrincipal aziendaInfo;
		if (vaDatiVacancy.getPfPrincipalPalese() != null) {
			aziendaInfo = vaDatiVacancy.getPfPrincipalPalese();
		} else {
			aziendaInfo = vaDatiVacancy.getPfPrincipal();
		}

		boolean error = false;
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append(
				"Attenzione: per poter attivare la trasmissione dell’Offerta di lavoro al sistema nazionale Cliclavoro è necessario procedere all’inserimento delle seguenti informazioni: <br/> ");
		errorMessage.append("<ul> ");

		String codiceFiscale = aziendaInfo.getAziendaInfo().getCodiceFiscale();
		if (codiceFiscale == null || codiceFiscale.isEmpty()) {
			error = true;
			errorMessage.append("<li>Codice fiscale nella sezione Profilo dell'azienda</li>");
		}

		DeAttivita settore = vaDatiVacancy.getDeAttivita();
		if (settore == null) {
			error = true;
			errorMessage.append("<li>Settore dell'offerta di lavoro</li>");
		}

		DeAttivitaMin attivitaATECO = vaDatiVacancy.getAttivitaMin();
		if (attivitaATECO == null) {
			error = true;
			errorMessage.append("<li>Attività ATECO dell'offerta di lavoro</li>");
		}

		String descrizioneAttivita = vaDatiVacancy.getAttivitaDescrizioneEstesa();
		if (descrizioneAttivita == null || descrizioneAttivita.isEmpty()) {
			error = true;
			errorMessage.append("<li>Descrizione attività dell'offerta di lavoro</li>");
		}

		DeMansioneMin qualificaRichiesta = vaDatiVacancy.getMansioneMin();
		if (qualificaRichiesta == null) {
			error = true;
			errorMessage.append("<li>Qualifica richiesta dell'offerta di lavoro</li>");
		}

		if (vaDatiVacancy.getOpzNullaOsta() == null || vaDatiVacancy.getOpzNullaOsta().isEmpty()) {
			error = true;
			errorMessage.append("<li>Nulla Osta nella sezione altre informazioni</li>");
		}

		Set<VaContratto> tipologieContratto = vaContrattoHome
				.findNonScaduteByVacancyId(vaDatiVacancy.getIdVaDatiVacancy());
		if (tipologieContratto == null || tipologieContratto.isEmpty()) {
			error = true;
			errorMessage.append("<li>una Tipologia di contratto non scaduta nella sezione rapporto di lavoro</li>");
		}

		if (!vaDatiVacancy.isOriginariaSIL() || (vaDatiVacancy.isOriginariaSIL()
				&& vaDatiVacancy.getPfPrincipalPalese() == null && !vaDatiVacancy.getVisibilita())) {
			VaContatto contattoPrincipale = vaDatiVacancy.getVaContattoPrinc();
			if (contattoPrincipale == null || contattoPrincipale.getMail() == null
					|| contattoPrincipale.getMail().isEmpty()) {
				error = true;
				errorMessage.append("<li>Indirizzo E-mail nella sezione Contatto (o Contatto alternativo)</li>");
			}
		}

		if (vaDatiVacancy.isOriginariaSIL() && vaDatiVacancy.getPfPrincipalPalese() != null
				&& vaDatiVacancy.getVisibilita()) {
			VaContatto contattoAlternativo = vaDatiVacancy.getVaContattoAlt();
			if (contattoAlternativo == null || contattoAlternativo.getMail() == null
					|| contattoAlternativo.getMail().isEmpty()) {
				error = true;
				errorMessage.append("<li>indirizzo e-mail nella sezione contatto alternativo</li>");
			}
		}

		errorMessage.append("</ul> ");

		if (error) {
			log.debug("Errori di validazione vacancy + " + idVaDatiVacancy + " per clic lavoro");
			List<String> warningMessages = new ArrayList<String>();
			warningMessages.add(errorMessage.toString().substring(0, errorMessage.length() - 2));
			throw new MyPortalException("Errore Clic Lavoro", true, warningMessages);
		}
	}

	public VaDatiVacancyDTO mergeClicLavoro(Integer idPfPrincipal, Integer idVaDatiVacancy) {
		if (idVaDatiVacancy == null) {
			// ERRORE
			throw new MyPortalException("vacancy.error_updating");
		}
		Boolean flagInvioCl = findById(idVaDatiVacancy).getFlagInvioCl();

		/*
		 * questo metodo viene chiamato dai bean delle tab, percio' il flag di invio a cliclavoro non puo' essere
		 * cambiato
		 */
		return mergeClicLavoro(idPfPrincipal, idVaDatiVacancy, flagInvioCl, flagInvioCl);
	}

	public VaDatiVacancyDTO mergeClicLavoro(Integer idPfPrincipal, Integer idVaDatiVacancy,
			Boolean flagInvioClPrecedente, Boolean flagInvioClNuovo) {
		if (idVaDatiVacancy == null) {
			throw new MyPortalException("vacancy.error_loading");
		}

		VaDatiVacancy vaDatiVacancy = findById(idVaDatiVacancy);
		VaVacancyCl vaVacancyCl = vaVacancyClHome.findById(idVaDatiVacancy);
		/*
		 * indica se e' la prima volta che il CV viene sincronizzato su cliclavoro
		 */
		boolean primaApertura;
		if (vaVacancyCl == null) {
			primaApertura = true;
		} else {
			primaApertura = false;
		}

		if (flagInvioClPrecedente || flagInvioClNuovo) {
			/*
			 * controllo dei vincoli per la sincronizzazione con cliclavoro, se falliscono viene lanciata un'eccezione e
			 * la transazione fa rollback
			 */
			checkErrorsClicLavoro(idVaDatiVacancy);
		}

		if (flagInvioClPrecedente.equals(flagInvioClNuovo)) {
			/* l'ambito di diffusione non e' cambiato */
			if (flagInvioClPrecedente) {
				/* sono sincronizzato con cliclavoro */
				/* mando una comunicazione di rettifica */
				vaVacancyCl = vaVacancyClHome.updateVacancy(idPfPrincipal, idVaDatiVacancy, vaVacancyCl,
						AzioneCliclavoro.MODIFICA_CV);
			} else {
				if (!primaApertura
						&& deStatoInvioClHome.isErroreChiusura(vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl())) {
					/* ripristino lo stato in CA */
					vaVacancyCl = vaVacancyClHome.updateVacancy(idPfPrincipal, idVaDatiVacancy, vaVacancyCl,
							AzioneCliclavoro.MODIFICA_CV);
				} else {
					/* non sono sincronizzato con cliclavoro, non faccio nulla */
					return toDTO(vaDatiVacancy);
				}
			}
		} else {
			/* l'ambito di diffusione e' cambiato */
			if (flagInvioClPrecedente) {
				/*
				 * sono sincronizzato con cliclavoro e devo chiudere la sincronizzazione con cliclavoro
				 */
				if (!primaApertura && deStatoInvioClHome
						.giaComunicatoCliclavoro(vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl())) {
					/* mando una comunicazione di chiusura */
					vaVacancyCl = vaVacancyClHome.updateVacancy(idPfPrincipal, idVaDatiVacancy, vaVacancyCl,
							AzioneCliclavoro.CHIUSURA);
				} else {
					/* non ho ancora comunicato l'apertura, la elimino da DB */
					vaVacancyClHome.remove(vaVacancyCl);
					vaVacancyCl = null;
				}
			} else {
				/* mi devo sincronizzare con cliclavoro */
				if (primaApertura) {
					/* mando una comunicazione di apertura */
					vaVacancyCl = vaVacancyClHome.updateVacancy(idPfPrincipal, idVaDatiVacancy, vaVacancyCl,
							AzioneCliclavoro.SINCRONIZZO);
				} else {
					if (vaVacancyClHome.isChiusuraInviata(vaVacancyCl)) {
						/* ripristino una comunicazione di apertura */
						vaVacancyCl = vaVacancyClHome.updateVacancy(idPfPrincipal, idVaDatiVacancy, vaVacancyCl,
								AzioneCliclavoro.SINCRONIZZO);
					} else {
						/* ripristino una comunicazione di rettifica */
						vaVacancyCl = vaVacancyClHome.updateVacancy(idPfPrincipal, idVaDatiVacancy, vaVacancyCl,
								AzioneCliclavoro.SINCRONIZZO);
					}
				}
			}

			/* aggiorno il flag di invio a CL della VA */
			vaDatiVacancy.setFlagInvioCl(flagInvioClNuovo);

			/*
			 * se non ho eliminato la riga dal DB procedo con l'inserimento o l'aggiornamento
			 */
			if (vaVacancyCl != null) {
				if (primaApertura) {
					vaVacancyClHome.persist(vaVacancyCl);
				} else {
					vaVacancyClHome.merge(vaVacancyCl);
				}
			}
		}
		return toDTO(vaDatiVacancy);
	}

	public CvFilterDTO findDatiRicerca(Integer vacancyId) {
		CvFilterDTO cvFilterDTO = new CvFilterDTO();

		VaDatiVacancy vaDatiVacancy = findById(vacancyId);

		// Setta DeComune
		cvFilterDTO.setComune(deComuneHome.toDTO(vaDatiVacancy.getDeComune()));
		// Setta VaLingua
		for (VaLingua lingua : vaDatiVacancy.getVaLinguas()) {
			cvFilterDTO.getLingue().add(deLinguaHome.toDTO(lingua.getDeLingua()));
		}
		// Setta deMansione (Gruppo professionale)
		cvFilterDTO.setGruppoProf(deMansioneHome.toDTO(vaDatiVacancy.getDeMansione()));
		// Setta DeTitolo
		for (VaIstruzione istruzione : vaDatiVacancy.getVaIstruziones()) {
			cvFilterDTO.getTitoli().add(deTitoloHome.toDTO(istruzione.getDeTitolo()));
		}

		if (vaDatiVacancy.getVaAltreInfo() != null) {
			String opzAutomunito = vaDatiVacancy.getVaAltreInfo().getOpzAutomunito();
			if (opzAutomunito == null || opzAutomunito.equalsIgnoreCase("N")) {
				cvFilterDTO.setAutomunito(false);
			} else {
				cvFilterDTO.setAutomunito(true);
			}

			String opzMotomunito = vaDatiVacancy.getVaAltreInfo().getOpzMotomunito();
			if (opzMotomunito == null || opzMotomunito.equalsIgnoreCase("N")) {
				cvFilterDTO.setMotomunito(false);
			} else {
				cvFilterDTO.setMotomunito(true);
			}

			String opzTrasferte = vaDatiVacancy.getVaAltreInfo().getOpzFuorisede();
			if (opzTrasferte == null || opzTrasferte.equalsIgnoreCase("N")) {
				cvFilterDTO.setTrasferte(false);
			} else {
				cvFilterDTO.setTrasferte(true);
			}

			if (UtilsBean.isStaticRER()) {
				String opzTrasferta = vaDatiVacancy.getVaAltreInfo().getOpzTrasferta();
				if (opzTrasferta == null || opzTrasferta.equalsIgnoreCase("N")) {
					cvFilterDTO.setTrasferte(false);
				} else {
					cvFilterDTO.setTrasferte(true);
				}

			}

		}

		return cvFilterDTO;
	}

	/**
	 * recupero la vacancy importato dal SIL associato al codice comunicazione passato
	 * 
	 * @param codComunicazioneSil
	 * @return
	 */
	public VaDatiVacancy findVacancyByCodComunicazioneSil(String codComunicazioneSil) {
		try {
			VaDatiVacancy vacancy = entityManager
					.createNamedQuery("findVacancyByCodComunicazioneSil", VaDatiVacancy.class)
					.setParameter("codComunicazioneSil", codComunicazioneSil).getSingleResult();
			if (vacancy != null)
				return vacancy;
			else
				return null;
		} catch (Exception e) {
			return null;
		}

	}

	public VaDatiVacancy findByCodComunicazione(String codiceVacancy) {
		List<VaDatiVacancy> vacancies = entityManager
				.createNamedQuery("findVaDatiVacancyByCodComunicazione", VaDatiVacancy.class)
				.setParameter("codComunicazione", codiceVacancy).getResultList();
		if (vacancies != null && !vacancies.isEmpty()) {
			return vacancies.get(0);
		} else {
			return null;
		}
	}

	/**
	 * cancella la vacancy da eliminare dall'indicizzazione di SOLR
	 * 
	 * @param idVaDatiVacancy
	 */
	public void deleteVacancySolr(Integer idVaDatiVacancy) {
		String baseDominio = ConstantsSingleton.getSolrUrl();
		String strParamUrl = URL.escapeSolr("/core0/update?stream.body=<delete><query>id_va_dati_vacancy:"
				+ idVaDatiVacancy + "</query></delete>&commit=true");
		log.info(baseDominio + strParamUrl + " INVOCATA ELIMINAZIONE VACANCY");
		Utils.documentSOLR(baseDominio + strParamUrl);
	}

	/**
	 * 
	 * Restituisce il DTO per la visualizzazione di una vacancy
	 * 
	 * @param vacancyId
	 *            id della vacancy che si vuole visualizzare
	 * @param idPfPrincipal
	 *            utente he vuole visualizzare la vacancy
	 * @return dto contenente i dati per la visualizzazione nella pagina
	 */
	public VaVisualizzaDTO findVisualizzaDTOById(Integer vacancyId, Integer idPfPrincipal) {
		VaVisualizzaDTO vaVisualizzaDTO = new VaVisualizzaDTO();

		vaVisualizzaDTO.setId(vacancyId);
		VaDatiVacancy vaDatiVacancy = findById(vacancyId);
		vaVisualizzaDTO.setVaDatiVacancyDTO(toDTO(vaDatiVacancy));
		vaVisualizzaDTO.getVaDatiVacancyDTO().setProprietary(isProprietary(vaDatiVacancy, idPfPrincipal));
		vaVisualizzaDTO.getVaDatiVacancyDTO().setAziendaPrincipale(isAziendaPrincipale(vaDatiVacancy, idPfPrincipal));
		vaVisualizzaDTO.setListaVaIstruzioneDTO(vaIstruzioneHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setListaVaLinguaDTO(vaLinguaHome.findDTOByVacancyIdOrdered(vacancyId));
		vaVisualizzaDTO.setVaInformaticaDTO(vaInformaticaHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setVaCompetenzeTrasvDTO(vaCompetenzeTrasvHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setVaContattoPrincDTO(vaContattoHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setVaContattoAltDTO(vaContattoHome.findAltDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setVaPubblicazioneDTO(vaPubblicazioneHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setVaEsperienzeDTO(vaEsperienzeHome.findDTOByVacancyId(vacancyId));
		VaAltreInfoDTO vaAltreInfo = vaAltreInfoHome.findDTOByVacancyId(vacancyId);
		if (vaDatiVacancy.getOpzNullaOsta() != null && !("").equalsIgnoreCase(vaDatiVacancy.getOpzNullaOsta())) {
			if (vaAltreInfo == null) {
				vaAltreInfo = new VaAltreInfoDTO();
			}
			vaAltreInfo.setOpzNullaOsta(vaDatiVacancy.getOpzNullaOsta());
		}
		vaVisualizzaDTO.setVaAltreInfoDTO(vaAltreInfo);

		vaVisualizzaDTO.setListaVaContrattoDTO(vaContrattoHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setListaVaTurnoDTO(vaTurnoHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setListaVaOrarioDTO(vaOrarioHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setVaRetribuzioneDTO(vaRetribuzioneHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setListaVaPatenteDTO(vaPatenteHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setListaVaPatentinoDTO(vaPatentinoHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setListaVaAlboDTO(vaAlboHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setListaVaAgevolazioneDTO(vaAgevolazioneHome.findDTOByVacancyId(vacancyId));
		vaVisualizzaDTO.setListAcCandidaturaDTO(acCandidaturaHome.findDtosByVacancyId(vacancyId));

		Integer idAziendaInfo = vaVisualizzaDTO.getVaDatiVacancyDTO().getIdPfPrincipalAzienda();
		Integer idDatiAziendaPalese = vaVisualizzaDTO.getVaDatiVacancyDTO().getIdPfPrincipalAziendaPalese();
		Boolean anonima = vaVisualizzaDTO.getVaDatiVacancyDTO().getAnonima();
		vaVisualizzaDTO.setRagioneSociale(ottieniRagioneSociale(idAziendaInfo, idDatiAziendaPalese, anonima));

		/*
		 * se esiste la vetrina assegno l'id, altrimenti rimane a null come assegnato precedentemente
		 */
		vaVisualizzaDTO.setIdSvAziendaInfo(ottieniVetrina(idAziendaInfo, idDatiAziendaPalese, anonima));

		// calcolo la visibilità della sezione contatti
		// se provengono da myportal o da SIL, sono sempre visibili
		if (vaDatiVacancy.isFromMyPortal() || vaDatiVacancy.isFromSIL()) {
			vaVisualizzaDTO.setMostraContattoPrincipale(true);
			vaVisualizzaDTO.setMostraContattoAlternativo(true);
		}
		// se provengono dal ministero devo avere un esito di contatto positivo
		// ed il flag visibilità a 'Y'
		else if (vaDatiVacancy.isFromClicLavoro()) {
			PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
			if (pfPrincipal.isUtente()) {
				// le info contatti sono visibili se all'utente è stato concesso
				// visualizzarle
				Set<PfPrincipal> set = vaDatiVacancy.getContactInfoViewableBy();
				boolean canView = set.contains(pfPrincipal);
				// se è intermediata mostro il contatto alternativo, altrimenti
				// quello principale
				// NOVELLA 21/12/2012 il contatto principale per una vacancy
				// visibile e intermedita
				// deve essere visualizzato sempre
				if (vaDatiVacancy.isIntermediata()) {
					vaVisualizzaDTO.setMostraContattoPrincipale(vaDatiVacancy.getVisibilita());
				} else {
					vaVisualizzaDTO.setMostraContattoPrincipale(canView && vaDatiVacancy.getVisibilita());
				}

				vaVisualizzaDTO.setMostraContattoAlternativo(
						canView && vaDatiVacancy.getVisibilita() && vaDatiVacancy.isIntermediata());
			} else if (pfPrincipal.isProvincia()) {
				vaVisualizzaDTO.setMostraContattoPrincipale(false);
				vaVisualizzaDTO.setMostraContattoAlternativo(false);
			} else {
				vaVisualizzaDTO.setMostraContattoPrincipale(true);
				vaVisualizzaDTO.setMostraContattoAlternativo(true);
			}
		}

		return vaVisualizzaDTO;
	}

	private String ottieniRagioneSociale(Integer idAziendaInfo, Integer idAziendaInfoPalese, Boolean anonima) {
		String ragioneSociale = "";

		if (idAziendaInfoPalese != null && !anonima) {
			AziendaInfo aziendaInfoPalese = aziendaInfoHome.findById(idAziendaInfoPalese);
			if (aziendaInfoPalese != null) {
				ragioneSociale = aziendaInfoPalese.getRagioneSociale();
			}

		} else {
			AziendaInfo aziendaInfo = aziendaInfoHome.findById(idAziendaInfo);
			if (aziendaInfo != null) {
				ragioneSociale = aziendaInfo.getRagioneSociale();
			}
		}

		return ragioneSociale;
	}

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	public Integer getIdPfPrincipalVetrinaAzienda(VaDatiVacancy vacancy) {
		Integer idPfPrincipalVetrinaAzienda = null;

		if (!vacancy.getFlagAnonima()) {
			PfPrincipal pfPrincipalAzienda = pfPrincipalHome.findByIdVaDatiVacancy(vacancy.getIdVaDatiVacancy());
			AziendaInfo aziendaInfo = aziendaInfoHome.findById(pfPrincipalAzienda.getIdPfPrincipal());
			if (aziendaInfo != null) {
				if (svAziendaInfoHome.exists(pfPrincipalAzienda.getIdPfPrincipal())) {
					idPfPrincipalVetrinaAzienda = pfPrincipalAzienda.getIdPfPrincipal();
				}
			}
		}

		return idPfPrincipalVetrinaAzienda;

	}

	private Integer ottieniVetrina(Integer idAziendaInfo, Integer idAziendaInfoPalese, Boolean anonima) {
		Integer idSvAziendaInfo = null;

		if (idAziendaInfoPalese != null && !anonima) {
			AziendaInfo aziendaInfoPalese = aziendaInfoHome.findById(idAziendaInfoPalese);
			if (aziendaInfoPalese != null) {
				if (svAziendaInfoHome.exists(idAziendaInfoPalese)) {
					idSvAziendaInfo = idAziendaInfoPalese;
				}
			}

		} else {
			AziendaInfo aziendaInfo = aziendaInfoHome.findById(idAziendaInfo);
			if (aziendaInfo != null) {
				if (svAziendaInfoHome.exists(idAziendaInfo)) {
					idSvAziendaInfo = idAziendaInfo;
				}
			}
		}

		return idSvAziendaInfo;
	}

	public VaDatiVacancy copyByIdNoDTO(Integer pfPrincipalId, Integer vacancyId, String descrizione) {
		Date now = new Date();

		/* viene generato un nuovo riferimento per la vacancy */
		Integer numAnno = Calendar.getInstance().get(Calendar.YEAR);
		DeProvenienza provenienzaMyPortal = deProvenienzaVacancyHome
				.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL);
		String provenienza = provenienzaMyPortal.getCodProvenienza();

		Integer numRichiesta = getNextNumRichiesta(numAnno, provenienza);
		VaDatiVacancy vaDatiVacancy = findById(vacancyId);

		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			vaDatiVacancy.setFlagNoDiscriminazione(false); // l'azienda dopo la copia è obbligata a scegliere
															// l'accettazione delle condizioni di servizio e questo vale
															// sempre sia se la provenienza è SIL sia se è MYPORTAL
			vaDatiVacancy.setMotivazione(null);
			vaDatiVacancy.setDtArchiviazione(null);
			vaDatiVacancy.setUtenteArchiviazione(null);
			if (vaDatiVacancy.getDeProvenienzaVacancy() != null) {
				if (vaDatiVacancy.getDeProvenienzaVacancy().getCodProvenienza()
						.startsWith(ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC)) {
					vaDatiVacancy.setCodiceFiscale(null);
					vaDatiVacancy.setRagioneSociale(null);
					vaDatiVacancy.setNomeOperatore(null);
					vaDatiVacancy.setCognomeOperatore(null);
					vaDatiVacancy.setNotaOperatore(null);
					vaDatiVacancy.setDeComuneAzienda(null);
				}
			}
		}

		if (descrizione.length() > 100) {
			throw new MyPortalException("vacancy.error_copying.longname");
		}
		if (vaDatiVacancy == null) {
			throw new EJBException("Impossibile trovare l'entity corrispondente a va_dati_vacancy con id " + vacancyId);
		}

		// eventualmente corregge errori.
		// attenzione, trasparente
		checkDatiCopia(vaDatiVacancy);

		VaInformatica vaInformatica = vaDatiVacancy.getVaInformatica();
		VaContatto vaContattoPrincipale = vaDatiVacancy.getVaContattoPrinc();
		VaContatto vaContattoAlternativo = vaDatiVacancy.getVaContattoAlt();
		VaAltreInfo vaAltreInfo = vaDatiVacancy.getVaAltreInfo();
		VaCompetenzeTrasv vaCompetenzeTrasv = vaDatiVacancy.getVaCompetenzeTrasv();
		VaRetribuzione vaRetribuzione = vaDatiVacancy.getVaRetribuzione();
		VaPubblicazione vaPubblicazione = vaDatiVacancy.getVaPubblicazione();
		VaEsperienze vaEsperienze = vaDatiVacancy.getVaEsperienze();
		Collection<VaLingua> vaLinguas = fetchCollection(vaDatiVacancy.getVaLinguas());
		Collection<VaPatentino> vaPatentinos = fetchCollection(vaDatiVacancy.getVaPatentinos());
		Collection<VaOrario> vaOrarios = fetchCollection(vaDatiVacancy.getVaOrarios());
		Collection<VaIstruzione> vaIstruziones = fetchCollection(vaDatiVacancy.getVaIstruziones());
		Collection<VaPatente> vaPatentes = fetchCollection(vaDatiVacancy.getVaPatentes());
		Collection<VaAlbo> vaAlbos = fetchCollection(vaDatiVacancy.getVaAlbos());
		Collection<VaTurno> vaTurnos = fetchCollection(vaDatiVacancy.getVaTurnos());
		Collection<VaContratto> vaContrattos = fetchCollection(vaDatiVacancy.getVaContrattos());

		Collection<VaAgevolazione> vaAgevolaziones = fetchCollection(vaDatiVacancy.getVaAgevolaziones());

		entityManager.detach(vaDatiVacancy);

		vaDatiVacancy.setIntermediario(null);
		vaDatiVacancy.setDenominazioneIntermediario(null);
		vaDatiVacancy.setDeEvasioneRich(null);// #8473
		PfPrincipal aziendaInfoUtenteSessione = pfPrincipalHome.findById(pfPrincipalId);
		vaDatiVacancy.setPfPrincipal(aziendaInfoUtenteSessione);
		vaDatiVacancy.setPfPrincipalPalese(null);

		/*
		 * se la vacancy e' intermedia e proviente da ClicLavoro allora elimino i dati di contatto, altrimenti vengono
		 * mantenuti
		 */
		if (vaDatiVacancy.isFromClicLavoro()) {
			vaDatiVacancy.setVaContattoPrinc(null);
			vaDatiVacancy.setVaContattoAlt(null);
		} else {
			if (vaContattoPrincipale != null && vaContattoPrincipale.getIdVaContatto() != null) {
				vaContattoHome.copyById(pfPrincipalId, vaContattoPrincipale, vaDatiVacancy);
			}
			if (vaContattoAlternativo != null && vaContattoAlternativo.getIdVaContatto() != null) {
				vaContattoHome.copyById(pfPrincipalId, vaContattoAlternativo, vaDatiVacancy);
			}
		}

		vaDatiVacancy.setIdVaDatiVacancy(null);

		vaDatiVacancy.setAttivitaPrincipale(descrizione);
		vaDatiVacancy.setAcCandidaturas(new HashSet<AcCandidatura>(0));
		vaDatiVacancy.setNumAnno(numAnno);
		vaDatiVacancy.setDeProvenienzaVacancy(provenienzaMyPortal);
		vaDatiVacancy.setNumRichiesta(numRichiesta);
		vaDatiVacancy.setDtPubblicazione(now);
		vaDatiVacancy.setFlagInvioCl(false);

		vaDatiVacancy.setDtmIns(now);
		vaDatiVacancy.setDtmMod(now);
		vaDatiVacancy.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
		vaDatiVacancy.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));

		vaDatiVacancy.setVaAltreInfo(null);
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO)) {
			Calendar scadenza = Calendar.getInstance();
			scadenza.setTime(now);
			scadenza.add(Calendar.DAY_OF_MONTH, TestataVacancyBean.NUM_GIORNI_VALIDITA_VACANCY_PAT);
			vaDatiVacancy.setDtScadenzaPubblicazione(scadenza.getTime());
			vaDatiVacancy.setCodStatoVacancyEnum(CodStatoVacancyEnum.LAV);
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			Calendar scadenza = Calendar.getInstance();
			scadenza.setTime(now);
			scadenza.add(Calendar.DAY_OF_MONTH, 15);
			vaDatiVacancy.setDtScadenzaPubblicazione(scadenza.getTime());
			vaDatiVacancy.setCodStatoVacancyEnum(CodStatoVacancyEnum.LAV);
		}
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_PUGLIA)) {
			Calendar scadenza = Calendar.getInstance();
			scadenza.setTime(now);
			scadenza.add(Calendar.DAY_OF_MONTH, 60);
			vaDatiVacancy.setDtScadenzaPubblicazione(scadenza.getTime());
		}

		persist(vaDatiVacancy);

		if (vaInformatica != null) {
			vaInformaticaHome.copyById(pfPrincipalId, vaInformatica, vaDatiVacancy);
		}
		if (vaAltreInfo != null) {
			vaAltreInfoHome.copyById(pfPrincipalId, vaAltreInfo, vaDatiVacancy);
		}
		if (vaCompetenzeTrasv != null) {
			vaCompetenzeTrasvHome.copyById(pfPrincipalId, vaCompetenzeTrasv, vaDatiVacancy);
		}
		if (vaRetribuzione != null) {
			vaRetribuzioneHome.copyById(pfPrincipalId, vaRetribuzione, vaDatiVacancy);
		}
		if (vaPubblicazione != null) {
			vaPubblicazioneHome.copyById(pfPrincipalId, vaPubblicazione, vaDatiVacancy);
		}
		if (vaEsperienze != null) {
			vaEsperienzeHome.copyById(pfPrincipalId, vaEsperienze, vaDatiVacancy);
		}

		copyCollection(pfPrincipalId, vaLinguaHome, vaLinguas, vaDatiVacancy);
		copyCollection(pfPrincipalId, vaPatentinoHome, vaPatentinos, vaDatiVacancy);
		copyCollection(pfPrincipalId, vaOrarioHome, vaOrarios, vaDatiVacancy);
		copyCollection(pfPrincipalId, vaIstruzioneHome, vaIstruziones, vaDatiVacancy);
		copyCollection(pfPrincipalId, vaPatenteHome, vaPatentes, vaDatiVacancy);
		copyCollection(pfPrincipalId, vaAlboHome, vaAlbos, vaDatiVacancy);
		copyCollection(pfPrincipalId, vaTurnoHome, vaTurnos, vaDatiVacancy);
		copyCollection(pfPrincipalId, vaAgevolazioneHome, vaAgevolaziones, vaDatiVacancy);

		copyCollection(pfPrincipalId, vaContrattoHome, vaContrattos, vaDatiVacancy);

		entityManager.flush();

		return vaDatiVacancy;
	}

	/**
	 * Aggiungere qui altri controlli eventuali da effettuare in copia
	 * 
	 * @param vacancy
	 */
	private void checkDatiCopia(VaDatiVacancy vacancy) {

		Collection<VaContratto> vaContrattos = fetchCollection(vacancy.getVaContrattos());
		List<VaContratto> vaContrattosChecked = new ArrayList<>();

		// Tirocinio rimosso in RER con IDO 2020
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			for (VaContratto vaContratto : vaContrattos) {
				if (ConstantsSingleton.DeContrattoSil.TIROCINIO
						.equals(vaContratto.getDeContrattoSil().getCodContrattoSil())) {

					continue;// lo ignoro, come detto da Simone

					/*
					 * MessageFormat formatter = new MessageFormat("");
					 * formatter.applyPattern(errorsBean.getProperty("vacancy.error_copying.wrongcontratto"));
					 * 
					 * Object[] paramObject = { vaContratto.getDeContrattoSil().getDescrizione() }; String output =
					 * formatter.format(paramObject); throw new MyPortalException(output, true);
					 */
				} else
					vaContrattosChecked.add(vaContratto);
			}
		}
		vacancy.setVaContrattos(vaContrattosChecked);
	}

	public VaDatiVacancyDTO copyById(Integer pfPrincipalId, Integer vacancyId, String descrizione) {
		VaDatiVacancyDTO vaDatiVacancyDTO = toDTO(copyByIdNoDTO(pfPrincipalId, vacancyId, descrizione));
		vaDatiVacancyDTO.setProprietary(true);
		return vaDatiVacancyDTO;
	}

	/**
	 * Esegue il fetch di una collection, eseguendo la query su DB. Permette di evitare una complessità ciclomatica
	 * eccessiva e di eseguire una specie di 'eager loading' all'occorrenza.
	 * 
	 * @param collection
	 *            la collezione di cui effettuare il fetching
	 * @return la collection stessa ma popolata degli elementi
	 */
	private <Entity> Collection<Entity> fetchCollection(Collection<Entity> collection) {
		if (collection != null && collection.size() > 0) {
			collection.iterator().next();
		}
		return collection;
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
			AbstractVacancyEntityListHome<Entity, ?> home, Collection<Entity> collection, VaDatiVacancy vaDatiVacancy) {
		if (collection != null) {
			for (Entity entity : collection) {
				home.copyById(pfPrincipalId, entity, vaDatiVacancy);
			}
		}
	}

	public List<AziendaInfo> ottienilistAzInfoRichiedente(String datiVacancyXml)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
		List<AziendaInfo> aziendaInfoRichiedente = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(datiVacancyXml));
		Document document = builder.parse(source);
		XPath xpath = XPathFactory.newInstance().newXPath();

		String cfAzienda = xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/codicefiscale[1]")
				.evaluate(document);

		// verifica se esiste l'azienda per CF
		aziendaInfoRichiedente = aziendaInfoHome.findListByCodiceFiscale(cfAzienda);
		return aziendaInfoRichiedente;
	}

	/**
	 * Metodo che stabilisce se una vacancy e` archiviabile utilizzato in due bean differenti: VacancyViewBean e
	 * VacancyFormPfBean, quindi valida sia per redaz. che per aziende, sia vacancy palesi che "da portale"
	 * 
	 * @param vacancy
	 * @param session
	 * @return
	 */
	public boolean isArchiviaRendered(VaDatiVacancy vacancy, SessionBean session) {
		// comuni tra prov e AZI
		if (vacancy.getIdVaDatiVacancy() == null)
			return false;
		if (!CodStatoVacancyEnum.PUB.equals(vacancy.getCodStatoVacancyEnum()))
			return false;
		if (!ConstantsSingleton.isSysBetweenDtScadDtPubbStatic(vacancy.getDtScadenzaPubblicazione(),
				vacancy.getDtPubblicazione()))
			return false;

		if (session.isAzienda()) {
			// archiviaz. come azienda puo` essere fatta SOLO dal titolare dell annuncio
			// per definizione, il titolare e` sempre id_pf_princ sulla vaDatiVacancy
			return (vacancy.getDeProvenienzaVacancy() != null
					// come azienda vedi solo le tue
					&& session.getPrincipalId().equals(vacancy.getPfPrincipal().getIdPfPrincipal()));
		} else if (session.isProvincia()) {
			return true;
		}

		log.warn("Caso imprevisto l'archiviazione non è per cittadini");
		return false;
	}

	/**
	 * Metodo che stabilisce se una vacancy e` modificable utilizzato in due bean differenti: VacancyViewBean e
	 * RicercaOffertaBean. ATTENZIONE: alla pagina VacancyViewBean possono accedere gli utenti: azienda, redazione,
	 * cittadino.
	 * 
	 * @param vacancy
	 * @param session
	 * @return
	 */
	public boolean isModificaRendered(Integer idVaDatiVacancy, SessionBean session) {
		VaDatiVacancy vacancy = findById(idVaDatiVacancy);
		if (session.isAzienda()) {
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				if (!vacancy.getFlagIdo()) {
					return false;
				} else {
					if (ConstantsSingleton.DeProvenienza.COD_MYPORTAL
							.equals(vacancy.getDeProvenienzaVacancy().getCodProvenienza())
							&& vacancy.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.LAV)) {
						return true;
					}
				}
			}
			if (ConstantsSingleton.DeProvenienza.COD_MYPORTAL
					.equals(vacancy.getDeProvenienzaVacancy().getCodProvenienza())
					&& (vacancy.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.LAV)
							|| vacancy.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB))) {
				// Se non è scaduta ritorna true
				return !(new Date()).after(vacancy.getDtScadenzaPubblicazione());
			}
		}
		return false;
	}

	public List<PfPrincipal> ottieniEmailListAzInfo(String datiVacancyXml)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
		List<PfPrincipal> aziendaInfoPfPrincipal = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(datiVacancyXml));
		Document document = builder.parse(source);
		XPath xpath = XPathFactory.newInstance().newXPath();

		String cfAzienda = xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/codicefiscale[1]")
				.evaluate(document);

		// verifica se esiste l'azienda per CF
		aziendaInfoPfPrincipal = pfPrincipalHome.findEmailByCFAziendaInfo(cfAzienda);
		return aziendaInfoPfPrincipal;
	}

	public int insertVacancyFromSIL(String datiVacancyXml)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
		log.info("VACANCY DA SIL:" + datiVacancyXml);
		int retCode = 0;
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(datiVacancyXml));
		Document document = builder.parse(source);
		XPath xpath = XPathFactory.newInstance().newXPath();

		PfPrincipal principal = pfPrincipalHome.findByUsername("amministratore");
		// // inserisco i dati azienda vacancy
		// VaDatiAziendaInfo vaDatiAziendaInfo = new VaDatiAziendaInfo();
		// AZIENDA CPI
		XPathExpression annoOffertaXp = xpath.compile("/Vacancy/DatiSistema/annoofferta[1]");
		XPathExpression numeroOffertaXp = xpath.compile("/Vacancy/DatiSistema/numeroofferta[1]");
		XPathExpression provenienzaXp = xpath.compile("/Vacancy/DatiSistema/provenienza[1]");
		Integer numAnno = new Integer(annoOffertaXp.evaluate(document));
		Integer numRichiesta = new Integer(numeroOffertaXp.evaluate(document));
		String codProvenienza = provenienzaXp.evaluate(document);

		/*
		 * Controllo se la vacancy usa le decodifiche ministeriali o quelle SIL. (Per ora solo Trento, da maggio 2017,
		 * usa le decodifiche SIL) Uso questo flag anche per capire se tirare fuori dall'XML i campi introdotti per
		 * Trento a maggio 2017.
		 */
		boolean usaDecodificheSil = false;
		try {
			if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(
					xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/opz_tipo_decodifiche").evaluate(document))) {
				usaDecodificheSil = true;
			}
		} catch (Exception e) {
			// Non faccio niente : di default usiamo le decodifiche ministeriali
		}

		/*
		 * Se la vacancy e' gia' stata importata e successivamente modificata dal portale, allora non si deve ne
		 * aggiornare ne importare una seconda volta, fa fede la versione modificata nel portale.
		 * 
		 * Quindi se e' presente una vacancy con num_richiesta_sil, num_anno_sil e cod_provenienza_vacancy_sil uguali a
		 * quelli presenti nella vacancy ricevuta da SIL significa che non bisogna fare niente.
		 */
		VaDatiVacancy vaDatiVacancyDaSILModificata = findVacancyModificataByRiferimentoSIL(numAnno, numRichiesta,
				codProvenienza);
		if (vaDatiVacancyDaSILModificata != null) {
			// return;
			return retCode;
		}

		// cerchiamo la corrispondente vacancy
		VaDatiVacancy vaDatiVacancy = findVacancyByAlternateKey(numAnno, numRichiesta, codProvenienza);

		boolean isVacancy2upd = (vaDatiVacancy != null);
		if (!isVacancy2upd) {
			// se non esiste inserisco la vacancy
			vaDatiVacancy = new VaDatiVacancy();
			vaDatiVacancy.setDtmIns(now);
			vaDatiVacancy.setCodStatoVacancyEnum(CodStatoVacancyEnum.PUB);
			vaDatiVacancy.setPfPrincipalIns(principal);

			/* flag di invio a cliclavoro */
			vaDatiVacancy.setFlagInvioCl(false);
		}
		vaDatiVacancy.setDtmMod(now);
		vaDatiVacancy.setPfPrincipalMod(principal);

		/* inserire campi nuovi */

		/*
		 * Controllo se la vacancy è stata inviata come parte del pacchetto Cresco.
		 */
		try {
			if ("Y".equalsIgnoreCase(
					xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/opz_cresco").evaluate(document))) {
				vaDatiVacancy.setFlgPacchettoCresco(true);
			} else {
				vaDatiVacancy.setFlgPacchettoCresco(false);
			}
		} catch (Exception e) {
			// Di default, la vacancy NON fa parte del pacchetto Cresco.
			vaDatiVacancy.setFlgPacchettoCresco(false);
		}

		/*
		 * Controllo se è stato incluso il flg_candidature (che determina se per questa vacancy è possibile candidarsi)
		 */
		vaDatiVacancy.setFlgCandidatura(true);
		try {
			if ("N".equalsIgnoreCase(
					xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/flgcandidatura").evaluate(document))) {
				vaDatiVacancy.setFlgCandidatura(false);
			}
		} catch (Exception e) {
			// Di default, lascio a TRUE il flag (è possibile candidarsi).
		}

		String codCpi = xpath.compile("/Vacancy/DatiSistema/cpi[1]").evaluate(document);
		PfPrincipal aziendaInfoCpi = aziendaInfoHome.findByCodiceFiscale(codCpi).getPfPrincipal();

		if (aziendaInfoCpi == null) {
			throw new EJBException("Inserisci vacancy da SIL::impossibile trovare l'azienda CPI con codice=" + codCpi);
		} else {
			vaDatiVacancy.setPfPrincipal(aziendaInfoCpi);
		}

		// SE VACANCY PALESE inserisce un nuovo utente per l'AZIENDA (se non
		// esiste) che ha richiesto la vacancy al CPI
		// AZIENDA
		AziendaInfo aziendaInfoRichiedente = null;
		XPathExpression datiRichiedenteCodiceFiscaleXP = xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda");
		Object resultCf = datiRichiedenteCodiceFiscaleXP.evaluate(document, XPathConstants.NODESET);
		NodeList nodesAziendas = (NodeList) resultCf;
		for (int i = 1; i <= nodesAziendas.getLength(); i++) {
			String cfAzienda = xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/codicefiscale[" + i + "]")
					.evaluate(document);
			String emailRegAzRich = xpath
					.compile("/Vacancy/RegistrazioneAzienda/DatiRichiedente/emailregistrazione[" + i + "]")
					.evaluate(document);

			// verifica se esiste l'azienda per CF e EMAIL
			if (UtilsBean.isStaticRER()) {
				aziendaInfoRichiedente = aziendaInfoHome.findByCFAndMail(cfAzienda, emailRegAzRich);
			} else {
				aziendaInfoRichiedente = aziendaInfoHome.findByCFAndMailReferente(cfAzienda, emailRegAzRich);
			}
			if (aziendaInfoRichiedente == null) {
				// verifico se esistono azienda con lo stesso CF
				List<AziendaInfo> listAzInfoRichiedente = aziendaInfoHome.findListByCodiceFiscale(cfAzienda);
				// se non esistono inserisco l'azienda
				if (listAzInfoRichiedente.size() == 0) {
					// inserisce un nuovo utente per l'azienda richiedente
					// inserimento dei principal delle aziende
					RegisterAziendaDTO registerAzRichiedente = new RegisterAziendaDTO();

					// String[] arrEmail = emailRegAzRich.split("@");
					registerAzRichiedente.setUsername(cfAzienda);
					registerAzRichiedente.setPassword(Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH));
					registerAzRichiedente.setPasswordConfirm(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
					registerAzRichiedente.setEmail(emailRegAzRich);
					registerAzRichiedente.setEmailConfirm(emailRegAzRich);

					registerAzRichiedente.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
					registerAzRichiedente.setAttivo(false);
					registerAzRichiedente.setAcceptInformativa(true);
					registerAzRichiedente
							.setNome(xpath.compile("/Vacancy/RegistrazioneAzienda/DatiRichiedente/nome[" + i + "]")
									.evaluate(document));
					registerAzRichiedente.setCognome(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiRichiedente/cognome[" + i + "]")
									.evaluate(document));
					registerAzRichiedente.setCodiceFiscale(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/codicefiscale[" + i + "]")
									.evaluate(document));
					registerAzRichiedente.setIndirizzo(null);
					registerAzRichiedente.setComune(null);
					registerAzRichiedente.setCap(null);
					registerAzRichiedente.setTelefono(null);
					registerAzRichiedente.setDomanda("Il codice fiscale dell'azienda è?");
					registerAzRichiedente.setRisposta(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/codicefiscale[" + i + "]")
									.evaluate(document));

					registerAzRichiedente.setRagioneSociale(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/ragionesociale[" + i + "]")
									.evaluate(document));
					SedeDTO sedeAzRichiedente = new SedeDTO();
					sedeAzRichiedente.setIndirizzo(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/ragionesociale[" + i + "]")
									.evaluate(document));
					DeComuneDTO comuneSedeAzRich = deComuneHome.findDTOById(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/codcomunesedeoperativa[" + i + "]")
									.evaluate(document));
					sedeAzRichiedente.setComune(comuneSedeAzRich);
					sedeAzRichiedente.setCap(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/capsedeoperativa[" + i + "]")
									.evaluate(document));
					sedeAzRichiedente.setFax(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/faxsedeoperativa[" + i + "]")
									.evaluate(document));
					sedeAzRichiedente.setTelefono(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/telefonosedeoperativa[" + i + "]")
									.evaluate(document));
					sedeAzRichiedente.setIndirizzo(
							xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/indirizzosedeoperativa[" + i + "]")
									.evaluate(document));
					registerAzRichiedente.setSedeOperativa(sedeAzRichiedente);

					AziendaInfo azPalese = aziendaInfoHome.register(registerAzRichiedente, true);

					vaDatiVacancy.setPfPrincipalPalese(azPalese.getPfPrincipal());

					// per avvio di TRENTO
					Calendar dtAvvioClic = Calendar.getInstance();
					dtAvvioClic.setTime(tsGetOpzioniEJB.getDtAvvioClic());
					Calendar today = Calendar.getInstance();
					if (dtAvvioClic != null) {
						if (today.after(dtAvvioClic)) {
							EmailDTO registerAzRichEmail = EmailDTO
									.buildRegistrationEmailRichiestaPersonale(registerAzRichiedente);
							Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registerAzRichEmail);
						}
					}

					creaVetrinaMinimale(azPalese, document, xpath, i);
				} else if (listAzInfoRichiedente.size() == 1) {
					// se esiste solo una è l'azienda da associare alla vacancy
					// se ne esiste più di una la vacancy non risulta associata
					// a nessuna azienda
					aziendaInfoRichiedente = listAzInfoRichiedente.get(0);
					vaDatiVacancy.setPfPrincipalPalese(aziendaInfoRichiedente.getPfPrincipal());

					creaVetrinaMinimale(aziendaInfoRichiedente, document, xpath, i);
				} else if (listAzInfoRichiedente.size() > 1) {
					if (UtilsBean.isStaticRER()) {
						return -1; // da restituire errore SIL
					}
				}
			} else {
				vaDatiVacancy.setPfPrincipalPalese(aziendaInfoRichiedente.getPfPrincipal());
				creaVetrinaMinimale(aziendaInfoRichiedente, document, xpath, i);
			}

		}

		String codEvasione = xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/cod_evasione[1]").evaluate(document);

		String nomeOperatore = xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/nome_operatore[1]")
				.evaluate(document);

		String cognomeOperatore = xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/cognome_operatore[1]")
				.evaluate(document);
		/*
		 * String codiceFiscale =
		 * xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/codicefiscale[1]").evaluate(document); String
		 * ragioneSociale =
		 * xpath.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/ragionesociale[1]").evaluate(document);
		 */
		String codiceFiscale = xpath.compile("/Vacancy/DatoreLavoro/DatiAnagrafici/codicefiscale[1]")
				.evaluate(document);
		String ragioneSociale = xpath.compile("/Vacancy/DatoreLavoro/DatiAnagrafici/denominazione[1]")
				.evaluate(document);

		// da gestire anche il codice evasione
		DeEvasioneRich deEvs = null;
		if (codEvasione != null && !codEvasione.isEmpty()) {
			deEvs = deEvasioneRichHome.findById(codEvasione);
		}
		vaDatiVacancy.setDeEvasioneRich(deEvs);
		if (nomeOperatore != null && !nomeOperatore.isEmpty()) {
			vaDatiVacancy.setNomeOperatore(nomeOperatore);
		} else {
			vaDatiVacancy.setNomeOperatore(null);
		}
		if (cognomeOperatore != null && !cognomeOperatore.isEmpty()) {
			vaDatiVacancy.setCognomeOperatore(cognomeOperatore);
		} else {
			vaDatiVacancy.setCognomeOperatore(null);
		}
		vaDatiVacancy.setCodiceFiscale(codiceFiscale);
		vaDatiVacancy.setRagioneSociale(ragioneSociale);
		if (UtilsBean.isStaticRER()) {
			vaDatiVacancy.setFlagIdo(new Boolean(true));
		}

		DeComune comune = deComuneHome
				.findById(xpath.compile("/Vacancy/DatoreLavoro/DatiAnagrafici/codcomunelavoro[1]").evaluate(document)); // codcomunelavoro
																														// è
																														// sempre
																														// obbligatorio

		if (UtilsBean.isStaticRER()) {
			vaDatiVacancy.setDeComuneAzienda(comune);
		}

		if (UtilsBean.isUmbria() || UtilsBean.isStaticPAT() || UtilsBean.isStaticRER()) {
			comune = deComuneHome
					.findById(xpath.compile("/Vacancy/Richiesta/CondizioniOfferte/codcomune").evaluate(document));
			vaDatiVacancy.setIndirizzoLavoro(comune.getDenominazione());

		} else {
			vaDatiVacancy.setIndirizzoLavoro(
					xpath.compile("/Vacancy/DatoreLavoro/DatiAnagrafici/indirizzolavoro[1]").evaluate(document));
		}
		vaDatiVacancy.setDeComune(comune);

		VaContatto vaContatto = null;
		VaContatto vaContattoAlternativo = null;
		if (isVacancy2upd) {
			vaContatto = vaDatiVacancy.getVaContattoPrinc();
			vaContattoAlternativo = vaDatiVacancy.getVaContattoAlt();
			if (vaContattoAlternativo == null) {
				vaContattoAlternativo = new VaContatto();
			}
		}
		if (vaContatto == null) {
			vaContatto = new VaContatto();
			vaContattoAlternativo = new VaContatto();
		}

		popolaVaContattoCpi(document, xpath, vaContatto, now, principal, isVacancy2upd);
		popolaVaContattoAlternativo(document, xpath, vaContattoAlternativo, now, principal, isVacancy2upd);
		popolaAbstractEntity(now, principal, vaContatto);

		vaDatiVacancy.setVaContattoPrinc(vaContatto);
		if (vaContattoAlternativo != null) {
			if (vaContattoAlternativo.getDtmMod() != null) {
				vaDatiVacancy.setFlagAnonima(false);
				vaDatiVacancy.setVaContattoAlt(vaContattoAlternativo);

				popolaAbstractEntity(now, principal, vaContattoAlternativo);
				entityManager.persist(vaContattoAlternativo);
			} else {
				vaDatiVacancy.setFlagAnonima(true);
			}

		}

		entityManager.persist(vaContatto);

		// NON è da aggiornare -> i dati della alternate key son gli stessi
		if (!isVacancy2upd) {
			DeProvenienza deProvenienza = deProvenienzaVacancyHome.findById(codProvenienza);
			vaDatiVacancy.setDeProvenienzaVacancy(deProvenienza);
			vaDatiVacancy.setNumAnno(numAnno);
			vaDatiVacancy.setNumRichiesta(numRichiesta);
		}
		popolaVaDatiVacancy(dateFormat, document, xpath, vaDatiVacancy, usaDecodificheSil);

		popolaAbstractEntity(now, principal, vaDatiVacancy);
		// manca mappatura
		// vaDatiVacancy.setDeTrasferta(deTrasferta);

		Set<String> codInWsSet = new HashSet<String>();
		Set<String> codInDbSet = new HashSet<String>();

		// GEOLOCALIZZO LA VACANCY
		Poi poi = poiHome.createOrUpdatePoi(vaDatiVacancy);
		vaDatiVacancy.setPoi(poi);

		// NULLA OSTA
		XPathExpression nullaOstaXp = xpath.compile("/Vacancy/Richiesta/Flgnullaosta[1]");
		Object objNO = nullaOstaXp.evaluate(document);
		if (objNO != null && !("").equals(objNO)) {
			String opzNullaOsta = objNO.toString();
			if (("N").equalsIgnoreCase(opzNullaOsta)) {
				vaDatiVacancy.setOpzNullaOsta("N");
			} else {
				vaDatiVacancy.setOpzNullaOsta("Y");
			}
		} else {
			vaDatiVacancy.setOpzNullaOsta("N");
		}

		entityManager.persist(vaDatiVacancy);

		popolaVaAltreInfo(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, usaDecodificheSil);

		popolaVaEsperienze(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, usaDecodificheSil);

		if (UtilsBean.isStaticRER()) {
			popolaVaIstruzioneSILRER(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet,
					codInDbSet, usaDecodificheSil);
		} else {
			popolaVaIstruzione(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
					usaDecodificheSil);
		}
		popolaVaLingua(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
				usaDecodificheSil);

		popolaVaPatente(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
				usaDecodificheSil);

		popolaVaPatentino(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
				usaDecodificheSil);

		popolaVaTurno(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
				usaDecodificheSil);

		popolaVaAlbo(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
				usaDecodificheSil);

		popolaVaInformatica(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet);

		if (UtilsBean.isStaticRER()) {
			popolaListVaOrarioRER(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
					usaDecodificheSil);
		} else {
			popolaListVaOrario(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
					usaDecodificheSil);
		}
		popolaVaAgevolazione(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
				usaDecodificheSil);

		popolaVaContratto(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, codInWsSet, codInDbSet,
				usaDecodificheSil);

		popolaVaPubblicazione(now, document, xpath, principal, vaDatiVacancy, isVacancy2upd, usaDecodificheSil);

		try {
			Set<MsgMessaggioDTO> notifications = notificationBuilder.buildNotifications(vaDatiVacancy, isVacancy2upd,
					codProvenienza);
			notificationBuilder.sendNotification(notifications);

		} catch (EJBTransactionRolledbackException pe) {
			log.error("Errore durante salvataggio nella base dati: " + pe.getMessage());
			throw new PersistenceException("Errore durante salvataggio nella base dati: " + pe.getMessage());
		} catch (Exception e) {
			log.warn("Errore durante l'invio della notifica dopo l'inserimento vacancy: " + e.toString());
		}

		try {
			EmailDTO vacancyImportataEmail = EmailDTO.buildVacancyImportataEmail(
					pfPrincipalHome.findDTOById(aziendaInfoCpi.getIdPfPrincipal()),
					findDTOById(vaDatiVacancy.getIdVaDatiVacancy()), isVacancy2upd, codProvenienza);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, vacancyImportataEmail);
		} catch (Exception e) {
			log.warn("Errore durante l'invio della email dopo l'inserimento vacancy: " + e.toString());
		}
		return retCode;
	}

	private void creaVetrinaMinimale(VaDatiVacancyDTO data, Integer idPrincipalIns) {
		SvAziendaInfoDTO svdto;
		svdto = new SvAziendaInfoDTO();
		AziendaInfoDTO aziendaInfoDTO = aziendaInfoHome.findDTOById(data.getIdPfPrincipalAzienda());

		svdto.setId(aziendaInfoDTO.getId());
		svdto.setCodiceTemplate("CLASS");
		StringBuilder chiSiamo = new StringBuilder("<p>" + aziendaInfoDTO.getRagioneSociale() + "</p>");
		if (data.getStrAteco() != null && !data.getStrAteco().isEmpty()) {
			chiSiamo.append("<p>La nostra azienda opera nel settore " + data.getStrAteco().toLowerCase() + "</p>");
		}

		svdto.setChiSiamo(chiSiamo.toString());
		String dove = "";

		DeComuneDTO comune = aziendaInfoDTO.getSedeOperativa().getComune();

		// DeProvinciaDTO provincia =
		// deProvinciaHome.findDTOById(comune.getIdProvincia());

		dove += "<p>";
		dove += aziendaInfoDTO.getSedeOperativa().getIndirizzo();
		dove += ", " + comune.getDescrizione();
		dove += "</p>";

		svdto.setDoveSiamo(dove);
		// svdto.setDoveSiamo("<p>" +
		// aziendaInfoDTO.getSedeOperativa().getIndirizzo() + "</p>");

		svAziendaInfoHome.persistDTO(svdto, idPrincipalIns);
	}

	private void creaVetrinaMinimale(AziendaInfo info, Document doc, XPath xp, int i) throws XPathExpressionException {

		if (!svAziendaInfoHome.exists(info.getIdPfPrincipal())) {
			SvAziendaInfo svAziendaInfo = new SvAziendaInfo();

			svAziendaInfo.setIdPfPrincipal(info.getIdPfPrincipal());
			svAziendaInfo.setPfPrincipal(info.getPfPrincipal());
			svAziendaInfo.setFlagPubblicabile(true);
			svAziendaInfo.setDeSvTemplate(deSvTemplateHome.findById("CLASS"));
			StringBuilder chiSiamo = new StringBuilder("<p>"
					+ xp.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/ragionesociale[" + i + "]").evaluate(doc)
					+ "</p>");
			String codAttivita = xp.compile("/Vacancy/DatoreLavoro/DatiAnagrafici/settore").evaluate(doc);
			if (codAttivita != null && !codAttivita.isEmpty()) {
				String[] decodeAtt = codAttivita.split("\\.");
				DeAttivitaSil attivitaSil = deAttivitaSilHome.findById(decodeAtt[0]);
				DeAttivita attivita = attivitaSil.getDeAttivita();
				chiSiamo.append(
						"<p>La nostra azienda opera nel settore " + attivita.getDescrizione().toLowerCase() + "</p>");
			}

			svAziendaInfo.setChiSiamo(chiSiamo.toString());

			String dove = "";
			String cod = xp.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/codcomunesedeoperativa[" + i + "]")
					.evaluate(doc);
			DeComune comune = deComuneHome.findById(cod);

			dove += "<p>";
			dove += xp.compile("/Vacancy/RegistrazioneAzienda/DatiAzienda/indirizzosedeoperativa[" + i + "]")
					.evaluate(doc);
			dove += ", " + comune.getDenominazione() + " (" + comune.getDeProvincia().getTarga() + ")";
			dove += "</p>";

			svAziendaInfo.setDoveSiamo(dove);

			svAziendaInfo.setProfiliRichiesti("<p></p>");
			svAziendaInfo.setDtmIns(new Date());
			svAziendaInfo.setDtmMod(new Date());
			svAziendaInfo.setPfPrincipalIns(info.getPfPrincipalIns());
			svAziendaInfo.setPfPrincipalMod(info.getPfPrincipalMod());
			entityManager.persist(svAziendaInfo);
		}
	}

	private void popolaVaIstruzione(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		XPathExpression exprIstruzioneFormazioneTitoloStudio = xpath
				.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio");
		Object resultTitoloStudio = exprIstruzioneFormazioneTitoloStudio.evaluate(document, XPathConstants.NODESET);
		NodeList nodesTitoloStudio = (NodeList) resultTitoloStudio;
		Map<String, VaIstruzione> cod2vaIstr = new HashMap<String, VaIstruzione>();
		if (isVacancy2upd) {
			// aggiornamento liste utilizzando mappe by codTitolo
			for (VaIstruzione vaIstruzione : vaDatiVacancy.getVaIstruziones()) {
				cod2vaIstr.put(vaIstruzione.getDeTitolo().getCodTitolo(), vaIstruzione);
			}
		}
		if (codInDbSet != null) {
			codInDbSet.clear();
			codInDbSet.addAll(cod2vaIstr.keySet());
		}

		// XPathExpression codTitoloStudioLocal =
		// xpath.compile("./codtitolostudio");
		for (int i = 1; i <= nodesTitoloStudio.getLength(); i++) {
			// Node nodo = nodesTitoloStudio.item(i);
			String cod = xpath
					.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio[" + i + "]/codtitolostudio[1]")
					.evaluate(document);
			// String codTitolo = codTitoloStudioLocal.evaluate(nodo);
			codInWsSet.add(cod);

			VaIstruzione vaIstruzione = cod2vaIstr.get(cod);
			if (vaIstruzione == null) {
				vaIstruzione = new VaIstruzione();
			}
			DeTitolo titolo = deTitoloHome.findById(cod);
			vaIstruzione.setDeTitolo(titolo);
			vaIstruzione.setVaDatiVacancy(vaDatiVacancy);

			if (usaDecodificheSil) {
				vaIstruzione.setSpecifica(
						xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio[" + i + "]/specifica[1]")
								.evaluate(document));
				String opzConseguito = xpath
						.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio[" + i + "]/conseguito[1]")
						.evaluate(document);
				String opzIndispensabile = xpath
						.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio[" + i + "]/titoloindisp[1]")
						.evaluate(document);
				// Se i flag opzConseguito o opzIndispensabile sono stringhe vuote, li metto a NULL sul db
				vaIstruzione
						.setOpzConseguito((opzConseguito != null && opzConseguito.isEmpty()) ? null : opzConseguito);
				vaIstruzione.setOpzIndispensabile(
						(opzIndispensabile != null && opzIndispensabile.isEmpty()) ? null : opzIndispensabile);
			}

			popolaAbstractEntity(now, principal, vaIstruzione);
			entityManager.persist(vaIstruzione);

		}

		if (codInDbSet != null) {
			codInDbSet.removeAll(codInWsSet);

			for (String codTit : codInDbSet) {
				VaIstruzione va = cod2vaIstr.get(codTit);
				entityManager.remove(va);
			}
		}
	}

	private void popolaVaIstruzioneSILRER(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		XPathExpression exprIstruzioneFormazioneTitoloStudio = xpath
				.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio");
		Object resultTitoloStudio = exprIstruzioneFormazioneTitoloStudio.evaluate(document, XPathConstants.NODESET);
		NodeList nodesTitoloStudio = (NodeList) resultTitoloStudio;
		if (isVacancy2upd) {

			for (VaIstruzione vaIstruzione : vaDatiVacancy.getVaIstruziones()) {
				vaIstruzioneHome.remove(vaIstruzione);
			}
			vaDatiVacancy.setVaIstruziones(null);
		}
		for (int i = 1; i <= nodesTitoloStudio.getLength(); i++) {
			// Node nodo = nodesTitoloStudio.item(i);
			String cod = xpath
					.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio[" + i + "]/codtitolostudio[1]")
					.evaluate(document);

			DeTitolo titolo = deTitoloHome.findById(cod);

			if (UtilsBean.isStaticRER()) {
				if (titolo != null && titolo.getFlagIdo() != null && !titolo.getFlagIdo().booleanValue()) { // se
																											// flagido
																											// =false
																											// imposto
																											// come
																											// titolo il
																											// titolo
																											// padre
					titolo = deTitoloHome.findById(titolo.getPadre().getCodTitolo());
					cod = titolo.getCodTitolo();
				}
			}
			VaIstruzione vaIstruzione = new VaIstruzione();

			vaIstruzione.setDeTitolo(titolo);
			vaIstruzione.setVaDatiVacancy(vaDatiVacancy);

			if (usaDecodificheSil) {
				String specifica = xpath
						.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio[" + i + "]/specifica[1]")
						.evaluate(document);
				vaIstruzione.setSpecifica(specifica);
				if (UtilsBean.isStaticRER()) {
					if (specifica.isEmpty()) {
						vaIstruzione.setSpecifica(titolo.getDescrizione());
					}
				}
				String opzConseguito = xpath
						.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio[" + i + "]/conseguito[1]")
						.evaluate(document);
				String opzIndispensabile = xpath
						.compile("/Vacancy/Richiesta/IstruzioneFormazione/titolostudio[" + i + "]/titoloindisp[1]")
						.evaluate(document);
				// Se i flag opzConseguito o opzIndispensabile sono stringhe vuote, li metto a
				// NULL sul db
				vaIstruzione
						.setOpzConseguito((opzConseguito != null && opzConseguito.isEmpty()) ? null : opzConseguito);
				vaIstruzione.setOpzIndispensabile(
						(opzIndispensabile != null && opzIndispensabile.isEmpty()) ? null : opzIndispensabile);
			}

			popolaAbstractEntity(now, principal, vaIstruzione);
			entityManager.persist(vaIstruzione);

		}
	}

	private void popolaVaPatentino(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		Map<String, VaPatentino> cod2VaPatentino = new HashMap<String, VaPatentino>();
		if (isVacancy2upd) {
			// aggiornamento liste utilizzando mappe by CodPatente
			for (VaPatentino va : vaDatiVacancy.getVaPatentinos()) {
				if (usaDecodificheSil) {
					cod2VaPatentino.put(va.getDePatentinoSil().getCodPatentinoSil(), va);
				} else {
					cod2VaPatentino.put(va.getDePatentino().getCodPatentino(), va);
				}
			}
		}

		codInWsSet.clear();
		if (codInDbSet != null) {
			codInDbSet.clear();
			codInDbSet.addAll(cod2VaPatentino.keySet());
		}

		XPathExpression exprIstruzioneFormazionePatentini = null;
		if (usaDecodificheSil) {
			exprIstruzioneFormazionePatentini = xpath.compile("/Vacancy/DatiAggiuntivi/Abilitazioni/Patentini");
		} else {
			exprIstruzioneFormazionePatentini = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/codpatentino");
		}
		Object resultPatentini = exprIstruzioneFormazionePatentini.evaluate(document, XPathConstants.NODESET);
		NodeList nodesPatentini = (NodeList) resultPatentini;
		for (int i = 1; i <= nodesPatentini.getLength(); i++) {
			String codPatentino = null;
			String patentinoIndispensabile = null;
			if (usaDecodificheSil) {
				codPatentino = xpath.compile("/Vacancy/DatiAggiuntivi/Abilitazioni/Patentini[" + i + "]/codpatentino")
						.evaluate(document);
				patentinoIndispensabile = xpath
						.compile("/Vacancy/DatiAggiuntivi/Abilitazioni/Patentini[" + i + "]/patentinoindisp")
						.evaluate(document);
				if (patentinoIndispensabile != null && patentinoIndispensabile.isEmpty())
					patentinoIndispensabile = null;
			} else {
				codPatentino = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/codpatentino[" + i + "]")
						.evaluate(document);
			}

			codInWsSet.add(codPatentino);

			VaPatentino patentino = cod2VaPatentino.get(codPatentino);
			if (patentino == null) {
				patentino = new VaPatentino();
			}
			patentino.setVaDatiVacancy(vaDatiVacancy);

			if (usaDecodificheSil) {
				patentino.setDePatentino(null);
				patentino.setDePatentinoSil(dePatentinoSilHome.findById(codPatentino));
				patentino.setOpzIndispensabile(patentinoIndispensabile);
			} else {
				patentino.setDePatentino(dePatentinoHome.findById(codPatentino));
				patentino.setDePatentinoSil(null);
			}

			if (patentino.getDePatentino() != null || patentino.getDePatentinoSil() != null) {
				popolaAbstractEntity(now, principal, patentino);
				entityManager.persist(patentino);
			}
		}

		if (codInDbSet != null) {
			codInDbSet.removeAll(codInWsSet);

			// rimuovo tutti i record richiesti che non lo sono più
			for (String cod : codInDbSet) {
				VaPatentino va = cod2VaPatentino.get(cod);
				entityManager.remove(va);
			}
		}
	}

	private void popolaVaLingua(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		XPathExpression exprIstruzioneFormazioneLingua = xpath
				.compile("/Vacancy/Richiesta/IstruzioneFormazione/Lingua");
		Object resultLingua = exprIstruzioneFormazioneLingua.evaluate(document, XPathConstants.NODESET);

		Map<String, VaLingua> cod2VaLingua = new HashMap<String, VaLingua>();
		if (isVacancy2upd) {
			// aggiornamento liste utilizzando mappe by codlingua
			for (VaLingua va : vaDatiVacancy.getVaLinguas()) {
				cod2VaLingua.put(va.getDeLingua().getCodLingua(), va);
			}
		}

		NodeList nodesLingua = (NodeList) resultLingua;
		// for (int i = 1; i <= nodesLingua.getLength(); i++) { }

		codInWsSet.clear();
		if (codInDbSet != null) {
			codInDbSet.clear();
			codInDbSet.addAll(cod2VaLingua.keySet());
		}

		for (int i = 1; i <= nodesLingua.getLength(); i++) {
			String cod = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/Lingua[" + i + "]/codlingua[1]")
					.evaluate(document);
			DeLingua lingua = deLinguaHome.findById(cod);
			codInWsSet.add(cod);

			VaLingua vaLingua = cod2VaLingua.get(cod);
			if (vaLingua == null) {
				vaLingua = new VaLingua();
			}
			String gradoLinguaLetto = xpath
					.compile("/Vacancy/Richiesta/IstruzioneFormazione/Lingua[" + i + "]/codlivelloletto[1]")
					.evaluate(document);
			String gradoLinguaScritto = xpath
					.compile("/Vacancy/Richiesta/IstruzioneFormazione/Lingua[" + i + "]/codlivelloscritto[1]")
					.evaluate(document);
			String gradoLinguaParlato = xpath
					.compile("/Vacancy/Richiesta/IstruzioneFormazione/Lingua[" + i + "]/codlivelloparlato[1]")
					.evaluate(document);

			if (!gradoLinguaLetto.isEmpty() && !gradoLinguaScritto.isEmpty() && !gradoLinguaParlato.isEmpty()) {
				if (usaDecodificheSil) {
					vaLingua.setDeGradoSilLetto(deGradoLinSilHome.findById(gradoLinguaLetto));
					vaLingua.setDeGradoSilScritto(deGradoLinSilHome.findById(gradoLinguaScritto));
					vaLingua.setDeGradoSilParlato(deGradoLinSilHome.findById(gradoLinguaParlato));
					String opzIndispensabile = xpath
							.compile("/Vacancy/Richiesta/IstruzioneFormazione/Lingua[" + i + "]/linguaindisp[1]")
							.evaluate(document);
					vaLingua.setOpzIndispensabile(
							(opzIndispensabile != null && opzIndispensabile.isEmpty()) ? null : opzIndispensabile);
				} else {
					// GradoLinguaLetto
					vaLingua.setDeGradoLinByCodGradoLetto(deGradoLinHome.findById(gradoLinguaLetto));
					// GradoLinguaScritto
					vaLingua.setDeGradoLinByCodGradoScritto(deGradoLinHome.findById(gradoLinguaScritto));
					// GradoLinguaParlato
					vaLingua.setDeGradoLinByCodGradoParlato(deGradoLinHome.findById(gradoLinguaParlato));
				}
			} else if (gradoLinguaLetto.isEmpty() && gradoLinguaScritto.isEmpty() && gradoLinguaParlato.isEmpty()) {
				if (usaDecodificheSil) {
					vaLingua.setDeGradoSilLetto(null);
					vaLingua.setDeGradoSilScritto(null);
					vaLingua.setDeGradoSilParlato(null);
					String opzIndispensabile = xpath
							.compile("/Vacancy/Richiesta/IstruzioneFormazione/Lingua[" + i + "]/linguaindisp[1]")
							.evaluate(document);
					vaLingua.setOpzIndispensabile(
							(opzIndispensabile != null && opzIndispensabile.isEmpty()) ? null : opzIndispensabile);
				} else {// GradoLinguaLetto
					vaLingua.setDeGradoLinByCodGradoLetto(null);
					// GradoLinguaScritto
					vaLingua.setDeGradoLinByCodGradoScritto(null);
					// GradoLinguaParlato
					vaLingua.setDeGradoLinByCodGradoParlato(null);
				}
			}

			vaLingua.setFlagMadrelingua(false);

			if (UtilsBean.isStaticRER()) { // caso RER prevede sempre usaDecodificheSil == true e obbligatorietà gradi
											// lingua
				if (!gradoLinguaLetto.isEmpty() && !gradoLinguaScritto.isEmpty() && !gradoLinguaParlato.isEmpty()) {
					if (gradoLinguaLetto.equalsIgnoreCase(ConstantsSingleton.Lingua.MADRELINGUA)
							|| gradoLinguaScritto.equalsIgnoreCase(ConstantsSingleton.Lingua.MADRELINGUA)
							|| gradoLinguaParlato.equalsIgnoreCase(ConstantsSingleton.Lingua.MADRELINGUA)) {
						vaLingua.setFlagMadrelingua(new Boolean(true));
						vaLingua.setDeGradoSilLetto(null);
						vaLingua.setDeGradoSilScritto(null);
						vaLingua.setDeGradoSilParlato(null);
						vaLingua.setDeGradoLinByCodGradoLetto(null);
						vaLingua.setDeGradoLinByCodGradoScritto(null);
						vaLingua.setDeGradoLinByCodGradoParlato(null);
					}
				}
			}

			vaLingua.setDeLingua(lingua);
			vaLingua.setVaDatiVacancy(vaDatiVacancy);

			popolaAbstractEntity(now, principal, vaLingua);
			entityManager.persist(vaLingua);
		}

		if (codInDbSet != null) {
			codInDbSet.removeAll(codInWsSet);

			for (String codTit : codInDbSet) {
				VaLingua va = cod2VaLingua.get(codTit);
				entityManager.remove(va);
			}
		}
	}

	private void popolaVaPatente(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		XPathExpression exprIstruzioneFormazionePatente = null;

		if (usaDecodificheSil) {
			exprIstruzioneFormazionePatente = xpath.compile("/Vacancy/DatiAggiuntivi/Abilitazioni/Patenti");
		} else {
			exprIstruzioneFormazionePatente = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/codpatenteguida");
		}
		Object resultPatente = exprIstruzioneFormazionePatente.evaluate(document, XPathConstants.NODESET);
		NodeList nodesPatente = (NodeList) resultPatente;

		Map<String, VaPatente> cod2VaPatente = new HashMap<String, VaPatente>();
		if (isVacancy2upd) {
			// aggiornamento liste utilizzando mappe by CodPatente
			for (VaPatente va : vaDatiVacancy.getVaPatentes()) {
				if (usaDecodificheSil) {
					cod2VaPatente.put(va.getDePatenteSil().getCodPatenteSil(), va);
				} else {
					cod2VaPatente.put(va.getDePatente().getCodPatente(), va);
				}
			}
		}

		codInWsSet.clear();
		if (codInDbSet != null) {
			codInDbSet.clear();
			codInDbSet.addAll(cod2VaPatente.keySet());
		}

		for (int i = 1; i <= nodesPatente.getLength(); i++) {
			String codPatente = null;
			String patenteIndispensabile = null;
			if (usaDecodificheSil) {
				codPatente = xpath.compile("/Vacancy/DatiAggiuntivi/Abilitazioni/Patenti[" + i + "]/codpatenteguida")
						.evaluate(document);
				patenteIndispensabile = xpath
						.compile("/Vacancy/DatiAggiuntivi/Abilitazioni/Patenti[" + i + "]/patguidaindisp")
						.evaluate(document);
				if (patenteIndispensabile != null && patenteIndispensabile.isEmpty())
					patenteIndispensabile = null;
			} else {
				codPatente = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/codpatenteguida[" + i + "]")
						.evaluate(document);
			}
			codInWsSet.add(codPatente);

			VaPatente patente = cod2VaPatente.get(codPatente);
			if (patente == null) {
				patente = new VaPatente();
			}
			patente.setVaDatiVacancy(vaDatiVacancy);

			if (usaDecodificheSil) {
				DePatenteSil dePatenteSil = dePatenteSilHome.findById(codPatente);
				patente.setDePatente(null);
				patente.setDePatenteSil(dePatenteSil);
				patente.setOpzIndispensabile(patenteIndispensabile);
			} else {
				DePatente depatente = dePatenteHome.findById(codPatente);
				patente.setDePatente(depatente);
				patente.setDePatenteSil(null);
			}

			if (patente.getDePatente() != null || patente.getDePatenteSil() != null) {
				popolaAbstractEntity(now, principal, patente);
				entityManager.persist(patente);
			}
		}

		// rimuovo tutte le patenti che erano richieste e non lo sono più
		if (codInDbSet != null) {
			codInDbSet.removeAll(codInWsSet);
			for (String cod : codInDbSet) {
				VaPatente vaPatente = cod2VaPatente.get(cod);
				entityManager.remove(vaPatente);
			}
		}
	}

	private void popolaVaAlbo(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		Map<String, VaAlbo> cod2VaAlbo = new HashMap<String, VaAlbo>();
		if (isVacancy2upd) {
			// aggiornamento liste utilizzando mappe by cod
			for (VaAlbo va : vaDatiVacancy.getVaAlbos()) {
				if (usaDecodificheSil) {
					cod2VaAlbo.put(va.getDeAlboSil().getCodAlboSil(), va);
				} else {
					cod2VaAlbo.put(va.getDeAlbo().getCodAlbo(), va);
				}
			}
		}

		codInWsSet.clear();
		if (codInDbSet != null) {
			codInDbSet.clear();
			codInDbSet.addAll(cod2VaAlbo.keySet());
		}

		/* Uso il solito boolean 'usaDecodificheSil' come discriminante tra "vecchio" e "nuovo" XML */
		XPathExpression exprIstruzioneFormazioneAlbo = null;
		if (usaDecodificheSil) {
			exprIstruzioneFormazioneAlbo = xpath.compile("/Vacancy/DatiAggiuntivi/Abilitazioni/Albi");
		} else {
			exprIstruzioneFormazioneAlbo = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/codalbo");
		}
		Object resultAlbo = exprIstruzioneFormazioneAlbo.evaluate(document, XPathConstants.NODESET);
		NodeList nodesAlbo = (NodeList) resultAlbo;

		/* Per ciascuno dei nodi 'albo' trovati nell'XML, creo un VaAlbo (o modifico quello già esistente se c'è) */
		for (int i = 1; i <= nodesAlbo.getLength(); i++) {
			String codAlbo = null;
			String opzAlboIndispensabile = null;
			if (usaDecodificheSil) {
				codAlbo = xpath.compile("/Vacancy/DatiAggiuntivi/Abilitazioni/Albi[" + i + "]/codalbo")
						.evaluate(document);
				opzAlboIndispensabile = xpath.compile("/Vacancy/DatiAggiuntivi/Abilitazioni/Albi[" + i + "]/alboindisp")
						.evaluate(document);
				if (opzAlboIndispensabile != null && opzAlboIndispensabile.isEmpty())
					opzAlboIndispensabile = null;
			} else {
				codAlbo = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/codalbo[" + i + "]")
						.evaluate(document);
			}

			codInWsSet.add(codAlbo);
			VaAlbo albo = cod2VaAlbo.get(codAlbo);
			if (albo == null) {
				albo = new VaAlbo();
			}
			albo.setVaDatiVacancy(vaDatiVacancy);

			if (usaDecodificheSil) {
				albo.setDeAlbo(null);
				albo.setDeAlboSil(deAlboSilHome.findById(codAlbo));
				albo.setOpzIndispensabile(opzAlboIndispensabile);
			} else {
				albo.setDeAlbo(deAlboHome.findById(codAlbo));
				albo.setDeAlboSil(null);
			}

			if (albo.getDeAlbo() != null || albo.getDeAlboSil() != null) {
				popolaAbstractEntity(now, principal, albo);
				entityManager.persist(albo);
			}
		}

		if (codInDbSet != null) {
			codInDbSet.removeAll(codInWsSet);

			// rimuovo tutti i record richiesti che non lo sono più
			for (String cod : codInDbSet) {
				VaAlbo va = cod2VaAlbo.get(cod);
				entityManager.remove(va);
			}
		}
	}

	/**
	 * Popola la lista di oggetti VaTurno della vacancy. NOTA IMPORTANTE ! Per il momento, i VaTurno vengono popolati
	 * SOLO se si stanno usando le decodificheSil.
	 */
	private void popolaVaTurno(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		if (!usaDecodificheSil)
			return; // SE NON STO USANDO LE DECODIFICHE SIL, NON FACCIO NIENTE!

		Map<String, VaTurno> cod2VaTurno = new HashMap<String, VaTurno>();
		if (isVacancy2upd) {
			// aggiornamento liste utilizzando mappe by cod
			for (VaTurno vt : vaDatiVacancy.getVaTurnos()) {
				if (usaDecodificheSil) {
					cod2VaTurno.put(vt.getDeTurnoSil().getCodTurnoSil(), vt);
				} else {
					cod2VaTurno.put(vt.getDeTurno().getCodTurno(), vt);
				}
			}
		}

		codInWsSet.clear();
		if (codInDbSet != null) {
			codInDbSet.clear();
			codInDbSet.addAll(cod2VaTurno.keySet());
		}

		XPathExpression exprCodTurno = xpath.compile("/Vacancy/DatiAggiuntivi/Turni/codturno");
		Object resultTurno = exprCodTurno.evaluate(document, XPathConstants.NODESET);
		NodeList nodesTurno = (NodeList) resultTurno;
		for (int i = 1; i <= nodesTurno.getLength(); i++) {
			String cod = xpath.compile("/Vacancy/DatiAggiuntivi/Turni/codturno[" + i + "]").evaluate(document);
			codInWsSet.add(cod);

			VaTurno vaTurno = cod2VaTurno.get(cod);
			if (vaTurno == null) {
				vaTurno = new VaTurno();
			}
			vaTurno.setVaDatiVacancy(vaDatiVacancy);

			if (usaDecodificheSil) {
				vaTurno.setDeTurno(null);
				vaTurno.setDeTurnoSil(deTurnoSilHome.findById(cod));
			} else {
				vaTurno.setDeTurno(deTurnoHome.findById(cod));
				vaTurno.setDeTurnoSil(null);
			}

			if (vaTurno.getDeTurno() != null || vaTurno.getDeTurnoSil() != null) {
				popolaAbstractEntity(now, principal, vaTurno);
				entityManager.persist(vaTurno);
			}
		}

		if (codInDbSet != null) {
			codInDbSet.removeAll(codInWsSet);

			// rimuovo tutti i record richiesti che non lo sono più
			for (String cod : codInDbSet) {
				VaTurno va = cod2VaTurno.get(cod);
				entityManager.remove(va);
			}
		}
	}

	private void popolaVaAgevolazione(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		XPathExpression exprIstruzioneFormazioneAgevolazione = null;

		if (usaDecodificheSil) {
			exprIstruzioneFormazioneAgevolazione = xpath.compile("/Vacancy/DatiAggiuntivi/Agevolazioni");
		} else {
			exprIstruzioneFormazioneAgevolazione = xpath
					.compile("/Vacancy/Richiesta/IstruzioneFormazione/codagevolazione");
		}
		Object resultAgevolazione = exprIstruzioneFormazioneAgevolazione.evaluate(document, XPathConstants.NODESET);
		NodeList nodesAgevolazione = (NodeList) resultAgevolazione;

		Map<String, VaAgevolazione> cod2VaAgevolazione = new HashMap<String, VaAgevolazione>();
		if (isVacancy2upd) {
			// aggiornamento liste utilizzando mappe by CodPatente
			for (VaAgevolazione va : vaDatiVacancy.getVaAgevolaziones()) {
				if (usaDecodificheSil) {
					cod2VaAgevolazione.put(va.getDeAgevolazioneSil().getCodAgevolazioneSil(), va);
				} else {
					cod2VaAgevolazione.put(va.getDeAgevolazione().getCodAgevolazione(), va);
				}
			}
		}

		codInWsSet.clear();
		if (codInDbSet != null) {
			codInDbSet.clear();
			codInDbSet.addAll(cod2VaAgevolazione.keySet());
		}

		for (int i = 1; i <= nodesAgevolazione.getLength(); i++) {
			String codAgevolazione = null;
			String agevolazioneIndispensabile = null;

			if (usaDecodificheSil) {
				codAgevolazione = xpath.compile("/Vacancy/DatiAggiuntivi/Agevolazioni[" + i + "]/codagevolazione")
						.evaluate(document);
				agevolazioneIndispensabile = xpath
						.compile("/Vacancy/DatiAggiuntivi/Agevolazioni[" + i + "]/agevolazioneindisp")
						.evaluate(document);
				if (agevolazioneIndispensabile != null && agevolazioneIndispensabile.isEmpty())
					agevolazioneIndispensabile = null;
			} else {
				codAgevolazione = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/codagevolazione[" + i + "]")
						.evaluate(document);
			}
			codInWsSet.add(codAgevolazione);
			VaAgevolazione agevolazione = cod2VaAgevolazione.get(codAgevolazione);
			if (agevolazione == null) {
				agevolazione = new VaAgevolazione();
			}

			// VaAgevolazione agevolazione = new VaAgevolazione();
			agevolazione.setVaDatiVacancy(vaDatiVacancy);
			if (usaDecodificheSil) {
				agevolazione.setDeAgevolazioneSil(deAgevolazioneSilHome.findById(codAgevolazione));
				agevolazione.setOpzIndispensabile(agevolazioneIndispensabile);
			} else {
				agevolazione.setDeAgevolazione(deAgevolazioneHome.findById(codAgevolazione));
			}

			if (agevolazione.getDeAgevolazione() != null || agevolazione.getDeAgevolazioneSil() != null) {
				popolaAbstractEntity(now, principal, agevolazione);
				entityManager.persist(agevolazione);
			}
		}

		if (codInDbSet != null) {
			codInDbSet.removeAll(codInWsSet);

			// rimuovo tutte le patenti che erano richieste e non lo sono più
			for (String cod : codInDbSet) {
				VaAgevolazione va = cod2VaAgevolazione.get(cod);
				entityManager.remove(va);
			}
		}
	}

	private void popolaVaInformatica(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet)
			throws XPathExpressionException {
		XPathExpression exprIstruzioneFormazioneInformatica = xpath
				.compile("/Vacancy/Richiesta/IstruzioneFormazione/conoscenzeinformatiche");
		Object resultInformatica = exprIstruzioneFormazioneInformatica.evaluate(document, XPathConstants.NODESET);
		NodeList nodesInformatica = (NodeList) resultInformatica;
		for (int i = 1; i <= nodesInformatica.getLength(); i++) {
			VaInformatica informatica = null;
			if (isVacancy2upd) {
				informatica = vaDatiVacancy.getVaInformatica();
			}
			if (informatica == null) {
				informatica = new VaInformatica();
			}
			String cod = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/conoscenzeinformatiche[" + i + "]")
					.evaluate(document);
			informatica.setConoscenzaInfo(cod);
			codInWsSet.add(cod);
			// TODO duplicato
			informatica.setVaDatiVacancy(vaDatiVacancy);
			popolaAbstractEntity(now, principal, informatica);
			entityManager.persist(informatica);
		}
	}

	private void popolaListVaOrario(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		Map<String, VaOrario> cod2VaOrario = new HashMap<String, VaOrario>();
		if (isVacancy2upd) {
			// aggiornamento liste utilizzando mappe by CodPatente
			for (VaOrario va : vaDatiVacancy.getVaOrarios()) {
				if (usaDecodificheSil) {
					cod2VaOrario.put(va.getDeOrarioSil().getCodOrarioSil(), va);
				} else {
					cod2VaOrario.put(va.getDeOrario().getCodOrario(), va);
				}
			}
		}

		codInWsSet.clear();
		if (codInDbSet != null) {
			codInDbSet.clear();
			codInDbSet.addAll(cod2VaOrario.keySet());
		}

		XPathExpression exprIstruzioneFormazioneOrario = xpath
				.compile("/Vacancy/Richiesta/IstruzioneFormazione/codorario");
		Object resultOrario = exprIstruzioneFormazioneOrario.evaluate(document, XPathConstants.NODESET);
		NodeList nodesOrario = (NodeList) resultOrario;
		List<String> buffer = new ArrayList<String>();
		for (int i = 1; i <= nodesOrario.getLength(); i++) {
			String cod = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/codorario[" + i + "]")
					.evaluate(document);
			codInWsSet.add(cod);

			VaOrario orario = cod2VaOrario.get(cod);
			if (orario == null) {
				orario = new VaOrario();
			}
			orario.setVaDatiVacancy(vaDatiVacancy);
			DeOrarioSil deOrarioSil = null;
			if (usaDecodificheSil) {
				orario.setDeOrario(null);
				deOrarioSil = deOrarioSilHome.findById(cod);

				if (UtilsBean.isStaticRER()) {
					if (!buffer.contains(deOrarioSil.getTipoOrario())) {
						if (!deOrarioSil.getFlgIdo()) {
							deOrarioSil = deOrarioSilHome.findValideSILByFlagIdo(true, deOrarioSil.getTipoOrario());
						}
						buffer.add(deOrarioSil.getTipoOrario());
					} else
						continue;
				}

				orario.setDeOrarioSil(deOrarioSil);
			} else {
				orario.setDeOrario(deOrarioHome.findById(cod));
				orario.setDeOrarioSil(null);
			}

			if (orario.getDeOrario() != null || orario.getDeOrarioSil() != null) {
				popolaAbstractEntity(now, principal, orario);
				entityManager.persist(orario);
			}
		}

		if (codInDbSet != null) {
			codInDbSet.removeAll(codInWsSet);

			// rimuovo tutte le patenti che erano richieste e non lo sono più
			for (String cod : codInDbSet) {
				VaOrario vaOrario = cod2VaOrario.get(cod);
				entityManager.remove(vaOrario);
			}
		}

	}

	private void popolaListVaOrarioRER(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		Map<String, VaOrario> cod2VaOrario = new HashMap<String, VaOrario>();
		if (isVacancy2upd) {
			for (VaOrario va : vaDatiVacancy.getVaOrarios()) {
				vaOrarioHome.remove(va);
			}
			vaDatiVacancy.setVaOrarios(null);
		}

		XPathExpression exprIstruzioneFormazioneOrario = xpath
				.compile("/Vacancy/Richiesta/IstruzioneFormazione/codorario");
		Object resultOrario = exprIstruzioneFormazioneOrario.evaluate(document, XPathConstants.NODESET);
		NodeList nodesOrario = (NodeList) resultOrario;
		List<String> buffer = new ArrayList<String>();
		for (int i = 1; i <= nodesOrario.getLength(); i++) {
			String cod = xpath.compile("/Vacancy/Richiesta/IstruzioneFormazione/codorario[" + i + "]")
					.evaluate(document);

			VaOrario orario = cod2VaOrario.get(cod);
			if (orario == null) {
				orario = new VaOrario();
			}
			orario.setVaDatiVacancy(vaDatiVacancy);
			DeOrarioSil deOrarioSil = null;
			if (usaDecodificheSil) {
				orario.setDeOrario(null);
				deOrarioSil = deOrarioSilHome.findById(cod);

				if (UtilsBean.isStaticRER()) {
					if (!buffer.contains(deOrarioSil.getTipoOrario())) {
						if (!deOrarioSil.getFlgIdo()) {
							deOrarioSil = deOrarioSilHome.findValideSILByFlagIdo(true, deOrarioSil.getTipoOrario());
						}
						buffer.add(deOrarioSil.getTipoOrario());
					} else
						continue;
				}

				orario.setDeOrarioSil(deOrarioSil);
			} else {
				orario.setDeOrario(deOrarioHome.findById(cod));
				orario.setDeOrarioSil(null);
			}

			if (orario.getDeOrario() != null || orario.getDeOrarioSil() != null) {
				popolaAbstractEntity(now, principal, orario);
				entityManager.persist(orario);
			}
		}

	}

	private void popolaVaContratto(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, Set<String> codInWsSet, Set<String> codInDbSet,
			boolean usaDecodificheSil) throws XPathExpressionException {
		XPathExpression exprTipoContratto = null;
		if (usaDecodificheSil) {
			exprTipoContratto = xpath.compile("/Vacancy/DatiAggiuntivi/TipoRapporto/codcontratto");
		} else {
			exprTipoContratto = xpath.compile("/Vacancy/Richiesta/CondizioniOfferte/codtipologiacontratto");
		}
		Object resultContratto = exprTipoContratto.evaluate(document, XPathConstants.NODESET);
		NodeList nodesContratto = (NodeList) resultContratto;

		Map<String, VaContratto> cod2VaContratto = new HashMap<String, VaContratto>();
		if (isVacancy2upd) {
			// aggiornamento liste utilizzando mappe by CodPatente
			for (VaContratto va : vaDatiVacancy.getVaContrattos()) {
				if (usaDecodificheSil) {
					cod2VaContratto.put(va.getDeContrattoSil().getCodContrattoSil(), va);
				} else {
					cod2VaContratto.put(va.getDeContratto().getCodContratto(), va);
				}
			}
		}

		codInWsSet.clear();
		if (codInDbSet != null) {
			codInDbSet.clear();
			codInDbSet.addAll(cod2VaContratto.keySet());
		}

		for (int i = 1; i <= nodesContratto.getLength(); i++) {
			String codContratto = null;
			if (usaDecodificheSil) {
				codContratto = xpath.compile("/Vacancy/DatiAggiuntivi/TipoRapporto/codcontratto[" + i + "]")
						.evaluate(document);
			} else {
				codContratto = xpath.compile("/Vacancy/Richiesta/CondizioniOfferte/codtipologiacontratto[" + i + "]")
						.evaluate(document);
			}
			codInWsSet.add(codContratto);
			VaContratto contratto = cod2VaContratto.get(codContratto);
			if (contratto == null) {
				contratto = new VaContratto();
			}

			contratto.setVaDatiVacancy(vaDatiVacancy);
			if (usaDecodificheSil) {
				contratto.setDeContrattoSil(deContrattoSilHome.findById(codContratto));
			} else {
				contratto.setDeContratto(deContrattoHome.findById(codContratto));
			}

			if (contratto.getDeContratto() != null || contratto.getDeContrattoSil() != null) {
				popolaAbstractEntity(now, principal, contratto);
				entityManager.persist(contratto);
			}
		}

		if (codInDbSet != null) {
			codInDbSet.removeAll(codInWsSet);

			// rimuovo tutte le patenti che erano richieste e non lo sono più
			for (String cod : codInDbSet) {
				VaContratto va = cod2VaContratto.get(cod);
				entityManager.remove(va);
			}
		}
	}

	/**
	 * @param now
	 * @param principal
	 * @param aeList
	 */
	private void popolaAbstractEntity(Date now, PfPrincipal principal, AbstractEntity... aeList) {
		for (AbstractEntity ae : aeList) {
			if (ae.getPfPrincipalIns() == null) {
				ae.setDtmIns(now);
				ae.setPfPrincipalIns(principal);
			}
			ae.setDtmMod(now);
			ae.setPfPrincipalMod(principal);
		}
	}

	private void popolaVaDatiVacancy(SimpleDateFormat dateFormat, Document document, XPath xpath,
			VaDatiVacancy vaDatiVacancy, boolean usaDecodificheSil) throws XPathExpressionException, ParseException {
		vaDatiVacancy.setNumLavRicercati(new Integer(
				xpath.compile("/Vacancy/Richiesta/ProfiloRichiesto/numerolavoratori[1]").evaluate(document)));
		vaDatiVacancy.setAttivitaPrincipale(
				xpath.compile("/Vacancy/Richiesta/ProfiloRichiesto/attivitaprincipale[1]").evaluate(document));
		vaDatiVacancy.setAttivitaDescrizioneEstesa(
				xpath.compile("/Vacancy/Richiesta/ProfiloRichiesto/descrizionericerca[1]").evaluate(document));
		vaDatiVacancy.setOpzEsperienze(
				xpath.compile("/Vacancy/Richiesta/ProfiloRichiesto/esperienzarichiesta[1]").evaluate(document));
		vaDatiVacancy.setOpzPubblicabile("S");

		vaDatiVacancy.setOpzPubblicabile("S");
		String dataPubblicazione = xpath.compile("/Vacancy/Richiesta/DurataRichiesta/datapubblicazione[1]")
				.evaluate(document);
		String dataScadenzaPubblicazione = xpath
				.compile("/Vacancy/Richiesta/DurataRichiesta/datascadenzapubblicazione[1]").evaluate(document);
		vaDatiVacancy.setDtPubblicazione(dateFormat.parse(dataPubblicazione));
		vaDatiVacancy.setDtScadenzaPubblicazione(dateFormat.parse(dataScadenzaPubblicazione));
		String codMansione = xpath.compile("/Vacancy/Richiesta/ProfiloRichiesto/codprofessione[1]").evaluate(document);

		String codMansioneRER = xpath.compile("/Vacancy/Richiesta/ProfiloRichiesto/codmansione[1]").evaluate(document);
		DeBpMansione deBpMansione = null;
		if (UtilsBean.isStaticRER()) {

			vaDatiVacancy.setFlagNoDiscriminazione(true); // quando s'invia una vacancy dal SIL al portale , la vacancy
															// è sempre già pubblicata quindi ha già settata
															// l'accettazione condizioni di servizio

			if (usaDecodificheSil && codMansioneRER != null && !codMansioneRER.isEmpty()) {
				codMansioneRER = codMansioneRER.substring(0, 4);
				deBpMansione = deBpMansioneHome.findById(codMansioneRER);
			}
			vaDatiVacancy.setDeBpMansione(deBpMansione);
		}

		DeMansioneSil mansioneSil = deMansioneSilHome.findById(codMansione);
		DeMansione mansione = mansioneSil.getDeMansione();
		// String codMasioneMp = mmm.getCodMansione();
		vaDatiVacancy.setDeMansione(mansione);

		// Se sto usando le decodifiche SIL, posso riempire anche codMansioneMin
		// (passando per il cod_mansione_dot)
		if (usaDecodificheSil && codMansione != null && !codMansione.isEmpty()) {
			DeMansioneMin deMansioneMin = deMansioneMinHome.findByDot(codMansione);
			vaDatiVacancy.setMansioneMin(deMansioneMin);
			if (deBpMansione != null) {
				vaDatiVacancy.setProfessione(deMansioneMin.getDescrizione());
			}
		}

		String codAttivita = xpath.compile("/Vacancy/DatoreLavoro/DatiAnagrafici/settore").evaluate(document);
		if (codAttivita != null && !("").equalsIgnoreCase(codAttivita)) {
			String[] decodeAtt = codAttivita.split("\\.");
			DeAttivitaSil attivitaSil = deAttivitaSilHome.findById(decodeAtt[0]);
			DeAttivita attivita = attivitaSil.getDeAttivita();
			vaDatiVacancy.setDeAttivita(attivita);
		}

		// Se sto usando le decodifiche SIL, posso riempire anche codAttivitaMin (passando per il cod_attivta_dot)
		if (usaDecodificheSil && codAttivita != null && !codAttivita.isEmpty()) {
			DeAttivitaMin deAttivitaMin = deAttivitaMinHome.findByDot(codAttivita);
			vaDatiVacancy.setAttivitaMin(deAttivitaMin);
		}

		vaDatiVacancy.setFlagEliminata(false);
		vaDatiVacancy.setVisibilita(true);

		// Campi aggiunti per Trento (aprile 2017)
		if (usaDecodificheSil) {
			String noteOperatore = xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/noteoperatore")
					.evaluate(document);
			vaDatiVacancy.setNotaOperatore(noteOperatore);
			vaDatiVacancy.setOpzTipoDecodifiche(VaDatiVacancy.OpzTipoDecodifiche.SIL);

			String codTrasferta = xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/trasferta").evaluate(document);
			if (codTrasferta != null && !codTrasferta.isEmpty())
				vaDatiVacancy.setDeTrasferta(deTrasfertaHome.findById(codTrasferta));

		} else {
			vaDatiVacancy.setOpzTipoDecodifiche(VaDatiVacancy.OpzTipoDecodifiche.MINISTERO);
		}
	}

	/** TEMP : Gli unici valori da inizializzare in VaAltreInfo ci sono solo se usiamo le decodifiche SIL */
	private void popolaVaAltreInfo(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, boolean usaDecodificheSil)
			throws XPathExpressionException {
		if (!usaDecodificheSil)
			return;

		VaAltreInfo vaAltreInfo = null;
		if (isVacancy2upd) {
			vaAltreInfo = vaDatiVacancy.getVaAltreInfo();
		}
		if (vaAltreInfo == null) {
			vaAltreInfo = new VaAltreInfo();
			popolaAbstractEntity(now, principal, vaAltreInfo);
			vaAltreInfo.setVaDatiVacancy(vaDatiVacancy);
		}

		vaAltreInfo
				.setOpzAutomunito(xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/automunito").evaluate(document));
		vaAltreInfo
				.setOpzMotomunito(xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/motomunito").evaluate(document));
		vaAltreInfo.setOpzAlloggio(xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/alloggio").evaluate(document));
		vaAltreInfo.setOpzFuorisede(xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/fuorisede").evaluate(document));
		vaAltreInfo.setOpzMilite(xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/milite").evaluate(document));
		vaAltreInfo.setNotaMotivoGenere(
				xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/notamotivosesso").evaluate(document));
		vaAltreInfo.setLocalita(xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/localita").evaluate(document));
		vaAltreInfo.setOpzAlloggio(xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/alloggio").evaluate(document));

		String codGenere = xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/sesso").evaluate(document);
		if (codGenere != null && !codGenere.isEmpty())
			vaAltreInfo.setDeGenere(deGenereHome.findById(codGenere));

		String codMotivoGenereSil = xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/motivosesso")
				.evaluate(document);
		if (codMotivoGenereSil != null && !codMotivoGenereSil.isEmpty())
			vaAltreInfo.setDeMotivoGenereSil(deMotivoGenereSilHome.findById(codMotivoGenereSil));

		String codAreaSil = xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/codarea").evaluate(document);
		if (codAreaSil != null && !codAreaSil.isEmpty())
			vaAltreInfo.setDeAreaSil(deAreaSilHome.findById(codAreaSil));

		String codTrasferta = xpath.compile("/Vacancy/DatiAggiuntivi/DatiGenerali/trasferta").evaluate(document);
		if (codTrasferta != null && !codTrasferta.isEmpty()) {
			if (UtilsBean.isStaticRER()) {
				String info = null;
				if (!codTrasferta.equals("1")) {
					info = deTrasfertaHome.findById(codTrasferta).getDescrizione();
					vaAltreInfo.setOpzTrasferta("Y");
					vaAltreInfo.setInfoTrasferta(info);
				}
			}
		}

		entityManager.persist(vaAltreInfo);
	}

	/** TEMP : La VaEsperienza viene compilata SOLO se stiamo usando le decodifiche SIL */
	private void popolaVaEsperienze(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, boolean usaDecodificheSil)
			throws XPathExpressionException {
		if (!usaDecodificheSil)
			return;

		VaEsperienze vaEsperienze = null;
		if (isVacancy2upd) {
			vaEsperienze = vaDatiVacancy.getVaEsperienze();
		}
		if (vaEsperienze == null) {
			vaEsperienze = new VaEsperienze();
			popolaAbstractEntity(now, principal, vaEsperienze);
			vaEsperienze.setVaDatiVacancy(vaDatiVacancy);
		}

		try {
			String numDaString = xpath.compile("/Vacancy/DatiAggiuntivi/Esperienze/numda").evaluate(document);
			if (numDaString != null && !numDaString.isEmpty()) {
				vaEsperienze.setNumDa(Integer.parseInt(numDaString));
			}
		} catch (Exception e) {
			log.warn("Errore durante il parsing di VaEsperienze.numDa: " + e.toString());
			vaEsperienze.setNumDa(null);
		}

		try {
			String numAString = xpath.compile("/Vacancy/DatiAggiuntivi/Esperienze/numa").evaluate(document);
			if (numAString != null && !numAString.isEmpty()) {
				vaEsperienze.setNumA(Integer.parseInt(numAString));
			}
		} catch (Exception e) {
			log.warn("Errore durante il parsing di VaEsperienze.numA: " + e.toString());
			vaEsperienze.setNumA(null);
		}

		try {
			String numAnniEsperienzaString = xpath.compile("/Vacancy/DatiAggiuntivi/Esperienze/anniesperienza")
					.evaluate(document);
			// Nuovo Campo Anni Esperienza IDO RER
			if (UtilsBean.isStaticRER()) {
				vaEsperienze.setDescEsperienzaLav(numAnniEsperienzaString);
			} else {
				vaEsperienze.setNumAnniEsperienza(Integer.parseInt(numAnniEsperienzaString));
			}

		} catch (Exception e) {
			log.warn("Errore durante il parsing di VaEsperienze.numAnniEsperienza: " + e.toString());
			vaEsperienze.setNumAnniEsperienza(null);
		}

		vaEsperienze
				.setNotaMotivoEta(xpath.compile("/Vacancy/DatiAggiuntivi/Esperienze/notamotivoeta").evaluate(document));
		vaEsperienze
				.setOpzEsperienza(xpath.compile("/Vacancy/DatiAggiuntivi/Esperienze/esperienza").evaluate(document));
		vaEsperienze.setNotaEsperienza(
				xpath.compile("/Vacancy/DatiAggiuntivi/Esperienze/notaesperienza").evaluate(document));
		vaEsperienze.setOpzFormazione(
				xpath.compile("/Vacancy/DatiAggiuntivi/Esperienze/formazioneprof").evaluate(document));

		String codMotivoEtaSil = xpath.compile("/Vacancy/DatiAggiuntivi/Esperienze/codmoteta").evaluate(document);
		if (codMotivoEtaSil != null && !codMotivoEtaSil.isEmpty()) {
			vaEsperienze.setDeMotivoEtaSil(deMotivoEtaSilHome.findById(codMotivoEtaSil));
		}

		entityManager.persist(vaEsperienze);
	}

	/** TEMP : La VaPubblicazione viene compilata SOLO se stiamo usando le decodifiche SIL */
	private void popolaVaPubblicazione(Date now, Document document, XPath xpath, PfPrincipal principal,
			VaDatiVacancy vaDatiVacancy, boolean isVacancy2upd, boolean usaDecodificheSil)
			throws XPathExpressionException {
		if (!usaDecodificheSil)
			return;

		VaPubblicazione vaPubblicazione = null;
		if (isVacancy2upd) {
			vaPubblicazione = vaDatiVacancy.getVaPubblicazione();
		}
		if (vaPubblicazione == null) {
			vaPubblicazione = new VaPubblicazione();
			popolaAbstractEntity(now, principal, vaPubblicazione);
			vaPubblicazione.setVaDatiVacancy(vaDatiVacancy);
		}

		vaPubblicazione.setDatiAzienda(
				xpath.compile("/Vacancy/DatiAggiuntivi/Pubblicazione/DatiAziendaPub").evaluate(document));
		vaPubblicazione.setLuogo(xpath.compile("/Vacancy/DatiAggiuntivi/Pubblicazione/LuogoPub").evaluate(document));
		vaPubblicazione
				.setFormazione(xpath.compile("/Vacancy/DatiAggiuntivi/Pubblicazione/FormazionePub").evaluate(document));
		vaPubblicazione
				.setContratto(xpath.compile("/Vacancy/DatiAggiuntivi/Pubblicazione/ContrattoPub").evaluate(document));
		vaPubblicazione
				.setConoscenze(xpath.compile("/Vacancy/DatiAggiuntivi/Pubblicazione/ConoscenzePub").evaluate(document));
		vaPubblicazione.setCaratteristiche(
				xpath.compile("/Vacancy/DatiAggiuntivi/Pubblicazione/CaratteristichePub").evaluate(document));
		vaPubblicazione.setOrario(xpath.compile("/Vacancy/DatiAggiuntivi/Pubblicazione/OrarioPub").evaluate(document));
		vaPubblicazione.setCandidatura(
				xpath.compile("/Vacancy/DatiAggiuntivi/Pubblicazione/CandidaturaPub").evaluate(document));

		entityManager.persist(vaPubblicazione);
	}

	private void popolaVaContattoCpi(Document document, XPath xpath, VaContatto vaContatto, Date now,
			PfPrincipal principal, boolean isVacancy2upd) throws XPathExpressionException {
		vaContatto.setCognomeRiferimentoPub(
				xpath.compile("/Vacancy/DatoreLavoro/DatiContatto/cognomereferente[1]").evaluate(document));
		vaContatto.setNomeRiferimentoPub(
				xpath.compile("/Vacancy/DatoreLavoro/DatiContatto/nomereferente[1]").evaluate(document));
		vaContatto.setFaxRiferimentoPub(xpath.compile("/Vacancy/DatoreLavoro/DatiContatto/fax[1]").evaluate(document));
		vaContatto.setMail(xpath.compile("/Vacancy/DatoreLavoro/DatiContatto/email[1]").evaluate(document));
		vaContatto.setTelRiferimentoPub(
				xpath.compile("/Vacancy/DatoreLavoro/DatiContatto/telefono[1]").evaluate(document));

		if (!isVacancy2upd) {
			vaContatto.setDtmIns(now);
			vaContatto.setPfPrincipalIns(principal);
		}

		vaContatto.setDtmMod(now);
		vaContatto.setPfPrincipalMod(principal);
	}

	private void popolaVaContattoAlternativo(Document document, XPath xpath, VaContatto vaContatto, Date now,
			PfPrincipal principal, boolean isVacancy2upd) throws XPathExpressionException {

		if (vaContatto == null) {
			vaContatto = new VaContatto();
			vaContatto.setDtmIns(now);
			vaContatto.setPfPrincipalIns(principal);
		}

		XPathExpression exprContattoAlt = xpath.compile("/Vacancy/DatoreLavoro/DatiContattoAlternativo");
		Object resultContat = exprContattoAlt.evaluate(document, XPathConstants.NODESET);
		NodeList nodesContat = (NodeList) resultContat;
		for (int i = 1; i <= nodesContat.getLength(); i++) {

			String cognome = xpath.compile("/Vacancy/DatoreLavoro/DatiContattoAlternativo/cognomereferente[" + i + "]")
					.evaluate(document);
			String nome = xpath.compile("/Vacancy/DatoreLavoro/DatiContattoAlternativo/nomereferente[" + i + "]")
					.evaluate(document);
			String fax = xpath.compile("/Vacancy/DatoreLavoro/DatiContattoAlternativo/fax[" + i + "]")
					.evaluate(document);
			String email = xpath.compile("/Vacancy/DatoreLavoro/DatiContattoAlternativo/email[" + i + "]")
					.evaluate(document);
			String telefono = xpath.compile("/Vacancy/DatoreLavoro/DatiContattoAlternativo/telefono[" + i + "]")
					.evaluate(document);
			if (cognome != null && !"".equalsIgnoreCase(cognome)) {
				vaContatto.setCognomeRiferimentoPub(cognome);
			}
			if (nome != null && !"".equalsIgnoreCase(nome)) {
				vaContatto.setNomeRiferimentoPub(nome);
			}
			if (fax != null && !"".equalsIgnoreCase(fax)) {
				vaContatto.setFaxRiferimentoPub(fax);
			}
			if (email != null && !"".equalsIgnoreCase(email)) {
				vaContatto.setMail(email);
			}
			if (telefono != null && !"".equalsIgnoreCase(telefono)) {
				vaContatto.setTelRiferimentoPub(telefono);
			}

			vaContatto.setDtmMod(now);
			vaContatto.setPfPrincipalMod(principal);
		}
	}

	public List<VaDatiVacancy> findAllWithoutPOI() {
		TypedQuery<VaDatiVacancy> query = entityManager.createNamedQuery("findAllWithoutPOI", VaDatiVacancy.class);

		query.setParameter("dtScadenzaPubblicazione", new Date());
		query.setMaxResults(150);
		List<VaDatiVacancy> list = query.getResultList();
		return list;
	}

	public List<NotificaScadenzaVacancyDTO> findAllInScadenza() {
		// Data corrente
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		// Manipola la data corrente aggiungendo n_giorni
		int n_giorni = 5;
		c.add(Calendar.DATE, n_giorni);
		// Converte il Calendar in Date
		Date currentDatePlus5 = c.getTime();
		// Invoco la query e ritorno la lista di vacancy
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<NotificaScadenzaVacancyDTO> cq = cb.createQuery(NotificaScadenzaVacancyDTO.class);
		// FROM
		Root<VaDatiVacancy> root = cq.from(VaDatiVacancy.class);
		Join<VaDatiVacancy, PfPrincipal> joinPfPrincipal = root.join(VaDatiVacancy_.pfPrincipal);
		Join<VaDatiVacancy, DeProvenienza> joinDeProvenienza = root.join(VaDatiVacancy_.deProvenienzaVacancy);
		// SELECT
		cq.select(cb.construct(NotificaScadenzaVacancyDTO.class, root.get(VaDatiVacancy_.idVaDatiVacancy),
				root.get(VaDatiVacancy_.numAnno), root.get(VaDatiVacancy_.numRichiesta),
				joinDeProvenienza.get(DeProvenienza_.codProvenienza), root.get(VaDatiVacancy_.attivitaPrincipale),
				root.get(VaDatiVacancy_.dtPubblicazione), root.get(VaDatiVacancy_.dtScadenzaPubblicazione),
				joinPfPrincipal.get(PfPrincipal_.email)));
		// WHERE
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(cb.equal(root.get(VaDatiVacancy_.dtScadenzaPubblicazione), currentDatePlus5));
		whereConditions.add(cb.equal(root.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.PUB));
		whereConditions.add(cb.equal(root.get(VaDatiVacancy_.opzTipoDecodifiche), "S"));
		whereConditions.add(cb.equal(joinDeProvenienza.get(DeProvenienza_.codProvenienza),
				ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
		whereConditions.add(cb.greaterThanOrEqualTo(root.get(VaDatiVacancy_.numAnno), 2017));

		cq.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<NotificaScadenzaVacancyDTO> typedQuery = entityManager.createQuery(cq);
		List<NotificaScadenzaVacancyDTO> results = typedQuery.getResultList();

		return results;
	}

	public VaDatiVacancy findVacancyByKey(String numAnno, String numRichiesta, String codProvenienza) {
		VaDatiVacancy vac = null;
		TypedQuery<VaDatiVacancy> query = entityManager.createNamedQuery("findVacancyByKey", VaDatiVacancy.class)
				.setParameter("numAnno", new Integer(numAnno)).setParameter("numRichiesta", new Integer(numRichiesta))
				.setParameter("codProvenienza", codProvenienza);
		List<VaDatiVacancy> list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			vac = list.get(0);
		}

		return vac;
	}

	public void pubblica(VaDatiVacancy data, Integer idPfPrincipalMod) {
		data.setCodStatoVacancyEnum(CodStatoVacancyEnum.PUB);
		data.setDtmMod(new Date());

		merge(data, idPfPrincipalMod);
	}

	public void pubblica(VaDatiVacancyDTO data, Integer idPfPrincipalMod) {
		data.setCodStatoVacancyEnum(CodStatoVacancyEnum.PUB);
		data.setDtmMod(new Date());
		data.setIdPrincipalMod(idPfPrincipalMod);
		merge(fromDTO(data));
	}

	public void archiviaDTO(VaDatiVacancyDTO data, Integer idPfPrincipalMod) {
		data.setCodStatoVacancyEnum(CodStatoVacancyEnum.ARC);
		data.setDtmMod(new Date());
		data.setIdPrincipalMod(idPfPrincipalMod);
		data.setMotivazione("");
		data.setDtArchiviazione(new Date());
		data.setUtenteArchiviazione(idPfPrincipalMod);
		merge(fromDTO(data));
	}

	public void archivia(VaDatiVacancy data, Integer idPfPrincipalMod, String motivazione) {
		VaDatiVacancy toArc = findById(data.getIdVaDatiVacancy());
		PfPrincipal usr = pfPrincipalHome.findById(idPfPrincipalMod);
		toArc.setCodStatoVacancyEnum(CodStatoVacancyEnum.ARC);
		toArc.setDtmMod(new Date());
		toArc.setPfPrincipalMod(usr);
		toArc.setMotivazione(motivazione);
		toArc.setDtArchiviazione(new Date());
		toArc.setUtenteArchiviazione(usr);
		super.merge(toArc);
	}

	public void proroga(VaDatiVacancyDTO data, Integer idPrincipalMod) {
		if (data.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB)) {
			// Aggiungo 30 giorni alla data di scadenza
			Calendar scadenza = Calendar.getInstance();
			scadenza.setTime(data.getScadenzaPubblicazione());
			scadenza.add(Calendar.DAY_OF_MONTH, 30);
			data.setScadenzaPubblicazione(scadenza.getTime());
			data.setDtmMod(new Date());
			data.setIdPrincipalMod(idPrincipalMod);
			merge(fromDTO(data));
		} else {
			log.error(
					"codestatovacancy non è in stato di programmazione, quindi non è possibile rinnovare la vacanza scaduta con proroga");
			throw new MyPortalException("Vacancy scadenza date error");
		}
	}

	public void prorogaRER(VaDatiVacancy data, Integer idPfPrincipalMod) {
		VaDatiVacancy toArc = findById(data.getIdVaDatiVacancy());
		prorgaRERNew(toDTO(toArc), idPfPrincipalMod);
	}

	public void prorgaRERNew(VaDatiVacancyDTO data, Integer idPrincipalMod) {
		if (data.getCodStatoVacancyEnum().equals(CodStatoVacancyEnum.PUB)) {
			// Aggiungo 15 giorni alla data di scadenza
			Calendar scadenza = Calendar.getInstance();
			scadenza.setTime(data.getScadenzaPubblicazione());
			scadenza.add(Calendar.DAY_OF_MONTH, 15);
			data.setScadenzaPubblicazione(scadenza.getTime());
			data.setDtmMod(new Date());
			data.setIdPrincipalMod(idPrincipalMod);
			merge(fromDTO(data));
		} else {
			log.error(
					"codestatovacancy non è in stato di programmazione, quindi non è possibile rinnovare la vacanza scaduta con proroga");
			throw new MyPortalException("Vacancy scadenza date error");
		}
	}

	@Override
	public VaDatiVacancy persist(VaDatiVacancy entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);

		Poi poi = poiHome.createOrUpdatePoi(entity);
		entity.setPoi(poi);

		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}

	@Override
	public void remove(VaDatiVacancy entity) {
		VaDatiVacancy vaEntity = findById(entity.getIdVaDatiVacancy());
		super.remove(vaEntity);
	}

	@Override
	public VaDatiVacancy merge(VaDatiVacancy in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

	public VaDatiVacancy createDefaultVacancy() {
		VaDatiVacancy vaDatiVacancy = new VaDatiVacancy();
		int numAnno = Calendar.getInstance().get(Calendar.YEAR);
		vaDatiVacancy.setNumAnno(numAnno);
		DeProvenienza provenienzaMyPortal = deProvenienzaHome.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL);
		vaDatiVacancy.setDeProvenienzaVacancy(provenienzaMyPortal);

		// spostati
		// Integer numRichiesta = getNextNumRichiesta(numAnno, provenienzaMyPortal.getCodProvenienza());
		// vaDatiVacancy.setNumRichiesta(numRichiesta);

		DeMansione nt = deMansioneHome.findById("NT");
		vaDatiVacancy.setDeMansione(nt);

		vaDatiVacancy.setFlagAnonima(false);
		vaDatiVacancy.setFlagEliminata(false);
		vaDatiVacancy.setFlagInvioCl(false);
		vaDatiVacancy.setFlagIdo(true);
		vaDatiVacancy.setVisibilita(true);

		vaDatiVacancy.setCodStatoVacancyEnum(CodStatoVacancyEnum.LAV);

		// FIXME temporaneo per persist, va usato il comune
		vaDatiVacancy.setIndirizzoLavoro("");

		return vaDatiVacancy;
	}

	public List<NotificaInLavVacancyDTO> findAllInLav(Integer numGGVacInLav) {
		// Data corrente
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int n_giorni = numGGVacInLav.intValue();
		c.add(Calendar.DATE, -n_giorni);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date currentDatePlus5 = c.getTime();

		// Invoco la query e ritorno la lista di vacancy
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<NotificaInLavVacancyDTO> cq = cb.createQuery(NotificaInLavVacancyDTO.class);
		// FROM
		Root<VaDatiVacancy> root = cq.from(VaDatiVacancy.class);
		Join<VaDatiVacancy, PfPrincipal> joinPfPrincipal = root.join(VaDatiVacancy_.pfPrincipal);
		Join<VaDatiVacancy, DeProvenienza> joinDeProvenienza = root.join(VaDatiVacancy_.deProvenienzaVacancy);
		// SELECT
		cq.select(cb.construct(NotificaInLavVacancyDTO.class, root.get(VaDatiVacancy_.idVaDatiVacancy),
				root.get(VaDatiVacancy_.numAnno), root.get(VaDatiVacancy_.numRichiesta),
				joinDeProvenienza.get(DeProvenienza_.codProvenienza), root.get(VaDatiVacancy_.attivitaPrincipale),
				root.get(VaDatiVacancy_.dtmIns), root.get(VaDatiVacancy_.dtmMod),
				joinPfPrincipal.get(PfPrincipal_.email)));
		// WHERE
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		Expression<Calendar> dateTruncExpr = cb
				.function("date_trunc", Calendar.class, cb.literal("day"), root.get(VaDatiVacancy_.dtmIns))
				.as(Calendar.class);

		whereConditions.add(cb.equal(dateTruncExpr, currentDatePlus5));
		whereConditions.add(cb.equal(root.get(VaDatiVacancy_.codStatoVacancyEnum), CodStatoVacancyEnum.LAV));
		whereConditions.add(cb.equal(root.get(VaDatiVacancy_.flagIdo), true));
		whereConditions.add(cb.equal(root.get(VaDatiVacancy_.flagEliminata), false));
		whereConditions.add(cb.equal(joinDeProvenienza.get(DeProvenienza_.codProvenienza),
				ConstantsSingleton.DeProvenienza.COD_MYPORTAL));

		cq.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<NotificaInLavVacancyDTO> typedQuery = entityManager.createQuery(cq);
		List<NotificaInLavVacancyDTO> results = typedQuery.getResultList();

		return results;
	}

}
