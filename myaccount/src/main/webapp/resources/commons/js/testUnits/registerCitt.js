/**
 * 
 */

function registerCittWithoutServAmini() {
	console.log('filling citt');
	var username = 'user' + Math.floor((Math.random() * 10000) + 1);
	PrimeFaces.widgets.usernameWV.jq.val(username);
	PrimeFaces.widgets.passwordWV.jq.val('D1ff1c1l3');
	PrimeFaces.widgets.passwordRepeatWV.jq.val('D1ff1c1l3');
	PrimeFaces.widgets.secretQuestionWV.jq.val('What is your password ?');
	PrimeFaces.widgets.answerWV.jq.val('D1ff1c1l3');
	PrimeFaces.widgets.emailWV.jq.val(username + "@mailinator.com");
	PrimeFaces.widgets.emailConfirmWV.jq.val(username + "@mailinator.com");
	PrimeFaces.widgets.nomeWV.jq.val(username);
	PrimeFaces.widgets.cognomeWV.jq.val(username);
	// comuneNascitaWV
	PrimeFaces.widgets.comuneNascitaWV.search('Bologna')
	setTimeout(function() {
		PrimeFaces.widgets.comuneNascitaWV.items.first().click()
	}, 3000);
	// comuneDomicilioWV
	PrimeFaces.widgets.comuneDomicilioWV.search('Bologna')
	setTimeout(function() {
		PrimeFaces.widgets.comuneDomicilioWV.items.first().click()
	}, 3000);
	setTimeout(function() {
		PrimeFaces.widgets.enableServAmiCheckWV.check()
	}, 4000);

	setTimeout(function() {
		PrimeFaces.widgets.emailPecWV.jq.val(username + "@mailinator.it");
		PrimeFaces.widgets.codiceFiscaleWV.jq.val("RSSMRA85T10A562S");
		PrimeFaces.widgets.dataNascitaWV.setDate('30/10/1996');
		// cittadinanzaWV
		PrimeFaces.widgets.cittadinanzaWV.search('Italiana');
		setTimeout(function() {
			PrimeFaces.widgets.cittadinanzaWV.items.first().click();
			setTimeout(function() {
				PrimeFaces.widgets.documentoIdentitaWV.jq.val(Math.floor((Math.random() * 100000) + 1))
				PrimeFaces.widgets.numeroDocumentoWV.jq.val(Math.floor((Math.random() * 100000) + 1))
				PrimeFaces.widgets.dataScadenzaWV.setDate('30/10/2014')
				window.scrollTo(0,document.body.scrollHeight);
			}, 2000);
		}, 3000);
	}, 6000);

	PrimeFaces.widgets.acceptTermsWV.check();	
	$('.panel-footer').append('<a target="_blank" href="'+ "http://mailinator.com/inbox.jsp?to="+ username + '">Go to Email </a>');
	
}