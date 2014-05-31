/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seleniumAutomation;

import UI.Controls;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author pmuir
 */
public class flowControls {

    public static Selenium selenium;
    public static int[] steps;

    public static Selenium getSelenium() {
        if (selenium == null) {
            selenium = new Selenium();
        }
        return selenium;
    }

    public static void runFlow() {
        Thread thread = new Thread() {
            public void run() {
                try {
                    steps = UI.Controls.getSelectedSteps();
                    if (steps.length > 1) {
                        for (int stepNumber : steps) {
                            if (UI.Controls.getPlayButton().isSelected()) {
                                String step = UI.Controls.getStepValue(stepNumber);
                                UI.Controls.selectNextStep(stepNumber);
                                UI.Controls.setSelectedVisible();
                                getSelenium().runStep(step);
                            }
                        }
                    } else {
                        for (int i = steps[0] - 1; UI.Controls.getNextStep(i) != null; i++) {
                            if (UI.Controls.getPlayButton().isSelected()) {
                                String step = UI.Controls.getNextStep(i);
                                UI.Controls.selectNextStep(i);
                                UI.Controls.setSelectedVisible();
                                getSelenium().runStep(step);
                            }
                        }
                    }
                    UI.Controls.getPlayButton().setSelected(false);
                    if (!UI.Controls.getpauseButton().isSelected()) {
                        getSelenium().teardown();
                    }
                } catch (Exception e) {
                    if (UI.Controls.getPlayButton().isSelected()) {
                        System.err.println(e);
                        UI.Controls.getPlayButton().setSelected(false);
                        getSelenium().teardown();
                    }
                }
            }

        };
        thread.start();
    }

    public static void runStep() {
        Thread thread = new Thread() {
            public void run() {
                int[] steps = UI.Controls.getSelectedSteps();
                String step = UI.Controls.getStepValue(steps[0]);
                UI.Controls.selectNextStep(steps[0]);
                UI.Controls.setSelectedVisible();
                getSelenium().runStep(step);
                UI.Controls.getstepButton().setSelected(false);
            }
        };
        thread.start();
    }

    public static ArrayList<String> openTestSuite(File fileName) {
        String testCase = "";
        ArrayList<String> testSuite = new ArrayList<String>();
        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader(fileName));
            testCase = file.readLine();
            while (testCase != null) {
                if (testCase.length() > 4) {
                    testSuite.add(testCase);
                }
                testCase = file.readLine();
            }
            return testSuite;
        } catch (IOException ex) {
            Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void launchFlow(final String testCase) {
        try {
            if (testCase.length() > 4) {
                while (Thread.activeCount() > 5) {
                    Thread.sleep(1000);
                }
                Thread thread = new Thread() {
                    public void run() {
                        getSelenium().parseTestCase(testCase);
                        return;
                    }
                };
                thread.start();
                Thread.sleep(10);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(flowControls.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<File> scanDirectoryForSuites() {
        File dir;
        try {
            dir = new File(new File("").getCanonicalPath() + "/Data");
            List<File> files = (List<File>) FileUtils.listFiles(dir, new String[]{"testsuite"}, true);
            return files;
        } catch (IOException ex) {
            Logger.getLogger(flowControls.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static LinkedHashMap<String, ArrayList<String>> parseTree(javax.swing.event.TreeSelectionEvent TSE) {
        testSuite.clear();
        testStep.clear();
        parseNodes(((javax.swing.tree.DefaultMutableTreeNode) ((javax.swing.JTree) TSE.getSource()).getSelectionPath().getLastPathComponent()));
        Controls.getTestSuite().setModel(new DefaultListModel());
        for (String key : testSuite.keySet()) {
            ((DefaultListModel) Controls.getTestSuite().getModel()).addElement(key);
        }
        return testSuite;
    }

    public static LinkedHashMap<String, ArrayList<String>> testSuite = new LinkedHashMap<String, ArrayList<String>>();
    public static ArrayList<String> testStep = new ArrayList<String>();

    public static void parseNodes(javax.swing.tree.DefaultMutableTreeNode nodes) {
        for (int i = 0; i < nodes.getChildCount(); i++) {
            parseNodes((javax.swing.tree.DefaultMutableTreeNode) nodes.getChildAt(i));
        }
        if (nodes.getChildCount() == 0) {
            if (!testStep.isEmpty()) {
                if (!((javax.swing.tree.DefaultMutableTreeNode) nodes.getParent()).getLastChild().equals(nodes)) {
                    testStep.add(nodes.getUserObject().toString());
                } else {
                    testStep.add(nodes.getUserObject().toString());
                    testSuite.put(testStep.get(0), testStep);
                    testStep = new ArrayList<String>();
                }
            } else {
                testStep = new ArrayList();
                testStep.add(nodes.getUserObject().toString());
                testSuite.put(testStep.get(0), testStep);
            }
        }
    }
}
