/*

contextMenuFix for FCKeditor
============================

Version 1.2.2 - updated 13 October 2009

Written by J. Reijers (Osem Websystems) - http://www.osem.nl

---------------------------------------------------------

This plugin fixes bug #703 (no context menu when right-clicking certain components in Firefox): http://dev.fckeditor.net/ticket/703

It simply replaces the checkbox, radio button and select box with clickable placeholders.

*/



// Include fix.css

FCKConfig.EditorAreaCSS = String(FCKConfig.EditorAreaCSS) + ", " + FCKPlugins.Items["contextMenuFix"].Path + "fix.css";

// Append document processor

var contextMenuFixProcessor = FCKDocumentProcessor.AppendNew();
contextMenuFixProcessor.ProcessDocument = function(document) {
	var el;

	var aTags = document.getElementsByTagName("input") ;
	var i = aTags.length - 1;
	while (i >= 0 && (el = aTags[i--])) {
		var type = el.getAttribute("type");
		if (type == "checkbox") {
			var fakeEl = FCKDocumentProcessor_CreateFakeImage("FCK__" + type + " " + (el.checked ? "FCK__" + type + "_checked" : ""), el.cloneNode(true));
			fakeEl.setAttribute("_" + type, "true", 0);
			el.parentNode.insertBefore(fakeEl, el);
			el.parentNode.removeChild(el);
		}
	}
}

// Overwrite the related command

FCKCommands.LoadedCommands["Checkbox"] = new FCKDialogCommand("Checkbox", "Checkbox", FCKPlugins.Items["contextMenuFix"].Path + "checkbox.html", 300, 280);

// Create the toolbar button

var o = new FCKToolbarButton("Checkbox", "Checkbox", "Checkbox", null, null, false, true);
o.IconPath = FCKPlugins.Items["contextMenuFix"].Path + "checkbox_on.gif";
FCKToolbarItems.RegisterItem("Checkbox", o);

FCK.ContextMenu.RegisterListener({
	AddItems: function(menu, tag, tagName) {
		if (tagName == "IMG" && tag.getAttribute("_checkbox")) {
			menu.AddSeparator();
			menu.AddItem("Checkbox", "Checkbox Properties", FCKPlugins.Items["contextMenuFix"].Path + "checkbox_on.gif");
		}
	}
});


