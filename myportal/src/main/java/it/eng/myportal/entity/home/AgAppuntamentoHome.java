package it.eng.myportal.entity.home;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import it.eng.myportal.dtos.AgAppuntamentoDTO;
import it.eng.myportal.dtos.AppuntamentoDTO;
import it.eng.myportal.dtos.AppuntamentoFilterDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.AgAppAnagrafica;
import it.eng.myportal.entity.AgAppAnagrafica_;
import it.eng.myportal.entity.AgAppuntamento;
import it.eng.myportal.entity.AgAppuntamento_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.YgAdesione;
import it.eng.myportal.entity.YgAdesione_;
import it.eng.myportal.entity.YgGaranziaOver;
import it.eng.myportal.entity.YgGaranziaOver_;
import it.eng.myportal.entity.decodifiche.DeAmbienteSil;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeCpi_;
import it.eng.myportal.entity.decodifiche.DeTipoAppuntamento;
import it.eng.myportal.entity.decodifiche.DeTipoAppuntamento_;
import it.eng.myportal.entity.enums.TipoAppuntamentoEnum;
import it.eng.myportal.entity.home.decodifiche.DeAmbienteSilHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoAppuntamentoHome;
import it.eng.myportal.exception.DisponibilitaAppuntamentoException;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.output.Risposta.DatiAppuntamento;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.coop.webservices.agenda.appuntamento.AppuntamentoServiceProxy;

/**
 * 
 * @author Enrico
 */
@Stateless
public class AgAppuntamentoHome extends AbstractUpdatableHome<AgAppuntamento, AgAppuntamentoDTO> {

	private static String ESITO_POSITIVO = "00";

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private DeTipoAppuntamentoHome deTipoAppuntamentoHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private YgGaranziaOverHome ygGaranziaOverHome;

	@EJB
	private DeAmbienteSilHome deAmbienteSilHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	public AgAppuntamento findById(Integer id) {
		AgAppuntamento obj = findById(AgAppuntamento.class, id);
		return obj;
	}

	@Override
	public AgAppuntamentoDTO toDTO(AgAppuntamento entity) {
		if (entity == null) {
			return null;
		}
		AgAppuntamentoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdAgAppuntamento());
		dto.setAmbiente(entity.getAmbiente());
		dto.setDeCpiDTO(deCpiHome.toDTO(entity.getDeCpi()));
		dto.setDenominazioneCpi(entity.getDenominazioneCpi());
		dto.setDeTipoAppuntamentoDTO(deTipoAppuntamentoHome.toDTO(entity.getDeTipoAppuntamento()));
		dto.setDtAppuntamento(entity.getDtAppuntamento());
		dto.setIndirizzoCpiStampa(entity.getIndirizzoCpiStampa());
		dto.setOraAppuntamento(entity.getOraAppuntamento());
		dto.setPfPrincipalDTO(pfPrincipalHome.toDTO(entity.getPfPrincipal()));
		dto.setSiglaOperatore(entity.getSiglaOperatore());
		dto.setYgAdesione(entity.getYgAdesione());
		return dto;
	}

	@Override
	public AgAppuntamento fromDTO(AgAppuntamentoDTO dto) {
		if (dto == null) {
			return null;
		}
		AgAppuntamento entity = super.fromDTO(dto);
		entity.setIdAgAppuntamento(dto.getId());
		entity.setAmbiente(dto.getAmbiente());
		entity.setDeCpi(deCpiHome.findById(dto.getDeCpiDTO().getId()));
		entity.setDenominazioneCpi(dto.getDenominazioneCpi());
		entity.setDeTipoAppuntamento(deTipoAppuntamentoHome.findById(dto.getDeTipoAppuntamentoDTO().getId()));
		entity.setDtAppuntamento(dto.getDtAppuntamento());
		entity.setIndirizzoCpiStampa(dto.getIndirizzoCpiStampa());
		entity.setOraAppuntamento(dto.getOraAppuntamento());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getPfPrincipalDTO().getId()));
		entity.setSiglaOperatore(dto.getSiglaOperatore());
		entity.setYgAdesione(dto.getYgAdesione());
		return entity;
	}

	public AgAppuntamentoDTO findDTOAppuntamentoYG(Integer idPfPrincipal, Integer idAdesione) {
		if (idPfPrincipal == null) {
			return null;
		}
		AgAppuntamentoDTO result = null;

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AgAppuntamento> query = qb.createQuery(AgAppuntamento.class);
		Root<AgAppuntamento> agAppuntamento = query.from(AgAppuntamento.class);
		Join<AgAppuntamento, YgAdesione> ygAdesione = agAppuntamento.join(AgAppuntamento_.ygAdesione);
		Join<AgAppuntamento, PfPrincipal> pfPrincipal = agAppuntamento.join(AgAppuntamento_.pfPrincipal);
		Join<AgAppuntamento, DeTipoAppuntamento> deTipoAppuntamento = agAppuntamento
				.join(AgAppuntamento_.deTipoAppuntamento);
		Predicate principalPredicate = qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		Predicate tipoAppuntamentoPredicate = qb.equal(deTipoAppuntamento.get(DeTipoAppuntamento_.codTipoAppuntamento),
				TipoAppuntamentoEnum.PL_APPGG.getCodTipoAppuntamento());

		Predicate adePredicate = qb.equal(ygAdesione.get(YgAdesione_.idYgAdesione), idAdesione);

		query.where(qb.and(principalPredicate, tipoAppuntamentoPredicate, adePredicate));

		TypedQuery<AgAppuntamento> q = entityManager.createQuery(query);

		List<AgAppuntamento> resultList = q.getResultList();
		if (resultList.size() == 1) {
			result = toDTO(resultList.get(0));
		}
		return result;
	}

	public AgAppuntamentoDTO findDTOAppuntamentoGaranziaOver(Integer idPfPrincipal, Integer idAdesione) {
		if (idPfPrincipal == null || idAdesione == null) {
			return null;
		}
		AgAppuntamentoDTO result = null;

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AgAppuntamento> query = qb.createQuery(AgAppuntamento.class);
		Root<AgAppuntamento> agAppuntamento = query.from(AgAppuntamento.class);
		Join<AgAppuntamento, YgGaranziaOver> ygGaranziaOver = agAppuntamento.join(AgAppuntamento_.idYgGaranziaOver);
		Join<AgAppuntamento, PfPrincipal> pfPrincipal = agAppuntamento.join(AgAppuntamento_.pfPrincipal);
		Join<AgAppuntamento, DeTipoAppuntamento> deTipoAppuntamento = agAppuntamento
				.join(AgAppuntamento_.deTipoAppuntamento);
		Predicate principalPredicate = qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		Predicate tipoAppuntamentoPredicate = qb.equal(deTipoAppuntamento.get(DeTipoAppuntamento_.codTipoAppuntamento),
				TipoAppuntamentoEnum.PL_APPGO.getCodTipoAppuntamento());

		Predicate adePredicate = qb.equal(ygGaranziaOver.get(YgGaranziaOver_.idYgGaranziaOver), idAdesione);

		query.where(qb.and(principalPredicate, tipoAppuntamentoPredicate, adePredicate));

		TypedQuery<AgAppuntamento> q = entityManager.createQuery(query);

		List<AgAppuntamento> resultList = q.getResultList();
		if (resultList.size() > 0) {
			result = toDTO(resultList.get(0));
		}
		return result;

	}

	public AppuntamentoDTO findAppuntamentoDTObyIdAgAppuntamento(Integer idAgAppuntamento) {
		AppuntamentoDTO result;

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AppuntamentoDTO> cq = cb.createQuery(AppuntamentoDTO.class);
		Root<AgAppAnagrafica> agAppAnagrafica = cq.from(AgAppAnagrafica.class);
		Join<AgAppAnagrafica, AgAppuntamento> agAppuntamento = agAppAnagrafica.join(AgAppAnagrafica_.agAppuntamento);
		Join<AgAppuntamento, DeCpi> deCpi = agAppuntamento.join(AgAppuntamento_.deCpi);

		cq.where(cb.equal(agAppuntamento.get(AgAppuntamento_.idAgAppuntamento), idAgAppuntamento));

		cq.select(cb.construct(AppuntamentoDTO.class, agAppuntamento.get(AgAppuntamento_.idAgAppuntamento),
				agAppuntamento.get(AgAppuntamento_.deTipoAppuntamento),
				agAppuntamento.get(AgAppuntamento_.dtAppuntamento),
				agAppuntamento.get(AgAppuntamento_.oraAppuntamento),
				agAppuntamento.get(AgAppuntamento_.denominazioneCpi),
				agAppuntamento.get(AgAppuntamento_.indirizzoCpiStampa),
				agAppuntamento.get(AgAppuntamento_.siglaOperatore), agAppuntamento.get(AgAppuntamento_.ambiente),
				deCpi.get(DeCpi_.telPatronato), agAppAnagrafica.get(AgAppAnagrafica_.codiceFiscale),
				agAppAnagrafica.get(AgAppAnagrafica_.nome), agAppAnagrafica.get(AgAppAnagrafica_.cognome)));

		TypedQuery<AppuntamentoDTO> query = entityManager.createQuery(cq);
		result = query.getSingleResult();
		return result;
	}

	public List<AppuntamentoDTO> findAppuntamentoPatronatoDTObyFilter(Integer idPfPrincipal,
			AppuntamentoFilterDTO filter) {
		List<AppuntamentoDTO> result;

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AppuntamentoDTO> cq = cb.createQuery(AppuntamentoDTO.class);
		Root<AgAppAnagrafica> agAppAnagrafica = cq.from(AgAppAnagrafica.class);
		Join<AgAppAnagrafica, AgAppuntamento> agAppuntamento = agAppAnagrafica.join(AgAppAnagrafica_.agAppuntamento);
		Join<AgAppuntamento, DeCpi> deCpi = agAppuntamento.join(AgAppuntamento_.deCpi);

		cq = cq.select(cb.construct(AppuntamentoDTO.class, agAppuntamento.get(AgAppuntamento_.idAgAppuntamento),
				agAppuntamento.get(AgAppuntamento_.deTipoAppuntamento),
				agAppuntamento.get(AgAppuntamento_.dtAppuntamento),
				agAppuntamento.get(AgAppuntamento_.oraAppuntamento),
				agAppuntamento.get(AgAppuntamento_.denominazioneCpi),
				agAppuntamento.get(AgAppuntamento_.indirizzoCpiStampa),
				agAppuntamento.get(AgAppuntamento_.siglaOperatore), agAppuntamento.get(AgAppuntamento_.ambiente),
				deCpi.get(DeCpi_.telPatronato), agAppAnagrafica.get(AgAppAnagrafica_.codiceFiscale),
				agAppAnagrafica.get(AgAppAnagrafica_.nome), agAppAnagrafica.get(AgAppAnagrafica_.cognome)));

		List<Predicate> predicates = new ArrayList<Predicate>();
		if (filter.getCodiceFiscale() != null) {
			if (filter.getRicercaEsatta()) {
				predicates.add(cb.equal(cb.upper(agAppAnagrafica.get(AgAppAnagrafica_.codiceFiscale)), filter
						.getCodiceFiscale().toUpperCase()));
			} else {
				predicates.add(cb.like(cb.upper(agAppAnagrafica.get(AgAppAnagrafica_.codiceFiscale)), filter
						.getCodiceFiscale().toUpperCase() + "%"));
			}
		}
		if (filter.getCognome() != null) {
			if (filter.getRicercaEsatta()) {
				predicates.add(cb.equal(cb.upper(agAppAnagrafica.get(AgAppAnagrafica_.cognome)), filter.getCognome()
						.toUpperCase()));
			} else {
				predicates.add(cb.like(cb.upper(agAppAnagrafica.get(AgAppAnagrafica_.cognome)), filter.getCognome()
						.toUpperCase() + "%"));
			}
		}
		if (filter.getNome() != null) {
			if (filter.getRicercaEsatta()) {
				predicates.add(cb.equal(cb.upper(agAppAnagrafica.get(AgAppAnagrafica_.nome)), filter.getNome()
						.toUpperCase()));
			} else {
				predicates.add(cb.like(cb.upper(agAppAnagrafica.get(AgAppAnagrafica_.nome)), filter.getNome()
						.toUpperCase() + "%"));
			}
		}
		if (filter.getDataDal() != null) {
			predicates.add(cb.greaterThanOrEqualTo(agAppuntamento.get(AgAppuntamento_.dtAppuntamento),
					filter.getDataDal()));
		}
		if (filter.getDataAl() != null) {
			predicates
					.add(cb.lessThanOrEqualTo(agAppuntamento.get(AgAppuntamento_.dtAppuntamento), filter.getDataAl()));
		}
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
		predicates.add(cb.equal(agAppAnagrafica.get(AgAppAnagrafica_.pfPrincipalIns), pfPrincipal));

		cq = cq.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<AppuntamentoDTO> query = entityManager.createQuery(cq);
		result = query.getResultList();
		return result;
	}

	public List<it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta.ElencoDisponibilita.DatiAppuntamento> disponibilitaAppuntamentoOver(
			Integer principalId, Integer idYgGaranziaOver, Date data, Date dataA, String mattinaPomeriggio,
			Integer idAmbienteSil, String codCpi) {
		if (principalId == null) {
			log.error("Prenotaziona appuntamento GaranziaOver fallita: id utente non passato");
		}

		// Recupero i dati dell'utente.
		UtenteCompletoDTO utenteCompletoDTO = utenteInfoHome.findDTOCompletoById(principalId);
		if (utenteCompletoDTO == null) {
			log.error("Prenotaziona appuntamento GaranziaOver fallita: recupero utente nullo id= " + principalId);
		}

		if (StringUtils.isEmpty(utenteCompletoDTO.getCodiceFiscale())) {
			log.error("Prenotaziona appuntamento GaranziaOver fallita: Il cittadino non ha indicato il suo codice fiscale.");
		}

		// Recupero i dati dell'adesione a Umbriattiva Adulti
		YgGaranziaOver adesione = ygGaranziaOverHome.findById(idYgGaranziaOver);
		if (adesione.getDtPresaInCarico() != null) {
			log.error("Prenotaziona appuntamento GaranziaOver fallita: E' stato già fissato un appuntamento per la tua adesione.");
		}

		String codProvinciaAdesione = null;
		if (adesione.getDeProvincia() != null) {
			codProvinciaAdesione = adesione.getDeProvincia().getCodProvincia();
		} else {
			log.error("Prenotaziona appuntamento GaranziaOver fallita: l'adesione non ha una provincia di riferimento.");
		}

		// Chiamo il ws del SIL della provincia di adesione per la lista degli slot.
		List<Risposta.ElencoDisponibilita.DatiAppuntamento> res = new ArrayList<Risposta.ElencoDisponibilita.DatiAppuntamento>();
		try {
			res = disponibilitaAppuntamento(data, dataA, mattinaPomeriggio, codProvinciaAdesione, codCpi,
					idAmbienteSil, TipoAppuntamentoEnum.PL_APPGO);
		} catch (Exception e) {
			log.error("Errore in disponibilitaAppuntamentoOver(): " + e.getMessage());
		}
		return res;
	}

	public List<Risposta.ElencoDisponibilita.DatiAppuntamento> disponibilitaAppuntamentoYG(Integer idPfPrincipal,
			Integer idYgAdesione, Date dataDa, Date dataA, String mattinaPomeriggio, Integer idAmbienteSil) {
		if (idPfPrincipal == null) {
			log.error("Prenotaziona appuntamento fallita: id utente non passato");
		}

		UtenteCompletoDTO utenteCompletoDTO = utenteInfoHome.findDTOCompletoById(idPfPrincipal);
		if (utenteCompletoDTO == null) {
			log.error("Prenotaziona appuntamento fallita: recupero utente nullo id= " + idPfPrincipal);
		}

		if (StringUtils.isEmpty(utenteCompletoDTO.getCodiceFiscale())) {
			log.error("Il cittadino non ha indicato il suo codice fiscale.");
		}

		DeProvinciaDTO provinciaRiferimento = utenteCompletoDTO.getProvinciaRiferimento();
		YgAdesione adesione = ygAdesioneHome.findById(idYgAdesione);
		if (provinciaRiferimento == null) {
			log.error("Il cittadino non ha selezionato alcuna provincia di riferimento.");
		}
		if (adesione.getDtPresaInCarico() != null) {
			log.error("E' stato già fissato un appuntamento per la tua adesione.");
		}

		List<Risposta.ElencoDisponibilita.DatiAppuntamento> res = new ArrayList<Risposta.ElencoDisponibilita.DatiAppuntamento>();
		try {
			res = disponibilitaAppuntamento(dataDa, dataA, mattinaPomeriggio, provinciaRiferimento.getId(), adesione
					.getDeCpiAssegnazione().getCodCpi(), idAmbienteSil, TipoAppuntamentoEnum.PL_APPGG);
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage());
		}
		return res;
	}

	public StreamedContent createPromemoriaPdfFile(Integer idAgAppuntamento) {
		StreamedContent result = null;

		String promemoriaHtmlFile = getPromemoriaHtmlFile(idAgAppuntamento);

		result = new DefaultStreamedContent(Utils.PDF.htmlToPDF(promemoriaHtmlFile,
				"patronato/promemoria_appuntamento.xhtml"), "application/pdf", "Promemoria_Appuntamento.pdf");

		return result;
	}

	private String getPromemoriaHtmlFile(Integer idAgAppuntamento) {
		String result = null;
		WebClient webClient = new WebClient();
		webClient.getOptions().setJavaScriptEnabled(false);
		HtmlPage requestedPage = null;

		try {
			requestedPage = webClient.getPage(ConstantsSingleton.BASE_URL
					+ "/faces/public/print/patronato/promemoria_appuntamento.xhtml" + "?idAgAppuntamento="
					+ idAgAppuntamento);
		} catch (Exception e) {
			String errMsg = "Errore durante il recupero del documento XML: patronato/promemoria_appuntamento.xhtml. idAgAppuntamento = "
					+ idAgAppuntamento;
			log.error(errMsg);
			log.error("Eccezione originale: " + e.getClass() + " - " + e.getLocalizedMessage());
			throw new MyPortalException(errMsg, e, true);
		}
		if (requestedPage != null) {
			result = requestedPage.asXml();
		}

		return result;
	}

	public List<Risposta.ElencoDisponibilita.DatiAppuntamento> disponibilitaAppuntamentoPatronato(Date dataDa,
			Date dataA, String mattinaPomeriggio, String codProvincia, String codCpi, Integer idAmbienteSil)
			throws RemoteException, DisponibilitaAppuntamentoException {

		return disponibilitaAppuntamento(dataDa, dataA, mattinaPomeriggio, codProvincia, codCpi, idAmbienteSil,
				TipoAppuntamentoEnum.PL_APPSE);
	}

	public List<Risposta.ElencoDisponibilita.DatiAppuntamento> disponibilitaAppuntamentoOrientamentoBase(Date dataDa,
			Date dataA, String mattinaPomeriggio, String codProvincia, String codCpi, Integer idAmbienteSil)
			throws RemoteException, DisponibilitaAppuntamentoException {

		return disponibilitaAppuntamento(dataDa, dataA, mattinaPomeriggio, codProvincia, codCpi, idAmbienteSil,
				TipoAppuntamentoEnum.PL_APPOB);
	}

	private List<Risposta.ElencoDisponibilita.DatiAppuntamento> disponibilitaAppuntamento(Date dataDa, Date dataA,
			String mattinaPomeriggio, String codProvincia, String codCpi, Integer idAmbienteSil,
			TipoAppuntamentoEnum tipoAppuntamento) throws DisponibilitaAppuntamentoException, RemoteException {
		String outputXML = null;
		List<Risposta.ElencoDisponibilita.DatiAppuntamento> res = new ArrayList<Risposta.ElencoDisponibilita.DatiAppuntamento>();
		try {
			// viene passato il PRG della tabella del SIL per mappatura codici
			// ambiente
			Integer prgAmbienteSil = null;
			if (idAmbienteSil != null) {
				DeAmbienteSil deAmbienteSil = deAmbienteSilHome.findById(idAmbienteSil);
				prgAmbienteSil = deAmbienteSil.getPrgAmbienteSil();
			}

			String inputXML = marshallDispAppuntamentoInput(dataDa, dataA, mattinaPomeriggio, codProvincia, codCpi,
					prgAmbienteSil, tipoAppuntamento);

			/* invocazione del servizio */
			AppuntamentoServiceProxy service = new AppuntamentoServiceProxy(
					wsEndpointHome.getAppuntamentoAddress(codProvincia));
			outputXML = service.getDispAppuntamento(inputXML);

			/* unmarshalling messaggio in output */
			it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta risposta = unmarshallDispAppuntamentoOutput(outputXML);

			if (risposta.getEsito().getCodice().equals(ESITO_POSITIVO)) {
				if (risposta.getElencoDisponibilita() != null) {
					res = risposta.getElencoDisponibilita().getDatiAppuntamento();
				}
			}
		} catch (RemoteException e) {
			log.error(e.getMessage());
			log.error("response = " + outputXML);
			throw e;
		} catch (DisponibilitaAppuntamentoException e) {
			log.error(e.getMessage());
			log.error("response = " + outputXML);
			throw e;
		}
		return res;
	}

	private String marshallDispAppuntamentoInput(Date dataDa, Date dataA, String mattinaPomeriggio,
			String codProvincia, String codCpi, Integer prgAmbienteSil, TipoAppuntamentoEnum tipoAppuntamento)
			throws DisponibilitaAppuntamentoException {
		String inputXML = "";
		XMLGregorianCalendar dataDaXMLGregCal = null;
		XMLGregorianCalendar dataAXMLGregCal = null;
		try {
			if (dataDa != null) {
				GregorianCalendar dataDaGregCal = new GregorianCalendar();
				dataDaGregCal.setTime(dataDa);
				dataDaXMLGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataDaGregCal);
			}
			if (dataA != null) {
				GregorianCalendar dataAGregCal = new GregorianCalendar();
				dataAGregCal.setTime(dataA);
				dataAXMLGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataAGregCal);
			}
		} catch (DatatypeConfigurationException e) {
			throw new DisponibilitaAppuntamentoException(e);
		}

		it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.input.ObjectFactory objectFactory = new it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.input.ObjectFactory();
		it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.input.DispAppuntamento dispAppuntamento = objectFactory
				.createDispAppuntamento();
		dispAppuntamento.setIdProvincia(codProvincia);
		it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.input.DispAppuntamento.ParametriAppuntamento parametriAppuntamento = objectFactory
				.createDispAppuntamentoParametriAppuntamento();
		parametriAppuntamento.setCodiceRichiesta(tipoAppuntamento.getCodTipoAppuntamento());
		it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.input.DispAppuntamento.ParametriAppuntamento.DatiRicerca datiRicerca = objectFactory
				.createDispAppuntamentoParametriAppuntamentoDatiRicerca();
		if (prgAmbienteSil != null) {
			datiRicerca.setAmbiente(BigInteger.valueOf(prgAmbienteSil.intValue()));
		}
		datiRicerca.setDataAl(dataAXMLGregCal);
		datiRicerca.setDataDal(dataDaXMLGregCal);
		datiRicerca.setMattinaPomeriggio(mattinaPomeriggio);
		parametriAppuntamento.setDatiRicerca(datiRicerca);
		parametriAppuntamento.setIdCPI(codCpi);
		dispAppuntamento.setParametriAppuntamento(parametriAppuntamento);
		dispAppuntamento.setUtenteServizio("L");

		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.input.DispAppuntamento.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			Schema schema = Utils.getXsdSchema("appuntamento" + File.separator + "InputDispAppuntamento.xsd");
			jaxbMarshaller.setSchema(schema);
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(dispAppuntamento, writer);
			inputXML = writer.getBuffer().toString();
		} catch (JAXBException e) {
			log.error(e);
		} catch (SAXException e) {
			log.error(e);
		}

		return inputXML;
	}

	private it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta unmarshallDispAppuntamentoOutput(
			String outputXml) {
		it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta risposta = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta> root = unmarshaller
					.unmarshal(new StreamSource(new StringReader(outputXml)),
							it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta.class);
			risposta = root.getValue();
		} catch (JAXBException e) {
			log.error(e.getMessage());
		}
		return risposta;
	}

	public Integer persistAppuntamento(Integer idPfPrincipal, DatiAppuntamento datiAppuntamento,
			TipoAppuntamentoEnum tipoAppuntamento, YgAdesione adesione) throws DatatypeConfigurationException,
			ParseException {
		AgAppuntamento agAppuntamento = new AgAppuntamento();
		agAppuntamento.setPfPrincipal(pfPrincipalHome.findById(idPfPrincipal));
		if (datiAppuntamento.getDataAppuntamento() != null) {
			agAppuntamento.setDtAppuntamento(Utils.gregorianDateToDate(datiAppuntamento.getDataAppuntamento()));
		}
		if (datiAppuntamento.getOraAppuntamento() != null) {
			agAppuntamento.setOraAppuntamento(datiAppuntamento.getOraAppuntamento());
		}
		if (datiAppuntamento.getIdCPI() != null) {
			agAppuntamento.setDeCpi(deCpiHome.findById(datiAppuntamento.getIdCPI()));
		}
		if (datiAppuntamento.getDenominazioneCPI() != null) {
			agAppuntamento.setDenominazioneCpi(datiAppuntamento.getDenominazioneCPI());
		}
		if (datiAppuntamento.getIndirizzoCPIstampa() != null) {
			agAppuntamento.setIndirizzoCpiStampa(datiAppuntamento.getIndirizzoCPIstampa());
		}
		if (datiAppuntamento.getSiglaOperatore() != null) {
			agAppuntamento.setSiglaOperatore(datiAppuntamento.getSiglaOperatore());
		}
		if (adesione != null) {
			agAppuntamento.setYgAdesione(adesione);
		}
		if (datiAppuntamento.getAmbiente() != null) {
			agAppuntamento.setAmbiente(datiAppuntamento.getAmbiente());
		}
		agAppuntamento
				.setDeTipoAppuntamento(deTipoAppuntamentoHome.findById(tipoAppuntamento.getCodTipoAppuntamento()));
		Date now = new Date();
		agAppuntamento.setDtmIns(now);
		agAppuntamento.setDtmMod(now);
		agAppuntamento.setPfPrincipalIns(pfPrincipalHome.findById(idPfPrincipal));
		agAppuntamento.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));

		persist(agAppuntamento);

		return agAppuntamento.getIdAgAppuntamento();
	}

	public Integer persistAppuntamentoGaranziaOver(Integer idPfPrincipal, DatiAppuntamento datiAppuntamento,
			TipoAppuntamentoEnum tipoAppuntamento, YgGaranziaOver adesione) throws DatatypeConfigurationException,
			ParseException {
		AgAppuntamento agAppuntamento = new AgAppuntamento();
		agAppuntamento.setPfPrincipal(pfPrincipalHome.findById(idPfPrincipal));
		if (datiAppuntamento.getDataAppuntamento() != null) {
			agAppuntamento.setDtAppuntamento(Utils.gregorianDateToDate(datiAppuntamento.getDataAppuntamento()));
		}
		if (datiAppuntamento.getOraAppuntamento() != null) {
			agAppuntamento.setOraAppuntamento(datiAppuntamento.getOraAppuntamento());
		}
		if (datiAppuntamento.getIdCPI() != null) {
			agAppuntamento.setDeCpi(deCpiHome.findById(datiAppuntamento.getIdCPI()));
		}
		if (datiAppuntamento.getDenominazioneCPI() != null) {
			agAppuntamento.setDenominazioneCpi(datiAppuntamento.getDenominazioneCPI());
		}
		if (datiAppuntamento.getIndirizzoCPIstampa() != null) {
			agAppuntamento.setIndirizzoCpiStampa(datiAppuntamento.getIndirizzoCPIstampa());
		}
		if (datiAppuntamento.getSiglaOperatore() != null) {
			agAppuntamento.setSiglaOperatore(datiAppuntamento.getSiglaOperatore());
		}
		if (adesione != null) {
			agAppuntamento.setIdYgGaranziaOver(adesione);
		}
		if (datiAppuntamento.getAmbiente() != null) {
			agAppuntamento.setAmbiente(datiAppuntamento.getAmbiente());
		}
		agAppuntamento
				.setDeTipoAppuntamento(deTipoAppuntamentoHome.findById(tipoAppuntamento.getCodTipoAppuntamento()));
		Date now = new Date();
		agAppuntamento.setDtmIns(now);
		agAppuntamento.setDtmMod(now);
		agAppuntamento.setPfPrincipalIns(pfPrincipalHome.findById(idPfPrincipal));
		agAppuntamento.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));

		persist(agAppuntamento);

		return agAppuntamento.getIdAgAppuntamento();
	}

	/*
	 * Restituisce l'appuntamento piu' recente preso dall'utente passato in input
	 */
	public AppuntamentoDTO findLatestAppuntamento(Integer idPfPrincipal, TipoAppuntamentoEnum tipoAppuntamento) {
		if (idPfPrincipal == null) {
			return null;
		}
		AppuntamentoDTO result = null;

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AppuntamentoDTO> cq = qb.createQuery(AppuntamentoDTO.class);
		Root<AgAppAnagrafica> agAppAnagrafica = cq.from(AgAppAnagrafica.class);
		Join<AgAppAnagrafica, AgAppuntamento> agAppuntamento = agAppAnagrafica.join(AgAppAnagrafica_.agAppuntamento);
		Join<AgAppuntamento, PfPrincipal> pfPrincipal = agAppuntamento.join(AgAppuntamento_.pfPrincipal);
		Join<AgAppuntamento, DeTipoAppuntamento> deTipoAppuntamento = agAppuntamento
				.join(AgAppuntamento_.deTipoAppuntamento);
		Join<AgAppuntamento, DeCpi> deCpi = agAppuntamento.join(AgAppuntamento_.deCpi);

		Predicate principalPredicate = qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		Predicate tipoAppuntamentoPredicate = qb.equal(deTipoAppuntamento.get(DeTipoAppuntamento_.codTipoAppuntamento),
				tipoAppuntamento.getCodTipoAppuntamento());

		cq.where(qb.and(principalPredicate, tipoAppuntamentoPredicate));
		cq.orderBy(qb.desc(agAppuntamento.get(AgAppuntamento_.dtAppuntamento)));

		cq.select(qb.construct(AppuntamentoDTO.class, agAppuntamento.get(AgAppuntamento_.idAgAppuntamento),
				agAppuntamento.get(AgAppuntamento_.deTipoAppuntamento),
				agAppuntamento.get(AgAppuntamento_.dtAppuntamento),
				agAppuntamento.get(AgAppuntamento_.oraAppuntamento),
				agAppuntamento.get(AgAppuntamento_.denominazioneCpi),
				agAppuntamento.get(AgAppuntamento_.indirizzoCpiStampa),
				agAppuntamento.get(AgAppuntamento_.siglaOperatore), agAppuntamento.get(AgAppuntamento_.ambiente),
				deCpi.get(DeCpi_.telPatronato), agAppAnagrafica.get(AgAppAnagrafica_.codiceFiscale),
				agAppAnagrafica.get(AgAppAnagrafica_.nome), agAppAnagrafica.get(AgAppAnagrafica_.cognome)));

		TypedQuery<AppuntamentoDTO> query = entityManager.createQuery(cq);
		query.setMaxResults(1);
		List<AppuntamentoDTO> resultList = query.getResultList();
		if (resultList.size() == 1) {
			result = resultList.get(0);
		}
		return result;

		// CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		// CriteriaQuery<AgAppuntamento> query =
		// qb.createQuery(AgAppuntamento.class);
		// Root<AgAppuntamento> agAppuntamento =
		// query.from(AgAppuntamento.class);
		// Join<AgAppuntamento, PfPrincipal> pfPrincipal =
		// agAppuntamento.join(AgAppuntamento_.pfPrincipal);
		// Join<AgAppuntamento, DeTipoAppuntamento> deTipoAppuntamento =
		// agAppuntamento
		// .join(AgAppuntamento_.deTipoAppuntamento);
		// Predicate principalPredicate =
		// qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		// Predicate tipoAppuntamentoPredicate =
		// qb.equal(deTipoAppuntamento.get(DeTipoAppuntamento_.codTipoAppuntamento),
		// tipoAppuntamento.getCodTipoAppuntamento());
		//
		// query.where(qb.and(principalPredicate, tipoAppuntamentoPredicate));
		// query.orderBy(qb.desc(agAppuntamento.get(AgAppuntamento_.dtAppuntamento)));
		//
		// TypedQuery<AgAppuntamento> q = entityManager.createQuery(query);
		// q.setMaxResults(1);
		//
		// List<AgAppuntamento> resultList = q.getResultList();
		// if (resultList.size() == 1) {
		// result = toDTO(resultList.get(0));
		// }
		// return result;
	}

}
