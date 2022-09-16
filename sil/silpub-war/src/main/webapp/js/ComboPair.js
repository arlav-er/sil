
function ComboPair(selectParent, selectChild, optsChild, addBlank, defaultSelected){
	this.parent= selectParent;
	this.child= selectChild;	
	this.childs=optsChild;	
	this.addBlank=addBlank;
	this.defaultSelected=defaultSelected;
	this.populate= makeSelectChild;
	this.resetSelectChild= resetSelectChild;
	this.addOption=addOption;
	this.addEmptyOption=addEmptyOption;		
	this.DEBUG=false;
}


function makeSelectChild(parentValue, childValue){
	this.resetSelectChild();		
	if(this.addBlank) {
		this.addEmptyOption(arguments.length==0);		
	}
	if (arguments.length==0 )
		parentValue=this.parent.options[this.parent.selectedIndex].value;
	for(i=0;i<this.childs.length;i++) {
		if (this.DEBUG) alert(this.childs[i].parent+"<"+this.childs[i].value+"><"+childValue+">"+codSelected);
		if (this.childs[i].parent==parentValue) {
   			this.addOption(this.childs[i]);	
			if (arguments.length>0 && this.childs[i].value==childValue) {
				this.childs[i].selected=true;				
			}
		}
		else this.childs[i].selected=false;
	}	
}

function addOption(optObj){
	index = this.child.options.length;
	this.child.options[index]=optObj;	
}
function resetSelectChild(){	
	len=this.child.length;
	for(i=0;i<len;i++) {
		this.child.options[0]=null;	
	}
}

function addEmptyOption(selected){
	this.child.options[0]=new Option("","", selected);	
}
