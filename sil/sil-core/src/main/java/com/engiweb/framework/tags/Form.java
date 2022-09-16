package com.engiweb.framework.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Questa classe costruisce un &lt;form&gt; con determinati attributi. (si presti attenzione al parametro "onSubmit")
 * 
 * (versione 1.2)
 * 
 * Esempio generico: &lt;customTL:form name="jellyForm" method="get" action="/jsp/memory.jsp" target="" encType =
 * "text/plain" className="stileLibero" onSubmit="" onReset="" /&gt;
 * 
 * Significato degli attributi: name ........ il nome della form (nota che *non* è obbligatorio!) method ...... metodo
 * di invio dati: "GET" o "POST" action ...... pagina di destinazione (URL) a cui inviare i dati (per la submit dei
 * dati) target ...... finestra/frame di destinazione. può valere "_blank", "_parent", "_self", "_top" oppure un nome di
 * un "frame" esistente. encType ..... è un attributo della form che può valere: "multipart/form-data" oppure
 * "application/x-www-form-urlencoded" oppure "text/plain". className ... attributo "class" per lo stile onSubmit ....
 * codice JavaScript da eseguire prima di fare la "submit" dei dati contenuti nei campi della form. ATTENZIONE: poiché
 * la "customTL" ha bisogno di inserire qui la chiamata alla sua funzione interna "controllaFunzTL" (la quale controlla
 * l'obbligatorità e la validità dei campi stabiliti dai parametri delle "TextBox") verrà generato un codice del tipo:
 * onSubmit="return ( controllaFunzTL() && funzUtente() ); dove si è impostato onSubmit="funzUtente()" nella "customTL"!
 * La funzione utente DEVE RENDERE UN BOOLEANO che indica se fare o meno la "submit" (false=abort; true=submit). La
 * nuova versione crea un codice del tipo: onSubmit="return ( controllaFunzTL() && riportaControlloUtente(funzUtente())
 * ); La "riportaControlloUtente()" serve per fare la "undoSubmit()" in caso che i controlli utente rendano FALSE.
 * dontValidate..può valere false (valore di default) o true. Nel caso si ponga a "true", nel campo onSubmit generato
 * per il tag di FORM viene usata la funzione JS "controllaNullaTL()" anziché la solita "controllaFunzTL()". E' utile se
 * si vogliono creare form che NON devono essere validate! id ...........ID dell'elemento HTML onReset ..... codice
 * JavaScript da eseguire prima di fare la "reset" dei campi della form
 * 
 * ATTENZIONE: la "submit" fatta via JavaScript *non* è come la pressione del bottone di "submit" poiché via JS *non*
 * viene eseguito il codice inserito nella "onSubmit"! In caso si dovesse fare la "submit" via JS occorre stabilire se
 * sia o meno il caso di eseguire il controllo dei dati definiti nei tag della tag library; in caso affermativo basta
 * invocare la funzione "controllaFunzTL()" la quale rende "true" se tutti i controlli sono passati, "false" se c'è
 * stato almeno un errore. Esempio di JavaScript: function Salva() { var datiOk = controllaFunzTL(); datiOk = datiOk &&
 * riportaControlloUtente( altriControlliPersonali() ); if (datiOk) document.miaForm.submit(); } oppure, se si vuole
 * annullare la submit (e riabilitare i componenti della pagina) in modo manuale: function Salva() { var datiOk =
 * controllaFunzTL(); datiOk = datiOk && altriControlliPersonali(); if (datiOk) document.miaForm.submit(); else
 * undoSubmit(); } NOTA BENE: la nuova versione di questo tag fa disabilita in automatico tutti i bottoni della form.
 * Questo viene fatto via JavaScript all'interno della funzione "controllaFunzTL()" nel caso che venga reso TRUE. Se
 * successivamente si trova che *non* bisogna fare il 'submit', occorre ripristinare tutti i bottoni: questo va fatto
 * invocando la 'undoSubmit()' oppure invocando 'riportaControlloUtente(false)'. Se si aggiunge una funzione
 * sull'onSubmit della FORM non occorre chiamare tale funzione: basta rendere FALSE e il Javascript messo da questo tag
 * di form chiamerà inautomatico la 'undoSubmit()'. Si noti che se la "controllaFunzTL()" rende FALSE, ha gia' fatto da
 * sé la 'undoSubmit()', ripristinando tutti gli oggetti della pagina; il problema è quando lei rende TRUE e un'altra
 * condizione in AND rende FALSE. I modi corretti di usare la funzione sono: a) if (controllaFunzTL()) { submit() } else
 * ... b) if (controllaFunzTL() && riportaControlloUtente( ... ) ) { submit() } else ... c) var datiOk =
 * controllaFunzTL(); datiOk = datiOk && ...; if (riportaControlloUtente( datiOk )) { submit() } else ... d) var datiOk
 * = controllaFunzTL(); datiOk = datiOk && ...; if (datiOk) { submit() } else { undoSubmit(); ... }
 */
public class Form extends TagSupport {
	private String name = ""; // Elenco attributi con il lovo valore di
								// DEFAULT!!!
	private String method = "";
	private String action = "";
	private String target = "";
	private String className = "";
	private String onSubmit = "";
	private String dontValidate = "";
	private String onReset = "";
	private String encType = "";
	private String id = "";

	public int doStartTag() throws JspException {
		try {
			// HttpServletRequest req = ( HttpServletRequest )
			// pageContext.getRequest();
			JspWriter out = pageContext.getOut();

			out.print("<FORM ");

			if (!name.equals("")) {
				out.print("name=\"" + name + "\" ");
			}

			if (!method.equals("")) {
				out.print("method=\"" + method + "\" ");
			}

			if (!action.equals("")) {
				out.print("action=\"" + action + "\" ");
			}

			if (!target.equals("")) {
				out.print("target=\"" + target + "\" ");
			}

			if (!className.equals("")) {
				out.print("class=\"" + className + "\" ");
			}

			// Gestione particolare del "onSubmit" (+ dontValidate)---------
			// A seconda del "dontValidate" scelgo la funzione JS che si userà.
			String controllaXxxTL = "controllaFunzTL()";
			if ((dontValidate != null) && dontValidate.equalsIgnoreCase("TRUE")) {
				controllaXxxTL = "controllaNullaTL()";
			}
			if (onSubmit.equals("")) {
				out.print("onSubmit=\"return " + controllaXxxTL + ";\" ");
			} else {
				out.print(
						"onSubmit=\"return ( " + controllaXxxTL + " && riportaControlloUtente(" + onSubmit + ") )\" ");
			}

			if (!onReset.equals("")) {
				out.print("onReset=\"" + onReset + "\" ");
			}

			if (!id.equals("")) {
				out.print("id=\"" + id + "\" ");
			}

			if (!encType.equals("")) {
				out.print("encType=\"" + encType + "\" ");
			}

			out.println(">");

		} catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}

		return EVAL_BODY_INCLUDE; // Valuto tutto ciò che c'è dentro il tag!
	} // doStartTag

	public int doEndTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			out.print("</FORM>");

		} catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}

		return super.doEndTag();
	} // doEndTag

	public String getAction() {
		return action;
	}

	public void setAction(String newAction) {
		action = newAction;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String newClassName) {
		className = newClassName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String newMethod) {
		method = newMethod;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public String getOnReset() {
		return onReset;
	}

	public void setOnReset(String newOnReset) {
		onReset = newOnReset;
	}

	public String getOnSubmit() {
		return onSubmit;
	}

	public void setOnSubmit(String newOnSubmit) {
		onSubmit = newOnSubmit;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String newTarget) {
		target = newTarget;
	}

	public String getEncType() {
		return encType;
	}

	public void setEncType(String newEncType) {
		encType = newEncType;
	}

	public String getDontValidate() {
		return dontValidate;
	}

	public void setDontValidate(String string) {
		dontValidate = string;
	}

	public String getId() {
		return id;
	}

	public void setId(String string) {
		id = string;
	}

} // Form
