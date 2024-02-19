**Before running the application:**

1. Open your application.properties file and add the following line:

`spring.config.import=optional:secrets.properties
`
2. Create a secrets.properties file in the src/main/resources folder.
3. Configure the following properties in the secrets.properties file:

`spring.datasource.url=jdbc:mysql://localhost:3306/zalando_shop_api
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password`

4. Ensure that you have specified your database name in the src/test/resources/db/dropDB.sql file.

**Access Swagger UI:**

You can access Swagger UI by navigating to http://localhost:8080/swagger-ui/index.html after starting the application.
or https://zalandorestfulapi.ew.r.appspot.com/swagger-ui/index.html 


