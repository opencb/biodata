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

package org.opencb.biodata.formats.graph.dot;

import java.util.HashMap;
import java.util.Map;

public class Node {

    public static String COLOR = "color";
    public static String COMMENT = "comment";
    public static String DISTORSION = "distorsion";
    public static String FILLCOLOR = "fillcolor";
    public static String FIXEDSIZE = "fixedsize";
    public static String FONTCOLOR = "fontcolor";
    public static String FONTNAME = "fontname";
    public static String GROUP = "group";
    public static String HEIGHT = "height";
    public static String ID = "id";
    public static String LABEL = "label";
    public static String LAYER = "layer";
    public static String ORIENTATION = "orientation";
    public static String PERIPHERIES = "peripheries";
    public static String REGULAR = "regular";
    public static String SHAPE = "shape";

    public enum SHAPE_VALUES {box, box3d, circle, component, diamond, doublecircle, doubleoctagon, egg, ellipse, folder, hexagon, house, invhouse, invtrapezium, invtriangle, Mcircle, Mdiamond, Msquare, none, note, octagon, oval, parallelogram, pentagon, plaintext, polygon, point, rect, rectangle, septagon, square, tab, trapezium, triangle, tripleoctagon}

    ;
    public static String SHAPEFILE = "shapefile";
    public static String SIDES = "sides";
    public static String SKEW = "skew";
    public static String STYLE = "style";

    public enum STYLE_VALUES {bold, dashed, diagonals, dotted, filled, invisible, rounded, solid}

    ;
    public static String URL = "URL";
    public static String WIDTH = "width";
    public static String Z = "z";

    private String name = null;
    private Map<String, String> attrs = new HashMap<String, String>();

    public static final String FILL_COLOR = "fillcolor";

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, Map<String, String> attrs) {
        this.name = name;
        this.attrs = attrs;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (attrs != null && attrs.size() > 0) {
            sb.append(" [");
            for (String key : attrs.keySet()) {
                if (key.equalsIgnoreCase("fixedsize") || key.equalsIgnoreCase("regular")) {
                    sb.append(key).append(",");
                } else {
                    sb.append(key.toLowerCase()).append("=\"").append(attrs.get(key)).append("\",");
                }
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append(" ]");
        }
        sb.append(";\n");

        return sb.toString();
    }

    public void setAttribute(String key, String value) {
        attrs.put(key, value);
    }

    public void setAttributes(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    public Map<String, String> getAttributes() {
        return attrs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
