/*
 * Copyright (C) 2015 Keith M. Hughes
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.smartspaces.sandbox.service.scheduler.action.internal.quartz;

import io.smartspaces.SmartSpacesException;
import io.smartspaces.sandbox.service.action.ActionService;
import io.smartspaces.sandbox.service.scheduler.action.ActionSchedulerService;
import io.smartspaces.service.BaseSupportedService;

import org.apache.commons.logging.Log;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * A {@link ActionSchedulerService} which uses quartz.
 *
 * @author Keith M. Hughes
 */
public class QuartzActionSchedulerService extends BaseSupportedService implements
    ActionSchedulerService {

  /**
   * JobMap property for the action name.
   */
  public static final String JOB_MAP_PROPERTY_ACTION_NAME = "action.name";

  /**
   * JobMap property for the action source.
   */
  public static final String JOB_MAP_PROPERTY_ACTION_SOURCE = "action.source";

  /**
   * The quartz scheduler.
   */
  private Scheduler scheduler;

  /**
   * The action service.
   */
  private ActionService actionService;

  @Override
  public String getName() {
    return ActionSchedulerService.SERVICE_NAME;
  }

  @Override
  public void startup() {
    try {
      // TODO(keith): Get Smart Spaces thread pool in here.
      Properties properties = new Properties();
      properties.put("org.quartz.scheduler.skipUpdateCheck", "true");
      properties.put("org.quartz.threadPool.threadCount", "10");

      SchedulerFactory schedulerFactory = new StdSchedulerFactory(properties);
      scheduler = schedulerFactory.getScheduler();
      scheduler.setJobFactory(new MyJobFactory());

      scheduler.start();
    } catch (SchedulerException e) {
      throw new SmartSpacesException("Could not start Smart Spaces scheduler", e);
    }
  }

  @Override
  public void shutdown() {
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {
      throw new SmartSpacesException("Could not shutdown Smart Spaces scheduler", e);
    }
  }

  @Override
  public void schedule(String jobName, String groupName, String actionSource, String actionName,
      Date when) {
    schedule(jobName, groupName, actionSource, actionName, null, when);
  }

  @Override
  public void schedule(String jobName, String groupName, String actionSource, String actionName,
      Map<String, Object> data, Date when) {
    try {
      JobDetail detail = newJobDetail(jobName, groupName, actionSource, actionName, data);

      Trigger trigger =
          TriggerBuilder.newTrigger().withIdentity(TriggerKey.triggerKey(jobName, groupName))
              .startAt(when).build();
      scheduler.scheduleJob(detail, trigger);

      getSpaceEnvironment().getLog().info(
          String.format("Scheduled job %s:%s for %s\n", groupName, jobName, new SimpleDateFormat(
              "MM/dd/yyyy@HH:mm:ss").format(when)));
    } catch (SchedulerException e) {
      throw new SmartSpacesException(String.format(
          "Unable to schedule job %s:%s for action %s:%s", groupName, jobName, actionSource,
          actionName), e);
    }
  }

  @Override
  public void scheduleWithCron(String jobName, String groupName, String actionSource,
      String actionName, String schedule) {
    scheduleWithCron(jobName, groupName, actionSource, actionName, null, schedule);
  }

  @Override
  public void scheduleWithCron(String jobName, String groupName, String actionSource,
      String actionName, Map<String, Object> data, String schedule) {
    try {
      JobDetail detail = newJobDetail(jobName, groupName, actionSource, actionName, data);

      CronTrigger trigger =
          TriggerBuilder.newTrigger().withIdentity(TriggerKey.triggerKey(jobName, groupName))
              .withSchedule(CronScheduleBuilder.cronSchedule(schedule)).build();

      scheduler.scheduleJob(detail, trigger);
    } catch (Exception e) {
      throw new SmartSpacesException(String.format(
          "Unable to schedule job %s:%s for action %s:%s", groupName, jobName, actionSource,
          actionName), e);
    }
  }

  /**
   * Create a new job detail.
   * 
   * @param jobName
   *          the name of the job
   * @param groupName
   *          the group name for the job, can be {@code null}
   * @param actionSource
   *          the source of the action
   * @param actionName
   *          the action name
   * @param data
   *          data for the call, can be {@code null}
   * 
   * @return the Quartz job detail
   */
  private JobDetail newJobDetail(String jobName, String groupName, String actionSource,
      String actionName, Map<String, Object> data) {
    JobBuilder jobBuilder =
        JobBuilder.newJob(ActionSchedulerJob.class).withIdentity(jobName, groupName);

    JobDetail detail = jobBuilder.build();

    JobDataMap jobDataMap = detail.getJobDataMap();
    jobDataMap.put(JOB_MAP_PROPERTY_ACTION_SOURCE, actionSource);
    jobDataMap.put(JOB_MAP_PROPERTY_ACTION_NAME, actionName);

    if (data != null) {
      jobDataMap.putAll(data);
    }

    return detail;
  }

  /**
   * The job factory to use for Quartz job creation.
   * 
   * @author Keith M. Hughes
   */
  public class MyJobFactory implements JobFactory {

    /**
     * The parameter types for SmartSpacesSchedulerJob subclass
     * constructors.
     */
    private final Class<?>[] SMARTSPACES_JOB_CONSTRUCTOR_PARAMETER_TYPES = new Class<?>[] {
        ActionService.class, Log.class };

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
      Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
      try {
        if (InteractiveSpacesSchedulerJob.class.isAssignableFrom(jobClass)) {
          Constructor<? extends Job> constructor =
              jobClass.getConstructor(SMARTSPACES_JOB_CONSTRUCTOR_PARAMETER_TYPES);
          if (constructor != null) {
            return constructor.newInstance(actionService, getSpaceEnvironment().getLog());
          } else {
            throw new SchedulerException(String.format(
                "Interactive Spaces job class %s does not have a proper constructor",
                jobClass.getName()));
          }
        } else {
          return jobClass.getConstructor().newInstance();
        }
      } catch (Exception e) {
        throw new SchedulerException("Could not instantiate job class " + jobClass, e);
      }
    }
  }

  public static abstract class InteractiveSpacesSchedulerJob implements Job {

    /**
     * The action service.
     */
    private ActionService actionService;

    /**
     * Logger for the class
     */
    private Log log;

    public InteractiveSpacesSchedulerJob(ActionService actionService, Log log) {
      this.actionService = actionService;
      this.log = log;
    }

    protected Log getLog() {
      return log;
    }

    protected ActionService getActionService() {
      return actionService;
    }
  }

  /**
   * The job which the scheduler will run.
   *
   * @author Keith M. Hughes
   */
  public static class ActionSchedulerJob extends InteractiveSpacesSchedulerJob {

    public ActionSchedulerJob(ActionService actionService, Log log) {
      super(actionService, log);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      try {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        getActionService().performAction(jobDataMap.getString(JOB_MAP_PROPERTY_ACTION_SOURCE),
            jobDataMap.getString(JOB_MAP_PROPERTY_ACTION_NAME), jobDataMap);
      } catch (Exception e) {
        getLog().error("Could not run scheduled job", e);
      }
    }
  }

  /**
   * Set the action service to use.
   * 
   * @param actionService
   *          the action service
   */
  public void setActionService(ActionService actionService) {
    this.actionService = actionService;
  }
}
