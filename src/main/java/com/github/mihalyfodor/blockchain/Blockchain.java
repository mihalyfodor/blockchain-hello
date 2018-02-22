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
	 * 
	 * @return genesis block
	 */
	public Block initializeChain() {
		Block genesisBlock = new Block("Genesis Block", "0");
		blockchain.add(genesisBlock);
		return genesisBlock;
	}
	
	/**
	 * Add a block to the chain with the given data. Will only work if the chain is not empty.
	 * 
	 * @param data data to add
	 * @return the newly added block
	 */
	public Block addBlock(String data) {
		if (!blockchain.isEmpty()) {
			Block prevBlock = blockchain.get(blockchain.size()-1);
			Block newBlock = new Block(data, prevBlock.getHash());
			blockchain.add(newBlock);
			return newBlock;
		}
		return initializeChain();
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
			boolean hashMinedCorrectly = currentBlock.getHash().substring( 0, Block.LEADING_ZEROES.length()).equals(Block.LEADING_ZEROES);
			
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
