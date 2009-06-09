
/** Java class "Ascii.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker.treelet;

import de.gulden.util.treemaker.TreeMakerTreeNode;
import de.gulden.util.treemaker.TreeletRendererAbstract;
import java.lang.Exception;
import java.lang.String;
import java.util.*;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * 
 * </p>
 */
public class Ascii extends TreeletRendererAbstract {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    protected int treecolumns; 

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
    public String usage() {        
        return super.usage()
               + "-columns <number of columns before comments are displayed> (default: '40')\n";
    } // end usage        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param opts 
 */
    public void init(Properties opts) throws Exception {        
        super.init(opts);
        setTreecolumns(Integer.parseInt(opts.getProperty("columns", "40")));
    } // end init        

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
    protected String renderNode(TreeMakerTreeNode node, TreeMakerTreeNode parent, List children, List parentChildren, String folderState, String icon, int restDepth) {        
        String s = ((parent!=null) ?
                       renderBranchBefore(
                                            (((parent.getParent()!=null || this.isRenderRoot()) || (parentChildren.indexOf(node)!=0) ) ? "u" 
                                                                                                                                       : "" )
                                            +"r"
                                            +((parentChildren.indexOf(node)!=parentChildren.size()-1) ? "d" 
                                                                                                      : ""))
                       : "")+
                   ((parent!=null || this.isRenderRoot()) ?
                       node.getLabel()
                     + "*"+((node.getComment() != null) ? node.getComment() : "") // node
                     // '*' will be replaced later when correct column width is ensured (little dirty)
                     : "");
        return s;
    } // end renderNode        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param branch 
 * @return 
 */
    protected String renderBranchBefore(String branch) {        
        if (branch.equals("ud")) {
            return "| ";
        } else if (branch.equals("urd")) {
            return "+-";
        } else if (branch.equals("ur")) {
            return "+-";
        } else {
            return "  ";
        }
    } // end renderBranchBefore        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param s 
 * @return 
 */
    protected String renderAsRow(String s) {        
        // split at comment (a little dirty, but useful...)
        int pos = s.indexOf('*');
        String comment = s.substring(pos+1);
        // make sure not too wide
        int columns = getTreecolumns();
        if (pos > columns) {
            s = s.substring(0, columns-3) + "...";
        } else {
            s = s.substring(0, pos) + de.gulden.util.Toolbox.repeat(' ', columns - pos);
        }
        return s + " " + stripHtml(comment) + "\n";
    } // end renderAsRow        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param s 
 * @return 
 */
    protected String renderAsTable(String s) {        
        return s;
    } // end renderAsTable        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public int getTreecolumns() {        
        return treecolumns;
    } // end getTreecolumns        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _treecolumns 
 */
    public void setTreecolumns(int _treecolumns) {        
        treecolumns = _treecolumns;
    } // end setTreecolumns        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param s 
 * @return 
 */
    protected static String stripHtml(String s) {        
        int p = s.indexOf('<');
        if (p != -1) {
            int q = s.indexOf('>', p);
            if (q != -1) {
                return s.substring(0, p) + stripHtml(s.substring(q + 1));
            }
        }
        return s;
    } // end stripHtml        

 } // end Ascii



