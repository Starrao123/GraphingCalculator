
public class VariableExpression implements Expression {

	public VariableExpression() {
	}
	
	/**
     * Creates and returns a deep copy of the Multiplication expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    public Expression deepCopy () {
    	return new VariableExpression();
    }

	/**
	 * Creates a String representation of this Multiplication expression with a given starting
	 * indent level. If indentLevel is 0, then the produced string should have no
	 * indent; if the indentLevel is 1, then there should be 1 tab '\t'
	 * character at the start of every line produced by this method; etc.
	 * @param indentLevel how many tab characters should appear at the beginning of each line.
	 * @return the String representing this expression.
	 */
	public String convertToString (int indentLevel) {
		String indts = "\t".repeat(indentLevel);
		return indts + "x\n";
	}

	/**
	 * Given the value of the independent variable x, compute the value of this Multiplication expression.
	 * @param x the value of the independent variable x
	 * @return the value of this expression.
	 */
	public double evaluate (double x) {
		return x;
	}

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Multiplication Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate () {
		return new LiteralExpression(1);
	}
	
}
