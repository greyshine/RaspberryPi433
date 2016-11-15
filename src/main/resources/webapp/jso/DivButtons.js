function DivButtons($inParent) {

	var buttons = [];
		
	this.render = function() {
	
		$inParent.children().remove();
		buttons = [];
	
		request('/switches', function(inKey, inSwitch) {
			
			var $divLaststate = $('<div id="laststate-'+ inSwitch.id +'" style="margin-top:10px;color:silver; text-align:center;"></div>');
			
			$inParent.append('<h1>'+ inSwitch.name +'</h1>');
			
			var click = function(inEvt) {
				
				console.log('send '+ $(inEvt.target).attr('data-code'));
				
				$(inEvt.target).removeClass('bgYellow');
				$(inEvt.target).addClass('bgYellow');
				
				$.ajax( {
					url:'/code/'+ $(inEvt.target).attr('data-code') ,
					method:'PUT',
					asyn:false,
					success:function(inData) {
						
						$divLaststate.html( inData.message );
					},
					error:function(inData) {
						
						$divLaststate.html( 'fail' );
					},
					complete:function() {
						$(inEvt.target).removeClass('bgYellow');
					}
				} );
			};
			
			$inParent.append( $('<button class="on" data-code="'+ inSwitch['code-0-id'] +'" >ON<br/>'+ inSwitch['code-0'] +'</button>').click( click ) );
			$inParent.append( $('<button class="off" data-code="'+ inSwitch['code-1-id'] +'" >OFF<br/>'+ inSwitch['code-1']+'</button>').click( click ) );
			
			$inParent.append( $divLaststate );
			
		} );
		
		
	};
};