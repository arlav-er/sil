package it.eng.sil.module.patto.bean;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;

public class PacchettoAdulti {

	private BigDecimal cdnLavoratore = null;
	private String dataPatto = null;
	private String servizio = null;

	public static final String CONFIG_PA = "PACCO_AD";

	public PacchettoAdulti(BigDecimal cdnLav) {
		// TODO Auto-generated constructor stub
		this.cdnLavoratore = cdnLav;
	}

	public PacchettoAdulti(BigDecimal cdnLav, String tipoServizio) {
		// TODO Auto-generated constructor stub
		this.cdnLavoratore = cdnLav;
		this.servizio = tipoServizio;
	}

	public PacchettoAdulti(BigDecimal cdnLav, String dataProgramma, String tipoServizio) {
		// TODO Auto-generated constructor stub
		this.cdnLavoratore = cdnLav;
		this.dataPatto = dataProgramma;
		this.servizio = tipoServizio;
	}

	public BigDecimal getCdnLavoratore() {
		return this.cdnLavoratore;
	}

	public void setCdnLavoratore(BigDecimal value) {
		this.cdnLavoratore = value;
	}

	public String getDataPatto() {
		return this.dataPatto;
	}

	public void setDataPatto(String value) {
		this.dataPatto = value;
	}

	public String getServizio() {
		return this.servizio;
	}

	public void setServizio(String value) {
		this.servizio = value;
	}

	public String caricaDataAdesione() {
		String dataAdesione = null;
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = getCdnLavoratore();
		row = (SourceBean) QueryExecutor.executeQuery("GET_DATA_ADESIONE_PATTO_PACCHETTO_ADULTI", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			if (row.getAttribute("DATADESIONEPA") != null) {
				dataAdesione = row.getAttribute("DATADESIONEPA").toString();
			}
		}
		return dataAdesione;
	}

	public String caricaDataAdesionePortale() {
		String dataAdesione = null;
		SourceBean row = null;
		Object params[] = new Object[3];
		params[0] = getCdnLavoratore();
		params[1] = getDataPatto();
		params[2] = getServizio();
		row = (SourceBean) QueryExecutor.executeQuery("GET_PROGRAMMA_DATA_ADESIONE_PORTALE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			if (row.getAttribute("SDATADESIONE") != null) {
				dataAdesione = row.getAttribute("SDATADESIONE").toString();
			}
		}
		return dataAdesione;
	}

	public String caricaMaxDataAdesionePortale() {
		String dataAdesione = null;
		SourceBean row = null;
		Object params[] = new Object[2];
		params[0] = getCdnLavoratore();
		params[1] = getServizio();
		row = (SourceBean) QueryExecutor.executeQuery("GET_PROGRAMMA_MAX_DATA_ADESIONE_PORTALE", params, "SELECT",
				Values.DB_SIL_DATI);
		if (row != null) {
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			if (row.getAttribute("SDATADESIONE") != null) {
				dataAdesione = row.getAttribute("SDATADESIONE").toString();
			}
		}
		return dataAdesione;
	}
}
