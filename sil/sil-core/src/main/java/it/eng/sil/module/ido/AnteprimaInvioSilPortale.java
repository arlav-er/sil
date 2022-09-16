package it.eng.sil.module.ido;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Types;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.ido.vacancy.Vacancy;

public class AnteprimaInvioSilPortale extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7732946136762795948L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AnteprimaInvioSilPortale.class.getName());

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		DataConnectionManager dcm = null;
		DataConnection dc = null;
		Connection con = null;
		CallableStatement commandXML = null;
		String prgRichiesta = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();

		try {
			dcm = DataConnectionManager.getInstance();
			dc = dcm.getConnection(Values.DB_SIL_DATI);
			con = dc.getInternalConnection();

			String xmlGenerato = "";

			String statement = "{ call ? := PG_PORTALE.GETXMLVACANCYSIL (?,?) }";

			commandXML = con.prepareCall(statement);

			commandXML.registerOutParameter(1, Types.CLOB);
			commandXML.setBigDecimal(2, new BigDecimal(prgRichiesta));
			commandXML.registerOutParameter(2, Types.BIGINT);
			commandXML.setString(3, "S");
			commandXML.registerOutParameter(3, Types.VARCHAR);

			commandXML.executeQuery();

			Clob result = commandXML.getClob(1);

			if (result != null) {
				if (result.length() > 0) {
					xmlGenerato = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
					xmlGenerato += result.getSubString(1, (int) result.length());
					Vacancy vacancy = getVacancy(xmlGenerato);
					serviceResponse.setAttribute("ANTEPRIMA", vacancy);
				}
			} else {
				_logger.error("Errore recupero vacancy con progressivo:" + prgRichiesta);
				reportOperation.reportFailure(MessageCodes.IDO.ERR_CHECK_VACANCY_ANTEPRIMA);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (commandXML != null) {
				commandXML.close();
			}
			Utils.releaseResources(dc, null, null);
		}
	}

	private Vacancy getVacancy(String xmlVacancy) throws JAXBException {
		JAXBContext jaxbContext;
		Vacancy vac = null;
		jaxbContext = JAXBContext.newInstance(it.eng.sil.module.ido.vacancy.ObjectFactory.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		vac = (Vacancy) jaxbUnmarshaller.unmarshal(new StringReader(xmlVacancy));
		return vac;
	}
}
