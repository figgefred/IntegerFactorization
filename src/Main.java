
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
   
    public static boolean DEBUG = false;
    public static long totalTimeout = 18000;
    public static Stopwatch globalTimer = new Stopwatch();
    
    public static boolean DoBackupPlan = false;
    public static boolean Do_PreWork = true; //true;
    public static boolean Do_Sort_After_Division_First = true;
    public static boolean Do_Reverse_Sort = true;

    public static boolean Relocate_Elems = true;
    
    public static boolean switchYes = false;
    
    public static int sieveLimit = 500;
    public static int BackupTimeThreshold = 20;
    
    //public static FactorMethod f = new Factor_PollardRho();
    //public static FactorMethod f = new Factor_TrialDivision(sieveLimit);
    //public static FactorMethod f = new Factor_TrialPollardRho(sieveLimit);
//    public static FactorMethod f = new Factor_PerfectPollardRho();
    //public static FactorMethod f = new Factor_TrialPerfectRho(sieveLimit);
    //public static FactorMethod f = new Factor_PerfRhoBrentSQUFOF();
//    public static FactorMethod f = new Factor_TrialRhoBrent(sieveLimit);
   //public static FactorMethod f = new Factor_PollardRhoBrent();
    //public static FactorMethod f = new Factor_TrialPerfectRhoBrent(sieveLimit);
    public static FactorMethod f = new Factor_PerfectRhoBrent();
    //public static FactorMethod f = new Factor_SQUFOF();
    
    public static void main(String args[]) throws IOException {
        
        Task.REVERSE_SORT = Do_Reverse_Sort;
        
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
        if(Do_PreWork)
            results = doPreWork(tasks);
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
    
    public static Task[] doPreWork(List<Task> tasks)
    {
        FactorMethod fStart = new Factor_TrialDivision(sieveLimit);
        FactorMethod fGeo = new Factor_PerfectGeometry();
        
        for(Task task:tasks)
        {
            fStart.factor(task);
            fGeo.factor(task);
        }
        
        if(Do_Sort_After_Division_First)
        	Collections.sort(tasks);
        if(Relocate_Elems)
                tasks = getRelocation(tasks);
        
        Task[] results = doWork(tasks);
        return results;
    }
    
    private static List<Task> getRelocation(List<Task> tasks)
    {
        
        // Very undynamic
        int chunksize = tasks.size()/4;
        
        int s1 = 0;
        int s2 = chunksize;
        int s3 = s2 + chunksize;
        
        int half = chunksize*2;
        
        List<Task> newTasks = new ArrayList<Task>();
        for(int i = 0; i < tasks.size(); i++)
        {
            // First chunk is at same place
            if(i >= s1 && i < s2)
            {
                newTasks.add(tasks.get(i));
            }
            // Next chunk is put in the back
            else if(i >= s2 && i < s3)
            {
                int index = i+half;
                if(index < tasks.size())
                    newTasks.add(tasks.get(index));
            }
            // The last half is pushed (chunksize) to left
            else if(i >= s3)
            {
                int index = i-chunksize;
                if(index < tasks.size())
                    newTasks.add(tasks.get(index));
            }
        }
        return newTasks;
    }
    
    public static Task[] doWork(List<Task> tasks)
    {
        Task[] results = new Task[tasks.size()];
        int tasksleft = tasks.size();
        long timeleft = totalTimeout - globalTimer.milliseconds();
        dPrintln("Time left for work is " + timeleft + "ms");
        
        List<Task> failedTasks = new ArrayList<>();
        int i = 0;
        for(Task t: tasks)
        {
            results[t.index] = t;
            long timeout = getNextTimeoutDuration(timeleft, tasksleft);
            t.setNewTimout(new Timing(timeout));
            dPrint("Task-" + t.index + "(" + t.initial + ") was allocated " + timeout + "ms working time" );
            // Work!
            f.factor(t);
            t.timer.stop();
            if(t.isTimeout())
                failedTasks.add(t);
            //timeleft -= t.getExecutionTime();
            timeleft = totalTimeout - globalTimer.milliseconds();
            dPrintln("Task-" + t.index + " executed for " + t.getExecutionTime() + "ms");
            tasksleft--;
        }
        
        if(DoBackupPlan)
        {
            //f = new Factor_PollardRhoBrent();
            f = new Factor_SQUFOF();
            doBackupWork(failedTasks);
        }
        
        return results;
    }

    public static void doBackupWork(List<Task> tasks)
    {
        long timeleft = totalTimeout - globalTimer.milliseconds();
//        System.out.println("Backup for " + timeleft + "ms. Failed tasks count is " + tasks.size());
        if(tasks.size() == 0 || timeleft/tasks.size() < BackupTimeThreshold)
        {
            return;
        }
        
        if(Do_Sort_After_Division_First)
        {
            Task.REVERSE_SORT = !Task.REVERSE_SORT;
            Collections.sort(tasks);
        }
        
//        System.out.println("Starting backup!");
        int tasksleft = tasks.size();
        List<Task> failedTasks = new ArrayList<>();
        dPrintln("REDO of factoring!");
        dPrintln("Time left for backup work is " + timeleft + "ms");
        for(Task t: tasks)
        {
            long timeout = getNextTimeoutDuration(timeleft, tasksleft);
            t.setNewTimout(new Timing(timeout));
            dPrint("Task-" + t.index + "(" + t.initial + ") was allocated " + timeout + "ms working time" );
            // Work!
            f.factor(t);
            t.timer.stop();
            if(t.isTimeout())
                failedTasks.add(t);
            else if(t.isFinished())
                dPrintln("SOLVED one in backup plan phase");
            //results[t.index] = t;            
            timeleft = totalTimeout - globalTimer.milliseconds();
            dPrintln("Task-" + t.index + " executed for " + t.getExecutionTime() + "ms");
            tasksleft--;
        }
        
        timeleft = totalTimeout - globalTimer.milliseconds();
//      System.out.println("Next time slice is " + timeleft + "ms. Failed tasks count is " + failedTasks.size());
        
        if(switchYes)
        {
            switchYes = false;
            f = new Factor_PollardRhoBrent();
        }
        doBackupWork(failedTasks);
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
