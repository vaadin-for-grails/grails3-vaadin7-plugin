package grails.plugins.vaadin.server

import com.vaadin.server.SessionDestroyEvent
import com.vaadin.server.SessionDestroyListener
import com.vaadin.server.VaadinSession
import com.vaadin.ui.UI
import org.apache.log4j.Logger

class UIAttributes implements Serializable, SessionDestroyListener {

    private static final log = Logger.getLogger(UIAttributes)

    private static String sessionAttributeNameFor(UIID uiid) {
        "${UIAttributes}#${uiid}"
    }

    static UIAttributes forUI(UI ui) {
        def uiid = UIID.forUI(ui)
        def session = ui.session
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

    static UIAttributes getCurrent() {
        forUI(UI.getCurrent())
    }

    private final UIID uiid
    private final Map<String, Object> attributeMap = new HashMap<>()

    UIAttributes(UIID uiid) {
        this.uiid = uiid
    }

    def <T> T getAttribute(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null")
        }
        (T) attributeMap.get(type.name)
    }

    def <T> void setAttribute(Class<T> type, T value) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null")
        }
        attributeMap.put(type.name, value)
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
            log.debug("Removing all attributes with key [${attributeName}] from session with id [$sessionId]")
            attributeMap.clear()
            session.setAttribute(attributeName, null)
        }
    }
}
