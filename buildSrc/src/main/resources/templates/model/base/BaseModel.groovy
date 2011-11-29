package PACKAGE.model.base

import javax.persistence.MappedSuperclass
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.PrePersist
import javax.persistence.PreUpdate

@MappedSuperclass
abstract class BaseModel {
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