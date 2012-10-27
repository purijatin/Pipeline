package os;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;


/**
 * Handler called in the respective thread, will generate an id number for that handler.
 * This id number will be the same for all handlers of the same queue.
 * @author Jatin
 * @version 1.0
 */
public class Handler 
{
    /**
     * The id so that the handle can map to the respective queue.
     */
    private final Double id;
    
    public Handler()
    {
        id = Looper.getIdForQueue();
    }
    
    /**
     * When the Looper runs the loop, then the runnable is executed. The runnable
     * is added to the end of the queue.
     * If the runnable throws an exception, the Looper shall stop executing the current
     * runnable and will execute the next one.
     * 
     * @param runnable The Runnable to be executed
     * @return 
     */public boolean post(Runnable runnable)
    {
        return Looper.post(id, runnable);
    }
    /**
      * When the Looper runs the loop, then the runnable is executed. The runnable
      * is added to the end of the queue.
      * If the callable throws an exception, the exception can be known
      * by calling Future#get(); It shall throw the exception which is thrown while executing it.
      * See {@link java.util.concurrent.Future} for further info.
      * @param <T> The result type returned by this Future's get method
      * @param call The callable to be executed.
      * @return The Future<T> to the Callable
      */
    public <T> Future<T> execute(Callable<T> call)
    {
        return Looper.execute(id, call);
    }
    
    /**
      * When the Looper runs the loop, then the runnable is executed. The runnable
      * is added to the end of the queue.
      * If the callable throws an exception, the exception can be known
      * by calling Future#get(); It shall throw the exception which is thrown while executing it.
      * See {@link java.util.concurrent.Future} for further info.
     * @param <T> The result type returned by this Future's get method
      * @param runnable The callable to be executed.
     * @param result The result which will be returned, when Future#get is called.
      * @return The Future<T> to the Runnable
      */
    public <T> Future<T> execute(Runnable runnable, T result)
    {
        return Looper.execute(id, runnable, result);
    }
    
    /**
     * It functions very similar to {@link execute(Runnable,T result)}. The only 
     * difference is that when this method is called after shutdown, for some very
     * small time (unpredictable) it might still allow runnable's to be added
     * to the queue. But this in turn is faster than {@link execute(Runnable,T result)}
     * due to removal of unnecessary synchronization whose job was to make sure
     * that no element is added after shutdown is called
     * 
     * @param <T> The result type returned by this Future's get method
     * @param runnable runnable The callable to be executed.
     * @param result The result which will be returned, when Future#get is called.
     * @return The Future<T> to the Runnable
     */
    public <T> Future<T> executeLoosely(Runnable runnable, T result)
    {
        return Looper.executeLoosely(id, runnable, result);
    }
    
        
    
    public void shutDown()
    {
        Looper.shutDown(id);
    }

}
