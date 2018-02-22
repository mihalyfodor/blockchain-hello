# Let's build a Blockchain

I was interested in the technology for a while now, so after reading a couple of articles and guides I decided to roll my own.
This will be split into sections, so if anyone wants to follow it will be easier to do so.

## Blocks and Chains

According to Google a blockchain is "a digital ledger in which transactions made in bitcoin or another cryptocurrency are recorded chronologically and publicly."
For us that means we need to have an immutable and sequential chain of records somewhere. This gives us the blocks part.

### Blocks

A Block is a simle Java class in our case, with a couple of important fields:

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
