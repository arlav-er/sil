package it.eng.myportal.entity.ejb;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.validation.Schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import it.eng.myportal.cliclavoro.vacancy.AltreInformazioni;
import it.eng.myportal.cliclavoro.vacancy.CondizioniOfferte;
import it.eng.myportal.cliclavoro.vacancy.DatiAnagrafici;
import it.eng.myportal.cliclavoro.vacancy.DatiContatto;
import it.eng.myportal.cliclavoro.vacancy.DatiSistema;
import it.eng.myportal.cliclavoro.vacancy.DatoreLavoro;
import it.eng.myportal.cliclavoro.vacancy.DurataRichiesta;
import it.eng.myportal.cliclavoro.vacancy.IstruzioneFormazione;
import it.eng.myportal.cliclavoro.vacancy.Lingua;
import it.eng.myportal.cliclavoro.vacancy.ProfiloRichiesto;
import it.eng.myportal.cliclavoro.vacancy.Richiesta;
import it.eng.myportal.cliclavoro.vacancy.SiNo;
import it.eng.myportal.cliclavoro.vacancy.Titolostudio;
import it.eng.myportal.cliclavoro.vacancy.Vacancy;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.SedeDTO;
import it.eng.myportal.entity.AbstractEntity;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.Poi;
import it.eng.myportal.entity.VaAlbo;
import it.eng.myportal.entity.VaAltreInfo;
import it.eng.myportal.entity.VaCompetenzeTrasv;
import it.eng.myportal.entity.VaContatto;
import it.eng.myportal.entity.VaContratto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaInformatica;
import it.eng.myportal.entity.VaIstruzione;
import it.eng.myportal.entity.VaLingua;
import it.eng.myportal.entity.VaOrario;
import it.eng.myportal.entity.VaPatente;
import it.eng.myportal.entity.VaPatentino;
import it.eng.myportal.entity.VaRetribuzione;
import it.eng.myportal.entity.VaVacancyCl;
import it.eng.myportal.entity.decodifiche.DeAlbo;
import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.decodifiche.DeAutorizzazioneSare;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeOrario;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.DePatentino;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeRetribuzione;
import it.eng.myportal.entity.decodifiche.DeTipoComunicazioneCl;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.min.DeAttivitaMin;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.decodifiche.sil.DeAttivitaSil;
import it.eng.myportal.entity.decodifiche.sil.DeMansioneSil;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;
import it.eng.myportal.entity.home.MaPatenteHome;
import it.eng.myportal.entity.home.MaPatentinoHome;
import it.eng.myportal.entity.home.MaRapportoLavoroHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.PoiHome;
import it.eng.myportal.entity.home.SvAziendaInfoHome;
import it.eng.myportal.entity.home.VaAlboHome;
import it.eng.myportal.entity.home.VaAltreInfoHome;
import it.eng.myportal.entity.home.VaCompetenzeTrasvHome;
import it.eng.myportal.entity.home.VaContrattoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.VaInformaticaHome;
import it.eng.myportal.entity.home.VaIstruzioneHome;
import it.eng.myportal.entity.home.VaLinguaHome;
import it.eng.myportal.entity.home.VaOrarioHome;
import it.eng.myportal.entity.home.VaPatenteHome;
import it.eng.myportal.entity.home.VaPatentinoHome;
import it.eng.myportal.entity.home.VaRetribuzioneHome;
import it.eng.myportal.entity.home.VaVacancyClHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaSilHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoChiusuraHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeRetribuzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeSvTemplateHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoComunicazioneClHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.enums.AzioneServizio;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.AzioneCliclavoro;
import it.eng.myportal.utils.ConstantsSingleton.DeStatoInvioCl;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;
import it.gov.lavoro.servizi.cliclavoro.CliclavoroWSProxy;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioVacancy_Type;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_TypeTipo_Risposta;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_Type;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioVacancy_TypeTipo_Risposta;

/**
 * Session Bean implementation class ClicLavoroVacancyEjb
 */
@Stateless
public class ClicLavoroVacancyEjb {

	protected final Log log = LogFactory.getLog(ClicLavoroVacancyEjb.class);

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	@EJB
	DeCpiHome deCpiHome;
	
	@EJB
	PoiHome poiHome;

	@EJB
	DeProvenienzaHome deProvenienzaHome;

	@EJB
	MaRapportoLavoroHome maRapportoLavoroHome;

	@EJB
	MaPatenteHome maPatenteHome;

	@EJB
	MaPatentinoHome maPatentinoHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	VaVacancyClHome vaVacancyClHome;

	@EJB
	DeTipoComunicazioneClHome deTipoComunicazioneClHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	DeAttivitaMinHome deAttivitaMinHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	VaIstruzioneHome vaIstruzioneHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	ClicLavoroEjb clicLavoroEjb;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	VaLinguaHome vaLinguaHome;

	@EJB
	DeGradoLinHome deGradoLinHome;

	@EJB
	VaPatenteHome vaPatenteHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	VaPatentinoHome vaPatentinoHome;

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	VaInformaticaHome vaInformaticaHome;

	@EJB
	VaAlboHome vaAlboHome;

	@EJB
	DeAlboHome deAlboHome;

	@EJB
	VaContrattoHome vaContrattoHome;

	@EJB
	DeContrattoHome deContrattoHome;

	CliclavoroWSProxy inviaVacancy;

	@EJB
	NotificationBuilder notificationBuilder;

	@EJB
	SvAziendaInfoHome svAziendaInfoHome;

	@EJB
	DeSvTemplateHome deSvTemplateHome;

	@EJB
	VaRetribuzioneHome vaRetribuzioneHome;

	@EJB
	DeRetribuzioneHome deRetribuzioneHome;

	@EJB
	DeAttivitaSilHome deAttivitaSilHome;

	@EJB
	DeMotivoChiusuraHome deMotivoChiusuraHome;

	@EJB
	VaOrarioHome vaOrarioHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	VaCompetenzeTrasvHome vaCompetenzeTrasvHome;

	@EJB
	VaAltreInfoHome vaAltreInfoHome;

	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;

	@EJB
	TsGetOpzioniEJB tsGetOpzioniEJB;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@PersistenceContext
	protected EntityManager entityManager;

	public Vacancy createVacancyClicLavoro(VaDatiVacancy vaDatiVacancy) {
		try {
			Vacancy vacancy = new Vacancy();
			DatiSistema datiSistema = creaDatiSistema(vaDatiVacancy);
			DatoreLavoro datoreLavoro = creaDatoreLavoro(vaDatiVacancy);
			Richiesta richiesta = creaRichiesta(vaDatiVacancy);
			AltreInformazioni altreInformazioni = creaAltreInformazioni(vaDatiVacancy);

			vacancy.setDatiSistema(datiSistema);
			vacancy.setDatoreLavoro(datoreLavoro);
			vacancy.setRichiesta(richiesta);
			vacancy.setAltreInformazioni(altreInformazioni);

			return vacancy;
		} catch (DatatypeConfigurationException e) {
			throw new MyPortalException("Errore durante l'invio a ClicLavoro", e, true);
		}

	}

	private AltreInformazioni creaAltreInformazioni(VaDatiVacancy vaDatiVacancy) {
		AltreInformazioni altreInformazioni = new AltreInformazioni();

		if (vaDatiVacancy.getOpzNullaOsta() != null && "Y".equals(vaDatiVacancy.getOpzNullaOsta())) {
			altreInformazioni.setNO(SiNo.SI);
		} else {
			altreInformazioni.setNO(SiNo.NO);
		}

		return altreInformazioni;
	}

	private Richiesta creaRichiesta(VaDatiVacancy vaDatiVacancy) throws DatatypeConfigurationException {
		Richiesta richiesta = new Richiesta();

		CondizioniOfferte condizioniOfferte = creaCondizioniOfferte(vaDatiVacancy);
		DurataRichiesta durataRichiesta = creaDurataRichiesta(vaDatiVacancy);
		IstruzioneFormazione istruzioneFormazione = creaIstruzioneFormazione(vaDatiVacancy);
		ProfiloRichiesto profiloRichiesto = creaProfiloRichiesto(vaDatiVacancy);

		richiesta.setCondizioniOfferte(condizioniOfferte);
		richiesta.setDurataRichiesta(durataRichiesta);
		richiesta.setIstruzioneFormazione(istruzioneFormazione);
		richiesta.setProfiloRichiesto(profiloRichiesto);
		return richiesta;
	}

	private ProfiloRichiesto creaProfiloRichiesto(VaDatiVacancy vaDatiVacancy) {
		ProfiloRichiesto profiloRichiesto = new ProfiloRichiesto();
		// Questo deve rimanere null
		profiloRichiesto.setDescrizioneesperienza(null);
		profiloRichiesto.setDescrizionericerca(Utils.cut(vaDatiVacancy.getAttivitaDescrizioneEstesa(), 5000));
		if (vaDatiVacancy.getMansioneMin() != null) {
			profiloRichiesto.setDescrprofessione(Utils.cut(vaDatiVacancy.getMansioneMin().getDescrizione(), 250));
		}
		if (vaDatiVacancy.getOpzEsperienze() != null) {
			if (vaDatiVacancy.getOpzEsperienze().equals("S") || vaDatiVacancy.getOpzEsperienze().equals("Y")) {
				profiloRichiesto.setEsperienzarichiesta(SiNo.SI);
			} else if (vaDatiVacancy.getOpzEsperienze().equals("N")) {
				profiloRichiesto.setEsperienzarichiesta(SiNo.NO);
			}
		}
		/**
		 * TODO La professione NON PUO' essere NULL, Qua va messo a punto questo controllo
		 */
		profiloRichiesto.setIdprofessione(vaDatiVacancy.getMansioneMin() == null ? null : vaDatiVacancy
				.getMansioneMin().getDeMansioneSil().getCodMansioneSil());
		profiloRichiesto.setNumerolavoratori(vaDatiVacancy.getNumLavRicercati() > 99 ? "99" : vaDatiVacancy
				.getNumLavRicercati().toString());
		return profiloRichiesto;
	}

	/**
	 * @param vaDatiVacancy
	 * @return
	 */
	private IstruzioneFormazione creaIstruzioneFormazione(VaDatiVacancy vaDatiVacancy) {
		IstruzioneFormazione istruzioneFormazione = new IstruzioneFormazione();
		if (vaDatiVacancy.getVaCompetenzeTrasv() != null) {
			istruzioneFormazione.setAltrecapacita(vaDatiVacancy.getVaCompetenzeTrasv().getCapCompSintesiCl());
		}
		if (vaDatiVacancy.getVaInformatica() != null && vaDatiVacancy.getVaInformatica().getConoscenzaInfo() != null) {
			istruzioneFormazione.setConoscenzeinformatiche(Utils.cut(vaDatiVacancy.getVaInformatica()
					.getConoscenzaInfo(), 1000));
		}

		List<VaAlbo> vaAlbos = vaDatiVacancy.getVaAlbos();
		for (VaAlbo vaAlbo : vaAlbos) {
			if (vaAlbo.getDeAlbo().getFlagMin()) {
				istruzioneFormazione.setIdalbo(vaAlbo.getDeAlbo().getCodAlbo());
				break;
			}
		}
		if (vaDatiVacancy.getVaAltreInfo() == null || vaDatiVacancy.getVaAltreInfo().getOpzAutomunito() == null) {
			istruzioneFormazione.setIdmezzitrasporto(SiNo.NO);
		} else if (vaDatiVacancy.getVaAltreInfo().getOpzAutomunito().equals("S")
				|| vaDatiVacancy.getVaAltreInfo().getOpzAutomunito().equals("Y")
				|| vaDatiVacancy.getVaAltreInfo().getOpzMotomunito().equals("S")
				|| vaDatiVacancy.getVaAltreInfo().getOpzMotomunito().equals("Y")) {
			istruzioneFormazione.setIdmezzitrasporto(SiNo.SI);
		} else if (vaDatiVacancy.getVaAltreInfo().getOpzAutomunito().equals("N")
				&& vaDatiVacancy.getVaAltreInfo().getOpzMotomunito().equals("N")) {
			istruzioneFormazione.setIdmezzitrasporto(SiNo.NO);
		}

		List<String> patenteGuidas = istruzioneFormazione.getIdpatenteguida();
		creaListaPatentiGuida(patenteGuidas, vaDatiVacancy);
		List<String> patentinos = istruzioneFormazione.getIdpatentino();
		creaListaPatentini(patentinos, vaDatiVacancy);
		List<Lingua> linguas = istruzioneFormazione.getLingua();
		creaListaLingue(linguas, vaDatiVacancy);
		List<Titolostudio> titolostudios = istruzioneFormazione.getTitolostudio();
		creaListaTitoliStudio(titolostudios, vaDatiVacancy);

		if (vaDatiVacancy.getVaAltreInfo() == null || vaDatiVacancy.getVaAltreInfo().getOpzFuorisede() == null) {
			istruzioneFormazione.setTrasferte(SiNo.NO);
		} else if (vaDatiVacancy.getVaAltreInfo().getOpzFuorisede().equals("Y")
				|| vaDatiVacancy.getVaAltreInfo().getOpzFuorisede().equals("S")) {
			istruzioneFormazione.setTrasferte(SiNo.SI);
		} else if (vaDatiVacancy.getVaAltreInfo().getOpzFuorisede().equals("N")) {
			istruzioneFormazione.setTrasferte(SiNo.NO);
		}
		istruzioneFormazione.setUlteriorirequisiti(vaDatiVacancy.getVaAltreInfo() == null ? null : vaDatiVacancy
				.getVaAltreInfo().getUlterioriRequisiti());
		return istruzioneFormazione;
	}

	/**
	 * @param titolostudios
	 * @param vaDatiVacancy
	 */
	private void creaListaTitoliStudio(List<Titolostudio> titolostudios, VaDatiVacancy vaDatiVacancy) {
		Set<VaIstruzione> vaIstruziones = vaDatiVacancy.getVaIstruziones();
		for (VaIstruzione vaIstruzione : vaIstruziones) {
			Titolostudio titolostudio = new Titolostudio();

			/**
			 * TODO se il titolo non è mappato che si fa?
			 */
			String codTitolo = vaIstruzione.getDeTitolo().getCodTitoloClicLavoro();
			if (codTitolo == null)
				break;
			titolostudio.setIdtitolostudio(vaIstruzione.getDeTitolo().getCodTitoloClicLavoro());
			titolostudio.setDescrizionestudio(vaIstruzione.getDeTitolo().getDescrizione());
			titolostudios.add(titolostudio);
		}

	}

	/**
	 * 
	 * @param linguas
	 * @param vaDatiVacancy
	 */
	private void creaListaLingue(List<Lingua> linguas, VaDatiVacancy vaDatiVacancy) {
		Set<VaLingua> vaLinguas = vaDatiVacancy.getVaLinguas();

		for (VaLingua vaLingua : vaLinguas) {
			Lingua lingua = new Lingua();
			lingua.setIdlingua(vaLingua.getDeLingua().getCodLingua());
			// Se madrelingua si imposta la lingua senza valorizzare i gradi di
			// conoscenza
			if (!vaLingua.getFlagMadrelingua()) {

				lingua.setIdlivelloletto(vaLingua.getDeGradoLinByCodGradoLetto().getCodGrado());
				lingua.setIdlivelloparlato(vaLingua.getDeGradoLinByCodGradoParlato().getCodGrado());
				lingua.setIdlivelloscritto(vaLingua.getDeGradoLinByCodGradoScritto().getCodGrado());
			}
			linguas.add(lingua);
		}

	}

	private void creaListaPatentini(List<String> patentinos, VaDatiVacancy vaDatiVacancy) {
		Set<VaPatentino> vaPatentinos = vaDatiVacancy.getVaPatentinos();
		for (VaPatentino vaPatentino : vaPatentinos) {
			if (vaPatentino.getDePatentino() != null && vaPatentino.getDePatentino().getMaPatentino() != null) {
				String patentino = vaPatentino.getDePatentino().getMaPatentino().getCodPatentinoMin();
				Utils.addUniqueMapping(patentinos, patentino);
			}
		}

	}

	/**
	 * 
	 * @param patenteGuidas
	 * @param vaDatiVacancy
	 */
	private void creaListaPatentiGuida(List<String> patenteGuidas, VaDatiVacancy vaDatiVacancy) {
		Set<VaPatente> vaPatentes = vaDatiVacancy.getVaPatentes();
		for (VaPatente vaPatente : vaPatentes) {
			/**
			 * TODO cosa fare se la patente non è codificata?
			 */
			if (vaPatente.getDePatente() != null && vaPatente.getDePatente().getMaPatente() != null) {
				String patente = vaPatente.getDePatente().getMaPatente().getCodPatenteMin();
				Utils.addUniqueMapping(patenteGuidas, patente);
			}
		}

	}

	/**
	 * 
	 * @param vaDatiVacancy
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	private DurataRichiesta creaDurataRichiesta(VaDatiVacancy vaDatiVacancy) throws DatatypeConfigurationException {
		DurataRichiesta durataRichiesta = new DurataRichiesta();
		durataRichiesta.setDatapubblicazione(Utils.toXMLGregorianCalendar(vaDatiVacancy.getDtPubblicazione()));
		durataRichiesta.setDatascadenza(Utils.toXMLGregorianCalendar(vaDatiVacancy.getDtScadenzaPubblicazione()));
		return durataRichiesta;
	}

	/**
	 * 
	 * @param vaDatiVacancyR
	 * @return
	 */
	private CondizioniOfferte creaCondizioniOfferte(VaDatiVacancy vaDatiVacancy) {
		CondizioniOfferte condizioniOfferte = new CondizioniOfferte();
		List<VaContratto> vaContrattos = vaDatiVacancy.getVaContrattos();
		condizioniOfferte.setIdtipologiacontratto(vaContrattos.get(0).getDeContratto().getCodContratto());
		Set<VaOrario> vaOrarios = vaDatiVacancy.getVaOrarios();
		for (VaOrario vaOrario : vaOrarios) {
			condizioniOfferte.setIdmodalitalavoro(vaOrario.getDeOrario().getCodOrario());
			break;
		}
		BigDecimal media = null;
		if (vaDatiVacancy.getVaRetribuzione() != null) {
			BigDecimal limMin = vaDatiVacancy.getVaRetribuzione().getDeRetribuzione().getLimInfDecimale();
			BigDecimal limMax = vaDatiVacancy.getVaRetribuzione().getDeRetribuzione().getLimSupDecimale();
			media = limMax.add(limMin).divide(new BigDecimal(2));
			media = media.setScale(0, BigDecimal.ROUND_DOWN);
		}

		// Da non valorizzare
		condizioniOfferte.setDurata(null);
		// Da non valorizzare
		condizioniOfferte.setIdccnl(null);
		condizioniOfferte.setIdcomune(vaDatiVacancy.getDeComune().getCodCom());
		// Da non valorizzare
		condizioniOfferte.setIdlivellocontrattuale(null);
		condizioniOfferte.setRetribuzioneannualorda(media == null ? null : media.toString());
		// Da non valorizzare
		condizioniOfferte.setUlterioricondizioni(null);
		return condizioniOfferte;
	}

	/**
	 * 
	 * @param vaDatiVacancy
	 * @return
	 */
	private DatoreLavoro creaDatoreLavoro(VaDatiVacancy vaDatiVacancy) {
		DatoreLavoro datoreLavoro = new DatoreLavoro();
		DatiAnagrafici datiAnagrafici = creaDatiAnagrafici(vaDatiVacancy);
		DatiContatto datiContatto = creaDatiContatto(vaDatiVacancy);
		datoreLavoro.setDatiAnagrafici(datiAnagrafici);
		datoreLavoro.setDatiContatto(datiContatto);
		return datoreLavoro;
	}

	/**
	 * 
	 * @param vaDatiVacancy
	 * @return
	 */
	private DatiContatto creaDatiContatto(VaDatiVacancy vaDatiVacancy) {
		DatiContatto datiContatto = new DatiContatto();
		datiContatto.setCap(vaDatiVacancy.getDeComune().getCap());

		datiContatto.setIdcomune(vaDatiVacancy.getDeComune().getCodCom());
		datiContatto.setIndirizzo(vaDatiVacancy.getIndirizzoLavoro());
		if (!vaDatiVacancy.getVisibilita()) {
			VaContatto vaContattoPrinc = vaDatiVacancy.getVaContattoPrinc();
			if (vaContattoPrinc != null) {
				datiContatto.setTelefono(vaContattoPrinc.getTelRiferimentoPub());
				datiContatto.setFax(vaContattoPrinc.getFaxRiferimentoPub());
				datiContatto.setEmail(vaContattoPrinc.getMail());
			}
		} else {
			VaContatto vaContatto = vaDatiVacancy.getVaContattoAlt();
			/**
			 * fix 27/05/2013 - Davide se non trovo i dati del contatto alternativo allora torno a prendermi quelli del
			 * contatto principale
			 */
			if (vaContatto == null) {
				vaContatto = vaDatiVacancy.getVaContattoPrinc();
			}
			datiContatto.setTelefono(vaContatto.getTelRiferimentoPub());
			datiContatto.setFax(vaContatto.getFaxRiferimentoPub());
			datiContatto.setEmail(vaContatto.getMail());
		}

		return datiContatto;
	}

	/**
	 * @param vaDatiVacancy
	 *            I campi valorizzati a null non devono essere valorizzati (da analisi)
	 * @return
	 */
	private DatiAnagrafici creaDatiAnagrafici(VaDatiVacancy vaDatiVacancy) {
		DatiAnagrafici datiAnagrafici = new DatiAnagrafici();
		AziendaInfo aziendaInfo = vaDatiVacancy.getPfPrincipal().getAziendaInfo();
		datiAnagrafici.setAmpiezza(null);
		/**
		 * Nel caso in cui la vacancy proviede da sil ed è intermediata allora prendo il CF dell'azienda palese,
		 * altrimenti lasciamo il vecchio 27/05/2013 - Davide
		 */
		if (vaDatiVacancy.isFromSIL() && vaDatiVacancy.isIntermediata()) {
			PfPrincipal palese = vaDatiVacancy.getPfPrincipalPalese();
			if (palese != null) {
				if (palese.getAziendaInfo() != null) {
					aziendaInfo = palese.getAziendaInfo();
				}

			}
		}
		datiAnagrafici.setCodicefiscale(aziendaInfo.getCodiceFiscale().toUpperCase());
		datiAnagrafici.setDenominazione(aziendaInfo.getRagioneSociale());

		if (vaDatiVacancy.getAttivitaMin() != null) {
			datiAnagrafici.setSettore(vaDatiVacancy.getAttivitaMin().getCodAttivitaDot());
		}
		datiAnagrafici.setWeb(null);
		return datiAnagrafici;
	}

	/**
	 * 
	 * @param vaDatiVacancy
	 * @return
	 */
	private DatiSistema creaDatiSistema(VaDatiVacancy vaDatiVacancy) {
		DatiSistema datiSistema = new DatiSistema();
		DeCpi deCpi = null;
		if (vaDatiVacancy.getIntermediario() != null) {
			deCpi = deCpiHome.findByCodIntermediarioCl(vaDatiVacancy.getIntermediario());
		}

		// da non valorizzare
		datiSistema.setCodicefiscaleintermediario(null);
		if (vaDatiVacancy.getVaVacancyCl() == null) {
			log.error("Errore! non c'e' l'oggetto VaVacancyCl per la vacancy " + vaDatiVacancy.getIdVaDatiVacancy());
			throw new MyPortalException("cliclavoro.novacancycl");
		}
		datiSistema.setCodiceofferta(vaDatiVacancy.getVaVacancyCl().getCodComunicazione());
		datiSistema.setCodiceoffertaintermediario(vaDatiVacancy.getNumAnno() + "/" + vaDatiVacancy.getNumRichiesta());
		datiSistema.setCodiceoffertaprecedente(vaDatiVacancy.getVaVacancyCl().getCodComunicazionePrec());

		// Controllo: se la vacancy e' intermediata inserisco i dati
		// dell'intermediario
		if (vaDatiVacancy.isOriginariaSIL() && deCpi != null) {

			datiSistema.setIntermediario(deCpi.getCodIntermediarioCl());
			datiSistema.setDenominazioneintermediario("Centro per l'impiego di " + deCpi.getDescrizione());
			datiSistema.setIndirizzo(deCpi.getIndirizzo());
			datiSistema.setIdcomune(deCpi.getDeComune().getCodCom());
			datiSistema.setCap(deCpi.getCap());
			datiSistema.setTelefono(deCpi.getTelRifCl());
			datiSistema.setFax(deCpi.getFax());
			if (vaDatiVacancy.getPfPrincipalPalese() != null) {
				datiSistema.setVisibilita(SiNo.SI);
			} else {
				datiSistema.setVisibilita(SiNo.NO);
			}
		} else {
			datiSistema.setVisibilita(SiNo.SI);
		}
		// Il contatto principale e' obblicatorio, quindi non devo controllare
		// che sia diverso da null
		datiSistema.setEmail(vaDatiVacancy.getVaContattoPrinc().getMail());
		if (vaDatiVacancy.getMotivoChiusura() != null) {
			datiSistema.setMotivochiusura(vaDatiVacancy.getMotivoChiusura().getCodMotivoChiusura());
		}
		datiSistema.setTipoofferta(vaDatiVacancy.getVaVacancyCl().getDeTipoComunicazioneCl()
				.getCodTipoComunicazioneCl());
		return datiSistema;
	}

	private String convertToString(Vacancy vacancy) throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(Vacancy.class);
		Marshaller marshaller = jc.createMarshaller();
		Schema schema = Utils.getXsdSchema("cliclavoro" + File.separator + "ricerca_personale.xsd");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		marshaller.setSchema(schema);
		StringWriter writer = new StringWriter();
		// marshaller.setEventHandler(eventHandler);
		marshaller.marshal(vacancy, writer);
		String xmlRichiesta = writer.getBuffer().toString();
		return xmlRichiesta;
	}

	public Vacancy convertToVacancy(String xmlVacancy) throws JAXBException {
		JAXBContext jaxbContext;
		Vacancy vacancy = null;
		try {
			jaxbContext = JAXBContext.newInstance(Vacancy.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			vacancy = (Vacancy) jaxbUnmarshaller.unmarshal(new StringReader(xmlVacancy));
		} catch (JAXBException e) {
			log.error("Errore durante la costruzione dell'oggetto dall'xml:" + e.getMessage());
		}
		return vacancy;
	}

	/**
	 * Crea una comunicazione da inviare a partire dalla vacancy e la inserisce nella tabella delle comunicazioni da
	 * inviare.
	 * 
	 * @param vaVacancyCl
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ClInvioComunicazione creaComunicazione(VaVacancyCl vaVacancyCl) {
		log.info("Creo la comunicazione relativa alla candidatura " + vaVacancyCl.getCodComunicazione() + " in  stato "
				+ vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl());
		Date now = new Date();
		PfPrincipal amministratore = pfPrincipalHome.findById(0);
		ClInvioComunicazione comunicazione = new ClInvioComunicazione();
		comunicazione.setDtmIns(now);
		comunicazione.setDtmMod(now);
		comunicazione.setPfPrincipalIns(amministratore);
		comunicazione.setPfPrincipalMod(amministratore);
		comunicazione.setAzioneServizio(AzioneServizio.INVIO_VACANCY);
		comunicazione.setCodComunicazione(vaVacancyCl.getCodComunicazione());
		comunicazione.setDeStatoInvioCl(vaVacancyCl.getDeStatoInvioCl());
		comunicazione.setFlagInviato(false);
		comunicazione.setMittente("MYPORTAL");
		comunicazione.setDestinatario(ConstantsSingleton.DeProvenienza.COD_MINISTERO);
		entityManager.persist(comunicazione);
		return comunicazione;
	}

	/**
	 * Effettua l'invio di una vaVacancyCl verso clicLavoro
	 * 
	 * @param vaVacancyCl
	 * @param comunicazione
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws RemoteException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void inviaVacancy(VaVacancyCl vaVacancyCl, ClInvioComunicazione comunicazione) throws JAXBException,
			SAXException {
		try {
			log.info("Invio la comunicazione " + vaVacancyCl.getCodComunicazione());
			// genera la candidatura
			Vacancy vacancy = createVacancyClicLavoro(vaVacancyCl.getVaDatiVacancy());
			// convertila in xml
			String xmlRichiesta = convertToString(vacancy);
			// costruisci la richiesta
			Richiesta_invioVacancy_Type richiesta = new Richiesta_invioVacancy_Type();
			richiesta.setVacancyXML(xmlRichiesta);
			// salva l'xml costruito
			comunicazione.setFileComunicazione(xmlRichiesta);
			entityManager.merge(comunicazione);

			// effettua l'invio
			String inviaVacancyAddress = wsEndpointHome.getClicLavoroInviaVacancyAddress();
			inviaVacancy = new CliclavoroWSProxy(inviaVacancyAddress);
			Risposta_invioVacancy_Type response = inviaVacancy.invioVacancy(richiesta);
			log.info("Invio effettuato");
			String nuovoCodice;
			String vecchioCodice = vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl();
			// se l'invio è andato a buon fine, imposto il nuovo stato
			// 'completato' alla comunicazione
			if (response.getTipo_Risposta().getValue().equals(Risposta_invioCandidatura_TypeTipo_Risposta._OK)) {
				log.info("Response: OK");
				nuovoCodice = deStatoInvioClHome.calcolaStatoInvioCL(vecchioCodice, AzioneCliclavoro.CLICLAVORO_OK);
				if (nuovoCodice == null) {
					throw new MyPortalException("Stato invio comunicazione non riconosciuto");
				}
			}
			// se l'invio è andato in errore
			else {
				log.info("Response: KO");
				// salvo l'errore
				comunicazione.setDescrizioneErrore(response.getDescr_Esito());
				nuovoCodice = deStatoInvioClHome.calcolaStatoInvioCL(vecchioCodice, AzioneCliclavoro.CLICLAVORO_ERR);
				if (nuovoCodice == null) {
					throw new MyPortalException("Stato invio comunicazione non riconosciuto");
				}

			}
			log.info("Il nuovo stato della comunicazione è " + nuovoCodice);
			// salva il nuovo stato
			it.eng.myportal.entity.decodifiche.DeStatoInvioCl nuovoStato = deStatoInvioClHome.findById(nuovoCodice);
			comunicazione.setDeStatoInvioCl(nuovoStato);
			vaVacancyCl.setDeStatoInvioCl(nuovoStato);
			comunicazione.setFlagInviato(true);
		} catch (MarshalException me) {
			log.error("Errore durante la costruzione dell'xml" + me.getMessage());
			comunicazione.setDescrizioneErrore("Errore di costruzione xml: " + me.getLinkedException().getMessage());
			String vecchioCodice = vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl();
			String nuovoCodice = deStatoInvioClHome.calcolaStatoInvioCL(vecchioCodice, AzioneCliclavoro.CLICLAVORO_ERR);
			comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
			comunicazione.setFlagInviato(false);
			vaVacancyCl.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
		} catch (RemoteException e) {
			log.error("Errore durante l'invio dell'xml" + e.getMessage());
			comunicazione.setDescrizioneErrore("Errore di invio xml: " + e.getMessage());
			String vecchioCodice = vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl();
			String nuovoCodice = deStatoInvioClHome.calcolaStatoInvioCL(vecchioCodice, AzioneCliclavoro.CLICLAVORO_ERR);
			comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
			comunicazione.setFlagInviato(false);
			vaVacancyCl.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
		} catch (Exception ex) {
			log.error("Errore inaspettato durante l'invio dell'xml" + ex.getMessage());
			comunicazione.setDescrizioneErrore("Errore di invio xml: " + ex.getMessage());
			String vecchioCodice = vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl();
			String nuovoCodice = deStatoInvioClHome.calcolaStatoInvioCL(vecchioCodice, AzioneCliclavoro.CLICLAVORO_ERR);
			comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
			comunicazione.setFlagInviato(false);
			vaVacancyCl.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
		} finally {
			// segna che l'invio è stato effettuato e aggiorna i record
			// vacancy e comunicazione
			log.info("aggiorno la vacancy su DB");
			entityManager.merge(comunicazione);
			log.info("aggiorno la vacancy su DB");
			entityManager.merge(vaVacancyCl);
		}
	}

	public ClInvioComunicazione setClComunicazioneInvioFromPDD(String xml, String codComunicazione, String tipoAzione) {
		Date now = new Date();
		PfPrincipal amministratore = pfPrincipalHome.findById(0);
		ClInvioComunicazione comunicazione = new ClInvioComunicazione();
		comunicazione.setFileComunicazione(xml);
		if (("INVIOCANDIDATURA").equalsIgnoreCase(tipoAzione)) {
			comunicazione.setAzioneServizio(AzioneServizio.INVIO_CANDIDATURA);
		} else if (("INVIOVACANCY").equalsIgnoreCase(tipoAzione)) {
			comunicazione.setAzioneServizio(AzioneServizio.INVIO_VACANCY);
		} else if (("INVIOMESSAGGIO").equalsIgnoreCase(tipoAzione)) {
			comunicazione.setAzioneServizio(AzioneServizio.INVIO_MESSAGGIO);
		}
		comunicazione.setCodComunicazione(codComunicazione);
		comunicazione.setFlagInviato(false);
		comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById("PA"));
		comunicazione.setMittente(ConstantsSingleton.DeProvenienza.COD_MINISTERO);
		comunicazione.setDestinatario("MYPORTAL");
		comunicazione.setDtmIns(now);
		comunicazione.setDtmMod(now);
		comunicazione.setPfPrincipalIns(amministratore);
		comunicazione.setPfPrincipalMod(amministratore);

		entityManager.persist(comunicazione);
		return comunicazione;
	}

	/**
	 * Ricezione vacancy da PDD/SIL, inserisce la vacancy partendo dall'xml ricevuto
	 * 
	 * 
	 * @param messaggio
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void riceviVacancyCliclavoro(Vacancy vacancy, String provenienza) {
		String codComRic = vacancy.getDatiSistema().getCodiceofferta();
		String codComPrecRic = vacancy.getDatiSistema().getCodiceoffertaprecedente();

		// Se il codice comunicazione == null errore
		if (codComRic == null) {
			throw new MyPortalException("Errore codice comunicazione nullo", true);
		}

		if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
			VaVacancyCl vaVacancyCl = vaVacancyClHome.findByCodComunicazione(codComRic);
			VaDatiVacancy vaDatiVacancy = null;
			if (codComPrecRic != null) {
				// caso di rettifica
				if (vaVacancyCl == null) {
					VaVacancyCl vaVacancyClPrec = vaVacancyClHome.findByCodComunicazione(codComPrecRic);
					if (vaVacancyClPrec != null) {
						// prima rettifica
						VaDatiVacancy vacancyDaModificare = vaDatiVacancyHome.findById(vaVacancyClPrec
								.getIdVaDatiVacancy());
						VaDatiVacancy vacancyModificata = costruisciVacancyCliclavoro(vacancy, provenienza, true,
								vacancyDaModificare);

						costruisciVacancyCl(vacancy, vacancyModificata, vaVacancyClPrec, provenienza);
					} else {
						// dalla seconda rettifica in poi devo controllare che
						// ho lo stesso codcomunicazioneprec (è quello della
						// comunicazione originaria)
						vaVacancyClPrec = vaVacancyClHome.findByCodComunicazionePrec(codComPrecRic);
						if (vaVacancyClPrec != null) {
							// prima rettifica
							VaDatiVacancy vacancyDaModificare = vaDatiVacancyHome.findById(vaVacancyClPrec
									.getIdVaDatiVacancy());
							VaDatiVacancy vacancyModificata = costruisciVacancyCliclavoro(vacancy, provenienza, true,
									vacancyDaModificare);

							costruisciVacancyCl(vacancy, vacancyModificata, vaVacancyClPrec, provenienza);
						} else {
							throw new MyPortalException(
									ConstantsSingleton.MyPortalExceptionErrorCode.COMUNICAZIONE_PREC_NON_TROVATA);
						}
					}
				} else {
					throw new MyPortalException("Rettifica vacancy gia presente", true);
				}
			} else {
				// nuovo inserimento: la comunicazione non deve essere presente
				// su DB
				if (vaVacancyCl == null) {
					vaDatiVacancy = costruisciVacancyCliclavoro(vacancy, provenienza, false, null);
					if (vaDatiVacancy == null) {
						throw new MyPortalException("Errore nella costruzione del CV", true);
					}
					costruisciVacancyCl(vacancy, vaDatiVacancy, null, provenienza);
				} else {
					throw new MyPortalException("Vacancy gia presente", true);
				}
			}
		} else {
			// deve essere creato un solo CV per utente
			// da SIL arriva sempre un codice comunicazione diverso
			// per cui verifico se si tratta dello stesso lavoratore
			String codiceOffertaIntermediario = vacancy.getDatiSistema().getCodiceoffertaintermediario();
			String[] numAnnoRichiesta = codiceOffertaIntermediario.split("_");
			String numero = numAnnoRichiesta[0];
			String anno = numAnnoRichiesta[1];
			String provenienzaSil = (vacancy.getDatiSistema().getCodiceofferta()).substring(0, 5);
			VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findVacancyByKey(anno, numero, provenienzaSil);

			if (vaDatiVacancy == null) {
				vaDatiVacancy = costruisciVacancyCliclavoro(vacancy, provenienza, false, null);
				if (vaDatiVacancy == null) {
					throw new MyPortalException("Errore nella costruzione della Vacancy", true);
				}

				costruisciVacancyCl(vacancy, vaDatiVacancy, null, provenienza);
			} else {
				VaDatiVacancy vacancyModificata = costruisciVacancyCliclavoro(vacancy, provenienza, true, vaDatiVacancy);
				VaVacancyCl vaVacancyCl = vaVacancyClHome.findById(vacancyModificata.getIdVaDatiVacancy());

				costruisciVacancyCl(vacancy, vacancyModificata, vaVacancyCl, provenienza);
			}
		}

	}

	private VaVacancyCl costruisciVacancyCl(Vacancy vacancy, VaDatiVacancy vaDatiVacancy, VaVacancyCl vaVacancyClmod,
			String provenienza) {
		VaVacancyCl vaVacancyCl = null;
		boolean rettifica;
		if (vaVacancyClmod == null) {
			vaVacancyCl = new VaVacancyCl();
			rettifica = false;
		} else {
			vaVacancyCl = vaVacancyClmod;
			rettifica = true;
		}
		if (rettifica) {
			if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				if (vaVacancyCl.getCodComunicazionePrec() == null) {
					vaVacancyCl.setCodComunicazionePrec(vaVacancyCl.getCodComunicazione());
					vaVacancyCl.setCodComunicazione(vacancy.getDatiSistema().getCodiceofferta());
				} else {
					vaVacancyCl.setCodComunicazione(vacancy.getDatiSistema().getCodiceofferta());
				}
			} else {
				// nel caso proviene da SIL il codice comunicazione prec è
				// quello già inviato alla PDD
				// e il codice comunicazione viene creato nuovo
				vaVacancyCl.setCodComunicazionePrec(vaVacancyClmod.getCodComunicazione());
				vaVacancyCl.setCodComunicazione(clicLavoroEjb.calcolaNuovoCodComunicazioneInvioVacancy());
			}
		} else {
			// In caso di primo inserimento setto i codici di comunicazione come
			// li ho ricevuti.
			if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				vaVacancyCl.setCodComunicazione(vacancy.getDatiSistema().getCodiceofferta());
				vaVacancyCl.setCodComunicazionePrec(vacancy.getDatiSistema().getCodiceoffertaprecedente());
			} else {
				// nel caso proviene da SIL viene creato nuovo
				vaVacancyCl.setCodComunicazione(clicLavoroEjb.calcolaNuovoCodComunicazioneInvioVacancy());
			}
		}

		vaVacancyCl.setVaDatiVacancy(vaDatiVacancy);
		if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
			vaVacancyCl.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
		} else {
			if (rettifica) {
				vaVacancyCl.setDeStatoInvioCl(deStatoInvioClHome.findById("VA"));
			} else {
				vaVacancyCl.setDeStatoInvioCl(deStatoInvioClHome.findById("PA"));
			}
		}
		DeTipoComunicazioneCl deTipoComunicazioneCl = deTipoComunicazioneClHome.findById(vacancy.getDatiSistema()
				.getTipoofferta());
		vaVacancyCl.setDeTipoComunicazioneCl(deTipoComunicazioneCl);
		vaVacancyCl.setDtInvio(vaDatiVacancy.getDtmMod());
		// XXX controllare il pfprincipalmod se passato correttamente
		popolaAbstractEntity(vaDatiVacancy.getDtmMod(), vaDatiVacancy.getPfPrincipalMod(), !rettifica, vaVacancyCl);
		if (rettifica) {
			vaVacancyClHome.merge(vaVacancyCl);
		} else {
			vaVacancyClHome.persist(vaVacancyCl);
		}
		return vaVacancyCl;
	}

	/**
	 * a partire dal tracciato XML proveniente da SIL o ClicLavoro inserisce la vacancy
	 * 
	 * @param vacancy
	 * @param provenienza
	 * @param merge
	 * @param vacancydaModificare
	 * @return
	 */
	public VaDatiVacancy costruisciVacancyCliclavoro(Vacancy vacancy, String provenienza, boolean merge,
			VaDatiVacancy vacancydaModificare) {
		try {
			DatoreLavoro datore = vacancy.getDatoreLavoro();
			CondizioniOfferte condizioniOfferta = vacancy.getRichiesta().getCondizioniOfferte();
			DatiSistema datiSistema = vacancy.getDatiSistema();

			// NB: Da giugno 2017, anche la VDA ha implementato la validazione dell'azienda.
			// Questo significa che nel flusso di ClicLavoro usa lo stesso metodo di Trento, invece di
			// quello usato dalle altre regioni.
			PfPrincipal utenzaPalese = null;
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_TRENTO
					|| ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_VDA) {
				utenzaPalese = trovaUtenzaPaleseTrento(datore, datiSistema, provenienza);
			} else {
				utenzaPalese = trovaUtenzaPalese(datore, datiSistema, provenienza);
			}

			PfPrincipal utenzaPrincipale = trovaUtenzaPrincipale(datiSistema, provenienza);
			PfPrincipal utenzaAdmin = pfPrincipalHome.findById(0);
			Date now = new Date();

			if (utenzaPrincipale == null) {
				throw new MyPortalException("Utenza principale vacancy non trovata", true);
			}

			VaDatiVacancy vaDatiVacancy = null;
			if (merge) {
				vaDatiVacancy = vacancydaModificare;
			} else {
				vaDatiVacancy = new VaDatiVacancy();

				if (provenienza.startsWith(ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC)) {
					/*
					 * per le vacancy sincronizzata su cliclavoro provenienti da SIL il num e anno richiesta sono quelli
					 * dell'XML
					 */
					String codiceOffertaIntermediario = vacancy.getDatiSistema().getCodiceoffertaintermediario();
					String[] numAnnoRichiesta = codiceOffertaIntermediario.split("_");
					vaDatiVacancy.setNumRichiesta(Integer.parseInt(numAnnoRichiesta[0]));
					vaDatiVacancy.setNumAnno(Integer.parseInt(numAnnoRichiesta[1]));
				} else {
					// XXX anno e numero vacancy per provenienza PDD
					Integer numAnno = Calendar.getInstance().get(Calendar.YEAR);
					Integer numRichiesta = vaDatiVacancyHome.getNextNumRichiesta(numAnno, provenienza);

					vaDatiVacancy.setNumAnno(numAnno);
					vaDatiVacancy.setNumRichiesta(numRichiesta);
				}

				if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
					vaDatiVacancy.setOpzTipoDecodifiche("M");
					vaDatiVacancy.setDeProvenienzaVacancy(deProvenienzaHome
							.findById(ConstantsSingleton.DeProvenienza.COD_MINISTERO));
				} else {
					String provenienzaSil = (vacancy.getDatiSistema().getCodiceofferta()).substring(0, 5);
					vaDatiVacancy.setDeProvenienzaVacancy(deProvenienzaHome.findById(provenienzaSil));
				}
				
				vaDatiVacancy.setCodStatoVacancyEnum(CodStatoVacancyEnum.PUB);
			}

			vaDatiVacancy.setPfPrincipal(utenzaPrincipale);

			if (!(ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				if ((SiNo.NO).equals(datiSistema.getVisibilita())) {
					vaDatiVacancy.setVisibilita(false);
				} else {
					vaDatiVacancy.setVisibilita(true);
				}
				vaDatiVacancy.setCodComunicazioneSil(vacancy.getDatiSistema().getCodiceofferta());
				/*
				 * DAVIDE-CARMELA 29/05/2013 per le vacancy anonime da sil viene comunque settato l'utente palese serve
				 * per inviare il CF a cliclavoro
				 */
				// if (intermediario != null &&
				// (SiNo.NO).equals(datiSistema.getVisibilita())) {
				// vaDatiVacancy.setPfPrincipalPalese(null);
				// } else {
				// vaDatiVacancy.setPfPrincipalPalese(utenzaPalese);
				// }
				vaDatiVacancy.setPfPrincipalPalese(utenzaPalese);
			} else {
				vaDatiVacancy.setPfPrincipalPalese(utenzaPalese);
				vaDatiVacancy.setVisibilita(true);
			}
			vaDatiVacancy.setFlagInvioCl(true);

			DeComune comune = deComuneHome.findById(condizioniOfferta.getIdcomune());
			String indirizzo = comune.getDenominazione();
			vaDatiVacancy.setDeComune(comune);
			vaDatiVacancy.setIndirizzoLavoro(indirizzo);

			// popolo i contatti
			VaContatto vaContatto = null;
			VaContatto vaContattoAlternativo = null;
			if (merge) {
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

			if (!(ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				popolaVaContattoSil(datore, datiSistema, vaContatto, now, utenzaAdmin, merge);
			} else {
				popolaVaContatto(datore, datiSistema, vaContatto, now, utenzaAdmin, merge);
				popolaVaContattoAlternativo(datore, datiSistema, vaContattoAlternativo, now, utenzaAdmin, merge);
			}
			popolaAbstractEntity(now, utenzaAdmin, merge, vaContatto);

			vaDatiVacancy.setVaContattoPrinc(vaContatto);

			// controllo se la vacancy è anonima o palese
			if (!(ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				String intermediario = datiSistema.getIntermediario();
				if (intermediario != null && !("").equalsIgnoreCase(intermediario)
						&& (SiNo.NO).equals(datiSistema.getVisibilita())) {
					vaDatiVacancy.setFlagAnonima(true);
				} else {
					vaDatiVacancy.setFlagAnonima(false);
				}
			} else {
				if (vaContattoAlternativo != null) {
					if (vaContattoAlternativo.getDtmMod() != null) {
						vaDatiVacancy.setFlagAnonima(false);
						vaDatiVacancy.setVaContattoAlt(vaContattoAlternativo);

						popolaAbstractEntity(now, utenzaAdmin, merge, vaContattoAlternativo);
						entityManager.persist(vaContattoAlternativo);
					} else {
						vaDatiVacancy.setFlagAnonima(true);
					}
				}
			}

			entityManager.persist(vaContatto);

			popolaVaDatiVacancy(vacancy, vaDatiVacancy);

			popolaAbstractEntity(now, utenzaAdmin, !merge, vaDatiVacancy);

			if (!merge) {
				vaDatiVacancyHome.persist(vaDatiVacancy);
			}

			// GEOLOCALIZZO LA VACANCY
			Poi poi = poiHome.createOrUpdatePoi(vaDatiVacancy);
			vaDatiVacancy.setPoi(poi);

			// NULLA OSTA
			if (SiNo.SI.equals(vacancy.getAltreInformazioni().getNO())) {
				vaDatiVacancy.setOpzNullaOsta("Y");
			} else {
				vaDatiVacancy.setOpzNullaOsta("N");
			}

			entityManager.persist(vaDatiVacancy);

			Set<VaIstruzione> setVaIstruziones = null;
			List<Titolostudio> titoli = vacancy.getRichiesta().getIstruzioneFormazione().getTitolostudio();
			if (titoli.size() > 0) {
				setVaIstruziones = creaVaIstruzione(vacancy.getRichiesta().getIstruzioneFormazione(), vaDatiVacancy,
						now, utenzaAdmin, merge);
			}

			Set<VaLingua> setVaLinguas = null;
			List<Lingua> lingue = vacancy.getRichiesta().getIstruzioneFormazione().getLingua();
			if (lingue.size() > 0) {
				setVaLinguas = creaVaLingua(vacancy.getRichiesta().getIstruzioneFormazione(), vaDatiVacancy, now,
						utenzaAdmin, merge);
			}

			Set<VaPatente> setVaPatentes = null;
			List<String> patenti = vacancy.getRichiesta().getIstruzioneFormazione().getIdpatenteguida();
			if (patenti.size() > 0) {
				setVaPatentes = creaVaPatente(vacancy.getRichiesta().getIstruzioneFormazione(), vaDatiVacancy, now,
						utenzaAdmin, merge);
			}

			Set<VaPatentino> setVaPatentinos = null;
			List<String> patentini = vacancy.getRichiesta().getIstruzioneFormazione().getIdpatentino();
			if (patentini.size() > 0) {
				setVaPatentinos = creaVaPatentino(vacancy.getRichiesta().getIstruzioneFormazione(), vaDatiVacancy, now,
						utenzaAdmin, merge);
			}

			List<VaAlbo> setVaAlbos = null;
			String albo = vacancy.getRichiesta().getIstruzioneFormazione().getIdalbo();
			if (albo != null && !("").equalsIgnoreCase(albo)) {
				setVaAlbos = creaVaAlbo(vacancy.getRichiesta().getIstruzioneFormazione(), vaDatiVacancy, now,
						utenzaAdmin, merge);
			}

			String informatica = vacancy.getRichiesta().getIstruzioneFormazione().getConoscenzeinformatiche();
			if (informatica != null && !("").equalsIgnoreCase(informatica)) {
				creaVaInformatica(vacancy.getRichiesta().getIstruzioneFormazione(), vaDatiVacancy, now, utenzaAdmin,
						merge);
			}

			String retribuzione = vacancy.getRichiesta().getCondizioniOfferte().getRetribuzioneannualorda();
			if (retribuzione != null && !("").equalsIgnoreCase(retribuzione)) {
				creaVaRetribuzione(vacancy.getRichiesta().getCondizioniOfferte(), vaDatiVacancy, now, utenzaAdmin,
						merge);
			}

			List<VaContratto> setVaContrattos = null;
			String contratto = vacancy.getRichiesta().getCondizioniOfferte().getIdtipologiacontratto();
			if (contratto != null && !("").equalsIgnoreCase(contratto)) {
				setVaContrattos = creaVaContratto(vacancy.getRichiesta().getCondizioniOfferte(), vaDatiVacancy, now,
						utenzaAdmin, merge);
			}

			VaAltreInfo vaAltreInfo = null;
			String ulterioriInfo = vacancy.getRichiesta().getCondizioniOfferte().getUlterioricondizioni();
			if (ulterioriInfo != null && !("").equalsIgnoreCase(ulterioriInfo)) {
				vaAltreInfo = creaVaAltreInfo(vacancy.getRichiesta().getCondizioniOfferte(), vaDatiVacancy, now,
						utenzaAdmin, merge);
			}

			VaCompetenzeTrasv vaCompetenzeTrasv = null;
			String altreCapacita = vacancy.getRichiesta().getIstruzioneFormazione().getAltrecapacita();
			if (altreCapacita != null && !("").equalsIgnoreCase(altreCapacita)) {
				vaCompetenzeTrasv = creaVaCompetenzeTrasv(vacancy.getRichiesta().getIstruzioneFormazione(),
						vaDatiVacancy, now, utenzaAdmin, merge);
			}

			Set<VaOrario> setVaOrarios = null;
			String orario = vacancy.getRichiesta().getCondizioniOfferte().getIdmodalitalavoro();
			if (orario != null && !("").equalsIgnoreCase(orario)) {
				setVaOrarios = creaVaOrario(vacancy.getRichiesta().getCondizioniOfferte(), vaDatiVacancy, now,
						utenzaAdmin, merge);
			}

			vaDatiVacancy.setVaIstruziones(setVaIstruziones);
			vaDatiVacancy.setVaLinguas(setVaLinguas);
			vaDatiVacancy.setVaPatentes(setVaPatentes);
			vaDatiVacancy.setVaPatentinos(setVaPatentinos);
			vaDatiVacancy.setVaAlbos(setVaAlbos);
			vaDatiVacancy.setVaContrattos(setVaContrattos);
			vaDatiVacancy.setVaAltreInfo(vaAltreInfo);
			vaDatiVacancy.setVaCompetenzeTrasv(vaCompetenzeTrasv);
			vaDatiVacancy.setVaOrarios(setVaOrarios);

			entityManager.merge(vaDatiVacancy);

			// nel caso di aggiornamento di una vacancy
			// se la vacancy è di chiusura viene settato il flagEliminata = true
			// contemporaneamente bisogna eliminare l'indice dalla ricerca di
			// SOLR
			if (merge) {
				Integer id = vaDatiVacancy.getIdVaDatiVacancy();
				vaDatiVacancyHome.deleteVacancySolr(id);
			}

			return vaDatiVacancy;

		} catch (ParseException e) {
			throw new MyPortalException("Errore durante l'import della VACANCY", e, true);
		}

	}

	private PfPrincipal trovaUtenzaPalese(DatoreLavoro datore, DatiSistema datiSistema, String provenienza) {
		PfPrincipal utenzaPalese = null;
		String cfAz = datore.getDatiAnagrafici().getCodicefiscale();
		String emailAz = datore.getDatiContatto().getEmail();
		if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
			// verifico l'esistenza di una azienda (con accesso al SARE) con il
			// cf uguale a quello dell'xml
			// se non esiste verifico la presenza di un'azienda con la mail di
			// registrazione
			// se non esiste nessuna la vacancy NON risulta PALESE
			List<AziendaInfo> aziendaInfoList = aziendaInfoHome.findListByCodiceFiscale(cfAz.toUpperCase());
			if (aziendaInfoList.size() == 1) {
				AziendaInfo aziendaInfo = aziendaInfoList.get(0);
				DeAutorizzazioneSare deAutorizzazioneSare = aziendaInfo.getDeAutorizzazioneSare();
				if (deAutorizzazioneSare != null) { // 03/06/13 fix per aziende
													// senza codice
													// autorizzazione
					String codAutorizzazioneSare = deAutorizzazioneSare.getCodAutorizzazioneSare();
					if (ConstantsSingleton.DeAutorizzazioneSare.CONCESSA.equals(codAutorizzazioneSare)) {
						utenzaPalese = aziendaInfo.getPfPrincipal();
					}
				}
			} else {
				List<PfPrincipal> listaPrincipal = pfPrincipalHome.findByEmail(emailAz.toUpperCase());
				if (listaPrincipal.size() == 1) {
					PfPrincipal pfPrinc = listaPrincipal.get(0);
					if (pfPrinc.isAzienda()) {
						AziendaInfo az = pfPrinc.getAziendaInfo();
						DeAutorizzazioneSare deAutorizzazioneSare = az.getDeAutorizzazioneSare();
						if (deAutorizzazioneSare != null) { // 06/06/13 fix per
															// aziende senza
															// codice
															// autorizzazione
							String codAutorizzazioneSare = deAutorizzazioneSare.getCodAutorizzazioneSare();
							if (ConstantsSingleton.DeAutorizzazioneSare.CONCESSA.equals(codAutorizzazioneSare)) {
								utenzaPalese = pfPrinc;
							}
						}
					}
				}
			}
			// 12/06/2013 DONA
			// per inviare il messaggion di contatto la vacancy
			// deve essere agganciata sempre ad una azienda (se non trovata
			// viene creata una fittizia)
			// 26/06/2013 commentata per ulteriori verifiche
			// if (utenzaPalese == null) {
			// verifico se non ho già creato un'azienda palese cl_XXXXXXXX
			// List<AziendaInfo> aziendaInfoClPaleseList =
			// aziendaInfoHome.findListByCodiceFiscale(Utils.cut("cl_"+cfAz, 16,
			// false));
			// if (aziendaInfoClPaleseList.size() == 1) {
			// AziendaInfo aziendaInfo = aziendaInfoClPaleseList.get(0);
			// utenzaPalese = aziendaInfo.getPfPrincipal();
			// } else {
			// AziendaInfo azInfo = creaAziendaPalese(datore, false,
			// provenienza);
			// utenzaPalese = azInfo.getPfPrincipal();
			// }
			// }
		} else {
			// CARMELA 29/05/2013
			// creo un'azienda fittizia per recuperare i dati anagrafici da
			// spedire a ClicLavoro
			if ((SiNo.NO).equals(datiSistema.getVisibilita())) {
				// inserisco l'azienda proveniente da SIL
				AziendaInfo aziendaInfo = aziendaInfoHome.findByCodiceFiscale(cfAz.toUpperCase());
				if (aziendaInfo != null) {
					utenzaPalese = aziendaInfo.getPfPrincipal();
				} else {
					aziendaInfo = creaAziendaPalese(datore, false, provenienza);
					utenzaPalese = aziendaInfo.getPfPrincipal();
				}
			} else {
				// non si controlla se l'azienda ha l'abilitazione al SARE
				AziendaInfo aziendaInfo = aziendaInfoHome.findByCodiceFiscale(cfAz.toUpperCase());
				if (aziendaInfo != null) {
					utenzaPalese = aziendaInfo.getPfPrincipal();
				} else {
					List<PfPrincipal> listaPrincipal = pfPrincipalHome.findByEmail(emailAz.toUpperCase());
					if (listaPrincipal.size() > 0) {
						PfPrincipal pfPrinc = listaPrincipal.get(0);
						if (pfPrinc.isAzienda()) {
							utenzaPalese = pfPrinc;
						} else {
							// inserisco l'azienda proveniente da SIL
							AziendaInfo azInfo = creaAziendaPalese(datore, false, provenienza);
							utenzaPalese = azInfo.getPfPrincipal();
						}
					} else {
						// inserisco l'azienda proveniente da SIL
						AziendaInfo azInfo = creaAziendaPalese(datore, true, provenienza);
						utenzaPalese = azInfo.getPfPrincipal();
					}
				}
			}
		}

		return utenzaPalese;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param datore
	 * @param datiSistema
	 * @param provenienza
	 * @return
	 */
	private PfPrincipal trovaUtenzaPaleseTrento(DatoreLavoro datore, DatiSistema datiSistema, String provenienza) {
		PfPrincipal utenzaPalese = null;
		String cfAz = datore.getDatiAnagrafici().getCodicefiscale();
		String emailAz = datore.getDatiContatto().getEmail();
		if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
			List<AziendaInfo> aziendaInfoList = aziendaInfoHome.findListByCodiceFiscale(cfAz.toUpperCase());
			if (aziendaInfoList.size() == 1) {
				AziendaInfo aziendaInfo = aziendaInfoList.get(0);
				if (aziendaInfo.getFlagValida()) {
					utenzaPalese = aziendaInfo.getPfPrincipal();
				}
			} else {
				List<PfPrincipal> listaPrincipal = pfPrincipalHome.findByEmail(emailAz.toUpperCase());
				if (listaPrincipal.size() == 1) {
					PfPrincipal pfPrinc = listaPrincipal.get(0);
					if (pfPrinc.isAzienda()) {
						AziendaInfo az = pfPrinc.getAziendaInfo();
						if (az.getFlagValida()) {
							utenzaPalese = az.getPfPrincipal();
						}
					}
				} else {
					DeComune comuneSede = deComuneHome.findById(datore.getDatiContatto().getIdcomune());
					DeProvincia provSede = comuneSede.getDeProvincia();
					String codProvSede = provSede.getCodProvincia();
					if ("22".equalsIgnoreCase(codProvSede)) {
						AziendaInfo azInfo = creaAziendaPaleseTrento(datore, false, false);
						utenzaPalese = azInfo.getPfPrincipal();
					}
				}
			}
		} else {
			if ((SiNo.NO).equals(datiSistema.getVisibilita())) {
				// inserisco l'azienda proveniente da SIL
				AziendaInfo aziendaInfo = aziendaInfoHome.findByCodiceFiscale(cfAz.toUpperCase());
				if (aziendaInfo != null) {
					utenzaPalese = aziendaInfo.getPfPrincipal();

					// per le aziende proveniente da SIL viene forzato il FLG VALIDA
					if (!aziendaInfo.getFlagValida()) {
						aziendaInfo.setFlagValida(true);
						aziendaInfoHome.merge(aziendaInfo);
					}
				} else {
					aziendaInfo = creaAziendaPaleseTrento(datore, true, true);
					utenzaPalese = aziendaInfo.getPfPrincipal();
				}
			} else {
				// non si controlla se l'azienda ha l'abilitazione al SARE
				AziendaInfo aziendaInfo = aziendaInfoHome.findByCodiceFiscale(cfAz.toUpperCase());
				if (aziendaInfo != null) {
					utenzaPalese = aziendaInfo.getPfPrincipal();

					// per le aziende proveniente da SIL viene forzato il FLG VALIDA
					if (!aziendaInfo.getFlagValida()) {
						aziendaInfo.setFlagValida(true);
						aziendaInfoHome.merge(aziendaInfo);
					}
				} else {
					List<PfPrincipal> listaPrincipal = pfPrincipalHome.findByEmail(emailAz.toUpperCase());
					if (listaPrincipal.size() > 0) {
						PfPrincipal pfPrinc = listaPrincipal.get(0);
						if (pfPrinc.isAzienda()) {
							utenzaPalese = pfPrinc;
							AziendaInfo aziendaInfoEmail = pfPrinc.getAziendaInfo();
							// per le aziende proveniente da SIL viene forzato il FLG VALIDA
							if (!aziendaInfoEmail.getFlagValida()) {
								aziendaInfoEmail.setFlagValida(true);
								aziendaInfoHome.merge(aziendaInfoEmail);
							}
						} else {
							// inserisco l'azienda proveniente da SIL
							AziendaInfo azInfo = creaAziendaPaleseTrento(datore, true, true);
							utenzaPalese = azInfo.getPfPrincipal();
						}
					} else {
						// inserisco l'azienda proveniente da SIL
						AziendaInfo azInfo = creaAziendaPaleseTrento(datore, true, true);
						utenzaPalese = azInfo.getPfPrincipal();
					}
				}
			}
		}

		return utenzaPalese;
	}

	private PfPrincipal trovaUtenzaPrincipale(DatiSistema datiSistema, String provenienza) {
		PfPrincipal utentePrincipale = null;
		String username = null;
		if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
			// Se arriva da CLICLAVORO, aggancio l'utente del gruppo
			// azienda/cliclavoro
			username = ConstantsSingleton.USERNAME_CLICLAVORO_VACANCY;
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
			utentePrincipale = pfPrincipal;
		} else {
			// XXX controllare se l'intermediario da SIL è il CODCPI
			String intermediario = datiSistema.getIntermediario();
			// recupero il CPI
			DeCpi cpi = deCpiHome.findByCodIntermediarioCl(intermediario);
			if (cpi != null) {
				AziendaInfo aziendaInfoCpi = aziendaInfoHome.findByCodiceFiscale(cpi.getCodCpi());
				utentePrincipale = aziendaInfoCpi.getPfPrincipal();
			}
		}

		return utentePrincipale;
	}

	private void popolaAbstractEntity(Date now, PfPrincipal principal, boolean isInsert, AbstractEntity... aeList) {
		for (AbstractEntity ae : aeList) {
			if (isInsert) {
				ae.setDtmIns(now);
				ae.setPfPrincipalIns(principal);
			}
			ae.setDtmMod(now);
			ae.setPfPrincipalMod(principal);
		}
	}

	private void popolaVaDatiVacancy(Vacancy vacancy, VaDatiVacancy vaDatiVacancy) throws ParseException {
		Richiesta ric = vacancy.getRichiesta();
		vaDatiVacancy.setNumLavRicercati(new Integer(ric.getProfiloRichiesto().getNumerolavoratori()));
		vaDatiVacancy.setAttivitaPrincipale(ric.getProfiloRichiesto().getDescrprofessione());
		vaDatiVacancy.setAttivitaDescrizioneEstesa(ric.getProfiloRichiesto().getDescrizionericerca());
		if ((SiNo.SI).equals(ric.getProfiloRichiesto().getEsperienzarichiesta())) {
			vaDatiVacancy.setOpzEsperienze("Y");
		}
		vaDatiVacancy.setOpzPubblicabile("Y");
		vaDatiVacancy.setDtPubblicazione(Utils
				.fromXMLGregorianCalendar(ric.getDurataRichiesta().getDatapubblicazione()));
		vaDatiVacancy.setDtScadenzaPubblicazione(Utils.fromXMLGregorianCalendar(ric.getDurataRichiesta()
				.getDatascadenza()));
		vaDatiVacancy.setDtScadenza(Utils.fromXMLGregorianCalendar(ric.getDurataRichiesta().getDatascadenza()));
		String codMansioneMin = ric.getProfiloRichiesto().getIdprofessione();
		DeMansioneMin mansMin = deMansioneMinHome.findByDot(codMansioneMin);
		DeMansioneSil mansSil = mansMin.getDeMansioneSil();
		vaDatiVacancy.setDeMansione(mansSil.getDeMansione());
		vaDatiVacancy.setMansioneMin(mansMin);
		String codAttivita = vacancy.getDatoreLavoro().getDatiAnagrafici().getSettore();
		if (codAttivita != null && !("").equalsIgnoreCase(codAttivita)) {
			DeAttivitaMin attivitaMin = deAttivitaMinHome.findByDot(codAttivita);
			vaDatiVacancy.setAttivitaMin(attivitaMin);
			String codAttivitaPadreSil = attivitaMin.getPadre().getCodAttivitaMin();
			DeAttivitaSil attivitaSil = deAttivitaSilHome.findById(codAttivitaPadreSil);
			DeAttivita deAttivita = attivitaSil.getDeAttivita();
			vaDatiVacancy.setDeAttivita(deAttivita);
		}

		if (vacancy.getDatiSistema().getIntermediario() != null
				&& !("").equalsIgnoreCase(vacancy.getDatiSistema().getIntermediario())) {
			vaDatiVacancy.setIntermediario(vacancy.getDatiSistema().getIntermediario());
		}

		if (vacancy.getDatiSistema().getDenominazioneintermediario() != null
				&& !("").equalsIgnoreCase(vacancy.getDatiSistema().getDenominazioneintermediario())) {
			vaDatiVacancy.setDenominazioneIntermediario(vacancy.getDatiSistema().getDenominazioneintermediario());
		}

		if ("02".equalsIgnoreCase(vacancy.getDatiSistema().getTipoofferta())) {
			if (vacancy.getDatiSistema().getMotivochiusura() != null) {
				vaDatiVacancy.setMotivoChiusura(deMotivoChiusuraHome.findById(vacancy.getDatiSistema()
						.getMotivochiusura()));
				vaDatiVacancy.setFlagEliminata(true);
			} else {
				vaDatiVacancy.setFlagEliminata(false);
			}
		} else {
			vaDatiVacancy.setFlagEliminata(false);
		}

	}

	private VaAltreInfo creaVaAltreInfo(CondizioniOfferte condizioni, VaDatiVacancy vaDatiVacancy, Date now,
			PfPrincipal principal, boolean merge) {

		if (merge) {
			vaAltreInfoHome.remove(vaDatiVacancy.getVaAltreInfo());
			vaDatiVacancy.setVaAltreInfo(null);
		}

		VaAltreInfo info = new VaAltreInfo();
		info.setUlterioriRequisiti(condizioni.getUlterioricondizioni());
		info.setVaDatiVacancy(vaDatiVacancy);
		popolaAbstractEntity(now, principal, true, info);
		vaAltreInfoHome.persist(info);

		return info;
	}

	private VaCompetenzeTrasv creaVaCompetenzeTrasv(IstruzioneFormazione istruzione, VaDatiVacancy vaDatiVacancy,
			Date now, PfPrincipal principal, boolean merge) {

		if (merge) {
			vaCompetenzeTrasvHome.remove(vaDatiVacancy.getVaCompetenzeTrasv());
			vaDatiVacancy.setVaCompetenzeTrasv(null);
		}

		VaCompetenzeTrasv compet = new VaCompetenzeTrasv();
		compet.setCapCompSintesiCl(istruzione.getAltrecapacita());
		compet.setVaDatiVacancy(vaDatiVacancy);
		popolaAbstractEntity(now, principal, true, compet);
		vaCompetenzeTrasvHome.persist(compet);

		return compet;
	}

	private Set<VaIstruzione> creaVaIstruzione(IstruzioneFormazione istruzione, VaDatiVacancy vaDatiVacancy, Date now,
			PfPrincipal principal, boolean merge) throws ParseException {

		List<Titolostudio> listTitoliStudio = istruzione.getTitolostudio();
		if (merge) {
			// Se sono nel caso di rettifica, cancello prima le vecchie
			// relazioni
			for (VaIstruzione vaIstruzione : vaDatiVacancy.getVaIstruziones()) {
				vaIstruzioneHome.remove(vaIstruzione);
			}
			vaDatiVacancy.setVaIstruziones(null);
		}

		Set<VaIstruzione> setVaIstruziones = new HashSet<VaIstruzione>();
		for (Titolostudio titoloStudio : listTitoliStudio) {
			VaIstruzione vaIstruzione = new VaIstruzione();
			DeTitolo titolo = deTitoloHome.getCodTitoloClicLavoro(titoloStudio.getIdtitolostudio());
			vaIstruzione.setDeTitolo(titolo);
			vaIstruzione.setVaDatiVacancy(vaDatiVacancy);
			popolaAbstractEntity(now, principal, true, vaIstruzione);
			vaIstruzioneHome.persist(vaIstruzione);
			setVaIstruziones.add(vaIstruzione);
		}
		if (setVaIstruziones.size() > 0)
			return setVaIstruziones;
		else
			return null;
	}

	private Set<VaLingua> creaVaLingua(IstruzioneFormazione istruzione, VaDatiVacancy vaDatiVacancy, Date now,
			PfPrincipal principal, boolean merge) {
		List<Lingua> listaLingues = istruzione.getLingua();
		Set<VaLingua> setLingues = new HashSet<VaLingua>();
		if (merge) {
			// Se sono nel caso di rettifica, cancello prima le vecchie
			// relazioni

			for (VaLingua entity : vaDatiVacancy.getVaLinguas()) {
				vaLinguaHome.remove(entity);
			}
			vaDatiVacancy.setVaLinguas(null);
		}
		for (Lingua lingua : listaLingues) {
			VaLingua vaLingua = new VaLingua();

			DeLingua deLingua = deLinguaHome.findById(lingua.getIdlingua());
			if (deLingua == null)
				continue;
			vaLingua.setDeLingua(deLingua);

			if (lingua.getIdlivelloletto() == null && lingua.getIdlivelloscritto() == null
					&& lingua.getIdlivelloparlato() == null) {
				vaLingua.setFlagMadrelingua(true);
			} else {
				if (lingua.getIdlivelloletto() != null) {
					vaLingua.setDeGradoLinByCodGradoLetto(deGradoLinHome.findById(lingua.getIdlivelloletto()));
				} else {
					vaLingua.setDeGradoLinByCodGradoLetto(deGradoLinHome.findById("A1"));
				}
				if (lingua.getIdlivelloscritto() != null) {
					vaLingua.setDeGradoLinByCodGradoScritto(deGradoLinHome.findById(lingua.getIdlivelloscritto()));
				} else {
					vaLingua.setDeGradoLinByCodGradoScritto(deGradoLinHome.findById("A1"));
				}
				if (lingua.getIdlivelloparlato() != null) {
					vaLingua.setDeGradoLinByCodGradoParlato(deGradoLinHome.findById(lingua.getIdlivelloparlato()));
				} else {
					vaLingua.setDeGradoLinByCodGradoParlato(deGradoLinHome.findById("A1"));
				}
				vaLingua.setFlagMadrelingua(false);
			}

			vaLingua.setVaDatiVacancy(vaDatiVacancy);
			popolaAbstractEntity(now, principal, true, vaLingua);
			vaLinguaHome.persist(vaLingua);
			setLingues.add(vaLingua);
		}
		if (setLingues.size() > 0)
			return setLingues;
		else
			return null;
	}

	private Set<VaPatente> creaVaPatente(IstruzioneFormazione istruzione, VaDatiVacancy vaDatiVacancy, Date now,
			PfPrincipal principal, boolean merge) {
		List<String> patenti = istruzione.getIdpatenteguida();
		Set<VaPatente> setVaPatentes = new HashSet<VaPatente>();
		if (merge) {
			// Se sono nel caso di rettifica, cancello prima le vecchie
			// relazioni

			for (VaPatente entity : vaDatiVacancy.getVaPatentes()) {

				vaPatenteHome.remove(entity);
			}
			vaDatiVacancy.setVaPatentes(null);
		}
		for (String s : patenti) {
			VaPatente vaPatente = new VaPatente();
			DePatente patente = dePatenteHome.findById(s);
			if (patente == null)
				continue;
			vaPatente.setDePatente(patente);
			vaPatente.setVaDatiVacancy(vaDatiVacancy);
			popolaAbstractEntity(now, principal, true, vaPatente);
			vaPatenteHome.persist(vaPatente);
			setVaPatentes.add(vaPatente);
		}
		if (setVaPatentes.size() > 0)
			return setVaPatentes;
		else
			return null;
	}

	private Set<VaPatentino> creaVaPatentino(IstruzioneFormazione istruzione, VaDatiVacancy vaDatiVacancy, Date now,
			PfPrincipal principal, boolean merge) {
		List<String> patentini = istruzione.getIdpatentino();
		Set<VaPatentino> setVaPatentinos = new HashSet<VaPatentino>();
		if (merge) {
			// Se sono nel caso di rettifica, cancello prima le vecchie
			// relazioni

			for (VaPatentino entity : vaDatiVacancy.getVaPatentinos()) {

				vaPatentinoHome.remove(entity);
			}
			vaDatiVacancy.setVaPatentinos(null);
		}
		for (String s : patentini) {
			VaPatentino vaPatentino = new VaPatentino();
			DePatentino patentino = dePatentinoHome.findById(s);
			if (patentino == null)
				continue;
			vaPatentino.setDePatentino(patentino);
			vaPatentino.setVaDatiVacancy(vaDatiVacancy);
			popolaAbstractEntity(now, principal, true, vaPatentino);
			vaPatentinoHome.persist(vaPatentino);
			setVaPatentinos.add(vaPatentino);
		}
		if (setVaPatentinos.size() > 0)
			return setVaPatentinos;
		else
			return null;
	}

	private List<VaAlbo> creaVaAlbo(IstruzioneFormazione istruzione, VaDatiVacancy vaDatiVacancy, Date now,
			PfPrincipal principal, boolean merge) {
		String idAlbo = istruzione.getIdalbo();
		List<VaAlbo> setVaAlbos = new ArrayList<VaAlbo>();
		if (merge) {
			// Se sono nel caso di rettifica, cancello prima le vecchie
			// relazioni
			for (VaAlbo entity : vaDatiVacancy.getVaAlbos()) {
				vaAlboHome.remove(entity);
			}
			vaDatiVacancy.setVaAlbos(null);
		}

		if (idAlbo != null && !("").equalsIgnoreCase(idAlbo)) {
			VaAlbo vaAlbo = new VaAlbo();
			DeAlbo albo = deAlboHome.findById(idAlbo);
			if (albo == null) {
				return null;
			}
			vaAlbo.setDeAlbo(albo);
			vaAlbo.setVaDatiVacancy(vaDatiVacancy);
			popolaAbstractEntity(now, principal, true, vaAlbo);
			vaAlboHome.persist(vaAlbo);
			setVaAlbos.add(vaAlbo);

		}
		if (setVaAlbos.size() > 0)
			return setVaAlbos;
		else
			return null;
	}

	private Set<VaInformatica> creaVaInformatica(IstruzioneFormazione istruzione, VaDatiVacancy vaDatiVacancy,
			Date now, PfPrincipal principal, boolean merge) {
		String conoscenzeInformatiche = istruzione.getConoscenzeinformatiche();
		Set<VaInformatica> setVaInformaticas = new HashSet<VaInformatica>();
		if (merge) {
			// Se sono nel caso di rettifica, cancello prima le vecchie
			// relazioni
			VaInformatica entity = vaDatiVacancy.getVaInformatica();
			if (entity != null) {
				vaInformaticaHome.remove(entity);
			}
			vaDatiVacancy.setVaInformatica(null);
		}

		if (conoscenzeInformatiche != null && !("").equalsIgnoreCase(conoscenzeInformatiche)) {
			VaInformatica vaInformatica = new VaInformatica();
			vaInformatica.setConoscenzaInfo(conoscenzeInformatiche);
			vaInformatica.setVaDatiVacancy(vaDatiVacancy);
			popolaAbstractEntity(now, principal, true, vaInformatica);
			vaInformaticaHome.persist(vaInformatica);
			setVaInformaticas.add(vaInformatica);

		}
		if (setVaInformaticas.size() > 0)
			return setVaInformaticas;
		else
			return null;
	}

	private Set<VaRetribuzione> creaVaRetribuzione(CondizioniOfferte condizioni, VaDatiVacancy vaDatiVacancy, Date now,
			PfPrincipal principal, boolean merge) {
		String retribuzione = condizioni.getRetribuzioneannualorda();
		Set<VaRetribuzione> setVaRetribuziones = new HashSet<VaRetribuzione>();
		if (merge) {
			// Se sono nel caso di rettifica, cancello prima le vecchie
			// relazioni
			VaRetribuzione entity = vaDatiVacancy.getVaRetribuzione();
			if (entity != null) {
				vaRetribuzioneHome.remove(entity);
			}
			vaDatiVacancy.setVaInformatica(null);
		}

		if (retribuzione != null && !("").equalsIgnoreCase(retribuzione)) {
			BigDecimal retribuzioneCL = new BigDecimal(retribuzione);
			List<DeRetribuzione> listDeRetribuzione = deRetribuzioneHome.findAll();
			DeRetribuzione deRetribuzioneMyP = null;
			for (DeRetribuzione deRetribuzioneIesimo : listDeRetribuzione) {
				if ((deRetribuzioneIesimo.getLimSupDecimale()).compareTo(retribuzioneCL) > 0) {
					if ((deRetribuzioneIesimo.getLimInfDecimale()).compareTo(retribuzioneCL) < 0) {
						deRetribuzioneMyP = deRetribuzioneIesimo;
						break;
					}
				}
			}
			if (deRetribuzioneMyP != null) {
				VaRetribuzione vaRetribuzione = new VaRetribuzione();
				vaRetribuzione.setDeRetribuzione(deRetribuzioneMyP);
				vaRetribuzione.setVaDatiVacancy(vaDatiVacancy);
				popolaAbstractEntity(now, principal, true, vaRetribuzione);
				vaRetribuzioneHome.persist(vaRetribuzione);
				setVaRetribuziones.add(vaRetribuzione);
			}
		}

		if (setVaRetribuziones.size() > 0)
			return setVaRetribuziones;
		else
			return null;
	}

	private List<VaContratto> creaVaContratto(CondizioniOfferte condizioniOfferte, VaDatiVacancy vaDatiVacancy,
			Date now, PfPrincipal principal, boolean merge) {
		String idTipologiaContratto = condizioniOfferte.getIdtipologiacontratto();
		List<VaContratto> setVaContrattos = new ArrayList<VaContratto>();
		if (merge) {
			// Se sono nel caso di rettifica, cancello prima le vecchie
			// relazioni
			for (VaContratto entity : vaDatiVacancy.getVaContrattos()) {
				vaContrattoHome.remove(entity);
			}
			vaDatiVacancy.setVaContrattos(null);
		}

		if (idTipologiaContratto != null && !("").equalsIgnoreCase(idTipologiaContratto)) {
			DeContratto contratto = deContrattoHome.findById(idTipologiaContratto);
			if (contratto != null) {
				VaContratto vaContratto = new VaContratto();
				vaContratto.setDeContratto(contratto);
				vaContratto.setVaDatiVacancy(vaDatiVacancy);
				popolaAbstractEntity(now, principal, true, vaContratto);
				vaContrattoHome.persist(vaContratto);
				setVaContrattos.add(vaContratto);
			}

		}
		if (setVaContrattos.size() > 0)
			return setVaContrattos;
		else
			return null;
	}

	private Set<VaOrario> creaVaOrario(CondizioniOfferte condizioniOfferte, VaDatiVacancy vaDatiVacancy, Date now,
			PfPrincipal principal, boolean merge) {
		String idOrario = condizioniOfferte.getIdmodalitalavoro();
		Set<VaOrario> setVaOrarios = new HashSet<VaOrario>();
		if (merge) {
			// Se sono nel caso di rettifica, cancello prima le vecchie
			// relazioni

			for (VaOrario entity : vaDatiVacancy.getVaOrarios()) {

				vaOrarioHome.remove(entity);
			}
			vaDatiVacancy.setVaOrarios(null);
		}

		if (idOrario != null && !("").equalsIgnoreCase(idOrario)) {
			VaOrario vaOrario = new VaOrario();
			DeOrario orario = deOrarioHome.findById(idOrario);
			if (orario == null) {
				return null;
			}
			vaOrario.setDeOrario(orario);
			vaOrario.setVaDatiVacancy(vaDatiVacancy);
			popolaAbstractEntity(now, principal, true, vaOrario);
			vaOrarioHome.persist(vaOrario);
			setVaOrarios.add(vaOrario);

		}
		if (setVaOrarios.size() > 0)
			return setVaOrarios;
		else
			return null;
	}

	/**
	 * Crea un'azienda a partire dai dati provenienti da SIL nel caso esiste un utente con la mail passata ma non
	 * risulta essere un'azienda ne viene creata una nuova non attiva con una mail errata
	 * 
	 * In caso di FLAGATTIVO = FALSE vengono settati i seguenti dati custom: username = SIL_CFAZIENDA mail =
	 * SIL_CFAZIENDA@mail.it
	 * 
	 * SERVE PER POTER INVIARE VACANCY A CL CON I DATI AZIENDA CORRETTI DELL'AZIENDA E NON CON QUELLI DEL CPI
	 * 
	 * @param datore
	 * @param flagAttiva
	 * @return AziendaInfo
	 */
	private AziendaInfo creaAziendaPalese(DatoreLavoro datore, boolean flagAttiva, String provenienza) {
		// per avvio di TRENTO
		Calendar dtAvvioClic = Calendar.getInstance();
		//dtAvvioClic.setTime(stConfigurazioneHome.findById(new Integer("1")).getDtAvvioClic());
		dtAvvioClic.setTime(tsGetOpzioniEJB.getDtAvvioClic());
		Calendar today = Calendar.getInstance();

		DatiAnagrafici datiAnag = datore.getDatiAnagrafici();
		DatiContatto datiCont = datore.getDatiContatto();
		// inserisce un nuovo utente per l'azienda richiedente
		// inserimento dei principal delle aziende
		RegisterAziendaDTO registerAzRichiedente = new RegisterAziendaDTO();
		// String[] arrEmail = emailRegAzRich.split("@");
		if (flagAttiva) {
			registerAzRichiedente.setUsername(datiAnag.getCodicefiscale().trim());
			registerAzRichiedente.setPassword(Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH));
			registerAzRichiedente.setPasswordConfirm(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			registerAzRichiedente.setEmail(datiCont.getEmail().trim());
			registerAzRichiedente.setEmailConfirm(datiCont.getEmail().trim());
		} else {
			if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				registerAzRichiedente.setUsername(Utils.cut("cl_" + datiAnag.getCodicefiscale(), 16, false));
				registerAzRichiedente.setPassword(Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH));
				registerAzRichiedente.setPasswordConfirm(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
				String mailRandom = "cl_" + Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH) + "@mail.it";
				registerAzRichiedente.setEmail(mailRandom);
				registerAzRichiedente.setEmailConfirm(mailRandom);
			} else {
				registerAzRichiedente.setUsername(Utils.cut("SIL_" + datiAnag.getCodicefiscale(), 16, false));
				registerAzRichiedente.setPassword(Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH));
				registerAzRichiedente.setPasswordConfirm(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
				String mailRandom = "SIL_" + Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH) + "@mail.it";
				registerAzRichiedente.setEmail(mailRandom);
				registerAzRichiedente.setEmailConfirm(mailRandom);
			}
		}
		registerAzRichiedente.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		registerAzRichiedente.setAttivo(false);
		registerAzRichiedente.setAcceptInformativa(true);
		registerAzRichiedente.setNome(Utils.cut(datiAnag.getCodicefiscale().trim(), 50, false));
		registerAzRichiedente.setCognome(Utils.cut(datiAnag.getDenominazione().trim(), 50, false));
		registerAzRichiedente.setCodiceFiscale(datiAnag.getCodicefiscale());
		registerAzRichiedente.setIndirizzo((datiCont.getIndirizzo() == null ? "via" : datiCont.getIndirizzo()));
		registerAzRichiedente.setComune(deComuneHome.findDTOById(datiCont.getIdcomune()));
		registerAzRichiedente.setCap((datiCont.getCap() == null ? "0" : datiCont.getCap()));
		registerAzRichiedente.setTelefono((datiCont.getTelefono() == null ? "0" : datiCont.getTelefono()));
		registerAzRichiedente.setDomanda("Il codice fiscale dell'azienda è?");
		registerAzRichiedente.setRisposta(datiAnag.getCodicefiscale());

		registerAzRichiedente.setRagioneSociale(datiAnag.getDenominazione());
		SedeDTO sedeAzRichiedente = new SedeDTO();
		sedeAzRichiedente.setIndirizzo(datiCont.getIndirizzo());
		sedeAzRichiedente.setComune(deComuneHome.findDTOById(datiCont.getIdcomune()));
		sedeAzRichiedente.setCap(datiCont.getCap());
		sedeAzRichiedente.setFax((datiCont.getFax() == null ? "0" : datiCont.getFax()));
		sedeAzRichiedente.setTelefono((datiCont.getTelefono() == null ? "0" : datiCont.getTelefono()));
		registerAzRichiedente.setSedeOperativa(sedeAzRichiedente);

		AziendaInfo azPalese = aziendaInfoHome.register(registerAzRichiedente, true);
		if (flagAttiva) {
			if (dtAvvioClic != null) {
				if (today.after(dtAvvioClic)) {
					EmailDTO registerAzRichEmail = EmailDTO
							.buildRegistrationEmailRichiestaPersonale(registerAzRichiedente);
					Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registerAzRichEmail);
				}
			}
		}

		// XXX per ora non viene creata
		// creaVetrinaMinimale(azPalese, datiAnag, datiCont);

		return azPalese;
	}

	private AziendaInfo creaAziendaPaleseTrento(DatoreLavoro datore, boolean flagInvioMail, boolean valida) {
		// per avvio di TRENTO
		Calendar dtAvvioClic = Calendar.getInstance();
		dtAvvioClic.setTime(tsGetOpzioniEJB.getDtAvvioClic());
		Calendar today = Calendar.getInstance();

		DatiAnagrafici datiAnag = datore.getDatiAnagrafici();
		DatiContatto datiCont = datore.getDatiContatto();
		// inserisce un nuovo utente per l'azienda richiedente
		// inserimento dei principal delle aziende
		RegisterAziendaDTO registerAzRichiedente = new RegisterAziendaDTO();

		registerAzRichiedente.setUsername(datiAnag.getCodicefiscale().trim());
		registerAzRichiedente.setPassword(Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH));
		registerAzRichiedente.setPasswordConfirm(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		registerAzRichiedente.setEmail(datiCont.getEmail().trim());
		registerAzRichiedente.setEmailConfirm(datiCont.getEmail().trim());

		registerAzRichiedente.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		registerAzRichiedente.setAttivo(false);
		registerAzRichiedente.setAcceptInformativa(true);
		registerAzRichiedente.setNome(Utils.cut(datiAnag.getCodicefiscale().trim(), 50, false));
		registerAzRichiedente.setCognome(Utils.cut(datiAnag.getDenominazione().trim(), 50, false));
		registerAzRichiedente.setCodiceFiscale(datiAnag.getCodicefiscale());
		registerAzRichiedente.setIndirizzo((datiCont.getIndirizzo() == null ? "via" : datiCont.getIndirizzo()));
		registerAzRichiedente.setComune(deComuneHome.findDTOById(datiCont.getIdcomune()));
		registerAzRichiedente.setCap((datiCont.getCap() == null ? "0" : datiCont.getCap()));
		registerAzRichiedente.setTelefono((datiCont.getTelefono() == null ? "0" : datiCont.getTelefono()));
		registerAzRichiedente.setDomanda("Il codice fiscale dell'azienda è?");
		registerAzRichiedente.setRisposta(datiAnag.getCodicefiscale());

		registerAzRichiedente.setRagioneSociale(datiAnag.getDenominazione());
		SedeDTO sedeAzRichiedente = new SedeDTO();
		sedeAzRichiedente.setIndirizzo(datiCont.getIndirizzo());
		sedeAzRichiedente.setComune(deComuneHome.findDTOById(datiCont.getIdcomune()));
		sedeAzRichiedente.setCap(datiCont.getCap());
		sedeAzRichiedente.setFax((datiCont.getFax() == null ? "0" : datiCont.getFax()));
		sedeAzRichiedente.setTelefono((datiCont.getTelefono() == null ? "0" : datiCont.getTelefono()));
		registerAzRichiedente.setSedeOperativa(sedeAzRichiedente);
		registerAzRichiedente.setFlgValida(valida);

		AziendaInfo azPalese = aziendaInfoHome.register(registerAzRichiedente, true);

		if (flagInvioMail) {
			if (dtAvvioClic != null) {
				if (today.after(dtAvvioClic)) {
					EmailDTO registerAzRichEmail = EmailDTO
							.buildRegistrationEmailRichiestaPersonale(registerAzRichiedente);
					Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registerAzRichEmail);
				}
			}
		}

		return azPalese;
	}

	// private void creaVetrinaMinimale(AziendaInfo info, DatiAnagrafici
	// datiAnag, DatiContatto datiCont) {
	//
	// if (!svAziendaInfoHome.exists(info.getIdAziendaInfo())) {
	// SvAziendaInfo svAziendaInfo = new SvAziendaInfo();
	// svAziendaInfo.setAziendaInfo(info);
	// svAziendaInfo.setFlagPubblicabile(true);
	// svAziendaInfo.setDeSvTemplate(deSvTemplateHome.findById("CLASS"));
	// svAziendaInfo.setChiSiamo("<p>" + datiAnag.getDenominazione() + "</p>");
	//
	// String dove = "";
	// DeComune comune = deComuneHome.findById(datiCont.getIdcomune());
	//
	// dove += "<p>";
	// dove += datiCont.getIndirizzo();
	// dove += ", " + comune.getDenominazione() + " (" +
	// comune.getDeProvincia().getTarga() + ")";
	// dove += "</p>";
	//
	// svAziendaInfo.setDoveSiamo(dove);
	//
	// svAziendaInfo.setProfiliRichiesti("<p></p>");
	// svAziendaInfo.setDtmIns(new Date());
	// svAziendaInfo.setDtmMod(new Date());
	// svAziendaInfo.setPfPrincipalIns(info.getPfPrincipalIns());
	// svAziendaInfo.setPfPrincipalMod(info.getPfPrincipalMod());
	// entityManager.persist(svAziendaInfo);
	// }
	// }

	private void popolaVaContatto(DatoreLavoro datore, DatiSistema datiSistema, VaContatto vaContatto, Date now,
			PfPrincipal principal, boolean merge) {
		String intermediario = datiSistema.getIntermediario();
		if (intermediario != null && !("").equalsIgnoreCase(intermediario)) {
			vaContatto.setCognomeRiferimentoPub(datiSistema.getDenominazioneintermediario());
			// vaContatto.setNomeRiferimentoPub();
			vaContatto.setFaxRiferimentoPub(datiSistema.getFax());
			vaContatto.setMail(datiSistema.getEmail());
			vaContatto.setTelRiferimentoPub(datiSistema.getTelefono());
		} else {
			vaContatto.setCognomeRiferimentoPub(datore.getDatiAnagrafici().getDenominazione());
			// vaContatto.setNomeRiferimentoPub();
			vaContatto.setFaxRiferimentoPub(datore.getDatiContatto().getFax());
			vaContatto.setMail(datore.getDatiContatto().getEmail());
			vaContatto.setTelRiferimentoPub(datore.getDatiContatto().getTelefono());
		}

		if (!merge) {
			vaContatto.setDtmIns(now);
			vaContatto.setPfPrincipalIns(principal);
		}

		vaContatto.setDtmMod(now);
		vaContatto.setPfPrincipalMod(principal);
	}

	private void popolaVaContattoSil(DatoreLavoro datore, DatiSistema datiSistema, VaContatto vaContatto, Date now,
			PfPrincipal principal, boolean merge) {
		// CARMELA 17/05/2013 se c'è l'intemediario e la visibilità è NO allora
		// il contatto è l'intermediario
		String intermediario = datiSistema.getIntermediario();
		if (intermediario != null && !("").equalsIgnoreCase(intermediario)
				&& (SiNo.NO).equals(datiSistema.getVisibilita())) {
			vaContatto.setCognomeRiferimentoPub(datiSistema.getDenominazioneintermediario());
			vaContatto.setFaxRiferimentoPub(datiSistema.getFax());
			vaContatto.setMail(datiSistema.getEmail());
			vaContatto.setTelRiferimentoPub(datiSistema.getTelefono());
		} else {
			vaContatto.setCognomeRiferimentoPub(datore.getDatiAnagrafici().getDenominazione());
			// vaContatto.setNomeRiferimentoPub();
			vaContatto.setFaxRiferimentoPub(datore.getDatiContatto().getFax());
			vaContatto.setMail(datore.getDatiContatto().getEmail());
			vaContatto.setTelRiferimentoPub(datore.getDatiContatto().getTelefono());
		}

		if (!merge) {
			vaContatto.setDtmIns(now);
			vaContatto.setPfPrincipalIns(principal);
		}

		vaContatto.setDtmMod(now);
		vaContatto.setPfPrincipalMod(principal);
	}

	private void popolaVaContattoAlternativo(DatoreLavoro datore, DatiSistema datiSistema,
			VaContatto vaContattoAlternativo, Date now, PfPrincipal principal, boolean merge) {
		String intermediario = datiSistema.getIntermediario();
		if (intermediario != null && !("").equalsIgnoreCase(intermediario)) {
			if (!merge) {
				vaContattoAlternativo.setDtmIns(now);
				vaContattoAlternativo.setPfPrincipalIns(principal);
			}

			String cognome = datore.getDatiAnagrafici().getDenominazione();
			String fax = datore.getDatiContatto().getFax();
			String email = datore.getDatiContatto().getEmail();
			String telefono = datore.getDatiContatto().getTelefono();
			if (cognome != null) {
				vaContattoAlternativo.setCognomeRiferimentoPub(cognome);
			}
			if (fax != null) {
				vaContattoAlternativo.setFaxRiferimentoPub(fax);
			}
			if (email != null) {
				vaContattoAlternativo.setMail(email);
			}
			if (telefono != null) {
				vaContattoAlternativo.setTelRiferimentoPub(telefono);
			}

			vaContattoAlternativo.setDtmMod(now);
			vaContattoAlternativo.setPfPrincipalMod(principal);
		}
	}

	public void inviaVacancyCliLavoro() {
		log.info("===FORZATURA BATCH CREAZIONE E INVIO COMUNICAZIONI VACANCY CLICLAVORO START===");
		List<VaVacancyCl> elencoVacancy = getVacancyDaInviare();
		for (VaVacancyCl vaVacancyCl : elencoVacancy) {
			// T1 - crea la comunicazione
			ClInvioComunicazione daInviare = creaComunicazione(vaVacancyCl);
			try {
				// T2 - invia la candidatura
				inviaVacancy(vaVacancyCl, daInviare);
			} catch (Exception e) {
				log.error("Errore durante l'invio della vacancy: " + e.getMessage());
			}
		}
		log.info("===FORZATURA BATCH CREAZIONE E INVIO COMUNICAZIONI VACANCY CLICLAVORO TERMINATO===");
	}

	/**
	 * Restituisce l'elenco di tutte le vacancy da inviare ovvero che sono in uno di questi stati:
	 * <ul>
	 * <li>PA</li>
	 * <li>MA</li>
	 * <li>VA</li>
	 * <li>CA</li>
	 * </ul>
	 * 
	 * @return
	 */
	private List<VaVacancyCl> getVacancyDaInviare() {
		TypedQuery<VaVacancyCl> query = entityManager.createNamedQuery("findVacancyDaInviare", VaVacancyCl.class);
		List<VaVacancyCl> list = query.getResultList();
		return list;
	}

	/**
	 * Effettua il reinvio di una vacancy verso clicLavoro
	 * 
	 * @param cvCandidaturaCl
	 * @param comunicazione
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws RemoteException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void reinviaVacancy(ClInvioComunicazione comunicazione) throws JAXBException, SAXException {
		VaVacancyCl vacancy = null;
		try {
			log.info("Invio la comunicazione " + comunicazione.getCodComunicazione());

			String codComunicazione = comunicazione.getCodComunicazione();
			vacancy = entityManager.createNamedQuery("findVacancyByCodComunicazione", VaVacancyCl.class)
					.setParameter("codComunicazione", codComunicazione).getSingleResult();
			if (vacancy == null) {
				throw new MyPortalException("Vacancy non riconosciuta");
			}

			String xmlRichiesta = comunicazione.getFileComunicazione();
			// costruisci la richiesta
			Richiesta_invioVacancy_Type richiesta = new Richiesta_invioVacancy_Type();
			richiesta.setVacancyXML(xmlRichiesta);

			// effettua l'invio
			String inviaVacancyAddress = wsEndpointHome.getClicLavoroInviaVacancyAddress();
			inviaVacancy = new CliclavoroWSProxy(inviaVacancyAddress);
			Risposta_invioVacancy_Type response = inviaVacancy.invioVacancy(richiesta);
			log.info("Invio effettuato");
			String nuovoCodice;
			String vecchioCodice = comunicazione.getDeStatoInvioCl().getCodStatoInvioCl();
			// se l'invio è andato a buon fine, imposto il nuovo stato
			// 'completato' alla comunicazione
			if (response.getTipo_Risposta().getValue().equalsIgnoreCase(Risposta_invioVacancy_TypeTipo_Risposta._OK)) {
				log.info("Response: OK");
				if (DeStatoInvioCl.IN_ATTESA_INVIO.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.INVIATA;
				} else if (DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.CHIUSURA_INVIATA;
				} else if (DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.VARIAZIONE_INVIATA;
				} else if (DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.PRIMO_INVIO_COMPLETATO;
				} else if (DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.PRIMO_INVIO_COMPLETATO;
				} else if (DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.CHIUSURA_INVIATA;
				} else if (DeStatoInvioCl.VARIAZIONE_INVIATA_ERRORE.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.VARIAZIONE_INVIATA;
				} else if (DeStatoInvioCl.INVIATA_ERRORE.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.INVIATA;
				} else {
					throw new MyPortalException("Stato invio comunicazione non riconosciuto");
				}

			}
			// se l'invio è andato in errore
			else {
				log.info("Response: KO");
				// salvo l'errore
				comunicazione.setDescrizioneErrore(response.getDescr_Esito());
				if (DeStatoInvioCl.IN_ATTESA_INVIO.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.INVIATA_ERRORE;
				} else if (DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE;
				} else if (DeStatoInvioCl.IN_ATTESA_INVIO_VARIAZIONE.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.VARIAZIONE_INVIATA_ERRORE;
				} else if (DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO.equals(vecchioCodice)) {
					nuovoCodice = DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE;
				} else {
					throw new MyPortalException("Stato invio comunicazione non riconosciuto");
				}

			}
			log.info("Il nuovo stato della comunicazione è " + nuovoCodice);
			// salva il nuovo stato
			it.eng.myportal.entity.decodifiche.DeStatoInvioCl nuovoStato = deStatoInvioClHome.findById(nuovoCodice);
			comunicazione.setDeStatoInvioCl(nuovoStato);
			vacancy.setDeStatoInvioCl(nuovoStato);

		} catch (RemoteException e) {
			log.error("Errore durante l'invio dell'xml");
			comunicazione.setDescrizioneErrore("Errore di invio xml: " + e.getMessage());
		} finally {
			// segna che l'invio è stato effettuato e aggiorna i record
			// candidatura e comunicazione
			comunicazione.setFlagInviato(true);
			try{
				log.info("aggiorno la comunicazione su DB");
				entityManager.merge(comunicazione);
				log.info("aggiorno la candidatura su DB");
				entityManager.merge(vacancy);
			}
			catch(IllegalArgumentException e){
				log.error("Errore nella merge in seguito alla chiamata del metodo reinviaVacancy di ClicLavoroVacancyEJB: " + e.getMessage());
			}
		}
	}

	public void riceviVacancyCliLavoro() {
		log.info("===FORZATURA BATCH CREAZIONE VACANCY MYPORTAL START===");
		List<ClInvioComunicazione> elencoVacancy = getComunicazioniDaInserire(AzioneServizio.INVIO_VACANCY);
		for (ClInvioComunicazione clInvioCom : elencoVacancy) {
			try {
				Vacancy vac = convertToVacancy(clInvioCom.getFileComunicazione());
				// inserisco la vacancy
				riceviVacancyCliclavoro(vac, clInvioCom.getMittente());
				// aggiorno il flginviato a INVIATO CORRETTAMENTE
				clInvioCom.setFlagInviato(true);
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
				clInvioComunicazioneHome.merge(clInvioCom);

			} catch (MyPortalException e) {
				if (ConstantsSingleton.MyPortalExceptionErrorCode.COMUNICAZIONE_PREC_NON_TROVATA.equals(e
						.getCodErrore())) {
					log.info("Ricezione vacancy cliclavoro, comunicazione precedente non trovata. Cod comunicazione= "
							+ clInvioCom.getCodComunicazione());

				} else {
					log.error("Errore durante la creazione della vacancy: " + e.getMessage());
					clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
					clInvioCom
							.setDescrizioneErrore("Errore durante la creazione della vacancy: " + e.getStrMessaggio());
					clInvioComunicazioneHome.merge(clInvioCom);
				}
			} catch (Exception e) {
				log.error("Errore durante la creazione della vacancy:" + e.getMessage());
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
				clInvioCom.setDescrizioneErrore("Errore durante la creazione della vacancy: " + e.getMessage());
				clInvioComunicazioneHome.merge(clInvioCom);
			}
		}
		log.info("===FORZATURA BATCH CREAZIONE VACANCY MYPORTAL TERMINATO===");
	}

	/**
	 * Restituisce l'elenco di tutte le candidature da inserire sul portale di provenienza da CLICLAVORO e SIL
	 * 
	 * 
	 * @return
	 */
	private List<ClInvioComunicazione> getComunicazioniDaInserire(AzioneServizio servizio) {
		TypedQuery<ClInvioComunicazione> query = entityManager.createNamedQuery("findComunicazioniDaInserire",
				ClInvioComunicazione.class).setParameter("azServ", servizio);
		List<ClInvioComunicazione> list = query.getResultList();
		return list;
	}
}
