package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.math.BigDecimal;

public abstract class Decodifica {

	public static abstract class Stato {
		public static final String DA_ELABORARE = "DAE";
		public static final String AUTORIZZATO = "AUT";
		public static final String DA_VERIFICARE = "DAV";
		public static final String NON_AUTORIZZATO = "NON";
		public static final String DECADUTO = "DEC";
		public static final String ANNULLATO = "ANN";
	}

	public static abstract class StatoVerifica {
		public static final String RESIDENZA_PORTING = "00";
		public static final String RESIDENZA_TRASFERIMENTO = "01";
		public static final String PERCETTORE_MOB_DEROGA = "02";
		public static final String SUPERAMENTO_DURATA = "03";
		public static final String RESIDENZA_DATI_ASSENTI = "04";
		public static final String LAV_ISCRITTO_MOB_DEROGA = "05";
		public static final String LAV_NON_297 = "06";
		public static final String LAV_NON_RESIDENTE = "07";
		public static final String LAV_NON_COMPETENZA = "08";
		public static final String LAV_PERDITA_DISOCCUPAZIONE = "09";
		public static final String LAV_PERCEPISCE_MOB_DEROGA = "10";
	}

	public static abstract class Provenienza {
		public static final String DA_FILE = "F";
		public static final String AGG_MANUALE = "M";
	}

	public static abstract class StatoAtto {
		public static final String PROTOCOLLATO = "PR";
	}

	public static abstract class StatoDomanda {
		public static final String DA_ESAMINARE = "DES";
		public static final String VALIDATA = "VAL";
		public static final String NON_VALIDATA = "NVA";
	}

	public static abstract class EsitoElaborazioneDomanda {
		public static final String DOMANDA_VALIDATA = "Si";
		public static final String DOMANDA_NON_VALIDATA = "No";
	}

	public static abstract class Costanti {
		public static final BigDecimal CHECK_ETA_MOB_DEROGA = new BigDecimal("54");
		public static final BigDecimal CHECK_DURATA_MAX_PRESTAZIONE = new BigDecimal("90");
		public static final BigDecimal CDNUT_PORTING = new BigDecimal("200");
		public static final String SI = "S";
		public static final String NO = "N";
		public static final String DISOCCUPATO = "A21";
		public static final String ATTIVITA_SENZA_CONTRATTO = "A213";
	}

}
