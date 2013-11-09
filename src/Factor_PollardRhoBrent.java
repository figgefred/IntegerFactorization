
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
    private BigInteger THREE = new BigInteger("3");
    private BigInteger TENTHOUSAND = new BigInteger("10000");
    private BigInteger ONE = BigInteger.ONE;
    private BigInteger ZERO = BigInteger.ZERO;
    
    private Random random = new Random(1337);
    
    private BigInteger f(BigInteger y, BigInteger c, BigInteger n)
    {
        return ((y.multiply(y)).add(c)).mod(n);
    }
    
    public BigInteger factorizebrent(Task task, BigInteger n) {
        
        if(n.mod(TWO).equals(ZERO))
        {
            return TWO;
        }
        
        BigInteger k = new BigInteger("1");
        BigInteger i = new BigInteger("1");
        
        BigInteger x = new BigInteger("1");
        BigInteger y = new BigInteger("1");
        BigInteger ys = new BigInteger("1");

        BigInteger m=TEN;
        BigInteger r=ONE;
        BigInteger iter=ZERO;
        BigInteger z=ZERO;
        BigInteger q=ONE;

        y=z;
        
        do {
            x=y;
            for (i=ONE;i.compareTo(r)<=0;i=i.add(ONE)) 
                y= f(y, THREE, n);
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
                    y = f(y, THREE, n);
                    q = ((y.subtract(x)).multiply(q)).mod(n);
                }
                z = n.gcd(q);
                k = k.add(m);
            } while (k.compareTo(r)<0 && z.compareTo(ONE)==0);
            r = r.multiply(TWO);
        } while (z.compareTo(ONE)==0 && iter.compareTo(TENTHOUSAND)<0);

        if (z.compareTo(n)==0)  {
            do {
                ys = f(ys, THREE, n);
                z = n.gcd(ys.subtract(x));
            } while (z.compareTo(ONE)==0);
        }
        if(z.equals(n)) {
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
