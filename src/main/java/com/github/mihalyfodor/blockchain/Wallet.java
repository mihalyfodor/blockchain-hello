/**
 * 
 */
package com.github.mihalyfodor.blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A representation of the coins we own. The amount is given from the sum of transactions that
 * were addressed to us and we did not spend.
 * 
 * @author Mihaly Fodor
 *
 */
public class Wallet {
	
	/**
	 * The address of the wallet. This should normally be a private and public key pair.
	 */
	private String address;
	
	/**
	 * Map keeping track of the transactions we did not spend.
	 */
	private Map<String, TransactionOutput> unspentTransactionOutputs = new HashMap<String, TransactionOutput>();
	
	/**
	 * Creating a wallet needs only an owner
	 * 
	 * @param address name of the wallet/owner
	 */
	public Wallet(String address) {
		this.address = address;
	}
	
	/**
	 * Helper method to find out how much money we have and update our internal map for it.
	 * We are checking the transaction list of the blockchain and are checking if any of the transactions
	 * are ours.
	 * 
	 * @return the amount of coins we have
	 */
	public int getBalance() {
		int balance = 0;
		
		for (TransactionOutput output: Blockchain.unspentTransactionOutputs.values()) {
			if (output.isOwnedBy(this.address)) {
				this.unspentTransactionOutputs.put(output.getId(), output);
				balance = balance + output.getValue();
			}
		}
		
		return balance;
	}
	
	/**
	 * Send coins from this wallet to another.
	 * 
	 * @param recipient the address of the recipient wallet
	 * @param value the amount of coins we send
	 * 
	 * @return the transaction
	 */
	public Transaction sendCoins(String recipient, int value) {
		
		// we can't send coins we don't have
		if (getBalance() < value) {
			return null;
		}
		
		List<TransactionInput> inputs = gatherTransactionInputs(value);
		
		Transaction transaction = new Transaction(this.address, recipient, value, inputs);
		transaction.generateSignature();
		
		
		for (TransactionInput input: inputs) {
			unspentTransactionOutputs.remove(input.getTransactionOutputId());
		}
		
		return transaction;
	}

	/**
	 * Collect all relevant transactions that were addressed to us and are not yet spent.
	 * Find enough of them so we can send the coins.
	 * 
	 * @param value the total we are looking for
	 * @return the gathered transaction inputs
	 */
	private List<TransactionInput> gatherTransactionInputs(int value) {
		List<TransactionInput> inputs = new ArrayList<TransactionInput>();
		
		int total = 0;
		for (TransactionOutput output: Blockchain.unspentTransactionOutputs.values()) {
			
			if (output.isOwnedBy(this.getAddress())) {
				total = total + output.getValue();
				inputs.add(new TransactionInput(output.getId()));
				
				if (total > value) {
					break;
				}
			}
		}
		return inputs;
	}
	
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	

}
