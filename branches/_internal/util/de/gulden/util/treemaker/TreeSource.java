
/** Java interface "TreeSource.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package de.gulden.util.treemaker;

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
public interface TreeSource {

   ///////////////////////////////////////
  // associations



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
    public String usage();
/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param args 
 */
    public void init(Properties args) throws Exception;
/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @param input 
 */
    public void setInput(String input);
/**
 * <p>
 * Does ...
 * </p>
 * 
 * 
 * @return 
 */
    public TreeModel getModel() throws Exception;

} // end TreeSource





