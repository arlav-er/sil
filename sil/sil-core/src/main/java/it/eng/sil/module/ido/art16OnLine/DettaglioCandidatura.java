package it.eng.sil.module.ido.art16OnLine;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.art16online.istanze.xsd.types.CandidaturaType;
import it.eng.sil.module.AbstractSimpleModule;

public class DettaglioCandidatura extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		disableMessageIdFail();
		disableMessageIdSuccess();
		TransactionQueryExecutor trans = null;
		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			SourceBean dettaglio = doSelect(serviceRequest, serviceResponse);

			String xmlCandidatura = dettaglio.getAttribute("ROW.strCandidatura").toString();

			CandidaturaType candidatura = new IstanzeBeanUtils().getCandidaturaFromXml(xmlCandidatura);

			SourceBean comuneNas = (SourceBean) trans.executeQuery("GET_DESCR_COD_COM",
					new Object[] { candidatura.getAnagrafica().getComune() }, "SELECT");

			serviceResponse.setAttribute("comuneNascita", comuneNas.getAttribute("ROW.STRCOM").toString());

			SourceBean cittad = (SourceBean) trans.executeQuery("GET_DESC_CITTADINANZA",
					new Object[] { candidatura.getAnagrafica().getCittadinanza() }, "SELECT");

			serviceResponse.setAttribute("cittadinanza", cittad.getAttribute("ROW.DESCRIZIONE").toString());

			SourceBean comuneRes = (SourceBean) trans.executeQuery("GET_DESCR_COD_COM",
					new Object[] { candidatura.getResidenza().getComune() }, "SELECT");

			serviceResponse.setAttribute("comuneRes", comuneRes.getAttribute("ROW.STRCOM").toString());

			if (candidatura.getExtraUE() != null) {

				SourceBean titolosoggiornoSB = (SourceBean) trans.executeQuery("GET_CODTIPODOCEX",
						new Object[] { candidatura.getExtraUE().getTitolosoggiorno() }, "SELECT");

				String descrTitoloSoggiorno = titolosoggiornoSB.getAttribute("ROW.STRDESCRTIPODOC").toString();

				serviceResponse.setAttribute("descrTitoloSoggiorno", descrTitoloSoggiorno);

				SourceBean motivopermessoSB = (SourceBean) trans.executeQuery("GET_CODMOTIVOPERMSOGGEX",
						new Object[] { candidatura.getExtraUE().getMotivopermesso() }, "SELECT");

				String descrMotivoPermesso = motivopermessoSB.getAttribute("ROW.STRDESCRMOTIVOPERM").toString();

				serviceResponse.setAttribute("descrMotivoPermesso", descrMotivoPermesso);
			}

			serviceResponse.setAttribute("XSDCandidatura", candidatura);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
