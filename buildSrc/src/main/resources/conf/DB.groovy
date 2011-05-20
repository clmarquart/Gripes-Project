database {
	gripesPU {
		schema = "create-drop"
		dialect = "org.hibernate.dialect.HSQLDialect"
		driver = "org.hsqldb.jdbc.JDBCDriver"
		url = "jdbc:hsqldb:mem:gripes"
		user = "sa"
		password = ""
	
		// auto or ["com.acme.model.Page"]
		classes = "auto"
	}
}