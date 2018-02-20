/**
 * 
 */
package com.github.mihalyfodor.blockchain;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.GsonBuilder;

/**
 * @author Mihaly Fodor
 *
 */
public class BlockchainTest {
	
	/**
	 * The simplest test we can do is one containing only a genesis block.
	 * It should be as valid as possible :)
	 */
	@Test
	public void testGenesisChain() {
		Blockchain blockchain = new Blockchain();
		blockchain.initializeChain();
		
		Assert.assertTrue(blockchain.isChainValid());
		prettyPrint(blockchain);
	}
	
	/**
	 * We can also test a straightforward chain.
	 */
	@Test
	public void testChain() {
		Blockchain blockchain = new Blockchain();
		blockchain.initializeChain();
		blockchain.addBlock("2");
		blockchain.addBlock("3");
		blockchain.addBlock("4");
		
		Assert.assertTrue(blockchain.isChainValid());
		prettyPrint(blockchain);
	}
	
	/**
	 * Also tampering with the chain should result in it beeing invalid.
	 */
	@Test
	public void testChainTampering() {
		Blockchain blockchain = new Blockchain();
		blockchain.initializeChain();
		blockchain.addBlock("2");
		blockchain.addBlock("3");
		blockchain.addBlock("4");
		
		blockchain.getBlockchain().get(1).setData("Lots of Coins");
		
		Assert.assertFalse(blockchain.isChainValid());
		prettyPrint(blockchain);
	}
	
	private void prettyPrint(Blockchain blockchain) {
		String blockchainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println(blockchainJSON);
	}

}
