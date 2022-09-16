package it.eng.myportal.dtos;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Size;

public class SoggettoDTO implements IDTO {
		private static final long serialVersionUID = 3064898772631255022L;
		@Size(max=80)
		private String ordineIscrizione;
		@Valid
		private DeComuneDTO luogoIscrizione;
		
		@Size(max=30)
		private String numeroIscrizione;
		
		private Date dataIscrizione;

		public SoggettoDTO() {
			super();
			luogoIscrizione = new DeComuneDTO();
		}

		public String getOrdineIscrizione() {
			return ordineIscrizione;
		}

		public void setOrdineIscrizione(String ordineIscrizione) {
			this.ordineIscrizione = ordineIscrizione;
		}

		public DeComuneDTO getLuogoIscrizione() {
			return luogoIscrizione;
		}

		public void setLuogoIscrizione(DeComuneDTO luogoIscrizione) {
			this.luogoIscrizione = luogoIscrizione;
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

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format(
					"SoggettoDTO [ordineIscrizione=%s, luogoIscrizione=%s, numeroIscrizione=%s, dataIscrizione=%s]",
					ordineIscrizione, luogoIscrizione, numeroIscrizione, dataIscrizione);
		}
	}