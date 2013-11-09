
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author fred
 */
public class Main {
   
    public static boolean DEBUG = true;
    public static long totalTimeout = 17500;
    public static Stopwatch globalTimer = new Stopwatch();
    
    public static boolean Do_Division_First = true; //true;
    
    public static int sieveLimit = 500;
    
    //public static FactorMethod f = new Factor_PollardRho();
    //public static FactorMethod f = new Factor_TrialDivision(sieveLimit);
    //public static FactorMethod f = new Factor_TrialPollardRho(sieveLimit);
//    public static FactorMethod f = new Factor_PerfectPollardRho();
    //public static FactorMethod f = new Factor_TrialPerfectRho(sieveLimit);
    
//    public static FactorMethod f = new Factor_TrialRhoBrent(sieveLimit);
//    public static FactorMethod f = new Factor_PollardRhoBrent();
//    public static FactorMethod f = new Factor_TrialPerfectRhoBrent(sieveLimit);
    public static FactorMethod f = new Factor_PerfectRhoBrent();
    
    public static void main(String args[]) throws IOException {
        
        
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        List<Task> tasks = new ArrayList<>();
        String line = read.readLine();
        
        globalTimer.start();
        
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
        Task[] results;
        if(Do_Division_First)
            results = doWorkDivFirst(tasks);
        else
            results = doWork(tasks);
        
        printResults(results);
        
        if(DEBUG) 
        {
            int finished = 0;
            for (Task result : results) {
                if (!result.isTimeout() && result.isFinished()) {
                    finished++;
                }
                else {
                    dPrintln("FAILED: ");
                    dPrintln("  " + result);
                }
            }
            dPrintln("Finished " + finished + "/"+ results.length);
            dPrintln("Executed for " + globalTimer.stop().milliseconds() + "ms");
        }
        
        
    }
    
    public static Task[] doWorkDivFirst(List<Task> tasks)
    {
        FactorMethod fStart = new Factor_TrialDivision(sieveLimit);
        
        for(Task task:tasks)
        {
            fStart.factor(task);
        }
        Task[] results = doWork(tasks);
        return results;
    }
    
    public static Task[] doWork(List<Task> tasks)
    {
        Task[] results = new Task[tasks.size()];
        int tasksleft = tasks.size();
        long timeleft = totalTimeout - globalTimer.milliseconds();
        dPrintln("Time left for work is " + timeleft + "ms");
        for(Task t: tasks)
        {
        	
            long timeout = getNextTimeoutDuration(timeleft, tasksleft);
            t.setNewTimout(new Timing(timeout));
            dPrint("Task-" + t.index + "(" + t.initial + ") was allocated " + timeout + "ms working time" );
            // Work!
            f.factor(t);
            
            t.timer.stop();
            results[t.index] = t;            
            timeleft -= t.getExecutionTime();
            dPrintln("Task-" + t.index + " executed for " + t.getExecutionTime() + "ms");
            tasksleft--;
        }
        return results;
    }
    
    
    public static void printResults(Task[] tasks)
    {
        for(Task task : tasks)
        {
            if(!task.isTimeout() && task.isFinished()) {
                for(BigInteger b: task.getResults())
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
