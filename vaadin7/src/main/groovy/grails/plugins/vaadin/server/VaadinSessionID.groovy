package grails.plugins.vaadin.server

import com.vaadin.server.VaadinSession
import org.apache.log4j.Logger

/**
 * Identifier for {@link VaadinSession}s.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
class VaadinSessionID implements Serializable {

    private static final Logger log = Logger.getLogger(VaadinSessionID)

    static VaadinSessionID forSession(VaadinSession session) {
        assert session != null, "session must not be null"
        def sessionId = session.getAttribute(VaadinSessionID)
        if (sessionId == null) {
            def id = session.session?.id
            if (id == null) {
                throw new RuntimeException("Invalid session")
            }
            sessionId = new VaadinSessionID(id)
            session.setAttribute(VaadinSessionID, sessionId)
        }
        sessionId
    }

    static VaadinSessionID getCurrent() {
        forSession(VaadinSession.getCurrent())
    }

    final String id

    private VaadinSessionID(String id) {
        assert id != null, "id must not be null"
        this.id = id
    }

    boolean equals(other) {
        if (this.is(other)) {
            return true
        }
        if (VaadinSessionID != other.getClass()) {
            return false
        }

        id == ((VaadinSessionID) other).id
    }

    int hashCode() {
        id.hashCode()
    }

    @Override
    public String toString() {
        id.toString()
    }
}
