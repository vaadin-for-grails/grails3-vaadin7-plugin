package demo

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.ui.Button
import com.vaadin.ui.CustomComponent
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.vaadin.grails.navigator.Navigation
import org.vaadin.grails.stereotype.VaadinComponent

@VaadinComponent
//@Scope('ui')
class WelcomeView extends CustomComponent implements View {

    @Autowired
    MenuPanel menuPanel

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) {
        def root = new VerticalLayout()
        root.setMargin(true)
        root.addComponent(menuPanel)

        def title = new Label("Welcome to Vaadin 7 Plugin for Grails 3.x")
        title.setStyleName("h1 colored")
        root.addComponent(title)
        root.addComponent(new Button("Info", { e ->
            Navigation.navigateTo(InfoView)
        } as Button.ClickListener))

        compositionRoot = root
    }
}
