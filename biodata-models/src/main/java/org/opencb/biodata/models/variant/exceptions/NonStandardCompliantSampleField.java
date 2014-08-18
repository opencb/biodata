package org.opencb.biodata.models.variant.exceptions;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class NonStandardCompliantSampleField extends Exception {

    /**
     * Constructs an instance of <code>NonStandardCompliantSampleField</code>
     * for a field, and with the specified detail message.
     *
     * @param field non-compliant field
     * @param value non-compliant value
     * @param msg the detail message.
     */
    public NonStandardCompliantSampleField(String field, String value, String msg) {
        super(String.format("Field %s=%s is non-compliant with the VCF specification: %s", field, value, msg));
    }
}
