/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author fred
 */
public class Factor_TrialPollardRho implements FactorMethod {

    private FactorMethod trialDivison;
    private FactorMethod pollardRho;
    
    public Factor_TrialPollardRho(int primeNumbers)
    {
        this(new SieveAtkins(primeNumbers));
    }
    
    public Factor_TrialPollardRho(SievePrime primeSieve)
    {
        trialDivison = new Factor_TrialDivision(primeSieve);
        pollardRho = new Factor_PollardRho();        
    }
    
    @Override
    public void factor(Task task) {
        
        trialDivison.factor(task);
        if(task.isFinished()) {
            return;
        }
        // Perfect square        
        pollardRho.factor(task);
        return;
    }
    
}
