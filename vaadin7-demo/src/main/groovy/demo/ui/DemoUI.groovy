package demo.ui

import com.vaadin.navigator.Navigator
import com.vaadin.server.VaadinRequest
import com.vaadin.shared.Version
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import grails.util.Environment
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
@Scope('prototype')
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
        def envName = Environment.current.name
        def vaadinVersion = Version.fullVersion
        def footer = new Label("Powered by Grails and Vaadin; Environment: $envName")
        footer.styleName = "light"
        content.addComponent(footer)
        navigator
    }

    @PostConstruct
    void foo() {

    }

    @PreDestroy
    void bar() {

    }

    @Override
    protected void init(VaadinRequest request) {}
}
