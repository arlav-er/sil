package it.eng.sil.coop.webservices.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TsEndpoint {

	private String endPoint = null;

	public TsEndpoint(Connection connection, String servizio) throws Exception {
		PreparedStatement psUrl = null;
		String endPointCurr = null;
		ResultSet rsUrl = null;

		String statementUrl = "SELECT strName, strUrl, codProvincia, flgpoloattivo FROM TS_ENDPOINT WHERE strName = '"
				+ servizio + "'";
		psUrl = connection.prepareStatement(statementUrl);

		rsUrl = psUrl.executeQuery();
		if (rsUrl.next()) {
			endPointCurr = rsUrl.getString("strUrl");
		}

		rsUrl.close();
		psUrl.close();

		this.endPoint = endPointCurr;
	}

	public String getEndpointUrl() throws Exception {
		return this.endPoint;
	}
}