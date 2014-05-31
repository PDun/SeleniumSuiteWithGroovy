/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GroovyScripting;

import UI.ScriptingUI;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.SyntaxTester;

/**
 *
 * @author pmuir
 */
public class EmbeddedGroovy {
    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                if (comp instanceof javax.swing.JComboBox) {
                    for (ItemListener listener:((javax.swing.JComboBox)comp).getItemListeners()) {
                        System.out.println(listener.getClass().getName());
                    }
                }
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

    public EmbeddedGroovy(String script) {
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("groovy");
            engine.eval(script);
        } catch (ScriptException ex) {
            Logger.getLogger(EmbeddedGroovy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static JEditorPane getSyntaxKit() {
        DefaultSyntaxKit.initKit();
        JEditorPane codeEditor = new JEditorPane();
        JScrollPane scrPane = new JScrollPane(codeEditor);
        codeEditor.setContentType("text/java");
        codeEditor.setText("public static void main(String[] args) {\n}");
        return codeEditor;
    }
    
    public static void saveScript(String script, File file) {
        
    }
}
