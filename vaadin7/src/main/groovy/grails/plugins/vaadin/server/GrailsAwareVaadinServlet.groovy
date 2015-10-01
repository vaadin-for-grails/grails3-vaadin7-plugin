package grails.plugins.vaadin.server

import com.vaadin.server.VaadinServlet
import org.vaadin.grails.util.ApplicationContextUtils

import javax.servlet.ServletException

/**
 * Grails specific implementation of {@link VaadinServlet}.
 *
 * @author Stephan Grundner
 */
class GrailsAwareVaadinServlet extends VaadinServlet {

    @Override
    protected void servletInitialized() throws ServletException {

        def sessionInitListener = ApplicationContextUtils
                .getBeanOrInstance(GrailsAwareSessionInitListener)
        service.addSessionInitListener(sessionInitListener)
    }
}
