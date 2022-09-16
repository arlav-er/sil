package it.eng.sil.util.patto;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.sil.Values;

public class PageProperties {
	private String page;
	private String codProvincia;
	private List properties;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PageProperties.class.getName());

	/**
	 * @param String
	 *            page nome della page associata alla jsp
	 * @param String
	 *            codProvincia codice della provincia che ha definito le caratteristiche di visualizzazione della pagina
	 */
	public PageProperties(String page, String codProvincia) throws Exception {
		this.page = page;
		this.codProvincia = codProvincia;
		loadProperties();
	}

	private void loadProperties() throws Exception {
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		properties = new ArrayList();
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("PROPRIETA_PAGINE_PATTO");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, this.page));

			// inputParameter.add(dataConnection.createDataField("",
			// Types.NUMERIC, new Integer(this.user.getCdnProfilo())));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List rows = sb.getAttributeAsVector("ROW");
			for (int i = 0; i < rows.size(); i++) {
				SourceBean row = (SourceBean) rows.get(i);
				PageProperty property = new PageProperty();
				String flgElenco = (String) row.getAttribute("flgmostraelencostessariga");
				property.elencoEspanso = !(flgElenco != null && flgElenco.equals("S"));
				String flgInfCorrenti = (String) row.getAttribute("flginfcorrenti");
				String flgVisualizzaStruttura = (String) row.getAttribute("flgvisualizzastruttura");
				String strAzione = (String) row.getAttribute("strazione");
				String codLstTag = (String) row.getAttribute("codlsttab");
				String monovisualizzazione = (String) row.getAttribute("codmonovisualizzazione");
				BigDecimal posizione = (BigDecimal) row.getAttribute("prgposizione");
				//
				if (flgElenco != null && flgElenco.toUpperCase().equals("S"))
					property.elencoEspanso = true;
				if (flgInfCorrenti != null && flgInfCorrenti.toUpperCase().equals("S"))
					property.infCorrenti = true;
				property.visualizzaStruttura = (flgVisualizzaStruttura != null
						&& flgVisualizzaStruttura.toUpperCase().equals("S"));
				property.nome = strAzione;
				property.codLstTab = codLstTag;
				property.monoVisualizzazione = monovisualizzazione == null ? "N" : monovisualizzazione;
				property.posizione = posizione.intValue();
				properties.add(property);
			}
		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			_logger.fatal("QueryExecutor::executeQuery:", (Exception) ex);
			throw new Exception(ex.getMessage());
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
	}

	/**
	 * 
	 */
	public PageProperty getProperty(String azione) {
		PageProperty property = null;
		for (int i = 0; i < properties.size(); i++) {
			if (((PageProperty) properties.get(i)).getNome().equals(azione)) {
				property = (PageProperty) properties.get(i);
				break;
			}
		}
		return property;
	}

	public boolean containsProperty(String azione) {
		return (getProperty(azione) != null);
	}

	public int getPropertyIndex(String azione) {
		PageProperty property = getProperty(azione);
		if (property != null)
			return property.getPosizione();
		else
			return -1;
	}

	public List getProperties() {
		return this.properties;
	}

	public static class PageProperty {
		private String nome;
		private int posizione;
		private boolean elencoEspanso = true;
		private boolean infCorrenti;
		private boolean visualizzaStruttura = true;
		/*
		 * Se l'attributo (sezione) va visualizzato sempre (S), se non va visualizzato (N), se va visualizzato se legato
		 * (L; vedi la gestione del legame)
		 */
		private String monoVisualizzazione;
		private String codLstTab;

		public void setNome(String nome) {
			this.nome = nome;
		}

		public void setPosizione(int pos) {
			this.posizione = pos;
		}

		public String getNome() {
			return this.nome;
		}

		public boolean isElencoEspanso() {
			return this.elencoEspanso;
		}

		public boolean isInfCorrenti() {
			return this.infCorrenti;
		}

		public boolean isVisualizzaStruttura() {
			return visualizzaStruttura;
		}

		public String getCodLstTab() {
			return this.codLstTab;
		}

		public String getMonoVisualizzazione() {
			return this.monoVisualizzazione;
		}

		public int getPosizione() {
			return this.posizione;
		}
	}

}
