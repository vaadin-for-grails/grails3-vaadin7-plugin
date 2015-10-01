package grails.plugins.vaadin.server

import com.vaadin.server.ServiceException
import com.vaadin.server.SessionInitEvent
import com.vaadin.server.SessionInitListener
import org.vaadin.grails.data.util.converter.GrailsAwareConverterFactory
import org.vaadin.grails.util.ApplicationContextUtils

/**
 * Grails specific implementation for {@link SessionInitListener}.
 *
 * @author Stephan Grundner
 * @since 2.0
 */
class GrailsAwareSessionInitListener implements SessionInitListener {

    @Override
    void sessionInit(SessionInitEvent event) throws ServiceException {
        def session = event.session

        def converterFactory = ApplicationContextUtils
                .getBeanOrInstance(GrailsAwareConverterFactory)
        session.setConverterFactory(converterFactory)

        def requestHandler = ApplicationContextUtils
                .getBeanOrInstance(GrailsAwareRequestHandler)
        session.addRequestHandler(requestHandler)

        def errorHandler = ApplicationContextUtils
                .getBeanOrInstance(DefaultErrorHandler)
        session.setErrorHandler(errorHandler)
    }
}
