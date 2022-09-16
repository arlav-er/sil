package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynListaMovimenti implements IDynamicStatementProvider {
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String query = "Select  Mov.Prgmovimento Prgmov, Mov.Prgmovimentoprec Prgmovimentoprec, "
				+ "Mov.Prgmovimentosucc Prgmovimentosucc, Lav.Cdnlavoratore Cdnlav, "
				+ "Az.Prgazienda Prgaz, Uaz.Prgunita Prguaz, "
				+ "To_Char (Mov.Datcomunicaz, 'DD/MM/YYYY') Datcomunicaz, "
				+ "To_Char (Mov.Datiniziomov, 'DD/MM/YYYY') Datamov, " + "Substr (Mov.Codtipomov, 1, 1) Codtipomov, "
				+ "	Substr (Mov.Codtipomov, 1, 1) " + "|| Decode (Mov.Codtipomov, " + "			'AVV', '', "
				+ "			Decode (Nvl (Mov.Prgmovimentoprec, '0'), "
				+ "					'0', '&lt;--', '&nbsp;&nbsp;&nbsp;') " + "		   ) Codtipomovvisual, "
				+ "Mov.CODTIPOCONTRATTO Codtipoass, "
				+ "Mov.CODTIPOCONTRATTO || ' ' || Mov.Codmvcessazione Codasscesvisual, "
				+ "Decode (Mov.Codmonomovdich, " + "		 'O', 'Obb', " + "		 'D', 'Doc', "
				+ "		 'C', 'Dic' " + "		) Prov, Mov.Codmonotipofine Codmonotipofine, "
				+ "Mov.Codmonotempo Codmonotempo, Mov.Codstatoatto, " + "Mov.Codmotannullamento, "
				+ "To_Char (Mov.Datfinemoveffettiva, 'DD/MM/YYYY') Datfinemoveffettiva, "
				+ "To_Char (Mov.Datfinemoveffettiva, " + "		  'DD/MM/YYYY' " + "		 ) Datfinemoveffettivavis, "
				+ "Mov.Decretribuzionemen, Mov.Decretribuzionemensanata, " + "Decode (Mov.Codtipomov, "
				+ "		 'CES', '', " + "			'R.' " + "		 || Mov.Decretribuzionemen "
				+ "		 || Decode (Nvl (Mov.Codtipodich, '0'), " + "					'0', '', "
				+ "					' S.' || Mov.Decretribuzionemensanata " + "				   ) "
				+ "		) Movretribuzione, " + "Tmov.Strdescrizione Descrmov, "
				+ "Lav.Strcognome || ' ' || Lav.Strnome Cognomenomelav, "
				+ "Lav.Strcodicefiscale Codfisclav, Az.Strragionesociale Ragsocaz, " + "	Uaz.Strindirizzo "
				+ " || ', ' " + "|| Com.Strdenominazione " + "|| '(' " + "|| Rtrim (Prov.Stristat) "
				+ "|| ')' Indirazienda, " + "Com.Strdenominazione Comaz, Prov.Strdenominazione Provaz, "
				+ "De_Tipo_Contratto.Codmonotipo Codmonotipoass " + "From Am_Movimento Mov, "
				+ "	 De_Mv_Tipo_Mov Tmov, " + "	 An_Unita_Azienda Uaz, " + "	 An_Azienda Az, "
				+ "	 An_Lavoratore Lav, " + "	 De_Comune Com, " + "	 De_Provincia Prov, " + "	 De_Tipo_Contratto "
				+ "Where Mov.Codtipomov = Tmov.Codtipomov "
				+ " And Mov.CODTIPOCONTRATTO = De_Tipo_Contratto.CODTIPOCONTRATTO (+) "
				+ "And Uaz.Codcom = Com.Codcom(+) " + "And Com.Codprovincia = Prov.Codprovincia(+) "
				+ "And Mov.Codstatoatto = 'PR' " + "And Mov.Cdnlavoratore = Lav.Cdnlavoratore "
				+ "And Mov.Prgazienda = Az.Prgazienda " + "And Mov.Prgazienda = Uaz.Prgazienda "
				+ "And Mov.Prgunita = Uaz.Prgunita " + "And Uaz.Codcom = Com.Codcom "
				+ "And Com.Codprovincia = Prov.Codprovincia " + "And Mov.Cdnlavoratore = "
				+ req.getAttribute("CDNLAVORATORE").toString() + " Order By Mov.Datiniziomov Desc, "
				+ "		 Decode (Mov.Codtipomov, 'AVV', 1, 'PRO', 2, 'TRA', 3, 'CES', 4) Desc, "
				+ "		 Codtipomov, " + "		 Cognomenomelav, " + "		 Ragsocaz, " + "		 Indirazienda";
		return (query);
	}
}
