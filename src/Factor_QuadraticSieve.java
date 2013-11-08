
import java.io.ObjectInputStream.GetField;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.IIOException;
import javax.management.RuntimeErrorException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Frederick Ceder
 */
public class Factor_QuadraticSieve implements FactorMethod {

    private int precision = 10000;
    private BigDecimal TWO = new BigDecimal("2");
    private BigInteger TWOi = new BigInteger("2");
    private BigInteger THREEi = new BigInteger("3");
    private BigInteger FOURi = new BigInteger("4");
    
    private FactorMethod trialDivision;
    private Factor_PerfectGeometry perfectGeo = new Factor_PerfectGeometry();
    private SievePrime sieve;
    
    public Factor_QuadraticSieve(int primeSieveLimit)
    {
        this(new SieveAtkins(primeSieveLimit));
    }
    
    public Factor_QuadraticSieve(SievePrime primeSieve)
    {
    	this.sieve = primeSieve;
        trialDivision = new Factor_TrialDivision(primeSieve);
    }
    
    public int legendre_symbol(BigInteger a, BigInteger p) {
        int t = a.pow(p.subtract(BigInteger.ONE).divide(TWOi).intValue()).mod(p).intValue();
        return t > 1 ? -1 : t;
    }
    
    
    public BigInteger[] tonelli_shanks(BigInteger n, BigInteger p) {
    	
    	List<BigInteger> results = new ArrayList<BigInteger>();
    	
    	// Factor out powers of 2 from p - 1
    	// so that p - 1 = Q2^s
    	BigInteger s = BigInteger.ZERO;
    	BigInteger Q = p.subtract(BigInteger.ONE);
    	for(; ; s.add(BigInteger.ONE)) {
    		BigInteger[] vals = Q.divideAndRemainder(TWOi);
    		if(vals[1].compareTo(BigInteger.ZERO) == 1)
    			break;
    		Q = vals[0];     		
    	}
    	
    	// Special case s = 1, p = 3 mod 4
    	if(s.equals(BigInteger.ONE) || p.mod(FOURi).equals(THREEi))
    	{
    		BigInteger exponent = p.add(BigInteger.ONE).divide(FOURi);
    		BigInteger res = n.pow(exponent.intValue()).mod(p);
    		results.add(res);
    		results.add(res.negate());
    		return (BigInteger[]) results.toArray();
    	}
    	
    	BigInteger z = TWOi;
    	while(legendre_symbol(z, p) != -1)
    		z.add(BigInteger.ONE);
    	
    	BigInteger c = z.pow(Q.intValue()).mod(p);
    	BigInteger resExp = Q.add(BigInteger.ONE).divide(TWOi);
		BigInteger res = n.pow()
		
    	while {
    		
    		
    	}
    	
    	
    	return TWOi;
    	
    }
    
    
    public void factor(Task task, BigInteger toFactor) {        
        // Step 1, 2  - factor out small primes
//        trialDivision.factor(task);
//        if(task.isFinished()) {
//            System.out.println("Done!");
//	        System.out.println("Factors found: ");
//	        for(BigInteger r: task.getResults())
//	        {
//	            System.out.println(r);
//	        }
//	        System.out.println();
//            return;
//        }
//        System.out.println(toFactor);
        
        // Step 3 - is 'N' perfect exponent?
        // Step over
//        if(perfectGeo.factor(task, toFactor))
//        	return;        
        
        // Step 4 - Find smoothness value: O(e^(0.5*sqrt(logNloglogN))
        BigInteger B = getSmoothnessValue(toFactor);
        System.out.println(B);
        System.out.println();
        
        // Step 5 - Determine factor base, primes where N / P = 1
        BigInteger lastPrime = BigInteger.valueOf(sieve.getPrimes().get(sieve.getPrimes().size() - 1));
        if(B.compareTo(lastPrime) == 1)
        	throw new RuntimeErrorException(null, "herpetyderp: not enough primes!");
        
        List<BigInteger> factorBase = new ArrayList<>();
        for(int prime : this.sieve.getPrimes()) {
        	BigInteger p = BigInteger.valueOf(prime);
        	if(p.compareTo(B) == 1)
        		break;
        	
        	// Solve for n = x2 mod p = q mod p
        	BigInteger root = toFactor.mod(p);        			
        	if(root != null) {
        		factorBase.add(p);
        		System.out.println(p);
        	}
        }        
        
        // Step 6 - Get primes to check with. How many???
//        List<BigInteger>
//        
//        
//        return;
    }
    //  Q(x) = (√N + x)^2 − N
    public BigDecimal Q(BigDecimal x, BigDecimal toFactor) {
    	//perfectGeo.getPowRoot(x, k)
    	BigDecimal qx = _BigDecimalMath.sqrt(toFactor).add(x).pow(2).subtract(toFactor);
    	return qx;
    }
    
    @Override
    public void factor(Task task) {
    	while(!task.isFinished()) {
    		factor(task, task.poll());
    	}
    }
    
    private BigInteger getSmoothnessValue(BigInteger N)
    {
        BigDecimal b = new BigDecimal(N.toString());
//        b.setScale(precision, RoundingMode.DOWN);
        
        BigDecimal C = new BigDecimal(3); 
//        C.setScale(precision, RoundingMode.HALF_DOWN);
        BigDecimal logn = _BigDecimalMath.log(b);
        BigDecimal loglogn = _BigDecimalMath.log(logn);
        BigDecimal exponent = _BigDecimalMath.sqrt(logn.multiply(loglogn)).divide(TWO, RoundingMode.HALF_DOWN);
        BigDecimal s = _BigDecimalMath.exp(exponent).multiply(C);
        return s.toBigInteger();
    }
    
    public static void main(String[] args)
    {
        BigInteger b = new BigInteger("15347");
        //BigDecimal d = new BigDecimal("87");
        //d.setScale(10000, RoundingMode.UP);
        
        Factor_QuadraticSieve f = new Factor_QuadraticSieve(new SieveAtkins(1000));
        
//        System.out.println(f.getSmoothnessValue(b));
        
        
        
        Task t = new Task(0, b, new Timing(200000));
        
        f.factor(t);
        
        //BigInteger res = _BigIntegerMath.isqrt(b);
        //System.out.println("sqrt(" + b.toString() + ") = " + res);
    }
    
    
    
}
