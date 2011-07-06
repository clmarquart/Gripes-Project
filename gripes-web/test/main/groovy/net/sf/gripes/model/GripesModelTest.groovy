package net.sf.gripes.model

import org.junit.Test

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import net.sf.gripes.GripesBaseTest

public class GripesModelTest extends GripesBaseTest {
	Logger logger = LoggerFactory.getLogger(GripesModelTest.class)

	@Test public void testStaticSave() {
		ModelTester.save([name: 'Ima Test'])
		
		org.junit.Assert.assertTrue( ModelTester.list().size() == 1 )
	}
	
	@Test public void testInstanceSave() {		
		ModelTester modelTester = new ModelTester(name:"Ima Test")
		modelTester.save()
		
		org.junit.Assert.assertTrue( ModelTester.list().size() == 1 )
	}
	
	@Test public void testStaticAndInstanceSave() {
		ModelTester.save([name: 'Ima Test2'])
		
		ModelTester modelTester = new ModelTester(name:"Ima Test")
		modelTester.save()
		
		org.junit.Assert.assertTrue( ModelTester.list().size() == 2 )
	}
}