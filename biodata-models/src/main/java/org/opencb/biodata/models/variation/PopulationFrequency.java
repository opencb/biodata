package org.opencb.biodata.models.variation;

public class PopulationFrequency {

	private String pop;
	private String superPop;
	private String refAllele;			// TODO: remove after checking compatibility with dependencies
	private String altAllele;			// TODO: remove after checking compatibility with dependencies
	private double refAlleleFreq;		// TODO: remove after checking compatibility with dependencies
	private double altAlleleFreq;		// TODO: remove after checking compatibility with dependencies


	private double[] freqs = {-1, -1, -1, -1, -1};  // refFreq,altFreq,refRefFreq,refAltFreq,altAltFreq


	public PopulationFrequency() {
	}

	public PopulationFrequency(String pop, String refAllele, String altAllele, double refAlleleFreq,
			double altAlleleFreq) {
		this.pop = pop;
		this.refAllele = refAllele;
		this.altAllele = altAllele;
		this.refAlleleFreq = refAlleleFreq;
		this.altAlleleFreq = altAlleleFreq;
	}

	public PopulationFrequency(String pop, String superPop, String refAllele, String altAllele) {
		this.pop = pop;
		this.superPop = superPop;
		this.refAllele = refAllele;
		this.altAllele = altAllele;
	}

	public String getPop() {
		return pop;
	}

	public void setPop(String pop) {
		this.pop = pop;
	}

	public String getRefAllele() {
		return refAllele;
	}

	public void setRefAllele(String refAllele) {
		this.refAllele = refAllele;
	}

	public String getAltAllele() {
		return altAllele;
	}

	public void setAltAllele(String altAllele) {
		this.altAllele = altAllele;
	}

	public double getRefAlleleFreq() {
		return refAlleleFreq;
	}

	public void setRefAlleleFreq(double refAlleleFreq) {
		this.refAlleleFreq = refAlleleFreq;
	}

	public double getAltAlleleFreq() {
		return altAlleleFreq;
	}

	public void setAltAlleleFreq(double altAlleleFreq) {
		this.altAlleleFreq = altAlleleFreq;
	}

	public void setRefAllFreq(double refAlleleFreq) {			// TODO: rename to setRefAlleleFreq when possible
		freqs[0] = refAlleleFreq;
	}

	public void setAltAllFreq(double altAlleleFreq) {			// TODO: rename to setAltAlleleFreq when possible
		freqs[1] = altAlleleFreq;
	}

	public void setHetGenotypeFreq(double hetGenotypeFreq) {
		freqs[3] = hetGenotypeFreq;
	}

	public void setHomRefGenotypeFreq(double homReferenceGenotypeFreq) {
		freqs[2] = homReferenceGenotypeFreq;
	}

	public void setHomAltGenotypeFreq(double homAlternativeGenotypeFreq) {
		freqs[4] = homAlternativeGenotypeFreq;
	}

	// private double homRefAlleleFreq;
	// private double hetAlleleFreq;
	// private double HomAltAlleleFreq;

}
