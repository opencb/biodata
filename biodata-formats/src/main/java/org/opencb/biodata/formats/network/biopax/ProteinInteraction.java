/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.network.biopax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProteinInteraction {

    private BioPax bioPax;

    private Map<String, Integer> visitedMap;
    private Map<String, String> interactions;

    public ProteinInteraction(BioPax bioPax) {
        this.bioPax = bioPax;
        this.visitedMap = new HashMap<String, Integer>();
        this.interactions = new HashMap<String, String>();
    }

    public List<String> getList() {

        BioPaxElement pathway;
        List<String> pathwayList = bioPax.getPathwayList();
        System.out.println("number of pathways = " + pathwayList.size());
        for (int i = 0; i < pathwayList.size(); i++) {
//		for(int i=0 ; i<5 ; i++) {
            pathway = bioPax.getElementMap().get(pathwayList.get(i));

            //System.out.println("------------------------------------------------");
            //System.out.println(pathway.toString());
            //System.out.println("------");

            if (pathway.getParams().containsKey("pathwayOrder-id")) {
                for (String stepId : pathway.getParams().get("pathwayOrder-id")) {

                    //System.out.println("step id ------ " + stepId);
                    BioPaxElement step = bioPax.getElementMap().get(stepId);
                    //System.out.println(step.toString());

                    if (step.getParams().containsKey("stepProcess-id") && step.getParams().get("stepProcess-id") != null && step.getParams().get("stepProcess-id").size() > 0 &&
                            step.getParams().containsKey("nextStep-id") && step.getParams().get("nextStep-id") != null && step.getParams().get("nextStep-id").size() > 0) {
                        for (String stepProcessId : step.getParams().get("stepProcess-id")) {
                            for (String nextProcessId : step.getParams().get("nextStep-id")) {
                                processPair(stepProcessId, nextProcessId);
                            }
                        }
                    }
                }
            }
        }
        List<String> list = new ArrayList<String>();
        for (String key : interactions.keySet()) {
            list.add(interactions.get(key));
        }
        return list;
    }


    private void processPair(String processId, String nextProcessId) {
        List<String> list = new ArrayList<String>();
        String pairName = processId + ">>>>>" + nextProcessId;

        if (!visitedMap.containsKey(pairName)) {

            visitedMap.put(pairName, 1);

            List<String> proteinFrom, proteinTo;
            BioPaxElement from = bioPax.getElementMap().get(processId);
            BioPaxElement to = bioPax.getElementMap().get(nextProcessId);

            //System.out.println("------------------- processing " + from.getBioPaxClassName() + " -> " + to.getBioPaxClassName() + ": " + pairName);

            proteinFrom = getProteinsFromElement(from, "out");

            if (proteinFrom != null && proteinFrom.size() > 0) {

                proteinTo = getProteinsFromElement(to, "in");

                if (proteinTo != null && proteinTo.size() > 0) {

                    for (String pFrom : proteinFrom) {
                        for (String pTo : proteinTo) {
                            interactions.put(pFrom + "-" + pTo, pFrom + "\t" + pTo);
                        }
                    }

                }
            }
        }
    }

    private List<String> getProteinsFromElement(BioPaxElement element, String mode) {

        BioPaxElement e = null;
        List<String> from = null, to = null;
        List<String> proteinFrom = new ArrayList<String>(), proteinTo = new ArrayList<String>();

        if (element.getParams().containsKey("controller-id") || element.getParams().containsKey("controlled-id")) {
            from = element.getParams().get("controller-id");
            to = element.getParams().get("controlled-id");
        } else if (element.getParams().containsKey("left-id") || element.getParams().containsKey("right-id")) {
            from = element.getParams().get("left-id");
            to = element.getParams().get("right-id");
        }

        List<String> list = new ArrayList<String>();

        if (("in".equalsIgnoreCase(mode) && from != null) || ("out".equalsIgnoreCase(mode) && to != null)) {
            //System.out.println("++++++++++++++++++ element " + element.getBioPaxClassName() + ", mode = " + mode + ", id = " + element.getId() + " !!!");

            //System.out.println("++++ from = " + ListUtils.toString(from));
            if (from != null) {
                for (String id : from) {
                    e = bioPax.getElementMap().get(id);
                    proteinFrom.addAll(getProteinsFromElement(e));
                }
            }

            //System.out.println("++++ to = " + ListUtils.toString(to));
            if (to != null) {
                for (String id : to) {
                    e = bioPax.getElementMap().get(id);
                    proteinTo.addAll(getProteinsFromElement(e));
                }
            }

            if (proteinFrom.size() > 0 && proteinTo.size() > 0) {
                for (String f : proteinFrom) {
                    for (String t : proteinTo) {
                        interactions.put(f + "-" + t, f + "\t" + t);
                    }
                }
            }

            if ("in".equalsIgnoreCase(mode)) {
                return proteinFrom;
            } else if ("out".equalsIgnoreCase(mode)) {
                return proteinTo;
            }

        } else {
            System.out.println("------------------ element " + element.getBioPaxClassName() + ", mode = " + mode + ", id = " + element.getId() + " no processed !!!");
            //System.out.println(element.toString());
            return null;
        }

        return list;
    }

    private List<String> getProteinsFromElement(BioPaxElement e) {
        List<String> list = new ArrayList<String>();

//		System.out.println("oooooooooooooo ---> getting protein from " + e.getBioPaxClassName() + ", id = " + e.getId());

        if ("Protein".equalsIgnoreCase(e.getBioPaxClassName())) {

//			System.out.println("protein, element id : " + e.getId());
            if (e.getParams().containsKey("entityReference-id") && e.getParams().get("entityReference-id") != null) {

                //System.out.println(e.getParams().get("entityReference-id").get(0) + " is stored in map ? " + bioPax.getElementMap().containsKey(e.getParams().get("entityReference-id").get(0)));

                BioPaxElement entityRef = bioPax.getElementMap().get(e.getParams().get("entityReference-id").get(0));
                //System.out.println("entity reference: " + entityRef.toString());
                if (entityRef.getParams().containsKey("xref-id") && entityRef.getParams().get("xref-id") != null) {
                    BioPaxElement xRef = bioPax.getElementMap().get(entityRef.getParams().get("xref-id").get(0));
                    if (xRef.getParams().containsKey("id") && xRef.getParams().get("id") != null) {

                        //System.out.println(">>>>> protein: " + xRef.getParams().get("id").get(0));
                        list.add(xRef.getParams().get("id").get(0));
                    }
                }
            } else if (e.getParams().containsKey("memberPhysicalEntity-id") && e.getParams().get("memberPhysicalEntity-id") != null) {
                list.addAll(getProteinsFromElement(bioPax.getElementMap().get(e.getParams().get("memberPhysicalEntity-id").get(0))));
            } else {
                System.out.println("!!!!!!!!!!!!!!!!!!!! no entityRef, no memberPhysicalEntity !!!!!!!!!!!!!!!!!!!!! -> name = " + e.getParams().get("name").get(0));
                //list.add(e.getParams().get("name").get(0));
            }
        } else if ("Complex".equalsIgnoreCase(e.getBioPaxClassName())) {
            //System.out.println("complex, element id : " + e.getId());
            if (e.getParams().containsKey("component-id") && e.getParams().get("component-id") != null) {
                List<String> compIds = e.getParams().get("component-id");
                for (String compId : compIds) {
                    list.addAll(getProteinsFromElement(bioPax.getElementMap().get(compId)));
                }
            }
        } else {
            System.out.println("oooooooooooooo, not implemented yet ---> getting protein from " + e.getBioPaxClassName() + ", id = " + e.getId());
        }
        return list;
    }

}
