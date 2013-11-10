/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author figgefred
 */
public class Factor_TrialRhoBrent extends Factor_PollardRhoBrent {
    private FactorMethod trialDivison;
    
    public Factor_TrialRhoBrent(int primeNumbers)
    {
        this(new SieveAtkins(primeNumbers));
    }
    
    public Factor_TrialRhoBrent(SieveAtkins primeSieve)
    {
        trialDivison = new Factor_TrialDivision(primeSieve);
    }
    
    @Override
    public void factor(Task task) {
        
        trialDivison.factor(task);
        if(task.isFinished()) {
            return;
        }
              
        super.factor(task);
        return;
    }
}
