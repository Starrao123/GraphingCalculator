
public class EExpression implements Expression {
	
	Expression _leftNode;
	Expression _rightNode;
	String _opr;

	public EExpression(Expression left, Expression right, String operator) {
		_leftNode = left;
		_rightNode = right;
		_opr = operator;
	}
	
	/**
     * Creates and returns a deep copy of the Exponentiation expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    public Expression deepCopy () {
    	// If the expression is logarithmic, the program doesn't call deep copy on the null leftNode,
    	// which would have caused an error.
    	if (_opr.equals("log")) {
    		return new EExpression(null, _rightNode.deepCopy(), new String(_opr));
    	}
    	
    	return new EExpression(_leftNode.deepCopy(), _rightNode.deepCopy(), new String(_opr));
    }

	/**
	 * Creates a String representation of this Exponentiation expression with a given starting
	 * indent level. If indentLevel is 0, then the produced string should have no
	 * indent; if the indentLevel is 1, then there should be 1 tab '\t'
	 * character at the start of every line produced by this method; etc.
	 * @param indentLevel how many tab characters should appear at the beginning of each line.
	 * @return the String representing this expression.
	 */
	public String convertToString (int indentLevel) {
		String indts = "\t".repeat(indentLevel);
		if (_opr.equals("^")) {
			return indts + _opr + "\n" + _leftNode.convertToString(indentLevel + 1) + _rightNode.convertToString(indentLevel + 1);
		} else {
			return indts + _opr + "\n" + _rightNode.convertToString(indentLevel + 1	);
		}
	}

	/**
	 * Given the value of the independent variable x, compute the value of this Exponentiation expression.
	 * @param x the value of the independent variable x
	 * @return the value of this expression.
	 */
	public double evaluate (double x) {
		if(_opr.equals("^")) {
			return Math.pow(_leftNode.evaluate(x), _rightNode.evaluate(x));
		}
		else {
			return Math.log(_rightNode.evaluate(x));
		}
	}

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Exponentiation Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate () {
		EExpression copy = (EExpression) this.deepCopy();
		
		if(_opr.equals("^")) {
			// f(x)= C^h(x) (where C is a positive constant)
			// f'(x)= (log C) C^h(x)*h'(x)
			if((copy._leftNode instanceof LiteralExpression) && !(copy._rightNode instanceof LiteralExpression)) {
				Expression c = copy._leftNode;
				Expression h = copy._rightNode;
				// (log C)
				Expression p1 = new EExpression(null, c, "log");
				// C^h(x)
				Expression p2 = new EExpression(c, h, "^");
				// p1 * p2
				Expression p3 = new MExpression(p1, p2, "*");
				
				return new MExpression(p3, h.differentiate(), "*");
			}

			// f(x) = g(x)^C (where C is a constant)
			// f'(x) = C * g(x)^C-1 * g'(x)
			else if((copy._rightNode instanceof LiteralExpression) && !(copy._leftNode instanceof LiteralExpression)){
				Expression c = copy._rightNode;
				Expression g = copy._leftNode;
				// g(x)^C-1
				Expression p22 = new EExpression(g, new SExpression(c, new LiteralExpression(1), "-"), "^");
				// p22 * g'(x)
				Expression p23 = new MExpression(p22, g.differentiate(), "*");
				
				return new MExpression (c, p23, "*");
			} 
			
			// Returns null if both base and power are functions, as this rule was not mentioned in the assignment.
			// Thus, we inferred that this would not be considered a valid derivative to graph.
			else {
				return null;
			}
		}
		else {
			// f(x) = log g(x)
			// f'(x) = g'(x)/g(x)
			return new MExpression(copy._rightNode.differentiate(), copy._rightNode,"/");
		}
	}

	
}
