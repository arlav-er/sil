<div class="row top-buffer">
	<div class="panel-heading cas-heading bg-document-icon">
		<div class="title">Avvisi per utenti che utilizzano l'applicativo SARE/Tirocini</div>
	</div>
	<div class="panel-body cas-body">
		<ul>
			<%=new NotizieBean().getNotizie()%>
		</ul>
	</div>
</div>



</section>
<footer id="footer" class="app-footer" role="complementary">
	<div class="center-text">
		<a href="http://www.regione.vda.it/default_i.asp">
			<img src="/MyCas/images/vda/footer-logo.jpg" width="186" height="48"
				alt="Sito ufficiale della Regione Autonoma Valle d'Aosta">
		</a>
	</div>
	<div class="footer-links">
		<ul>
			<li>
				<a href="http://appweb.regione.vda.it/DBWeb/Contatti.nsf/vedicontatti?OpenForm">Contatti</a>
			</li>
			<li>
				<a href="http://www.regione.vda.it/informazioni_utili/pec_i.aspx">Posta certificata</a>
			</li>
			<li>
				<a href="http://www.regione.vda.it/informazioni_utili/migliorare_i.asp">Aiutaci a migliorare</a>
			</li>

			<li>
				<a href="http://www.regione.vda.it/informazioni_utili/privacy_i.asp" title="Informativa Privacy">Privacy</a>
			</li>
			<li>
				<a href="http://www.regione.vda.it/informazioni_utili/cookies_i.aspx" title="Informativa sui cookie">Cookie</a>
			</li>
			<li>
				<a href="http://www.regione.vda.it/informazioni_utili/note_legali_i.asp" title="Note legali">Note legali</a>
			</li>
			<li class="last">
				<a href="http://www.regione.vda.it/intranet/default_i.asp">Intranet</a>
			</li>
		</ul>
	</div>
	<div class="footer-copyright">
		Pagina a cura dei
		<a href="http://www.regione.vda.it/amministrazione/struttura/">Sistemi informativi</a>
		© 2017 Regione Autonoma Valle d'Aosta
		<br />
		Piazza Deffeyes 1 - 11100 Aosta - Tel. +39 0165 273111 p.iva 00368440079
	</div>
</footer>
<!-- FOOTER END -->
<div class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-draggable ui-resizable" tabindex="-1" role="dialog"
	aria-labelledby="ui-dialog-title-dialog"
	style="display: none; z-index: 1002; outline: 0px; position: absolute; height: auto; width: 50%; top: 326px; left: 473px;">
	<div class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix">
		<span class="ui-dialog-title" id="ui-dialog-title-dialog">informazioni</span>
		<a href="#" class="ui-dialog-titlebar-close ui-corner-all" role="button">
			<span class="ui-icon ui-icon-closethick">close</span>
		</a>
	</div>
	<div id="dialog" class="ui-dialog-content ui-widget-content" scrolltop="0" scrollleft="0"
		style="width: auto; min-height: 55.2992000579834px; height: auto;">
		<div class="home-dialog-info-content">
			<ul>
				<li>
					<span class="leftMargin">Dopo 3 tentativi falliti di login, il sistema blocca l'accesso dell'account per 3
						minuti.</span>
				</li>
				<li>
					<span class="leftMargin">Per una corretta visualizzazione di tutte le funzionalita' del Portale, si
						consiglia di utilizzare uno dei seguenti browser: </span>
					<ul class="browser-list">
						<li>
							<i>
								<span class="leftMargin">Internet Explorer dalla versione 8</span>
							</i>
						</li>
						<li>
							<i>
								<span class="leftMargin">Firefox dalla versione 7</span>
							</i>
						</li>
						<li>
							<i>
								<span class="leftMargin">Google Chrome dalla versione 12</span>
							</i>
						</li>
						<li>
							<i>
								<span class="leftMargin">Safari dalla versione 5</span>
							</i>
						</li>
						<li>
							<i>
								<span class="leftMargin">Opera dalla versione 12</span>
							</i>
						</li>
					</ul>
				</li>
			</ul>
		</div>
	</div>
	<div class="ui-resizable-handle ui-resizable-n"></div>
	<div class="ui-resizable-handle ui-resizable-e"></div>
	<div class="ui-resizable-handle ui-resizable-s"></div>
	<div class="ui-resizable-handle ui-resizable-w"></div>
	<div class="ui-resizable-handle ui-resizable-se ui-icon ui-icon-gripsmall-diagonal-se ui-icon-grip-diagonal-se"
		style="z-index: 1001;"></div>
	<div class="ui-resizable-handle ui-resizable-sw" style="z-index: 1002;"></div>
	<div class="ui-resizable-handle ui-resizable-ne" style="z-index: 1003;"></div>
	<div class="ui-resizable-handle ui-resizable-nw" style="z-index: 1004;"></div>
	<div class="ui-dialog-buttonpane ui-widget-content ui-helper-clearfix">
		<div class="ui-dialog-buttonset">
			<button type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" role="button"
				aria-disabled="false">
				<span class="ui-button-text">Ok</span>
			</button>
		</div>
	</div>
</div>
</body>
</html>
