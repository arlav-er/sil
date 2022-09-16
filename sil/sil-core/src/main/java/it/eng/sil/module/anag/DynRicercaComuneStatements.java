package it.eng.sil.module.anag;

import it.eng.afExt.utils.StringUtils;

public class DynRicercaComuneStatements {

	public DynRicercaComuneStatements() { // costruttore vuoto
	}

	public String getRicercaXcodice(String codCom, String tipoRicerca) {
		if (tipoRicerca == null) {
			tipoRicerca = "";
		}

		String query = "";

		if (tipoRicerca.equalsIgnoreCase("COMUNI")) { // Ricerco i comuni (il
														// codice NON deve
														// iniziare per 'Z')
			query = "SELECT c.codcom," + "     c.strdenominazione|| "
					+ "       DECODE (SYSDATE, GREATEST(SYSDATE, C.DATFINEVAL), ' (Comune non più esistente)',"
					+ "                        LEAST(SYSDATE, c.DATINIZIOVAL),  ' (Comune non ancora esistente)',"
					+ "               '') AS strdenominazione, " + "     p.stristat,  " + "     c.strcap,  "
					+ "     nvl(c.codcpi, '-1') as codcpi " + " FROM de_comune c "
					+ " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia ";

			if (codCom.length() == 4) {// Il codice comune ha 4 cifre. Quindi
										// eseguo una ricerca puntuale
										// (corrispondenza 1-1 codice-comune)
				query += " WHERE c.codcom = '" + codCom.toUpperCase() + "' ";
			} else if (codCom.length() < 4) {// Eseguo una ricerca generica
				query += " WHERE c.codcom like '" + codCom.toUpperCase() + "%'  ";
			} else {
				return "";
			}
			query += "   AND substr(codcom,1,1) <> 'Z'";
		} else if (tipoRicerca.equalsIgnoreCase("STATI")) { // Ricerco gli stati
															// (il codice DEVE
															// iniziare per 'Z')
			query = "SELECT c.codcom," + "     c.strdenominazione|| "
					+ "       DECODE (SYSDATE, GREATEST(SYSDATE, C.DATFINEVAL), ' (Stato non più esistente)',"
					+ "                        LEAST(SYSDATE, c.DATINIZIOVAL),  ' (Stato non ancora esistente)',"
					+ "               '') AS strdenominazione, " + "     p.stristat,  " + "     c.strcap,  "
					+ "     nvl(c.codcpi, '-1') as codcpi " + " FROM de_comune c "
					+ " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia ";

			if (codCom.length() == 4) {// Il codice comune ha 4 cifre. Quindi
										// eseguo una ricerca puntuale
										// (corrispondenza 1-1 codice-comune)
				query += " WHERE c.codcom = '" + codCom.toUpperCase() + "' ";
			} else if (codCom.length() < 4) {// Eseguo una ricerca generica
				query += " WHERE c.codcom like '" + codCom.toUpperCase() + "%'  ";
			} else {
				return "";
			}
			query += "   AND substr(codcom,1,1) = 'Z'";
		} else { // Ricerco sia stati che comuni
			query = "SELECT c.codcom," + "     c.strdenominazione|| " + "      DECODE (SYSDATE, ";
			if (codCom.charAt(0) == 'Z' || codCom.charAt(0) == 'z') {
				query += "       GREATEST(SYSDATE,C.DATFINEVAL),' (Stato non più esistente)',"
						+ "       LEAST(SYSDATE, c.DATINIZIOVAL), '(Stato non ancora esistente)', '') AS strdenominazione,  ";
			} else {
				query += "       GREATEST(SYSDATE,C.DATFINEVAL),' (Comune non più esistente)',"
						+ "       LEAST(SYSDATE, c.DATINIZIOVAL), '(Comune non ancora esistente)', '') AS strdenominazione,  ";
			}
			query += "     p.stristat,  " + "     c.strcap,  " + "     nvl(c.codcpi, '-1') as codcpi "
					+ " FROM de_comune c " + " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia ";

			if (codCom.length() == 4) {// Il codice comune ha 4 cifre. Quindi
										// eseguo una ricerca puntuale
										// (corrispondenza 1-1 codice-comune)
				query += " WHERE c.codcom = '" + codCom.toUpperCase() + "' ";
			} else if (codCom.length() < 4) {// Eseguo una ricerca generica
				query += " WHERE c.codcom like '" + codCom.toUpperCase() + "%'  ";
			} else {
				return "";
			}
		}

		query += " AND c.codcom <> 'NT'";
		query += " ORDER BY codcom";
		return query;

	}

	public String getRicercaXdescrizione(String descr, String tipoRicerca) {
		String prov = "";

		if (tipoRicerca == null) {
			tipoRicerca = "";
		}

		int idx = descr.indexOf('(');
		if (idx != -1) { // Eliminiamo l'eventuale contenuto tra parentesi in
							// modo da evitare errore in caso di
			// ricerca con la provincia o ricerca con eventuali commenti come
			// (Stato non più esistente)
			prov = descr.substring(idx, descr.length());
			descr = descr.substring(0, idx);
			// descr = descr.trim();
		}

		descr = descr.trim();
		descr = StringUtils.removeDoubleWhiteSpace(descr);

		String query = "";

		if (tipoRicerca.equalsIgnoreCase("COMUNI")) { // Ricerco i comuni (il
														// codice NON deve
														// iniziare per 'Z')
			boolean ricercaConProv = false;
			prov = prov.trim();
			if (prov.length() == 4) {
				prov = prov.substring(1, 3);
				ricercaConProv = true;
			}

			query = "SELECT c.codcom, " + " c.strdenominazione|| "
					+ "       DECODE (SYSDATE, GREATEST(SYSDATE, C.DATFINEVAL), ' (Comune non più esistente)',"
					+ "                        LEAST(SYSDATE, c.DATINIZIOVAL),  ' (Comune non ancora esistente)',"
					+ "               '') AS strdenominazione, " + " p.stristat,  " + " c.strcap,  "
					+ " nvl(c.codcpi, '-1') as codcpi " + " FROM de_comune c "
					+ " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia" + " WHERE c.strdenominazione like '"
					+ descr.toUpperCase() + "' || '%' " + "   AND substr(codcom,1,1) <> 'Z'";
			if (ricercaConProv) {
				query += " AND p.stristat = '" + prov + "' ";
			}

		} else if (tipoRicerca.equalsIgnoreCase("STATI")) { // Ricerco gli stati
															// (il codice DEVE
															// iniziare per 'Z')
			query = "SELECT c.codcom, " + " c.strdenominazione|| "
					+ "       DECODE (SYSDATE, GREATEST(SYSDATE, C.DATFINEVAL), ' (Stato non più esistente)',"
					+ "                        LEAST(SYSDATE, c.DATINIZIOVAL),  ' (Stato non ancora esistente)',"
					+ "               '') AS strdenominazione, " + " p.stristat,  " + " c.strcap,  "
					+ " nvl(c.codcpi, '-1') as codcpi " + " FROM de_comune c "
					+ " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia" + " WHERE c.strdenominazione like '"
					+ descr.toUpperCase() + "' || '%' " + "   AND substr(codcom,1,1) = 'Z'";
		} else { // Ricerco sia stati che comuni
			boolean ricercaConProv = false;
			prov = prov.trim();
			if (prov.length() == 4) {
				prov = prov.substring(1, 3);
				ricercaConProv = true;
			}

			query = "SELECT c.codcom, " + " c.strdenominazione|| "
					+ "   DECODE (SYSDATE, GREATEST(SYSDATE, C.DATFINEVAL),decode(SUBSTR(c.codcom,1,1),'Z',' (Stato non più esistente)',' (Comune non più esistente)'),"
					+ "                    LEAST(SYSDATE, c.DATINIZIOVAL), decode(SUBSTR(c.codcom,1,1),'Z',' (Stato non più esistente)',' (Comune non più esistente)'),"
					+ "           '') AS strdenominazione," + " p.stristat,  " + " c.strcap,  "
					+ " nvl(c.codcpi, '-1') as codcpi " + " FROM de_comune c "
					+ " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia" + " WHERE c.strdenominazione like '"
					+ descr.toUpperCase() + "' || '%' ";
			if (ricercaConProv) {
				query += " AND p.stristat = '" + prov + "' ";
			}

		}

		query += " AND c.codcom <> 'NT'";
		query += " ORDER BY strdenominazione";
		return query;

	}

	public String getRicercaXdescrPrecisa(String descr, String tipoRicerca) {
		String prov = "";

		if (tipoRicerca == null) {
			tipoRicerca = "";
		}

		int idx = descr.indexOf('(');
		if (idx != -1) { // Eliminiamo l'eventuale contenuto tra parentesi in
							// modo da evitare errore in caso di
			// ricerca con la provincia o ricerca con eventuali commenti come
			// (Stato non più esistente)
			prov = descr.substring(idx, descr.length());
			descr = descr.substring(0, idx);
			descr = descr.trim();
		}

		// descr = descr.trim();
		descr = StringUtils.removeDoubleWhiteSpace(descr);

		String query = "";

		if (tipoRicerca.equalsIgnoreCase("COMUNI")) { // Ricerco i comuni (il
														// codice NON deve
														// iniziare per 'Z')
			boolean ricercaConProv = false;
			prov = prov.trim();
			if (prov.length() == 4) {
				prov = prov.substring(1, 3);
				ricercaConProv = true;
			}

			query = "SELECT c.codcom, " + " c.strdenominazione|| "
					+ "       DECODE (SYSDATE, GREATEST(SYSDATE, C.DATINIZIOVAL, C.DATFINEVAL), "
					+ "                       ' (Comune non più esistente)',  LEAST(SYSDATE, c.DATINIZIOVAL, c.DATFINEVAL), "
					+ "                       ' (Comune non più esistente)', '') AS strdenominazione, "
					+ " p.stristat,  " + " c.strcap,  " + " nvl(c.codcpi, '-1') as codcpi " + " FROM de_comune c "
					+ " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia" + " WHERE c.strdenominazione = '"
					+ descr.toUpperCase() + "' " + "   AND substr(codcom,1,1) <> 'Z'";
			if (ricercaConProv) {
				query += " AND p.stristat = '" + prov + "' ";
			}

		} else if (tipoRicerca.equalsIgnoreCase("STATI")) { // Ricerco gli stati
															// (il codice DEVE
															// iniziare per 'Z')
			query = "SELECT c.codcom, " + " c.strdenominazione|| "
					+ "       DECODE (SYSDATE, GREATEST(SYSDATE, C.DATINIZIOVAL, C.DATFINEVAL), "
					+ "                       ' (Stato non più esistente)',  LEAST(SYSDATE, c.DATINIZIOVAL, c.DATFINEVAL), "
					+ "                       ' (Stato non più esistente)', '') AS strdenominazione, "
					+ " p.stristat,  " + " c.strcap,  " + " nvl(c.codcpi, '-1') as codcpi " + " FROM de_comune c "
					+ " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia" + " WHERE c.strdenominazione = '"
					+ descr.toUpperCase() + "' " + "   AND substr(codcom,1,1) = 'Z'";
		} else { // Ricerco sia stati che comuni
			boolean ricercaConProv = false;
			prov = prov.trim();
			if (prov.length() == 4) {
				prov = prov.substring(1, 3);
				ricercaConProv = true;
			}

			query = "SELECT c.codcom, " + " c.strdenominazione|| "
					+ "   DECODE (SYSDATE, GREATEST(SYSDATE, C.DATFINEVAL),decode(SUBSTR(c.codcom,1,1),'Z',' (Stato non più esistente)',' (Comune non più esistente)'),"
					+ "                    LEAST(SYSDATE, c.DATINIZIOVAL), decode(SUBSTR(c.codcom,1,1),'Z',' (Stato non più esistente)',' (Comune non più esistente)'),"
					+ "           '') AS strdenominazione," + " p.stristat,  " + " c.strcap,  "
					+ " nvl(c.codcpi, '-1') as codcpi " + " FROM de_comune c "
					+ " INNER JOIN de_provincia p ON c.codprovincia=p.codprovincia" + " WHERE c.strdenominazione = '"
					+ descr.toUpperCase() + "' ";
			if (ricercaConProv) {
				query += " AND p.stristat = '" + prov + "' ";
			}

		}

		query += " AND c.codcom <> 'NT'";
		query += " ORDER BY strdenominazione";
		return query;

	}

}// end class
