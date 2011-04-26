

/**
 * AVL Node in the {@link AVLTree}.
 */
public class AVLNode {
	/** The size of the tree. */
	private int size;
	
	/** The height of the tree. */
	private int height;
	
	/** The key of the current node. */
	private Object key;
	
	/** The data of the current node. */
	private Object data;
	
	/** The {@link Comparator} used by the node. */
	private Comparator comp;
	
	/** All the nodes pointed by the current node. */
	private AVLNode left,right,parent,succ,pred;

	/**
	 * Instantiates a new AVL node.
	 *
	 * @param key the key of the node
	 * @param data the data that the node should keep
	 * @param comp the comparator to be used in the tree
	 */
	public AVLNode(Object key, Object data, Comparator comp) {
		this(key,data,comp,null);
	}

	/**
	 * Instantiates a new AVL node.
	 *
	 * @param key the key of the node
	 * @param data the data that the node should keep
	 * @param comp the comparator to be used in the tree
	 * @param parent the parent of the created node
	 */
	public AVLNode(Object key, Object data, Comparator comp, AVLNode parent) {
		this.data = data;
		this.key = key;
		this.comp = comp;
		this.parent = parent;
		
		this.left = null;
		this.right = null;
		this.succ = null;
		this.pred = null;
		
		this.size = 1;
		this.height = 0;
	}

	/**
	 * Adds the given data to the tree.
	 *
	 * @param key the key
	 * @param data the data
	 * @return the root of the tree after insertion and rotations
	 * @author <b>Asaf Flescher, Dana Katz-Buchstav</b>
	 */
	public AVLNode add(Object key,Object data) {
		int compResult = comp.compare(key, this.key);
		boolean newLevelAdded = false;
                AVLNode nodeToUse = null;
                if (compResult <= 0)
		{
			nodeToUse = left;	
		}
		else
		{
			nodeToUse = right;
		}
		// If the tree is empty
		if (nodeToUse == null)
		{
			nodeToUse = new AVLNode(key, data, this.comp, this);	
			// If left and right are null, then we're adding a new 'level' to the tree and need to update the height of everything in the tree
			if ((left == null) && (right == null))
			{
				newLevelAdded = true;
			}
			// Put the new node in the tree itself
	                if (compResult <= 0)
			{
				left = nodeToUse;

                                // Setting inorder parameters
                                nodeToUse.succ = this;
				nodeToUse.pred = pred;
				pred = nodeToUse;
                                
				AVLNode currNode = this;
				if (nodeToUse.pred != null)
				{
					nodeToUse.pred.succ = nodeToUse;
				}
			}
			else
			{
				right = nodeToUse;

                                // Setting inorder parameters
                                nodeToUse.pred = this;
				nodeToUse.succ = succ;
				succ = nodeToUse;
				if (nodeToUse.succ != null)
				{
					nodeToUse.succ.pred = nodeToUse;
				}
				AVLNode currNode = this;
				
			}
                        nodeToUse.parent = this;
		}
		else
		{
			nodeToUse.add(key, data);
		}

		if (newLevelAdded)
		{
			height = 1;
			boolean needToChangeHeight = true;
			AVLNode currParent = parent;
			// Run until we either hit the root or the change stops reverbating - i.e, the added node doesn't change the height of the level
			while ((currParent != null) && (needToChangeHeight))
			{
				int maxHeight = currParent.getMaxHeight() + 1;
				if (maxHeight > currParent.height)
				{
					currParent.height = maxHeight;
					currParent = currParent.parent;
				}	
				else
				{
					needToChangeHeight = false;
				}
			}
		}

		// Adjusting the size - we added a node, so it goes up by 1.
		size = size + 1;

		return balanceTree();
	}

        private AVLNode balanceTree()
        {
                // Now we check if the new tree is still balanced
		int balance = balanceFactor();

		// Four cases for balancing
		if (balance == -2)
		{
			int rightBalance = right.balanceFactor();
			if (rightBalance <= 0)
			{
				// RRC - one left rotation needed

				// left rotation

				rotateLeft(this);
			}
			else
			{
				// RLC - first rotate the subtree to the right, then rotate to the left

				// right rotation of subtree
				rotateRight(right);
				right.adjustHeight();
				right.adjustSize();
				if (right.right != null)
				{
					right.right.adjustSize();
					right.right.adjustHeight();
				}
				// left rotation

				rotateLeft(this);
			}
		}
		else if (balance == 2)
		{
			int leftBalance = left.balanceFactor();
			if (leftBalance <= 0)
			{
				// LRC - first rotate the subtree to the left then rotate right

				// left rotation of subtree
				rotateLeft(left);

				left.adjustHeight();
				left.adjustSize();
				if (left.left != null)
				{
					left.left.adjustHeight();
					left.left.adjustSize();
				}

				// right rotation

				rotateRight(this);
			}
			else
			{
				// LLC - one right rotation needed

				// right rotation

				rotateRight(this);
			}
		}
		// Any changes require a re-balancing of the height and size of the subtree - which, at most, changed the level and size of two nodes
		adjustSize();
		adjustHeight();
		if (parent != null)
		{
			parent.adjustSize();
			parent.adjustHeight();
		}

		// Getting the tree root to return
		AVLNode currParent = parent;
		AVLNode root = this;
		while (currParent != null)
		{
			root = currParent;
			currParent = currParent.parent;
		}

		return root;
        }

	private int balanceFactor()
	{
		int leftFactor = -1;	
		int rightFactor = -1;
		if (left != null)
			leftFactor = left.height;
		if (right != null)
			rightFactor = right.height;
		return leftFactor - rightFactor;
	}

	private void rotateRight(AVLNode root)
	{
		if (root.left != null)
		{
			AVLNode newRoot = root.left;
			AVLNode dataToTransfer = newRoot.right;
			root.left = dataToTransfer;
			if (dataToTransfer != null)
			{
				dataToTransfer.parent = root;
			}
			
			newRoot.right = root;
			newRoot.parent = root.parent;
			if (root.parent != null)
			{
				if (root.isRightChild())
				{
					root.parent.right = newRoot;
				}
				else
				{
					root.parent.left = newRoot;
				}
			}
			root.parent = newRoot;
		}
	}

	private void rotateLeft(AVLNode root)
	{
		if (root.right != null)
		{
			AVLNode newRoot = root.right;
			AVLNode dataToTransfer = newRoot.left;
			root.right = dataToTransfer;
			if (dataToTransfer != null)
			{
				dataToTransfer.parent = root;
			}

			newRoot.left = root;
			newRoot.parent = root.parent;
			if (root.parent != null)
			{
				if (root.isRightChild())
				{
					root.parent.right = newRoot;
				}
				else
				{
					root.parent.left = newRoot;
				}
			}
			root.parent = newRoot;
		}
	}

	private int getMaxHeight()
	{
		int leftHeight = (left != null) ? left.height : 0;
		int rightHeight = (right != null) ? right.height : 0;
		return Math.max(leftHeight, rightHeight);
	}
	private void adjustSize()
	{
		int leftSize = (left != null) ? left.size : 0;
		int rightSize = (right != null) ? right.size : 0;
		size = leftSize + rightSize + 1;
	}
	private void adjustHeight()
	{
		height = getMaxHeight();
		// Adding the level this node gets for having ANY children at all
		if ((left != null) || (right != null))
		{
			height = height + 1;
		}
	}

	private boolean isRightChild()
	{
		boolean isRight = false;

                if (parent != null) {

                    if (parent.right != null)
                    {
                            if (comp.compare(key, parent.right.getKey()) == 0)
                            {
                                    isRight = true;
                            }
                    }
                }
		return isRight;
	}

	/**
	 * Removes a Node which key is equal (by {@link Comparator}) to the given argument.
	 *
	 * @param key the key
	 * @return the root after deletion and rotations
	 * @author <b>Asaf Flescher, Dana Katz-Buchstav</b>
	 */
	public AVLNode remove(Object key) {
                AVLNode ret = null;

                switch (comp.compare(key, this.key))
                {
                    // Removing this item
                    case 0:
                        // If item is a leaf
                        if (left == null && right == null)
                        {
                            if (isRightChild())
                                parent.right = null;
                            else
                                parent.left = null;
                        }
                        // if it has one child, moving it to the parent
                        else if (left == null || right == null)
                        {
                            AVLNode child = (left == null ? right : left);

			    if (parent != null)
			    {
                            	if (isRightChild())
				{
                                	parent.right = child;
				}
                            	else
				{
                               	 	parent.left = child;
				}
			    }
			    child.parent = parent;

                        }
                        // if it has two children, switching places with predecessor.
                        else
                        {
                            // switching pred's son to be parented by pred's parent - it will have
			    // either a left child if it itself is a right child or the other way around
                            if (pred.isRightChild())
                                pred.parent.right = pred.left;
                            else if (pred.parent != null)
                                pred.parent.left = pred.right;

                            // giving pred custody of this node's children
                            pred.right = right;
			    if (right != null)
			    {
				    right.parent = pred;
			    }
                            pred.left = left;
			    if (left != null)
			    {
				    left.parent = pred;
			    }

                            // keeping inorder in order
                            pred.succ = succ;
			    if (succ != null)
			    {
                            	succ.pred = pred;
			    }

			    // replacing this node in the parent's eyes
			    if (parent != null)
			    {
                            	if (isRightChild())
				{
                                	parent.right = pred;
				}
                            	else
				{
                               	 	parent.left = pred;
				}
			    }
			    pred.parent = parent;
			    	
                        }

                        ret = balanceTree();

                        break;
                    case -1:
                        if (left != null)
                            ret = left.remove(key);
                        break;
                    case 1:
                        if (right != null)
                            ret = right.remove(key);
                        break;
                }

                // If key not found, returning current head
                if (ret == null)
                {
                    AVLNode root = this;
                    while (root.parent != null)
                    {
                        root = root.parent;
                    }
                    ret = root;
                }


		return ret;
	}

	/**
	 * Gets all the elements between the K'th and the H'th element in the tree.
	 *
	 * @param k A number between 1 and @link {@link #size()}
	 * @param h A number between k and @link {@link #size()}
	 * @return elements between k and h
	 * @author <b>Asaf Flescher, Dana Katz-Buchstav</b>
	 */
	public LinkedList getKthTillHth(int k, int h) {

            LinkedList ret = new LinkedList();

            // Get Kth element
            AVLNode curr = findKthNode(k);

            // Adding elements to array
            for (int i = k; i < h && curr != null; ++i)
            {
                ret.addLast(curr.data);
                curr = curr.succ;
            }


            return ret;
	}
	

	/**
	 * Finds a Node which key is equal (by {@link Comparator}) to the given argument.
	 *
	 * @param key the key of the node
	 * @return the data of the found Node, returns null if node isn't found
	 * @author <b>Asaf Flescher, Dana Katz-Buchstav</b>
	 */
	public Object find(Object key) {
		int result = comp.compare(key, this.key);
		if (result ==0)
		{
			return this;
		}
		else if (result < 0)
		{
			if (left != null)
			{
				return left.find(key);
			}
			else
			{
				return null;
			}
		}
		else
		{
			if (right != null)
			{
				return right.find(key);
			}
			else
			{
				return null;
			}
		}
	}

	/**
	 * Find K'th element in the tree.
	 *
	 * @param k is a number between 1 and {@link #size()}
	 * @return the object
	 * @author <b>Asaf Flescher, Dana Katz-Buchstav</b>
	 */
	public Object findKthElement(int k){

            AVLNode elem = findKthNode(k);

            Object ret = null;
            if (elem != null)
                ret = elem.data;

            return ret;
	}

        private AVLNode findKthNode(int k){

            AVLNode ret = null;


            // Checking the left subtree
            if (left == null)
            {
                // If there are no elements to the left, this is the
                // first element in this tree
                if (k == 1)
                {
                    ret = this;
                }
                else
                {
                    if (right != null)
                        ret = right.findKthNode(k - 1);
                }
            }
            else
            {
                if (left.size() >= k) {
                    ret = left.findKthNode(k);
                }
                else if (left.size() == k - 1)
                {
                    ret = this;
                }
                else if (left.size() < k - 1)
                {
                    if (right != null)
                        ret = right.findKthNode(k - (left.size() + 1));
                }
            }

            return ret;
        }
	
	/**
	 * Return the size of the tree.
	 *
	 * @return the size of the tree.
	 */
	public int size(){
		return this.size;
	}

	
	/**
	 * Return the height of the tree.
	 *
	 * @return the height of the tree.
	 */
	public int height(){
		return this.height;
	}

	/**
	 * Gets the key of the current node.
	 *
	 * @return the key
	 */
	public Object getKey() {
		return this.key;
	}

	/**
	 * Gets the data of the current node.
	 *
	 * @return the data
	 */
	public Object getData() {
		return this.data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.key + " " + this.data + ")";
	}

	/**
	 * Load the current tree (starting with the current node) into a {@link StringBuilder} in a in-order way.
	 *
	 * @param sb the {@link StringBuilder}
	 */
	public void inOrderToString(StringBuilder sb){
		if (this.left != null){	
			this.left.inOrderToString(sb);
			sb.append(this.key + " -> " + left.key + ";\n");
		}else
		{
			sb.append("nulll"+this.key+" [shape=point];\n");
			sb.append(this.key + " -> nulll" + this.key + ";\n");
		}

		sb.append(this.key + "[label=\""+this.data+",s:"+this.size()+",h:"+this.height()+"\"];\n");
		if (this.parent != null)
			sb.append(this.key + " -> " + parent.key + "[style=dashed];\n");

		if (this.right != null){
			this.right.inOrderToString(sb);
			sb.append(this.key + " -> " + right.key + ";\n");
		}else
		{
			sb.append("nullr"+this.key+" [shape=point];\n");
			sb.append(this.key + " -> nullr" + this.key + ";\n");
		}
	}

	/**
	 * Gets the predecessor of the current node.
	 *
	 * @return the predecessor
	 */
	public AVLNode getPred() {
		return pred;
	}
	
	/**
	 * Gets the successor of the current node.
	 *
	 * @return the successor
	 */
	public AVLNode getSucc(){
		return succ;
	}
}
