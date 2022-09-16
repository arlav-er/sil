package it.eng.myportal.rest.report;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.CvLinguaDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvLingua;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvLinguaHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.utils.Utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.query.JRJpaQueryExecuterFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
@Path("rest/services/")
public class GetCurriculumUtente {

	private static final String REPORT_DIR = "REPORT_DIR";

	private static final String RESOURCES_REPORT = "/resources/report/";

	protected static Log log = LogFactory.getLog(GetCurriculumUtente.class);
	private  boolean flagIdo;
	String curriculum="";
	byte[] foto;
	BufferedImage bImageFromConvert;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	CvLinguaHome cvLinguaHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Restituisce la stampa del Curriculum corrispondente all'id passato come
	 * parametro. (se appartiene all'utente loggato)
	 *
	 * @param curriculumId
	 *            Integer id dell'utente del quale si vuole la foto
	 * @param request
	 *            HttpServletRequest
	 * @return Response
	 */
	@GET
	@Path("getCurriculumUtente")
	@Produces("application/pdf")
	public Response getCurriculumUtente(@QueryParam("curriculumId") Integer curriculumId,
										@Context HttpServletRequest request) {
		boolean canAccess = false;
		InputStream bis = null;
		bImageFromConvert = null;
		foto = null;
		Response resp;
		try {
			Principal up = request.getUserPrincipal();
			if (up == null) {
				throw new Exception();
			}
			String username = AuthUtil.removeSocialPrefix(up.getName());
			PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOByUsername(username);
			CvDatiPersonaliDTO cvDatiPersonaliDTO = cvDatiPersonaliHome.findDTOById(curriculumId);
			UtenteDTO utenteDTO = utenteInfoHome.findMiniDTOById(cvDatiPersonaliDTO.getIdPfPrincipal());
			if (utenteDTO == null) {
				utenteDTO = utenteInfoHome.findMiniDTOById(cvDatiPersonaliDTO.getIdPfPrincipalPalese());
			}
			String nome = "";
			String cognome = "";
			if (utenteDTO != null) {
				cognome = utenteDTO.getCognome();
				nome = utenteDTO.getNome();
			}

			/*
			 * Controllo le credenziali di accesso, per utente e per azienda
			 */
			Integer idPfPrincipal = pfPrincipalDTO.getId();
			if (pfPrincipalHome.isUtente(idPfPrincipal)) {
				if (cvDatiPersonaliDTO.isProprietario(idPfPrincipal)) {
					canAccess = true;
				}
			} else if (pfPrincipalHome.isAzienda(idPfPrincipal)) {
				// AziendaSessionDTO aziendaDTO =
				// aziendaInfoHome.findDTOByUserName(username);
				// Integer idAziendaInfo = aziendaDTO.getIdAziendaInfo();
				// canAccess =
				// acCandidaturaHome.checkCurriculumCandidatura(curriculumId,
				// idAziendaInfo);
				canAccess = true;
			} else if (pfPrincipalHome.isProvincia(idPfPrincipal)) {
				// per la provincia è sempre possibile
				canAccess = true;
			}

			curriculum = contolloIdo(curriculumId);

			if (canAccess) {
				// costruisco il jasperReport
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				String relativeWebPath = RESOURCES_REPORT;
				String absoluteDiskPath = request.getServletContext().getRealPath(relativeWebPath);

				File file = new File(absoluteDiskPath + File.separator + curriculum);
				FileInputStream fis = new FileInputStream(file);
				JasperReport jasperReport = JasperCompileManager.compileReport(fis);
				boolean testataVisible = cvDatiPersonaliHome.isTestataVisibile(curriculumId, idPfPrincipal);
				Map<String, Object> parameters = getParameterCurriculum(entityManager, curriculumId, absoluteDiskPath
						+ File.separator, testataVisible);

				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters);
				JasperExportManager.exportReportToPdfStream(jasperPrint, bos);
				bis = new ByteArrayInputStream(bos.toByteArray());
				String filename;
				// costruisco il file vero e proprio
				if (testataVisible) {
					filename = String.format("%s_%s_%s.pdf", cvDatiPersonaliDTO.getDescrizione(), cognome, nome)
							.replace(' ', '_');
				} else {
					filename = String.format("%s_.pdf", cvDatiPersonaliDTO.getDescrizione()).replace(' ', '_');
				}
				resp = Utils.fileResponseBuilder(filename, bis);
			} else {
				throw new Exception("Non hai i permessi per stampare il curriculum selezionato.");
			}

		} catch (Exception e) {
			log.error(e);
			ResponseBuilder response = Response.serverError();
			resp = response.build();
		} finally {

		}
		return resp;
	}

	/**
	 * @param curriculumId
	 * @param reportDir
	 * @param testataVisible
	 *
	 */
	private Map<String, Object> getParameterCurriculum(EntityManager em, Integer curriculumId, String reportDir,
													   boolean testataVisible) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER, em);
		List<CvLinguaDTO> list = cvLinguaHome.findDTOByCurriculumId(curriculumId);
		List<CvLingua> list_ido = cvLinguaHome.findLinguaByCurriculumId(curriculumId);
		List<CvLinguaDTO> madreLinguaList = null;
		List<CvLinguaDTO> altreLingueList = null;
		List<CvLingua> madreLinguaList_ido = null;
		List<CvLingua> altreLingueList_ido = null;
		if (flagIdo) {
			if (list_ido != null) {
			}
			for (CvLingua cvLingua : list_ido) {
				if ((cvLingua.getFlagMadrelingua() !=null) && (cvLingua.getFlagMadrelingua())) {
					if (madreLinguaList_ido == null) {
						madreLinguaList_ido = new ArrayList<CvLingua>();
					}
					madreLinguaList_ido.add(cvLingua);

				} else {
					if (altreLingueList_ido == null) {
						altreLingueList_ido = new ArrayList<CvLingua>();
					}
					altreLingueList_ido.add(cvLingua);
				}

			}

			parameters.put("fotoProfilo", bImageFromConvert);
			parameters.put("altreLingueListIdo", altreLingueList_ido);
			parameters.put("madreLinguaListIdo", madreLinguaList_ido);

		}
		else {
			if (list != null) {
				for (CvLinguaDTO cvLinguaDTO : list) {
					if (cvLinguaDTO.isMadrelingua()) {
						if (madreLinguaList == null) {
							madreLinguaList = new ArrayList<CvLinguaDTO>();
						}
						madreLinguaList.add(cvLinguaDTO);

					} else {
						if (altreLingueList == null) {
							altreLingueList = new ArrayList<CvLinguaDTO>();
						}
						altreLingueList.add(cvLinguaDTO);
					}

				}
			}

		}

		parameters.put(JRParameter.REPORT_LOCALE, Locale.ITALIAN);
		parameters.put("curriculumId", curriculumId);
		parameters.put("madreLinguaList", madreLinguaList);
		parameters.put("altreLingueList", altreLingueList);
		parameters.put("testataVisible", Boolean.valueOf(testataVisible));
		parameters.put(REPORT_DIR, reportDir);

		return parameters;
	}

	private String contolloIdo(Integer curriculumId) throws Exception{

		CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(curriculumId);
		if(cvDatiPersonali.getFlagIdo() != null){
			if (cvDatiPersonali.getFlagIdo()){
				flagIdo = true;
				curriculum="Curriculum_CC_IDO.jrxml";
				if(cvDatiPersonali.getFoto() !=null){
					foto = cvDatiPersonali.getFoto();
					InputStream in = new ByteArrayInputStream(foto);
					bImageFromConvert = ImageIO.read(in);
				}
			}else {
				curriculum = "Curriculum_CC.jrxml";
				flagIdo = false;
			}
		}else {
			curriculum = "Curriculum_CC.jrxml";
			flagIdo = false;
		}
		return curriculum;
	}

}
