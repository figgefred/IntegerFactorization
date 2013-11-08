
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    
    private BigInteger factor(Task task, BigInteger toFactor) {
    	 
        if(toFactor.equals(BigInteger.ONE)) {            
            // task.setPartResult(toFactor); nja?
            return null;
        }
        
        Iterator<Integer> iter = sieve.getPrimes().iterator();
        while(iter.hasNext())
        {
            if(task.isTimeout())
                return toFactor;
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
           return null;
        }
        else if(toFactor.isProbablePrime(20))
        {
            task.setPartResult(toFactor);
            return null;
        }
        
        return toFactor;
    }
    
    @Override
    public void factor(Task task) {        
        List<BigInteger> partFactors = new ArrayList<>();
        while(!task.isFinished())
        {
        	BigInteger val = task.poll();
        	BigInteger factorized = factor(task, val);
        	if(factorized != null)
        		partFactors.add(factorized);        	
        }
        
        for(BigInteger leftToFactor : partFactors) {
        	task.push(leftToFactor);
        }
    }
    
}
