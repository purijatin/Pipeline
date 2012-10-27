package os;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;

/** 
 *<p> Class used to run a message loop for a thread. 
 * Threads by default do not have a Runnable/Callable loop associated with them; to create one, 
 * call prepare() in the thread that is to run the loop, and then loop() to have it process messages until 
 * the loop is stopped.</p>
 * <p>Most interaction with a message loop is through the Handler class.</p>
 * <p>This is a typical example of the implementation of a Looper thread, 
 * using the separation of prepare() and loop() to create an initial Handler to communicate with the Looper. </p>
 *  <p>If the runnable throws an exception, the Looper shall stop executing the current
 *  runnable and will execute the next one.</p>
 * @author Jatin
 * @version 1.0
 */
public class Looper {

    /**
     * 
     * <p> Thread Safety is done on the basis of the id number which the handler passes. ie it is synchronized on the Double
     * obtained from the handler. The focus was that, shutDown needs to be threadsafe and it all matters on the
     * queue, so by synchronizing on the Double, we are indirectly synchronizing on the Queue and hence safe.</p>
     * 
     *<p> The Shutdown policy is that, Once the respective handler#shutDown() is called, no more elements will be accepted
     * to add in the queue. It shall return a false, when the adding the element was unsuccessful.</p>
     * <p> If the number of threads considerably increase(ie more than say 2 pow(20+), then the system might fail.
     * This problem can be solved later, by replacing a Double in ThreadLocal to an array of Double's depending on need.
     * </p>
     */
    private static final ThreadLocal<Double> tlocal = new ThreadLocal<Double>() {
        @Override
        protected Double initialValue() {
            return null;
        }
    };
    /**
     * Atomicity guarded by this Boolean is by default false.
     * Which means that the current queue is not shut down.
     */
    private static final ConcurrentHashMap<Double, Boolean> mapBoolean =
            new ConcurrentHashMap<Double, Boolean>();
    private static final ConcurrentHashMap<Double, LinkedBlockingQueue<Runnable>> map = new ConcurrentHashMap<Double, LinkedBlockingQueue<Runnable>>();
    /**
     * The sole intention of having this variable is to recheck if shutDown is called.
     * 
     * Looper should loop and should wait for event for pollTime time.
     * THe value is in milli seconds. A lot can be done about the value to be set here.
     * Kind of like Congestion Control algorithm can be applied here,depending on traffic Will be done later
     */
    private static final boolean shut = false;
    /*
     * This is used as a poison pill, ie once this is added to any queue,
     * and when looper in loop comes till the POISON_PILL, then no more runnables
     * are executed.
     */
    private static final Runnable POISON_PILL = new Runnable() {
        @Override
        public void run() {
        }
    };

    private Looper() {
    }

    /**
     * <p>This method can only be called Once. If called more than once shall throw an exception.
     *  It prepares the Queue for the thread called so that handle can access the queue.</p>
     */
    //on profiling observed that, it takes a lot of time to process the below code.
    public static void prepare() {
        if (tlocal.get() != null) {
            throw new RuntimeException("Only One Looper allowed per thread");// tested (correct)
        }
        final Double rand = (Math.random()) * Double.MAX_VALUE * (Math.random() > 0.5 ? -1 : 1);
        //There is very little probability, but it might 
        //so happen that the random number is same to some number generated before, 
        //if that happens, then the system fails completely. some another mechanism is also needed.
        tlocal.set(rand);//System.out.println(rand);
        map.put(rand, new LinkedBlockingQueue<Runnable>()); // safely published (the queue)
        mapBoolean.put(rand, shut);
    }

    /**
     * 
     * @param id The id of the handler. Needed to put the runnable in the respective queue.
     * @param runnable The Runnable which needs to be added to the respective queue.
     * @return If adding the runnable to queue was successful. It might not be a success
     * if shutdown was called.
     */
    //@Guarded-By : this
    protected static boolean post(final Double id, Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("Runnable is null");
        }
        //see execute(Double id,Callable<T> call
        synchronized (id) {
            if (mapBoolean.get(id)) {
                return false;
                }
            //}  
            LinkedBlockingQueue<Runnable> queue = map.get(id);
            queue.add(runnable);//System.out.println("added");
            return true;
        }

    }

    protected static <T> Future<T> execute(final Double id, Callable<T> call) {
        if (call == null) {
            throw new NullPointerException("Callable is null");
        }
        /*
         * The reason of synchroning the whole block below is that, if suppose I synchronized till only
         * then end of 'if', then what might happen is that in between the end of 'if' and starting
         * RunnableFuture<T> ftask.... the shutdown is called, then the actual system for the queue
         * might be shutdown and still we are adding a task, hence the whole block need to be synchronized.
         * Not doing the whole block, wont create any trouble, but queue might accaept a new Runnable
         * when it was still closed. And what if there are many threads waiting for the syncronized to get over?,
         * in that case many elements would have been added.
         * 
         * After lots and lots of profiling, I have found out that the performance is
         * atleast 100 times better (yep literally 100 times) under heavy load, 
         * if you synchronize the whole block below rather than only synchronizing
         * the 'if' part or not synchonizing at all. This is absolutely remarkable.
         * 
         * 
         * 
         */
        synchronized (id) {
            if (mapBoolean.get(id)) 
            {
                throw new RejectedExecutionException();
            }

            //-------------------------Master Piece--------------------------------//

            RunnableFuture<T> ftask = new FutureTask<T>(call);
            LinkedBlockingQueue<Runnable> queue = map.get(id);
            queue.add(ftask);//System.out.println("added");
            return ftask;

            //------------------------Master Piece---------------------------------//
        }

    }

    protected static <T> Future<T> execute(final Double id, Runnable runnable, T result) {
        if (runnable == null) {
            throw new NullPointerException("Callable is null");
        }
        //see execute(Double id,Callable<T> call
        synchronized (id) {
            if (mapBoolean.get(id)) {
                throw new RejectedExecutionException();
            }

            RunnableFuture<T> ftask = new FutureTask<T>(runnable, result);
            LinkedBlockingQueue<Runnable> queue = map.get(id);
            queue.add(ftask);//System.out.println("added");
            return ftask;
        }

    }
    
    protected static <T> Future<T> executeLoosely(final Double id, Runnable runnable,T result)
    {
        if(runnable==null)
            throw new NullPointerException("Callable is null");
        //see execute(Double id,Callable<T> call
                
            if(mapBoolean.get(id))
            {
                throw new RejectedExecutionException();
            }

            RunnableFuture<T> ftask = new FutureTask<T>(runnable,result);
            LinkedBlockingQueue<Runnable> queue = map.get(id);
            queue.add(ftask);//System.out.println("added");
            return ftask;        
               
        
    }
    /**
     * 
     * @throws InterruptedException 
     */
    /*
     * The policy followed here is that:
     * If Runnable throws an exception, then that Runnable is no longer executed,
     * and the next one in the queue is executed next.
     */
    public static void loop() throws InterruptedException {
        LinkedBlockingQueue<Runnable> queue = map.get(tlocal.get());
        while (!Thread.currentThread().isInterrupted()) {
            try {

                Runnable runnable = queue.take();
                if (runnable == POISON_PILL) {
                    break;
                }

                if (!(runnable instanceof RunnableFuture)) {
                    try {
                        runnable.run();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    //FutureTask will make sure that the runnable is run well, if exception is thrown
                    //then FutureTask will register it, so that when user calls Future#get
                    //then exception is returned.
                    runnable.run();//System.out.println("ha");
                }

                runnable = null;//so that it is ready for gc, or else had the runnable consumed
                //a lot of resources, then for one loop it will remain in invisible state,
                //see http://java.sun.com/docs/books/performance/1st_edition/html/JPAppGC.fm.html#997426 for more info                                        



                if (mapBoolean.get(tlocal.get())) {
                    if (queue.isEmpty()) {
                        break;
                    }
                }

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();//let the stack know, that interrupt was thrown.
                throw new InterruptedException();
            }

        }

    }

    /**
     * Designed for Handler.
     * @return The ID of the Queue of the thread which calls. 
     */
    protected static Double getIdForQueue() {
        return tlocal.get();
    }

    /**
     * Shut'sDown the respective handler.
     * @param id The handler's id.
     */
    protected static void shutDown(Double id) {
        /**
         * The policy to shutdown is : To put a boolean, put the poison pill here,
         * @Guarded-By the id of the Handler which calls it
         */        
        synchronized (id) {
            if (mapBoolean.get(id)) //check if shutdown was already called. if so, then do nothing
                return;                
            post(id,POISON_PILL);// this should be called before mapBoolean(id,!shut)
            //reason: post will never accept it if called the other wise and ultimately
            //thread will never shutdown
            mapBoolean.put(id, !shut);           
        }
    }

    protected static void shutDonwNow(Double id) {
        
    }
}
