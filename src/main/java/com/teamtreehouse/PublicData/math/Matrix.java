package com.teamtreehouse.PublicData.math;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matrix {

    public static double[] getNthDegLeastSquares(int degree, double[] a, double[] b) {
        // Check the degree
        if (1 > degree) {
            System.out.println("The degree must be at least 1");
            return null;
        }

        // Check the sizes of the arrays
        if (a.length != b.length) {
            System.out.println("Cannot perform least squares with arrays of unequal length");
            return null;
        }

        // Form the A matrix
        double[][] A = new double[a.length][degree+1];
        for (int ii=0; ii<a.length; ii++) {
            for (int jj=0; jj<=degree; jj++) {
                A[ii][jj] = Math.pow(a[ii],jj);
            }
        }

        // Do the math: output = ((A'A)^-1)A'b where ' denotes the Transpose function
        double[][] At = transpose(A);
        double[][] AtA = matrixMultMxN(At,A);
        double[][] AtAinv = inverse(AtA);
        double[][] AtAinvAt = matrixMultMxN(AtAinv,At);
        double[] solution = matrixMultMx1(AtAinvAt,b);

        return solution;
    }

    public static double[][] matrixMultMxN(double[][] a, double[][] b) {
        // Get the dimensions of the inputs
        int[] dimsA = getMatrixDimensions(a);
        int[] dimsB = getMatrixDimensions(b);

        /* We can do MxN*NxL matrix multiplication by doing Mx1 matrix multiplication
           over each column of b.  However, selecting b[0] will select the first row.
           We can get around this by first taking the transpose of b and then
           selecting the first row of the transposed bT matrix. */
        double[][] bT = transpose(b);

        // Do the math
        double[][] outMatrix = new double[dimsA[0]][dimsB[1]];
        for (int jj=0; jj<dimsB[1]; jj++) {
            double[] thisCol = matrixMultMx1(a,bT[jj]);
            for (int ii=0; ii<dimsA[0]; ii++) {
                outMatrix[ii][jj] = thisCol[ii];
            }
        }
        return outMatrix;
    }

    public static double[] matrixMultMx1(double[][] a, double[] b) {
        // Get the dimensions of the inputs
        int[] dimsA = getMatrixDimensions(a);
        int dimB = b.length;

        // Check for matrix dimension agreement
        if (dimsA[1] != dimB) {
            System.out.println("Improper matrix dimension agreement.  Matrices should be of size MxN and Nx1");
            return null;
        }

        // Do the math
        double[] outArray = new double[dimsA[0]];
        for (int ii=0; ii<dimsA[0]; ii++) {
            // Initialize the output value for this element
            outArray[ii] = 0.0;
            for (int jj=0; jj<dimB; jj++) {
                outArray[ii] += a[ii][jj] * b[jj];
            }

        }
        return outArray;
    }

    public static Map<String, double[][]> getLUDecomposition(double[][] A) {
        // Get the dimensions of the matrix
        int[] dims = getMatrixDimensions(A);

        // Check the dimensions for squareness
        if (dims[0] != dims[1]){
            System.out.println("Input matrix must be square and non-singular");
            return null;
        }
        int n = dims[0];

        // Initialize the output map
        Map<String, double[][]> outMap = new HashMap<>();

        // Initialize the output matrices
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];
        for (int ii=0; ii<n; ii++) {
            for (int jj=0; jj<n; jj++) {
                L[ii][jj] = 0.0;
                U[ii][jj] = 0.0;
            }
            U[ii][ii] = 1.0;
        }

        // Perform the decomposition
        L[0][0] = A[0][0];

        for (int jj=1; jj<n; jj++) {
            L[jj][0] = A[jj][0];
            U[0][jj] = A[0][jj] / L[0][0];
        }

        for (int jj=1; jj<n-1; jj++) {
            for (int ii=jj; ii<n; ii++) {
                L[ii][jj] = A[ii][jj];
                for(int kk=0; kk<jj; kk++) {
                    L[ii][jj] -= L[ii][kk] * U[kk][jj];
                }
            }
            for (int kk=jj+1; kk<n; kk++) {
                U[jj][kk] = A[jj][kk];
                for (int ii=0; ii<jj; ii++) {
                    U[jj][kk] -= L[jj][ii] * U[ii][kk];
                }
                U[jj][kk] /= L[jj][jj];
            }
        }

        L[n-1][n-1] = A[n-1][n-1];

        for (int kk=0; kk<n-1; kk++) {
            L[n-1][n-1] -= L[n-1][kk] * U[kk][n-1];
        }

        // Set the matrices to the map
        outMap.put("L",L);
        outMap.put("U",U);

        return outMap;
    }

    public static double[] linearSolve(double[][] A, double[] b) {
        // Check the dimensions of the inputs
        int[] dimsA = getMatrixDimensions(A);
        int dimB = b.length;
        if (dimsA[0] != dimB) {
            System.out.println("Improper matrix sizes.  A must be MxN and b must be Mx1");
            return null;
        }

        // Initialize the output array
        double[] x = new double[dimB];
        double[] d = new double[dimB];

        // Perform the LU Decomposition of A
        Map<String, double[][]> luDecompMap = getLUDecomposition(A);
        double[][] L = luDecompMap.get("L");
        double[][] U = luDecompMap.get("U");

        // Calculate the solution to Ld=b
        d[0] = b[0]/L[0][0];

        for (int ii=1; ii<dimB; ii++) {
            double sum = 0.0;
            for (int jj=0; jj<ii; jj++) {
                sum += L[ii][jj] * d[jj];
            }
            d[ii] = (b[ii] - sum) / L[ii][ii];
        }

        // Calculate the solution to Ux=d
        x[dimB-1] = d[dimB-1];
        for (int ii=dimB-2; ii>=0; ii--) {
            double sum = 0.0;
            for (int jj=ii+1; jj<dimB; jj++) {
                sum += U[ii][jj] * x[jj];
            }
            x[ii] = d[ii] - sum;
        }

        return x;
    }

    public static double[][] inverse(double[][] a) {
        // Get the matrix dimensions for a
        int[] dims = getMatrixDimensions(a);

        // Set up the Identy matrix
        double[][] I = new double[dims[0]][dims[0]];
        for (int ii=0; ii<dims[0]; ii++) {
            for (int jj=0; jj<dims[0]; jj++) {
                I[ii][jj] = 0.0;
            }
            I[ii][ii] = 1.0;
        }

        // Solve for the inverse
        double[][] inv = new double[dims[0]][dims[0]];
        for (int jj=0; jj<dims[0]; jj++) {
            double[] thisCol = linearSolve(a,I[jj]);
            for (int ii=0; ii<dims[0]; ii++) {
                inv[ii][jj] = thisCol[ii];
            }
        }

        return inv;
    }

    public static double[][] transpose(double[][] a) {
        int[] dims = getMatrixDimensions(a);
        double[][] aT = new double[dims[1]][dims[0]];
        for (int ii=0; ii<dims[0]; ii++) {
            for (int jj=0; jj<dims[1]; jj++) {
                aT[jj][ii] = a[ii][jj];
            }
        }
        return aT;
    }

    public static int[] getMatrixDimensions(double[][] a){
        int[] dims = new int[2];
        dims[0] = a.length;
        dims[1] = a[0].length;
        return dims;
    }
}
