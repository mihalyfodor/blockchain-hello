/**
 * 
 */
package com.github.mihalyfodor.blockchain;

/**
 * A Transaction Input represents references to transactions that were received
 * in a wallet and have not yet been spent. These references are implemented
 * using TransactionOutput.
 * 
 * @author Mihaly Fodor
 *
 */
public class TransactionInput {

	/**
	 * The ID of the output mapped to this input, for quicker reference.
	 */
	private String transactionOutputId;
	
	/**
	 * The output mapped to this input.
	 */
	private TransactionOutput unspentTransactionOutput;
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	/**
	 * @return the transactionOutputId
	 */
	public String getTransactionOutputId() {
		return transactionOutputId;
	}

	/**
	 * @param transactionOutputId the transactionOutputId to set
	 */
	public void setTransactionOutputId(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	/**
	 * @return the unspentTransactionOutput
	 */
	public TransactionOutput getUnspentTransactionOutput() {
		return unspentTransactionOutput;
	}

	/**
	 * @param unspentTransactionOutput the unspentTransactionOutput to set
	 */
	public void setUnspentTransactionOutput(TransactionOutput unspentTransactionOutput) {
		this.unspentTransactionOutput = unspentTransactionOutput;
	}
	
	
	
}
