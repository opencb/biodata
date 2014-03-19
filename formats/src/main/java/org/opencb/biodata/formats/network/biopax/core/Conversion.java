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
