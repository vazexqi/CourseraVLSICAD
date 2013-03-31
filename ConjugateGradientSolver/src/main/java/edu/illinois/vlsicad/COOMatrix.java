package edu.illinois.vlsicad;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

/**
 * <p>
 * COOrdinate Sparse Matrix that solves a linear equation of the form Ax=b.
 * Refer to <a href="http://en.wikipedia.org/wiki/Sparse_matrix#Coordinate_list_.28COO.29">wikipedia</a> for more
 * information about COO format. And refer to <a href="https://en.wikipedia.org/wiki/Conjugate_gradient#Example_code_in_Matlab">Conjugate Gradient Solver</a>
 * for more information about the solver.
 * </p>
 * <p>
 * <b>REMEMBER:</b> This solver assumes that your matrix is symmetric and positive-definite!
 * </p>
 * <p>
 * This is a quick-n-dirty version of the C++ code that we are distributing. As you can see,
 * Java doesn't have built-in support
 * for a lot of numerical functions so we have to write them from scratch.
 * </p>
 * <p>
 * If you are looking into serious numerical applications with Java, consider <a href="http://math.nist
 * .gov/javanumerics/jama/">JAMA</a>.
 * </p>
 */
public class COOMatrix {
    static final int MAX_ITERATION = 1000;

    final int dim; // The dimensions of the square matrix
    final int nnz; // The rows of the sparse representation
    final int[] row;
    final int[] col;
    final double[] data;

    // All initialization should happen through the static method that reads from file
    // BUT you can change this as necessary to suit your code
    private COOMatrix(int dim, int nnz) {
        this.dim = dim;
        this.nnz = nnz;
        this.row = new int[nnz];
        this.col = new int[nnz];
        this.data = new double[nnz];
    }

    /**
     * Matrix format
     * n nnz
     * 1 j1 A[i1,j1]
     * 2 j2 A[i2,j2]
     * ...
     * ...
     *
     * @param fileName The name of the file that contains the matrix in COO format
     * @return Newly initialized matrix with data from file
     */
    public static COOMatrix readCOOMatrix(String fileName) throws FileNotFoundException {

        InputStream iStream = new FileInputStream(fileName);
        Scanner scanner = new Scanner(iStream);

        int n = scanner.nextInt();
        int nnz = scanner.nextInt();
        COOMatrix matrix = new COOMatrix(n, nnz);

        for (int index = 0; index < nnz; index++) {
            matrix.row[index] = scanner.nextInt();
            matrix.col[index] = scanner.nextInt();
            matrix.data[index] = scanner.nextDouble();
        }

        return matrix;
    }

    /**
     * Multiplies the data in this matrix with the data in vector
     *
     * You might want to change this to operate in place if the performance is not acceptable
     *
     * @param vector The vector to multiply - check that you have the right dimensions!
     * @return The result of the multiplication
     */
    public double[] multiplyWithVector(double[] vector) {
        double[] result = new double[vector.length];

        for (int index = 0; index < nnz; index++) {
            result[row[index]] += data[index] * vector[col[index]];
        }

        return result;
    }

    /**
     * Solves x = A^{-1} b with conjugate gradient method
     *
     * @param b the b vector in a typical linear equation
     * @param x the x vector in a typical linear equation
     */
    public void solve(double[] b, double[] x) {
        double[] Ax;
        double[] Ap;
        double[] r;
        double[] p;

        double alpha, rnorm, rnormOld;
        double error, errorOld = 1.0;

        populateVectorWithRandomNumbers(x);
        Ax = multiplyWithVector(x);
        r = COOMatrix.subtract(b, Ax);
        p = copyOf(r);
        rnormOld = COOMatrix.dot(r, r);

        int iteration;
        for (iteration = 0; iteration < MAX_ITERATION; iteration++) {
            Ap = multiplyWithVector(p);
            alpha = rnormOld / COOMatrix.dot(p, Ap);

            COOMatrix.multiplyWith(p, alpha);
            COOMatrix.addTo(x, p);

            COOMatrix.multiplyWith(Ap, alpha);
            COOMatrix.subtractFrom(r, Ap);

            rnorm = COOMatrix.dot(r, r);

            if (Math.sqrt(rnorm) < 1e-8) {
                break;
            } else {
                error = Math.abs(COOMatrix.dot(r, x));
                errorOld = error;
            }

            COOMatrix.multiplyWith(p, rnorm / rnormOld);
            COOMatrix.addTo(p, r);

            rnormOld = rnorm;
        }

        if (iteration == MAX_ITERATION) {
            System.err.println("Warning: Reached maximum iterations!");
        }

    }

    private double[] copyOf(double[] vector) {
        double[] copy = new double[vector.length];
        System.arraycopy(vector, 0, copy, 0, vector.length);
        return copy;
    }

    /*
    Populates the parameter with random numbers in the range (0,1)
     */
    private void populateVectorWithRandomNumbers(double[] x) {
        Random generator = new Random();
        for (int row = 0; row < x.length; row++) {
            x[row] = generator.nextDouble();
        }
    }

    // Some utility methods with arrays that are handwritten since we don't have the valarray STL class
    // Be aware that some methods return new arrays while some methods operate in place
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
        Returns vector1 - vector2
     */
    public static double[] subtract(double[] vector1, double[] vector2) {
        assert vector1.length == vector2.length;

        double[] result = new double[vector1.length];

        for (int index = 0; index < vector1.length; index++) {
            result[index] = vector1[index] - vector2[index];
        }

        return result;
    }

    /*
        Returns vector *= scalar
     */
    public static double[] multiplyWith(double[] vector, double scalar) {

        for (int index = 0; index < vector.length; index++) {
            vector[index] *= scalar;
        }

        return vector;
    }

    /*
        Returns vector1 += vector2
     */
    public static double[] addTo(double[] vector1, double[] vector2) {
        assert vector1.length == vector2.length;

        for (int index = 0; index < vector1.length; index++) {
            vector1[index] += vector2[index];
        }

        return vector1;
    }

    /*
       Return vector1 -= vector2
     */
    public static double[] subtractFrom(double[] vector1, double[] vector2) {
        assert vector1.length == vector2.length;

        for (int index = 0; index < vector1.length; index++) {
            vector1[index] -= vector2[index];
        }

        return vector1;
    }


    /*
        Returns dot product of vector1 and vector2
     */
    public static double dot(double[] vector1, double[] vector2) {
        assert vector1.length == vector2.length;

        double result = 0.0;

        for (int index = 0; index < vector1.length; index++) {
            result += vector1[index] * vector2[index];
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%d %d%n", dim, nnz));

        for (int index = 0; index < nnz; index++) {
            sb.append(String.format("%d %d %.2f%n", row[index], col[index], data[index]));
        }

        return sb.toString();
    }

    // These getters are mostly for testing purposes so that we can assert certain properties
    // You can use them to access some values of the matrix
    public int[] getCol() {
        return col;
    }

    public double[] getData() {
        return data;
    }

    public int getDim() {
        return dim;
    }

    public int getNnz() {
        return nnz;
    }

    public int[] getRow() {
        return row;
    }
}
