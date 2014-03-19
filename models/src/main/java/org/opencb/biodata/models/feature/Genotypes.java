package org.opencb.biodata.models.feature;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 8/27/13
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class Genotypes {

    public static void addGenotypeToList(List<Genotype> list, Genotype g) {

        Genotype auxG;
        int index = list.indexOf(g);

        if (index >= 0) {
            auxG = list.get(index);
            auxG.setCount(auxG.getCount() + 1);
        } else {
            g.setCount(g.getCount() + 1);
            list.add(g);
        }

    }
}
