package grails.plugins.vaadin.spring

import com.vaadin.server.SessionDestroyEvent
import com.vaadin.server.SessionDestroyListener
import com.vaadin.server.VaadinService
import com.vaadin.server.VaadinSession
import grails.plugins.vaadin.server.VaadinSessionID
import org.apache.log4j.Logger

/**
 * Vaadin session scope.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
class VaadinSessionScope extends AbstractScope implements SessionDestroyListener {

    private static final log = Logger.getLogger(VaadinSessionScope)

    static final SCOPE_NAME = 'vaadin-session'

    VaadinSessionScope() {
        super(SCOPE_NAME)
    }

    @Override
    String getConversationId() {
        log.debug("Getting conversation id for session")
        VaadinSessionID.getCurrent().toString()
    }

    @Override
    BeanMap getBeanMap() {
        def session = VaadinSession.getCurrent()
        session.lock()
        try {
            def sessionId = VaadinSessionID.getCurrent()
            def beanMap = session.getAttribute(BeanMap)
            if (beanMap == null) {
                log.debug("Create bean map for session with id [$sessionId]")
                beanMap = new BeanMap()
                session.setAttribute(BeanMap, beanMap)
                VaadinService.getCurrent().addSessionDestroyListener(this)
            }
            return beanMap
        } finally {
            session.unlock()
        }
    }

    @Override
    void sessionDestroy(SessionDestroyEvent event) {
        def session = event.session
        def sessionId = VaadinSessionID.forSession(session)
        def beanMap = session.getAttribute(BeanMap)
        if (beanMap == null) {
            log.warn("No bean map found to get removed from session id [$sessionId]")
            return
        }
        log.debug("Removing beans from session id [$sessionId]")
        session.setAttribute(BeanMap, null)
        session.access({
            beanMap.clear()
        })
    }
}
