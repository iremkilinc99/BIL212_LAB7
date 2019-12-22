
/*
 * Copyright 2014, Michael T. Goodrich, Roberto Tamassia, Michael H. Goldwasser
 *
 * Developed for use with the book:
 *
 *    Data Structures and Algorithms in Java, Sixth Edition
 *    Michael T. Goodrich, Roberto Tamassia, and Michael H. Goldwasser
 *    John Wiley & Sons, 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
 * Map implementation using hash table with linear probing.
 *
 * @author Eric Zamore
 * @author Michael T. Goodrich
 * @author Roberto Tamassia
 * @author Michael H. Goldwasser
 */
public class ProbeHashMap<K, V> extends AbstractHashMap<K, V> {
	private MapEntry<K, V>[] table; // a fixed array of entries (all initially null)
	private MapEntry<K, V> DEFUNCT = new MapEntry<>(null, null); // sentinel
	private int probeNumber = 0, max = 0;

	// provide same constructors as base class
	/** Creates a hash table with capacity 17 and prime factor 109345121. */
	public ProbeHashMap() {
		super();
	}

	/** Creates a hash table with given capacity and prime factor 109345121. */
	public ProbeHashMap(int cap) {
		super(cap);
	}

	/** Creates a hash table with the given capacity and prime factor. */
	public ProbeHashMap(int cap, int p) {
		super(cap, p);
	}

	/** Creates an empty table having length equal to current capacity. */
	@Override
	@SuppressWarnings({ "unchecked" })
	protected void createTable() {
		table = (MapEntry<K, V>[]) new MapEntry[capacity]; // safe cast
	}

	/** Returns true if location is either empty or the "defunct" sentinel. */
	private boolean isAvailable(int j) {
		return (table[j] == null || table[j] == DEFUNCT);
	}

	/**
	 * Searches for an entry with key equal to k (which is known to have hash value
	 * h), returning the index at which it was found, or returning -(a+1) where a is
	 * the index of the first empty or available slot that can be used to store a
	 * new such entry.
	 *
	 * @param h
	 *            the precalculated hash value of the given key
	 * @param k
	 *            the key
	 * @return index of found entry or if not found, value -(a+1) where a is index
	 *         of first available slot
	 */
	private int findSlot(int h, K k) {
		int avail = -1; // no slot available (thus far)
		int j = h; // index while scanning table
		do {
			if (isAvailable(j)) { // may be either empty or defunct
				if (avail == -1)
					avail = j; // this is the first available slot!
				if (table[j] == null)
					break; // if empty, search fails immediately
			} else if (table[j].getKey().equals(k))
				return j; // successful match
			j = (j + 1) % capacity; // keep looking (cyclically)
			probeNumber++;
			max++;
		} while (j != h); // stop if we return to the start
		return -(avail + 1); // search has failed
	}

	/**
	 * Returns value associated with key k in bucket with hash value h. If no such
	 * entry exists, returns null.
	 * 
	 * @param h
	 *            the hash value of the relevant bucket
	 * @param k
	 *            the key of interest
	 * @return associate value (or null, if no such entry)
	 */
	@Override
	protected V bucketGet(int h, K k) {
		int j = findSlot(h, k);
		if (j < 0)
			return null; // no match found
		return table[j].getValue();
	}

	/**
	 * Associates key k with value v in bucket with hash value h, returning the
	 * previously associated value, if any.
	 * 
	 * @param h
	 *            the hash value of the relevant bucket
	 * @param k
	 *            the key of interest
	 * @param v
	 *            the value to be associated
	 * @return previous value associated with k (or null, if no such entry)
	 */
	@Override
	protected V bucketPut(int h, K k, V v) {
		int j = findSlot(h, k);
		if (j >= 0) // this key has an existing entry
			return table[j].setValue(v);
		table[-(j + 1)] = new MapEntry<>(k, v); // convert to proper index
		n++;
		return null;
	}

	/**
	 * Removes entry having key k from bucket with hash value h, returning the ü *
	 * previously associated value, if found.
	 * 
	 * @param h
	 *            the hash value of the relevant bucket
	 * @param k
	 *            the key of interest
	 * @return previous value associated with k (or null, if no such entry)
	 */
	@Override
	protected V bucketRemove(int h, K k) {
		int j = findSlot(h, k);
		if (j < 0)
			return null; // nothing to remove
		V answer = table[j].getValue();
		table[j] = DEFUNCT; // mark this slot as deactivated
		n--;
		return answer;
	}

	/**
	 * Returns an iterable collection of all key-value entries of the map.
	 *
	 * @return iterable collection of the map's entries
	 */
	@Override
	public Iterable<Entry<K, V>> entrySet() {
		ArrayList<Entry<K, V>> buffer = new ArrayList<>();
		for (int h = 0; h < capacity; h++)
			if (!isAvailable(h))
				buffer.add(table[h]);
		return buffer;
	}

	public void findMisspelledWord(String str) throws Exception {
		System.out.println("kelimenin ilk hali: " + str);
		StringBuilder sbldr = new StringBuilder(str);
		String tmp = "";

		for (int i = 0; i < str.length() - 1; i++) {
			char temp = sbldr.charAt(i);
			sbldr.setCharAt(i, sbldr.charAt(i + 1));
			sbldr.setCharAt(i + 1, temp);
			tmp = sbldr.toString();
		//	System.out.println("Tmp: " + tmp);

			for (Entry<K, V> e : this.entrySet()) {
				if(e.getKey().equals(str)) {
					System.out.println("Found Output " + str + ": " + e.getKey());
					return;
				}
         		if (e.getKey().equals(tmp)) {
					System.out.println("Formatted Output " + str + ": " + e.getKey());
					return;
				}
			}

			temp = sbldr.charAt(i);
			sbldr.setCharAt(i, sbldr.charAt(i + 1));
			sbldr.setCharAt(i + 1, temp);
			//System.out.println(sbldr.toString());
			tmp = "";

		}
	}

	public static void main(String... strings) throws Exception {
		ArrayList<String> searchTxt = new ArrayList<>();
		ProbeHashMap<String, String> hashMap = new ProbeHashMap<>(37199);

		BufferedReader reader = null;
		BufferedReader reader2 = null;

		try {
			reader = new BufferedReader(new FileReader("C:\\Users\\user\\Downloads\\dictionary.txt"));
			reader2 = new BufferedReader(new FileReader("C:\\Users\\user\\Downloads\\search.txt"));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = null, line2 = null;
		int count = 0, j = 0;
		while ((line = reader.readLine()) != null) {
			hashMap.put(line, null);
			count++;
		}

		while ((line2 = reader2.readLine()) != null) {
			searchTxt.add(j++, line2);
		}
		for (int i = 0; i < searchTxt.size(); i++) {
			for (Entry<String, String> e : hashMap.entrySet()) {
				if(hashMap.bucketGet(0, e.getKey().toString())==null){
					hashMap.findMisspelledWord(searchTxt.get(i));
				    break;
				}
			}
		}
		
		System.out.println("size: " + count);
		System.out.println("table's capacity: " + hashMap.capacity);
		hashMap.findSlot(99, "best");
		System.out.println("Average Number of Probes: " + hashMap.probeNumber / (double) hashMap.capacity);
		System.out.println("Max Number of Probes: " + hashMap.max);

	}}
