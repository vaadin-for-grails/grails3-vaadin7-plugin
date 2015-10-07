package demo.ui

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.ui.*
import grails.plugins.vaadin.server.UIAttributes
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.vaadin.grails.navigator.Navigation

import javax.annotation.PostConstruct

@Component
@Scope('vaadin-session')
class WelcomeView extends CustomComponent implements View {

    Button changeTitleButton

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) { }

    @PostConstruct
    void init() {
        def root = new VerticalLayout()
        root.setMargin(true)
        root.setSpacing(true)

        Label title = new Label("Welcome to Vaadin 7 Plugin for Grails 3.x")
        title.setStyleName("h2 colored")
        root.addComponent(title)
        root.addComponent(new Button("Goto Book Demo", { e ->
            Navigation.navigateTo(fragment: "book/a=1")
        } as Button.ClickListener))

        UIAttributes.current.setAttribute(Integer, 7)

        def textField = new TextField("Title")
        root.addComponent(textField)
        changeTitleButton = new Button()
        changeTitleButton.caption = "Change Title"
        changeTitleButton.styleName = 'primary'
        changeTitleButton.addClickListener({
            title.value = textField.value
        })
        root.addComponent(changeTitleButton)
        root.addComponent(new Button("Show params", new Button.ClickListener() {
            @Override
            void buttonClick(Button.ClickEvent e) {
                Notification.show(Navigation.currentParams.toString())
            }
        }))

        compositionRoot = root
    }

}
