package PACKAGE.model

import javax.persistence.Entity
import javax.persistence.ManyToMany

import net.sf.gripes.model.GripesRole

import PACKAGE.model.User

@Entity
class Role extends GripesRole implements Serializable {
	
	@ManyToMany
	List<User> users
}