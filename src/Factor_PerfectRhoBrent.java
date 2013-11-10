
import java.math.BigInteger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author figgefred
 */
public class Factor_PerfectRhoBrent extends Factor_PollardRhoBrent {
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
                    BigInteger divisor;
                    while(true)
                    {
                            divisor = factorizebrent(task, toFactor);
                            if(task.isTimeout())
                                return;
                            if(divisor  == null)
                                continue;
//                            if(!divisor.equals(toFactor))
//                                    break;
                            break;
                    }
                    
                    if(divisor.isProbablePrime(10))
                    {
                        task.setPartResult(divisor);
                    } else if(!divisor.equals(BigInteger.ONE)) {
                        task.push(divisor); 
                    } 

                    BigInteger quo = toFactor.divide(divisor);
                    if(quo.isProbablePrime(10))
                    {
                            task.setPartResult(quo);
                    } else if(!quo.equals(BigInteger.ONE)) {
                            task.push(quo);
                    }  

                    geo.factor(task);
            }
	     
	}   
}
