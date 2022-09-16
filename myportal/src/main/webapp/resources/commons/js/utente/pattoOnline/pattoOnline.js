/**
 * PattoOnline
 * 
 * @author: hatemalimam and his friend shine
 * @date: 14/05/2020
 */

var otpTimeoutInterval = function(args) {
	return setInterval(function() {
		// Get today's date and time
		var countDownDate = Number(args.scadenza);
		var now = new Date().getTime();

		// Find the distance between now and the count down date
		var distance = countDownDate - now;

		// Time calculations for days, hours, minutes and seconds
		var days = Math.floor(distance / (1000 * 60 * 60 * 24));
		var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
		var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
		var seconds = Math.floor((distance % (1000 * 60)) / 1000);

		if (days > 0)
			hours = hours + (days * 24);
		seconds = seconds.toString().length < 2 ? "0" + seconds : seconds;

		$('.countDown').html(hours + ':' + minutes + ':' + seconds);
		document.title = hours + ':' + minutes + ':' + seconds + " Scadenza codice OTP";
		if (distance < 0) {
			clearInterval(otpTimeoutIntervalSpan);
			// TODO what shall we do in case the otp is expired ?
		}
	}, 1000);
}

var requestOtpTimeout = function() {
	$(document).on('show.bs.modal', '.modal', centerModal);
	$(window).on("resize", function() {
		$('.modal:visible').each(centerModal);
	});

	$('#otp-timeout').modal("show");
}

var otpTimeout = function(args) {
	var currentTitle = document.title;
	var otpTimeoutIntervalSpan = otpTimeoutInterval(args);

	$('#otp-timeout').on('hidden.bs.modal', function() {
		clearInterval(otpTimeoutIntervalSpan);
		document.title = currentTitle;
		clearOTPRC();
	});
}

var nootpTimeout = function(args) {
	var currentTitle = document.title;

	$(document).on('show.bs.modal', '.modal', centerModal);
	$(window).on("resize", function() {
		$('.modal:visible').each(centerModal);
	});

	$('#no-otp-timeout').modal("show");

	$('#no-otp-timeout').on('hidden.bs.modal', function() {
		clearInterval(otpTimeoutIntervalSpan);
		document.title = currentTitle;
	});

	var otpTimeoutIntervalSpan = otpTimeoutInterval(args);
}

var requestSmsTimeout = function() {
	$(document).on('show.bs.modal', '.modal', centerModal);
	$(window).on("resize", function() {
		$('.modal:visible').each(centerModal);
	});

	$('#sms-otp-timeout').modal("show");
}

var requestOTPandSMS = function() {
	$(document).on('show.bs.modal', '.modal', centerModal);
	$(window).on("resize", function() {
		$('.modal:visible').each(centerModal);
	});

	$('#pre-sms-otp').modal("show");
}

var closeOTPandSMS = function() {
	$('#pre-sms-otp').modal("hide");
}

var smsTimeout = function(args) {
	var currentTitle = document.title;
	var otpTimeoutIntervalSpan = otpTimeoutInterval(args);

	$('#sms-otp-timeout').on('hidden.bs.modal', function() {
		clearInterval(otpTimeoutIntervalSpan);
		document.title = currentTitle;
		clearOTPRC();
	});
}

var smsotpTimeout = function(args) {
	var currentTitle = document.title;

	$(document).on('show.bs.modal', '.modal', centerModal);
	$(window).on("resize", function() {
		$('.modal:visible').each(centerModal);
	});

	$('#sms-otp-timeout').modal("show");

	$('#sms-otp-timeout').on('hidden.bs.modal', function() {
		clearInterval(otpTimeoutIntervalSpan);
		document.title = currentTitle;
	});

	var otpTimeoutIntervalSpan = otpTimeoutInterval(args);
}

var storicoPatto = function(args) {
	var currentTitle = document.title;

	$(document).on('show.bs.modal', '.modal', centerModal);
	$(window).on("resize", function() {
		$('.modal:visible').each(centerModal);
	});

	$('#storico-patto-sil-modal').modal("show");

	$('#storico-patto-sil-modal').on('hidden.bs.modal', function() {
		document.title = currentTitle;
	});
}

var centerModal = function() {
	$(this).css('display', 'block');
	var $dialog = $(this).find(".modal-dialog"), offset = ($(window).height() - $dialog.height()) / 2, bottomMargin = parseInt(
			$dialog.css('marginBottom'), 10);
	if (offset < bottomMargin)
		offset = bottomMargin;
	$dialog.css("margin-top", offset);
}

var codiceOTPErrato = function() {
	$('.modal').modal('hide');
	swal({
		title : "Codice OTP errato",
		text : "Il codice inserito non e` valido",
		type : "error",

	});
}

var codiceOTPSuccess = function() {
	$('.modal').modal('hide');
	swal({
		title : "Complimenti, accettazione del patto/accordo inviata al CPI",
		type : "success",
		confirmButtonColor : "#c82b2e",
	});

	refreshRC();
}

var silNonDisponibile = function() {
	$('.modal').modal('hide');
	swal({
		title : "SIL non disponibile",
		type : "error",
		confirmButtonColor : "#c82b2e",
	});
}
var showErrMessage = function(msg) {
	$('.modal').modal('hide');
	swal({
		title : "Errore invio accettazione patto/accordo",
		text : msg,
		type : "error",
		confirmButtonColor : "#c82b2e",
	});
}

var blackholeTimeout = function(args) {
	$(document).on('show.bs.modal', '.modal', centerModal);
	$(window).on("resize", function() {
		$('.modal:visible').each(centerModal);
	});

	$('#black-hole').modal("show");

	var currentTitle = document.title;
	var otpTimeoutIntervalSpan = otpTimeoutInterval(args);

	$('#black-hole').on('hidden.bs.modal', function() {
		clearInterval(otpTimeoutIntervalSpan);
		document.title = currentTitle;
	});
}

/**
 * END PattoOnline
 */
