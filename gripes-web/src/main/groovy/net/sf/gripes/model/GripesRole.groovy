package net.sf.gripes.model

import javax.persistence.MappedSuperclass
import javax.persistence.GeneratedValue
import javax.persistence.Id

import net.sf.gripes.model.GripesBaseModel

@MappedSuperclass
class GripesRole extends GripesBaseModel {
	@Id @GeneratedValue Long id
	
    String name

    public GripesRole() { }

    public GripesRole(String name) {
        this.name = name;
    }

    @Override boolean equals(Object object) {
        try {
            name.equals(((GripesRole) object).getName())
        } catch (e) {
            false
        }
    }
    @Override int hashCode() {
        name.hashCode()
    }

    @Override String toString() {
        name
    }
}

