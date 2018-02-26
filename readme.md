# Let's build a Blockchain

I was interested in the technology for a while now, so after reading a couple of articles and guides I decided to roll my own.

## Blocks and Chains

According to Google a blockchain is "a digital ledger in which transactions made in bitcoin or another cryptocurrency are recorded chronologically and publicly."
For us that means we need to have an immutable and sequential chain of records somewhere. This gives us the blocks part.

### Blocks

A Block is a simple Java class in our case, with a couple of important fields:

* hash: storing the digital signature of our block
* previousHash: storing the digital signature of the previous block
* data: will be important later, for now it holds our hello text
* timestamp: the point in time when the block was created

The hash itself identifies the block, and is generated based on the previous block's hash, the data of the block and the timestamp of the block's creation. Since a block is based on a previous block's hash, the two parameters for our constructor are the data and the hash:

```
public Block(String data, String previousHash) {
	super();
	this.previousHash = previousHash;
	this.data = data;
	this.timestamp = System.currentTimeMillis();
	this.setHash(calculateHash());
}
```

Timestamp can be set automatically, and the hash is generated. One way to do this is to concat the above 3 fields, and run that through a hashing algorithm. I decided to go for Google's solution:

```
public String calculateHash() {
	return Hashing.sha256().hashString(previousHash + data + timestamp, StandardCharsets.UTF_8)
				.toString();
```

The library in question is


```
<dependency>
	<groupId>com.google.guava</groupId>
	<artifactId>guava</artifactId>
	<version>20.0</version>
</dependency>
```

I also added some basic unit test to this, just to see that some blocks are generated. Using

```
<dependency>
	<groupId>com.google.code.gson</groupId>
	<artifactId>gson</artifactId>
	<version>2.8.2</version>
</dependency>
```

to print them nicely to console.

### Chain

Next up is the chain itself. It can also be a simple Java class, wit a list of Blocks as the only field. The first thing we need to do is initialize it. This means creating a genesis block, whose previous hash is "0":

```
public void initializeChain() {
	blockchain.add(new Block("Genesis Block", "0"));
}
```
After this we can keep adding blocks to the chain.

But we also need the ability to tell that the chain itself is valid. This presumes a couple of tests:
* we need to be able to regenerate the hash code at any time, and it should give the same result
* we beed to be able to follow the previousHash fields through the chain and that should be continuous.
With that in mind we can have the following:

```
public Boolean isChainValid() {
		
	if (blockchain.size() <= 1) {
		return true;
	}
	
	Block prevBlock = null;
	
	for (Block currentBlock : blockchain) {
	
		if (prevBlock == null) {
			prevBlock = currentBlock;
			continue;
		}
		
		boolean currentHashCorrect = currentBlock.getHash().equals(currentBlock.calculateHash());
		boolean prevHashCorrect = prevBlock.getHash().equals(currentBlock.getPreviousHash());
		
		if (!currentHashCorrect || !prevHashCorrect) {
			return false;
		}
		
		prevBlock = currentBlock;
	}
	return true;
}
```
If we have just the genesis block, that is valid. Otherwise we traverse the chain with two variables, and compare the hashes as described above.

## Mining

Mining a block is mostly understood as doing some amount of work before we consider a block as being valid. This is called proof of work. One simple way of doing this is repeatedly generating hashes until we hit one with a set amount of leading zeroes.

We will need the following method on the Block class:

```
public void mineBlock() {
	while(!hash.substring( 0, LEADING_ZEROES.length()).equals(LEADING_ZEROES)) {
		delta ++;
		hash = calculateHash();
	}
}
```
Delta is a new integer field used to ensure a new hash on a new try. It is concatenated to the other fields inside the calculateHash() method. We also need to update our validateChain, as we need to check that each block was mined correctly:

```
boolean hashMinedCorrectly = currentBlock.getHash().substring( 0, Block.LEADING_ZEROES.length()).equals(Block.LEADING_ZEROES);
```

Running the unit tests at this time will show a better representation of the blockchain, as each block will take some time to mine.

## Wallet and Transactions

We need a representation of the money (coins) we own. This can be a wallet, with an address and the money we still have. The address can be a string for now, and the coins we have can be the sum of all the transactions we received that we did not spend. This way we can find out how many coins we have:

```
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
```
We can ask for all the transactions from the blockcain, check if we own it, and if yes then add it to our tally. We also need to build a list of our known transactions, as it will be the input for the transaction we create when sending coins. We are selecting the minimum amount of transactions we need to use up in order to send coins:

```
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

```

When we send coins to someone we can create a transaction with from, to and amount, along with the inputs we mentioned. Sending coins thus becomes:

```
public Transaction sendCoins(String recipient, int value) {
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
```
If we have a transaction created from a wallet, we need to process it. This is done when adding the transaction to the blockchain. If the transaction is valid we can create the necessary inputs and outputs to shuffle the coins around:
1. if the signature verification fails, we cannot proceed
2. we collect all the transaction outputs given by the sender as inputs for the transaction
3. we calculate how much money we need to send and how much remains, based on the above transactions
4. we send the intended amount to the recipient
5. we send the leftover back to the sender
6. we remove the input transactions from the unspent transactions list
The code for the above looks like this:

```
public boolean processTransaction() {
	if (!this.veifySignature()) {
		return false;
	}
		
	for (TransactionInput input : inputs) {
		TransactionOutput unspentTransactionOutput = Blockchain.unspentTransactionOutputs.get(input.getTransactionOutputId());
		input.setUnspentTransactionOutput(unspentTransactionOutput);
	}
		
	int sumOfUnspentInputs = inputs.stream()
			                .filter(e -> e.getUnspentTransactionOutput() != null)
			                .mapToInt(e -> e.getUnspentTransactionOutput().getValue())
			                .sum();
		
	int leftOverValue = sumOfUnspentInputs - value;
	transactionId = calculateHash();
	TransactionOutput recipientReceived = new TransactionOutput(this.recipient, value, transactionId);
	outputs.add(recipientReceived);
	TransactionOutput senderReceived = new TransactionOutput(this.sender, leftOverValue, transactionId);
	outputs.add(senderReceived);
		
	for (TransactionOutput output : outputs) {
		Blockchain.unspentTransactionOutputs.put(output.getId(), output);
	}
		
	for (TransactionInput input : inputs) {
		if (input.getUnspentTransactionOutput() != null) {
			Blockchain.unspentTransactionOutputs.remove(input.getUnspentTransactionOutput().getId());
		}
	}
	return true;
}
	
```

## Sources
I was interested in looking into how one can build a blockchain, so I started looking for existing resources:
* [Blockchain in python](https://hackernoon.com/learn-blockchains-by-building-one-117428612f46)
* [Blockchain in Java, with Crypto included](https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa)
* [Simple theory](http://ccwikia.com/the-ultimate-3500-word-plain-english-guide-to-blockchain/)