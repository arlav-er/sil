package it.eng.myportal.rest.fileManager;

import it.eng.myportal.dtos.AcAllegatoDTO;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.CvAllegatoDTO;
import it.eng.myportal.dtos.MsgAllegatoDTO;
import it.eng.myportal.dtos.SvImmagineDTO;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.entity.home.AcAllegatoHome;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.CvAllegatoHome;
import it.eng.myportal.entity.home.MsgAllegatoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.SvImmagineHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.FileUtils;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
@Path("/rest")
public class GetFoto {

	private static final String CONTENT_DISPOSITION = "Content-Disposition";

	protected Log log = LogFactory.getLog(this.getClass());

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	AcAllegatoHome acAllegatoHome;

	@EJB
	CvAllegatoHome cvAllegatoHome;
	@EJB
	MsgAllegatoHome msgAllegatoHome;

	@EJB
	SvImmagineHome svImmagineHome;

	/**
	 * Metodo che restituisce la foto del cittadino o il logo dell'azienda, in
	 * base a quale tipologia di utente invoca il servizio. Se l'utente non ha
	 * inserito alcuna immagine allora viene restituita un'immagine di default.
	 * Inoltre il metodo ridimensiona anche l'immagine come richiesto seguende
	 * delle dimensioni standard.
	 * 
	 * @param idPfPrincipal
	 *            idPfPrincipal dell'utente che richiede l'immagine
	 * @param id
	 *            idUtenteInfo o idAziendaInfo dell'utente che richiede
	 *            l'immagine
	 * @param size
	 *            dimensione dell'immagine, puo' essere una tra:
	 *            ConstantsSingleton.Img.IMG_SMALL,
	 *            ConstantsSingleton.Img.IMG_MEDIUM, o
	 *            ConstantsSingleton.Img.IMG_NORMAL
	 * @param request
	 * @return la foto dell'utente, o quella di default, ridimensionata come
	 *         richiesto
	 */
	@GET
	@Path("getFoto")
	@Produces("image/jpg")
	public Response getFoto(@QueryParam("type") Integer type,
			@QueryParam("id") Integer id, @QueryParam("size") String size,
			@Context HttpServletRequest request) {
		byte[] foto = null;
		byte[] resizedFoto = null;

		if (ConstantsSingleton.Img.TYPE_FOTO.equals(type)) {
			UtenteInfoDTO utenteInfoDTO = utenteInfoHome.findDTOById(id);

			foto = utenteInfoDTO.getFoto();
		} else if (ConstantsSingleton.Img.TYPE_LOGO.equals(type)) {
			AziendaInfoDTO aziendaInfoDTO = aziendaInfoHome.findDTOById(id);

			foto = aziendaInfoDTO.getLogo();
		}

		/* foto di default nel caso non ne sia stata inserita nessuna */
		if (foto == null) {
			try {
				InputStream input = null;

				if (ConstantsSingleton.Img.TYPE_FOTO.equals(type)) {
					input = new FileInputStream(request.getServletContext().getRealPath("/") +  
							ConstantsSingleton.Img.DEFAULT_IMG_UTENTE);
				} else if (ConstantsSingleton.Img.TYPE_LOGO.equals(type)) {
					input = new FileInputStream(request.getServletContext().getRealPath("/") +
							ConstantsSingleton.Img.DEFAULT_IMG_AZIENDA);
				}

				foto = IOUtils.toByteArray(input);
			} catch (IOException e) {
				log.error("Errore servizio recupero foto: "+e.getMessage());
				foto = null;
			}
		}

		/* resize */
		if (foto != null) {
			try {
				if (ConstantsSingleton.Img.IMG_SMALL.equals(size.toUpperCase())) {
					resizedFoto = FileUtils.scale(foto,
							ConstantsSingleton.Img.IMG_PX_SMALL,
							Image.SCALE_FAST);
					foto = resizedFoto;
				} else if (ConstantsSingleton.Img.IMG_MEDIUM.equals(size
						.toUpperCase())) {
					resizedFoto = FileUtils.scale(foto,
							ConstantsSingleton.Img.IMG_PX_MEDIUM,
							Image.SCALE_FAST);
					foto = resizedFoto;
				} else if (ConstantsSingleton.Img.IMG_NORMAL.equals(size
						.toUpperCase())) {
					resizedFoto = FileUtils.scale(foto,
							ConstantsSingleton.Img.IMG_PX_NORMAL,
							Image.SCALE_FAST);
					foto = resizedFoto;
				} else {
					log.warn("Dimensione immagine non specificata.");
					foto = null;
				}
			} catch (IOException e) {
				log.error("Errore servizio recupero foto: "+e.getMessage());
				foto = null;
			}
		}

		return respBuilder(id, foto);
	}

	/**
	 * Costruisce la risposta del Web Service
	 * 
	 * @param userId
	 *            Integer l'id dell'utente del quale vogliamo la foto
	 * @param foto
	 *            byte[] la foto da inviare in risposta, se presente.
	 * @return
	 */
	private Response respBuilder(Integer userId, byte[] foto) {
		ResponseBuilder response;

		if (foto == null) {
			response = Response.noContent();
		} else {
			ByteArrayInputStream bis = new ByteArrayInputStream(foto);
			response = Response.ok(bis);
			String fname = String.format(
					"attachment; filename=image_usr_%s.jpg", userId);
			response.header(CONTENT_DISPOSITION, fname);
		}

		return response.build();
	}

	@GET
	@Path("getTmpFoto")
	@Produces("image/jpg")
	public Response getTmpFoto(
			@QueryParam(ConstantsSingleton.FileUpload.FILE_NAME) String fName,
			@Context HttpServletRequest request) {

		String baseDir = ConstantsSingleton.TMP_DIR;
		File file2send = new File(baseDir + File.separator + fName);

		ResponseBuilder response = Response.ok(file2send);
		String fname = String.format("attachment; filename=%s",
				file2send.getName());
		response.header(CONTENT_DISPOSITION, fname);
		return response.build();
	}

	/**
	 * Recupero il campo immagine dalla tabella SvImmagine a fronte del
	 * parametro Id 'imgId' se presente oppure del Codice Sezione 'sezCod'
	 * 
	 * @param imgId
	 *            Integer
	 * @param sezCod
	 *            String
	 * @param request
	 *            HttpServletRequest
	 * @return Response
	 */
	@GET
	@Path("getSvImmagine")
	@Produces("image/jpg")
	public Response getSvImmagine(@QueryParam("imgId") Integer imgId,
			@QueryParam("sezCod") String sezCod,
			@Context HttpServletRequest request) {

		SvImmagineDTO svImmagineDTO;
		if (imgId != null) {
			svImmagineDTO = svImmagineHome.findDTOById(imgId);
		} else if (sezCod != null) {
			svImmagineDTO = svImmagineHome.findByCodSezione(sezCod);
		} else {
			return null;
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(
				svImmagineDTO.getImmagine());

		ResponseBuilder response = Response.ok(bis);
		String fname = String
				.format("attachment; filename=image_%s.jpg", imgId);
		response.header(CONTENT_DISPOSITION, fname);
		return response.build();
	}

	/**
	 * Restituisce il file allegato al cv.
	 * 
	 * @param idCvAllegato
	 *            Integer id del file allegato al cv
	 * @param request
	 *            HttpServletRequest
	 * @return Response
	 */
	@GET
	@Path("services/getAllegatoCv")
	public Response getAllegatoCv(
			@QueryParam("idCvAllegato") Integer idCvAllegato,
			@Context HttpServletRequest request) {

		// List<CvAllegato> l = cvAllegatoHome.findByCurriculumId(curriculumId);
		CvAllegatoDTO cvAllegato = cvAllegatoHome.findDTOById(idCvAllegato);
		if (cvAllegato == null) {
			return respGenFileBuilder(null, "");
		}

		return respGenFileBuilder(cvAllegato.getContenuto(),
				cvAllegato.getFilename());
	}

	/**
	 * Restituisce il file allegato alla candidatura.
	 * 
	 * @param idAcCandidatura
	 *            Integer id del file allegato alla candidatura
	 * @param request
	 *            HttpServletRequest
	 * @return Response
	 */
	@GET
	@Path("services/getAllegatoCandidatura")
	public Response getAllegatoCandidatura(
			@QueryParam("idAcCandidatura") Integer idAcCandidatura,
			@Context HttpServletRequest request) {

		AcAllegatoDTO acAllegato = acAllegatoHome.findDTOById(idAcCandidatura);
		if (acAllegato == null) {
			return respGenFileBuilder(null, "");
		}

		return respGenFileBuilder(acAllegato.getContenuto(),
				acAllegato.getFilename());
	}

	/**
	 * Restituisce il file allegato al cv.
	 * 
	 * @param idMsgAllegato
	 *            Integer id del file allegato al messaggio
	 * @param request
	 *            HttpServletRequest
	 * @return Response
	 */
	@GET
	@Path("services/getAllegatoMsg")
	public Response getAllegatoMsg(
			@QueryParam("idMsgAllegato") Integer idAcCandidatura,
			@Context HttpServletRequest request) {

		MsgAllegatoDTO dto = msgAllegatoHome.findDTOById(idAcCandidatura);
		if (dto == null) {
			return respGenFileBuilder(null, "");
		}

		return respGenFileBuilder(dto.getContenuto(), dto.getFilename());
	}

	/**
	 * Costruisce la risposta del Web Service
	 * 
	 * @param contenuto
	 *            byte[] il file da inviare in risposta, se presente.
	 * @param filename
	 *            il nome del file inviato
	 * 
	 * @return
	 */
	private Response respGenFileBuilder(byte[] contenuto, String filename) {
		ResponseBuilder response;

		if (contenuto == null) {
			response = Response.noContent();
		} else {
			ByteArrayInputStream bis = new ByteArrayInputStream(contenuto);			
			String strMimeType = URLConnection.guessContentTypeFromName(filename);
			response = Response.ok(bis);
			String fname = String.format("attachment; filename=\"%s\"", filename);
			response.header(CONTENT_DISPOSITION, fname);
			response.header("Content-Type", strMimeType);
		}

		return response.build();
	}

	private String getContentType(String fileName) {
		String suffix = null;
		int puntoIdx = fileName.lastIndexOf(".");
		if (puntoIdx > 0) {
			suffix = fileName.substring(puntoIdx + 1).toLowerCase();
		}
		
		if (suffix == null) {
			return "application/octet-stream";
		} else if (suffix.equals("txt")) {
			return "text/plain";
		} else if (suffix.equals("pdf")) {
			return "application/pdf";
		} else if (suffix.equals("xml")) {
			return "text/xml";
		} else if (suffix.equals("html")) {
			return "text/html";
		} else if (suffix.equals("htm")) {
			return "text/html";
		} else if (suffix.equals("gif")) {
			return "image/gif";
		} else if (suffix.equals("jpg")) {
			return "image/jpeg";
		} else if (suffix.equals("jpeg")) {
			return "image/jpeg";
		} else if (suffix.equals("doc") || suffix.equals("rtf")) {
			return "application/vnd.ms-word";
		} else if (suffix.equals("xls") || suffix.equals("cvs")) {
			return "application/vnd.ms-excel";
		} else {
			return "application/octet-stream";
		}	
	}
	
}