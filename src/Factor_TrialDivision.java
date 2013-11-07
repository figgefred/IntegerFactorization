
import java.math.BigInteger;
import java.util.Iterator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fred
 */
public class Factor_TrialDivision implements FactorMethod {

    public SievePrime sieve;
    
    public Factor_TrialDivision(int primenumbers)
    {
        this(new SieveAtkins((primenumbers)));
    }
    
    public Factor_TrialDivision(SievePrime sieve)
    {
        if(sieve == null) {
            throw new IllegalArgumentException("Sieve is null");
        }
        this.sieve = sieve;
    }
    
    @Override
    public void factor(Task task) {
        
        BigInteger toFactor = task.toFactor;
        
        if(toFactor.equals(BigInteger.ONE)) {
            task.finish();
            task.setPartResult(toFactor);
            return;
        }
        
        Iterator<Integer> iter = sieve.getPrimes().iterator();
        while(iter.hasNext())
        {
            if(task.isTimeout())
                return;
            Integer p = iter.next();
            BigInteger prime = BigInteger.valueOf(p.intValue());
            if(prime.multiply(prime).compareTo(toFactor) > 0)
                break;
            while (toFactor.mod(prime).equals(BigInteger.ZERO))
            {
                task.setPartResult(prime);
                toFactor = toFactor.divide(prime);
            }
        }
        if(toFactor.equals(BigInteger.ONE))
        {
            task.finish();
            task.toFactor = null;
        }
        else if(toFactor.isProbablePrime(20))
        {
            task.setPartResult(toFactor);
            task.finish();
            task.toFactor = null;
        }
        else
            task.toFactor = toFactor;
    }
    
}
