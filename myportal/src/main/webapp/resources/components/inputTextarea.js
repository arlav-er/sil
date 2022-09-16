if (!myportal)
	var myportal = {};

function tinymce_init(){
	tinymce.init({
		
		mode : "none",
		theme : "advanced",
		//language : 'it',
		add_form_submit_trigger : true,
		debug : true,

		setup : function(ed) {
			ed.onChange.add(function(ed) {
				console.log('onChange - tinymce.triggerSave on:' + ed.id);
				tinymce.triggerSave();
				if(typeof window.setValueChanged === "function")
					setValueChanged()
				
			});

			ed.onReset.add(function(ed, e) {
				console.log('Form reset:' + e.target);
			});
			ed.onActivate.add(function(ed) {
				console.log('onActivate done: ' + ed.id);
			});
			ed.onPostProcess.add(function(ed) {
				console.log('onPostProcess done: ' + ed.id);
			});
			ed.onInit.add(function(ed) {
				$(jq(ed.id + '_parent')).css('display', 'block');
				console.log('Editor is done: ' + ed.id);
			});
			ed.onDeactivate.add(function(ed) {
				console.log('onDeactivate done: ' + ed.id);
			});
			ed.onSubmit.add(function(ed) {
				console.log('onSubmit done: ' + ed.id);
			});
			ed.onLoadContent.add(function(ed, o) {
				console.log('onLoadContent on:' + ed.id);
				// Output the element name
				console.log(o.element.nodeName);
			});

		},

		// Theme options
		theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,bullist,numlist,|,justifyleft,justifycenter,justifyright,justifyfull,|,outdent,indent,blockquote,|,undo,redo,|",
		theme_advanced_buttons2 : "",
		theme_advanced_buttons3 : "",
		theme_advanced_path : false,
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left",
		theme_advanced_statusbar_location : "none",
		theme_advanced_resizing : true,

		// Example content CSS (should be your site CSS)
		// using false to ensure that the default browser settings are used
		// for best Accessibility
		// ACCESSIBILITY SETTINGS
		content_css : "/" + contextName + "/resources/css/tiny.css",
		// Use browser preferred colors for dialogs.
		browser_preferred_colors : true,
		detect_highcontrast : true,

	});
}

if (!myportal.inputTextarea) {
	myportal.inputTextarea = {
		init : function(ccid) {
			var myDiv = document.getElementById(ccid);
			myDiv.effect = document.getElementById(ccid + ":effect");
			myDiv.inputTextarea = document.getElementById(ccid
					+ ':inputTextarea');
			myDiv.message = document.getElementById(ccid + ':message');
			// Inserimento TinyMce
			//var elTA = $(jq(ccid + ':inputTextarea'));
			//console.log('valore inputTextarea:' + elTA.val());
			//console.log('id inputTextarea:  ' + ccid + ':inputTextarea');
			edInstance = tinyMCE.get(ccid + ':inputTextarea');
			//console.log('tinyMCE.get() edInstance:' + edInstance);
			tinymce_init();
			tinymce.execCommand("mceAddControl", true, ccid + ':inputTextarea');

			edInstance = tinyMCE.get(ccid + ':inputTextarea');
			console.log('fine TinyMCE id inputTextarea:' + ccid
					+ ':inputTextarea');
		},
	};

	function myCustomOnChangeHandler(inst) {
		if (tinyMCE.activeEditor.isDirty()) {
			console.log("Some one modified something - You must save your contents.");
			tinyMCE.triggerSave();
			console.log("after tinyMCE.triggerSave()");
		}
		console.log("The HTML is now:" + inst.getBody().innerHTML);
	}

};
