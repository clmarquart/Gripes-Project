package net.sf.gripes

import net.sf.gripes.*

import static org.junit.Assert.*;
import org.junit.*
import org.gradle.api.*
import org.gradle.testfixtures.*

class GripesPluginTest {
	static Project project
	
	@Before void setupGripesTests() {
		project = ProjectBuilder.builder().build()
		project.plugins.apply 'war'
		project.plugins.apply 'groovy'
	  	project.plugins.apply 'gripes'
	}

	@Test void gradleHasGripes() {
		assertTrue(project.plugins.hasPlugin(GripesPlugin))		
		assertTrue(project.plugins.findPlugin("gripes") instanceof GripesPlugin)
	}
	
    @Test void gradleHasGripesTasks() {
		assertTrue(project.tasks.init instanceof Task)
		assertTrue(project.tasks.create.dependsOn.contains(project.tasks.compileGroovy))
    }
}