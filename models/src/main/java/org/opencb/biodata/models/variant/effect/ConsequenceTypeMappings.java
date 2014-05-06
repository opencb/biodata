package org.opencb.biodata.models.variant.effect;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class ConsequenceTypeMappings {
    
    public static final Map<String, Integer> termToAccession = new HashMap<>();
    
    public static final Map<Integer, String> accessionToTerm = new HashMap<>();
    
    static {
        
        // Fill the term to accession map
        termToAccession.put("SNV", 1483);
        termToAccession.put("indel", 1000032);
	termToAccession.put("substitution", 1000002);
	termToAccession.put("tandem_repeat", 705);
	termToAccession.put("complex_structural_alteration", 1784);
	termToAccession.put("copy_number_gain", 17420);
	termToAccession.put("copy_number_loss", 1743);
	termToAccession.put("copy_number_variation", 1019);
	termToAccession.put("duplication", 1000035);
	termToAccession.put("interchromosomal_breakpoint", 1873);
	termToAccession.put("intrachromosomal_breakpoint", 1874);
	termToAccession.put("inversion", 1000036);
	termToAccession.put("mobile_element_insertion", 1837);
	termToAccession.put("novel_sequence_insertion", 1838);
	termToAccession.put("tandem_duplication", 1000173);
	termToAccession.put("translocation", 199);
	termToAccession.put("deletion", 159);
	termToAccession.put("insertion", 667);
	termToAccession.put("sequence_alteration", 1059);
	termToAccession.put("probe", 51);
        
        // Fill the accession to term map
        accessionToTerm.put(1483, "SNV");
        accessionToTerm.put(1000032, "indel");
	accessionToTerm.put(1000002, "substitution");
	accessionToTerm.put(705, "tandem_repeat");
	accessionToTerm.put(1784, "complex_structural_alteration");
	accessionToTerm.put(17420, "copy_number_gain");
	accessionToTerm.put(1743, "copy_number_loss");
	accessionToTerm.put(1019, "copy_number_variation");
	accessionToTerm.put(1000035, "duplication");
	accessionToTerm.put(1873, "interchromosomal_breakpoint");
	accessionToTerm.put(1874, "intrachromosomal_breakpoint");
	accessionToTerm.put(1000036, "inversion");
	accessionToTerm.put(1837, "mobile_element_insertion");
	accessionToTerm.put(1838, "novel_sequence_insertion");
	accessionToTerm.put(1000173, "tandem_duplication");
	accessionToTerm.put(199, "translocation");
	accessionToTerm.put(159, "deletion");
	accessionToTerm.put(667, "insertion");
	accessionToTerm.put(1059, "sequence_alteration");
	accessionToTerm.put(51, "probe");
        
    }
    
}
