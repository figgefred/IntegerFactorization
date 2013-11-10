import java.math.BigInteger;


public class Factor_SQUFOF implements FactorMethod {
	
	public Factor_PerfectGeometry geo = new Factor_PerfectGeometry();

	public BigInteger SQUFOF(Task task, BigInteger N, int k) {
		System.out.println("# input");
		System.out.println("N " + N);
		System.out.println("k "  + k);
		System.out.println();
		
		
		BigInteger kn = N.multiply(BigInteger.valueOf(k));		
		BigInteger sqrkn = BigMath.isqrt(kn);
		BigInteger P0 = sqrkn;
		BigInteger Q0 = BigInteger.ONE;
		BigInteger Q1 = kn.subtract(P0.pow(2));
		BigInteger P1 = null;
		System.out.println("# init");
		System.out.println("P0 " + P0);
		System.out.println("Q0 " + Q0);
		System.out.println("Q1 " + Q1);
		System.out.println();
		
		int i = 0;
		do {
			
			BigInteger bi = sqrkn.add(P0).divide(Q1);
			P1 = bi.multiply(Q1).subtract(P0);
			BigInteger tmp = Q1;
			Q1 = Q0.add(bi.multiply(P0.subtract(P1)));
			P0 = P1;
			
			Q0 = tmp;
			System.out.println("P"+(++i)+" " + P0);
			System.out.println("Q"+(i)+" "  + Q0);
			System.out.println("Q"+(i+1)+ " " + Q1);
			System.out.println();
			
		} while(Q1.compareTo(BigInteger.ONE) > 0 && geo.getPowRoot(Q1, 2) == null);
		
		System.out.println("# Perfect square found:");
		System.out.println(Q1);
		System.out.println(geo.getPowRoot(Q1, 2));
		System.out.println();
		
		
		BigInteger qsqrt = BigMath.isqrt(Q1);		
		BigInteger b0 = sqrkn.subtract(P0).divide(qsqrt);
		P0 = b0.multiply(qsqrt).add(P0);
		Q0 = qsqrt;
		Q1 = kn.subtract(P0.pow(2)).divide(Q0);
		System.out.println("# init second loop");
		System.out.println("P0 " + P0);
		System.out.println("Q0 " + Q0);
		System.out.println("Q1 " + Q1);
		System.out.println();
		i = 0;
		
		while(true) {
		 
		  BigInteger bi = sqrkn.add(P0).divide(Q1);
		  P1 = bi.multiply(Q1).subtract(P0);
		  BigInteger tmp = Q1;
		  Q1 = Q0.add(bi.multiply(P0.subtract(P1)));
	
			if(P0.equals(P1))
				break;
			P0 = P1;
			Q0 = tmp;
			System.out.println("P"+(++i)+" " + P0);
			System.out.println("Q"+(i)+" "  + Q0);
			System.out.println("Q"+(i+1)+ " " + Q1);
			System.out.println();
		} 
		
		return N.gcd(P1);
	}
	
	@Override
	public void factor(Task task) {
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BigInteger N = new BigInteger("205830336406552449043829074205");
		int k = 1;
		
		Factor_SQUFOF squ = new Factor_SQUFOF();
		System.out.println(squ.SQUFOF(N, k));

	}

}
