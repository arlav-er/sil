function resizeProgressBar(idElement, value) {
	var bar = document.getElementById(idElement);	
	try{
		value = value.replace(',', '.');
		var floatValue = parseFloat(value);
		if(floatValue > 0){
			floatValue = floatValue/10;
			bar.style.width = ((floatValue * 250) + 50) + "px";
		}else{
			bar.style.width = "0px";
		} 
	}catch(xxx){
		bar.style.width =	"0px";	
	}
	
};