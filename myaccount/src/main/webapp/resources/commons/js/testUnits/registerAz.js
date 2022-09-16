/**
 * 
 */

function registerAzienda() {
	console.log('filling azienda');
	var username = 'azienda' + Math.floor((Math.random() * 10000) + 1);
	PrimeFaces.widgets.activeSAREWV.check();
	PrimeFaces.widgets.usernameWV.jq.val(username);
	PrimeFaces.widgets.passwordWV.jq.val('D1ff1c1l3');
	PrimeFaces.widgets.passwordRepeatWV.jq.val('D1ff1c1l3');
	PrimeFaces.widgets.secretQuestionWV.jq.val('What is your password ?');
	PrimeFaces.widgets.answerWV.jq.val('D1ff1c1l3');
	PrimeFaces.widgets.emailWV.jq.val(username + "@mailinator.com");
	PrimeFaces.widgets.emailConfirmWV.jq.val(username + "@mailinator.com");
	PrimeFaces.widgets.codiceFiscaleWV.jq.val("RSSMRA85T10A562S");
	PrimeFaces.widgets.ragioneSocialeWV.jq.val("Engineering");
	PrimeFaces.widgets.addressWV.jq.val("Via Marconi");
	PrimeFaces.widgets.capWV.jq.val("12345");
	//comuneWV	
	PrimeFaces.widgets.comuneWV.search('Bologna');
	setTimeout(function() {
		PrimeFaces.widgets.comuneWV.items.first().click()
	}, 3000);
	PrimeFaces.widgets.telefonoWV.jq.val("12345789");
	PrimeFaces.widgets.faxWV.jq.val("12345789");
	PrimeFaces.widgets.nomeRicWV.jq.val(username);
	PrimeFaces.widgets.cognomeRicWV.jq.val(username);
	PrimeFaces.widgets.dtDataNascitaRicWV.setDate('30/10/1996');	
	//comuneNascitaRicWV	
	PrimeFaces.widgets.comuneNascitaRicWV.search('bologna')
	setTimeout(function() {
		PrimeFaces.widgets.comuneNascitaRicWV.items.first().click()
	}, 3000);
	
	// other sections	
	setTimeout(function() {
		PrimeFaces.widgets.indirizzoRicWV.jq.val("Via Marconi");
		PrimeFaces.widgets.capRicWV.jq.val("12345");
		//comuneRichiedenteWV
		PrimeFaces.widgets.comuneRichiedenteWV.search('Bologna')
		setTimeout(function() {
			PrimeFaces.widgets.comuneRichiedenteWV.items.first().click()
		}, 3000);
		PrimeFaces.widgets.provRifWV.selectValue('37');
		PrimeFaces.widgets.mittenteSareWV.jq.val("mittente");
		PrimeFaces.widgets.referenteSareWV.jq.val('refer');
		PrimeFaces.widgets.telefonoReferenteWV.jq.val('13245678');
		PrimeFaces.widgets.emailReferenteWV.jq.val('no-reply@eng.it');
		PrimeFaces.widgets.tipoAbilitatoWV.select($('.ui-radiobutton-box').first())
		PrimeFaces.widgets.partitaIVAWV.jq.val('12345678901');
		
		PrimeFaces.widgets.indirizzoSedeLegaleWV.jq.val("Via Marconi");
		PrimeFaces.widgets.capSedeLegaleWV.jq.val("12345");
		//comuneWV	
		PrimeFaces.widgets.comuneSedeLegaleWV.search('Bologna');
		setTimeout(function() {
			PrimeFaces.widgets.comuneSedeLegaleWV.items.first().click()
			window.scrollTo(0,document.body.scrollHeight);
		}, 3000);
		PrimeFaces.widgets.telefonoSedeLegaleWV.jq.val("12345789");
		PrimeFaces.widgets.faxSedeLegaleWV.jq.val("12345789");
		
	}, 4000);
		
	PrimeFaces.widgets.acceptTermsWV.check();	
	$('.panel-footer').append('<a target="_blank" href="'+ "http://mailinator.com/inbox.jsp?to="+ username + '">Go to Email </a>');
	
}