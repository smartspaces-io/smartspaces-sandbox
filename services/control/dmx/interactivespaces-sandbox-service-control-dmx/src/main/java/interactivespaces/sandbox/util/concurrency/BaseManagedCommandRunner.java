/**
 *
 */
package interactivespaces.sandbox.util.concurrency;

import interactivespaces.util.concurrency.ManagedCommand;
import interactivespaces.util.concurrency.ManagedCommands;
import interactivespaces.util.concurrency.RuntimeState;

import org.apache.commons.logging.Log;

/**
 * A superclass that simplifies implementing a {@link Runner} implementation.
 *
 * @author Keith M. Hughes
 */
public abstract class BaseManagedCommandRunner implements Runner {

  /**
   * The managed commands factory.
   */
  private ManagedCommands managedCommands;

  /**
   * The logger to use.
   */
  private Log log;

  /**
   * The runnable for the runner.
   */
  private Runnable runnable;

  /**
   * The managed command for the runner.
   */
  private ManagedCommand command;

  /**
   * Runtime state of the runner.
   */
  private RuntimeState runtimeState = RuntimeState.READY;

  /**
   * The state lock.
   */
  private Object stateLock = new Object();

  /**
   * Any exception which may have happened during execution.
   */
  private Throwable exception;

  /**
   * Construct the runner.
   *
   * @param managedCommands
   *          the managed commands to use
   * @param log
   *          the logger to use
   */
  public BaseManagedCommandRunner(ManagedCommands managedCommands, Log log) {
    this.managedCommands = managedCommands;
    this.log = log;

    runnable = new Runnable() {

      @Override
      public void run() {
        performTask();
      }
    };
  }

  @Override
  public void startup() {
    synchronized (stateLock) {
      if (runtimeState.isRunning()) {
        return;
      }

      runtimeState = RuntimeState.RUNNING;

      onStartup();
      command = newManagedCommand(managedCommands, runnable);
    }
  }

  @Override
  public void shutdown() {
    performShutdown(RuntimeState.SHUTDOWN);
  }

  @Override
  public boolean isRunning() {
    synchronized (stateLock) {
      return runtimeState.isRunning();
    }
  }

  @Override
  public RuntimeState getRuntimeState() {
    synchronized (stateLock) {
      return runtimeState;
    }
  }

  @Override
  public Throwable getRuntimeStateException() {
    synchronized (stateLock) {
      return exception;
    }
  }

  /**
   * Perform the runner's task.
   */
  protected abstract void performTask();

  /**
   * Create a new managed command for the runner.
   *
   * @param managedCommands
   *          the managed commands to use
   * @param runnable
   *          the runnable for the managed command
   *
   * @return a new managed command
   */
  protected abstract ManagedCommand newManagedCommand(ManagedCommands managedCommands, Runnable runnable);

  /**
   * Perform any additional startup.
   */
  protected void onStartup() {
    // Default is do nothing.
  }

  /**
   * Shut down the runner successfully. This is meant to be called from the subclass.
   *
   * <p>
   * If the run is stopped, the runner is marked as crashed.
   *
   * @param message
   *          the message for the error
   * @param e
   *          any exception that caused the error, can be {@code null}
   * @param stopRun
   *          {@code true} if the runner should be stopped
   */
  protected void handleTaskError(String message, Throwable e, boolean stopRun) {
    synchronized (stateLock) {
      if (stopRun) {
        exception = e;

        performShutdown(RuntimeState.CRASH);
      }

      getLog().error(message, e);
    }
  }

  /**
   * Shut down the runner successfully. This is meant to be called from the subclass.
   */
  protected void shutdownSuccessfullyFromTask() {
    performShutdown(RuntimeState.SHUTDOWN);
  }

  /**
   * Do a shutdown and set the new state of the runner.
   *
   * @param newState
   *          the new state
   */
  private void performShutdown(RuntimeState newState) {
    synchronized (stateLock) {
      if (runtimeState.isRunning()) {
        command.cancel();
        command = null;
      }

      runtimeState = newState;
    }
  }

  /**
   * Get the logger.
   *
   * @return the logger
   */
  protected Log getLog() {
    return log;
  }
}
