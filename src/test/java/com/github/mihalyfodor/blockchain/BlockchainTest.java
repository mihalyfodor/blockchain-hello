/**
 * 
 */
package com.github.mihalyfodor.blockchain;

import java.util.ArrayList;

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
//		Blockchain blockchain = new Blockchain();
//		blockchain.initializeChain();
//		blockchain.addBlock("2").mineBlock();
//		blockchain.addBlock("3").mineBlock();
//		blockchain.addBlock("4").mineBlock();
//		
//		Assert.assertTrue(blockchain.isChainValid());
//		prettyPrint(blockchain);
	}
	
	/**
	 * Also tampering with the chain should result in it being invalid.
	 */
	@Test
	public void testChainTampering() {
//		Blockchain blockchain = new Blockchain();
//		blockchain.initializeChain();
//		blockchain.addBlock("2").mineBlock();
//		blockchain.addBlock("3").mineBlock();
//		blockchain.addBlock("4").mineBlock();
//		
//		blockchain.getBlockchain().get(1).setHash("modified");
//		
//		Assert.assertFalse(blockchain.isChainValid());
//		prettyPrint(blockchain);
	}
	
	/**
	 * Not mining a block means the chain is invalid.
	 */
	@Test
	public void testChainNotMined() {
//		Blockchain blockchain = new Blockchain();
//		blockchain.initializeChain();
//		blockchain.addBlock("2").mineBlock();
//		blockchain.addBlock("3");
//		blockchain.addBlock("4").mineBlock();
//		
//		Assert.assertFalse(blockchain.isChainValid());
//		prettyPrint(blockchain);
	}
	
	private void prettyPrint(Blockchain blockchain) {
		String blockchainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println(blockchainJSON);
	}

}
