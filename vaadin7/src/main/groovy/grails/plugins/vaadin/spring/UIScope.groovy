package grails.plugins.vaadin.spring

import com.vaadin.server.ClientConnector
import com.vaadin.ui.UI
import grails.plugins.vaadin.server.UIAttributes
import grails.plugins.vaadin.server.UIID
import org.apache.log4j.Logger

/**
 * UI scope.
 *
 * @author Stephan Grundner
 * @since 3.0
 */
class UIScope extends AbstractScope implements ClientConnector.DetachListener {

    private static final log = Logger.getLogger(UIScope)

    static final SCOPE_NAME = 'vaadin-ui'

    UIScope() {
        super(SCOPE_NAME)
    }

    @Override
    String getConversationId() {
        log.debug("Getting conversation id for ui")
        UIID.getCurrent().toString()
    }

    @Override
    BeanMap getBeanMap() {
        def uiAttributes = UIAttributes.getCurrent()
        def uiid = UIID.getCurrent()
        def beanMap = uiAttributes.getAttribute(BeanMap)
        if (beanMap == null) {
            log.debug("Create bean map for ui with id [$uiid]")
            beanMap = new BeanMap()
            uiAttributes.setAttribute(BeanMap, beanMap)
            UI.getCurrent().addDetachListener(this)
        }
        return beanMap
    }

    @Override
    void detach(ClientConnector.DetachEvent event) {
        if (event.source instanceof UI) {
            def ui = event.source as UI
            def uiAttributes = UIAttributes.forUI(ui)
            def beanMap = uiAttributes.getAttribute(BeanMap)
            if (beanMap == null) {
                log.warn("No bean map found to get removed from ui with id [${ui.getUIId()}]")
                return
            }
            log.debug("Removing beans from ui with id [${ui.getUIId()}]")
            uiAttributes.setAttribute(BeanMap, null)
            ui.accessSynchronously({
                beanMap.clear()
            })
        }
    }
}
