package com.fiestacabin.dropwizard.quartz;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;
import org.quartz.spi.TriggerFiredBundle;

import com.fiestacabin.dropwizard.quartz.test.SampleJob;
import com.google.inject.Injector;


public class GuiceJobFactoryTest {

	@Mock Injector injector;
	@Mock TriggerFiredBundle bundle;
	
	@InjectMocks GuiceJobFactory jobFactory;
	
	@Before 
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testJobCreation() throws Exception {
		JobDetailImpl jd = new JobDetailImpl();
		jd.setJobClass(SampleJob.class);
		
		when(bundle.getJobDetail()).thenReturn(jd);
		
		SampleJob j = new SampleJob();
		when(injector.getInstance(SampleJob.class)).thenReturn(j);
		
		Job newJob = jobFactory.newJob(bundle, mock(Scheduler.class));
		assertSame(j, newJob);
	}
	
}
