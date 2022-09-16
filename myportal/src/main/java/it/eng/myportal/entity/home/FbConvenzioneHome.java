package it.eng.myportal.entity.home;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import it.eng.myportal.dtos.FbConvenzioneDTO;
import it.eng.myportal.dtos.FbConvenzioneFilterDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.AziendaInfo_;
import it.eng.myportal.entity.FbConvenzione;
import it.eng.myportal.entity.FbConvenzione_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbConvenzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoFbConvenzioneHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

@Stateless
public class FbConvenzioneHome extends AbstractUpdatableHome<FbConvenzione, FbConvenzioneDTO> {
	protected final Log log = LogFactory.getLog(FbConvenzioneHome.class);

	@EJB
	private DeStatoFbConvenzioneHome deStatoConvenzioneHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	private DeTipoFbConvenzioneHome deTipoConvenzioneHome;

	@Override
	public FbConvenzione findById(Integer id) {
		return findById(FbConvenzione.class, id);
	}

	@Override
	public FbConvenzione fromDTO(FbConvenzioneDTO dto) {
		if (dto == null) {
			return null;
		}

		FbConvenzione entity = super.fromDTO(dto);
		entity.setIdConvenzione(dto.getId());
		entity.setDataStipula(dto.getDataStipula());
		entity.setNomeConvenzione(dto.getNomeConvenzione());
		entity.setDataScadenza(dto.getDataScadenza());
		entity.setNumProtocollo(dto.getNumProtocollo());
		entity.setDataProtocollo(dto.getDataProtocollo());
		entity.setIdPrincipalProtocollo(dto.getIdPrincipalProtocollo());
		entity.setDataRevoca(dto.getDataRevoca());
		entity.setMotivoRevoca(dto.getMotivoRevoca());
		entity.setIdPrincipalRevoca(dto.getIdPrincipalRevoca());
		entity.setNomeLegaleRappresentante(dto.getNomeLegaleRappresentante());
		entity.setCognomeLegaleRappresentante(dto.getCognomeLegaleRappresentante());
		entity.setDataProtocollazione(dto.getDataProtocollazione());

		if (dto.getCodStatoConv() != null && dto.getCodStatoConv().getId() != null)
			entity.setCodStatoConv(deStatoConvenzioneHome.findById(dto.getCodStatoConv().getId()));
		if (dto.getCodTipoConvenzione() != null && dto.getCodTipoConvenzione().getId() != null)
			entity.setCodTipoConvenzione(deTipoConvenzioneHome.findById(dto.getCodTipoConvenzione().getId()));

		return entity;
	}

	@Override
	public FbConvenzioneDTO toDTO(FbConvenzione entity) {
		if (entity == null) {
			return null;
		}

		FbConvenzioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdConvenzione());
		dto.setDataStipula(entity.getDataStipula());
		dto.setNomeConvenzione(entity.getNomeConvenzione());
		dto.setDataScadenza(entity.getDataScadenza());
		dto.setCodStatoConv(deStatoConvenzioneHome.toDTO(entity.getCodStatoConv()));
		dto.setCodTipoConvenzione(deTipoConvenzioneHome.toDTO(entity.getCodTipoConvenzione()));
		dto.setDataProtocollo(entity.getDataProtocollo());
		dto.setIdPrincipalProtocollo(entity.getIdPrincipalProtocollo());
		dto.setNumProtocollo(entity.getNumProtocollo());
		dto.setDataRevoca(entity.getDataRevoca());
		dto.setMotivoRevoca(entity.getMotivoRevoca());
		dto.setIdPrincipalRevoca(entity.getIdPrincipalRevoca());
		dto.setNomeLegaleRappresentante(entity.getNomeLegaleRappresentante());
		dto.setCognomeLegaleRappresentante(entity.getCognomeLegaleRappresentante());
		dto.setDataProtocollazione(entity.getDataProtocollazione());

		if (entity.getPfPrincipalIns() != null && entity.getPfPrincipalIns().getAziendaInfo() != null) {
			dto.setRagioneSociale(entity.getPfPrincipalIns().getAziendaInfo().getRagioneSociale());
			dto.setCodiceFiscale(entity.getPfPrincipalIns().getAziendaInfo().getCodiceFiscale());
		}

		return dto;
	}

	public List<FbConvenzioneDTO> findByIdPrincipal(Integer pfPrincipalId) {
		List<FbConvenzioneDTO> dto = new ArrayList<FbConvenzioneDTO>();
		TypedQuery<FbConvenzione> queryConvenzione = entityManager.createNamedQuery("findConvenzioneByPfPrincipalId",
				FbConvenzione.class);
		queryConvenzione.setParameter("pfPrincipalId", pfPrincipalId);
		List<FbConvenzione> queryResults = queryConvenzione.getResultList();
		for (FbConvenzione conv : queryResults) {
			dto.add(toDTO(conv));
		}
		return dto;
	}

	public List<FbConvenzioneDTO> findConvenzioneByFilter(FbConvenzioneFilterDTO filter, String codeci) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FbConvenzione> query = cb.createQuery(FbConvenzione.class);
		Root<FbConvenzione> root = query.from(FbConvenzione.class);
		query.select(root);
		query.where(getDTOByFilterCriteria(cb, root, filter, codeci));
		query.orderBy(cb.desc(root.get(FbConvenzione_.dataStipula)));

		TypedQuery<FbConvenzione> typedQuery = entityManager.createQuery(query);
		List<FbConvenzione> searchResult = typedQuery.getResultList();
		List<FbConvenzioneDTO> dtoList = new ArrayList<FbConvenzioneDTO>(searchResult.size());
		for (FbConvenzione entity : searchResult) {
			dtoList.add(toDTO(entity));
		}

		return dtoList;
	}

	private Predicate getDTOByFilterCriteria(CriteriaBuilder cb, Root<FbConvenzione> root,
			FbConvenzioneFilterDTO filter, String codeci) {
		Predicate perdicate = null;

		List<Predicate> wherePredicates = new ArrayList<Predicate>();
		Join<FbConvenzione, PfPrincipal> pfPrincipalJoin = root.join(FbConvenzione_.pfPrincipalIns);
		Join<PfPrincipal, AziendaInfo> aziendiaInfoJoin = pfPrincipalJoin.join(PfPrincipal_.aziendaInfo);

		if (codeci != null) {
			wherePredicates
					.add(cb.equal(aziendiaInfoJoin.get(AziendaInfo_.codiceFiscale), codeci.trim().toUpperCase()));
		}

		if (filter.getSoggettoPromotore() != null) {
			wherePredicates.add(cb.or(
					cb.like(cb.upper(aziendiaInfoJoin.get(AziendaInfo_.ragioneSociale)),
							"%" + filter.getSoggettoPromotore().trim().toUpperCase() + "%"),
					cb.like(cb.upper(aziendiaInfoJoin.get(AziendaInfo_.codiceFiscale)),
							"%" + filter.getSoggettoPromotore().trim().toUpperCase() + "%")));
		}

		if (filter.getFilterDeTipoConvenzione() != null) {
			wherePredicates.add(cb.equal(root.get(FbConvenzione_.codTipoConvenzione),
					deTipoConvenzioneHome.fromDTO(filter.getFilterDeTipoConvenzione())));
		}

		if (filter.getDataStipulaDa() != null) {
			wherePredicates
					.add(cb.greaterThanOrEqualTo(root.get(FbConvenzione_.dataStipula), filter.getDataStipulaDa()));

		}

		if (filter.getDataStipulaA() != null) {
			wherePredicates.add(cb.lessThanOrEqualTo(root.get(FbConvenzione_.dataStipula),
					getFineGiornata(filter.getDataStipulaA())));

		}

		if (filter.getDataRepertorioDa() != null) {
			wherePredicates.add(
					cb.greaterThanOrEqualTo(root.get(FbConvenzione_.dataProtocollo), filter.getDataRepertorioDa()));

		}

		if (filter.getDataRepertorioA() != null) {
			wherePredicates.add(cb.lessThanOrEqualTo(root.get(FbConvenzione_.dataProtocollo),
					getFineGiornata(filter.getDataRepertorioA())));

		}

		if (filter.getNumeroRepertorio() != null) {
			wherePredicates.add(cb.equal(root.get(FbConvenzione_.numProtocollo), filter.getNumeroRepertorio()));
		}

		// we NEVER want "in lavorazione"
		wherePredicates.add(cb.notEqual(root.get(FbConvenzione_.codStatoConv), deStatoConvenzioneHome.findById("LAV")));
		if (filter.getFilterDeStatoConvenzione() != null) {
			wherePredicates.add(cb.equal(root.get(FbConvenzione_.codStatoConv),
					deStatoConvenzioneHome.fromDTO(filter.getFilterDeStatoConvenzione())));
		}

		if (wherePredicates.size() > 0) {
			perdicate = cb.and(wherePredicates.toArray(new Predicate[wherePredicates.size()]));
		}

		return perdicate;
	}

	private Date getFineGiornata(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	/** Conta il numero di convenzioni attive (non scadute) per un certo utente e un certo tipo */
	public int countConvenzioniAttiveByIdPrincipalAndStato(Integer idPfPrincipal, String codStatoConvenzione) {
		TypedQuery<Long> query = entityManager.createNamedQuery("countConvenzioniAttiveByIdPrincipalAndStato",
				Long.class);
		query.setParameter("idPfPrincipal", idPfPrincipal);
		query.setParameter("codStatoProt", codStatoConvenzione);
		query.setParameter("currentDate", new Date());
		return query.getSingleResult().intValue();
	}

	/** Conta il numero di convenzioni attive (non scadute nè revocate) per un certo utente */
	public int countConvenzioniAttiveByIdPrincipalAndTipo(Integer idPfPrincipal, String codTipoConvenzione) {
		TypedQuery<Long> query = entityManager.createNamedQuery("countConvenzioniAttiveByIdPrincipalAndTipo",
				Long.class);
		query.setParameter("idPfPrincipal", idPfPrincipal);
		query.setParameter("codTipoConvenzione", codTipoConvenzione);
		query.setParameter("codStatoRevocata", ConstantsSingleton.DeStatoFbConvenzione.REVOCATA);
		query.setParameter("currentDate", new Date());
		return query.getSingleResult().intValue();
	}

	public InputStream getStampaHtmlFile(Integer idFbConvenzione) {
		FbConvenzione convenzione = findById(idFbConvenzione);
		String result = null;
		WebClient webClient = new WebClient();
		webClient.getOptions().setJavaScriptEnabled(false);
		Page requestedPage = null;
		String nomeImmagine = null;

		try {
			if (convenzione.getCodTipoConvenzione().getCodTipoConvenzione()
					.equals(ConstantsSingleton.DeTipoFbConvenzione.MULTIMISURA)) {
				requestedPage = webClient
						.getPage(ConstantsSingleton.BASE_URL
								+ "/faces/public/print/convenzioni_calabria/convenzione_multimisura.xhtml"
								+ "?idFbConvenzione=" + idFbConvenzione);
				nomeImmagine = "fb_convenzione_header.png";
			} else if (convenzione.getCodTipoConvenzione().getCodTipoConvenzione()
					.equals(ConstantsSingleton.DeTipoFbConvenzione.TIROCINI)) {
				requestedPage = webClient
						.getPage(ConstantsSingleton.BASE_URL
								+ "/faces/public/print/convenzioni_calabria/convenzione_tirocini.xhtml"
								+ "?idFbConvenzione=" + idFbConvenzione);
				nomeImmagine = "fb_convenzione_header.png";
			} else {
				requestedPage = webClient.getPage(ConstantsSingleton.BASE_URL
						+ ConstantsSingleton.CONTESTO_APP + "/faces/public/print/convenzioni_calabria/avviso_dote.xhtml"
						+ "?idFbConvenzione=" + idFbConvenzione);
				nomeImmagine = "fb_convenzione_dote_header.png";
			}
		} catch (Exception e) {
			String errMsg = "Errore durante il recupero del documento XML: convenzioni_calabria/convenzione_multimisura.xhtml. idFbConvenzione = "
					+ idFbConvenzione;
			log.error(errMsg);
			log.error("Eccezione originale: " + e.getClass() + " - " + e.getLocalizedMessage());
			throw new MyPortalException(errMsg, e, true);
		}

		if (requestedPage != null) {
			if (requestedPage instanceof XmlPage) {
				result = ((XmlPage) requestedPage).asXml();
			} else if (requestedPage instanceof HtmlPage) {
				result = ((HtmlPage) requestedPage).asXml();
			}
		}

		return htmlToPDF(result, nomeImmagine);
	}

	private InputStream htmlToPDF(String html, String nomeImmagine) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, output);
			document.setPageSize(PageSize.A4);
			document.setMargins(10, 10, 15, 10);
			document.open();

			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String imagePath = externalContext.getRealPath("/resources/images/calabria/" + nomeImmagine);
			Image img = Image.getInstance(imagePath);
			img.scalePercent(50f);// W i numeri magici
			document.add(img);

			StringBuilder HTMLtoPDF = new StringBuilder();
			HTMLtoPDF.append(Utils.PDF.htmltoXHTML(html));

			InputStream stream = new ByteArrayInputStream(HTMLtoPDF.toString().getBytes("UTF-8"));
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, stream);
			document.close();
		} catch (DocumentException e) {
			log.error("DocumentException Error generating pdf from HTML:" + e);
		} catch (Exception e) {
			log.error("DocumentException Error generating pdf from HTML:" + e);
		}
		byte[] data = output.toByteArray();
		return new ByteArrayInputStream(data);
	}

}
