/*
 * Patto.java
 *
 * Created on 25 gennaio 2004, 23.38
 * 
 ***********************************************
 *  FAI L'OVERLOAD DEL METODO CREATETEMPFILE DI DOCUMENT IN MODO DA PASSARE L' ENGINE GIA' BELLO E FATTO
 *  private void createReportTempFile(Engine eng) throws IOException, ReportException ;
 */
package it.eng.sil.action.report.test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.inet.report.Area;
import com.inet.report.BorderProperties;
import com.inet.report.Box;
import com.inet.report.Element;
import com.inet.report.Engine;
import com.inet.report.Fields;
import com.inet.report.Paragraph;
import com.inet.report.Picture;
import com.inet.report.RDC;
import com.inet.report.ReportProperties;
import com.inet.report.Section;
import com.inet.report.SpecialField;
import com.inet.report.Subreport;
import com.inet.report.Text;
import com.inet.report.TextPart;

// package report;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.Controlli;

/**
 * 
 * @author Administrator
 */
public class Patto {
	/**
	 * estremo sinistro del report da cui poter scrivere (in decimi di millimetro)
	 */
	private int LEFT = 80;
	private int SEP = 30;
	private int LEFT2 = LEFT + 1000;
	private int X_COL_1 = 700;
	private int X_COL_2 = 1600;
	private int LEFT_TITOLO_PUNTO = LEFT;
	private int LEFT_TITOLO_PUNTO_T = LEFT_TITOLO_PUNTO + 100;
	/*
	 * private int LEFT_COL_T = LEFT+100; private int LEFT2_COL_T = LEFT2+100; private int ETICHETTA_WIDTH =
	 * StyleUtils.toMM(StyleFactory.makeStyle(Style.ETICHETTA).getWidth()); private int LEFT_COL_V = LEFT_COL_T +
	 * ETICHETTA_WIDTH+SEP; private int LEFT2_COL_V = LEFT2_COL_T + ETICHETTA_WIDTH+SEP;
	 */
	private int ETICHETTA_WIDTH = StyleUtils.toMM(StyleFactory.makeStyle(Style.ETICHETTA).getWidth());
	private int LEFT_COL_T = X_COL_1 - ETICHETTA_WIDTH;
	private int LEFT2_COL_T = X_COL_2 - ETICHETTA_WIDTH;;

	private int LEFT_COL_V = X_COL_1 + SEP;
	private int LEFT2_COL_V = X_COL_2 + SEP;
	//
	// private SourceBean testata = null;
	private SourceBean obbligoFormativo = null;
	private SourceBean statoOccupazionale = null;
	private SourceBean permessoDiSoggiorno = null;
	private SourceBean collocamentoMirato = null;
	private SourceBean listeMobilita = null;
	private Vector esperienzeProfessionali = null;
	private Vector indispTemporanee = null;
	private Vector titoliStudio = null;
	private Vector ambitoProfessionale = null;
	private Vector corsiFormazione = null;
	private SourceBean cat181 = null;
	// private SourceBean informazioniUtente = null;
	private Vector indisponibilitaPressoImprese = null;
	private Vector appuntamenti = null;
	private Vector azioniConcordate = null;
	private SourceBean infoGenerali = null;
	private Vector impegni = null;
	private SourceBean laurea;
	private Vector movimenti;
	private String docInOut;
	//

	private String numProtocollo;
	private String numAnnoProt;
	private String datProtocollazione;
	private String riferimentoDoc;
	//
	private Engine eng = null;
	private String installAppPath = null;

	private String fileType = Engine.EXPORT_PDF;

	public String getFileType() {
		return this.fileType;
	}

	// public void setNumProtocollo(String numProtocollo){this.nProtocollo =
	// numProtocollo;}
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

	public void setRiferimentoDoc(String s) {
		this.riferimentoDoc = s;
	}

	public void setInfoGenerali(SourceBean infoGenerali) {
		this.infoGenerali = infoGenerali;
	}

	public void setAzioniConcordate(Vector azioniConcordate) {
		this.azioniConcordate = azioniConcordate;
	}

	public void setAppuntamenti(Vector appuntamenti) {
		this.appuntamenti = appuntamenti;
	}

	public void setIndisponibilitaPressoImprese(Vector indisponibilitaPressoImprese) {
		this.indisponibilitaPressoImprese = indisponibilitaPressoImprese;
	}

	public void setCorsiFormazione(Vector corsiFormazione) {
		this.corsiFormazione = corsiFormazione;
	}

	public void setTitoliStudio(Vector titoliStudio) {
		this.titoliStudio = titoliStudio;
	}

	public void setIndispTemporanee(Vector indispTemporanee) {
		this.indispTemporanee = indispTemporanee;
	}

	public void setEsperienzeProfessionali(Vector esperienzeProfessionali) {
		this.esperienzeProfessionali = esperienzeProfessionali;
	}

	public void setListeMobilita(SourceBean listeMobilita) {
		this.listeMobilita = listeMobilita;
	}

	public void setCollocamentoMirato(SourceBean collocamentoMirato) {
		this.collocamentoMirato = collocamentoMirato;
	}

	public void setPermessoDiSoggiorno(SourceBean permessoDiSoggiorno) {
		this.permessoDiSoggiorno = permessoDiSoggiorno;
	}

	public void setStatoOccupazionale(SourceBean statoOccupazionale) {
		this.statoOccupazionale = statoOccupazionale;
	}

	public void setObbligoFormativo(SourceBean obbligoFormativo) {
		this.obbligoFormativo = obbligoFormativo;
	}

	public void setAmbitoProfessionale(Vector ambitoProfessionale) throws Exception {
		this.ambitoProfessionale = getMansioni(ambitoProfessionale);
	}

	public void setImpegni(Vector impegni) {
		this.impegni = impegni;
	}

	public void setCat181(SourceBean cat181) {
		this.cat181 = cat181;
	}

	public void setLaurea(SourceBean laurea) {
		this.laurea = laurea;
	}

	public void setMovimenti(Vector movimenti) {
		this.movimenti = movimenti;
	}

	public Engine getEngine() {
		return this.eng;
	}

	public void setStrNumProtocollo(String s) {
		this.numProtocollo = s;
	}

	public void setNumAnnoProt(String s) {
		this.numAnnoProt = s;
	}

	public void setDatProtocollazione(String s) {
		this.datProtocollazione = s;
	}

	public void setDocInOut(String s) {
		this.docInOut = s;
	}

	// *********************************************
	// public static final boolean TEST = false;
	// dati di test
	/*
	 * { /* if (TEST) { try { infoGenerali = new SourceBean("TEST"); infoGenerali.setAttribute("CPIDESCRIZIONE", "XXX");
	 * infoGenerali.setAttribute("PROVINCIA", "Bologna"); infoGenerali.setAttribute("TEL", "051000000");
	 * infoGenerali.setAttribute("FAX", "051999999"); infoGenerali.setAttribute("EMAIL", "bologna@provincia.bo.it");
	 * infoGenerali.setAttribute("NOMELAVORATORE", "Piero"); infoGenerali.setAttribute("COGNOMELAVORATORE", "Vance");
	 * infoGenerali.setAttribute("CODICEFISCALE", "svnndr67r15g482g"); infoGenerali.setAttribute("sessolavoratore",
	 * "M"); infoGenerali.setAttribute("DATA_DICHIARAZIONE", "10/06/2004");
	 * infoGenerali.setAttribute("NUMPROTOCOLLO","1300"); // obbligoFormativo = new SourceBean("TEST");
	 * obbligoFormativo.setAttribute("FLG_ASSOLTO","S"); obbligoFormativo.setAttribute("FLG_SCUOLA_ASSOLTO","S");
	 * obbligoFormativo.setAttribute("MODALITA","......."); // statoOccupazionale = new SourceBean("TEST");
	 * statoOccupazionale.setAttribute("DATINIZIO","10/10/2003");
	 * statoOccupazionale.setAttribute("DESCRIZIONESTATO","Disoccupato df ..............................................
	 * dfdfg dfg dfg fdg dfg dfg df gfdgdfffffffffff ffffffffffffffffff..");
	 * statoOccupazionale.setAttribute("DESCRIZIONE181","Non giovani");
	 * statoOccupazionale.setAttribute("FLGPENSIONATO","N");
	 * statoOccupazionale.setAttribute("DATANZIANITADISOCC","10/09/2003");
	 * statoOccupazionale.setAttribute("NUMMESISOSP","2"); statoOccupazionale.setAttribute("MESI_ANZ",new
	 * BigDecimal("3")); statoOccupazionale.setAttribute("FLGINDENNIZZATO","N");
	 * statoOccupazionale.setAttribute("NUMREDDITO","1000.00"); statoOccupazionale.setAttribute("CODCATEGORIA181","D");
	 * // permessoDiSoggiorno = new SourceBean(""); permessoDiSoggiorno.setAttribute("DATSCADENZA","15/07/2004");
	 * permessoDiSoggiorno.setAttribute("DATRICHIESTA","15/01/2004");
	 * permessoDiSoggiorno.setAttribute("DESCRIZIONEMOT","..");
	 * permessoDiSoggiorno.setAttribute("DESCRIZIONERICH",".."); // collocamentoMirato = new SourceBean("");
	 * collocamentoMirato.setAttribute("DATINIZIO","15/03/2004");
	 * collocamentoMirato.setAttribute("DESCRIZIONEISCR","...."); collocamentoMirato.setAttribute("DESCRIZIONEINV",
	 * "....."); collocamentoMirato.setAttribute("NUMPERCINVALIDITA", "20"); // listeMobilita = new SourceBean("");
	 * listeMobilita.setAttribute("DATINIZIO","15/09/2003");
	 * listeMobilita.setAttribute("DESCRIZIONE","inv. temporanea"); listeMobilita.setAttribute("FLGINDENNITA","S"); //
	 * 
	 * esperienzeProfessionali = new Vector(); SourceBean espRow = new SourceBean("");
	 * espRow.setAttribute("DESCRIZIONECONTR","...."); espRow.setAttribute("DESCRIZIONEMANS", "...");
	 * espRow.setAttribute("NUMANNOINIZIO", "1989"); espRow.setAttribute("NUMANNOFINE", "2001");
	 * espRow.setAttribute("NUMMESEFINE", new BigDecimal("2")); espRow.setAttribute("NUMMESEINIZIO", new
	 * BigDecimal("11")); espRow.setAttribute("RETRIBANNUA", "20000"); esperienzeProfessionali.add(espRow); //
	 * 
	 * indispTemporanee = new Vector(); SourceBean indRow = new SourceBean(""); indRow.setAttribute("DESCRIZIONE",
	 * "......."); indRow.setAttribute("DATFINE", "21/06/2005"); indRow.setAttribute("DATINIZIO", "12/02/2004");
	 * indispTemporanee.add(indRow); //
	 * 
	 * titoliStudio = new Vector(); SourceBean titRow =new SourceBean (""); titRow.setAttribute("DESTIPOTITOLO","...");
	 * titRow.setAttribute("DESTITOLO","......"); titRow.setAttribute("NUMANNO","13/06/2004"); titoliStudio.add(titRow);
	 * //
	 * 
	 * corsiFormazione = new Vector(); SourceBean corsiRow = new SourceBean("");
	 * corsiRow.setAttribute("FLGCOMPLETATO","S"); corsiRow.setAttribute("NUMANNO","1985");
	 * corsiRow.setAttribute("CORSO","..."); corsiFormazione.add(corsiRow); // }catch(Exception e)
	 * {//e.printStackTrace(); throw new RuntimeException(); } } }
	 */
	/** Creates a new instance of Patto */
	public Patto() {
	}

	public Patto(String[] a) throws Exception {
		start();
	}

	public void start() throws Exception {
		try {
			StyleFactory.reset();
			eng = RDC.createEmptyEngine(getFileType());
			eng.setReportTitle("report patto di test .....");
			eng.setLocale(new Locale("IT", "it"));
			initReport(eng);
			//
			Area ph = eng.getArea("PH");
			ph.getSection(0).setHeight(StyleUtils.toInch(50));
			Area details = eng.getArea("D");
			// -
			protocollazione(details);
			addBR(details, 50);
			testata(details, infoGenerali);
			addBR(details, 80);
			titoloMinore(details);
			// -
			// misureConcordate(details);
			// -
			addBR(details, 50);
			dichiarazioneLavoratore(details, infoGenerali);
			// -

			addBR(details, 50);
			// obbligoFormativo(details, obbligoFormativo);
			// -
			statoOccupazionale(details, statoOccupazionale, infoGenerali, cat181, laurea, movimenti);
			// -
			addBR(details, 50);
			misure(details, infoGenerali, azioniConcordate);

			// permessoDiSoggiorno(details, permessoDiSoggiorno);
			// -

			// collocamentoMirato(details, collocamentoMirato);
			// -
			/*
			 * addBR(details, 03); //listeMobilita(details, listeMobilita); //- addBR(details, 03);
			 * //indisponibilitaTemporanee(details, indispTemporanee); //- addBR(details, 03);
			 * //esperienzeProfessionali(details, esperienzeProfessionali); //- addBR(details, 03);
			 * //titoliStudio(details, titoliStudio); //- addBR(details, 03); //corsiFormazione(details,
			 * corsiFormazione); //- addBR(details, 20); // concordano(details); //??
			 * 
			 */

			addBR(details, 80);
			ambitoProfessionale(details, ambitoProfessionale, infoGenerali);
			// -
			addBR(details, 50);
			// indisponibilitaPressoImprese(details,
			// indisponibilitaPressoImprese);
			// -
			titoloDisponibilita(details);
			addBR(details, 50);
			azioniConcordate(details, azioniConcordate);
			// -
			addBR(details, 50);
			appuntamenti(details, appuntamenti);
			addBR(details, 100);
			impegni(details, impegni);
			// -

			// -

			// verifica(details, infoGenerali);
			// -

			informazioniUtente(details, infoGenerali);
			//

			// ////////////////////////////////
			Section section = details.addSection();
			// //////////////////////////////
			// System.out.println("costruzione report terminata. Salvataggio su
			// file");
			// RDC.saveEngine(new File("C:\\Programmi\\Crystal
			// Clear5.0\\report\\report.rpt"), eng);
			// RDC.saveEngine(new
			// File("C:/Progetti/SIL/sviluppo/applicazioni/sil/web/WEB-INF/report/pattoTEST.rpt"),
			// eng);
			// RDC.saveEngine(new File("d:\\report\\report.rpt"), eng);
			// System.out.println("end patto.java");
		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Ok
	 */
	private void protocollazione(Area details) throws Exception {
		Section section = details.addSection();
		Text t = addText(section, "Anno ", Style.ETICHETTA, LEFT, 500);
		addTextPart(t.getParagraph(0), this.numAnnoProt, Style.CAMPO);
		t = addText(section, "Num ", Style.ETICHETTA, 420);
		addTextPart(t.getParagraph(0), this.numProtocollo, Style.CAMPO);
		t = addText(section, "Data di prot. ", Style.ETICHETTA, 920);
		addTextPart(t.getParagraph(0), this.datProtocollazione, Style.CAMPO);
		section = details.addSection();
		t = addText(section, "Doc. di ", Style.ETICHETTA, LEFT);
		addTextPart(t.getParagraph(0), this.docInOut, Style.CAMPO);
		t = addText(section, "Rif. ", Style.ETICHETTA, 420);
		addTextPart(t.getParagraph(0), this.riferimentoDoc, Style.CAMPO);
	}

	private void verifica(Area details, SourceBean infoGenerali) throws Exception {
		String dataVerifica = (String) infoGenerali.getAttribute("DATSCADCONFERMA");
		Section section = details.addSection();
		addText(section, "VERIFICA DEL PATTO", Style.TITOLO2, LEFT_TITOLO_PUNTO);
		//
		section = details.addSection();
		addText(section, "Verifica il ", Style.ETICHETTA, LEFT_COL_T);
		addText(section, dataVerifica, Style.CAMPO, LEFT_COL_V);
	}

	private void misure(Area details, SourceBean row, Vector azioni) throws Exception {
		Section section = details.addSection();
		String deTipoPatto = (String) row.getAttribute("deTipoPattoMisura");
		Text t = addText(section, "", Style.ETICHETTA, LEFT);
		Paragraph p = t.getParagraph(0);
		addTextPart(p, deTipoPatto.trim(), Style.CAMPO);
		addTextPart(t.getParagraph(0), " con l'obiettivo di:", Style.ETICHETTA);
		addBR(details, 03);
		Set misure = new HashSet(azioni.size());
		for (int i = 0; i < azioni.size(); i++)
			misure.add(((SourceBean) azioni.get(i)).getAttribute("azione_ragg"));
		Iterator misureIter = misure.iterator();
		for (; misureIter.hasNext();) {
			// SourceBean azione = (SourceBean)azioni.get(i);
			String misura = (String) misureIter.next();
			titoloPuntato(details, misura);
		}
	}

	private void titoloDisponibilita(Area details) throws Exception {
		Section section = details.addSection();
		String s = "Il dettaglio delle disponibilità  dell'utente sono riportate in allegato e costituiscono"
				+ " parte integrante del presente patto.";
		addText(section, s, Style.ETICHETTA, LEFT);
	}

	private void ambitoProfessionale(Area details, Vector rows, SourceBean patto) throws Exception {
		Section section = details.addSection();
		Style s = StyleFactory.makeStyle(Style.ETICHETTA);
		s.setFontSize(12);
		if (rows.size() > 0) {
			addText(section, "nella mansione di:", s, LEFT);
			addBR(details, 50);
			for (int i = 0; i < rows.size(); i++) {
				SourceBean row = (SourceBean) rows.get(i);
				String mansione = (String) row.getAttribute("mansione");
				titoloPuntato(details, mansione, 12);
			}
		} else {
			Text t = addText(section, "ambito professionale di:", s, LEFT);
			// Paragraph p = t.getParagraph(0);
			String noteAmbitoProfessionale = (String) patto.getAttribute("strNoteAmbitoProf");
			if (noteAmbitoProfessionale != null) {
				s = StyleFactory.makeStyle(Style.CAMPO);
				s.setCanGrow(true);
				s.setWidth(StyleUtils.toInch(1200));
				addText(section, noteAmbitoProfessionale, s, LEFT + 450);
			}

		}
	}

	private void titoloMinore(Area details) throws Exception {
		Section section = details.addSection();
		String s = "Patto di adesione alle misure concordate con il Centro per l'Impiego per l'uscita dallo stato"
				+ " di disoccupazione ai sensi del D.Lgs 181/00 come modificato dal D.Lgs 150/15.";
		addText(section, s, Style.ETICHETTA, LEFT);
	}

	/**
	 * ?????????????????
	 */
	private void ambitoProfessionale(Area details, SourceBean row) throws Exception {
		if (row == null)
			row = new SourceBean("");
		String esperienza = (String) row.getAttribute("esperienza");
		String mansioneATempo = (String) row.getAttribute("mansioneATempo");
		String mansione = (String) row.getAttribute("mansione");
		String prgLavPattoScelta = (String) row.getAttribute("prgLavPattoScelta");
		String prgMansione = (String) row.getAttribute("prgMansione");
		//
		List misure = (List) row.getAttribute("misura");
		//
		List contratti = (ArrayList) row.getAttribute("contratti");
		List orari = (ArrayList) row.getAttribute("orari");
		List turni = (ArrayList) row.getAttribute("turni");
		//
		List comuni = (ArrayList) row.getAttribute("comuni");
		List provincie = (ArrayList) row.getAttribute("provincie");
		List regioni = (ArrayList) row.getAttribute("regioni");
		List stati = (ArrayList) row.getAttribute("stati");
		//
		MobilitaGeoWrapper geo = (MobilitaGeoWrapper) row.getAttribute("mobilitaGeo");
		//
		Section section = details.addSection();
		addText(section, "Mansione", Style.ETICHETTA, LEFT_COL_T);
		addText(section, mansione, Style.CAMPO, LEFT_COL_V);
		addText(section, "Esperienza", Style.ETICHETTA, LEFT2_COL_T);
		addText(section, esperienza, Style.CAMPO, LEFT2_COL_V);
		//
		if (mansioneATempo != null || mansioneATempo.equals("")) {
			section = details.addSection();
			addText(section, "Mansione a tempo", Style.ETICHETTA, LEFT_COL_T);
			addText(section, mansioneATempo, Style.CAMPO, LEFT_COL_V);
		}
		//
		coppia(details, "Misure concordate", misure, LEFT_COL_T, LEFT_COL_V);
		// VINCOLI
		if (contratti.size() > 0 || orari.size() > 0 || turni.size() > 0 || comuni.size() > 0 || provincie.size() > 0
				|| regioni.size() > 0 || stati.size() > 0) {
			if (contratti.size() > 0 || orari.size() > 0 || turni.size() > 0) {
				section = details.addSection();
				addText(section, "Vincoli", Style.TITOLO3, LEFT_TITOLO_PUNTO_T);
				coppia(details, "Contratti", contratti, LEFT_COL_T, LEFT_COL_V);
				coppia(details, "Orari", orari, LEFT_COL_T, LEFT_COL_V);
				coppia(details, "Turni", turni, LEFT_COL_T, LEFT_COL_V);
			}
			// TERRITORIO
			if (comuni.size() > 0 || provincie.size() > 0 || regioni.size() > 0 || stati.size() > 0) {
				section = details.addSection();
				addText(section, "Territorio", Style.TITOLO3, LEFT_TITOLO_PUNTO_T + 200);
				coppiaCompatta(details, "Comuni", comuni, LEFT_COL_T + 200, LEFT_COL_V + 200);
				coppiaCompatta(details, "Provincie", provincie, LEFT_COL_T + 200, LEFT_COL_V + 200);
				coppiaCompatta(details, "Regione", regioni, LEFT_COL_T + 200, LEFT_COL_V + 200);
				coppiaCompatta(details, "Stati", stati, LEFT_COL_T + 200, LEFT_COL_V + 200);
			}
		}
		// MOBILITA' GEOGRAFICA
		if (geo != null && geo.tipoTrasferta != null) {
			section = details.addSection();
			addText(section, "Mobilità geografica", Style.TITOLO3, LEFT_TITOLO_PUNTO_T);
			//
			section = details.addSection();
			addText(section, "Disponibilità auto", Style.ETICHETTA, LEFT_COL_T);
			addText(section, geo.utilizzoAuto, Style.CAMPO, LEFT_COL_V);
			addText(section, "Disponibilità moto", Style.ETICHETTA, LEFT2_COL_T);
			addText(section, geo.utilizzoMoto, Style.CAMPO, LEFT2_COL_V);
			//
			section = details.addSection();
			addText(section, "Pend. giornaliero", Style.ETICHETTA, LEFT_COL_T);
			addText(section, geo.pendGiornaliero, Style.CAMPO, LEFT_COL_V);
			addText(section, "Durata di perc. max", Style.ETICHETTA, LEFT2_COL_T);
			Text t = addText(section, geo.percentuale, Style.CAMPO, LEFT2_COL_V);
			addTextPart(t.getParagraph(0), " ore", Style.ETICHETTA);
			//
			section = details.addSection();
			addText(section, "Mob. settimanale", Style.ETICHETTA, LEFT_COL_T);
			addText(section, geo.mobSett, Style.CAMPO, LEFT_COL_V);
			//
			section = details.addSection();
			addText(section, "Tipo di trasferta", Style.ETICHETTA, LEFT_COL_T);
			addText(section, geo.tipoTrasferta, Style.CAMPO, LEFT_COL_V);
		}
	}

	private void coppiaCompatta(Area details, String titolo, List valori, int left, int right) throws Exception {
		if (valori == null || valori.size() == 0)
			return;
		Section section = details.addSection();
		addText(section, titolo, Style.ETICHETTA, left);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < valori.size(); i++) {
			sb.append((String) valori.get(i));
			sb.append(", ");
		}
		if (sb.length() > 2)
			sb.deleteCharAt(sb.length() - 2);
		Text t = addText(section, sb.toString(), Style.CAMPO, right, 1000);
		t.setCanGrow(true);
	}

	/**
	 * 
	 */
	private void coppia(Area details, String titolo, List valori, int left, int right) throws Exception {
		if (valori == null || valori.size() == 0)
			return;
		Section section = details.addSection();
		addText(section, titolo, Style.ETICHETTA, left);
		if (valori.size() > 0)
			addText(section, (String) valori.get(0), Style.CAMPO, right);
		for (int i = 1; i < valori.size(); i++) {
			section = details.addSection();
			addText(section, (String) valori.get(i), Style.CAMPO, right);
		}
	}

	private void impegni(Area details, Vector rows) throws Exception {

		Style s = StyleFactory.makeStyle(Style.ETICHETTA);
		s.setFontSize(12);
		Section section = details.addSection();
		addText(section, "In rispetto del presente Patto il lavoratore si impegna a:", s, LEFT);
		// //////////////////
		addBR(details, 50);
		//
		Vector impegniLavoratore = new Vector();
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			String codiceImp = (String) row.getAttribute("codMonoImpegnoDi");
			if (Utils.notNull(codiceImp).equals("S"))
				impegniLavoratore.add(row);
		}
		rows.removeAll(impegniLavoratore);
		impegniLavoratore(details, impegniLavoratore);
		//
		addBR(details, 100);
		section = details.addSection();
		addText(section, "Il CPI si impegna a:", s, LEFT);
		addBR(details, 50);
		impegniCPI(details, rows);
	}

	private void impegniLavoratore(Area details, Vector rows) throws Exception {
		if (rows == null)
			return;
		// Section section = details.addSection();
		// addText(section, "Il lavoratore si impegna a", Style.TITOLO_SEZIONE,
		// LEFT+50);
		// addBR(details, 03);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			impegni(details, row);
		}
	}

	private void impegniCPI(Area details, Vector rows) throws Exception {
		// addBR(details, 30);
		if (rows == null)
			return;
		// Section section = details.addSection();
		// addText(section, "Il Centro per l'impiego si impegna a",
		// Style.TITOLO_SEZIONE, LEFT+50);
		// addBR(details, 03);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			impegni(details, row);
		}
	}

	private void impegni(Area details, SourceBean impegni) throws Exception {
		Section section = details.addSection();
		Style s = StyleFactory.makeStyle(Style.CAMPO);
		s.setFontSize(12);
		section.addPicture(StyleUtils.toInch(LEFT + 50), StyleUtils.toInch(10), StyleUtils.toInch(20),
				StyleUtils.toInch(20), this.installAppPath + "/WEB-INF/report/img/punto_carta.gif");
		String impegno = (String) impegni.getAttribute("strDescrizione");
		Text t = addText(section, impegno, s, LEFT + 100);
	}

	private void appuntamenti(Area details, Vector rows) throws Exception {
		addBR(details, 30);
		// titoloPuntato(details,"4.","RINVIO AL SERVIZIO DI:");
		if (rows == null)
			return;
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			appuntamenti(details, row);
		}
	}

	/**
	 * Ok
	 */
	private void appuntamenti(Area details, SourceBean appuntamenti) throws Exception {
		if (appuntamenti == null)
			appuntamenti = new SourceBean("");
		String servizio = (String) appuntamenti.getAttribute("DESSERVIZIO");
		String data = (String) appuntamenti.getAttribute("DATA");
		//
		Section section = details.addSection();
		Text t = addText(section, "mediante presentazione al servizio di: ", Style.ETICHETTA, LEFT);
		Paragraph p = t.getParagraph(0);
		addTextPart(p, servizio, Style.CAMPO);
		section = details.addSection();
		t = addText(section, "Da svolgersi entro il: ", Style.ETICHETTA, LEFT);
		p = t.getParagraph(0);
		addTextPart(p, data, Style.CAMPO);
	}

	private void azioniConcordate(Area details, Vector rows) throws Exception {
		Section section = details.addSection();
		// titoloPuntato(details,"2.","LE SEGUENTI AZIONI PER MIGLIORARE LA
		// RICERCA DEL LAVORO");
		String dataPercorso = null;
		addText(section, "mediante l'azione di:", Style.ETICHETTA, LEFT);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			String dataPercTmp = (String) row.getAttribute("dataPercorso");
			if (dataPercorso != null && !dataPercorso.equals(dataPercTmp)) {
				dataAzioniConcordate(details, dataPercorso);
			}
			azioniConcordate(details, row);
			dataPercorso = dataPercTmp;
		}
		if (dataPercorso != null)
			dataAzioniConcordate(details, dataPercorso);
		// addBR(details, 50);
		// dateAzioniConcordate(details, rows);
	}

	/**
	 * Ok
	 */
	private void azioniConcordate(Area details, SourceBean azioniConcordate) throws Exception {
		if (azioniConcordate == null)
			azioniConcordate = new SourceBean("");
		String azione = (String) azioniConcordate.getAttribute("DESCRIZIONE");
		Section section = details.addSection();
		addText(section, "- " + azione, Style.CAMPO, LEFT);

		//
		/*
		 * Section section = details.addSection(); addText(section, "Azione",Style.ETICHETTA, LEFT_COL_T);
		 * addText(section,azione , Style.CAMPO, LEFT_COL_V); section = details.addSection(); addText(section,
		 * "Entro il",Style.ETICHETTA, LEFT_COL_T); addText(section,entroIl , Style.CAMPO, LEFT_COL_V);
		 */
		// titoloPuntato(details, "-", azione);
	}

	private void dateAzioniConcordate(Area details, Vector azioniConcordate) throws Exception {
		for (int i = 0; i < azioniConcordate.size(); i++) {
			SourceBean row = (SourceBean) azioniConcordate.get(i);
			String entroIl = (String) row.getAttribute("DATAPERCORSO");
			Section section = details.addSection();
			Text t = addText(section, "da svolgersi entro il: ", Style.ETICHETTA, LEFT);
			Paragraph p = t.getParagraph(0);
			addTextPart(p, entroIl, Style.CAMPO);
		}
	}

	private void dataAzioniConcordate(Area details, String dataAzione) throws Exception {
		addBR(details, 20);
		Section section = details.addSection();
		Text t = addText(section, "da svolgersi entro il: ", Style.ETICHETTA, LEFT);
		Paragraph p = t.getParagraph(0);
		addTextPart(p, dataAzione, Style.CAMPO);
		addBR(details, 50);
	}

	private void indisponibilitaPressoImprese(Area details, Vector rows) throws Exception {
		if (rows.size() == 0)
			return;
		Section section = details.addSection();
		addText(section, "Indisponibilità presso Imprese", Style.TITOLO_SEZIONE, LEFT);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			indisponibilitaPressoImprese(details, row);
		}
	}

	/**
	 * Ok
	 */
	private void indisponibilitaPressoImprese(Area details, SourceBean indisponibilita) throws Exception {
		if (indisponibilita == null)
			indisponibilita = new SourceBean("");
		String ragioneSociale = (String) indisponibilita.getAttribute("STRRAGSOCIALEAZIENDA");
		//
		Section section = details.addSection();
		addText(section, ragioneSociale, Style.CAMPO, LEFT_COL_V);
	}

	/**
	 * descrizione statica
	 */
	private void concordano(Area details) throws Exception {
		Section section = details.addSection();
		addText(section, "CONCORDANO", Style.TITOLO2CENTRATO, 0);
		addBR(details, 30);
		titoloPuntato(details, "1.", "LA SUA DISPONIBILITA' A SVOLGERE LE SEGUENTI ATTIVITA'");
		addBR(details, 20);

	}

	/**
	 * 
	 */
	private void titoloPuntato(Area details, String punto, String titolo) throws Exception {
		Section section = details.addSection();
		addText(section, punto, Style.TITOLO2, LEFT_TITOLO_PUNTO, 100);
		addText(section, titolo, Style.TITOLO2, LEFT_TITOLO_PUNTO_T);
	}

	private void titoloPuntato(Area details, String punto, String titolo, int size) throws Exception {
		Section section = details.addSection();
		addText(section, punto, Style.TITOLO2, LEFT_TITOLO_PUNTO, 100);
		Style s = StyleFactory.makeStyle(Style.CAMPO);
		s.setFontSize(size);
		addText(section, titolo, s, LEFT_TITOLO_PUNTO_T);
	}

	private void titoloPuntato(Area details, String titolo, int size) throws Exception {
		Section section = details.addSection();
		section.addPicture(StyleUtils.toInch(LEFT + 50), StyleUtils.toInch(10), StyleUtils.toInch(20),
				StyleUtils.toInch(20), this.installAppPath + "/WEB-INF/report/img/punto_carta.gif");
		Style s = StyleFactory.makeStyle(Style.CAMPO);
		s.setFontSize(size);
		addText(section, titolo, s, LEFT_TITOLO_PUNTO_T);
	}

	private void titoloPuntato(Area details, String titolo) throws Exception {
		Section section = details.addSection();
		section.addPicture(StyleUtils.toInch(LEFT + 50), StyleUtils.toInch(10), StyleUtils.toInch(20),
				StyleUtils.toInch(20), this.installAppPath + "/WEB-INF/report/img/punto_carta.gif");
		addText(section, titolo, Style.CAMPO, LEFT_TITOLO_PUNTO_T);
	}

	/**
	 * Ok
	 */
	private void informazioniUtente(Area details, SourceBean infoGenerali) throws Exception {
		if (infoGenerali == null)
			infoGenerali = new SourceBean("");
		String cpi = (String) infoGenerali.getAttribute("CPI");
		// String nProtocollo =
		// (String)infoGenerali.getAttribute("STRNUMPROTOCOLLO");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dataStipula = (String) infoGenerali.getAttribute("datStipula");
		String datScadConferma = (String) infoGenerali.getAttribute("datScadConferma");
		//
		Section section = details.addSection();
		section.setNewPageBefore(true);
		//
		Subreport sub = section.addSubreport(0, 0, StyleUtils.toInch(2000), StyleUtils.toInch(56),
				"file:" + this.installAppPath + "/WEB-INF/report/patto/_informazioniUtente_new_CC.rpt");
		sub.setLeftLineStyle(BorderProperties.NO_LINE);
		sub.setBottomLineStyle(BorderProperties.NO_LINE);
		sub.setTopLineStyle(BorderProperties.NO_LINE);
		sub.setRightLineStyle(BorderProperties.NO_LINE);
		Engine eng = sub.getEngine();
		eng.getArea("RH").setHeight(0);
		eng.getArea("RF").setHeight(0);
		eng.getArea("RH").setSuppress(true);
		eng.getArea("RF").setSuppress(true);
		//
		eng.setPrompt("cpi", cpi);
		eng.setPrompt("dataStipula", dataStipula);
		eng.setPrompt("dataScadenza", datScadConferma);
		//
		/*
		 * section = details.addSection(); //sub =
		 * section.addSubreport(0,0,StyleUtils.toInch(2000),StyleUtils.toInch(56),
		 * "file:C:/Progetti/SIL/sviluppo/applicazioni/sil/web/WEB-INF/report/patto/_tail_CC.rpt"); sub =
		 * section.addSubreport(0,0,StyleUtils.toInch(2000),StyleUtils.toInch(56),"file:"+this.installAppPath+
		 * "/WEB-INF/report/patto/_tail_CC.rpt"); sub.setLeftLineStyle(BorderProperties.NO_LINE);
		 * sub.setBottomLineStyle(BorderProperties.NO_LINE); sub.setTopLineStyle(BorderProperties.NO_LINE);
		 * sub.setRightLineStyle(BorderProperties.NO_LINE); eng = sub.getEngine(); eng.getArea("RH").setHeight(0);
		 * eng.getArea("RH").setSuppress(true); eng.getArea("RF").setHeight(0); eng.getArea("RF").setSuppress(true); //
		 * addBR(details, 03); section = details.addSection(); addText(section, cpi + " , li "+ dataOdierna,
		 * Style.CAMPO, 200); this.nProtocollo = (this.nProtocollo==null)?"":this.nProtocollo; addText(section, "Prot.
		 * N "+nProtocollo, Style.CAMPO, 1400);
		 * section.addHorizontalLine(StyleUtils.toInch(1320),StyleUtils.toInch(60),StyleUtils.toInch(400));
		 */
	}

	private void corsiFormazione(Area details, Vector rows) throws Exception {
		if (rows.size() == 0)
			return;
		//
		Section section = details.addSection();
		addText(section, "Ultima formazione professionale: precedente o in corso", Style.TITOLO_SEZIONE, LEFT);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			corsiFormazione(details, row);
		}
	}

	/**
	 * Ok
	 */
	private void corsiFormazione(Area details, SourceBean corsiFormazione) throws Exception {
		if (corsiFormazione == null)
			corsiFormazione = new SourceBean("");
		String flgCompletato = (String) corsiFormazione.getAttribute("FLGCOMPLETATO");
		String corso = (String) corsiFormazione.getAttribute("CORSO");
		BigDecimal anno = (BigDecimal) corsiFormazione.getAttribute("NUMANNO");
		//
		Section section = details.addSection();
		addText(section, "Nome del corso", Style.ETICHETTA, LEFT_COL_T);
		Text t = addText(section, corso, Style.CAMPO, LEFT_COL_V, 1140);
		t.setCanGrow(true);
		//
		section = details.addSection();
		addText(section, "Anno del corso", Style.ETICHETTA, LEFT_COL_T);
		if (anno != null)
			addText(section, anno.toString(), Style.CAMPO, LEFT_COL_V);
		addText(section, "Percorso completato", Style.ETICHETTA, LEFT2_COL_T);
		addText(section, flgCompletato, Style.CAMPO, LEFT2_COL_V);
	}

	private void titoliStudio(Area details, Vector rows) throws Exception {
		if (rows.size() == 0)
			return;
		//
		Section section = details.addSection();
		addText(section, "Titolo di studio", Style.TITOLO_SEZIONE, LEFT);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			titoliStudio(details, row);
		}
	}

	/**
	 * Ok
	 */
	private void titoliStudio(Area details, SourceBean titoliStudio) throws Exception {
		if (titoliStudio == null)
			titoliStudio = new SourceBean("");
		String tipoTitolo = (String) titoliStudio.getAttribute("DESTIPOTITOLO");
		String titolo = (String) titoliStudio.getAttribute("DESTITOLO");
		BigDecimal anno = (BigDecimal) titoliStudio.getAttribute("NUMANNO");
		//
		Section section = details.addSection();
		addText(section, "Tipo", Style.ETICHETTA, LEFT_COL_T);
		addText(section, tipoTitolo, Style.CAMPO, LEFT_COL_V);
		//
		section = details.addSection();
		addText(section, "Titolo di studio", Style.ETICHETTA, LEFT_COL_T);
		addText(section, titolo, Style.CAMPO, LEFT_COL_V);
		addText(section, "Anno", Style.ETICHETTA, LEFT2_COL_T);
		if (anno != null)
			addText(section, anno.toString(), Style.CAMPO, LEFT2_COL_V);

	}

	private void indisponibilitaTemporanee(Area details, Vector rows) throws Exception {
		//
		if (rows.size() == 0)
			return;
		Section section = details.addSection();
		// addText(section, "Indisponibilità temporanee", Style.TITOLO_SEZIONE,
		// LEFT);
		addText(section, "Condizioni", Style.TITOLO_SEZIONE, LEFT);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			indisponibilitaTemporanee(details, row);
		}
	}

	/**
	 * Ok
	 */
	private void indisponibilitaTemporanee(Area details, SourceBean indisponibilitaTemporanee) throws Exception {
		if (indisponibilitaTemporanee == null)
			indisponibilitaTemporanee = new SourceBean("");
		String tipoIndisponibilita = (String) indisponibilitaTemporanee.getAttribute("DESCRIZIONE");
		String inizio = (String) indisponibilitaTemporanee.getAttribute("DATINIZIO");
		String fine = (String) indisponibilitaTemporanee.getAttribute("DATFINE");
		//
		Section section = details.addSection();
		addText(section, "Tipo indisponibililtà", Style.ETICHETTA, LEFT_COL_T);
		addText(section, tipoIndisponibilita, Style.CAMPO, LEFT_COL_V);
		//
		Text t = addText(section, "", Style.CAMPO, 1400, 530);
		Paragraph p = t.getParagraph(0);
		addTextPart(p, " dal ", Style.ETICHETTA);
		addTextPart(p, inizio, Style.CAMPO);
		addTextPart(p, " al ", Style.ETICHETTA);
		addTextPart(p, fine, Style.CAMPO);
	}

	private void esperienzeProfessionali(Area details, Vector rows) throws Exception {
		if (rows.size() == 0)
			return;
		//
		Section section = details.addSection();
		addText(section, "nella mansione di:", Style.TITOLO_SEZIONE, LEFT);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			esperienzeProfessionali(details, row);
		}
	}

	/**
	 * Ok
	 */
	private void esperienzeProfessionali(Area details, SourceBean esperienzeProfessionali) throws Exception {
		if (esperienzeProfessionali == null)
			esperienzeProfessionali = new SourceBean("");
		String tipoEsperienza = (String) esperienzeProfessionali.getAttribute("DESCRIZIONECONTR");
		String mansione = (String) esperienzeProfessionali.getAttribute("DESCRIZIONEMANS");
		BigDecimal annoInizio = (BigDecimal) esperienzeProfessionali.getAttribute("NUMANNOINIZIO");
		BigDecimal annoFine = (BigDecimal) esperienzeProfessionali.getAttribute("NUMANNOFINE");
		BigDecimal meseFine = (BigDecimal) esperienzeProfessionali.getAttribute("NUMMESEFINE");
		BigDecimal meseInizio = (BigDecimal) esperienzeProfessionali.getAttribute("NUMMESEINIZIO");
		BigDecimal retribuzioneAnnua = (BigDecimal) esperienzeProfessionali.getAttribute("RETRIBANNUA");
		String meseInizioStr = null;
		String meseFineStr = null;
		titoloPuntato(details, mansione);
		//
		/*
		 * if (meseInizio!=null){ if (meseInizio.intValue()<10) meseInizioStr = "0"+meseInizio.intValue(); else
		 * meseInizioStr = meseInizio.toString(); } if (meseFine !=null) { if (meseFine.intValue()<10) meseFineStr =
		 * "0"+meseFine.intValue(); else meseFineStr = meseFine.toString(); } // Section section = details.addSection();
		 * addText(section, "Tipo esperienza",Style.ETICHETTA, LEFT_COL_T); addText(section, tipoEsperienza,
		 * Style.CAMPO, LEFT_COL_V); // section = details.addSection(); addText(section, "Mansione",Style.ETICHETTA,
		 * LEFT_COL_T); addText(section, mansione, Style.CAMPO, LEFT_COL_V); // section = details.addSection();
		 * addText(section, "Data inizio",Style.ETICHETTA, LEFT_COL_T); Text t = addText(section, meseInizioStr,
		 * Style.CAMPO, LEFT_COL_V); Paragraph p = t.getParagraph(0); addTextPart(p, " / ",Style.ETICHETTA); if
		 * (annoInizio!=null) addTextPart(p, annoInizio.toString(),Style.CAMPO); // addText(section,
		 * "Data fine",Style.ETICHETTA, LEFT2_COL_T); Text t1 = addText(section, meseFineStr, Style.CAMPO, LEFT2_COL_V);
		 * Paragraph p1 = t1.getParagraph(0); addTextPart(p1, " / ",Style.ETICHETTA); if (annoFine!=null)
		 * addTextPart(p1, annoFine.toString(),Style.CAMPO); // section = details.addSection(); addText(section,
		 * "Retribuzione annua",Style.ETICHETTA, LEFT_COL_T); if (retribuzioneAnnua!=null) addText(section,
		 * retribuzioneAnnua.toString(), Style.CAMPO, LEFT_COL_V);
		 */
	}

	private void listeMobilita(Area details, Vector rows) throws Exception {
		if (rows.size() == 0)
			return;
		//
		Section section = details.addSection();
		addText(section, "Liste speciali: mobilità", Style.TITOLO_SEZIONE, LEFT);
		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);
			listeMobilita(details, row);
		}
	}

	/**
	 * Ok
	 */
	private void listeMobilita(Area details, SourceBean listeMobilita) throws Exception {
		if (listeMobilita == null)
			listeMobilita = new SourceBean("");
		String dataInizio = (String) listeMobilita.getAttribute("DATINIZIO");
		String tipoLista = (String) listeMobilita.getAttribute("DESCRIZIONE");
		String indennita = (String) listeMobilita.getAttribute("FLGINDENNITA");
		//
		Section section = details.addSection();
		addText(section, "Data inizio", Style.ETICHETTA, LEFT_COL_T);
		addText(section, dataInizio, Style.CAMPO, LEFT_COL_V);
		//
		section = details.addSection();
		addText(section, "Tipo lista", Style.ETICHETTA, LEFT_COL_T);
		addText(section, tipoLista, Style.CAMPO, LEFT_COL_V);
		//
		section = details.addSection();
		addText(section, "Indennità", Style.ETICHETTA, LEFT_COL_T);
		addText(section, indennita, Style.CAMPO, LEFT_COL_V);
	}

	// private void collocamentoMirato(Area details, Vector rows) throws
	// Exception {}
	/**
	 * Ok
	 */
	private void collocamentoMirato(Area details, SourceBean collocamentoMirato) throws Exception {
		if (collocamentoMirato == null || !collocamentoMirato.containsAttribute("DATINIZIO"))
			return;
		String dataInizio = (String) collocamentoMirato.getAttribute("DATINIZIO");
		String iscrizione = (String) collocamentoMirato.getAttribute("DESCRIZIONEISCR");
		String invalidita = (String) collocamentoMirato.getAttribute("DESCRIZIONEINV");
		BigDecimal percentualeInvalidita = (BigDecimal) collocamentoMirato.getAttribute("NUMPERCINVALIDITA");
		if (percentualeInvalidita == null)
			percentualeInvalidita = new BigDecimal("0");
		//
		Section section = details.addSection();
		addText(section, "Liste speciali: collocamento mirato", Style.TITOLO_SEZIONE, LEFT);
		//
		section = details.addSection();
		addText(section, "Data inizio", Style.ETICHETTA, LEFT_COL_T);
		addText(section, dataInizio, Style.CAMPO, LEFT_COL_V);
		section = details.addSection();
		addText(section, "Tipo lista", Style.ETICHETTA, LEFT_COL_T);
		addText(section, iscrizione, Style.CAMPO, LEFT_COL_V);
		section = details.addSection();
		addText(section, "Tipo invalidità", Style.ETICHETTA, LEFT_COL_T);
		addText(section, invalidita, Style.CAMPO, LEFT_COL_V);
		addText(section, "Percentuale invalidità", Style.ETICHETTA, LEFT2_COL_T);
		addText(section, percentualeInvalidita.toString(), Style.CAMPO, LEFT2_COL_V);
	}

	/**
	 * dati anagrafici del lavoratore e data dichiarazione immediata disponibilita'
	 */
	private void dichiarazioneLavoratore(Area details, SourceBean infoLavoratore) throws Exception {
		if (infoLavoratore == null)
			infoLavoratore = new SourceBean("");
		Section section = details.addSection();
		String cpi = Utils.notNull(infoLavoratore.getAttribute("CPI"));
		String nome = Utils.notNull(infoLavoratore.getAttribute("NOMELAVORATORE"));
		String cognome = Utils.notNull(infoLavoratore.getAttribute("COGNOMELAVORATORE"));
		String codiceFiscale = (String) infoLavoratore.getAttribute("STRCODICEFISCALE");
		String flgSesso = Utils.notNull(infoLavoratore.getAttribute("sessoLavoratore"));
		String lavArticolo = "e la Signora ";

		//
		if ("M".equals(flgSesso.toUpperCase()))
			lavArticolo = "e il Signor ";
		//
		addText(section, "Il centro per l'impiego di " + cpi, Style.ETICHETTA, LEFT);

		section = details.addSection();
		Text t = addText(section, lavArticolo, Style.ETICHETTA, LEFT);
		addTextPart(t.getParagraph(0), cognome + " " + nome, Style.ETICHETTA);
		t = addText(section, "Cod. fisc. ", Style.ETICHETTA, 1350);
		addTextPart(t.getParagraph(0), codiceFiscale, Style.CAMPO);
		//

	}

	/**
	 * Ok
	 */
	private void statoOccupazionale(Area details, SourceBean statoOccupazionale, SourceBean infoLavoratore,
			SourceBean cat181Rows, SourceBean laureaRows, Vector movimenti) throws Exception {
		String dich1 = "che ha reso dichiarazione di immediata disponibilità allo svolgimento di attività lavorativa in data ";
		String dich2 = " e che si trova nella condizione di ";
		// non piu' voluta
		// String dich3 = ", con mesi di anzianità di disoccupazione ";
		String dich4 = ", concordano per l'uscita dallo stato di disoccupazione:";
		//
		int par = 0;
		String tipoDisoccupato = "";
		String cdnLavoratore = ((BigDecimal) infoLavoratore.getAttribute("cdnLavoratore")).toString();
		String dataDichiarazioneImmDisp = Utils.notNull(infoLavoratore.getAttribute("DATDICHIARAZIONE"));
		String statoOccupaz = (String) statoOccupazionale.getAttribute("DESCRIZIONESTATO");
		BigDecimal mesiAnzianita = (BigDecimal) statoOccupazionale.getAttribute("MESI_ANZ");
		if (mesiAnzianita == null)
			mesiAnzianita = new BigDecimal("0");
		String cat181 = (String) statoOccupazionale.getAttribute("DESCRIZIONE181");
		String codStatoOccRagg = (String) statoOccupazionale.getAttribute("codstatooccupazragg");
		BigDecimal mesiAnzPrec = (BigDecimal) statoOccupazionale.getAttribute("MESI_ANZ_PREC");
		String mesiAnzPrecInt = null;
		BigDecimal mesiAnz = null;
		String mesiAnzInt = null;
		BigDecimal numMesiSosp = null;
		String numMesiSospInt = null;
		numMesiSosp = (BigDecimal) statoOccupazionale.getAttribute("NUMMESISOSP");
		mesiAnz = (BigDecimal) statoOccupazionale.getAttribute("MESI_ANZ");
		BigDecimal numMesiSospPrec = null;
		String numMesiSospPrecInt = null;
		numMesiSospPrec = (BigDecimal) statoOccupazionale.getAttribute("NUMMESISOSPPREC");
		if (mesiAnz != null)
			mesiAnzInt = String.valueOf(mesiAnz.intValue());
		else
			mesiAnzInt = "0";
		if (mesiAnzPrec != null) {
			mesiAnzPrecInt = String.valueOf(mesiAnzPrec.intValue());
		} else
			mesiAnzPrecInt = "0";
		if (numMesiSospPrec != null) {
			numMesiSospPrecInt = String.valueOf(numMesiSospPrec.intValue());
		} else
			numMesiSospPrecInt = "0";
		if (numMesiSosp != null)
			numMesiSospInt = String.valueOf(numMesiSosp.intValue());
		else
			numMesiSospInt = "0";
		if (numMesiSospInt != null && Integer.parseInt(numMesiSospInt) < 0)
			numMesiSospInt = "0"; // null;
		if (numMesiSospPrecInt != null && Integer.parseInt(numMesiSospPrecInt) < 0)
			numMesiSospPrecInt = "0";
		if (mesiAnzInt != null && Integer.parseInt(mesiAnzInt) < 0)
			mesiAnzInt = "0";// null;
		if (mesiAnzPrecInt != null && Integer.parseInt(mesiAnzPrecInt) < 0)
			mesiAnzPrecInt = "0";
		BigDecimal eta = new BigDecimal("-1");
		String flgObbSco = null;
		String flgLaurea = "";
		String annoNascita = null;

		if (cat181Rows != null) {
			eta = (BigDecimal) cat181Rows.getAttribute("ANNI");
			flgObbSco = (String) cat181Rows.getAttribute("FLGOBBLIGOSCOLASTICO");
			annoNascita = (String) cat181Rows.getAttribute("datNasc");

		}

		flgLaurea = laureaRows != null && !laureaRows.containsAttribute("ROW") ? "S" : "N";
		cat181 = Controlli.getCat181(annoNascita, DateUtils.getNow(), flgObbSco, flgLaurea);

		String codCat181 = null;
		String dataFineMovimento = null;
		BigDecimal mesiInattivita = null;
		it.eng.sil.module.movimenti.InfoLavoratore _lav = new it.eng.sil.module.movimenti.InfoLavoratore(
				new BigDecimal(cdnLavoratore));
		if (cat181 != null && cat181.equalsIgnoreCase("GIOVANE"))
			codCat181 = "G";
		if (movimenti != null && movimenti.size() > 0) {
			SourceBean row = (SourceBean) movimenti.get(0);
			dataFineMovimento = (String) row.getAttribute("DATAFINEMOVIMENTO");
			mesiInattivita = (BigDecimal) row.getAttribute("mesiInattivita");
		}
		// Numero effettivo dimesi di anzianità
		BigDecimal totMesiAnz = new BigDecimal(
				String.valueOf(Integer.parseInt(mesiAnzPrecInt) + Integer.parseInt(mesiAnzInt)
						- (Integer.parseInt(numMesiSospPrecInt) + Integer.parseInt(numMesiSospInt))));
		tipoDisoccupato = Controlli.disoccInoccLungaDurata(codStatoOccRagg, totMesiAnz, codCat181);// mesiAnz
		String sesso = _lav.getSesso();
		boolean donnaInReinserimento = Controlli.donnaInInserimentoLavorativo(codStatoOccRagg, mesiInattivita, sesso);

		/*
		 * String codCat181 = Utils.notNull(statoOccupazionale.getAttribute("CODCATEGORIA181")); if
		 * ((mesiAnzianita.intValue()>12 || mesiAnzianita.intValue()>6) && codCat181.toUpperCase().equals("G"))
		 * tipoDisoccupato = "disoccupato/inoccupato di lunga durata";
		 */
		Section section = details.addSection();
		Text t = addText(section, dich1, Style.ETICHETTA, LEFT);
		addTextPart(t.getParagraph(par), dataDichiarazioneImmDisp, Style.CAMPO);
		addTextPart(t.getParagraph(par), dich2, Style.ETICHETTA);
		addTextPart(t.getParagraph(par), statoOccupaz, Style.CAMPO);
		// ultime richieste (31/01/2005) nascondere questa informazione
		// addTextPart(t.getParagraph(par), dich3, Style.ETICHETTA);
		addTextPart(t.getParagraph(par), totMesiAnz.toString(), Style.CAMPO);
		if (cat181 != null && !cat181.equals("")) {
			addTextPart(t.getParagraph(par), ", ", Style.ETICHETTA);
			addTextPart(t.getParagraph(par), cat181, Style.CAMPO);
		}
		if (tipoDisoccupato != null && !tipoDisoccupato.equals("")) {
			addTextPart(t.getParagraph(par), ", ", Style.ETICHETTA);
			addTextPart(t.getParagraph(par), tipoDisoccupato, Style.CAMPO);
		}
		/*
		 * if (cat181 !=null && !cat181.equals("")) { addTextPart(t.getParagraph(par), ", ", Style.ETICHETTA);
		 * addTextPart(t.getParagraph(par), cat181, Style.CAMPO); }
		 */
		if (donnaInReinserimento) {
			addTextPart(t.getParagraph(par), ", ", Style.ETICHETTA);
			addTextPart(t.getParagraph(par), "donna in reinserimento lavorativo", Style.CAMPO);
		}
		addTextPart(t.getParagraph(par), dich4, Style.ETICHETTA);
		/*
		 * section.addPicture(StyleUtils.toInch(LEFT+50), StyleUtils.toInch(10), StyleUtils.toInch(20),
		 * StyleUtils.toInch(20), this.installAppPath+"/img/punto_carta.gif"); addText(section, dich2,Style.ETICHETTA3,
		 * LEFT + 200, 1430);
		 */

		/*
		 * 
		 * if (statoOccupazionale==null) statoOccupazionale = new SourceBean(""); String tipoDisoccupato = ""; String
		 * dataInizio = (String)statoOccupazionale.getAttribute("DATINIZIO");
		 * 
		 * BigDecimal mesiAnzianita = (BigDecimal)statoOccupazionale.getAttribute("MESI_ANZ"); if (mesiAnzianita==null)
		 * mesiAnzianita = new BigDecimal("0"); String cat181 =
		 * (String)statoOccupazionale.getAttribute("DESCRIZIONE181"); String flgPensionato
		 * =(String)statoOccupazionale.getAttribute("FLGPENSIONATO"); BigDecimal reddito =
		 * (BigDecimal)statoOccupazionale.getAttribute("NUMREDDITO"); if (reddito==null) reddito = new BigDecimal("0");
		 * String dataAnzianitaDisocc = (String)statoOccupazionale.getAttribute("DATANZIANITADISOC"); String
		 * flgIndennizzato = (String)statoOccupazionale.getAttribute("FLGINDENNIZZATO"); BigDecimal numMesiSosp =
		 * (BigDecimal)statoOccupazionale.getAttribute("NUMMESISOSP"); if (numMesiSosp==null) numMesiSosp = new
		 * BigDecimal("0"); String codCat181 = Utils.notNull(statoOccupazionale.getAttribute("CODCATEGORIA181"));
		 * 
		 * if ((mesiAnzianita.intValue()>12 || mesiAnzianita.intValue()>6) && codCat181.toUpperCase().equals("G"))
		 * tipoDisoccupato = "disoccupato/inoccupato di lunga durata"; else if (mesiAnzianita.intValue()>24 )
		 * tipoDisoccupato = "soggetto alla legge 407/90"; // Section section = details.addSection(); addText(section,
		 * "Notizie sullo stato occupazionale", Style.TITOLO_SEZIONE, LEFT); // section = details.addSection();
		 * addText(section, "Inizio",Style.ETICHETTA, LEFT_COL_T); addText(section, dataInizio, Style.CAMPO,
		 * LEFT_COL_V); section = details.addSection(); addText(section, "Stato occupazionale",Style.ETICHETTA,
		 * LEFT_COL_T); Text t = addText(section, statoOccupaz,Style.CAMPO, LEFT_COL_V , 1200); t.setCanGrow(true);
		 * section = details.addSection(); addText(section, "Categoria dlg 181",Style.ETICHETTA, LEFT_COL_T);
		 * addText(section, cat181, Style.CAMPO, LEFT_COL_V); addText(section,
		 * "Pensionato",Style.ETICHETTA,LEFT2_COL_T); addText(section, flgPensionato, Style.CAMPO, LEFT2_COL_V); section
		 * = details.addSection(); addText(section, "Reddito", Style.ETICHETTA, LEFT_COL_T); addText(section,
		 * reddito.toString() , Style.CAMPO, LEFT_COL_V); // section = details.addSection(); addText(section,
		 * "Anzianità di disoccupazione", Style.CAMPO, LEFT+150, 1000); section = details.addSection(); addText(section,
		 * "Dal", Style.ETICHETTA, LEFT_COL_T); addText(section , dataAnzianitaDisocc, Style.CAMPO, LEFT_COL_V);
		 * addText(section, "Mesi sosp.", Style.ETICHETTA, LEFT2_COL_T); addText(section, numMesiSosp.toString(),
		 * Style.CAMPO, LEFT2_COL_V); section = details.addSection(); addText(section, "Mesi anzianità",
		 * Style.ETICHETTA,LEFT_COL_T); addText(section, mesiAnzianita.toString(), Style.CAMPO, LEFT_COL_V); section =
		 * details.addSection(); addText(section, "Indennizzato", Style.ETICHETTA,LEFT_COL_T);
		 * addText(section,flgIndennizzato , Style.CAMPO, LEFT_COL_V);
		 */
	}

	/**
	 * Ok
	 */
	private void permessoDiSoggiorno(Area details, SourceBean permessoDiSoggiorno) throws Exception {
		if (permessoDiSoggiorno == null || !permessoDiSoggiorno.containsAttribute("DATRICHIESTA"))
			return;
		String dataScadenza = (String) permessoDiSoggiorno.getAttribute("DATSCADENZA");
		String dataRichiesta = (String) permessoDiSoggiorno.getAttribute("DATRICHIESTA");
		String descrizioneMotivo = (String) permessoDiSoggiorno.getAttribute("DESCRIZIONEMOT");
		String descrizioneRichiesta = (String) permessoDiSoggiorno.getAttribute("DESCRIZIONERICH");
		//
		Section section = details.addSection();
		addText(section, "Notizie sui cittadini stranieri", Style.TITOLO_SEZIONE, LEFT);
		//
		section = details.addSection();
		addText(section, "Scadenza permesso di soggiorno", Style.ETICHETTA, LEFT_COL_T);
		addText(section, dataScadenza, Style.CAMPO, LEFT_COL_V);
		section = details.addSection();
		addText(section, "Data richiesta/sanatoria", Style.ETICHETTA, LEFT_COL_T);
		addText(section, dataRichiesta, Style.CAMPO, LEFT_COL_V);
		section = details.addSection();
		addText(section, "Stato richiesta", Style.ETICHETTA, LEFT_COL_T);
		addText(section, descrizioneRichiesta, Style.CAMPO, LEFT_COL_V);
		section = details.addSection();
		addText(section, "Motivo permesso di soggiorno", Style.ETICHETTA, LEFT_COL_T);
		addText(section, descrizioneMotivo, Style.CAMPO, LEFT_COL_V);
	}

	/**
	 * Ok
	 */
	private void obbligoFormativo(Area details, SourceBean obbligoFormativo) throws Exception {
		if (obbligoFormativo == null)
			obbligoFormativo = new SourceBean("");
		String flgObbFoAssolto = (String) obbligoFormativo.getAttribute("FLGOBBLIGOFORMATIVO");
		String flgOggScolAssolto = (String) obbligoFormativo.getAttribute("FLGOBBLIGOSCOLASTICO");
		String modalita = (String) obbligoFormativo.getAttribute("DESCRIZIONE");
		//
		Section section = details.addSection();
		addText(section, "Notizie sull' assolvimento dell'obbligo formativo ", Style.TITOLO_SEZIONE, LEFT);
		section = details.addSection();
		addText(section, "Obbligo formativo assolto", Style.ETICHETTA, LEFT_COL_T);
		addText(section, flgObbFoAssolto, Style.CAMPO, LEFT_COL_V);
		addText(section, "Obbligo scolastico assolto", Style.ETICHETTA, LEFT2_COL_T);
		addText(section, flgOggScolAssolto, Style.CAMPO, LEFT2_COL_V);
		section = details.addSection();
		Text t = addText(section, "Modalità di assolvimento obbligo", Style.ETICHETTA, LEFT_COL_T);
		t.setCanGrow(true);
		addText(section, modalita, Style.CAMPO, LEFT_COL_V);

	}

	private void addBR(Area details, int height) throws Exception {
		Section section = details.addSection();
		section.setHeight(StyleUtils.toInch(height));
	}

	private void initReport(Engine eng) throws Exception {
		
		// om20201211: fix orientation and size
		eng.getReportProperties().setPaperOrient(ReportProperties.DEFAULT_PAPER_ORIENTATION,ReportProperties.PAPER_A4);
		
		Area header = eng.getArea("RH");
		header.setSuppress(true);
		Section sec = header.getSection(0);
		sec.setHeight(StyleUtils.toInch(0));
		//
		header = eng.getArea("PH");
		sec = header.getSection(0);
		sec.setHeight(StyleUtils.toInch(100));
		//
		Area footer = eng.getArea("RF");
		sec = footer.getSection(0);
		sec.setHeight(StyleUtils.toInch(0));
		//
		footer = eng.getArea("PF");
		sec = footer.getSection(0);
		sec.setHeight(StyleUtils.toInch(100));
		//
		Fields fields = eng.getFields();
		SpecialField sp = fields.getSpecialField(SpecialField.PAGE_N_OF_M);
		Section s = footer.getSection(0);
		s.addFieldElement(sp, StyleUtils.toInch(1700), 0, StyleUtils.toInch(300), StyleUtils.toInch(85));
	}

	/**
	 * Descrizione statica misure concordate tra il cpi ed il lavoratore
	 */
	private void misureConcordate(Area details) throws Exception {
		Section section = details.addSection();
		Text t = section.addText(StyleUtils.toInch(300), StyleUtils.toInch(50), StyleUtils.toInch(1300),
				StyleUtils.toInch(100));
		Paragraph p = t.addParagraph();
		TextPart tp = p.addTextPart("MISURE CONCORDATE tra il centro per l'Impiego e la persona in stato"
				+ "di disoccupazione nell'ambito degli adempimenti previsti dal D.Lgs 181/2000, cosi' modificato dal D.Lgs 150/2015");
		t.setCanGrow(true);
		tp.setBold(true);
		tp.setFontSize(11);
	}

	/**
	 * Intestazione report. OK
	 */
	private void testata(Area area, SourceBean row) throws Exception {
		if (row == null)
			row = new SourceBean("");
		//
		String cpi = (String) row.getAttribute("CPI");
		String provincia = (String) row.getAttribute("PROVINCIA");
		String tel = (String) row.getAttribute("TEL");
		String fax = (String) row.getAttribute("FAX");
		String eMail = (String) row.getAttribute("EMAIL");
		String indirizzo = (String) row.getAttribute("INDIRIZZO");
		//
		Section sec = area.addSection();
		sec.setHeight(StyleUtils.toInch(250));

		Box box = sec.addBox(StyleUtils.toInch(80), 0, StyleUtils.toInch(1370), StyleUtils.toInch(205));

		//
		Text text = sec.addText(StyleUtils.toInch(100), StyleUtils.toInch(5), StyleUtils.toInch(1250),
				StyleUtils.toInch(80));
		Paragraph p = text.addParagraph();
		TextPart tp = p.addTextPart("CENTRO PER L'IMPIEGO DI " + Utils.notNull(cpi).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		tp.setUnderline(true);
		//
		text = sec.addText(StyleUtils.toInch(100), StyleUtils.toInch(95), StyleUtils.toInch(350),
				StyleUtils.toInch(50));
		p = text.addParagraph();
		tp = p.addTextPart("tel. " + Utils.notNull(tel));
		Style s = StyleFactory.makeStyle(Style.CAMPO);
		s.setStyle(tp);
		tp.setBold(true);
		//
		text = sec.addText(StyleUtils.toInch(470), StyleUtils.toInch(95), StyleUtils.toInch(350),
				StyleUtils.toInch(50));
		p = text.addParagraph();
		tp = p.addTextPart("fax. " + Utils.notNull(fax));
		s = StyleFactory.makeStyle(Style.CAMPO);
		s.setStyle(tp);
		tp.setBold(true);
		//
		/*
		 * text = sec.addText(StyleUtils.toInch(840), StyleUtils.toInch(100), StyleUtils.toInch(580),
		 * StyleUtils.toInch(80)); p = text.addParagraph(); tp = p.addTextPart("e-mail. " + Utils.notNull(eMail));
		 */
		text = sec.addText(StyleUtils.toInch(100), StyleUtils.toInch(150), StyleUtils.toInch(580),
				StyleUtils.toInch(50));
		p = text.addParagraph();
		tp = p.addTextPart("e-mail. " + Utils.notNull(eMail));
		s = StyleFactory.makeStyle(Style.CAMPO);
		s.setStyle(tp);
		tp.setBold(true);
		// /////////
		sec.addBox(StyleUtils.toInch(1450), 0, StyleUtils.toInch(500), StyleUtils.toInch(205));
		text = sec.addText(StyleUtils.toInch(1600), StyleUtils.toInch(5), StyleUtils.toInch(350),
				StyleUtils.toInch(80));
		p = text.addParagraph();
		tp = p.addTextPart("PROVINCIA DI " + Utils.notNull(provincia).toUpperCase());
		tp.setFontSize(12);
		tp.setFontName("Arial");
		tp.setBold(true);
		text.setCanGrow(true);
		//
		// sec.addPicture(StyleUtils.toInch(1480),StyleUtils.toInch(26),
		// 1215,1350,
		// this.installAppPath+"/WEB-INF/report/img/_loghiAvuti/logo_BO.gif");
		sec.addPicture(StyleUtils.toInch(1455), StyleUtils.toInch(26), 795, 870,
				this.installAppPath + "/WEB-INF/report/img/_loghiAvuti/logo_BO.gif");
		Vector elements = sec.getElementsV();
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) instanceof Picture) {
				((Element) elements.get(i)).setSuppress(true);
			}
		}

	}

	/**
	 * 
	 */
	public Text addText(Section section, String text, int style, int x) throws Exception {
		Style s = StyleFactory.makeStyle(style);
		Text t = section.addText(StyleUtils.toInch(x), 0, s.getWidth(), s.getHeight());

		Paragraph p = t.addParagraph();
		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);

		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		s.setStyle(section);
		return t;
	}

	public Text addText(Section section, String text, int style, int x, int width) throws Exception {
		Style s = StyleFactory.makeStyle(style);
		Text t = section.addText(StyleUtils.toInch(x), 0, StyleUtils.toInch(width), s.getHeight());

		Paragraph p = t.addParagraph();
		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);

		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		s.setStyle(section);
		return t;
	}

	public Paragraph addTextPart(Paragraph p, String text, int style) throws Exception {
		Style s = StyleFactory.makeStyle(style);

		// p.setX(0);
		// p.setForeColor(RDC.COLOR_PURPLE);

		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		// s.setStyle(p);
		s.setStyle(tp);

		return p;
	}

	public Text addText(Section section, String text, Style s, int x) throws Exception {
		// Style s = StyleFactory.makeStyle(style);
		Text t = section.addText(StyleUtils.toInch(x), 0, s.getWidth(), s.getHeight());

		Paragraph p = t.addParagraph();
		p.setX(0);
		p.setForeColor(RDC.COLOR_PURPLE);

		TextPart tp = p.addTextPart(Utils.notNull(text));
		tp.setX(0);
		s.setStyle(p);
		s.setStyle(t);
		s.setStyle(tp);
		s.setStyle(section);
		return t;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		try {
			Patto p = new Patto(args);
		} finally {
			System.out.println("exit");
			System.exit(0);
		}
	}

	private class MobilitaGeoWrapper {
		String utilizzoAuto;
		String pendGiornaliero;
		String mobSett;
		String tipoTrasferta;
		String utilizzoMoto;
		String percentuale;
	}

	private Vector getMansioni(Vector mansioniRows) throws Exception {

		//
		SourceBean row_Mansioni = null;
		String Mansione = null;
		String esperienza = null;
		String Misura_concordata = null;
		String Contratti = null;
		String Orario = null;
		String Turni = null;
		String Territorio = null;
		String Comuni = null;
		String Province = null;
		String Regioni = null;
		String Stato = null;
		String mansioneATempo = null;
		String disponibile = null;
		String formazione = null;
		String inserimentoProfessionale = null;
		// String indisponibilita = null;
		// variabili mobilita' geografica
		String utilizzoAuto = null;
		String utilizzoMoto = null;
		String pendolarismoGiornaliero = null;
		String mobilitaSett = null;
		String tipoTrasferta = null;
		String durataPercorrenzaMax = null;
		String MansionePrec = null;
		//

		boolean nuovaMansione = false;
		Vector mansioni = new Vector();
		SourceBean rowBean = new SourceBean("ROW");
		if (mansioniRows.size() > 0) {
			MansionePrec = (String) ((SourceBean) mansioniRows.elementAt(0)).getAttribute("MANSIONE");
		}
		ArrayList contratti = new ArrayList();
		ArrayList orari = new ArrayList();
		ArrayList turni = new ArrayList();
		ArrayList comuni = new ArrayList();
		ArrayList provincie = new ArrayList();
		ArrayList regioni = new ArrayList();
		ArrayList stati = new ArrayList();
		// String MansionePrec=null;
		MobilitaGeoWrapper geo = new MobilitaGeoWrapper();
		// int duegiri=2;
		boolean stampa = true;
		for (int n = 0; n < mansioniRows.size(); n++) {
			// if (mansioniRows.size()>0) {
			row_Mansioni = (SourceBean) mansioniRows.elementAt(n);
			int ordine = ((BigDecimal) row_Mansioni.getAttribute("ORDINE")).intValue();
			// if (ordine==1) {
			// MansionePrec=(String)row_Mansioni.getAttribute("MANSIONE");
			// }

			switch (ordine) {
			case 2:
				contratti.add(row_Mansioni.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {// MansionePrec =
													// "";ordine=1;
					stampa = false;
					row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
					row_Mansioni.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 3:
				orari.add(row_Mansioni.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {// MansionePrec =
													// "";ordine=1;
					stampa = false;
					row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
					row_Mansioni.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 4:
				turni.add(row_Mansioni.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {// MansionePrec =
													// "";ordine=1;
					stampa = false;
					row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
					row_Mansioni.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 5:
				comuni.add(row_Mansioni.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {// MansionePrec =
													// "";ordine=1;
					stampa = false;
					row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
					row_Mansioni.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 6:
				provincie.add(row_Mansioni.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {// MansionePrec =
													// "";ordine=1;
					stampa = false;
					row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
					row_Mansioni.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 7:
				regioni.add(row_Mansioni.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {// MansionePrec =
													// "";ordine=1;
					stampa = false;
					row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
					row_Mansioni.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 8:
				stati.add(row_Mansioni.getAttribute("VINCOLO"));
				if (mansioniRows.size() == n + 1) {// MansionePrec =
													// "";ordine=1;
					stampa = false;
					row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
					row_Mansioni.updAttribute("MANSIONE", "");
					n--;
				}
				break;
			case 9:
				//
				utilizzoAuto = (String) row_Mansioni.getAttribute("FLGDISPAUTO");
				utilizzoAuto = (utilizzoAuto == null) ? "" : utilizzoAuto;
				utilizzoMoto = (String) row_Mansioni.getAttribute("flgDispMoto");
				utilizzoMoto = (utilizzoMoto == null) ? "" : utilizzoMoto;
				pendolarismoGiornaliero = (String) row_Mansioni.getAttribute("flgPendolarismo");
				pendolarismoGiornaliero = (pendolarismoGiornaliero == null) ? "" : pendolarismoGiornaliero;
				mobilitaSett = (String) row_Mansioni.getAttribute("FLGMOBSETT");
				mobilitaSett = (mobilitaSett == null) ? "" : mobilitaSett;
				tipoTrasferta = (String) row_Mansioni.getAttribute("VINCOLO");
				durataPercorrenzaMax = Utils.notNull(row_Mansioni.getAttribute("NUMOREPERC"));
				durataPercorrenzaMax = (durataPercorrenzaMax == null) ? "" : durataPercorrenzaMax;
				//
				geo.pendGiornaliero = pendolarismoGiornaliero;
				geo.percentuale = durataPercorrenzaMax;
				geo.tipoTrasferta = tipoTrasferta;
				geo.utilizzoAuto = utilizzoAuto;
				geo.utilizzoMoto = utilizzoMoto;
				geo.mobSett = mobilitaSett;
				if (mansioniRows.size() != n + 1)
					break;
				else {
					MansionePrec = "";
					ordine = 1;
					stampa = false;
				}
				// se sono alla fine del ciclo debbo registrare le info
				// accumulate
				// otterro' che la mansione letta e quella precedente saranno
				// diverse
			case 1:
				Mansione = (String) row_Mansioni.getAttribute("MANSIONE");
				if (!Mansione.equals(MansionePrec)) {
					rowBean.setAttribute("contratti", contratti);
					rowBean.setAttribute("orari", orari);
					rowBean.setAttribute("turni", turni);
					rowBean.setAttribute("comuni", comuni);
					rowBean.setAttribute("provincie", provincie);
					rowBean.setAttribute("regioni", regioni);
					rowBean.setAttribute("stati", stati);
					rowBean.setAttribute("mobilitaGeo", geo);

					mansioni.add(rowBean);
					//
					MansionePrec = Mansione;

				}
				mansioneATempo = (String) row_Mansioni.getAttribute("TEMPO");
				// mansioneATempo = (mansioneATempo.equals(""))? "9"
				// :mansioneATempo;
				if (mansioneATempo != null) {
					switch (mansioneATempo.charAt(0)) {
					case 'D':
						mansioneATempo = "Tempo determinato";
						break;
					case 'I':
						mansioneATempo = "Tempo indeterminato";
						break;
					case 'E':
						mansioneATempo = "Tempo det/indet";
						break;
					}
				}
				esperienza = Utils.notNull(row_Mansioni.getAttribute("ESPERIENZA"));
				disponibile = (String) row_Mansioni.getAttribute("FLGDISPONIBILE");
				inserimentoProfessionale = (String) row_Mansioni.getAttribute("FLGPIP");
				formazione = (String) row_Mansioni.getAttribute("FLGDISPFORMAZIONE");
				String prgMansione = row_Mansioni.getAttribute("prgMansione").toString();
				String prgLavPattoScelta = row_Mansioni.getAttribute("prgLavPattoScelta").toString();
				ArrayList al = new ArrayList(3);
				if (inserimentoProfessionale != null)
					al.add("Progetti di inserimento");
				if (formazione != null)
					al.add("Percorsi formativi");
				if (disponibile != null)
					al.add("Attività lavorativa");
				rowBean = new SourceBean("ROW");
				rowBean.setAttribute("mansioneATempo", Utils.notNull(mansioneATempo));
				rowBean.setAttribute("esperienza", esperienza);
				rowBean.setAttribute("mansione", Mansione);
				rowBean.setAttribute("prgLavPattoScelta", prgLavPattoScelta);
				rowBean.setAttribute("prgMansione", prgMansione);
				rowBean.setAttribute("misura", al);
				// inizializza arrays
				contratti = new ArrayList();
				orari = new ArrayList();
				turni = new ArrayList();
				comuni = new ArrayList();
				provincie = new ArrayList();
				regioni = new ArrayList();
				stati = new ArrayList();
				geo = new MobilitaGeoWrapper();
				// caso particolare in cui siamo nell' ultima mansione ed un
				// solo ordine=1
				if (mansioniRows.size() == n + 1 && stampa) {
					rowBean.setAttribute("contratti", contratti);
					rowBean.setAttribute("orari", orari);
					rowBean.setAttribute("turni", turni);
					rowBean.setAttribute("comuni", comuni);
					rowBean.setAttribute("provincie", provincie);
					rowBean.setAttribute("regioni", regioni);
					rowBean.setAttribute("stati", stati);
					rowBean.setAttribute("mobilitaGeo", geo);
					mansioni.add(rowBean);
				}
				break;
			default: // per il momento non fare niente. Una situazione del
						// genere non deve verificarsi
			}// switch
		}
		return mansioni;
	}

}
