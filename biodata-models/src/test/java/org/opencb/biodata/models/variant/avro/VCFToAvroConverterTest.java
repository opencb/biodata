package org.opencb.biodata.models.variant.avro;

import java.io.FileNotFoundException;





public class VCFToAvroConverterTest {
	
	public static void main(String[] args) throws Exception {
		
		VariantContextToVariantConverter variants = new VariantContextToVariantConverter();

		String vcffilepath = "C:\\Users\\Bawan Pal\\ProjectGEL\\VCFInput\\example.vcf.gz";
		//String tbifilepath = "C:\\Users\\Bawan Pal\\ProjectGEL\\VCFInput\\example.vcf.gz.tbi";		
		String outputAvroFilePath = "C:\\Users\\Bawan Pal\\ProjectGEL\\avro\\avdl\\vcf_out_1109.avro";

		variants.readVCFFile(vcffilepath, outputAvroFilePath);
	}

}
