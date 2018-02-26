/**
 * 
 */
package com.github.mihalyfodor.blockchain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.GsonBuilder;

/**
 * @author Mihaly Fodor
 *
 */
public class TransactionTest {
	
	private Wallet walletA;
	private Wallet walletB;
	private Wallet bank;
	private Blockchain blockChain;
	
	@Before
	public void setUp() {
		walletA = new Wallet("Wallet A");
		walletB = new Wallet("Wallet B");
		bank = new Wallet("Bank");
		blockChain = new Blockchain();
	}
	
	@Test
	public void testBlock() {
		Transaction transaction = new Transaction(walletA.getAddress(), walletB.getAddress(), 13, new ArrayList<>());
		transaction.generateSignature();
		Assert.assertTrue(transaction.veifySignature());
	}
	
	@Test
	public void testTransactions() {
		
		// lets send someone some money
		Block genesisBlock = sendSomeMoneyFromThinAir();
		
		// thin air is weird
		assertEquals(bank.getBalance(), 0);
		assertEquals(walletA.getBalance(), 100);
		assertEquals(walletB.getBalance(), 0);
		
		Block block1 = sendMoneyAtoB(genesisBlock);
		
		assertEquals(bank.getBalance(), 0);
		assertEquals(walletA.getBalance(), 60);
		assertEquals(walletB.getBalance(), 40);
		
		Block block2 = sendMoneyAtoBfails(block1);
		
		assertEquals(bank.getBalance(), 0);
		assertEquals(walletA.getBalance(), 60);
		assertEquals(walletB.getBalance(), 40);
		
		sendMoneyBtoA(block2);
		
		assertEquals(bank.getBalance(), 0);
		assertEquals(walletA.getBalance(), 80);
		assertEquals(walletB.getBalance(), 20);
		
		assertTrue(blockChain.isChainValid());
	}
	
	private Block sendSomeMoneyFromThinAir() {
		
		printStatusBefore("Money from thin air:");
		Block genesisBlock = blockChain.addOriginTransaction(bank, walletA, 100);
		printStatusAfter();
		return genesisBlock;
	}

	private void printStatusBefore(String description) {
		System.out.println("-----------------------------------------");
		System.out.println(description);
		System.out.println("Bank: " + bank.getBalance());
		System.out.println("A: " + walletA.getBalance());
		System.out.println("B: " + walletB.getBalance());
		System.out.println("Unspent money: ");
		prettyPrint(Blockchain.unspentTransactionOutputs);
	}
	
	private void printStatusAfter() {
		System.out.println("Bank: " + bank.getBalance());
		System.out.println("A: " + walletA.getBalance());
		System.out.println("B: " + walletB.getBalance());
		System.out.println("Unspent money: ");
		prettyPrint(Blockchain.unspentTransactionOutputs);
		System.out.println("-----------------------------------------");
	}
	
	private Block sendMoneyAtoB(Block genesisBlock) {
		printStatusBefore("Money from A to B");
		Block block1 = new Block(genesisBlock.getHash());
		block1.addTransaction(walletA.sendCoins(walletB.getAddress(), 40));
		blockChain.addBlock(block1);
		printStatusAfter();
		return block1;
	}

	private Block sendMoneyAtoBfails(Block block1) {
		printStatusBefore("Money from A to B whilst not having enough");
		Block block2 = new Block(block1.getHash());
		block2.addTransaction(walletA.sendCoins(walletB.getAddress(), 1000));
		blockChain.addBlock(block2);
		printStatusAfter();
		return block2;
	}

	private void sendMoneyBtoA(Block block2) {
		printStatusBefore("Money from B to A");
		Block block3 = new Block(block2.getHash());
		block3.addTransaction(walletB.sendCoins(walletA.getAddress(), 20));
		blockChain.addBlock(block3);
		printStatusAfter();
	}

	private void prettyPrint(Object item) {
		String json = new GsonBuilder().setPrettyPrinting().create().toJson(item);
		System.out.println(json);
	}

}
