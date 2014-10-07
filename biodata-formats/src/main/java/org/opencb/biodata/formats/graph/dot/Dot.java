package org.opencb.biodata.formats.graph.dot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dot {

    public static String BGCOLOR = "bgcolor";
    public static String CENTER = "center";
    public static String CLUSTERRANK = "clusterrank";

    public enum STYLE_CLUSTERRANK {global, none}

    ;
    public static String COLOR = "color";
    public static String COMMENT = "comment";
    public static String COMPOUND = "compound";
    public static String CONCENTRATE = "concentrate";
    public static String FILLCOLOR = "fillcolor";
    public static String FONTCOLOR = "fontcolor";
    public static String FONTNAME = "fontname";
    public static String FONTPATH = "fontpath";
    public static String FONTSIZE = "fontsize";
    public static String LABEL = "label";
    public static String LABELJUST = "labeljust";

    public enum LABELJUST_VALUES {c, l, r}

    ;
    public static String LABELLOC = "labelloc";

    public enum LABELLOC_VALUES {c, b, t}

    ;
    public static String LAYERS = "layers";
    public static String MARGIN = "margin";
    public static String MCLIMIT = "mclimit";
    public static String NODESEP = "nodesep";
    public static String NSLIMIT = "nslimit";
    public static String NSLIMIT1 = "nslimit1";
    public static String ORDERING = "ordering";

    public enum ORDERING_VALUES {in, out}

    ;
    public static String ORIENTATION = "orientation";

    public enum ORIENTATION_VALUES {portrait, landscape}

    ;
    public static String PAGE = "page";
    public static String PAGEDIR = "pagedir";
    public static String QUANTUM = "quantum";
    public static String RANK = "rank";
    public static String RANKDIR = "rankdir";
    public static String RANKSEP = "ranksep";
    public static String RATIO = "ratio";
    public static String REMINCROSS = "remincross";
    public static String ROTATE = "rotate";
    public static String SAMPLEPOINTS = "samplepoints";
    public static String SEARCHSIZE = "searchsize";
    public static String SIZE = "size";
    public static String STYLE = "style";

    public enum STYLE_VALUES {bold, dashed, diagonals, dotted, filled, invisible, rounded, solid}

    ;
    public static String URL = "URL";

    private String name;
    private boolean directed = true;
    private Map<String, Node> nodes = new HashMap<String, Node>();
    private List<Edge> edges = new ArrayList<Edge>();

    private Map<String, String> attrs = new HashMap<String, String>();

    public Dot(String name) {
        this.name = name;
    }

    public Dot(String name, boolean directed) {
        this.name = name;
        this.directed = directed;
    }

    public Dot(String name, boolean directed, Map<String, String> attrs) {
        this.name = name;
        this.directed = directed;
        this.attrs = attrs;
    }

    public void addNode(Node node) {
        nodes.put(node.getName(), node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
        if (!nodes.containsKey(edge.getSrcName())) {
            nodes.put(edge.getSrcName(), edge.getSource());
        }
        if (!nodes.containsKey(edge.getDestName())) {
            nodes.put(edge.getDestName(), edge.getDestination());
        }
    }

    public void save(String filename) throws IOException {
        PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(filename), Charset.defaultCharset()));
        pw.print(toString());
        pw.close();
//		IOUtils.write(filename, toString());
    }

    public void save(File file) throws IOException {
        PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(file.getAbsolutePath()), Charset.defaultCharset()));
        pw.print(toString());
        pw.close();
//		IOUtils.write(path, toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        //System.out.println("number of nodes = " + nodes.size());
        try {
            sb.append(directed ? "digraph \"" : "graph \"").append(name).append("\" {\n");
            if (attrs != null && attrs.size() > 0) {
                for (String key : attrs.keySet()) {
                    sb.append("\t");
                    if (key.equalsIgnoreCase("center") || key.equalsIgnoreCase("compound") || key.equalsIgnoreCase("concentrate")) {
                        sb.append(key).append(";\n");
                    } else {
                        sb.append(key.toLowerCase()).append("=\"").append(attrs.get(key)).append("\";\n");
                    }
                }
            }

            for (String key : nodes.keySet()) {
                if (nodes.get(key) != null) {
                    sb.append("\t").append(nodes.get(key).toString());
                } else {
                    Node n = new Node(key);
                    n.setAttribute(Node.LABEL, "");
                    sb.append("\t").append(n.toString());
                    System.out.println("---> node " + key + " is null !!!");
                }
            }

            for (Edge edge : edges) {
                sb.append("\t").append(edge.toString());
            }

            sb.append("}\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
