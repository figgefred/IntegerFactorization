
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fred
 */
public interface SievePrime {
    public List<Integer> getPrimes();
    public boolean contains(int val);
    public int getLargestPrime();
    public int getLimit();
    public int getPrimeCount();
    public int getPrimeAt(int index);
}