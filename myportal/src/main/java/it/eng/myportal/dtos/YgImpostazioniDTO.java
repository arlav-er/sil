package it.eng.myportal.dtos;

import java.util.Date;


public class YgImpostazioniDTO extends AbstractUpdatablePkDTO {
		private static final long serialVersionUID = 3064898772631255022L;

		private Integer id;
		private Boolean flgCreazioneAccount;	
		private Boolean flgInvioSms;
		private Date dtInizioAdesione;
		private Date dtFineAdesione;
		private Boolean flgAbilitazioneAmbiente;
		private String codCheckYgStop;
		private String strMessErrCheckYg;
		private Boolean flgInvioCodRegione;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Boolean getFlgCreazioneAccount() {
			return flgCreazioneAccount;
		}
		public void setFlgCreazioneAccount(Boolean flgCreazioneAccount) {
			this.flgCreazioneAccount = flgCreazioneAccount;
		}
		public Boolean getFlgInvioSms() {
			return flgInvioSms;
		}
		public void setFlgInvioSms(Boolean flgInvioSms) {
			this.flgInvioSms = flgInvioSms;
		}
		public Date getDtInizioAdesione() {
			return dtInizioAdesione;
		}
		public void setDtInizioAdesione(Date dtInizioAdesione) {
			this.dtInizioAdesione = dtInizioAdesione;
		}
		public Date getDtFineAdesione() {
			return dtFineAdesione;
		}
		public void setDtFineAdesione(Date dtFineAdesione) {
			this.dtFineAdesione = dtFineAdesione;
		}
		public Boolean getFlgAbilitazioneAmbiente() {
			return flgAbilitazioneAmbiente;
		}
		public void setFlgAbilitazioneAmbiente(Boolean flgAbilitazioneAmbiente) {
			this.flgAbilitazioneAmbiente = flgAbilitazioneAmbiente;
		}
		public String getCodCheckYgStop() {
			return codCheckYgStop;
		}
		public void setCodCheckYgStop(String codCheckYgStop) {
			this.codCheckYgStop = codCheckYgStop;
		}
		public String getStrMessErrCheckYg() {
			return strMessErrCheckYg;
		}
		public void setStrMessErrCheckYg(String strMessErrCheckYg) {
			this.strMessErrCheckYg = strMessErrCheckYg;
		}
		public Boolean getFlgInvioCodRegione() {
			return flgInvioCodRegione;
		}
		public void setFlgInvioCodRegione(Boolean flgInvioCodRegione) {
			this.flgInvioCodRegione = flgInvioCodRegione;
		}
		
	}