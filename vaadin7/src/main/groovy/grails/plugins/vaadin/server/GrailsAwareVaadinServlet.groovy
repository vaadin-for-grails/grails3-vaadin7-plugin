package grails.plugins.vaadin.server

import com.vaadin.server.DeploymentConfiguration
import com.vaadin.server.ServiceException
import com.vaadin.server.VaadinServlet
import com.vaadin.server.VaadinServletService
import org.vaadin.grails.util.ApplicationContextUtils

import javax.servlet.ServletException

/**
 * Grails specific implementation for {@link VaadinServlet}.
 *
 * @author Stephan Grundner
 * @since 1.0
 */
class GrailsAwareVaadinServlet extends VaadinServlet {

    @Override
    protected void servletInitialized() throws ServletException {

        def sessionInitListener = ApplicationContextUtils
                .getBeanOrInstance(GrailsAwareSessionInitListener)
        service.addSessionInitListener(sessionInitListener)
    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        def service = new GrailsAwareVaadinServletService(this, deploymentConfiguration)
        service.init()
        service
    }
}
