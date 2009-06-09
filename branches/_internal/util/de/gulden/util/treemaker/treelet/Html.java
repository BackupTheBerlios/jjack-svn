
/** Java class "Html.java" generated from Poseidon for UML.
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
public class Html extends TreeletRendererAbstract {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    protected String imgPrefix; 

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
             + "-img <relative path to images dir> (default: 'img')\n";
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
        setImgPrefix(opts.getProperty("img", "img/"));
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
                       "<td width=\"16\"><img src=\""+getImgPrefix()+"/"+folderState+( ( (parent.getParent()!=null || this.isRenderRoot()) || (parentChildren.indexOf(node)!=0) ) ? "u" : "" )+"r"+((parentChildren.indexOf(node)!=parentChildren.size()-1) ? "d" : "")+".png\"></td>" // before node
                       : "")+
                   ((parent!=null || this.isRenderRoot()) ?
                       "<td colspan=\""+(restDepth+1)+"\" nowrap><img src=\""+getImgPrefix()+"/"+icon+".png\"><font face=\"Arial,Helvetica\" size=\"2\"> "+node.getLabel()+"</font></td>"
                     + "<td>&nbsp;</td><td nowrap>"+((node.getComment() != null) ? node.getComment() : "")+"</td>" // node
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
        return "<td width=\"16\"><img src=\"" + this.getImgPrefix() + "/"+branch+".png\"></td>";
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
        return "<tr>"+s+"</tr>";
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
        String html = 
            "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"
            + s
            +"</table>";
        return html;
    } // end renderAsTable        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public String getImgPrefix() {        
        return imgPrefix;
    } // end getImgPrefix        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _imgPrefix 
 */
    public void setImgPrefix(String _imgPrefix) {        
        imgPrefix = _imgPrefix;
    } // end setImgPrefix        

 } // end Html



