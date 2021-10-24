package com.lucas.compilador.parsers;

import com.lucas.compilador.lexers.Lexer;
import com.lucas.compilador.objetos.TabelaSimbolos;
import com.lucas.compilador.objetos.Token;

import java.io.IOException;

public abstract class Parser {
    TabelaSimbolos ts;
    Lexer lexer;
    Token t;

    protected Parser() {
        ts = new TabelaSimbolos();
        lexer = new Lexer("/ProgramaTeste.txt");
    }

    public void buscaToken() throws IOException {
        t = lexer.buscarToken();
    }
}
