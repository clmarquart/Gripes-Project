/*
import com.acme.model.*
import net.sf.gripes.converter.*

def adminRole = new Role(name:"admin")
adminRole.save()
def userRole = new Role(name:"user")
userRole.save()

User.save(
	firstName: "Cody",
	lastName: "Marquart",
	username: "cody",
	password: new PasswordTypeConverter().hash("test"),
	activated: true,
	roles: [adminRole,userRole]
)
*/