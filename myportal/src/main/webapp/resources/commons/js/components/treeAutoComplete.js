/**
 * TreeAutoComplete Component
 */
Eng.widget.TreeAutoComplete = PrimeFaces.widget.BaseWidget.extend({
	init : function(cfg) {
		this._super(cfg);
		this.cleanState();

		this.treeContainerId = PrimeFaces.escapeClientId(this.id + "_tree_container");
		this.searchBtnId = PrimeFaces.escapeClientId(this.id + "_search_btn");
		this.treeContainer = $(this.treeContainerId);
		this.searchBtn = $(this.searchBtnId);
		this.autoComplete = PF(cfg.autoCompleteWV);
		this.tree = PF(cfg.treeWV);
		this.autoComplete.alignPanel();
		this.autoComplete.showSuggestions();
		this.treePanel = this.autoComplete.panel.clone().appendTo('body').show();
		this.treePanel.attr('id', this.id + "treePanel");
		this.autoComplete.panel.hide();
		this.treePanel.hide();
		this.treeContainer.hide();
		this.treePanel.empty()
		this.treeContainer.find('.ui-tree').prependTo(this.treePanel);
		this.bindEvents();
	},
	bindEvents : function() {
		var _self = this;

		$(document).on('click', _self.searchBtnId, function(event) {
			_self.autoComplete.alignPanel();
			_self.treePanel.attr('style', _self.autoComplete.panel.attr('style')).css('height', '200px');
			_self.treeReset();
			_self.treePanel.show();
			_self.tree.jq.focus();

			$('html').one('click', function(e) {
				if (_self.treePanel.is(":visible") && e.target != _self.treePanel)
					_self.treePanel.hide();
			});

			$(_self.treePanel).click(function(event) {
				event.stopPropagation();
			});

			event.stopPropagation();
		})

		_self.tree.jq.keyup(function(e) {
			var keyCode = $.ui.keyCode;
			key = e.which;
			if (key === keyCode.ESCAPE && _self.treePanel.is(":visible")) {
				_self.treePanel.hide();
				_self.autoComplete.input.focus();
			}
		});

	},

	selectionComplete : function() {
		var _self = this;
		_self.treePanel.hide();
	},

	treeReset : function() {
		var _self = this;
		_self.tree.collapseNode(_self.tree.jq.find('li.ui-treenode-parent'));
	},

	cleanState : function() {
		// if we update the component the previouse state should be cleaned.
		$(PrimeFaces.escapeClientId(this.id + "treePanel")).remove();
	}

});
