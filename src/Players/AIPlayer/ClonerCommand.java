package Players.AIPlayer;

/**
 * A specification for the implementation of procedures to implement a cloning
 * operation for a new class. It is an alternative to the not-so-useful
 * Cloneable interface in java.lang.
 * The class that the implementor of iClone can clone is not specified
 * here; everything is cast to or from Object. The class is identified
 * when the ClonerCommand implementor is added to Cloner's
 * SUPPORTED_CLASSES.
 *
 * Author's Note: Because iClone does not know the types of the components
 * it is copying, and because Cloner keeps data structures of known clonable
 * and immutable types, it is impossible to type-parameterize this class.
 *
 * @author James Heliotis
 */
public interface ClonerCommand {
    /**
     * Perform a deep copy of an object of a certain type.
     * This is a convenience method that defaults the second argument of the
     * abstract method of this same name.
     * @param original the object to be copied
     * @return a deep copy of the original
     */
    default Object iClone(Object original) {
        return iClone(original, true);
    }

    /**
     * Perform a copy of an object of a certain type.<br/>
     * Implementation tip: deep => recursively call Cloner.deepCopy on the
     * components of the type, otherwise just add the components directly
     * to your new data structure.
     * @param original the object to be copied
     * @param deep true if a deep copy is requested, otherwise a shallow copy
     * @return a deep or shallow copy of the original
     */
    Object iClone(Object original, boolean deep);
}