package net.sf.gripes.entity.transform

import org.codehaus.groovy.transform.ASTTransformation

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
class EntityMappingASTTransformation implements ASTTransformation {

}