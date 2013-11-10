/*************************************************************************
 *  Compilation:  javac PollardRho.java
 *  Execution:    java PollardRho N
 *  
 *  Factor N using the Pollard-Rho method.
 * 
 *  % java PollardRho 44343535354351600000003434353
 *  149
 *  329569479697
 *  903019357561501
 *
 * Source: http://introcs.cs.princeton.edu/java/78crypto/PollardRho.java.html
 * 
 * Modified by: Frederick Ceder
 * 
 *************************************************************************/

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
    

public class Factor_PollardRho implements FactorMethod {
    protected final static BigInteger ZERO = new BigInteger("0");
    protected final static BigInteger ONE  = new BigInteger("1");
    protected final static BigInteger TWO  = new BigInteger("2");
    protected final static Random random = new Random(1337);

    public BigInteger rho(Task task, BigInteger val, int constant) {
        
        BigInteger toFactor = val;
        
        BigInteger divisor;
        BigInteger c  = BigInteger.ONE; //new BigInteger(toFactor.bitLength(), random);
        BigInteger x  = BigInteger.valueOf(2 + constant);//new BigInteger(toFactor.bitLength(), random);
        BigInteger xx = x;

        // check divisibility by 2
        if (toFactor.mod(TWO).compareTo(ZERO) == 0) return TWO;

        do {
            if(task.isTimeout())
                return null;
            x  =  x.multiply(x).mod(toFactor).add(c).mod(toFactor);
            xx = xx.multiply(xx).mod(toFactor).add(c).mod(toFactor);
            xx = xx.multiply(xx).mod(toFactor).add(c).mod(toFactor);
            divisor = x.subtract(xx).gcd(toFactor);
        } while((divisor.compareTo(ONE)) == 0);

        return divisor;
    }
    
    @Override
    public void factor(Task task) {
    	while(!task.isFinished()) {    		
	        BigInteger toFactor = task.poll();
//	        System.out.println(toFactor);
	        
	        if (toFactor.isProbablePrime(20)) { 
	            task.setPartResult(toFactor);
	            continue;
	        }
                if(toFactor.equals(BigInteger.ONE)) continue;
                if(task.isTimeout())
                {
                    task.push(toFactor);
                    return;
                }
	        
	        BigInteger divisor;
	        for(int i = 0; ; i++)
	        {
		        divisor = rho(task, toFactor, i);
		        if(task.isTimeout())
                        {
                            task.push(toFactor);
		            return;
                        }
		        if(!divisor.equals(toFactor))
                    break;
	        }
	        
	        task.push(divisor);	   
//	        factor(task);
	        task.push(toFactor.divide(divisor));	   
//	        factor(task);
    	}
    }
    
    public static void main(String[] args) {
        FactorMethod f = new Factor_PollardRho();
        
        BigInteger N = new BigInteger("781236781263781278312873").pow(4);
        //BigInteger N = new BigInteger("59826358926598236235");
        Task t = new Task(0, N, new Timing(15000));
        f.factor(t);
        for(BigInteger val: t.getResults())
        {
            System.out.println(val);
        }
    }
}