/*
 * Creato il 29-ott-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import it.eng.sil.bean.Documento;
import it.eng.sil.module.movimenti.constant.Properties;

/**
 * @author Alessandro Pegoraro
 * 
 *         Lo Stomper si occupa di apporre su un PDF già generato il numero di protocollo. Per aggiungere una stampa
 *         alla lista dei tipi di documento trattati dallo stomper occorre modificare il metodo isProtPosticipated()
 *         della classe Documento
 * 
 *         chiunque denigri questa classe è un impenitente. Sappiate che si può disabilitare tutto nella RDFDBLocale, ma
 *         poi sono cazzi vostri
 * 
 */
public class pdfStomper {

	/* Logger statico per il debug */
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(pdfStomper.class.getName());

	/**
	 * Restituisce la posizione in cui va collocato il Timbro, sulla base del codtipodocumento
	 * 
	 * @author Alessandro Pegoraro
	 * @param Documento
	 *            Il Documento da Protocollare
	 * @return le coordinate in cui il timbro andrà posizionato
	 * @since 2.1.0l_test
	 * @see Documento
	 * 
	 *      Aggiunta gestione posizioni in provincia Trento (CODREGIONE = 22) Se dovessero nascere nuove necessità di
	 *      configurazione e doveste inorridire leggendo questo metodo si può sempre inserire questi dati in
	 *      TS_CONFIG_CODIFICA...
	 * 
	 *      x si conta da sinistra, Y dal basso
	 * 
	 */
	private Point getPos(Documento doc, String regione) {
		Point to_ret = new Point();
		/* Scheda anag professionale */
		if (doc.getCodTipoDocumento().equalsIgnoreCase("SAP"))
			to_ret.setLocation(160, 777);
		/* DID */
		if (doc.getCodTipoDocumento().equalsIgnoreCase("IM")) {
			if (regione.equals("10"))// Umbria
				to_ret.setLocation(169, 598);
			else
				to_ret.setLocation(178, 800);
		}
		/* Agenda stampa giornaliera */
		if (doc.getCodTipoDocumento().equalsIgnoreCase("LTC")) {
			// regione emilia romagna: logo unione europea oppure regione calabria
			if (regione.equals("8") || regione.equals("18")) {
				to_ret.setLocation(160, 740);
			}	
			else {
				if (regione.equals("10")) {
					to_ret.setLocation(160, 701);
				}
				else {
					to_ret.setLocation(160, 728);
				}
			}
		}
		/* Scheda situaz. lav */
		if (doc.getCodTipoDocumento().equalsIgnoreCase("SSL"))
			to_ret.setLocation(160, 696);
		/* SITUAZIONE LAVORATIVA PATRONATO */
		// TOGLIERE I COMMENTI QUANDO TI DIRA' DI INSERIRE IL NUM PROTOCOLLO
		// NELLA STAMPA.
		if (doc.getCodTipoDocumento().equalsIgnoreCase("SSLP"))
			to_ret.setLocation(-100, -100);

		/* Trasferimento */
		if (doc.getCodTipoDocumento().equalsIgnoreCase("TRDOC"))
			to_ret.setLocation(190, 705);
		/* Trasferimento */
		if (doc.getCodTipoDocumento().equalsIgnoreCase("TRCPI"))
			to_ret.setLocation(190, 683);
		/* Patto */
		if (doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DEFAULT_DOCUMENTO_PATTO)
				|| doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DOCUMENTO_PATTO_GENERICO))
			if (("22").compareTo(regione) == 0) {
				if (doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DEFAULT_DOCUMENTO_PATTO)) {
					to_ret.setLocation(290, 680);
				} else {
					to_ret.setLocation(184, 762); //762
				}
			} else {
				if (regione.equals("10")) {// Umbria
					to_ret.setLocation(184, 571);
				}
				else {
					to_ret.setLocation(184, 762);
				}
			}
		/* Dichiarazioni/attestazioni */
		if (doc.getCodTipoDocumento().equalsIgnoreCase("DICH")) {
			if (("22").compareTo(regione) == 0) {
				to_ret.setLocation(204, 665);
			} else {
				if (("10").compareTo(regione) == 0) {
					to_ret.setLocation(204, 690);
				} else {
					to_ret.setLocation(204, 709);
				}
			}
		}
		if (doc.getCodTipoDocumento().equalsIgnoreCase("IMDICANN")) {
			to_ret.setLocation(63, 687);
		}

		return to_ret; // default, ma non dovrebbe mai essere eseguito
	}

	/**
	 * Restituisce la dimensione del font con cui stampare il numero di protocollo
	 * 
	 * @author Alessandro Pegoraro
	 * @param Documento
	 *            Il Documento da Protocollare
	 * @return la dimensione del font espressa in pixel
	 * @since 2.1.0l_test
	 * 
	 */
	private int getFontSize(Documento doc, String regione) {
		if (doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DEFAULT_DOCUMENTO_PATTO)
				|| doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DOCUMENTO_PATTO_GENERICO)) {
			if (("22").compareTo(regione) == 0) {
				return 9;
			} else {
				return 11;
			}
		}
		if (doc.getCodTipoDocumento().equalsIgnoreCase("DICH"))
			return 12;
		if (doc.getCodTipoDocumento().equalsIgnoreCase("IMDICANN"))
			return 12;
		/* Le stampe seguenti sono protocollate, ma il num NON appare */
		if (doc.getCodTipoDocumento().equalsIgnoreCase("CUL"))
			return 1;
		if (doc.getCodTipoDocumento().equalsIgnoreCase("CUE"))
			return 1;
		if (doc.getCodTipoDocumento().equalsIgnoreCase("CUA"))
			return 1;
		if (doc.getCodTipoDocumento().equalsIgnoreCase("SSLP"))
			return 12;

		return 10; // default
	}

	/**
	 * Restituisce lo stile del font con cui stampare il numero di protocollo
	 * 
	 * @author Alessandro Pegoraro
	 * @param regione
	 * @param Documento
	 *            Il Documento da Protocollare
	 * @return il codice (intero) che rappresenta il font con cui stampare
	 * @since 2.1.0l_test
	 * 
	 */
	private String getFont(Documento doc, String regione) {
		if (doc.getCodTipoDocumento().equalsIgnoreCase("LTC")) {
			return BaseFont.HELVETICA_BOLD;
		}
		if (doc.getCodTipoDocumento().equalsIgnoreCase("DICH")) {
			return BaseFont.HELVETICA_BOLD;
		}
		if (doc.getCodTipoDocumento().equalsIgnoreCase("IMDICANN")) {
			return BaseFont.HELVETICA_BOLD;
		}
		if (doc.getCodTipoDocumento().equalsIgnoreCase("IM")) {
			return BaseFont.HELVETICA_BOLD;
		}
		if (doc.getCodTipoDocumento().equalsIgnoreCase("SSL")) {
			return BaseFont.HELVETICA;
		}
		if (doc.getCodTipoDocumento().equalsIgnoreCase("TRDOC")) {
			return BaseFont.HELVETICA;
		}
		if (doc.getCodTipoDocumento().equalsIgnoreCase("TRCPI")) {
			return BaseFont.HELVETICA;
		}
		if (doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DEFAULT_DOCUMENTO_PATTO)
				|| doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DOCUMENTO_PATTO_GENERICO)) {
			if (("22").compareTo(regione) == 0) {
				return BaseFont.HELVETICA_BOLD;
			} else {
				return BaseFont.TIMES_BOLDITALIC;
			}
		}

		return BaseFont.TIMES_BOLD; // default
	}

	/**
	 * Aggiunge un timbro al pdf in input. Serve per posticipare il lock della protocollazione
	 * 
	 * @author Alessandro Pegoraro
	 * @param stomp_text
	 *            Il testo da inserire (numero diprotocollo)
	 * @param in
	 *            il File pdf in input
	 * @param doc
	 *            il documento che si sta modificando, serve per stabilire la posizione del timbro
	 * 
	 * @return il File .pdf modificato
	 * @since 2.1.0l_test
	 * @throws un'Eccezione
	 *             se non è possibile aprire/scrivere il file di output
	 */
	public File aggiungi_timbro(String stomp_text, File in, Documento doc, String regione) throws Exception {

		File to_ret = null;
		PdfReader pdf = null;
		PdfStamper stp = null;
		BaseFont bf = null;

		to_ret = File.createTempFile("in_stamper", "out_stamper");

		/* Ottiene il Font per la stampa */
		bf = BaseFont.createFont(getFont(doc, regione), BaseFont.WINANSI, BaseFont.EMBEDDED);
		pdf = new PdfReader(in.getAbsolutePath());
		stp = new PdfStamper(pdf, new FileOutputStream(to_ret));

		/* Ottiene la posizione del timbro */
		Point where = getPos(doc, regione);
		PdfContentByte over;
		/* Tibra sulla prima pagina */
		over = stp.getOverContent(1);
		over.beginText();
		/* Setta la dimensione del font, default 10 */
		over.setFontAndSize(bf, getFontSize(doc, regione));

		if (regione.equalsIgnoreCase("2") && doc.getCodTipoDocumento() != null
				&& doc.getCodTipoDocumento().equalsIgnoreCase("IM") && doc.getServizioOnLine() != null
				&& doc.getServizioOnLine().equalsIgnoreCase("DID")) {
			over.showTextAligned(Element.ALIGN_LEFT, stomp_text, where.x, where.y, 0);
		} else {
			if (!regione.equalsIgnoreCase("2")
					|| ((doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DEFAULT_DOCUMENTO_PATTO)
							|| doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DOCUMENTO_PATTO_GENERICO)
							|| doc.getCodTipoDocumento().equalsIgnoreCase("SSL")) && regione.equalsIgnoreCase("2"))) {

				if ((regione.equalsIgnoreCase("8"))
						&& (doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DEFAULT_DOCUMENTO_PATTO)
								|| doc.getCodTipoDocumento().equalsIgnoreCase(Properties.DOCUMENTO_PATTO_GENERICO))) {
					over.showTextAligned(Element.ALIGN_TOP, doc.getNumProtocollo().toString(), where.x, where.y - 90, 0);
				} else {
					over.showTextAligned(Element.ALIGN_LEFT, stomp_text, where.x, where.y, 0);
				}
			} else {
				if (regione.equalsIgnoreCase("2") && doc.getCodTipoDocumento().equalsIgnoreCase("IM")) {
					over.showTextAligned(Element.ALIGN_LEFT, stomp_text, where.x, where.y, 0);
				}
			}
		}
		
		if (doc.getCodTipoDocumento().equalsIgnoreCase("SSLP")) {
			over.setFontAndSize(bf, 12);
			over.showTextAligned(Element.ALIGN_LEFT, doc.getDatProtocollazione(), 450, 773, 0);
		}
		
		over.endText();

		/* Rilascio le Risorse allocate */
		stp.close();
		pdf.close();
		in.delete();

		_logger.info("Aggiunto Stomp: " + stomp_text + " su " + doc.getStrNomeDoc() + " in posizione x: " + where.x + "y: " + where.y);

		return to_ret;
	}

	/**
	 * Classe Test-only
	 */
	public static void main(String[] args) {

		pdfStomper tester = new pdfStomper();
		// tester.aggiungi_timbro("Prot.Num=1234532", "c:\\prova.pdf",
		// "c:\\out.pdf");
	}

}