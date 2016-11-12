function DivButtons($inParent) {

	var buttons = [];
		
	this.render = function() {
	
		$inParent.children().remove();
		buttons = [];
	
		request('/switches', function(inKey, inSwitch) {
			
			$inParent.append('<h1>'+ inKey +'</h1>');
			
			var click = function(inEvt) {
				console.log('send '+ $(inEvt.target).attr('data-code'));
				$.ajax( {
					url:'/code/'+ $(inEvt.target).attr('data-code') ,
					method:'PUT',
					asyn:false
				} );
			};
			
			$inParent.append( $('<button class="on" data-code="'+ inSwitch['code-0-id'] +'" >ON<br/>'+ inSwitch['code-0'] +'</button>').click( click ) );
			$inParent.append( $('<button class="off" data-code="'+ inSwitch['code-1-id'] +'" >OFF<br/>'+ inSwitch['code-1']+'</button>').click( click ) );
			
		} );
		
		
	};
};