package com.lucas.compilador;

import com.lucas.compilador.lexers.Lexer;
import com.lucas.compilador.objetos.Token;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Map;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        Lexer l = new Lexer("/ProgramaTeste.txt");
        Map<Integer, Token> tokens = l.lex();
        logger.info("Tokens: {}", tokens.size());
        tokens.forEach((id, token) -> logger.info(token.toString()));
    }
}
