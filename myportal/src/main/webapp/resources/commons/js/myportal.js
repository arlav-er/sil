/* 
 * MyPortal object, the main module of the application
 */

(function(MyPortal, $, undefined) {

	MyPortal.init = function() {
		if ($.browser.msie && parseInt($.browser.version, 10) === 8) {
			window.location.href = "ie";
		}

		if ($.browser.msie) {
			$('body').addClass('ie');
		}
		$(document).on("pfAjaxError", function(event, xhr, options) {
			MyPortal.unexpectedError();
		});

		$('.loadScript').remove();
	};

	MyPortal.version = function() {
		return "1.0";
	};

	MyPortal.activeTab = function(tab) {
		$("." + tab).addClass('active');
	}

	MyPortal.createNS = function(namespace) {
		var nsparts = namespace.split(".");
		var parent = this;

		if (nsparts[0] === "MyPortal") {
			nsparts = nsparts.slice(1);
		}

		for (var i = 0; i < nsparts.length; i++) {
			var partname = nsparts[i];
			if (typeof parent[partname] === "undefined") {
				parent[partname] = {};
			}
			parent = parent[partname];
		}
		return parent;
	};

	MyPortal.isVacancySaved = function(id, callback) {
		var savedVacancies = store.get("savedVacancies");
		if ($.inArray(id, savedVacancies) > -1) {
			return true;
		}

		if (typeof callback === "function") {
			callback();
		}

		return false;
	};

	MyPortal.unexpectedError = function() {
		// Error Handling
		swal({
			title : "Errore Inatteso",
			text : "La pagina verrà aggiornata in 2 secondi",
			timer : 2000,
			type : "warning",
			confirmButtonColor : "#c82b2e"
		});

		setTimeout(function() {
			location.reload();
		}, 2000);
	};

	MyPortal.sucessMessage = function(message) {
		$.UIkit.notify('<i class="fa fa-check fa-success fa-success"></i> ' + message, {
			status : 'success'
		});
	};

	MyPortal.warnMesssage = function(message) {
		$.UIkit.notify('<i class="fa fa-exclamation-triangle fa-infor"></i> ' + message, {
			status : 'warning'
		});
	};

	MyPortal.validationErrorMesssage = function() {
		$.UIkit.notify('<i class="fa fa-exclamation-triangle fa-infor"></i> Attenzione, ricontrolla i dati inseriti', {
			status : 'danger'

		});
	};

	MyPortal.infoMesssage = function(message) {
		$.UIkit.notify('<i class="fa fa-info"></i> ' + message, {
			status : 'info'
		});
	};

	MyPortal.dangerMesssage = function(message) {
		$.UIkit.notify('<i class="fa fa-frown-o fa-errorRed"></i> ' + message, {
			status : 'danger'
		});
	};

	MyPortal.successAlert = function(title, message) {
		swal({
			title : title,
			text : message,
			type : "success",
			confirmButtonColor : "#c82b2e"
		});
	}

	MyPortal.warnAlert = function(title, message) {
		swal({
			title : title,
			text : message,
			type : "warning",
			confirmButtonColor : "#c82b2e"
		});
	}

	MyPortal.infoAlert = function(title, message) {
		swal({
			title : title,
			text : message,
			type : "info",
			confirmButtonColor : "#c82b2e"
		});
	}

	MyPortal.errorAlert = function(title, message) {
		swal({
			title : title,
			text : message,
			type : "error",
			confirmButtonColor : "#c82b2e"
		});
	}

	MyPortal.initTooltip = function() {
		$('[data-toggle="tooltip"]').tooltip();
	};

	MyPortal.enableLoadingButton = function(btn) {
		if (!$.browser.msie) {
			$(btn).attr("data-loading-text", "<i class='fa fa-circle-o-notch fa-spin '></i> Caricamento")
			return $(btn).button('loading');
		}
	};

	MyPortal.disableLoadingButton = function(btn) {
		$('.ui-messages').attr('role', 'alert');
		$('.ui-message').attr('role', 'alert');

		// KILL ME
		$('.ui-messages ul li').remove();
		$('.ui-messages ul').append('<li>Sono presenti alcuni errori, verifica per favore</li>')

		if (!$.browser.msie) {
			if (buttonToDisable)
				$(buttonToDisable).button('reset');
		}
	};

	MyPortal.getWidgetVarById = function(id) {
		for ( var propertyName in PrimeFaces.widgets) {
			if (PrimeFaces.widgets[propertyName] != null && PrimeFaces.widgets[propertyName].hasOwnProperty('id')) {
				if (PrimeFaces.widgets[propertyName].id === id) {
					return PrimeFaces.widgets[propertyName];
				}
			}

		}
	}

	// Nav Module
	MyPortal.Nav = {
		getStateParam : function(name) {
			name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
			var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(History.getState()['url']);
			return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
		},

		comeToYuri : function(title) {

			if (typeof title === 'undefined') {
				title = "MyPortal";
			}

			$(document).on('click', '.navYuri', function(event) {
				var urlParams = new URLSearchParams(window.location.search);
				event.preventDefault();
				var currentNav = $(this).attr('href');
				if (MyPortal.Nav.formDirty) {
					MyPortal.Nav.dirtyFormFound(currentNav, title);
					return;
				}

				if (urlParams.has('id')) {
					History.pushState(null, title, "?yuri=" + currentNav + "&id=" + urlParams.get('id'));
				} else {
					History.pushState(null, title, "?yuri=" + currentNav);
				}

			})
		},

		bindDetectDirtyEvents : function(formId) {
			MyPortal.Nav.formDirty = false;
			selector = '#' + formId + ' input, #' + formId + ' select, #' + formId + ' textarea';
			$(document).on('change', selector, function() {
				MyPortal.Nav.formDirty = true;
			});
		},

		dirtyFormFound : function(currentNav, title) {
			swal(
					{
						title : "Abbandonare la pagina ?",
						text : "Questa pagina richiede una conferma (Continua) prima di poter uscire. I dati inseriti potrebbero non essere stati salvati.",
						type : "warning",
						showCancelButton : true,
						cancelButtonText : "Annulla",
						confirmButtonText : "Abbandona la pagina",
						confirmButtonColor : "#c82b2e",
						closeOnConfirm : true
					}, function() {
						History.pushState(null, title, "?yuri=" + currentNav);
					});
		}

	}

	MyPortal.dataTableLanguage = {
		"sEmptyTable" : "Nessun dato presente nella tabella",
		"sInfo" : "Vista da _START_ a _END_ di _TOTAL_ elementi",
		"sInfoEmpty" : "Vista da 0 a 0 di 0 elementi",
		"sInfoFiltered" : "(filtrati da _MAX_ elementi totali)",
		"sInfoPostFix" : "",
		"sInfoThousands" : ".",
		"sLengthMenu" : "Visualizza _MENU_ elementi",
		"sLoadingRecords" : "Caricamento...",
		"sProcessing" : "Elaborazione...",
		"sSearch" : "Cerca:",
		"sZeroRecords" : "La ricerca non ha portato alcun risultato.",
		"oPaginate" : {
			"sFirst" : "Inizio",
			"sPrevious" : "Precedente",
			"sNext" : "Successivo",
			"sLast" : "Fine"
		},
		"oAria" : {
			"sSortAscending" : ": attiva per ordinare la colonna in ordine crescente",
			"sSortDescending" : ": attiva per ordinare la colonna in ordine decrescente"
		}
	}

	MyPortal.fireAutocompleteSearch = function(widgetName) {
		PF(widgetName).search('');
		PF(widgetName).alignPanel();
		PF(widgetName).input.focus();
	}

	MyPortal.isMobileView = function() {
		return MyPortal.deviceWidth() < 978;
	}

	MyPortal.deviceWidth = function() {
		return $(window).width();
	}

	MyPortal.fetchStampa = function(source) {
		myHandler = function() {
			var content = document.getElementById('ifrm').contentDocument.firstChild.childNodes[1].innerHTML;

			$("#waitStampa").hide();
			$("#answer .ok").hide();
			if (content.indexOf('HTTP Status 500 - ') != -1) {

				$("#answer .errorStampa").fadeIn();

			}
		};

		$('.errorStampa').hide();
		$("#waitStampa").fadeIn();
		$('#ifrm').remove();
		var el = document.createElement("iframe");
		el.setAttribute('id', 'ifrm');
		el.setAttribute('style', "visibility:hidden");
		el.setAttribute('src', source);
		if ($.browser.msie && $.browser.version < 9) {
			// alert("8");
			el.onreadystatechange = myHandler;
		} else if ($.browser.msie && $.browser.version >= 9) {
			// alert("9");
			el.onreadystatechange = myHandler;
		} else {
			el.onload = myHandler;
		}
		document.body.appendChild(el);
	}
}(window.MyPortal = window.MyPortal || {}, jQuery));

PrimeFaces.locales['it'] = {
	closeText : 'Chiudi',
	prevText : 'Precedente',
	nextText : 'Prossimo',
	monthNames : [ 'Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno', 'Luglio', 'Agosto', 'Settembre',
			'Ottobre', 'Novembre', 'Dicembre' ],
	monthNamesShort : [ 'Gen', 'Feb', 'Mar', 'Apr', 'Mag', 'Giu', 'Lug', 'Ago', 'Set', 'Ott', 'Nov', 'Dic' ],
	dayNames : [ 'Domenica', 'Luned�', 'Marted�', 'Mercoled�', 'Gioved�', 'Venerd�', 'Sabato' ],
	dayNamesShort : [ 'Dom', 'Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab' ],
	dayNamesMin : [ 'D', 'L', 'M', 'M ', 'G', 'V ', 'S' ],
	weekHeader : 'Sett',
	firstDay : 1,
	isRTL : false,
	showMonthAfterYear : false,
	yearSuffix : '',
	timeOnlyTitle : 'Ora',
	timeText : 'Ora',
	hourText : 'Ore',
	minuteText : 'Minuto',
	secondText : 'Secondo',
	currentText : 'Data Odierna',
	ampm : false,
	month : 'Mese',
	week : 'Settimana',
	day : 'Giorno',
	allDayText : 'Tutto il Giorno'
};

/**
 * read state params
 */

PrimeFaces.widget.SelectOneMenu.prototype._render = function() {
	var userStyle = this.jq.attr('style');

	// do not adjust width of container if there is user width defined
	if (!userStyle || userStyle.indexOf('width') == -1) {
		this.jq.width(this.input.outerWidth(true) + 5);
	}

	// shitty design by Primefaces
	// this.label.width('72%');
	this.label.width("72%");

	// align panel and container
	var jqWidth = this.jq.innerWidth();
	if (this.panel.outerWidth() < jqWidth) {
		this.panel.width(jqWidth);
	}
}

PrimeFaces.widget.InputTextarea.prototype.occurrences = function(string, subString, allowOverlapping) {

	string += "";
	subString += "";
	if (subString.length <= 0)
		return string.length + 1;

	var n = 0, pos = 0;
	var step = (allowOverlapping) ? (1) : (subString.length);

	while (true) {
		pos = string.indexOf(subString, pos);
		if (pos >= 0) {
			n++;
			pos += step;
		} else
			break;
	}
	return (n);
}

PrimeFaces.widget.InputTextarea.prototype.applyMaxlength = function() {
	var _self = this;

	this.jq.keyup(function(e) {
		var value = _self.jq.val(), length = value.length;
		newLineOccurrences = _self.occurrences(value, '\n'), length = value.length + newLineOccurrences;
		if (length > _self.cfg.maxlength) {
			_self.jq.val(value.substr(0, _self.cfg.maxlength - newLineOccurrences));
		}

		if (_self.counter) {
			_self.updateCounter();
		}
	});
}

PrimeFaces.widget.InputTextarea.prototype.updateCounter = function() {
	var value = this.jq.val(), length = value.length;
	length = value.length + this.occurrences(value, '\n');
	if (this.counter) {
		var remaining = this.cfg.maxlength - length, remainingText = this.cfg.counterTemplate.replace('{0}', remaining);

		this.counter.html(remainingText);
	}
}

String.prototype.trunc = String.prototype.trunc || function(n) {
	return this.length > n ? this.substr(0, n - 1) + '...' : this;
};

jQuery.fn.extend({
	getPath : function() {
		var path, node = this;
		while (node.length) {
			var realNode = node[0], name = realNode.localName;
			if (!name)
				break;
			name = name.toLowerCase();

			var parent = node.parent();

			var sameTagSiblings = parent.children(name);
			if (sameTagSiblings.length > 1) {
				allSiblings = parent.children();
				var index = allSiblings.index(realNode) + 1;
				if (index > 1) {
					name += ':nth-child(' + index + ')';
				}
			}

			path = name + (path ? '>' + path : '');
			node = parent;
		}

		return path;
	}
});

$(function() {
	MyPortal.init();
});
