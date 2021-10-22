package com.lucas.compilador.lexers;

import java.util.LinkedList;

/**
 * Lexical analyzer for Java arithmetic expressions
 */
public class ArithmeticLexer {

    /**
     * Gets an operand starting at the specified index
     * Parses both variable names and numeric literals
     *
     * @param operand The string to be parsed
     * @param index   The starting index
     * @return The operand
     */
    private static String getOperand(String operand, int index) {
        int i = index;
        for (; i < operand.length(); ) {
            if (Character.isLetterOrDigit(operand.charAt(i))) {
                i++;
            } else {
                return operand.substring(index, i);
            }
        }
        return operand.substring(index, i);
    }

    /**
     * Lexically analyzes the expression
     *
     * @param expression The expression to be analyzed
     * @return A linked list of token objects
     */
    private static LinkedList<Token<Type, String>> analyze(String expression) {
        LinkedList<Token<Type, String>> tokens = new LinkedList<>();
        for (int i = 0; i < expression.length(); ) {
            char currentCharacter = expression.charAt(i);
            switch (currentCharacter) {
                case '+':
                    tokens.add(new Token<>(Type.ADD, String.valueOf(currentCharacter)));
                    i++;
                    break;
                case '-':
                    tokens.add(new Token<>(Type.SUBTRACT, String.valueOf(currentCharacter)));
                    i++;
                    break;
                case '*':
                    tokens.add(new Token<>(Type.MULTIPLY, String.valueOf(currentCharacter)));
                    i++;
                    break;
                case '/':
                    tokens.add(new Token<>(Type.DIVIDE, String.valueOf(currentCharacter)));
                    i++;
                    break;
                case '%':
                    tokens.add(new Token<>(Type.REMAINDER, String.valueOf(currentCharacter)));
                    i++;
                    break;
                default:
                    if (Character.isWhitespace(currentCharacter)) {
                        i++;
                    } else {

                        //Get the operand and increment the counter by the operand's length to continue parsing the expression
                        String operand = ArithmeticLexer.getOperand(expression, i);
                        i += operand.length();
                        tokens.add(new Token<>(Type.OPERAND, operand));
                    }
                    break;
            }
        }
        return tokens;
    }

    /**
     * Lexically analyzes input and prints out tokens and their associated value
     *
     * @param args The first parameter should be the expression to analyze
     */
    public static void main(String[] args) {
        String code = "1+1/2 teste asdsd";
        LinkedList<Token<Type, String>> tokens = ArithmeticLexer.analyze(code);
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    /**
     * Types for this lexical analyzer to analyze
     */
    private static enum Type {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, REMAINDER, OPERAND
    }

    /**
     * Token class which holds the type and value of the parsed token
     *
     * @param <TT> Token type
     * @param <TV> Token value
     */
    private static class Token<TT, TV> {
        private final TT t;
        private final TV value;

        /**
         * Token constructor
         *
         * @param t    Token type
         * @param text Token value
         */
        public Token(TT t, TV value) {
            this.t = t;
            this.value = value;
        }

        /**
         * Returns the token's value and lexical category
         */
        @Override
        public String toString() {
            return "Token text: " + this.value + System.getProperty("line.separator") + "Token lexical category: " + this.t;
        }
    }
}
