package eu.xenit.restrequests.api.support;

public interface Ordered {


    /**
     * Useful constant for the highest precedence value.
     * @see java.lang.Integer#MIN_VALUE
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * Useful constant for the lowest precedence value.
     * @see java.lang.Integer#MAX_VALUE
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    /**
     * The default priority.
     */
    int DEFAULT_PRIORITY = 0;

    /**
     * Returns an order used for comparison and sorting.
     *
     * @return An order used for comparison and sorting.
     */
    default int getOrder() {
        return DEFAULT_PRIORITY;
    }
}
