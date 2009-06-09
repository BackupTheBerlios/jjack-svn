
/** Java class "Filesystem.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker.treesource;

import de.gulden.util.treemaker.TreeMakerTreeNode;
import de.gulden.util.treemaker.TreeSourceAbstract;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.tree.TreeModel;

/**
 * <p>
 * 
 * </p>
 */
public class Filesystem extends TreeSourceAbstract {

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public TreeModel getModel() throws IOException {        
        java.io.File file = new java.io.File(getInput());
        TreeMakerTreeNode root = buildTree(file);
        return new javax.swing.tree.DefaultTreeModel(root);
    } // end getModel        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param file 
 * @return 
 */
    protected TreeMakerTreeNode buildTree(File file) {        
        if (file.exists()) {
            de.gulden.util.treemaker.TreeMaker.log(file.getAbsolutePath());
            TreeMakerTreeNode node = new TreeMakerTreeNode();
            String name = file.getName();
            node.setLabel(name);
            if (file.isDirectory()) {
                node.setType("folder");
                java.io.File[] files = file.listFiles();
                for (int i=0; i < files.length; i++) {
                    TreeMakerTreeNode childNode = buildTree(files[i]); // recursion
                    if (childNode != null) {
                        node.addChild(childNode);
                    }
                }
            } else {
                int dot = name.indexOf('.');
                String suffix;
                if (dot != -1) {
                    suffix = name.substring(dot+1);
                } else {
                    suffix = "";
                }
                node.setLeaf(true);
                node.setType(suffix);
            }
            return node;
        } else {
            return null;
        }
    } // end buildTree        

 } // end Filesystem



