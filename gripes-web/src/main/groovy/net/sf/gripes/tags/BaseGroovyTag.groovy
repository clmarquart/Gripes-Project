package net.sf.gripes.tags

import groovy.lang.Binding
import groovy.lang.GroovyShell

class BaseGroovyTag {
	def executeBody(args) {
		def binding = new Binding(args.attrs)
		binding.setVariable "out", args.out
		def gs = new GroovyShell(binding)
		def bodyResult = gs.parse(args.body);
		
		bodyResult	
	}
	
	static def executeBodyTag(attrs, body, out) {
		println "BINDING: $attrs"
		def binding = new Binding(attrs)
		binding.setVariable "out", out
		def gs = new GroovyShell(binding)
		def bodyResult = gs.parse(body);
		
		bodyResult	
	}
}