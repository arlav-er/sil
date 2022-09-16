package it.eng.sil.test.coop.clicLavoro.ricercaPersonale;

import java.math.BigDecimal;
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
public class RicercaDiPersonaleTestSuiteLingua {

	private static final int minPrgRichiestaAz = 3050;
	private static final int maxPrgRichiestaAz = 3050;

	public static Test suite() {
		System.setProperty("CONTEXT_NAME", "sil");
		BaseTestCase.getInstance();

		TestSuite suite = new TestSuite("Sample Tests");

		Object[] inputParameters = new Object[2];
		inputParameters[0] = minPrgRichiestaAz;
		inputParameters[1] = maxPrgRichiestaAz;

		SourceBean test = (SourceBean) QueryExecutor.executeQuery("RP_GET_TESTCASES", inputParameters, "SELECT",
				Values.DB_SIL_DATI);
		Vector<SourceBean> test_cases = test.getAttributeAsVector("ROW");
		for (int i = 0; i < test_cases.size(); i++) {
			SourceBean testCase = test_cases.get(i);
			BigDecimal bdRichiesta = (BigDecimal) testCase.getAttribute("PRGRICHIESTAAZ");
			BigDecimal bdAlternativa = (BigDecimal) testCase.getAttribute("PRGALTERNATIVA");
			String codCPI = "085003400";
			String codiceOfferta = "codice";

			RicercaDiPersonaleTestLingua caso = new RicercaDiPersonaleTestLingua(bdRichiesta, bdAlternativa, codCPI,
					codiceOfferta);

			suite.addTest(caso);

		}
		return suite;
	}
}