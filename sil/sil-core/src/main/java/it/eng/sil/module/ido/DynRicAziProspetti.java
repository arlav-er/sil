package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;

public class DynRicAziProspetti implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select p.PRGPROSPETTOINF, p.PRGAZIENDA, p.PRGUNITA, p.PRGAZREFERENTE, "
			+ "    az.strcodicefiscale as codicefiscale, " + " 	az.strpartitaiva as piva, "
			+ "	az.strragionesociale as ragionesociale, " + " 	p.numannorifprospetto as anno, " +
			// scopertura
			// " nvl(to_char(round(p.numscopcentnonvedenti)),'_') as numscoperturanved,
			// nvl(to_char(round(p.numscopmassofisioterapisti)),'_') as numscoperturamassofisio , " +
			"    de_tipo_azienda.strdescrizione, auz.strindirizzo, " + "    de_comune.strdenominazione, "
			+ " 	p.flggradualita flggradualita, "
			+ "    p.flgsospensione flgsospensione, p.flgsospensionemob flgsospensionemob, "
			+ "    to_char(p.DATSOSPENSIONE,'dd/mm/yyyy') datSospensione, " + "	p.codmonostatoprospetto, " + "    CASE "
			+ "    WHEN p.codmonostatoprospetto = 'A'  " + "	     THEN 'In corso d&#39;anno' "
			+ "    WHEN p.codmonostatoprospetto = 'S'     " + "      THEN 'Storicizzato' "
			+ "    WHEN p.codmonostatoprospetto = 'V' " + "      THEN 'Da validare' "
			+ "    WHEN p.codmonostatoprospetto = 'N' " + "      THEN 'Annullato' "
			+ "    WHEN p.codmonostatoprospetto = 'U' " + "	     THEN 'Storicizzato: uscita dall&#39;obbligo' "
			+ "    END as stato," + "    CASE " + "    WHEN p.codmonocategoria = 'A'  "
			+ "	     THEN 'più di 50 dipendenti' " + "    WHEN p.codmonocategoria = 'B'     "
			+ "      THEN 'da 36 a 50 dipendenti' " + "    WHEN p.codmonocategoria = 'C' "
			+ "      THEN 'da 15 a 35 dipendenti' " + "	ELSE ' ' " + "    END as fascia,"
			+ "	p.codprovincia as prov, " + "    to_char(p.datconsegnaprospetto,'dd/mm/yyyy') datconsegna, "
			+ "    dp.strdenominazione as provincia";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		String anno = (String) req.getAttribute("anno");
		String codMonoCategoria = (String) req.getAttribute("codMonoCategoria");
		String codMonoStatoProspetto = (String) req.getAttribute("codMonoStatoProspetto");
		String codMonoProv = (String) req.getAttribute("codMonoProv");
		String flgSospNazMob = (String) req.getAttribute("flgSospensioneMob");
		String flgCapoGruppo = (String) req.getAttribute("flgCapoGruppo");
		String strCodFiscAzCapogruppo = (String) req.getAttribute("strCFAZCapogruppo");
		String flgCompetenza = (String) req.getAttribute("flgCompetenzaProsp");
		// 07/04/2010 inserito nuovo campo d'accordo config
		String numNved_Masso = (String) req.getAttribute("numNved_Masso");
		String flagScoperturaCentral_N_Vedente_o_Masso_Fisiot = SourceBeanUtils.getAttrStrNotNull(req,
				"flagScoperturaCentral_N_Vedente_o_Masso_Fisiot");
		// Proviamo se c'è la conf per Centralisnista e Masso
		if ((numNved_Masso != null) && (!numNved_Masso.equals("")) && (numNved_Masso.equals("1"))) {
			buf.append(
					"  , nvl(to_char(round(p.numscopcentnonvedenti)),'0') as numscoperturanved, nvl(to_char(round(p.numscopmassofisioterapisti)),'0') as numscoperturamassofisio  ");
		}

		String flgConvenzione = (String) req.getAttribute("FlgConvenzione");

		if ((flgConvenzione != null) && (!flgConvenzione.equals(""))) {
			buf.append(" ,pg_coll_mirato_2.getScopertura(p.prgprospettoinf, '" + flgConvenzione + "') as scopertura ");
		}

		buf.append(" from cm_prospetto_inf p " + " inner join an_azienda az on az.prgazienda = p.prgazienda "
				+ " INNER JOIN AN_UNITA_AZIENDA auz on az.prgAzienda=auz.prgAzienda "
				+ " and auz.prgunita = p.prgunita " + " inner join de_provincia dp on dp.codprovincia = p.codprovincia "
				+ " inner join de_comune on de_comune.codcom = auz.codcom "
				+ " inner join de_tipo_azienda on de_tipo_azienda.codtipoazienda = az.codtipoazienda"
				+ " where p.codprovincia = " + "	        (select de_provincia.CODPROVINCIA "
				+ "			 from de_provincia "
				+ "			 inner join ts_generale on (de_provincia.codprovincia = ts_generale.CODPROVINCIASIL))  ");

		if ((flgCompetenza != null) && (!flgCompetenza.equals(""))) {
			buf.append(" and p.flgcompetenza = '" + flgCompetenza + "' ");
		}

		if ((flgConvenzione != null)
				&& (!flgConvenzione.equals("") && (flagScoperturaCentral_N_Vedente_o_Masso_Fisiot.equals("N")))) {
			buf.append(" and pg_coll_mirato_2.checkscopertura(p.prgprospettoinf, '" + flgConvenzione + "') = 1 ");
		}

		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" and  p.numannorifprospetto = '" + anno + "' ");
		}

		if ((codMonoCategoria != null) && (!codMonoCategoria.equals(""))) {
			if (codMonoCategoria.equals(ProspettiConstant.CATEGORIA_NULLA)) {
				buf.append("and p.codmonocategoria is null ");
			} else {
				buf.append("and p.codmonocategoria = '" + codMonoCategoria + "' ");
			}
		}

		if ((codMonoStatoProspetto != null) && (!codMonoStatoProspetto.equals(""))) {
			buf.append("and p.codMonoStatoProspetto = '" + codMonoStatoProspetto + "' ");
		} else {
			buf.append("and p.codMonoStatoProspetto IN ('A','S','V','U') ");
		}

		if ((codMonoProv != null) && (!codMonoProv.equals(""))) {
			buf.append("and p.codMonoProv = '" + codMonoProv + "' ");
		}

		if ((flgSospNazMob != null) && (!flgSospNazMob.equals(""))) {
			buf.append("and p.flgsospensionemob = '" + flgSospNazMob + "' ");
		}

		if ((flgCapoGruppo != null) && (!flgCapoGruppo.equals(""))) {
			buf.append("and p.flgcapogruppo = '" + flgCapoGruppo + "' ");
		}

		if ((strCodFiscAzCapogruppo != null) && (!strCodFiscAzCapogruppo.equals(""))) {
			buf.append("and p.strcfazcapogruppo = '" + strCodFiscAzCapogruppo + "' ");
		}

		// Masso e centralinista
		// p.numscopcentnonvedenti as numscoperturanved, p.numscopmassofisioterapisti as numscoperturamassofisio , " +
		if ((flagScoperturaCentral_N_Vedente_o_Masso_Fisiot != null)
				&& (!flagScoperturaCentral_N_Vedente_o_Masso_Fisiot.equals(""))
				&& (flagScoperturaCentral_N_Vedente_o_Masso_Fisiot.equals("S"))) {
			buf.append("and (p.numscopcentnonvedenti > 0 or p.numscopmassofisioterapisti > 0) ");
		}
		if ((flagScoperturaCentral_N_Vedente_o_Masso_Fisiot != null)
				&& (!flagScoperturaCentral_N_Vedente_o_Masso_Fisiot.equals(""))
				&& (flagScoperturaCentral_N_Vedente_o_Masso_Fisiot.equals("N"))) {
			buf.append(
					"and ((p.numscopcentnonvedenti <= 0 or p.numscopmassofisioterapisti <= 0) and (p.numscopmassofisioterapisti is not null or numscopcentnonvedenti is not null)) ");
		}

		buf.append(
				" order by p.datconsegnaprospetto desc," + " az.strRagioneSociale, auz.prgunita, p.PRGPROSPETTOINF ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}