/*
 * ChainedHashTable.java
 *
 * Computer Science 112, Boston University
 * 
 * Modifications and additions by:
 *     name:
 *     email:
 */

import java.util.*;     // to allow for the use of Arrays.toString() in testing

/*
 * A class that implements a hash table using separate chaining.
 */
public class ChainedHashTable implements HashTable {
    /* 
     * Private inner class for a node in a linked list
     * for a given position of the hash table
     */
    private class Node {
        private Object key;
        private LLQueue<Object> values;
        private Node next;
        
        private Node(Object key, Object value) {
            this.key = key;
            values = new LLQueue<Object>();
            values.insert(value);
            next = null;
        }
    }
    
    private Node[] table;      // the hash table itself
    private int numKeys;       // the total number of keys in the table
        
    /* hash function */
    public int h1(Object key) {
        int h1 = key.hashCode() % table.length;
        if (h1 < 0) {
            h1 += table.length;
        }
        return h1;
    }
    
    /*** Add your constructor here ***/
    public ChainedHashTable(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException();
        }
        table = new Node[size];
    }
    
    /*
     * insert - insert the specified (key, value) pair in the hash table.
     * Returns true if the pair can be added and false if there is overflow.
     */
    public boolean insert(Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int i = h1(key);

        if (table[i] == null) {            // if position i is empty
            table[i] = new Node(key, value);
            numKeys++;
        } else {
            if (key.equals(table[i].key)) {      // check if the key is a duplicate to the first key in the chain at position i
                table[i].values.insert(value);
            } 
            else {
                Node trav = table[i];        // traverse through the chain to add a new node
                while (trav != null && !key.equals(trav.key)) {
                    trav = trav.next;
                }
                if (trav != null) { // if there is a duplicate, add the values, do not add a new Node
                    trav.values.insert(value);
                } else {
                    Node newNode = new Node(key,value);
                    newNode.next = table[i];
                    table[i] = newNode;
                    numKeys++;
                }
            }
            
        } 
        return true;
    }
    
    /*
     * search - search for the specified key and return the
     * associated collection of values, or null if the key 
     * is not in the table
     */
    public Queue<Object> search(Object key) {
        int i = h1(key);

        if (table[i] == null) {
            return null;
        } else {
            Node trav = table[i];
            while (trav != null && !key.equals(trav.key)) {
                trav = trav.next;
            }
            if (trav == null) {
                return null;
            } else {
                return trav.values;
            }

        }
    }
    
    /* 
     * remove - remove from the table the entry for the specified key
     * and return the associated collection of values, or null if the key 
     * is not in the table
     */
    public Queue<Object> remove(Object key) {
        int i = h1(key);

        if (table[i] == null) { // position is empty, the key is not found
            return null;
        }
        else {
            Node trav = table[i];
            if (key.equals(trav.key)) {     // the key is found as the first key in position i
                table[i] = trav.next;
                numKeys--;
                return trav.values;
            } 
            else {
                Node prev = null;
                while (trav != null && !key.equals(trav.key)) {
                    prev = trav;
                    trav = trav.next;
                }
                if (trav == null) { // the key is not found after traversal 
                    return null; 
                } else {
                    prev.next = trav.next;  
                    numKeys--;
                    return trav.values;
    
                }

            }   
        }
    }
    
    
    /*** Add the other required methods here ***/
    public int getNumKeys() {
        return numKeys;
    }
    
    public double load() {
        return (double)(numKeys)/ (double)(table.length);
    }

    public Object[] getAllKeys() {
        Object[] result = new Object[numKeys];
        int arrIndex = 0;
        for (int i = 0; i < table.length; i ++) {
            if (table[i] != null) {
                Node trav = table[i];
                while (trav != null) {
                    result[arrIndex] = trav.key;
                    arrIndex++;
                    trav = trav.next;
                }
            }
        }
        return result;
    }
    
    public void resize(int newSize) {
        if (newSize < table.length) {
            throw new IllegalArgumentException();
        } 
        else if (newSize == table.length) {
            return;
        }

        ChainedHashTable resizeTable = new ChainedHashTable(newSize);
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                Node trav = table[i];
                while (trav != null) {
                    resizeTable.insert(trav.key, trav.values);
                    trav = trav.next;
                }
            }
        }
        table = resizeTable.table; 
    }
    
    /*
     * toString - returns a string representation of this ChainedHashTable
     * object. *** You should NOT change this method. ***
     */
    public String toString() {
        String s = "[";
        
        for (int i = 0; i < table.length; i++) {
            if (table[i] == null) {
                s += "null";
            } else {
                String keys = "{";
                Node trav = table[i];
                while (trav != null) {
                    keys += trav.key;
                    if (trav.next != null) {
                        keys += "; ";
                    }
                    trav = trav.next;
                }
                keys += "}";
                s += keys;
            }
        
            if (i < table.length - 1) {
                s += ", ";
            }
        }       
        
        s += "]";
        return s;
    }


    public static void main(String[] args) {
        /** Add your unit tests here 

        ChainedHashTable table = new ChainedHashTable(5);
        table.insert("howdy", 15);
        table.insert("goodbye", 10);
        System.out.println(table.insert("apple", 5));
        table.insert("apple", 10);
        System.out.println(table.remove("goodbye"));
        System.out.println(table.search("howdy"));
        System.out.println(table.search("apple"));
        System.out.println(table.search("goodbye"));

        System.out.println(table);
        
        ChainedHashTable table = new ChainedHashTable(5);
        table.insert("howdy", 15);
        table.insert("goodbye", 10);
        table.insert("apple", 5);
        System.out.println(table.getNumKeys());
        table.insert("howdy", 25);     // insert a duplicate
        System.out.println(table.getNumKeys());
        
        ChainedHashTable table = new ChainedHashTable(5);
        table.insert("howdy", 15);
        table.insert("goodbye", 10);
        table.insert("apple", 5);
        System.out.println(table.load());
        table.insert("pear", 6);
        System.out.println(table.load());
        
        ChainedHashTable table = new ChainedHashTable(5);
        table.insert("howdy", 15);
        table.insert("goodbye", 10);
        System.out.println(table.insert("apple", 5));
        System.out.println(table);
        

        ChainedHashTable table = new ChainedHashTable(5);
        table.insert("howdy", 15);
        table.insert("goodbye", 10);
        table.insert("apple", 5);
        table.insert("howdy", 25);    // insert a duplicate
        Object[] keys = table.getAllKeys();
        System.out.println(Arrays.toString(keys));
        

        ChainedHashTable table = new ChainedHashTable(5);
        table.insert("howdy", 15);
        table.insert("goodbye", 10);
        table.insert("apple", 5);
        System.out.println(table);
        table.resize(7);
        System.out.println(table);
        **/

        System.out.println("--- Testing method insert() ---");
        System.out.println();
        System.out.println("(0) Testing on insert()");
        System.out.println();
        
        try {
            ChainedHashTable table = new ChainedHashTable(5);
            table.insert("strawberry", 15);
            table.insert("apple", 5);
            table.insert("banana",10);
            String results = table.toString();
            String expected = "[{apple}, null, null, {banana; strawberry}, null]";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected)); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();

        System.out.println("(1) Testing on insert()");
        System.out.println();

        try {
            ChainedHashTable table = new ChainedHashTable(3);
            table.insert("latte", 15);
            table.insert("americano", 5);
            table.insert("americano",10);
            String results = table.toString();
            String expected = "[null, {latte}, {americano}]";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected)); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();

        System.out.println("--- Testing method remove() ---");
        System.out.println();
        System.out.println("(0) Testing on remove()");
        System.out.println();

      
        try {
            ChainedHashTable table = new ChainedHashTable(4);
            table.insert("piano", 15);
            table.insert("violin", 5);
            table.insert("cello",10);
            table.insert("violin",10);
            String results_values = table.remove("violin").toString();
            String expected_values = "{5, 10}";
            System.out.println("actual results returned:");
            System.out.println(results_values);
            System.out.println("expected results returned:");
            System.out.println(expected_values);
            String results_table = table.toString();
            String expected_table = "[null, {cello; piano}, null, null]";
            System.out.println("actual results table:");
            System.out.println(results_table);
            System.out.println("expected results table:");
            System.out.println(expected_table);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results_values.equals(expected_values)); 
            System.out.println(results_table.equals(expected_table));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();
        System.out.println("(1) Testing on remove()");
        System.out.println();


        try {
            ChainedHashTable table = new ChainedHashTable(7);
            table.insert("noodles", 20);
            table.insert("pizza", 10);
            table.insert("pizza", 15);
            table.insert("pizza",25);
            String results_values = table.remove("pizza").toString();
            String expected_values = "{10, 15, 25}";
            System.out.println("actual results returned:");
            System.out.println(results_values);
            System.out.println("expected results returned:");
            System.out.println(expected_values);
            String results_table = table.toString();
            String expected_table = "[null, null, null, null, {noodles}, null, null]";
            System.out.println("actual results table:");
            System.out.println(results_table);
            System.out.println("expected results table:");
            System.out.println(expected_table);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results_values.equals(expected_values)); 
            System.out.println(results_table.equals(expected_table));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();

        System.out.println("--- Testing method search() ---");
        System.out.println();
        System.out.println("(0) Testing on search()");
        System.out.println();

        try {
            ChainedHashTable table = new ChainedHashTable(7);
            table.insert("happy", 20);
            table.insert("sad", 10);
            table.insert("angry", 15);
            table.insert("happy",25);
            String results = table.search("happy").toString();
            String expected = "{20, 25}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected)); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();
        System.out.println("(1) Testing on search()");
        System.out.println();

        try {
            ChainedHashTable table = new ChainedHashTable(3);
            table.insert("math", 20);
            table.insert("biology", 10);
            table.insert("chemistry", 15);
            table.insert("history",25);
            String results = table.search("biology").toString();
            String expected = "{10}";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected)); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();

        System.out.println("--- Testing method getNumKeys() ---");
        System.out.println();
        System.out.println("(0) Testing on getNumKeys()");
        System.out.println();

        try {
            ChainedHashTable table = new ChainedHashTable(7);
            table.insert("math", 20);
            table.insert("biology", 10);
            table.insert("chemistry", 15);
            table.insert("history",25);
            table.remove("history");
            table.insert("biology", 20);
            table.insert("literature", 10);
            table.insert("math", 30);
            table.remove("math");
            int results = table.getNumKeys();
            int expected = 3;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results==expected); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();
        System.out.println("(1) Testing on getNumKeys()");
        System.out.println();

        try {
            ChainedHashTable table = new ChainedHashTable(4);
            table.insert("volleyball", 20);
            table.insert("basketball", 10);
            table.insert("tennis", 15);
            table.insert("jogging",25);
            table.remove("jogging");
            table.insert("tennis", 20);
            int results = table.getNumKeys();
            int expected = 3;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results==expected); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println("--- Testing method getAllKeys() ---");
        System.out.println();
        System.out.println("(0) Testing on getAllKeys()");
        System.out.println();


        try {
            ChainedHashTable table = new ChainedHashTable(8);
            table.insert("australia", 20);
            table.insert("america", 10);
            table.insert("belgium", 15);
            table.insert("thailand",25);
            table.insert("dubai", 20);
            String results = Arrays.toString(table.getAllKeys());
            String expected = "[australia, dubai, belgium, america, thailand]";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected)); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);

        }
        
        System.out.println();
        System.out.println("(1) Testing on getAllKeys()");
        System.out.println();


        try {
            ChainedHashTable table = new ChainedHashTable(7);
            table.insert("pepsi", 20);
            table.insert("coke", 10);
            table.insert("lemonade", 15);
            table.insert("coffee",25);
            String results = Arrays.toString(table.getAllKeys());
            String expected = "[coffee, lemonade, coke, pepsi]";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected)); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);

        }

        System.out.println();

        System.out.println("--- Testing method load() ---");
        System.out.println();
        System.out.println("(0) Testing on load()");
        System.out.println();

        try {
            ChainedHashTable table = new ChainedHashTable(7);
            table.insert("pepsi", 20);
            table.insert("coke", 10);
            table.insert("lemonade", 15);
            table.insert("coffee",25);
            table.insert("juice", 20);
            double results = table.load();
            double expected = 5.0/7.0;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results==expected); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();
        System.out.println("(1) Testing on load()");
        System.out.println();

        try {
            ChainedHashTable table = new ChainedHashTable(3);
            table.insert("library", 20);
            table.insert("dorm", 10);
            table.insert("kitchen", 15);
            table.insert("bedroom",25);
            table.insert("classroom", 20);
            double results = table.load();
            double expected = 5.0/3.0;
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results==expected); 
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();

        System.out.println("--- Testing method resize() ---");
        System.out.println();
        System.out.println("(0) Testing on resize()");
        System.out.println();


        try {
            ChainedHashTable table = new ChainedHashTable(3);
            table.insert("library", 20);
            table.insert("dorm", 10);
            table.insert("kitchen", 15);
            table.insert("bedroom",25);
            table.insert("classroom", 20);
            System.out.println("table before resize: ");
            System.out.println(table);
            table.resize(7);
            String results = table.toString();
            String expected = "[{kitchen}, null, null, null, null, {classroom}, {library; dorm; bedroom}]";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }

        System.out.println();
        System.out.println("(0) Testing on resize()");
        System.out.println();


        try {
            ChainedHashTable table = new ChainedHashTable(4);
            table.insert("book", 20);
            table.insert("phone", 10);
            table.insert("mirror", 15);
            table.insert("table",25);
            table.insert("desk", 20);
            System.out.println("table before resize: ");
            System.out.println(table);
            table.resize(9);
            String results = table.toString();
            String expected = "[null, null, null, {mirror}, {book}, null, {desk}, {phone; table}, null]";
            System.out.println("actual results:");
            System.out.println(results);
            System.out.println("expected results:");
            System.out.println(expected);
            System.out.print("MATCHES EXPECTED RESULTS?: ");
            System.out.println(results.equals(expected));
        } catch (Exception e) {
            System.out.println("INCORRECTLY THREW AN EXCEPTION: " + e);
        
        }







        






    }
}
