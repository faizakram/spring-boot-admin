# Introduction to Spring Boot Admin #

## Spring Boot Admin ##
Spring Boot admin is a community project use to manage and monitor your Spring Boot applications. The client application gets register themselves with the admin server (via Http) or is discovered using Spring Cloud discover server like Eureka, Consul.

Each client application needs to have Spring Actuator jars in it. The endpoints provided by the Actuator jar is polled by the Spring Boot Admin server to get the metrics of that particular application.
Actuators endpoints let you monitor and interact with your application. Spring Boot includes a number of built-in endpoints and lets you add your own.

We will first set up a Spring Boot Admin server and then create a simple rest service and register it with the admin server.

## 1.Admin Server Setup ##
The best way to create a spring boot application is Spring Initializr.  Select your Spring Boot version (2+ recommended) and add the ‘Spring Boot Admin Server’ dependency. Generate it as a Maven project and you are all set.



```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.2.6.RELEASE</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.app.server</groupId>
	<artifactId>app-server</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>app-server</name>
	<description>Client Module</description>

	<properties>
		<java.version>1.8</java.version>
		<spring-boot-admin-server.version>2.2.2</spring-boot-admin-server.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<!-- https://mvnrepository.com/artifact/de.codecentric/spring-boot-admin-server -->
		<dependency>
			<groupId>de.codecentric</groupId>
			<artifactId>spring-boot-admin-server</artifactId>
			<version>${spring-boot-admin-server.version}</version>
		</dependency>
		<dependency>
			<groupId>de.codecentric</groupId>
			<artifactId>spring-boot-admin-server-ui</artifactId>
			<version>${spring-boot-admin-server.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

Enable the admin server by adding ** @EnableAdminServer ** at your main class.

```
@SpringBootApplication
@EnableAdminServer
public class SpringAdminServerApplication {
 public static void main(String[] args) {
  SpringApplication.run(SpringAdminServerApplication.class, args);
 }
}
```
This is all needed to enable the Spring Boot admin server. Let’s run the application and open http://localhost:8080.

## 2. Setting Up an Admin Client ##
We need to create a simple Spring Boot web application and add the following maven dependencies

```
<dependency>
   <groupId>de.codecentric</groupId>
   <artifactId>spring-boot-admin-starter-client</artifactId>
</dependency>
```

Spring Boot Admin (client) automatically bring in the Actuator jar. The application includes a simple REST controller with a single GET mapping that just returns a greeting message.

```
@RestController
public class GreetController {

 @GetMapping("/greet")
 public String greet() {
  return "Hi!! there...";
 }
}
```

As the last step, let’s update
application.properties file with the following properties.

```
server.port=8060
spring.application.name=greet-service
spring.boot.admin.client.url=http://localhost:8080
management.endpoints.web.exposure.include=*
```
spring.boot.admin.client.url is a mandatory property which is the URL of the Spring Boot Admin Server to register at ** management.endpoints.web.exposure.include ** is used to expose all the actuators endpoints.

Now boot the application up and visit the Admin server.
![picture alt](http://faizakram.com/git-hub/spring-boot-admin/spring_boot_admin.png "Spring Boot Admin")

Let’s go into details.

![picture alt](http://faizakram.com/git-hub/spring-boot-admin/admin_ui.png "Spring Boot Admin")

As you can see, you can get much info about your application using the UI.

## 3. Client Using Spring Cloud Discovery ##
If you have a Spring Cloud Discovery (like Eureka) for your application, you don’t need to have Spring Boot Admin Client jar in each of your client application (Although each one of them must have actuator jar in their classpath).

### 3.1 Maven Dependencies ###
Add the following dependency in the pom.xml

```
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency
```

Enable discovery by adding ** @EnableDiscoveryClient ** in your main class

```
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class SpringAdminServerApplication {

 public static void main(String[] args) {
  SpringApplication.run(SpringAdminServerApplication.class, args);
 }
}
```

As the last point, tell the Eureka client where to find the service registry by adding following properties in the
application.properties file.

```
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=false
```
Spring Boot Admin Server will get all client application details from Eureka and poll them for metrics.

## 4. Monitoring and Management Features ##
Let’s take a look at some of the monitoring and management features available by Spring Boot admin.

### 4.1 Logging ###
You can reconfigure your log level (root or a particular package) directly from the UI itself without having to change a properties file or without any restart of your application.

![picture alt](http://faizakram.com/git-hub/spring-boot-admin/spring-boot-admin-logging.png "Spring Boot Admin")

### 4.2 JVM Metrics ###
If you are using tomcat as your container, JMX-beans are exposed via Http. This is because of the Jolokia (auto-included in spring-admin-server-client) jar in your classpath. As Jolokia is servlet based there is no support for reactive applications.

![picture alt](http://faizakram.com/git-hub/spring-boot-admin/JMV-Metrics.png "Spring Boot Admin")

### 4.3 Web Mappings and Traces ###
These ware views for all the mappings that are present in your application and what all Http traces were made.

![picture alt](http://faizakram.com/git-hub/spring-boot-admin/Web-Metrics.png "Spring Boot Admin")

## 5. Security ##
The ** Spring Boot admin ** server has access to application sensitive endpoints, so its advisable to have some sort of security enabled to both admin and client application. Since there are several approaches to solving authentication and authorization in distributed web applications Spring Boot Admin doesn’t ship a default one.

You can go with a basic username/password set up on both the admin server and the client application and then configure each other credentials in the properties file. Let’s see the same.

Add the following dependency in your pom.xml file to enable Spring Security for your application.

```
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
Add the following properties to the application.properties file

```
spring.security.user.name=admin
spring.security.user.password=admin
```
---
> ** Above example only provides basic security and not recommended for the production applications. **

To force the login page, add the following code in your application

```
@SpringBootApplication
@EnableAdminServer
public class SpringAdminServerApplication {

 private final String adminContextPath;

 public SpringAdminServerApplication(AdminServerProperties adminServerProperties) {
  this.adminContextPath = adminServerProperties.getContextPath();
 }

 public static void main(String[] args) {
  SpringApplication.run(SpringAdminServerApplication.class, args);
 }

 @Bean
 public SecurityWebFilterChain securityWebFilterChainSecure(ServerHttpSecurity http) {

  return http.authorizeExchange()
   .pathMatchers(adminContextPath + "/assets/**").permitAll()
   .pathMatchers(adminContextPath + "/login").permitAll()
   .anyExchange().authenticated()
   .and().formLogin().loginPage(adminContextPath + "/login")
   .and().logout().logoutUrl(adminContextPath + "/logout")
   .and().httpBasic()
   .and().csrf().disable().build();

 }

}
```
Open the page by visiting http://localhost:8080

![picture alt](http://faizakram.com/git-hub/spring-boot-admin/Admin-server-login.png "Spring Boot Admin")

Once security is active the clients have to know about this authentication in order to register themselves to Spring Boot Admin Server. Also, they have to tell Spring Boot Admin Server how it should connect its actuator endpoints, i.e pass its own credentials (passed via metadata). Add the following properties in greet-service application.properties:
```
#Required for this application to connect to SBA
spring.boot.admin.client.username=admin
spring.boot.admin.client.password=admin

#basic auth creddentials
spring.security.user.name=client
spring.security.user.password=client

#configs to give secured server info to SBA while registering
spring.boot.admin.client.instance.metadata.user.name= ${spring.security.user.name}
spring.boot.admin.client.instance.metadata.user.password=${spring.security.user.password}
```

This will set up the basic auth in both Spring Boot Admin Server and the Client application. Most of the distributed applications are setup using some sort of token implementation (ID token or using basic client credential grant type) with a common authorization server that grants tokens. You can setup using this if that’s the scenario.
## 6. Notifications ##
* Spring Boot Admin Server can send you notifications if something fails. The following notifiers are available out of the box:
	* Email
	* PagerDuty
	* OpsGenie
	* Hipchat
	* Slack
	* Let’s Chat
	* Microsoft Teams
	* Telegram

You can, of course, implement your own notification. Let’s just see how you will implement a mail notification. Add spring-boot-starter-mail to your dependencies:

```
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```
Then add the mail configuration in the application.properties.

```
spring.mail.host=smtp.example.com
spring.boot.admin.notify.mail.to=admin@example.com
spring.mail.username=smtp_user
spring.mail.password=smtp_password
```
You can also add * Microsoft Teams * configuration in the application.properties.

```
#MiroSoft Team
spring.boot.admin.notify.ms-teams.enabled=true
spring.boot.admin.notify.ms-teams.webhook-url=<<WebhookURL>>
#spring.boot.admin.notify.ms-teams.theme-color=FF5733
```
