/*
 * Creato il 20-Jun-08
 * Author: riccardi
 * 
 */

package it.eng.sil.util;

import java.sql.Connection;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;

import it.eng.sil.coop.utils.QueryExecutor;

public class CompatibilitaVersionePoli {

	private Connection conn;
	private QueryExecutor qe;
	private String strVerLocaleDa;
	private String strVerLocaleA;
	private String strVerEsternaDa;
	private String strVerEsternaA;
	private String contesto;

	public CompatibilitaVersionePoli(String ambient) {
		this.conn = this.getConnection();
		this.qe = new QueryExecutor(conn);
		this.contesto = ambient;
	}

	public boolean checkCompatibilitaVersionePoli(String versLocale, String versEsterna) throws Exception {
		String stm = "select strverlocaleda, strverlocalea, strveresternada, strveresternaa from ts_coopera where codtipoconfig = upper(?)";
		SourceBean rows = qe.executeQuery(stm, new String[] { contesto });
		Vector list = rows.getAttributeAsVector("ROW");

		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				SourceBean row = (SourceBean) list.get(i);
				strVerLocaleDa = Utils.notNull(row.getAttribute("strverlocaleda"));
				strVerLocaleA = Utils.notNull(row.getAttribute("strverlocalea"));
				strVerEsternaDa = Utils.notNull(row.getAttribute("strveresternada"));
				strVerEsternaA = Utils.notNull(row.getAttribute("strveresternaa"));

				if (isNewer(versLocale, strVerLocaleDa) && isNewer(strVerLocaleA, versLocale)) {
					if (isNewer(versEsterna, strVerEsternaDa) && isNewer(strVerEsternaA, versEsterna)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Connection getConnection() {
		try {
			conn = DataConnectionManager.getInstance().getConnection().getInternalConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conn;
	}

	/**
	 * Funzione per il confronto di versioni
	 * 
	 * @param srcVer
	 * @param dstVer
	 * @author pegoraro la stringa vuota è intesa come infinito
	 */
	static boolean isNewer(String srcVer, String dstVer) throws NumberFormatException {
		String[] ver1;
		String[] ver2;
		int z = 0;
		int[] intver1;
		int[] intver2;

		if (srcVer.compareTo("") == 0)
			return true;

		if (dstVer.compareTo("") == 0)
			return false;

		if (dstVer.compareTo(srcVer) == 0)
			return true;

		ver1 = srcVer.split("\\.");
		ver2 = dstVer.split("\\.");

		intver1 = new int[ver1.length];
		intver2 = new int[ver2.length];
		/* Traduco in int. Se va male, solleva Illegalformat */
		for (int i = 0; i < intver1.length; i++) {
			intver1[i] = Integer.parseInt(ver1[i]);
		}
		for (int i = 0; i < intver2.length; i++) {
			intver2[i] = Integer.parseInt(ver2[i]);
		}

		/* Ciclo sulle due stringhe, torno se strettamente < o > */
		while (z < intver1.length && z < intver2.length) {
			if (intver1[z] > intver2[z])
				return true;
			else if (intver1[z] < intver2[z])
				return false;
			z++;
		}
		/* Sono identici ma uno è più lungo, se è il primo -> true */
		if (intver1.length >= intver2.length)
			return true;

		/* Se arrivo in fondo devo tornare false */
		return false;
	}

}
