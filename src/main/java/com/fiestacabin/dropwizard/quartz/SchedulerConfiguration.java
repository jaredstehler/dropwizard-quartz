package com.fiestacabin.dropwizard.quartz;

import java.util.TimeZone;

public class SchedulerConfiguration {

	private String basePackage;
	private TimeZone timezone;
	
	public SchedulerConfiguration() {}
	
	public SchedulerConfiguration(String basePackage) {
		this.basePackage = basePackage;
		this.timezone = TimeZone.getDefault();
	}
	
	public String getBasePackage() {
		return basePackage;
	}

	public TimeZone getTimezone() {
    return timezone;
  }
	
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public void setTimezone(String timezoneId) {
    this.timezone = TimeZone.getTimeZone(timezoneId);
  }

}
