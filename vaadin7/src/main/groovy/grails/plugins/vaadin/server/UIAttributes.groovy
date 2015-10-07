package grails.plugins.vaadin.server

import com.vaadin.server.SessionDestroyEvent
import com.vaadin.server.SessionDestroyListener
import com.vaadin.server.VaadinSession
import com.vaadin.ui.UI
import org.apache.log4j.Logger

/**
 * Storing and retrieving data in UI contexts.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
class UIAttributes implements Serializable, SessionDestroyListener {

    private static final log = Logger.getLogger(UIAttributes)

    private static String sessionAttributeNameFor(UIID uiid) {
        "${UIAttributes}#${uiid}"
    }

    static UIAttributes forUI(VaadinSession session, UIID uiid) {
        def service = session.service
        session.lock()
        try {
            def uiAttributes = session.getAttribute(sessionAttributeNameFor(uiid))
            if (uiAttributes == null) {
                uiAttributes = new UIAttributes(uiid)
                service.addSessionDestroyListener(uiAttributes)
                session.setAttribute(sessionAttributeNameFor(uiid), uiAttributes)
            }
            return uiAttributes
        } finally {
            session.unlock()
        }
    }

    static UIAttributes forUI(UI ui) {
        def uiid = UIID.forUI(ui)
        forUI(ui.session, uiid)
    }

    static UIAttributes getCurrent() {
        forUI(UI.getCurrent())
    }

    private final UIID uiid
    private final Map<String, Object> attributeMap = new HashMap<>()

    UIAttributes(UIID uiid) {
        this.uiid = uiid
    }

    def <T> T getAttribute(String name) {
        (T) attributeMap.get(name)
    }

    def <T> T getAttribute(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null")
        }
        getAttribute(type.name)
    }

    def <T> void setAttribute(String name, T value) {
        attributeMap.put(name, value)
    }

    def <T> void setAttribute(Class<T> type, T value) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null")
        }
        setAttribute(type.name, value)
    }

    boolean belongsToSession(VaadinSession session) {
        session.getAttribute(sessionAttributeNameFor(uiid)) == this
    }

    @Override
    void sessionDestroy(SessionDestroyEvent event) {
        def session = event.session
        if (belongsToSession(session)) {
            def sessionId = VaadinSessionID.forSession(session)
            def attributeName = sessionAttributeNameFor(uiid)
            log.debug("Removing all attributes with propertyName [${attributeName}] from session with id [$sessionId]")
            attributeMap.clear()
            session.setAttribute(attributeName, null)
        }
    }
}
