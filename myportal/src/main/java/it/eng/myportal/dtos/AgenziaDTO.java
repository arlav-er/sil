package it.eng.myportal.dtos;

import java.util.Date;

import javax.validation.constraints.Size;

public class AgenziaDTO implements IDTO {
		private static final long serialVersionUID = 3064898772631255022L;
		private Boolean estera;
		@Size(max=30)
		private String numeroProvvedimento;
		private Date dataProvvedimento;
		private DeComuneDTO comune;
		@Size(max=50)
		private String numeroIscrizione;
		private Date dataIscrizione;

		public AgenziaDTO() {
			comune = new DeComuneDTO();
		}

		public Boolean getEstera() {
			return estera;
		}

		public void setEstera(Boolean estera) {
			this.estera = estera;
		}

		public String getNumeroProvvedimento() {
			return numeroProvvedimento;
		}

		public void setNumeroProvvedimento(String numeroProvvedimento) {
			this.numeroProvvedimento = numeroProvvedimento;
		}

		public Date getDataProvvedimento() {
			return dataProvvedimento;
		}

		public void setDataProvvedimento(Date dataProvvedimento) {
			this.dataProvvedimento = dataProvvedimento;
		}

		public DeComuneDTO getComune() {
			return comune;
		}

		public void setComune(DeComuneDTO comune) {
			this.comune = comune;
		}

		public String getNumeroIscrizione() {
			return numeroIscrizione;
		}

		public void setNumeroIscrizione(String numeroIscrizione) {
			this.numeroIscrizione = numeroIscrizione;
		}

		public Date getDataIscrizione() {
			return dataIscrizione;
		}

		public void setDataIscrizione(Date dataIscrizione) {
			this.dataIscrizione = dataIscrizione;
		}
	}