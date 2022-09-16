		</div>
	</div>

	<div class="row top-buffer">
		<div class="panel-heading cas-heading bg-document-icon">
			<div class="title">Avvisi per utenti che utilizzano applicativo Tirocini</div>
		</div>
		<div class="panel-body cas-body">
			<ul>
				<%=new NotizieBean().getNotizie()%>
			</ul>
		</div>
	</div>



	</section>



<div id="container-fluid portalFooter">

	
	<div id="portal-colophon">
		<div class="colophonWrapper">
			<div class="shadow_wrapper_sx">
				<div class="shadow_wrapper_dx">

						Regione Calabria - Ufficio Relazioni con il Pubblico - Cittadella Regionale - 
						Viale Europa, Località Germaneto 88100 - Catanzaro. - 
						Numero verde 800 84 12 89<br />
					<a href="http://www.regione.calabria.it/">© 2016 Regione Calabria - P.IVA 02205340793</a>
				</div>
			</div>
		</div>
	</div>
	<div class="clearfix">
		<!-- -->
	</div>
</div>
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
					<span class="leftMargin">Dopo 3 tentativi falliti di login, il sistema blocca l'accesso dell'account per 3 minuti.</span>
				</li>
				<li>
					<span class="leftMargin">Per una corretta visualizzazione di tutte le funzionalita' del Portale, si consiglia di utilizzare uno
						dei seguenti browser: </span>
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
	<div class="ui-resizable-handle ui-resizable-se ui-icon ui-icon-gripsmall-diagonal-se ui-icon-grip-diagonal-se" style="z-index: 1001;"></div>
	<div class="ui-resizable-handle ui-resizable-sw" style="z-index: 1002;"></div>
	<div class="ui-resizable-handle ui-resizable-ne" style="z-index: 1003;"></div>
	<div class="ui-resizable-handle ui-resizable-nw" style="z-index: 1004;"></div>
	<div class="ui-dialog-buttonpane ui-widget-content ui-helper-clearfix">
		<div class="ui-dialog-buttonset">
			<button type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" role="button" aria-disabled="false">
				<span class="ui-button-text">Ok</span>
			</button>
		</div>
	</div>
</div>
</body>
</html>
