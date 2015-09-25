/**
 * 
 */
package org.opencb.biodata.models.variant.converter;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 *
 */
public interface Converter <FROM,TO> {

    TO convert(FROM from);



}
