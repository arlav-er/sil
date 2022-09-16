package it.eng.myportal.entity.home;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeStatoAdesioneDTO;
import it.eng.myportal.dtos.DeStatoAdesioneMinDTO;
import it.eng.myportal.dtos.DeTitoloSoggiornoDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.dtos.YgRicercaAdesioneDTO;
import it.eng.myportal.dtos.YgRisultatoRicercaAdesioneDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.Provincia_;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.UtenteInfo_;
import it.eng.myportal.entity.YgAdesione;
import it.eng.myportal.entity.YgAdesione_;
import it.eng.myportal.entity.YgImpostazioni;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeCpi_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.DeRegione_;
import it.eng.myportal.entity.decodifiche.DeStatoAdesione;
import it.eng.myportal.entity.decodifiche.DeStatoAdesione_;
import it.eng.myportal.entity.decodifiche.min.DeStatoAdesioneMin;
import it.eng.myportal.entity.decodifiche.min.DeStatoAdesioneMin_;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeAdesioneEjb;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeSapEjb;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoPermessoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloSoggiornoHome;
import it.eng.myportal.enums.YgAssegnazioneProvincia;
import it.eng.myportal.enums.YgRegioneRifNotifica;
import it.eng.myportal.enums.YgStatoPresaCarico;
import it.eng.myportal.enums.YgTipoAdesione;
import it.eng.myportal.enums.YgTipoRicercaAdesione;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.output.Risposta.DatiAppuntamento;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.utils.yg.YgConstants;
import it.eng.myportal.utils.yg.YgDebugConstants;
import it.eng.myportal.youthGuarantee.bean.RisultatoInvioAdesione;
import it.eng.myportal.youthGuarantee.bean.RisultatoInvioSap;
import it.eng.myportal.youthGuarantee.checkUtenteYG.CheckUtenteYG;
import it.eng.myportal.youthGuarantee.richiestaSAP.IDSAP;
import it.eng.myportal.youthGuarantee.sap.Datianagrafici;
import it.eng.myportal.youthGuarantee.sap.LavoratoreType;
import it.eng.myportal.youthGuarantee.utenteYG.Utente;
import it.eng.myportal.youthGuarantee.utenteYG.UtenteygType;

@Stateless
public class YgAdesioneHome extends AbstractUpdatableHome<YgAdesione, YgAdesioneDTO> {

	protected final Log log = LogFactory.getLog(YouthGuaranteeSapEjb.class);

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private ProvinciaHome provinciaHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private YouthGuaranteeAdesioneEjb youthGuaranteeAdesioneEjb;

	@EJB
	private YouthGuaranteeSapEjb youthGuaranteeSapEjb;

	@EJB
	private DeRegioneHome deRegioneHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private DeComuneHome deComuneHome;

	@EJB
	private ClicLavoroEjb clicLavoroEjb;

	@EJB
	private DeTitoloSoggiornoHome deTitoloSoggiornoHome;

	@EJB
	private DeMotivoPermessoHome deMotivoPermessoHome; 
	
	@EJB
	private TsGetOpzioniEJB tsGetOpzioniEJB;

	@EJB
	private DeStatoAdesioneHome deStatoAdesioneHome;

	@EJB
	private YgImpostazioniHome ygImpostazioniHome;

	@EJB
	private YgSapHome ygSapHome;

	@EJB
	private DeStatoAdesioneMinHome deStatoAdesioneMinHome;

	@EJB
	private YgAdesioneStoriaHome ygAdesioneStoriaHome;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	public YgAdesione findLatestAttivaByIdPfPrincipal(Integer idPfPrincipal) {

		YgAdesione ygAdesione = null;
		List<YgAdesione> adesioni = entityManager
				.createNamedQuery("findYgAdesioneAttivaByIfPfPrincipal", YgAdesione.class)
				.setParameter("id_pf_principal", idPfPrincipal).getResultList();
		// la get(0) torna la più recente, order by nella query
		if (adesioni != null && !adesioni.isEmpty()) {
			ygAdesione = adesioni.get(0);
		}
		return ygAdesione;

	}

	public YgAdesioneDTO findLatestDTOAttivaByIdPfPrincipal(Integer idPfPrincipal) {
		YgAdesione adesione = findLatestAttivaByIdPfPrincipal(idPfPrincipal);
		YgAdesioneDTO ygAdesioneDTO = null;
		// la get(0) torna la più recente, order by nella query
		if (adesione != null) {
			ygAdesioneDTO = toDTO(adesione);
		}
		return ygAdesioneDTO;

	}

	public YgAdesione findLatestByIdPfPrincipal(Integer idPfPrincipal) {

		YgAdesione ygAdesione = null;
		List<YgAdesione> adesioni = entityManager.createNamedQuery("findYgAdesioneByIfPfPrincipal", YgAdesione.class)
				.setParameter("id_pf_principal", idPfPrincipal).getResultList();
		// la get(0) torna la più recente, order by nella query
		if (adesioni != null && !adesioni.isEmpty()) {
			ygAdesione = adesioni.get(0);
		}
		return ygAdesione;

	}

	public YgAdesioneDTO findLatestDTOByIdPfPrincipal(Integer idPfPrincipal) {
		YgAdesione adesione = findLatestByIdPfPrincipal(idPfPrincipal);
		YgAdesioneDTO ygAdesioneDTO = null;
		// la get(0) torna la più recente, order by nella query
		if (adesione != null) {
			ygAdesioneDTO = toDTO(adesione);
		}
		return ygAdesioneDTO;

	}

	public YgAdesione fromDTO(YgAdesioneDTO ygAdesioneDTO) {

		YgAdesione ygAdesione = new YgAdesione();

		ygAdesione.setFlgSap(ygAdesioneDTO.getFlgSap());
		ygAdesione.setStrMessWsInvioSap(ygAdesioneDTO.getStrMessWsInvioSap());
		ygAdesione.setStrMessWsAdesione(ygAdesioneDTO.getStrMessWsAdesione());
		ygAdesione.setStrMessWsNotifica(ygAdesioneDTO.getStrMessWsNotifica());
		ygAdesione.setFlgAdesione(ygAdesioneDTO.getFlgAdesione());
		ygAdesione.setCodMonoProv(ygAdesioneDTO.getCodMonoProv());
		if (ygAdesioneDTO.getPfPrincipal() != null && ygAdesioneDTO.getPfPrincipal().getId() != null) {
			ygAdesione.setPfPrincipal(pfPrincipalHome.findById(ygAdesioneDTO.getPfPrincipal().getId()));
		}
		ygAdesione.setIdentificativoSap(ygAdesioneDTO.getIdentificativoSap());
		ygAdesione.setIdentificativoSapOld(ygAdesioneDTO.getIdentificativoSapOld());
		ygAdesione.setDtAdesione(ygAdesioneDTO.getDtAdesione());
		ygAdesione.setDtStatoAdesioneMin(ygAdesioneDTO.getDtStatoAdesioneMin());
		ygAdesione.setIdYgAdesione(ygAdesioneDTO.getId());
		ygAdesione.setCodiceFiscale(ygAdesioneDTO.getCodiceFiscale());
		ygAdesione.setDtmIns(ygAdesioneDTO.getDtmIns());
		ygAdesione.setDtmMod(ygAdesioneDTO.getDtmMod());

		if (ygAdesioneDTO.getIdPrincipalIns() != null) {
			ygAdesione.setPfPrincipalIns(pfPrincipalHome.findById(ygAdesioneDTO.getIdPrincipalIns()));
		}

		if (ygAdesioneDTO.getIdPrincipalMod() != null) {
			ygAdesione.setPfPrincipalMod(pfPrincipalHome.findById(ygAdesioneDTO.getIdPrincipalMod()));
		}

		ygAdesione.setFlgPresoInCarico(ygAdesioneDTO.getFlgPresoInCarico());
		ygAdesione.setDtPresaInCarico(ygAdesioneDTO.getDtPresaInCarico());
		if (ygAdesioneDTO.getPfPrincipalPic() != null && ygAdesioneDTO.getPfPrincipalPic().getId() != null) {
			ygAdesione.setPfPrincipalPic(pfPrincipalHome.findById(ygAdesioneDTO.getPfPrincipalPic().getId()));
		}

		if (ygAdesioneDTO.getDeProvinciaNotifica() != null && ygAdesioneDTO.getDeProvinciaNotifica().getId() != null) {
			ygAdesione.setDeProvincia(deProvinciaHome.findById(ygAdesioneDTO.getDeProvinciaNotifica().getId()));
		}

		if (ygAdesioneDTO.getDeRegioneRifNotifica() != null && ygAdesioneDTO.getDeRegioneRifNotifica().getId() != null) {
			ygAdesione.setDeRegione(deRegioneHome.findById(ygAdesioneDTO.getDeRegioneRifNotifica().getId()));
		}

		if (ygAdesioneDTO.getDeCpiAdesione() != null && ygAdesioneDTO.getDeCpiAdesione().getId() != null) {
			ygAdesione.setDeCpiAdesione(deCpiHome.findById(ygAdesioneDTO.getDeCpiAdesione().getId()));
		}

		ygAdesione.setFlgRecuperoProv(ygAdesioneDTO.getFlgRecuperoProv());

		if (ygAdesioneDTO.getDeCpiAssegnazione() != null && ygAdesioneDTO.getDeCpiAssegnazione().getId() != null) {
			ygAdesione.setDeCpiAssegnazione(deCpiHome.findById(ygAdesioneDTO.getDeCpiAssegnazione().getId()));
		}
		ygAdesione.setStrMessAccount(ygAdesioneDTO.getStrMessAccount());
		ygAdesione.setCodMonoRecuperoCpi(ygAdesioneDTO.getCodMonoRecuperoCpi());

		ygAdesione.setEmailRifNotifica(ygAdesioneDTO.getEmailRifNotifica());
		ygAdesione.setNomeRifNotifica(ygAdesioneDTO.getNomeRifNotifica());
		ygAdesione.setCognomeRifNotifica(ygAdesioneDTO.getCognomeRifNotifica());
		ygAdesione.setFlgCreatoAccount(ygAdesioneDTO.getFlgCreatoAccount());

		ygAdesione.setDtFineStatoAdesione(ygAdesioneDTO.getDtFineStatoAdesione());
		ygAdesione.setNote(ygAdesioneDTO.getNote());
		if (ygAdesioneDTO.getDeStatoAdesione() != null && ygAdesioneDTO.getDeStatoAdesione().getId() != null) {
			ygAdesione.setDeStatoAdesione(deStatoAdesioneHome.findById(ygAdesioneDTO.getDeStatoAdesione().getId()));
		}

		if (ygAdesioneDTO.getDeComuneResidenzaRifNotifica() != null
				&& ygAdesioneDTO.getDeComuneResidenzaRifNotifica().getId() != null) {
			ygAdesione.setDeComuneResidenzaRifNotifica(deComuneHome.findById(ygAdesioneDTO
					.getDeComuneResidenzaRifNotifica().getId()));
		}

		if (ygAdesioneDTO.getDeComuneDomicilioRifNotifica() != null
				&& ygAdesioneDTO.getDeComuneDomicilioRifNotifica().getId() != null) {
			ygAdesione.setDeComuneDomicilioRifNotifica(deComuneHome.findById(ygAdesioneDTO
					.getDeComuneDomicilioRifNotifica().getId()));
		}

		if (ygAdesioneDTO.getDeStatoAdesioneMin() != null && ygAdesioneDTO.getDeStatoAdesioneMin().getId() != null) {
			ygAdesione.setDeStatoAdesioneMin(deStatoAdesioneMinHome.findById(ygAdesioneDTO.getDeStatoAdesioneMin()
					.getId()));
		}

		ygAdesione.setDtStatoAdesioneMin(ygAdesioneDTO.getDtStatoAdesioneMin());

		return ygAdesione;

	}

	public YgAdesioneDTO toDTO(YgAdesione ygAdesione) {
		if (ygAdesione == null) {
			return null;
		}
		YgAdesioneDTO ygAdesioneDTO = super.toDTO(ygAdesione);

		ygAdesioneDTO.setFlgSap(ygAdesione.getFlgSap());
		ygAdesioneDTO.setStrMessWsInvioSap(ygAdesione.getStrMessWsInvioSap());
		ygAdesioneDTO.setStrMessWsAdesione(ygAdesione.getStrMessWsAdesione());
		ygAdesioneDTO.setStrMessWsNotifica(ygAdesione.getStrMessWsNotifica());
		ygAdesioneDTO.setFlgAdesione(ygAdesione.getFlgAdesione());
		ygAdesioneDTO.setCodMonoProv(ygAdesione.getCodMonoProv());
		if (ygAdesione.getPfPrincipal() != null && ygAdesione.getPfPrincipal().getIdPfPrincipal() != null) {
			ygAdesioneDTO.setPfPrincipal(pfPrincipalHome.findDTOById(ygAdesione.getPfPrincipal().getIdPfPrincipal()));
		}
		ygAdesioneDTO.setIdentificativoSap(ygAdesione.getIdentificativoSap());
		ygAdesioneDTO.setIdentificativoSapOld(ygAdesione.getIdentificativoSapOld());
		ygAdesioneDTO.setDtAdesione(ygAdesione.getDtAdesione());
		ygAdesioneDTO.setDtStatoAdesioneMin(ygAdesione.getDtStatoAdesioneMin());
		ygAdesioneDTO.setId(ygAdesione.getIdYgAdesione());
		ygAdesioneDTO.setCodiceFiscale(ygAdesione.getCodiceFiscale());

		ygAdesioneDTO.setFlgPresoInCarico(ygAdesione.getFlgPresoInCarico());
		ygAdesioneDTO.setDtPresaInCarico(ygAdesione.getDtPresaInCarico());
		if (ygAdesione.getPfPrincipalPic() != null && ygAdesione.getPfPrincipalPic().getIdPfPrincipal() != null) {
			ygAdesioneDTO.setPfPrincipalPic(pfPrincipalHome.findDTOById(ygAdesione.getPfPrincipalPic()
					.getIdPfPrincipal()));
		}

		if (ygAdesione.getDeProvincia() != null && ygAdesione.getDeProvincia().getCodProvincia() != null) {
			ygAdesioneDTO.setDeProvinciaNotifica(deProvinciaHome.findDTOById(ygAdesione.getDeProvincia()
					.getCodProvincia()));
		}

		if (ygAdesione.getDeRegione() != null && ygAdesione.getDeRegione().getCodRegione() != null) {
			ygAdesioneDTO.setDeRegioneRifNotifica(deRegioneHome.findDTOById(ygAdesione.getDeRegione().getCodRegione()));
		}

		if (ygAdesione.getDeCpiAdesione() != null && ygAdesione.getDeCpiAdesione().getCodCpi() != null) {
			ygAdesioneDTO.setDeCpiAdesione(deCpiHome.findDTOById(ygAdesione.getDeCpiAdesione().getCodCpi()));
		}

		ygAdesioneDTO.setFlgRecuperoProv(ygAdesione.getFlgRecuperoProv());

		if (ygAdesione.getDeCpiAssegnazione() != null && ygAdesione.getDeCpiAssegnazione().getCodCpi() != null) {
			ygAdesioneDTO.setDeCpiAssegnazione(deCpiHome.findDTOById(ygAdesione.getDeCpiAssegnazione().getCodCpi()));
		}
		ygAdesioneDTO.setStrMessAccount(ygAdesione.getStrMessAccount());
		ygAdesioneDTO.setCodMonoRecuperoCpi(ygAdesione.getCodMonoRecuperoCpi());

		ygAdesioneDTO.setEmailRifNotifica(ygAdesione.getEmailRifNotifica());
		ygAdesioneDTO.setNomeRifNotifica(ygAdesione.getNomeRifNotifica());
		ygAdesioneDTO.setCognomeRifNotifica(ygAdesione.getCognomeRifNotifica());
		ygAdesioneDTO.setFlgCreatoAccount(ygAdesione.getFlgCreatoAccount());

		ygAdesioneDTO.setDtFineStatoAdesione(ygAdesione.getDtFineStatoAdesione());
		ygAdesioneDTO.setNote(ygAdesione.getNote());
		if (ygAdesione.getDeStatoAdesione() != null && ygAdesione.getDeStatoAdesione().getCodStatoAdesione() != null) {
			ygAdesioneDTO.setDeStatoAdesione(deStatoAdesioneHome.findDTOById(ygAdesione.getDeStatoAdesione()
					.getCodStatoAdesione()));
		}

		if (ygAdesione.getDeComuneResidenzaRifNotifica() != null
				&& ygAdesione.getDeComuneResidenzaRifNotifica().getCodCom() != null) {
			ygAdesioneDTO.setDeComuneResidenzaRifNotifica(deComuneHome.findDTOById(ygAdesione
					.getDeComuneResidenzaRifNotifica().getCodCom()));
		}

		if (ygAdesione.getDeComuneDomicilioRifNotifica() != null
				&& ygAdesione.getDeComuneDomicilioRifNotifica().getCodCom() != null) {
			ygAdesioneDTO.setDeComuneDomicilioRifNotifica(deComuneHome.findDTOById(ygAdesione
					.getDeComuneDomicilioRifNotifica().getCodCom()));
		}

		if (ygAdesione.getDeStatoAdesioneMin() != null
				&& ygAdesione.getDeStatoAdesioneMin().getCodStatoAdesioneMin() != null) {
			ygAdesioneDTO.setDeStatoAdesioneMin(deStatoAdesioneMinHome.findDTOById(ygAdesione.getDeStatoAdesioneMin()
					.getCodStatoAdesioneMin()));
		}

		ygAdesioneDTO.setDtStatoAdesioneMin(ygAdesione.getDtStatoAdesioneMin());

		return ygAdesioneDTO;

	}

	@Override
	public YgAdesione findById(Integer id) {
		return findById(YgAdesione.class, id);
	}

	public boolean esisteAdesioneSuDb(Integer idPfPrincipalUtente) {
		boolean esiste = false;

		YgAdesioneDTO oldYgAdesioneDTO = findLatestDTOAttivaByIdPfPrincipal(idPfPrincipalUtente);

		if (oldYgAdesioneDTO != null && oldYgAdesioneDTO.getDtAdesione() != null
				&& oldYgAdesioneDTO.getFlgAdesione() != null && oldYgAdesioneDTO.getFlgAdesione()) {
			esiste = true;
		}

		return esiste;
	}

	public boolean isAdesioneSuDbAttiva(Integer idPfPrincipalUtente) {

		YgAdesioneDTO oldYgAdesioneDTO = findLatestDTOAttivaByIdPfPrincipal(idPfPrincipalUtente);

		if (oldYgAdesioneDTO != null && oldYgAdesioneDTO.getDtAdesione() != null
				&& oldYgAdesioneDTO.getFlgAdesione() != null && oldYgAdesioneDTO.getFlgAdesione()) {
			if (oldYgAdesioneDTO.getDeStatoAdesioneMin() != null) {
				return ("A".equals(oldYgAdesioneDTO.getDeStatoAdesioneMin().getCodMonoAttiva()));
			}
		}

		return false;

	}

	private String getInputXmlForCheckUtenteYg(UtenteCompletoDTO utente, Date dataAdesione, Boolean flgInvioCodRegione, String codRegioneMin) throws JAXBException,
			SAXException, DatatypeConfigurationException, ParseException {

		String inputXml = null;
		CheckUtenteYG checkUtente = new CheckUtenteYG();
		checkUtente.setCodiceFiscale(utente.getCodiceFiscale());
		if (flgInvioCodRegione!=null && flgInvioCodRegione) checkUtente.setCodiceRegione(codRegioneMin);
		inputXml = youthGuaranteeAdesioneEjb.convertCheckUtenteYGToString(checkUtente);

		return inputXml;

	}

	private String getInputXmlForInvioUtenteYg(UtenteCompletoDTO utente, String identificativoSap, Date dataAdesione) {
		String inputXml = null;
		try {
			UtenteygType utenteYgType = new UtenteygType();
			Utente utenteYg = new Utente();
			utenteYg.setCodiceFiscale(utente.getCodiceFiscale());
			utenteYg.setDataadesione(Utils.dateToGregorianDate(dataAdesione));
			if (identificativoSap != null && !"".equalsIgnoreCase(identificativoSap)) {
				utenteYg.setIdentificativoSap(identificativoSap);
			}
			
			// Recupero del codice regione ministeriale partendo dal codice regione (il codice ministeriale presente nelle costanti è un intero)
			DeRegione deRegione = deRegioneHome.findById(String.valueOf(ConstantsSingleton.COD_REGIONE));
			utenteYg.setRegione(deRegione.getCodMin());
			
			utenteYgType.setUtente(utenteYg);
			inputXml = youthGuaranteeAdesioneEjb.convertToString(utenteYgType);

			// Validazione XSD
			Utils.validateXml(inputXml, "yg" + File.separator + "Rev004_UTENTI-YG.xsd");
		} catch (SAXException e) {
			log.error("Errore SAXException validazione: " + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_VALIDAZIONE_XSD, "Errore nella validazione dei dati");
		} catch (IOException e) {
			log.error("Errore IOException validazione: " + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_VALIDAZIONE_XSD, e.getMessage());
		} catch (Exception e) {
			log.error("Errore Generico invioUtenteYG: " + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_GENERICO, YgConstants.DESCRIZIONE_ERRORE_GENERICO);
		}

		return inputXml;

	}

	public boolean checkUtenteYg(Date currentDate, UtenteCompletoDTO utente) throws MyPortalException {

		boolean esisteAdesioneSuYg = false;

		// Lettura impostazioni regione
		YgImpostazioni ygImpostazioni = ygImpostazioniHome.findByCodRegione(ConstantsSingleton.COD_REGIONE.toString());		
		DeRegione deRegione = deRegioneHome.findById(ygImpostazioni.getDeRegione().getCodRegione());
		
		String codiceFiscale = utente.getCodiceFiscale();

		// verifica presenza codice fiscale senza il quale non si può procedere

		if (codiceFiscale == null || "".equalsIgnoreCase(codiceFiscale)) {
			return false;
		}

		// verifica esistenza adesione precedente
		// Integer idPfPrincipalUtente =
		// utente.getUtenteDTO().getPfPrincipalDTO().getId();
		// chiamata al ws
		String inputXml = null;

		try {
			inputXml = getInputXmlForCheckUtenteYg(utente, currentDate, ygImpostazioni.getFlgInvioCodRegione(), deRegione.getCodMin());
			if (YgDebugConstants.IS_DEBUG) {
				esisteAdesioneSuYg = YgDebugConstants.DEBUG_ADESIONE_ESITO_CHECK;
				if (YgDebugConstants.DEBUG_ECCEZIONE_SERVIZIO_NON_DISPONIBILE_CHECK_ADESIONE) {
					// throw new
					// MyPortalException(YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE,
					// YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
					// su indicazione di Stefania il 07/04/2014
					// si fa in modo che se vi è un errore di servizio non
					// disponibile durante la chiamata al WS
					// si ipotizza che l'adesione non sia stata inviata
					return false;
				}
				if (YgDebugConstants.DEBUG_ECCEZIONE_ERRORE_GENERICO_CHECK_ADESIONE) {
					throw new MyPortalException(YgConstants.COD_ERRORE_GENERICO,
							YgConstants.DESCRIZIONE_ERRORE_GENERICO);
				}
			} else {
				esisteAdesioneSuYg = youthGuaranteeAdesioneEjb.checkUtente(inputXml, ygImpostazioni.getCodCheckYgStop());
				
				if (esisteAdesioneSuYg) {
					// Se presente si utilizza il messaggio specializzato
					String strMessage = ygImpostazioni.getStrMessErrCheckYg();
					if (strMessage == null) strMessage = YgConstants.DESCRIZIONE_ERRORE_ADESIONE_PRESENTE;
					
					throw new MyPortalException(YgConstants.COD_ERRORE_ADESIONE_NON_POSSIBILE, strMessage); /*YgConstants.DESCRIZIONE_ERRORE_ADESIONE_PRESENTE*/
				}
			}
		} catch (RemoteException e) {
			log.error("Errore Servizio non disponibile per checkUtente" + e.getMessage());
			// throw new
			// MyPortalException(YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE,
			// YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
			// su indicazione di Stefania il 07/04/2014
			// si fa in modo che se vi è un errore di servizio non
			// disponibile durante la chiamata al WS
			// si ipotizza che l'adesione non sia stata inviata
			return false;
		} catch (MyPortalException e) {
			// utilizzato per il debug
			throw new MyPortalException(e.getCodErrore(), e.getStrMessaggio());
		} catch (Exception e) {
			log.error("Errore Generico checkUtente: " + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_GENERICO, YgConstants.DESCRIZIONE_ERRORE_GENERICO);
		}

		return esisteAdesioneSuYg;

	}

	public boolean esisteIdentificativoSap(UtenteCompletoDTO utente) {

		boolean esisteIdentificativoSap = false;

		Integer idPfPrincipalUtente = utente.getUtenteDTO().getPfPrincipalDTO().getId();
		YgAdesioneDTO oldYgAdesioneDTO = findLatestDTOAttivaByIdPfPrincipal(idPfPrincipalUtente);

		if (oldYgAdesioneDTO != null && oldYgAdesioneDTO.getIdentificativoSap() != null
				&& !"".equalsIgnoreCase(oldYgAdesioneDTO.getIdentificativoSap())
				&& !"0".equalsIgnoreCase(oldYgAdesioneDTO.getIdentificativoSap())) {

			esisteIdentificativoSap = true;

		}

		return esisteIdentificativoSap;

	}

	public void salvaInformazioniUtente(UtenteCompletoDTO utente) {

		// salvataggio dei nuovi dati immessi dall'utente su db;
		// essendo in nuova transazione va a buon fine anche se la
		// transazione chiamante fallisce

		utenteInfoHome.mergeDTO(utente.getUtenteInfo(), utente.getUtenteDTO().getPfPrincipalDTO().getId());

	}

	private RisultatoInvioSap getIdentificativoSAP(UtenteCompletoDTO utente) {
		String inputXml = null;
		String identificativoSap = "";

		try {
			inputXml = ygSapHome.getInputXmlForVerificaEsistenzaSap(utente);
		} catch (Exception e1) {
			log.error("Errore SAXException validazione: " + e1.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_VALIDAZIONE_XSD, "Errore nella validazione dei dati");
		}

		if (YgDebugConstants.IS_DEBUG) {
			identificativoSap = YgDebugConstants.DEBUG_IDENTIFICATIVO_SAP_VERIFICA;
		} else {
			try {
				identificativoSap = youthGuaranteeSapEjb.verificaEsistenzaSap(inputXml);
			} catch (RemoteException e) {
				log.error("Errore RemoteException invioUtenteYG: " + e.getMessage());
				throw new MyPortalException(YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE,
						YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
			}
		}

		RisultatoInvioSap risultatoInvioSap = new RisultatoInvioSap();
		if ("0".equals(identificativoSap)) {
			risultatoInvioSap.setSuccess(false);
		} else {
			risultatoInvioSap.setSuccess(true);
		}
		risultatoInvioSap.setCodiceSAP(identificativoSap);
		return risultatoInvioSap;
	}

	private RisultatoInvioSap inviaSAP(Date dataAdesione, UtenteCompletoDTO utente, DeCpiDTO deCpiAdesione) {
		String inputXML = null;
		try {
			inputXML = ygSapHome.getInputXmlForInvioSap(utente, dataAdesione, deCpiAdesione);
		} catch (Exception e1) {
			log.error("Errore getInputXmlForInvioSap invioUtenteYG" + e1.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_VALIDAZIONE_XSD, YgConstants.DESCRIZIONE_ERRORE_GENERICO);
		}

		RisultatoInvioSap risultatoInvioSap;
		try {
			if (YgDebugConstants.IS_DEBUG) {
				risultatoInvioSap = new RisultatoInvioSap();
				risultatoInvioSap.setSuccess(true);
				risultatoInvioSap.setMessaggioErrore(YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
				risultatoInvioSap.setCodiceSAP("AA12345678A");
			} else
				risultatoInvioSap = youthGuaranteeSapEjb.inviaSap(inputXML);
		} catch (RemoteException e) {
			log.error("Errore RemoteException invioUtenteYG:" + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE,
					YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
		}

		return risultatoInvioSap;
	}

	private RisultatoInvioAdesione inviaAdesioneYG(String identificativoSAP, Date dataAdesione, UtenteCompletoDTO utente) {
		String inputXML = null;
		RisultatoInvioAdesione risultatoInvioAdesione = new RisultatoInvioAdesione();
		try {
			inputXML = getInputXmlForInvioUtenteYg(utente, identificativoSAP, dataAdesione);

			if (YgDebugConstants.IS_DEBUG) {
				risultatoInvioAdesione.setSuccess(YgDebugConstants.DEBUG_ADESIONE_ESITO_INVIO);
				if (YgDebugConstants.DEBUG_ECCEZIONE_SERVIZIO_NON_DISPONIBILE_INVIO_ADESIONE) {
					throw new MyPortalException(YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE,
							YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
				}
				if (YgDebugConstants.DEBUG_ECCEZIONE_ERRORE_GENERICO_INVIO_ADESIONE) {
					throw new MyPortalException(YgConstants.COD_ERRORE_GENERICO,
							YgConstants.DESCRIZIONE_ERRORE_GENERICO);
				}
			} else {
				risultatoInvioAdesione = youthGuaranteeAdesioneEjb.inviaAdesione(inputXML);
			}
			return risultatoInvioAdesione;
		} catch (RemoteException e) {
			log.error("Errore RemoteException invioUtenteYG: " + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE,
					YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
		} catch (MyPortalException e) {
			// utilizzato per il debug
			throw new MyPortalException(e.getCodErrore(), e.getStrMessaggio());
		}
	}

	private void persistYgAdesione(Date dataAdesione, UtenteCompletoDTO utente, DeCpiDTO deCpiAdesione,
			RisultatoInvioSap risultatoInvioSAP, RisultatoInvioAdesione risultatoInvioAdesione) {
		boolean flgAdesione = false;
		Date dtAdesione = null;
		DeCpiDTO deCpiAdesioneDTO = deCpiAdesione;
		DeCpiDTO deCpiAssegnazioneDTO = null;

		if (risultatoInvioAdesione.isSuccess()) {
			flgAdesione = true;
			dtAdesione = dataAdesione;
		}

		if (deCpiAdesione == null || deCpiAdesione.getId() == null || "".equalsIgnoreCase(deCpiAdesione.getId())) {
			deCpiAdesioneDTO = null;
		}

		if (utente.getComuneDomicilio() != null && utente.getComuneDomicilio().getId() != null
				&& utente.getComuneDomicilio().getIdProvincia() != null) {

			boolean comuneInRegione = false;
			DeProvinciaDTO deProvinciaDTO = deProvinciaHome.findDTOById(utente.getComuneDomicilio().getIdProvincia());
			if (deProvinciaDTO != null && deProvinciaDTO.getIdRegione() != null) {
				if (ConstantsSingleton.COD_REGIONE.toString().equalsIgnoreCase(deProvinciaDTO.getIdRegione())) {
					comuneInRegione = true;
					DeComune deComune = deComuneHome.findById(utente.getComuneDomicilio().getId());
					DeCpiDTO deCpiDTO = deCpiHome.toDTO(deComune.getDeCpi());
					deCpiAssegnazioneDTO = deCpiDTO;
				}
			}
			if (!comuneInRegione) {
				deCpiAssegnazioneDTO = deCpiAdesioneDTO;
			}
		}

		// salvataggio delle informazioni sull'adesione
		YgAdesioneDTO newYgAdesioneDTO = new YgAdesioneDTO();

		newYgAdesioneDTO.setCodiceFiscale(utente.getCodiceFiscale());
		newYgAdesioneDTO.setDtAdesione(dtAdesione);
		newYgAdesioneDTO.setFlgAdesione(flgAdesione);
		newYgAdesioneDTO.setStrMessWsAdesione(risultatoInvioAdesione.getMessaggioErrore());
		newYgAdesioneDTO.setPfPrincipal(utente.getUtenteDTO().getPfPrincipalDTO());
		newYgAdesioneDTO.setCodMonoProv("I");
		newYgAdesioneDTO.setIdentificativoSap(risultatoInvioSAP.getCodiceSAP());
		newYgAdesioneDTO.setFlgSap(risultatoInvioSAP.isSuccess());
		newYgAdesioneDTO.setStrMessWsInvioSap(risultatoInvioSAP.getMessaggioErrore());
		newYgAdesioneDTO.setDtmIns(new Date());
		newYgAdesioneDTO.setDtmMod(new Date());
		newYgAdesioneDTO.setIdPrincipalIns(utente.getId());
		newYgAdesioneDTO.setIdPrincipalMod(utente.getId());
		newYgAdesioneDTO.setStrMessWsNotifica(null);
		newYgAdesioneDTO.setDeRegioneRifNotifica(deRegioneHome.findDTOByCodRegioneMin(StringUtils.leftPad(
				ConstantsSingleton.COD_REGIONE_MIN.toString(), 2, '0')));
		newYgAdesioneDTO.setDeCpiAdesione(deCpiAdesioneDTO);
		newYgAdesioneDTO.setDeCpiAssegnazione(deCpiAssegnazioneDTO);
		newYgAdesioneDTO.setDeComuneResidenzaRifNotifica(utente.getComuneResidenza());
		newYgAdesioneDTO.setDeComuneDomicilioRifNotifica(utente.getComuneDomicilio());

		DeStatoAdesioneMinDTO deStatoAdesioneMin = deStatoAdesioneMinHome.findDTOById("A");
		if (deStatoAdesioneMin != null && deStatoAdesioneMin.getId() != null
				&& !"".equalsIgnoreCase(deStatoAdesioneMin.getId())) {
			newYgAdesioneDTO.setDeStatoAdesioneMin(deStatoAdesioneMin);
		}
		newYgAdesioneDTO.setDtStatoAdesioneMin(dtAdesione);

		if (utente.getProvinciaRiferimento() != null && utente.getProvinciaRiferimento().getId() != null) {
			// Carmela 2014/06/25
			DeProvinciaDTO deProvinciaDTO = deProvinciaHome.findDTOById(utente.getProvinciaRiferimento().getId());
			newYgAdesioneDTO.setDeProvinciaNotifica(deProvinciaDTO);
		}

		persistDTO(newYgAdesioneDTO, utente.getId());
	}

	public boolean invioAdesioneYgSAP(Date dataAdesione, UtenteCompletoDTO utente, DeCpiDTO deCpiAdesione)
			throws MyPortalException {
		RisultatoInvioSap risultatoInvioSAP = getIdentificativoSAP(utente);
		RisultatoInvioAdesione risultatoInvioAdesione = new RisultatoInvioAdesione();

		/* SAP non ancora inviata al ministero per questo utente, invio adesso */
		if (!risultatoInvioSAP.isSuccess()) {
			risultatoInvioSAP = inviaSAP(dataAdesione, utente, deCpiAdesione);
		}

		/*
		 * Se tutto e' andato a buon fine ora ho inviato la SAP al ministero e procedo con l'adesione YG
		 */
		if (risultatoInvioSAP.isSuccess()) {
			risultatoInvioAdesione = inviaAdesioneYG(risultatoInvioSAP.getCodiceSAP(), dataAdesione, utente);
		}

		persistYgAdesione(dataAdesione, utente, deCpiAdesione, risultatoInvioSAP, risultatoInvioAdesione);

		return risultatoInvioAdesione.isSuccess();
	}

	public RisultatoInvioAdesione ripristinaAdesione(Integer idYgAdesione, String identificativoSap,
			Integer idPfPrincipalMod) throws MyPortalException {
		YgAdesione ygAdesione = findById(idYgAdesione);

		/* reinvio dell'adesione con i vecchi dati */
		RisultatoInvioAdesione risultatoInvioAdesione = inviaAdesioneYGPerRipristino(ygAdesione.getCodiceFiscale(),
				identificativoSap, ygAdesione.getDtAdesione());

		if (risultatoInvioAdesione.isSuccess()) {
			/* storicizzo lo stato dell'adesione */
			ygAdesioneStoriaHome.storicizza(ygAdesione, idPfPrincipalMod);

			/* aggiorno il recod di yg_adesione */
			PfPrincipal pfPrincipalMod = pfPrincipalHome.findById(idPfPrincipalMod);
			Date dtAdesione = Calendar.getInstance().getTime();
			DeStatoAdesioneMin deStatoAdesioneMin = deStatoAdesioneMinHome.findById("A");
			ygAdesione.setDeStatoAdesioneMin(deStatoAdesioneMin);
			ygAdesione.setDtStatoAdesioneMin(dtAdesione);
			ygAdesione.setDtmMod(dtAdesione);
			ygAdesione.setPfPrincipalMod(pfPrincipalMod);
			/* e' cambiato l'identificativo SAP */
			if (!identificativoSap.equals(ygAdesione.getIdentificativoSap())) {
				ygAdesione.setIdentificativoSapOld(ygAdesione.getIdentificativoSap());
				ygAdesione.setIdentificativoSap(identificativoSap);
			}
			merge(ygAdesione);
		}

		return risultatoInvioAdesione;
	}

	private RisultatoInvioAdesione inviaAdesioneYGPerRipristino(String codiceFiscale, String identificativoSAP,
			Date dataAdesione) {
		String inputXML = null;
		RisultatoInvioAdesione risultatoInvioAdesione = new RisultatoInvioAdesione();
		try {
			inputXML = getInputXmlForInvioUtenteYgPerRipristino(codiceFiscale, identificativoSAP, dataAdesione);

			if (YgDebugConstants.IS_DEBUG) {
				risultatoInvioAdesione.setSuccess(YgDebugConstants.DEBUG_ADESIONE_ESITO_INVIO);
				if (YgDebugConstants.DEBUG_ECCEZIONE_SERVIZIO_NON_DISPONIBILE_INVIO_ADESIONE) {
					throw new MyPortalException(YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE,
							YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
				}
				if (YgDebugConstants.DEBUG_ECCEZIONE_ERRORE_GENERICO_INVIO_ADESIONE) {
					throw new MyPortalException(YgConstants.COD_ERRORE_GENERICO,
							YgConstants.DESCRIZIONE_ERRORE_GENERICO);
				}
			} else {
				risultatoInvioAdesione = youthGuaranteeAdesioneEjb.inviaAdesione(inputXML);
			}
			return risultatoInvioAdesione;
		} catch (RemoteException e) {
			log.error("Errore RemoteException invioUtenteYG: " + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE,
					YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
		} catch (MyPortalException e) {
			// utilizzato per il debug
			throw new MyPortalException(e.getCodErrore(), e.getStrMessaggio());
		}
	}

	private String getInputXmlForInvioUtenteYgPerRipristino(String codiceFiscale, String identificativoSap,
			Date dataAdesione) {
		String inputXml = null;
		try {
			UtenteygType utenteYgType = new UtenteygType();
			Utente utenteYg = new Utente();
			utenteYg.setCodiceFiscale(codiceFiscale);
			utenteYg.setDataadesione(Utils.dateToGregorianDate(dataAdesione));
			if (identificativoSap != null && !"".equalsIgnoreCase(identificativoSap)) {
				utenteYg.setIdentificativoSap(identificativoSap);
			}

			// Recupero del codice regione ministeriale partendo dal codice regione (il codice ministeriale presente nelle costanti è un intero)
			DeRegione deRegione = deRegioneHome.findById(String.valueOf(ConstantsSingleton.COD_REGIONE));
			utenteYg.setRegione(deRegione.getCodMin());
			
			utenteYgType.setUtente(utenteYg);
			inputXml = youthGuaranteeAdesioneEjb.convertToString(utenteYgType);

			// Validazione XSD
			Utils.validateXml(inputXml, "yg" + File.separator + "Rev004_UTENTI-YG.xsd");
		} catch (SAXException e) {
			log.error("Errore SAXException validazione: " + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_VALIDAZIONE_XSD, "Errore nella validazione dei dati");
		} catch (IOException e) {
			log.error("Errore IOException validazione: " + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_VALIDAZIONE_XSD, e.getMessage());
		} catch (Exception e) {
			log.error("Errore Generico invioUtenteYG: " + e.getMessage());
			throw new MyPortalException(YgConstants.COD_ERRORE_GENERICO, YgConstants.DESCRIZIONE_ERRORE_GENERICO);
		}

		return inputXml;

	}

	public boolean getNuovoIdentificativoSAP(YgAdesioneDTO ygAdesioneDTO) {
		boolean result = false;
		try {
			String nuovoIdentificativoSap = ygSapHome.verificaEsistenzaSap(ygAdesioneDTO.getCodiceFiscale());
			if (nuovoIdentificativoSap != null && !"0".equalsIgnoreCase(nuovoIdentificativoSap)
					&& !isAdesioneFromCliclavoro(nuovoIdentificativoSap)) {
				ygAdesioneDTO.setIdentificativoSapOld(ygAdesioneDTO.getIdentificativoSap());
				ygAdesioneDTO.setIdentificativoSap(nuovoIdentificativoSap);
				result = true;
			}
		} catch (Exception e) {
			log.error("Errore Generico verifica esistenza SAP: " + e.getMessage());
		}
		return result;
	}

	public boolean isAdesioneFromCliclavoro(String identificativoSAP) {
		if (identificativoSAP != null && (identificativoSAP.startsWith("ZZ") || identificativoSAP.startsWith("zz"))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Risultati ordinati per YgAdesione_.dtAdesione desc metodo invocato solo da utenti provincia, contiene lavoratore
	 * anche diversi
	 * 
	 * @param parametriRicerca
	 * @param idPfPrincipal
	 * @return
	 */
	public List<YgRisultatoRicercaAdesioneDTO> findByFilter(YgRicercaAdesioneDTO parametriRicerca, Integer idPfPrincipal) {
		PfPrincipal pfPrincipalRequest = pfPrincipalHome.findById(idPfPrincipal);
		Provincia provinciaCollegata = pfPrincipalRequest.getProvinciasForIdPfPrincipal().iterator().next();

		if (!pfPrincipalRequest.isProvincia()) {
			throw new EJBException("Devi essere una provincia per eseguire le ricerche.");
		}

		// parametri di input
		String nome = parametriRicerca.getNome();
		String cognome = parametriRicerca.getCognome();
		String codiceFiscale = parametriRicerca.getCodiceFiscale();
		String email = parametriRicerca.getEmail();
		Date dataAdesioneDa = parametriRicerca.getDataAdesioneDa();
		Date dataAdesioneA = parametriRicerca.getDataAdesioneA();
		DeCpiDTO cpiAdesione = parametriRicerca.getCpiAdesione();
		Date dataFineStatoAdesioneDa = parametriRicerca.getDataFineStatoAdesioneDa();
		Date dataFineStatoAdesioneA = parametriRicerca.getDataFineStatoAdesioneA();
		DeStatoAdesioneDTO deStatoAdesioneDTO = parametriRicerca.getStatoAdesione();
		DeStatoAdesioneMinDTO deStatoMin = parametriRicerca.getStatoAdesioneMin();

		if (StringUtils.isNotEmpty(nome)) {
			nome = nome.trim().toUpperCase();
		}
		if (StringUtils.isNotEmpty(cognome)) {
			cognome = cognome.trim().toUpperCase();
		}
		if (StringUtils.isNotEmpty(codiceFiscale)) {
			codiceFiscale = codiceFiscale.trim().toUpperCase();
		}
		if (StringUtils.isNotEmpty(email)) {
			email = email.trim().toUpperCase();
		}

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<YgRisultatoRicercaAdesioneDTO> query = qb.createQuery(YgRisultatoRicercaAdesioneDTO.class);

		// La ricerca avviene a partire dalla tabella YG_ADESIONE
		// left join con la PF_PRINCIPAL
		// left join con UTENTE_INFO

		Root<YgAdesione> ygAdesione = query.from(YgAdesione.class);
		Join<YgAdesione, PfPrincipal> pfPrincipal = ygAdesione.join(YgAdesione_.pfPrincipal, JoinType.LEFT);
		Join<PfPrincipal, UtenteInfo> utenteInfo = pfPrincipal.join(PfPrincipal_.utenteInfo, JoinType.LEFT);
		Join<UtenteInfo, DeComune> deComuneDomicilio = utenteInfo.join(UtenteInfo_.deComuneDomicilio, JoinType.LEFT);
		Join<YgAdesione, PfPrincipal> pfPrincipalPic = ygAdesione.join(YgAdesione_.pfPrincipalPic, JoinType.LEFT);
		Join<PfPrincipal, Provincia> provincia = pfPrincipalPic.join(PfPrincipal_.provinciasForIdPfPrincipal,
				JoinType.LEFT);
		Join<Provincia, DeProvincia> deProvincia = provincia.join(Provincia_.deProvincia, JoinType.LEFT);
		Join<YgAdesione, DeRegione> deRegione = ygAdesione.join(YgAdesione_.deRegione, JoinType.LEFT);
		Join<YgAdesione, DeCpi> deCpi = ygAdesione.join(YgAdesione_.deCpiAssegnazione, JoinType.LEFT);
		Join<YgAdesione, DeStatoAdesione> deStatoAdesione = ygAdesione.join(YgAdesione_.deStatoAdesione, JoinType.LEFT);
		Join<YgAdesione, DeStatoAdesioneMin> deStatoAdesioneMin = ygAdesione.join(YgAdesione_.deStatoAdesioneMin,
				JoinType.LEFT);

		Join<YgAdesione, DeProvincia> deProvinciaRif = ygAdesione.join(YgAdesione_.deProvincia, JoinType.LEFT);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// TIPOLOGIA RICERCA ADESIONE

		if (parametriRicerca.getTipoRicerca().equals(YgTipoRicercaAdesione.ESATTA)) {

			if (StringUtils.isNotEmpty(nome)) {
				// whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.nome)),
				// nome));

				whereConditions.add(qb.or(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), nome),
						qb.equal(qb.upper(ygAdesione.get(YgAdesione_.nomeRifNotifica)), nome)));
			}
			if (StringUtils.isNotEmpty(cognome)) {
				// whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)),
				// cognome));
				whereConditions.add(qb.or(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), cognome),
						qb.equal(qb.upper(ygAdesione.get(YgAdesione_.cognomeRifNotifica)), cognome)));
			}
			if (StringUtils.isNotEmpty(codiceFiscale)) {
				whereConditions.add(qb.equal(qb.upper(ygAdesione.get(YgAdesione_.codiceFiscale)), codiceFiscale));
			}
			if (StringUtils.isNotEmpty(email)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.email)), email));
			}

		} else if (parametriRicerca.getTipoRicerca().equals(YgTipoRicercaAdesione.INIZIA_PER)) {

			if (StringUtils.isNotEmpty(nome)) {
				// whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)),
				// nome + "%"));

				whereConditions.add(qb.or(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), nome + "%"),
						qb.like(qb.upper(ygAdesione.get(YgAdesione_.nomeRifNotifica)), nome + "%")));
			}
			if (StringUtils.isNotEmpty(cognome)) {
				// whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)),
				// cognome + "%"));

				whereConditions.add(qb.or(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), cognome + "%"),
						qb.like(qb.upper(ygAdesione.get(YgAdesione_.cognomeRifNotifica)), cognome + "%")));
			}
			if (StringUtils.isNotEmpty(codiceFiscale)) {
				whereConditions.add(qb.like(qb.upper(ygAdesione.get(YgAdesione_.codiceFiscale)), codiceFiscale + "%"));
			}
			if (StringUtils.isNotEmpty(email)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.email)), email + "%"));
			}

		} else if (parametriRicerca.getTipoRicerca().equals(YgTipoRicercaAdesione.CONTIENE)) {

			if (StringUtils.isNotEmpty(nome)) {
				// whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)),
				// nome + "%"));

				whereConditions.add(qb.or(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), "%" + nome + "%"),
						qb.like(qb.upper(ygAdesione.get(YgAdesione_.nomeRifNotifica)), "%" + nome + "%")));
			}
			if (StringUtils.isNotEmpty(cognome)) {
				// whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)),
				// cognome + "%"));

				whereConditions.add(qb.or(
						qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), "%" + cognome + "%"),
						qb.like(qb.upper(ygAdesione.get(YgAdesione_.cognomeRifNotifica)), "%" + cognome + "%")));
			}
			if (StringUtils.isNotEmpty(codiceFiscale)) {
				whereConditions.add(qb.like(qb.upper(ygAdesione.get(YgAdesione_.codiceFiscale)), "%" + codiceFiscale
						+ "%"));
			}
			if (StringUtils.isNotEmpty(email)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.email)), "%" + email + "%"));
			}
		}

		if (dataAdesioneDa != null) {
			Path<Date> dataAdesione = ygAdesione.<Date> get("dtAdesione");
			whereConditions.add(qb.greaterThanOrEqualTo(dataAdesione, dataAdesioneDa));
		}
		if (dataAdesioneA != null) {
			Path<Date> dataAdesione = ygAdesione.<Date> get("dtAdesione");
			whereConditions.add(qb.lessThanOrEqualTo(dataAdesione, dataAdesioneA));
		}

		if (dataFineStatoAdesioneDa != null) {
			Path<Date> dataFineStatoAdesione = ygAdesione.<Date> get("dtFineStatoAdesione");
			whereConditions.add(qb.greaterThanOrEqualTo(dataFineStatoAdesione, dataFineStatoAdesioneDa));
		}
		if (dataFineStatoAdesioneA != null) {
			Path<Date> dataFineStatoAdesione = ygAdesione.<Date> get("dtFineStatoAdesione");
			whereConditions.add(qb.lessThanOrEqualTo(dataFineStatoAdesione, dataFineStatoAdesioneA));
		}

		if (cpiAdesione.getId() != null) {
			DeCpi cpi = deCpiHome.findById(cpiAdesione.getId());
			whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.deCpiAssegnazione), cpi));
		}

		if (deStatoAdesioneDTO != null && deStatoAdesioneDTO.getId() != null) {
			if (YgConstants.COD_FILTRO_RICERCA_ADESIONE_SENZA_STATO.equalsIgnoreCase(deStatoAdesioneDTO.getId())) {
				whereConditions.add(qb.isNull(ygAdesione.get(YgAdesione_.deStatoAdesione)));
			} else {
				DeStatoAdesione statoAdesione = deStatoAdesioneHome.findById(deStatoAdesioneDTO.getId());
				whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.deStatoAdesione), statoAdesione));
			}
		}
		if (deStatoMin != null && deStatoMin.getId() != null) {
			if (YgConstants.COD_FILTRO_RICERCA_ADESIONE_SENZA_STATO.equalsIgnoreCase(deStatoAdesioneDTO.getId())) {
				whereConditions.add(qb.isNull(ygAdesione.get(YgAdesione_.deStatoAdesione)));
			} else {
				DeStatoAdesioneMin statoAdesioneMinisteriale = deStatoAdesioneMinHome.findById(deStatoMin.getId());
				whereConditions
						.add(qb.equal(ygAdesione.get(YgAdesione_.deStatoAdesioneMin), statoAdesioneMinisteriale));
			}
		}

		// devono essere soddisfatte le condizioni base:
		// - flg_adesione = Y
		// - dt_adesione valorizzato

		whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.flgAdesione), true));
		whereConditions.add(qb.isNotNull(ygAdesione.get(YgAdesione_.dtAdesione)));

		// Carmela 2014/06/25 La provincia vede, a prescindere dal codMonoProv,
		// tutte:
		// - le adesioni cod_provincia_rif_notitica = cod provincia collegata
		// - le adesioni cod_provincia_rif_notitica nullo

		Predicate provinciaRifNotificaPredicate = qb.or(
				qb.equal(ygAdesione.get(YgAdesione_.deProvincia), provinciaCollegata.getDeProvincia()),
				qb.isNull(ygAdesione.get(YgAdesione_.deProvincia)));
		whereConditions.add(provinciaRifNotificaPredicate);

		// Assegnazione

		if (parametriRicerca.getAssegnazioneProvincia().equals(YgAssegnazioneProvincia.ASSEGNATE)) {
			whereConditions.add(qb.isNotNull(ygAdesione.get(YgAdesione_.deProvincia)));
		} else if (parametriRicerca.getAssegnazioneProvincia().equals(YgAssegnazioneProvincia.NON_ASSEGNATE)) {
			whereConditions.add(qb.isNull(ygAdesione.get(YgAdesione_.deProvincia)));
		}

		// Filtro Tipo di Adesione:
		// da Portale => solo con cod_mono_prov = I
		// da Cooperazione => solo con cod_mono_prov = N

		if (parametriRicerca.getTipoAdesione().equals(YgTipoAdesione.DA_COOPERAZIONE)) {
			whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.codMonoProv), "N"));
		} else if (parametriRicerca.getTipoAdesione().equals(YgTipoAdesione.DA_PORTALE)) {
			whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.codMonoProv), "I"));
		}

		// Filtro Stato di presa in Carico:
		// da prendere in carico => dtPresaInCarico nullo
		// presi in carico => dtPresaInCarico valorizzato

		if (parametriRicerca.getStatoPresaCarico().equals(YgStatoPresaCarico.SENZA_APPUNTAMENTO)) {
			whereConditions.add(qb.isNull(ygAdesione.get(YgAdesione_.dtPresaInCarico)));
		} else if (parametriRicerca.getStatoPresaCarico().equals(YgStatoPresaCarico.CON_APPUNTAMENTO)) {
			whereConditions.add(qb.isNotNull(ygAdesione.get(YgAdesione_.dtPresaInCarico)));
		}

		// Filtro Regione di adesione

		if (parametriRicerca.getRegioneRifNotifica().equals(YgRegioneRifNotifica.REGIONE_PORTALE)) {
			whereConditions.add(qb.equal(deRegione.get(DeRegione_.codRegione),
					ConstantsSingleton.COD_REGIONE.toString()));
		} else if (parametriRicerca.getRegioneRifNotifica().equals(YgRegioneRifNotifica.ALTRE)) {
			whereConditions.add(qb.notEqual(deRegione.get(DeRegione_.codRegione),
					ConstantsSingleton.COD_REGIONE.toString()));
		}

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		Order orderA = qb.desc(ygAdesione.get(YgAdesione_.dtAdesione));
		Order orderB = qb.asc(ygAdesione.get(YgAdesione_.codiceFiscale));
		List<Order> orderBy = new ArrayList<Order>();
		orderBy.add(orderA);
		orderBy.add(orderB);
		query.orderBy(orderBy);

		query.select(
				qb.construct(YgRisultatoRicercaAdesioneDTO.class, ygAdesione.get(YgAdesione_.idYgAdesione),
						ygAdesione.get(YgAdesione_.codiceFiscale), qb.upper(pfPrincipal.get(PfPrincipal_.cognome)),
						qb.upper(pfPrincipal.get(PfPrincipal_.nome)),
						qb.upper(ygAdesione.get(YgAdesione_.cognomeRifNotifica)),
						qb.upper(ygAdesione.get(YgAdesione_.nomeRifNotifica)), pfPrincipal.get(PfPrincipal_.email),
						ygAdesione.get(YgAdesione_.emailRifNotifica), ygAdesione.get(YgAdesione_.dtAdesione),
						ygAdesione.get(YgAdesione_.dtStatoAdesioneMin), ygAdesione.get(YgAdesione_.flgPresoInCarico),
						ygAdesione.get(YgAdesione_.dtPresaInCarico), deProvincia.get(DeProvincia_.denominazione),
						deRegione.get(DeRegione_.codRegione), deRegione.get(DeRegione_.denominazione),
						ygAdesione.get(YgAdesione_.codMonoProv), deCpi.get(DeCpi_.descrizione),
						ygAdesione.get(YgAdesione_.codMonoRecuperoCpi), pfPrincipal.get(PfPrincipal_.username),
						deStatoAdesione.get(DeStatoAdesione_.codStatoAdesione),
						deStatoAdesione.get(DeStatoAdesione_.descrizione),
						deStatoAdesioneMin.get(DeStatoAdesioneMin_.codStatoAdesioneMin),
						deStatoAdesioneMin.get(DeStatoAdesioneMin_.codMonoAttiva),
						deStatoAdesioneMin.get(DeStatoAdesioneMin_.descrizione),
						ygAdesione.get(YgAdesione_.dtFineStatoAdesione), ygAdesione.get(YgAdesione_.note),
						deProvinciaRif.get(DeProvincia_.codProvincia)

				)).distinct(true);

		TypedQuery<YgRisultatoRicercaAdesioneDTO> q = entityManager.createQuery(query);

		if (parametriRicerca.getStartResultsFrom() != 0) {
			q.setFirstResult(parametriRicerca.getStartResultsFrom());
		}

		if (parametriRicerca.getMaxResults() != 0) {
			q.setMaxResults(parametriRicerca.getMaxResults());
		}

		return q.getResultList();
	}

	/**
	 * Esegue la stessa ricerca del metodo findByFilter() ma il risultato viene restituito sotto forma di stringa in
	 * formato CSV.
	 * 
	 * Il metodo findByFilter() esegue la paginazione del risultato, se si vuole ottenere l'intero risultato della
	 * ricerca occorre settare a 0 i campi startResultsFrom e maxResults dell'oggetto YgRicercaAdesioneDTO passato in
	 * input.
	 * 
	 * @param parametriRicerca
	 * @param idPfPrincipal
	 * @return
	 */
	public String findByFilterCSV(YgRicercaAdesioneDTO parametriRicerca, Integer idPfPrincipal) {
		List<YgRisultatoRicercaAdesioneDTO> list = findByFilter(parametriRicerca, idPfPrincipal);

		String FIELD_DELIMITER = "\"";
		String FIELD_SEPARATOR = ";";
		String LINE_SEPARATOR = "\n";
		StringBuilder csvBuilder = new StringBuilder();

		/* riga dei titoli */
		csvBuilder.append(FIELD_DELIMITER + "Tipo" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Codice Fiscale" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Cognome" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Nome" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "E-mail" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Username" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Data adesione" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Regione adesione" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Data appuntamento" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "CPI" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Stato adesione" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Data fine stato adesione" + FIELD_DELIMITER + FIELD_SEPARATOR);
		csvBuilder.append(FIELD_DELIMITER + "Note" + FIELD_DELIMITER + LINE_SEPARATOR);

		/* dati */
		for (YgRisultatoRicercaAdesioneDTO ygRisultatoRicercaAdesioneDTO : list) {
			if (ygRisultatoRicercaAdesioneDTO.getCodMonoProv() != null) {
				csvBuilder.append("I".equals(ygRisultatoRicercaAdesioneDTO.getCodMonoProv()) ? FIELD_DELIMITER
						+ "da Portale" + FIELD_DELIMITER + FIELD_SEPARATOR : FIELD_DELIMITER + "da Cooperazione"
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getCodiceFiscale() != null) {
				csvBuilder.append(FIELD_DELIMITER + ygRisultatoRicercaAdesioneDTO.getCodiceFiscale() + FIELD_DELIMITER
						+ FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getCognomeUtente() != null) {
				csvBuilder.append(FIELD_DELIMITER + ygRisultatoRicercaAdesioneDTO.getCognomeUtente() + FIELD_DELIMITER
						+ FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getNomeUtente() != null) {
				csvBuilder.append(FIELD_DELIMITER + ygRisultatoRicercaAdesioneDTO.getNomeUtente() + FIELD_DELIMITER
						+ FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getEmailUtente() != null) {
				csvBuilder.append(FIELD_DELIMITER + ygRisultatoRicercaAdesioneDTO.getEmailUtente() + FIELD_DELIMITER
						+ FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getUsername() != null) {
				csvBuilder.append(FIELD_DELIMITER + ygRisultatoRicercaAdesioneDTO.getUsername() + FIELD_DELIMITER
						+ FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getDtAdesione() != null) {
				csvBuilder.append(FIELD_DELIMITER
						+ new SimpleDateFormat("dd/MM/yyyy").format(ygRisultatoRicercaAdesioneDTO.getDtAdesione())
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getDenominazioneRegioneAdesione() != null) {
				csvBuilder.append(FIELD_DELIMITER + ygRisultatoRicercaAdesioneDTO.getDenominazioneRegioneAdesione()
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getModificaDtPresoInCarico() != null) {
				csvBuilder.append(FIELD_DELIMITER
						+ new SimpleDateFormat("dd/MM/yyyy").format(ygRisultatoRicercaAdesioneDTO
								.getModificaDtPresoInCarico()) + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getDenominazioneCpiAssegnazione() != null) {
				csvBuilder.append(FIELD_DELIMITER + ygRisultatoRicercaAdesioneDTO.getDenominazioneCpiAssegnazione()
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getDescrizioneStatoAdesione() != null) {
				csvBuilder.append(FIELD_DELIMITER + ygRisultatoRicercaAdesioneDTO.getDescrizioneStatoAdesione()
						+ FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getDtFineStatoAdesione() != null) {
				csvBuilder.append(FIELD_DELIMITER
						+ new SimpleDateFormat("dd/MM/yyyy").format(ygRisultatoRicercaAdesioneDTO
								.getDtFineStatoAdesione()) + FIELD_DELIMITER + FIELD_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + FIELD_SEPARATOR);
			}
			if (ygRisultatoRicercaAdesioneDTO.getNote() != null) {
				csvBuilder.append(FIELD_DELIMITER + ygRisultatoRicercaAdesioneDTO.getNote() + FIELD_DELIMITER
						+ LINE_SEPARATOR);
			} else {
				csvBuilder.append(FIELD_DELIMITER + FIELD_DELIMITER + LINE_SEPARATOR);
			}
		}

		String csv = csvBuilder.toString();
		return csv;
	}

	/**
	 * Identica alla findByFilter, solo per utenti provincia
	 * 
	 * @param parametriRicerca
	 * @param idPfPrincipal
	 * @return
	 */
	public Long findCountByFilter(YgRicercaAdesioneDTO parametriRicerca, Integer idPfPrincipal) {

		PfPrincipal pfPrincipalRequest = pfPrincipalHome.findById(idPfPrincipal);
		Provincia provinciaCollegata = pfPrincipalRequest.getProvinciasForIdPfPrincipal().iterator().next();

		if (!pfPrincipalRequest.isProvincia()) {
			throw new EJBException("Devi essere una provincia per eseguire le ricerche.");
		}

		// parametri di input

		String nome = parametriRicerca.getNome();
		String cognome = parametriRicerca.getCognome();
		String codiceFiscale = parametriRicerca.getCodiceFiscale();
		String email = parametriRicerca.getEmail();
		Date dataAdesioneDa = parametriRicerca.getDataAdesioneDa();
		Date dataAdesioneA = parametriRicerca.getDataAdesioneA();
		DeCpiDTO cpiAdesione = parametriRicerca.getCpiAdesione();
		Date dataFineStatoAdesioneDa = parametriRicerca.getDataFineStatoAdesioneDa();
		Date dataFineStatoAdesioneA = parametriRicerca.getDataFineStatoAdesioneA();
		DeStatoAdesioneDTO deStatoAdesioneDTO = parametriRicerca.getStatoAdesione();
		DeStatoAdesioneMinDTO deStatoMin = parametriRicerca.getStatoAdesioneMin();

		if (StringUtils.isNotEmpty(nome)) {
			nome = nome.trim().toUpperCase();
		}
		if (StringUtils.isNotEmpty(cognome)) {
			cognome = cognome.trim().toUpperCase();
		}
		if (StringUtils.isNotEmpty(codiceFiscale)) {
			codiceFiscale = codiceFiscale.trim().toUpperCase();
		}
		if (StringUtils.isNotEmpty(email)) {
			email = email.trim().toUpperCase();
		}

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = qb.createQuery(Long.class);

		// La ricerca avviene a partire dalla tabella YG_ADESIONE
		// left join con la PF_PRINCIPAL
		// left join con UTENTE_INFO

		Root<YgAdesione> ygAdesione = query.from(YgAdesione.class);
		Join<YgAdesione, PfPrincipal> pfPrincipal = ygAdesione.join(YgAdesione_.pfPrincipal, JoinType.LEFT);
		Join<PfPrincipal, UtenteInfo> utenteInfo = pfPrincipal.join(PfPrincipal_.utenteInfo, JoinType.LEFT);
		Join<UtenteInfo, DeComune> deComuneDomicilio = utenteInfo.join(UtenteInfo_.deComuneDomicilio, JoinType.LEFT);
		Join<YgAdesione, PfPrincipal> pfPrincipalPic = ygAdesione.join(YgAdesione_.pfPrincipalPic, JoinType.LEFT);
		Join<PfPrincipal, Provincia> provincia = pfPrincipalPic.join(PfPrincipal_.provinciasForIdPfPrincipal,
				JoinType.LEFT);
		Join<Provincia, DeProvincia> deProvincia = provincia.join(Provincia_.deProvincia, JoinType.LEFT);
		Join<YgAdesione, DeRegione> deRegione = ygAdesione.join(YgAdesione_.deRegione, JoinType.LEFT);
		Join<YgAdesione, DeCpi> deCpi = ygAdesione.join(YgAdesione_.deCpiAssegnazione, JoinType.LEFT);
		Join<YgAdesione, DeStatoAdesione> deStatoAdesione = ygAdesione.join(YgAdesione_.deStatoAdesione, JoinType.LEFT);
		Join<YgAdesione, DeStatoAdesioneMin> deStatoAdesioneMin = ygAdesione.join(YgAdesione_.deStatoAdesioneMin,
				JoinType.LEFT);

		Join<YgAdesione, DeProvincia> deProvinciaRif = ygAdesione.join(YgAdesione_.deProvincia, JoinType.LEFT);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// TIPOLOGIA RICERCA ADESIONE

		if (parametriRicerca.getTipoRicerca().equals(YgTipoRicercaAdesione.ESATTA)) {

			if (StringUtils.isNotEmpty(nome)) {
				// whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.nome)),
				// nome));

				whereConditions.add(qb.or(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), nome),
						qb.equal(qb.upper(ygAdesione.get(YgAdesione_.nomeRifNotifica)), nome)));
			}
			if (StringUtils.isNotEmpty(cognome)) {
				// whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)),
				// cognome));
				whereConditions.add(qb.or(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), cognome),
						qb.equal(qb.upper(ygAdesione.get(YgAdesione_.cognomeRifNotifica)), cognome)));
			}
			if (StringUtils.isNotEmpty(codiceFiscale)) {
				whereConditions.add(qb.equal(qb.upper(ygAdesione.get(YgAdesione_.codiceFiscale)), codiceFiscale));
			}
			if (StringUtils.isNotEmpty(email)) {
				whereConditions.add(qb.equal(qb.upper(pfPrincipal.get(PfPrincipal_.email)), email));
			}

		} else if (parametriRicerca.getTipoRicerca().equals(YgTipoRicercaAdesione.INIZIA_PER)) {

			if (StringUtils.isNotEmpty(nome)) {
				// whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)),
				// nome + "%"));

				whereConditions.add(qb.or(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), nome + "%"),
						qb.like(qb.upper(ygAdesione.get(YgAdesione_.nomeRifNotifica)), nome + "%")));
			}
			if (StringUtils.isNotEmpty(cognome)) {
				// whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)),
				// cognome + "%"));

				whereConditions.add(qb.or(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), cognome + "%"),
						qb.like(qb.upper(ygAdesione.get(YgAdesione_.cognomeRifNotifica)), cognome + "%")));
			}
			if (StringUtils.isNotEmpty(codiceFiscale)) {
				whereConditions.add(qb.like(qb.upper(ygAdesione.get(YgAdesione_.codiceFiscale)), codiceFiscale + "%"));
			}
			if (StringUtils.isNotEmpty(email)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.email)), email + "%"));
			}

		} else if (parametriRicerca.getTipoRicerca().equals(YgTipoRicercaAdesione.CONTIENE)) {

			if (StringUtils.isNotEmpty(nome)) {
				// whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)),
				// nome + "%"));

				whereConditions.add(qb.or(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.nome)), "%" + nome + "%"),
						qb.like(qb.upper(ygAdesione.get(YgAdesione_.nomeRifNotifica)), "%" + nome + "%")));
			}
			if (StringUtils.isNotEmpty(cognome)) {
				// whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)),
				// cognome + "%"));

				whereConditions.add(qb.or(
						qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.cognome)), "%" + cognome + "%"),
						qb.like(qb.upper(ygAdesione.get(YgAdesione_.cognomeRifNotifica)), "%" + cognome + "%")));
			}
			if (StringUtils.isNotEmpty(codiceFiscale)) {
				whereConditions.add(qb.like(qb.upper(ygAdesione.get(YgAdesione_.codiceFiscale)), "%" + codiceFiscale
						+ "%"));
			}
			if (StringUtils.isNotEmpty(email)) {
				whereConditions.add(qb.like(qb.upper(pfPrincipal.get(PfPrincipal_.email)), "%" + email + "%"));
			}

		}

		if (dataAdesioneDa != null) {
			Path<Date> dataAdesione = ygAdesione.<Date> get("dtAdesione");
			whereConditions.add(qb.greaterThanOrEqualTo(dataAdesione, dataAdesioneDa));
		}
		if (dataAdesioneA != null) {
			Path<Date> dataAdesione = ygAdesione.<Date> get("dtAdesione");
			whereConditions.add(qb.lessThanOrEqualTo(dataAdesione, dataAdesioneA));
		}

		if (dataFineStatoAdesioneDa != null) {
			Path<Date> dataFineStatoAdesione = ygAdesione.<Date> get("dtFineStatoAdesione");
			whereConditions.add(qb.greaterThanOrEqualTo(dataFineStatoAdesione, dataFineStatoAdesioneDa));
		}
		if (dataFineStatoAdesioneA != null) {
			Path<Date> dataFineStatoAdesione = ygAdesione.<Date> get("dtFineStatoAdesione");
			whereConditions.add(qb.lessThanOrEqualTo(dataFineStatoAdesione, dataFineStatoAdesioneA));
		}

		if (cpiAdesione.getId() != null) {
			DeCpi cpi = deCpiHome.findById(cpiAdesione.getId());
			whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.deCpiAssegnazione), cpi));
		}

		if (deStatoAdesioneDTO != null && deStatoAdesioneDTO.getId() != null) {
			if (YgConstants.COD_FILTRO_RICERCA_ADESIONE_SENZA_STATO.equalsIgnoreCase(deStatoAdesioneDTO.getId())) {
				whereConditions.add(qb.isNull(ygAdesione.get(YgAdesione_.deStatoAdesione)));
			} else {
				DeStatoAdesione statoAdesione = deStatoAdesioneHome.findById(deStatoAdesioneDTO.getId());
				whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.deStatoAdesione), statoAdesione));
			}
		}
		if (deStatoMin != null && deStatoMin.getId() != null) {
			if (YgConstants.COD_FILTRO_RICERCA_ADESIONE_SENZA_STATO.equalsIgnoreCase(deStatoAdesioneDTO.getId())) {
				whereConditions.add(qb.isNull(ygAdesione.get(YgAdesione_.deStatoAdesione)));
			} else {
				DeStatoAdesioneMin statoAdesioneMinisteriale = deStatoAdesioneMinHome.findById(deStatoMin.getId());
				whereConditions
						.add(qb.equal(ygAdesione.get(YgAdesione_.deStatoAdesioneMin), statoAdesioneMinisteriale));
			}
		}

		// devono essere soddisfatte le condizioni base:
		// - flg_adesione = Y
		// - dt_adesione valorizzato

		whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.flgAdesione), true));
		whereConditions.add(qb.isNotNull(ygAdesione.get(YgAdesione_.dtAdesione)));

		// Carmela 2014/06/25 La provincia vede, a prescindere dal codMonoProv,
		// tutte:
		// - le adesioni cod_provincia_rif_notitica = cod provincia collegata
		// - le adesioni cod_provincia_rif_notitica nullo

		Predicate provinciaRifNotificaPredicate = qb.or(
				qb.equal(ygAdesione.get(YgAdesione_.deProvincia), provinciaCollegata.getDeProvincia()),
				qb.isNull(ygAdesione.get(YgAdesione_.deProvincia)));
		whereConditions.add(provinciaRifNotificaPredicate);

		// Assegnazione

		if (parametriRicerca.getAssegnazioneProvincia().equals(YgAssegnazioneProvincia.ASSEGNATE)) {
			whereConditions.add(qb.isNotNull(ygAdesione.get(YgAdesione_.deProvincia)));
		} else if (parametriRicerca.getAssegnazioneProvincia().equals(YgAssegnazioneProvincia.NON_ASSEGNATE)) {
			whereConditions.add(qb.isNull(ygAdesione.get(YgAdesione_.deProvincia)));
		}

		// Filtro Tipo di Adesione:
		// da Portale => solo con cod_mono_prov = I
		// da Cooperazione => solo con cod_mono_prov = N

		if (parametriRicerca.getTipoAdesione().equals(YgTipoAdesione.DA_COOPERAZIONE)) {
			whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.codMonoProv), "N"));
		} else if (parametriRicerca.getTipoAdesione().equals(YgTipoAdesione.DA_PORTALE)) {
			whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.codMonoProv), "I"));
		}

		// Filtro Stato di presa in Carico:
		// da prendere in carico => dtPresaInCarico nullo
		// presi in carico => dtPresaInCarico valorizzato

		if (parametriRicerca.getStatoPresaCarico().equals(YgStatoPresaCarico.SENZA_APPUNTAMENTO)) {
			whereConditions.add(qb.isNull(ygAdesione.get(YgAdesione_.dtPresaInCarico)));
		} else if (parametriRicerca.getStatoPresaCarico().equals(YgStatoPresaCarico.CON_APPUNTAMENTO)) {
			whereConditions.add(qb.isNotNull(ygAdesione.get(YgAdesione_.dtPresaInCarico)));
		}

		// Filtro Regione di adesione

		if (parametriRicerca.getRegioneRifNotifica().equals(YgRegioneRifNotifica.REGIONE_PORTALE)) {
			whereConditions.add(qb.equal(deRegione.get(DeRegione_.codRegione),
					ConstantsSingleton.COD_REGIONE.toString()));
		} else if (parametriRicerca.getRegioneRifNotifica().equals(YgRegioneRifNotifica.ALTRE)) {
			whereConditions.add(qb.notEqual(deRegione.get(DeRegione_.codRegione),
					ConstantsSingleton.COD_REGIONE.toString()));
		}

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		query.select(qb.count(ygAdesione));

		TypedQuery<Long> q = entityManager.createQuery(query);

		return q.getSingleResult();
	}

	public void updateAdesioni(List<YgRisultatoRicercaAdesioneDTO> adesioniModificate, Integer principalId) {
		for (YgRisultatoRicercaAdesioneDTO adesioneModificata : adesioniModificate) {
			YgAdesioneDTO oldYgAdesioneDTO = findDTOById(adesioneModificata.getIdYgAdesione());

			// oldYgAdesioneDTO.setIdPrincipalMod(principalId);
			// oldYgAdesioneDTO.setDtmMod(new Date());
			oldYgAdesioneDTO.setPfPrincipalPic(pfPrincipalHome.findDTOById(principalId));
			oldYgAdesioneDTO.setDtFineStatoAdesione(adesioneModificata.getModificaDtFineStatoAdesione());
			oldYgAdesioneDTO.setNote(adesioneModificata.getModificaNote());

			if (adesioneModificata.getModificaCodStatoAdesione() != null
					&& !"".equalsIgnoreCase(adesioneModificata.getModificaCodStatoAdesione())) {
				DeStatoAdesioneDTO deStatoAdesioneDTO = deStatoAdesioneHome.findDTOById(adesioneModificata
						.getModificaCodStatoAdesione());
				oldYgAdesioneDTO.setDeStatoAdesione(deStatoAdesioneDTO);
			} else {
				oldYgAdesioneDTO.setDeStatoAdesione(null);
			}

			// CARMELA 12/06/2014
			// per le sole notifiche vengono
			if (adesioneModificata.getModificaDtPresoInCarico() != null) {
				oldYgAdesioneDTO.setDtPresaInCarico(adesioneModificata.getModificaDtPresoInCarico());
				oldYgAdesioneDTO.setFlgPresoInCarico(true);
				String codMonoProv = oldYgAdesioneDTO.getCodMonoProv();
				if ("N".equalsIgnoreCase(codMonoProv)) {
					DeProvinciaDTO provNotifica = oldYgAdesioneDTO.getDeProvinciaNotifica();
					if (provNotifica == null) {
						PfPrincipal userRedaz = pfPrincipalHome.findById(principalId);
						Provincia userProvincia = provinciaHome.findByUsername(userRedaz.getUsername());
						String codProvincia = userProvincia.getDeProvincia().getCodProvincia();
						DeProvinciaDTO deProvinciaNotifica = deProvinciaHome.findDTOById(codProvincia);
						oldYgAdesioneDTO.setDeProvinciaNotifica(deProvinciaNotifica);
					}
				}
			}

			mergeDTO(oldYgAdesioneDTO, principalId);
		}
	}

	public LavoratoreType callRichiestaSap(Integer idAdesione) throws JAXBException, SAXException, RemoteException {
		YgAdesione adesione = findById(idAdesione);

		// chiamata al WS RICHIESTA SAP
		IDSAP xmlIdSap = new IDSAP();
		xmlIdSap.setIdentificativoSap(adesione.getIdentificativoSap());

		String xmlIdentificativoSap = null;
		xmlIdentificativoSap = youthGuaranteeSapEjb.convertRichiestaSapToString(xmlIdSap);

		log.debug("Chiamata Richiesta SAP:" + xmlIdentificativoSap);

		return youthGuaranteeSapEjb.richiestaSap(xmlIdentificativoSap);

	}

	public LavoratoreType processRecuperoProvicia(YgAdesione ygAdesioneInput) {
		YgAdesione adesione = findById(ygAdesioneInput.getIdYgAdesione());
		LavoratoreType lavSap = null;
		String identificativoSap = adesione.getIdentificativoSap();

		// chiamata al servizio richiesta SAP
		if (identificativoSap != null && !"".equalsIgnoreCase(identificativoSap)
				&& !"0".equalsIgnoreCase(identificativoSap)) {
			try {
				lavSap = callRichiestaSap(adesione.getIdYgAdesione());
			} catch (Exception e) {
				log.error("chiamata servizio richiesta sap min id_yg_adesione: " + adesione.getIdYgAdesione()
						+ " - errore: " + e.getMessage());
				lavSap = null;
			}
		}

		/* dati per completare l'adesione */
		String codComuneDomicilio = null;
		String codComuneResidenza = null;
		String email = null;
		String nome = null;
		String cognome = null;

		if (lavSap != null) {
			/* prendo i dati dalla SAP */
			Datianagrafici datiAnagrafici = lavSap.getDatianagrafici();
			if (datiAnagrafici != null) {
				if (datiAnagrafici.getDomicilio() != null) {
					codComuneDomicilio = datiAnagrafici.getDomicilio().getCodcomune();
				}
				if (datiAnagrafici.getResidenza() != null) {
					codComuneResidenza = datiAnagrafici.getResidenza().getCodcomune();
				}
				if (datiAnagrafici.getRecapiti() != null) {
					email = datiAnagrafici.getRecapiti().getEmail();
				}
				if (datiAnagrafici.getDatipersonali() != null) {
					nome = datiAnagrafici.getDatipersonali().getNome();
					cognome = datiAnagrafici.getDatipersonali().getCognome();
				}
			}
		} else {
			/* prendo i dati dal portale */
			PfPrincipal pfPrincipalAdesione = adesione.getPfPrincipal();
			/*
			 * NB: sto elaborando le adesioni provenienti da cooperazione, il pfPrincipal associato (se presente) e'
			 * derivato dall'elaborazione di una precedente adesione da cooperazione dello stesso codice fiscale.
			 */
			if (pfPrincipalAdesione != null) {
				UtenteInfo utenteAdesione = pfPrincipalAdesione.getUtenteInfo();

				if (utenteAdesione.getDeComuneDomicilio() != null) {
					codComuneDomicilio = utenteAdesione.getDeComuneDomicilio().getCodCom();
				}
				if (utenteAdesione.getDeComuneResidenza() != null) {
					codComuneResidenza = utenteAdesione.getDeComuneResidenza().getCodCom();
				}
				email = pfPrincipalAdesione.getEmail();
				nome = pfPrincipalAdesione.getNome();
				cognome = pfPrincipalAdesione.getCognome();
			}
		}

		/* completo i dati dell'adesione */
		completaYgAdesione(adesione, codComuneDomicilio, codComuneResidenza, email, nome, cognome);
		merge(adesione);

		return lavSap;
	}

	private void completaYgAdesione(YgAdesione adesione, String codComuneDomicilio, String codComuneResidenza,
			String email, String nome, String cognome) {
		/* comune di domicilio */
		DeComune deComuneDomicilio = null;
		if (codComuneDomicilio != null) {
			deComuneDomicilio = deComuneHome.findById(codComuneDomicilio);
		}
		adesione.setDeComuneDomicilioRifNotifica(deComuneDomicilio);

		/* comune di residenza */
		DeComune deComuneResidenza = null;
		if (codComuneResidenza != null) {
			deComuneResidenza = deComuneHome.findById(codComuneResidenza);
		}
		if (deComuneResidenza != null) {
			adesione.setDeComuneResidenzaRifNotifica(deComuneResidenza);
		} else {
			/*
			 * se il comune di residenza non è disponibile si ipotizza che sia uguale a quello di domicilio
			 */
			adesione.setDeComuneResidenzaRifNotifica(deComuneDomicilio);
		}

		/* provincia e cpi di riferimento sono quelli del comune di domicilio */
		DeProvincia deProvincia = null;
		DeCpi deCpi = null;
		if (deComuneDomicilio != null) {
			/*
			 * se ho un domicilio provincia e cpi di riferimento sono quelli del comune di domicilio
			 */
			deProvincia = deComuneDomicilio.getDeProvincia();
			deCpi = deComuneDomicilio.getDeCpi();
		}

		if (deProvincia == null
				|| !ConstantsSingleton.COD_REGIONE.toString().equalsIgnoreCase(
						deProvincia.getDeRegione().getCodRegione())) {
			/*
			 * se pero' il domicilio e' fuori regione la provincia e cpi di riferimento sono quelli del comune di
			 * residenza
			 */
			if (deComuneResidenza != null) {
				deProvincia = deComuneResidenza.getDeProvincia();
				deCpi = deComuneResidenza.getDeCpi();
			}
			/*
			 * se non ho una residenza o se e' fuori regione, la provincia e cpi di riferimento sono nulli
			 */
		}

		/*
		 * se non ho una residenza o se e' fuori regione, la provincia e cpi di riferimento sono nulli
		 */
		if (deProvincia != null
				&& ConstantsSingleton.COD_REGIONE.toString().equalsIgnoreCase(
						deProvincia.getDeRegione().getCodRegione())) {
			adesione.setDeProvincia(deProvincia);
			adesione.setDeCpiAssegnazione(deCpi);
		} else {
			adesione.setDeProvincia(null);
			adesione.setDeCpiAssegnazione(null);
		}

		adesione.setEmailRifNotifica(email);
		adesione.setNomeRifNotifica(nome);
		adesione.setCognomeRifNotifica(cognome);

		if ((codComuneDomicilio == null || codComuneDomicilio.isEmpty())
				&& (codComuneResidenza == null || codComuneResidenza.isEmpty()) && (email == null || email.isEmpty())
				&& (nome == null || nome.isEmpty()) && (cognome == null || cognome.isEmpty())) {
			/*
			 * non ho ne i dati dalla SAP ne da una precedente adesione da cooperazione l'adesione fallisce
			 */
			adesione.setCodMonoRecuperoCpi("F");
		} else if (isRecuperoSapFallito(adesione)) {
			/*
			 * se era già fallito una volta allora lo si rende non più processabile
			 */
			adesione.setCodMonoRecuperoCpi("N");
		} else {
			/* adesione riuscita */
			adesione.setCodMonoRecuperoCpi("Y");
		}

		merge(adesione);
	}

	/**
	 * Metodo per il reinvio delle adesioni della regione Umbria che sono erroneamente finite nell'ambiente di test in
	 * ministero. Funzione da eliminare subito!
	 * 
	 * @return
	 */
	public List<YgAdesioneDTO> findDTOByIdDateRange(Integer id, String da, String a) {
		Date inizioPeriodo = null;
		Date finePeriodo = null;
		List<YgAdesione> entityResult = new ArrayList<YgAdesione>();
		List<YgAdesioneDTO> result = new ArrayList<YgAdesioneDTO>();

		if (da != null && a != null) {
			try {
				inizioPeriodo = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(da);
				finePeriodo = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(a);
			} catch (ParseException e) {
				log.error(this.getClass().getName() + ": " + e.getMessage());
			}
		}

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<YgAdesione> query = qb.createQuery(YgAdesione.class);
		Root<YgAdesione> ygAdesione = query.from(YgAdesione.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.codMonoProv), "I"));
		whereConditions.add(qb.isTrue(ygAdesione.get(YgAdesione_.flgAdesione)));
		whereConditions.add(qb.isNotNull(ygAdesione.get(YgAdesione_.dtAdesione)));
		if (inizioPeriodo != null && finePeriodo != null) {
			whereConditions.add(qb.between(ygAdesione.get(YgAdesione_.dtmIns), inizioPeriodo, finePeriodo));
		}
		if (id != null) {
			whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.idYgAdesione), id));
		}
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<YgAdesione> q = entityManager.createQuery(query);
		entityResult = q.getResultList();

		for (YgAdesione entity : entityResult) {
			result.add(toDTO(entity));
		}

		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public PfPrincipal registraNuovoUtente(LavoratoreType lavoratore) throws ParseException {

		RegisterUtenteDTO dto = new RegisterUtenteDTO();
		PfPrincipal pfPrincipal = null;

		Datianagrafici datiAnagrafici = lavoratore.getDatianagrafici();

		//Boolean flgAbilitato = stConfigurazioneHome.findById(new Integer("1")).getFlgRegUtenteAbilitatoYg();
		Boolean flgAbilitato = tsGetOpzioniEJB.getFlgRegUtenteAbilitatoYg();

		// pf_principal

		boolean passwordScaduta = true;

		String nomeXml = datiAnagrafici.getDatipersonali().getNome();
		String nome = nomeXml.trim();
		dto.setNome(nome);

		String cognomeXml = datiAnagrafici.getDatipersonali().getCognome();
		String cognome = cognomeXml.trim();
		dto.setCognome(cognome);

		String username = clicLavoroEjb.creaUsername(Utils.normalizeAscii(nome).substring(0, 1)
				+ Utils.normalizeAscii(cognome).replaceAll(" {1,}", ""));
		dto.setUsername(Utils.cut(username.replaceAll("'", "").toLowerCase(), 16));

		String emailUtente = datiAnagrafici.getRecapiti().getEmail();
		dto.setEmail(emailUtente);
		dto.setEmailConfirm(emailUtente);

		dto.setAutenticazioneForte(false);
		dto.setAttivo(false);

		String password = Utils.randomString(ConstantsSingleton.TOKEN_PASSWORD_LENGTH);
		dto.setPassword(password);
		dto.setPasswordConfirm(password);

		dto.setAcceptInformativa(true);
		dto.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));

		// utente info

		dto.setCodiceFiscale(datiAnagrafici.getDatipersonali().getCodicefiscale());
		// dom
		dto.setIndirizzo(datiAnagrafici.getDomicilio().getIndirizzo());
		DeComuneDTO domicilio = deComuneHome.findDTOById(datiAnagrafici.getDomicilio().getCodcomune());
		dto.setDomicilio(domicilio);
		dto.setCap(datiAnagrafici.getDomicilio().getCap());
		// nasc
		dto.setDataNascita(Utils.fromXMLGregorianCalendar(datiAnagrafici.getDatipersonali().getDatanascita()));
		DeComuneDTO comuneNascita = deComuneHome.findDTOById(datiAnagrafici.getDatipersonali().getCodcomune());
		dto.setComune(comuneNascita);
		// provincia
		boolean provinciaSettata = false;
		DeProvinciaDTO deProvinciaDomicilioDTO = deProvinciaHome.findDTOById(domicilio.getIdProvincia());
		if (deProvinciaDomicilioDTO != null) {
			dto.setProvincia(deProvinciaDomicilioDTO);
			provinciaSettata = true;
		}
		// res
		if (datiAnagrafici.getResidenza() != null && datiAnagrafici.getResidenza().getCodcomune() != null) {
			DeComuneDTO residenza = deComuneHome.findDTOById(datiAnagrafici.getResidenza().getCodcomune());
			DeProvinciaDTO deProvinciaResidenzaDTO = deProvinciaHome.findDTOById(residenza.getIdProvincia());
			if (!provinciaSettata && deProvinciaResidenzaDTO != null) {
				dto.setProvincia(deProvinciaResidenzaDTO);
			}
		}
		// citt
		DeCittadinanzaDTO cittadinanzaDTO = new DeCittadinanzaDTO();
		cittadinanzaDTO.setId(datiAnagrafici.getDatipersonali().getCodcittadinanza());
		dto.setCittadinanza(cittadinanzaDTO);
		// cell
		dto.setCellulare(datiAnagrafici.getRecapiti().getCellulare());
		// soggiorno
		if (datiAnagrafici.getDatistranieri() != null
				&& datiAnagrafici.getDatistranieri().getCodtipodocumento() != null) {
			DeTitoloSoggiornoDTO deTitoloSoggiornoDTO = deTitoloSoggiornoHome.findDTOById(datiAnagrafici
					.getDatistranieri().getCodtipodocumento());
			dto.setDocumentoSoggiorno(deTitoloSoggiornoDTO);
			dto.setNumeroDocumento(datiAnagrafici.getDatistranieri().getNumero());
			if (datiAnagrafici.getDatistranieri().getValidoal() != null) {
				Date dtScadenzaDocumento;
				try {
					dtScadenzaDocumento = Utils.gregorianDateToDate(datiAnagrafici.getDatistranieri().getValidoal());
					dto.setDataScadenzaDocumento(dtScadenzaDocumento);
				} catch (DatatypeConfigurationException e) {
					log.error("Errore conversione data scadenza documento soggiorno: " + e.getMessage());
				}
			}
		} else {
			dto.setDataScadenzaDocumento(null);
			dto.setDocumentoIdentita(null);
			dto.setDocumentoSoggiorno(null);
		}

		dto.setCodiceRichiestaAutForte(null);
		dto.setDataAssicurata(null);
		dto.setDomanda("Quale e' il tuo codice fiscale?");
		dto.setIndirizzoPEC(null);
		dto.setNumeroAssicurata(null);
		dto.setRegisterFromProvider(false);
		dto.setRisposta(datiAnagrafici.getDatipersonali().getCodicefiscale());

		// //////////////////////////
		// REGISTRAZIONE STANDARD //
		// //////////////////////////

		Integer idPfPrincipal = utenteInfoHome.register(dto, passwordScaduta);
		pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);

		// /////////////////////
		// INTEGRAZIONE DATI //
		// /////////////////////

		String codGenere = datiAnagrafici.getDatipersonali().getSesso();
		String indirizzoResidenza = null;
		String capResidenza = null;
		String codComResidenza = null;
		String codMotivoPermesso = null;
		if (datiAnagrafici.getResidenza() != null) {
			indirizzoResidenza = datiAnagrafici.getResidenza().getIndirizzo();
			capResidenza = datiAnagrafici.getResidenza().getCap();
			codComResidenza = datiAnagrafici.getResidenza().getCodcomune();
		}
		if (datiAnagrafici.getDatistranieri() != null) {
			Object motivo = datiAnagrafici.getDatistranieri().getMotivo();
			if (motivo != null && motivo instanceof String) {
				codMotivoPermesso = (String) motivo;
			}
		}
		utenteInfoHome.integraDatiGenereResidenzaPermesso(idPfPrincipal, codGenere, indirizzoResidenza, capResidenza,
				codComResidenza, codMotivoPermesso);

		if (flgAbilitato) {
			pfPrincipalHome.abilitaUtente(idPfPrincipal);
		}

		// ///////////////
		// INVIO EMAIL //
		// ///////////////

		EmailDTO registerEmail = EmailDTO.buildRegistrationEmailYg(dto, flgAbilitato);
		Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registerEmail);

		return pfPrincipal;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateIdPfPrincipal(Integer idYgAdesione, Integer idPfPrincipal, Boolean flgCreatoAccount) {

		YgAdesione ygAdesione = findById(idYgAdesione);

		ygAdesione.setDtmMod(new Date());
		ygAdesione.setPfPrincipalMod(pfPrincipalHome.getAdministrator());
		ygAdesione.setPfPrincipal(pfPrincipalHome.findById(idPfPrincipal));
		ygAdesione.setFlgCreatoAccount(flgCreatoAccount);

		merge(ygAdesione);

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateStrMessAccount(Integer idYgAdesione, String errore) {

		YgAdesione ygAdesione = findById(idYgAdesione);

		ygAdesione.setDtmMod(new Date());
		ygAdesione.setPfPrincipalMod(pfPrincipalHome.getAdministrator());
		if (errore != null) {
			if (errore.length() > 2000) {
				// troncamento
				errore = errore.substring(0, 2000);
			}
			ygAdesione.setStrMessAccount(errore);
		}
		ygAdesione.setFlgCreatoAccount(false);

		merge(ygAdesione);

	}

	public boolean esisteAdesioneByCodiceFiscaleAndCodMonoProv(String codiceFiscale, String codMonoProv) {

		boolean esiste = false;

		List<YgAdesione> adesioni = entityManager
				.createNamedQuery("findYgAdesioneByCodiceFiscaleAndCodMonoProv", YgAdesione.class)
				.setParameter("codice_fiscale", codiceFiscale).setParameter("cod_mono_prov", codMonoProv)
				.getResultList();

		if (adesioni != null && !adesioni.isEmpty()) {
			esiste = true;
		}

		return esiste;

	}

	public boolean esisteAdesioneInviataByCodiceFiscaleAndCodMonoProv(String codiceFiscale, String codMonoProv) {

		boolean esiste = false;

		List<YgAdesione> adesioni = entityManager
				.createNamedQuery("findYgAdesioneInviataByCFAndCodMonoProv", YgAdesione.class)
				.setParameter("codice_fiscale", codiceFiscale).setParameter("cod_mono_prov", codMonoProv)
				.getResultList();

		if (adesioni != null && !adesioni.isEmpty()) {
			esiste = true;
		}

		return esiste;

	}

	public String[] getErroreAdesioneGiaPresenteLatoMinistero(Integer idPfPrincipal) {


		YgAdesioneDTO currentYgAdesioneDTO = findLatestDTOByIdPfPrincipal(idPfPrincipal);
		if (currentYgAdesioneDTO != null && !currentYgAdesioneDTO.getFlgAdesione()) {
			String messaggioErrore = currentYgAdesioneDTO.getStrMessWsAdesione();
			if (messaggioErrore != null && !"".equalsIgnoreCase(messaggioErrore)) {
				String descrizioneAnomalia = "";
				String strListaAnomalie = messaggioErrore.split("Riscontrato errore nella validazione dell'input :")[1];

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db;
				try {
					
					db = dbf.newDocumentBuilder();
					InputSource is = new InputSource(new StringReader(strListaAnomalie));
					Document doc = db.parse(is);
					XPath xpath = XPathFactory.newInstance().newXPath();
					// recupero valori
					XPathExpression exprCodAnomalia = xpath.compile("/ListaAnomalie/Anomalia/CodiceAnomalia");
					XPathExpression exprDescrAnomalia = xpath.compile("/ListaAnomalie/Anomalia/DescrizioneAnomalia");
					Object anomalie = exprCodAnomalia.evaluate(doc, XPathConstants.NODESET);
					Object anomalieDescr = exprDescrAnomalia.evaluate(doc, XPathConstants.NODESET);
					NodeList anomalieList = (NodeList) anomalie;
					NodeList anomalieDescrList = (NodeList) anomalieDescr;
					boolean isMsgUmb = false;			
					for (int i = 0; i < anomalieList.getLength(); i++) {
						Node anomaliaNode = anomalieList.item(i);
						String codiceAnomalia = anomaliaNode.getFirstChild().getNodeValue();
						Node anomaliaDescrNode = anomalieDescrList.item(i);
						String descrAnomaliaXml = anomaliaDescrNode.getFirstChild().getNodeValue();
						descrizioneAnomalia = descrizioneAnomalia + " " + descrAnomaliaXml + "<br/>";
						if (codiceAnomalia != null
								&& YgConstants.CODICE_ANOMALIA_ADESIONE_GIA_PRESENTE.equalsIgnoreCase(codiceAnomalia)) {
								
								if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)) { 
									isMsgUmb = true;
								}
							
								String[] retArr = new String[2];
								retArr[0] = descrAnomaliaXml;
								retArr[1] = Boolean.toString(isMsgUmb);
							return retArr;
						}
					}
				} catch (Exception e) {
					log.error("Errore parsing XML mess ws adesione: " + e.getMessage());
				}
			}
		}

		String[] retArr = new String[2];
		retArr[0] = null;
		retArr[1] = Boolean.toString(false);
				
		return retArr;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void salvaErroreStrMessAdesione(Integer idYgAdesione, String msg) {

		YgAdesione ygAdesione = findById(idYgAdesione);
		ygAdesione.setStrMessWsAdesione(msg);
		ygAdesione.setFlgAdesione(false);
		merge(ygAdesione);

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private String getDataAdesioneDal() {

		String dtAdesioneDalString = null;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		//Date dtAdesioneDal = stConfigurazioneHome.findById(new Integer("1")).getDtBatchYg();
		Date dtAdesioneDal = tsGetOpzioniEJB.getDtBatchYg();
		if (dtAdesioneDal != null) {
			dtAdesioneDalString = df.format(dtAdesioneDal);
		}

		return dtAdesioneDalString;

	}

	private void processInserimentoLavoratore(YgAdesione ygAdesione, LavoratoreType sap) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		DateFormat dateFormatShort = new SimpleDateFormat("dd/MM/yyyy");
		boolean accountInseribile;

		YgImpostazioni ygImpostazioni = ygImpostazioniHome.findByCodRegione(ConstantsSingleton.COD_REGIONE.toString());

		if (ygImpostazioni.getFlgCreazioneAccount()) {
			try {
				Date dateCur = new Date();
				Date dateAdesione = ygAdesione.getDtAdesione();
				if (dateAdesione == null) {
					dateAdesione = new Date();
				}
				Date min = ygImpostazioni.getDtInizioAdesione();
				if (min == null) {
					String strDateMin = "01/05/2014";
					min = dateFormatShort.parse(strDateMin);
				}
				Date max = ygImpostazioni.getDtFineAdesione();
				if (max == null) {
					String strDateMax = "31/12/2100";
					max = dateFormatShort.parse(strDateMax);
				}

				if (dateAdesione.after(min) && dateAdesione.before(max)) {
					accountInseribile = checkAccountInseribile(ygAdesione, sap);
					if (accountInseribile) {
						try {
							PfPrincipal pfPrincipal = registraNuovoUtente(sap);

							if (pfPrincipal != null) {
								updateIdPfPrincipal(ygAdesione.getIdYgAdesione(), pfPrincipal.getIdPfPrincipal(), true);
							} else {
								updateStrMessAccount(ygAdesione.getIdYgAdesione(),
										"Errore inserimento account lavoratore: " + dateFormat.format(dateCur)
												+ " PFPRINCIPAL NULLO");
							}
						} catch (Exception e) {
							log.error("Eccezione inserimento account lavoratore yg: " + e.getMessage());
							updateStrMessAccount(
									ygAdesione.getIdYgAdesione(),
									"Errore inserimento account lavoratore: " + dateFormat.format(dateCur) + " \n"
											+ e.getMessage());
						}
					}
				}
			} catch (Exception e1) {
				log.error("Eccezione processInserimentoLavoratore yg: " + e1.getMessage());
			}
		}
	}

	private boolean checkAccountInseribile(YgAdesione ygAdesione, LavoratoreType sap)
			throws DatatypeConfigurationException, ParseException {

		int eta = 0;
		boolean accountInseribile = true;

		boolean emailDisponibile = false;
		if (sap != null && sap.getDatianagrafici() != null && sap.getDatianagrafici().getRecapiti() != null
				&& sap.getDatianagrafici().getRecapiti().getEmail() != null
				&& !"".equalsIgnoreCase(sap.getDatianagrafici().getRecapiti().getEmail())) {
			String emailLavoratore = sap.getDatianagrafici().getRecapiti().getEmail();

			XMLGregorianCalendar dataNascitaGregCal = sap.getDatianagrafici().getDatipersonali().getDatanascita();
			Date datNascita = Utils.gregorianDateToDate(dataNascitaGregCal);
			eta = Utils.getAge(datNascita, ygAdesione.getDtAdesione());

			List<PfPrincipal> lista = pfPrincipalHome.findByEmail(emailLavoratore.toUpperCase());
			emailDisponibile = (lista == null || lista.size() == 0);
		}

		boolean isOkPerPortale = true;

		// MAIL ANGELA 02/07/2014 10.38 sia tolto il filtro sul domicilio di
		// Trento (così da inviare ai domiciliati e
		// non)
		/*
		 * if (("22".equalsIgnoreCase(ConstantsSingleton.COD_REGIONE.toString()))) { isOkPerPortale = false; String
		 * comuneDomicilio = null; if (sap.getDatianagrafici() != null && sap.getDatianagrafici().getDomicilio() != null
		 * && sap.getDatianagrafici().getDomicilio().getCodcomune() != null && !"".equalsIgnoreCase
		 * (sap.getDatianagrafici().getDomicilio().getCodcomune())) { comuneDomicilio =
		 * sap.getDatianagrafici().getDomicilio().getCodcomune(); } if (comuneDomicilio != null) { DeComune
		 * deComuneDomicilio = deComuneHome.findById(comuneDomicilio); if (deComuneDomicilio != null &&
		 * deComuneDomicilio.getDeProvincia() != null && deComuneDomicilio.getDeProvincia().getDeRegione() != null &&
		 * "22".equalsIgnoreCase (deComuneDomicilio.getDeProvincia().getDeRegione().getCodRegione())) { isOkPerPortale =
		 * true; } } }
		 */

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		String dataOra = dateFormat.format(date);

		String erroreInserimentoAccount = "Account non inserito (" + dataOra + "): ";

		if (sap == null || sap.getDatianagrafici() == null) {
			if (!accountInseribile) {
				erroreInserimentoAccount += ", ";
			}
			erroreInserimentoAccount += "SAP nulla o senza dati anagrafici";
			accountInseribile = false;
		}

		if (ygAdesione.getDtPresaInCarico() != null) {
			if (!accountInseribile) {
				erroreInserimentoAccount += ", ";
			}
			erroreInserimentoAccount += "data adesione già presente";
			accountInseribile = false;
		}

		if (!ConstantsSingleton.COD_REGIONE.toString().equalsIgnoreCase(ygAdesione.getDeRegione().getCodRegione())) {
			if (!accountInseribile) {
				erroreInserimentoAccount += ", ";
			}
			erroreInserimentoAccount += "la Regione di adesione non corrisponde a quella de portale";
			accountInseribile = false;
		}

		if (esisteAdesioneByCodiceFiscaleAndCodMonoProv(ygAdesione.getCodiceFiscale(), "I")) {
			if (!accountInseribile) {
				erroreInserimentoAccount += ", ";
			}
			erroreInserimentoAccount += "esiste per il CF una adesione con provenienza interna";
			accountInseribile = false;
		}

		if (!emailDisponibile) {
			if (!accountInseribile) {
				erroreInserimentoAccount += ", ";
			}
			erroreInserimentoAccount += "indirizzo email non disponibile";
			accountInseribile = false;
		}

		if (eta >= 30) {
			if (!accountInseribile) {
				erroreInserimentoAccount += ", ";
			}
			erroreInserimentoAccount += "età lavoratore maggiore o uguale a 30";
			accountInseribile = false;
		}

		if (!isOkPerPortale) {
			if (!accountInseribile) {
				erroreInserimentoAccount += ", ";
			}
			erroreInserimentoAccount += "Account non inserito: comune domicilio non appartenente a PAT";
			accountInseribile = false;
		}

		if (!accountInseribile) {
			updateStrMessAccount(ygAdesione.getIdYgAdesione(), erroreInserimentoAccount);
		}

		return accountInseribile;

	}

	public List<YgAdesione> getAdesioniYgSenzaProvinciaRifNotifica() {

		String dataAdesioneDal = getDataAdesioneDal();

		TypedQuery<YgAdesione> q = entityManager
				.createQuery(" select yg " + " from YgAdesione yg " + " where yg.codMonoProv = 'N'"
						+ " and (yg.codMonoRecuperoCpi is null) " + " and (yg.dtAdesione >= TO_DATE('"
						+ dataAdesioneDal + "','DD/MM/YYYY')) "
						+ " and (yg.deRegione is not null and yg.deRegione.codRegione = '"
						+ ConstantsSingleton.COD_REGIONE.toString() + "')" + " order by idYgAdesione", YgAdesione.class);

		q.setMaxResults(50);
		List<YgAdesione> results = q.getResultList();

		return results;

	}

	public YgAdesione getAdesioniYgDaCooperazionePrecedenteConAccount(String codiceFiscale) {
		YgAdesione result = null;

		TypedQuery<YgAdesione> q = entityManager.createQuery(
				" select yg " + " from YgAdesione yg " + " where yg.codMonoProv = 'N'"
						+ " and (yg.pfPrincipal is not null and yg.pfPrincipal.idPfPrincipal is not null)"
						+ " and (yg.deRegione is not null and yg.deRegione.codRegione = '"
						+ ConstantsSingleton.COD_REGIONE.toString() + "')" + " and upper(yg.codiceFiscale) = '"
						+ codiceFiscale.toUpperCase() + "'", YgAdesione.class);

		List<YgAdesione> results = q.getResultList();
		if (results != null && results.size() > 0) {
			result = results.get(0);
		}

		return result;

	}

	public List<YgAdesione> getAdesioniYgSenzaSap() {

		String dataAdesioneDal = getDataAdesioneDal();

		TypedQuery<YgAdesione> q = entityManager
				.createQuery(" select yg " + " from YgAdesione yg " + " where yg.codMonoProv = 'N'"
						+ " and yg.dtPresaInCarico is null"
						+ " and (yg.codMonoRecuperoCpi is not null and yg.codMonoRecuperoCpi = 'F') "
						+ " and (yg.dtAdesione >= TO_DATE('" + dataAdesioneDal + "','DD/MM/YYYY')) "
						+ " and (yg.deRegione is not null and yg.deRegione.codRegione = '"
						+ ConstantsSingleton.COD_REGIONE.toString() + "')" + " order by idYgAdesione", YgAdesione.class);

		q.setMaxResults(50);
		List<YgAdesione> results = q.getResultList();

		return results;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processBatch(YgAdesione ygAdesione) {
		boolean esegui = true;
		/*
		 * se l'adesione era già stata processata e non era stata trovata una sap, si verifica se esiste un nuovo
		 * identificativo sap
		 */
		if (isRecuperoSapFallito(ygAdesione)) {
			/*
			 * se il recupero fallisce per qualche motivo (ad esempio servizio non disponibile) non si procede, si
			 * riproverà la prossima volta che il batch verrà eseguito
			 */
			esegui = recuperaNuovoIdentificativoSap(ygAdesione);
		}
		if (esegui) {
			/*
			 * se ho gia' creato un utente per questo lavoratore (sempre proveniente da cooperazione) lo collego
			 */
			collegaAccountDaCooperazione(ygAdesione);
			/* recupero provincia */
			LavoratoreType sap = processRecuperoProvicia(ygAdesione);

			if (ygAdesione.getPfPrincipal() == null) {
				/*
				 * se non c'e' gia' un utente collegato provo ad inserirlo (in base alle direttive della regione)
				 */
				processInserimentoLavoratore(ygAdesione, sap);
			}
		}
	}

	private boolean recuperaNuovoIdentificativoSap(YgAdesione ygAdesioneInput) {
		YgAdesione ygAdesione = findById(ygAdesioneInput.getIdYgAdesione());
		boolean success = false;
		String identificativoSap = null;

		try {
			identificativoSap = ygSapHome.verificaEsistenzaSap(ygAdesione.getCodiceFiscale());
			if (identificativoSap != null && !"0".equalsIgnoreCase(identificativoSap)) {
				String identificativoSapOld = ygAdesione.getIdentificativoSap();
				ygAdesione.setIdentificativoSapOld(identificativoSapOld);
				ygAdesione.setIdentificativoSap(identificativoSap);
				ygAdesione.setDtmMod(new Date());
				ygAdesione.setPfPrincipalMod(pfPrincipalHome.getAdministrator());
				merge(ygAdesione);
				success = true;
			} else if (identificativoSap != null && "0".equalsIgnoreCase(identificativoSap)) {
				ygAdesione.setCodMonoRecuperoCpi("N");
				ygAdesione.setDtmMod(new Date());
				ygAdesione.setPfPrincipalMod(pfPrincipalHome.getAdministrator());
				merge(ygAdesione);
			}
		} catch (Exception e) {
			log.error("Errore Generico verifica esistenza SAP: " + e.getMessage());
		}

		return success;
	}

	private boolean isRecuperoSapFallito(YgAdesione ygAdesione) {
		if (ygAdesione != null && ygAdesione.getCodMonoRecuperoCpi() != null
				&& "F".equalsIgnoreCase(ygAdesione.getCodMonoRecuperoCpi())) {
			return true;
		}
		return false;
	}

	public List<YgRisultatoRicercaAdesioneDTO> findByFilter(String codiceFiscale, String codRegione) {

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<YgRisultatoRicercaAdesioneDTO> query = qb.createQuery(YgRisultatoRicercaAdesioneDTO.class);

		Root<YgAdesione> ygAdesione = query.from(YgAdesione.class);
		Join<YgAdesione, DeRegione> deRegione = ygAdesione.join(YgAdesione_.deRegione, JoinType.LEFT);
		Join<YgAdesione, DeProvincia> deProvincia = ygAdesione.join(YgAdesione_.deProvincia, JoinType.LEFT);
		Join<YgAdesione, DeStatoAdesioneMin> deStatoAdesioneMin = ygAdesione.join(YgAdesione_.deStatoAdesioneMin,
				JoinType.LEFT);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(qb.upper(ygAdesione.get(YgAdesione_.codiceFiscale)), codiceFiscale.toUpperCase()));

		if (StringUtils.isNotEmpty(codRegione)) {
			whereConditions.add(qb.equal(qb.upper(deRegione.get(DeRegione_.codMin)), codRegione.toUpperCase()));
		}

		whereConditions.add(qb.equal(ygAdesione.get(YgAdesione_.flgAdesione), true));

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		Order orderA = qb.desc(ygAdesione.get(YgAdesione_.dtAdesione));
		List<Order> orderBy = new ArrayList<Order>();
		orderBy.add(orderA);
		query.orderBy(orderBy);

		query.select(
				qb.construct(YgRisultatoRicercaAdesioneDTO.class, ygAdesione.get(YgAdesione_.dtAdesione),
						deRegione.get(DeRegione_.codMin), deProvincia.get(DeProvincia_.codProvincia),
						deStatoAdesioneMin.get(DeStatoAdesioneMin_.codStatoAdesioneMin),
						ygAdesione.get(YgAdesione_.dtStatoAdesioneMin))).distinct(true);

		TypedQuery<YgRisultatoRicercaAdesioneDTO> q = entityManager.createQuery(query);

		return q.getResultList();

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processBatchRecuperoAnagNoAccount() {
		List<YgAdesione> listaAdesioni = getAdesioniYgSenzaAccount();

		for (YgAdesione ygAdesione : listaAdesioni) {
			log.info("start adesione id=" + ygAdesione.getIdYgAdesione());

			// reupero la SAP
			String identificativoSap = ygAdesione.getIdentificativoSap();
			LavoratoreType lavSap = null;
			if (identificativoSap != null && !"".equalsIgnoreCase(identificativoSap)
					&& !"0".equalsIgnoreCase(identificativoSap)) {
				try {
					lavSap = callRichiestaSap(ygAdesione.getIdYgAdesione());
				} catch (Exception e) {
					log.error("chiamata servizio richiesta sap min id_yg_adesione: " + ygAdesione.getIdYgAdesione()
							+ " - errore: " + e.getMessage());
					lavSap = null;
				}
			}

			if (lavSap != null) {

				Datianagrafici datianagrafici = lavSap.getDatianagrafici();
				if (datianagrafici != null) {
					// processing del domicilio
					try {
						String codiceComune = datianagrafici.getDomicilio().getCodcomune();
						DeComune deComune = deComuneHome.findById(codiceComune);
						ygAdesione.setDeComuneDomicilioRifNotifica(deComune);

					} catch (Exception e) {
						// comune di domicilio non trovata
						log.error(
								"Errore durante ricerca comune domicilio id_yg_adesione: "
										+ ygAdesione.getIdYgAdesione(), e);
					}

					// processing comune residenza (sempre)
					try {
						boolean isComuneResidenzaTrovato = false;
						if (datianagrafici.getResidenza() != null
								&& datianagrafici.getResidenza().getCodcomune() != null) {
							String codiceComune = datianagrafici.getResidenza().getCodcomune();
							DeComune deComune = deComuneHome.findById(codiceComune);
							if (deComune != null) {
								ygAdesione.setDeComuneResidenzaRifNotifica(deComune);
								isComuneResidenzaTrovato = true;
							}
						}
						if (!isComuneResidenzaTrovato) {
							// se il comune di residenza non è disponibile
							// si ipotizza che sia uguale a quello di domicilio
							ygAdesione.setDeComuneResidenzaRifNotifica(ygAdesione.getDeComuneDomicilioRifNotifica());
						}
					} catch (Exception e) {
						log.error(
								"Errore durante ricerca comune residenza id_yg_adesione: "
										+ ygAdesione.getIdYgAdesione(), e);
					}
				}

				// aggiorno i dati anagrafici
				merge(ygAdesione);
			}

			log.info("end adesione id=" + ygAdesione.getIdYgAdesione());
		}
	}

	public List<YgAdesione> getAdesioniYgSenzaAccount() {
		TypedQuery<YgAdesione> q = entityManager
				.createQuery(
						" select yg "
								+ " from YgAdesione yg "
								+ " where yg.pfPrincipal.idPfPrincipal is null"
								+ " and (yg.deComuneResidenzaRifNotifica.codCom is null or yg.deComuneResidenzaRifNotifica.codCom is null)",
						YgAdesione.class);
		List<YgAdesione> results = q.getResultList();

		return results;
	}

	/**
	 * Cerco l'adesione corriaspondente ai dati in input con flg_adesione = 'Y'.
	 * 
	 * @param codiceFiscale
	 * @param dataAdesione
	 * @param codRegione
	 * @return //
	 */
	public YgAdesione findByCodiceFiscaleDataRegioneAdesione(String codiceFiscale, Date dataAdesione, String codRegione) {
		YgAdesione adesione = null;
		List<YgAdesione> list = entityManager.createNamedQuery("findYgAdesioneByCfDtAdesCodReg", YgAdesione.class)
				.setParameter("codiceFiscale", codiceFiscale.toUpperCase()).setParameter("dtAdesione", dataAdesione)
				.setParameter("codRegione", codRegione).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			adesione = list.get(0);
			return adesione;
		}
	}

	/**
	 * Cerco l'ultima adesione (dt_adesione piu' recente) corrispondente alla regione del portale con flg_adesione = 'Y'
	 * relativa al codice fiscale passto in input.
	 * 
	 * @param codiceFiscale
	 * @return
	 */
	public YgAdesione findLatestByCodiceFiscaleInRegionePortale(String codiceFiscale) {
		YgAdesione adesione = null;
		List<YgAdesione> list = entityManager
				.createNamedQuery("findLatestByCodiceFiscaleInRegionePortale", YgAdesione.class)
				.setParameter("codiceFiscale", codiceFiscale.toUpperCase())
				.setParameter("codRegione", String.valueOf(ConstantsSingleton.COD_REGIONE)).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			adesione = list.get(0);
			return adesione;
		}
	}
	
	public YgAdesioneDTO findLatestDTOByCodiceFiscaleInRegionePortale(String codiceFiscale){
		return toDTO(findLatestByCodiceFiscaleInRegionePortale(codiceFiscale));
	}

	private void collegaAccountDaCooperazione(YgAdesione ygAdesione) {
		YgAdesione ygAdesionePrecAccount = getAdesioniYgDaCooperazionePrecedenteConAccount(ygAdesione
				.getCodiceFiscale());
		if (ygAdesionePrecAccount != null) {
			ygAdesione.setPfPrincipal(ygAdesionePrecAccount.getPfPrincipal());
			merge(ygAdesione);
		}
	}

	public YgAdesione aggiornaAdesione(Integer idYgAdesione, DatiAppuntamento datiAppuntamento,
			UtenteCompletoDTO utenteCompletoDTO) throws DatatypeConfigurationException, ParseException {

		YgAdesione ygAdesione = findById(idYgAdesione);
		ygAdesione.setDtPresaInCarico(Utils.gregorianDateToDate(datiAppuntamento.getDataAppuntamento()));
		ygAdesione.setDeProvincia(deProvinciaHome.findById(utenteCompletoDTO.getProvinciaRiferimento().getId()));
		ygAdesione.setDeCpiAssegnazione(deCpiHome.findById(datiAppuntamento.getIdCPI()));
		ygAdesione.setFlgPresoInCarico(true);

		return merge(ygAdesione);
	}
	
	public List<YgAdesione> findByCodStatoAdesioneMinWithConstraint(Integer idPfPrincipal, String codStatoAdesioneMin, Date dateConstraint){
		List<YgAdesione> ygAdesiones = entityManager
				.createNamedQuery("findByCodStatoAdesioneMinWithConstraintInDtAdesione", YgAdesione.class)
				.setParameter("idPfPrincipal", idPfPrincipal)
				.setParameter("codStatoAdesioneMin", codStatoAdesioneMin)
				.setParameter("dtAdesione", dateConstraint).getResultList();
		return ygAdesiones;
	}
	
	public List<YgAdesioneDTO> findDTOByCodStatoAdesioneMinAndDateConstraint(Integer idPfPrincipal, String codStatoAdesioneMin, Date dateConstraint){
		List<YgAdesione> ygAdesiones = findByCodStatoAdesioneMinWithConstraint(idPfPrincipal, codStatoAdesioneMin, dateConstraint);
		List<YgAdesioneDTO> ygAdesioneDTOs = new ArrayList<YgAdesioneDTO>();
		for(YgAdesione current: ygAdesiones){
			ygAdesioneDTOs.add(toDTO(current));			
		}
		return ygAdesioneDTOs;
	}
	
}
