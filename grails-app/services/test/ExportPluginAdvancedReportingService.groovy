package test

import grails.util.Holders
import org.grails.plugin.queuekit.ReportsQueue
import org.grails.plugin.queuekit.reports.QueuekitBaseReportsService
import testing.TestAddress
import testing.TestAttributes

class ExportPluginAdvancedReportingService extends QueuekitBaseReportsService {

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


	/**
	 *
	 *
	 * @param out -> Where out is provided by plugin
	 * @param bean ->Where bean is your actual user params from the front end screen
	 * @param queryResults -> QueryResults would be what would be produced by your code
	 * 				In the case of this we are setting it to [something:'aa']
	 * 				above. This then will continue working and hit this block
	 * 				which will carry out real export service task at hand.
     */

	private void actionReport1Report(queue,out,bean,queryResults) {
		String format=bean.f ?: 'html'
		if(format && format != "html"){
			log.debug "Params received  ${bean.f} ${bean.extension} "
			def domain= bean.domainClass
			try {
				if (domain) {
					println "got Domain ${domain}"
					//	def domainClass = Holders.grailsApplication?.domainClasses?.find { it.clazz.simpleName == uc(domain) }?.clazz
					def domainClass = Holders.grailsApplication.getDomainClass(domain)?.clazz
					if (domainClass) {
						println "we have a real domainClass ${domainClass}"
						domainClass.withTransaction {
							Map formatters=[:]
							Map parameters=[:]
							switch (domain) {
								case 'testing.TestAddress':
									println "custom testAddress stuff here"
									//formatters=[:]
									//parameters=[:]
									//bean.something=SomethingElse
									break
								case 'testing.TestAttribues':
									println "custom testAttributes stuff here"
									//What would you like to do
									//formatters=[:]
									//parameters=[:]
									break
							}
							exportService.export(format, (OutputStream) out, domainClass.list(bean),formatters,parameters)
						}
					}
				}
			} catch (Exception e) {
				println "EROROROR_________________________________________"
				super.errorReport(queue,bean)
			}
		}
	}
	private String uc(String s) {
		s.substring(0,1).toUpperCase() + s.substring(1)
	}

	/*
	 * 
	 * Overriding how QueuekitBaseReportsService names it here
	 */
	String getReportName(ReportsQueue queue,bean) {
		return "ExportPlugin-${queue.id}.${bean.extension?:reportExension}"
	}
/*
	void setGrailsApplication(GrailsApplication ga) {
		config = ga.config.queuekit
	}*/

}
