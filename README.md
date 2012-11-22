dropwizard-quartz
=================

This is a simple Job Scheduler implementation for dropwizard, integrating Guice and Quartz. The nice thing about it is that it allows you to use @Inject to wire up dependencies in your Job instances, and define your scheduling via a @Scheduled annotation.

Usage
-----

In order to use this framework, you need to add an instance of ManagedScheduler to your Dropwizard environment (or use the dropwizard-guice AutoConfigService). This will search the classpath for Job classes marked with the @Scheduled annotation and register them with the Quartz scheduler.

The @Scheduled annotation has two incantations, one using a cron string, and the other specifying a recurring interval.

Here's an example using the cron syntax, setting up a job to run at midnight every weekday:

    @Scheduled(cron="0 0 0 ? * MON-FRI")
    public class MyJob implements org.quartz.Job {
        @Inject
        public MyJob(MyDep dep){
            this.dep = dep;
        }
   
        void execute(JobExecutionContext context) throws JobExecutionException { /* ... */ }
    }

And here's an example using the interval syntax (setting up a job which runs every 5 minutes):

    @Scheduled(interval=5, unit=TimeUnit.MINUTES)
    public class OtherJob implements org.quartz.Job {
        @Inject
        public MyJob(MyDep dep){
            this.dep = dep;
        }
   
        void execute(JobExecutionContext context) throws JobExecutionException { /* ... */ }
    }
