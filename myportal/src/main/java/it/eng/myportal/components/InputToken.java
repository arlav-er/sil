package it.eng.myportal.components;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

/**
 * Non utilizzata al momento. Serve tanto per capire un po' che, volendo, se po' fà!
 * @author Rodi A.
 *
 */
@FacesComponent("InputToken")
public class InputToken extends UINamingContainer {

	
	public InputToken() {
		;
		;
		List<UIComponent> cc = this.getChildren();
		Map<String, UIComponent> fs = this.getFacets();
		int count = this.getChildCount();
		return ;
	}

	@Override
	public void encodeEnd(FacesContext arg0) throws IOException {
		List<UIComponent> cc = this.getChildren();
		Map<String, UIComponent> fs = this.getFacets();
		int count = this.getChildCount();
		super.encodeEnd(arg0);
	}
	
	
	
	
	
//	public String getValue() {
//		for (UIComponent el : this.getChildren()) {
//			;
//		} 
//		return "";
//	}
//	
//	public String getCodValue() {
//		return "";
//	}
	
	
	
}
