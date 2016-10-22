package testing

import org.springframework.web.servlet.support.RequestContextUtils

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class TestAddressController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    //def exportService
    def myUserService
    def queueReportService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        String format=params.f  ?: params.extensions ?: 'html'
        if(format && format != "html"){
            def locale = RequestContextUtils.getLocale(request)
            def userId = myUserService.currentuser
            String reportName = 'exportPluginAdvanced'
            /**
             * ! -- IMPORTANT
             * In order to let this dynamic exportPluginAdvancedReportingService pickup the correct domainClass
             * We must send an additional params as part of reports calls and bind in the actual domainClass we would be listing
             * just like shown here
             *
             */
            params.domainClass=TestAddress.class

            log.debug "Sending task as default priority to queueReportService instead of exportService.export"
            //def queue = queueReportService.buildReport(reportName,userId , locale, params,Priority.HIGH,ReportsQueue.PRIORITYBLOCKING)
            def queue = queueReportService.buildReport(reportName,userId , locale, params)

            flash.message = g.message(code: 'queuekit.reportQueued.label', args: [reportName, queue?.id])

            /**
             * How you would normally export using Export plugin
             * Changed to above to go through queuekit plugin and queue request instead
             *
             * Take a look at ExportPluginAdvancedReportingService to see how you can do the same
             *
             */
            // response.contentType = grailsApplication.config.grails.mime.types[format]
            // response.setHeader("Content-disposition", "attachment; filename=books.${params.extension}")
            // exportService.export(format, response.outputStream,TestExport.list(params), [:], [:])
        }
        respond TestAddress.list(params), model:[testAddressCount: TestAddress.count()]
    }

    def show(TestAddress testAddress) {
        respond testAddress
    }

    def create() {
        respond new TestAddress(params)
    }

    @Transactional
    def save(TestAddress testAddress) {
        if (testAddress == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (testAddress.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond testAddress.errors, view:'create'
            return
        }

        testAddress.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'testAddress.label', default: 'TestAddress'), testAddress.id])
                redirect testAddress
            }
            '*' { respond testAddress, [status: CREATED] }
        }
    }

    def edit(TestAddress testAddress) {
        respond testAddress
    }

    @Transactional
    def update(TestAddress testAddress) {
        if (testAddress == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (testAddress.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond testAddress.errors, view:'edit'
            return
        }

        testAddress.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'testAddress.label', default: 'TestAddress'), testAddress.id])
                redirect testAddress
            }
            '*'{ respond testAddress, [status: OK] }
        }
    }

    @Transactional
    def delete(TestAddress testAddress) {

        if (testAddress == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        testAddress.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'testAddress.label', default: 'TestAddress'), testAddress.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'testAddress.label', default: 'TestAddress'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
