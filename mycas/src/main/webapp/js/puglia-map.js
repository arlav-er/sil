$(document).ready(function() {
	$('#foggia').tooltip({
		trigger : 'hover',
		placement : 'right',
		title : 'Collegamento al sistema SINTESI di Foggia',
		container : 'body',
		boundary : 'viewport',
	});

	$('#foggia').on('click', function() {
		window.open('http://sintesi.provincia.foggia.it/portale/', "_blank");
	});

	$('#bat').tooltip({
		trigger : 'hover',
		placement : 'top',
		title : 'Collegamento al sistema SINTESI di Barletta-Andria-Trani',
		container : 'body',
		boundary : 'viewport',
	});

	$('#bat').on('click', function() {
		window.open('http://sintesi.provincia.bt.it/portale/', "_blank");
	});

	$('#bari').tooltip({
		trigger : 'hover',
		placement : 'top',
		title : 'Collegamento al sistema SINTESI di Bari',
		container : 'body',
		boundary : 'viewport',
	});

	$('#bari').on('click', function() {
		window.open('http://sintesi.cittametropolitana.ba.it/portale/', "_blank");
	});

	$('#taranto').tooltip({
		trigger : 'hover',
		placement : 'top',
		title : 'Collegamento al sistema SINTESI di Taranto',
		container : 'body',
		boundary : 'viewport',
	});

	$('#taranto').on('click', function() {
		window.open('http://sintesi.provincia.taranto.it/portale/', "_blank");
	});

	$('#brindisi').tooltip({
		trigger : 'hover',
		placement : 'top',
		title : 'Collegamento al sistema SINTESI di Brindisi',
		container : 'body',
		boundary : 'viewport',
	});

	$('#brindisi').on('click', function() {
		window.open('http://sintesi.provincia.brindisi.it/portale/', "_blank");
	});

	$('#lecce').tooltip({
		trigger : 'hover',
		placement : 'right',
		title : 'Collegamento al sistema SINTESI di Lecce',
		container : 'body',
		boundary : 'viewport',
	});

	$('#lecce').on('click', function() {
		window.open('http://sintesi.provincia.le.it/portale/', "_blank");
	});
});
