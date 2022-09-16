package it.eng.myportal.entity.ejb;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.validation.Schema;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import it.eng.myportal.beans.UtilsBean;
import it.eng.myportal.cliclavoro.candidatura.AbilitazioniPatenti;
import it.eng.myportal.cliclavoro.candidatura.Albi;
import it.eng.myportal.cliclavoro.candidatura.Candidatura;
import it.eng.myportal.cliclavoro.candidatura.ConoscenzeInformatiche;
import it.eng.myportal.cliclavoro.candidatura.DatiAnagrafici;
import it.eng.myportal.cliclavoro.candidatura.DatiCurriculari;
import it.eng.myportal.cliclavoro.candidatura.DatiSistema;
import it.eng.myportal.cliclavoro.candidatura.DispComune;
import it.eng.myportal.cliclavoro.candidatura.Domicilio;
import it.eng.myportal.cliclavoro.candidatura.EsperienzeLavorative;
import it.eng.myportal.cliclavoro.candidatura.Formazione;
import it.eng.myportal.cliclavoro.candidatura.Istruzione;
import it.eng.myportal.cliclavoro.candidatura.Lavoratore;
import it.eng.myportal.cliclavoro.candidatura.Lingue;
import it.eng.myportal.cliclavoro.candidatura.Mobilita;
import it.eng.myportal.cliclavoro.candidatura.Patenti;
import it.eng.myportal.cliclavoro.candidatura.Patentini;
import it.eng.myportal.cliclavoro.candidatura.ProfessioneDesiderataDisponibilita;
import it.eng.myportal.cliclavoro.candidatura.Recapiti;
import it.eng.myportal.cliclavoro.candidatura.SM;
import it.eng.myportal.cliclavoro.candidatura.Sessocheck;
import it.eng.myportal.cliclavoro.candidatura.SiNo;
import it.eng.myportal.cliclavoro.candidatura.Territorio;
import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.AbstractEntity;
import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvCandidaturaCl;
import it.eng.myportal.entity.CvCompetenzeTrasv;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.CvFormazione;
import it.eng.myportal.entity.CvInformatica;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.CvLingua;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.CvProfDesiderate;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.decodifiche.DeAlbo;
import it.eng.myportal.entity.decodifiche.DeAmbitoDiffusione;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeGradoLin;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.decodifiche.DeOrario;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.DePatentino;
import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.DeTipoComunicazioneCl;
import it.eng.myportal.entity.decodifiche.DeTipoCorsoSil;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.decodifiche.sil.DeAlboSil;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.decodifiche.sil.DePatentinoSil;
import it.eng.myportal.entity.decodifiche.sil.DeTurnoSil;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;
import it.eng.myportal.entity.home.CvAlboHome;
import it.eng.myportal.entity.home.CvAltreInfoHome;
import it.eng.myportal.entity.home.CvCandidaturaClHome;
import it.eng.myportal.entity.home.CvCompetenzeTrasvHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvEsperienzeProfHome;
import it.eng.myportal.entity.home.CvFormazioneHome;
import it.eng.myportal.entity.home.CvInformaticaHome;
import it.eng.myportal.entity.home.CvIstruzioneHome;
import it.eng.myportal.entity.home.CvLinguaHome;
import it.eng.myportal.entity.home.CvPatenteHome;
import it.eng.myportal.entity.home.CvPatentinoHome;
import it.eng.myportal.entity.home.CvProfDesiderateHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.AbstractDecodeHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDisciplinareHome;
import it.eng.myportal.entity.home.decodifiche.DeAreaSilHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeBpMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeCorsoHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinSilHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeModalitaLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoCessazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoChiusuraHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoTitoloHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoComunicazioneClHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoCorsoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoSilHome;
import it.eng.myportal.entity.home.decodifiche.nodto.DeAreaFormazioneHome;
import it.eng.myportal.entity.home.decodifiche.nodto.DeAreaTitoloHome;
import it.eng.myportal.enums.AzioneServizio;
import it.eng.myportal.exception.EmailPfPrincipalException;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.AzioneCliclavoro;
import it.eng.myportal.utils.ConstantsSingleton.DeStatoInvioCl;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;
import it.gov.lavoro.servizi.cliclavoro.CliclavoroWSProxy;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Richiesta_invioCandidatura_Type;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_Type;
import it.gov.lavoro.servizi.cliclavoro.typesInvio.Risposta_invioCandidatura_TypeTipo_Risposta;

/**
 * Session Bean implementation class ClicLavoroEjb
 */
@Stateless
public class ClicLavoroEjb {

	protected final Log log = LogFactory.getLog(ClicLavoroEjb.class);

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@EJB
	CvCandidaturaClHome cvCandidaturaClHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeBpMansioneHome deBpMansioneHome;

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	DeAreaTitoloHome deAreaTitoloHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeGradoLinHome deGradoLinHome;

	@EJB
	DeAlboHome deAlboHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeAmbitoDiffusioneHome deAmbitoDiffusioneHome;

	@EJB
	DeCpiHome deCpiHome;

	@EJB
	DeProvenienzaHome deProvenienzaHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	CvEsperienzeProfHome cvEsperienzeProfHome;

	@EJB
	CvAlboHome cvAlboHome;

	@EJB
	CvFormazioneHome cvFormazioneHome;

	@EJB
	DeAreaFormazioneHome deAreaFormazioneHome;

	@EJB
	CvInformaticaHome cvInformaticaHome;

	@EJB
	CvIstruzioneHome cvIstruzioneHome;

	@EJB
	DeMotivoChiusuraHome deMotivoChiusuraHome;

	@EJB
	CvLinguaHome cvLinguaHome;

	@EJB
	CvPatentinoHome cvPatentinoHome;

	@EJB
	CvPatenteHome cvPatenteHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeTipoComunicazioneClHome deTipoComunicazioneClHome;

	@EJB
	CvProfDesiderateHome cvProfDesiderateHome;

	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;

	@EJB
	TsGetOpzioniEJB tsGetOpzioniEJB;

	@EJB
	DeStatoTitoloHome deStatoTitoloHome;

	@EJB
	DeTipoCorsoSilHome deTipoCorsoHome;

	@EJB
	DeCorsoHome deCorsoHome;

	@EJB
	DeAmbitoDisciplinareHome deAmbitoDisciplinareHome;

	@EJB
	DeGradoLinSilHome deGradoLinSilHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	@EJB
	DeAlboSilHome deAlboSilHome;

	@EJB
	DePatentinoSilHome dePatentinoSilHome;

	@EJB
	DeModalitaLinguaHome deModalitaLinguaHome;

	@EJB
	CvAltreInfoHome cvAltreInfoHome;

	@EJB
	CvCompetenzeTrasvHome cvCompetenzeTrasvHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	DeMotivoCessazioneHome deMotivoCessazioneHome;

	@EJB
	DeAreaSilHome deAreaSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DeTurnoSilHome deTurnoSilHome;

	@EJB
	DeRegioneHome deRegioneHome;

	@EJB
	DeTrasfertaHome deTrasfertaHome;

	CliclavoroWSProxy inviaCandidatura;

	@PersistenceContext
	protected EntityManager entityManager;

	/* cod_mansione della qualifica "ALTRO" */
	private static String DE_MANSIONE_COD_ALTRO = "107";

	private static String DE_AREA_TITOLO_COD_NT = "32";
	private static String DE_AREA_FORMAZIONE_COD_NT = "NT";

	public Candidatura createCandidaturaClicLavoro(CvDatiPersonali cvDatiPersonali) {
		try {
			// utente principale
			UtenteInfo userCandidatura = null;
			PfPrincipal pfPrincipalPalese = cvDatiPersonali.getPfPrincipalPalese();
			PfPrincipal pfPrincipal = cvDatiPersonali.getPfPrincipal();
			String cognome = null;
			String nome = null;
			if (pfPrincipalPalese != null) {
				userCandidatura = pfPrincipalPalese.getUtenteInfo();
				cognome = pfPrincipalPalese.getCognome();
				nome = pfPrincipalPalese.getNome();
			} else {
				userCandidatura = pfPrincipal.getUtenteInfo();
				cognome = pfPrincipal.getCognome();
				nome = pfPrincipal.getNome();
			}

			DeProvenienza provenienzaCv = cvDatiPersonali.getProvenienzaCurriculum();
			String codProvenienza = provenienzaCv.getCodProvenienza();
			String codiceFiscale = null;
			if (codProvenienza.startsWith(ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC)) {
				codiceFiscale = userCandidatura.getCodiceFiscale().toUpperCase();
			} else {
				codiceFiscale = userCandidatura.getCodiceFiscale().toUpperCase();
			}

			Candidatura candidatura = new Candidatura();
			Lavoratore lavoratore = new Lavoratore();

			DatiAnagrafici datiAnagrafici = new DatiAnagrafici();
			datiAnagrafici.setCodicefiscale(codiceFiscale);
			datiAnagrafici.setCognome(cognome);
			datiAnagrafici.setNome(nome);
			String giornoNascita = codiceFiscale.substring(9, 11);
			Sessocheck sesso = (Integer.valueOf(giornoNascita) > 40) ? Sessocheck.F : Sessocheck.M;
			datiAnagrafici.setSesso(sesso);
			datiAnagrafici.setDatanascita(Utils.toXMLGregorianCalendar(cvDatiPersonali.getDtNascita()));
			datiAnagrafici.setIdcomune(userCandidatura.getDeComuneNascita().getCodCom());
			if (userCandidatura.getDeCittadinanza() != null
					&& userCandidatura.getDeCittadinanza().getCodCittadinanza() != null) {
				datiAnagrafici.setIdcittadinanza(userCandidatura.getDeCittadinanza().getCodCittadinanza());
			}
			lavoratore.setDatiAnagrafici(datiAnagrafici);

			Domicilio domicilio = new Domicilio();
			domicilio.setIdcomune(userCandidatura.getDeComuneDomicilio().getCodCom());
			domicilio.setCap(userCandidatura.getDeComuneDomicilio().getCap());
			lavoratore.setDomicilio(domicilio);

			Recapiti recapiti = new Recapiti();
			recapiti.setIndirizzo(Utils.cut(cvDatiPersonali.getIndirizzoDomicilio(), 100));
			recapiti.setTelefono(Utils.cut(cvDatiPersonali.getTel1(), 15));
			recapiti.setCellulare(Utils.cut(cvDatiPersonali.getTel2(), 15));
			recapiti.setFax(Utils.cut(cvDatiPersonali.getFax(), 15));
			recapiti.setEmail(Utils.cut(cvDatiPersonali.getEmail(), 256));
			lavoratore.setRecapiti(recapiti);

			candidatura.setLavoratore(lavoratore);

			DatiCurriculari datiCurriculari = new DatiCurriculari();

			popolaEsperienzeClicLavoro(cvDatiPersonali, datiCurriculari);
			popolaIstruzioneClicLavoro(cvDatiPersonali, datiCurriculari);
			popolaFormazioneClicLavoro(cvDatiPersonali, datiCurriculari);
			popolaLingueClicLavoro(cvDatiPersonali, datiCurriculari);

			CvInformatica cvInformatica = cvDatiPersonali.getCvInformatica();
			if (cvInformatica != null) {
				ConoscenzeInformatiche conoscenzeInformatiche = new ConoscenzeInformatiche();
				conoscenzeInformatiche.setTipoconoscenza(Utils.cut(cvInformatica.getDescrizione(), 1000));
				datiCurriculari.setConoscenzeInformatiche(conoscenzeInformatiche);
			}

			Set<CvAlbo> cvAlbos = cvDatiPersonali.getCvAlbos();
			Set<CvPatente> cvPatentes = cvDatiPersonali.getCvPatentes();
			Set<CvPatentino> cvPatentinos = cvDatiPersonali.getCvPatentinos();
			AbilitazioniPatenti abilitazioniPatenti = new AbilitazioniPatenti();
			log.debug("costruisco abilitazione patenti");
			for (CvAlbo cvAlbo : cvAlbos) {
				DeAlbo deAlbo = cvAlbo.getDeAlbo();
				if (deAlbo.getFlagMin()) {
					log.debug("aggiungo " + deAlbo.getCodAlbo());
					List<String> albi = abilitazioniPatenti.getIdalbo();
					albi = Utils.addUniqueMapping(albi, deAlbo.getCodAlbo());
				}
			}
			for (CvPatente cvPatente : cvPatentes) {
				DePatente dePatente = cvPatente.getDePatente();
				log.debug("aggiungo " + dePatente.getCodPatente());
				List<String> patenti = abilitazioniPatenti.getIdpatenteguida();
				patenti = Utils.addUniqueMapping(patenti, dePatente.getCodPatente());
			}
			for (CvPatentino cvPatentino : cvPatentinos) {
				DePatentino dePatentino = cvPatentino.getDePatentino();
				log.debug("aggiungo " + dePatentino.getCodPatentino());
				List<String> patentini = abilitazioniPatenti.getIdpatentino();
				patentini = Utils.addUniqueMapping(patentini, dePatentino.getCodPatentino());
			}
			datiCurriculari.setAbilitazioniPatenti(abilitazioniPatenti);

			popolaProfessioniClicLavoro(cvDatiPersonali, datiCurriculari);

			candidatura.setDatiCurriculari(datiCurriculari);

			CvCandidaturaCl cvCandidaturaCl = cvCandidaturaClHome.findById(cvDatiPersonali.getIdCvDatiPersonali());
			DatiSistema datiSistema = new DatiSistema();
			datiSistema.setPercettore(SiNo.NO);
			datiSistema.setAmbitodiffusione(cvDatiPersonali.getDeAmbitoDiffusione().getCodAmbitoDiffusione());
			datiSistema.setDatainserimento(Utils.toXMLGregorianCalendar(new Date()));
			if (cvCandidaturaCl != null) {
				if (cvDatiPersonali.getDtScadenza() != null) {
					datiSistema.setDatascadenza(Utils.toXMLGregorianCalendar(cvDatiPersonali.getDtScadenza()));
				} else {
					Date now = new Date();
					datiSistema.setDatascadenza(Utils.toXMLGregorianCalendar(DateUtils.addDays(now, 180)));
				}

				String tipoComunicazione = cvCandidaturaCl.getDeTipoComunicazioneCl().getCodTipoComunicazioneCl();
				datiSistema.setTipocandidatura(tipoComunicazione);
				datiSistema.setCodicecandidatura(cvCandidaturaCl.getCodComunicazione());
				if (ConstantsSingleton.DeTipoComunicazioneCl.CHIUSURA.equals(tipoComunicazione)) {
					datiSistema.setMotivochiusura(cvDatiPersonali.getDeMotivoChiusura().getCodMotivoChiusura());
					datiSistema.setCodicecandidaturaprecedente(cvCandidaturaCl.getCodComunicazionePrec());
				}
			}

			boolean isIntermediato = false;

			if (provenienzaCv != null) {
				isIntermediato = cvDatiPersonali.isOriginarioSIL();
				String intermediario = cvDatiPersonali.getIntermediario();
				if (isIntermediato && intermediario != null) {
					DeCpi deCpi = deCpiHome.findByCodIntermediarioCl(intermediario);
					if (deCpi != null) {
						datiSistema.setIdintermediario(deCpi.getCodIntermediarioCl());
						datiSistema.setDenominazioneintermediario("Centro per l'impiego di " + deCpi.getDescrizione());
						datiSistema.setIndirizzo(deCpi.getIndirizzo());
						datiSistema.setIdcomune(deCpi.getDeComune().getCodCom());
						datiSistema.setCap(deCpi.getCap());
						datiSistema.setTelefono(deCpi.getTelRifCl());
						datiSistema.setFax(deCpi.getFax());
						datiSistema.setEmail(deCpi.getEmailRifCl());
						datiSistema.setVisibilita(SiNo.SI);
					}
				} else {
					datiSistema.setEmail(cvDatiPersonali.getEmail());
				}
			}

			candidatura.setDatiSistema(datiSistema);

			return candidatura;
		} catch (Exception e) {
			throw new MyPortalException("Errore durante l'invio a ClicLavoro", e, true);
		}
	}

	/**
	 * @param cvDatiPersonali
	 * @param datiCurriculari
	 */
	private void popolaProfessioniClicLavoro(CvDatiPersonali cvDatiPersonali, DatiCurriculari datiCurriculari) {
		for (CvProfDesiderate cvProfDesiderata : cvDatiPersonali.getCvProfDesiderates()) {
			ProfessioneDesiderataDisponibilita professioneDesiderata = new ProfessioneDesiderataDisponibilita();
			professioneDesiderata
					.setIdprofessione(cvProfDesiderata.getDeMansioneMin().getDeMansioneSil().getCodMansioneSil());
			professioneDesiderata.setDescrizioneesperienza(cvProfDesiderata.getBreveDescrProfessione());
			professioneDesiderata.setDescrizioneprofessione(cvProfDesiderata.getDescrizioneProfessione());
			professioneDesiderata.setDescrprofessione(cvProfDesiderata.getDeMansioneMin().getDescrizione());
			if (cvProfDesiderata.getFlagEspSettore() != null) {
				professioneDesiderata.setEsperienzasettore(cvProfDesiderata.getFlagEspSettore() ? SiNo.SI : SiNo.NO);
			}
			if (cvProfDesiderata.getFlagDispTrasferte() != null) {
				professioneDesiderata.setTrasferte(cvProfDesiderata.getFlagDispTrasferte() ? SiNo.SI : SiNo.NO);
			}
			for (DeOrario deOrario : cvProfDesiderata.getDeOrarios()) {
				List<String> listModLav = professioneDesiderata.getIdmodalitalavorativa();
				listModLav = Utils.addUniqueMapping(listModLav, deOrario.getCodOrario());
			}
			for (DeContratto deContratto : cvProfDesiderata.getDeContrattos()) {
				List<String> listTipologiaContratto = professioneDesiderata.getIdtipologiacontratto();
				listTipologiaContratto = Utils.addUniqueMapping(listTipologiaContratto, deContratto.getCodContratto());
			}
			if (cvProfDesiderata.getFlagDispMezzoProprio() != null) {
				professioneDesiderata.setMezzitrasporto(cvProfDesiderata.getFlagDispMezzoProprio() ? SiNo.SI : SiNo.NO);
			}
			datiCurriculari.getProfessioneDesiderataDisponibilita().add(professioneDesiderata);
		}
	}

	/**
	 * @param cvDatiPersonali
	 * @param datiCurriculari
	 * @throws DatatypeConfigurationException
	 */
	private void popolaEsperienzeClicLavoro(CvDatiPersonali cvDatiPersonali, DatiCurriculari datiCurriculari)
			throws DatatypeConfigurationException {
		for (CvEsperienzeProf cvEsperienzeProf : cvDatiPersonali.getCvEsperienzeProfs()) {
			EsperienzeLavorative esperienza = new EsperienzeLavorative();
			DeContratto deContratto = cvEsperienzeProf.getDeContratto();
			if (deContratto != null) {
				esperienza.setTipoesperienza(deContratto.getCodContratto());
			}
			if (cvEsperienzeProf.getDeMansioneMin() != null) {
				esperienza
						.setQualificasvolta(cvEsperienzeProf.getDeMansioneMin().getDeMansioneSil().getCodMansioneSil());
				esperienza.setDescrqualificasvolta(cvEsperienzeProf.getDeMansioneMin().getDescrizione());
			}
			esperienza.setPrincipalimansioni(cvEsperienzeProf.getAttivitaResponsabilita());
			esperienza.setNomedatore(Utils.cut(cvEsperienzeProf.getDatoreLavoro(), 100));
			esperienza.setDatainizio(Utils.toXMLGregorianCalendar(cvEsperienzeProf.getDa()));
			esperienza.setDatafine(Utils.toXMLGregorianCalendar(cvEsperienzeProf.getA()));
			datiCurriculari.getEsperienzeLavorative().add(esperienza);
		}
	}

	/**
	 * @param cvDatiPersonali
	 * @param datiCurriculari
	 */
	private void popolaIstruzioneClicLavoro(CvDatiPersonali cvDatiPersonali, DatiCurriculari datiCurriculari) {
		for (CvIstruzione cvIstruzione : cvDatiPersonali.getCvIstruziones()) {
			String codTitolo = cvIstruzione.getDeTitolo().getCodTitoloClicLavoro();
			if (codTitolo != null) {
				Istruzione istruzione = new Istruzione();
				istruzione.setTitolostudio(codTitolo);
				istruzione.setDescrizioneistruzione(Utils.cut(cvIstruzione.getDeTitolo().getDescrizione(), 100));
				istruzione.setVotazione(Utils.cut(cvIstruzione.getVotazione(), 30));
				datiCurriculari.getIstruzione().add(istruzione);
			}
		}
	}

	/**
	 * @param cvDatiPersonali
	 * @param datiCurriculari
	 */
	private void popolaFormazioneClicLavoro(CvDatiPersonali cvDatiPersonali, DatiCurriculari datiCurriculari) {
		for (CvFormazione cvFormazione : cvDatiPersonali.getCvFormaziones()) {
			Formazione formazione = new Formazione();
			formazione.setTitolocorso(Utils.cut(cvFormazione.getTitoloCorso(), 100));
			datiCurriculari.getFormazione().add(formazione);
		}
	}

	/**
	 * @param cvDatiPersonali
	 * @param datiCurriculari
	 */
	private void popolaLingueClicLavoro(CvDatiPersonali cvDatiPersonali, DatiCurriculari datiCurriculari) {
		for (CvLingua cvLingua : cvDatiPersonali.getCvLinguas()) {
			Lingue lingua = new Lingue();
			lingua.setIdlingua(cvLingua.getDeLingua().getCodLingua());

			DeGradoLin gradoLetto = cvLingua.getDeGradoLinLetto();
			DeGradoLin gradoScritto = cvLingua.getDeGradoLinScritto();
			DeGradoLin gradoParlato = cvLingua.getDeGradoLinParlato();

			if (gradoLetto != null) {
				lingua.setIdlivelloletto(gradoLetto.getCodGrado());
			} else {
				lingua.setIdlivelloletto("A1");
			}
			if (gradoScritto != null) {
				lingua.setIdlivelloscritto(gradoScritto.getCodGrado());
			} else {
				lingua.setIdlivelloscritto("A1");
			}
			if (gradoParlato != null) {
				lingua.setIdlivelloparlato(gradoParlato.getCodGrado());
			} else {
				lingua.setIdlivelloparlato("A1");
			}
			datiCurriculari.getLingue().add(lingua);
		}
	}

	private String convertToString(Candidatura candidatura) throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(Candidatura.class);
		Marshaller marshaller = jc.createMarshaller();
		Schema schema = Utils.getXsdSchema("cliclavoro" + File.separator + "candidatura.xsd");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		marshaller.setSchema(schema);
		StringWriter writer = new StringWriter();
		marshaller.marshal(candidatura, writer);
		String xmlRichiesta = writer.getBuffer().toString();
		return xmlRichiesta;
	}

	/**
	 * Crea una comunicazione da inviare a partire dalla candidatura e la inserisce nella tabella delle comunicazioni da
	 * inviare.
	 * 
	 * @param cvCandidaturaCl
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ClInvioComunicazione creaComunicazione(CvCandidaturaCl cvCandidaturaCl) {
		log.info("Creo la comunicazione relativa alla candidatura " + cvCandidaturaCl.getCodComunicazione()
				+ " in  stato " + cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl());

		ClInvioComunicazione comunicazione = getComunicazioneDaInviareByCodComunicazione(
				cvCandidaturaCl.getCodComunicazione());
		if (comunicazione == null) {
			Date now = new Date();
			comunicazione = new ClInvioComunicazione();
			comunicazione.setDtmIns(now);
			comunicazione.setDtmMod(now);
			comunicazione.setPfPrincipalIns(cvCandidaturaCl.getPfPrincipalMod());
			comunicazione.setPfPrincipalMod(cvCandidaturaCl.getPfPrincipalMod());
			comunicazione.setAzioneServizio(AzioneServizio.INVIO_CANDIDATURA);
			comunicazione.setCodComunicazione(cvCandidaturaCl.getCodComunicazione());
			comunicazione.setDeStatoInvioCl(cvCandidaturaCl.getDeStatoInvioCl());
			comunicazione.setFlagInviato(false);
			comunicazione.setMittente("MYPORTAL");
			comunicazione.setDestinatario(ConstantsSingleton.DeProvenienza.COD_MINISTERO);
			entityManager.persist(comunicazione);
		}
		return comunicazione;
	}

	/**
	 * Effettua l'invio di una candidatura verso clicLavoro
	 * 
	 * @param cvCandidaturaCl
	 * @param comunicazione
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws RemoteException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void inviaCandidatura(CvCandidaturaCl cvCandidaturaCl, ClInvioComunicazione comunicazione)
			throws JAXBException, SAXException {
		try {
			log.info("Invio la comunicazione " + cvCandidaturaCl.getCodComunicazione());
			// genera la candidatura
			CvDatiPersonali cvDatiPersonali = cvCandidaturaCl.getCvDatiPersonali();
			/*
			 * if (cvDatiPersonali.getPfPrincipal().getUtenteInfo() == null) {
			 * log.warn("Non da inviare idCvDatiPersonali:" + cvDatiPersonali.getIdCvDatiPersonali() +
			 * ", idPfPrincipal:" + cvDatiPersonali.getPfPrincipal().getIdPfPrincipal()); return; }
			 */
			Candidatura candidatura = createCandidaturaClicLavoro(cvDatiPersonali);
			// convertila in xml
			String xmlRichiesta = convertToString(candidatura);
			// costruisci la richiesta
			Richiesta_invioCandidatura_Type richiesta = new Richiesta_invioCandidatura_Type();
			richiesta.setCandidaturaXML(xmlRichiesta);
			// salva l'xml costruito
			comunicazione.setFileComunicazione(xmlRichiesta);
			entityManager.merge(comunicazione);
			// effettua l'invio
			String inviaCandidaturaAddress = wsEndpointHome.getClicLavoroInviaCandidaturaAddress();
			inviaCandidatura = new CliclavoroWSProxy(inviaCandidaturaAddress);
			Risposta_invioCandidatura_Type response = inviaCandidatura.invioCandidatura(richiesta);
			log.info("Invio effettuato: " + response);
			String nuovoCodice;
			String vecchioCodice = cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl();
			// se l'invio è andato a buon fine, imposto il nuovo stato
			// 'completato' alla comunicazione
			if (response.getTipo_Risposta().getValue()
					.equalsIgnoreCase(Risposta_invioCandidatura_TypeTipo_Risposta._OK)) {
				log.info("Response: OK");
				comunicazione.setDescrizioneErrore(response.getDescr_Esito());
				nuovoCodice = deStatoInvioClHome.calcolaStatoInvioCL(vecchioCodice, AzioneCliclavoro.CLICLAVORO_OK);
				if (nuovoCodice == null) {
					throw new MyPortalException("Stato invio comunicazione non riconosciuto");
				}
			} else {
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
			cvCandidaturaCl.setDeStatoInvioCl(nuovoStato);
			comunicazione.setFlagInviato(true);

		} catch (MarshalException me) {
			log.error("Errore durante la costruzione dell'xml" + me.getMessage());
			comunicazione.setDescrizioneErrore("Errore di costruzione xml: " + me.getLinkedException().getMessage());
			String vecchioCodice = cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl();
			String nuovoCodice = deStatoInvioClHome.calcolaStatoInvioCL(vecchioCodice, AzioneCliclavoro.CLICLAVORO_ERR);
			comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
			comunicazione.setFlagInviato(false);
			cvCandidaturaCl.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
		} catch (RemoteException e) {
			log.error("Errore durante l'invio dell'xml" + e.getMessage());
			comunicazione.setDescrizioneErrore("Errore di invio xml: " + e.getMessage());
			String vecchioCodice = cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl();
			String nuovoCodice = deStatoInvioClHome.calcolaStatoInvioCL(vecchioCodice, AzioneCliclavoro.CLICLAVORO_ERR);
			comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
			comunicazione.setFlagInviato(false);
			cvCandidaturaCl.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
		} catch (Exception e) {
			log.error("Errore Generico durante l'invio dell'xml" + e.getMessage());
			comunicazione.setDescrizioneErrore("Errore di invio xml: " + e.getMessage());
			String vecchioCodice = cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl();
			String nuovoCodice = deStatoInvioClHome.calcolaStatoInvioCL(vecchioCodice, AzioneCliclavoro.CLICLAVORO_ERR);
			comunicazione.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
			comunicazione.setFlagInviato(false);
			cvCandidaturaCl.setDeStatoInvioCl(deStatoInvioClHome.findById(nuovoCodice));
		} finally {
			// segna che l'invio è stato effettuato e aggiorna i record
			// candidatura e comunicazione
			log.info("aggiorno la comunicazione su DB");
			entityManager.merge(comunicazione);
			log.info("aggiorno la candidatura su DB");
			entityManager.merge(cvCandidaturaCl);
		}
	}

	/**
	 * Effettua il reinvio di una candidatura verso clicLavoro
	 * 
	 * @param cvCandidaturaCl
	 * @param comunicazione
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws RemoteException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void reinviaCandidatura(ClInvioComunicazione comunicazione) throws JAXBException, SAXException {
		CvCandidaturaCl candidatura = null;
		try {
			log.info("Invio la comunicazione " + comunicazione.getCodComunicazione());

			String codComunicazione = comunicazione.getCodComunicazione();
			candidatura = entityManager.createNamedQuery("findCandidaturaByCodComunicazione", CvCandidaturaCl.class)
					.setParameter("codComunicazione", codComunicazione).getSingleResult();
			if (candidatura == null) {
				throw new MyPortalException("Candidatura non riconosciuta");
			}

			String xmlRichiesta = comunicazione.getFileComunicazione();
			// costruisci la richiesta
			Richiesta_invioCandidatura_Type richiesta = new Richiesta_invioCandidatura_Type();
			richiesta.setCandidaturaXML(xmlRichiesta);

			// effettua l'invio
			String inviaCandidaturaAddress = wsEndpointHome.getClicLavoroInviaCandidaturaAddress();
			inviaCandidatura = new CliclavoroWSProxy(inviaCandidaturaAddress);
			Risposta_invioCandidatura_Type response = inviaCandidatura.invioCandidatura(richiesta);
			log.info("Invio effettuato");
			String nuovoCodice;
			String vecchioCodice = comunicazione.getDeStatoInvioCl().getCodStatoInvioCl();
			// se l'invio è andato a buon fine, imposto il nuovo stato
			// 'completato' alla comunicazione
			if (response.getTipo_Risposta().getValue()
					.equalsIgnoreCase(Risposta_invioCandidatura_TypeTipo_Risposta._OK)) {
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
			candidatura.setDeStatoInvioCl(nuovoStato);

		} catch (RemoteException e) {
			log.error("Errore durante l'invio dell'xml");
			comunicazione.setDescrizioneErrore("Errore di invio xml: " + e.getMessage());
		}

		try {
			// segna che l'invio è stato effettuato e aggiorna i record
			// candidatura e comunicazione
			comunicazione.setFlagInviato(true);
			log.info("aggiorno la comunicazione su DB");
			entityManager.merge(comunicazione);
			log.info("aggiorno la candidatura su DB");
			entityManager.merge(candidatura);
		} catch (Exception e) {
			log.error("Errore nella merge in seguito alla chiamata del metodo reinviaCandidatura di ClicLavoroEJB: "
					+ e.getMessage());
		}
	}

	public Candidatura convertToCandidatura(String xmlCandidatura) throws JAXBException {
		JAXBContext jaxbContext;
		Candidatura candidatura = null;
		try {
			jaxbContext = JAXBContext.newInstance(Candidatura.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			candidatura = (Candidatura) jaxbUnmarshaller.unmarshal(new StringReader(xmlCandidatura));
		} catch (JAXBException e) {
			log.error("Errore durante la costruzione dell'oggetto dall'xml: " + e.getMessage());
		}
		return candidatura;
	}

	/**
	 * Ricezione candidatura da SIL e PDD, inserisce il CV partendo dall'xml ricevuto se arriva dalla PDD inserisce
	 * solamente il CV se arriva dal SIL inserisce il CV e crea la comunicazione da invare alla PDD
	 * 
	 * 
	 * @param candidatura
	 * @param provenienza
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void riceviCandidaturaCliclavoro(Candidatura candidatura, String provenienza) {
		// Capisco se è una nuova comunicazione dalla combinazione dei
		// campi codice comunicazione e codice comunicazione precedente:
		// 1) Se com != null e prec == null si tratta di una nuova comunicazione
		// 2) Se prec != null si tratta di una rettifica
		// Algoritmo di ricerca:
		// 1) Se sono nel caso di nuovo inserimento devo cercare prima
		// se esiste già un record con quel coddice comunicazione
		String codComRic = candidatura.getDatiSistema().getCodicecandidatura();
		String codComPrecRic = candidatura.getDatiSistema().getCodicecandidaturaprecedente();
		String ambitoDiffusione = candidatura.getDatiSistema().getAmbitodiffusione();

		// Se il codice comunicazione è null errore
		if (codComRic == null) {
			throw new MyPortalException("Errore codice comunicazione nullo", true);
		}

		if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
			// se la comunicazione arriva dalla PDD

			CvCandidaturaCl cvCandidaturaCl = cvCandidaturaClHome.findByCodComunicazione(codComRic);
			CvDatiPersonali curriculum = null;
			if (codComPrecRic != null) {
				// caso di rettifica
				if (cvCandidaturaCl == null) {
					CvCandidaturaCl cvCandidaturaClPrec = cvCandidaturaClHome.findByCodComunicazione(codComPrecRic);
					if (cvCandidaturaClPrec != null) {
						// prima rettifica
						CvDatiPersonali cvDaModificare = cvDatiPersonaliHome
								.findById(cvCandidaturaClPrec.getIdCvDatiPersonali());
						CvDatiPersonali cvModificato = costruisciCvDaCandidaturaClicLavoro(candidatura, provenienza,
								true, cvDaModificare);

						costruisciCandidaturaCl(candidatura, cvModificato, cvCandidaturaClPrec, provenienza);
					} else {
						// dalla seconda rettifica in poi devo controllare che
						// ho lo stesso codcomunicazioneprec (è quello della
						// comunicazione originaria)
						cvCandidaturaClPrec = cvCandidaturaClHome.findByCodComunicazionePrec(codComPrecRic);
						if (cvCandidaturaClPrec != null) {
							// prima rettifica
							CvDatiPersonali cvDaModificare = cvDatiPersonaliHome
									.findById(cvCandidaturaClPrec.getIdCvDatiPersonali());
							CvDatiPersonali cvModificato = costruisciCvDaCandidaturaClicLavoro(candidatura, provenienza,
									true, cvDaModificare);

							costruisciCandidaturaCl(candidatura, cvModificato, cvCandidaturaClPrec, provenienza);
						} else {
							throw new MyPortalException(
									ConstantsSingleton.MyPortalExceptionErrorCode.COMUNICAZIONE_PREC_NON_TROVATA);
						}
					}
				} else {
					throw new MyPortalException("Rettifica candidatura già presente", true);
				}
			} else {
				// nuovo inserimento: la comunicazione non deve essere presente
				// su DB
				if (cvCandidaturaCl == null) {
					curriculum = costruisciCvDaCandidaturaClicLavoro(candidatura, provenienza, false, null);
					if (curriculum == null) {
						throw new MyPortalException("Errore nella costruzione del CV", true);
					}

					costruisciCandidaturaCl(candidatura, curriculum, null, provenienza);
				} else {
					throw new MyPortalException("Candidatura già presente", true);
				}
			}
		} else {
			CvDatiPersonali curriculum = null;

			// deve essere creato un solo CV per utente
			// da SIL arriva sempre un codice comunicazione diverso
			// per cui verifico se si tratta dello stesso lavoratore
			Integer idPfPrincipalPalese = getIdPfPrincipalPalese(candidatura);
			if (idPfPrincipalPalese != null) {
				if (UtilsBean.isStaticRER() && idPfPrincipalPalese == -1) {
					// throw new MyPortalException("Non è stato possibile importare il curriculum perchè esistono piu
					// utenze con lo stesso indirizzo email", true);
					throw new EmailPfPrincipalException(
							"Non è stato possibile importare il curriculum perchè esistono piu utenze con lo stesso indirizzo email");
				}
				curriculum = cvDatiPersonaliHome.findCvByUtPaleseFromSIL(idPfPrincipalPalese);
				if (curriculum == null) {
					curriculum = cvDatiPersonaliHome
							.findByCodComunicazione(candidatura.getDatiSistema().getCodicecandidatura());
				}
			}

			DeAmbitoDiffusione ambitoDiff = deAmbitoDiffusioneHome.findById(ambitoDiffusione);
			if (curriculum == null) {
				curriculum = costruisciCvDaCandidaturaClicLavoro(candidatura, provenienza, false, null);
				if (curriculum == null) {
					throw new MyPortalException("Errore nella costruzione del CV", true);
				}

				if (!ConstantsSingleton.DeAmbitoDiffusione.REGIONALE
						.equalsIgnoreCase(ambitoDiff.getCodAmbitoDiffusione())) {
					costruisciCandidaturaCl(candidatura, curriculum, null, provenienza);
				}
			} else {
				CvDatiPersonali cvModificato = costruisciCvDaCandidaturaClicLavoro(candidatura, provenienza, true,
						curriculum);
				CvCandidaturaCl cvCandidaturaCl = cvCandidaturaClHome.findById(cvModificato.getIdCvDatiPersonali());
				if (!ConstantsSingleton.DeAmbitoDiffusione.REGIONALE
						.equalsIgnoreCase(ambitoDiff.getCodAmbitoDiffusione())) {
					costruisciCandidaturaCl(candidatura, cvModificato, cvCandidaturaCl, provenienza);
				}
			}

		}
	}

	private CvCandidaturaCl costruisciCandidaturaCl(Candidatura candidatura, CvDatiPersonali curriculum,
			CvCandidaturaCl candidaturaClmod, String provenienza) {
		CvCandidaturaCl candidaturaCl = null;
		boolean rettifica;
		if (candidaturaClmod == null) {
			candidaturaCl = new CvCandidaturaCl();
			rettifica = false;
		} else {
			candidaturaCl = candidaturaClmod;
			rettifica = true;
		}
		if (rettifica) {
			// in caso di rettifica posso trovarmi in 2 situazioni:
			// 1) cod com prec è null: è la prima rettifica.
			// Quindi copio il cod com nel cod com prec e imposto il nuovo cod
			// com.
			// 2) cod com prec è diverso da null: è una rettifica successiva
			// alla prima.
			// Quindi il cod com prec lo lascio così com'è, aggiorno solo il cod
			// com.
			if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				if (candidaturaCl.getCodComunicazionePrec() == null) {
					candidaturaCl.setCodComunicazionePrec(candidaturaCl.getCodComunicazione());
					candidaturaCl.setCodComunicazione(candidatura.getDatiSistema().getCodicecandidatura());
				} else {
					candidaturaCl.setCodComunicazione(candidatura.getDatiSistema().getCodicecandidatura());
				}
			} else {
				// nel caso proviene da SIL il codice comunicazione prec è
				// quello già inviato alla PDD
				// e il codice comunicazione viene creato nuovo
				candidaturaCl.setCodComunicazionePrec(candidaturaClmod.getCodComunicazione());
				candidaturaCl.setCodComunicazione(calcolaNuovoCodComunicazioneInvioCandidatura());
			}
		} else {
			// In caso di primo inserimento setto i codici di comunicazione come
			// li ho ricevuti.
			if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				candidaturaCl.setCodComunicazione(candidatura.getDatiSistema().getCodicecandidatura());
				candidaturaCl.setCodComunicazionePrec(candidatura.getDatiSistema().getCodicecandidaturaprecedente());
			} else {
				// nel caso proviene da SIL viene creato nuovo
				candidaturaCl.setCodComunicazione(calcolaNuovoCodComunicazioneInvioCandidatura());
			}
		}
		candidaturaCl.setCvDatiPersonali(curriculum);
		if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
			candidaturaCl.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
		} else {
			if (rettifica) {
				candidaturaCl.setDeStatoInvioCl(deStatoInvioClHome.findById("VA"));
			} else {
				candidaturaCl.setDeStatoInvioCl(deStatoInvioClHome.findById("PA"));
			}
		}
		DeTipoComunicazioneCl deTipoComunicazioneCl = deTipoComunicazioneClHome
				.findById(candidatura.getDatiSistema().getTipocandidatura());
		candidaturaCl.setDeTipoComunicazioneCl(deTipoComunicazioneCl);
		candidaturaCl.setDtInvio(curriculum.getDtmMod());
		popolaAbstractEntity(curriculum.getDtmMod(), curriculum.getPfPrincipal(), !rettifica, candidaturaCl);
		if (rettifica) {
			cvCandidaturaClHome.merge(candidaturaCl);
		} else {
			cvCandidaturaClHome.persist(candidaturaCl);
		}
		return candidaturaCl;
	}

	public CvDatiPersonali costruisciCvDaCandidaturaClicLavoro(Candidatura candidatura, String provenienza,
			boolean merge, CvDatiPersonali cvdaModificare) {
		try {

			// Estraggo gli oggetti che compongono la candidatura
			Lavoratore lavoratore = candidatura.getLavoratore();
			DatiSistema datiSistema = candidatura.getDatiSistema();
			DatiCurriculari datiCurriculari = candidatura.getDatiCurriculari();

			DatiAnagrafici datiAnagrafici = lavoratore.getDatiAnagrafici();
			Domicilio domicilio = lavoratore.getDomicilio();

			// Devo controllare se esiste l'utente collegato a questo
			// curriculum.
			// Per farlo devo confrontare l'email con quelle contenute in
			// myportal
			Recapiti recapiti = lavoratore.getRecapiti();

			PfPrincipal utenzaPalese = trovaUtenzaPalese(recapiti, lavoratore, datiSistema, provenienza);
			PfPrincipal utenzaPrincipale = trovaUtenzaPrincipale(datiSistema, provenienza);
			PfPrincipal utenzaAdmin = pfPrincipalHome.findById(0);
			Date now = new Date();

			if (utenzaPrincipale == null)
				throw new MyPortalException("Utenza principale non trovata", true);

			String email = recapiti.getEmail();

			// Dati personali del lavoratore
			CvDatiPersonali cvDatiPersonali = null;
			if (merge) {
				cvDatiPersonali = cvdaModificare;
			} else {
				cvDatiPersonali = new CvDatiPersonali();
			}
			cvDatiPersonali.setPfPrincipal(utenzaPrincipale);
			cvDatiPersonali.setPfPrincipalPalese(utenzaPalese);

			if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				cvDatiPersonali
						.setDescrizione("CV ClicLavoro di " + (lavoratore.getDatiAnagrafici().getNome()).substring(0, 1)
								+ (lavoratore.getDatiAnagrafici().getCognome()).substring(0, 1));
			} else {

				if (UtilsBean.isStaticRER()) {
					/* provenienza sil se sei RER in setDescri metti la figura professionale leggo da DatiGenerali */
					if (candidatura.getDatiAggiuntivi() != null
							&& candidatura.getDatiAggiuntivi().getDatiGenerali() != null) {
						cvDatiPersonali.setDescrizione(candidatura.getDatiAggiuntivi().getDatiGenerali().getTitolo());
					}
					if (cvDatiPersonali.getDescrizione() == null
							|| ("").equalsIgnoreCase(cvDatiPersonali.getDescrizione())) {
						cvDatiPersonali.setDescrizione("CV di " + lavoratore.getDatiAnagrafici().getNome() + " "
								+ lavoratore.getDatiAnagrafici().getCognome());
					}
				} else {
					cvDatiPersonali.setDescrizione("CV di " + lavoratore.getDatiAnagrafici().getNome() + " "
							+ lavoratore.getDatiAnagrafici().getCognome());
				}
			}

			if (UtilsBean.isStaticRER()) {
				cvDatiPersonali.setFlagTrattamentoDati(new Boolean(true));
				cvDatiPersonali.setCodiceFiscale(datiAnagrafici.getCodicefiscale());
			}
			cvDatiPersonali.setFlagIdo(ConstantsSingleton.App.NUOVO_IDO);

			// Se la descrizione è vuota, lo chiamo "CV"
			if (cvDatiPersonali.getDescrizione() == null || ("").equalsIgnoreCase(cvDatiPersonali.getDescrizione())) {
				cvDatiPersonali.setDescrizione("CV");
			}

			// Controllo se il CV è stato inviato tramite il nuovo flusso da SIL (e quindi usa le decodifiche SIL)
			boolean usaDecodificheSil;
			if (candidatura.getDatiAggiuntivi() != null && candidatura.getDatiAggiuntivi().getDatiGenerali() != null
					&& candidatura.getDatiAggiuntivi().getDatiGenerali().getOpzTipoDecodifiche() != null) {
				cvDatiPersonali.setOpzTipoDecodifiche(
						candidatura.getDatiAggiuntivi().getDatiGenerali().getOpzTipoDecodifiche().toString());
				usaDecodificheSil = SM.S
						.equals(candidatura.getDatiAggiuntivi().getDatiGenerali().getOpzTipoDecodifiche());
			} else {
				cvDatiPersonali.setOpzTipoDecodifiche(CvDatiPersonali.OpzTipoDecodifiche.MINISTERO);
				usaDecodificheSil = false;
			}

			cvDatiPersonali.setFlagInviato(false);
			cvDatiPersonali.setDtNascita(Utils.fromXMLGregorianCalendar(datiAnagrafici.getDatanascita()));
			cvDatiPersonali.setDeComuneNascita(deComuneHome.findById(datiAnagrafici.getIdcomune()));
			cvDatiPersonali.setDeComuneDomicilio(deComuneHome.findById(domicilio.getIdcomune()));
			cvDatiPersonali.setIndirizzoDomicilio(recapiti.getIndirizzo());
			cvDatiPersonali.setTel1(recapiti.getTelefono());
			cvDatiPersonali.setTel2(recapiti.getCellulare());
			cvDatiPersonali.setFax(recapiti.getFax());
			cvDatiPersonali.setEmail(email);

			cvDatiPersonali.setDeAmbitoDiffusione(deAmbitoDiffusioneHome.findById(datiSistema.getAmbitodiffusione()));
			if (!(ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
				cvDatiPersonali.setCodComunicazioneSil(candidatura.getDatiSistema().getCodicecandidatura());
			}

			cvDatiPersonali.setProvenienzaCurriculum(deProvenienzaHome.findById(provenienza));

			if ("02".equalsIgnoreCase(datiSistema.getTipocandidatura())) {
				if (datiSistema.getMotivochiusura() != null) {
					cvDatiPersonali.setDeMotivoChiusura(deMotivoChiusuraHome.findById(datiSistema.getMotivochiusura()));
					cvDatiPersonali.setFlagEliminato(true);
				} else {
					cvDatiPersonali.setFlagEliminato(false);
				}
			} else {
				cvDatiPersonali.setFlagEliminato(false);
			}

			if (datiSistema.getVisibilita() == null) {
				cvDatiPersonali.setVisibilita(true);
			} else {
				cvDatiPersonali.setVisibilita(datiSistema.getVisibilita().toBoolean());
			}

			if (isStringNotEmpty(datiSistema.getIdintermediario())) {
				cvDatiPersonali.setIntermediario(datiSistema.getIdintermediario());
			}

			if (isStringNotEmpty(datiSistema.getDenominazioneintermediario())) {
				cvDatiPersonali.setDenominazioneIntermediario(datiSistema.getDenominazioneintermediario());
			}

			// CARMELA 20/10/2015
			// settare la data scadenza per CV provenienti da SPIL e CL
			if (datiSistema.getDatascadenza() != null) {
				cvDatiPersonali.setDtScadenza(Utils.fromXMLGregorianCalendar(datiSistema.getDatascadenza()));
			}

			// Nel nuovo flusso da SIL sono compresi due campi in più, titolo e descrizioneCv
			if (candidatura.getDatiAggiuntivi() != null
					&& isStringNotEmpty(candidatura.getDatiAggiuntivi().getDatiGenerali().getTitolo())) {
				cvDatiPersonali.setTitolo(candidatura.getDatiAggiuntivi().getDatiGenerali().getTitolo());
			}

			if (candidatura.getDatiAggiuntivi() != null && candidatura.getDatiAggiuntivi().getAnnotazioni() != null
					&& isStringNotEmpty(candidatura.getDatiAggiuntivi().getAnnotazioni().getNotecv())) {
				cvDatiPersonali.setDescrizioneCv(candidatura.getDatiAggiuntivi().getAnnotazioni().getNotecv());
			} else {
				cvDatiPersonali.setDescrizioneCv("");
			}

			// Riempio i campi generici della entity.
			popolaAbstractEntity(now, utenzaAdmin, !merge, cvDatiPersonali);

			// Questa persist di deve fare solo in caso di primo inserimento,
			// perchè salva la nuova testata senza relazioni
			if (!merge) {
				cvDatiPersonaliHome.persist(cvDatiPersonali);
			}

			// Dati curriculari
			AbilitazioniPatenti abilitazioniPatenti = datiCurriculari.getAbilitazioniPatenti();

			// creo tutte le relazioni. In caso di merge, cancello le vecchie e
			// ne creo di nuove
			Set<CvEsperienzeProf> setEsperienzeProfs = null;
			if (datiCurriculari != null && datiCurriculari.getEsperienzeLavorative() != null) {
				setEsperienzeProfs = creaListaEsperienzeProf(datiCurriculari.getEsperienzeLavorative(), cvDatiPersonali,
						now, utenzaAdmin, merge, usaDecodificheSil);
			}

			Set<CvIstruzione> setCvIstruziones = null;
			if (datiCurriculari != null && datiCurriculari.getIstruzione() != null) {
				setCvIstruziones = creaListaIstruzione(datiCurriculari.getIstruzione(), cvDatiPersonali, now,
						utenzaAdmin, merge, usaDecodificheSil);
			}

			Set<CvFormazione> setCvFormaziones = null;
			if (datiCurriculari != null && datiCurriculari.getFormazione() != null) {
				setCvFormaziones = creaListaFormazione(datiCurriculari.getFormazione(), cvDatiPersonali, now,
						utenzaAdmin, merge, usaDecodificheSil);
			}

			Set<CvLingua> setCvLinguas = null;
			if (datiCurriculari != null && datiCurriculari.getLingue() != null) {
				setCvLinguas = creaListaLingue(datiCurriculari.getLingue(), cvDatiPersonali, now, utenzaAdmin, merge,
						usaDecodificheSil);
			}

			// Resetto il campo noteAbilitazioni, in cui concatenerò le note di albi, patentini e patenti
			cvDatiPersonali.setNoteAbilitazioni("");

			Set<CvAlbo> setCvAlbos = null;
			if (usaDecodificheSil) {
				if (candidatura.getDatiAggiuntivi().getAbilitazioni() != null) {
					setCvAlbos = creaListaAlbiFromSil(candidatura.getDatiAggiuntivi().getAbilitazioni().getAlbi(),
							cvDatiPersonali, now, utenzaAdmin, merge);
				}
			} else {
				if (abilitazioniPatenti != null && abilitazioniPatenti.getIdalbo() != null) {
					setCvAlbos = creaListaAlbi(abilitazioniPatenti.getIdalbo(), cvDatiPersonali, now, utenzaAdmin,
							merge);
				}
			}

			Set<CvPatente> setCvPatentes = null;
			if (usaDecodificheSil) {
				if (candidatura.getDatiAggiuntivi().getAbilitazioni() != null) {
					setCvPatentes = creaListaPatentiFromSil(
							candidatura.getDatiAggiuntivi().getAbilitazioni().getPatenti(), cvDatiPersonali, now,
							utenzaAdmin, merge);
				}
			} else {
				if (abilitazioniPatenti != null && abilitazioniPatenti.getIdpatenteguida() != null) {
					setCvPatentes = creaListaPatenti(abilitazioniPatenti.getIdpatenteguida(), cvDatiPersonali, now,
							utenzaAdmin, merge);
				}
			}

			Set<CvPatentino> setCvPatentinis = null;
			if (usaDecodificheSil) {
				if (candidatura.getDatiAggiuntivi().getAbilitazioni() != null) {
					setCvPatentinis = creaListaPatentiniFromSil(
							candidatura.getDatiAggiuntivi().getAbilitazioni().getPatentini(), cvDatiPersonali, now,
							utenzaAdmin, merge);
				}
			} else {
				if (abilitazioniPatenti != null && abilitazioniPatenti.getIdpatentino() != null) {
					setCvPatentinis = creaListaPatentini(abilitazioniPatenti.getIdpatentino(), cvDatiPersonali, now,
							utenzaAdmin, merge);
				}
			}

			if (UtilsBean.isStaticRER()) {
				if ((datiSistema.getAmbitodiffusione() != null && !datiSistema.getAmbitodiffusione().equals("01"))) {
					if (datiCurriculari == null || datiCurriculari.getProfessioneDesiderataDisponibilita() == null
							|| (datiCurriculari.getProfessioneDesiderataDisponibilita() != null
									&& datiCurriculari.getProfessioneDesiderataDisponibilita().isEmpty())) {
						throw new MyPortalException("Mancano i dati curriculari", true);
					}
				}
			} else {
				if (datiCurriculari == null || datiCurriculari.getProfessioneDesiderataDisponibilita() == null) {
					throw new MyPortalException("Mancano i dati curriculari", true);
				}
			}
			Set<CvProfDesiderate> setCvProfDesiderates = creaListaProfDesiderate(
					datiCurriculari.getProfessioneDesiderataDisponibilita(), cvDatiPersonali, now, utenzaAdmin, merge,
					usaDecodificheSil, datiSistema);

			ConoscenzeInformatiche inform = datiCurriculari.getConoscenzeInformatiche();
			if (inform != null) {
				CvInformatica cvInformatica = creaInformatica(datiCurriculari.getConoscenzeInformatiche(),
						cvDatiPersonali, now, utenzaAdmin, merge);
				cvDatiPersonali.setCvInformatica(cvInformatica);
			}

			// Informazioni contenute solo nel nuovo flusso da SIL
			if (usaDecodificheSil) {
				CvAltreInfo cvAltreInfo = creaAltreInfo(candidatura, cvDatiPersonali, now, utenzaAdmin, merge);
				cvDatiPersonali.setCvAltreInfo(cvAltreInfo);
			}

			// Informazioni contenute solo nel nuovo flusso da SIL -- CVComptenzeTrasversali
			if (usaDecodificheSil) {
				CvCompetenzeTrasv cvCompetenzeTrasv = creaCompetenzeTrasversali(candidatura, cvDatiPersonali, now,
						utenzaAdmin, merge);
				Set<CvCompetenzeTrasv> cvCompetenzeTrasvs = new HashSet<CvCompetenzeTrasv>(0);
				cvCompetenzeTrasvs.add(cvCompetenzeTrasv);
				cvDatiPersonali.setCvCompetenzeTrasvs(cvCompetenzeTrasvs);
			}

			// Setto le nuove relazioni nel CV
			cvDatiPersonali.setCvEsperienzeProfs(setEsperienzeProfs);
			cvDatiPersonali.setCvIstruziones(setCvIstruziones);
			cvDatiPersonali.setCvFormaziones(setCvFormaziones);
			cvDatiPersonali.setCvLinguas(setCvLinguas);
			cvDatiPersonali.setCvAlbos(setCvAlbos);
			cvDatiPersonali.setCvPatentes(setCvPatentes);
			cvDatiPersonali.setCvPatentinos(setCvPatentinis);
			cvDatiPersonali.setCvProfDesiderates(setCvProfDesiderates);

			// Questo merge si fa in ogni caso, sia primo inserimento che in
			// rettifica.
			// a) Primo inserimento. Aggiorna la testata cone le relazioni.
			// b) Rettifica. Aggiorna tutti i campi della testata e le
			// relazioni.
			cvDatiPersonaliHome.merge(cvDatiPersonali);
			return cvDatiPersonali;
		} catch (Exception e) {
			throw new MyPortalException("Errore durante l'import del CV", e, true);
		}
	}

	private CvInformatica creaInformatica(ConoscenzeInformatiche conoscenzeInformatiche,
			CvDatiPersonali cvDatiPersonali, Date now, PfPrincipal utenzaAdmin, boolean merge) {

		if (merge) {
			cvInformaticaHome.remove(cvDatiPersonali.getCvInformatica());
		}
		cvDatiPersonali.setCvInformatica(null);
		CvInformatica cvInformatica = new CvInformatica();
		String descrizioneInf = "-";
		if (conoscenzeInformatiche.getTipoconoscenza() != null) {
			descrizioneInf += conoscenzeInformatiche.getTipoconoscenza();
		}

		cvInformatica.setDescrizione(descrizioneInf);

		cvInformatica.setCvDatiPersonali(cvDatiPersonali);
		popolaAbstractEntity(now, utenzaAdmin, true, cvInformatica);
		cvInformaticaHome.persist(cvInformatica);

		return cvInformatica;
	}

	private CvAltreInfo creaAltreInfo(Candidatura candidatura, CvDatiPersonali cvDatiPersonali, Date now,
			PfPrincipal utenzaAdmin, boolean merge) {
		if (merge) {
			cvAltreInfoHome.remove(cvDatiPersonali.getCvAltreInfo());
		}

		cvDatiPersonali.setCvAltreInfo(null);
		CvAltreInfo cvAltreInfo = new CvAltreInfo();
		cvAltreInfo.setFlagAutomunito(false);
		cvAltreInfo.setFlagMotomunito(false);

		// Faccio un ciclo sulle professioni desiderate; se ha indicato auto/motomunito, lo setto anche qui.
		for (ProfessioneDesiderataDisponibilita profDes : candidatura.getDatiCurriculari()
				.getProfessioneDesiderataDisponibilita()) {
			if (profDes.getDisponibilita() != null && profDes.getDisponibilita().getMobilita() != null) {
				Mobilita mobilita = profDes.getDisponibilita().getMobilita();
				if (mobilita.getDispauto() != null && mobilita.getDispauto().toBoolean()) {
					cvAltreInfo.setFlagAutomunito(true);
				}

				if (mobilita.getDispmoto() != null && mobilita.getDispmoto().toBoolean()) {
					cvAltreInfo.setFlagMotomunito(true);
				}
			}
		}

		if (candidatura.getDatiAggiuntivi() != null && candidatura.getDatiAggiuntivi().getAnnotazioni() != null
				&& isStringNotEmpty(candidatura.getDatiAggiuntivi().getAnnotazioni().getNotecv())) {
			cvAltreInfo.setUlterioriInfo(candidatura.getDatiAggiuntivi().getAnnotazioni().getNotecv());
		} else {
			cvAltreInfo.setUlterioriInfo("");
		}

		if (candidatura.getDatiAggiuntivi() != null && candidatura.getDatiAggiuntivi().getAnnotazioni() != null) {
			cvAltreInfo.setLimitazioni(candidatura.getDatiAggiuntivi().getAnnotazioni().getLimitazionicv());
		} else {
			cvAltreInfo.setLimitazioni("");
		}

		cvAltreInfo.setCvDatiPersonali(cvDatiPersonali);
		popolaAbstractEntity(now, utenzaAdmin, true, cvAltreInfo);
		cvAltreInfoHome.persist(cvAltreInfo);

		return cvAltreInfo;
	}

	private CvCompetenzeTrasv creaCompetenzeTrasversali(Candidatura candidatura, CvDatiPersonali cvDatiPersonali,
			Date now, PfPrincipal utenzaAdmin, boolean merge) {
		if (merge) {
			for (CvCompetenzeTrasv cvCompetenzeTrasv : cvDatiPersonali.getCvCompetenzeTrasvs()) {
				cvCompetenzeTrasvHome.remove(cvCompetenzeTrasv);
			}
			cvDatiPersonali.setCvCompetenzeTrasvs(null);
		}

		cvDatiPersonali.setCvCompetenzeTrasvs(null);
		CvCompetenzeTrasv cvCompetenzeTrasv = new CvCompetenzeTrasv();

		if (candidatura.getDatiAggiuntivi() != null && candidatura.getDatiAggiuntivi().getAnnotazioni() != null) {
			cvCompetenzeTrasv.setCapCompAltre(candidatura.getDatiAggiuntivi().getAnnotazioni().getCompetenze());
		} else {
			cvCompetenzeTrasv.setCapCompAltre("");
		}

		cvCompetenzeTrasv.setCvDatiPersonali(cvDatiPersonali);
		popolaAbstractEntity(now, utenzaAdmin, true, cvCompetenzeTrasv);
		cvCompetenzeTrasvHome.persist(cvCompetenzeTrasv);

		return cvCompetenzeTrasv;
	}

	/**
	 * ricerca l'utente palese per email di registrazione, nel caso di utenti da cliclavoro la mail ha un prefisso "cl_"
	 * 
	 * se non viene trovato nessun utente viene creato
	 * 
	 * l'utente trovato deve essere unico nel caso risultino più utenti con la stessa mail il cv non viene agganciato
	 * 
	 * @param recapiti
	 * @param lavoratore
	 * @param datiSistema
	 * @param provenienza
	 * @return
	 * @throws ParseException
	 */
	private PfPrincipal trovaUtenzaPalese(Recapiti recapiti, Lavoratore lavoratore, DatiSistema datiSistema,
			String provenienza) throws ParseException {
		PfPrincipal utenzaPalese = null;

		String emailLavoratore = recapiti.getEmail().toUpperCase();

		List<PfPrincipal> listaPrincipal = pfPrincipalHome.findByEmail(emailLavoratore);
		if (listaPrincipal == null || listaPrincipal.isEmpty()) {
			// XXX verifico se è un utente inserito da ClicLavoro con EMAIL =
			// "cl_MAILUTENTE"
			String emailLavCl = "cl_" + emailLavoratore;
			listaPrincipal = pfPrincipalHome.findByEmail(emailLavCl.toUpperCase());
		}

		if (listaPrincipal == null || listaPrincipal.isEmpty()) {
			// creo un nuovo utente
			utenzaPalese = registraNuovoUtente(datiSistema, lavoratore, provenienza, true);
		} else {
			// aggancio l'utente (se c'è ed è unico)
			if (listaPrincipal.size() == 1) {
				utenzaPalese = listaPrincipal.get(0);

				// si verifica se la mail è associata ad un utente cittadino
				// se risulta diverso non si aggancia a nessun utente palese
				if (utenzaPalese.isUtente()) {
					UtenteInfo user = utenzaPalese.getUtenteInfo();
					if (user.getCodiceFiscale() == null || ("").equalsIgnoreCase(user.getCodiceFiscale())) {
						user.setCodiceFiscale(lavoratore.getDatiAnagrafici().getCodicefiscale());
						entityManager.persist(user);
					}
				} else {
					utenzaPalese = null;
				}
			}
		}

		return utenzaPalese;
	}

	private PfPrincipal trovaUtenzaPrincipale(DatiSistema datiSistema, String provenienza) {

		PfPrincipal principal = null;
		String username = null;
		if (provenienza.startsWith(ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC)) {
			// Se arriva da SIL lo aggancio all'utente del gruppo Provincia
			// corrispondente al CPI scritto nel campo
			// "datiSistema.intermediario"
			DeCpi deCpi = deCpiHome.findByCodIntermediarioCl(datiSistema.getIdintermediario());
			if (deCpi == null) {
				throw new MyPortalException("CPI non riconosciuto", true);
			}
			DeProvincia deProvincia = deCpi.getDeProvincia();
			username = "redazione" + deProvincia.getTarga().toLowerCase();
			principal = pfPrincipalHome.findByUsername(username);
		} else {
			// Se arriva da CLICLAVORO, aggancio l'utente del gruppo
			// provincia/cliclavoro
			principal = pfPrincipalHome.getClicLavoro();
		}

		return principal;
	}

	private Set<CvEsperienzeProf> creaListaEsperienzeProf(List<EsperienzeLavorative> listaEsperienze,
			CvDatiPersonali cvDatiPersonali, Date now, PfPrincipal principal, boolean merge, boolean usaDecodificheSil)
			throws ParseException {
		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvEsperienzeProf cvEsperienzeProf : cvDatiPersonali.getCvEsperienzeProfs()) {
				cvEsperienzeProfHome.remove(cvEsperienzeProf);
			}
			cvDatiPersonali.setCvEsperienzeProfs(null);
		}

		Set<CvEsperienzeProf> setEsperienzeProfs = new HashSet<CvEsperienzeProf>();
		for (EsperienzeLavorative esperienzeLavorative : listaEsperienze) {
			CvEsperienzeProf cvEsperienzeProf = new CvEsperienzeProf();

			if (isStringNotEmpty(esperienzeLavorative.getTipoesperienza())) {
				cvEsperienzeProf.setDeContratto(deContrattoHome.findById(esperienzeLavorative.getTipoesperienza()));
			}

			if (usaDecodificheSil && isStringNotEmpty(esperienzeLavorative.getTiporapporto())) {
				cvEsperienzeProf.setDeContrattoSil(deContrattoSilHome.findById(esperienzeLavorative.getTiporapporto()));
			}

			// Se non c'è il codice qualifica, setto DeMansione = ALTRO e DeMansioneMin = null
			String codDotQualifica = esperienzeLavorative.getQualificasvolta();
			if (codDotQualifica == null || codDotQualifica.isEmpty()) {
				cvEsperienzeProf.setDeMansioneMin(null);
				cvEsperienzeProf.setDeMansione(deMansioneHome.findById(DE_MANSIONE_COD_ALTRO));
			} else {
				DeMansioneMin mansioneMin = deMansioneMinHome.findByDot(codDotQualifica);
				cvEsperienzeProf.setDeMansioneMin(mansioneMin);
				cvEsperienzeProf.setDeMansione(mansioneMin.getDeMansioneSil().getDeMansione());
				if (UtilsBean.isStaticRER()) {
					String codMansioneIstat = esperienzeLavorative.getCodmansione();
					DeMansioneMin mansioneMinTmp = null;

					if (codMansioneIstat != null && !codMansioneIstat.isEmpty()) {
						mansioneMinTmp = deMansioneMinHome.findById(codMansioneIstat);

						codMansioneIstat = codMansioneIstat.substring(0, 4);
						DeBpMansione deBpMansioneEsp = deBpMansioneHome.findById(codMansioneIstat);
						if (codMansioneIstat != null && !codMansioneIstat.isEmpty()) {
							cvEsperienzeProf.setDeBpMansione(deBpMansioneEsp);
						}
					}
					if (mansioneMinTmp != null) {
						cvEsperienzeProf.setDeMansioneMin(mansioneMinTmp);
						cvEsperienzeProf.setDeMansione(mansioneMinTmp.getDeMansioneSil().getDeMansione());
						cvEsperienzeProf.setProfessione(mansioneMinTmp.getDescrizione());
					}
				}
			}

			if (esperienzeLavorative.getPrincipalimansioni() != null) {
				cvEsperienzeProf.setAttivitaResponsabilita(esperienzeLavorative.getPrincipalimansioni());
			}
			cvEsperienzeProf.setDatoreLavoro(esperienzeLavorative.getNomedatore());

			if (esperienzeLavorative.getDatainizio() != null) {
				cvEsperienzeProf.setDa(Utils.fromXMLGregorianCalendar(esperienzeLavorative.getDatainizio()));
			} else {
				cvEsperienzeProf.setDa(now);
			}

			if (esperienzeLavorative.getDatafine() != null) {
				cvEsperienzeProf.setA(Utils.fromXMLGregorianCalendar(esperienzeLavorative.getDatafine()));
			}

			// Alcuni dati arrivano solo dal nuovo flusso SIL
			if (usaDecodificheSil) {
				if (isStringNotEmpty(esperienzeLavorative.getAreaesperienza())) {
					cvEsperienzeProf.setDeAreaSil(deAreaSilHome.findById(esperienzeLavorative.getAreaesperienza()));
				}

				if (isStringNotEmpty(esperienzeLavorative.getCodmvcessazione())) {
					cvEsperienzeProf.setDeMotivoCessazione(
							deMotivoCessazioneHome.findById(esperienzeLavorative.getCodmvcessazione()));
				}

				if (isStringNotEmpty(esperienzeLavorative.getCodcomunedatore())) {
					cvEsperienzeProf
							.setDeComuneDatore(deComuneHome.findById(esperienzeLavorative.getCodcomunedatore()));
				}

				cvEsperienzeProf.setIndirizzoDatore(esperienzeLavorative.getIndirizzodatore());
				cvEsperienzeProf.setAltroMotivoCessazione(esperienzeLavorative.getAltrocessazione());
				cvEsperienzeProf.setCodiceFiscale(esperienzeLavorative.getCfdatorelavoro());
				cvEsperienzeProf.setPartitaIva(esperienzeLavorative.getPivadatorelavoro());
			}

			cvEsperienzeProf.setCvDatiPersonali(cvDatiPersonali);
			popolaAbstractEntity(now, principal, true, cvEsperienzeProf);
			cvEsperienzeProfHome.persist(cvEsperienzeProf);
			setEsperienzeProfs.add(cvEsperienzeProf);
		}

		return (setEsperienzeProfs.size() > 0) ? setEsperienzeProfs : null;
	}

	private Set<CvIstruzione> creaListaIstruzione(List<Istruzione> listaIstruziones, CvDatiPersonali cvDatiPersonali,
			Date now, PfPrincipal principal, boolean merge, boolean usaDecodificheSil) {
		Set<CvIstruzione> setIstruziones = new HashSet<CvIstruzione>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvIstruzione entity : cvDatiPersonali.getCvIstruziones()) {
				cvIstruzioneHome.remove(entity);
			}
			cvDatiPersonali.setCvIstruziones(null);
		}

		for (Istruzione istruzione : listaIstruziones) {
			CvIstruzione cvIstruzione = new CvIstruzione();
			String codTitolo = istruzione.getTitolostudio();

			String codTitoloDeTitolo = istruzione.getCodtitolo();

			DeTitolo titolo = deTitoloHome.findById(codTitolo);

			// Se il codTitolo che mi è stato passato non corrisponde, aggiungo degli zeri finchè non ha length = 8
			// (per via della differenza tra come sono memorizzati i codici sul SIL e sul Portale)
			if (titolo == null) {
				StringBuilder padded = new StringBuilder(codTitolo);
				while ((padded.length() % 8) > 0) {
					padded.append("0");
				}
				titolo = deTitoloHome.findById(padded.toString());
			}

			if (titolo == null) {
				continue;
			}

			if (UtilsBean.isStaticRER()) {
				titolo = deTitoloHome.findById(codTitoloDeTitolo);
				if (titolo != null && titolo.getFlagIdo() != null && !titolo.getFlagIdo().booleanValue()) { // se
																											// flagido
					// =false
					// imposto come
					// titolo il
					// titolo padre
					if (titolo.getPadre() == null) {
						continue;
					}
					titolo = deTitoloHome.findById(titolo.getPadre().getCodTitolo());
				}
			}

			cvIstruzione.setDeTitolo(titolo);
			cvIstruzione.setVotazione(istruzione.getVotazione());
			cvIstruzione.setCvDatiPersonali(cvDatiPersonali);

			// Campi nuovi, inviati solo nel nuovo flusso da SIL
			if (usaDecodificheSil) {
				cvIstruzione.setSpecifica(istruzione.getSpecifica());
				if (UtilsBean.isStaticRER()) {
					if (istruzione.getSpecifica() == null
							|| (istruzione.getSpecifica() != null && istruzione.getSpecifica().isEmpty())) {
						cvIstruzione.setSpecifica(titolo.getDescrizione());
					}
				}
				cvIstruzione.setNomeIstituto(istruzione.getNomeistituto());

				if (isStringNotEmpty(istruzione.getAnnoistr())) {
					cvIstruzione.setNumAnno(Integer.parseInt(istruzione.getAnnoistr()));
				}
				if (isStringNotEmpty(istruzione.getCodcomistituto())) {
					cvIstruzione.setDeComune(deComuneHome.findById(istruzione.getCodcomistituto()));
				}
				if (isStringNotEmpty(istruzione.getStato())) {
					cvIstruzione.setDeStatoTitolo(deStatoTitoloHome.findById(istruzione.getStato()));
				}
			}

			popolaAbstractEntity(now, principal, true, cvIstruzione);
			cvIstruzioneHome.persist(cvIstruzione);
			setIstruziones.add(cvIstruzione);
		}

		return (setIstruziones.size() > 0) ? setIstruziones : null;
	}

	private Set<CvFormazione> creaListaFormazione(List<Formazione> listaFormaziones, CvDatiPersonali cvDatiPersonali,
			Date now, PfPrincipal principal, boolean merge, boolean usaDecodificheSil) {
		Set<CvFormazione> setFormaziones = new HashSet<CvFormazione>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvFormazione entity : cvDatiPersonali.getCvFormaziones()) {
				cvFormazioneHome.remove(entity);
			}
			cvDatiPersonali.setCvFormaziones(null);
		}

		for (Formazione formazione : listaFormaziones) {
			CvFormazione cvFormazione = new CvFormazione();
			cvFormazione.setTitoloCorso(formazione.getTitolocorso());
			cvFormazione.setCvDatiPersonali(cvDatiPersonali);

			/*
			 * if(UtilsBean.isStaticRER()) { DeAreaFormazione codAreaFormazioneNT =
			 * deAreaFormazioneHome.findById(DE_AREA_FORMAZIONE_COD_NT);
			 * cvFormazione.setDeAreaFormazione(codAreaFormazioneNT); }
			 */

			// Nuovi campi, popolati solo nel nuovo flusso da SIL
			if (usaDecodificheSil) {
				cvFormazione.setDescrizione(formazione.getDescrizione());
				cvFormazione.setPrincipaliTematiche(formazione.getContenuto());

				if (formazione.getCompletato() != null) {
					cvFormazione.setFlgCompletato(formazione.getCompletato().toBoolean());
				}

				if (isStringNotEmpty(formazione.getCodice())) {
					cvFormazione.setDeCorso(deCorsoHome.findById(formazione.getCodice()));
				}

				if (isStringNotEmpty(formazione.getAnnoform())) {
					cvFormazione.setNumAnno(Integer.parseInt(formazione.getAnnoform()));
				}

				if (isStringNotEmpty(formazione.getIdsede())) {
					cvFormazione.setDeComuneEnte(deComuneHome.findById(formazione.getIdsede()));
				}

				if (isStringNotEmpty(formazione.getSede())) {
					cvFormazione.setNomeIstituto(formazione.getSede());
				}

				if (isStringNotEmpty(formazione.getIdattestazionesil())) {
					DeTipoCorsoSil deTipoCorso = deTipoCorsoHome.findById(formazione.getIdattestazionesil());
					cvFormazione.setDeTipoCorso(deTipoCorso);
					cvFormazione.setAttestazioneConseguita(deTipoCorso.getDescrizione());
				}

				if (isStringNotEmpty(formazione.getCdnambitodisciplinare())) {
					cvFormazione.setDeAmbitoDisciplinare(
							deAmbitoDisciplinareHome.findById(formazione.getCdnambitodisciplinare()));
				}

				if (isStringNotEmpty(formazione.getDurata())) {
					cvFormazione.setDurata(formazione.getDurata());
				}
			}

			popolaAbstractEntity(now, principal, true, cvFormazione);
			cvFormazioneHome.persist(cvFormazione);
			setFormaziones.add(cvFormazione);
		}

		return (setFormaziones.size() > 0) ? setFormaziones : null;
	}

	private Set<CvLingua> creaListaLingue(List<Lingue> listaLingues, CvDatiPersonali cvDatiPersonali, Date now,
			PfPrincipal principal, boolean merge, boolean usaDecodificheSil) {
		Set<CvLingua> setLingues = new HashSet<CvLingua>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvLingua entity : cvDatiPersonali.getCvLinguas()) {
				cvLinguaHome.remove(entity);
			}
			cvDatiPersonali.setCvLinguas(null);
		}

		for (Lingue lingue : listaLingues) {
			CvLingua cvLingua = new CvLingua();

			DeLingua lingua = deLinguaHome.findById(lingue.getIdlingua());
			if (lingua == null)
				continue;
			cvLingua.setDeLingua(lingua);

			if (lingue.getIdlivelloletto() == null && lingue.getIdlivelloscritto() == null
					&& lingue.getIdlivelloparlato() == null) {
				cvLingua.setFlagMadrelingua(true);
			} else {
				cvLingua.setFlagMadrelingua(false);

				// Livelli di conoscenza della lingua, cod ministeriali
				if (isStringNotEmpty(lingue.getIdlivelloletto())) {
					cvLingua.setDeGradoLinLetto(deGradoLinHome.findById(lingue.getIdlivelloletto()));
				}
				if (isStringNotEmpty(lingue.getIdlivelloscritto())) {
					cvLingua.setDeGradoLinScritto(deGradoLinHome.findById(lingue.getIdlivelloscritto()));
				}
				if (isStringNotEmpty(lingue.getIdlivelloparlato())) {
					cvLingua.setDeGradoLinParlato(deGradoLinHome.findById(lingue.getIdlivelloparlato()));
				}

				// Livelli di conoscenza della lingua, codifiche SIL
				if (usaDecodificheSil) {
					if (isStringNotEmpty(lingue.getIdlivellolettosil())) {
						cvLingua.setDeGradoLinSilLetto(deGradoLinSilHome.findById(lingue.getIdlivellolettosil()));
					}
					if (isStringNotEmpty(lingue.getIdlivelloscrittosil())) {
						cvLingua.setDeGradoLinSilScritto(deGradoLinSilHome.findById(lingue.getIdlivelloscrittosil()));
					}
					if (isStringNotEmpty(lingue.getIdlivelloparlatosil())) {
						cvLingua.setDeGradoLinSilParlato(deGradoLinSilHome.findById(lingue.getIdlivelloparlatosil()));
					}
					if (UtilsBean.isStaticRER()) { // caso RER prevede sempre usaDecodificheSil == true e obbligatorietà
													// gradi lingua
						if (isStringNotEmpty(lingue.getIdlivellolettosil())
								&& isStringNotEmpty(lingue.getIdlivelloscrittosil())
								&& isStringNotEmpty(lingue.getIdlivelloparlatosil())) {
							if (lingue.getIdlivellolettosil().equalsIgnoreCase(ConstantsSingleton.Lingua.MADRELINGUA)
									|| lingue.getIdlivelloscrittosil()
											.equalsIgnoreCase(ConstantsSingleton.Lingua.MADRELINGUA)
									|| lingue.getIdlivelloparlatosil()
											.equalsIgnoreCase(ConstantsSingleton.Lingua.MADRELINGUA)) {
								cvLingua.setFlagMadrelingua(new Boolean(true));
								cvLingua.setDeGradoLinSilLetto(null);
								cvLingua.setDeGradoLinSilScritto(null);
								cvLingua.setDeGradoLinSilParlato(null);
								cvLingua.setDeGradoLinLetto(null);
								cvLingua.setDeGradoLinScritto(null);
								cvLingua.setDeGradoLinParlato(null);
							}
						}
					}
				}

			}

			cvLingua.setCvDatiPersonali(cvDatiPersonali);

			// Campi passati solo nel nuovo flusso da SIL
			if (usaDecodificheSil) {
				if (lingue.getCertificata() != null) {
					cvLingua.setFlgCertificata(lingue.getCertificata().toBoolean());
				}

				cvLingua.setAltraModalita(lingue.getAltramodlingua());
				if (isStringNotEmpty(lingue.getCodmodlingua())) {
					cvLingua.setDeModalitaLingua(deModalitaLinguaHome.findById(lingue.getCodmodlingua()));
				}
			}

			popolaAbstractEntity(now, principal, true, cvLingua);
			cvLinguaHome.persist(cvLingua);
			setLingues.add(cvLingua);
		}

		return (setLingues.size() > 0) ? setLingues : null;
	}

	private Set<CvPatentino> creaListaPatentini(List<String> idpatentino, CvDatiPersonali cvDatiPersonali, Date now,
			PfPrincipal principal, boolean merge) {
		Set<CvPatentino> setCvPatentinos = new HashSet<CvPatentino>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvPatentino entity : cvDatiPersonali.getCvPatentinos()) {
				cvPatentinoHome.remove(entity);
			}
			cvDatiPersonali.setCvPatentinos(null);
		}

		for (String s : idpatentino) {
			CvPatentino cvPatentino = new CvPatentino();
			DePatentino patentino = dePatentinoHome.findById(s);
			if (patentino == null)
				continue;
			cvPatentino.setDePatentino(patentino);
			cvPatentino.setCvDatiPersonali(cvDatiPersonali);
			popolaAbstractEntity(now, principal, true, cvPatentino);
			cvPatentinoHome.persist(cvPatentino);
			setCvPatentinos.add(cvPatentino);
		}

		return (setCvPatentinos.size() > 0) ? setCvPatentinos : null;
	}

	private Set<CvPatentino> creaListaPatentiniFromSil(List<Patentini> patentini, CvDatiPersonali cvDatiPersonali,
			Date now, PfPrincipal principal, boolean merge) {
		Set<CvPatentino> cvPatentinoList = new HashSet<CvPatentino>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvPatentino entity : cvDatiPersonali.getCvPatentinos()) {
				cvPatentinoHome.remove(entity);
			}
			cvDatiPersonali.setCvPatentinos(null);
		}

		for (Patentini patentino : patentini) {
			CvPatentino cvPatentino = new CvPatentino();
			if (isStringNotEmpty(patentino.getIdpatentino())) {
				DePatentino dePatentino = dePatentinoHome.findById(patentino.getIdpatentino());
				cvPatentino.setDePatentino(dePatentino);
			}

			if (isStringNotEmpty(patentino.getIdpatentinosil())) {
				DePatentinoSil dePatentinoSil = dePatentinoSilHome.findById(patentino.getIdpatentinosil());
				cvPatentino.setDePatentinoSil(dePatentinoSil);
			}

			cvPatentino.setCvDatiPersonali(cvDatiPersonali);
			popolaAbstractEntity(now, principal, true, cvPatentino);
			cvPatentinoHome.persist(cvPatentino);
			cvPatentinoList.add(cvPatentino);

			if (isStringNotEmpty(patentino.getNotepatentini())) {
				cvDatiPersonali.setNoteAbilitazioni(
						cvDatiPersonali.getNoteAbilitazioni() + patentino.getNotepatentini() + "; ");
			}
		}

		return (cvPatentinoList.size() > 0) ? cvPatentinoList : null;
	}

	private Set<CvPatente> creaListaPatenti(List<String> idpatente, CvDatiPersonali cvDatiPersonali, Date now,
			PfPrincipal principal, boolean merge) {
		Set<CvPatente> setCvPatentes = new HashSet<CvPatente>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvPatente entity : cvDatiPersonali.getCvPatentes()) {
				cvPatenteHome.remove(entity);
			}
			cvDatiPersonali.setCvPatentes(null);
		}

		for (String s : idpatente) {
			CvPatente cvPatente = new CvPatente();
			DePatente patente = dePatenteHome.findById(s);
			if (patente == null)
				continue;
			cvPatente.setDePatente(patente);
			cvPatente.setCvDatiPersonali(cvDatiPersonali);
			popolaAbstractEntity(now, principal, true, cvPatente);
			cvPatenteHome.persist(cvPatente);
			setCvPatentes.add(cvPatente);
		}

		return (setCvPatentes.size() > 0) ? setCvPatentes : null;
	}

	private Set<CvPatente> creaListaPatentiFromSil(List<Patenti> patenti, CvDatiPersonali cvDatiPersonali, Date now,
			PfPrincipal principal, boolean merge) {
		Set<CvPatente> cvPatenteList = new HashSet<CvPatente>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvPatente entity : cvDatiPersonali.getCvPatentes()) {
				cvPatenteHome.remove(entity);
			}
			cvDatiPersonali.setCvPatentes(null);
		}

		for (Patenti patente : patenti) {
			CvPatente cvPatente = new CvPatente();
			if (isStringNotEmpty(patente.getIdpatenteguida())) {
				DePatente dePatente = dePatenteHome.findById(patente.getIdpatenteguida());
				cvPatente.setDePatente(dePatente);
			}

			if (isStringNotEmpty(patente.getIdpatenteguidasil())) {
				DePatenteSil dePatenteSil = dePatenteSilHome.findById(patente.getIdpatenteguidasil());
				cvPatente.setDePatenteSil(dePatenteSil);
			}

			cvPatente.setCvDatiPersonali(cvDatiPersonali);
			popolaAbstractEntity(now, principal, true, cvPatente);
			cvPatenteHome.persist(cvPatente);
			cvPatenteList.add(cvPatente);

			if (isStringNotEmpty(patente.getNotepatenti())) {
				cvDatiPersonali
						.setNoteAbilitazioni(cvDatiPersonali.getNoteAbilitazioni() + patente.getNotepatenti() + "; ");
			}
		}

		return (cvPatenteList.size() > 0) ? cvPatenteList : null;
	}

	private Set<CvAlbo> creaListaAlbi(List<String> idalbo, CvDatiPersonali cvDatiPersonali, Date now,
			PfPrincipal principal, boolean merge) {
		Set<CvAlbo> setCvAlbos = new HashSet<CvAlbo>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvAlbo entity : cvDatiPersonali.getCvAlbos()) {
				cvAlboHome.remove(entity);
			}
			cvDatiPersonali.setCvAlbos(null);
		}

		for (String s : idalbo) {
			CvAlbo cvAlbo = new CvAlbo();
			DeAlbo albo = deAlboHome.findById(s);
			if (albo == null)
				continue;
			cvAlbo.setDeAlbo(albo);
			cvAlbo.setCvDatiPersonali(cvDatiPersonali);
			popolaAbstractEntity(now, principal, true, cvAlbo);
			cvAlboHome.persist(cvAlbo);
			setCvAlbos.add(cvAlbo);
		}

		return (setCvAlbos.size() > 0) ? setCvAlbos : null;
	}

	private Set<CvAlbo> creaListaAlbiFromSil(List<Albi> albi, CvDatiPersonali cvDatiPersonali, Date now,
			PfPrincipal principal, boolean merge) {
		Set<CvAlbo> setCvAlbos = new HashSet<CvAlbo>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvAlbo entity : cvDatiPersonali.getCvAlbos()) {
				cvAlboHome.remove(entity);
			}
			cvDatiPersonali.setCvAlbos(null);
		}

		for (Albi albo : albi) {
			CvAlbo cvAlbo = new CvAlbo();
			if (isStringNotEmpty(albo.getIdalbo())) {
				DeAlbo deAlbo = deAlboHome.findById(albo.getIdalbo());
				cvAlbo.setDeAlbo(deAlbo);
			}
			if (isStringNotEmpty(albo.getIdalbosil())) {
				DeAlboSil deAlboSil = deAlboSilHome.findById(albo.getIdalbosil());
				cvAlbo.setDeAlboSil(deAlboSil);
			}
			cvAlbo.setCvDatiPersonali(cvDatiPersonali);
			popolaAbstractEntity(now, principal, true, cvAlbo);
			cvAlboHome.persist(cvAlbo);
			setCvAlbos.add(cvAlbo);

			if (albo.getNotealbo() != null && !albo.getNotealbo().trim().isEmpty()) {
				cvDatiPersonali.setNoteAbilitazioni(cvDatiPersonali.getNoteAbilitazioni() + albo.getNotealbo() + "; ");
			}
		}

		return (setCvAlbos.size() > 0) ? setCvAlbos : null;
	}

	private Set<CvProfDesiderate> creaListaProfDesiderate(List<ProfessioneDesiderataDisponibilita> listaProfDesiderate,
			CvDatiPersonali cvDatiPersonali, Date now, PfPrincipal principal, boolean merge, boolean usaDecodificheSil,
			DatiSistema datiSistema) {
		Set<CvProfDesiderate> setCvProfDesiderates = new HashSet<CvProfDesiderate>();

		// Se sono nel caso di rettifica, cancello prima le vecchie relazioni
		if (merge) {
			for (CvProfDesiderate entity : cvDatiPersonali.getCvProfDesiderates()) {
				cvProfDesiderateHome.remove(entity);
			}
			cvDatiPersonali.setCvProfDesiderates(null);
		}

		for (ProfessioneDesiderataDisponibilita profDes : listaProfDesiderate) {
			CvProfDesiderate cvProfDesiderate = new CvProfDesiderate();
			DeMansioneMin mansioneMin = deMansioneMinHome.findByDot(profDes.getIdprofessione());
			cvProfDesiderate.setDeMansioneMin(mansioneMin);
			cvProfDesiderate.setDescrizioneProfessione(profDes.getDescrizioneprofessione());
			if (UtilsBean.isStaticRER()) {
				String codMansioneIstat = profDes.getCodmansione();
				DeMansioneMin mansioneMinTmp = null;

				if (codMansioneIstat != null && !codMansioneIstat.isEmpty()) {
					mansioneMinTmp = deMansioneMinHome.findById(codMansioneIstat);

					codMansioneIstat = codMansioneIstat.substring(0, 4);
					DeBpMansione deBpMansioneProf = deBpMansioneHome.findById(codMansioneIstat);
					if (codMansioneIstat != null && !codMansioneIstat.isEmpty()) {
						cvProfDesiderate.setDeBpMansione(deBpMansioneProf);
					}
				}

				if (mansioneMinTmp != null) {
					cvProfDesiderate.setDeMansioneMin(mansioneMinTmp);
					cvProfDesiderate.setDescrizioneProfessione(mansioneMinTmp.getDescrizione());
				}
			}
			if (profDes.getEsperienzasettore() != null) {
				cvProfDesiderate.setFlagEspSettore(profDes.getEsperienzasettore().toBoolean());
			}
			cvProfDesiderate.setBreveDescrProfessione(profDes.getDescrizioneesperienza());
			if (profDes.getTrasferte() != null) {
				cvProfDesiderate.setFlagDispTrasferte(profDes.getTrasferte().toBoolean());
			}
			if (profDes.getMezzitrasporto() != null) {
				cvProfDesiderate.setFlagDispMezzoProprio(profDes.getMezzitrasporto().toBoolean());
			}

			Set<DeOrario> setPrefModLavoro = creaListaDecodifiche(profDes.getIdmodalitalavorativa(), deOrarioHome);
			Set<DeContratto> setPrefContratto = creaListaDecodifiche(profDes.getIdtipologiacontratto(),
					deContrattoHome);
			cvProfDesiderate.setDeContrattos(setPrefContratto);
			cvProfDesiderate.setDeOrarios(setPrefModLavoro);

			// Se sono nel nuovo flusso da SIL, riempio altri campi
			if (usaDecodificheSil) {
				Set<DeOrarioSil> setPrefModLavoroSil = creaListaDecodifiche(profDes.getIdmodalitalavorativasil(),
						deOrarioSilHome);
				Set<DeContrattoSil> setPrefContrattoSil = creaListaDecodifiche(profDes.getIdtipologiacontrattosil(),
						deContrattoSilHome);
				cvProfDesiderate.setDeOrarioSils(setPrefModLavoroSil);
				cvProfDesiderate.setDeContrattoSils(setPrefContrattoSil);

				if (profDes.getDisponibilita() != null) {
					// Disponibilità turni
					Set<DeTurnoSil> setPrefTurnoSil = creaListaDecodifiche(profDes.getDisponibilita().getTurno(),
							deTurnoSilHome);
					cvProfDesiderate.setDeTurnoSils(setPrefTurnoSil);

					// Disponibilità territoriali
					Territorio territorio = profDes.getDisponibilita().getTerritorio();
					if (territorio != null) {
						if (profDes.getMezzitrasporto() != null) {
							cvProfDesiderate.setFlagDispMezzoProprio(profDes.getMezzitrasporto().toBoolean());
						}

						Set<DeProvincia> setDeProvincia = creaListaDecodifiche(territorio.getCodprovinciadisp(),
								deProvinciaHome);
						Set<DeRegione> setDeRegione = creaListaDecodifiche(territorio.getCodregionedisp(),
								deRegioneHome);
						Set<DeComune> setStati = creaListaDecodifiche(territorio.getCodstatodisp(), deComuneHome);
						cvProfDesiderate.setDeProvinciaList(setDeProvincia);
						cvProfDesiderate.setDeRegioneList(setDeRegione);
						cvProfDesiderate.setDeStatoList(setStati);

						// Aggiungo a parte i comuni, e nel frattempo costruisco anche il campo note concatenato.
						if (!territorio.getDispComune().isEmpty()) {
							Set<DeComune> deComuneSet = new HashSet<DeComune>();
							StringBuilder noteComuniBuilder = new StringBuilder();
							for (DispComune disponibilita : territorio.getDispComune()) {
								DeComune deComune = deComuneHome.findById(disponibilita.getCodcomdisp());
								if (deComune != null) {
									deComuneSet.add(deComune);
									if (isStringNotEmpty(disponibilita.getNotedispcomune())) {
										noteComuniBuilder.append(deComune.getDenominazione() + ": "
												+ disponibilita.getNotedispcomune() + "; ");
									}
								}
							}

							cvProfDesiderate.setDeComuneList(deComuneSet);
							cvProfDesiderate.setNoteComuni(noteComuniBuilder.toString());
						}
					}

					// Disponibilità per mobilità
					Mobilita mobilita = profDes.getDisponibilita().getMobilita();
					if (mobilita != null) {
						if (mobilita.getDispmezzipubblici() != null) {
							cvProfDesiderate.setFlagDispMezziPubblici(mobilita.getDispmezzipubblici().toBoolean());
						}

						if (mobilita.getPendolare() != null) {
							cvProfDesiderate.setFlagPendolarismo(mobilita.getPendolare().toBoolean());
						}

						if (mobilita.getPercorrenza() != null) {
							cvProfDesiderate.setNumOrePercorrenza(mobilita.getPercorrenza().intValue());
						}

						if (mobilita.getMobsettimanale() != null) {
							cvProfDesiderate.setFlagMobilitaSett(mobilita.getMobsettimanale().toBoolean());
						}

						if (isStringNotEmpty(mobilita.getTipotrasferta())) {
							cvProfDesiderate.setDeTrasferta(deTrasfertaHome.findById(mobilita.getTipotrasferta()));
						}

						cvProfDesiderate.setNoteDisponibilita(mobilita.getNotedisponibilita());
					}
				}
			}

			cvProfDesiderate.setCvDatiPersonali(cvDatiPersonali);
			popolaAbstractEntity(now, principal, true, cvProfDesiderate);
			cvProfDesiderateHome.persist(cvProfDesiderate);

			setCvProfDesiderates.add(cvProfDesiderate);
		}

		if (setCvProfDesiderates.size() > 0) {
			return setCvProfDesiderates;
		} else {
			if (UtilsBean.isStaticRER()) {
				if ((datiSistema.getAmbitodiffusione() != null && !datiSistema.getAmbitodiffusione().equals("01"))) {
					throw new MyPortalException("Professioni desiderate non presenti nel tracciato record", true);
				} else {
					return setCvProfDesiderates;
				}
			} else {
				throw new MyPortalException("Professioni desiderate non presenti nel tracciato record", true);
			}
		}
	}

	private PfPrincipal registraNuovoUtente(DatiSistema datiSistema, Lavoratore lavoratore, String provenienza,
			boolean pwdScaduta) throws ParseException {
		RegisterUtenteDTO dto = new RegisterUtenteDTO();

		// per avvio di TRENTO
		Calendar dtAvvioClic = Calendar.getInstance();
		//dtAvvioClic.setTime(stConfigurazioneHome.findById(new Integer("1")).getDtAvvioClic());
		dtAvvioClic.setTime(tsGetOpzioniEJB.getDtAvvioClic());
		Calendar today = Calendar.getInstance();

		dto.setAcceptInformativa(true);
		dto.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
		dto.setAttivo(false);
		dto.setAutenticazioneForte(false);
		dto.setCap(lavoratore.getDomicilio().getCap());
		DeCittadinanzaDTO cittadinanzaDTO = new DeCittadinanzaDTO();
		cittadinanzaDTO.setId(lavoratore.getDatiAnagrafici().getIdcittadinanza());
		dto.setCittadinanza(cittadinanzaDTO);
		dto.setCodiceFiscale(lavoratore.getDatiAnagrafici().getCodicefiscale());
		dto.setCodiceRichiestaAutForte(null);
		String cognomeXml = lavoratore.getDatiAnagrafici().getCognome();
		String cognome = cognomeXml.trim();
		dto.setCognome(cognome);
		DeComuneDTO comune = deComuneHome.findDTOById(lavoratore.getDomicilio().getIdcomune());
		dto.setComune(comune);

		dto.setDataAssicurata(null);
		dto.setDataNascita(Utils.fromXMLGregorianCalendar(lavoratore.getDatiAnagrafici().getDatanascita()));
		dto.setDataScadenzaDocumento(null);
		dto.setDocumentoIdentita(null);
		dto.setDocumentoSoggiorno(null);
		dto.setDomanda("Quale e' il tuo codice fiscale?");
		dto.setDomicilio(comune);
		String emailUtente = lavoratore.getRecapiti().getEmail();
		if ((ConstantsSingleton.DeProvenienza.COD_MINISTERO).equalsIgnoreCase(provenienza)) {
			// nel caso arrivi da CL la mail è settata in modo che l'utente non
			// può mai riceverla
			dto.setEmail("cl_" + emailUtente);
			dto.setEmailConfirm("cl_" + emailUtente);

			// nel caso il comune non è in emilia romagna inserisco di default
			// la provincia del capoluogo di regione BOLOGNA
			String idProv = comune.getIdProvincia();
			DeProvincia deProvincia = deProvinciaHome.findById(idProv);
			String codRegione = deProvincia.getDeRegione().getCodRegione();

			if (!"8".equalsIgnoreCase(codRegione)) {
				dto.setProvincia(deProvinciaHome.findDTOById("37"));
			} else {
				dto.setProvincia(deProvinciaHome.findDTOById(comune.getIdProvincia()));
			}

		} else {
			dto.setEmail(emailUtente);
			dto.setEmailConfirm(emailUtente);

			// nel caso di provenienza da SIL la provincia è quella del CPI
			DeCpi deCpi = deCpiHome.findByCodIntermediarioCl(datiSistema.getIdintermediario());
			if (deCpi == null) {
				dto.setProvincia(deProvinciaHome.findDTOById("37"));
			} else {
				DeProvincia deProvincia = deCpi.getDeProvincia();
				dto.setProvincia(deProvinciaHome.findDTOById(deProvincia.getCodProvincia()));
			}

		}

		dto.setIndirizzo(lavoratore.getRecapiti().getIndirizzo());
		dto.setIndirizzoPEC(null);
		String nomeXml = lavoratore.getDatiAnagrafici().getNome();
		String nome = nomeXml.trim();
		dto.setNome(nome);
		dto.setNumeroAssicurata(null);
		dto.setNumeroDocumento(null);
		String password = Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH);
		dto.setPassword(password);
		dto.setPasswordConfirm(password);
		dto.setRegisterFromProvider(false);
		dto.setRisposta(lavoratore.getDatiAnagrafici().getCodicefiscale());
		if (lavoratore.getRecapiti().getTelefono() != null) {
			dto.setTelefono(lavoratore.getRecapiti().getTelefono());
		}

		String username = creaUsername(
				"cl_" + nome.substring(0, 1) + Utils.escapeChrUsername(cognome).replaceAll(" {1,}", ""));
		dto.setUsername(Utils.cut(username.replaceAll("'", "").toLowerCase(), 16));
		Integer idPfPrincipal = utenteInfoHome.register(dto, pwdScaduta);

		if (provenienza.startsWith(ConstantsSingleton.DeProvenienza.COD_SIL_GENERIC)) {
			if (dtAvvioClic != null) {
				if (today.after(dtAvvioClic)) {
					EmailDTO registerEmail = null;
					if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER))
						registerEmail = EmailDTO.buildRegistrationEmailCurriculumDaSil(dto);
					else
						registerEmail = EmailDTO.buildRegistrationEmailCurriculum(dto);
					Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registerEmail);
				}
			}
		}

		return pfPrincipalHome.findById(idPfPrincipal);
	}

	public String creaUsername(String userBasename) {
		int lunghezza = 16;
		String newUsername = null;
		// tonco quello che mi arriva a 16
		String usernameTroncato = Utils.cut(userBasename, lunghezza, false);
		List<PfPrincipal> principal = pfPrincipalHome.findByPartialUsername(usernameTroncato);
		if (principal.isEmpty()) {
			newUsername = usernameTroncato;
		} else {
			// devo concateneare un numero progressivo allo user visto che gia
			// esiste,
			boolean usernameExist = true;
			int progressivo = 1;
			while (usernameExist && progressivo < 1000) {
				String postisso = "_" + progressivo;
				newUsername = usernameTroncato + postisso;
				if (newUsername.length() > lunghezza) {
					newUsername = Utils.cut(usernameTroncato, (lunghezza - postisso.length()), false) + postisso;
				}

				usernameExist = pfPrincipalHome.findByUsername(newUsername) == null ? false : true;
				progressivo++;
			}

		}

		return newUsername;
	}

	/** Controllo che una stringa non sia nè null, nè stringa vuota, nè solo spazi */
	private boolean isStringNotEmpty(String stringa) {
		return (stringa != null && !stringa.trim().isEmpty());
	}

	/** Crea una lista di decodifiche, partendo dalla lista dei loro ID */
	private <T extends AbstractDecodeEntity, D extends IDecode, H extends AbstractDecodeHome<T, D>> Set<T> creaListaDecodifiche(
			List<String> codDecodificaList, H home) {
		Set<T> decodificaSet = new HashSet<T>();
		for (String codDecodifica : codDecodificaList) {
			T decodifica = home.findById(codDecodifica);
			if (decodifica != null) {
				decodificaSet.add(decodifica);
			}
		}
		return decodificaSet;
	}

	/** Faccio un metodo a parte per i comuni, dato che non mi arriva una lista di stringhe ma di DispComune **/
	private Set<DeComune> creaListaDispComuni(List<DispComune> disponibilitaList) {
		Set<DeComune> deComuneSet = new HashSet<DeComune>();
		for (DispComune disponibilita : disponibilitaList) {
			DeComune comune = deComuneHome.findById(disponibilita.getCodcomdisp());
			if (comune != null) {
				deComuneSet.add(comune);
			}
		}
		return deComuneSet;
	}

	/** Riempio i campi comuni a tutte le entity (dtmIns/mod, idPfPrincipalIns/Mod) */
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

	private ClInvioComunicazione getComunicazioneDaInviareByCodComunicazione(String codComunicazione) {
		TypedQuery<ClInvioComunicazione> query = entityManager
				.createNamedQuery("findComunicazioneDaInviareByCodComunicazione", ClInvioComunicazione.class)
				.setParameter("codComunicazione", codComunicazione);
		List<ClInvioComunicazione> clInvioCom = query.getResultList();
		if (clInvioCom.size() > 0) {
			return clInvioCom.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Calcola un nuovo codiceComunicazione per l'invio di dati a clicLavoro relativi ad una candidatura
	 * 
	 * @return
	 */
	public String calcolaNuovoCodComunicazioneInvioCandidatura() {
		return calcolaNuovoCodComunicazione(true, true);
	}

	/**
	 * Calcola un nuovo codiceComunicazione per l'invio chiusura a clicLavoro relativi ad una candidatura
	 * 
	 * @return
	 */
	public String calcolaNuovoCodComunicazioneChiusuraCandidatura() {
		return calcolaNuovoCodComunicazione(false, true);
	}

	/**
	 * Calcola un nuovo codiceComunicazione per l'invio di dati a clicLavoro relativi ad una vacancy
	 * 
	 * @return
	 */
	public String calcolaNuovoCodComunicazioneInvioVacancy() {
		return calcolaNuovoCodComunicazione(true, false);
	}

	/**
	 * Calcola un nuovo codiceComunicazione per l'invio chiusura a clicLavoro relativi ad una vacancy
	 * 
	 * @return
	 */
	public String calcolaNuovoCodComunicazioneChiusuraVacancy() {
		return calcolaNuovoCodComunicazione(false, false);
	}

	/**
	 * Calcola un nuovo codiceComunicazione per l'invio a ClicLavoro, in base al parametro in input genera un codice di
	 * invio o di chiusura
	 * 
	 * @param chiusura
	 * @return
	 */
	private String calcolaNuovoCodComunicazione(boolean chiusura, boolean candidatura) {
		String regioneInvio = StringUtils.leftPad(ConstantsSingleton.COD_REGIONE_MIN.toString(), 2, '0');
		String provinciaInvio = "000";
		String annoInvio = new SimpleDateFormat("yy").format(new Date());
		String modalitaInvio = "2"; // modalità web da un sistema regionale
		String moduloComunicazione;
		if (candidatura) {
			moduloComunicazione = "2"; // invio di una candidatura
		} else {
			moduloComunicazione = "1"; // invio di una vacancy
		}
		String tipoComunicazione;
		if (chiusura) {
			tipoComunicazione = String.valueOf(Integer.parseInt(ConstantsSingleton.DeTipoComunicazioneCl.CHIUSURA));
		} else {
			tipoComunicazione = String.valueOf(Integer.parseInt(ConstantsSingleton.DeTipoComunicazioneCl.INVIO));
		}
		String sequence;
		if (candidatura) {
			sequence = StringUtils.leftPad(selectNextCodComunicazioneCandidatura().toString(), 15, '0');
		} else {
			sequence = StringUtils.leftPad(selectNextCodComunicazioneVacancy().toString(), 15, '0');
		}

		return regioneInvio + provinciaInvio + annoInvio + modalitaInvio + moduloComunicazione + tipoComunicazione
				+ sequence;
	}

	/**
	 * Esegue la fetch del successivo codice comunicazione dalla sequence associata alle candidature
	 * 
	 * @return
	 */
	private Long selectNextCodComunicazioneCandidatura() {
		String query = "select nextval('myportal.cv_candidatura_cl_cod_comunicazione')";
		Query nq = entityManager.createNativeQuery(query);
		BigInteger codComunicazione = (BigInteger) nq.getSingleResult();
		return codComunicazione.longValue();
	}

	/**
	 * Esegue la fetch del successivo codice comunicazione dalla sequence associata alle vacancies
	 * 
	 * @return
	 */
	private BigInteger selectNextCodComunicazioneVacancy() {
		String query = "select nextval('myportal.va_vacancy_cl_cod_comunicazione')";
		Query nq = entityManager.createNativeQuery(query);
		BigInteger codComunicazione = (BigInteger) nq.getSingleResult();
		return codComunicazione;
	}

	public void inviaCvCliLavoro() {
		log.info("===FORZATURA BATCH CREAZIONE E INVIO COMUNICAZIONI CANDIDATURA CLICLAVORO START===");
		List<CvCandidaturaCl> elencoCandidature = getCandidatureDaInviare();
		for (CvCandidaturaCl cvCandidaturaCl : elencoCandidature) {
			// T1 - crea la comunicazione
			ClInvioComunicazione daInviare = creaComunicazione(cvCandidaturaCl);
			try {
				// T2 - invia la candidatura
				inviaCandidatura(cvCandidaturaCl, daInviare);
			} catch (Exception e) {
				log.error("Errore durante l'invio della candidatura: " + e.getMessage());
			}
		}
		log.info("===FORZATURA BATCH CREAZIONE E INVIO COMUNICAZIONI CANDIDATURA CLICLAVORO TERMINATO===");
	}

	/**
	 * Restituisce l'elenco di tutte le candidature da inviare ovvero che sono in uno di questi stati:
	 * <ul>
	 * <li>PA</li>
	 * <li>MA</li>
	 * <li>VA</li>
	 * <li>CA</li>
	 * </ul>
	 * 
	 * @return
	 */
	private List<CvCandidaturaCl> getCandidatureDaInviare() {
		TypedQuery<CvCandidaturaCl> query = entityManager.createNamedQuery("findCandidatureDaInviare",
				CvCandidaturaCl.class);
		List<CvCandidaturaCl> list = query.getResultList();
		return list;
	}

	public void riceviCvCliLavoro() {
		log.info("===FORZATURA BATCH CREAZIONE CANDIDATURA MYPORTAL START===");
		List<ClInvioComunicazione> elencoCandidature = getComunicazioniDaInserire(AzioneServizio.INVIO_CANDIDATURA);
		for (ClInvioComunicazione clInvioCom : elencoCandidature) {
			try {
				// recupero oggetto candidatura per inserimento record CV
				// sul portale
				Candidatura candidatura = convertToCandidatura(clInvioCom.getFileComunicazione());
				// inserisco il cv
				riceviCandidaturaCliclavoro(candidatura, clInvioCom.getMittente());

				// aggiorno il flginviato a INVIATO CORRETTAMENTE
				clInvioCom.setFlagInviato(true);
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("MI"));
				clInvioComunicazioneHome.merge(clInvioCom);

			} catch (MyPortalException e) {
				if (ConstantsSingleton.MyPortalExceptionErrorCode.COMUNICAZIONE_PREC_NON_TROVATA
						.equals(e.getCodErrore())) {
					log.info(
							"Ricezione candidatura cliclavoro, comunicazione precedente non trovata. Cod comunicazione= "
									+ clInvioCom.getCodComunicazione());

				} else {
					log.error("Errore durante la creazione della candidatura: " + e.getMessage());
					clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
					clInvioCom.setDescrizioneErrore(
							"Errore durante la creazione della candidatura: " + e.getStrMessaggio());
					clInvioComunicazioneHome.merge(clInvioCom);
				}
			} catch (Exception e) {
				log.error("Errore durante la creazione della candidatura: " + e.getMessage());
				clInvioCom.setDeStatoInvioCl(deStatoInvioClHome.findById("PE"));
				clInvioCom.setDescrizioneErrore("Errore durante la creazione della candidatura: " + e.getMessage());
				clInvioComunicazioneHome.merge(clInvioCom);
			}
		}
		log.info("===FORZATURA BATCH CREAZIONE CANDIDATURA MYPORTAL TERMINATO===");
	}

	/**
	 * Restituisce l'elenco di tutte le candidature da inserire sul portale di provenienza da CLICLAVORO e SIL
	 * 
	 * 
	 * @return
	 */
	private List<ClInvioComunicazione> getComunicazioniDaInserire(AzioneServizio servizio) {
		TypedQuery<ClInvioComunicazione> query = entityManager
				.createNamedQuery("findComunicazioniDaInserire", ClInvioComunicazione.class)
				.setParameter("azServ", servizio);
		List<ClInvioComunicazione> list = query.getResultList();
		return list;
	}

	private Integer getIdPfPrincipalPalese(Candidatura candidatura) {

		// Estraggo gli oggetti che compongono la candidatura
		Lavoratore lavoratore = candidatura.getLavoratore();
		DatiSistema datiSistema = candidatura.getDatiSistema();
		DatiCurriculari datiCurriculari = candidatura.getDatiCurriculari();

		DatiAnagrafici datiAnagrafici = lavoratore.getDatiAnagrafici();
		Domicilio domicilio = lavoratore.getDomicilio();

		// Devo controllare se esiste l'utente collegato a questo
		// curriculum.
		// Per farlo devo confrontare l'email con quelle contenute in
		// myportal
		Recapiti recapiti = lavoratore.getRecapiti();

		PfPrincipal utenzaPalese = null;
		Integer idUtenzaPalese = null;

		String emailLavoratore = recapiti.getEmail().toUpperCase();

		List<PfPrincipal> listaPrincipal = pfPrincipalHome.findByEmail(emailLavoratore);
		if (listaPrincipal == null || listaPrincipal.isEmpty()) {
			// XXX verifico se un utente inserito da ClicLavoro con EMAIL =
			// "cl_MAILUTENTE"
			String emailLavCl = "cl_" + emailLavoratore;
			listaPrincipal = pfPrincipalHome.findByEmail(emailLavCl.toUpperCase());
			if (listaPrincipal == null || listaPrincipal.isEmpty()) {
				utenzaPalese = null;
			}
		} else {
			// aggancio l'utente (se esiste e unico)
			if (listaPrincipal.size() == 1) {
				utenzaPalese = listaPrincipal.get(0);

				// si verifica se la mail associata ad un utente cittadino
				// se risulta diverso non si aggancia a nessun utente palese
				if (utenzaPalese.isUtente()) {
					idUtenzaPalese = utenzaPalese.getIdPfPrincipal();
				} else {
					utenzaPalese = null;
				}
			} else if (listaPrincipal.size() > 1) {
				if (UtilsBean.isStaticRER()) {
					return -1; // da restituire errore SIL
				}
			}
		}

		return idUtenzaPalese;

	}
}
