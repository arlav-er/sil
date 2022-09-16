package it.eng.sil.security;

import java.math.BigDecimal;

/**
 * @author Paolo Roccetti Classe che contiene i dati di un riferimento ipertestuale (nome del riferimento, indirizzo del
 *         riferimento, target e attributi caratteristici). Restituisce gli attributi: descrizione, href, target e
 *         attributi caratteristici per la creazione del link nella pagina HTML. Puo inoltre indicare se il link punta
 *         ad una pagina interna o esterna del sistema.
 */
public class HyperLink {
	private String descrizione;
	private String href;
	private String target;
	private boolean external;
	private BigDecimal targetattrib;

	/**
	 * Non ammesso
	 */
	private HyperLink() {
	}

	/**
	 * Costruttore che accetta in ingresso: nome del riferimento, nome simbolico della pagina di riferimento, indirizzo
	 * esterno del riferimento. Nel caso in cui il nome simbolico sia nullo il riferimento viene considerato esterno.
	 * Altrimenti il riferimento viene considerato interno. In caso di riferimento interno la visualizzazione avviene
	 * nel frame "main", diversamente la visualizzazione avviene in una nuova finestra. Nel secondo caso gli attributi
	 * caratteristici del riferimento sono settati come stringa vuota.
	 * 
	 * @param descrizione
	 *            Nome da assegnare al riferimento
	 * @param targetpage
	 *            Indica il nome simbolico della pagina associata al riferimento.
	 * @param targetattrib
	 *            Attributi del riferimento (se interno)
	 * @param uri
	 *            Destinazione del riferimento (se esterno)
	 */
	public HyperLink(String descrizione, String targetpage, BigDecimal targetattrib, String uri) {
		this.descrizione = descrizione;
		// Se targetpage è vuoto allora il riferiemnto è esterno ed è indicato
		// dall'uri
		// Altrimenti href è rappresentato dal targetpage e il riferimento è
		// interno.
		if ((targetpage == null) || (targetpage == "")) {
			this.href = uri;
			this.target = "_blank";
			this.external = true;
			this.targetattrib = null;
		} else {
			this.href = targetpage;
			this.targetattrib = targetattrib;
			this.target = "main";
			this.external = false;

		}

	}

	/**
	 * Costruttore che accetta in ingresso: nome del riferimento, indirizzo del riferimento, finestra di target e
	 * parametri carattristici. Permette di specificare se il link è interno o esterno al sistema. E' un costruttore che
	 * consente di creare riferimenti con caratteristiche diverse dal default
	 * 
	 * @param descrizione
	 *            Nome da assegnare al riferimento
	 * @param href
	 *            Indirizzo del riferimento
	 * @param target
	 *            Componente di destinazione del riferimento
	 * @param targetattrib
	 *            Attributi caratteristici del riferimento
	 * @param external
	 *            Indica se il riferimento è interno o esterno
	 */
	/*
	 * public HyperLink(String descrizione, String href, String target, BigDecimal targetattrib, boolean external) {
	 * this.descrizione = descrizione; this.href = href; this.target = target; this.targetattrib = targetattrib;
	 * this.external = external; }
	 */
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String newDescrizione) {
		descrizione = newDescrizione;
	}

	public String getHref() {
		return href;
	}

	/**
	 * Inserisce un nuovo href, non modifica il valore del flag che indica se il riferimento è interno o esterno al
	 * sistema.
	 */
	public void setHref(String newHref) {
		href = newHref;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String newTarget) {
		target = newTarget;
	}

	/**
	 * Setta l'attributo che indica che il link è interno al sistema
	 */
	public void setInternal() {
		this.external = false;
	}

	/**
	 * Setta l'attributo che indica che il link è esterno al sistema
	 */
	public void setExternal() {
		this.external = true;
	}

	/**
	 * Indica se il riferimento è interno o esterno al sistema
	 * 
	 * @return Ritorna true se il riferimento è esterno al sistema, false in caso contrario.
	 */
	public boolean isExternal() {
		return external;
	}

	public BigDecimal getTargetattrib() {
		return targetattrib;
	}

	public void setTargetattrib(BigDecimal newTargetattrib) {
		targetattrib = newTargetattrib;
	}
}