package it.eng.sil.cig.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * 
 * @author Rodi
 *
 */
public class CIRelAccordoAzBean {
	private String codaztipo;
	private String strorgdatoriale;
	private String strconsulentelav;
	private String prgazienda; // not null
	private String prgunita; // not null
	private String prgaccordo; // not null
	private String flgcoinvolta; // not null
	private String numlavforza;
	private String strnumeroinps;
	private String strsedeinps;
	private String cdnutins; // not null
	private String cdnutmod; // not null

	private TransactionQueryExecutor tex;

	private List<CILavoratoreBean> listaLavoratori;
	private UnitaAziendaBean sede;

	private static final Logger _logger = Logger.getLogger(CIRelAccordoAzBean.class.getName());

	public CIRelAccordoAzBean(String flgcoinvolta, String numlavforza, Object cdnUtIns, Object cdnUtMod,
			TransactionQueryExecutor tex) {
		if (flgcoinvolta == null)
			throw new IllegalArgumentException("Il flgcoinvolta non puo' essere null.");
		if (numlavforza == null)
			throw new IllegalArgumentException("Il numlavforza non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");

		this.flgcoinvolta = flgcoinvolta;
		this.numlavforza = numlavforza;
		this.cdnutins = cdnUtIns.toString();
		this.cdnutmod = cdnUtMod.toString();

		this.listaLavoratori = new ArrayList<CILavoratoreBean>();

		this.tex = tex;
	}

	public void addLavoratore(CILavoratoreBean lav) {
		listaLavoratori.add(lav);
	}

	public String getCodaztipo() {
		return codaztipo;
	}

	public void setCodaztipo(String codaztipo) {
		this.codaztipo = codaztipo;
	}

	public String getStrorgdatoriale() {
		return strorgdatoriale;
	}

	public void setStrorgdatoriale(String strorgdatoriale) {
		this.strorgdatoriale = strorgdatoriale;
	}

	public String getStrconsulentelav() {
		return strconsulentelav;
	}

	public void setStrconsulentelav(String strconsulentelav) {
		this.strconsulentelav = strconsulentelav;
	}

	public String getPrgazienda() {
		return prgazienda;
	}

	public void setPrgazienda(String prgazienda) {
		this.prgazienda = prgazienda;
	}

	public String getPrgunita() {
		return prgunita;
	}

	public void setPrgunita(String prgunita) {
		this.prgunita = prgunita;
	}

	public String getPrgaccordo() {
		return prgaccordo;
	}

	public void setPrgaccordo(String prgaccordo) {
		this.prgaccordo = prgaccordo;
	}

	public String getFlgcoinvolta() {
		return flgcoinvolta;
	}

	public void setFlgcoinvolta(String flgcoinvolta) {
		this.flgcoinvolta = flgcoinvolta;
	}

	public String getNumlavforza() {
		return numlavforza;
	}

	public void setNumlavforza(String numlavforza) {
		this.numlavforza = numlavforza;
	}

	public String getStrnumeroinps() {
		return strnumeroinps;
	}

	public void setStrnumeroinps(String strnumeroinps) {
		this.strnumeroinps = strnumeroinps;
	}

	public String getStrsedeinps() {
		return strsedeinps;
	}

	public void setStrsedeinps(String strsedeinps) {
		this.strsedeinps = strsedeinps;
	}

	public TransactionQueryExecutor getTex() {
		return tex;
	}

	public void setTex(TransactionQueryExecutor tex) {
		this.tex = tex;
	}

	public UnitaAziendaBean getSede() {
		return sede;
	}

	public void setSede(UnitaAziendaBean sede) {
		this.sede = sede;
	}

	public List<CILavoratoreBean> getListaLavoratori() {
		return listaLavoratori;
	}

	public void setListaLavoratori(List<CILavoratoreBean> listaLavoratori) {
		this.listaLavoratori = listaLavoratori;
	}

	/**
	 * Restituisce <code>true</code> solo se la tripletta prgazienda,prgunita,prgaccordo non è già stata inserita.
	 * Questa condizione viene calcolata ogni volta che viene richiamato il metodo e non viene salvata all'interno di
	 * una variabile al momento della costruzione dell'istanza. Questo perché possono esserci più istanze che
	 * rappresentano lo stesso record contemporaneamente e qualcuna potrebbe essere inserita, cambiando lo stato di
	 * tutte le altre.
	 * 
	 * @return true, se rappresenta un record da inserire sul DB, false altrimenti.
	 */
	public boolean canBeInserted() {
		Object pRelAccordo[] = new Object[3];

		pRelAccordo[0] = prgazienda;
		pRelAccordo[1] = prgunita;
		pRelAccordo[2] = prgaccordo;

		// recupero il numero di record con la stessa tripletta
		SourceBean rowAcc;
		try {
			rowAcc = (SourceBean) tex.executeQuery("GET_CI_REL_ACCORDO", pRelAccordo, "SELECT");
		} catch (EMFInternalError e) {
			_logger.error("Errore durante la verifica su ci_rel_accordo_unit_az", e);
			return true;
		}

		Vector rowsAcc = rowAcc.getAttributeAsVector("ROW");
		if (rowsAcc.size() != 1) {
			_logger.error("Errore durante la verifica su ci_rel_accordo_unit_az");
			return false;
		}
		SourceBean singlerow = (SourceBean) rowsAcc.get(0);
		BigDecimal numero = (BigDecimal) singlerow.getAttribute("numero");

		if (numero.intValue() > 0) {
			_logger.info("ci_relaccordounitaaz prgazienda: " + prgazienda + ", prgunita: " + prgunita + ", prgaccordo: "
					+ prgaccordo + " già inserito");
			return false;
		}

		return true;
	}

	/**
	 * Inserisce su DB nella tabella CI_REL_ACCORDO_UNIT_AZ l'istanza.
	 * 
	 * @throws EMFInternalError
	 *             in caso di errore durante l'inserimento
	 */
	public void insert() throws EMFInternalError {
		Object pRelAccordo[] = new Object[10];

		pRelAccordo[0] = codaztipo;
		pRelAccordo[1] = strorgdatoriale;
		pRelAccordo[2] = strconsulentelav;
		pRelAccordo[3] = prgazienda;
		pRelAccordo[4] = prgunita;
		pRelAccordo[5] = prgaccordo;
		pRelAccordo[6] = flgcoinvolta;
		pRelAccordo[7] = numlavforza;
		pRelAccordo[8] = strnumeroinps;
		pRelAccordo[9] = strsedeinps;

		tex.executeQuery("INSERT_CI_RELACCORDOUNITAAZ", pRelAccordo, "INSERT");

		_logger.info("inserito ci_relaccordounitaaz. prgazienda: " + prgazienda + ", prgunita: " + prgunita
				+ ", prgaccordo: " + prgaccordo);
	}
}
