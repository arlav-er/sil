package it.eng.myportal.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.entity.DoTirocini;
import it.eng.myportal.entity.ejb.tirocini.DoTirociniEjb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import com.lowagie.text.pdf.PRAcroForm;

public class LazyDoTirociniModel extends LazyDataModel<DoTirocini> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3678086042641116051L;

	protected final Log log = LogFactory.getLog(this.getClass());

	private DoTirociniEjb doTirociniEjb;

	private Integer count;

	public final static int FILTER_PROV = 1;
	public final static int FILTER_AZIENDA = 2;

	// params
	private int pfPrincipalId;
	private int currentFilter;
	private DoTirocini searchParams;

	public LazyDoTirociniModel(int currentFilter, int pfPrincipalId) {
		this.currentFilter = currentFilter;
		this.pfPrincipalId = pfPrincipalId;
		injectEjb();
	}

	public LazyDoTirociniModel(int currentFilter, Integer pfPrincipalId, DoTirocini searchParams) {
		this.currentFilter = currentFilter;
		this.searchParams = searchParams;
		this.pfPrincipalId = pfPrincipalId;
		injectEjb();
	}

	private void injectEjb() {
		try {
			InitialContext ic = new InitialContext();
			doTirociniEjb = (DoTirociniEjb) ic.lookup("java:app/MyPortal/DoTirociniEjb");
		} catch (NamingException e) {
			log.error("Lookup Failed");
			e.printStackTrace();
		}
	}

	@Override
	public List<DoTirocini> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		List<DoTirocini> data = new ArrayList<DoTirocini>();

		if (currentFilter == FILTER_AZIENDA)
			data = doTirociniEjb.findAllPaged(first, pageSize, pfPrincipalId);
		else if (currentFilter == FILTER_PROV)
			data = doTirociniEjb.findByExamplePagedProvincia(first, pageSize, pfPrincipalId, searchParams);

		// rowCount
		if (count == null) {
			if (currentFilter == FILTER_AZIENDA)
				count = doTirociniEjb.findCountAllPaged(pfPrincipalId).intValue();
			else if (currentFilter == FILTER_PROV) {
				count = doTirociniEjb.findCountByExamplePagedProvincia(pfPrincipalId, searchParams).intValue();
			}
			this.setRowCount(count);
		}

		return data;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
