function apri(url_pagina) 
{
  if (url_pagina != '')
  {
  	//str = 'file:///S:/CTRI008/Pagine HTML/'+url_pagina	
	window.location = "//Lupo_67/sict/CTRI008/Pagine HTML/" + url_pagina;
  }
}

function enableField(oggetto_input)
{
   oggetto_input.className = "edit";
   if (navigator.appName.indexOf("Netscape") > -1)
       {
         oggetto_input.readOnly= false;
       }
   else
       {
        if (navigator.appName.indexOf("Microsoft") > -1)
       	   {
            oggetto_input.disabled= false;
       		
           }	
       }	
}
function disableField(oggetto_input)
{
   if (navigator.appName.indexOf("Netscape") > -1)
       {
         oggetto_input.write("<STYLE> .Change { color: #005555; } <STYLE>")
 
         oggetto_input.readOnly= true;
       }
   else
       {
        if (navigator.appName.indexOf("Microsoft") > -1)
       	   {
            oggetto_input.disabled= true;
       	   }	
       }	
}
		