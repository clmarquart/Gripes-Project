package net.sf.gripes.model

import javax.persistence.MappedSuperclass
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.PrePersist

import net.sf.gripes.GripesBaseModel
import net.sf.gripes.converter.PasswordTypeConverter

@MappedSuperclass
class GripesUser extends GripesBaseModel {
	@Id @GeneratedValue Long id
	
    String firstName
    String lastName
	String username
	
	String password
	
    boolean activated

    @Override String toString() {
        String.format("%s %s (activated: %s)", firstName, lastName, activated);
    }
}

