/**
 * TODO
 */
package it.eng.sil.module.sap;

import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

public class SelectPropenSIL {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SelectPropenSIL.class.getName());

	private List<MansioneSIL> mansioni = new ArrayList<MansioneSIL>();

	/**
	 * 
	 * @param cdnlavoratore
	 */
	public SelectPropenSIL(String cdnlavoratore) {
		SelectSAP sltMansioni = null;
		try {
			sltMansioni = new SelectSAP("SELECT_SAP_PROPEN_MANSIONI");
			sltMansioni.parametro("cdnlavoratore", cdnlavoratore);
			ScrollableDataResult sdrMansioni = sltMansioni.esegui();
			for (int r = 1; r <= sdrMansioni.getRowsNumber(); r++) {
				MansioneSIL mansione = new MansioneSIL(sdrMansioni.getDataRow(r));
				String prgMansione = mansione.getPrgMansione();
				mansione.orari = listaPropensioni("SELECT_SAP_PROPEN_ORARIO", prgMansione, "strDescrizione");
				mansione.contratti = listaPropensioni("SELECT_SAP_PROPEN_CONTRATTO", prgMansione, "strDescrizione");
				mansione.turni = listaPropensioni("SELECT_SAP_PROPEN_TURNO", prgMansione, "strDescrizione");
				mansione.comuni = listaPropensioni("SELECT_SAP_PROPEN_COMUNE", prgMansione, "strDenominazione");
				mansione.province = listaPropensioni("SELECT_SAP_PROPEN_PROVINCIA", prgMansione, "strDenominazione");
				mansione.regioni = listaPropensioni("SELECT_SAP_PROPEN_REGIONE", prgMansione, "strDenominazione");
				mansione.stati = listaPropensioni("SELECT_SAP_PROPEN_STATO", prgMansione, "strDenominazione");
				mobilita(mansione);
				mansioni.add(mansione);
			} // for
			sltMansioni.chiudi();
		} catch (Exception e) {
			if (sltMansioni != null) {
				sltMansioni.chiudi();
			}
			_logger.error(e.getMessage());
		}
	}

	/**
	 * Genera una lista di propensioni in base alla mansione
	 * 
	 * @param xmlSelect
	 *            Statement SPAGO.
	 * @param prgMansione
	 *            Codice progressivo mansione.
	 * @param strDescrizione
	 *            Nome del campo descrizoine nella tabella di decodifica.
	 * @return Una lista di propensioni in base alla mansione.
	 */
	private List<String> listaPropensioni(String xmlSelect, String prgMansione, String strDescrizione) {
		List<String> lista = new ArrayList<String>();
		SelectSAP sltLista = null;
		try {
			sltLista = new SelectSAP(xmlSelect);
			sltLista.parametro("prgMansione", prgMansione);
			ScrollableDataResult sdrLista = sltLista.esegui();
			if (sdrLista != null && sdrLista.hasRows())
				for (int r = 1; r <= sdrLista.getRowsNumber(); r++)
					lista.add(sdrLista.getDataRow(r).getColumn(strDescrizione).getStringValue());
			sltLista.chiudi();
		} catch (Exception e) {
			if (sltLista != null) {
				sltLista.chiudi();
			}
			_logger.error(e.getMessage());
		}
		return lista;
	}

	private void mobilita(MansioneSIL mansione) {
		SelectSAP sltMobilita = new SelectSAP("SELECT_SAP_PROPEN_MOBILITA");
		sltMobilita.parametro("prgMansione", mansione.getPrgMansione());
		ScrollableDataResult sdrMobilita = sltMobilita.esegui();
		mansione.mobilita(sdrMobilita);
	}

	public List<MansioneSIL> getMansioni() {
		return mansioni;
	}

}
