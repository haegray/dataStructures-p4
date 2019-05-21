
/**
 * @author Helena Gray
 * @version 11-29-2018
 * 
 * This class represents a set of objects using a linked list implementation. 
 * This is used in DisjointSets<T> to store actual data in the same sets.

 * Task 1. Set<T> class (10%)
 */

import java.util.AbstractCollection;
import java.util.Iterator;

public class Set<T> extends AbstractCollection<T> {
	private Node head = null;
	private Node tail = null;
	private int size = 0;

	/**
	 * This class represents a node which will be used to implement the set
	 * class.
	 */
	private static class Node<T> {
		private T value;
		private Node<T> next;

		/**
		 * @param newValue
		 *            value of node
		 */
		public Node(T newValue) {
			value = newValue;
			next = null;
		}

		/**
		 * @return value of node
		 */
		public T getValue() {
			return value;
		}

		/**
		 * @param newValue
		 *            the new value of the node
		 */
		public void setValue(T newValue) {
			value = newValue;
		}

		/**
		 * @return returns the next node
		 */
		public Node getNext() {
			return next;
		}

		/**
		 * @param newNode
		 *            the new next node
		 */
		public void setNext(Node newNode) {
			next = newNode;
		}

		/**
		 * @return the string version of the node value
		 */
		public String toString() {
			return value.toString();
		}

	}

	/**
	 * Set class constructor O(1)
	 */
	public Set() {
		head = null;
		tail = head;
	}

	/**
	 * @param item
	 *            to be added to list
	 * @return if the node can be added true will be returned, else a null
	 *         pointer exception will be thrown O(1)
	 */
	public boolean add(T item) {
		if (item != null) {
			Node newEnd = new Node(item);
			if (head == null) {
				head = newEnd;
				tail = head;
			} else {
				tail.setNext(newEnd);
				tail = tail.getNext();
			}
			size++;
			return true;
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * @param other
	 *            the set to be added to the other set
	 * @return true if set is not empty or null else null pointer exception will
	 *         be thrown O(1)
	 */
	public boolean addAll(Set<T> other) {
		if (!isEmpty(other) || other != null) {
			if (this.head == null) {
				this.head = other.head;
				this.tail = other.tail;
			} else {
				this.tail.setNext(other.head);
				this.tail = other.tail;
			}
			size = size + other.size();
			return true;
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * clears the set O(1)
	 */
	public void clear() {
		head = null;
		tail = null;
	}

	/**
	 * @param other
	 *            the set to be checked
	 * @return true if the set is empty and false if it is not O(1)
	 */
	public boolean isEmpty(Set<T> other) {
		if (head == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return size of set O(1)
	 */
	public int size() {
		return size;
	}

	/**
	 * iterator for Set
	 */
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			Node<T> current = head;

			/**
			 * @return the value of the next item in the set
			 * O(1)
			 */
			public T next() {
				if (!hasNext()) {
					throw new NullPointerException("No more items!");
				}
				Node<T> oldCurrent = current;
				current = current.getNext();
				return oldCurrent.getValue();
			}

			/**
			 * @return true if there is another value in the set and false if not
			 */
			public boolean hasNext() {
				return (current != null);
			}
		};
	}

	/**
	 * @param args command line arguments
	 * Main method
	 */
	public static void main(String[] args) {

		Set testSet = new Set();
		testSet.add("A");
		testSet.add("B");
		testSet.add("C");

		Set testSet2 = new Set();
		testSet2.add("D");
		testSet2.add("E");
		testSet2.add("F");

		testSet.addAll(testSet2);
		Node value = testSet.head;
		while (value != null) {
			System.out.println("Value is: " + value.getValue());
			value = value.getNext();
		}
		Iterator it = testSet.iterator();
		System.out.println(it.next());
		System.out.println(it.next());
		System.out.println(it.next());
		System.out.println(it.next());
		System.out.println(it.next());
		System.out.println(it.next());

		try {
			System.out.println(it.next());
		} catch (NullPointerException e) {
			System.out.println("Good job!");
		}

	}
}
