package PACKAGE.model.base

import net.sf.gripes.model.GripesBaseModel
import javax.persistence.MappedSuperclass
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

@MappedSuperclass
abstract class BaseModel extends GripesBaseModel {
	@Id @GeneratedValue Long id
	
	Date created
	Date updated
	

	@PrePersist protected void onCreate() {
	  created = new Date()
	}

	@PreUpdate protected void onUpdate() {
	  updated = new Date()
	}
}