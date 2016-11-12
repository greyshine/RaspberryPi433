function request( inUrl, inCallback ) {
	$.ajax( {
		url: inUrl,
		async:false,
		success:function(inData) {
			for(var i in inData) {
				inCallback(i, inData[i] );
			}
		}
	} );
};

function App () {
	
	var that = this;
	var divButtons = new DivButtons( $('body div#buttons') );
	
	this.init = function() {
		
		divButtons.render();
		
		return that;
		
	};
	
	
};