
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
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
        
        Stopwatch readingTimer = new Stopwatch();
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        List<Task> tasks = new ArrayList<>();
        String line = read.readLine();
        
        readingTimer.start();
        
        DEBUG = false;
        
        //FactorMethod f = new Factor_TrialDivision(10000);
        
        long totalTimeout = 15000;
        FactorMethod f = new Factor_PollardRho();
        
        //long totalTimeout = 16500;
        //FactorMethod f = new Factor_TrialPollardRho(175000);
        int i = 0;
        while(true) 
        {
            if(line == null || line.trim().equals("")) {
                break;
            }
            BigInteger b = new BigInteger(line.trim());
            Task t = new Task(i++, b, null);
            tasks.add(t);
            line = read.readLine();
        }
        
        Collections.sort(tasks);
        Task[] results = new Task[tasks.size()];
        int tasksleft = tasks.size();
        long timeleft = totalTimeout - readingTimer.milliseconds();
        dPrintln("Time left for work is " + timeleft + "ms");
        for(Task t: tasks)
        {
            long timeout = getNextTimeoutDuration(timeleft, tasksleft);
            t.setNewTimout(new Timing(timeout));
            dPrint("Task-" + t.index + "(" + t.toFactor + ") was allocated " + timeout + "ms working time" );
            // Work!
            f.factor(t);
            if(!t.isTimeout())
                t.finish();
            results[t.index] = t;
            timeleft -= t.getExecutionTime();
            dPrintln("Task-" + t.index + " executed for " + t.getExecutionTime() + "ms");
            tasksleft--;
        }
        
        printResults(results);
        
        if(DEBUG) 
        {
            int finished = 0;
            for (Task result : results) {
                if (result.isFinished()) {
                    finished++;
                }
            }
            dPrintln("Finished " + finished + "/"+ results.length);
            dPrintln("Executed for " + readingTimer.stop().milliseconds() + "ms");
        }
    }
    
    public static void printResults(Task[] tasks)
    {
        for(int i = 0; i < tasks.length; i++)
        {
            if(tasks[i].isFinished()) {
                for(BigInteger b: tasks[i].getResults())
                {
                    System.out.println(b);
                }
            }
            else {
                System.out.println("fail");
            }
            System.out.println(""); 
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
