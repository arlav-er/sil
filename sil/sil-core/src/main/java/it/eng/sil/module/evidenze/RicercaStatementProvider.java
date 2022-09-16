package it.eng.sil.module.evidenze;

import java.util.Enumeration;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class RicercaStatementProvider implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RicercaStatementProvider.class.getName());

	SourceBean serviceRequest = RequestContainer.getRequestContainer().getServiceRequest();
	String codiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "codiceFiscale");
	String cognome = StringUtils.getAttributeStrNotNull(serviceRequest, "cognome");
	String nome = StringUtils.getAttributeStrNotNull(serviceRequest, "nome");
	String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");
	Vector codTipoEvidenzaVector = serviceRequest.getAttributeAsVector("codTipoEvidenza");
	String codTipoEvidenza = null;
	String codTipoValidita = StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoValidita");
	String dataScad_DAL = StringUtils.getAttributeStrNotNull(serviceRequest, "dataScadenza_DAL");
	String dataScad_AL = StringUtils.getAttributeStrNotNull(serviceRequest, "dataScadenza_AL");
	String messaggio = StringUtils.getAttributeStrNotNull(serviceRequest, "messaggio");
	String codCPI = StringUtils.getAttributeStrNotNull(serviceRequest, "codCPI");
	Integer cdnGruppo = null;
	Integer cdnProfilo = null;

	private void setBackPageAndUrl() {
		SessionContainer sessionContainer = RequestContainer.getRequestContainer().getSessionContainer();

		User user = (User) sessionContainer.getAttribute(User.USERID);
		// prelevo il gruppo ed il profilo
		cdnGruppo = new Integer(user.getCdnGruppo());
		cdnProfilo = new Integer(user.getCdnProfilo());

		Enumeration params = serviceRequest.getContainedAttributes().elements();
		String key = null;
		String value = null;
		SourceBeanAttribute elem = null;
		String backURL = "";
		while (params.hasMoreElements()) {
			elem = (SourceBeanAttribute) params.nextElement();
			key = elem.getKey();
			value = (String) elem.getValue();
			backURL += "&" + key + "=" + value;
		}
		backURL = backURL.replace("'", "\\'");

		sessionContainer.setAttribute("_BACKPAGE_", "RisultatiRicercaEvidenzePage");
		sessionContainer.setAttribute("_BACKURL_", backURL);
	}

	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		DynamicStatementUtils dsu = new DynamicStatementUtils();

		setBackPageAndUrl();

		String statement = null;

		final String SELECT = " LAV.STRCODICEFISCALE CODICEFISCALE, LAV.STRCOGNOME COGNOME, LAV.STRNOME NOME, "
				+ "TO_CHAR(EVID.DATDATASCAD,'dd/mm/yyyy') datScad, TIPOEVID.STRDESCRIZIONE strTipoEvidenza, "
				+ "case when length(evid.strevidenza)>100 then substr(evid.strevidenza,0,100) || '...' else evid.strevidenza end messaggio, UTENTE.STRCOGNOME ||' '||UTENTE.STRNOME UTINS, "
				+ "CPI.STRDESCRIZIONE STRCPI, EVID.PRGEVIDENZA, LAV.CDNLAVORATORE ";

		dsu.addSelect(SELECT);

		dsu.addFrom("AN_LAVORATORE LAV");
		dsu.addFrom("AN_EVIDENZA EVID");
		dsu.addFrom("DE_TIPO_EVIDENZA TIPOEVID");
		dsu.addFrom("TS_UTENTE UTENTE");
		dsu.addFrom("DE_CPI CPI");
		dsu.addFrom("AN_LAV_STORIA_INF ALSI");

		dsu.addWhere(" LAV.CDNLAVORATORE = EVID.CDNLAVORATORE ");
		dsu.addWhere(" TIPOEVID.PRGTIPOEVIDENZA = EVID.PRGTIPOEVIDENZA ");
		dsu.addWhere(" UTENTE.CDNUT = EVID.CDNUTINS ");
		dsu.addWhere(" LAV.CDNLAVORATORE = ALSI.CDNLAVORATORE ");
		dsu.addWhere(" CPI.CODCPI = ALSI.CODCPITIT ");

		if (tipoRicerca.equals("esatta")) {
			dsu.addWhereIfFilledStrUpper("LAV.STRCODICEFISCALE", codiceFiscale);
			dsu.addWhereIfFilledStrUpper("LAV.STRCOGNOME", cognome);
			dsu.addWhereIfFilledStrUpper("LAV.STRNOME", nome);
		} else {
			dsu.addWhereIfFilledStrLikeUpper("LAV.STRCODICEFISCALE", codiceFiscale, 1);
			dsu.addWhereIfFilledStrLikeUpper("LAV.STRCOGNOME", cognome, 1);
			dsu.addWhereIfFilledStrLikeUpper("LAV.STRNOME", nome, 1);
		}

		dsu.addWhere("TIPOEVID.PRGTIPOEVIDENZA IN (SELECT PRGTIPOEVIDENZA FROM TS_VIS_EVIDENZA WHERE CDNGRUPPO ="
				+ cdnGruppo + " AND CDNPROFILO = " + cdnProfilo + " ) ");

		if (codTipoEvidenzaVector.size() > 0) {
			String valuesIn = "(";
			for (int i = 0; i < codTipoEvidenzaVector.size(); i++) {
				codTipoEvidenza = (String) codTipoEvidenzaVector.get(i);
				valuesIn += codTipoEvidenza + ",";
			}
			valuesIn = valuesIn.substring(0, valuesIn.length() - 1);
			valuesIn += ") ";
			dsu.addWhere("TIPOEVID.PRGTIPOEVIDENZA IN " + valuesIn);
		}

		if (codTipoValidita.equals("V")) {
			dsu.addWhere("EVID.DATDATASCAD > SYSDATE");
		}
		if (codTipoValidita.equals("S")) {
			dsu.addWhere("EVID.DATDATASCAD < SYSDATE");
		}

		boolean flgBetween = (!dataScad_DAL.equals("") && !dataScad_AL.equals(""));

		if (!dataScad_DAL.equals("") && !flgBetween)
			dsu.addWhere("EVID.DATDATASCAD >= TO_DATE('" + dataScad_DAL + "','dd/mm/yyyy')");

		if (!dataScad_AL.equals("") && !flgBetween)
			dsu.addWhere("EVID.DATDATASCAD <= TO_DATE('" + dataScad_AL + "','dd/mm/yyyy')");

		if (flgBetween)
			dsu.addWhere("EVID.DATDATASCAD BETWEEN TO_DATE('" + dataScad_DAL + "','dd/mm/yyyy') and TO_DATE('"
					+ dataScad_AL + "','dd/mm/yyyy')");

		dsu.addWhereIfFilledStrLike("EVID.STREVIDENZA", messaggio, null, 4);

		dsu.addWhereIfFilled("CPI.CODCPI", codCPI, true);

		// prendi solo l'ultimo record di an_lav_storia_inf, è il CPI valido
		dsu.addWhere(" (ALSI.DATFINE IS NULL OR ALSI.DATFINE > SYSDATE) ");

		dsu.addOrder("datscad asc,codicefiscale");

		statement = dsu.getStatement();

		return statement;
	}

}
