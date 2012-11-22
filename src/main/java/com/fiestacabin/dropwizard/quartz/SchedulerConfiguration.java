package com.fiestacabin.dropwizard.quartz;

public class SchedulerConfiguration {

	private String basePackage;
	
	public SchedulerConfiguration() {}
	
	public SchedulerConfiguration(String basePackage) {
		this.basePackage = basePackage;
	}
	
	public String getBasePackage() {
		return basePackage;
	}
	
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	
}
