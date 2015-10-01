package grails.plugins.vaadin.server

import grails.plugins.vaadin.config.VaadinConfig
import grails.util.Environment
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.vaadin.grails.server.UriMappings

import javax.servlet.ServletRegistration

class GrailsAwareVaadinServletRegistrationBean extends ServletRegistrationBean {

    private static final Logger log = Logger.getLogger(GrailsAwareVaadinServletRegistrationBean)

    @Autowired
    UriMappings uriMappings

    GrailsAwareVaadinServletRegistrationBean() {
        super(new GrailsAwareVaadinServlet(), ['/VAADIN/*'] as String[])
    }

    @Override
    protected void configure(ServletRegistration.Dynamic registration) {
        log.debug("Configuring Vaadin servlet")
        try {
//            Notice: You cannot use UriMappings.getCurrent() here
            uriMappings.allPaths.each { path ->
                def urlMapping = "$path/*"
                addUrlMappings(urlMapping)
                log.debug("URL mapping [$urlMapping] added")
            }

            def config = VaadinConfig.getCurrent()
            def closeIdleSessions = config.getProperty('vaadin.closeIdleSessions', 'false')
            def productionMode = Boolean.toString(Environment.current != Environment.DEVELOPMENT)
            addInitParameter('UIProvider', GrailsAwareUIProvider.name)
            addInitParameter('closeIdleSessions', closeIdleSessions)
            addInitParameter('productionMode', productionMode)
            asyncSupported = true

            super.configure(registration)
        } catch (e) {
            log.error("Configuring Vaadin servlet failed", e)
            throw new RuntimeException(e)
        }
    }
}
