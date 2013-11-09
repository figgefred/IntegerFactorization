
import java.io.ObjectInputStream.GetField;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
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
    	
    	
    	// Special case for p = 2. Then n is just n mod 2 
    	if(p.equals(TWOi)) {
    		BigInteger[] results = new BigInteger[1];
    		results[0] = n.mod(p);
    		return results;
    	}
    	BigInteger[] results = new BigInteger[2];
    
    	// Factor out powers of 2 from p - 1
    	// so that p - 1 = Q2^s
    	BigInteger s = BigInteger.ZERO;
    	BigInteger Q = p.subtract(BigInteger.ONE);
    	for(; ; s = s.add(BigInteger.ONE)) {
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
    		results[0] = res;
    		results[1] = res.negate();
    
    		return results;
    	}
    	
    	
    	BigInteger z = TWOi;
    	
    	while(legendre_symbol(z, p) != -1)
    		z = z.add(BigInteger.ONE);
//    	System.out.println(z);
//    	System.out.println();
    	
    	
    	BigInteger c = z.pow(Q.intValue()).mod(p);
    	BigInteger resExp = Q.add(BigInteger.ONE).divide(TWOi);
		BigInteger R = n.pow(resExp.intValue()).mod(p);
		BigInteger t = n.pow(Q.intValue()).mod(p);
		BigInteger M = s;
//		System.out.println(R);
//		System.out.println(t);
//		System.out.println(M);
//		
//		System.out.println();
    	while(!t.equals(BigInteger.ONE)) {
//    		System.out.println(6);
    		int i = 1;    		
    		while (!t.pow(TWOi.pow(i).intValue()).mod(p).equals(BigInteger.ONE)) {   			
    			i++;
			} 
    		
    		BigInteger b = c.pow((int) Math.pow(2, M.intValue() - i - 1)).mod(p);
    		R = R.multiply(b).mod(p);
    		t = t.multiply(b.pow(2)).mod(p);
    		c = b.pow(2);
    		M = BigInteger.valueOf(i);
//    		System.out.println(R);
//    		System.out.println(t);
//    		System.out.println(c);
//    		System.out.println(M);
    	}   	
    	
    	
    	results[0] = R;
    	results[1] = p.subtract(R);   	
    	
    	return results;
    }
    
    
    public void factor(Task task, BigInteger N) {        
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
        
    	if(N.isProbablePrime(20)) {
    		task.setPartResult(N);
    		return;
    	}
    		
    	
        // Step 3 - is 'N' perfect exponent?
        // Step over
        if(perfectGeo.factor(task, N))
        	return;        
        
        // Step 4 - Find smoothness value: O(e^(0.5*sqrt(logNloglogN))
        BigInteger B = getSmoothnessValue(N);
      
        System.out.println("# Smoothness value:");
        System.out.println(B);
        System.out.println();
        
        // Step 5 - Determine factor base, primes where N / P = 1
        BigInteger lastPrime = BigInteger.valueOf(sieve.getPrimes().get(sieve.getPrimes().size() - 1));
        if(B.compareTo(lastPrime) == 1)
        	throw new RuntimeErrorException(null, "herpetyderp: not enough primes!");
        
        List<BigInteger> factorBase = new ArrayList<>();
        System.out.println("# Factor base:");
        for(int prime : this.sieve.getPrimes()) {
        	BigInteger p = BigInteger.valueOf(prime);
        	if(p.compareTo(B) > -1)
        		break;
        	
        	// Solve for n = x2 mod p = q mod p        	   
        	// Legendre symbol definition:
        	// 1 if N is quadratic residue modulo p, that is x2 = q mod p = N exists and q != 0
        	// http://en.wikipedia.org/wiki/Legendre_symbol
        	if(legendre_symbol(N, p) == 1) {
        		factorBase.add(p);
        		System.out.println(p);
        	}
        }        
        System.out.println("# End of factor base");
        
//        System.out.println();
        // Step 6 - Determine nrootceil (fulhack med string?)
        BigInteger nrootceil = BigMath.sqrt(new BigDecimal(N.toString())).setScale(0, RoundingMode.CEILING).toBigInteger(); // Fred förstår inte. Men jag gör det.
        System.out.println("# nRoot:");
        System.out.println(nrootceil);
        System.out.println();
//        System.out.println();
        System.out.println("# Q values");
        BigInteger[] V = new BigInteger[100]; // 100?!?!?
        BigInteger[] Qs = new BigInteger[V.length];
        for(int i = 0; i < V.length; i++)
        {
        	V[i] = Q(BigInteger.valueOf(i), nrootceil, N);
        	Qs[i] = V[i];
        	System.out.println(BigInteger.valueOf(i) + " " + V[i]);
        }
        System.out.println();
        
        System.out.println();
        System.out.println("# x2 = n mod p values:");
    	for(int x = 0 ; x < factorBase.size(); x++)
    	{
    		BigInteger prime = factorBase.get(x);
    		
    		BigInteger[] results = tonelli_shanks(N.mod(prime), prime);
    		
    		for(BigInteger result : results) {
    			System.out.println(prime + " tonnelli: " + result);
    			int startindex = result.intValue() - nrootceil.intValue() % prime.intValue();  
    			System.out.println(prime + " (before) " + startindex);
    			while(startindex < 0)
    				startindex += prime.intValue();
    			System.out.println(prime + " (after) " + startindex);
    			System.out.println();
    			for(int i = startindex; i < V.length; i += prime.intValue())    
    				while(V[i].mod(prime).equals(BigInteger.ZERO))
    					V[i] = V[i].divide(prime);
    		}
    	}
    	System.out.println("# end");
    	System.out.println();
    	
    	List<BigInteger> ys = new ArrayList<BigInteger>();
    	List<BigInteger> is = new ArrayList<BigInteger>();
    	System.out.println("# Vs found:");
    	for(int i = 0; i < V.length; i++)
        {
    		if(V[i].equals(BigInteger.ONE)) {
    			BigInteger q = Qs[i];
    			System.out.println((i+nrootceil.intValue()) + " " + V[i] + " " + q);
    			
    			
    			ys.add(q);
    			is.add(BigInteger.valueOf(i).add(nrootceil));
    		}
        }
    	System.out.println("# End of vs");
    	System.out.println();
    	
   
    	System.out.println("# Matrix:");
    	// Whoa
    	
    	int rows = factorBase.size();
    	int cols = ys.size();
//    	double[][] ematrix = new double[ys.size()][factorBase.size()];
    	double[][] matrix = new double[rows][cols];
    	
		for(int i = 0; i < rows; i++) {			
			
			
	    	for(int j = 0; j < cols; j++) {	
	    		BigInteger tmp = ys.get(j);
	    		BigInteger prime = factorBase.get(i);
	    		
    			int e = 0;    			
    			while(tmp.mod(prime).equals(BigInteger.ZERO)) {
    				tmp = tmp.divide(prime);
    				e++;
    			}
    			
//    			ematrix[i][j] = e;
    			matrix[i][j] = e % 2;
    			
    			System.out.print(matrix[i][j]);
    		}
	    	
	    	System.out.println();
//	    	System.out.println(matrix[i]);    		
    	}
		System.out.println("#EOF matrix");
    	System.out.println();
    	
//    	System.out.println("#E matrix");
//    	for(int i = 0; i < ys.size(); i++) {
//    		for(int j = 0; j < factorBase.size(); j++) {
//    			System.out.print(ematrix[i][j]);
//    		}
//    		System.out.println();
//    	}
//    	System.out.println();
    	
    	// Gauss elimination
//    	for(int k = 0; k < factorBase.size() && k < ys.size(); k++) {
//    		// find pivot
//    		int maxi = 0;
//    		for(int i = k; i < ys.size(); ++i) { // m = factorBase.size()?
//    			if(matrix[i][k] == 1)
//    			{
//    				maxi = i;
//    				break;
//    			}
//    		}
//    		if(matrix[maxi][k] == 1) {
////    			throw new RuntimeException("Singular matrix");
//    		
//    		swap_rows(matrix[k], matrix[maxi]);
//    		
//    		for(int i = k+1; i < ys.size(); i++)
//    		{
//    			for(int j = k; j < factorBase.size(); j++)
//    			{
//    				matrix[i][j] = matrix[i][j] - matrix[k][j] * (matrix[i][k] / matrix[k][k]); 
//    			}
//    			matrix[i][k] = 0;
//    		}   
//    		}
//    	}
    	double[] nulls = new double[factorBase.size()]; 
    	double[] x = GaussianElimination.solve(matrix, nulls);
    	
    	for(int i = 0; i < x.length; i++)
    	{
    		System.out.print(x[i]);
    	}
    	System.out.println();
    	
//    	System.out.println("# Gauss eliminated?");
//    	for(int[] row : matrix) {
//    		for(int col : row)
//    		{
//    			System.out.print(col);
//    		}
//    		System.out.println();
//    	}
    	

    	System.out.println();
    	
		BigInteger prod1 = BigInteger.ONE;
		BigInteger prod2 = BigInteger.ONE;

		System.out.println("# Products found:");
		System.out.println(prod1);
		System.out.println(prod2);
    	
		
//        BigInteger factor = perfectGeo.getPowRoot(prod2, 2).subtract(perfectGeo.getPowRoot(prod1, 2)).abs();
		BigInteger factor = BigMath.isqrt(prod2).subtract(BigMath.isqrt(prod1)).abs();
        
        factor = factor.gcd(N);

        
        BigInteger quo = N.divide(factor);
        if(factor.isProbablePrime(20)) {
        	System.out.println("# Nu lägger jag till " + factor + " som ett primtal!");
        	task.setPartResult(factor);
        } else if(!factor.equals(BigInteger.ONE)) {
        	System.out.println("# Adding factor to queue: " + factor);
        	task.push(factor);
        }
        
        if(quo.isProbablePrime(20)) {
        	System.out.println("# Nu lägger jag  (qup) till " + quo + " som ett primtal!");
        	task.setPartResult(quo);
        } else if(!quo.equals(BigInteger.ONE)) {
        	System.out.println("# Adding quo to queue: " + quo);
        	task.push(quo);
        }
        
        System.out.println("# Current results:");
        for(BigInteger result : task.getResults()) 
        	System.out.println(result);
        System.out.println();
        System.out.println();
       
    }
	
	public void swap_rows(int[] row1, int[] row2) {
		if(row1.length != row2.length)
			throw new IllegalArgumentException("different row sizes");		
		
		for(int i = 0; i < row1.length; i++) {
			int tmp = row1[i];
			row1[i] = row2[i];
			row2[i] = tmp;
		}
	}

    //  Q(x) = (√N + x)^2 − N
    public BigInteger Q(BigInteger x, BigInteger nrootceil, BigInteger toFactor) {
    	//perfectGeo.getPowRoot(x, k)
    	// nrootceil.add
    	//.mod(toFactor);
    	return nrootceil.add(x).pow(2).subtract(toFactor);
    }
    
    @Override
    public void factor(Task task) {
    	while(!task.isFinished()) {
    		factor(task, task.poll());
    		return;
    	}
    }
    
    private BigInteger getSmoothnessValue(BigInteger N)
    {
        BigDecimal b = new BigDecimal(N.toString());
//        b.setScale(precision, RoundingMode.DOWN);
        
        BigDecimal C = new BigDecimal(3); 
//        C.setScale(precision, RoundingMode.HALF_DOWN);
        BigDecimal logn = BigMath.log(b);
        BigDecimal loglogn = BigMath.log(logn);
        BigDecimal exponent = BigMath.sqrt(logn.multiply(loglogn)).divide(TWO, RoundingMode.HALF_DOWN);
        BigDecimal s = BigMath.exp(exponent).multiply(C);
        return s.toBigInteger();
    }
    
    public static void main(String[] args)
    {
    	BigInteger b = new BigInteger("15347");
//    	BigInteger b = new BigInteger("87463");
//    	BigInteger b = new BigInteger("90283");
//    	BigInteger b = new BigInteger("90283");
//        BigInteger b = new BigInteger("138");
//    	BigInteger b = new BigInteger("16843009");
        //BigDecimal d = new BigDecimal("87");
        //d.setScale(10000, RoundingMode.UP);
        
        Factor_QuadraticSieve f = new Factor_QuadraticSieve(new SieveAtkins(1000));
        
//        System.out.println(f.getSmoothnessValue(b));
        
        BigInteger x = new BigInteger("22678");
        BigInteger y = new BigInteger("195").pow(2);
        
        BigInteger t1 = y.subtract(x).gcd(b);
        BigInteger t2 = x.add(y).gcd(b);
        
        System.out.println(t1);
        System.out.println(t2);
        System.out.println();
        
        Task t = new Task(0, b, new Timing(200000));
//        BigInteger n = new BigInteger("13");
//        BigInteger p = new BigInteger("17");
//        BigInteger[] results = f.tonelli_shanks(n.mod(p), p);
//        System.out.println(results[0]);
//        System.out.println(results[1]);
        
        f.factor(t);
        System.out.println();
        System.out.println();
        if(t.isFinished())
		    for(BigInteger val: t.getResults())
		    {
		        System.out.println(val);
		    }
        
        //BigInteger res = _BigIntegerMath.isqrt(b);
        //System.out.println("sqrt(" + b.toString() + ") = " + res);
    }
    
    
    
}
