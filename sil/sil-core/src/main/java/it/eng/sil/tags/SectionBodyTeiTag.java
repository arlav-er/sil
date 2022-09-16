package it.eng.sil.tags;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

//import com.engiweb.framework.base.SourceBean;
/**
 * @author user
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public class SectionBodyTeiTag extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData d) {
		return new VariableInfo[] { new VariableInfo("row", "SourceBean", true, VariableInfo.NESTED) };
	}
}