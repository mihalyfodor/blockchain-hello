/**
 * 
 */
package com.github.mihalyfodor.blockchain;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

/**
 * A Blockchain is made up of a chain of blocks. Each block has its own digital
 * signature, a hash in this case and also knows the hash of the previous block.
 * Besides this strucutre any data can be stored on a block. That will be a
 * simple string from now.
 * 
 * @author Mihaly Fodor
 */
public class Block {
	
	/**
	 * Leading zeroes used for verifying proof of work.
	 */
	public static String LEADING_ZEROES = "00000";

	/**
	 * Digital signature of the block.
	 */
	private String hash;

	/**
	 * Digital signature of the previous block.
	 */
	private String previousHash;

	/**
	 * Data we want to store on the block. Will be coins later :)
	 */
	private String data;
	
	/**
	 * Timestamp of when the block was created. Used in generating the digital signature.
	 */
	private long timestamp;
	
	/**
	 * Small flag we use to generate a different hash when mining.
	 */
	private int delta;

	public Block(String data, String previousHash) {
		super();
		this.previousHash = previousHash;
		this.data = data;
		this.timestamp = System.currentTimeMillis();
		this.setHash(calculateHash());
	}

	/**
	 * One way of generating a digital signature is using a SHA-256 algorithm. Instead of implementing one
	 * ourselves, we are using the one from google. 
	 * 
	 * The new signature is based on the previous hash, the data, and the timestamp of the block's creation.
	 * 
	 * @return
	 */
	public String calculateHash() {
		return Hashing.sha256().hashString(previousHash + data + timestamp + delta, StandardCharsets.UTF_8)
				.toString();
	}
	
	/**
	 * Mining a Block using Proof of Work.
	 * 
	 * Essentially proof of work is solving a problem to create a new block. We can understand that as
	 * generating a new hashcode, still based on the basic fields, until we get one that has a given
	 * number of leading zeroes.
	 */
	public void mineBlock() {
		System.out.println("Mining block ");
		while(!hash.substring( 0, LEADING_ZEROES.length()).equals(LEADING_ZEROES)) {
			delta ++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
		
	}
	
	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the previousHash
	 */
	public String getPreviousHash() {
		return previousHash;
	}

	/**
	 * @param previousHash the previousHash to set
	 */
	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
	

}
