/*
 * Created on Aug 21, 2006
 */
package it.eng.sil.action.report.schedaLavoratore;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.inet.report.Area;
import com.inet.report.Engine;
import com.inet.report.GeneralProperties;
import com.inet.report.RDC;
import com.inet.report.ReportException;
import com.inet.report.Section;

import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.action.report.utils.inet.ReportUtils;
import it.eng.sil.action.report.utils.inet.Style;
import it.eng.sil.action.report.utils.inet.StyleFactory;
import it.eng.sil.action.report.utils.inet.StyleUtils;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class SchedaLavoratore extends AbstractSimpleReport {

	private Engine eng;
	private String fileType = Engine.EXPORT_PDF;
	private String installAppPath = null;
	private SourceBean dati;
	private SourceBean sezioniConfig;

	private int LEFT_MARGIN = 50;
	private int SEZIONE_LENGTH = 10000;
	private int RIENTRO_SEZIONE = 0;
	private int TITOLO_MARGIN = LEFT_MARGIN;
	private int TITOLO_LENGTH = 2800;
	private int DESCRIZIONE_MARGIN = LEFT_MARGIN + 500;

	private Area area;

	private Style stileTitolo;
	private Style stileCampo;
	private Style stileTitoloSezione;

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		try {
			SourceBean responseRemota = (SourceBean) RequestContainer.getRequestContainer().getSessionContainer()
					.getAttribute("SCHEDA_LAVORATORE_COOP_ID");
			dati = (SourceBean) responseRemota.getAttribute("service_response");
			// dati = SourceBean.fromXMLFile("WEB-INF/conf/soapResponse.xml");
			// dati = (SourceBean)dati.getAttribute("service_response");
			// sezioniConfig =
			// SourceBean.fromXMLFile("WEB-INF/conf/configuratore_stampa_scheda_lavoratore.xml");
			sezioniConfig = (SourceBean) ConfigSingleton.getInstance().getAttribute("scheda-lavoratore");
			String tipoFile = (String) request.getAttribute("tipoFile");
			if (tipoFile == null)
				tipoFile = "PDF";
			setStrNomeDoc("SchedaLavoratore." + tipoFile);
			setFileType(tipoFile);
			start();

			setStrDescrizione("Scheda Lavoratore in cooperazione");
			setReportPath("schedaLavoratore/SchedaLavoratore.rpt"); // necessario
																	// per la
																	// classe
																	// Documento
																	// anche se
																	// il file
																	// rpt non
																	// esiste

			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				showDocument(request, response, eng);
			} else {
				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true"))
					insertDocument(request, response, eng);
				else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response, eng);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() throws Exception {
		try {
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("SchedaLavoratore");
			eng.setLocale(new Locale("IT", "it"));
			ReportUtils.initReport(eng);

			Area ph = eng.getArea("PH");
			ph.getSection(0).setHeight(StyleUtils.toTwips(1));

			area = eng.getArea("D");

			StyleFactory styleFactory = new StyleFactory();
			this.stileCampo = styleFactory.makeStyle(StyleFactory.ETICHETTA);
			this.stileTitolo = styleFactory.makeStyle(StyleFactory.ETICHETTA);

			stileTitolo.setAlign(GeneralProperties.ALIGN_HORIZONTAL_RIGHT);
			stileTitolo.setWidth(TITOLO_LENGTH);
			stileTitolo.setCanGrow(true);

			this.stileTitoloSezione = (Style) stileTitolo.clone();
			this.stileTitoloSezione.setWidth(5000);
			this.stileTitoloSezione.setAlign(GeneralProperties.ALIGN_HORIZONTAL_LEFT);
			intestazioneReport();
			stampaInterlinea();
			scorriSezioni();

			// RDC.saveEngine(new File("c:/prova.rpt"),eng);
		} catch (Exception e) {
			throw e;
		}
	}

	private void intestazioneReport() throws Exception {
		StyleFactory styleFactory = new StyleFactory();
		Style stileTitoloReport = styleFactory.makeStyle(StyleFactory.TITOLO2CENTRATO);
		Section section = area.addSection();
		ReportUtils.addText(section, "Scheda lavoratore di altro polo", stileTitoloReport, 0);

	}

	public String getFileType() {
		return this.fileType;
	}

	public void setInstallAppPath(String installAppPath) {
		this.installAppPath = installAppPath;
	}

	public void setFileType(String eft) {
		if (eft.equalsIgnoreCase("PDF"))
			this.fileType = Engine.EXPORT_PDF;
		else if (eft.equalsIgnoreCase("RTF"))
			this.fileType = Engine.EXPORT_RTF;
		else if (eft.equalsIgnoreCase("HTML"))
			this.fileType = Engine.EXPORT_HTML;
		else if (eft.equalsIgnoreCase("XLS"))
			this.fileType = Engine.EXPORT_XLS;
		else
			this.fileType = Engine.EXPORT_PDF;

	}

	private void scorriSezioni() throws Exception {
		int livello = 1;
		Vector sezioni = sezioniConfig.getAttributeAsVector("sezione");
		for (int i = 0; i < sezioni.size(); i++) {
			SourceBean sezione = (SourceBean) sezioni.get(i);
			creaSezione(sezione, dati, livello, true);
		}
	}

	private void creaSezione(SourceBean sezioneConfig, SourceBean rowsDati, int livello, boolean stampaTitoloSezione)
			throws Exception {
		String titolo = (String) sezioneConfig.getAttribute("titolo");
		String nomeModulo = (String) sezioneConfig.getAttribute("nome_modulo");
		Vector righe = sezioneConfig.getAttributeAsVector("riga");

		// recupero i dati
		Vector datiModulo = rowsDati.getAttributeAsVector(nomeModulo);
		// stampo l'intestazione della sezione
		if (stampaTitoloSezione)
			stampaIntestazioneSezione(titolo);
		// per ogni riga xml di dati bisogna recuperare le informazioni sui suoi
		// campi
		for (int i = 0; i < datiModulo.size(); i++) {
			SourceBean rowDati = (SourceBean) datiModulo.get(i);

			for (int i_riga = 0; i_riga < righe.size(); i_riga++) {
				SourceBean riga = (SourceBean) righe.get(i_riga);
				String valoreCampo = "";
				String titoloCampo = (String) riga.getAttribute("titolo");
				String chiaveCampo = (String) riga.getAttribute("chiave");
				String decodificaSQL = (String) riga.getAttribute("decodifica_sql");
				String riferimentoAdAltraSezione = (String) riga.getAttribute("riferimento");
				// prima bisogna stampare il titolo del campo
				if (stampaTitoloSezione)
					stampaTitoloCampo(titoloCampo, livello);
				// ora controlliamo se il valore e' una sottosezione
				if (riferimentoAdAltraSezione != null) {
					SourceBean sezioneCollegata = getSezione(riferimentoAdAltraSezione);
					// si entra in una ricorsione
					creaSezione(sezioneCollegata, rowDati, livello + 1, false);
				} else {
					if (chiaveCampo != null && decodificaSQL != null) {
						String codiceCampo = (String) rowDati.getAttribute(chiaveCampo);
						valoreCampo = decodificaCodice(codiceCampo, decodificaSQL);
					}
					if (chiaveCampo != null && decodificaSQL == null) {
						valoreCampo = Utils.notNull(rowDati.getAttribute(chiaveCampo));
					}
					stampaValoreCampo(valoreCampo, livello);
				}
			}
			// System.out.println("");
			stampaInterlinea();
		}
		// System.out.println("");
		stampaInterlinea();
	}

	/**
	 * @param codiceCampo
	 * @param decodificaSQL
	 * @return
	 */
	private String decodificaCodice(String codiceCampo, String decodificaSQL) throws Exception {
		// return "ciao";
		String valore = null;
		if (codiceCampo != null && !codiceCampo.equals("")) {
			Connection connection = getConnection();
			PreparedStatement ps = connection.prepareStatement(decodificaSQL);
			ps.setString(1, codiceCampo);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				valore = rs.getString("descrizione");
			rs.close();
			ps.close();
			connection.close();
		}
		return valore;
	}

	private Connection getConnection() throws Exception {
		return DataConnectionManager.getInstance().getConnection().getInternalConnection();
	}

	/**
	 * @param valoreCampo
	 * @param livello
	 */
	private void stampaValoreCampo(String valoreCampo, int livello) throws Exception {
		// for (int i=0;i<livello;i++){
		// System.out.print("\t");
		// }
		// System.out.println(valoreCampo);
		// riprendo l'ultima sezione, ovvero quella corrente
		int i = area.getSectionCount() - 1;
		Section section = area.getSection(i);
		ReportUtils.addText(section, valoreCampo, stileCampo, DESCRIZIONE_MARGIN + (RIENTRO_SEZIONE * (livello - 1)));
	}

	/**
	 * @param titoloCampo
	 * @param livello
	 */
	private void stampaTitoloCampo(String titoloCampo, int livello) throws Exception {
		// for (int i=0;i<livello;i++){
		// System.out.print("\t");
		// }
		// System.out.print(titoloCampo);
		Section section = area.addSection();
		ReportUtils.addText(section, titoloCampo, stileTitolo,
				StyleUtils.toMM(TITOLO_MARGIN) + (RIENTRO_SEZIONE * (livello - 1)));

	}

	private void stampaInterlinea() throws Exception {
		// stampiamo una riga vuota
		Section section = area.addSection();
		// ReportUtils.addText(section, "", stileTitolo, LEFT_MARGIN);
		section.setHeight(StyleUtils.toTwips(20));
	}

	private SourceBean getSezione(String nomeSezione) {
		return (SourceBean) sezioniConfig.getAttribute(nomeSezione);
	}

	/**
	 * @param titolo
	 */
	private void stampaIntestazioneSezione(String titolo) throws ReportException {
		// System.out.println("");
		// System.out.println(" --------------- " + titolo + "
		// ------------------");
		Section section = area.addSection();
		ReportUtils.addText(section, titolo, stileTitoloSezione, LEFT_MARGIN);
		section = area.addSection();
		section.addHorizontalLine(LEFT_MARGIN, StyleUtils.toTwips(1), SEZIONE_LENGTH);

	}

	public void setSezioniConfig(SourceBean newSezioniConfig) {
		this.sezioniConfig = newSezioniConfig;
	}

	public void setDati(SourceBean newDati) {
		this.dati = newDati;
	}

	public static void main(String s[]) {
		try {
			SourceBean dati = SourceBean.fromXMLFile("c:/soapResponse.xml");
			SourceBean sezioniConfiguratore = SourceBean.fromXMLFile("c:/configuratore_stampa_scheda_lavoratore.xml");
			SchedaLavoratore sc = new SchedaLavoratore();
			sc.setDati((SourceBean) dati.getAttribute("SERVICE_RESPONSE"));
			sc.setSezioniConfig(sezioniConfiguratore);
			sc.scorriSezioni();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
