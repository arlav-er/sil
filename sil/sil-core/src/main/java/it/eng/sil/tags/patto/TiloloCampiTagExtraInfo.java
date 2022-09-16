package it.eng.sil.tags.patto;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class TiloloCampiTagExtraInfo extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData param1) {
		return new VariableInfo[] { new VariableInfo("_titolo", "java.lang.String", true, VariableInfo.NESTED),
				new VariableInfo("_campo", "java.lang.String", true, VariableInfo.NESTED) };
	}
}
