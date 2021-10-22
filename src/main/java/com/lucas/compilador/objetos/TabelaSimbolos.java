package com.lucas.compilador.objetos;

import java.util.LinkedHashMap;
import java.util.Map;

public class TabelaSimbolos {
    Map<Integer, Token> ts;

    public TabelaSimbolos() {
        ts = new LinkedHashMap<>();
    }

    public void adicionarToken(Integer chave, Token t) {
        ts.put(chave, t);
    }

    public Token obterToken(Integer chave) {
        return ts.get(chave);
    }

    public Map<Integer, Token> listarTokens() {
        return ts;
    }
}
