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

    private FactorMethod trialDivision;
    
    public Factor_QuadraticSieve(int primeSieveLimit)
    {
        this(new SieveAtkins(primeSieveLimit));
    }
    
    public Factor_QuadraticSieve(SievePrime primeSieve)
    {
        trialDivision = new Factor_TrialDivision(primeSieve);
        
    }
    
    @Override
    public void factor(Task task) {
        
        // Step 1 - factor out small primes
        trialDivision.factor(task);
        if(task.isFinished()) {
            return;
        }
        
        // Step 2 - is 'N' perfect exponent?
        
        
    }
    
    
    
    
}
