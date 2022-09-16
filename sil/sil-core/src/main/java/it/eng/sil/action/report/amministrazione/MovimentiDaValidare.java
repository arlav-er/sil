package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.action.report.AbstractSimpleReport;

public class MovimentiDaValidare extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MovimentiDaValidare.class.getName());

	private static final String className = StringUtils.getClassName(Movimenti.class);

	private Vector prgMovimenti = null;
	private String prgValidazioneMassiva = null;
	private Vector vettPrgMovimenti = new Vector();

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		// recupero i parametri
		RequestContainer requestContainer = getRequestContainer();

		String tipoStampa = (String) request.getAttribute("tipoStampa");
		try {

			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null) {
					setStrNomeDoc("ReportMovimentiDaValidare." + tipoFile);
				} else {
					setStrNomeDoc("ReportMovimentiDaValidare.pdf");
				}
				setStrDescrizione("Stampa movimenti amministrativi");
				setReportPath("Amministrazione/MovimentiDaValidare_CC.rpt");

				prgMovimenti = request.getAttributeAsVector("prgMovimento");

				// Passo l'id della sessione al report
				Vector params = null;
				params = new Vector(1);

				params.add(prgMovimenti.toString());

				setParams(params);

				String tipoDoc = (String) request.getAttribute("tipoDoc");
				if (tipoDoc != null)
					setCodTipoDocumento(tipoDoc);

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true"))
					insertDocument(request, response);
				else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response);
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);

			setOperationFail(request, response, e);
		}
	}

}