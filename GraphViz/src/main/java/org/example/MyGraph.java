package org.example;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import java.io.*;
import java.util.*;

public class MyGraph {
    public DefaultDirectedGraph<String, DefaultEdge> graph;

    public MyGraph() {
        // Initialize an empty directed graph when creating a new MyGraph object.
        this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    public void parseGraph(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("->")) {
                    // Parse edges (e.g., "a -> b")
                    String[] parts = line.split("->");
                    String srcNode = parts[0].trim();
                    String dstNode = parts[1].trim();
                    this.graph.addVertex(srcNode);
                    this.graph.addVertex(dstNode);
                    this.graph.addEdge(srcNode, dstNode);
                }
                // TODO: add nodes without edges
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        // Print information about the graph, including nodes and edges.
        StringBuilder result = new StringBuilder();
        result.append("Number of nodes: ").append(graph.vertexSet().size()).append("\n");
        result.append("Nodes: ").append(graph.vertexSet()).append("\n");
        result.append("Number of edges: ").append(graph.edgeSet().size()).append("\n");
        result.append("Edges: ").append(graph.edgeSet()).append("\n");
        return result.toString();
    }

    public void outputGraph(String filepath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            for (DefaultEdge edge : graph.edgeSet()) {
                String srcNode = graph.getEdgeSource(edge);
                String dstNode = graph.getEdgeTarget(edge);
                writer.println(srcNode + " -> " + dstNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNode(String label) {
        if (!graph.containsVertex(label)) {
            graph.addVertex(label);
        } else {
            System.out.println("Node with label '" + label + "' already exists.");
        }
    }

    public void addNodes(String[] labels) {
        for (String label : labels) {
            addNode(label);
        }
    }

    public void addEdge(String srcLabel, String dstLabel) {
        if (graph.containsVertex(srcLabel) && graph.containsVertex(dstLabel)) {
            DefaultEdge newEdge = new DefaultEdge();
            if (!graph.containsEdge(srcLabel, dstLabel)) {
                graph.addEdge(srcLabel, dstLabel, newEdge);
            } else {
                System.out.println("Edge between '" + srcLabel + "' and '" + dstLabel + "' already exists.");
            }
        } else {
            System.out.println("One or both of the specified nodes do not exist.");
        }
    }

    public void outputDOTGraph(String path) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            writer.println("digraph G {");

            // Write nodes with attributes
            for (String node : graph.vertexSet()) {
                writer.println("    " + node + " [label=\"" + node + "\"];");
            }

            // Write edges with attributes
            for (DefaultEdge edge : graph.edgeSet()) {
                String srcNode = graph.getEdgeSource(edge);
                String dstNode = graph.getEdgeTarget(edge);
                writer.println("    " + srcNode + " -> " + dstNode + " [label=\"" + graph.getEdgeWeight(edge) + "\"];");
            }

            writer.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputGraphics(String path, String format) {
        try {
            Graphviz.fromFile(new File("output.dot"))
                    .render(Format.valueOf(format))
                    .toFile(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        MyGraph myGraph = new MyGraph();
        myGraph.parseGraph("input.dot");

        // Add nodes (if needed)
        String[] newNodes = {"D", "E", "F"};
        myGraph.addNodes(newNodes);

        // Add edges
        myGraph.addEdge("A", "D");
        myGraph.addEdge("D", "E");
        myGraph.addEdge("E", "A");

        myGraph.outputGraph("output.txt");

        // Output the graph to a DOT file
        myGraph.outputDOTGraph("output.dot");

        myGraph.outputGraphics("outputDOT.png", "PNG");
    }
}
