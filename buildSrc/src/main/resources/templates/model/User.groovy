package PACKAGE.model

import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.FetchType

import net.sf.gripes.model.GripesUser

import PACKAGE.model.Role

@Entity
class User extends GripesUser implements Serializable {
	
	@ManyToMany(fetch=FetchType.EAGER)
	List<Role> roles
}

