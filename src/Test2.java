
import java.util.logging.Level;
import java.util.logging.Logger;
import os.Handler;
import os.Looper;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jatin
 */
public class Test2 {
public static void main(String args[]) throws InterruptedException    
{
    new Test2().go();
}
int i=0;int j=0;
void go() throws InterruptedException
{
    Looper.prepare();
    final Handler h = new Handler();
    h.post(new Runnable(){
        public void run()
        {
            System.out.println("sdf");
        }
    });
    h.shutDown();
    h.post(new Runnable(){
        public void run()
        {
            System.out.println("sdf");
        }
    });
    h.shutDown();
    Looper.loop();
    
}
class A{
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        //System.out.println(" finalized   "+i);
        
    }
}

}

