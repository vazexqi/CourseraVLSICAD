package edu.illinois.vlsicad.test;

import edu.illinois.vlsicad.COOMatrix;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class COOMatrixTest {
    static final double DELTA = 0.01;
    COOMatrix testMatrix;
    COOMatrix hugeMatrix;

    @Before
    public void setUp() throws Exception {
        // This is what the actual 3x3 matrix looks like
        // 4.0 -1.0 0.0
        // -1.0 4.0 -1.0
        // 0.0 -1.0 4.0
        testMatrix = COOMatrix.readCOOMatrix("mat_test.txt");
        hugeMatrix = COOMatrix.readCOOMatrix("mat_helmholtz.txt");
    }

    @Test
    public void testReadTestCOOMatrix() throws Exception {
        assertEquals("n doesn't match", 3, testMatrix.getDim());
        assertEquals("nnz doesn't match", 7, testMatrix.getNnz());

        // Quick-n-dirty comparison
        String expected = "3 7\n" +
                "0 0 4.00\n" +
                "0 1 -1.00\n" +
                "1 0 -1.00\n" +
                "1 1 4.00\n" +
                "1 2 -1.00\n" +
                "2 1 -1.00\n" +
                "2 2 4.00\n";

        assertEquals("Contents don't match", expected, testMatrix.toString());
    }

    @Test
    public void testReadHugeCOOMatrix() throws Exception {
        assertEquals("n doesn't match", 400, hugeMatrix.getDim());
        assertEquals("nnz doesn't match", 1920, hugeMatrix.getNnz());

        // Check some values
        double[] data = hugeMatrix.getData();
        assertEquals(-1.0, data[100], DELTA);
        assertEquals(-1.0, data[200], DELTA);
        assertEquals(5.0, data[300], DELTA);
        assertEquals(-1.0, data[400], DELTA);
        assertEquals(-1.0, data[500], DELTA);
        assertEquals(-1.0, data[600], DELTA);
    }

    @Test
    public void testMultiplyWithVector() {
        double[] result = testMatrix.multiplyWithVector(new double[]{1, 2, 3});
        double[] expected = {2.0, 4.0, 10.0};
        assertTrue("Result of matrix vector multiplication not as expected", Arrays.equals(expected, result));
    }

    @Test
    public void testSolve1() {
        double[] b = new double[]{1.0, 2.0, 3.0};
        double[] x = new double[testMatrix.getDim()];
        testMatrix.solve(b, x);
        double[] expected = new double[]{0.4642857139733551, 0.8571428557253853, 0.9642857139733623};

        assertArrayEquals("Solution for x is not as expected", expected, x, DELTA);
    }

    @Test
    public void testSolve2() {
        double[] b = new double[]{-1.0, -2.0, -3.0};
        double[] x = new double[testMatrix.getDim()];
        testMatrix.solve(b, x);
        double[] expected = new double[]{-0.4642857139733551, -0.8571428557253853, -0.9642857139733623};

        assertArrayEquals("Solution for x is not as expected", expected, x, DELTA);
    }

    @Test
    public void testSolveHuge() {
        double[] b = new double[hugeMatrix.getDim()];
        double[] x = new double[hugeMatrix.getDim()];

        // Set two values of b and leave the rest as 0.0
        b[0] = 1.0;
        b[1] = 2.0;

        hugeMatrix.solve(b, x);

        // Comparing it to the values from the C++ version

        double[] expectedPartial = new double[]{
                0.323568,
                0.51812,
                0.122335,
                0.030585,
                0.00807145,
                0.00223629,
                0.000646228,
                0.000193474,
                5.9648e-05,
                1.88386e-05,
                6.0692e-06,
                1.98778e-06,
                6.60114e-07,
                2.21811e-07,
                7.5236e-08,
                2.57632e-08,
                8.89172e-09,
                3.15179e-09,
                1.05541e-09,
                2.92601e-10
        };

        for (int i = 0; i < expectedPartial.length; i++) {
            assertEquals(String.format("%d value doesn't match!", i), expectedPartial[i], x[i], DELTA);
        }
    }

    @Test
    public void testSubtract() {
        double[] vector1 = new double[]{4, 5, 6};
        double[] vector2 = new double[]{2, 4, 8};
        double[] result = COOMatrix.subtract(vector1, vector2);
        double[] expected = new double[]{2.0, 1.0, -2.0};

        assertArrayEquals("Result of vector subtraction not as expected", expected, result, DELTA);
    }

    @Test
    public void testMultiplyWith() {
        double[] vector = new double[]{3, 7, 11};
        double[] result = COOMatrix.multiplyWith(vector, 4.5);

        // Assert that we are not creating any new array
        assertEquals("Variables do not reference the same object", vector, result);

        assertArrayEquals("Multiplied value is not as expected", result, new double[]{3 * 4.5, 7 * 4.5,
                11 * 4.5}, DELTA);
    }

    @Test
    public void addTo() {
        double[] vector1 = new double[]{3, 7, 11};
        double[] vector2 = new double[]{11, 7, 3};
        double[] result = COOMatrix.addTo(vector1, vector2);

        // Assert that we are not creating any new array
        assertEquals("Variables do not reference the same object", result, vector1);

        assertArrayEquals("Added value is not as expected", result, new double[]{14, 14, 14}, DELTA);
    }

    @Test
    public void testSubtractFrom() {
        double[] vector1 = new double[]{3, 7, 11};
        double[] vector2 = new double[]{11, 7, 3};
        double[] result = COOMatrix.subtractFrom(vector1, vector2);

        // Assert that we are not creating any new array
        assertEquals("Variables do not reference the same object", result, vector1);

        assertArrayEquals("Added value is not as expected", result, new double[]{-8, 0, 8}, DELTA);
    }

    @Test
    public void testDotProduct() {
        double[] vector1 = new double[]{3, 2, 3};
        double[] vector2 = new double[]{-1, 4, 8};
        double result = COOMatrix.dot(vector1, vector2);
        double expected = 29;

        assertEquals("Result of dot product not as expected", expected, result, DELTA);
    }

}
