import java.math.BigInteger;


public class Factor_TrialPerfectRho extends Factor_PerfectPollardRho {
	
	protected Factor_TrialDivision trial;
	
    public Factor_TrialPerfectRho(int primenumbers)
    {
    	trial = new Factor_TrialDivision(primenumbers);        
    }

	@Override
	public void factor(Task task) {
		trial.factor(task);
		
		if(task.isTimeout() || task.isFinished())
			return;
		
		super.factor(task);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FactorMethod f = new Factor_TrialPerfectRho(1337);
	
	    BigInteger N = new BigInteger("1337").pow(10);
	    Task t = new Task(0, N, new Timing(1500));
	    f.factor(t);
	    if(t.isFinished())
		    for(BigInteger val: t.getResults())
		    {
		        System.out.println(val);
		    }		
	}

}
