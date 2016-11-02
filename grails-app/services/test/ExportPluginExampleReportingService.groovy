package test

import org.grails.plugin.queuekit.ReportsQueue
import org.grails.plugin.queuekit.reports.QueuekitBaseReportsService
import testing.TestExport

class ExportPluginExampleReportingService extends QueuekitBaseReportsService {


	def exportService
	def runReport(ReportsQueue queue,Map params) {
	runReport(queue,[something:'aa'],params)
	}
	// This doesn't matter so much so long as it meets the Type that is not of
	// config value of config.bufferedWriterTypes
	// Since it needs to call the other method in QueuekitBaseReportsService
	// Actual fileName extension is overriden right at the very bottom of this
	// class in getReportName by bean.extension (this ensures file is correctly labelled
	String getReportExension() {
		return 'xls'
	}

	def actionInternal(ReportsQueue queue,out,bean, queryResults,Locale locale) {
		actionReport1Report(queue,out,bean,queryResults)
	}


	private void actionReport1Report(queue,out,bean,queryResults) {
		String format=bean.f ?: 'html'
		log.debug "Params received  ${bean.f} ${bean.extension} "
		if(format && format != "html"){
			try {
				TestExport.withTransaction {
					exportService.export(format, (OutputStream) out, TestExport.list(bean), [:], [:])
				}
			} catch (Exception e) {
				println "EROROROR_________________________________________"
				super.errorReport(queue,bean)
			}
		}
	}
	
	/*
	 * 
	 * Overriding how QueuekitBaseReportsService names it here
	 */
	String getReportName(ReportsQueue queue,bean) {
		return "ExportPlugin-${queue.id}.${bean.extension?:reportExension}"
	}

}
