package com.engiweb.framework.dbaccess.test;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.InformationDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

public class TestInsert {
	public static void main(String[] args) {
		DataConnection dataConnection = null;
		try {
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			dataConnection = dcm.getConnection();
			testFixedInsert(dataConnection);
			testVariableInsert(dataConnection);
		} catch (Exception eie) {
			eie.printStackTrace();
		} // end try-cath
		finally {
			Utils.releaseResources(dataConnection, null, null);
		}
	} // public static void main(String[] args)

	public static void testFixedInsert(DataConnection dc) {
		SQLCommand insertCommand = null;
		DataResult dr = null;
		try {
			String queryString = "INSERT INTO TAB_PROVA(ID, DESCRIZIONE, DATA_INS, VALORE)  VALUES (10, 'valore 10',  TO_DATE('1900-01-01','YYYY-MM-DD') , 10.67)";
			insertCommand = dc.createInsertCommand(queryString);
			dr = insertCommand.execute();
			InformationDataResult idr = (InformationDataResult) dr.getDataObject();
			System.out.println(" Eseguito comando e ottenuto result set.... continua ");
			System.out.println(" Effettuate le close continua ");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Utils.releaseResources(null, insertCommand, dr);
		}
	} // public static void testFixedInsert(DataConnection dc)

	public static void testVariableInsert(DataConnection dc) {
		SQLCommand insertCommand = null;
		DataResult dr = null;
		try {
			System.out.println(" Query Variabile Iniziata");
			String queryString = "INSERT INTO TAB_PROVA(ID, DESCRIZIONE, DATA_INS, VALORE)  VALUES (?, ?,  ? , ?)";
			insertCommand = dc.createInsertCommand(queryString);
			List inputParameters = new ArrayList();
			inputParameters.add(0, dc.createDataField("ID", Types.DECIMAL, new BigDecimal(15)));
			inputParameters.add(1, dc.createDataField("DESCRIZIONE", Types.VARCHAR, "valore Parametrico"));
			// inputParameters.add(2,dc.createDataField("DATA_INS",Types.TIMESTAMP,"10-12-2001"));
			inputParameters.add(2, dc.createDataField("DATA_INS", Types.TIMESTAMP, "10-12-2001 01:01:57"));
			inputParameters.add(3, dc.createDataField("VALORE", Types.DOUBLE, new Double(1.4)));
			dr = insertCommand.execute(inputParameters);
			System.out.println(" Comando di insert Eseguito");
			InformationDataResult idr = (InformationDataResult) dr.getDataObject();
			System.out.println(" Eseguito comando e ottenuto result set.... continua ");
			System.out.println(" Effettuate le close continua ");
			String querySelect = "SELECT * FROM TAB_PROVA";
			SQLCommand selectCommand = dc.createSelectCommand(querySelect);
			ScrollableDataResult sdr = (ScrollableDataResult) selectCommand.execute().getDataObject();
			System.out.println((sdr.getSourceBean().toXML()));
			return;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Utils.releaseResources(null, insertCommand, dr);
		}
	} // public static void testVariableInsert(DataConnection dc)
}
