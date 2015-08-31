description("Creates a new Vaadin theme") {
    usage "grails create-vaadin-theme [THEME NAME]"
    argument name:'Theme name', description:"The name of the Vaadin theme"
}

def themeName = args[0]
render  template: "themes/custom/styles.scss.template",
        destination: file( "src/main/webapp/VAADIN/themes/$themeName/styles.scss"),
        model: [themeName: themeName]