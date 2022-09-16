package it.eng.sil.test.coop.clicLavoro.ricercaPersonale;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.CLRicercaPersonaleData;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import junit.framework.TestCase;

/**
 * Costruisce un test per una richiesta di personale. Il test consiste nella costruzione e validazione dell'xml tramite
 * xsd
 * 
 * @author rodi
 *
 */
public class RicercaDiPersonaleTestCostruzione extends TestCase {

	static Logger _logger = Logger.getLogger(RicercaDiPersonaleTestCostruzione.class.getName());

	private BigDecimal prgRichiestaAz = new BigDecimal(50067);
	private BigDecimal prgAlternativa = new BigDecimal(1);
	private String codTipoComunicazioneCl = "01";

	private String codCPI;

	public RicercaDiPersonaleTestCostruzione(BigDecimal bdRichiesta, BigDecimal bdAlternativa, String codCPI) {
		this.prgRichiestaAz = bdRichiesta;
		this.prgAlternativa = bdAlternativa;
		this.codCPI = codCPI;
	}

	@Override
	protected void runTest() {

		_logger.info("Test case) prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa);

		try {
			CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(prgRichiestaAz, prgAlternativa, codCPI,
					codTipoComunicazioneCl);
			risposta.costruisci(false);
			String xml = risposta.generaXML();
			_logger.info("xml generato:\n" + xml);
		} catch (EMFUserError e) {
			fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa
					+ ". Errore nella generazione xml: " + e.getDescription());
			e.printStackTrace();
		} catch (MandatoryFieldException e) {
			fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa
					+ ". Errore nella generazione xml: " + e.getExceptionMessage());
			e.printStackTrace();
		} catch (FieldFormatException e) {
			fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa
					+ ". Errore nella generazione xml: " + e.getExceptionMessage());
			e.printStackTrace();
		}

		/*
		 * } catch (CLRicercaPersonaleException e) { switch (e.getCodice()) { case
		 * CLRicercaPersonale.CODE_XML_VALIDATION: fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " +
		 * prgAlternativa + ". Errore nella generazione xml: " + e.getDescrizione()); break; case
		 * CLRicercaPersonale.CODE_INPUT_ERRATO: fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " +
		 * prgAlternativa + ". Errore nei dati di input"); break; case CLRicercaPersonale.CODE_SB_UPDATE:
		 * fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa + ". " +e.getDescrizione());
		 * case CLRicercaPersonale.CODE_CAMPOOBBLIGATORIO_NOTFOUND: fail("prgrichiesta: " + prgRichiestaAz +
		 * ", prgalternativa: " + prgAlternativa + ". " +e.getDescrizione()); case CLRicercaPersonale.CODE_DATI_SPORCHI:
		 * fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa + ". " +e.getDescrizione());
		 * default: fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa +
		 * ". Errore generico"); break; }
		 * 
		 * }
		 */
	}

}
