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
    

public class Factor_PollardRho implements FactorMethod {
    private final static BigInteger ZERO = new BigInteger("0");
    private final static BigInteger ONE  = new BigInteger("1");
    private final static BigInteger TWO  = new BigInteger("2");
    private final static SecureRandom random = new SecureRandom();

    public BigInteger rho(Task task) {
        
        BigInteger toFactor = task.toFactor;
        
        BigInteger divisor;
        BigInteger c  = new BigInteger(toFactor.bitLength(), random);
        BigInteger x  = new BigInteger(toFactor.bitLength(), random);
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
        BigInteger toFactor = task.toFactor;
        
        if(task.isTimeout())
            return;
        if (toFactor.compareTo(ONE) == 0) {
            task.finish();
            task.toFactor = null;
            return;
        }
        if (toFactor.isProbablePrime(20)) { 
            task.finish();
            task.setPartResult(toFactor);
            task.toFactor = null;
            return;
        }
        BigInteger divisor = rho(task);
        if(task.isTimeout())
            return;
        task.toFactor = divisor;
        factor(task);
        task.toFactor = toFactor.divide(divisor);
        factor(task);
    }
    
    public static void main(String[] args) {
        FactorMethod f = new Factor_PollardRho();
        
        BigInteger N = new BigInteger("59826358926598236235");
        Task t = new Task(N, null);
        f.factor(t);
        for(BigInteger val: t.getResults())
        {
            System.out.println(val);
        }
    }
}