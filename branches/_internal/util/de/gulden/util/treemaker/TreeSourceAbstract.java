
/** Java class "TreeSourceAbstract.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker;

import java.io.IOException;
import java.lang.Exception;
import java.lang.String;
import java.util.*;
import java.util.Properties;
import javax.swing.tree.TreeModel;

/**
 * <p>
 * 
 * </p>
 */
public abstract class TreeSourceAbstract implements TreeSource {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    protected String input; 

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
        String s = 
            "-o <output file>\n";
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
        TreeMaker.requireOption(opts, "i");
        setInput(opts.getProperty("i"));
    } // end init        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public abstract TreeModel getModel() throws Exception;

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public String getInput() {        
        return input;
    } // end getInput        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param _input 
 */
    public void setInput(String _input) {        
        input = _input;
    } // end setInput        

/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    protected String readInputFile() throws IOException {        
        String filename = getInput();
        if (filename != null) {
            java.io.FileInputStream f = new java.io.FileInputStream(filename);
            int avail = f.available();
            byte[] b = new byte[avail];
            f.read(b);
            f.close();
            return new String(b);
        } else {
            return null;
        }
    } // end readInputFile        

 } // end TreeSourceAbstract



