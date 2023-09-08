
public class PExpression implements Expression {
	
	Expression _node;

	public PExpression(Expression node) {
		_node = node;
	}
	
	/**
     * Creates and returns a deep copy of
     *  the Parentheses expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    public Expression deepCopy () {
    	return new PExpression(_node.deepCopy());
    }

	/**
	 * Creates a String representation of this Parentheses expression with a given starting
	 * indent level. If indentLevel is 0, then the produced string should have no
	 * indent; if the indentLevel is 1, then there should be 1 tab '\t'
	 * character at the start of every line produced by this method; etc.
	 * @param indentLevel how many tab characters should appear at the beginning of each line.
	 * @return the String representing this expression.
	 */
	public String convertToString (int indentLevel) {
		String indts = "\t".repeat(indentLevel);
		return indts + "()" + "\n" + _node.convertToString(indentLevel + 1);
	}

	/**
	 * Given the value of the independent variable x, compute the value of this Parentheses expression.
	 * @param x the value of the independent variable x
	 * @return the value of this expression.
	 */
	public double evaluate (double x) {
			return _node.evaluate(x);
	}

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Parentheses Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate () {
		PExpression copy = (PExpression) this.deepCopy();
		return new PExpression(copy._node.differentiate());
	}
	
}
