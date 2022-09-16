package it.eng.sil.coop.webservices.bean;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.security.ProfileDataFilter;
import it.eng.sil.security.User;
import it.eng.sil.util.StatementUtils;
import it.eng.sil.util.Utils;

public class UserBean {
	private boolean flgCompetenza = true;
	private BigDecimal userSP = null;
	private User user = null;

	public UserBean(BigDecimal userSP, BigDecimal cdnlavoratore) throws Exception {
		this.userSP = userSP;
		caricaUser(cdnlavoratore);
	}

	private void caricaUser(BigDecimal cdnlavoratore) throws Exception {
		SourceBean sb = StatementUtils.getSourceBeanByStatement("GET_UTENTE_INFO", this.userSP.toString());
		String username = Utils.notNull(sb.getAttribute("ROW.STRLOGIN"));
		String nome = Utils.notNull(sb.getAttribute("ROW.STRNOME"));
		String cognome = Utils.notNull(sb.getAttribute("ROW.STRCOGNOME"));

		this.user = new User(this.userSP.intValue(), username, nome, cognome);
		this.user.setCdnProfilo(4);

		String _pageDaValutare = "DispoDettaglioPage";
		ProfileDataFilter filter = new ProfileDataFilter(user, _pageDaValutare);
		filter.setCdnLavoratore(cdnlavoratore);
		setFlgCompetenza(filter.canEditLavoratore());
	}

	public boolean getFlgCompetenza() {
		return this.flgCompetenza;
	}

	public void setFlgCompetenza(boolean val) {
		this.flgCompetenza = val;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User val) {
		this.user = val;
	}

}
