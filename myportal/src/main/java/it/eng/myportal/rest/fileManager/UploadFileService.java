package it.eng.myportal.rest.fileManager;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.CvAllegatoDTO;
import it.eng.myportal.dtos.MsgAllegatoDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.ejb.ErrorsSingleton;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.CvAllegatoHome;
import it.eng.myportal.entity.home.MsgAllegatoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.exception.StipulaDidException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.FileUtils;

/**
 * Classe che permette l'upload di file sul server. Si occupa di memorizzare i
 * file caricati nella cartella temporanea e restituisce il nome del file
 * salvato.
 * 
 * @author Girotti S, Rodi A.
 * 
 */
@Stateless
@Path("/rest/uploadFileService")
public class UploadFileService {

	private static Log log = LogFactory.getLog(UploadFileService.class);

	/**
	 * Singleton con gli errori
	 */
	@EJB
	private ErrorsSingleton errorsBean;

	@EJB
	PfPrincipalHome pfPrincipalHome;
	@EJB
	MsgAllegatoHome msgAllegatoHome;

	@EJB
	CvAllegatoHome cvAllegatoHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;
	
	@EJB
	UtenteInfoHome utenteInfoHome;
	
	@POST
	@Path("uploadImg")
	@Produces("text/plain")
	public String uploadImg(@Context HttpServletRequest request, @QueryParam("idPfPrincipal") Integer idPfPrincipal, @QueryParam("id") Integer id)
			throws Exception {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		List items = upload.parseRequest(request);
		Iterator iter = items.iterator();
		JSONObject jsonObject = new JSONObject();

		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (!item.isFormField()) {
				//log.debug("fileName:" + item.getName());
				log.debug("size:" + item.getSize());
				persistImg(item, jsonObject, idPfPrincipal, id);
			}
		}
		return jsonObject.toString();
	}
	
	private int persistImg(FileItem item, JSONObject jsonObject, Integer idPfPrincipal, Integer id)
			throws JSONException {
		/* se l'immagine e' troppo grande ritorno senza fare nulla */
		if (item.get().length > ConstantsSingleton.FileUpload.MAX_FOTO_SIZE) {
			String errorMsg = errorsBean.getProperty("img.tooBig.500");
			jsonObject
					.put(ConstantsSingleton.FileUpload.ERROR_REASON, errorMsg);
			jsonObject.put(ConstantsSingleton.FileUpload.STATUS_CODE,
					HttpServletResponse.SC_BAD_REQUEST);
			return HttpServletResponse.SC_BAD_REQUEST;
		}
		try {
			byte[] resizedImg = FileUtils.scale(item.get(),
					ConstantsSingleton.Img.IMG_PX_NORMAL, Image.SCALE_SMOOTH);

			boolean isCittadino = pfPrincipalHome.isUtente(idPfPrincipal);
			boolean isAzienda = pfPrincipalHome.isAzienda(idPfPrincipal);

			if (isCittadino) {
				utenteInfoHome.salvaFoto(id, resizedImg);
			} else if (isAzienda) {
				aziendaInfoHome.salvaLogo(id, resizedImg);
			}
			
			jsonObject.put(ConstantsSingleton.FileUpload.STATUS_CODE,
					HttpServletResponse.SC_OK);
			return HttpServletResponse.SC_OK;
		} catch (IOException e) {
			log.error("persistFoto: " + e.getMessage());
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 * @author Girotti S.
	 */
	@POST
	@Path("uploadFoto")
	@Produces("text/plain")
	public String uploadFoto(@Context HttpServletRequest request)
			throws Exception {
		int returnSC = HttpServletResponse.SC_OK;
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		List items = upload.parseRequest(request);
		Iterator iter = items.iterator();
		JSONObject jsonObject = new JSONObject();

		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (item.isFormField()) {
				log.debug("FORM FIELD");

			} else {
				if (!item.isFormField()) {
					log.debug("fileName:" + item.getName());
					log.debug("size:" + item.getSize());
					returnSC = persistFoto(item, jsonObject);
				}
			}
		}
		return jsonObject.toString();
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 * @author Girotti S.
	 */
	@POST
	@Path("uploadAttachCV")
	@Produces("text/plain")
	public String uploadAttachCV(@Context HttpServletRequest request)
			throws Exception {
		int returnSC = HttpServletResponse.SC_OK;
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		List items = upload.parseRequest(request);
		Iterator iter = items.iterator();
		JSONObject jsonObject = new JSONObject();
		Integer idPrincipalIns = null;
		int curriculumId = 0;
		FileItem fileItem2save = null;
		String curriculumIdStr = null;
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (item.isFormField()) {
				log.debug("FORM FIELD - looking for cv id");
				if ("curriculumId".equals(item.getFieldName())) {
					curriculumIdStr = item.getString();
					curriculumId = Integer.parseInt(curriculumIdStr, 10);
					Principal up = request.getUserPrincipal();
					if (up == null)
						throw new StipulaDidException();
					String username = AuthUtil.removeSocialPrefix(up.getName());
					UtenteCompletoDTO utenteDTO = utenteInfoHome
							.findDTOCompletoByUsername(username);
					idPrincipalIns = utenteDTO.getId();
				}

			} else {
				if (!item.isFormField()) {
					log.debug("fileName:" + item.getName());
					log.debug("size:" + item.getSize());
					fileItem2save = item;
				}
			}
		}

		if (curriculumId == 0 || fileItem2save == null) {
			// TODO - return error code
			returnSC = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		} else {
			returnSC = persistAttachCV(fileItem2save, curriculumId, jsonObject,
					idPrincipalIns);
		}
		return jsonObject.toString();
	}

	private int persistAttachCV(FileItem item, int curriculumId,
			JSONObject jsonObject, Integer idPrincipalIns) throws JSONException {
		/* se l'immagine e' troppo grande ritorno senza fare nulla */
		if (item.get().length > ConstantsSingleton.FileUpload.MAX_ATTACHMENT_SIZE) {
			String errorMsg = errorsBean
					.getProperty("curriculum.attach.tooBig");
			jsonObject
					.put(ConstantsSingleton.FileUpload.ERROR_REASON, errorMsg);
			jsonObject.put(ConstantsSingleton.FileUpload.STATUS_CODE,
					HttpServletResponse.SC_BAD_REQUEST);
			return HttpServletResponse.SC_BAD_REQUEST;
		}
		byte[] attach = item.get();

		// List<CvAllegato> l = cvAllegatoHome.findByCurriculumId(curriculumId);
		CvAllegatoDTO dto = cvAllegatoHome.findDTOByCurriculumId(curriculumId);
		// boolean allegatoPresente = (l != null) && !(l.isEmpty());
		// CvAllegatoDTO dto;
		boolean allegatoEraPresente = dto != null;
		if (allegatoEraPresente) {
			// NOP
		} else {
			dto = new CvAllegatoDTO();
			dto.setIdCvDatiPersonali(curriculumId);
		}
		String nameRaw = item.getName();
		String name = FilenameUtils.getName(nameRaw);
		dto.setFilename(name);
		dto.setContenuto(attach);
		if (allegatoEraPresente) {
			cvAllegatoHome.mergeDTO(dto, idPrincipalIns);
		} else {
			cvAllegatoHome.persistDTO(dto, idPrincipalIns);
		}

		return HttpServletResponse.SC_OK;
	}
	
	private int persistFoto(FileItem item, JSONObject jsonObject)
			throws JSONException {
		/* se l'immagine e' troppo grande ritorno senza fare nulla */
		if (item.get().length > ConstantsSingleton.FileUpload.MAX_FOTO_SIZE) {
			String errorMsg = errorsBean.getProperty("img.tooBig.500");
			jsonObject
					.put(ConstantsSingleton.FileUpload.ERROR_REASON, errorMsg);
			jsonObject.put(ConstantsSingleton.FileUpload.STATUS_CODE,
					HttpServletResponse.SC_BAD_REQUEST);
			return HttpServletResponse.SC_BAD_REQUEST;
		}

		ImageOutputStream output = null;

		try {
			File tempFile = File.createTempFile("uploaded_img_", ".jpg");
			tempFile.deleteOnExit();

			output = new MemoryCacheImageOutputStream(new FileOutputStream(
					tempFile));

			byte[] resizedImg = FileUtils.scale(item.get(),
					ConstantsSingleton.Img.IMG_PX_NORMAL, Image.SCALE_SMOOTH);

			output.write(resizedImg);

			try {
				jsonObject.put(ConstantsSingleton.FileUpload.FILE_NAME,
						tempFile.getName());
			} catch (JSONException e) {
			}
			jsonObject.put(ConstantsSingleton.FileUpload.STATUS_CODE,
					HttpServletResponse.SC_OK);
			return HttpServletResponse.SC_OK;
		} catch (IOException e) {
			log.error("persistFoto: " + e.getMessage());
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@GET
	@Path("getTmpAttachment")
	public Response getTmpAttachment(
			@QueryParam(ConstantsSingleton.FileUpload.FILE_NAME) String fName,
			@Context HttpServletRequest request) {
		String filename = fName;
		String suffix = fName.substring(filename.lastIndexOf("."));
		String prefix = filename.substring(0,
				filename.indexOf("_uploaded_attachment_"));
		filename = prefix + suffix;

		String baseDir = ConstantsSingleton.TMP_DIR;
		File file2send = new File(baseDir + File.separator + fName);

		ResponseBuilder response = Response.ok(file2send);
		String fname = String.format("attachment; filename=%s", filename);
		response.header("Content-Disposition", fname);
		return response.build();
	}

	@GET
	@Path("getAttachment")
	public Response getAttachment(
			@QueryParam(ConstantsSingleton.FileUpload.ATTACHMENT_ID) String attachmentId,
			@Context HttpServletRequest request) {
		byte[] file = null;
		try {

			String username = FacesContext.getCurrentInstance()
					.getExternalContext().getUserPrincipal().getName();

			MsgAllegatoDTO allegato = msgAllegatoHome.findDTOByIdAndUsername(
					Integer.parseInt(attachmentId), username);
			if (allegato != null && allegato.getContenuto() != null) {
				file = allegato.getContenuto();
				ByteArrayInputStream bis = new ByteArrayInputStream(file);
				ResponseBuilder response = Response.ok(bis);
				String fname = String.format("attachment; filename=%s",
						allegato.getFilename());
				response.header("Content-Disposition", fname);
				return response.build();

			} else {
				return Response.serverError().build();
			}
		} catch (RuntimeException e) {
			return Response.serverError().build();
		}

	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 * @author Rodi A.
	 */
	@POST
	@Path("uploadAttachment")
	@Produces("text/plain")
	public String uploadAttachment(@Context HttpServletRequest request)
			throws Exception {
		int returnSC = HttpServletResponse.SC_OK;
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		List<?> items = upload.parseRequest(request);
		Iterator<?> iter = items.iterator();
		JSONObject jsonObject = new JSONObject();

		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (item.isFormField()) {
				// do nothing
			} else {
				log.debug("fileName caricato:" + item.getName());
				log.debug("size:" + item.getSize());
				returnSC = persistAttachment(item, jsonObject);
			}
		}

		try {
			jsonObject.put(ConstantsSingleton.FileUpload.STATUS_CODE, returnSC);
		} catch (JSONException e) {
		}

		return jsonObject.toString();

	}

	/**
	 * Salva l'allegato su filesystem
	 * 
	 * @param item
	 * @param jsonObject
	 *            inserisce il nome del file allegato
	 * @return
	 * @author Rodi A.
	 */
	private int persistAttachment(FileItem item, JSONObject jsonObject) {

		FileOutputStream output = null;
		try {
			/* se L'ALLEGATO e' troppo grande ritorno senza fare nulla */
			if (item.get().length > ConstantsSingleton.FileUpload.MAX_ATTACHMENT_SIZE) {
				jsonObject.put(ConstantsSingleton.FileUpload.ERROR_REASON,
						"La dimensione del file non deve superare i 5MB");
				return HttpServletResponse.SC_BAD_REQUEST;
			}

			String filename = item.getName();
//			String prefix=FilenameUtils.getBaseName(filename);
			String suffix=FilenameUtils.getExtension(filename);
			String origFileName=FilenameUtils.getName(filename);
			String tmpFileName = "_uploaded_attachment_";
//			String tmpFileNameN = FilenameUtils.normalize(tmpFileName);

			File tempFile = File.createTempFile(tmpFileName, suffix);
			tempFile.deleteOnExit();

			output = new FileOutputStream(tempFile);

			output.write(item.get());

			jsonObject.put(ConstantsSingleton.FileUpload.FILE_NAME,
					tempFile.getName());
			jsonObject.put(ConstantsSingleton.FileUpload.ORIGINAL_FILE_NAME,
					origFileName);

			return HttpServletResponse.SC_OK;
		} catch (IOException e) {
			log.error("Error during file upload: " + e.getMessage());
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		} catch (JSONException e) {
			log.error("Error during file upload: " + e.getMessage());
			return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
		}
	}
}