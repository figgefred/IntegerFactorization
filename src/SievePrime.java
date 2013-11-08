
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
    public int getLargestPrime();
    public int getUpperLimit();
    public int getPrimeCount();
    public int getPrimeAt(int index);
}