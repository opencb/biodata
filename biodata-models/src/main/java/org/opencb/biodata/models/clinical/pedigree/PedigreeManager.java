package org.opencb.biodata.models.clinical.pedigree;

import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.commons.utils.ListUtils;

import java.util.*;

public class PedigreeManager {

    private Pedigree pedigree;
    private List<Member> withoutParents;
    private List<Member> withOneParent;
    private List<Member> withoutChildren;
    private Map<String, Member> individualMap;
    private Map<String, List<Member>> partner;
    private Map<String, List<Member>> children;

    public PedigreeManager(Pedigree pedigree) {
        this.pedigree = pedigree;

        withoutParents = new ArrayList<>();
        withOneParent = new ArrayList<>();
        withoutChildren = new ArrayList<>();
        individualMap = new HashMap<>();
        partner = new HashMap<>();
        children = new HashMap<>();

        for (Member member : pedigree.getMembers()) {
            individualMap.put(member.getId(), member);

            // Parent and partner management
            if (member.getFather() == null && member.getMother() == null) {
                withoutParents.add(member);
            } else if (member.getFather() == null || member.getMother() == null) {
                withOneParent.add(member);
            } else {
                if (!partner.containsKey(member.getFather().getId())) {
                    partner.put(member.getFather().getId(), new ArrayList<>());
                }
                partner.get(member.getFather().getId()).add(member.getMother());

                if (!partner.containsKey(member.getMother().getId())) {
                    partner.put(member.getMother().getId(), new ArrayList<>());
                }
                partner.get(member.getMother().getId()).add(member.getFather());
            }

            // Children management
            if (member.getFather() != null) {
                if (!children.containsKey(member.getFather().getId())) {
                    children.put(member.getFather().getId(), new ArrayList<>());
                }
                children.get(member.getFather().getId()).add(member);
            }
            if (member.getMother() != null) {
                if (!children.containsKey(member.getMother().getId())) {
                    children.put(member.getMother().getId(), new ArrayList<>());
                }
                children.get(member.getMother().getId()).add(member);
            }
        }

        // Without children management
        for (Member member : pedigree.getMembers()) {
            if (!children.containsKey(member.getId())) {
                withoutChildren.add(member);
            }
        }
    }

    public Set<Member> getAffectedIndividuals(Phenotype phenotype) {
        Set<Member> members = new HashSet<>();
        for (Member member : pedigree.getMembers()) {
            if (ListUtils.isNotEmpty(member.getPhenotypes())) {
                for (Phenotype pheno: member.getPhenotypes()) {
                    if (StringUtils.isNotEmpty(pheno.getId()) && pheno.getId().equals(phenotype.getId())) {
                        members.add(member);
                        break;
                    }
                }
            }
        }
        return members;
    }

    public Set<Member> getUnaffectedIndividuals(Phenotype phenotype) {
        Set<Member> members = new HashSet<>();
        for (Member member : pedigree.getMembers()) {
            boolean affected = false;
            if (ListUtils.isNotEmpty(member.getPhenotypes())) {
                for (Phenotype pheno: member.getPhenotypes()) {
                    if (StringUtils.isNotEmpty(pheno.getId()) && pheno.getId().equals(phenotype.getId())) {
                        affected = true;
                        break;
                    }
                }
            }
            if (!affected) {
                members.add(member);
            }
        }
        return members;
    }

    public Pedigree getPedigree() {
        return pedigree;
    }

    public List<Member> getWithoutParents() {
        return withoutParents;
    }

    public List<Member> getWithOneParent() {
        return withOneParent;
    }

    public List<Member> getWithoutChildren() {
        return withoutChildren;
    }

    public Map<String, Member> getIndividualMap() {
        return individualMap;
    }

    public Map<String, List<Member>> getPartner() {
        return partner;
    }

    public Map<String, List<Member>> getChildren() {
        return children;
    }
}
