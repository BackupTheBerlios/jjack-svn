
/** Java class "TreeletRendererAbstract.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker;

import java.lang.Exception;
import java.lang.String;
import java.util.*;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 */
public abstract class TreeletRendererAbstract extends TreeletAbstract {

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p>
 * 
 */
    public void generateTree() throws Exception {        
        TreeMakerTreeNode root = (TreeMakerTreeNode)getModel().getRoot();
        int treeDepth = de.gulden.util.treemaker.TreeMaker.depth(root);
        String html = renderAsTable(renderRow(0, treeDepth, root));
        writeOutputFile(html);
    } // end generateTree        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param depth 
 * @param treeDepth 
 * @param node 
 * @return 
 */
    protected String renderRow(int depth, int treeDepth, TreeMakerTreeNode node) {        
        final Comparator comparator = new Comparator() {
            public int compare(Object a, Object b) {
                TreeMakerTreeNode nodeA = (TreeMakerTreeNode)a;
                TreeMakerTreeNode nodeB = (TreeMakerTreeNode)b;
                if (!nodeA.isLeaf() && nodeB.isLeaf()) {
                    return -1;
                } else if (nodeA.isLeaf() && !nodeB.isLeaf()) {
                    return 1;
                } else {
                    return stripSuffix(nodeA.getLabel()).compareTo(stripSuffix(nodeB.getLabel()));
                }
            }
            public boolean equals(Object o) {
                return false;
            }
            private String stripSuffix(String s) {
                int dotpos = s.indexOf('.');
                if (dotpos != -1) {
                    return s.substring(0, dotpos);
                } else {
                    return s;
                }
            }
        };
        TreeMakerTreeNode parent = (TreeMakerTreeNode)node.getParent();
        List children = Collections.list(node.children());
        Collections.sort(children, comparator);
        List parentChildren = null;
        if (parent != null) {
            parentChildren = Collections.list(parent.children());
            Collections.sort(parentChildren, comparator);
        }
        String folderState = (node.isLeaf() ? "" : (node.isOpen() ? "op" : "cl" ));
        String icon;
        if (!node.isLeaf()) {
            icon = "folder" + folderState;
        } else {
            icon = "file";
        }
        int restDepth = treeDepth - depth;
        
        String s = renderNode(node, parent, children, parentChildren, folderState, icon, restDepth);
        
        if (parent != null) {
            TreeMakerTreeNode lastParent = parent;
            parent = (TreeMakerTreeNode)parent.getParent();
            while (parent != null) {
                parentChildren = Collections.list(parent.children());
                Collections.sort(parentChildren, comparator);
                int lastParentIndex = parentChildren.indexOf(lastParent);
                String branch;
                if (lastParentIndex < parentChildren.size()-1) { // more following in this branch
                    branch = "ud";
                } else {
                    branch = "x";
                }
                lastParent = parent;
                parent = (TreeMakerTreeNode)parent.getParent();
                if ((parent!=null && parent.getParent()!=null) || this.isRenderRoot()) {
                    s = renderBranchBefore(branch) + s;
                    //s = "<td width=\"16\"><img src=\""+getImgPrefix()+"/"+branch+".png\"></td>" + s;
                }
            }
        }
        
        s = renderAsRow(s);
        if (node.isOpen()) {
            for (Iterator it=children.iterator(); it.hasNext();) {
                TreeMakerTreeNode child = (TreeMakerTreeNode)it.next();
                s += renderRow(depth + 1, treeDepth, child); // recursion
            }
        }
        return s;
    } // end renderRow        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param node 
 * @param parent 
 * @param children 
 * @param parentChildren 
 * @param folderState 
 * @param icon 
 * @param restDepth 
 * @return 
 */
    protected abstract String renderNode(TreeMakerTreeNode node, TreeMakerTreeNode parent, List children, List parentChildren, String folderState, String icon, int restDepth);

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param branch 
 * @return 
 */
    protected abstract String renderBranchBefore(String branch);

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param s 
 * @return 
 */
    protected abstract String renderAsRow(String s);

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param s 
 * @return 
 */
    protected abstract String renderAsTable(String s);

 } // end TreeletRendererAbstract



