package org.example;

import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.export.FileExporter;
import org.jgrapht.nio.export.FormatException;
import org.jgrapht.nio.export.FileExportException;
import org.jgrapht.nio.export.StringExporter;
import org.jgrapht.nio.json.JSONExporter;

import java.io.*;
import java.nio.file.*;
import java.util.*;

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
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> v);
        try {
            FileWriter writer = new FileWriter(path);
            exporter.exportGraph(graph, writer);
            writer.close();
            System.out.println("DOT graph exported to " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputGraphics(String path, String format) {
        File outputFile = new File(path);
        File parentDir = outputFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            Path outputPath = Paths.get(path);
            if (format.equalsIgnoreCase("png")) {
                // Export the graph to PNG format
                GraphExporter<String, DefaultEdge> exporter = new PngExporter<>();
                exporter.exportGraph(graph, outputPath);
                System.out.println("Graph exported as PNG to " + outputPath);
            } else {
                System.out.println("Unsupported format. Supported formats: PNG");
            }
        } catch (FileExportException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MyGraph myGraph = new MyGraph();
        myGraph.parseGraph("input.dot");

        // Add nodes and edges (if needed)
        String[] newNodes = {"D", "E", "F"};
        myGraph.addNodes(newNodes);
        myGraph.addEdge("A", "D");
        myGraph.addEdge("D", "E");
        myGraph.addEdge("E", "A");

        // Output the DOT graph to a file
        myGraph.outputDOTGraph("output.dot");

        // Output the graph as PNG
        myGraph.outputGraphics("output.png", "png");
    }

}
