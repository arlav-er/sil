package it.eng.sil.util.amministrazione.impatti;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.util.QueryExecutor;

public class TestBatch {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TestBatch.class.getName());

	public TestBatch() {
		try {
			ApplicationContainer.getInstance();
			String stm = SQLStatements.getStatement("GET_OBBLIGO_FORMATIVO");
			System.out.println(stm);
			System.out.println("connection manager istanziato");

			try {
				SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_DID_VALIDA", new Object[] { "467" },
						"SELECT", "SIL_DATI");
				System.out.println(row.toXML());
				it.eng.sil.util.TraceWrapper.debug(_logger, "test", new Exception("cdcdc"));

			} catch (Exception sql) {
				sql.printStackTrace();
			}

			ApplicationContainer.getInstance().release();
			System.out.println("connection manager rilasciato");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("end...");
		}
	}

	public static void main(String[] args) {
		TestBatch testBach = new TestBatch();
	}
}