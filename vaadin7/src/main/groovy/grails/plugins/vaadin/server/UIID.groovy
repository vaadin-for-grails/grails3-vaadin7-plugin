package grails.plugins.vaadin.server

import com.vaadin.server.UICreateEvent
import com.vaadin.ui.UI
import com.vaadin.util.CurrentInstance
import org.apache.log4j.Logger

/**
 * Identifier for {@link UI}s.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
final class UIID implements Serializable {

    private static final Logger log = Logger.getLogger(UIID)

    static UIID forUI(UI ui) {
        assert ui != null, "ui must not be null"
        def id = ui.getUIId()
        if (id != -1) {
            return new UIID(id)
        }
        if (UI.getCurrent() == ui) {
            def uiid = CurrentInstance.get(UIID)
            if (uiid) {
                return uiid
            }
        }
        throw new RuntimeException("No valid ui found")
    }

    static UIID getCurrent() {
        def ui = UI.getCurrent()
        if (ui != null) {
            return forUI(ui)
        }
        def uiid = CurrentInstance.get(UIID)
        if (uiid == null) {
            throw new RuntimeException("No valid ui found")
        }
        uiid
    }

    final Integer id

    private UIID(Integer id) {
        assert id != null, "id must not be null"
        this.id = id
    }

    UIID(UICreateEvent event) {
        this(event.uiId)
    }

    boolean equals(other) {
        if (this.is(other)) {
            return true
        }
        if (UIID != other.getClass()) {
            return false
        }

        id == ((UIID) other).id
    }

    int hashCode() {
        id.intValue()
    }

    @Override
    public String toString() {
        id.toString()
    }

    Object asType(Class clazz) {
        if (clazz == Integer) {
            return id
        }
        super.asType(clazz)
    }
}
