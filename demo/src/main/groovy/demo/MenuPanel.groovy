package demo

import com.vaadin.ui.Button
import com.vaadin.ui.CustomComponent
import com.vaadin.ui.VerticalLayout
import org.springframework.context.annotation.Scope
import org.vaadin.grails.stereotype.VaadinComponent

import javax.annotation.PostConstruct

@VaadinComponent
//@Scope('ui')
class MenuPanel extends CustomComponent {

    @PostConstruct
    private void init() {
        println "menu panel initialized"
        def root = new VerticalLayout()

        root.addComponent(new Button("Index"))

        compositionRoot = root
    }
}
