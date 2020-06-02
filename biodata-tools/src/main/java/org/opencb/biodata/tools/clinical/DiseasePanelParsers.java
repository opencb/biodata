package org.opencb.biodata.tools.clinical;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.clinical.interpretation.CancerPanel;
import org.opencb.biodata.models.clinical.ClinicalProperty;
import org.opencb.biodata.models.clinical.interpretation.DiseasePanel;
import org.opencb.biodata.models.core.OntologyTerm;
import org.opencb.biodata.models.core.Xref;
import org.opencb.commons.utils.FileUtils;
import org.opencb.commons.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class DiseasePanelParsers {

    protected static Logger logger = LoggerFactory.getLogger(DiseasePanelParsers.class);

    public static DiseasePanel parseCensus(Path censusTsvFile) throws IOException {
        Set<String> myKeys = new HashSet<>(Arrays.asList("Gene Symbol", "Name", "Entrez GeneId", "Genome Location",
                "Tier", "Hallmark", "Chr Band", "Somatic", "Germline", "Tumour Types(Somatic)",
                "Tumour Types(Germline)", "Cancer Syndrome", "Tissue Type", "Molecular Genetics", "Role in Cancer",
                "Mutation Types", "Translocation Partner", "Other Germline Mut", "Other Syndrome", "Synonyms"));

        try (BufferedReader bufferedReader = FileUtils.newBufferedReader(censusTsvFile)) {
            Map<Integer, String> keyPositionMap = new HashMap<>();
            String[] header = bufferedReader.readLine().split("\t");
            for (int i = 0; i < header.length; i++) {
                String key = header[i];
                if (!myKeys.contains(key)) {
                    throw new IOException("Key '" + key + "' from census file not found in our whitelist");
                }
                keyPositionMap.put(i, key);
            }

            DiseasePanel panel = new DiseasePanel("gene-census", "gene-census", new LinkedList<>(), new LinkedList<>(),
                    new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>(),
                    new HashMap<>(), new DiseasePanel.SourcePanel("", "", "", "", ""), "", "", "", new HashMap<>());
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splittedLine = line.split("\t");

                DiseasePanel.GenePanel genePanel = new DiseasePanel.GenePanel("", "", new LinkedList<>(),
                        ClinicalProperty.ModeOfInheritance.UNKNOWN, null, null, new LinkedList<>(), new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(), new CancerPanel(false, false, null, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(), new LinkedList<>()));
                for (int i = 0; i < splittedLine.length; i++) {
                    String value = StringUtils.strip(splittedLine[i], "\"'");

                    switch (keyPositionMap.get(i)) {
                        case "Gene Symbol":
                            genePanel.setId(value);
                            genePanel.setName(value);
                            break;
                        case "Name":
                            genePanel.getXrefs().add(new Xref(value, "Census", "Census", "Name"));
                            break;
                        case "Entrez GeneId":
                            genePanel.getXrefs().add(new Xref(value, "Census", "Census", "Entrez GeneId"));
                            break;
                        case "Genome Location":
                            genePanel.getCoordinates().add(new DiseasePanel.Coordinate("GRCh38", value, "Census"));
                            break;
                        case "Tier":
                            if ("1".equals(value)) {
                                genePanel.setConfidence(ClinicalProperty.Confidence.HIGH);
                            } else if ("2".equals(value)) {
                                genePanel.setConfidence(ClinicalProperty.Confidence.MEDIUM);
                            } else {
                                genePanel.setConfidence(ClinicalProperty.Confidence.LOW);
                            }
                            break;
                        case "Hallmark":
                            break;
                        case "Chr Band":
                            break;
                        case "Somatic":
                            if ("yes".equals(value)) {
                                genePanel.getCancer().setSomatic(true);
                            }
                            break;
                        case "Germline":
                            if ("yes".equals(value)) {
                                genePanel.getCancer().setGermline(true);
                            }
                            break;
                        case "Tumour Types(Somatic)":
                            if (StringUtils.isNotEmpty(value)) {
                                List<String> tumourTypes = Arrays.asList(value.split(", "));
                                genePanel.getCancer().setSomaticTumourTypes(tumourTypes);
                            }
                            break;
                        case "Tumour Types(Germline)":
                            if (StringUtils.isNotEmpty(value)) {
                                List<String> tumourTypes = Arrays.asList(value.split(", "));
                                genePanel.getCancer().setGermlineTumourTypes(tumourTypes);
                            }
                            break;
                        case "Cancer Syndrome":
                            if (StringUtils.isNotEmpty(value)) {
                                genePanel.getPhenotypes().add(new OntologyTerm(value, value, "Census", "", "", "",
                                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                                        Collections.emptyList()));
                            }
                            break;
                        case "Tissue Type":
                            if (StringUtils.isNotEmpty(value)) {
                                List<String> tissues = Arrays.asList(value.split(", "));
                                genePanel.getCancer().setTissues(tissues);
                            }
                            break;
                        case "Molecular Genetics":
                            if (StringUtils.isNotEmpty(value)) {
                                if ("Dom".equals(value)) {
                                    genePanel.setModeOfInheritance(ClinicalProperty.ModeOfInheritance.AUTOSOMAL_DOMINANT);
                                } else if ("Rec".equals(value)) {
                                    genePanel.setModeOfInheritance(ClinicalProperty.ModeOfInheritance.AUTOSOMAL_RECESSIVE);
                                } else if ("Dom/Rec".equals(value)) {
                                    genePanel.setModeOfInheritance(ClinicalProperty.ModeOfInheritance.AUTOSOMAL_DOMINANT);
                                } else if ("Rec/X".equals(value)) {
                                    genePanel.setModeOfInheritance(ClinicalProperty.ModeOfInheritance.X_LINKED_RECESSIVE);
                                } else {
                                    System.out.println("Unknown moi '" + value + "'");
                                }
                            }
                            break;
                        case "Role in Cancer":
                            if (StringUtils.isNotEmpty(value)) {
                                String[] roles = value.split(", ");
                                ClinicalProperty.RoleInCancer roleInCancer = null;
                                for (String role : roles) {
                                    ClinicalProperty.RoleInCancer tmpRole = null;
                                    if ("TSG".equals(role)) {
                                        tmpRole = ClinicalProperty.RoleInCancer.TUMOR_SUPPRESSOR_GENE;
                                    } else if ("oncogene".equals(role)) {
                                        tmpRole = ClinicalProperty.RoleInCancer.ONCOGENE;
                                    }
                                    if (tmpRole != null && roleInCancer == null) {
                                        roleInCancer = tmpRole;
                                    } else if (tmpRole != null) {
                                        if (tmpRole != roleInCancer) {
                                            roleInCancer = ClinicalProperty.RoleInCancer.BOTH;
                                        } else {
                                            System.out.println("Found repeated roles?");
                                        }
                                    }
                                }
                                if (roleInCancer != null) {
                                    genePanel.getCancer().setRole(roleInCancer);
                                }
                            }
                            break;
                        case "Mutation Types":
                            break;
                        case "Translocation Partner":
                            if (StringUtils.isNotEmpty(value) && !"?".equals(value)) {
                                List<String> partners = Arrays.asList(value.split(", "));
                                genePanel.getCancer().setFusionPartners(partners);
                            }
                            break;
                        case "Other Germline Mut":
                            break;
                        case "Other Syndrome":
                            break;
                        case "Synonyms":
                            String[] synonyms = value.split(",");
                            for (String synonym : synonyms) {
                                genePanel.getXrefs().add(new Xref(synonym, "Census", "Census", "Synonyms"));
                            }
                            break;
                        default:
                            break;
                    }

                }
                panel.getGenes().add(genePanel);
            }
            return panel;
        }
    }

    public static DiseasePanel parsePanelApp(Path panelAppJsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        Map<String, Object> panelInfo = objectMapper.readValue(panelAppJsonFile.toFile(), Map.class);

        List<DiseasePanel.PanelCategory> categories = new ArrayList<>(2);
        categories.add(new DiseasePanel.PanelCategory(String.valueOf(panelInfo.get("disease_group")), 1));
        categories.add(new DiseasePanel.PanelCategory(String.valueOf(panelInfo.get("disease_sub_group")), 2));

        List<OntologyTerm> disorders = new ArrayList<>();
        for (String relevantDisorder : (List<String>) panelInfo.get("relevant_disorders")) {
            if (StringUtils.isNotEmpty(relevantDisorder)) {
                disorders.add(new OntologyTerm(relevantDisorder, relevantDisorder, "", "", "", "",
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList()));
            }
        }

        List<DiseasePanel.GenePanel> genes = new ArrayList<>();
        for (Map<String, Object> gene : (List<Map>) panelInfo.get("genes")) {
            DiseasePanel.GenePanel genePanel = new DiseasePanel.GenePanel();

            extractCommonInformationFromPanelApp(gene, genePanel);

            List<DiseasePanel.Coordinate> coordinates = new ArrayList<>();

            Map<String, Object> geneData = (Map) gene.get("gene_data");
            Map<String, Object> ensemblGenes = (Map) geneData.get("ensembl_genes");
            // Read coordinates
            for (String assembly : ensemblGenes.keySet()) {
                Map<String, Object> assemblyObject = (Map<String, Object>) ensemblGenes.get(assembly);
                for (String version : assemblyObject.keySet()) {
                    Map<String, Object> coordinateObject = (Map<String, Object>) assemblyObject.get(version);
                    String correctAssembly = "GRch37".equals(assembly) ? "GRCh37" : "GRCh38";
                    coordinates.add(new DiseasePanel.Coordinate(correctAssembly, String.valueOf(coordinateObject.get("location")),
                            "Ensembl v" + version));
                }
            }

            genePanel.setName(String.valueOf(geneData.get("hgnc_symbol")));
            genePanel.setCoordinates(coordinates);

            genes.add(genePanel);
        }

        List<DiseasePanel.RegionPanel> regions = new ArrayList<>();
        for (Map<String, Object> panelAppRegion : (List<Map>) panelInfo.get("regions")) {
            DiseasePanel.RegionPanel region = new DiseasePanel.RegionPanel();

            extractCommonInformationFromPanelApp(panelAppRegion, region);

            List<Integer> coordinateList = null;
            if (ListUtils.isNotEmpty((Collection<?>) panelAppRegion.get("grch38_coordinates"))) {
                coordinateList = (List<Integer>) panelAppRegion.get("grch38_coordinates");
            } else if (ListUtils.isNotEmpty((Collection<?>) panelAppRegion.get("grch37_coordinates"))) {
                coordinateList = (List<Integer>) panelAppRegion.get("grch37_coordinates");
            }

            String id;
            if (panelAppRegion.get("entity_name") != null
                    && StringUtils.isNotEmpty(String.valueOf(panelAppRegion.get("entity_name")))) {
                id = String.valueOf(panelAppRegion.get("entity_name"));
            } else {
                id = (String) panelAppRegion.get("chromosome");
                if (coordinateList != null && coordinateList.size() == 2) {
                    id = id + ":" + coordinateList.get(0) + "-" + coordinateList.get(1);
                } else {
                    logger.warn("Could not read region coordinates");
                }
            }

            DiseasePanel.VariantType variantType = null;
            String typeOfVariant = String.valueOf(panelAppRegion.get("type_of_variants"));
            if ("cnv_loss".equals(typeOfVariant)) {
                variantType = DiseasePanel.VariantType.LOSS;
            } else if ("cnv_gain".equals(typeOfVariant)) {
                variantType = DiseasePanel.VariantType.GAIN;
            } else {
                System.out.println(typeOfVariant);
            }

            region.setId(id);
            region.setDescription(String.valueOf(panelAppRegion.get("verbose_name")));
            region.setHaploinsufficiencyScore(String.valueOf(panelAppRegion.get("haploinsufficiency_score")));
            region.setTriplosensitivityScore(String.valueOf(panelAppRegion.get("triplosensitivity_score")));
            region.setRequiredOverlapPercentage((int) panelAppRegion.get("required_overlap_percentage"));
            region.setTypeOfVariants(variantType);

            regions.add(region);
        }

        List<DiseasePanel.STR> strs = new ArrayList<>();
        for (Map<String, Object> panelAppSTR : (List<Map>) panelInfo.get("strs")) {
            DiseasePanel.STR str = new DiseasePanel.STR();

            extractCommonInformationFromPanelApp(panelAppSTR, str);

            str.setRepeatedSequence(String.valueOf(panelAppSTR.get("repeated_sequence")));
            str.setNormalRepeats((int) panelAppSTR.get("normal_repeats"));
            str.setPathogenicRepeats((int) panelAppSTR.get("pathogenic_repeats"));

            strs.add(str);
        }

        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("PanelAppInfo", panel);

        DiseasePanel diseasePanel = new DiseasePanel();
        diseasePanel.setId(String.valueOf(panelInfo.get("name"))
                .replace(" - ", "-")
                .replace("/", "-")
                .replace(" (", "-")
                .replace("(", "-")
                .replace(") ", "-")
                .replace(")", "")
                .replace(" & ", "_and_")
                .replace(", ", "-")
                .replace(" ", "_") + "-PanelAppId-" + panelInfo.get("id"));
        diseasePanel.setName(String.valueOf(panelInfo.get("name")));
        diseasePanel.setCategories(categories);
        diseasePanel.setDisorders(disorders);
        diseasePanel.setGenes(genes);
        diseasePanel.setStrs(strs);
        diseasePanel.setRegions(regions);
        diseasePanel.setSource(new DiseasePanel.SourcePanel()
                .setId(String.valueOf(panelInfo.get("id")))
                .setName(String.valueOf(panelInfo.get("name")))
                .setVersion(String.valueOf(panelInfo.get("version")))
                .setProject("PanelApp (GEL)")
        );
        diseasePanel.setDescription(panelInfo.get("disease_sub_group")
                + " (" + panelInfo.get("disease_group") + ")");
        diseasePanel.setAttributes(attributes);

        if ("Cancer Programme".equals(String.valueOf(panelInfo.get("disease_group")))) {
            diseasePanel.setTags(Collections.singletonList("cancer"));
        }

        return diseasePanel;
    }

    private static <T extends DiseasePanel.Common> void extractCommonInformationFromPanelApp(Map<String, Object> panelAppCommonMap, T common) {
        String ensemblGeneId = "";
        List<Xref> xrefs = new ArrayList<>();
        List<String> publications = new ArrayList<>();
        List<OntologyTerm> phenotypes = new ArrayList<>();
        List<DiseasePanel.Coordinate> coordinates = new ArrayList<>();

        Map<String, Object> geneData = (Map) panelAppCommonMap.get("gene_data");
        if (geneData != null) {
            Map<String, Object> ensemblGenes = (Map) geneData.get("ensembl_genes");

            if (ensemblGenes.containsKey("GRch37")) {
                ensemblGeneId = String.valueOf(((Map) ((Map) ensemblGenes.get("GRch37")).get("82")).get("ensembl_id"));
            } else if (ensemblGenes.containsKey("GRch38")) {
                ensemblGeneId = String.valueOf(((Map) ((Map) ensemblGenes.get("GRch38")).get("90")).get("ensembl_id"));
            }

            // read OMIM ID
            if (geneData.containsKey("omim_gene") && geneData.get("omim_gene") != null) {
                for (String omim : (List<String>) geneData.get("omim_gene")) {
                    xrefs.add(new Xref(omim, "OMIM", "OMIM"));
                }
            }
            xrefs.add(new Xref(String.valueOf(geneData.get("gene_name")), "GeneName", "GeneName"));
        }

        // Add coordinates
        String chromosome = String.valueOf(panelAppCommonMap.get("chromosome"));
        if (ListUtils.isNotEmpty((Collection<?>) panelAppCommonMap.get("grch38_coordinates"))) {
            List<Integer> auxCoordinates = (List<Integer>) panelAppCommonMap.get("grch38_coordinates");
            coordinates.add(new DiseasePanel.Coordinate("GRCh38", chromosome + ":" + auxCoordinates.get(0) + "-" + auxCoordinates.get(1),
                    "Ensembl"));
        }
        if (ListUtils.isNotEmpty((Collection<?>) panelAppCommonMap.get("grch37_coordinates"))) {
            List<Integer> auxCoordinates = (List<Integer>) panelAppCommonMap.get("grch37_coordinates");
            coordinates.add(new DiseasePanel.Coordinate("GRCh37", chromosome + ":" + auxCoordinates.get(0) + "-" + auxCoordinates.get(1),
                    "Ensembl"));
        }


        // read publications
        if (panelAppCommonMap.containsKey("publications")) {
            publications = (List<String>) panelAppCommonMap.get("publications");
        }

        // Read phenotypes
        if (panelAppCommonMap.containsKey("phenotypes") && !((List<String>) panelAppCommonMap.get("phenotypes")).isEmpty()) {
            for (String phenotype : ((List<String>) panelAppCommonMap.get("phenotypes"))) {
                String id = phenotype;
                String source = "";
                if (phenotype.length() >= 6) {
                    String substring = phenotype.substring(phenotype.length() - 6);
                    try {
                        Integer.parseInt(substring);
                        // If the previous call doesn't raise any exception, we are reading an OMIM id.
                        id = substring;
                        source = "OMIM";
                    } catch (NumberFormatException e) {
                        id = phenotype;
                    }
                }

                phenotypes.add(new OntologyTerm(id, phenotype, source, "", "", "", Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList()));
            }
        }

        // Read penetrance
        String panelAppPenetrance = String.valueOf(panelAppCommonMap.get("penetrance"));
        ClinicalProperty.Penetrance penetrance = null;
        if (StringUtils.isNotEmpty(panelAppPenetrance)) {
            try {
                penetrance = ClinicalProperty.Penetrance.valueOf(panelAppPenetrance.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Could not parse penetrance. Value found: " + panelAppPenetrance);
            }
        }

        common.setId(ensemblGeneId);
        common.setXrefs(xrefs);
        common.setModeOfInheritance(getMoiFromGenePanel(String.valueOf(panelAppCommonMap.get("mode_of_inheritance"))));
        common.setPenetrance(penetrance);
        ClinicalProperty.Confidence confidence = ClinicalProperty.Confidence.LOW;
        int confidenceLevel = Integer.valueOf(String.valueOf(panelAppCommonMap.get("confidence_level")));
        if (confidenceLevel == 2) {
            confidence = ClinicalProperty.Confidence.MEDIUM;
        } else if (confidenceLevel == 3) {
            confidence = ClinicalProperty.Confidence.HIGH;
        }
        common.setConfidence(confidence);
        common.setEvidences((List<String>) panelAppCommonMap.get("evidence"));
        common.setPublications(publications);
        common.setPhenotypes(phenotypes);
        common.setCoordinates(coordinates);
    }

    private static ClinicalProperty.ModeOfInheritance getMoiFromGenePanel(String inputMoi) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(inputMoi)) {
            return ClinicalProperty.ModeOfInheritance.UNKNOWN;
        }

        String moi = inputMoi.toUpperCase();

        if (moi.startsWith("BIALLELIC")) {
            return ClinicalProperty.ModeOfInheritance.AUTOSOMAL_RECESSIVE;
        }
        if (moi.startsWith("MONOALLELIC")) {
            if (moi.contains("NOT")) {
                return ClinicalProperty.ModeOfInheritance.MONOALLELIC_NOT_IMPRINTED;
            } else if (moi.contains("MATERNALLY")) {
                return ClinicalProperty.ModeOfInheritance.MONOALLELIC_MATERNALLY_IMPRINTED;
            } else if (moi.contains("PATERNALLY")) {
                return ClinicalProperty.ModeOfInheritance.MONOALLELIC_PATERNALLY_IMPRINTED;
            } else {
                return ClinicalProperty.ModeOfInheritance.AUTOSOMAL_DOMINANT;
            }
        }
        if (moi.startsWith("BOTH")) {
            if (moi.contains("SEVERE")) {
                return ClinicalProperty.ModeOfInheritance.MONOALLELIC_AND_MORE_SEVERE_BIALLELIC;
            } else if (moi.contains("")) {
                return ClinicalProperty.ModeOfInheritance.MONOALLELIC_AND_BIALLELIC;
            }
        }
        if (moi.startsWith("MITOCHONDRIAL")) {
            return ClinicalProperty.ModeOfInheritance.MITOCHONDRIAL;
        }
        if (moi.startsWith("X-LINKED")) {
            if (moi.contains("BIALLELIC")) {
                return ClinicalProperty.ModeOfInheritance.X_LINKED_RECESSIVE;
            } else {
                return ClinicalProperty.ModeOfInheritance.X_LINKED_RECESSIVE;
            }
        }
        return ClinicalProperty.ModeOfInheritance.UNKNOWN;
    }

}
