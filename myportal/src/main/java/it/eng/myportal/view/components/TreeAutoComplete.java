package it.eng.myportal.view.components;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.html.HtmlPanelGroup;

@FacesComponent("treeAutoComplete")
public class TreeAutoComplete extends UINamingContainer {
	private HtmlPanelGroup panelGroup;

	/**
	 * @return the panelGroup
	 */
	public HtmlPanelGroup getPanelGroup() {
		return panelGroup;
	}

	/**
	 * @param panelGroup
	 *            the panelGroup to set
	 */
	public void setPanelGroup(HtmlPanelGroup panelGroup) {
		this.panelGroup = panelGroup;
	}

}
