
public class SExpression implements Expression {
	
	Expression _leftNode;
	Expression _rightNode;
	String _opr;

	public SExpression(Expression left, Expression right, String operator) {
		_leftNode = left;
		_rightNode = right;
		_opr = operator;
	}
	
	/**
     * Creates and returns a deep copy of the Sum expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    public Expression deepCopy () {
    	return new SExpression(_leftNode.deepCopy(), _rightNode.deepCopy(), new String(_opr));
    }

	/**
	 * Creates a String representation of this Sum expression with a given starting
	 * indent level. If indentLevel is 0, then the produced string should have no
	 * indent; if the indentLevel is 1, then there should be 1 tab '\t'
	 * character at the start of every line produced by this method; etc.
	 * @param indentLevel how many tab characters should appear at the beginning of each line.
	 * @return the String representing this expression.
	 */
	public String convertToString (int indentLevel) {
		String indts = "\t".repeat(indentLevel);
		return indts + _opr + "\n" + _leftNode.convertToString(indentLevel + 1) + _rightNode.convertToString(indentLevel + 1);
	}

	/**
	 * Given the value of the independent variable x, compute the value of this Sum expression.
	 * @param x the value of the independent variable x
	 * @return the value of this expression.
	 */
	public double evaluate (double x) {
		if(_opr.equals("+")) {
			return _leftNode.evaluate(x) + _rightNode.evaluate(x);
		}
		else {
			return _leftNode.evaluate(x) - _rightNode.evaluate(x);
		}
	}

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Sum Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate() {
		SExpression copy = (SExpression) this.deepCopy();
		
		//f(x) = g(x) + h(x)
		//f'(x) = g'(x) + h'(x)
		if(_opr.equals("+")) {
			return new SExpression(copy._leftNode.differentiate(), copy._rightNode.differentiate(), "+");
		}
		
		//f(x) = g(x) - h(x)
		//f'(x) = g'(x) - h'(x)
		else {
			return new SExpression(copy._leftNode.differentiate(), copy._rightNode.differentiate(), "-");
		}
	}

	
}
