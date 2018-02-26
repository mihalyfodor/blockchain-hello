/**
 * 
 */
package com.github.mihalyfodor.blockchain;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
	 * Digital signature of the block.
	 */
	private String hash;

	/**
	 * Digital signature of the previous block.
	 */
	private String previousHash;

	private List<Transaction> transactions = new ArrayList<>();
	
	/**
	 * Timestamp of when the block was created. Used in generating the digital signature.
	 */
	private long timestamp;
	
	/**
	 * Small flag we use to generate a different hash when mining.
	 */
	private int delta;

	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timestamp = System.currentTimeMillis();
		this.hash = calculateHash();
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
		return Hashing.sha256().hashString(previousHash + transactions.hashCode() + timestamp + delta, StandardCharsets.UTF_8)
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
		while(!hash.substring( 0, Blockchain.LEADING_ZEROES.length()).equals(Blockchain.LEADING_ZEROES)) {
			delta ++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
		
	}
	
	/**
	 * Add a transaction to the block and mine it. Won't work if the transaction fails when processing
	 * or we are attempting to add a transaction to the genesis block.
	 * 
	 * @param transaction the transaction we are adding.
	 * @return transaction processing and adding successful not
	 */
	public boolean addTransaction(Transaction transaction) {
		
		System.out.println("Adding transaction to block");
		
		if (transaction == null) {
			return false;
		}
		
		boolean isGenesisBlock = previousHash.equals(Blockchain.GENESIS_HASH);
		boolean transactionSuccesful = transaction.processTransaction();
		
		if ( isGenesisBlock || !transactionSuccesful ) {
			return false;
		}
		
		transactions.add(transaction);
		
		return true;
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
	 * @return the transactions
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}

	/**
	 * @param transactions the transactions to set
	 */
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
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
