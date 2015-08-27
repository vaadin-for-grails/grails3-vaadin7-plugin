package demo

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.ui.Button
import com.vaadin.ui.CustomComponent
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import org.vaadin.grails.data.fieldgroup.DomainFieldGroup
import org.vaadin.grails.data.util.DomainItem
import org.vaadin.grails.data.util.DomainItemContainer

class BookView extends CustomComponent implements View {

    class BookEditor extends Window {

        DomainFieldGroup<Book> fieldGroup

        BookEditor() {
            fieldGroup = new DomainFieldGroup(Book)
            def form = new FormLayout()
            form.setSizeUndefined()
            form.setMargin(true)

            form.addComponent(fieldGroup.buildAndBind('Title', 'title'))
            form.addComponent(fieldGroup.buildAndBind('ISBN', 'isbn'))
            form.addComponent(fieldGroup.buildAndBind('Author', 'author'))

            def saveButton = new Button("Save", new Button.ClickListener() {
                @Override
                void buttonClick(Button.ClickEvent event) {
                    if (fieldGroup.commit(true)) {
                        def item = fieldGroup.itemDataSource
                        item.save(true)
                        reloadBooks()
                        close()
                    }

                }
            })
            def closeButton = new Button("Close", new Button.ClickListener() {
                @Override
                void buttonClick(Button.ClickEvent event) {
                    fieldGroup.discard()
                    close()
                }
            })
            def buttonBar = new HorizontalLayout()
            buttonBar.setSpacing(true)
            saveButton.styleName = "primary"
            buttonBar.addComponent(saveButton)
            closeButton.styleName = "quiet"
            buttonBar.addComponent(closeButton)
            form.addComponent(buttonBar)
            content = form
            caption = "Book"
        }

        void open(DomainItem<Book> itemDataSource) {
            fieldGroup.itemDataSource = itemDataSource

            def ui = com.vaadin.ui.UI.current
            if (!ui.windows.contains(this)) {
                ui.addWindow(this)
                center()
            }
        }

        void open() {
            open(new DomainItem(Book))
        }
    }

    DomainItemContainer<Book> bookContainer
    Grid bookGrid

    BookView() {
        def editor = new BookEditor()

        def root = new VerticalLayout()
        root.setMargin(true)
        root.setSpacing(true)

        def title = new Label("Books")
        title.styleName = "h1 colored"
        root.addComponent(title)

        bookGrid = new Grid()
        bookGrid.setSelectionMode(Grid.SelectionMode.MULTI)
        bookContainer = new DomainItemContainer(Book)
        root.addComponent(bookGrid)

        def createButton = new Button("New Book", new Button.ClickListener() {
            @Override
            void buttonClick(Button.ClickEvent event) {
                editor.open()
            }
        })

        def deleteButton = new Button("Delete Book(s)", new Button.ClickListener() {
            @Override
            void buttonClick(Button.ClickEvent event) {
                bookGrid.selectedRows.each { itemId ->
                    def item = bookContainer.getItem(itemId)
                    item.delete(true)
                }
                bookGrid.selectionModel.reset()
                reloadBooks()
            }
        })

        def buttonBar = new HorizontalLayout()
        buttonBar.setSpacing(true)
        createButton.styleName = "primary"
        buttonBar.addComponent(createButton)
        deleteButton.styleName = "danger"
        buttonBar.addComponent(deleteButton)

        root.addComponent(buttonBar)
        compositionRoot = root
    }

    void reloadBooks() {
        bookContainer.removeAllItems()
        Book.list().each { book ->
            bookContainer.addItem(book)
        }
        bookGrid.containerDataSource = bookContainer
    }

    @Override
    void enter(ViewChangeListener.ViewChangeEvent event) {
        reloadBooks()
    }
}
