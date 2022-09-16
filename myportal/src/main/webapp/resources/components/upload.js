
(function() {
	
	if(!this.myportal) 				
		myportal = {}
	
	if(!myportal.upload) {
		
		//---------------------------------------------------------------------------------------------		
		myportal.upload = {}
		myportal.upload.map = {}
		//---------------------------------------------------------------------------------------------		
		myportal.upload.remove = function(node) {
			try {
				var h1, h2
				
		    $('.upload_display', node.parentNode)[0].innerHTML = ""
		    $('.upload_display', node.parentNode)[0].style.display = "none"
		    $('.upload_rimuovi', node.parentNode)[0].style.display = "none"				
		    $('.upload_button',  node.parentNode)[0].style.display = ""				    		    
		    
		    h1 = $("input[type=hidden]",	node.parentNode)[0] 	
		    h2 = $("input[type=hidden]", 	node.parentNode)[1]
		    
		    $(h1).val("")
		   	$(h2).val("")
		   	
		   	delete this.map[node.parentNode.id]
		    return false	
			}
			catch(e) {
				this.console && console.log("error(component=upload): " + e)				
			}
		}
		//---------------------------------------------------------------------------------------------		
		myportal.upload.open = function(node) {
			try {
				var div, form, label, input, area
				
				div = document.createElement("div")		
				div.title = "Seleziona un file da allegare"
							
				form = document.createElement("form")
				form.id = "allegatoUpload"				
				form.enctype 	= "multipart/form-data"
				form.action 	= this.url
				form.method 	= "post"			
				
				label = document.createElement("span")													
				label.innerHTML = "Allegato "
				
				input = document.createElement("input")		
				input.id = "fileupload"
				input.name = "fileupload"
				input.type = "file"
				input.size = "60"
				
				area = document.createElement("div")		
				area.innerHTML = 	"<br/><br/><button type=\"submit\" title=\"Carica\" id=\"UploadBtn\" class=\"buttonStyle\">Carica</button>"
					
				form.appendChild(label)	
				form.appendChild(input)	
				form.appendChild(area)
				
				div.appendChild(form)
				document.body.appendChild(div)									
				disegnaBottoni()
				
				$(form).ajaxForm({
					dataType: "json",
					success:	function(responseJson, statusText, xhr, $form) {
						var h1, h2, id
						
						if(responseJson.statusCode === 200){
							
							if(responseJson.original_file_name) {
						    $('.upload_display', node.parentNode)[0].innerHTML = responseJson.original_file_name
						    
						    $('.upload_button',  node.parentNode)[0].style.display = "none"				    
						    $('.upload_display', node.parentNode)[0].style.display = ""
						    $('.upload_rimuovi', node.parentNode)[0].style.display = ""
						    							    
						    h1 = $("input[type=hidden]",	node.parentNode)[0] 	
						    h2 = $("input[type=hidden]", 	node.parentNode)[1]
						    
						    $(h1).val(responseJson.original_file_name)
						   	$(h2).val(responseJson.file_name)
	
						   	myportal.upload.map[node.parentNode.id] = { value: responseJson.original_file_name, tmp: responseJson.file_name }
							}							
							$(div).dialog("close")						
						} 
					}
				})
												
				$(div).dialog({
					width: 		520,
					autoOpen: true,
					modal: 		true
				})
				
				return false
			}
			catch(e) {
				this.console && console.log("error(component=upload): " + e)
			}
		}
	}
})()