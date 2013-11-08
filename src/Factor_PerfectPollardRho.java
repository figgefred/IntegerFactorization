import java.math.BigInteger;


public class Factor_PerfectPollardRho extends Factor_PollardRho {
	
	protected Factor_PerfectGeometry geo = new Factor_PerfectGeometry();

	@Override
	public void factor(Task task) {		
		geo.factor(task);
		
    	while(!task.isFinished()) {	        
	        
	        if(task.isTimeout())
	        	return;
	        
	        if(task.isFinished())
	        	return;

	        BigInteger toFactor = task.poll();
	        
//	        if (toFactor.compareTo(ONE) == 0) {
//	            continue;
//	        }
//	        if (toFactor.isProbablePrime(20)) { 
//	            task.setPartResult(toFactor);
//	            continue;
//	        }
	        
	        BigInteger divisor;
	        for(int i = 0; ; i++)
	        {
		        divisor = rho(task, toFactor, i);
		        if(task.isTimeout())
		            return;
		        if(!divisor.equals(toFactor))
		        	break;
	        }
//	        if(task.isTimeout())
//	            return;
//	        
//	        if(divisor.equals(toFactor)) {
//	        	task.push(toFactor);
//	        	continue;
//	        }
	        
	        if(divisor.isProbablePrime(20))
	        {
	        	task.setPartResult(divisor);
	        } else {
	        	task.push(divisor); 
	        }
	        
	        BigInteger quo = toFactor.divide(divisor);
	        if(quo.isProbablePrime(20))
	        {
	        	task.setPartResult(quo);
	        } else {
	        	task.push(quo);
        	}  
	        
	        geo.factor(task);
    	}
	     
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FactorMethod f = new Factor_PerfectPollardRho();
		
	    BigInteger N = new BigInteger("781236781263781278312873").pow(4);
	    Task t = new Task(0, N, new Timing(1500));
	    f.factor(t);
	    if(t.isFinished())
		    for(BigInteger val: t.getResults())
		    {
		        System.out.println(val);
		    }
	}

}
