
import java.util.ArrayList;
import java.util.List;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 *  Atkins Sieve that finds prime numbers up to N in O(N/loglogN) time
 *  http://en.wikipedia.org/wiki/Sieve_of_Atkin
 * 
 * @author Frederick CEder
 */
public class SieveAtkins implements SievePrime {

    private int SieveLimit;
    private double limitSqrt;
    
    private List<Integer> primes;
    
    public SieveAtkins(int limit)
    {
        SieveLimit = limit;
        initSieve();
    }

    /**
     * Populate the primes Set
     */
    private void initSieve()
    {
        
        boolean[] sieve = new boolean[SieveLimit+1];    // Initially false -> no prime
        sieve[0] = false;
        sieve[1] = false;
        sieve[2] = true;
        sieve[3] = true;
        
        limitSqrt = Math.sqrt((double)SieveLimit);
        for( int x = 1; x <= limitSqrt; x++)
        {
            int xSqr = x*x;
            for (int y = 1; y <= limitSqrt ; y++ ) 
            {
                int n;
                int ySqr = y*y;
                
                n = 4*(xSqr) + (ySqr);
                if( n <= SieveLimit && ( (n % 12 == 1) || (n % 12 == 5 ) ) )
                {
                    sieve[n] = !sieve[n];
                }

                n = 3*(xSqr) + (ySqr);
                if( n <= SieveLimit && (n % 12 == 7) )
                {
                    sieve[n] = !sieve[n];
                }   

                n = 3*(xSqr) - (ySqr);
                if( (x > y) && n <= SieveLimit && (n % 12 == 11) )
                {
                    sieve[n] = !sieve[n];
                }   
            }
        }
        
        // Remove perfect squares
        for(int n = 5; n <= limitSqrt; n++)
        {
            if(sieve[n]) {
                int x = n*n;
                for(int i = 0; i <= SieveLimit; i  += x)
                {
                    sieve[i] = false;
                }
            }
        }
        
        primes = new ArrayList<>();
        primes.add(2);primes.add(3);
        // OUtput to primes collection
        for(int i = 5; i <= SieveLimit; i++) {
            if(sieve[i]) {
               primes.add(i);
            }
        }
        
    }
    
    @Override
    public List<Integer> getPrimes() {
        return primes;
    }

    @Override
    public int getLimit() {
        return SieveLimit;
    }

    @Override
    public int getPrimeCount() {
        return primes.size();
    }

    @Override
    public int getPrimeAt(int index) {
        return primes.get(index);
    }
    
    @Override
    public int getLargestPrime() {
        return primes.get(primes.size()-1);
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for(Integer prime: primes)
        {
            sb.append(prime).append(" ");
        }
        return sb.toString();
    }
    
    public static void main(String args[])
    {
        SievePrime sieve = new SieveAtkins(33);
        System.out.println(sieve);
        System.out.println("Largest prime is " + sieve.getLargestPrime());
        System.out.println("Primes in sieve: " + sieve.getPrimeCount());
        System.out.println("Sieve limit is: " + sieve.getLimit());
        
    }
}
