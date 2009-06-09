
/** Java class "Xml.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker.treesource;

import de.gulden.util.treemaker.TreeMakerTreeNode;
import de.gulden.util.treemaker.TreeSourceAbstract;
import java.io.IOException;
import java.lang.String;
import java.util.*;
import java.util.Collection;
import java.util.Properties;
import javax.swing.tree.TreeModel;
import org.w3c.dom.Element;

/**
 * <p>
 * 
 * </p>
 */
public class Xml extends TreeSourceAbstract {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    protected Collection leafNodeNames; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected String labelAttribute; 

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
        String xmlString = readInputFile();
        org.w3c.dom.Element rootNode = de.gulden.util.xml.XMLToolbox.parseXML(xmlString);
        TreeMakerTreeNode root = buildTree(rootNode);
        return new javax.swing.tree.DefaultTreeModel(root);
    } // end getModel        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param tag 
 * @return 
 */
    protected TreeMakerTreeNode buildTree(Element tag) {        
        TreeMakerTreeNode node = new TreeMakerTreeNode();
        String tagname = tag.getTagName();
        node.setType(tagname);
        node.setLeaf(isLeaf(tagname));
        node.setLabel(tag.getAttribute(getLabelAttribute()));
        org.w3c.dom.NodeList children = tag.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            org.w3c.dom.Node child = children.item(i);            
            if (child instanceof org.w3c.dom.Element) {
                node.addChild(buildTree((org.w3c.dom.Element)child)); // recusrsion
            }
        }
        return node;
    } // end buildTree        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public String usage() {        
        String s = 
            super.usage()
            + "-leafs <comma-seperated names of leaf-tags> (default: every tag is branch-node)\n"
            + "-labelattr <name of aatribute to read node label from> (default: 'label')\n";
        return s;
    } // end usage        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param opts 
 */
    public void init(Properties opts) {        
        String leafs = opts.getProperty("leafs");
        Collection l;
        if (leafs != null) {
            l = new TreeSet(de.gulden.util.Toolbox.explode(leafs));
        } else {
            l = new TreeSet();
        }
        setLeafNodeNames(l);
        String attr = opts.getProperty("labelattr", "label");
        setLabelAttribute(attr);
    } // end init        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param tagname 
 * @return 
 */
    protected boolean isLeaf(String tagname) {        
        return getLeafNodeNames().contains(tagname);
    } // end isLeaf        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public Collection getLeafNodeNames() {        
        return leafNodeNames;
    } // end getLeafNodeNames        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _leafNodeNames 
 */
    public void setLeafNodeNames(Collection _leafNodeNames) {        
        leafNodeNames = _leafNodeNames;
    } // end setLeafNodeNames        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public String getLabelAttribute() {        
        return labelAttribute;
    } // end getLabelAttribute        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _labelAttribute 
 */
    public void setLabelAttribute(String _labelAttribute) {        
        labelAttribute = _labelAttribute;
    } // end setLabelAttribute        

 } // end Xml



