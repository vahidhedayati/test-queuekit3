package testing

/**
 * Test Address will be used for a more complex demonstrating of sharing a service that binds
 * to Export plugin
 *
 * TestAddress and TestExport will use a combined Service
 *
 * Where as TestExport uses it's own Service that calls the export plugin as required
 *
 */
class TestAddress {

    String name
    String adress
    static constraints = {
    }
}
