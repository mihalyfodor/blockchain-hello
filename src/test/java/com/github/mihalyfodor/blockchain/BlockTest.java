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
public class BlockTest {
	
	/**
	 * Not much we can test for now, besides the generated signatures not being equal.
	 * 
	 * One important observation is the very first block, as it cannot have a previous hash.
	 * Because of this it will be initialized to 0.
	 */
	@Test
	public void testBlock() {
		// Creating genesis block
		Block genesisBlock = new Block("First block!", "0");
		Assert.assertNotEquals(genesisBlock.getHash(), "0");
		prettyPrint(genesisBlock);
		
		Block secondBlock = new Block("Second", genesisBlock.getHash());
		Assert.assertNotEquals(genesisBlock.getHash(), secondBlock.getHash());
		prettyPrint(secondBlock);
		
		Block thirdBlock = new Block("Third", genesisBlock.getHash());
		Assert.assertNotEquals(secondBlock.getHash(), thirdBlock.getHash());
		prettyPrint(thirdBlock);
		
	}
	
	private void prettyPrint(Block block) {
		String blockJSON = new GsonBuilder().setPrettyPrinting().create().toJson(block);
		System.out.println(blockJSON);
	}

}
