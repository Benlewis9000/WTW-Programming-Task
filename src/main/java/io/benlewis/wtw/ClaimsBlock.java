package io.benlewis.wtw;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ClaimsBlock {

    private final String product;
    // Mapping of <originYear, <developmentYear, payment>>
    private Map<Integer, Map<Integer, Double>> payments;

    public ClaimsBlock(String product){

        this.product = product;
        this.payments = new HashMap<>();

    }

    /**
     * Add a payment to this claims block (overwrites an existing payment).
     * @param originYear of payment
     * @param developmentYear of payment
     * @param payment value
     */
    public void addPayment(int originYear, int developmentYear, double payment){

        if (!payments.containsKey(originYear))
            payments.put(originYear, new HashMap<>());

        payments.get(originYear).put(developmentYear, payment);

    }

    /**
     * Get a payment for a given origin year and development year, if it exists.
     * @param originYear of payment
     * @param developmentYear of payment
     * @return Optional of payment value, or empty if no payment found
     */
    public Optional<Double> getPayment(int originYear, int developmentYear){

        Optional<Double> payment = Optional.empty();

        if (payments.containsKey(originYear)) {

            if (payments.get(originYear).containsKey(developmentYear)){

                payment = Optional.of(payments.get(originYear).get(developmentYear));

            }

        }

        return  payment;

    }

    /**
     * Generate a payment Triangle based on the data in this claims block.
     * @param baseYear to begin triangle data from
     * @param span from first year of data to last
     * @return a Triangle of this blocks payment data for the given range.
     */
    public Triangle generateTriangle(int baseYear, int span){

        // Initialise data for Triangle
        double[][] data = new double[span][];

        // Iterate origin years
        for (int origin = baseYear; origin < baseYear + span; origin++){

            // Initialise columns for development years
            double[] cols = new double[span - (origin - baseYear)];

            // Iterate development years
            for (int dev = 0; dev < cols.length; dev++){

                // Get the payment for the given origin and development year
                Optional<Double> payment = getPayment(origin, origin + dev);
                // Insert payment into column, default to 0 if none found
                cols[dev] = (payment.isEmpty()) ? 0 : payment.get();

            }

            data[origin-baseYear] = cols;

        }

        return new Triangle(data);

    }

    @Override
    public String toString(){

        return this.product;

    }

    /**
     * Test harness.
     */
    public static void test(){

        ClaimsBlock cb = new ClaimsBlock("Comp");
        cb.addPayment(1992, 1992, 110);
        cb.addPayment(1992, 1993, 170);
        cb.addPayment(1993, 1993, 200);

        Triangle tri = cb.generateTriangle(1990, 4);
        System.out.println(tri);

        Triangle acc = tri.accumulate();
        System.out.println(acc);

    }

}
