package com.sohungry.search.indexer.job;

import javax.annotation.PostConstruct;

import org.parse4j.Parse;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.sohungry.search.parse.config.ParseConfig;

/**
 * Periodically re-index all objects
 * 
 * @author shiyan
 *
 */
@Component
public class IndexScheduleJob {

	@PostConstruct
	public void scheduleJob() {
		
		initializeParse();

//		JobDetail job = new JobDetail();
//		job.setName("dummyJobName");
//		job.setJobClass(HelloJob.class);
//
//		// configure the scheduler time
//		SimpleTrigger trigger = new SimpleTrigger();
//		trigger.setStartTime(new Date(System.currentTimeMillis() + 1000));
//		trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//		trigger.setRepeatInterval(30000);
//
//		// schedule it
//		Scheduler scheduler;
//		try {
//			scheduler = new StdSchedulerFactory().getScheduler();
//			scheduler.start();
//			scheduler.scheduleJob(job, trigger);
//		} catch (SchedulerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private void initializeParse() {
		Parse.initialize(ParseConfig.APP_ID, ParseConfig.APP_REST_API_ID);
	}

	public class HelloJob implements Job {
		public void execute(JobExecutionContext context) throws JobExecutionException {

			System.out.println("Hello Quartz!");

		}

	}

}
