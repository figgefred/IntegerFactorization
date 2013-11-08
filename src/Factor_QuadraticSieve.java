
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

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
    private BigDecimal TWO;
    private FactorMethod trialDivision;
    
    public Factor_QuadraticSieve(int primeSieveLimit)
    {
        this(new SieveAtkins(primeSieveLimit));
    }
    
    public Factor_QuadraticSieve(SievePrime primeSieve)
    {
        trialDivision = new Factor_TrialDivision(primeSieve);
        TWO = new BigDecimal(2);
        TWO.setScale(precision, RoundingMode.UP);
    }
    
    @Override
    public void factor(Task task) {
        
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
        System.out.println(task.toFactor);
        
        // Step 3 - is 'N' perfect exponent?
        // Step over
        
        // Step 4 - Find smoothness value: O(e^(0.5*sqrt(logNloglogN))

        
        BigInteger B = getSmoothnessValue(task.toFactor);
        System.out.println(B);
        return;
    }
    
    private BigInteger getSmoothnessValue(BigInteger N)
    {
        BigDecimal b = new BigDecimal(N.toString());
        b.setScale(precision, RoundingMode.UP);
        
        BigDecimal C = new BigDecimal(3D); C.setScale(precision, RoundingMode.UP);
        BigDecimal logn = _BigDecimalMath.log(b);
        BigDecimal loglogn = _BigDecimalMath.log(logn);
        BigDecimal exponent = _BigDecimalMath.sqrt(logn.multiply(loglogn)).divide(TWO);
        BigDecimal s = _BigDecimalMath.exp(exponent).multiply(C);
        return new BigInteger(s.toString().split("\\.")[0]);
    }
    
    public static void main(String[] args)
    {
        BigInteger b = new BigInteger("3489073525034625327");
        //BigDecimal d = new BigDecimal("87");
        //d.setScale(10000, RoundingMode.UP);
        
        Factor_QuadraticSieve f = new Factor_QuadraticSieve(new SieveAtkins(1000));
        Task t = new Task(0, b, new Timing(200000));
        
        f.factor(t);
        
        //BigInteger res = _BigIntegerMath.isqrt(b);
        //System.out.println("sqrt(" + b.toString() + ") = " + res);
    }
    
    
    
}
