package com.lucas.compilador.lexers;

import com.lucas.compilador.objetos.TabelaSimbolos;
import com.lucas.compilador.objetos.Tipo;
import com.lucas.compilador.objetos.Token;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class Lexer {
    final PushbackReader reader;
    Character ch;
    Integer linha = 1;
    Integer colunaAnterior = 1;
    Integer coluna = 1;
    boolean eof = false;

    public Lexer(String nomeArquivo) {
        InputStream inputStream = this.getClass().getResourceAsStream(nomeArquivo);
        reader = new PushbackReader(new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8)));
    }

    public void devolverCaractere() throws IOException {
        reader.unread(ch);
        if (ch == '\n') {
            linha--;
            coluna = colunaAnterior;
        } else
            coluna--;
    }

    public void lerCaractere() throws IOException {
        int codCh = reader.read();
        if (codCh == -1) {
            eof = true;
            return;
        }

        ch = (char) codCh;
        if (ch == '\n') {
            linha++;
            coluna = 1;
        } else
            coluna++;
    }

    public String lerPalavra() throws IOException {
        StringBuilder palavra = new StringBuilder();
        while (Character.isLetter(ch) || ch == '_') {
            colunaAnterior = coluna;
            palavra.append(ch);
            lerCaractere();
        }

        devolverCaractere();
        return palavra.toString();
    }

    public String lerNumero() throws IOException {
        StringBuilder numero = new StringBuilder();
        while (Character.isDigit(ch)) {
            colunaAnterior = coluna;
            numero.append(ch);
            lerCaractere();
        }

        devolverCaractere();
        return numero.toString();
    }

    public String lerOperador() throws IOException {
        StringBuilder operador = new StringBuilder();
        operador.append(ch);

        if (ch == ':') {
            lerCaractere();
            if (ch == '=') {
                operador.append(ch);
            } else {
                devolverCaractere();
            }
        }

        return operador.toString();
    }

    public Token buscarToken() throws IOException {
        String lexema;
        int colLex;
        lerCaractere();

        while (Character.isWhitespace(ch)) {
            lerCaractere();
        }
        
        if (!Character.isLetterOrDigit(ch)) {
            lexema = lerOperador();
            colLex = coluna - lexema.length();
            return switch (lexema) {
                case ":=" -> new Token(Tipo.SATRIBUICAO, lexema, linha, colLex);
                case ":" -> new Token(Tipo.STIPO, lexema, linha, colLex);
                case "+" -> new Token(Tipo.SMAIS, lexema, linha, colLex);
                case "-" -> new Token(Tipo.SMENOS, lexema, linha, colLex);
                case "*" -> new Token(Tipo.SMULTIPLICACAO, lexema, linha, colLex);
                case "/" -> new Token(Tipo.SDIVISAO, lexema, linha, colLex);
                case ";" -> new Token(Tipo.SPONTO_E_VIRGULA, lexema, linha, colLex);
                case "." -> new Token(Tipo.SPONTO, lexema, linha, colLex);
                case "," -> new Token(Tipo.SVIRGULA, lexema, linha, colLex);
                case "(" -> new Token(Tipo.SABRE_PARENTESIS, lexema, linha, colLex);
                case ")" -> new Token(Tipo.SFECHA_PARENTESIS, lexema, linha, colLex);
                case "{" -> new Token(Tipo.SABRE_COMENTARIO, lexema, linha, colLex);
                case "}" -> new Token(Tipo.SFECHA_COMENTARIO, lexema, linha, colLex);
                default -> new Token(Tipo.SERRO, lexema, linha, colLex);
            };
        } else if (Character.isDigit(ch)) {
            lexema = lerNumero();
            colLex = coluna - lexema.length();
            return new Token(Tipo.SNUMERO, lexema, linha, colLex);
        } else if (Character.isLetter(ch)) {
            lexema = lerPalavra();
            colLex = coluna - lexema.length();
            return switch (lexema) {
                case "programa" -> new Token(Tipo.SPROGRAMA, lexema, linha, colLex);
                case "inteiro" -> new Token(Tipo.SINTEIRO, lexema, linha, colLex);
                case "inicio" -> new Token(Tipo.SINICIO, lexema, linha, colLex);
                case "fim" -> new Token(Tipo.SFIM, lexema, linha, colLex);
                case "var" -> new Token(Tipo.SVAR, lexema, linha, colLex);
                case "escreva" -> new Token(Tipo.SESCREVA, lexema, linha, colLex);
                default -> new Token(Tipo.SIDENTIFICADOR, lexema, linha, colLex);
            };
        } else {
            lexema = String.valueOf(ch);
            return new Token(Tipo.SERRO, lexema, linha, coluna);
        }
    }

    public Map<Integer, Token> lex() throws IOException {
        TabelaSimbolos ts = new TabelaSimbolos();
        Token t;
        Integer i = 0;

        do {
            t = buscarToken();
            ts.adicionarToken(i, t);
            i++;
        }
        while (t.getTipo() != Tipo.SERRO && t.getTipo() != Tipo.SFIM && !eof);

        return ts.listarTokens();
    }
}
