
/**
 * @author Helena Gray
 * @version 11-29-2018
 * 
 *Task 1. DisjointSets class (15%)
 *An implementation of the DisjointSets class from the textbook
 *using union by size and path compression.
 */
import java.util.ArrayList;
import java.util.Iterator;


public class DisjointSets<T> {

	private int[] s; // the sets
	private ArrayList<Set<T>> sets; // the actual data for the sets
	private int size;

	/**
	 * @param data an ArrayList containing the data for the disjoint sets
	 */
	 
	public DisjointSets(ArrayList<T> data) {
		if(data == null){
			throw new NullPointerException();
		}
		size = data.size();
		s = new int[data.size()];
		sets = new ArrayList();
		int i = 0;
		for (T item : data) {
			Set tempSet = new Set();
			tempSet.add(item);
			sets.add(tempSet);
			s[i] = -1;
			i++;
		}
	}

	/**
	 * @param root1 the root of the first set to be joined
	 * @param root2 the root of the second set to be joined
	 * @return the root of the unioned set
	 * O(1) time
	 * */
	 
	public int union(int root1, int root2) {
		// throw IllegalArgumentException() if non-roots provided
		assertIsRoot(root1);
		assertIsRoot(root2);

		Set tempSetNewRoot;
		Set tempSet;
		// Compute the union of two sets using rank union by size
		// if two sets are equal, root1 is the new root
		if (s[root2] < s[root1]) {
			s[root2] = s[root2] + s[root1];
			s[root1] = root2;
			tempSetNewRoot = sets.get(root2);
			tempSet = sets.get(root1);
			tempSetNewRoot.addAll(tempSet);
			sets.get(root1).clear();
			// returns the new root of the unioned set
			return root2;
		} else {
			s[root1] = s[root1] + s[root2];
			s[root2] = root1;
			tempSetNewRoot = sets.get(root1);
			tempSet = sets.get(root2);
			tempSetNewRoot.addAll(tempSet);
			sets.get(root2).clear();
			// returns the new root of the unioned set
			return root1;
		}
	}

	/**
	 * @param x an item in a set whose root we want
	 * @return the root of the set
	 */
	public int find(int x) {
		assertIsItem(x);
		if (s[x] < 0) {
			return x;
		} else {
			// Find and return the root using path compression
			return s[x] = find(s[x]);
		}
	}

	/**
	 * @param root the root of the set to be returned
	 * @return the set of the root
	 * O(1) time
	 */
	public Set<T> get(int root) {
		Set testSet = sets.get(root);
		// Get all the data in the same set
		return testSet;

	}

	/**
	 * @param root the root in question
	 */
	private void assertIsRoot(int root) {
		assertIsItem(root);
		if (s[root] >= 0) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * @param x the item in question
	 */
	private void assertIsItem(int x) {
		if (x < 0 || x >= s.length) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * @param i the root of the set whose size we want returned
	 * @return the size of the set
	 */
	public int size(int i) {
		assertIsRoot(i);
		return s[i]*-1;
	}

	/**
	 * @return how many sets are in the disjoint set
	 */
	public int sizeDisjoint() {
		return size;
	}

	/**@param args command line arguments
	 * Main method
	 */
		public static void main(String[] args) {
		ArrayList<Integer> arr = new ArrayList<>();
		for (int i = 0; i < 10; i++)
			arr.add(i);

		DisjointSets<Integer> ds = new DisjointSets<>(arr);
		System.out.println(ds.find(0)); // should be 0
		System.out.println(ds.find(1)); // should be 1
		System.out.println(ds.union(0, 1)); // should be 0
		System.out.println("The size is: " + ds.size(0));
		System.out.println(ds.find(0)); // should be 0
		System.out.println(ds.find(1)); // should be 0
		System.out.println("-----");
		System.out.println(ds.find(0)); // should be 0
		System.out.println(ds.find(2)); // should be 2
		System.out.println(ds.union(0, 2)); // should be 0
		System.out.println("The size is: " + ds.size(0));
		System.out.println(ds.find(0)); // should be 0
		System.out.println(ds.find(2)); // should be 0
		System.out.println("-----");
		System.out.println("The size of 3 is: " + ds.size(3));
		System.out.println(ds.get(0)); // should be [0, 1, 2]
		System.out.println(ds.get(1)); // should be []
		System.out.println(ds.get(3)); // should be [3]
	}
}
