(function($){
	$(document).ready(function(){
		$(".deleteObject").bind("click.gripes",function(ev){
			ev.preventDefault();
			if(confirm("Are you sure you wish to delete this object?")){
				window.location = this.href
			}
		})
	})
})(jQuery);