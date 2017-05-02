package Players.AIPlayer;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Some utilities to make duplicates of objects of specific types
 *
 * @author James Heliotis
 */
public class Cloner {

    /**
     * The known classes and primitive types that will be copied
     * through simple assignment:
     * <ul>
     *   <li>byte, Byte</li>
     *   <li>short, Short</li>
     *   <li>int, Integer</li>
     *   <li>long, Long</li>
     *   <li>char, Character</li>
     *   <li>float, Float</li>
     *   <li>double, Double</li>
     *   <li>boolean, Boolean</li>
     *   <li>String</li>
     * </ul>
     */
    @SuppressWarnings("serial" )
    public static final HashSet< Class<?> > IMMUTABLES =
            new HashSet< Class<?> >() {{
                add( Byte.class );
                add( Short.class );
                add( Integer.class );
                add( Long.class );
                add( Character.class );
                add( Float.class );
                add( Double.class );
                add( Boolean.class );
                add( String.class );
                add( Enum.class );
            }};

    /**
     * Declare a class as "immutable", meaning that, when cloned,
     * it does not need to be copied. A simple assignment of any
     * instance of this class in a shallowCopy or deepCopy operation
     * is OK.<br/>
     * Usage: Cloner.addImmutable( <i>SomeClass</i>.class );
     *
     * @param userClass the class to be declared immutable;
     */
    public static void addImmutable( Class<?> userClass ) {
        IMMUTABLES.add( userClass );
    }

    /**
     * The classes that will be copied through copying of their components.
     * Though not included here, primitive arrays are also copied this way.
     * <ul>
     *   <li>java.util.LinkedList</li>
     *   <li>java.util.ArrayList</li>
     *   <li>java.util.Stack</li>
     *   <li>java.util.HashMap</li>
     *   <li>java.util.TreeMap</li>
     *   <li>primitive arrays</li>
     * </ul>
     */
    public static final
    HashMap< Class<?>, ClonerCommand > SUPPORTED_CLASSES =
            new HashMap< Class<?>, ClonerCommand >();

    /**
     * Add a new class and its cloning code to Cloner.
     */
    public static void addSupportedClass( Class<?> cls, ClonerCommand ccmd ) {
        SUPPORTED_CLASSES.put( cls, ccmd );
    }

    /**
     * Determine if a given class is not recognized by Cloner. For Cloner
     * to not throw an exception when copying, the classes it sees must
     * be included in SUPPORTED_CLASSES (Cloner knows how to copy them)
     * or IMMUTABLES (Cloner doesn't have to copy them). Note: immutable
     * classes can be added with the addImuutable method, but one can only
     * add classes to SUPPORTED_CLASSES by implementing the copying
     * algorithm in a new ClonerCommand implementation.
     *
     * @param testClass the class to be checked for recognition
     */
    public static boolean acceptable( Class<?> testClass ) {
        return !( IMMUTABLES.contains( testClass ) ||
                SUPPORTED_CLASSES.containsKey( testClass ) );
    }

    /** The ClonerCommand class for LinkedLists */
    private static class LinkedListCloner implements ClonerCommand {
        @SuppressWarnings("unchecked" )
        @Override
        public Object iClone( Object original, boolean deep ) {
            LinkedList< Object > result = new LinkedList< Object >();
            for ( Object element: (LinkedList<Object>)original ) {
                result.add( deep ? deepCopy( element ): element );
            }
            return result;
        }
    }
    static {
        SUPPORTED_CLASSES.put( LinkedList.class, new LinkedListCloner() );
    }

    /** The ClonerCommand class for ArrayLists */
    private static class ArrayListCloner implements ClonerCommand {
        @SuppressWarnings("unchecked" )
        @Override
        public Object iClone( Object original, boolean deep ) {
            ArrayList< Object > result = new ArrayList< Object >();
            for ( Object element: (ArrayList<Object>)original ) {
                result.add( deep ? deepCopy( element ) : element );
            }
            return result;
        }
    }
    static {
        SUPPORTED_CLASSES.put( ArrayList.class, new ArrayListCloner() );
    }

    /** The ClonerCommand class for Stacks */
    private static class StackCloner implements ClonerCommand {
        { SUPPORTED_CLASSES.put( Stack.class, this ); }
        @SuppressWarnings("unchecked" )
        @Override
        public Object iClone( Object original, boolean deep ) {
            Stack< Object > result = new Stack< Object >();
            for ( Object element: (Stack<Object>)original ) {
                result.add( deep ? deepCopy( element ) : element );
            }
            return result;
        }
    }
    static {
        SUPPORTED_CLASSES.put( Stack.class, new StackCloner() );
    }

    /** The ClonerCommand class for TreeMaps */
    private static class TreeMapCloner implements ClonerCommand {
        { SUPPORTED_CLASSES.put( TreeMap.class, this ); }
        @SuppressWarnings("unchecked" )
        @Override
        public Object iClone( Object original, boolean deep ) {
            TreeMap< Object, Object > result = new TreeMap< Object, Object >();
            TreeMap< Object, Object > originalMap =
                    (TreeMap< Object, Object >)original;
            for ( Object key: originalMap.keySet() ) {
                Object value = originalMap.get( key );
                result.put( key, deep ? deepCopy( value ) : value );
            }
            return result;
        }
    }
    static {
        SUPPORTED_CLASSES.put( TreeMap.class, new TreeMapCloner() );
    }

    /** The ClonerCommand class for HashMaps */
    private static class HashMapCloner implements ClonerCommand {
        { SUPPORTED_CLASSES.put( HashMap.class, this ); }
        @SuppressWarnings("unchecked" )
        @Override
        public Object iClone( Object original, boolean deep ) {
            HashMap< Object, Object > result = new HashMap< Object, Object >();
            HashMap< Object, Object > originalMap =
                    (HashMap< Object, Object >)original;
            for ( Object key: originalMap.keySet() ) {
                Object value = originalMap.get( key );
                result.put( key, deep ? deepCopy( value ) : value );
            }
            return result;
        }
    }
    static {
        SUPPORTED_CLASSES.put( HashMap.class, new HashMapCloner() );
    }

    /**
     * Make a shallow copy of the object provided. That is, make a copy
     * at the very top level but use assignment semantics for the object's
     * components.
     * @param original the object to be copied
     * @return a new object containing all of the elements of the original
     * @exception IllegalArgumentException if the Object's type
     *     is not listed in IMMUTABLES or SUPPORTED CLASSES and
     *     is not an array
     */
    public static Object shallowCopy( Object original ) {
        Class<?> objectClass = original.getClass();
        if ( IMMUTABLES.contains( objectClass ) ) {
            return original;
        }
        else if ( objectClass.isArray() ) {
            Class<?> eltType = objectClass.getComponentType();
            int length = Array.getLength( original );
            Object copy = Array.newInstance( eltType, length );
            for ( int i = 0; i < length; ++i ) {
                Array.set( copy, i, Array.get( original, i ) );
            }
            return copy;
        }
        else if ( SUPPORTED_CLASSES.containsKey( objectClass ) ) {
            return SUPPORTED_CLASSES.get( objectClass )
                    .iClone( original, false );
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Make a deep copy of the object provided. That is, the copying is done
     * recursively down to the basic types.
     * @see #IMMUTABLES
     * @param original the object to be copied
     * @return a precise copy of all of the elements of the object
     * @exception IllegalArgumentException if the Object's type
     *     is not listed in IMMUTABLES or SUPPORTED CLASSES and
     *     is not an array
     */
    public static Object deepCopy( Object original ) {
        Class<?> objectClass = original.getClass();
        if ( IMMUTABLES.contains( objectClass ) || objectClass.isEnum() ) {
            return original;
        }
        else if ( objectClass.isArray() ) {
            Class<?> eltType = objectClass.getComponentType();
            int length = Array.getLength( original );
            Object copy = Array.newInstance( eltType, length );
            for ( int i = 0; i < length; ++i ) {
                Array.set( copy, i, deepCopy( Array.get( original, i ) ) );
            }
            return copy;
        }
        else if ( SUPPORTED_CLASSES.containsKey( objectClass ) ) {
            return SUPPORTED_CLASSES.get( objectClass ).iClone( original );
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Demonstrate adding new classes to the Cloner
     *
     * @param args not used
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public static void main( String[] args ) {
        class Pair< G > {
            private G a, b;
            public Pair( G aa, G bb ) { a = aa; b = bb; }
            public void changeA( G newA ) { a = newA; }
            public G getA() { return a; }
            public String toString() { return "["+a +','+b+']'; }
        }
        addSupportedClass( Pair.class, new ClonerCommand() {
            public Object iClone( Object obj, boolean deep ) {
                Pair<?> orig = (Pair<?>)obj;
                return new Pair(
                        deep ? deepCopy( orig.a ) : orig.a,
                        deep ? deepCopy( orig.b ) : orig.b
                );
            }
        });

        class ConstPair {
            private int a, b;
            public ConstPair( int aa, int bb ) { a = aa; b = bb; }
            public String toString() { return "["+a +','+b+']'; }
        }
        addImmutable( ConstPair.class );

        Pair< Pair< ConstPair > > top = new Pair< Pair< ConstPair > >(
                new Pair< ConstPair >(
                        new ConstPair( 1, 2 ), new ConstPair( 3, 4 )
                ),
                new Pair< ConstPair >(
                        new ConstPair( 5, 6 ), new ConstPair( 7, 8 )
                )
        );

        System.out.println( "     top: " + top );

        Pair< Pair< ConstPair > > topCopy1 =
                (Pair< Pair< ConstPair > >)Cloner.shallowCopy( top );
        Pair< Pair< ConstPair > > topCopy2 =
                (Pair< Pair< ConstPair > >)Cloner.deepCopy( top );

        topCopy1.getA().changeA( new ConstPair( 100, 200 ) );

        topCopy2.changeA(
                new Pair< ConstPair >(
                        new ConstPair( 10, 20 ), new ConstPair( 30, 40 )
                )
        );

        System.out.println( "     top: " + top );
        System.out.println( "topCopy1: " + topCopy1 );
        System.out.println( "topCopy2: " + topCopy2 );
    }
}