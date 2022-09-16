package it.eng.sil.module.movimenti;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

/**
 * Crea la query dinamica per trovare tutti i movimenti che possono fungere da precedente per un certo lavoratore. Può
 * effettuare la ricerca sia nel caso che venga indicata l'azienda sia nel caso che non venga specificata.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class DynRicercaMovimentiCollegabili implements IDynamicStatementProvider {
	private final String QUERY_PROLOGO = "SELECT TO_CHAR(PRGMOVIMENTO) prgMovColl, "
			+ "TO_CHAR(MOV.PRGAZIENDA) prgAziendaColl, " + "TO_CHAR(MOV.PRGUNITA) prgUnitaColl, "
			+ "TO_CHAR(NUMKLOMOV) numKloMovColl, " + "CODTIPOMOV codTipoMovColl, " + "MOV.CODMVCESSAZIONE, "
			+ "TO_CHAR(MOV.datInizioMov, 'DD/MM/YYYY') DATAMOV, " + "SUBSTR(MOV.codTipoMov, 1, 1 ) codTipoMov, "
			+ "TO_CHAR(MOV.DATINIZIOAVV, 'DD/MM/YYYY') DATAINIZIOAVV, "
			+ "TO_CHAR(MOV.DATINIZIOMOVPREC, 'DD/MM/YYYY') DATAINIZIOMOVPREC, " + "MOV.CODMONOTEMPO CODMONOTEMPOAVV, "
			+ "TO_CHAR(MOV.datFineMov, 'DD/MM/YYYY') dataFineMovPrec, "
			+ "TO_CHAR(MOV.datFinePF, 'DD/MM/YYYY') datFinePF, "
			+ "case when (MOV.datFinePF is not null) then TO_CHAR((MOV.datFinePF + 1), 'DD/MM/YYYY') " + "else null "
			+ "end as datFinePFGGSucc, " + "MOV.CODTIPOCONTRATTO CODTIPOASS, " + "LAV.strCognome COGNOMELAV, "
			+ "LAV.strNome NOMELAV, " + "LAV.strCodiceFiscale CODFISCLAV, " + "AZ.strRagioneSociale RAGSOCAZ, "
			+ "UAZ.strIndirizzo || ', ' || COM.strDenominazione || '(' || RTRIM(PROV.strIstat) || ')' IndirAzienda,  "
			+ "UAZ.STRCFAZESTERA CODFISCAZESTERA, UAZ.STRRAGSOCAZESTERA RAGSOCAZESTERA, "
			+ "MOV.CODMONOTIPOFINE CODMONOTIPOFINE, MOV.CODTIPOENTEPROMOTORE, "
			+ "TO_CHAR(MOV.DATFINEMOVEFFETTIVA, 'DD/MM/YYYY') DATFINEMOVEFFETTIVA, "
			+ "DE_TIPO_CONTRATTO.CODMONOTIPO codMonoTipo, DE_TIPO_CONTRATTO.FLGTI flgTI " + "FROM AM_MOVIMENTO MOV, "
			+ "AN_UNITA_AZIENDA UAZ,  " + "AN_AZIENDA AZ, "
			+ "AN_LAVORATORE LAV, DE_COMUNE COM, DE_PROVINCIA PROV, DE_TIPO_CONTRATTO  " + "WHERE ";

	private String QUERY_EPILOGO = "AND MOV.prgAzienda = AZ.prgAzienda " + "AND MOV.prgAzienda = UAZ.prgAzienda "
			+ "AND MOV.prgUnita = UAZ.prgUnita " + "AND MOV.cdnLavoratore = LAV.cdnLavoratore "
			+ "AND UAZ.codCom = COM.codCom " + "AND COM.codProvincia = PROV.codProvincia "
			+ "AND MOV.PrgMovimentoSucc IS NULL " + "AND MOV.CODMONOTIPOFINE IS NULL "
			// + "AND MOV.CODTIPOMOV != 'CES' "
			+ "AND MOV.CODSTATOATTO = 'PR' " + "AND MOV.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+) ";

	public DynRicercaMovimentiCollegabili() {
	}

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		// Estraggo i dati dalla Request
		SourceBean req = requestContainer.getServiceRequest();
		Object prgMovimentoApp = req.getAttribute("PRGMOVIMENTOAPP");
		String prgAzienda = "";
		String prgUnita = "";
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(req, "CDNLAVORATORE");
		String codTipoMov = StringUtils.getAttributeStrNotNull(req, "CODTIPOMOV");
		String context = StringUtils.getAttributeStrNotNull(req, "CONTEXT");
		boolean trasferimento = (prgMovimentoApp != null && req.containsAttribute("TRASFERIMENTOAZIENDA"));
		// Se stiamo validando una trasformazione che comporta un trasferimento azienda, allora
		// recupero i dati dell'azienda precedente dal movimento che si sta validando e identificato da prgMovimentoApp
		if (!trasferimento) {
			prgAzienda = StringUtils.getAttributeStrNotNull(req, "PRGAZIENDA");
			prgUnita = StringUtils.getAttributeStrNotNull(req, "PRGUNITA");
		} else {
			Vector aziendaPrecedente = ProcessorsUtils.cercaTrasfAziendaPrecValManuale(prgMovimentoApp, context);
			if (aziendaPrecedente.size() > 2) {
				String indAzPrec = aziendaPrecedente.get(aziendaPrecedente.size() - 1).toString().toUpperCase();
				String comuneAzPrec = aziendaPrecedente.get(aziendaPrecedente.size() - 2).toString().toUpperCase();
				for (int i = 0; i < aziendaPrecedente.size() - 2; i++) {
					SourceBean azienda = (SourceBean) aziendaPrecedente.get(i);
					prgAzienda = azienda.getAttribute("PRGAZIENDA").toString();
					String comuneAz = azienda.containsAttribute("CODCOM")
							? azienda.getAttribute("CODCOM").toString().toUpperCase()
							: "";
					String indirizzoAz = azienda.containsAttribute("STRINDIRIZZO")
							? azienda.getAttribute("STRINDIRIZZO").toString().toUpperCase()
							: "";
					if (comuneAz.equals(comuneAzPrec) && indirizzoAz.equals(indAzPrec)) {
						prgUnita = azienda.getAttribute("PRGUNITA").toString();
						break;
					}
				}
			}
		}
		boolean proroga = (codTipoMov != null && codTipoMov.equalsIgnoreCase("PRO"));
		boolean trasformazione = (codTipoMov != null && codTipoMov.equalsIgnoreCase("TRA"));
		boolean cessazione = (codTipoMov != null && codTipoMov.equalsIgnoreCase("CES"));
		StringBuffer buf = new StringBuffer();
		buf.append(QUERY_PROLOGO);
		buf.append(" MOV.CDNLAVORATORE = TO_NUMBER(" + cdnLavoratore + ") ");
		if (!prgAzienda.equals("") && !prgUnita.equals("")) {
			buf.append("AND MOV.PRGAZIENDA = TO_NUMBER(" + prgAzienda + ") ");
			buf.append("AND MOV.PRGUNITA = TO_NUMBER(" + prgUnita + ") ");
		} else {
			buf.append("AND MOV.PRGAZIENDA IS NULL ");
			buf.append("AND MOV.PRGUNITA IS NULL ");
		}

		if (trasformazione) {
			buf.append(" AND (nvl(DE_TIPO_CONTRATTO.CODMONOTIPO,'0') != 'N' OR MOV.CODTIPOCONTRATTO IN ("
					+ DeTipoContrattoConstant.listaContrattiAut_Tra + ")) ");
		}
		// Se voglio inserire una proroga scarto tutti i movimenti a TI
		if (proroga) {
			buf.append("AND MOV.CODMONOTEMPO = 'D'");
		}
		if (cessazione) {
			QUERY_EPILOGO = QUERY_EPILOGO
					+ " AND ( (MOV.CODTIPOMOV = 'CES' AND NVL(MOV.CODMVCESSAZIONE, ' ') = 'SC') OR (MOV.CODTIPOMOV != 'CES') ) ";
		} else {
			QUERY_EPILOGO = QUERY_EPILOGO + " AND MOV.CODTIPOMOV != 'CES' ";
		}
		QUERY_EPILOGO = QUERY_EPILOGO
				+ " ORDER BY MOV.datInizioMov DESC, codTipoMov, COGNOMELAV, NOMELAV, RAGSOCAZ, IndirAzienda ";
		buf.append(QUERY_EPILOGO);
		return buf.toString();
	}
}