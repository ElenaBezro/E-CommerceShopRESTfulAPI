Before run the application:
1. add the next line in your application.properties file:
spring.config.import=optional:secrets.properties
2. create secrets.properties file in the resources folder
3. add and configure next properties:
   spring.datasource.url=jdbc:mysql://localhost:3306/zalando_shop_api
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password

SWAGGER http://localhost:8080/swagger-ui/index.html
