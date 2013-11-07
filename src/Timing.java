/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fred
 */
public class Timing {

    Stopwatch stopwatch;
    private long Timeout;
    private boolean timeout = false;
    private long finishTime;
    
    public Timing(long timeInMilliSeconds)
    {
        stopwatch = new Stopwatch();
        stopwatch.start();
        Timeout= timeInMilliSeconds;
        finishTime = Long.MIN_VALUE;
    }
    
    public void stop()
    {
        if(stopwatch.isRunning()) {
            stopwatch.stop();
            finishTime = stopwatch.milliseconds();
        }
    }
    
    public long getRunTime()
    {
        return finishTime;
    }
    
    public boolean timedout() {
        if(!timeout && stopwatch.milliseconds() < Timeout) {
            return false;
        }
        stop();
        timeout = true;
        return timeout;
    }
}
