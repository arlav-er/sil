/**
 * @namespace Eng namespace.
 */

(function(Eng, $, undefined) {

	Eng.cw = function(widgetName, widgetVar, cfg) {
		createWidget(widgetName, widgetVar, cfg);
	}

	function createWidget(widgetName, widgetVar, cfg) {
		if (Eng.widget[widgetName]) {
			initWidget(widgetName, widgetVar, cfg);
		}
	}

	function initWidget(widgetName, widgetVar, cfg) {
		if (PrimeFaces.widgets[widgetVar]) {
			PrimeFaces.widgets[widgetVar].refresh(cfg);
		} else {
			PrimeFaces.widgets[widgetVar] = new Eng.widget[widgetName](cfg);
			if (PrimeFaces.settings.legacyWidgetNamespace) {
				window[widgetVar] = PrimeFaces.widgets[widgetVar];
			}
		}
	}

	/**
	 * @namespace Namespace for widgets.
	 */
	Eng.widget = {};

}(window.Eng = window.Eng || {}, jQuery));
