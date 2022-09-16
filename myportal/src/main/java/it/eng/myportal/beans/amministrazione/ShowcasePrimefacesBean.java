package it.eng.myportal.beans.amministrazione;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@ViewScoped
public class ShowcasePrimefacesBean {
	
	String[] inputTokenParole = {"aa1", "aa2", "aa3", "aa4", "bb1", "bb2", "bb3", "bb4",
			"cc1", "cc2", "cc3", "cc4", "dd1", "dd2", "dd3", "dd4"};
	
	// Properties
	private String password1 = "";
	private String password2 = "";
	
	private String input1="";
	private Date input2;
	
	private String inputArea1 = "";
	private String inputArea2 = "";
	
	private List<String> inputToken;
	
	private String selectOneMenuValue;
	
	private String selectOneRadioValue1;
	private String selectOneRadioValue2;
	
	public String getPassword1() { return password1; }
	public void setPassword1(String password1) {this.password1 = password1;}
	
	public String getPassword2() {return password2;}
	public void setPassword2(String password2) {this.password2 = password2;}
	
	public String getInput1() { return input1; }
	public void setInput1(String input1) { this.input1 = input1; }
	
	public Date getInput2() { return input2; }
	public void setInput2(Date input2) { this.input2 = input2; }
	
	public String getInputArea1() {return inputArea1;}
	public void setInputArea1(String inputArea1) {this.inputArea1 = inputArea1;}
	
	public String getInputArea2() {return inputArea2;}
	public void setInputArea2(String inputArea2) {this.inputArea2 = inputArea2;}
	
	public List<String> getInputToken() {return inputToken; }
	public void setInputToken(List<String> inputToken) { this.inputToken = inputToken; }
	public List<String> completeInputToken(String query) {
		List<String> filteredStrings = new ArrayList<String>();
		for (String s : inputTokenParole) {
			if (s.toLowerCase().contains(query.toLowerCase()))
				filteredStrings.add(s);
		}
		return filteredStrings;
	}
	
	public String getSelectOneMenuValue() { return selectOneMenuValue;}
	public void setSelectOneMenuValue(String selectOneMenuValue)
		{this.selectOneMenuValue = selectOneMenuValue;}
	
	public List<SelectItem> getSelectOneMenuAll() {
		List<SelectItem> ret = new ArrayList<SelectItem>();
		ret.add(new SelectItem("uno", "uno"));
		ret.add(new SelectItem("due", "due"));
		ret.add(new SelectItem("tre", "tre"));
		ret.add(new SelectItem("quattro", "quattro"));
		ret.add(new SelectItem("cinque", "cinque"));
		return ret;
	}
	
	public String getSelectOneRadioValue1() { return selectOneRadioValue1; }
	public void setSelectOneRadioValue1(String selectOneRadioValue1) { 
		this.selectOneRadioValue1 = selectOneRadioValue1;}
	public String getSelectOneRadioValue2() {return selectOneRadioValue2;}
	public void setSelectOneRadioValue2(String selectOneRadioValue2) {
		this.selectOneRadioValue2 = selectOneRadioValue2;}
	
	public List<SelectItem> getSelectOneRadioAll() {
		List<SelectItem> ret = new ArrayList<SelectItem>();
		ret.add(new SelectItem("aaa", "aaa"));
		ret.add(new SelectItem("bbb", "bbb"));
		ret.add(new SelectItem("06", "ccc"));
		ret.add(new SelectItem("ddd", "ddd"));
		ret.add(new SelectItem("eee", "eee"));
		ret.add(new SelectItem("fff", "fff"));
		return ret;
	}
}
