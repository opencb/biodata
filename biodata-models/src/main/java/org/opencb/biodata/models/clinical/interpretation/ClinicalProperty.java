/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.clinical.interpretation;

public class ClinicalProperty {

    public enum ModeOfInheritance {
        MONOALLELIC,
        MONOALLELIC_NOT_IMPRINTED,
        MONOALLELIC_MATERNALLY_IMPRINTED,
        MONOALLELIC_PATERNALLY_IMPRINTED,
        BIALLELIC,
        MONOALLELIC_AND_BIALLELIC,
        MONOALLELIC_AND_MORE_SEVERE_BIALLELIC,
        XLINKED_BIALLELIC,
        XLINKED_MONOALLELIC,
        YLINKED,
        MITOCHONDRIAL,

        // Not modes of inheritance, but...
        DE_NOVO,
        COMPOUND_HETEROZYGOUS,

        UNKNOWN
    }

    public enum Penetrance {
        COMPLETE,
        INCOMPLETE
    }

    public enum RoleInCancer {
        ONCOGENE,
        TUMOR_SUPPRESSOR_GENE,
        BOTH
    }


//    public static Pedigree getPedigreeFromFamily(Family family) {
//        List<org.opencb.biodata.models.core.pedigree.Individual> individuals = parseMembersToBiodataIndividuals(family.getMembers());
//        return new Pedigree(family.getId(), individuals, family.getPhenotypes(), family.getAttributes());
//    }

//    private static List<org.opencb.biodata.models.core.pedigree.Individual> parseMembersToBiodataIndividuals(List<Individual> members) {
//        Map<String, org.opencb.biodata.models.core.pedigree.Individual> individualMap = new HashMap();
//
//        // Parse all the individuals
//        for (Individual member : members) {
//            org.opencb.biodata.models.core.pedigree.Individual individual =
//                    new org.opencb.biodata.models.core.pedigree.Individual(member.getId(), member.getName(), null, null,
//                            member.getMultiples(),
//                            org.opencb.biodata.models.core.pedigree.Individual.Sex.getEnum(member.getSex().toString()),
//                            member.getLifeStatus(),
//                            org.opencb.biodata.models.core.pedigree.Individual.AffectionStatus.getEnum(member.getAffectationStatus()
//                                    .toString()), member.getPhenotypes(), member.getAttributes());
//            individualMap.put(individual.getId(), individual);
//        }
//
//        // Fill parent information
//        for (Individual member : members) {
//            if (member.getFather() != null && StringUtils.isNotEmpty(member.getFather().getId())) {
//                individualMap.get(member.getId()).setFather(individualMap.get(member.getFather().getId()));
//            }
//            if (member.getMother() != null && StringUtils.isNotEmpty(member.getMother().getId())) {
//                individualMap.get(member.getId()).setMother(individualMap.get(member.getMother().getId()));
//            }
//        }
//
//        return new ArrayList<>(individualMap.values());
//    }

}
