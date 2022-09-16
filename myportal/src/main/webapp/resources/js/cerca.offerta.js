$(dataTableFiltri = function() {

$(".ui-datatable-scrollable-header").find("th").each(function( index ) {		  
	  if($(this).hasClass('ui-selection-column')) {
			$(this).hide();
			
		}
	});

$(".ui-datatable-scrollable-header").find("th").each(function( index ) {		  
	  if($(this).hasClass('ui-filter-column')) {
			$(this).find("input").css('width','80%').addClass('ricercaCosaInputText');
			
		}		
	  
});   

PrimeFaces.widget.DataTable.prototype.onRowClick = function (e,d,a){
	if($(e.target).is("td,span:not(.ui-c)")) {
		var g=$(d),
		c=g.hasClass("ui-state-highlight"),
		f=e.metaKey||e.ctrlKey,
		b=e.shiftKey;
		
		if(c){
			this.unselectRow(g,a);
		}
		else{
			if(this.isSingleSelection()||(this.isMultipleSelection()&&e&&!f&&!b)){
				/*this.unselectAllRows()*/
			}
			if(this.isMultipleSelection()&&e&&e.shiftKey){
				this.selectRowsInRange(g)
			}
			else{
				this.originRowIndex=g.index();
				this.cursorIndex=null;
				this.selectRow(g,a)
			}
		}
		PrimeFaces.clearSelection()
	}
}});

