/**
 * 
 */
package com.github.mihalyfodor.blockchain;

import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

/**
 * Represents a transaction sent towards a given wallet.
 * 
 * @author Mihaly Fodor
 *
 */
public class TransactionOutput {

	/**
	 * The hash of the transaction output, needed since we are keeping track of them on the
	 * blockchain.
	 */
	private String id;
	
	/**
	 * Who we are sending coins to.
	 */
	private String recipient;
	
	/**
	 * Amount of coins sent.
	 */
	private int value;
	
	/**
	 * The ID of the owning transaction, for which the output was created.
	 */
	private String originTransactionId;
	
	public TransactionOutput(String recipient, int value, String transactionId) {
		this.recipient = recipient;
		this.value = value;
		this.originTransactionId = transactionId;
		this.id = Hashing.sha256()
					.hashString(recipient + value + transactionId, StandardCharsets.UTF_8)
					.toString();
	}
	
	public boolean isOwnedBy(String owner) {
		return this.recipient == owner;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the recipient
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * @param recipient the recipient to set
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return the originTransactionId
	 */
	public String getOriginTransactionId() {
		return originTransactionId;
	}

	/**
	 * @param originTransactionId the originTransactionId to set
	 */
	public void setOriginTransactionId(String originTransactionId) {
		this.originTransactionId = originTransactionId;
	}
	
	

}
