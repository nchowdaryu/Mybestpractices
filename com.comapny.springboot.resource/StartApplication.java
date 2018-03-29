import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.company.cto.aws.gemfire.GemfireConfig;
import com.company.cto.support.SpringBootConfig;
import com.company.servlet.filter.F5Filter;
import com.company.spring.connector.CompanyTomcatConnectorCustomizer;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class})
@EnableCircuitBreaker
@ComponentScan(basePackages={"com.vanguard","com.vanguard.fas.literature.webservice"})
@ImportResource({"classpath*:/META-INF/**/spring-bootstrap.xml","classpath*:/META-INF/**/spring-bootstrap-standalone.xml","classpath*:/META-INF/**/spring-bootstrap-autowire.xml"})
public class Application {
	
	public static final String BASE_PATH = "/";
	protected static boolean IS_OS_WINDOWS = SystemUtils.IS_OS_WINDOWS;
	protected static final String LOCAL_RTEID = "catp";
	protected static final String LOCAL_SPRING_PROFILE = "local";
	protected static final String KEY_RTEID = "RTEID";
	static SpringApplication literatureApp = new SpringApplication(Application.class);
	TomcatEmbeddedServletContainerFactory tomcat = createTomcatEmbeddedServletContainerFactory();
	FilterRegistrationBean registration = new FilterRegistrationBean();
	
    @Value("${jersey.config.server.debug:false}")
    protected boolean isJerseyDebug;
    
	public static void main(String[] args) {
		GemfireConfig.initialize(Application.class);
		SpringBootConfig.initialize(Application.class);
		setLocalProperties(literatureApp);
		literatureApp.run(args);
	}

	/**
	 * For local testing, default to RTE=inte and spring.profiles.active=local, both of which can be overriden
	 * from the command line
	 * 
	 * @param app
	 */
	protected static void setLocalProperties(SpringApplication app) {
		if (IS_OS_WINDOWS) {
			System.setProperty(KEY_RTEID, System.getProperty(KEY_RTEID, LOCAL_RTEID));
			app.setAdditionalProfiles(LOCAL_SPRING_PROFILE);
		}
	}
	
	@ConfigurationProperties(prefix = "security", ignoreUnknownFields = true)
	public static class IgnoreUnknownFieldsSecurityProperties extends SecurityProperties {}	
	
	@Bean
	public SecurityProperties securityProperties() {
		return new IgnoreUnknownFieldsSecurityProperties();
	}	
	
	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		tomcat.addConnectorCustomizers(new VanguardTomcatConnectorCustomizer());
		return tomcat;
	}

	protected TomcatEmbeddedServletContainerFactory createTomcatEmbeddedServletContainerFactory() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {

			@Override
			protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(Tomcat tomcat) {
				((StandardHost) tomcat.getHost()).setErrorReportValveClass(StringUtils.EMPTY);
				return super.getTomcatEmbeddedServletContainer(tomcat);
			}
		};
		return tomcat;
	}
	
	@Bean
    public FilterRegistrationBean requestContextFilter() {
    	FilterRegistrationBean registration = new FilterRegistrationBean(new org.springframework.web.filter.RequestContextFilter());
        registration.setName("RequestContextFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.addUrlPatterns(BASE_PATH + "*");
        return registration;
    }    
    
	@Bean 
	public FilterRegistrationBean springSecurityFilterChain() {
		FilterRegistrationBean filterRegistration = new FilterRegistrationBean(new org.springframework.web.filter.DelegatingFilterProxy());
		filterRegistration.setName("SpringSecurityFilterChain");
		filterRegistration.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
		filterRegistration.addUrlPatterns(BASE_PATH + "*");
		return filterRegistration;
	}

	@Bean	
	public FilterRegistrationBean f5Filter() {
		FilterRegistrationBean registration = new FilterRegistrationBean(new F5Filter());
		registration.setName("F5Filter");
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 3);
		registration.addUrlPatterns(BASE_PATH + "*");
		return registration;
	}  

	
	
    /**
     * Initializes and registers the JAX-RS filter implementation, currently Jersey.
     * 
     * @return The JAX-RS filter registration.
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    @Bean
    public FilterRegistrationBean jaxrsFilter() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Filter filter = (Filter) Class.forName("org.glassfish.jersey.servlet.ServletContainer").newInstance();
        registration.setFilter(filter);
        registration.setName("JerseyFilter");
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        // Set the Jersey filter mapping and context path
        registration.addUrlPatterns(BASE_PATH + "*");
        registration.addInitParameter("jersey.config.servlet.filter.contextPath", BASE_PATH);
        // Load the common package and application package
        registration.addInitParameter("jersey.config.server.provider.packages", "com.vanguard.jaxrs.feature;com.vanguard.fas.literature.webservice.resource");
        // Enable media type mappings on the URI such as .xml and .json
        registration.addInitParameter("jersey.config.server.mediaTypeMappings", "xml:application/xml, json:application/json");
        // Enable Java bean validation integration
        registration.addInitParameter("jersey.config.beanValidation.enableOutputValidationErrorEntity.servers", "true");
        // Forward 404s to Spring MVC, which serves up the Actuator endpoints and non-jersey resources 
        registration.addInitParameter("jersey.config.servlet.filter.forwardOn404", "true");
        
        if (isJerseyDebug) {
            // Debug parameter switches
            registration.addInitParameter("jersey.config.server.monitoring.statistics.enabled", "true");
            registration.addInitParameter("jersey.config.server.tracing.type", "ALL");
            registration.addInitParameter("jersey.config.server.tracing.threshold", "VERBOSE");
        }
        
        return registration;
    }
    
    @Bean
	public ApplicationSecurity applicationSecurity() {
		return new ApplicationSecurity();
	}
    
	@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
	public static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// This disables Spring Security on only the Spring Boot Actuator endpoints.
			http.authorizeRequests().antMatchers("/spring/**").permitAll().and().csrf().disable();
		}
	}

}

public class SpringBootConfig {
	public static void initialize(Class<?> klass) {
		String caPath = TruststoreCopier.copyTruststoreDefault(klass);
		System.setProperty("javax.net.ssl.trustStore", caPath);
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
	}
}

mport org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;


public class VanguardTomcatConnectorCustomizer implements TomcatConnectorCustomizer {

	@Override
	public void customize(Connector connector) {
		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
    	connector.setURIEncoding("utf-8");
    	connector.setMaxPostSize(20971520);
    	connector.setScheme("http");
    	connector.setSecure(true);
    	connector.setAttribute("acceptCount", 100);
    	
//    	Defined in server.xml for regular apps but are not needed for spring boot applications
//	    connector.setPort(port);
//	    connector.setRedirectPort(redirectPort);
    	
    	// Missing acceptCount=100
    	protocol.setServer("Vanguard Server");
    	protocol.setAlgorithm("SunX509");
	    protocol.setCompressableMimeType("text/html,text/css,application/javascript,application/x-javascript,application/json,application/xml,csv/comma-separated-values,image/gif,image/jpeg,image/png");
    	protocol.setCompression("on");
    	protocol.setConnectionTimeout(60000);
    	protocol.setKeyAlias("tomcat");
    	protocol.setKeystoreFile(".keystore");
    	protocol.setKeystorePass("changeit");
    	protocol.setMaxHttpHeaderSize(65536);
    	protocol.setMaxKeepAliveRequests(80);
    	protocol.setNoCompressionUserAgents("illa/4.0.0000000000,illa/4.03,illa/4.04,illa/4.05,illa/4.06,illa/4.07,illa/4.08,illa/4.5,illa/4.51,illa/4.6,illa/4.61,illa/4.7,illa/4.72,illa/4.73,illa/4.74,illa/4.75,illa/4.76,illa/4.77,illa/4.78,illa/4.79,illa/4.8,Safari");
	}

}
