package com.engiweb.framework.tags;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SourceBean;

/**
 * Questa classe costruisce un &lt;select&gt; con determinati attributi.
 * 
 * (versione 1.3)
 * 
 * Esempio generico: &lt;customTL:comboBox name="jellicle" size="1" title="Jellicle" multiple="false" disabled="false"
 * required="true" validateWithFunction="myFun" focusOn="true" className="jellyStile" classNameBase="stile"
 * onChange="javascript:fieldChanged();" tabIndex="5" inline="onChange=\"javascript:fieldChanged();\""
 * moduleName="ComboStato" selectedValue="123" addBlank="true" blankValue="" truncAt="" /&gt;
 * 
 * Significato degli attributi: name ........ il nome del tag di select size ........ dimensione del campo (numero di
 * linee visualizzate) title ....... titolo (viene usato negli "alert" JavaScript per avvisare l'utente) multiple ....
 * se a "true" permette di fare la selezione multipla (altrimenti: singola) tabIndex .... per definire l'ordine di
 * navigazione tramite il tasto TAB (il numero di ordine è un valore numerico da 1 in su; la navigazione procede dal
 * valore più basso a quello più alto); disabled .... permette di disabilitare il tag. Oltre a impostare l'attributo di
 * "disabled" del tag, viene cambiato il valore di "name" e creato un nuovo campo di input con il nome di "name" ma di
 * tipo "HIDDEN"! Il tag nascosto serve perché sulla "submit" i campi "DISABLED" NON vengono passati alla pagina di
 * "action" della form. NOTA: non è stata implementato l'attributo di "readonly" poiché esso non modifica il
 * comportamento della combo: l'utente può sempre cambiare l'elemento selezionato! Usare disabled="true" per impedire
 * all'utente di modificare la combo! required .... "true" se il campo è obbligatorio (il controllo viene fatto via
 * JavaScript sul "submit" della form) ossia il "value" dell'elemento selezionato *non* deve valere stringa vuota.
 * validateWithFunction .... è una stringa che, se diversa da stringa vuota, indica il nome di una funzione definita dal
 * codice JavaScript contenuto nella pagina; tale funzione verrà invocata automaticamente quando si esegue il "submit"
 * della form. La funzione JS deve accettare come parametro di input una stringa contenente il nome del tag di select su
 * cui eseguire dei controlli. Inoltre deve rendere un valore booleano corrispondente all'esito del controllo
 * (true=tutto ok). (si veda il file "/js/customTL.js" per le funzioni definite). focusOn ..... indica se si vuole il
 * focus su questo tag per default (si noti che solo uno dei tag della form dovrebbe avere questo attributo a "true");
 * Non viene considerato se disabled="true" o se readonly="true"! className ... serve per definite lo stile (definito
 * nel solito file CSS) usato dal campo. (esso corrisponde all'attributo "CLASS" del normale tag di input).
 * classNameBase è simile a "className" ma corrisponde alla definizione di uno "stile di base"; in definitiva esso
 * contiene la prima parte del nome dello stile che verrà completato durante l'elaborazione del tag. Per il momento è
 * gestito il solo caso di "EDIT" e "READ-ONLY". Per es, se si definisce "classNameBase=mioStile" il nostro custom-tag
 * definirà l'attributo "class" uguale a "mioStileEdit" oppure "mioStileView" a seconda che l'attributo "DISABLED" valga
 * rispettivamente "false" e "true" (nota: nel CSS incluso nella pagina devono essere stati definiti entrambi gli stili
 * "mioStileEdit" e "mioStileView"). onChange .... codice JavaScript da invocare a ogni cambio di elemento selezionato
 * nella combo //onBlur ...... codice JavaScript da invocare sulla perdita del fuoco //onFocus ..... codice JavaScript
 * da invocare sulla presa del fuoco inline ...... contiene una stringa che verrà inserita così com'è alla fine di tutti
 * gli altri attributi. Essa può servire a definire qualsiasi proprietà del normale campo di SELECT che non sono state
 * gestite da questo Custom-Tag. moduleName ..... nome del modulo (opzionale) da cui prevelare i dati per riempire in
 * modo automatico tutti i campi di "OPTION"; selectedValue .. stringa contenente il valore dell'OPTION selezionata (su
 * cui mettere la voce "SELECTED") addBlank ....... se posto a "true" viene inserita la "OPTION bianca" come primo
 * elemento blankValue ..... valore relativo alla "OPTION bianca" (viene messo nel campo value) truncAt ........ se
 * diverso da stringa vuota, tronca la lunghezza delle sringhe di descrizione nelle <option> al numero di caratteri
 * specificato, aggiungendo alla fine tre puntini "..." selectedAll "true" allora tutte le option devono essere
 * selezionate
 * 
 */
public class ComboBox extends BodyTagSupport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ComboBox.class.getName());
	private String name = "";
	// Elenco attributi con il loro valore di DEFAULT!!!
	private String size = "";
	private String title = "";
	private String multiple = "false";
	private String tabIndex = "";
	private String disabled = "false";
	private String className = "";
	private String classNameBase = "";
	private String required = "false";
	private String validateWithFunction = "";
	private String focusOn = "false";
	private String onChange = "";
	private String onBlur = "";
	private String onFocus = "";
	private String inline = "";
	private String moduleName = "";
	private String selectedValue = "";
	private String selectedValueBody = "";
	private String descrizione = "";
	private String addBlank = "false";
	private String blankValue = "";
	private String truncAt = "";
	private String corpo = "";
	private boolean findSelected = false;
	private String selectedAll = "false";

	public void release() {
		size = "";
		title = "";
		multiple = "false";
		tabIndex = "";
		disabled = "false";
		className = "";
		classNameBase = "";
		required = "false";
		validateWithFunction = "";
		focusOn = "false";
		onChange = "";
		onBlur = "";
		onFocus = "";
		inline = "";
		moduleName = "";
		selectedValue = "";
		selectedValueBody = "";
		descrizione = "";
		addBlank = "false";
		blankValue = "";
		truncAt = "";
		corpo = "";
		findSelected = false;
		selectedAll = "false";
	}

	public int doAfterBody() throws JspException {
		BodyContent bodyCont = getBodyContent();

		corpo = bodyCont.getString();
		return EVAL_BODY_INCLUDE;

	} // doAfterBody()

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		boolean allSelectedOption = false;
		boolean isDisabled = disabled.equalsIgnoreCase("true");

		try {
			String[] selectedValues = null;
			if (multiple.equalsIgnoreCase("true") && selectedValue.contains(",")) {
				selectedValues = selectedValue.split(",");
			}

			if (!isDisabled) { // ====================== Apertura del tag
								// ==========================
				out.print("<SELECT ");

				out.print(" name=\"" + name + "\" ");

				if (multiple.equalsIgnoreCase("true")) {
					out.print(" MULTIPLE=\"true\" ");
				}

				if (!size.equals("")) {
					out.print(" size=\"" + size + "\" ");
				}

				if (!title.equals("")) {
					out.print(" title=\"" + title + "\" ");
				}

				if (!tabIndex.equals("")) {
					out.print(" tabIndex=\"" + tabIndex + "\" ");
				}

				if (!className.equals("")) {
					out.print(" class=\"" + className + "\" ");
				}

				if (!classNameBase.equals("")) {
					// seleziono la calsse che permette la modifica
					out.print(" class=\"" + classNameBase + "Edit\" ");
				}

				if (!onChange.equals("")) {
					if (onChange.startsWith("\"")) {
						out.print(" onChange=" + onChange + " ");
					} else {
						out.print(" onChange=\"" + onChange + "\" ");
					}

				}

				if (!onBlur.equals("")) {
					out.print(" onBlur=\"" + onBlur + "\" ");
				}

				if (!onFocus.equals("")) {
					out.print(" onFocus=\"" + onFocus + "\" ");
				}

				if (!inline.equals("")) {
					out.print(inline + " ");
				}

				if (selectedAll.equalsIgnoreCase("true")) {
					out.print(" SELECTEDALL=\"true\" ");
					allSelectedOption = true;
				}

				out.println(">");

				// NB: non ho ancora chiuso il tag!
				// DEVO GENERARE TUTTE LE "OPTION"!!!
				if (addBlank.equalsIgnoreCase("true")) {
					// NB: se addBlank=true, uso blankValue per inserire
					// l'elemento bianco
					out.print("<option value=\"" + blankValue + "\"");
					if (blankValue.equalsIgnoreCase(selectedValue) || allSelectedOption) {
						out.print(" SELECTED=\"true\"");
					}
					out.println("></option>");
				}

				// Controllo che sia stato specificato il modulo che esegue la
				// query
				// dalla quale la select deve prelevare le informazioni per
				// costruire le <OPTION>
				if (!moduleName.equals("")) {
					HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
					ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
					SourceBean actionResponseBean = responseContainer.getServiceResponse();
					SourceBean listaSB = (SourceBean) actionResponseBean.getAttribute(moduleName + ".ROWS");

					if ((listaSB != null) && listaSB.containsAttribute("ROW")) {
						Vector vectSB = listaSB.getAttributeAsVector("ROW");
						Iterator iter = vectSB.iterator();

						// HashSet<String> gruppi = new HashSet<String>();
						LinkedHashSet<String> gruppi = new LinkedHashSet<String>();

						while (iter.hasNext()) {
							SourceBean attuale = (SourceBean) iter.next();
							if (attuale.containsAttribute("GRUPPO")) {
								String gruppo = (String) attuale.getAttribute("GRUPPO");
								gruppi.add(gruppo);
							}
						}
						// if (gruppi.size() > 1) {
						if (gruppi.size() > 0) {
							Iterator<String> iterGruppi = gruppi.iterator();
							while (iterGruppi.hasNext()) {
								iter = vectSB.iterator();
								String iterGruppo = (String) iterGruppi.next();
								iterGruppo = iterGruppo.toUpperCase();
								out.print("<optgroup label=\"" + iterGruppo + "\">");
								while (iter.hasNext()) {
									SourceBean attuale = (SourceBean) iter.next();
									String gruppo = (String) attuale.getAttribute("GRUPPO");
									gruppo = gruppo.toUpperCase();
									if (gruppo.equals(iterGruppo)) {
										String codice = (attuale.getAttribute("CODICE")).toString();
										String descr = (String) attuale.getAttribute("DESCRIZIONE");

										out.print("<option value=\"" + codice + "\"");

										if (codice.equalsIgnoreCase(selectedValue) || allSelectedOption) {
											out.print(" SELECTED=\"true\"");
										}

										if (truncAt.equalsIgnoreCase("")) {
											out.println(">" + descr + "</option>");
										} else {
											int maxLength = Integer.parseInt(truncAt, 10);
											if (descr.length() > (maxLength)) {
												out.println(">" + descr.substring(0, maxLength) + "...</option>");
											} else {
												out.println(">" + descr + "</option>");
											}
										}
									}
								}
								out.println("</optgroup>");
							}
						} else {
							iter = vectSB.iterator();
							while (iter.hasNext()) {
								SourceBean attuale = (SourceBean) iter.next();
								String codice = (attuale.getAttribute("CODICE")).toString();
								String descr = (String) attuale.getAttribute("DESCRIZIONE");

								out.print("<option value=\"" + codice + "\"");

								if (selectedValues != null) {
									for (String selValue : selectedValues) {
										if (codice.equalsIgnoreCase(selValue)) {
											out.print(" SELECTED=\"true\"");
										}
									}
								} else if (codice.equalsIgnoreCase(selectedValue) || allSelectedOption) {
									out.print(" SELECTED=\"true\"");
								}

								if (truncAt.equalsIgnoreCase("")) {
									out.println(">" + descr + "</option>");
								} else {
									int maxLength = Integer.parseInt(truncAt, 10);
									if (descr.length() > (maxLength)) {
										out.println(">" + descr.substring(0, maxLength) + "...</option>");
									} else {
										out.println(">" + descr + "</option>");
									}
								}
							}
						}
					} // if((listaSB != null) &&
						// listaSB.containsAttribute("ROW"))
				} // if moduleName

				// controllo se vi sono delle option scritte nel corpo della
				// <af:comboBox>
				// prelevo il corpo della comboBox
				// String corpo = bodyCont.getString();
				boolean hasBody = (corpo != null) && (!corpo.equals(""));
				if (hasBody) {
					// scrivo il corpo all'interno della <SELECT>
					out.write(corpo);
				}
				/*
				 * try { if (!findSelected && hasBody) { //costruisco un corpo ben formato da dare in pasto al parser
				 * String corpoPerParser = "<SELECT>" + corpo + "</SELECT>"; //ParseOPTIONbySAX cerca la <OPTION>
				 * selezionata e restituisce il valore e il testo della option ParseOPTIONbySAX parser =
				 * ParseOPTIONbySAX.parse(corpoPerParser); selectedValue = parser.getCodiceSel(); descrizione =
				 * parser.getDescrSel(); } } catch (Exception e) { //e.printStackTrace(); String className =
				 * this.getClass().getName(); it.eng.sil.util.TraceWrapper.fatal( _logger,className + "\n Errore
				 * nell'elaborazione del corpo della TAG comboBox"+ "\n (controllare che l'attributo SELECTED sia stato
				 * posto =true)::", e);
				 * 
				 * }
				 */
				// CHIUSURA DEL TAG DI "SELECT"
				// ====================================================
				out.println("</SELECT>");
				// =================================================================================

				// Gestione del marcatore di necessario (*)
				boolean isRequired = required.equalsIgnoreCase("true");
				if (isRequired) {
					out.println("&nbsp;*&nbsp;");
				}

			} // if(!isDisabled)

			/*
			 * ========================================================================================== Se la <select>
			 * è stata disabilitata creiamo due tag <input>: uno in readonly in cui mettiamo il testo e uno hidden in
			 * cui mettiamo il valore del codice selezionato nella select
			 * ==========================================================================================
			 */
			else { // genero un tag di INPUT in READONLY che mostra il valore
					// selezionato (in caso di disabilitazione
					// NON si è ritenuto opportuno mostrare un tag SELECT dal
					// momento che non si può selezionare il valore)

				if (!moduleName.equals("")) {
					HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
					ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);
					SourceBean actionResponseBean = responseContainer.getServiceResponse();
					SourceBean listaSB = (SourceBean) actionResponseBean.getAttribute(moduleName + ".ROWS");

					if ((listaSB != null) && listaSB.containsAttribute("ROW")) {
						Vector vectSB = listaSB.getAttributeAsVector("ROW");
						Iterator iter = vectSB.iterator();
						findSelected = false;

						while (iter.hasNext()) {
							SourceBean attuale = (SourceBean) iter.next();
							String codice = (attuale.getAttribute("CODICE")).toString();

							if (selectedValues != null) {
								for (String selValue : selectedValues) {
									if (codice.equalsIgnoreCase(selValue)) {
										findSelected = true;
										String styleView = "";
										if (!classNameBase.equals("")) {
											// seleziono la calsse di sola visualizzazione
											styleView = "style=\"font-weight: bold;\"";
										}
										this.descrizione += "<li " + styleView + ">"
												+ (String) attuale.getAttribute("DESCRIZIONE") + "</li>";
									}
								}
							} else if (codice.equalsIgnoreCase(selectedValue)) {
								findSelected = true;
								this.descrizione = (String) attuale.getAttribute("DESCRIZIONE");
							}

						} // while
					} // if ((listaSB != null)
				} // if (!moduleName.equals(""))

				if (selectedValues == null) {
					out.print("<INPUT type=\"text\" name=\"" + name + "_TMP\"");

					// controllo se vi sono delle option scritte nel corpo della
					// <af:comboBox>
					try {
						boolean hasBody = (corpo != null) && (!corpo.equals(""));
						// Se il corpo esiste e non ho ancora trovato un'opzione
						// selezionata, la cerco nel corpo
						if (!findSelected && hasBody) {
							corpo = "<select>" + corpo + "</select>";
							ParseOPTIONbySAX parser = ParseOPTIONbySAX.parse(corpo);
							selectedValue = parser.getCodiceSel();
							this.descrizione = parser.getDescrSel();
						}
					} // try
					catch (Exception e) {
						// e.printStackTrace(); Pablo: ho commentato per evitare che scrivesse l'errore in STDERR sulla
						// console errori applicativi
						String className = this.getClass().getName();
						it.eng.sil.util.TraceWrapper
								.fatal(_logger,
										className + "\n Errore nell'elaborazione del corpo della TAG comboBox"
												+ "\n (controllare che l'attributo SELECTED sia stato posto =true)::",
										e);

					} // catch

					if (!truncAt.equalsIgnoreCase("")) {
						int maxLength = Integer.parseInt(truncAt, 10);
						if (descrizione.length() > maxLength) {
							this.descrizione = this.descrizione.substring(0, maxLength) + "...";
						}
					}

					// scrivo la descrizione nella input
					out.print(" value=\"" + this.descrizione + "\" ");

					Double sz = new Double((descrizione.length()) * 1.3);
					out.print(" size=\"" + sz.intValue() + "\" ");

					out.print(" READONLY=\"true\" ");

					if (!title.equals("")) {
						out.print(" title=\"" + title + "\" ");
					}

					if (!tabIndex.equals("")) {
						out.print(" tabIndex=\"" + tabIndex + "\" ");
					}

					if (!className.equals("")) {
						out.print(" class=\"" + className + "\" ");
					}

					if (!classNameBase.equals("")) {
						// seleziono la calsse di sola visualizzazione
						out.print(" class=\"" + classNameBase + "View\" ");
					}

					if (!onBlur.equals("")) {
						out.print(" onBlur=\"" + onBlur + "\" ");
					}

					if (!onFocus.equals("")) {
						out.print(" onFocus=\"" + onFocus + "\" ");
					}

					if (!inline.equals("")) {
						out.print(inline + " ");
					}

					// chiusura del tag INPUT -----------------
					out.println(" />");

					// Gestione del marcatore di necessario (*)
					boolean isRequired = required.equalsIgnoreCase("true");
					if (isRequired) {
						out.println("&nbsp;*&nbsp;");
					}

				} else {

					boolean hasBody = (corpo != null) && (!corpo.equals(""));
					// Se il corpo esiste e non ho ancora trovato un'opzione
					// selezionata, la cerco nel corpo
					if (!findSelected && hasBody) {
						corpo.replace("<option>", "<li>");
						corpo.replace("</option>", "</li>");
						this.descrizione += corpo;
					}

					out.print(this.descrizione);

				}

				// ----------------------------------------

				/*
				 * tentativo di usare il div al posto del tag input.... out.print("<div class=\""+
				 * classNameBase+"View\" >"); out.print(descrizione); out.println("</div>"); ...fallito!
				 */

				// generazione del tag nascosto che contiene il valore del
				// codice associato alla descrizione
				// selezionata così come avviene nella SELECT (in questo modo
				// non perdiamo l'associazione codice-descrizione
				out.println("<INPUT type=\"hidden\" name=\"" + name + "\" value=\"" + selectedValue + "\" />");

			} // else (!isDisabled)
				// fine della gestione "DISABILITAZIONE"
				// ===================================================================

			// GENERAZIONE DELLA PARTE RELATIVA AI CONTROLLI JAVASCRIPT
			// ----------------
			// ATTENZIONE: CONTA L'ORDINE CON CUI VENGONO ESEGUITI GLI
			// INSERIMENTI NELL'ARRAY
			// DELLE FUNZIONI (per es. prima conviene fare il TRIM e poi il
			// REQUIRED)!

			boolean apertoTagJS = false;
			// usata per aprire il tag "SCRIPT" una volta sola

			if (required.equalsIgnoreCase("true")) {
				if (!apertoTagJS) {
					out.print("<SCRIPT language=\"JavaScript\"> ");
					apertoTagJS = true;
				}
				out.print("_arrFunz[_arrIndex++]=\"isRequired('" + name + "')\"; ");
			} // if required

			if (!validateWithFunction.equals("")) {
				if (!apertoTagJS) {
					out.print("<SCRIPT language=\"JavaScript\"> ");
					apertoTagJS = true;
				}
				// La funzione da invocare è nel VALUE dell'attributo
				// "validateWithFunction"
				// NOTA: la funz.JS accetta come parametro di INPUT una STRINGA
				// contenente
				// il nome del tag di select su cui eseguire i controlli. La
				// funz. deve
				// rendere un valore booleano corrispondente all'esito dei
				// controlli.

				out.print("_arrFunz[_arrIndex++]=\"" + validateWithFunction + "('" + name + "')\"; ");

			} // if validateWithFunction

			if (focusOn.equalsIgnoreCase("true") && !isDisabled) {
				if (!apertoTagJS) {
					out.print("<SCRIPT language=\"JavaScript\"> ");
					apertoTagJS = true;
				}
				out.print("document.forms[0]." + name + ".focus(); ");
			} // if focusOn

			if (apertoTagJS) {
				out.print("</SCRIPT>");
				apertoTagJS = false;
			}

		} catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}

		return 0;

	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String newSize) {
		size = newSize;
	}

	/**
	 * @deprecated si consiglia di passare un parametro size come String e non come intero
	 */
	public void setSize(int size) {
		// BY GG: anche per un parametro INTERO
		this.size = String.valueOf(size);
	}

	public String getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(String newTabIndex) {
		tabIndex = newTabIndex;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String newMultiple) {
		multiple = newMultiple;
	}

	/**
	 * @deprecated l'uso di questo metodo è sconsigliato
	 */
	public String getOnBlur() {
		return onBlur;
	}

	/**
	 * @deprecated l'uso di questo metodo è sconsigliato
	 */
	public void setOnBlur(String newOnBlur) {
		onBlur = newOnBlur;
	}

	/**
	 * @deprecated l'uso di questo metodo è sconsigliato
	 */
	public String getOnFocus() {
		return onFocus;
	}

	/**
	 * @deprecated l'uso di questo metodo è sconsigliato
	 */
	public void setOnFocus(String newOnFocus) {
		onFocus = newOnFocus;
	}

	public String getOnChange() {
		return onChange;
	}

	public void setOnChange(String newOnChange) {
		onChange = newOnChange;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String newRequired) {
		required = newRequired;
	}

	public String getValidateWithFunction() {
		return validateWithFunction;
	}

	public void setValidateWithFunction(String newValidateWithFunction) {
		validateWithFunction = newValidateWithFunction;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String newClassName) {
		className = newClassName;
	}

	public String getClassNameBase() {
		return classNameBase;
	}

	public void setClassNameBase(String newClassNameBase) {
		classNameBase = newClassNameBase;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String newDisabled) {
		disabled = newDisabled;
	}

	public String getFocusOn() {
		return focusOn;
	}

	public void setFocusOn(String newFocusOn) {
		focusOn = newFocusOn;
	}

	public String getInline() {
		return inline;
	}

	public void setInline(String newInline) {
		inline = newInline;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String newTitle) {
		title = newTitle;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String newModuleName) {
		moduleName = newModuleName;
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String newSelectedValue) {
		// BY GG: se stringa "nulla" ci mette una stringa vuota
		if (newSelectedValue == null)
			this.selectedValue = "";
		else
			this.selectedValue = newSelectedValue;
	}

	/*
	 * public void setSelectedValue(Object newSelectedValue) { // BY GG: anche per un parametro OGGETTO (se "nullo" ci
	 * mette una stringa vuota) if (newSelectedValue == null) this.selectedValue = ""; else this.selectedValue =
	 * newSelectedValue.toString(); }
	 */
	public String getAddBlank() {
		return addBlank;
	}

	public void setAddBlank(String newAddBlank) {
		addBlank = newAddBlank;
	}

	public String getBlankValue() {
		return blankValue;
	}

	public void setBlankValue(String newBlankValue) {
		// BY GG: se stringa "nulla" ci mette una stringa vuota
		if (newBlankValue == null)
			blankValue = "";
		else
			blankValue = newBlankValue;
	}

	/*
	 * public void setBlankValue(Object newBlankValue) { // BY GG: anche per un parametro OGGETTO (se "nullo" ci mette
	 * una stringa vuota) if (newBlankValue == null) blankValue = ""; else blankValue = newBlankValue.toString(); }
	 */
	public void setTruncAt(String newTruncAt) {
		truncAt = newTruncAt;
	}

	public String getTruncAt() {
		return truncAt;
	}

	public String getSelectedAll() {
		return selectedAll;
	}

	public void setSelectedAll(String newSelectedAll) {
		this.selectedAll = newSelectedAll;
	}

} // ComboBox
