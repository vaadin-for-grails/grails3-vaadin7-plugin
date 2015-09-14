import org.apache.commons.lang.StringUtils

description("Creates a new Vaadin widgetset") {
    usage "grails create-vaadin-widgetset [WIDGETSET NAME]"
    argument name:'widgetsetName', description:"The name of the widgetset", required: true
    flag name: 'force', description: "Whether to overwrite existing files"
}

def widgetsetName = args[0]
def overwrite = flag('force') ? true : false

String widgetsetFilePath = StringUtils.replace(widgetsetName, ".", "/") + ".gwt.xml"

println widgetsetFilePath
render  template: "widgetset.gwt.xml.template",
        destination: file("src/main/groovy/${widgetsetFilePath}"),
        overwrite: overwrite

