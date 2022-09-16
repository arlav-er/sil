$(document).ready(function(){
    $(document).on('click', '.stampa', function(e){
    	e.preventDefault(); //otherwise a normal form submit would occur
    	
    	var oEditor = FCKeditorAPI.GetInstance('EditorDefault');
    	contents = oEditor.GetXHTML(true);
    	$('#EditorDefault').val(contents);
    	
    	//console.log($("#formSpil").serialize());
        $.fileDownload("/sil/creaPdf", {
            //preparingMessageHtml: "We are preparing your report, please wait...",
            //failMessageHtml: "There was a problem generating your report, please try again.",
            httpMethod: "POST",
            data: $("#formSpil").serialize()
        });
        
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
		
		//function apriGestioneDoc(rptAction       ,parametri,tipoDoc           , pageDaChiamare          , forzaProtocollazione, rptInPopup)
		apriGestioneDoc ('RPT_STAMPA_PAR', parameters , obj["codTipoDoc"]);
	});
});
