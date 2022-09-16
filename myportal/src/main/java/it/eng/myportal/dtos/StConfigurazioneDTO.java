package it.eng.myportal.dtos;

import java.util.Date;


public class StConfigurazioneDTO extends AbstractUpdatablePkDTO {
		private static final long serialVersionUID = 3064898772631255022L;

		private Integer idConfigurazione;
		
		private Boolean flagAggiornamento;
		private Date dataAggiornamentoMonit;
        private String versione;   

		public StConfigurazioneDTO() {
			super();			
		}

		public String getVersione() {
			return versione;
		}


		public void setVersione(String versione) {
			this.versione = versione;
		}
		
		public Integer getIdConfigurazione() {
			return idConfigurazione;
		}

		public void setIdConfigurazione(Integer idConfigurazione) {
			this.idConfigurazione = idConfigurazione;
		}
		
		/**
		 * @return the flagAggiornamento
		 */
		public Boolean getFlagAggiornamento() {
			return flagAggiornamento;
		}

		/**
		 * @param flagAggiornamento the flagAggiornamento to set
		 */
		public void setFlagAggiornamento(Boolean flagAggiornamento) {
			this.flagAggiornamento = flagAggiornamento;
		}


		public Date getDataAggiornamentoMonit() {
			return dataAggiornamentoMonit;
		}


		public void setDataAggiornamentoMonit(Date dataAggiornamentoMonit) {
			this.dataAggiornamentoMonit = dataAggiornamentoMonit;
		}
	}