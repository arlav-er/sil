/*
 * UserEdit object
 */

(function(UserEdit, $, undefined) {

	UserEdit.iti;
	UserEdit.init = function() {

	};

	UserEdit.generateOTPRequest = function() {
		PF('inviaCodiceBtnWV').disable();

		generateOTPRichiestaRC([ {
			name : 'tempCellurareFirmaOTP',
			value : UserEdit.iti.getNumber()
		} ])
	}

	UserEdit.generateOTPRequestComplete = function() {
		try {
			setTimeout(function() {
				initIntlInput();	
			}, 200)
			PF('inviaCodiceBtnWV').enable();
		} catch (e) {
			console.log(e)
		}

	}

	UserEdit.replyToOTP = function() {
		PF('confermaCodiceBtnWV').disable();
	}

	UserEdit.replyToOTPComplete = function(args) {
		PF('confermaCodiceBtnWV').enable();

		if (args && args.otpSuccess === "success") {
			resetOTPRC();
			PF('cellulareFirmaOTPWV').hide();
			sucessMessage("Cellulare per firma elettronica con verifica OTP registrato con successo")
		} else if (args && args.otpSuccess === "wrongPass") {
			resetOTPRC();
			PF('cellulareFirmaOTPWV').hide();
			dangerMesssage("Il codice inserito e` errato, si prega di riprovare")
		}else if (args && args.otpSuccess === "scaduto") {
			resetOTPRC();
			PF('cellulareFirmaOTPWV').hide();
			dangerMesssage("Questa password e` scaduta, si prega di riprovare")
		}else if (args && !args.validationFailed) {
			dangerMesssage("Errore nel servizio recapito SMS, si prega di riprovare")
		}
	}
}(window.UserEdit = window.UserEdit || {}, jQuery));

$(function() {
	UserEdit.init();
});
/**
 * read state params
 */
