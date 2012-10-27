/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package os;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import junit.framework.TestCase;

/**
 *
 * @author Jatin
 */
public class LooperTest extends TestCase {
    
    public LooperTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of prepare method, of class Looper.
     */
    public void testPrepare() {
        System.out.println("prepare");
        Looper.prepare();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of post method, of class Looper.
     */
    public void testPost() {
        System.out.println("post");
        Double id = null;
        Runnable runnable = null;
        boolean expResult = false;
        boolean result = Looper.post(id, runnable);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of execute method, of class Looper.
     */
    public void testExecute_Double_Callable() {
        System.out.println("execute");
        Double id = null;
        Callable<T> call = null;
        Future expResult = null;
        Future result = Looper.execute(id, call);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of execute method, of class Looper.
     */
    public void testExecute_3args() {
        System.out.println("execute");
        Double id = null;
        Runnable runnable = null;
        Object result_2 = null;
        Future expResult = null;
        Future result = Looper.execute(id, runnable, result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loop method, of class Looper.
     */
    public void testLoop() throws Exception {
        System.out.println("loop");
        Looper.loop();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIdForQueue method, of class Looper.
     */
    public void testGetIdForQueue() {
        System.out.println("getIdForQueue");
        Double expResult = null;
        Double result = Looper.getIdForQueue();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of shutDown method, of class Looper.
     */
    public void testShutDown() {
        System.out.println("shutDown");
        Double id = null;
        Looper.shutDown(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of shutDonwNow method, of class Looper.
     */
    public void testShutDonwNow() {
        System.out.println("shutDonwNow");
        Double id = null;
        Looper.shutDonwNow(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
