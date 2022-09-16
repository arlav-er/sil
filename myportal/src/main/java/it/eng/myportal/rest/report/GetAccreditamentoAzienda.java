package it.eng.myportal.rest.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.query.JRJpaQueryExecuterFactory;

@Stateless
@Path("rest/services/")
public class GetAccreditamentoAzienda {

	private static final String REPORT_DIR = "REPORT_DIR";

	private static final String RESOURCES_REPORT = "/resources/report/";

	protected static Log log = LogFactory.getLog(GetAccreditamentoAzienda.class);

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Restituisce la stampa del Curriculum corrispondente all'id passato come parametro. (se appartiene all'utente
	 * loggato)
	 * 
	 * @param curriculumId
	 *            Integer id dell'utente del quale si vuole la foto
	 * @param request
	 *            HttpServletRequest
	 * @return Response
	 */
	@GET
	@Path("getAccreditamentoAzienda")
	@Produces("application/pdf")
	public Response getAccreditamentoAzienda(@QueryParam("aziendaInfoId") Integer aziendaInfoId,
			@QueryParam("token") String token, @Context HttpServletRequest request) {

		InputStream bis = null;
		Response resp;

		if (!Utils.isTokenSecurityCorrect(token, String.valueOf(aziendaInfoId))) {
			throw new RuntimeException("Forzata la chiave per la stampa SARE");
		}

		try {

			AziendaInfoDTO aziendaInfoDTO = aziendaInfoHome.findDTOById(aziendaInfoId);
			if (aziendaInfoDTO == null) {
				throw new Exception("Non è possibile recuperare l'azienda con il parametro passato!");
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			String relativeWebPath = RESOURCES_REPORT;
			String absoluteDiskPath = request.getServletContext().getRealPath(relativeWebPath);

			// La VDA ha una stampa a parte, perchè per lei l'autorizzazione non è per il SARE
			// ma solo per i tirocini.
			File file;
			String filename;
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_VDA) {
				file = new File(absoluteDiskPath + File.separator + "AccreditamentoAzienda_VDA_CC.jrxml");
				filename = "CERTIFICAZIONE_" + aziendaInfoDTO.getRagioneSociale().replaceAll(" ", "_");
			} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_CALABRIA) {
				file = new File(absoluteDiskPath + File.separator + "AccreditamentoAzienda_Calabria_CC.jrxml");
				filename = "CERTIFICAZIONE_" + aziendaInfoDTO.getRagioneSociale().replaceAll(" ", "_");
			} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_UMBRIA) {
				file = new File(absoluteDiskPath + File.separator + "AccreditamentoAzienda_Umbria_CC.jrxml");
				filename = "CERTIFICAZIONE_" + aziendaInfoDTO.getRagioneSociale().replaceAll(" ", "_");
			} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_TRENTO) {
				file = new File(absoluteDiskPath + File.separator + "AccreditamentoAzienda_PAT_CC.jrxml");
				filename = "CERTIFICAZIONE_" + aziendaInfoDTO.getRagioneSociale().replaceAll(" ", "_");
			} else {//ER
				file = new File(absoluteDiskPath + File.separator + "AccreditamentoAzienda_CC.jrxml");
				filename = "SARE_" + aziendaInfoDTO.getRagioneSociale().replaceAll(" ", "_");
			}

			FileInputStream fis = new FileInputStream(file);
			JasperReport jasperReport = JasperCompileManager.compileReport(fis);
			Map<String, Object> parameters = getParameterStampaAccreditamento(entityManager, aziendaInfoId,
					aziendaInfoDTO, absoluteDiskPath + File.separator);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters);

			JasperExportManager.exportReportToPdfStream(jasperPrint, bos);
			bis = new ByteArrayInputStream(bos.toByteArray());
			resp = Utils.fileResponseBuilder(filename + ".pdf", bis);
			// finally we have to ensure that the document is properly closed:

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
	 * 
	 */
	private Map<String, Object> getParameterStampaAccreditamento(EntityManager em, Integer aziendaInfoId,
			AziendaInfoDTO aziendaInfoDTO, String reportDir) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER, em);
		parameters.put(JRParameter.REPORT_LOCALE, Locale.ITALIAN);

		parameters.put("aziendaInfoId", aziendaInfoId);
		// parameters.put("aziendaInfoDTO", aziendaInfoDTO);
		parameters.put(REPORT_DIR, reportDir);

		return parameters;
	}

}