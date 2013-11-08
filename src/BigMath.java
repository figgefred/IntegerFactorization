
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * "A Java Math.BigDecimal Implementation of Core Mathematical Functions"
 * @author Richard J. Mathar
 * (Submitted on 20 Aug 2009 (v1), last revised 4 Mar 2012 (this version, v2))
 * 
 * Cudos to him, its very extensive and usable.
 * 
 * ----
 * 
 * This code is a compression of the library found at http://arxiv.org/abs/0908.3030.
 * This implementation only contains the functions actually needed from the libraries.
 * 
 * This code was compressed in 2013-11-08
 * 
 * Modified by: Frederick Ceder
 */
public class BigMath {
    
        /** A suggestion for the maximum numter of terms in the Taylor expansion of the exponential.
        */
        static private int TAYLOR_NTERM = 8 ;
    
        /** The base of the natural logarithm in a predefined accuracy.
        * http://www.cs.arizona.edu/icon/oddsends/e.htm
        * The precision of the predefined constant is one less than
        * the string's length, taking into account the decimal dot.
        * static int E_PRECISION = E.length()-1 ;
        */
        static BigDecimal E = new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663035354"+
"759457138217852516642742746639193200305992181741359662904357290033429526059563"+
"073813232862794349076323382988075319525101901157383418793070215408914993488416"+
"750924476146066808226480016847741185374234544243710753907774499206955170276183"+
"860626133138458300075204493382656029760673711320070932870912744374704723069697"+
"720931014169283681902551510865746377211125238978442505695369677078544996996794"+
"686445490598793163688923009879312773617821542499922957635148220826989519366803"+
"318252886939849646510582093923982948879332036250944311730123819706841614039701"+
"983767932068328237646480429531180232878250981945581530175671736133206981125099"+
"618188159304169035159888851934580727386673858942287922849989208680582574927961"+
"048419844436346324496848756023362482704197862320900216099023530436994184914631"+
"409343173814364054625315209618369088870701676839642437814059271456354906130310"+
"720851038375051011574770417189861068739696552126715468895703503540212340784981"+
"933432106817012100562788023519303322474501585390473041995777709350366041699732"+
"972508868769664035557071622684471625607988265178713419512466520103059212366771"+
"943252786753985589448969709640975459185695638023637016211204774272283648961342"+
"251644507818244235294863637214174023889344124796357437026375529444833799801612"+
"549227850925778256209262264832627793338656648162772516401910590049164499828931") ;
    
    
        
        /** The square root.
        * @param x the non-negative argument.
        * @param mc
        * @return the square root of the BigDecimal.
        * @since 2008-10-27
        */
        static public BigDecimal sqrt(final BigDecimal x, final MathContext mc)
        {
                if ( x.compareTo(BigDecimal.ZERO) < 0 )
                        throw new ArithmeticException("negative argument "+x.toString()+ " of square root") ;
                if ( x.abs().subtract( new BigDecimal(Math.pow(10.,-mc.getPrecision())) ).compareTo(BigDecimal.ZERO) < 0 )
                        return scalePrec(BigDecimal.ZERO,mc) ;
                /* start the computation from a double precision estimate */
                BigDecimal s = new BigDecimal( Math.sqrt(x.doubleValue()) ,mc) ;
                final BigDecimal half = new BigDecimal("2") ;

                /* increase the local accuracy by 2 digits */
                MathContext locmc = new MathContext(mc.getPrecision()+2,mc.getRoundingMode()) ;

                /* relative accuracy requested is 10^(-precision) 
                */
                final double eps = Math.pow(10.0,-mc.getPrecision()) ;
                for (;;)
                {
                        /* s = s -(s/2-x/2s); test correction s-x/s for being
                        * smaller than the precision requested. The relative correction is 1-x/s^2,
                        * (actually half of this, which we use for a little bit of additional protection).
                        */
                        if ( Math.abs(BigDecimal.ONE.subtract(x.divide(s.pow(2,locmc),locmc)).doubleValue()) < eps)
                                break ;
                        s = s.add(x.divide(s,locmc)).divide(half,locmc) ;
                        /* debugging
                        * System.out.println("itr "+x.round(locmc).toString() + " " + s.round(locmc).toString()) ;
                        */
                }
                return s ;
        } /* BigDecimalMath.sqrt */

        /** The square root.
        * @param x the non-negative argument.
        * @return the square root of the BigDecimal rounded to the precision implied by x.
        * @since 2009-06-25
        */
        static public BigDecimal sqrt(final BigDecimal x)
        {
                if ( x.compareTo(BigDecimal.ZERO) < 0 )
                        throw new ArithmeticException("negative argument "+x.toString()+ " of square root") ;

                return root(2,x) ;
        } /* BigDecimalMath.sqrt */

        /** The cube root.
        * @param x The argument.
        * @return The cubic root of the BigDecimal rounded to the precision implied by x.
        * The sign of the result is the sign of the argument.
        * @since 2009-08-16
        */
        static public BigDecimal cbrt(final BigDecimal x)
        {
                if ( x.compareTo(BigDecimal.ZERO) < 0 )
                        return root(3,x.negate()).negate() ;
                else
                        return root(3,x) ;
        } /* BigDecimalMath.cbrt */

        /** The integer root.
        * @param n the positive argument.
        * @param x the non-negative argument.
        * @return The n-th root of the BigDecimal rounded to the precision implied by x, x^(1/n).
        * @since 2009-07-30
        */
        static public BigDecimal root(final int n, final BigDecimal x)
        {
                if ( x.compareTo(BigDecimal.ZERO) < 0 )
                        throw new ArithmeticException("negative argument "+x.toString()+ " of root") ;
                if ( n<= 0 )
                        throw new ArithmeticException("negative power "+ n + " of root") ;

                if ( n == 1 )
                        return x ;

                /* start the computation from a double precision estimate */
                BigDecimal s = new BigDecimal( Math.pow(x.doubleValue(),1.0/n) ) ;

                /* this creates nth with nominal precision of 1 digit
                */
                final BigDecimal nth = new BigDecimal(n) ;

                /* Specify an internal accuracy within the loop which is
                * slightly larger than what is demanded by 'eps' below.
                */
                final BigDecimal xhighpr = scalePrec(x,2) ;
                MathContext mc = new MathContext( 2+x.precision() ) ;

                /* Relative accuracy of the result is eps.
                */
                final double eps = x.ulp().doubleValue()/(2*n*x.doubleValue()) ;
                for (;;)
                {
                        /* s = s -(s/n-x/n/s^(n-1)) = s-(s-x/s^(n-1))/n; test correction s/n-x/s for being
                        * smaller than the precision requested. The relative correction is (1-x/s^n)/n,
                        */
                        BigDecimal c = xhighpr.divide( s.pow(n-1),mc ) ;
                        c = s.subtract(c) ;
                        MathContext locmc = new MathContext( c.precision() ) ;
                        c = c.divide(nth,locmc) ;
                        s = s. subtract(c) ;
                        if ( Math.abs( c.doubleValue()/s.doubleValue()) < eps)
                                break ;
                }
                return s.round(new MathContext( err2prec(eps)) ) ;
        } /* BigDecimalMath.root */
    
        /** The base of the natural logarithm.
        * @param mc the required precision of the result
        * @return exp(1) = 2.71828....
        * @since 2009-05-29
        */
        static public BigDecimal exp(final MathContext mc)
        {
                /* look it up if possible */
                if ( mc.getPrecision() < E.precision() )
                        return E.round(mc) ;
                else
                {
                        /* Instantiate a 1.0 with the requested pseudo-accuracy
                        * and delegate the computation to the public method above.
                        */
                        BigDecimal uni = scalePrec(BigDecimal.ONE, mc.getPrecision() ) ;
                        return exp(uni) ;
                }
        } /* BigDecimalMath.exp */

        /** The exponential function.
        * @param x the argument.
        * @return exp(x).
        * The precision of the result is implicitly defined by the precision in the argument.
        * In particular this means that "Invalid Operation" errors are thrown if catastrophic
        * cancellation of digits causes the result to have no valid digits left.
        * @since 2009-05-29
        * @author Richard J. Mathar
        */
        static public BigDecimal exp(BigDecimal x)
        {
                /* To calculate the value if x is negative, use exp(-x) = 1/exp(x)
                */
                if ( x.compareTo(BigDecimal.ZERO) < 0 )
                {
                        final BigDecimal invx = exp(x.negate() ) ;
                        /* Relative error in inverse of invx is the same as the relative errror in invx.
                        * This is used to define the precision of the result.
                        */
                        MathContext mc = new MathContext( invx.precision() ) ;
                        return BigDecimal.ONE.divide( invx, mc ) ;
                }
                else if ( x.compareTo(BigDecimal.ZERO) == 0 )
                {
                        /* recover the valid number of digits from x.ulp(), if x hits the
                        * zero. The x.precision() is 1 then, and does not provide this information.
                        */
                        return scalePrec(BigDecimal.ONE, -(int)(Math.log10( x.ulp().doubleValue() )) ) ;
                }
                else
                {
                        /* Push the number in the Taylor expansion down to a small
                        * value where TAYLOR_NTERM terms will do. If x<1, the n-th term is of the order
                        * x^n/n!, and equal to both the absolute and relative error of the result
                        * since the result is close to 1. The x.ulp() sets the relative and absolute error
                        * of the result, as estimated from the first Taylor term.
                        * We want x^TAYLOR_NTERM/TAYLOR_NTERM! < x.ulp, which is guaranteed if
                        * x^TAYLOR_NTERM < TAYLOR_NTERM*(TAYLOR_NTERM-1)*...*x.ulp.
                        */
                        final double xDbl = x.doubleValue() ;
                        final double xUlpDbl = x.ulp().doubleValue() ;
                        if ( Math.pow(xDbl,TAYLOR_NTERM) < TAYLOR_NTERM*(TAYLOR_NTERM-1.0)*(TAYLOR_NTERM-2.0)*xUlpDbl ) 
                        {
                                /* Add TAYLOR_NTERM terms of the Taylor expansion (Euler's sum formula)
                                */
                                BigDecimal resul = BigDecimal.ONE ;

                                /* x^i */
                                BigDecimal xpowi = BigDecimal.ONE ;

                                /* i factorial */
                                BigInteger ifac = BigInteger.ONE ;

                                /* TAYLOR_NTERM terms to be added means we move x.ulp() to the right
                                * for each power of 10 in TAYLOR_NTERM, so the addition won't add noise beyond
                                * what's already in x.
                                */
                                MathContext mcTay = new MathContext( err2prec(1.,xUlpDbl/TAYLOR_NTERM) ) ;
                                for(int i=1 ; i <= TAYLOR_NTERM ; i++)
                                {
                                        ifac = ifac.multiply(new BigInteger(""+i) ) ;
                                        xpowi = xpowi.multiply(x) ;
                                        final BigDecimal c= xpowi.divide(new BigDecimal(ifac),mcTay)  ;
                                        resul = resul.add(c) ;
                                        if ( Math.abs(xpowi.doubleValue()) < i && Math.abs(c.doubleValue()) < 0.5* xUlpDbl )
                                                break;
                                }
                                /* exp(x+deltax) = exp(x)(1+deltax) if deltax is <<1. So the relative error
                                * in the result equals the absolute error in the argument.
                                */
                                MathContext mc = new MathContext( err2prec(xUlpDbl/2.) ) ;
                                return resul.round(mc) ;
                        }
                        else
                        {
                                /* Compute exp(x) = (exp(0.1*x))^10. Division by 10 does not lead
                                * to loss of accuracy.
                                */
                                int exSc = (int) ( 1.0-Math.log10( TAYLOR_NTERM*(TAYLOR_NTERM-1.0)*(TAYLOR_NTERM-2.0)*xUlpDbl
                                                        /Math.pow(xDbl,TAYLOR_NTERM) ) / ( TAYLOR_NTERM-1.0) ) ; 
                                BigDecimal xby10 = x.scaleByPowerOfTen(-exSc) ;
                                BigDecimal expxby10 = exp(xby10) ;

                                /* Final powering by 10 means that the relative error of the result
                                * is 10 times the relative error of the base (First order binomial expansion).
                                * This looses one digit.
                                */
                                MathContext mc = new MathContext( expxby10.precision()-exSc ) ;
                                /* Rescaling the powers of 10 is done in chunks of a maximum of 8 to avoid an invalid operation
                                * response by the BigDecimal.pow library or integer overflow.
                                */
                                while ( exSc > 0 )
                                {
                                        int exsub = Math.min(8,exSc) ;
                                        exSc -= exsub ;
                                        MathContext mctmp = new MathContext( expxby10.precision()-exsub+2 ) ;
                                        int pex = 1 ;
                                        while ( exsub-- > 0 )
                                                pex *= 10 ;
                                        expxby10 = expxby10.pow(pex,mctmp) ;
                                }
                                return expxby10.round(mc) ;
                        }
                }
        } /* BigDecimalMath.exp */
        
        /** The natural logarithm.
        * @param x the argument.
        * @return ln(x).
        * The precision of the result is implicitly defined by the precision in the argument.
        * @since 2009-05-29
        * @author Richard J. Mathar
        */
        static public BigDecimal log(BigDecimal x)
        {
                /* the value is undefined if x is negative.
                */
                if ( x.compareTo(BigDecimal.ZERO) < 0 )
                        throw new ArithmeticException("Cannot take log of negative "+ x.toString() ) ;
                else if ( x.compareTo(BigDecimal.ONE) == 0 )
                {
                        /* log 1. = 0. */
                        return scalePrec(BigDecimal.ZERO, x.precision()-1) ;
                }
                else if ( Math.abs(x.doubleValue()-1.0) <= 0.3 )
                {
                        /* The standard Taylor series around x=1, z=0, z=x-1. Abramowitz-Stegun 4.124.
                        * The absolute error is err(z)/(1+z) = err(x)/x.
                        */
                        BigDecimal z = scalePrec(x.subtract(BigDecimal.ONE),2) ;
                        BigDecimal zpown = z ;
                        double eps = 0.5*x.ulp().doubleValue()/Math.abs(x.doubleValue()) ;
                        BigDecimal resul = z ;
                        for(int k= 2;; k++)
                        {
                                zpown = multiplyRound(zpown,z) ;
                                BigDecimal c = divideRound(zpown,k) ;
                                if ( k % 2 == 0)
                                        resul = resul.subtract(c) ;
                                else
                                        resul = resul.add(c) ;
                                if ( Math.abs(c.doubleValue()) < eps)
                                        break;
                        }
                        MathContext mc = new MathContext( err2prec(resul.doubleValue(),eps) ) ;
                        return resul.round(mc) ;
                }
                else
                {
                        final double xDbl = x.doubleValue() ;
                        final double xUlpDbl = x.ulp().doubleValue() ;

                        /* Map log(x) = log root[r](x)^r = r*log( root[r](x)) with the aim
                        * to move roor[r](x) near to 1.2 (that is, below the 0.3 appearing above), where log(1.2) is roughly 0.2.
                        */
                        int r = (int) (Math.log(xDbl)/0.2) ;

                        /* Since the actual requirement is a function of the value 0.3 appearing above,
                        * we avoid the hypothetical case of endless recurrence by ensuring that r >= 2.
                        */
                        r = Math.max(2,r) ;

                        /* Compute r-th root with 2 additional digits of precision
                        */
                        BigDecimal xhighpr = scalePrec(x,2) ;
                        BigDecimal resul = root(r,xhighpr) ;
                        resul = log(resul).multiply(new BigDecimal(r)) ;

                        /* error propagation: log(x+errx) = log(x)+errx/x, so the absolute error
                        * in the result equals the relative error in the input, xUlpDbl/xDbl .
                        */
                        MathContext mc = new MathContext( err2prec(resul.doubleValue(),xUlpDbl/xDbl) ) ;
                        return resul.round(mc) ;
                }
        } /* BigDecimalMath.log */
        
        /** Divide and round.
        * @param x The numerator
        * @param n The denominator
        * @return the divided x/n
        * @since 2009-07-30
        */
        static public BigDecimal divideRound(final BigDecimal x, final int n)
        {
                /* The estimation of the relative error in the result is |err(x)/x| 
                */
                MathContext mc = new MathContext( x.precision() ) ;
                return x.divide(new BigDecimal(n),mc) ;
        }
        
        /** Divide and round.
        * @param x The numerator
        * @param n The denominator
        * @return the divided x/n
        * @since 2009-07-30
        */
        static public BigDecimal divideRound(final BigDecimal x, final BigInteger n)
        {
                /* The estimation of the relative error in the result is |err(x)/x| 
                */
                MathContext mc = new MathContext( x.precision() ) ;
                return x.divide(new BigDecimal(n),mc) ;
        } /* divideRound */

        /** Divide and round.
        * @param n The numerator
        * @param x The denominator
        * @return the divided n/x
        * @since 2009-08-05
        */
        static public BigDecimal divideRound(final BigInteger n, final BigDecimal x)
        {
                /* The estimation of the relative error in the result is |err(x)/x| 
                */
                MathContext mc = new MathContext( x.precision() ) ;
                return new BigDecimal(n).divide(x,mc) ;
        } /* divideRound */
        
        /** Divide and round.
        * @param x The numerator
        * @param y The denominator
        * @return the divided x/y
        * @since 2009-07-30
        */
        static public BigDecimal divideRound(final BigDecimal x, final BigDecimal y)
        {
                /* The estimation of the relative error in the result is |err(y)/y|+|err(x)/x| 
                */
                MathContext mc = new MathContext( Math.min(x.precision(),y.precision()) ) ;
                BigDecimal resul = x.divide(y,mc) ;
                /* If x and y are precise integer values that may have common factors,
                * the method above will truncate trailing zeros, which may result in
                * a smaller apparent accuracy than starte... add missing trailing zeros now.
                */
                return scalePrec(resul,mc) ;
        }
        
        /** Multiply and round.
        * @param x The left factor.
        * @param y The right factor.
        * @return The product x*y.
        * @since 2009-07-30
        */
        static public BigDecimal multiplyRound(final BigDecimal x, final BigDecimal y)
        {
                BigDecimal resul = x.multiply(y) ;
                /* The estimation of the relative error in the result is the sum of the relative
                * errors |err(y)/y|+|err(x)/x| 
                */
                MathContext mc = new MathContext( Math.min(x.precision(),y.precision()) ) ;
                return resul.round(mc) ;
        } /* multiplyRound */
        
        /** Multiply and round.
        * @param x The left factor.
        * @param n The right factor.
        * @return The product x*n.
        * @since 2009-07-30
        */
        static public BigDecimal multiplyRound(final BigDecimal x, final int n)
        {
                BigDecimal resul = x.multiply(new BigDecimal(n)) ;
                /* The estimation of the absolute error in the result is |n*err(x)|
                */
                MathContext mc = new MathContext( n != 0 ? x.precision(): 0 ) ;
                return resul.round(mc) ;
        }
        
        /** Multiply and round.
        * @param x The left factor.
        * @param n The right factor.
        * @return the product x*n
        * @since 2009-07-30
        */
        static public BigDecimal multiplyRound(final BigDecimal x, final BigInteger n)
        {
                BigDecimal resul = x.multiply(new BigDecimal(n)) ;
                /* The estimation of the absolute error in the result is |n*err(x)|
                */
                MathContext mc = new MathContext( n.compareTo(BigInteger.ZERO) != 0 ? x.precision(): 0 ) ;
                return resul.round(mc) ;
        }
        
        
        /** Append decimal zeros to the value. This returns a value which appears to have
        * a higher precision than the input.
        * @param x The input value
        * @param d The (positive) value of zeros to be added as least significant digits.
        * @return The same value as the input but with increased (pseudo) precision.
        */
        static public BigDecimal scalePrec(final BigDecimal x, int d)
        {
                return x.setScale(d+x.scale()) ;
        }
        
        /** Boost the precision by appending decimal zeros to the value. This returns a value which appears to have
        * a higher precision than the input.
        * @param x The input value
        * @param mc The requirement on the minimum precision on return.
        * @return The same value as the input but with increased (pseudo) precision.
        */
        static public BigDecimal scalePrec(final BigDecimal x, final MathContext mc)
        {
                final int diffPr = mc.getPrecision() - x.precision() ;
                if ( diffPr > 0 )
                        return scalePrec(x, diffPr) ;
                else
                        return x ;
        } /* BigDecimalMath.scalePrec */
        
        /** Convert an absolute error to a precision.
        * @param x The value of the variable
        * @param xerr The absolute error in the variable
        * @return The number of valid digits in x.
        *    The value is rounded down, and on the pessimistic side for that reason.
        * @since 2009-06-25
        */
        static public int err2prec(BigDecimal x, BigDecimal xerr)
        {
                return err2prec( xerr.divide(x,MathContext.DECIMAL64).doubleValue() );
        }
        
        /** Convert a relative error to a precision.
        * @param xerr The relative error in the variable.
        *    The value returned depends only on the absolute value, not on the sign.
        * @return The number of valid digits in x.
        *    The value is rounded down, and on the pessimistic side for that reason.
        * @since 2009-08-05
        */
        static public int err2prec(double xerr)
        {
                /* Example: an error of xerr=+-0.5 a precision of 1 (digit), an error of
                * +-0.05 a precision of 2 (digits)
                */
                return 1+(int)(Math.log10(Math.abs(0.5/xerr) ) );
        }

        /** Convert an absolute error to a precision.
        * @param x The value of the variable
        *    The value returned depends only on the absolute value, not on the sign.
        * @param xerr The absolute error in the variable
        *    The value returned depends only on the absolute value, not on the sign.
        * @return The number of valid digits in x.
        *    Derived from the representation x+- xerr, as if the error was represented
        *    in a "half width" (half of the error bar) form.
        *    The value is rounded down, and on the pessimistic side for that reason.
        * @since 2009-05-30
        */
        static public int err2prec(double x, double xerr)
        {
                /* Example: an error of xerr=+-0.5 at x=100 represents 100+-0.5 with
                * a precision = 3 (digits).
                */
                return 1+(int)(Math.log10(Math.abs(0.5*x/xerr) ) );
        }
        
        
        
        /****************************/
        /* END OF BIG DECIMAL MATH */
        /**************************/
    
}
