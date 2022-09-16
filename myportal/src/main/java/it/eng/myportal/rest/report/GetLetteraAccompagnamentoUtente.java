package it.eng.myportal.rest.report;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.AziendaSessionDTO;
import it.eng.myportal.dtos.CvLetteraAccDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.CvLetteraAccHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
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
public class GetLetteraAccompagnamentoUtente {

	private static final String REPORT_DIR = "REPORT_DIR";

	private static final String RESOURCES_REPORT = "/resources/report/";

	// private static final String CONTENT_DISPOSITION = "Content-Disposition";

	protected static Log log = LogFactory.getLog(GetLetteraAccompagnamentoUtente.class);

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	CvLetteraAccHome cvLetteraAccHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Restituisce la stampa della lettera accompagnamento corrispondente all'id
	 * passato come parametro. (se appartiene all'utente loggato)
	 * 
	 * @param id
	 *            Integer id dell'utente del quale si vuole la foto
	 * @param request
	 *            HttpServletRequest
	 * @return Response
	 */
	@GET
	@Path("getLetteraAccompagnamento")
	@Produces("application/pdf")
	public Response getLetteraAccompagnamento(@QueryParam("id") Integer letteraId, @Context HttpServletRequest request) {
		boolean canAccess = false;
		InputStream bis = null;
		Response resp;
		try {
			Principal up = request.getUserPrincipal();
			if (up == null) {
				throw new Exception();
			}
			String username = AuthUtil.removeSocialPrefix(up.getName());
			PfPrincipalDTO pfPrincipalDTO = pfPrincipalHome.findDTOByUsername(username);
			CvLetteraAccDTO cvLetteraAccDTO = cvLetteraAccHome.findDTOById(letteraId);

			/*
			 * Controllo le credenziali di accesso, per utente e per azienda
			 */
			if (pfPrincipalHome.isUtente(pfPrincipalDTO.getId())) {
				UtenteCompletoDTO utenteDTO = utenteInfoHome.findDTOCompletoByUsername(username);
				if (cvLetteraAccDTO.isProprietary(utenteDTO.getId())) {
					canAccess = true;
				}
			} else if (pfPrincipalHome.isAzienda(pfPrincipalDTO.getId())) {
				AziendaSessionDTO aziendaDTO = aziendaInfoHome.findSessionDTOByUserName(username);
				Integer idAziendaInfo = aziendaDTO.getIdAziendaInfo();
				canAccess = acCandidaturaHome.checkLetteraCandidatura(letteraId, idAziendaInfo);
			} else if (pfPrincipalHome.isProvincia(pfPrincipalDTO.getId())) {
				// per la provincia è sempre possibile
				canAccess = true;
			}

			if (canAccess) {
				// costruisco il jasperReport
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				String relativeWebPath = RESOURCES_REPORT;
				String absoluteDiskPath = request.getServletContext().getRealPath(relativeWebPath);
				File file = new File(absoluteDiskPath + File.separator + "lettera_accompagnamento_cc.jrxml");
				FileInputStream fis = new FileInputStream(file);
				JasperReport jasperReport = JasperCompileManager.compileReport(fis);
				Map<String, Object> parameters = getParameterLetteraAccompagnamento(entityManager, letteraId,
						absoluteDiskPath + File.separator);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters);
				JasperExportManager.exportReportToPdfStream(jasperPrint, bos);
				bis = new ByteArrayInputStream(bos.toByteArray());

				// costruisco il file vero e proprio
				UtenteDTO utenteDTO = utenteInfoHome.findMiniDTOById(cvLetteraAccDTO.getIdPfPrincipal());
				String filename = String.format("%s_%s_%s.pdf", cvLetteraAccDTO.getNome(), utenteDTO.getCognome(),
						utenteDTO.getNome()).replace(' ', '_');
				resp = Utils.fileResponseBuilder(filename, bis);
			} else {
				throw new Exception("Non hai i permessi per stampare la lettera di presentazione selezionata.");
			}

		} catch (Exception e) {
			log.error(e);
			ResponseBuilder response = Response.serverError();
			resp = response.build();
		} finally {

		}
		return resp;
	}

	private Map<String, Object> getParameterLetteraAccompagnamento(EntityManager em, Integer idCvLetteraAcc,
			String reportDir) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER, em);
		parameters.put("idCvLetteraAcc", idCvLetteraAcc);
		parameters.put(REPORT_DIR, reportDir);
		parameters.put(JRParameter.REPORT_LOCALE, Locale.ITALIAN);

		return parameters;
	}

}