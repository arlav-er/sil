package it.eng.sil.test.coop.clicLavoro.candidatura;

import org.apache.log4j.Logger;

import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.coop.webservices.clicLavoro.candidatura.CLCandidaturaData;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import junit.framework.TestCase;

public class CandidaturaTestCase extends TestCase {

	static Logger _logger = Logger.getLogger(CandidaturaTestCase.class.getName());
	private String codiceFiscaleLavoratore;
	private String codCPI;
	private String dataInvio;

	public CandidaturaTestCase() {
	}

	public CandidaturaTestCase(String codiceFiscaleLavoratore, String codCPI, String dataInvio) {
		this.codiceFiscaleLavoratore = codiceFiscaleLavoratore;
		this.codCPI = codCPI;
		this.dataInvio = dataInvio;
	}

	@Override
	protected void runTest() {

		CLCandidaturaData risposta = new CLCandidaturaData(codiceFiscaleLavoratore, codCPI, dataInvio, null);

		System.out.println("Test case) codice fiscale: " + codiceFiscaleLavoratore);

		try {
			risposta.costruisci();
			risposta.generaXML();
		} catch (MandatoryFieldException e) {
			e.printStackTrace();
			fail("codice fiscale: " + codiceFiscaleLavoratore + ". Errore nella generazione xml: "
					+ e.getExceptionMessage());
		} catch (FieldFormatException e) {
			e.printStackTrace();
			fail("codice fiscale: " + codiceFiscaleLavoratore + ". Errore nella generazione xml: "
					+ e.getExceptionMessage());
		} catch (EMFUserError e) {
			e.printStackTrace();
			fail("codice fiscale: " + codiceFiscaleLavoratore + ". Errore nella generazione xml: " + e.getSeverity());
		}

	}

}
