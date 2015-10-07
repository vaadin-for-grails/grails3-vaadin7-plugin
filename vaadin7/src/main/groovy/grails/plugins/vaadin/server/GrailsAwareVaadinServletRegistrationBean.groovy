package grails.plugins.vaadin.server

import grails.plugins.vaadin.config.VaadinConfig
import grails.util.Environment
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.vaadin.grails.navigator.UriMappings

import javax.servlet.ServletRegistration

/**
 * Factory bean for {@link GrailsAwareVaadinServlet}.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
class GrailsAwareVaadinServletRegistrationBean extends ServletRegistrationBean {

    private static final Logger log = Logger.getLogger(GrailsAwareVaadinServletRegistrationBean)

    @Autowired
    UriMappings uriMappings

    GrailsAwareVaadinServletRegistrationBean() {
        super(new GrailsAwareVaadinServlet(), ['/VAADIN/*'] as String[])
    }

    protected String getUIProviderClassName() {
        GrailsAwareUIProvider.name
    }

    protected boolean isCloseIdleSessions() {
        def config = VaadinConfig.getCurrent()
        config.getProperty('closeIdleSessions', Boolean, false)
    }

    protected boolean isProductionMode() {
        Environment.current != Environment.DEVELOPMENT
    }

    @Override
    protected void configure(ServletRegistration.Dynamic registration) {
        log.debug("Configuring Vaadin servlet")
        try {
            def listOfPaths = uriMappings.allPathPatterns.findAll { !uriMappings.isPattern(it) }
            if (!listOfPaths.isEmpty()) {
                listOfPaths.each { path ->
                    if (!path.endsWith('/')) {
                        path += '/'
                    }
                    addUrlMappings("$path*")
                    log.debug("URL mapping [$path*] added")
                }
            } else {
                addUrlMappings('/*')
                log.warn("Vaadin servlet mapped to [/*]")
            }

            addInitParameter('UIProvider', getUIProviderClassName())
            addInitParameter('closeIdleSessions', Boolean.toString(isCloseIdleSessions()))
            addInitParameter('productionMode', Boolean.toString(isProductionMode()))
            asyncSupported = true

            super.configure(registration)
        } catch (e) {
            log.error("Configuring Vaadin servlet failed", e)
            throw new RuntimeException(e)
        }
    }
}
