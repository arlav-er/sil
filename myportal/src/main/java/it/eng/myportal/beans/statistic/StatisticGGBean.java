package it.eng.myportal.beans.statistic;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;


@ManagedBean
public class StatisticGGBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1941996409053201768L;

	protected static Log log = LogFactory.getLog(StatisticGGBean.class);
	
	@EJB
	private StatitisticsEJB statitisticsEJB;  
	
	private BarChartModel barModelAdesioniStato;
	private BarChartModel barModelAdesioniStatoRegione;
	private BarChartModel barModelAdesioniRegione;
	
	private List<StatsGGDTO> statsGG;
 
    @PostConstruct
    public void init() {
    	// crea la tabella QUADRO D'INSIEME
    	createTableGG();
    	
    	// crea grafico BarChart per le adesioni GG
        createBarModels();
    }
    
    public BarChartModel getBarModelAdesioniStato() {
        return barModelAdesioniStato;
    }
    
    public BarChartModel getBarModelAdesioniStatoRegione() {
        return barModelAdesioniStatoRegione;
    }
    
    public BarChartModel getBarModelAdesioniRegione() {
        return barModelAdesioniRegione;
    }

 
    private void createBarModels() {
        createBarModelAdesioniStato();
        
        createBarModelAdesioniStatoRegione();
        
        createBarModelAdesioniRegione();   
    }      
    
    public void createTableGG() {
    	BigInteger numTotAdesioni = statitisticsEJB.getTotNumAdesioni();
    	
    	BigInteger numTotAdesioniReg = new BigInteger("0");
    	BigInteger numTotAdesioniNoReg = new BigInteger("0");
    	BigInteger numTotAdesioniNoRil = new BigInteger("0");
    	List<StatsDTO> listaTotAdesioniReg = statitisticsEJB.getNumAdesioniDiviseRegione();
    	for (StatsDTO statsRegDTO : listaTotAdesioniReg) {
    		if ("Residente in Regione".equalsIgnoreCase(statsRegDTO.getCodice())) {
    			numTotAdesioniReg = statsRegDTO.getNum();
    		}
    		else if ("Residente Fuori Regione".equalsIgnoreCase(statsRegDTO.getCodice())) {
    			numTotAdesioniNoReg = statsRegDTO.getNum();
    		}
    		else if ("Non rilevato".equalsIgnoreCase(statsRegDTO.getCodice())) {
    			numTotAdesioniNoRil = statsRegDTO.getNum();
    		}    	
		}
    	
    	BigInteger numTotAppuntamento = statitisticsEJB.getTotNumAdesioniConAppuntamento();
    	
    	BigInteger numTotAppuntamentoReg = new BigInteger("0");
    	BigInteger numTotAppuntamentoNoReg = new BigInteger("0");
    	BigInteger numTotAppuntamentoNoRil = new BigInteger("0");
    	List<StatsDTO> listaTotAppuntamentoReg = statitisticsEJB.getNumAdesioniAppuntamentoDiviseRegione();
    	for (StatsDTO statsAppRegDTO : listaTotAppuntamentoReg) {
    		if ("Residente in Regione".equalsIgnoreCase(statsAppRegDTO.getCodice())) {
    			numTotAppuntamentoReg = statsAppRegDTO.getNum();
    		}
    		else if ("Residente Fuori Regione".equalsIgnoreCase(statsAppRegDTO.getCodice())) {
    			numTotAppuntamentoNoReg = statsAppRegDTO.getNum();
    		}
    		else if ("Non rilevato".equalsIgnoreCase(statsAppRegDTO.getCodice())) {
    			numTotAppuntamentoNoRil = statsAppRegDTO.getNum();
    		}    		
		}
    	
    	BigInteger numTotAdesioniNonAssegnate = statitisticsEJB.getTotNumAdesioniNonAssegnate();
    	
    	statsGG = new ArrayList<StatsGGDTO>();
    	statsGG.add(new StatsGGDTO("Quanti giovani hanno aderito al Programma/si sono registrati scegliendo la Regione/PA?", numTotAdesioni, numTotAdesioniReg, numTotAdesioniNoReg, numTotAdesioniNoRil));
    	statsGG.add(new StatsGGDTO("Giovani con appuntamento", numTotAppuntamento, numTotAppuntamentoReg, numTotAppuntamentoNoReg, numTotAppuntamentoNoRil));
    	statsGG.add(new StatsGGDTO("Presi in carico (A02 e profiling)", new BigInteger("0"), new BigInteger("0"), new BigInteger("0"), new BigInteger("0")));
    	statsGG.add(new StatsGGDTO("Adesioni non assegnate", numTotAdesioniNonAssegnate, new BigInteger("0"), new BigInteger("0"), new BigInteger("0")));
    }  
    
       
    private void createBarModelAdesioniStato() {
    	barModelAdesioniStato = initBarModelAdesioniStato();
         
    	barModelAdesioniStato.setTitle("Adesioni Garanzia Giovani per stato adesione");
    	barModelAdesioniStato.setLegendPosition("ne");                
        
    	barModelAdesioniStato.setSeriesColors("2487c1");
    	barModelAdesioniStato.setShowPointLabels(true);
    	barModelAdesioniStato.setShowDatatip(false);
        
        Axis xAxis = barModelAdesioniStato.getAxis(AxisType.X);
        xAxis.setLabel("stato adesione");               
        
        Axis yAxis = barModelAdesioniStato.getAxis(AxisType.Y);
        yAxis.setLabel("numero");
        yAxis.setMin(0);                      
        
    }
    
    private BarChartModel initBarModelAdesioniStato() {
    	List<StatsDTO> stats = statitisticsEJB.getStatsAdesione();
    	
    	BarChartModel model = new BarChartModel();
        
    	ChartSeries ades = new ChartSeries();
        ades.setLabel("Adesioni");
        
        for (StatsDTO statsDTO : stats) {
        	ades.set(statsDTO.getDescrizione(), statsDTO.getNum());                	
		}
            
        model.addSeries(ades);
        
        return model;
    }

	public List<StatsGGDTO> getStatsGG() {
		return statsGG;
	}

	public void setStatsGG(List<StatsGGDTO> statsGG) {
		this.statsGG = statsGG;
	}
     
	private void createBarModelAdesioniStatoRegione() {
    	barModelAdesioniStatoRegione = initBarModelAdesioniStatoRegione();
         
    	barModelAdesioniStatoRegione.setTitle("Adesioni Garanzia Giovani per stato adesione e residenza");
    	barModelAdesioniStatoRegione.setLegendPosition("ne");                
        
    	barModelAdesioniStatoRegione.setSeriesColors("2487c1,FF0000,FF00FF");
    	barModelAdesioniStatoRegione.setShowPointLabels(true);
    	barModelAdesioniStatoRegione.setShowDatatip(false);
        
        Axis xAxis = barModelAdesioniStatoRegione.getAxis(AxisType.X);
        xAxis.setLabel("stato adesione");               
        
        Axis yAxis = barModelAdesioniStatoRegione.getAxis(AxisType.Y);
        yAxis.setLabel("numero");
        yAxis.setMin(0);                      
        
    }
   
	
	private BarChartModel initBarModelAdesioniStatoRegione() {
    	List<StatsDTO> stats = statitisticsEJB.getNumAdesioniDiviseRegionePerStato();
    	
    	BarChartModel model = new BarChartModel();
        
    	ChartSeries adesReg = new ChartSeries();
    	adesReg.setLabel("Residente in Regione");
        ChartSeries adesNoReg = new ChartSeries();
        adesNoReg.setLabel("Residente Fuori Regione");
        ChartSeries adesNoRil = new ChartSeries();
        adesNoRil.setLabel("Non rilevato");
        
        for (StatsDTO statsDTO : stats) {
        	if ("Residente in Regione".equalsIgnoreCase(statsDTO.getDescrizione())) {
        		adesReg.set(statsDTO.getCodice(), statsDTO.getNum()); 
    		}
    		else if ("Residente Fuori Regione".equalsIgnoreCase(statsDTO.getDescrizione())) {
    			adesNoReg.set(statsDTO.getCodice(), statsDTO.getNum());
    		}
    		else if ("Non rilevato".equalsIgnoreCase(statsDTO.getDescrizione())) {
    			adesNoRil.set(statsDTO.getCodice(), statsDTO.getNum());
    		}            	        	               
		}
            
        model.addSeries(adesReg);
        model.addSeries(adesNoReg);
        model.addSeries(adesNoRil);
        
        return model;
    }
	
	private void createBarModelAdesioniRegione() {
    	barModelAdesioniRegione = initBarModelAdesioniRegione();
         
    	barModelAdesioniRegione.setTitle("Adesioni Garanzia Giovani per stato adesione e residenza");
    	barModelAdesioniRegione.setLegendPosition("ne");                
        
    	barModelAdesioniRegione.setSeriesColors("009900");
    	barModelAdesioniRegione.setShowPointLabels(true);
    	barModelAdesioniRegione.setShowDatatip(false);
        
        Axis xAxis = barModelAdesioniRegione.getAxis(AxisType.X);
        xAxis.setLabel("");               
        
        Axis yAxis = barModelAdesioniRegione.getAxis(AxisType.Y);
        yAxis.setLabel("numero");
        yAxis.setMin(0);                      
        
    }
	
	private BarChartModel initBarModelAdesioniRegione() {
    	List<StatsDTO> stats = statitisticsEJB.getNumAdesioniDiviseRegione();
    	
    	BarChartModel model = new BarChartModel();
        
    	ChartSeries ades = new ChartSeries();
    	ades.setLabel("Residente");
                
        for (StatsDTO statsDTO : stats) {
        	if ("Residente in Regione".equalsIgnoreCase(statsDTO.getCodice())) {
        		ades.set(statsDTO.getCodice(), statsDTO.getNum());
    		}
    		else if ("Residente Fuori Regione".equalsIgnoreCase(statsDTO.getCodice())) {
    			ades.set(statsDTO.getCodice(), statsDTO.getNum());
    		}
    		else if ("Non rilevato".equalsIgnoreCase(statsDTO.getCodice())) {
    			ades.set(statsDTO.getCodice(), statsDTO.getNum());
    		}      	        	               
		}
            
        model.addSeries(ades);        
        
        return model;
    }
 
}
