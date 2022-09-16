package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.ConferimentoDidDTO;
import it.eng.myportal.entity.ConferimentoDid;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeCondizioneOccupazMinHome;
import it.eng.myportal.entity.home.decodifiche.DeIscrizioneCorsoMinHome;
import it.eng.myportal.entity.home.decodifiche.DePosizioneProfessionaleMinHome;
import it.eng.myportal.entity.home.decodifiche.DePresenzaItaliaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

@Stateless
public class ConferimentoDidHome extends AbstractUpdatableHome<ConferimentoDid, ConferimentoDidDTO> {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeCittadinanzaMinHome deCittadinanzaMinHome;

	@EJB
	DePresenzaItaliaMinHome dePresenzaItaliaMinHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeCondizioneOccupazMinHome deCondizioneOccupazMinHome;

	@EJB
	DePosizioneProfessionaleMinHome dePosizioneProfessionaleMinHome;

	@EJB
	DeIscrizioneCorsoMinHome deIscrizioneCorsoMinHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@Override
	public ConferimentoDid findById(Integer id) {
		return findById(ConferimentoDid.class, id);
	}

	@Override
	public ConferimentoDid fromDTO(ConferimentoDidDTO dto) {
		if (dto == null) {
			return null;
		}

		ConferimentoDid entity = super.fromDTO(dto);
		entity.setIdConferimentoDid(dto.getId());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));
		entity.setDataDid(dto.getDataDid());
		entity.setOpzTipoEvento(dto.getOpzTipoEvento());
		entity.setCodEntePromotore(dto.getCodEntePromotore());
		entity.setDataEvento(dto.getDataEvento());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setEta(dto.getEta());
		entity.setOpzGenere(dto.getOpzGenere());
		entity.setFlgEsperienzaLavoro(dto.getFlgEsperienzaLavoro());
		entity.setNumMesiRapporto(dto.getNumMesiRapporto());
		entity.setNumMesiRicercaLavoro(dto.getNumMesiRicercaLavoro());
		entity.setNumComponentiFamiglia(dto.getNumComponentiFamiglia());
		entity.setFlgFigliCarico(dto.getFlgFigliCarico());
		entity.setFlgFigliMinoriCarico(dto.getFlgFigliMinoriCarico());
		entity.setCondizioneOccupazCalc(dto.getCondizioneOccupazCalc());
		entity.setDurataDisoccupazCalc(dto.getDurataDisoccupazCalc());
		entity.setIdProfiling(dto.getIdProfiling());
		entity.setDataInserimento(dto.getDataInserimento());
		entity.setProbabilita(dto.getProbabilita());

		if (dto.getDeTitoloDTO() != null && dto.getDeTitoloDTO().getId() != null)
			entity.setDeTitolo(deTitoloHome.findById(dto.getDeTitoloDTO().getId()));

		if (dto.getDeCittadinanzaMinDTO() != null && dto.getDeCittadinanzaMinDTO().getId() != null)
			entity.setDeCittadinanzaMin(deCittadinanzaMinHome.findById(dto.getDeCittadinanzaMinDTO().getId()));

		if (dto.getDePresenzaItaliaMinDTO() != null && dto.getDePresenzaItaliaMinDTO().getId() != null)
			entity.setDePresenzaItaliaMin(dePresenzaItaliaMinHome.findById(dto.getDePresenzaItaliaMinDTO().getId()));

		if (dto.getDeProvinciaResidenzaDTO() != null && dto.getDeProvinciaResidenzaDTO().getId() != null)
			entity.setDeProvinciaResidenza(deProvinciaHome.findById(dto.getDeProvinciaResidenzaDTO().getId()));

		if (dto.getDeCondizioneOccupazMinDTO() != null && dto.getDeCondizioneOccupazMinDTO().getId() != null)
			entity.setDeCondizioneOccupazMin(deCondizioneOccupazMinHome.findById(dto.getDeCondizioneOccupazMinDTO()
					.getId()));

		if (dto.getDePosizioneProfessionaleMinDTO() != null && dto.getDePosizioneProfessionaleMinDTO().getId() != null)
			entity.setDePosizioneProfessionaleMin(dePosizioneProfessionaleMinHome.findById(dto
					.getDePosizioneProfessionaleMinDTO().getId()));

		if (dto.getDeIscrizioneCorsoMinDTO() != null && dto.getDeIscrizioneCorsoMinDTO().getId() != null)
			entity.setDeIscrizioneCorsoMin(deIscrizioneCorsoMinHome.findById(dto.getDeIscrizioneCorsoMinDTO().getId()));

		return entity;
	}

	@Override
	public ConferimentoDidDTO toDTO(ConferimentoDid entity) {
		if (entity == null) {
			return null;
		}

		ConferimentoDidDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdConferimentoDid());
		dto.setIdPfPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		dto.setDataDid(entity.getDataDid());
		dto.setOpzTipoEvento(entity.getOpzTipoEvento());
		dto.setCodEntePromotore(entity.getCodEntePromotore());
		dto.setDataEvento(entity.getDataEvento());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setEta(entity.getEta());
		dto.setOpzGenere(entity.getOpzGenere());
		dto.setFlgEsperienzaLavoro(entity.getFlgEsperienzaLavoro());
		dto.setNumMesiRapporto(entity.getNumMesiRapporto());
		dto.setNumMesiRicercaLavoro(entity.getNumMesiRicercaLavoro());
		dto.setNumComponentiFamiglia(entity.getNumComponentiFamiglia());
		dto.setFlgFigliCarico(entity.getFlgFigliCarico());
		dto.setFlgFigliMinoriCarico(entity.getFlgFigliMinoriCarico());
		dto.setCondizioneOccupazCalc(entity.getCondizioneOccupazCalc());
		dto.setDurataDisoccupazCalc(entity.getDurataDisoccupazCalc());
		dto.setProbabilita(entity.getProbabilita());
		dto.setIdProfiling(entity.getIdProfiling());
		dto.setDataInserimento(entity.getDataInserimento());

		if (entity.getDeTitolo() != null)
			dto.setDeTitoloDTO(deTitoloHome.toDTO(entity.getDeTitolo()));

		if (entity.getDeCittadinanzaMin() != null)
			dto.setDeCittadinanzaMinDTO(deCittadinanzaMinHome.toDTO(entity.getDeCittadinanzaMin()));

		if (entity.getDePresenzaItaliaMin() != null)
			dto.setDePresenzaItaliaMinDTO(dePresenzaItaliaMinHome.toDTO(entity.getDePresenzaItaliaMin()));

		if (entity.getDeProvinciaResidenza() != null)
			dto.setDeProvinciaResidenzaDTO(deProvinciaHome.toDTO(entity.getDeProvinciaResidenza()));

		if (entity.getDeCondizioneOccupazMin() != null)
			dto.setDeCondizioneOccupazMinDTO(deCondizioneOccupazMinHome.toDTO(entity.getDeCondizioneOccupazMin()));

		if (entity.getDePosizioneProfessionaleMin() != null)
			dto.setDePosizioneProfessionaleMinDTO(dePosizioneProfessionaleMinHome.toDTO(entity
					.getDePosizioneProfessionaleMin()));

		if (entity.getDeIscrizioneCorsoMin() != null)
			dto.setDeIscrizioneCorsoMinDTO(deIscrizioneCorsoMinHome.toDTO(entity.getDeIscrizioneCorsoMin()));

		return dto;
	}

	public List<ConferimentoDid> findByIdPfPrincipal(Integer idPfPrincipal) {
		return entityManager.createNamedQuery("findConferimentoDidByIdPrincipal", ConferimentoDid.class)
				.setParameter("idPfPrincipal", idPfPrincipal).getResultList();
	}

	public List<ConferimentoDidDTO> findDTOByIdPfPrincipal(Integer idPfPrincipal) {
		List<ConferimentoDid> entityList = findByIdPfPrincipal(idPfPrincipal);
		List<ConferimentoDidDTO> result = new ArrayList<ConferimentoDidDTO>(entityList.size());
		for (ConferimentoDid entity : entityList) {
			result.add(toDTO(entity));
		}
		return result;
	}

	public InputStream getStampaHtmlFile(Integer idConferimentoDid) {
		String result = null;
		WebClient webClient = new WebClient();
		webClient.getOptions().setJavaScriptEnabled(false);
		Page requestedPage = null;

		try {
			requestedPage = webClient.getPage(ConstantsSingleton.BASE_URL
					+ "/faces/public/print/did/conferimento_did_anpal.xhtml" + "?idConferimentoDid="
					+ idConferimentoDid);
		} catch (Exception e) {
			String errMsg = "Errore durante il recupero del documento XML: did/conferimento_did_anpal.xhtml. idConferimentoDid = "
					+ idConferimentoDid;
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

		return htmlToPDF(result);
	}

	private InputStream htmlToPDF(String html) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, output);
			document.setPageSize(PageSize.A4);
			document.setMargins(10, 10, 15, 10);
			document.open();

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
