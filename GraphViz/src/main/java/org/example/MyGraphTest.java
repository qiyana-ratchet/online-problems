package org.example;

import org.junit.jupiter.api.Test;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyGraphTest {
    @Test
    public void testParseGraph() {
        MyGraph myGraph = new MyGraph();
        myGraph.parseGraph("testInput.dot"); // Provide a test input file

        // Check if the number of nodes, edges match the expected values
        Assert.assertEquals(3, myGraph.graph.vertexSet().size());
        Assert.assertEquals(3, myGraph.graph.edgeSet().size());
    }

    @Test
    public void testAddNode() {
        MyGraph myGraph = new MyGraph();
        myGraph.parseGraph("testInput.dot"); // Provide a test input file

        // Add a node and check if it exists in the graph
        myGraph.addNode("D");
        Assert.assertTrue(myGraph.graph.containsVertex("D"));

        // Add a list of nodes and check if they exist in the graph
        String[] newNodes = {"E", "F"};
        myGraph.addNodes(newNodes);
        Assert.assertTrue(myGraph.graph.containsVertex("E"));
        Assert.assertTrue(myGraph.graph.containsVertex("F"));
    }

    @Test
    public void testAddEdge() {
        MyGraph myGraph = new MyGraph();
        myGraph.parseGraph("testInput.dot"); // Provide a test input file

        // Add an edge and check if it exists in the graph
        myGraph.addEdge("A", "C");
        Assert.assertTrue(myGraph.graph.containsEdge("A", "C"));
    }

    @Test
    public void testOutputDOTGraph() throws IOException {
        MyGraph myGraph = new MyGraph();
        myGraph.parseGraph("testInput.dot"); // Provide a test input file
        myGraph.outputDOTGraph("testOutput.dot"); // Output to a test file

        // Read the expected output from a file
        String expected = new String(Files.readAllBytes(Paths.get("testExpectedOutput.dot")), StandardCharsets.UTF_8);

        // Read the actual output from the generated file
        String actual = new String(Files.readAllBytes(Paths.get("testOutput.dot")), StandardCharsets.UTF_8);

        // Compare the expected and actual output
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testOutputGraphics() {
        MyGraph myGraph = new MyGraph();
        myGraph.parseGraph("testInput.dot"); // Provide a test input file
        myGraph.outputGraphics("testOutput.png", "PNG"); // Output to a test PNG file

        // Check if the PNG file exists
        File pngFile = new File("testOutput.png");
        Assert.assertTrue(pngFile.exists());

    }
}
