package com.fiestacabin.dropwizard.quartz;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.lifecycle.Managed;

public class ManagedScheduler implements Managed {
	private static final Logger LOG = LoggerFactory.getLogger(ManagedScheduler.class);

	private Scheduler scheduler;
	private GuiceJobFactory jobFactory;
	private SchedulerConfiguration config;
	
	@Inject
	public ManagedScheduler(Scheduler scheduler, GuiceJobFactory jobFactory,
			SchedulerConfiguration config) {
		this.scheduler = scheduler;
		this.jobFactory = jobFactory;
		this.config = config;
	}

	public void start() throws Exception {
		scheduler.setJobFactory(jobFactory);
		scheduler.start();

		Reflections reflections = new Reflections(config.getBasePackage(), new SubTypesScanner());
		Set<Class<? extends Job>> scheduledClasses = reflections.getSubTypesOf(Job.class);

		for (Class<? extends Job> scheduledClass : scheduledClasses) {
			Scheduled scheduleAnn = scheduledClass
					.getAnnotation(Scheduled.class);
			if (scheduleAnn != null) {
				JobDetail job = newJob(scheduledClass).build();
				Trigger trigger = buildTrigger(scheduleAnn);
				
				LOG.info("Scheduled job {} with trigger {}", job, trigger);
				scheduler.scheduleJob(job, trigger);
			}
		}
	}
	
	public static Trigger buildTrigger(Scheduled ann) {
		TriggerBuilder<Trigger> trigger = newTrigger();

		if (ann.cron() != null && ann.cron().trim().length() > 0) {
			trigger.withSchedule(CronScheduleBuilder.cronSchedule(ann.cron()));
		} else if (ann.interval() != -1) {
			trigger.withSchedule(simpleSchedule()
					.withIntervalInMilliseconds(
							TimeUnit.MILLISECONDS.convert(ann.interval(), ann.unit()))
					.repeatForever()).startNow();
		} else {
			throw new IllegalArgumentException("One of 'cron', 'interval' is required for the @Scheduled annotation");
		}

		return trigger.build();
	}

	public void stop() throws Exception {
		scheduler.shutdown();
	}

}
