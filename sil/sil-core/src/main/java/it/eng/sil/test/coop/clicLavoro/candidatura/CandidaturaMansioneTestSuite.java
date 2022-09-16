package it.eng.sil.test.coop.clicLavoro.candidatura;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;
import it.eng.sil.junit.BaseTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class CandidaturaMansioneTestSuite {
	private static final int minCdnLavoratore = 165760;
	private static final int maxCdnLavoratore = 167760;

	public static Test suite() {
		String codCPI = "085003400";
		String dataInvio = "30/05/2011";
		System.setProperty("CONTEXT_NAME", "sil");
		TestSuite test = new TestSuite(
				"Classe Test (Inserimento e verifica Mansione, Esperienza di Lavoro e Tipo Contratto)");

		Object[] inputParameters = new Object[2];
		inputParameters[0] = minCdnLavoratore;
		inputParameters[1] = maxCdnLavoratore;

		BaseTestCase.getInstance();

		SourceBean sb = (SourceBean) QueryExecutor.executeQuery("CL_GET_TESTCASES", inputParameters, "SELECT",
				Values.DB_SIL_DATI);
		Vector<SourceBean> test_cases = sb.getAttributeAsVector("ROW");
		for (int i = 0; i < test_cases.size(); i++) {
			SourceBean sbTestCase = test_cases.get(i);
			String codiceFiscale = (String) sbTestCase.getAttribute("strcodicefiscale");
			BigDecimal cdnLavoratore = (BigDecimal) sbTestCase.getAttribute("cdnlavoratore");

			CandidaturaMansioneTestCase caso = new CandidaturaMansioneTestCase(codiceFiscale, cdnLavoratore, codCPI,
					dataInvio);

			test.addTest(caso);

		}
		return test;
	}
}
