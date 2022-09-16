
var DIR_IMAGES = "../../img/";

var IMG_PLUS  = DIR_IMAGES + "btnPlus.gif";
var IMG_MINUS = DIR_IMAGES + "btnMinus.gif";
var IMG_DOC   = DIR_IMAGES + "i_p.gif";
var IMG_BLANK   = DIR_IMAGES + "b.gif";


var SPAZIO =     "<img src='" + IMG_BLANK + "' width=9 height=9>";
var SPAZIO_DOC = "<img src='" + IMG_DOC + "'>";

var imgPlus = new Image();
imgPlus.src = IMG_PLUS;
var imgMinus = new Image();
imgMinus.src = IMG_MINUS;

var imgDoc = new Image();
imgDoc.src = IMG_DOC;

var imgBlank = new Image();
imgBlank.src = IMG_BLANK;



var objLocalTree = null;

var INDENT_WIDTH = 8;

//-----------------------------------------------------------------
// Class jsTree
//-----------------------------------------------------------------
// Author(s)
//  Nicholas C. Zakas (NCZ), 1/27/02
//  ranco Vuoto 12/09/2003 16.58
//
// Description
//  The jsTreeNode class encapsulates the functionality of a tree.
//
// Parameters
//  (none)
//-----------------------------------------------------------------
function jsTree() {

    //Public Properties (NCZ, 1/27/02)
    this.root = null;           //the root node of the tree
 
     //Public Collections (NCZ, 1/27/02)
    this.nodes = new Array;     //array for all nodes in the tree
   
    //Constructor
    //assign to local copy of the tree (NCZ, 1/27/02)
    objLocalTree = this;
}

//-----------------------------------------------------------------
// Method jsTree.createRoot()
//-----------------------------------------------------------------
// Author(s)
//  Nicholas C. Zakas (NCZ), 1/27/02
//
// Description
//  This method creates the root of the tree.
//
// Parameters
//  strLivello (string) - the icon to display for the root.
//  strText (string) - the text to display for the root.
//  strURL (string) - the URL to navigate to when the root is clicked.
//  strTarget (string) - the target for the URL (optional).
//
// Returns
//  The jsTreeNode that was created.
//-----------------------------------------------------------------
jsTree.prototype.createRoot = function(strLivello, strText, strURL, strTarget) {

    this.root = new jsTreeNode(strLivello, strText, strURL, strTarget);
    this.root.id = "root";
    this.nodes["root"] = this.root;
    this.root.expanded = true;
    return this.root;
}

//-----------------------------------------------------------------
// Method jsTree.buildDOM()
//-----------------------------------------------------------------
// Author(s)
//  Nicholas C. Zakas (NCZ), 1/27/02
//
// Description
//  This method creates the HTML for the tree.
//
// Parameters
//  (none)
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTree.prototype.buildDOM = function() {

    //add all other nodes (NCZ, 1/27/02)
    this.root.addToDOM(document.body);
}

//-----------------------------------------------------------------
// Method jsTree.toggleExpand()
//-----------------------------------------------------------------
// Author(s)
//  Nicholas C. Zakas (NCZ), 1/27/02
//
// Description
//  This toggles the expansion of a node identified by an ID.
//
// Parameters
//  strNodeID (string) - the ID of the node that is being expanded/collapsed.
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTree.prototype.toggleExpand = function(strNodeID) {

    //get the node (NCZ, 1/27/02)
    var objNode = this.nodes[strNodeID];
	// modifica Andrea (22/11/2004)
	if (strNodeID=='root_0' && objNode.childNodes.length==0)
		return;    
    //determine whether to expand or collapse
    if (objNode.expanded)
        objNode.collapse();
    else
        objNode.expand();
}

//-----------------------------------------------------------------
// Class jsTreeNode
//-----------------------------------------------------------------
// Author(s)
//  Nicholas C. Zakas (NCZ), 1/27/02
//
// Description
//  The jsTreeNode class encapsulates the basic information for a node
//  in the tree.
//
// Parameters
//  strLivello (string) - the icon to display for this node.
//  strText (string) - the text to display for this node.
//  strURL (string) - the URL to navigate to when this node is clicked.
//  strTarget (string) - the target for the URL (optional).
//-----------------------------------------------------------------
function jsTreeNode(strLivello, strText, strURL, strTarget) {

    //Public Properties (NCZ, 1/27/02)
    this.livello = strLivello;            //the icon to display
    this.text = strText;            //the text to display
    this.url = strURL;              //the URL to link to
    this.target = strTarget;        //the target for the URL
    
    //Private Properties (NCZ, 1/27/02)
    // FV coincide col livello 
    this.indent = 0;                //the indent for the node
    
    //Public States (NCZ, 1/27/02)
    this.expanded = false;          //is this node expanded?
 
    //Public Collections (NCZ, 1/27/02)   
    this.childNodes = new Array;    //the collection of child nodes
}

//-----------------------------------------------------------------
// Method jsTreeNode.addChild()
//-----------------------------------------------------------------
// Author(s)
//  Nicholas C. Zakas (NCZ), 1/27/02
//
// Description
//  This method adds a child node to the current node.
//
// Parameters
//  strLivello (string) - the icon to display for this node.
//  strText (string) - the text to display for this node.
//  strURL (string) - the URL to navigate to when this node is clicked.
//  strTarget (string) - the target for the URL (optional).
//
// Returns
//  The jsTreeNode that was created.
//-----------------------------------------------------------------
jsTreeNode.prototype.addChild = function (strLivello, strText, strURL, strTarget) {

    //create a new node (NCZ, 1/27/02)
    var objNode = new jsTreeNode(strLivello, strText, strURL, strTarget);
    
    //assign an ID for internal tracking (NCZ, 1/27/02)
    objNode.id = this.id + "_" + this.childNodes.length;
    
    //assign the indent for this node
    objNode.indent = this.indent + 1;
    
    //add into the array of child nodes (NCZ, 1/27/02)
    this.childNodes[this.childNodes.length] = objNode;
    
    //add it into the array of all nodes (NCZ, 1/27/02)
    objLocalTree.nodes[objNode.id] = objNode;
    
    //return the created node (NCZ, 1/27/02)
    return objNode;
}

//-----------------------------------------------------------------
// Method jsTreeNode.addToDOM()
//-----------------------------------------------------------------
// Author(s)
//  Nicholas C. Zakas (NCZ), 1/27/02
//
// Description
//  This method adds DOM elements to a parent DOM element.
//
// Parameters
//  objDOMParent (HTMLElement) - the parent DOM element to add to.
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTreeNode.prototype.addToDOM = function (objDOMParent) {

    //create the layer for the node (NCZ, 1/27/02)
    var objNodeDiv = document.createElement("div");
    
    //add it to the DOM parent element (NCZ, 1/27/02)
    objDOMParent.appendChild(objNodeDiv);
    
    //create string buffer (NCZ, 1/27/02)
    var d = new jsDocument;
    
    //begin the table (NCZ, 1/27/02)
    d.writeln("<table border=\"0\" cellpadding=\"0\" cellspacing=\"2\"><tr>");
    
    //no indent needed for root or level under root (NCZ, 1/27/02)
//    if (this.indent > 1) {
//        d.write("<td>");
//        d.write(indenta(this.indent));    
//        d.write("</td>");
//    }
    
    //there is no plus/minus image for the root (NCZ, 1/27/02)
    if (this.indent > 0) {
    
        d.write("<td>");
        d.write(indenta(this.indent));    
       
        //if there are children, then add a plus/minus image (NCZ, 1/27/02)
        if (this.childNodes.length > 0) {
            d.write("<a href=\"javascript:objLocalTree.toggleExpand('");
            d.write(this.id);
            d.write("')\"><img src=\"");
            d.write(this.expanded ? imgMinus.src : imgPlus.src);
            d.write("\" border=\"0\"  id=\"");
            d.write("imgPM_" + this.id);
            d.write("\" /></a>");
        }
        
        d.write("</td>");
       
    }
     
    //FV 02/09/2003 18.24
    
    d.write("<td class=menu nowrap=\"nowrap\" >");

    if (this.livello == "1"){
       d.write("<td class=menu width='100%'>");
       d.write( "<a class=menu "); 
       
        d.write("href=\"javascript:objLocalTree.toggleExpand('");
        d.write(this.id);
        d.write("')\"\>");
           
       d.write( this.text + "</a></td>");
    }
    else {
        d.write("<td>");

         if (this.childNodes.length > 0 && (this.indent>0)) {
            d.write( "<b>"); 

            if ( this.url.length > 0 )
              d.write("<a href=\"" + this.url + "\" target=\"" + this.target + "\">");

            d.write( SPAZIO_DOC );
            d.write( this.text);

            if ( this.url.length > 0 )
              d.write("</a>");

            d.write( "</b>"); 

        /*    d.write( "<b>"); 
            d.write("<a href=\"javascript:objLocalTree.toggleExpand('");
            d.write(this.id);
            d.write("')\"\>");
            d.write( this.text + "</a>");
            d.write( "</b>"); */

         
        } else if (this.indent>0) {
            if ( this.url.length > 0 )
              d.write( "<a href=\"" + this.url + "\" target=\"" + this.target + "\">");
            
            d.write( SPAZIO_DOC );
            d.write( this.text);
            
            if ( this.url.length > 0 )
              d.write("</a>");
        }

    }
    
   
    d.writeln("</td></tr></table>");
         
    //assign the HTML to the layer (NCZ, 1/27/02)
    objNodeDiv.innerHTML = d;
    
    //create the layer for the children (NCZ, 1/27/02)
    var objChildNodesLayer = document.createElement("div");
    objChildNodesLayer.setAttribute("id", "divChildren_" + this.id);
    objChildNodesLayer.style.position = "relative";
    objChildNodesLayer.style.display = (this.expanded ? "block" : "none");
    objNodeDiv.appendChild(objChildNodesLayer);
    
    //call for all children (NCZ, 1/27/02)
    for (var i=0; i < this.childNodes.length; i++)
        this.childNodes[i].addToDOM(objChildNodesLayer);
}

//-----------------------------------------------------------------
// Method jsTreeNode.collapse()
//-----------------------------------------------------------------
// Author(s)
//  Nicholas C. Zakas (NCZ), 1/27/02
//
// Description
//  This method expands the jsTreeNode's children to be hidden.
//
// Parameters
//  (none)
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTreeNode.prototype.collapse = function () {

    //check to see if the node is already collapsed (NCZ, 1/27/02)
    if (!this.expanded) {
    
        //throw an error (NCZ, 1/27/02)
        throw "Node is already collapsed"

    } else {
    
        //change the state of the node (NCZ, 1/27/02)
        this.expanded = false;
        
        //change the plus/minus image to be plus (NCZ, 1/27/02)
        document.images["imgPM_" + this.id].src = imgPlus.src;
        
        //hide the child nodes (NCZ, 1/27/02)
        document.getElementById("divChildren_" + this.id).style.display = "none";
    }
}


//-----------------------------------------------------------------
// Method jsTreeNode.expand()
//-----------------------------------------------------------------
// Author(s)
//  Nicholas C. Zakas (NCZ), 1/27/02
//
// Description
//  This method expands the jsTreeNode's children to be displayed.
//
// Parameters
//  (none)
//
// Returns
//  (nothing)
//-----------------------------------------------------------------
jsTreeNode.prototype.expand = function () {

    //check to see if the node is already expanded (NCZ, 1/27/02)
    if (this.expanded) {
    
        //throw an error (NCZ, 1/27/02)
        alert("Node is already expanded")
        throw "Node is already expanded"
    
    } else {
    
        //change the state of the node (NCZ, 1/27/02)
        this.expanded = true;
        
        //change the plus/minus image to be minus (NCZ, 1/27/02)
        document.images["imgPM_" + this.id].src = imgMinus.src;
        
        //show the child nodes (NCZ, 1/27/02)
        document.getElementById("divChildren_" + this.id).style.display = "block";
    }
}



jsTree.prototype.espandiMenuLav = function(idMenu) {

    //call method to add root to document, which will recursively
    //add all other nodes (NCZ, 1/27/02)
    var nodoLav = objLocalTree.nodes[idMenu];
    
    this.espandiAll(nodoLav);
}



jsTree.prototype.espandiAll = function(nodo) {

    if (nodo.childNodes.length>0){
        nodo.expand()

        for (var i=0; i<nodo.childNodes.length; i++){
            this.espandiAll(nodo.childNodes[i])            
        }
    }    
    
}



// FV 12/09/2003 17.00
function indenta(volte){
 var n = 2 * (volte -1)
 var buf ="";
 for(var i=0; i< n; i++){
       buf+=SPAZIO;
 }   
 return buf;
 
}