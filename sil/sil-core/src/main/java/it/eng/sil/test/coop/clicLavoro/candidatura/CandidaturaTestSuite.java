package it.eng.sil.test.coop.clicLavoro.candidatura;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.junit.BaseTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/*I test vengono effettuati con i seguenti filtri:
 * data pubblicazione è diverso da null
 * il codice mansione dell'azienda è presente e corretto 
 */
public class CandidaturaTestSuite {

	private static final int minCdnLavoratore = 221467;
	private static final int maxCdnLavoratore = 221467;

	public static Test suite() {
		String codCPI = "085003400";
		String dataInvio = "30/05/2011";
		System.setProperty("_ENCRYPTER_KEY_", "chiaveCM");
		System.setProperty("CONTEXT_NAME", "sil");

		BaseTestCase bt = BaseTestCase.getInstance();

		TestSuite suite = new TestSuite("Sample Tests");

		Object[] inputParameters = new Object[2];
		inputParameters[0] = minCdnLavoratore;
		inputParameters[1] = maxCdnLavoratore;

		SourceBean test = (SourceBean) QueryExecutor.executeQuery("CL_GET_TESTCASES", inputParameters, "SELECT",
				Values.DB_SIL_DATI);
		Vector<SourceBean> test_cases = test.getAttributeAsVector("ROW");
		for (int i = 0; i < test_cases.size(); i++) {
			SourceBean testCase = test_cases.get(i);
			String codiceFiscale = (String) testCase.getAttribute("strcodicefiscale");

			CandidaturaTestCase caso = new CandidaturaTestCase(codiceFiscale, codCPI, dataInvio);

			suite.addTest(caso);

		}
		return suite;
	}
}