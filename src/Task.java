
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fred
 */
public class Task implements Comparable<Task>  {
    
    public BigInteger toFactor;
    public final int index;
    public final BigInteger initial;
    private List<BigInteger> results;
    private Timing timer;
    private boolean finished;
    
    public Task(int index, BigInteger toFactor, Timing t)
    {
        this.index = index;
        this.initial = toFactor;
        this.toFactor = toFactor;
        results = new ArrayList<>();
        timer = t; // Is ok can be set later
    }
    
    public void finish()
    {
        finished = true;
        timer.stop();
    }
    
    public void setNewTimout(Timing t)
    {
        timer = t;
    }
    
    public void setPartResult(BigInteger b)
    {
        results.add(b);
    }
    
    public long getExecutionTime()
    {
        return timer.getRunTime();
    }
    
    public List<BigInteger> getResults()
    {
        if(!finished)
            return null;
        return results;
    }
    
    public boolean isFinished() 
    {
        return finished;
    }
    
    public boolean isTimeout()
    {
        return timer.timedout();
    }

    @Override
    public int compareTo(Task t) {        
        return (this.initial.compareTo(t.initial));
    }
    
}
