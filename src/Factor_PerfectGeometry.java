import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class Factor_PerfectGeometry implements FactorMethod {
	
	public static int NumberOfDimensions = 4;
	public static int PrimeCertainty = 20;
	
	/**
	 * Checks if task.toFactor is a perfect square, cube or ... up to NumberOfDimensions.
	 * If so appends that root that many times and returns finished.
	 */
	public boolean factor(Task task, BigInteger val) {		
		
		for(int k = 2; k < NumberOfDimensions; k++)
		{
			if(task.isTimeout()) {				
				return false;
			}
			
			BigInteger root = getPowRoot(val, k);
			if(root == null)
				continue;
			
			if(root.isProbablePrime(PrimeCertainty))
				for(int i = 0; i < k; i++)
					task.setPartResult(root);	
			else {
				for(int i = 0; i < k; i++)
					task.push(root);		
			}
			return true;
		}
		
		return false;
				
	}
	
	public void factor(Task task) {
		List<BigInteger> partFactors = new ArrayList<>();
        while(!task.isFinished())
        {
        	BigInteger val = task.poll();
        	boolean managedToFilter = factor(task, val);
        	if(task.isTimeout())
        		return;        
        	if(!managedToFilter)
        		partFactors.add(val);
        }
        
        for(BigInteger leftToFactor : partFactors) {
        	task.push(leftToFactor);
        }
	}
	
	
    /**
     * Finds root with exponent k of given value. 
     * Returns null on failure.
     * @param value
     * @param k
     * @return
     */
    public static BigInteger getPowRoot(BigInteger value, int k) {
        BigDecimal converted_value = new BigDecimal(value.toString());
        //new BigDecimal(value.toString());
        BigDecimal x = new BigDecimal("2").pow(value.bitLength() / k);   
        
        BigDecimal fx;
        BigDecimal fprimx;
        BigDecimal xold;
        
        // Newton-Raphson
        // x - f(x) / f'(x)
        // for function: x2 - n = 0

        do {
            xold = x;
            fx = xold.pow(k).subtract(converted_value);                                
            fprimx = xold.pow(k-1).multiply(BigDecimal.valueOf(k));
            // Note: not sure why I m using halfdown here. But it seems to work.
            x = xold.subtract(fx.divide(fprimx, RoundingMode.HALF_DOWN));
//            System.out.println(x + " " + xold + " " + x.subtract(xold) + " " + x.pow(k));
        } while(x.subtract(xold).abs().compareTo(BigDecimal.ONE) >= 0);
        
        if(x.pow(k).equals(converted_value))
            return x.toBigIntegerExact();
        return null;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FactorMethod f = new Factor_PerfectGeometry();
		
	    BigInteger N = new BigInteger("1337").pow(10);
	    Task t = new Task(0, N, new Timing(1500));
	    f.factor(t);
	    if(t.isFinished())
		    for(BigInteger val: t.getResults())
		    {
		        System.out.println(val);
		    }
	}

}
