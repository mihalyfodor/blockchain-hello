/**
 * 
 */
package com.github.mihalyfodor.blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Blockchain containing our Blocks.
 * 
 * @author Mihaly Fodor
 *
 */
public class Blockchain {
	
	/**
	 * Storing our blocks in a list.
	 */
	private List<Block> blockchain = new ArrayList<Block>();
	
	/**
	 * Keeping track of all the transaction outputs that have not been spent.
	 */
	public static Map<String, TransactionOutput> unspentTransactionOutputs = new HashMap<String, TransactionOutput>();
	
	/**
	 * The hash code of the very first transaction that we create.
	 */
	public static final String GENESIS_HASH = "0";
	
	/**
	 * Leading zeroes used for verifying proof of work.
	 */
	public static final String LEADING_ZEROES = "00000";
	
	
	/**
	 * Initialize the chain with a genesis block.
	 * 
	 * @return genesis block
	 */
	public Block initializeChain() {
		Block genesisBlock = new Block(Blockchain.GENESIS_HASH);
		blockchain.add(genesisBlock);
		return genesisBlock;
	}
	
	/**
	 * Create the very first transaction. We need to set most of the fields manually
	 * - create the transaction itself
	 * - create the single output (we are creating money from nothing)
	 * - create the block for it
	 * 
	 * @param originWallet the "Bank"'s wallet
	 * @param targetWallet the wallet of a lucky person
	 * @param coins the amount of coins we send
	 * 
	 * @return the block created for the transaction
	 */
	public Block addOriginTransaction(Wallet originWallet, Wallet targetWallet, int coins) {
		
		System.out.println("creating origin cash");
		
		Transaction genesisTransaction = new Transaction(originWallet.getAddress(), targetWallet.getAddress(), coins, new ArrayList<>());
		genesisTransaction.generateSignature();
		genesisTransaction.setTransactionId(GENESIS_HASH);
		
		TransactionOutput genesisOutput = new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId());
		genesisTransaction.getOutputs().add(genesisOutput);
		Blockchain.unspentTransactionOutputs.put(genesisOutput.getId(), genesisOutput);
		
		Block genesisBlock = new Block(Blockchain.GENESIS_HASH);
		genesisBlock.getTransactions().add(genesisTransaction);
		this.blockchain.add(genesisBlock);
		
		return genesisBlock;
	}
	
	/**
	 * Add a block to the chain with the given data. Will only work if the chain is not empty.
	 * 
	 * @param data data to add
	 * @return the newly added block
	 */
	public Block addBlock(Block block) {
		block.mineBlock();
		blockchain.add(block);
		return block;
	}
	
	
	/**
	 * Validate the chain. If we have just the genesis block, that is valid. Otherwise we 
	 * traverse the chain with two variables, and compare the hashes as follows:
	 * - the hashcode needs to be able to be regenerated
	 * - the prevHash codes need to be continuous
	 * - the block must have been mined if not genesis block
	 * 
	 * @return chain validity
	 */
	public Boolean isChainValid() {
		
		if (blockchain.size() <= 1) {
			return true;
		}
		
		Block prevBlock = null;
		
		for (Block currentBlock : blockchain) {
			
			// skip testing the genesis block, we have no prevBlock in this case
			if (prevBlock == null) {
				prevBlock = currentBlock;
				continue;
			}
			
			// verify against tampering. If we cannot regenerate the hash correctly the chain is not valid anymore.
			boolean currentHashCorrect = currentBlock.getHash().equals(currentBlock.calculateHash());
			
			// similarly if the previous hash reference is incorrect it is also a problem
			boolean prevHashCorrect = prevBlock.getHash().equals(currentBlock.getPreviousHash());
			
			// also each block must have been mined for the chain to be valid
			boolean hashMinedCorrectly = currentBlock.getHash().substring( 0, Blockchain.LEADING_ZEROES.length()).equals(Blockchain.LEADING_ZEROES);
			
			if (!currentHashCorrect || !prevHashCorrect || !hashMinedCorrectly) {
				return false;
			}
			
			prevBlock = currentBlock;
		}
		
		return true;
	}

	/**
	 * @return the blockchain
	 */
	public List<Block> getBlockchain() {
		return blockchain;
	}

	

}
