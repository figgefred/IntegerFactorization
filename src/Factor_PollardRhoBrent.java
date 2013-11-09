
import java.math.BigInteger;
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author figgefred
 */
public class Factor_PollardRhoBrent implements FactorMethod {
    
    private BigInteger TEN = BigInteger.TEN;
    private BigInteger TWO = new BigInteger("2");
    private BigInteger ONE = BigInteger.ONE;
    private BigInteger ZERO = BigInteger.ZERO;
    
    private BigInteger MAXITERS = new BigInteger("10000");
    
    private Random random = new Random(1337);
    
    private BigInteger f(BigInteger y, BigInteger C, BigInteger n)
    {
        return ((y.multiply(y)).add(C)).mod(n);
    }
    
    public BigInteger factorizebrent(Task task, BigInteger n) {
        
        if(n.mod(TWO).equals(ZERO))
        {
            return TWO;
        }
        
        BigInteger k = null;
        BigInteger i = null;
        BigInteger x = null;
        BigInteger ys = null;

        long tmp =(long) random.nextInt(10)+1; 
        BigInteger m = BigInteger.valueOf(tmp);
        BigInteger r=ONE;
        BigInteger iter=ZERO;
        BigInteger z=new BigInteger(n.bitCount(), random);
        BigInteger q=ONE;
        
        
        BigInteger C = new BigInteger(n.bitCount(), random);

        BigInteger y = z;
        
        do {
            x=y;
            for (i=ONE;i.compareTo(r)<=0;i=i.add(ONE)) 
                y= f(y, C, n);
            k=ZERO;
            do {
                if(task.isTimeout())
                {
                    return null;
                }
                
                iter=iter.add(ONE);
                // System.out.print("iter=" + iter.toString() + '\r');
                ys=y;
                for (i=ONE; i.compareTo(min(m,r.subtract(k))) <=0 ; i=i.add(ONE)) {
                    y = f(y, C, n);
                    q = ((y.subtract(x)).multiply(q)).mod(n);
                }
                z = n.gcd(q);
                k = k.add(m);
            } while (k.compareTo(r)<0 && z.compareTo(ONE)==0);
            r = r.multiply(TWO);
        } while (z.compareTo(ONE)==0 && iter.compareTo(MAXITERS)<0);

        if (z.compareTo(n)==0)  {
            do {
                ys = f(ys, C, n);
                z = n.gcd(ys.subtract(x));
            } while (z.compareTo(ONE)==0);
        }
        if(z.equals(n) || z.equals(ONE)) {
            return null;
        }
        return z;
    }
    
    private BigInteger min(BigInteger v1, BigInteger v2)
    {
        if(v1.compareTo(v2) > 1)
        {
            return v2;
        }
        return v1;
    }
    
    @Override
    public void factor(Task task) {
    	while(!task.isFinished()) {    		
	        BigInteger toFactor = task.poll();
//	        System.out.println(toFactor);
	        
	        if (toFactor.compareTo(ONE) == 0) {
	            continue;
	        }
	        if (toFactor.isProbablePrime(20)) { 
	            task.setPartResult(toFactor);
	            continue;
	        }
	        
                // Tofactor may not be prime
                
	        BigInteger divisor = null;
	        while(true)
	        {
                    divisor = factorizebrent(task, toFactor);
                    if(task.isTimeout())
                        return;
                    if(divisor == null)
                        continue;
                    if(!divisor.equals(toFactor))
                        break;
                    System.out.println(divisor);
	        }
	        task.push(divisor);	   
//	        factor(task);
	        task.push(toFactor.divide(divisor));	   
//	        factor(task);
    	}
    }
    
    public static void main(String[] args)
    {
        BigInteger b = new  BigInteger("83209473483892");
        Task t = new Task(0, b, new Timing(200000000));
        FactorMethod f = new Factor_PollardRhoBrent();
        f.factor(t);
        System.out.println(t);
        
    }
    
}
