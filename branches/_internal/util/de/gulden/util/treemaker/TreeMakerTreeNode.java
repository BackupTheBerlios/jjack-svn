
/** Java class "TreeMakerTreeNode.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker;

import java.lang.Object;
import java.lang.String;
import java.util.*;
import java.util.Collection;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 * <p>
 * 
 * </p>
 */
public class TreeMakerTreeNode implements TreeNode {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    protected String type; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected String label; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected String comment; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected boolean leaf; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected boolean open; 

   ///////////////////////////////////////
   // associations

/**
 * <p>
 * 
 * </p>
 */
    public TreeMakerTreeNode parentNode; 
/**
 * <p>
 * 
 * </p>
 */
    public Collection childNode = new ArrayList(); // of type TreeMakerTreeNode


   ///////////////////////////////////////
   // access methods for associations

    /** @poseidon-generated */
    public TreeMakerTreeNode getParentNode() {
        return parentNode;
    }
    /** @poseidon-generated */
    public void setParentNode(TreeMakerTreeNode treeMakerTreeNode) {
        if (this.parentNode != treeMakerTreeNode) {
            if (this.parentNode != null) this.parentNode.removeChildNode(this);
            this.parentNode = treeMakerTreeNode;
            if (treeMakerTreeNode != null) treeMakerTreeNode.addChildNode(this);
        }
    }
    /** @poseidon-generated */
    public Collection getChildNodes() {
        return childNode;
    }
    /** @poseidon-generated */
    public void addChildNode(TreeMakerTreeNode treeMakerTreeNode) {
        if (! this.childNode.contains(treeMakerTreeNode)) {
            this.childNode.add(treeMakerTreeNode);
            treeMakerTreeNode.setParentNode(this);
        }
    }
    /** @poseidon-generated */
    public void removeChildNode(TreeMakerTreeNode treeMakerTreeNode) {
        boolean removed = this.childNode.remove(treeMakerTreeNode);
        if (removed) treeMakerTreeNode.setParentNode((TreeMakerTreeNode)null);
    }


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
    public String getType() {        
        return type;
    } // end getType        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _type 
 */
    public void setType(String _type) {        
        type = _type;
    } // end setType        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public Enumeration children() {        
        return Collections.enumeration(getChildren());
    } // end children        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public boolean getAllowsChildren() {        
        return !isLeaf();
    } // end getAllowsChildren        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param index 
 * @return 
 */
    public TreeNode getChildAt(int index) {        
        return (TreeNode)((ArrayList)getChildren()).get(index);
    } // end getChildAt        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public int getChildCount() {        
        return getChildren().size();
    } // end getChildCount        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param child 
 * @return 
 */
    public int getIndex(TreeNode child) {        
        return ((ArrayList)getChildren()).indexOf(child);
    } // end getIndex        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public String getLabel() {        
        return label;
    } // end getLabel        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _label 
 */
    public void setLabel(String _label) {        
        label = _label;
    } // end setLabel        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public boolean isLeaf() {        
        return leaf;
    } // end isLeaf        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _leaf 
 */
    public void setLeaf(boolean _leaf) {        
        leaf = _leaf;
    } // end setLeaf        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public Collection getChildren() {        
        return getChildNodes();
    } // end getChildren        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public TreeNode getParent() {        
        return getParentNode();
    } // end getParent        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param node 
 */
    public void addChild(TreeNode node) {        
        addChildNode((TreeMakerTreeNode)node);
    } // end addChild        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public String getComment() {        
        return comment;
    } // end getComment        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _comment 
 */
    public void setComment(String _comment) {        
        comment = _comment;
    } // end setComment        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public String getPath() {        
        TreeMakerTreeNode p = (TreeMakerTreeNode)this.getParent();
        return ( (p!=null) ? p.getPath() : "" ) + "/" + this.getLabel(); // recursion
    } // end getPath        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public boolean isOpen() {        
        return open;
    } // end isOpen        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _open 
 */
    public void setOpen(boolean _open) {        
        open = _open;
    } // end setOpen        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public String toString() {        
        return getLabel();
    } // end toString        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param o 
 * @return 
 */
    public int compareTo(Object o) {        
        return this.toString().compareTo(o.toString());
    } // end compareTo        

 } // end TreeMakerTreeNode



