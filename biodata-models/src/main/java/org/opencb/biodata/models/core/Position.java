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

package org.opencb.biodata.models.core;

import java.util.ArrayList;
import java.util.List;

public class Position {

    private String chromosome;
    private int position;

    public Position(String chromosome, int position) {
        this.chromosome = chromosome;
        this.position = position;
    }

    public static Position parsePosition(String positionString) {
        Position position = null;
        if (positionString != null && !positionString.equals("")) {
            String[] fields = positionString.split(":", -1);
            if (fields.length == 2) {
                position = new Position(fields[0], Integer.parseInt(fields[1]));
            } else {
                position = null;
            }
        }
        return position;
    }

    public static List<Position> parsePositions(String positionString) {
        List<Position> positions = null;
        if (positionString != null && !positionString.equals("")) {
            String[] positionItems = positionString.split(",");
            positions = new ArrayList<Position>(positionItems.length);
            String[] fields;
            for (String posString : positionItems) {
                fields = posString.split(":", -1);
                if (fields.length == 2) {
                    positions.add(new Position(fields[0], Integer.parseInt(fields[1])));
                } else {
                    positions.add(null);
                }
            }
        }
        return positions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Position{");
        sb.append("chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append('}');
        return sb.toString();
    }

    public String getChromosome() {
        return chromosome;
    }

    public Position setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public Position setPosition(int position) {
        this.position = position;
        return this;
    }
}
