package com.engiweb.framework.dbaccess.test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;

public class TestGetConnectio {
	public static String queryString = "select banca.cod_abiswift ABI ," + "banca.desc_ragsoc DENOMINAZIONE_BANCA,"
			+ "sportello.cod_cabswift CAB," + "rapporto.cod_rapporto CC,"
			+ "info_poe.get_desc_cc(cc.id) DESCRIZIONE_RAPPORTO," + "rapporto.cod_divisa DIVISA," + "s.data_saldo DATA,"
			+ "s.imp_saldo_contabile_ricevuto SALDO " + "FROM conto_corrente cc," + "rapporto," + "sportello,"
			+ "banca," + "utente_telematico ut," + "saldo_di_conto_corrente s " + "where s.conto_corrente = cc.id AND "
			+ "s.data_saldo in (select max(data_saldo) from saldo_di_conto_corrente where conto_corrente = cc.id) and "
			+ "UT.COD_SIA='DEMO2' AND UT.COD_PIVA=UT.COD_FISCALE AND " + "sportello.banca = banca.id and "
			+ "rapporto.id = cc.id and " + "rapporto.sportello = sportello.id and "
			+ "rapporto.utente_telematico = UT.ID and " + "banca.cod_abiswift = '05728' and "
			+ "info_poe.is_expired(cc.data_estinzione) = 'N' " + "order by " + "rapporto.DATA_ACCENSIONE desc";
	public static String BLANK = " ";
	public static String parQuery = "select" + BLANK + "a.ID ID," + "DESCRIZIONE_RAPPORTO," + "CODICE_ABI_BANCA,"
			+ "CODICE_CAB_BANCA," + "CONTO_CORRENTE," + "nvl(CODICE_DIVISA, 'ITL') DIVISA," + "DATA_CONTABILE,"
			+ "SALDO_INIZIALE_QUADRATURA," + "SALDO_CONTABILE_FINALE," + "SALDO_LIQUIDO_FINALE,"
			+ "CONTROVALORE_SALDO_FINALE NUM_CONTROVALORE_SALDO_FINALE," + "DATA_LIQUIDITA_PRIMO_SALDO,"
			+ "PRIMO_SALDO_LIQUIDO," + "DATA_LIQUIDITA_SECONDO_SALDO," + "SECONDO_SALDO_LIQUIDO,"
			+ "DATA_LIQUIDITA_TERZO_SALDO," + "TERZO_SALDO_LIQUIDO," + "DATA_LIQUIDITA_QUARTO_SALDO,"
			+ "QUARTO_SALDO_LIQUIDO," + "DATA_LIQUIDITA_QUINTO_SALDO," + "QUINTO_SALDO_LIQUIDO," + "DE1ABI,"
			+ "COD_RAPPORTO," + "info_poe.is_expired(cc.data_estinzione) SCADUTO " + "from" + BLANK
			+ "saldo_iniziale_finale_liq a," + BLANK + "rapporto b," + BLANK + "pf_abi c," + BLANK + "conto_corrente cc"
			+ BLANK + "where" + BLANK + "a.CONTO_CORRENTE = ?" + BLANK + "and a.CONTO_CORRENTE = b.ID" + BLANK
			+ "and cc.ID = b.ID" + BLANK
			+ "and a.DATA_CONTABILE = (SELECT MAX(DATA_CONTABILE) FROM saldo_iniziale_finale_liq where CONTO_CORRENTE = ?)"
			+ BLANK + "and CODABI (+) = a.CODICE_ABI_BANCA";

	public static void main(String[] args) {
		DataConnection dataConnection = null;
		SQLCommand command = null;
		DataResult dr = null;
		try {
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			dataConnection = dcm.getConnection();

			/*
			 * for (int i=0; i < 10; i++){ variableQuery(dataConnection, parQuery); }
			 */

			String strSelect = "SELECT id_licenza,nome,descrizione,versione,tipo,tipo_macchina,categoria,note,id_modifier,data_modifica FROM LICENZA WHERE DELETED=0 ORDER BY categoria";
			command = dataConnection.createSelectCommand(strSelect);
			// Chiamo la stored procedure
			dr = command.execute();
			ScrollableDataResult resultInterface = (ScrollableDataResult) dr.getDataObject();
			Vector result = new Vector(resultInterface.getRowsNumber());
			// <<<<<<<<<<<<<---------- E QUI SCOPPIA TUTTO
			while (resultInterface.hasRows()) {
				// Trasformo il DataRow in un DataBean
				DataRow row = resultInterface.getDataRow();
				// DataBean dataBean = create();
				// rowToDataBean(row, dataBean);
				// result.addElement(dataBean);
				System.out.println("-----------------");
				System.out.println(row.getSourceBean().toXML());
				System.out.println("-----------------");
			}
		} catch (EMFInternalError eie) {
			System.out.println(eie.getDescription());
		} finally {
			Utils.releaseResources(dataConnection, command, dr);
		}
	} // end main

	public static void fixedQuery(DataConnection dc) {
		SQLCommand selectCommand = null;
		DataResult dr = null;
		try {
			selectCommand = dc.createSelectCommand(queryString);
			dr = selectCommand.execute();
			if (dr.getDataResultType().equals("SCROLLABLE_DATA_RESULT")) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				System.out.println(sdr.getRowsNumber());
				// Scrollable DataResult
				while (sdr.hasRows()) {
					DataRow row = sdr.getDataRow();
					System.out.println((row.getColumn("ABI")).getObjectValue());
				}
				sdr.moveTo(1);
				SourceBean sourceBean = sdr.getSourceBean();
				System.out.println(sourceBean.toXML());
				System.out.println(" Eseguito comando e ottenuto result set.... continua ");
				// char c = (char)System.in.read();
				System.out.println(" Effettuate le close continua ");
				// c = (char)System.in.read();
			} // END IF
		} catch (Exception eie) {
			eie.printStackTrace();
		} finally {
			Utils.releaseResources(null, selectCommand, dr);
		}
	}

	public static void variableQuery(DataConnection dc, String parametricQueryString) {
		SQLCommand selectCommand = null;
		DataResult dr = null;
		try {
			selectCommand = dc.createSelectCommand(parametricQueryString);
			List inputParameters = new ArrayList(2);
			inputParameters.add(dc.createDataField("", Types.INTEGER, new Integer(4594)));
			inputParameters.add(dc.createDataField("", Types.INTEGER, new Integer(4594)));
			dr = selectCommand.execute(inputParameters);
			if (dr.getDataResultType().equals("SCROLLABLE_DATA_RESULT")) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				System.out.println(sdr.getRowsNumber());
				// Scrollable DataResult
				while (sdr.hasRows()) {
					DataRow row = sdr.getDataRow();
					// System.out.println((row.getColumn("ABI")).getObjectValue());
				}
				sdr.moveTo(1);
				SourceBean sourceBean = sdr.getSourceBean();
				System.out.println(sourceBean.toXML());
				System.out.println(" Eseguito comando e ottenuto result set.... continua ");
				// char c = (char)System.in.read();
				System.out.println(" Effettuate le close continua ");
				// c = (char)System.in.read();
			} // END IF
		} catch (Exception eie) {
			eie.printStackTrace();
		} finally {
			Utils.releaseResources(null, selectCommand, dr);
		}
	} // end query
}
