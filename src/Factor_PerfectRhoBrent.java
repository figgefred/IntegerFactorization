
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

                    BigInteger toFactor = task.poll();
                
                    if(toFactor.isProbablePrime(10))
                    {
                        task.setPartResult(toFactor);
                        continue;
                    }
                    if(toFactor.equals(BigInteger.ONE)) continue;
                    if(task.isTimeout())
                    {
                        task.push(toFactor);
                        return;
                    }

//                    if(task.isFinished())
//                        return;

                    
                    BigInteger divisor;
                    while(true)
                    {
                            divisor = factorizebrent(task, toFactor);
                            if(task.isTimeout())
                            {
                                task.push(toFactor);
                                return;
                            }
                            if(divisor  == null)
                                continue;
//                            if(!divisor.equals(toFactor))
//                                    break;
                            break;
                    }
                    
                    task.push(divisor); 
                    BigInteger quo = toFactor.divide(divisor);
                    task.push(quo);
                    
                    geo.factor(task);
            }
	     
	}   
}
