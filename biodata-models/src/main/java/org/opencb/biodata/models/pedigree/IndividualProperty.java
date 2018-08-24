package org.opencb.biodata.models.pedigree;

public class IndividualProperty {

    public enum Sex {
        MALE, FEMALE, UNKNOWN, UNDETERMINED
    }

    public enum LifeStatus {
        ALIVE, ABORTED, DECEASED, UNBORN, STILLBORN, MISCARRIAGE, UNKNOWN
    }

    public enum AffectationStatus {
        CONTROL, AFFECTED, UNAFFECTED, UNKNOWN
    }

    public enum KaryotypicSex {
        UNKNOWN, XX, XY, XO, XXY, XXX, XXYY, XXXY, XXXX, XYY, OTHER
    }

}
