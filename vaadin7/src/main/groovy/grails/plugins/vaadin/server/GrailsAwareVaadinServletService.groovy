package grails.plugins.vaadin.server

import com.vaadin.server.DeploymentConfiguration
import com.vaadin.server.ServiceException
import com.vaadin.server.VaadinServletService

/**
 * Grails specific implementation for {@link com.vaadin.server.VaadinServletService}.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
class GrailsAwareVaadinServletService extends VaadinServletService {

    GrailsAwareVaadinServletService(GrailsAwareVaadinServlet servlet, DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        super(servlet, deploymentConfiguration)
    }
}
