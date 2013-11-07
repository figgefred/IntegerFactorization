
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class Main {
   
    public static boolean DEBUG;
    
    public static void main(String args[]) throws IOException {
        
        DEBUG = false;
        long totalTimeout = 5000;
        
        Stopwatch readingTimer = new Stopwatch();
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        List<Task> tasks = new ArrayList<>();
        String line = read.readLine();
        readingTimer.start();
        while(true) 
        {
            if(line == null || line.trim().equals("")) {
                break;
            }
            BigInteger i = new BigInteger(line.trim());
            Task t = new Task(i, null);
            tasks.add(t);
            line = read.readLine();
        }
        
        //FactorMethod f = new Factor_TrialDivision(10000);
        //FactorMethod f = new Factor_PollardRho();
        FactorMethod f = new Factor_TrialPollardRho(10000);
        int tasksleft = tasks.size();
        long timeleft = totalTimeout - readingTimer.milliseconds();
        dPrintln("Time left for work is " + timeleft + "ms");
        int i = 0;
        for(Task t: tasks)
        {
            long timeout = getNextTimeoutDuration(timeleft, tasksleft);
            t.setNewTimout(new Timing(timeout));
            f.factor(t);
            if(t.isFinished()) {
                for(BigInteger b: t.getResults())
                {
                    System.out.println(b);
                }
            }
            else {
                System.out.println("fail");
            }
            System.out.println("");   
            
            timeleft -= t.getExecutionTime();
            dPrintln("Task-" + i + " executed for " + t.getExecutionTime() + "ms");
            tasksleft--;
        }
        
        if(DEBUG) 
        {
            dPrintln("");
            dPrintln("Executed for " + readingTimer.stop().milliseconds() + "ms");
        }
    }
    
    public static long getNextTimeoutDuration(long totalTime, long numTasksLeft)
    {
        return totalTime/numTasksLeft;
    }
    
    public static void dPrint(String s)
    {
        if(DEBUG)
        {
            if(s.equals(""))
                System.out.print(s);
            else
                System.out.print("DEBUG: " + s);
        }
    }
    
    public static void dPrintln(String s)
    {
        if(DEBUG)
        {
            if(s.equals(""))
                System.out.println(s);
            else
                System.out.println("DEBUG: " + s);
        }
    }
}
