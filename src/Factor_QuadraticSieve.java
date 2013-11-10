
import java.io.ObjectInputStream.GetField;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
    
    public Factor_QuadraticSieve(SieveAtkins primeSieve)
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
    
  
// Pre INIT for step 4 >>> X
        BigInteger B = getSmoothnessValue(N);
        BigInteger nrootceil = BigMath.sqrt(new BigDecimal(N.toString())).setScale(0, RoundingMode.CEILING).toBigInteger();
        int magicIntervalConstant = 60;
        
// Step 4 - Find smoothness value: O(e^(0.5*sqrt(logNloglogN)) 
        // Also find nrootceiling sqrt(N)
        System.out.println("# Smoothness (B):");
        System.out.println();
        System.out.println("  " + B);
        System.out.println("# END of Smoothness");
        System.out.println();
        System.out.println("# nRoot:");
        System.out.println();
        System.out.println("  " + nrootceil);
        System.out.println("# END of nRoot");
        System.out.println();
        
// Step 5 - Determine factor base, primes where N / P = 1
        System.out.println("# Factor base");
        System.out.println();
        BigInteger lastPrime = BigInteger.valueOf(sieve.getLargestPrime());
        List<BigInteger> factorBase = getFactorBase(N, B);
        printList(factorBase);
        System.out.println("# End of factor base");
        System.out.println();
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Matrix xRes = null;
        
    redo:
    while(true) {
// Step 6 - Determine inital values of V  --> Y(X)
        BigInteger[] Vs = new BigInteger[magicIntervalConstant]; // 100?!?!?
        BigInteger[] Qs = new BigInteger[Vs.length];
        setVsAndQs(N, nrootceil, Vs, Qs);
        
        System.out.println("# Q values");
        System.out.println();
        printList(Qs);
        System.out.println("# END of Q values");
        System.out.println();
        
        System.out.println("# V values");
        System.out.println();
        printList(Vs);
        System.out.println("# END of V values");
        System.out.println();
        
// Step 7 - Sieving --> Y(X) congr. (X+ sqrt(N))^2 - N congr. n mod p
        System.out.println("# Sieving - Y(X) congr. (X+ sqrt(N))^2 - N congr. n mod p");
        System.out.println();
    	for(int x = 0 ; x < factorBase.size(); x++)
    	{
    		BigInteger prime = factorBase.get(x);
    		BigInteger[] results = tonelli_shanks(N.mod(prime), prime);
    		
    		for(BigInteger result : results) {
    			System.out.println("  " + prime + " tonnelli: " + result);
    			int startindex = result.intValue() - nrootceil.intValue() % prime.intValue();  
    			System.out.println("  " + prime + " (before) " + startindex);
    			while(startindex < 0)
    				startindex += prime.intValue();
    			System.out.println("  " + prime + " (after) " + startindex);
    			System.out.println();
    			
	// V[i]/p until Vs[i] mod prime != 0
    			for(int i = startindex; i < Vs.length; i += prime.intValue())    
    				while(Vs[i].mod(prime).equals(BigInteger.ZERO))
    					Vs[i] = Vs[i].divide(prime);
    		}
    	}
    	System.out.println("# END of Sieving");
    	System.out.println();
        
	// Compiling sieving results - determining "smooth values"
    	List<BigInteger> ys = new ArrayList<BigInteger>();
    	List<BigInteger> is = new ArrayList<BigInteger>();
    	
    	System.out.println("# Smooth values found:");
    	System.out.println();
    	for(int i = 0; i < Vs.length; i++) 
        {
    		if(Vs[i].equals(BigInteger.ONE)) {
    			BigInteger q = Qs[i];
    			System.out.println(("  " + "i:" + (i+nrootceil.intValue())) + " y:" + Vs[i] + " q:" + q);
    			System.out.println();
    			ys.add(q);
    			is.add(BigInteger.valueOf(i).add(nrootceil));
    		}
        }
    	System.out.println("# End of Smooth values");
    	System.out.println();
   
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if(ys.size() == 0) {
    		magicIntervalConstant += 2;
    		continue redo;
    	}
    	
// Step 8 - Setup matrix then gauss eliminate    	

   
    	System.out.println("# Matrix voodoo:");
    	System.out.println();
    	xRes = setupMatrix(ys, factorBase);
    	System.out.println("# END Matrix voodoo");
    	System.out.println();
    	
    	
    	BigInteger[] factors = findEquation(ys, is, xRes, factorBase, N);
    	if(factors == null) {
    		magicIntervalConstant += 2;    	
    		continue redo;
    	} else {
    		task.setPartResult(factors[0]);
    		task.setPartResult(factors[1]);
    	}
    	
    	break;
    }
    // 	END OF WHILE ITERATION
    	
	/*	BigInteger prod1 = BigInteger.ONE;
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
       */
    }
	
	private void setVsAndQs(BigInteger N, BigInteger nrootceil, BigInteger[] vs, BigInteger[] qs) {
        
        for(int i = 0; i < vs.length; i++)
        {
        	vs[i] = Q(BigInteger.valueOf(i), nrootceil, N);
        	qs[i] = vs[i];
        }
		
	}

	private List<BigInteger> getFactorBase(BigInteger N, BigInteger B) {
		List<BigInteger> factorBase = new ArrayList<>();
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
        return factorBase;
	}
	
	private Matrix setupMatrix(List<BigInteger> ys, List<BigInteger> factorBase) {
    	// Whoa
    	
		
		System.out.println("# Init matrix A");
		System.out.println();
    	Matrix A = new Matrix(ys.size(), factorBase.size());
		for(int i = 0; i < A.rowCount(); i++) {			
	    	for(int j = 0; j < A.colCount(); j++) {
	    		BigInteger tmp = ys.get(i);
	    		BigInteger prime = factorBase.get(j);
	    		
    			int e = 0;    			
    			while(tmp.mod(prime).equals(BigInteger.ZERO)) {
    				tmp = tmp.divide(prime);
    				e++;
    			}
    			
//    			ematrix[i][j] = e;
    			A.set(i, j, e );
    		}
	    	
//	    	System.out.println(matrix[i]);    		
    	}
		printMatrix(A);
		
		System.out.println("# END matrix A");
    	System.out.println();
    	return A;
	}
	
	public void printMatrix(Matrix a)
	{
		for(int r = 0; r < a.rowCount(); r++)
		{
			System.out.print("  ");
			for(int c = 0; c < a.colCount(); c++)
			{
				System.out.print( ((int)a.get(r, c)) + " ");
			}
			System.out.println();
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
    		// Step 1, 2  - factor out small primes
            trialDivision.factor(task);
            if(task.isFinished()) {
                System.out.println("Done!");
    	        System.out.println("Factors found: ");
    	        for(BigInteger r: task.getResults())
    	        {
    	            System.out.println(r);
    	        }
    	        System.out.println();
                return;
            }
        	
        	// Step 3 - is 'N' perfect exponent?
            // Step over
            perfectGeo.factor(task);
            
            if(task.isFinished())
            	return;
            	    
    		factor(task, task.poll());
    		return;
    	}
    }
    
    private BigInteger getSmoothnessValue(BigInteger N)
    {
        BigDecimal b = new BigDecimal(N.toString());
//        b.setScale(precision, Rounding        for()Mode.DOWN);
        
        BigDecimal C = new BigDecimal(3); 
//        C.setScale(precision, RoundingMode.HALF_DOWN);
        BigDecimal logn = BigMath.log(b);
        BigDecimal loglogn = BigMath.log(logn);
        BigDecimal exponent = BigMath.sqrt(logn.multiply(loglogn)).divide(TWO, RoundingMode.HALF_DOWN);
        BigDecimal s = BigMath.exp(exponent).multiply(C);
        return s.toBigInteger();
    }
    
    public BigInteger[] findEquation(List<BigInteger> smoothNumbers, List<BigInteger> Qs, Matrix exponentsMatrix, List<BigInteger> factorBase, BigInteger target) {
    	Queue<BigInteger> products = new LinkedList<>();
    	Queue<Integer[]> exponentsQueue = new LinkedList<Integer[]>(); 
    	Queue<Set<BigInteger>> visitedQueue = new LinkedList<Set<BigInteger>>();
    	
//    	Set<BigInteger> visited = new HashSet<>();
    	
    	Map<BigInteger, Integer[]> savedExponents = new HashMap<BigInteger, Integer[]>();
    	
    	for(int i = 0; i < smoothNumbers.size(); i++) {
    		BigInteger smooth = smoothNumbers.get(i);
    		products.add(smooth);
    		Integer[] exponents = new Integer[factorBase.size()];
    		for(int e = 0; e < factorBase.size(); e++)
    			exponents[e] = (int) exponentsMatrix.get(i, e);
    		exponentsQueue.add(exponents);
    		savedExponents.put(smooth, exponents);
    		HashSet<BigInteger> visited = new HashSet<BigInteger>();
    		visited.add(smooth);
    		visitedQueue.add(visited);
    	}

    	while(!products.isEmpty()) {
    		BigInteger product = products.poll();
    		Integer[] exponents = exponentsQueue.poll();
    		Set<BigInteger> visited = visitedQueue.poll();
    		System.out.println(product);
    	
    		for(BigInteger smooth : smoothNumbers) {
    			if(visited.contains(smooth))
    				continue;
    			Set<BigInteger> newVisited = new HashSet<BigInteger>();
    			newVisited.add(smooth);
    			for(BigInteger b : visited)
    				newVisited.add(b);
//    			if(product.mod(smooth).equals(BigInteger.ZERO))
//    				continue;    			
    			BigInteger newProd = smooth.multiply(product);
    		
    			
    			Integer[] savedExponent = savedExponents.get(smooth);
    			Integer[] newExponents = new Integer[factorBase.size()];
    			for(int e = 0; e < factorBase.size(); e++)
    			{
    				newExponents[e] = savedExponent[e] + exponents[e];
    			}
    			System.out.println(Arrays.toString(newExponents));
    			
    			//check if each exponent mod 2 == 0, if so break.
    			boolean allMod2 = true;
    			for(int e = 0; e < factorBase.size(); e++) {
    				allMod2 = allMod2 && (newExponents[e] % 2 == 0);
    				if(!allMod2)
    					break;
    			}
    			
    			if(allMod2) {
    				System.out.println("Trying: " + product + " * " + smooth + " = " + newProd);
    				BigInteger[] result = new BigInteger[2];  
    				 
    				BigInteger y = BigInteger.ONE;    				 
					for(int i = 0; i < factorBase.size(); i++) {
						BigInteger f = factorBase.get(i);				
						y = y.multiply(f.pow(newExponents[i] / 2));					
					}				
					
//					result[1] = perfectGeo.getPowRoot(result[1], 2);
					System.out.println("x: " + newProd);
					System.out.println("y: " + y);		
					
					// Try for gcd.
					result[0] = newProd.subtract(y).gcd(target);
					result[1] = newProd.add(y).gcd(target);
					System.out.println("Result0,0: " + result[0]);
					System.out.println("Result0,1: " + result[1]);
					if(result[0].compareTo(BigInteger.ONE) == 1 && result[0].isProbablePrime(20))
					{
						return result;
					}					
					
					y = BigInteger.ONE;    				 
					for(int i = 0; i < Qs.size(); i++) {
//						BigInteger f = factorBase.get(i);				
						if(newVisited.contains(smoothNumbers.get(i)))
							y = y.multiply(Qs.get(i).pow(2));					
					}		
					y = perfectGeo.getPowRoot(y, 2);
					BigInteger x = perfectGeo.getPowRoot(newProd, 2);
					
					result[0] = y.subtract(x).gcd(target);
					result[1] = result[0];
					System.out.println("Result1,0: " + result[0]);
					System.out.println("Result1,1: " + result[1]);
					
					if(result[0].compareTo(BigInteger.ONE) == 1 && result[0].isProbablePrime(20))
					{
						return result;
					}	
					
    			}
    			
    			
    					
    			products.add(newProd);
    			exponentsQueue.add(newExponents);
    			visitedQueue.add(newVisited);
    			
    		}    

    	}
    		
    	
    	
    	
    	return null;
    }
    
    public static void main(String[] args)
    {
//    	BigInteger b = new BigInteger("15347");
//    	BigInteger b = new BigInteger("87463");Object
//    	BigInteger b = new BigInteger("90283");
    	BigInteger b = new BigInteger("592626948853377802963333718761");
//        BigInteger b = new BigInteger("138");
//    	BigInteger b = new BigInteger("16843009");
        //BigDecimal d = new BigDecimal("87");
        //d.setScale(10000, RoundingMode.UP);
        
        Factor_QuadraticSieve f = new Factor_QuadraticSieve(new SieveAtkins(100));
        
//        System.out.println(f.getSmoothnessValue(b));
        
        BigInteger x = new BigInteger("22678");
        BigInteger y = new BigInteger("195").pow(2);
        
        BigInteger t1 = y.subtract(x).gcd(b);
        BigInteger t2 = x.add(y).gcd(b);
        int i = 0;
    /*	for(Object o: objs)
    	{
    		System.out.println("  [" + (i++) + "]" + ": " + o.toString());
    	}*/
        System.out.println(t1);
        System.out.println(t2);
        System.out.println();
        
        Task t = new Task(0, b, new Timing(200000));
//        BigInteger n = new BigInteger("13");
//        BigInteger p = new BigInteger("17");
//  smooth.add(new BigInteger("9"));      BigInteger[] results = f.tonelli_shanks(n.mod(p), p);
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
//        
        
//        BigInteger target = new BigInteger("15347");
//        List<BigInteger> smooth = new ArrayList<BigInteger>();
//        smooth.add(new BigInteger("29"));
//        smooth.add(new BigInteger("782"));
//        smooth.add(new BigInteger("22678"));
//        List<BigInteger> Is = new ArrayList<BigInteger>();
//        Is.add(new BigInteger("124"));
//        Is.add(new BigInteger("127"));
//        Is.add(new BigInteger("195"));
//        List<BigInteger> factorBase = new ArrayList<BigInteger>();
//        factorBase.add(new BigInteger("2"));
//        factorBase.add(new BigInteger("17"));
//        factorBase.add(new BigInteger("23"));
//        factorBase.add(new BigInteger("29"));
//        double exponents[][] = {
//        		{0,0,0,1},
//        		{1,1,1,0},
//        		{1,1,1,1}
//        };
//        Matrix exMatrix = new Matrix(exponents);
        
        
//        BigInteger target = new BigInteger("16843009");
//        List<BigInteger> smooth = new ArrayList<BigInteger>();
//        smooth.add(new BigInteger("4122"));
//        smooth.add(new BigInteger("4159"));
//        smooth.add(new BigInteger("4187"));
//        smooth.add(new BigInteger("4241"));
//        smooth.add(new BigInteger("4497"));
//        smooth.add(new BigInteger("4993"));        
//        List<BigInteger> Is = new ArrayList<BigInteger>();
//        Is.add(new BigInteger("147875"));
//        Is.add(new BigInteger("454272"));
//        Is.add(new BigInteger("687960"));
//        Is.add(new BigInteger("1143072"));
//        Is.add(new BigInteger("3380000"));
//        Is.add(new BigInteger("8087040"));
//        List<BigInteger> factorBase = new ArrayList<BigInteger>();
//        factorBase.add(new BigInteger("2"));
//        factorBase.add(new BigInteger("3"));
//        factorBase.add(new BigInteger("5"));
//        factorBase.add(new BigInteger("7"));
//        factorBase.add(new BigInteger("13"));
//        double exponents[][] = {
//        		{0,0,3,1,2},
//        		{7,1,0,1,2},
//        		{3,3,1,2,1},
//        		{5,6,0,2,0},
//        		{5,0,4,0,2},
//        		{9,5,1,0,1}
//        };
//        Matrix exMatrix = new Matrix(exponents);
//        
//      
//        BigInteger[] result = f.findEquation(smooth, Is, exMatrix, factorBase, target);
//        System.out.println();
//        if(result != null) {
//        	System.out.println(result[0]);
//        	System.out.println(result[1]);
//        }
//        System.out.println(result[0].subtract(result[1]).gcd(target));
//        System.out.println(result[0].add(result[1]).gcd(target));
        
    }
    
    private void printList(Object[] objs)
    {
    	int i = 0;
    	for(Object o: objs)
    	{
    		System.out.println("  [" + (i++) + "]" + ": " + o.toString());
    	}    	
    }
    
    private void printList(List objs)
    {   	
    	int i = 0;
    	for(Object o: objs)
    	{
    		System.out.println("  [" + (i++) + "]" + ": " + o.toString());
    	}
    }
    

    
    
}
