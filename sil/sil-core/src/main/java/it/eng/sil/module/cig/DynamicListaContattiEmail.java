package it.eng.sil.module.cig;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;

public class DynamicListaContattiEmail implements IDynamicStatementProvider {
	private static final String SELECT_SQL_BASE = " select  to_char(AG_CONTATTO.DATCONTATTO,'dd/mm/yyyy') as Data, "
			+ " AG_CONTATTO.STRORACONTATTO as Ora, "
			+ " InitCap(AN_SPI.STRNOME || ' '||AN_SPI.STRCOGNOME) as Operatore, "
			+ " concat(substr(AG_CONTATTO.TXTCONTATTO,1,instr(AG_CONTATTO.TXTCONTATTO, 'che')+3),'[...]') as Testo, "
			+ " AG_CONTATTO.STRCELLSMSINVIO as Destinatario, " + " DE_MOTIVO_CONTATTOAG.STRDESCRIZIONE as Motivo , "
			+ " decode(AG_CONTATTO.FLGINVIATOSMS,'S','S','N','N',null,'In attesa') as Inviato "
			+ " from AG_CONTATTO, CI_CORSO_CATALOGO, AN_SPI, DE_MOTIVO_CONTATTOAG " + " where "
			+ " AG_CONTATTO.PRGCONTATTO = CI_CORSO_CATALOGO.PRGCONTATTOISCR and "
			+ " AG_CONTATTO.PRGCONTATTO = CI_CORSO_CATALOGO.PRGCONTATTOISCR and "
			+ " AG_CONTATTO.PRGSPICONTATTO = AN_SPI.PRGSPI and "
			+ " AG_CONTATTO.PRGMOTCONTATTO = DE_MOTIVO_CONTATTOAG.PRGMOTCONTATTO(+) and "
			+ " CI_CORSO_CATALOGO.PRGCORSOCI in (select PRGCORSOCI from CI_CORSO "
			+ " where  PRGALTRAISCR in (select PRGALTRAISCR from AM_ALTRA_ISCR where ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(req, "cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		buf.append(" CDNLAVORATORE = " + cdnLavoratore + ")) order by AG_CONTATTO.DATCONTATTO desc ");

		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
