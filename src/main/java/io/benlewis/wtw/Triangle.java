package io.benlewis.wtw;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Triangle {

    private double[][] data;

    /**
     * Construct a Triangle with data.
     * @param data valid payment data
     */
    public Triangle(double[][] data){

        this.data = data;

    }

    /**
     * Accumulate the rows of this Triangle and return in a new Triangle.
     * @return accumulated Triangle
     */
    public Triangle accumulate(){

        Triangle accumulated = new Triangle(this.data);

        // Iterate rows
        for (int r = 0; r < data.length; r++){

            // Iterate columns
            for (int c = 1; c < data[r].length; c++){

                accumulated.data[r][c] += accumulated.data[r][c-1];

            }

        }

        return accumulated;

    }

    @Override
    public String toString(){

        StringBuilder sb = new StringBuilder();

        // Iterate rows
        for (int r = 0; r < data.length; r++){

            // Iterate columns
            for (int c = 0; c < data[r].length; c++){

                // Append value at location to output
                sb.append(cleanDouble(data[r][c]));
                if (!(r == data.length-1 && c == data[r].length-1)) sb.append(", ");

            }

        }

        return sb.toString();

    }

    /**
     * Remove trailing zeroes from a double.
     * @param d double to clean
     * @return double as a String
     */
    private static String cleanDouble(double d){

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);

        return df.format(d);

    }

    /**
     * Test harness.
     */
    public static void test(){

        double[][] data  = {{2.5,4,8},{1,3},{9}};
        Triangle tri = new Triangle(data);

        System.out.println(tri);

        Triangle acc = tri.accumulate();

        System.out.println(acc);

    }

}
