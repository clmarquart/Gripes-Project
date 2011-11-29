package net.sf.gripes.model

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GripesBaseModel {
	Logger logger = LoggerFactory.getLogger(GripesBaseModel.class)
	static Logger _logger = LoggerFactory.getLogger(GripesBaseModel.class)
		
	static void crudify(Class cls) {
		_logger.debug "CRUDify the Entity: $cls"
		
		cls.metaClass.static.getDao = {
			Class.forName("${cls.package.name.replace('model','dao')}.${cls.simpleName}Dao").newInstance()
		}
		cls.metaClass.static.getList = {
			getDao().list()
		}
		cls.metaClass.static.list = {
			getList()
		}
		cls.metaClass.static.save = {
			_logger.debug "Saving for {}", cls.simpleName
			_logger.debug "Object is {}", delegate
			
			def dao = getDao()
			dao.save(delegate)
			dao.commit()
		}
		
		cls.metaClass.static.save = { map ->
			_logger.debug "Saving for {}", cls.simpleName

			def obj = cls.newInstance()
			def dao = getDao()
			map.each {k,v->
				obj."${k}" = v
			}
			
			dao.save(obj)
			dao.commit()
		}
		
		cls.metaClass.static.find = { id ->
			getDao().find(new Long(id))
		}
		
		cls.metaClass.static.findBy = { id, field ->
			getDao().findBy(field.toLowerCase(), id)
		}
	}
}