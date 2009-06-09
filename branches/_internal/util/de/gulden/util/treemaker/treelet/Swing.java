
/** Java class "Swing.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker.treelet;

import de.gulden.util.treemaker.TreeletAbstract;
import java.util.*;

/**
 * <p>
 * 
 * </p>
 */
public class Swing extends TreeletAbstract {

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p>
 * 
 */
    public void generateTree() {        
        javax.swing.JFrame frame = new javax.swing.JFrame("Swing Treelet");
        frame.addWindowListener(
            new java.awt.event.WindowAdapter() {
                  public void windowClosing(java.awt.event.WindowEvent e) {
                      System.exit(0);
                  }
            }
        );
        javax.swing.JTree tree = new javax.swing.JTree(getModel()); /* {
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                return ((de.gulden.util.treemaker.TreeMakerTreeNode)value).getLabel();
            }
        };*/
        for (int i=0; i<tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(tree);
        frame.getContentPane().add(scrollPane);
        frame.setSize(300, 400);
        frame.setLocation(200, 200);
        frame.setVisible(true);
    } // end generateTree        

 } // end Swing



