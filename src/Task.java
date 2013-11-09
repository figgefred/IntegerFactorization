
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fred
 */
public class Task implements Comparable<Task>  {
    
//    public BigInteger toFactor;
    public final int index;
    public final BigInteger initial;
    private List<BigInteger> results;
    private Queue<BigInteger> toFactor = new LinkedList<BigInteger>();
    public Timing timer;
    private boolean finished;
    
    public Task(int index, BigInteger toFactor, Timing t)
    {
        this.index = index;
        this.initial = toFactor;
        this.toFactor.add(toFactor);
        results = new ArrayList<>();
        timer = t; // Is ok can be set later
    }
    
    public BigInteger poll() {
    	return toFactor.poll();
    }
    
    public BigInteger peek() {
    	return toFactor.peek();
    }
    
    public void push(BigInteger val) {
    	toFactor.add(val);
    }
    
//    public void finish()
//    {
//        finished = true;
//        timer.stop();
//    }
    
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
//        if(!finished)
//            return null;
        return results;
    }
    
    public boolean isFinished() 
    {
        return toFactor.isEmpty();
    }
    
    public boolean isTimeout()
    {
        return timer.timedout();
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("# Init: ").append(initial).append("\n");
        sb.append("# Tasks: ").append("\n").append("  ");
        for(BigInteger b: toFactor)
        {
            sb.append(b.toString()).append(" ");
        }
        sb.append("# Results: ").append("\n").append("  ");
        for(BigInteger r: results)
        {
            sb.append(r.toString()).append(" ");
        }
        sb.append("\n");
        return sb.toString();
    }
    
    

    @Override
    public int compareTo(Task t) {        
        return (this.initial.compareTo(t.initial));
    }
    
}
