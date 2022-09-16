package it.eng.sil.module.sap;

import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

public class MansioneSIL {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MansioneSIL.class.getName());
	private final String SI = "S";

	private String codMansione = "";
	private String prgMansione = "";
	private String strDescrizione = "";
	private String flgDisponibile = "";
	private String flgEsperienza = "";
	private String strNote = "";

	public List<String> orari = new ArrayList<String>();
	public List<String> contratti = new ArrayList<String>();
	public List<String> turni = new ArrayList<String>();
	public List<String> comuni = new ArrayList<String>();
	public List<String> province = new ArrayList<String>();
	public List<String> regioni = new ArrayList<String>();
	public List<String> stati = new ArrayList<String>();

	private String flgDispAuto = "";
	private String flgDispMoto = "";
	private String flgMezziPub = "";

	public MansioneSIL(DataRow recMansione) {
		codMansione = recMansione.getColumn("codMansione").getStringValue();
		prgMansione = recMansione.getColumn("prgMansione").getStringValue();
		strDescrizione = recMansione.getColumn("strDescrizione").getStringValue();
		flgDisponibile = recMansione.getColumn("flgDisponibile").getStringValue();
		flgEsperienza = recMansione.getColumn("flgEsperienza").getStringValue();
		strNote = recMansione.getColumn("strNote").getStringValue();
	}

	public void mobilita(ScrollableDataResult sdrMobilita) {
		try {
			if (sdrMobilita.hasRows()) {
				flgDispAuto = sdrMobilita.getDataRow(1).getColumn("flgDispAuto").getStringValue();
				;
				flgDispMoto = sdrMobilita.getDataRow(1).getColumn("flgDispMoto").getStringValue();
				;
				flgMezziPub = sdrMobilita.getDataRow(1).getColumn("flgMezziPub").getStringValue();
				;
			}
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
	}

	public String lista(List<String> lista) {
		String s = "";
		for (String nodo : lista)
			s += nodo + "<br/>";
		return s;
	}

	public String getCodMansione() {
		return codMansione;
	}

	public String getPrgMansione() {
		return prgMansione;
	}

	public String getStrDescrizione() {
		return strDescrizione;
	}

	public boolean getFlgDisponibile() {
		return flgDisponibile != null && flgDisponibile.equals(SI);
	}

	public boolean getFlgEsperienza() {
		return flgEsperienza != null && flgEsperienza.equals(SI);
	}

	public String getStrNote() {
		return strNote;
	}

	public List<String> getOrari() {
		return orari;
	}

	public List<String> getContratti() {
		return contratti;
	}

	public List<String> getTurni() {
		return turni;
	}

	public List<String> getComuni() {
		return comuni;
	}

	public List<String> getProvince() {
		return province;
	}

	public List<String> getRegioni() {
		return regioni;
	}

	public List<String> getStati() {
		return stati;
	}

	public boolean getFlgDispAuto() {
		return flgDispAuto != null && flgDispAuto.equals(SI);
	}

	public boolean getFlgDispMoto() {
		return flgDispMoto != null && flgDispMoto.equals(SI);
	}

	public boolean getFlgMezziPub() {
		return flgMezziPub != null && flgMezziPub.equals(SI);
	}

	@Override
	public String toString() {
		return "MansioneSIL [SI=" + SI + ", codMansione=" + codMansione + ", prgMansione=" + prgMansione
				+ ", strDescrizione=" + strDescrizione + ", flgDisponibile=" + flgDisponibile + ", flgEsperienza="
				+ flgEsperienza + ", strNote=" + strNote + ", orari=" + orari + ", contratti=" + contratti + ", turni="
				+ turni + ", comuni=" + comuni + ", province=" + province + ", stati=" + stati + ", flgDispAuto="
				+ flgDispAuto + ", flgDispMoto=" + flgDispMoto + ", flgMezziPub=" + flgMezziPub + "]";
	}

}
