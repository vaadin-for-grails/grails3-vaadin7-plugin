package demo.ui

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.ui.Button
import com.vaadin.ui.CustomComponent
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import org.vaadin.grails.navigator.Navigation

class WelcomeView extends CustomComponent implements View {

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) {
        def root = new VerticalLayout()
        root.setMargin(true)

        def title = new Label("Welcome to Vaadin 7 Plugin for Grails 3.x")
        title.setStyleName("h2 colored")
        root.addComponent(title)
        root.addComponent(new Button("Goto Book Demo", { e ->
            Navigation.navigateTo(BookView)
        } as Button.ClickListener))

        root.addComponent(new Button("1"){{styleName = "friendly"}})
        root.addComponent(new Button("2"){{styleName = "danger"}})
        root.addComponent(new Button("3"){{styleName = "primary"}})
        root.addComponent(new Button("4"){{styleName = "default"}})
        compositionRoot = root
    }
}
