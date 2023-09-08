import java.util.function.*;

public class SimpleExpressionParser implements ExpressionParser {
        /*
         * Attempts to create an expression tree from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * Grammar:
	 * S -> A | P
	 * A -> A+M | A-M | M
	 * M -> M*E | M/E | E
	 * E -> P^E | P | log(P)
	 * P -> (S) | L | V
	 * L -> <float>
	 * V -> x
         * @param str the string to parse into an expression tree
         * @return the Expression object representing the parsed expression tree
         */
	public Expression parse (String str) throws ExpressionParseException {
		str = str.replaceAll(" ", "");
		
		Expression expression = parseS(str);
		if (expression == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}
		

		return expression;
	}
	
	/**
	 * Helps parse a string by separating it based on a given operation, and parsing each side with given
	 * parse lambda functions. The function will iterate through all instances of the operator in the given string.
	 * If the string is valid, the function will return an array of the operand expressions.
	 * If the string is not valid, the function will return null.
	 * @param str the string to be parsed
	 * @param op the given operator string
	 * @param m1 the parsing function for the left of the operator
	 * @param m2 the parsing function for the right of the operator
	 * @return a array of the operand expressions
	 */
	protected Expression[] parseHelper (String str, char op, Function<String, Expression> m1, Function<String, Expression> m2) {
		Expression[] output = new Expression[2];
		for (int i = 0; i < str.length(); i++) {
			// If the program finds an instance of the operator, it checks if the operands are valid expressions of the given type. 
			// If they are, it returns the operands. If they aren't, the program keeps searching for an instance of the operator.
			if (str.charAt(i) == op && m1.apply(str.substring(0, i)) != null && m2.apply(str.substring(i+1)) != null) {
				output[0] = m1.apply(str.substring(0, i));
				output[1] = m2.apply(str.substring(i+1));
				return output;
			}
		}
		
		// If the program determines that the expression is not valid, it communicates this with a null object.
		return null;
	}
	
	/**
	 * Helper function that checks if the given string is a valid logarithm expression. If the string is valid, the function returns the nested expression.
	 * If the string is not a valid log expression, the function returns null.
	 * @param str the given string
	 * @return the nested expression or null
	 */
	protected Expression logHelper (String str) {
		// The function checks if the expression meets a minimum length and contains "log"
		if (str.length() >= 3 && str.substring(0, 3).equals("log")) {
			
			int index = str.indexOf("log");
			// The function checks if the nested expression is a valid expression.
			if (parseP(str.substring(index + 3)) != null) {
				return parseP(str.substring(index + 3));
			}
		}
		
		// If the program determines that the expression is not valid, it communicates this with a null object.
		return null;
	}

	/**
	 * Helper function that checks if the given string is a valid parentheses expression. If it is the function
	 * returns the nested expression. If it isn't, the function returns null.
	 * @param str the given string
	 * @return the nested expression or null
	 */
	protected Expression parenthesesHelper (String str) {
		// Strips any whitespace that might confuse the program
		String condensed = str.strip();
		int condensedLen = condensed.length();
		// Checks if the string is wrapped by ()
		if (condensedLen >= 3 && condensed.charAt(0) == '(' && condensed.charAt(condensedLen - 1) == ')') {
			// Checks if nested expression is valid
			if (parseS(condensed.substring(1, condensedLen - 1)) != null) {
				return parseS(condensed.substring(1, condensedLen - 1));
			} else {
				// Nested expression is not valid
				return null;
			}
		} else {
			// String is not wrapped by ()
			return null;
		}
	}
	
	/**
	 * Checks if the given string meets the S (start/sum/subtraction) production rule (see above). If it does, the expression is parsed and returned
	 * to be stored in a tree. If it isn't, the function returns null.
	 * @param str the given string
	 * @return parsed expression or null
	 */
	protected Expression parseS (String str) {
		Expression expression;
		Expression[] plusOperands = parseHelper(str, '+', this::parseS, this::parseM);
		Expression[] minusOperands = parseHelper(str, '-', this::parseS, this::parseM);
		Expression multDivExpression = parseM(str);

		// S+M
		if (plusOperands != null) {
			expression = new SExpression(plusOperands[0], plusOperands[1], "+");
		} 
		// S-M
		else if (minusOperands != null) {
			expression = new SExpression(minusOperands[0], minusOperands[1], "-");
		} 
		// M
		else if (multDivExpression != null) {
			expression = multDivExpression;
		} 
		// Meets none of the rules, not a valid expression.
		else {
			expression = null;
		}
		
		return expression;
	}
	
	/**
	 * Checks if the given string meets the M (multiplication/division) production rule (see above). If it does, the expression is parsed and returned
	 * to be stored in a tree. If it isn't, the function returns null.
	 * @param str the given string
	 * @return parsed expression or null
	 */
	protected Expression parseM (String str) {
		Expression expression;
		Expression[] multOperands = parseHelper(str, '*', this::parseM, this::parseE);
		Expression[] divOperands = parseHelper(str, '/', this::parseM, this::parseE);
		Expression exponentExpression = parseE(str);
		
		// M*E
		if (multOperands != null) {
			expression = new MExpression(multOperands[0], multOperands[1], "*");
		} 
		// M/E
		else if (divOperands != null) {
			expression = new MExpression(divOperands[0], divOperands[1], "/");
		} 
		// E
		else if (exponentExpression != null) {
			expression = exponentExpression;
		} 
		// Meets none of the rules, not a valid expression
		else {
			expression = null;
		}
	
		return expression;
	}
	
	/**
	 * Checks if the given string meets the E (exponents/logarithms) production rule (see above). If it does, the expression is parsed and returned
	 * to be stored in a tree. If it isn't, the function returns null.
	 * @param str the given string
	 * @return parsed expression or null
	 */
	protected Expression parseE (String str) {
		Expression expression;
		Expression[] exponentOperands = parseHelper(str, '^', this::parseP, this::parseE);
		Expression logExpression = logHelper(str);
		Expression parenExpression = parseP(str);
		
		// P^E
		if (exponentOperands != null) {
			expression = new EExpression(exponentOperands[0], exponentOperands[1], "^");
		} 
		// log P
		else if (logExpression != null) {
			expression = new EExpression(null, logExpression, "log");
		} 
		// P
		else if (parenExpression != null) {
			expression = parenExpression;
		} 
		// Meets none of the rules, not a valid expression
		else {
			expression = null;
		}
		
		return expression;
	}
	
	/**
	 * Checks if the given string meets the P (parentheses) production rule (see above). If it does, the expression is parsed and returned
	 * to be stored in a tree. If it isn't, the function returns null.
	 * @param str the given string
	 * @return parsed expression or null
	 */
	protected Expression parseP (String str) {
		Expression expression;
		Expression parenExpression = parenthesesHelper(str);
		
		// (S)
		if (parenExpression != null) {
			expression = new PExpression(parenExpression);
		} 
		// L
		else if (parseL(str) != null) {
			expression = parseL(str);
		} 
		// V
		else if (parseV(str) != null) {
			expression = parseV(str);
		} 
		// Meets none of the rules, not a valid expression
		else {
			expression = null;
		}
		
		return expression;
	}
	
	/**
	 * Checks if the given string meets the V (variable or 'x') production rule (see above). If it does, the expression is parsed and returned
	 * to be stored in a tree. If it isn't, the function returns null.
	 * @param str the given string
	 * @return parsed expression or null
	 */
	protected Expression parseV (String str) {
		// a VariableExpression can only ever take on the value "x"
		if (str.strip().equals("x")) {
			return new VariableExpression();
		} else {
			return null;
		}
	}

	/**
	 * Checks if the given string meets the L (literal or double) production rule (see above). If it does, the expression is parsed and returned
	 * to be stored in a tree. If it isn't, the function returns null.
	 * @param str the given string
	 * @return parsed expression or null
	 */
	protected /*Literal*/Expression parseL (String str) {
		// From https://stackoverflow.com/questions/3543729/how-to-check-that-a-string-is-parseable-to-a-double/22936891:
		final String Digits     = "(\\p{Digit}+)";
		final String HexDigits  = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally 
		// signed decimal integer.
		final String Exp        = "[eE][+-]?"+Digits;
		final String fpRegex    =
		    ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
		    "[+-]?(" +         // Optional sign character
		    "NaN|" +           // "NaN" string
		    "Infinity|" +      // "Infinity" string

		    // A decimal floating-point string representing a finite positive
		    // number without a leading sign has at most five basic pieces:
		    // Digits . Digits ExponentPart FloatTypeSuffix
		    // 
		    // Since this method allows integer-only strings as input
		    // in addition to strings of floating-point literals, the
		    // two sub-patterns below are simplifications of the grammar
		    // productions from the Java Language Specification, 2nd 
		    // edition, section 3.10.2.

		    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

		    // . Digits ExponentPart_opt FloatTypeSuffix_opt
		    "(\\.("+Digits+")("+Exp+")?)|"+

		    // Hexadecimal strings
		    "((" +
		    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "(\\.)?)|" +

		    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

		    ")[pP][+-]?" + Digits + "))" +
		    "[fFdD]?))" +
		    "[\\x00-\\x20]*");// Optional trailing "whitespace"

		if (str.matches(fpRegex)) {
			return new LiteralExpression(Double.parseDouble(str));
			// TODO: Once you implement LiteralExpression, replace the line above with the line below:
			// return new LiteralExpression(str);
		}
		return null;
	}

	public static void main (String[] args) throws ExpressionParseException {
		final ExpressionParser parser = new SimpleExpressionParser();
		System.out.println(parser.parse("10*2+12-4.").convertToString(0));
	}
}




