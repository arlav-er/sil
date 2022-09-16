/**
 * 
 */
package it.eng.sil.module.budget;

import java.math.BigDecimal;
import java.text.NumberFormat;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author Fatale
 *
 */
public class VisualizzaPaginaTotaliBudget extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5267530914114515308L;

	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(VisualizzaPaginaTotaliBudget.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("Sono dentro la classe VisualizzaPaginaTotaliBudget");

		DataConnection dc = null;
		DataConnectionManager dcm = null;
		SQLCommand cmdSelect = null;
		DataResult dr = null;

		try {

			RequestContainer reqCont = getRequestContainer();
			ResponseContainer resCont = getResponseContainer();

			SessionContainer session = reqCont.getSessionContainer();

			String pool = (String) getConfig().getAttribute("POOL");
			String statement = SQLStatements.getStatement("GET_SUB_TOTALI_BUDGET");
			StringBuffer buf = new StringBuffer(statement);

			SourceBean serviceReq = reqCont.getServiceRequest();

			String codiceCpiSel = "";
			String annoSel = "";

			String[] inputParam = (String[]) session.getAttribute("tipoRicerca");

			if (inputParam != null && inputParam.length > 0) {
				annoSel = inputParam[0];
				codiceCpiSel = inputParam[1];
			} else {
				codiceCpiSel = (String) serviceReq.getAttribute("codiceCPISel");
				annoSel = (String) serviceReq.getAttribute("AnnoSel");
			}

			if (codiceCpiSel != null && !"".equals(codiceCpiSel)) {
				buf.append(" AND cpi.CODCPI ='" + codiceCpiSel + "'");
			}
			if (annoSel != null && !"".equals(annoSel)) {
				buf.append(" AND NUMANNOBUDGET =" + annoSel);
			}

			String statementFinale = buf.toString();

			dcm = DataConnectionManager.getInstance();
			dc = dcm.getConnection(pool);
			cmdSelect = dc.createSelectCommand(statementFinale);

			// Messaggio di Debug
			_logger.debug(className + "::DynamicQueryGenerica: eseguo query " + statementFinale);

			// esegue la query
			dr = cmdSelect.execute();
			// Recupera il risultato della query
			ScrollableDataResult sdr = null;
			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				sdr = (ScrollableDataResult) dr.getDataObject();
			}
			// Recupero il bean di risposta
			SourceBean rowsSourceBean = sdr.getSourceBean();
			// Messaggio di Debug
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);
			//
			// BigDecimal impegnato =new BigDecimal(0);
			// BigDecimal residuo=new BigDecimal(0);
			// BigDecimal speso =new BigDecimal(0);
			// BigDecimal totbudget =new BigDecimal(0);

			String impegnato = null;
			String residuo = null;
			String speso = null;
			String totbudget = null;
			if (rowsSourceBean != null) {
				//
				// impegnato = (BigDecimal) rowsSourceBean.getAttribute("ROW.impegnato");
				// residuo = (BigDecimal) rowsSourceBean.getAttribute("ROW.residuo");
				// speso = (BigDecimal) rowsSourceBean.getAttribute("ROW.speso");
				// totbudget = (BigDecimal) rowsSourceBean.getAttribute("ROW.totbudget");

				impegnato = (String) rowsSourceBean.getAttribute("ROW.impegnato");
				residuo = (String) rowsSourceBean.getAttribute("ROW.residuo");
				speso = (String) rowsSourceBean.getAttribute("ROW.speso");
				totbudget = (String) rowsSourceBean.getAttribute("ROW.totbudget");

			}

			if (impegnato != null) {
				// String strImpegnato=getLocalizedBigDecimalValue(impegnato);
				// serviceResponse.setAttribute("impegnatoTot", impegnato.setScale(2, BigDecimal.ROUND_HALF_UP));
				// reqCont.setAttribute("impegnatoTot", impegnato.setScale(2, BigDecimal.ROUND_HALF_UP));
				// session.setAttribute("impegnatoTot", impegnato.setScale(2, BigDecimal.ROUND_HALF_UP));
				serviceResponse.setAttribute("impegnatoTot", impegnato);
				reqCont.setAttribute("impegnatoTot", impegnato);
				session.setAttribute("impegnatoTot", impegnato);
			}
			if (residuo != null) {
				// String strResiduo=getLocalizedBigDecimalValue(residuo);
				// serviceResponse.setAttribute("residuoTot",residuo.setScale(2, BigDecimal.ROUND_HALF_UP));
				// reqCont.setAttribute("residuoTot",residuo.setScale(2, BigDecimal.ROUND_HALF_UP));
				// session.setAttribute("residuoTot", residuo.setScale(2, BigDecimal.ROUND_HALF_UP));
				serviceResponse.setAttribute("residuoTot", residuo);
				reqCont.setAttribute("residuoTot", residuo);
				session.setAttribute("residuoTot", residuo);
			}
			if (speso != null) {
				// String strspeso=getLocalizedBigDecimalValue(speso);
				// serviceResponse.setAttribute("spesoTot", speso.setScale(2, BigDecimal.ROUND_HALF_UP));
				// reqCont.setAttribute("spesoTot", speso.setScale(2, BigDecimal.ROUND_HALF_UP));
				// session.setAttribute("spesoTot", speso.setScale(2, BigDecimal.ROUND_HALF_UP));
				serviceResponse.setAttribute("spesoTot", speso);
				reqCont.setAttribute("spesoTot", speso);
				session.setAttribute("spesoTot", speso);
			}

			if (totbudget != null) {
				// String strTotBudget=getLocalizedBigDecimalValue(totbudget);
				// serviceResponse.setAttribute("TotBudget", totbudget.setScale(2, BigDecimal.ROUND_HALF_UP));
				// reqCont.setAttribute("TotBudget", totbudget.setScale(2, BigDecimal.ROUND_HALF_UP));
				// session.setAttribute("TotBudget", totbudget.setScale(2, BigDecimal.ROUND_HALF_UP));
				serviceResponse.setAttribute("TotBudget", totbudget);
				reqCont.setAttribute("TotBudget", totbudget);
				session.setAttribute("TotBudget", totbudget);
			}

			serviceResponse.setAttribute("anno", annoSel);
			serviceResponse.setAttribute("codCpi", codiceCpiSel);
			serviceResponse.setAttribute((SourceBean) rowsSourceBean);

			SourceBeanUtils.setAttrIfFilled(serviceResponse, "totaliBudget", rowsSourceBean, "totaliBudget");

			String[] inputParameters = new String[2];
			inputParameters[0] = annoSel;
			inputParameters[1] = codiceCpiSel;

			session.setAttribute("tipoRicerca", inputParameters);

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowsSourceBean)", ex);

		} finally {
			Utils.releaseResources(dc, cmdSelect, dr);
		}

	}

	protected String getLocalizedBigDecimalValue(BigDecimal input) {
		final NumberFormat numberFormat = NumberFormat.getNumberInstance(java.util.Locale.ITALIAN);
		numberFormat.setGroupingUsed(true);
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		return numberFormat.format(input);
	}
}
