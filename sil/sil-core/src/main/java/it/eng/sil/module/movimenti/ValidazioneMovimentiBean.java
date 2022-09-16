package it.eng.sil.module.movimenti;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.util.amministrazione.impatti.MovimentoBean;

public class ValidazioneMovimentiBean extends SourceBean implements Comparable<Object> {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MovimentoBean.class.getName());

	private BigDecimal prgMovimentoApp = null;
	private String codTipoComunic = null;

	public ValidazioneMovimentiBean(SourceBean sb) throws SourceBeanException {
		super(sb);
		this.prgMovimentoApp = (BigDecimal) sb.getAttribute("PRGMOVIMENTOAPP");
		this.codTipoComunic = (String) sb.getAttribute("CODTIPOCOMUNIC");
	}

	public BigDecimal getPrgMovimentoApp() {
		return prgMovimentoApp;
	}

	public void setPrgMovimentoApp(BigDecimal prgMovimentoApp) {
		this.prgMovimentoApp = prgMovimentoApp;
	}

	public String getCodTipoComunic() {
		return codTipoComunic;
	}

	public void setCodTipoComunic(String codTipoComunic) {
		this.codTipoComunic = codTipoComunic;
	}

	public int compareTo(Object obj) {
		int ret = 0;
		ValidazioneMovimentiBean m = null;
		try {
			m = (ValidazioneMovimentiBean) obj;
			String codTipoComunicazione = m.getCodTipoComunic();
			String codTipoComunicazioneCurr = this.getCodTipoComunic();

			if (codTipoComunicazioneCurr != null && codTipoComunicazione != null) {
				int valoreCurr = 4;
				int valore = 4;
				if (codTipoComunicazioneCurr.equalsIgnoreCase("01")) {
					valoreCurr = 1;
				} else {
					if (codTipoComunicazioneCurr.equalsIgnoreCase("03")) {
						valoreCurr = 2;
					} else {
						if (codTipoComunicazioneCurr.equalsIgnoreCase("04")) {
							valoreCurr = 3;
						}
					}
				}
				if (codTipoComunicazione.equalsIgnoreCase("01")) {
					valore = 1;
				} else {
					if (codTipoComunicazione.equalsIgnoreCase("03")) {
						valore = 2;
					} else {
						if (codTipoComunicazione.equalsIgnoreCase("04")) {
							valore = 3;
						}
					}
				}
				if (valoreCurr > valore) {
					ret = -1;
				} else {
					if (valoreCurr < valore) {
						ret = 1;
					}
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "MovimentoBean.compareTo():" + m + ", " + this, e);
		}
		return ret;
	}

}
