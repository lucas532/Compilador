package com.lucas.compilador;

import com.lucas.compilador.objetos.Token;
import com.lucas.compilador.parsers.Parser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Parser p = new Parser("/ProgramaTeste3.txt");
        Map<Integer, Token> tokens = null;
        try {
            tokens = p.analise();
            logger.info("Tokens: {}", tokens.size());
            tokens.forEach((id, token) -> logger.info(token.toString()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
