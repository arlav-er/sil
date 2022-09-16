package it.eng.sil.module.trento;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownloadPdf
 */
/**
 * @author giorgini
 *
 */
public class DownloadPdf extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DownloadPdf.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadPdf() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Enumeration o = request.getParameterNames();

		ServletContext ctx = getServletContext();
		// System.out.println("REAL PATH =" + ctx.getRealPath("/_js"));
		String buffer = "";
		String INDIRIZZO_STAMPA = "";
		String COD_CPI = "";
		String DESC_CPI = "";
		String INDIRIZZO = "";
		String LOCALITA = "";
		String CAP = "";
		String CODCOM = "";
		String CODPROVINCIA = "";
		String COMUNE = "";
		String PROVINCIA = "";
		String TELEFONO = "";
		String FAX = "";
		String EMAIL = "";
		String ORARIO = "";
		String RESPONSABILE = "";
		String EMAIL_PEC = "";
		String EMAIL_ADL = "";

		try {
			while (o.hasMoreElements()) {
				String element = (String) o.nextElement();
				if ("EditorDefault".equals(element))
					buffer = adjustedEditorContent(request, ctx, element);
				if ("INDIRIZZO_STAMPA".equals(element))
					INDIRIZZO_STAMPA = request.getParameter(element);
				if ("COD_CPI".equals(element))
					COD_CPI = request.getParameter(element);
				if ("DESC_CPI".equals(element))
					DESC_CPI = request.getParameter(element);
				if ("INDIRIZZO".equals(element))
					INDIRIZZO = request.getParameter(element);
				if ("LOCALITA".equals(element))
					LOCALITA = request.getParameter(element);
				if ("CAP".equals(element))
					CAP = request.getParameter(element);
				if ("CODCOM".equals(element))
					CODCOM = request.getParameter(element);
				if ("CODPROVINCIA".equals(element))
					CODPROVINCIA = request.getParameter(element);
				if ("COMUNE".equals(element))
					COMUNE = request.getParameter(element);
				if ("PROVINCIA".equals(element))
					PROVINCIA = request.getParameter(element);
				if ("TELEFONO".equals(element))
					TELEFONO = request.getParameter(element);
				if ("FAX".equals(element))
					FAX = request.getParameter(element);
				if ("EMAIL".equals(element))
					EMAIL = request.getParameter(element);
				if ("ORARIO".equals(element))
					ORARIO = request.getParameter(element);
				if ("RESPONSABILE".equals(element))
					RESPONSABILE = request.getParameter(element);
				if ("EMAIL_PEC".equals(element))
					EMAIL_PEC = request.getParameter(element);
				if ("EMAIL_ADL".equals(element))
					EMAIL_ADL = request.getParameter(element);

			}

			/*
			 * inserisco tutto il contenuto della textarea in un box
			 */
			System.out.println("buffer =" + buffer);
			response.setHeader("Content-disposition",
					"attachment; filename=stampa.pdf");
			response.setHeader("Set-Cookie", "fileDownload=true; path=/");
			response.setContentType("application/pdf");

			ConvertToPdf convertToPdf = new ConvertToPdf();

			//TODO NUOVI CAMPI CPI
			String editor = Utility.replaceDatiLavoratoreFake(buffer);
			editor = Utility.replaceDatiCPI(editor, COD_CPI, DESC_CPI, INDIRIZZO,LOCALITA,	CAP,CODCOM,	CODPROVINCIA,COMUNE,PROVINCIA,TELEFONO,	FAX,EMAIL,ORARIO,RESPONSABILE, EMAIL_PEC, EMAIL_ADL);
			editor = editor.replace("\n", "").replace("\r", "");
			//String footerContent = INDIRIZZO_STAMPA;
			boolean footerContent = false;
			boolean footerContentPAT = false;
			boolean footerContentModCert = false;
//					"Centro per l'Impiego di " +DESC_CPI + " - " +INDIRIZZO + ""
//					+ " - " + LOCALITA + " "  + CAP + " " + PROVINCIA + ""
//					+ " - Tel. " + TELEFONO + " - Fax " + FAX + " - " + EMAIL;	
			
			OutputStream os = convertToPdf.createPDF(editor,response.getOutputStream(),footerContent, footerContentPAT, footerContentModCert);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 * Ritorna una stringa con il contenuto dell'editor opportunamente "sistemato"
	 * 
	 * @param request
	 *            la richiesta
	 * @param ctx
	 *            il contesto dell'applicazione
	 * @param element
	 *            il contenuto dell'editor
	 * @return ritorna il contenuto dell'editor in forma canonica
	 */
	private String adjustedEditorContent(HttpServletRequest request,
			ServletContext ctx, String element) {
		String par = request.getParameter(element);
		StringBuffer bf = new StringBuffer();
		String repl = par.replaceAll(
				" src=\"" + request.getContextPath(),
				" src=\"" + request.getScheme() + "://"
						+ request.getHeader("Host") + ctx.getContextPath());
		_logger.debug("path delle immagini: " + request.getScheme() + "://"	+ request.getHeader("Host") + ctx.getContextPath());
		repl = repl.replaceAll("<input ", "<img "); // bugfix
		bf.append(repl);
		bf.insert(0, "<div>");
		bf.append("</div>");
		return bf.toString();
	}
}
