package com.lucas.compilador.parsers;

import com.lucas.compilador.lexers.Lexer;
import com.lucas.compilador.objetos.TabelaSimbolos;
import com.lucas.compilador.objetos.Token;

import java.io.IOException;

public abstract class Parser {
    TabelaSimbolos ts;
    Lexer lexer;
    Token t;

    public Parser(String nomeArquivo) {
        ts = new TabelaSimbolos();
        lexer = new Lexer(nomeArquivo);
    }

    public void buscaToken() throws IOException {
        t = lexer.buscarToken();
    }
}
