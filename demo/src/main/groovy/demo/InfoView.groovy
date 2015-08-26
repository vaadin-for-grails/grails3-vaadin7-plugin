package demo

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.ui.CustomComponent
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.vaadin.grails.stereotype.VaadinComponent

import javax.annotation.PostConstruct

@Component
//@Scope('ui')
class InfoView extends CustomComponent implements View {

    @Autowired
    MenuPanel menuPanel

    @PostConstruct
    void init() {
        println "init info view"
    }

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) {
        def root = new VerticalLayout()
        root.setMargin(true)
        root.addComponent(menuPanel)

        def title = new Label(styleName: "h1 colored", value: "Info")
        root.addComponent(title)

        compositionRoot = root
    }
}
