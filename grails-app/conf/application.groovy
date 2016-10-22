// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'testing.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'testing.UserRole'
grails.plugin.springsecurity.authority.className = 'testing.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']],
	[pattern: '/fonts/**',      access: ['permitAll']],
	[pattern: '/logout/**',      access: ['permitAll']],
	[pattern: '/reportDemo/**',      access: ['ROLE_USER','ROLE_ADMIN']],
	[pattern: '/queueKit/**',      access: ['ROLE_USER','ROLE_ADMIN']],
	[pattern: '/reportDemo/**/**',      access: ['ROLE_USER','ROLE_ADMIN']],
	[pattern: '/queueKit/**/**',      access: ['ROLE_USER','ROLE_ADMIN']],
	[pattern: '/testExport/**/**',      access: ['ROLE_USER','ROLE_ADMIN']],
	[pattern: '/apachePoiXls/**/**',      access: ['ROLE_USER','ROLE_ADMIN']],
	[pattern: '/testAddress/**/**',      access: ['ROLE_USER','ROLE_ADMIN']],
	[pattern: '/testAttributes/**/**',      access: ['ROLE_USER','ROLE_ADMIN']],
	[pattern: '/**/test/**', access: ['permitAll']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]


queuekit {
	checkQueueOnStart=true
	keepAliveTime=300

	corePoolSize=3

	maximumPoolSize=3

	maxQueue=100
	preserveThreads = 1

	preservePriority = org.grails.plugin.queuekit.priority.Priority.MEDIUM

	reportDownloadPath='/tmp'
	removalDay=5
	removalDownloadedDay=1
	// CSV will now be handled by Export Plugin so in effect writerType is changed
	// If I were doing a basic stream of csv then it be added to this list
	bufferedWriterTypes=['TSV','tsv']
	//bufferedWriterTypes=['TSV','CSV','tsv','csv']
	reportPriorities = [
			tsvExample1:org.grails.plugin.queuekit.priority.Priority.REALLYSLOW,
			csvExample1:org.grails.plugin.queuekit.priority.Priority.HIGHEST,
			xlsExample1:org.grails.plugin.queuekit.priority.Priority.REALLYSLOW,
			xlsExample2:org.grails.plugin.queuekit.priority.Priority.LOWEST,
			paramsExample:org.grails.plugin.queuekit.priority.Priority.HIGHEST,
			xlsExample3:org.grails.plugin.queuekit.priority.Priority.MEDIUM
	]
	defaultReportsQueue=org.grails.plugin.queuekit.ReportsQueue.ENHANCEDPRIORITYBLOCKING
	durationThreshHold = [
			[hours: 1, minutes: 10, seconds: 2, color: 'blue'],
			[minutes: 1, seconds: 5, color: 'orange'],
			[minutes: 1, seconds: 9, color: '#FF86E3'],
			[minutes: 1, seconds: 10, color: '#C4ABFE'],
			[seconds: 1, color: '#9999CC'],
			[seconds: 12, color: '#FFFFAA'],
			[seconds: 10, color: '#E3E0FA'],
			[seconds: 25, color: '#52FF20'],
			[seconds: 20, color: '#F49AC2'],
			[seconds: 50, color: '#FF4848'],
			[seconds: 35, color: '#9999CC']
	]
	killLongRunningTasks=300

	defaultComparator=false
	useEmergencyExecutor=false
	manualDownloadEnabled=false
	deleteEntryOnDelete=false

	forceFloodControl=2
	//limitUserBelowPriority=0
	//limitUserAbovePriority=0
	disableExamples=false
	standardRunnable=false
	disableUserServicePriorityCheck=false

	//disableUserServicePriorityCheck=false
	hideQueuePriority=false
	hideQueueType=false

}
