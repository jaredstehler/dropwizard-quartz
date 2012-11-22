package com.fiestacabin.dropwizard.quartz.test;

import java.util.concurrent.TimeUnit;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fiestacabin.dropwizard.quartz.Scheduled;

@Scheduled(interval=5, unit=TimeUnit.MINUTES)
public class SampleJob implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
	}

}
