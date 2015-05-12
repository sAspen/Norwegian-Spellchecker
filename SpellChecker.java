import java.io.*;
import java.math.*;
import java.util.Scanner;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.text.DecimalFormat;

/********************************************************************************
 *A class used for spellchecking. This instance is customized for (a subset of) 
 *the Norwegian language, 
 *but the class can theoretically be used with any written language. 
 *Its dictionary has the structure of a binary search tree with a hash table. 
 *******************************************************************************/
class SpellChecker {

    private File f; 
    private BST dict = new BST(); //The dictionary
    char[] alphabet = 
		"abcdefghijklmnopqrstuvwxyzæøå".toCharArray(); //The Norwegian alphabet

	
	/****************************************************************************
	 *Builds the dictionary and prints out various statistics about it.
	 *@param filename the filename of the file containing the words that will 
	 *compose the dictionary.
	 ***************************************************************************/
    SpellChecker(String filename) {
		f = new File(filename);

		try {
			Scanner s = new Scanner(f);
			String word = "";

			//Reads a word and adds it and its hash value to the dictionary, 
			//until the end of the file is reached.
			while(s.hasNext())
				{
					word = s.next();
					dict.add(word);
					dict.hashValues[dict.hash(word)] = true;
				}
		} catch (Exception e) {
			System.out.println("Oops. Something went wrong\n" + e.getMessage());
		}
	
		dict.remove("familie");
		dict.add("familie");

		System.out.println("Depth of the tree: " + dict.depth());
		System.out.println("Nodes per depth: ");
	
		int[] DOAN = dict.depthOfAllNodes();

		for (int i = 0; i < DOAN.length; i++) {
			System.out.println("- Depth of " + (i + 1) + ": " + DOAN[i] + 
							   " nodes");
		}

		//Prints the average depth of the dictionary with two decimal places.
		System.out.println("Average depth of nodes: " + 
						   (new DecimalFormat("#0.00")).
						   format(dict.averageDepth())); 
		System.out.println("First word in dictionary: " + dict.min());
		System.out.println("Last word in dictionary: " + dict.max());
		System.out.println("Hash values generated: " + 
						   dict.numberOfHashValues());

		int[] HVFR = dict.hashValuesFillRate(30);
	
		System.out.println("Fill rate of hash table at intervals of 30: ");

		for (int j = 0; j < HVFR.length; j++) {
			System.out.println("Intervals with fill rate of " + j + ": " + 
							   HVFR[j]);
		}
    }

	/*****************************************************************
	 *Generates strings similar to the string parameter, 
	 *where two characters next to each other have been swapped.
	 *The amount of the strings generated is equal to the length of the 
	 *string minus one.
	 *@param s the string that will be used to generate similar strings.
	 *@return a string array containing the generated strings
	 ******************************************************************/
    public String[] similar1(String s) {
		char[] word = s.toCharArray();
		char[] temp;
		String[] words = new String[word.length - 1];
	
		for (int i = 0; i < words.length; i++) {
			temp = word.clone();
			words[i] = swap(i, i + 1, temp);
		}

		return words;
    }
	
	/********************************************************
	 *Swaps two characters next to each other in a string.
	 *@param a the first character's position in the string.
	 *@param a the second character's position in the string.
	 *@param word the string in question.
	 *@return returns the string with the swapped characters.
	 ********************************************************/
    public String swap(int a, int b, char[] word) {
		char temp = word[a];
		word[a] = word[b];
		word[b] = temp;
	
		return new String(word);
    }

	/*****************************************************************
	 *Generates strings similar to the string parameter, 
	 *where one character has been replaced with another from the alphabet.
	 *The amount of the strings generated is equal to the sum of the product of 
	 *the length of the string times the length of the alphabet being subtracted
	 *by the length of the string.
	 *@param s the string that will be used to generate similar strings.
	 *@return a string array containing the generated strings
	 ******************************************************************/
    public String[] similar2(String s) {
		String[] words = new String[(s.length() * alphabet.length) - s.length()];
		char[] temp;
		String temp2;
		int k = 0;

		for (int i = 0; i < s.length(); i++) {
			temp = s.toCharArray();
		
			for (int j = 0; j < alphabet.length; j++) {
				temp[i] = alphabet[j];
				temp2 = new String(temp);
		
				if (!(temp2.equals(s))) words[k++] = (new String(temp2)); 
			}
		}

		return words;
    }

	/*****************************************************************
	 *Generates strings similar to the string parameter, 
	 *where one character is missing.
	 *The amount of the strings generated is equal to the length of the string.
	 *@param s the string that will be used to generate similar strings.
	 *@return a string array containing the generated strings
	 ******************************************************************/
    public String[] similar3(String s) {
		String[] words = new String[s.length()];
		char[] temp1;
		char[] temp2 = s.toCharArray();
	
		for (int i = 0; i < s.length(); i++) {
			temp1 = new char[s.length() - 1];
	    
			for (int j = 0; j < temp1.length; j++) {
				if (i != j) {
					temp1[j] = temp2[j];
				}
			}
	    
			words[i] = new String(temp1);;
		}
	
		return words;
    }

	/*****************************************************************
	 *Generates strings similar to the string parameter, 
	 *where one character from the alphabet is added at the start, at the end, 
	 *or somewhere in between.
	 *The amount of the strings generated is equal to the product of the length 
	 *of the string times the length of the alphabet.
	 *@param s the string that will be used to generate similar strings.
	 *@return a string array containing the generated strings
	 ******************************************************************/
    public String[] similar4(String s) {
		String[] words = new String[s.length() * alphabet.length];
		char[] temp1;
		char[] temp2 = s.toCharArray();
		int m = 0;

		for (int i = 0; i < s.length(); i++) {
			temp1 = new char[s.length() + 1];

			for (int j = 0; j != i; j++) {
				temp1[j] = temp2[j];
			}
	
			for(int k = 0; k < alphabet.length; k++) {
				temp1[i] = alphabet[k];

				for(int l = i + 1; l < s.length(); l++) {
					temp1[l] = temp2[l - 1];
				} 

				words[m++] = new String(temp1);
			}
		}

		return words;
    }

	/*******************************************
	 *Runs a string through the spellcheck process.
	 *It first checks if its hash value is in the dictionary, then it checks if 
	 *it is, in fact, in the dictionary.
	 *If it is, then it gives positive feedback. If not, then it generates 
	 *possible solutions.
	 *@param s the string to be spellchecked.
	 ********************************************/
    void spellCheck(String s) {
		if (dict.hashValues[dict.hash(s)]) {
			if (dict.contains(s)) System.out.println("The word \"" + s + 
													 "\" is spelled correctly.");
			else {
				System.out.println("The word \"" + s + 
								   "\" is not spelled correctly.");
				generateWords(s);
			}
		} else {
			System.out.println("The word \"" + s + 
							   "\" is not spelled correctly.");
			generateWords(s);
		} 
    }

	/***************************************************
	 *Generates possible solutions for a misspelled word.
	 *See the similar1(), similar2(), similar3(), similar4() methods for 
	 *more details.
	 *@param s the misspelled word.
	 ****************************************************/
    public void generateWords(String s) {
	
		//stats[0]: The number of generated solutions that were found in the 
		//dictionary.
		//stats[1]: The number of generated solutions that were not in the 
		//dictionary, but their hash values was in the hash table.
		//stats[2]: The number of generated solutions that were not found in the
		//hash table.
	
		int[] stats = new int[3];
		long start, end; //Used to keep track of how much time the process took.

		System.out.println("Suggestions:");

		start = System.currentTimeMillis();
	
		checkWords(similar1(s), stats); 
		checkWords(similar2(s), stats);
		checkWords(similar3(s), stats);
		checkWords(similar4(s), stats);

		end = System.currentTimeMillis();

		System.out.println("Suggestions found: " + stats[0]);
		System.out.println("Invalid suggestions found: " + stats[1]);
		System.out.println("Non-words found: " + stats[2]);
		System.out.println("Time used to find suggestions: " + 
						   ((end - start) / 1000F) + " seconds");
    }

	/***************************************************************************
	 *Checks if the hash values of generated strings are in the hash table, and
	 *then if they are, checks if the strings are in the dictionary itself.
	 *Also keeps track of the number of strings that were in the dictionary when
	 *their hash values were in the hash table, 
	 *the number of strings that were not in the dictionary itself, but their 
	 *hash values were in the hash table, 
	 *and the number of strings whose hash values were not in the 
	 *hash table at all.
	 *@param words the generated strings to be checked
	 *@param stats the array holding the numbers previously described.
	 ***************************************************************************/
    public void checkWords(String[] words, int[] stats) {
		for (int i = 0; i < words.length; i++) {
			if (dict.hashValues[dict.hash(words[i])]) {
				if (dict.contains(words[i])) {
					System.out.println("- " + words[i]);
					stats[0]++;
				} else stats[1]++;
			} else stats[2]++;
		}
    }

	/******************************************
	 *The main method of the SpellChecker class.
	 ******************************************/
    public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("USAGE: java SpellChecker ordbok_???.txt");
			System.exit(0);
		}

		SpellChecker grammarNazi = new SpellChecker(args[0]);

		System.out.println("***************************************\n" +
						   "Welcome to Norwegian SpellChecker r9k!\n" + 
						   "***************************************\n");
	
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s = ""; 
	
		while (true) {
			System.out.print("Please enter a word to be spellchecked" + 
							 "(enter 'q' to quit): \n>");
		
			try {
				s = in.readLine().toLowerCase();
			} catch (Exception e) {
				System.out.println("Oops. Something went wrong\n" + 
								   e.getMessage());
			}		

			if (s.equals("q")) {
				System.out.println("Norwegian SpellChecker r9k signing off!");
				System.exit(1);
			}
		
			if (!(s.equals(""))) {
				char[] c = s.toCharArray();
				boolean b = false;
				for (int i = 0; i < c.length; i++) {
					if (!(Character.isLetter(c[i]))) {
						b = true;
						break;
					}
				}
				if (!b) grammarNazi.spellCheck(s);
				else System.out.println("Oops. That wasn't a word.");
			}
		}
    }
}
