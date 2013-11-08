
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Frederick Ceder
 */
public class Matrix {
    
    private List<RowVector> Matrix;
    private int RowDimension;
    
    public Matrix(int rowDimension)
    {
        Matrix = new ArrayList<>();
    }
    
    public void addVector(BigInteger[] list)
    {
        Matrix.add(new RowVector(list));
    }
    
    public RowVector sumRows(int... i)
    {
        if(i.length < 2)
            return null;
        RowVector result = Matrix.get(i[0]);
        for(int index: i)
        {
            result = result.add(Matrix.get(index));
        }
        return result;
    }
    
    private class RowVector 
    {
        private BigInteger[] Row;
        
        private RowVector(int dimension)
        {
            Row = new BigInteger[dimension];
        }
        
        private RowVector(BigInteger[] list)
        {
            Row = list;
        }
        
        private int dimension()
        {
            return Row.length;
        }
        
        private RowVector add(RowVector v)
        {
            RowVector row = new RowVector(dimension());
            for(int i = 0; i < dimension(); i++)
            {
                row.Row[i] = Row[i].add(v.Row[i]);
            }
            return row;
        }
    }
            
}
