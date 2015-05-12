import java.io.*;
import java.math.*;
import java.util.Scanner;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.text.DecimalFormat;

/*****************************************************
 *The binary search tree class, with a hash table.
 *This instance has been modified to only use strings.
 ******************************************************/
class BST {
    private BSTNode root;
    protected boolean[] hashValues = new boolean[150000];
		
    class BSTNode {
		BSTNode left,right, parent;
		String word;
		BSTNode(String s) {
			word = s;
			parent = null;
		}

		/*******************************************************
		 *Used recursively to traverse through the tree to 
		 *find the appropriate spot for a new object. 
		 *May also be used on a subtree.
		 *@param x the object to be added to the tree.
		 ******************************************************/
		protected void add(BSTNode x) {
			if (x.word.compareTo(word) < 0) {
				if (left == null) {
					left = x;
					left.parent = this;
				} else left.add(x);
			} else {
				if (right == null) {
					right = x;
					right.parent = this;
				} else right.add(x);
			}
		}

		/***********************************************************************
		 *Used recursively to traverse through the tree to search for an object. 
		 *May also be used on a subtree.
		 *@param s the object to search for.
		 *@return true if the object was found. False if the object has not been 
		 *found and the method has reached the end of the tree.
		 ***********************************************************************/
		protected boolean find(String s) {
			if (s.compareTo(word) == 0) return true;
			else if (s.compareTo(word) < 0) {
				if (left == null) return false;
				else return left.find(s);
			} else if (right == null) return false; 
			else return right.find(s);
		}
	
		/************************************************************************
		 *Used recursively to traverse through the entire tree and count 
		 *the nodes. 
		 *May also be used on a subtree.
		 *@return the number of nodes for this node's subtrees plus itself.
		 ***********************************************************************/
		public int count() {
			int i = 0;

			if(left != null) i += left.count();
	    
			i++;
	
			if(right != null) i += right.count();

			return i;
		}
		/************************************************************************
		 *Used recursively to traverse through the tree to find the node 
		 *containing the specified object.
		 *May also be used on a subtree.
		 *@param s the object to find.
		 *@return the desired object.
		 ***********************************************************************/
		protected BSTNode getNode(String s) {
			if (s.compareTo(word) == 0) return this;
			else if (s.compareTo(word) < 0) return left.getNode(s);
			else return right.getNode(s);
		}
	
		/************************************************************************
		 *Used recursively to traverse through the tree to find the data with 
		 *the lowest value.
		 *May also be used on a subtree.
		 *@return the data with the lowest value
		 ***********************************************************************/
		public String getMin() {
			if (left == null) return word;
			else return left.getMin();
		}

		/************************************************************************
		 *Used recursively to traverse through the tree to find the data with 
		 *the highest value.
		 *May also be used on a subtree.
		 *@return the data with the lowest value
		 ***********************************************************************/
		public String getMax() {
			if (right == null) return word;
			else return right.getMax();
		}

		/************************************************************************
		 *Used recursively to traverse through the tree to find and remove a 
		 *node with the specified data.
		 *May also be used on a subtree.
		 *@param s the data to be removed from the tree.
		 ***********************************************************************/
		protected void remove(String s) {
			if (s.compareTo(word) < 0) {
				if (left != null) left.remove(s);
			} else if (s.compareTo(word) > 0) {
				if (right != null) right.remove(s);
			} else {
				if (left != null && right != null) {
					word = right.getMin();
					right.remove(word);
				} else if (parent.left == this)  {
					parent.left = (left != null) ? left : right;
				} else if (parent.right == this) {
					parent.left = (left != null) ? left : right;
				}
			}
		}
	
		/***********************************************************************
		 *Used recursively to traverse through the tree to find the greatest 
		 *depth of the tree.
		 *May also be used on a subtree.
		 ***********************************************************************/
		public int getGreatestDepth() {
			int leftDepth = 0; 
			int rightDepth = 0;

			if (left == null && right == null) return 1;

			if (left != null) leftDepth = left.getGreatestDepth() + 1;
			else leftDepth = 0;
	    
			if (right != null) rightDepth = right.getGreatestDepth() + 1;
			else rightDepth = 0;

			return (leftDepth > rightDepth) ? leftDepth : rightDepth;
		}
	
		/************************************
		 *Finds this node's depth in the tree.
		 *@return this node's depth
		 *************************************/
		public int depth() {
			int i = 1;
			BSTNode temp = parent;
	
			while (temp != null) {
				temp = temp.parent;
				i++;
			}
	
			return i;
		}
    }

	/**********************************************************
	 *Generates a hash value from a string parameter.
	 *@param s the string that will be used to generate a hash value.
	 *@return the generated hash value.
	 ***********************************************************/
    public int hash(String s) {
		int hashVal = 1;
	
		for (int i = 0; i < s.length(); i++) {
			hashVal = 37 * hashVal + (int) s.charAt(i);
		}
	
		return Math.abs(hashVal % hashValues.length);
    } 
	
	/************************************************
	 *Adds a node with the specified data to the tree.
	 *@param s the data to be added
	 *************************************************/
    public void add(String s) {
		BSTNode x = new BSTNode(new String(s));
		if (root == null) {
			root = x;
		} else root.add(x);
    }
    
	/************************************************
	 *Checks if the tree contains the specified data
	 *@param s the data to be searched for.
	 *@return true if the data is in the tree, false if not.
	 *************************************************/
    public boolean contains(String s) {
		if (s.compareTo(root.word) == 0) return true;
		else return root.find(s);
    }
	
	/****************************************
	 *Removes the node with the specified data
	 *@param s the data to be removed
	 ******************************************/
    public void remove(String s) {
		if (root != null) {
			if (s.compareTo(root.word) == 0) {
				BSTNode temp = new BSTNode("");
				temp.left = root;
				root.parent = temp;
				root.remove(s);
				root = temp.left;
			} else root.remove(s);
		}
		hashValues[hash(s)] = false;
		assertHashValues();
    }
	
	/***************************************************************************
	 *Reconfirms the hash values of the data in the tree to be present in the 
	 *hash table.
	 *Used because the remove() method runs a risk of removing a common hash 
	 *value between two or more words.
	 ***************************************************************************/
	private void assertHashValues() {
		Iterator<BSTNode> iter = iterator();
		BSTNode temp = null;
	
		while (iter.hasNext()) {
			temp = iter.next();
			hashValues[hash(temp.word)] = true;
		} 
	}
	
	/*******************************
	 *Gets the depth of the tree.
	 *@return the depth of the tree
	 *******************************/
    public int depth() {
		return root.getGreatestDepth();
    }
	
	/*******************************************************
	 *Gets the frequency of nodes at each depth in the tree.
	 *@return an int array with the frequencies.
	 *******************************************************/
	public int[] depthOfAllNodes() {
		int[] DOAN = new int[depth()];
		Iterator<BSTNode> iter = iterator();
		BSTNode temp = null;
		int i = 0;

		while (iter.hasNext()) {
			temp = iter.next();
			DOAN[temp.depth() - 1]++;
		} 
	
		return DOAN;
    }
	
	/******************************************
	 *Gets the number of nodes in the tree.
	 *@return the number of nodes in the tree.
	 ******************************************/
    public int size() {
		return root.count();
    }	
	
	/*******************************************************
	 *Calculates the average depth of all nodes in the tree.
	 *@return the average depth of all nodes in the tree.
	 ********************************************************/
    public double averageDepth() {
		double avrg = 0;
		int[] DOAN = depthOfAllNodes();

		for (int i = 0; i < DOAN.length; i++) {
			avrg = avrg + (DOAN[i] * (i + 1));
		}

		avrg = avrg / size();
		return avrg;
    }

	/********************************************************************
	 *Gets the data with lowest value in the tree, 
	 *which would be located in the nodest farest to the left in the tree.
	 *@return the data with lowest value in the tree
	 *********************************************************************/
    public String min() {
		return root.getMin();
    }
	
	/********************************************************************
	 *Gets the data with highest value in the tree, 
	 *which would be located in the nodest farest to the right in the tree.
	 *@return the data with highest value in the tree
	 *********************************************************************/
    public String max() {
		return root.getMax();
    }
	
	/******************************************
	 *Gets the number of unique hash values that has previously been generated.
	 *@return the number of nodes in the tree.
	 ******************************************/
    public int numberOfHashValues() {
		int NOHV = 0;

		for (int i = 0; i < hashValues.length; i++) {
			if(hashValues[i]) NOHV++;
		}

		return NOHV;
    }
	
	/**************************************************************************
	 *Calculates the fill rate of the hash table in intervals of specified size.
	 *@param intervalSize the size of the intervals
	 *@return the data with highest value in the tree
	 ***************************************************************************/
    public int[] hashValuesFillRate(int intervalSize) {
		int[] HVFR = new int[hashValues.length / intervalSize];
		int cur = 0;

		for(int i = 0; i < HVFR.length; i++) {
			for(int j = 0; j < intervalSize; j++) {
				if(hashValues[cur++]) HVFR[i]++;
			}
		}

		int[] fillRate = new int[intervalSize + 1];
 
 
		for (int k = 0; k < HVFR.length; k++) {
			fillRate[HVFR[k]]++;
		}

		return fillRate;
    }
	
	/******************************************************
	 *The iterator class for this class
	 *Uses the ConcurrentLinkedQueue class and its iterator
	 ******************************************************/
	class BSTIter implements Iterator<BSTNode> {
		ConcurrentLinkedQueue<BSTNode> list;
		Iterator<BSTNode> iter;
		BSTNode current;

		BSTIter() {
			ConcurrentLinkedQueue<BSTNode> list = 
				new ConcurrentLinkedQueue<BSTNode>();
			if(root != null) collect(root, list);
			iter = list.iterator();
		}

		public BSTNode next() {
			current = iter.next();
			return current;
		}
	
		public boolean hasNext() {
			return iter.hasNext();
		}

		public void remove() {
			iter.remove();
			BST.this.remove(current.word);
		}
	
		protected void collect(BSTNode x, ConcurrentLinkedQueue<BSTNode> list) {
			if(x.left != null) collect(x.left,list);

			list.add(x);

			if(x.right != null) collect(x.right, list);
		}
	}
	
	public Iterator<BSTNode> iterator() {
		return new BSTIter();
	}
}