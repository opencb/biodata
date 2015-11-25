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

public class Edge {

    public static String ARROWHEAD = "arrowhead";

    public enum ARROWHEAD_VALUES {box, crow, diamond, dot, ediamond, empty, halfopen, inv, invempty, invdot, invodot, none, normal, obox, odiamond, odot, open, tee, vee}

    ;
    public static String ARROWSIZE = "arrowsize";
    public static String ARROWTAIL = "arrowtail";

    public enum ARROWTAIL_VALUES {box, crow, diamond, dot, ediamond, empty, halfopen, inv, invempty, invdot, invodot, none, normal, obox, odiamond, odot, open, tee, vee}

    ;
    public static String COLOR = "color";
    public static String COMMENT = "comment";
    public static String CONSTRAINT = "constraint";
    public static String DECORATE = "decorate";
    public static String DIR = "dir";

    public enum DIR_VALUES {forward, back, both, none}

    ;
    public static String FONTCOLOR = "fontcolor";
    public static String FONTNAME = "fontname";
    public static String FONTSIZE = "fontsize";
    public static String HEADLABEL = "headlabel";
    public static String HEADPORT = "headport";

    public enum HEADPORT_VALUES {n, ne, e, se, s, sw, w, nw}

    ;
    public static String HEADURL = "headURL";
    public static String LABEL = "label";
    public static String LABELANGLE = "labelangle";
    public static String LABELDISTANCE = "labeldistance";
    public static String LABELFLOAT = "labelfloat";
    public static String LABELFONTCOLOR = "labelfontcolor";
    public static String LABELFONTNAME = "labelfontname";
    public static String LABELFONTSIZE = "labelfontsize";
    public static String LAYER = "layer";
    public static String LHEAD = "lhead";
    public static String LTAIL = "ltail";
    public static String MINLEN = "minlen";
    public static String SAMEHEAD = "samehead";
    public static String SAMETAIL = "sametail";
    public static String STYLE = "style";

    public enum STYLE_VALUES {bold, dashed, dotted, invisible, solid}

    ;
    public static String TAILLABEL = "taillabel";
    public static String TAILPORT = "tailport";

    public enum TAILPORT_VALUES {n, ne, e, se, s, sw, w, nw}

    ;
    public static String TAILURL = "tailURL";
    public static String weight = "weight";

    private boolean directed = true;

    private String srcName = null;
    private String destName = null;

    private Node src = null;
    private Node dest = null;

    private Map<String, String> attrs = new HashMap<String, String>();

    public Edge(String src, String dest) {
        this(src, dest, true);
    }

    public Edge(String src, String dest, boolean directed) {
        this.directed = directed;
        this.srcName = src;
        this.destName = dest;
    }

    public Edge(String src, String dest, Map<String, String> attrs) {
        this(src, dest, true, attrs);
    }

    public Edge(String src, String dest, boolean directed, Map<String, String> attrs) {
        this.directed = directed;
        this.srcName = src;
        this.destName = dest;
        this.attrs = attrs;
    }

    public Edge(Node src, Node dest) {
        this(src, dest, true);
    }

    public Edge(Node src, Node dest, boolean directed) {
        this.directed = directed;
        this.src = src;
        this.dest = dest;

        this.srcName = src.getName();
        this.destName = dest.getName();
    }

    public Edge(Node src, Node dest, Map<String, String> attrs) {
        this(src, dest, true, attrs);
    }

    public Edge(Node src, Node dest, boolean directed, Map<String, String> attrs) {
        this.directed = directed;
        this.src = src;
        this.dest = dest;
        this.attrs = attrs;

        this.srcName = src.getName();
        this.destName = dest.getName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(srcName).append(directed ? "->" : "--").append(destName);
        if (attrs != null && attrs.size() > 0) {
            sb.append(" [");
            for (String key : attrs.keySet()) {
                if (key.equalsIgnoreCase("constraint") || key.equalsIgnoreCase("labelfloat")) {
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

    public void setSource(Node src) {
        this.src = src;
        this.srcName = src.getName();
    }

    public Node getSource() {
        return src;
    }

    public void setDestination(Node dest) {
        this.dest = dest;
        this.destName = dest.getName();
    }

    public Node getDestination() {
        return dest;
    }

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

}
