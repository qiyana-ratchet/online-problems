package org.example;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import java.io.*;
import java.util.*;

public class MyGraph {
    private DefaultDirectedGraph<String, DefaultEdge> graph;

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
                    graph.addVertex(srcNode);
                    graph.addVertex(dstNode);
                    graph.addEdge(srcNode, dstNode);
                }
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

    public static void main(String[] args) {
        MyGraph myGraph = new MyGraph();
        myGraph.parseGraph("input.dot");
        System.out.println(myGraph.toString());
        myGraph.outputGraph("output.dot");
    }
}
