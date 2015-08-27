description("Creates a new Vaadin theme") {
    usage "grails create-vaadin-theme [THEME NAME]"
    argument name:'Theme name', description:"The name of the Vaadin theme"
}

def themeName = args[0]
render  template: "styles.scss",
        destination: file( "src/main/resources/VAADIN/themes/$themeName/styles.scss"),
        model: [themeName: themeName]