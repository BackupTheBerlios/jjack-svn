
/** Java class "TreeMaker.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker;

import java.lang.Exception;
import java.lang.String;
import java.util.*;
import java.util.Properties;
import javax.swing.tree.TreeNode;

/**
 * <p>
 * 
 * </p>
 */
public class TreeMaker {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    public static final String BASE_PACKAGE = "de.gulden.util.treemaker"; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected static boolean verbose = false; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected Properties comments; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected boolean openall; 

   ///////////////////////////////////////
   // associations

/**
 * <p>
 * 
 * </p>
 */
    public TreeSource treeSource; 
/**
 * <p>
 * 
 * </p>
 */
    public Treelet treelet; 


   ///////////////////////////////////////
   // access methods for associations

    /** @poseidon-generated */
    public TreeSource getTreeSource() {
        return treeSource;
    }
    /** @poseidon-generated */
    public void setTreeSource(TreeSource treeSource) {
        this.treeSource = treeSource;
    }
    /** @poseidon-generated */
    public Treelet getTreelet() {
        return treelet;
    }
    /** @poseidon-generated */
    public void setTreelet(Treelet treelet) {
        this.treelet = treelet;
    }


  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param args 
 */
    public static void main(String[] args) {        
        new TreeMaker().doMain(args);
    } // end main        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param args 
 */
    public void doMain(String[] args) {        
        try {
            Properties opts = getDefaultOptions();
            parseOpts(args, opts);
            init(opts);
            makeTree();
        } catch (Exception e) {
            if (e.getClass() != Exception.class) { // more specific than just Exception
                System.out.println(e.getClass().getName()+":");
            }
            System.out.println(e.getMessage());
            System.out.println(usage());
            if (e.getClass() != Exception.class) { // more specific than just Exception
                e.printStackTrace(System.out);
            }
        }        
    } // end doMain        

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
            "Usage: <jre and classpath> TreeMaker <options> | -help\n"
          + "TreeMaker's own options are:\n"
          + "-S <TreeSource class>\n"
          + "-s <TreeSource class from package de.gulden.util.treemaker.treesource>\n"
          + "-T <Treelet class>\n"
          + "-t <Treelet class from package de.gulden.util.treemaker.treelet>\n"
          + "-c <config properties-file, setting nodes open/close and specifying comments strings>\n"
          + "-openall opens all nodes, overwrites entries in config file\n"
          + "-v verbose output\n"
          + "\n"
          + "example: -t filesystem -s html\n";
        TreeSource ts = getTreeSource();
        Treelet tl = getTreelet();
        if ((ts == null)||(tl == null)) {
            s += "\n-s|-S, -t|-T together with -help to see individual options.\n";
        } 
        if (ts != null) {
            s += ts.usage();
        }
        if (tl != null) {
            s += tl.usage();
        }
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
    public void init(Properties opts) throws Exception {        
        verbose = opts.containsKey("v");
        this.openall = opts.containsKey("openall");
        String treeSourceClassname = opts.getProperty("S");
        if (treeSourceClassname == null) {
            String s = opts.getProperty("s");
            s = s.substring(0,1).toUpperCase() + s.substring(1);
            treeSourceClassname = BASE_PACKAGE + ".treesource." + s;
            Class treeSourceClass = Class.forName(treeSourceClassname);
            this.treeSource = (TreeSource)treeSourceClass.newInstance();
            this.treeSource.init(opts);
        }
        String treeletClassname = opts.getProperty("T");
        if (treeletClassname == null) {
            String s = opts.getProperty("t");
            s = s.substring(0,1).toUpperCase() + s.substring(1);
            treeletClassname = BASE_PACKAGE + ".treelet." + s;
            Class treeletClass = Class.forName(treeletClassname);
            this.treelet = (Treelet)treeletClass.newInstance();
            this.treelet.init(opts);
        }
        String commentsFilename = opts.getProperty("c");
        if (commentsFilename != null) {
            this.comments = new Properties();
            this.comments.load(new java.io.FileInputStream(commentsFilename));
            
        }
        if (opts.containsKey("help")||opts.containsKey("h")||opts.containsKey("?")) {
            throw new Exception(); // will show usage-message only
        }
    } // end init        

/**
 * <p>
 * Does ...
 * </p>
 * 
 */
    public void makeTree() throws Exception {        
        log("collecting tree data...");
        javax.swing.tree.TreeModel model = getTreeSource().getModel();
        if (this.comments != null) {
            log("assigning comments");
        }
        assignComments((TreeMakerTreeNode)model.getRoot());
        getTreelet().setModel(model);
        log("generating tree...");
        getTreelet().generateTree();
        log("ok.");
    } // end makeTree        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    protected Properties getDefaultOptions() {        
        Properties p = new Properties();
        p.put("s","filesystem");
        p.put("t","html");
        return p;
    } // end getDefaultOptions        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param args 
 * @param opts 
 */
    protected void parseOpts(String[] args, Properties opts) throws Exception {        
        for (int i=0; i < args.length; i++) {
            String opt = args[i];
            String next = (i<args.length-1) ? args[i+1] : null;
            String value;
            if ((next!=null) && (!next.startsWith("-"))) {
                value = next;
                i++;
            } else {
                value = "true"; // single option switch
            }
            if (opt.startsWith("-")) {
                opts.put(opt.substring(1), value);
            } else {
                throw new Exception("unkown option '"+opt+"'");
            }
        }
    } // end parseOpts        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param node 
 */
    protected void assignComments(TreeMakerTreeNode node) {
//if (node==null) return; // 2004-11-11
            String path = node.getPath();
            String text;
            boolean open;
            if (this.comments != null) {
                open = (node.getParent()==null); // root open by default, others closed
                text = this.comments.getProperty(path);
                if (text != null) {
                    if (text.startsWith("open")) {
                        open = true;
                        text = text.substring(4);
                    } else if (text.startsWith("closed")) {
                        open = false;
                        text = text.substring(5);
                    }
                    node.setComment(text.trim());
                }
            } else {
                open = true; // if no config file: open all nodes
            }
            node.setOpen(open || this.openall);
            for (int i=0; i < node.getChildCount(); i++) {
                TreeMakerTreeNode child = (TreeMakerTreeNode)node.getChildAt(i);
                assignComments(child); // recursion
            }
        //} else {
        //    node.setOpen(true);
        //}
    } // end assignComments        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param node 
 * @return 
 */
    public static int depth(TreeNode node) {        
//if (node==null) return 0; // 2004-11-11
        int depth = 0;
        for (int i=0; i < node.getChildCount(); i++) {
            TreeNode child = (TreeNode)node.getChildAt(i);
            int d = depth(child) + 1;
            if (d > depth) {
                depth = d;
            }
        }
        return depth;
    } // end depth        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param opts 
 * @param opt 
 */
    public static void requireOption(Properties opts, String opt) throws Exception {        
        if (!opts.containsKey(opt)) {
            throw new Exception("option -"+opt+" is required");
        }
    } // end requireOption        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public static boolean isVerbose() {        
        return verbose;
    } // end isVerbose        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param msg 
 */
    public static void log(String msg) {        
        if (isVerbose()) {
            System.out.println("[treemaker] " + msg);
        }
    } // end log        

 } // end TreeMaker



