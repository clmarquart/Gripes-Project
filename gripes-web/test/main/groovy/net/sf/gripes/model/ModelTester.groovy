package net.sf.gripes.model

import net.sf.gripes.model.GripesBaseModel

import javax.persistence.Entity;
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class ModelTester extends GripesBaseModel {
	@Id @GeneratedValue Long id
	
	String name
}