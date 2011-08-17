Adding an Entity to the search index only takes 2 steps:

1. Add @org.hibernate.search.annotations.Indexed to the entity class
2. Add @org.hibernate.search.annotations.Field to any field on the entity that is to be searched