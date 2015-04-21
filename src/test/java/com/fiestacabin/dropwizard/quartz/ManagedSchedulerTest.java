package com.fiestacabin.dropwizard.quartz;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import com.fiestacabin.dropwizard.quartz.test.SampleJob;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class ManagedSchedulerTest {

	@Mock private Scheduler scheduler;
	@Mock private GuiceJobFactory jobFactory;
	private SchedulerConfiguration configuration;
	
	ManagedScheduler managedScheduler;
	
	@Before
	public void initMocks() {
	  configuration = new SchedulerConfiguration("com.fiestacabin.dropwizard.quartz.test");
	  configuration.setTimezone("UTC");
	  
	  managedScheduler = new ManagedScheduler(scheduler, jobFactory, configuration);
	}
	
	@Test
	public void itParsesRepeatingCronExpressions() throws Exception {
		Interval i = getFireInterval(FiveMinuteInterval.class);
		assertEquals(5, i.toDuration().getStandardMinutes());
	}

	@Test
	public void itParsesDailyCronExpressions() throws Exception {
		Interval i = getFireInterval(DailyMorningInterval.class);
		assertEquals(9, i.getStart().toDateTime(DateTimeZone.UTC).getHourOfDay());
		assertEquals(1, i.toDuration().getStandardDays());
	}
	
	@Test
	public void itUsesConfiguredTimeZone() throws Exception {
	  configuration.setTimezone("America/New_York");
	  
	  Interval i = getFireInterval(DailyMorningInterval.class);
    assertEquals(13, i.getStart().toDateTime(DateTimeZone.UTC).getHourOfDay());
    assertEquals(1, i.toDuration().getStandardDays());
	}
	
	@Test
	public void itParsesSpecificIntervals() throws Exception {
		assertEquals(5, getFireInterval(FiveSecondInterval.class).toDuration().getStandardSeconds());
	}

	@Test(expected=IllegalArgumentException.class)
	public void itExpectsEitherCronOrInterval() throws Exception {
		getFireInterval(Invalid.class);
	}
	
	@Test
	public void itDiscoversJobClasses() throws Exception {
		managedScheduler.start();
		
		verify(scheduler).setJobFactory(jobFactory);
		verify(scheduler).start();
		
		ArgumentCaptor<JobDetail> jobDetail = ArgumentCaptor.forClass(JobDetail.class);
		verify(scheduler, atLeast(1)).scheduleJob(jobDetail.capture(), any(Trigger.class));

		Set<Class<? extends Job>> jobClasses = Sets.newHashSet();
		for(JobDetail jd : jobDetail.getAllValues()) {
			jobClasses.add(jd.getJobClass());
		}
		
		assertThat(jobClasses, hasItem(SampleJob.class));
	}
	
	@Test
	public void itShutsDownQuartz() throws Exception {
		managedScheduler.stop();
		verify(scheduler).shutdown();
	}
	
	private Interval getFireInterval(Class<?> c){
		Scheduled s = c.getAnnotation(Scheduled.class);
		Trigger t = managedScheduler.buildTrigger(s);
		
		Date start = t.getStartTime();
		Date next = t.getFireTimeAfter(start);
		
		start = next;
		next = t.getFireTimeAfter(start);
		
		Interval i = new Interval(start.getTime(), next.getTime());
		return i;
	}
	
	@Scheduled(cron="0 */5 * * * ?")
	public static class FiveMinuteInterval {}
	
	@Scheduled(cron="0 0 9 ? * *")
	public static class DailyMorningInterval {}
	
	@Scheduled(interval=5, unit=TimeUnit.SECONDS)
	public static class FiveSecondInterval {}
	
	@Scheduled
	public static class Invalid {}
	
}
