package it.eng.sil.module.condizionalita.rdc;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

public class RicercaEventiCondizionalita implements IDynamicStatementProvider {
	
	private static final String QUERY = 
			" SELECT "+
					"     am_condizionalita.prgcondizionalita, "+
					"     an_lavoratore.strcodicefiscale, "+
					"     an_lavoratore.strcognome "+
					"     || ' ' "+
					"     ||an_lavoratore.strnome as datiLav, "+
					"     to_char(am_condizionalita.datevento, 'dd/mm/yyyy') AS strdatevento, "+
					"     am_condizionalita.codeventocond              AS tipoevento, "+
					"     am_condizionalita.strprotocolloinps, "+
					"     am_condizionalita.coddomcondizionalita   AS tipodomanda, "+
					"     de_cpi.codcpimin "+
					"     || ' - ' "+
					"     || de_cpi.strdescrizionemin AS strcpi, "+
					"     to_char(am_condizionalita.datinvio, 'dd/mm/yyyy hh24:mi') AS strdatinvio, "+
					"     to_char(or_colloquio.datcolloquio, 'dd/mm/yyyy') AS strdatinizioprogramma, "+
					"     to_char(or_colloquio.datfineprogramma, 'dd/mm/yyyy') AS strdatfineprogramma, "+
					"     de_servizio.strdescrizione                   AS strprogramma, "+
					"     am_condizionalita.datevento, am_condizionalita.datinvio, an_lavoratore.cdnlavoratore, de_cpi.codcpi "+
					" FROM "+
					"     am_condizionalita "+
					"     INNER JOIN de_cpi ON ( de_cpi.codcpi = am_condizionalita.codcpi ) "+
					"     INNER JOIN an_lavoratore ON ( an_lavoratore.cdnlavoratore = am_condizionalita.cdnlavoratore ) "+
					"     LEFT OUTER JOIN or_colloquio ON ( or_colloquio.prgcolloquio = am_condizionalita.prgcolloquio ) "+
					"     LEFT OUTER JOIN de_servizio ON ( de_servizio.codservizio = or_colloquio.codservizio ) ";
	
	private static final String QUERY_CSV = 
			" SELECT "+
					"     an_lavoratore.strcodicefiscale as codice_fiscale, "+
					"     an_lavoratore.strcognome "+
					"     || ' ' "+
					"     ||an_lavoratore.strnome as lavoratore, "+
					"     to_char(am_condizionalita.datevento, 'dd/mm/yyyy') AS data_evento_cond, "+
					"     am_condizionalita.codeventocond || ' - ' || "+
					"     DE_CONDIZIONALITA.strdescrizione   AS tipo_evento, " +
					"     am_condizionalita.strprotocolloinps AS protocollo_inps, "+
					"     am_condizionalita.coddomcondizionalita   AS tipo_domanda, "+
					"     to_char(am_condizionalita.DATDOMANDA, 'dd/mm/yyyy') AS data_domanda, "+
					"     de_cpi.codcpimin "+
					"     || ' - ' "+
					"     || de_cpi.strdescrizionemin AS cpi, "+
					"     case when am_condizionalita.datinvio is not null then "+
					"     	to_char(am_condizionalita.datinvio, 'dd/mm/yyyy hh24:mi') "+
					"	  	else ' ' " +
					"	  end AS data_invio, "+
					"     de_servizio.strdescrizione                   AS programma, "+
					"     case when or_colloquio.datcolloquio is not null then "+
					"     	to_char(or_colloquio.datcolloquio, 'dd/mm/yyyy') "+
					"	  	else ' ' " +
					"	  end AS data_inizio_prog, "+
					"     case when or_colloquio.datfineprogramma is not null then "+
					"     	to_char(or_colloquio.datfineprogramma, 'dd/mm/yyyy') "+
					"	  	else ' ' " +
					"	  end AS data_fine_prog, "+
					"     am_condizionalita.datevento as ordine_1, am_condizionalita.datinvio as ordine_2"+
					" FROM "+
					"     am_condizionalita "+
					"     INNER JOIN DE_CONDIZIONALITA ON (DE_CONDIZIONALITA.CODEVENTO = am_condizionalita.CODEVENTOCOND)" +
					"     INNER JOIN de_cpi ON ( de_cpi.codcpi = am_condizionalita.codcpi ) "+
					"     INNER JOIN an_lavoratore ON ( an_lavoratore.cdnlavoratore = am_condizionalita.cdnlavoratore ) "+
					"     LEFT OUTER JOIN or_colloquio ON ( or_colloquio.prgcolloquio = am_condizionalita.prgcolloquio ) "+
					"     LEFT OUTER JOIN de_servizio ON ( de_servizio.codservizio = or_colloquio.codservizio ) ";

	
	@Override
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
	 
		String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(req, "cdnLavoratore");
		String dataEventoDa = SourceBeanUtils.getAttrStrNotNull(req, "dataEventoDa");
		String dataEventoA = SourceBeanUtils.getAttrStrNotNull(req, "dataEventoA");
		String statoInvio = SourceBeanUtils.getAttrStrNotNull(req, "statoInvio");
		String tipoDomanda = SourceBeanUtils.getAttrStrNotNull(req, "tipoDomanda");
		String tipoEvento = SourceBeanUtils.getAttrStrNotNull(req, "tipoEvento");
		String ordDataEventoDC = StringUtils.getAttributeStrNotNull(req, "ordDataEventoDC");
		String ordDataEvento = StringUtils.getAttributeStrNotNull(req, "ordDataEvento");
		String ordDataInvioDC = StringUtils.getAttributeStrNotNull(req, "ordDataInvioDC");
		String ordDataInvio = StringUtils.getAttributeStrNotNull(req, "ordDataInvio");
		String ordINPSDC = StringUtils.getAttributeStrNotNull(req, "ordINPSDC");
		String ordINPS = StringUtils.getAttributeStrNotNull(req, "ordINPS"); 
		String ordCF = StringUtils.getAttributeStrNotNull(req, "ordCF"); 
		
		StringBuffer queryBuffer= new StringBuffer(QUERY);
		
		if(req.containsAttribute("CSV")){
			queryBuffer= new StringBuffer(QUERY_CSV);
		}
		
		StringBuffer queryParams = new StringBuffer();
		
		
		if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) {
			cdnLavoratore = StringUtils.replace(cdnLavoratore, "'", "''");
			if (queryParams.length() == 0) {
				queryParams.append(" WHERE ");
			} else {
				queryParams.append(" AND ");
			}
			queryParams.append(" upper(an_lavoratore.cdnlavoratore) = '" + cdnLavoratore.toUpperCase() + "' ");
		}
		
		if ((dataEventoDa != null) && (!dataEventoDa.equals(""))) {
			dataEventoDa = StringUtils.replace(dataEventoDa, "'", "''");
			if (queryParams.length() == 0) {
				queryParams.append(" WHERE ");
			} else {
				queryParams.append(" AND ");
			}
			queryParams.append(" trunc(am_condizionalita.datevento) >= to_date('"+ dataEventoDa +"','dd/mm/yyyy') ");
		}

		if ((statoInvio != null) && (!statoInvio.equals(""))) {
			statoInvio = StringUtils.replace(statoInvio, "'", "''");
			if (queryParams.length() == 0) {
				queryParams.append(" WHERE ");
			} else {
				queryParams.append(" AND ");
			}
			if(statoInvio.equalsIgnoreCase("I")) {
				queryParams.append("  am_condizionalita.datinvio is not null ");
			}else if(statoInvio.equalsIgnoreCase("D")) {
				queryParams.append("  am_condizionalita.datinvio is null ");
			}
		}
		
		if ((dataEventoA != null) && (!dataEventoA.equals(""))) {
			dataEventoA = StringUtils.replace(dataEventoDa, "'", "''");
			if (queryParams.length() == 0) {
				queryParams.append(" WHERE ");
			} else {
				queryParams.append(" AND ");
			}
			queryParams.append(" trunc(am_condizionalita.datevento) <= to_date('"+ dataEventoA +"','dd/mm/yyyy') ");
		}		
		
		if ((tipoDomanda != null) && (!tipoDomanda.equals(""))) {
			tipoDomanda = StringUtils.replace(tipoDomanda, "'", "''");
			if (queryParams.length() == 0) {
				queryParams.append(" WHERE ");
			} else {
				queryParams.append(" AND ");
			}
			queryParams.append(" upper(am_condizionalita.coddomcondizionalita) = upper('"+ tipoDomanda.toUpperCase() +"') ");
		}
		
		if ((tipoEvento != null) && (!tipoEvento.equals(""))) {
			tipoEvento = StringUtils.replace(tipoEvento, "'", "''");
			if (queryParams.length() == 0) {
				queryParams.append(" WHERE ");
			} else {
				queryParams.append(" AND ");
			}
			queryParams.append(" upper(am_condizionalita.codeventocond) = upper('"+ tipoEvento.toUpperCase() +"') ");
		}
		
		if( (ordCF == null || ordCF.equals("")) 
				&& (ordDataInvio == null || ordDataInvio.equals(""))  
				&& (ordINPS == null || ordINPS.equals("")) 
				&& (ordDataEvento == null || ordDataEvento.equals("")) 
				){
			queryParams.append(" ORDER BY am_condizionalita.datevento DESC");
		}else{
			StringBuffer bufOrd = new StringBuffer();
			
			queryParams.append(" ORDER BY ");
			if( ordDataInvio != null && !ordDataInvio.equals("")){
				bufOrd.append(" am_condizionalita.datinvio ");
				if(ordDataInvioDC.equalsIgnoreCase("D"))
					bufOrd.append("DESC");
				else bufOrd.append("ASC");
			}
			if( ordDataEvento != null && !ordDataEvento.equals("")){
				if (bufOrd.length() > 0) {
					bufOrd.append(",");
				}
				bufOrd.append(" am_condizionalita.datevento ");
				if(ordDataEventoDC.equalsIgnoreCase("D"))
					bufOrd.append("DESC");
				else bufOrd.append("ASC");
			}
			if( ordINPS != null && !ordINPS.equals("")){
				if (bufOrd.length() > 0) {
					bufOrd.append(",");
				}
				bufOrd.append(" am_condizionalita.strprotocolloinps ");
				if(ordINPSDC.equalsIgnoreCase("D"))
					bufOrd.append("DESC");
				else bufOrd.append("ASC");
			}
			if( ordCF != null && !ordCF.equals("")){
				if (bufOrd.length() > 0) {
					bufOrd.append(",");
				}
				bufOrd.append(" an_lavoratore.strcodicefiscale ");
			}
			
			queryParams.append(bufOrd);
		}
		
		queryBuffer.append(queryParams.toString());
		return queryBuffer.toString();
	}

	 

}
