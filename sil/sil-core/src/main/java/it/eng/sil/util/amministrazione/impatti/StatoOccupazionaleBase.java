package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

public class StatoOccupazionaleBase extends SourceBean {

	public static final String DB_DAT_INIZIO = "DATINIZIO";
	public static final String DB_DAT_FINE = "DATFINE";
	public static final String DB_COD_STATO_ATTO = "CODSTATOATTO";
	public final static int STATO_OCC_INIZIALE_DEFAULT = -1;
	public final static String DATA_RESET_MESI_SOSP = "DATA_RESET_MESI_SOSP";

	public static final int REDDITO_BASSO = 0;
	public static final int REDDITO_ALTO_CAT_SPECIALE = 1;
	public static final int REDDITO_ALTO = 2;
	public static final int MOV_MIN_15GG = 3;

	public static final int NO_ERR = 0;
	public static final int GENERIC_ERR = -1;
	public static final int DIMISSIONI = 1;

	public static final String STATO_OCC_MANUALE = "N";
	public static final String STATO_OCC_AGG_MANUALE = "G";
	public static final String STATO_OCC_REG_AGG_MANUALE = "O";
	public static final String STATO_OCC_DA_DID = "D";
	public static final String STATO_OCC_DA_MOVIMENTI = "M";
	public static final String STATO_OCC_DA_TRASFERIMENTO = "T";
	public static final String STATO_OCC_DA_PORTING = "P";

	public StatoOccupazionaleBase() throws SourceBeanException {
		super("EMPTY");
	}

	public static SourceBean FormattaSourceBean(SourceBean row) {
		if (row.getAttribute("ROW") == null)
			return row;
		else
			return (SourceBean) row.getAttribute("ROW");
	}

	public StatoOccupazionaleBase(SourceBean row) throws SourceBeanException {
		super(FormattaSourceBean(row));
	}

	public SourceBean getSource() {
		return this;
	}

	private BigDecimal toBigDecimal(Object o) {
		if (o != null) {
			if (o instanceof BigDecimal)
				return (BigDecimal) o;
			else
				return new BigDecimal((String) o);
		} else
			return null;
	}

	/**
	 * L'oggetto non e' realmente presente nel db. Per esempio se lo stato occupazionale di partenza non esiste, e'
	 * necessario avere comunque un oggetto, rappresentante il codStatoOcc di altro, per poter calcolare lo stato
	 * occupazionale successivo.
	 */
	public boolean virtuale() throws Exception {
		return this.getAttribute("prgStatoOccupaz") == null
				|| ((BigDecimal) this.getAttribute("prgStatoOccupaz")).intValue() == -1;
	}

	public BigDecimal getPrgStatoOccupaz() {
		if (this.containsAttribute("row"))
			return toBigDecimal(this.getAttribute("row.prgstatooccupaz"));
		else
			return toBigDecimal(this.getAttribute("prgstatooccupaz"));
	}

	public void setProgressivoDB(BigDecimal newPrgStatoOccupaz) throws SourceBeanException {
		if (this.containsAttribute("prgStatoOccupaz"))
			this.updAttribute("prgStatoOccupaz", newPrgStatoOccupaz);
		else
			this.setAttribute("prgStatoOccupaz", newPrgStatoOccupaz);
	}

	public BigDecimal getProgressivoDB() {
		return toBigDecimal(this.getAttribute("prgStatoOccupaz"));
	}

	public void setCdnLavoratore(BigDecimal cdnLavoratore) throws SourceBeanException {
		if (this.containsAttribute("cdnlavoratore"))
			this.updAttribute("cdnlavoratore", cdnLavoratore);
		else
			this.setAttribute("cdnlavoratore", cdnLavoratore);
	}

	public BigDecimal getCdnLavoratore() {
		return toBigDecimal(this.getAttribute("cdnlavoratore"));
	}

	public void setDataInizio(String newDataInizio) throws SourceBeanException {
		if (this.containsAttribute(DB_DAT_INIZIO))
			this.updAttribute(DB_DAT_INIZIO, newDataInizio);
		else
			this.setAttribute(DB_DAT_INIZIO, newDataInizio);
	}

	public String getDataInizio() {
		if (this.getAttribute(DB_DAT_INIZIO) != null)
			return this.getAttribute(DB_DAT_INIZIO).toString();
		else
			return "";
	}

	public void setDataFine(String newDataFine) throws SourceBeanException {
		if (newDataFine == null)
			newDataFine = "";
		if (this.containsAttribute(DB_DAT_FINE))
			this.updAttribute(DB_DAT_FINE, newDataFine);
		else
			this.setAttribute(DB_DAT_FINE, newDataFine);
	}

	public String getDataFine() {
		if (this.getAttribute(DB_DAT_FINE) != null)
			return this.getAttribute(DB_DAT_FINE).toString();
		else
			return "";
	}

	public void setNumKlo(BigDecimal newNumKlo) throws SourceBeanException {
		if (this.containsAttribute("numKLoStatoOccupaz"))
			this.updAttribute("numKLoStatoOccupaz", newNumKlo);
		else
			this.setAttribute("numKLoStatoOccupaz", newNumKlo);
	}

	public BigDecimal getNumKlo() {
		return toBigDecimal(this.getAttribute("numKLoStatoOccupaz"));
	}

	public void setPensionato(String strPensionato) throws SourceBeanException {
		if (strPensionato == null)
			strPensionato = "";
		if (this.containsAttribute("flgPensionato"))
			this.updAttribute("flgPensionato", strPensionato);
		else
			this.setAttribute("flgPensionato", strPensionato);
	}

	public String getPensionato() {
		if (this.getAttribute("flgPensionato") != null)
			return this.getAttribute("flgPensionato").toString();
		else
			return "";
	}

	public void setIndennizzato(String strIndennizzato) throws SourceBeanException {
		if (strIndennizzato == null)
			strIndennizzato = "";
		if (this.containsAttribute("flgIndennizzato"))
			this.updAttribute("flgIndennizzato", strIndennizzato);
		else
			this.setAttribute("flgIndennizzato", strIndennizzato);
	}

	public String getIndennizzato() {
		if (this.getAttribute("flgIndennizzato") != null)
			return this.getAttribute("flgIndennizzato").toString();
		else
			return "";
	}

	public void setDataAnzianita(String strDataAnzDisoc) throws SourceBeanException {
		if (strDataAnzDisoc == null)
			strDataAnzDisoc = "";
		if (this.containsAttribute("datAnzianitaDisoc"))
			this.updAttribute("datAnzianitaDisoc", strDataAnzDisoc);
		else
			this.setAttribute("datAnzianitaDisoc", strDataAnzDisoc);
	}

	public String getDataAnzianita() {
		if (this.getAttribute("datAnzianitaDisoc") != null)
			return this.getAttribute("datAnzianitaDisoc").toString();
		else
			return "";
	}

	public void setDataRichiestaRevisione(String dataRichiestaRevisione) throws SourceBeanException {
		if (dataRichiestaRevisione == null)
			dataRichiestaRevisione = "";
		if (this.containsAttribute("datRichRevisione"))
			this.updAttribute("datRichRevisione", dataRichiestaRevisione);
		else
			this.setAttribute("datRichRevisione", dataRichiestaRevisione);
	}

	public String getDataRichiestaRevisione() {
		if (this.getAttribute("datRichRevisione") != null)
			return this.getAttribute("datRichRevisione").toString();
		else
			return "";
	}

	public void setDataRicorsoGiurisdizionale(String dataRicorsoGiurisdizionale) throws SourceBeanException {
		if (dataRicorsoGiurisdizionale == null)
			dataRicorsoGiurisdizionale = "";
		if (this.containsAttribute("datRicorsoGiurisdiz"))
			this.updAttribute("datRicorsoGiurisdiz", dataRicorsoGiurisdizionale);
		else
			this.setAttribute("datRicorsoGiurisdiz", dataRicorsoGiurisdizionale);
	}

	public String getDataRicorsoGiurisdizionale() {
		if (this.getAttribute("datRicorsoGiurisdiz") != null)
			return this.getAttribute("datRicorsoGiurisdiz").toString();
		else
			return "";
	}

	public void setNumAnzianitaPrec297(String stringNumAnz) throws SourceBeanException {
		if (stringNumAnz == null)
			stringNumAnz = "";
		if (this.containsAttribute("numAnzianitaPrec297"))
			this.updAttribute("numAnzianitaPrec297", stringNumAnz);
		else
			this.setAttribute("numAnzianitaPrec297", stringNumAnz);
	}

	public String getNumAnzianitaPrec297() {
		if (this.getAttribute("numAnzianitaPrec297") != null)
			return this.getAttribute("numAnzianitaPrec297").toString();
		else
			return "";
	}

	public void setNumMesiSosp(String mesi) throws SourceBeanException {
		if (mesi == null)
			mesi = "";
		if (this.containsAttribute("numMesiSosp"))
			this.updAttribute("numMesiSosp", mesi);
		else
			this.setAttribute("numMesiSosp", mesi);
	}

	public String getNumMesiSosp() {
		if (this.getAttribute("numMesiSosp") != null)
			return this.getAttribute("numMesiSosp").toString();
		else
			return "";
	}

	public void setCodMonoCalcoloAnzianitaPrec297(String codMono) throws SourceBeanException {
		if (codMono == null)
			codMono = "";
		if (this.containsAttribute("codMonoCalcoloAnzianitaPrec297"))
			this.updAttribute("codMonoCalcoloAnzianitaPrec297", codMono);
		else
			this.setAttribute("codMonoCalcoloAnzianitaPrec297", codMono);
	}

	public String getCodMonoCalcoloAnzianitaPrec297() {
		if (this.getAttribute("codMonoCalcoloAnzianitaPrec297") != null)
			return this.getAttribute("codMonoCalcoloAnzianitaPrec297").toString();
		else
			return "";
	}

	public void setDataCalcoloAnzianita(String strDataAnz) throws SourceBeanException {
		if (strDataAnz == null)
			strDataAnz = "";
		if (this.containsAttribute("datCalcoloAnzianita"))
			this.updAttribute("datCalcoloAnzianita", strDataAnz);
		else
			this.setAttribute("datCalcoloAnzianita", strDataAnz);
	}

	public String getDataCalcoloAnzianita() {
		if (this.getAttribute("datCalcoloAnzianita") != null)
			return this.getAttribute("datCalcoloAnzianita").toString();
		else
			return "";
	}

	public void setDataCalcoloMesiSosp(String strDataMesiSosp) throws SourceBeanException {
		if (strDataMesiSosp == null)
			strDataMesiSosp = "";
		if (this.containsAttribute("datCalcoloMesiSosp"))
			this.updAttribute("datCalcoloMesiSosp", strDataMesiSosp);
		else
			this.setAttribute("datCalcoloMesiSosp", strDataMesiSosp);
	}

	public String getDataCalcoloMesiSosp() {
		if (this.getAttribute("datCalcoloMesiSosp") != null)
			return this.getAttribute("datCalcoloMesiSosp").toString();
		else
			return "";
	}

	public void setCodMonoProvenienza(String strCodMonoProv) throws SourceBeanException {
		if (strCodMonoProv == null)
			strCodMonoProv = "";
		if (this.containsAttribute("CODMONOPROVENIENZA"))
			this.updAttribute("CODMONOPROVENIENZA", strCodMonoProv);
		else
			this.setAttribute("CODMONOPROVENIENZA", strCodMonoProv);
	}

	public String getCodMonoProvenienza() {
		if (this.getAttribute("CODMONOPROVENIENZA") != null)
			return this.getAttribute("CODMONOPROVENIENZA").toString();
		else
			return "";
	}
}