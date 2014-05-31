/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import seleniumAutomation.flowControls;
import java.io.File;

/**
 *
 * @author pmuir
 */
public class UIComponentBuilder {
    public static javax.swing.tree.DefaultTreeModel TestCaseTreeBuilder() {
        javax.swing.tree.DefaultMutableTreeNode node1 = new javax.swing.tree.DefaultMutableTreeNode("TestSuites");
        for (File file:flowControls.scanDirectoryForSuites()) {
            javax.swing.tree.DefaultMutableTreeNode node2 = new javax.swing.tree.DefaultMutableTreeNode(file.getName().split("\\.")[0]);
            node1.add(node2);
            for (String testCase: 
                    flowControls.openTestSuite(file)) {
                javax.swing.tree.DefaultMutableTreeNode node3 = new javax.swing.tree.DefaultMutableTreeNode(testCase.split(",")[0]);
                node2.add(node3);
                for (String testStep:testCase.split(",")) {
                    javax.swing.tree.DefaultMutableTreeNode node4 = new javax.swing.tree.DefaultMutableTreeNode(testStep);
                    node3.add(node4);
                }
            }
        }
        javax.swing.tree.DefaultTreeModel TM = new javax.swing.tree.DefaultTreeModel(node1);
        return TM;
    }
}
