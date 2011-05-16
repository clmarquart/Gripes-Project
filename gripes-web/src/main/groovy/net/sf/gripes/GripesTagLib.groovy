package net.sf.gripes


class GripesTagLib {
	def inRender = 0
	def binding
	def request
	def properties = [:]
	def buffer = ""
	
	def include() { 
		""
	}
	
	def useActionBean(var) {
		properties."$var" = request.getAttribute("actionBean")
		
		""
	}
	
	def render(args, Closure c) {
		c.call()
		
		def text = new File(request.getRealPath(args.layout)).text
		def template = new GripesTemplateEngine().createTemplate(text).make(binding.getVariables()+args+properties)
		
		if(args.inline)
			return template
		else
			buffer += text
		
		binding.response.getWriter().print template
	}
	
	def section(args) {
		def text = new File(request.getRealPath(args.layout)).text
		properties[args.name] = new GripesTemplateEngine().createTemplate(text).make(binding.getVariables()+properties+args.findAll{k,v->k!='name'})
	}
}