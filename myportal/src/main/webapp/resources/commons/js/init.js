/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(function() {
	// popover init
	pop();
});

function pop() {
	$("[data-toggle=popover]").popover();
}

/*
 * funzioni per il logout dal MyCas
 */
function cleanCookies() {
	var cks = (document.cookie + "").split(";");

	var d = new Date(2000, 1)
	for (var a = 0; a < cks.length; a++)
		document.cookie = cks[a].split("=")[0] + "=-1;path=/;expires=" + d.toGMTString() + ";";
}

function doLogout() {
	cleanCookies();
	logout();
}