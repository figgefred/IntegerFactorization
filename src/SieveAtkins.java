
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
    
    private List<Integer> primes;
    
    public SieveAtkins(int limit)
    {
        SieveLimit = limit;
        init();
    }
    
    /**
     * Populate the primes Set
     */
    private void init()
    {
        boolean[] sieve = new boolean[SieveLimit+1];    // Initially false -> no prime
        
        sieve[0] = false;
        sieve[1] = false;
        sieve[2] = true;
        sieve[3] = true;
        
        double limitSqrt = Math.sqrt((double)SieveLimit);
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
    
}
