package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.beans.AbstractBaseBean;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@ViewScoped
public class ShowcaseBean extends AbstractBaseBean {
	private String inputSecret01Value = null;
	private String inputSecret02Value = null;
	private String inputText01Value = null;
	private String inputText02Value = null;
	private String inputTextarea01 = null;
	private String inputTextarea02 = null;
	private List<SelectItem> inputToken01 = null;
	private String inputSuggestion01 = null;
	private String inputSuggestion02Desc = null;
	private String inputSuggestion02Cod = null;
	private String selectOneMenu01 = null;
	private String selectOneRadio01 = null;
	private String selectOneRadio02 = null;
	private String modalDialog02InputText01 = null;
	private String modalDialog02InputText02 = null;
	private String modalDialog02InputSecret01 = null;
	private Boolean renderLabels = false;

	private List<SelectItem> inputToken01List;
	private List<SelectItem> selectOneMenu01List;
	private List<SelectItem> selectOneRadio01List;

	//
	//

	public String getInputSecret01Value() {
		return inputSecret01Value;
	}

	public void setInputSecret01Value(String inputSecret01Value) {
		this.inputSecret01Value = inputSecret01Value;
	}

	public String getInputSecret02Value() {
		return inputSecret02Value;
	}

	public void setInputSecret02Value(String inputSecret02Value) {
		this.inputSecret02Value = inputSecret02Value;
	}

	public String getInputText01Value() {
		return inputText01Value;
	}

	public void setInputText01Value(String inputText02Value) {
		this.inputText01Value = inputText02Value;
	}

	public String getInputText02Value() {
		return inputText02Value;
	}

	public void setInputText02Value(String inputText02Value) {
		this.inputText02Value = inputText02Value;
	}

	public String getInputTextarea01() {
		return inputTextarea01;
	}

	public void setInputTextarea01(String inputTextarea01) {
		this.inputTextarea01 = inputTextarea01;
	}

	public String getInputTextarea02() {
		return inputTextarea02;
	}

	public void setInputTextarea02(String inputTextarea02) {
		this.inputTextarea02 = inputTextarea02;
	}

	public List<SelectItem> getInputToken01() {
		return inputToken01;
	}

	public void setInputToken01(List<SelectItem> inputToken01) {
		this.inputToken01 = inputToken01;
	}

	public List<SelectItem> getInputToken01List() {
		return inputToken01List;
	}

	public String getInputSuggestion01() {
		return inputSuggestion01;
	}

	public void setInputSuggestion01(String inputSuggestion01) {
		this.inputSuggestion01 = inputSuggestion01;
	}

	public String getInputSuggestion02Desc() {
		return inputSuggestion02Desc;
	}

	public void setInputSuggestion02Desc(String inputSuggestion02Desc) {
		this.inputSuggestion02Desc = inputSuggestion02Desc;
	}

	public String getInputSuggestion02Cod() {
		return inputSuggestion02Cod;
	}

	public void setInputSuggestion02Cod(String inputSuggestion02Cod) {
		this.inputSuggestion02Cod = inputSuggestion02Cod;
	}

	public String getSelectOneMenu01() {
		return selectOneMenu01;
	}

	public void setSelectOneMenu01(String selectOneMenu01) {
		this.selectOneMenu01 = selectOneMenu01;
	}

	public List<SelectItem> getSelectOneMenu01List() {
		return selectOneMenu01List;
	}

	public String getSelectOneRadio01() {
		return selectOneRadio01;
	}

	public void setSelectOneRadio01(String selectOneRadio01) {
		this.selectOneRadio01 = selectOneRadio01;
	}

	public String getSelectOneRadio02() {
		return selectOneRadio02;
	}

	public void setSelectOneRadio02(String selectOneRadio02) {
		this.selectOneRadio02 = selectOneRadio02;
	}

	public List<SelectItem> getSelectOneRadio01List() {
		return selectOneRadio01List;
	}

	public String getModalDialog02InputText01() {
		return modalDialog02InputText01;
	}

	public void setModalDialog02InputText01(String modalDialog02InputText01) {
		this.modalDialog02InputText01 = modalDialog02InputText01;
	}

	public String getModalDialog02InputText02() {
		return modalDialog02InputText02;
	}

	public void setModalDialog02InputText02(String modalDialog02InputText02) {
		this.modalDialog02InputText02 = modalDialog02InputText02;
	}

	public String getModalDialog02InputSecret01() {
		return modalDialog02InputSecret01;
	}

	public void setModalDialog02InputSecret01(String modalDialog02InputSecret01) {
		this.modalDialog02InputSecret01 = modalDialog02InputSecret01;
	}

	public Boolean getRenderLabels() {
		return renderLabels;
	}

	public void setRenderLabels(Boolean renderLabels) {
		this.renderLabels = renderLabels;
	}

	//
	//

	@PostConstruct
	public void init() {
		SelectItem[] selectItems = new SelectItem[] { new SelectItem("01", "aa1"), new SelectItem("02", "aa2"),
				new SelectItem("03", "aa3"), new SelectItem("04", "bb1"), new SelectItem("05", "bb2"),
				new SelectItem("06", "ccc"), new SelectItem("07", "ddd"), new SelectItem("08", "eee") };

		this.inputToken01List = Arrays.asList(selectItems);
		this.selectOneMenu01List = Arrays.asList(selectItems);
		this.selectOneRadio01List = Arrays.asList(selectItems);
	}

	public void showError() {
		addErrorMessage("lingua.no_grado");
	}

	public void calculateRenderLabel() {
		if (modalDialog02InputText01 != null || modalDialog02InputText02 != null || modalDialog02InputSecret01 != null)
			this.renderLabels = true;
		else
			this.renderLabels = false;
	}

}
