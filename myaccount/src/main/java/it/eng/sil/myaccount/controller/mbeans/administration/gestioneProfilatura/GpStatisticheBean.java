package it.eng.sil.myaccount.controller.mbeans.administration.gestioneProfilatura;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.utils.MyAccountStatisticsEJB;
import it.eng.sil.myaccount.model.ejb.stateless.utils.MyAuthServiceRemoteStatsClientEJB;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.chartistjsf.model.chart.AspectRatio;
import org.chartistjsf.model.chart.BarChartModel;

/**
 * Questo è il backing bean della pagina delle statistiche. Comunica con l'EJB delle statistiche per generare i grafici
 * in base alle opzioni scelte dall'utente.
 * 
 * @author gicozza
 */
@ManagedBean(name = "gpStatisticheBean")
@ViewScoped
public class GpStatisticheBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = 2444873489297914480L;
	private String columnIndex = "2";
	private String order = "desc";

	@EJB
	MyAuthServiceRemoteStatsClientEJB statisticheEJB;

	@EJB
	MyAccountStatisticsEJB statisticheMyAccountEJB;

	private BarChartModel cacheHitBarChart;
	private BarChartModel cacheMyAccountHitBarChart;

	private String jamonChart;

	@Override
	protected void initPostConstruct() {
		setCacheHitBarChart(statisticheEJB.getCacheHitChartData());
		setCacheMyAccountHitBarChart(statisticheMyAccountEJB.getCacheHitChartData());

		cacheHitBarChart.setAspectRatio(AspectRatio.OCTAVE);
		cacheHitBarChart.setStackBars(true);
		cacheHitBarChart.setShowTooltip(true);

		cacheMyAccountHitBarChart.setAspectRatio(AspectRatio.OCTAVE);
		cacheMyAccountHitBarChart.setStackBars(true);
		cacheMyAccountHitBarChart.setShowTooltip(true);

		refreshRemote();
	}

	public void refreshRemote() {
		try {
			log.info("Re-requesting remote jamon, order=" + order + " idx=" + columnIndex);
			jamonChart = statisticheEJB.getJamonHtml(columnIndex, order);
		} catch (IOException e) {
			jamonChart = "<h2>" + e + "</h2>";
		}
	}

	public BarChartModel getCacheHitBarChart() {
		return cacheHitBarChart;
	}

	public void setCacheHitBarChart(BarChartModel cacheHitBarChart) {
		this.cacheHitBarChart = cacheHitBarChart;
	}

	public String getJamonChart() {
		return jamonChart;
	}

	public void setJamonChart(String jamonChart) {
		this.jamonChart = jamonChart;
	}

	public BarChartModel getCacheMyAccountHitBarChart() {
		return cacheMyAccountHitBarChart;
	}

	public void setCacheMyAccountHitBarChart(BarChartModel cacheMyAccountHitBarChart) {
		this.cacheMyAccountHitBarChart = cacheMyAccountHitBarChart;
	}

	public boolean isJamonMyAuthEnabled() {
		try {
			return statisticheEJB.isJamonEnabled();
		} catch (Exception e) {
			log.error("Errore chiamata remota jamon: " + e.getMessage());
			return false;
		}
	}

	public String jamonMyAuthEnable() {
		try {
			return statisticheEJB.jamonEnable();
		} catch (IOException e) {
			log.error("Errore chiamata remota jamon: " + e.getMessage());
			return "";
		}
	}

	public String jamonMyAuthReset() {
		try {
			return statisticheEJB.jamonReset();
		} catch (IOException e) {
			log.error("Errore chiamata remota jamon: " + e.getMessage());
			return "";
		}
	}

	public String jamonMyAuthDisable() {
		try {
			return statisticheEJB.jamonDisable();
		} catch (Exception e) {
			log.error("Errore chiamata remota jamon: " + e.getMessage());
			return "";
		}
	}

	public String getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(String columnIndex) {
		this.columnIndex = columnIndex;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}
