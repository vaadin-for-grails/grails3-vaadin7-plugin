package demo.ui

import com.vaadin.navigator.Navigator
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout

class DemoUI extends UI {

    DemoUI() {
        content = new VerticalLayout()
        content.setMargin(true)
        content.setSpacing(true)
        def title = new Label("Demo Application")
        title.styleName = "h1 colored"
        content.addComponent(title)
        def viewContainer = new Panel()
        navigator = new Navigator(this, viewContainer)
        content.addComponent(viewContainer)
        def footer = new Label("Powered by Grails and Vaadin")
        footer.styleName = "light"
        content.addComponent(footer)
    }

    @Override
    protected void init(VaadinRequest request) { }
}
