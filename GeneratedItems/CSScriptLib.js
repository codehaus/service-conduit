/* -- Adobe GoLive JavaScript Library */


function newImage(arg) {
	if (document.images) {
		rslt = new Image();
		rslt.src = arg;
		return rslt;
	}
}

function changeImagesArray(array) {
	if (preloadFlag == true) {
		var d = document; var img;
		for (var i=0; i<array.length; i+=2) {
			img = null; var n = array[i];
			if (d.images) {img = d.images[n];}
			if (!img && d.getElementById) {img = d.getElementById(n);}
			if (img) {img.src = array[i+1];}
		}
	}
}

function changeImages() {
	changeImagesArray(changeImages.arguments);
}