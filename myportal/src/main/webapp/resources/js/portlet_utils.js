var portlet_el = "dl.portlet";
var portlet_item = "dd.portletItem"; 

/**
 * Inizializza le portlet.
 * Ogni portlet e' un div con classe portlet.
 * Ogni div con classe portlet e' composto da:
 *  - un div con classe portlet-caption (la barra del titolo)
 *  - un div con classe portlet-content (il contenuto vero e proprio)
 * La barra del titolo contiene:
 *  - uno span con classe portlet-shade (il pulsante per minimizzare la portlet)
 *  - uno span con classe portlet-close (il pulsante per chiuderla)
 * Ogni portlet deve essere contenuta in un div con classe portlet-column
 */
function initPortlets() {
	
	
	// Riduce/apre le portlet	
	$(portlet_el +' span.portlet-shade').click(function() {
		
		//$(this).closest(portlet_el).find(portlet_el+'div.portlet-caption > img').slideToggle('normal');
    	$(this).closest(portlet_el).find(portlet_item).slideToggle('normal',
        	function() {
    			//alert("chiudo portlet");
    			// recupero il div più esterno del template portlet
    			btn = $('[id$=riduciPortlet]', this.parentNode);
    			btn.click();
					    				
    			// cambio lo "stato" del modulo
        		$(this).closest(portlet_el).toggleClass('rolledup');
        		// cambio l'icona
        		$(this).closest(portlet_el).find('span.portlet-shade').toggleClass('ui-icon-triangle-1-s ui-icon-triangle-1-n');
        	
    			//Maresta A.
  				var maps = $(this).find(".map_canvas")//;
  				for(var a = 0, l = maps.length; a < l; a++)
  					maps[a].map.redraw()//;    	
    	});
   	});

   	// Chiude le portlet
	$(portlet_el+' span.portlet-close').click(function() {
		if (confirm('Vuoi rimuovere la portlet dalla scrivania?')) {
			//aggiorno il flag sul db			
			// recupero il div più esterno del template portlet
			btn = $('[id$=eliminaPortlet]', this.parentNode.parentNode);
			btn.click();		
			$(this).closest(portlet_el).fadeOut('normal', function() {			
				$('*', $(this).closest(portlet_el)).remove();
			});
		};
	});

	var startPosition;
	
	
	// Rende le portlet posizionabili tramite drag-n-drop
	$('.portlet-column').sortable({		
		cursor: 'move',
		handle: 'dt.portletHeader',
		opacity: '0.7',
		connectWith: '.portlet-column',
		placeholder: 'portlet-placeholder',	
		start: function(event, ui) {
			startPosition = ui.item.index();			
		},				
		stop: function(event, ui) {
			resetPositions();
		}	
	});	
	
};	

function resetPositions() {
	var param = "";
	$('.portlet-column .portlet').each(function() {		
		var colonna;
		if ($(this).closest(".portlet-column").attr("id") == 'left_column')
			colonna = "L";
		else
			colonna = "R";		

		$(this).find('[id$=posizione]').val($(this).parent().index());
		$(this).find('[id$=colonna]').val(colonna);
		id = $(this).find('[id$=idPortlet]').val();
		if (id) {
			param += "id="+id;
			param += ",col="+colonna;
			param += ",pos="+$(this).parent().index() + ";";
		}
	});
	$(jq("portlet_positioner:params")).val(param);
	$(jq("portlet_positioner:posizionaPortlet")).click();
}


/**
 * Applica l'effetto di caduta sul componente passato come input
 * FIXME non funziona bene, al momento non viene piu' usata. Eliminare?
 * 
 * @param element l'elemento su cui applicare l'effetto 
 */
function drop(element) {
	element.css('position','relative')
	.animate(
		{
			opacity: 0,
			top: $(window).height() - element.height() - element.position().top
		},
		'slow',
		// function(){ element.hide(); }
		function(){ element.remove(); }
	);
};

function configurePortlet() {
	$(portlet_el).each(function(index) {
		// FLAG_VISUALIZZA						
		//alert($('[id$=flagVisualizza]', this).val());
		
		if ($('[id$=flagVisualizza]', this).val() == 'true') {			
			$(this).closest(portlet_el).show();
			
			if ($('[id$=flagRidotta]', this).val() == 'true') {
				//$(this).closest(portlet_el).find(portlet_el+'-caption > img').slideToggle('normal');				
				$(this).closest(portlet_el).find(portlet_item).slideToggle('normal',
		        	function() {
		        		// cambio lo "stato" del modulo
		        		$(this).closest(portlet_el).toggleClass('rolledup');
		        		// cambio l'icona
		        		$(this).closest(portlet_el).find('span.portlet-shade').toggleClass('ui-icon-triangle-1-s ui-icon-triangle-1-n');
		        	});
			}
		}			
	});
	
};

//elenco dei menu aperti nella pagina
var openMenus = [];


function openActionMenu(button) {
	var menu = button.parent().next();
	
	if (button.prop('menuOpen')) {
		hide_menus();
		return false;
	}
	else {
		hide_menus();									
		button.prop('menuOpen',true);
		openMenus.push(button);
		menu.show().position({
			my: "right top",
			at: "right bottom",
			of: button
		});
		$(document).one("click", function() {
			//menu.hide();
			hide_menus();
		});
		return false;
	}
}

function generateActionMenus(query) {
	$(query).click(function(e) {
		var button = $(this);
		return openActionMenu(button);		
	}).keypress(function(e) {
		if (e.keyCode == 13) {
			var button = $(this);			
			return openActionMenu(button);
		}
	})
	.parent().next().hide().menu().css('position','absolute').css('z-index','100').css('width','130px');	
	$(query).parent().next().find('a').attr('tabindex',0).focusin(function() {
		$(this).addClass('ui-state-hover');
	}).focusout(function() {
		$(this).removeClass('ui-state-hover');
	});
}

function generateActionMenus(query,width) {
	$(query).click(function(e) {
		var button = $(this);
		return openActionMenu(button);		
	}).keypress(function(e) {
		if (e.keyCode == 13) {
			var button = $(this);			
			return openActionMenu(button);
		}
	})
	.parent().next().hide().menu().css('position','absolute').css('z-index','100').css('width',width);	
	$(query).parent().next().find('a').attr('tabindex',0).focusin(function() {
		$(this).addClass('ui-state-hover');
	}).focusout(function() {
		$(this).removeClass('ui-state-hover');
	});
}
function hide_menus() {	
	for (var i=0;i<openMenus.length;i++) {		
		openMenus[i].parent().next().hide();
		openMenus[i].prop('menuOpen',false);
	}
}

function save_last_action(el,name) {
	document.cookie = "lastaction="+name+";";
	document.location = el.href;
	return false;
}

function read_cookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}								
