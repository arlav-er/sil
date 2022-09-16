var pageSizeWidth = 575;
var pageSizeHeight = 842;
var offsetHeightOld = 0;
var FCK;
function FCKeditor_OnComplete(editorInstance) {

	var body = editorInstance.EditingArea.IFrame.contentDocument.body;

	//var HTML = fileTemplate;
	//editorInstance.InsertHtml(HTML);

	//editorInstance.Events.AttachEvent('OnSelectionChange', HandleEditorChanges);
	editorInstance.Events.AttachEvent('OnFocus', HandleFocus);
	editorInstance.Events.AttachEvent('OnMouseUp', HandleMouseUp);

}

function HandlePaste(editorInstance) {
	console.log("paste");
}

function HandleFocus(editorInstance) {

}

function HandleMouseUp(editorInstance) {
	console.log("MouseUp");
}

function HandleEditorChanges(editorInstance) {
	var body = editorInstance.EditingArea.IFrame.contentDocument.body;
	offsetHeightNew = editorInstance.EditingArea.IFrame.contentDocument.body.offsetHeight;
	if (offsetHeightOld != offsetHeightNew) {
		offsetHeight = offsetHeightNew;
		var heightBreakPage = pageSizeHeight;
		$(body).children('.breakDownPage').each(function(index, element) {
			$(this).remove();
		});

		// logica per inserire i breakdown
		// SOSTITUIRE I DIV CON I BREAK DOWN DI FCKEDITOR
		// INSERIRE I BREAK DOWN NELLA GIUSTA POSIZIONE TRA I PARAGRAFI
		// (CONTROLLARE L'ATTRIBUTO OFFSETTOP DI <P>)
		while (!(heightBreakPage > offsetHeightNew)) {
			// console.log("altezza pagina di rottura :" + heightBreakPage);

			var prevOffsetTop;
			var prevClientHeight;
			inseriPageBreakAfter(body, heightBreakPage);
			heightBreakPage += pageSizeHeight;
		}
	}
}

function inseriPageBreakAfter(elem, heightBreakPage) {
	$(elem)
			.children('*')
			.each(
					function(index, element) {
						if (index > 0) {
							prevOffsetTop = $(this).prev()[0].offsetTop;
							prevClientHeight = $(this).prev()[0].clientHeight;
							/*
							 * console.log("resize " + this.offsetHeight +
							 * ";altezza pagina di rottura = " +
							 * heightBreakPage); console.log("previous offsetTop
							 * <p> =" + prevOffsetTop + prevClientHeight);
							 * console.log("actual offsetTop <p> =" +
							 * this.offsetTop + this.clientHeight);
							 */
							if ((prevOffsetTop + prevClientHeight) < heightBreakPage
									&& !((this.offsetTop + this.clientHeight) < heightBreakPage)) {
								if ($(elem).children(
										'.offsetHeight' + heightBreakPage + '').length == 0) {
									$(this)
											.before(
													"<div class='breakDownPage offsetHeight"
															+ heightBreakPage
															+ "' style='page-break-after: always;width:100%;border: 1px solid #d4cdcd;'><span style='DISPLAY:none'>&nbsp;</span></div>");
								}
							}
						}
					});
}

$(function() {
	$("select#tagAlias > option")
			.bind(
					"dblclick",
					function() {
						console.log($(this).text());
						var inst = FCKeditorAPI.GetInstance("EditorDefault");
						var selection = (inst.EditorWindow.getSelection ? inst.EditorWindow
								.getSelection()
								: inst.EditorDocument.selection);
						inst.InsertHtml("@" + $(this).text());
					});
	$("select#tagAliasDatiCPI > option")
			.bind(
					"dblclick",
					function() {
						console.log($(this).text());
						var inst = FCKeditorAPI.GetInstance("EditorDefault");
						var selection = (inst.EditorWindow.getSelection ? inst.EditorWindow
								.getSelection()
								: inst.EditorDocument.selection);
						inst.InsertHtml("@" + $(this).text());
					});
	$("select#tagAliasAngoliFirme > option")
	.bind(
			"dblclick",
			function() {
				console.log($(this).text());
				var inst = FCKeditorAPI.GetInstance("EditorDefault");
				var selection = (inst.EditorWindow.getSelection ? inst.EditorWindow
						.getSelection()
						: inst.EditorDocument.selection);
				inst.InsertHtml($(this).val());
			});	
	$("select#tagAliasDatiProt > option")
	.bind(
			"dblclick",
			function() {
				console.log($(this).text());
				var inst = FCKeditorAPI.GetInstance("EditorDefault");
				var selection = (inst.EditorWindow.getSelection ? inst.EditorWindow
						.getSelection()
						: inst.EditorDocument.selection);
				inst.InsertHtml($(this).val());
			});	
	$("select#tagListaAllegati > option")
	.bind(
			"dblclick",
			function() {
				console.log($(this).text());
				var inst = FCKeditorAPI.GetInstance("EditorDefault");
				var selection = (inst.EditorWindow.getSelection ? inst.EditorWindow
						.getSelection()
						: inst.EditorDocument.selection);
				inst.InsertHtml($(this).val());
			});	
	$("select#tagFooter > option")
	.bind(
			"dblclick",
			function() {
				console.log($(this).text());
				var inst = FCKeditorAPI.GetInstance("EditorDefault");
				var selection = (inst.EditorWindow.getSelection ? inst.EditorWindow
						.getSelection()
						: inst.EditorDocument.selection);
				inst.InsertHtml($(this).val());
			});	
});

function impostaAction(tipo) {
	// console.log("tipo = " + tipo);
	var filetemplate = document.getElementById("filetemplate");
	var oEditor = FCKeditorAPI.GetInstance('EditorDefault');
	//contents = oEditor.GetXHTML(true);

	filetemplate.value = oEditor.GetXHTML(true);
	// console.log("filetemplate " + contents)
	document.frmTemplate.action = "AdapterHTTP";
	var page = document.getElementById("page");
	var module = document.getElementById("module");
	

	if (tipo == "stampaFake")
		document.frmTemplate.action = "/sil/creaPdf";
	else if (tipo == "modificaEditor") { 		
		page.value = "DettaglioTemplatePage";
		module.value = "MAggiornaEditor";
	} else if (tipo == "apriEditor") {
		page.value = "DettaglioTemplatePage";
		module.value = "MDettaglioTemplate";
	} else if (tipo == "salvaEditor" || tipo == '') {
		page.value = "InsTemplatePage";
		module.value = "MInsTemplate";
	}	
	/*else if (tipo == "salvaDocLav")
	{
		page.value = "";//EditorPageLav";
		module.value = "";//MAggiornaEditor";
		action_name.value = "RPT_STAMPA_PAR";
		document.frmTemplate.undoSubmit();
	}*/
}

$(document).ready(function(){
    $(document).on('click', '.stampa', function(e){
    	e.preventDefault(); //otherwise a normal form submit would occur
    	
    	var oEditor = FCKeditorAPI.GetInstance('EditorDefault');
    	contents = oEditor.GetXHTML(true);
    	$('#EditorDefault').val(contents);
    	console.log("contextpath " + contextPath);
    	//console.log($("#formSpil").serialize());
        $.fileDownload( contextPath + "/services/creaPdf", {
            //preparingMessageHtml: "We are preparing your report, please wait...",
            //failMessageHtml: "There was a problem generating your report, please try again.",
            httpMethod: "POST",
            data: $("#formSpil").serialize()
        });
        
    });
    
    $("input#modificaEditor").click(function(e){
		var filetemplate = document.getElementById("filetemplate");
		var oEditor = FCKeditorAPI.GetInstance('EditorDefault');
		filetemplate.value = oEditor.GetXHTML(true);
		var template = filetemplate.value;
		var angoliFirme = document.getElementById("tagAliasAngoliFirme");
		//if (angoliFirme !=null) {
		//	for (var i = 0; i < angoliFirme.length; i++) {
		//		if (template.indexOf(angoliFirme.options[i].value) == -1) {
		//			alert("E' obbligatorio inserire i Tags Firma Grafometrica");
		//			e.preventDefault();
		//			break;
		//		}	
		//	}	
		//} 
    });   
    
    $('input#salvaDocLav').click(function(e){
		e.preventDefault();
		
		//ATTENZIONE NON INSERISCO IL FILETEMPLATE NELL'URL PERCHE' POTREBBE SUPERARE IL LIMITE DI LUNGHEZZA DELL'URL
		var parameters = "";
		for (var key in obj) {
			if (obj.hasOwnProperty(key)) {
				//sostituisdco i crt "spazio" con "%20"
				parameters += "&" + key + "=" + obj[key].replace(/ /g, '%20');;
			}
		}
		//console.log("st =" + st);
		var page = document.getElementById("page");
		var module = document.getElementById("module");
		var action_name = document.getElementById("action_name");
		action_name.value = "RPT_STAMPA_PAR";
		page.value = "";
		module.value = "";
		
		var filetemplate = document.getElementById("filetemplate");
		var oEditor = FCKeditorAPI.GetInstance('EditorDefault');
		filetemplate.value = oEditor.GetXHTML(true);
		
		console.log("filetemplate prima " + filetemplate.value);
		//trasformo i path delle immagini in path assoluti
		var prima = ' src="' + contextPath;
		var dopo = ' src="' + scheme + "://" + header + contextPath;
		var template = filetemplate.value;
		template = template.split(prima).join(dopo);
		filetemplate.value = template;
		//function apriGestioneDoc(rptAction       ,parametri,tipoDoc           , pageDaChiamare          , forzaProtocollazione, rptInPopup)
		console.log("filetemplate dopo " + filetemplate.value);
		apriGestioneDoc ('RPT_STAMPA_PAR', parameters , obj["codTipoDoc"]);
	});
});


