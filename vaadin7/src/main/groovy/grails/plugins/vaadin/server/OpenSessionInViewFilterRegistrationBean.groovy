package grails.plugins.vaadin.server

import grails.util.Holders
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.vaadin.grails.navigator.UriMappings

import javax.servlet.DispatcherType
import javax.servlet.FilterRegistration
import javax.servlet.ServletContext
import javax.servlet.ServletException

/**
 * Factory bean for the Hibernate specific OSIV filter.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
class OpenSessionInViewFilterRegistrationBean extends FilterRegistrationBean {

    private static final Logger log = Logger.getLogger(OpenSessionInViewFilterRegistrationBean)

    @Autowired
    UriMappings uriMappings

    @Override
    void onStartup(ServletContext servletContext) throws ServletException {
        try {
//            TODO Support für Hibernate 3!?
            def filterClassName = 'org.springframework.orm.hibernate4.support.OpenSessionInViewFilter'
            filter = Holders.grailsApplication.classLoader.loadClass(filterClassName).newInstance()
            log.debug("Using filter [$filterClassName]")
            super.onStartup(servletContext)
        } catch (e) {
            throw new RuntimeException(e)
        }
    }

    @Override
    protected void configure(FilterRegistration.Dynamic registration) {
        log.debug("Configuring OSIV filter")
        try {
            def dt = EnumSet.of(DispatcherType.REQUEST)
            def listOfPaths = uriMappings.allPathPatterns.findAll { !uriMappings.isPattern(it) }
            if (!listOfPaths.isEmpty()) {
                listOfPaths.each { path ->
                    if (!path.endsWith('/')) {
                        path += '/'
                    }
                    registration.addMappingForUrlPatterns(dt, true, "$path*")
                    log.debug("mapping for URL pattern [$path*] added")
                }
            } else {
                registration.addMappingForUrlPatterns(dt, true, '/*')
            }
//            registration.addMappingForUrlPatterns(dt, true, '/vaadin/*')
            super.configure(registration)
        } catch (e) {
            log.error("Configuring OSIV filter failed", e)
            throw new RuntimeException(e)
        }
    }
}
