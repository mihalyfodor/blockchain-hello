/**
 * 
 */
package com.github.mihalyfodor.blockchain;

import java.util.ArrayList;
import java.util.List;

/**
 * The Blockchain containing our Blocks.
 * 
 * @author Mihaly Fodor
 *
 */
public class Blockchain {

	private List<Block> blockchain = new ArrayList<Block>();
	
	/**
	 * Initialize the chain with a genesis block.
	 */
	public void initializeChain() {
		blockchain.add(new Block("Genesis Block", "0"));
	}
	
	/**
	 * Add a block to the chain with the given data. Will only work if the chain is not empty.
	 * 
	 * @param data data to add
	 */
	public void addBlock(String data) {
		if (!blockchain.isEmpty()) {
			Block prevBlock = blockchain.get(blockchain.size()-1);
			Block newBlock = new Block(data, prevBlock.getHash());
			blockchain.add(newBlock);
		}
	}
	
	
	/**
	 * Validate the chain. If we have just the genesis block, that is valid. Otherwise we 
	 * traverse the chain with two variables, and compare the hashes as follows:
	 * - the hashcode needs to be able to be regenerated
	 * - the prevHash codes need to be continuous
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
			
			if (!currentHashCorrect || !prevHashCorrect) {
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
