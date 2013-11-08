//package org.nevec.rjm ;

import java.lang.* ;
import java.security.* ;
import java.util.* ;
import java.math.* ;


/** Complex numbers with BigDecimal real and imaginary components
* @since 2008-10-26
* @author Richard J. Mathar
*/
public class _BigComplex
{
        /** real part
        */
        BigDecimal re ;

        /** imaginary part
        */
        BigDecimal im ;

        /** The constant that equals zero
        */
        final static _BigComplex ZERO = new _BigComplex(BigDecimal.ZERO, BigDecimal.ZERO) ;

        /** Default ctor equivalent to zero.
        */
        public _BigComplex()
        {
                re= BigDecimal.ZERO ;
                im= BigDecimal.ZERO ;
        }

        /** ctor with real and imaginary parts
        * @param x real part
        * @param y imaginary part
        */
        public _BigComplex( BigDecimal x, BigDecimal y)
        {
                re=x ;
                im=y ;
        }

        /** ctor with real part.
        * @param x real part.
        * The imaginary part is set to zero.
        */
        public _BigComplex( BigDecimal x )
        {
                re=x ;
                im= BigDecimal.ZERO ;
        }

        /** ctor with real and imaginary parts
        * @param x real part
        * @param y imaginary part
        */
        public _BigComplex( double x, double y)
        {
                re= new BigDecimal(x) ;
                im= new BigDecimal(y) ;
        }

        /** Multiply with another BigComplex
        * @param oth The BigComplex which is a factor in the product
        * @param mc Defining precision and rounding mode
        * @return This multiplied by oth
        * @since 2010-07-19 implemented with 3 multiplications and 5 additions/subtractions
        */
        _BigComplex multiply(final _BigComplex oth, MathContext mc)
        {
                final BigDecimal a = re.add(im).multiply(oth.re) ;
                final BigDecimal b = oth.re.add(oth.im).multiply(im) ;
                final BigDecimal c = oth.im.subtract(oth.re).multiply(re) ;
                final BigDecimal x = a.subtract(b,mc) ;
                final BigDecimal y = a.add(c,mc) ;
                return new _BigComplex(x,y) ;
        }

        /** Add a BigDecimal
        * @param oth the value to be added to the real part.
        * @return this added to oth
        */
        _BigComplex add(final BigDecimal oth)
        {
                final BigDecimal x = re.add(oth) ;
                return new _BigComplex(x,im) ;
        }

        /** Subtract another BigComplex
        * @param oth the value to be subtracted from this.
        * @return this minus oth
        */
        _BigComplex subtract(final _BigComplex oth)
        {
                final BigDecimal x = re.subtract(oth.re) ;
                final BigDecimal y = im.subtract(oth.im) ;
                return new _BigComplex(x,y) ;
        }

        /** Complex-conjugation
        * @return the complex conjugate of this.
        */
        _BigComplex conj()
        {
                return new _BigComplex(re,im.negate()) ;
        }

        /** The absolute value squared.
        * @return The sum of the squares of real and imaginary parts.
        * This is the square of BigComplex.abs() .
        */
        BigDecimal norm()
        {
                return re.multiply(re).add(im.multiply(im)) ;
        }

        /** The absolute value.
        * @return the square root of the sum of the squares of real and imaginary parts.
        * @since 2008-10-27
        */
        BigDecimal abs(MathContext mc)
        {
                return _BigDecimalMath.sqrt(norm(),mc) ;
        }

        /** The square root.
        * @return the square root of the this.
        *   The branch is chosen such that the imaginary part of the result has the
        *   same sign as the imaginary part of this.
        * @see Tim Ahrendt, <a href="http://dx.doi.org/10.1145/236869.236924">Fast High-precision computation of complex square roots</a>,
        *    ISSAC 1996 p142-149.
        * @since 2008-10-27  
        */
        _BigComplex sqrt(MathContext mc)
        {
                final BigDecimal half = new BigDecimal("2") ;
                /* compute l=sqrt(re^2+im^2), then u=sqrt((l+re)/2)
                * and v= +- sqrt((l-re)/2 as the new real and imaginary parts.
                */
                final BigDecimal l = abs(mc) ;
                if ( l.compareTo(BigDecimal.ZERO) == 0 )
                        return new _BigComplex( _BigDecimalMath.scalePrec(BigDecimal.ZERO,mc),
                                                _BigDecimalMath.scalePrec(BigDecimal.ZERO,mc) ) ;
                final BigDecimal u = _BigDecimalMath.sqrt( l.add(re).divide(half,mc), mc );
                final BigDecimal v = _BigDecimalMath.sqrt( l.subtract(re).divide(half,mc), mc );
                if ( im.compareTo(BigDecimal.ZERO)>= 0 )
                        return new _BigComplex(u,v) ;
                else
                        return new _BigComplex(u,v.negate()) ;
        }

        /** The inverse of this.
        * @return 1/this
        */
        _BigComplex inverse(MathContext mc)
        {
                final BigDecimal hyp = norm() ;
                /* 1/(x+iy)= (x-iy)/(x^2+y^2 */
                return new _BigComplex( re.divide(hyp,mc), im.divide(hyp,mc).negate() ) ;
        }

        /** Divide through another BigComplex number.
        * @return this/oth
        */
        _BigComplex divide(_BigComplex oth, MathContext mc)
        {
                /* lazy implementation: (x+iy)/(a+ib)= (x+iy)* 1/(a+ib) */
                return multiply(oth.inverse(mc),mc) ;
        }

        /** Human-readable Fortran-type display
        * @return real and imaginary part in parenthesis, divided by a comma.
        */
        public String toString()
        {
                return "("+re.toString()+","+im.toString()+")" ;
        }

        /** Human-readable Fortran-type display
        * @return real and imaginary part in parenthesis, divided by a comma.
        */
        public String toString(MathContext mc)
        {
                return "("+re.round(mc).toString()+","+im.round(mc).toString()+")" ;
        }


} /* BigComplex */
