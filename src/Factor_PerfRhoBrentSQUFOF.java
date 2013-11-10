
import java.math.BigInteger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author figgefred
 */
public class Factor_PerfRhoBrentSQUFOF extends Factor_PerfectRhoBrent {
    
    private Factor_SQUFOF m;
    
    public Factor_PerfRhoBrentSQUFOF()
    {
        m = new Factor_SQUFOF();
    }
    
    @Override
    public void factor(Task task)
    {
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
                    int k = 0;
                    while(true)
                    {
                            divisor = factorizebrent(task, toFactor);
                            if(task.isTimeout())
                            {
                                task.push(toFactor);
                                return;
                            }
                            if(divisor  != null)
                            {
                                break;
                            }
                            if(toFactor.bitCount() <= 16)
                            {
                                divisor = m.SQUFOF(task, divisor, k++);
                            }
                            if(divisor != null)
                                break;
                    }
                    
                    task.push(divisor); 
                    BigInteger quo = toFactor.divide(divisor);
                    task.push(quo);
                    
                    geo.factor(task);
            }
    }
    
}
