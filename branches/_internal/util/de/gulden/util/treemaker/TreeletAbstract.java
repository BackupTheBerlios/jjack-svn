
/** Java class "TreeletAbstract.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker;

import java.lang.Exception;
import java.lang.Object;
import java.lang.String;
import java.util.*;
import java.util.Properties;
import javax.swing.tree.TreeModel;

/**
 * <p>
 * 
 * </p>
 */
public abstract class TreeletAbstract implements Treelet {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    protected String output; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected TreeModel model; 

/**
 * <p>
 * Represents ...
 * </p>
 */
    protected boolean renderRoot = true; 

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Does ...
 * </p>
 * 
 */
    public abstract void generateTree() throws Exception;

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
            "-o <output file>\n"
          + "-noroot hide root node\n";
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
        TreeMaker.requireOption(opts, "o");
        setOutput(opts.getProperty("o"));
        String noroot = opts.getProperty("noroot");
        if (noroot != null) {
            setRenderRoot(false);
        }
    } // end init        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public String getOutput() {        
        return output;
    } // end getOutput        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _output 
 */
    public void setOutput(String _output) {        
        output = _output;
    } // end setOutput        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public TreeModel getModel() {        
        return model;
    } // end getModel        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _model 
 */
    public void setModel(TreeModel _model) {        
        model = _model;
    } // end setModel        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public boolean isRenderRoot() {        
        return renderRoot;
    } // end isRenderRoot        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _renderRoot 
 */
    public void setRenderRoot(boolean _renderRoot) {        
        renderRoot = _renderRoot;
    } // end setRenderRoot        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param tree 
 */
    protected void writeOutputFile(Object tree) throws Exception {        
        java.io.FileWriter f = new java.io.FileWriter(getOutput());
        if (tree instanceof String) {
            f.write((String)tree);
        } else {
            f.close();
            throw new Exception("INTERNAL ERROR: unsupported object type to write as Treelet output file");
        }
        f.close();
    } // end writeOutputFile        

 } // end TreeletAbstract



