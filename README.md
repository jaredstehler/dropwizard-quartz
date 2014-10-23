dropwizard-quartz
=================

This is a simple Job Scheduler implementation for dropwizard, integrating Guice and Quartz. The nice thing about it is that it allows you to use @Inject to wire up dependencies in your Job instances, and define your scheduling via a @Scheduled annotation.

Usage
-----

### Maven Central Dependency ###

    <dependency>
        <groupId>com.fiestacabin.dropwizard.quartz</groupId>
        <artifactId>dropwizard-quartz</artifactId>
        <version>0.7.1</version>
    </dependency>

In order to use this framework, you need to add an instance of ManagedScheduler to your Dropwizard environment (or use the dropwizard-guice AutoConfigService). This will search the classpath for Job classes marked with the @Scheduled annotation and register them with the Quartz scheduler.

### Example Integration with dropwizard-guice ###

The main service class, which constructs as an auto config service including the local app's base package as well as that of the managed scheduler class, so it will automatically register with the dropwizard environment:

```java
public class DwQuartzService extends AutoConfigService<DwQuartzConfiguration> {

  public DwQuartzService() {
    super("dw-quartz", DwQuartzService.class.getPackage().getName(), 
        ManagedScheduler.class.getPackage().getName());
  }
  
  @Override
  protected Injector createInjector(DwQuartzConfiguration configuration) {
    return super.createInjector(configuration).createChildInjector(new DwQuartzModule());
  }
  
  public static void main(String[] args) throws Exception {
    new DwQuartzService().run(args);
  }

}
```

Next, the app's guice module defines a provider for the quartz scheduler, and binds an instance of the scheduler config:

```java
public class DwQuartzModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SchedulerConfiguration.class).toInstance(new SchedulerConfiguration("sandbox"));
  }

  @Provides
  @Singleton
  Scheduler provideScheduler() throws SchedulerException {
    return StdSchedulerFactory.getDefaultScheduler();
  }

}
```

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
