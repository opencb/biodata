/**
 * 
 */
package org.opencb.biodata.tools.variant.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.avro.VariantFileMetadata;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfMeta;
import org.opencb.biodata.models.variant.protobuf.VcfSliceProtos.VcfMeta.Builder;

/**
 * @author Matthias Haimel mh719+git@cam.ac.uk
 *
 */
@Deprecated
public class VariantFileMetadataToVcfMeta implements Converter<VariantFileMetadata, VcfMeta> {

    @Override
    public VcfMeta convert(VariantFileMetadata from) {
        Builder builder = VcfMeta.newBuilder()
             // IDs
            .setStudyId(from.getStudyId().toString())
            .setFileId(from.getFileId().toString());

        // Samples
        builder.addAllSamples(convert(from.getSamples()));

        Map<String, String> map = getMap(from);
//      meta.put("FORMAT_DEFAULT", "GT:GQX:DP:DPF");
        builder.addAllFormatDefault(split(getString(map, "FORMAT_DEFAULT"),':'));
//      meta.put("INFO_DEFAULT", "END,BLOCKAVG_min30p3a");
        builder.addAllInfoDefault(split(getString(map, "INFO_DEFAULT"),','));
//      meta.put("FILTER_DEFAULT", "PASS");
        builder.setFilterDefault(getString(map, "FILTER_DEFAULT"));
//        meta.put("QUAL_DEFAULT", ".");
        builder.setQualityDefault(getString(map,"QUAL_DEFAULT"));
        return builder.build();
    }

    private Map<String, String> getMap(VariantFileMetadata from) {
        Map<String, String> smap = new HashMap<String, String>();
        Map<String, Object> map = from.getMetadata();
        if(null != map) {
            map.forEach((a, b) -> smap.put(a, b.toString()));
        }
        return smap;
    }

    private Iterable<String> convert(Iterable<String> c){
        List<String> lst = new ArrayList<>();
        for(CharSequence cs : c){
            lst.add(cs.toString());
        }
        return lst;
    }

    private Iterable<String> split(String string,char sep){
        return Arrays.asList(StringUtils.split(string, sep));
    }

    private String getString(Map<String,String> map,String key){
        if(map.containsKey(key)){
            return map.get(key);
        }
        return StringUtils.EMPTY;
    }

}
