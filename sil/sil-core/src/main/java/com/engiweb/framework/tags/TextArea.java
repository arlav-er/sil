package com.engiweb.framework.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Questa classe costruisce un &lt;textarea&gt; con determinati attributi.
 * 
 * (versione 1.0)
 * 
 * Esempio generico: &lt;customTL:textArea name="jellicle" value="45" cols="10" rows="10" maxlength="10"
 * title="Jellicle" alt="Join to the jellicle ball!" readonly="false" disabled="false" required="true"
 * validateOnPost="true" validateWithFunction="myFun" trim="false" focusOn="true" className="jellyStile"
 * classNameBase="stile" onKeyUp="javascript:fieldChanged();" onBlur="" tabIndex="5" accessKey="F"
 * inline="onKeyUp=\"javascript:fieldChanged(true);\"" /&gt;
 * 
 * Significato degli attributi: name ........ il nome del tag di input value ....... il valore del tag cols ........
 * numero di colonne visualizzate rows ........ numero di righe visualizzate maxlength ... dimensione massima del campo
 * (num. car. inseribili) title ....... titolo (viene usato negli "alert" JavaScript per avvisare l'utente) alt
 * ......... testo per l'"aternate-text" readonly .... posto a "true" rende il campo non editabile dall'utente; non fa
 * altro che impostare l'attributo "readonly" del tag di input disabled .... permette di disabilitare il tag. Oltre a
 * impostare l'attributo di "disabled" del tag, viene cambiato il valore di "name" e creato un nuovo campo di input con
 * il nome di "name" ma di tipo "HIDDEN"! Il tag nascosto serve perché sulla "submit" i campi "DISABLED" NON vengono
 * passati alla pagina di "action" della form. required .... "true" se il campo è obbligatorio (il controllo viene fatto
 * via JavaScript sul "submit" della form) validateOnPost .... se posta a "true, quando si esegue il "submit" della form
 * verrà controllato che il campo di testo contenga un dato valido. validateWithFunction .... è una stringa che, se
 * diversa da stringa vuota, indica il nome di una funzione definita dal codice JavaScript contenuto nella pagina; tale
 * funzione verrà invocata automaticamente quando si esegue il "submit" della form. La funzione JS deve accettare come
 * parametro di input una stringa contenente il nome del tag di input su cui eseguire dei controlli. Inoltre deve
 * rendere un valore booleano corrispondente all'esito del controllo (true=tutto ok). (si veda il file "/js/customTL.js"
 * per le funzioni definite). trim ........ è per default a "true": al momento della "submit" viene eseguita la funzione
 * di "trim" sul campo (ossia vengono tolti tutti i caratteri di spazio a inizio e fine stringa); focusOn ..... indica
 * se si vuole il focus su questo tag per default (si noti che solo uno dei tag della form dovrebbe avere questo
 * attributo a "true"); Non viene considerato se disabled="true" o se readonly="true"! className ... serve per definite
 * lo stile (definito nel solito file CSS) usato dal campo. (esso corrisponde all'attributo "CLASS" del normale tag di
 * input). classNameBase è simile a "className" ma corrisponde alla definizione di uno "stile di base"; in definitiva
 * esso contiene la prima parte del nome dello stile che verrà completato durante l'elaborazione del tag. Per il momento
 * è gestito il solo caso di "EDIT" e "READ-ONLY". Per es, se si definisce "classNameBase=mioStile" il nostro custom-tag
 * definirà l'attributo "class" uguale a "mioStileEdit" oppure "mioStileView" a seconda che l'attributo "READONLY" valga
 * rispettivamente "false" e "true" (nota: nel CSS incluso nella pagina devono essere stati definiti entrambi gli stili
 * "mioStileEdit" e "mioStileView"). onKeyUp ..... codice JavaScript da invocare dopo ogni rilascio di un tasto onBlur
 * ...... codice JavaScript da invocare sulla perdita del fuoco onFocus ..... codice JavaScript da invocare sulla presa
 * del fuoco tabIndex .... per definire l'ordine di navigazione tramite il tasto TAB (il numero di ordine è un valore
 * numerico da 1 in su; la navigazione procede dal valore più basso a quello più alto); accessKey ... tasto rapido per
 * accedere al campo (sulla pressione di ALT+accesskey) inline ...... contiene una stringa che verrà inserita così com'è
 * alla fine di tutti gli altri attributi. Essa può servire a definire qualsiasi proprietà del normale campo di INPUT
 * che non sono state gestite da questo Custom-Tag.
 * 
 */
public class TextArea extends TagSupport {
	private String accessKey = "";
	private String alt = "";
	private String className = "";
	private String classNameBase = "";
	private String cols = "60";
	private String disabled = "false";
	private String focusOn = "false";
	private String inline = "";
	private String maxlength = "";
	private String name = ""; // Elenco attributi con il lovo valore di
								// DEFAULT!!!
	private String onBlur = "";
	private String onFocus = "";
	private String onKeyUp = "";
	private String readonly = "false";
	private String required = "false";
	private String rows = "4";
	private String tabIndex = "";
	private String title = "";
	private String trim = "true";
	private String validateOnPost = "false";
	private String validateWithFunction = "";
	private String value = "";

	public void setAccessKey(String newAccessKey) {
		accessKey = newAccessKey;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAlt(String newAlt) {
		alt = newAlt;
	}

	public String getAlt() {
		return alt;
	}

	public void setClassName(String newClassName) {
		className = newClassName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassNameBase(String newClassNameBase) {
		classNameBase = newClassNameBase;
	}

	public String getClassNameBase() {
		return classNameBase;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public void setCols(int cols) {
		// BY GG: anche per un parametro INTERO
		this.cols = String.valueOf(cols);
	}

	public String getCols() {
		return this.cols;
	}

	public void setDisabled(String newDisabled) {
		disabled = newDisabled;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setFocusOn(String newFocusOn) {
		focusOn = newFocusOn;
	}

	public String getFocusOn() {
		return focusOn;
	}

	public void setInline(String newInline) {
		inline = newInline;
	}

	public String getInline() {
		return inline;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public void setMaxlength(int maxlength) {
		// BY GG: anche per un parametro INTERO
		this.maxlength = String.valueOf(maxlength);
	}

	public String getMaxlength() {
		return this.maxlength;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setOnBlur(String onBlur) {
		this.onBlur = onBlur;
	}

	public String getOnBlur() {
		return this.onBlur;
	}

	public void setOnFocus(String newOnFocus) {
		onFocus = newOnFocus;
	}

	public String getOnFocus() {
		return onFocus;
	}

	public void setOnKeyUp(String newOnKeyUp) {
		onKeyUp = newOnKeyUp;
	}

	public String getOnKeyUp() {
		return onKeyUp;
	}

	public void setReadonly(String newReadonly) {
		readonly = newReadonly;
	}

	public String getReadonly() {
		return readonly;
	}

	public void setRequired(String newRequired) {
		required = newRequired;
	}

	public String getRequired() {
		return required;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public void setRows(int rows) {
		// BY GG: anche per un parametro INTERO
		this.rows = String.valueOf(rows);
	}

	public String getRows() {
		return this.rows;
	}

	public void setTabIndex(String newTabIndex) {
		tabIndex = newTabIndex;
	}

	public String getTabIndex() {
		return tabIndex;
	}

	public void setTitle(String newTitle) {
		title = newTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTrim(String newTrim) {
		trim = newTrim;
	}

	public String getTrim() {
		return trim;
	}

	public void setValidateOnPost(String newValidateOnPost) {
		validateOnPost = newValidateOnPost;
	}

	public String getValidateOnPost() {
		return validateOnPost;
	}

	public void setValidateWithFunction(String newValidateWithFunction) {
		validateWithFunction = newValidateWithFunction;
	}

	public String getValidateWithFunction() {
		return validateWithFunction;
	}

	public void setValue(String newValue) {
		if (newValue == null) {
			this.value = "";
		} else {
			this.value = newValue;
		}
	}

	public String getValue() {
		return value;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			// GENERAZIONE DELLA PARTE RELATIVA AL TAG HTML DI "INPUT"
			// -----------------------
			boolean isReadOnly = readonly.equalsIgnoreCase("true");
			boolean isDisabled = disabled.equalsIgnoreCase("true");
			boolean isRequired = required.equalsIgnoreCase("true");
			String checkMaxLen = null;

			value = Util.replace(value, "\"", "&quot;");
			value = Util.replace(value, "'\r\n", "\r\n");

			// if(!isReadOnly)
			{
				out.print("<TEXTAREA ");

				// attributo obbligatorio:
				out.print("name=\"" + name);

				if (isDisabled) {
					out.print("_TMP"); // Se il tag è DISABILITATO, gli cambio
										// NOME (e dopo ne creo uno NASCOSTO con
										// lo stesso nome)
				}

				out.print("\" ");

				// altri attributi:
				out.print("rows=\"" + rows + "\" ");
				out.print("cols=\"" + cols + "\" ");

				if (!maxlength.equals("")) {
					// TODO: Integrare con lo script di Stefy (chiedere a
					// Franco)
					// out.print("maxlength=\"" + maxlength + "\" ");
					checkMaxLen = "CheckLen(" + maxlength + ",'" + name + "' )";
				}

				if (isReadOnly) {
					out.print("READONLY ");
				}

				if (isDisabled) {
					out.print("DISABLED ");

					// Inoltre viene inserito il tag nascosto dopo la chiusura
					// di questo tag.
				}

				if (!title.equals("")) {
					out.print("title=\"" + title + "\" ");
				}

				if (!alt.equals("")) {
					out.print("alt=\"" + alt + "\" ");
				}

				if (!className.equals("")) {
					out.print("class=\"" + className + "\" ");
				}

				if (!classNameBase.equals("")) {
					out.print("class=\"" + classNameBase);

					if (isReadOnly || isDisabled) {
						out.print("View");
					} else {
						out.print("Edit");
					}

					out.print("\" ");
				}

				if (!isDisabled && !isReadOnly & (checkMaxLen != null)) {
					onKeyUp = checkMaxLen + ";" + onKeyUp;
				}

				if (!onKeyUp.equals("")) {
					out.print("onKeyUp=\"" + onKeyUp + "\" ");
				}

				if (!onBlur.equals("")) {
					out.print("onBlur=\"" + onBlur + "\" ");
				}

				if (!onFocus.equals("")) {
					out.print("onFocus=\"" + onFocus + "\" ");
				}

				if (!tabIndex.equals("")) {
					out.print("tabIndex=\"" + tabIndex + "\" ");
				}

				if (!accessKey.equals("")) {
					out.print("accessKey=\"" + accessKey + "\" ");
				}

				if (!inline.equals("")) {
					out.print(inline + " ");
				}

				out.print(">" + value + "</TEXTAREA>");

				// out.println("/>"); // Fine tag
				// FV 24/09/2003
				if (isRequired) {
					out.println("&nbsp;*&nbsp;");
				}

			} // if(!isDisabled)

			/*
			 * else { out.println("<div name=\""+ name +"\" class=" + classNameBase + "View ><pre>");
			 * //out.println("<div style=\"background-color:#dcdcdc; height:100; width:400; overflow:auto;\" ><pre>");
			 * out.print( Util.quote(value) ); out.println("</pre></div>"); }
			 */

			// Gestione della "disabilitazione" del tag: creazione del tag
			// NASCOSTO.
			/*
			 * TODO: Come utilizzare ? if (isDisabled) {
			 * 
			 * out.print("<INPUT type=\"HIDDEN\" name=\"" + name + "\" value=\"" + Util.quote(value) + "\" />"); } //if
			 * isDisabled
			 */

			// GENERAZIONE DELLA PARTE RELATIVA AI CONTROLLI JAVASCRIPT
			// ----------------
			// ATTENZIONE: CONTA L'ORDINE CON CUI VENGONO ESEGUITI GLI
			// INSERIMENTI NELL'ARRAY
			// DELLE FUNZIONI (per es. prima conviene fare il TRIM e poi il
			// REQUIRED)!
			boolean apertoTagJS = false; // usata per aprire il tag "SCRIPT"
											// una volta sola

			if (trim.equalsIgnoreCase("true")) {
				if (!apertoTagJS) {
					out.print("<SCRIPT language=\"JavaScript\"> ");
					apertoTagJS = true;
				}

				out.print("_arrFunz[_arrIndex++]=\"trimObject('" + name + "')\"; ");
			}
			// if trim

			if (isRequired) {
				if (!apertoTagJS) {
					out.print("<SCRIPT language=\"JavaScript\"> ");
					apertoTagJS = true;
				}

				out.print("_arrFunz[_arrIndex++]=\"isRequired('" + name + "')\"; ");
			}
			// if required

			if (validateOnPost.equalsIgnoreCase("true")) {
				/*
				 * TODO: Decidere quali tipi di validazione permettere if (! apertoTagJS) {
				 * out.print("<SCRIPT language=\"JavaScript\"> "); apertoTagJS=true; } // Esegue la conversione: dato il
				 * nome del "type" rende la funzione JS da invocare boolean trovato = false; int idx=0; while ((idx <
				 * arrTypeValue.length) && !trovato) { if (type.equalsIgnoreCase(arrTypeValue[idx])) trovato = true;
				 * else idx++; } //while
				 * 
				 * if (trovato) { out.print("_arrFunz[_arrIndex++]=\"" + arrTypeFunction[idx] + "('" + name + "')\"; ");
				 * } else { out.println(" // ERRORE: campo non validabile"); }
				 */
			}
			// if validateOnPost

			if (!validateWithFunction.equals("")) {
				if (!apertoTagJS) {
					out.print("<SCRIPT language=\"JavaScript\"> ");
					apertoTagJS = true;
				}

				// ho già la funzione da invocare nel VALUE dell'attributo
				// NOTA: la funz.JS accetta come parametro di INPUT una STRINGA
				// contenente
				// il nome dell'input-tag su cui eseguire la validazione. La
				// funz. deve
				// rendere un valore booleano corrispondente all'esito della
				// validazione
				out.print("_arrFunz[_arrIndex++]=\"" + validateWithFunction + "('" + name + "')\"; ");
			}
			// if validateWithFunction

			if (focusOn.equalsIgnoreCase("true") && !isReadOnly && !isDisabled) {
				if (!apertoTagJS) {
					out.print("<SCRIPT language=\"JavaScript\"> ");
					apertoTagJS = true;
				}

				out.print("document.forms[0]." + name + ".focus(); ");
			}
			// if focusOn

			if (apertoTagJS) {
				out.print("</SCRIPT>");
				apertoTagJS = false;
			}
		} catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}

		return SKIP_BODY;
	}
	// doStartTag
}
