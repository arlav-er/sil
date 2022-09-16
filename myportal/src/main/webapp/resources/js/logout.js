/*
 * funzioni per il logout dal MyCas 
 */
function cleanCookies() {       
    var cks = (document.cookie + "").split(";");
    
    var d = new Date(2000, 1)
    for(var a = 0; a < cks.length; a++)
            document.cookie = cks[a].split("=")[0] + "=-1;path=/;expires=" + d.toGMTString() + ";"; 
}

function doLogout() {	
	logout();
	cleanCookies();	
}

function casLogout(path) {
    window.location = path; 
}

