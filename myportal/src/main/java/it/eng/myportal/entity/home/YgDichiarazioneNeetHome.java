package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.YgDichiarazioneNeetDTO;
import it.eng.myportal.dtos.YgDichiarazioneNeetFilterDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.YgDichiarazioneNeet;
import it.eng.myportal.entity.YgDichiarazioneNeet_;
import it.eng.myportal.entity.enums.YgDichiarazioneNeetStatoEnum;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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

@Stateless
public class YgDichiarazioneNeetHome extends AbstractUpdatableHome<YgDichiarazioneNeet, YgDichiarazioneNeetDTO> {

	protected final Log log = LogFactory.getLog(YgDichiarazioneNeetHome.class);

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	public YgDichiarazioneNeet fromDTO(YgDichiarazioneNeetDTO dto) {
		if (dto == null) {
			return null;
		}
		YgDichiarazioneNeet entity = super.fromDTO(dto);

		entity.setIdYgDichiarazioneNeet(dto.getId());
		entity.setDtDichiarazione(dto.getDtDichiarazione());
		entity.setStrCodiceFiscaleLav(dto.getStrCodiceFiscaleLav());
		entity.setStrNomeLav(dto.getStrNomeLav());
		entity.setStrCognomeLav(dto.getStrCognomeLav());
		entity.setDtNascitaLav(dto.getDtNascitaLav());
		entity.setStrCodiceFiscaleEnte(dto.getStrCodiceFiscaleEnte());
		entity.setStrRagioneSocialeEnte(dto.getStrRagioneSocialeEnte());
		entity.setExtDocumentoFileName(dto.getExtDocumentoFileName());
		entity.setExtDocumentoFile(dto.getExtDocumentoFile());
		entity.setExtDocumentoFileMimeType(dto.getExtDocumentoFileMimeType());
		entity.setFlgCancellata(dto.getFlgCancellata());
		entity.setOpzAutocertDisocc(dto.getOpzAutocertDisocc());
		entity.setOpzAutocertIstrForm(dto.getOpzAutocertIstrForm());
		entity.setOpzAutocertStatoIstr(dto.getOpzAutocertStatoIstr());
		entity.setOpzAutocertStatoOcc(dto.getOpzAutocertStatoOcc());
		entity.setOpzComObbl(dto.getOpzComObbl());
		entity.setOpzDisoccupato(dto.getOpzDisoccupato());
		entity.setOpzFormazione(dto.getOpzFormazione());
		entity.setOpzIstruzione(dto.getOpzIstruzione());
		entity.setOpzLegge150(dto.getOpzLegge150());
		entity.setStrNoteAutocertDisocc(dto.getStrNoteAutocertDisocc());
		entity.setStrNoteAutocertIstrForm(dto.getStrNoteAutocertIstrForm());
		entity.setStrNoteAutocertStatoIstr(dto.getStrNoteAutocertStatoIstr());
		entity.setStrNoteAutocertStatoOcc(dto.getStrNoteAutocertStatoOcc());
		entity.setStrNoteComObbl(dto.getStrNoteComObbl());
		entity.setStrNoteDisoccupato(dto.getStrNoteDisoccupato());
		entity.setStrNoteFormazione(dto.getStrNoteFormazione());
		entity.setStrNoteIstruzione(dto.getStrNoteIstruzione());
		entity.setStrNoteLegge150(dto.getStrNoteLegge150());
		entity.setExtUploadNeetFileName(dto.getExtUploadNeetFileName());
		entity.setExtUploadNeetFile(dto.getExtUploadNeetFile());
		entity.setExtUploadNeetFileMimeType(dto.getExtUploadNeetFileMimeType());

		if (dto.getDeComuneNascitaDTO() != null)
			entity.setDeComuneNascita(deComuneHome.findById(dto.getDeComuneNascitaDTO().getId()));

		return entity;

	}

	public YgDichiarazioneNeetDTO toDTO(YgDichiarazioneNeet entity) {
		if (entity == null) {
			return null;
		}
		YgDichiarazioneNeetDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdYgDichiarazioneNeet());
		dto.setDtDichiarazione(entity.getDtDichiarazione());
		dto.setStrCodiceFiscaleLav(entity.getStrCodiceFiscaleLav());
		dto.setStrNomeLav(entity.getStrNomeLav());
		dto.setStrCognomeLav(entity.getStrCognomeLav());
		dto.setDtNascitaLav(entity.getDtNascitaLav());
		dto.setStrCodiceFiscaleEnte(entity.getStrCodiceFiscaleEnte());
		dto.setStrRagioneSocialeEnte(entity.getStrRagioneSocialeEnte());
		dto.setExtDocumentoFileName(entity.getExtDocumentoFileName());
		dto.setExtDocumentoFile(entity.getExtDocumentoFile());
		dto.setExtDocumentoFileMimeType(entity.getExtDocumentoFileMimeType());
		dto.setFlgCancellata(entity.getFlgCancellata());
		dto.setStrDescrizioneStatoDichiarazione(entity.getStatoDichiarazione().getDescrizione());
		dto.setOpzAutocertDisocc(entity.getOpzAutocertDisocc());
		dto.setOpzAutocertIstrForm(entity.getOpzAutocertIstrForm());
		dto.setOpzAutocertStatoIstr(entity.getOpzAutocertStatoIstr());
		dto.setOpzAutocertStatoOcc(entity.getOpzAutocertStatoOcc());
		dto.setOpzComObbl(entity.getOpzComObbl());
		dto.setOpzDisoccupato(entity.getOpzDisoccupato());
		dto.setOpzFormazione(entity.getOpzFormazione());
		dto.setOpzIstruzione(entity.getOpzIstruzione());
		dto.setOpzLegge150(entity.getOpzLegge150());
		dto.setStrNoteAutocertDisocc(entity.getStrNoteAutocertDisocc());
		dto.setStrNoteAutocertIstrForm(entity.getStrNoteAutocertIstrForm());
		dto.setStrNoteAutocertStatoIstr(entity.getStrNoteAutocertStatoIstr());
		dto.setStrNoteAutocertStatoOcc(entity.getStrNoteAutocertStatoOcc());
		dto.setStrNoteComObbl(entity.getStrNoteComObbl());
		dto.setStrNoteDisoccupato(entity.getStrNoteDisoccupato());
		dto.setStrNoteFormazione(entity.getStrNoteFormazione());
		dto.setStrNoteIstruzione(entity.getStrNoteIstruzione());
		dto.setStrNoteLegge150(entity.getStrNoteLegge150());
		dto.setExtUploadNeetFileName(entity.getExtUploadNeetFileName());
		dto.setExtUploadNeetFile(entity.getExtUploadNeetFile());
		dto.setExtUploadNeetFileMimeType(entity.getExtUploadNeetFileMimeType());

		if (entity.getDeComuneNascita() != null)
			dto.setDeComuneNascitaDTO(deComuneHome.toDTO(entity.getDeComuneNascita()));

		return dto;
	}

	@Override
	public YgDichiarazioneNeet findById(Integer id) {
		return findById(YgDichiarazioneNeet.class, id);
	}

	public YgDichiarazioneNeetDTO findDTObyId(int id) {
		return toDTO(findById(id));
	}

	private Predicate getFindDTOByFilterPredicate(CriteriaBuilder cb, Root<YgDichiarazioneNeet> ygDichiarazioneNeet,
			YgDichiarazioneNeetFilterDTO filter) {
		Predicate res = null;

		List<Predicate> preds = new ArrayList<Predicate>();
		if (filter.getIdPrincipalAzienda() != null) {
			PfPrincipal pfPrincipalAzienda = pfPrincipalHome.findById(filter.getIdPrincipalAzienda());
			preds.add(cb.equal(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.pfPrincipalIns), pfPrincipalAzienda));
		}

		if (filter.getIdDichiarazione() != null && filter.getIdDichiarazione() != 0) {
			preds.add(cb.equal(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.idYgDichiarazioneNeet),
					filter.getIdDichiarazione()));
		}

		if (filter.getNome() != null && !filter.getNome().isEmpty()) {
			preds.add(cb.like(cb.upper(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.strNomeLav)), "%"
					+ filter.getNome().toUpperCase() + "%"));
		}

		if (filter.getCognome() != null && !filter.getCognome().isEmpty()) {
			preds.add(cb.like(cb.upper(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.strCognomeLav)), "%"
					+ filter.getCognome().toUpperCase() + "%"));
		}

		if (filter.getCodiceFiscale() != null && !filter.getCodiceFiscale().isEmpty()) {
			preds.add(cb.like(cb.upper(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.strCodiceFiscaleLav)), "%"
					+ filter.getCodiceFiscale().toUpperCase() + "%"));
		}

		if (filter.getCodiceFiscaleEnte() != null && !filter.getCodiceFiscaleEnte().isEmpty()) {
			preds.add(cb.like(cb.upper(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.strCodiceFiscaleEnte)), "%"
					+ filter.getCodiceFiscaleEnte().toUpperCase() + "%"));
		}

		if (filter.getDataDa() != null && filter.getDataA() != null) {
			preds.add(cb.between(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.dtDichiarazione), filter.getDataDa(),
					filter.getDataA()));
		} else if (filter.getDataDa() != null) {
			preds.add(cb.greaterThanOrEqualTo(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.dtDichiarazione),
					filter.getDataDa()));
		} else if (filter.getDataA() != null) {
			preds.add(cb.lessThanOrEqualTo(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.dtDichiarazione),
					filter.getDataA()));
		}

		if (filter.getStato() != null) {
			if (filter.getStato().equals(YgDichiarazioneNeetStatoEnum.COMPLETO)) {
				preds.add(cb.and(cb.equal(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.flgCancellata), false),
						cb.isNotNull(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.extDocumentoFile))));
			} else if (filter.getStato().equals(YgDichiarazioneNeetStatoEnum.INCOMPLETO)) {
				preds.add(cb.and(cb.equal(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.flgCancellata), false),
						cb.isNull(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.extDocumentoFile))));
			} else {
				preds.add(cb.isTrue(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.flgCancellata)));
			}
		}

		if (preds.size() > 0) {
			res = cb.and(preds.toArray(new Predicate[preds.size()]));
		}

		return res;
	}

	public List<YgDichiarazioneNeetDTO> findDTOByFilter(YgDichiarazioneNeetFilterDTO filter) {
		List<YgDichiarazioneNeetDTO> res = new ArrayList<YgDichiarazioneNeetDTO>();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<YgDichiarazioneNeet> query = cb.createQuery(YgDichiarazioneNeet.class);
		Root<YgDichiarazioneNeet> ygDichiarazioneNeet = query.from(YgDichiarazioneNeet.class);

		Predicate searchPred = getFindDTOByFilterPredicate(cb, ygDichiarazioneNeet, filter);
		if (searchPred != null) {
			query.where(searchPred);
		}
		query.orderBy(cb.desc(ygDichiarazioneNeet.get(YgDichiarazioneNeet_.dtDichiarazione)));

		TypedQuery<YgDichiarazioneNeet> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult(filter.getFirst());
		typedQuery.setMaxResults(filter.getPageSize());
		List<YgDichiarazioneNeet> list = typedQuery.getResultList();
		for (YgDichiarazioneNeet elem : list) {
			res.add(toDTO(elem));
		}

		return res;
	}

	public Long findDTOByFilterCount(YgDichiarazioneNeetFilterDTO filter) {
		Long res = new Long(0);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<YgDichiarazioneNeet> ygDichiarazioneNeet = query.from(YgDichiarazioneNeet.class);

		Predicate searchPred = getFindDTOByFilterPredicate(cb, ygDichiarazioneNeet, filter);
		if (searchPred != null) {
			query.where(searchPred);
		}

		query.select(cb.count(ygDichiarazioneNeet));
		TypedQuery<Long> typedQuery = entityManager.createQuery(query);
		res = typedQuery.getSingleResult();

		return res;
	}

	public void persistDTO(Integer idPfPrincipal, YgDichiarazioneNeetDTO ygDichiarazioneNeetDTO) {
		Date now = Calendar.getInstance().getTime();
		ygDichiarazioneNeetDTO.setDtmIns(now);
		ygDichiarazioneNeetDTO.setDtmMod(now);
		ygDichiarazioneNeetDTO.setIdPrincipalIns(idPfPrincipal);
		ygDichiarazioneNeetDTO.setIdPrincipalMod(idPfPrincipal);
		ygDichiarazioneNeetDTO.setStrCodiceFiscaleEnte(ygDichiarazioneNeetDTO.getStrCodiceFiscaleEnte().toUpperCase());
		ygDichiarazioneNeetDTO.setStrCodiceFiscaleLav(ygDichiarazioneNeetDTO.getStrCodiceFiscaleLav().toUpperCase());
		persist(fromDTO(ygDichiarazioneNeetDTO));
	}

	public YgDichiarazioneNeetDTO mergeDTO(Integer idPfPrincipal, YgDichiarazioneNeetDTO ygDichiarazioneNeetDTO) {
		Date now = Calendar.getInstance().getTime();
		ygDichiarazioneNeetDTO.setDtmMod(now);
		ygDichiarazioneNeetDTO.setIdPrincipalMod(idPfPrincipal);
		return toDTO(merge(fromDTO(ygDichiarazioneNeetDTO)));
	}

	public StreamedContent createStampaPdfFile(Integer idYgDichiarazioneNeet) {
		YgDichiarazioneNeet ygDichiarazioneNeet = findById(idYgDichiarazioneNeet);
		String stampaHtmlFile = getStampaHtmlFile(idYgDichiarazioneNeet);
		InputStream pdfStream = htmlToPDF(stampaHtmlFile);
		StreamedContent result = new DefaultStreamedContent(pdfStream, "application/pdf", "Dichiarazione_NEET_"
				+ ygDichiarazioneNeet.getStrCodiceFiscaleLav() + ".pdf");
		return result;
	}

	private String getStampaHtmlFile(Integer idYgDichiarazioneNeet) {
		String result = null;
		WebClient webClient = new WebClient();
		webClient.getOptions().setJavaScriptEnabled(false);
		Page requestedPage = null;

		try {
			requestedPage = webClient.getPage(ConstantsSingleton.BASE_URL
					+ "/faces/public/print/yg/dichiarazione_neet.xhtml" + "?idYgDichiarazioneNeet="
					+ idYgDichiarazioneNeet);
		} catch (Exception e) {
			String errMsg = "Errore durante il recupero del documento XML: yg/dichiarazione_neet.xhtml. idYgDichiarazioneNeet = "
					+ idYgDichiarazioneNeet;
			log.error(errMsg);
			log.error("Eccezione originale: " + e.getClass() + " - " + e.getLocalizedMessage());
			throw new MyPortalException(errMsg, e, true);
		}
		if (requestedPage != null) {
			if (requestedPage instanceof HtmlPage) {
				result = ((HtmlPage) requestedPage).asXml();
			} else if (requestedPage instanceof XmlPage) {
				result = ((XmlPage) requestedPage).asXml();
			}
		}

		return result;
	}

	private InputStream htmlToPDF(String html) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, output);
			document.setPageSize(PageSize.A4);
			document.setMargins(10, 10, 15, 10);
			document.open();

			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String imagePath = externalContext.getRealPath("/resources/images/rer/gg.jpg");
			Image img = Image.getInstance(imagePath);
			img.scalePercent(35f);
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
