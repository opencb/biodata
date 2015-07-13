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

package org.opencb.biodata.formats.network.biopax.core;

import java.util.List;

public class Conversion extends Interaction {
    private List<String> left;
    private List<String> participantStoichiometry;
    private List<String> right;
    private List<String> spontaneous;

    public Conversion(
            List<String> availability,
            List<String> comment,
            List<String> dataSource,
            List<String> evidence,
            List<String> name,
            List<String> xref,
            List<String> interactionType,
            List<String> participant,
            List<String> left,
            List<String> participantStoichiometry,
            List<String> right,
            List<String> spontaneous) {
        super(availability, comment, dataSource, evidence, name, xref,
                interactionType, participant);
        this.left = left;
        this.participantStoichiometry = participantStoichiometry;
        this.right = right;
        this.spontaneous = spontaneous;
    }

    public List<String> getLeft() {
        return left;
    }

    public void setLeft(List<String> left) {
        this.left = left;
    }

    public List<String> getParticipantStoichiometry() {
        return participantStoichiometry;
    }

    public void setParticipantStoichiometry(List<String> participantStoichiometry) {
        this.participantStoichiometry = participantStoichiometry;
    }

    public List<String> getRight() {
        return right;
    }

    public void setRight(List<String> right) {
        this.right = right;
    }

    public List<String> getSpontaneous() {
        return spontaneous;
    }

    public void setSpontaneous(List<String> spontaneous) {
        this.spontaneous = spontaneous;
    }


}
