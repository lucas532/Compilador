package com.lucas.compilador.lexers;

import com.lucas.compilador.objetos.TabelaSimbolos;
import com.lucas.compilador.objetos.Tipo;
import com.lucas.compilador.objetos.Token;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class Lexer {
    PushbackReader reader;
    char ch;
    int linha = 1;
    int colunaAnterior = 1;
    int coluna = 1;

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
        if (codCh == -1)
            return;

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
        StringBuilder lexema = new StringBuilder();
        lerCaractere();

        while (ch != '@') {
            if (!Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch)) {
                String operador = lerOperador();
                int col = coluna - operador.length();
                return switch (operador) {
                    case ":=" -> new Token(Tipo.SATRIBUICAO, ":=", linha, col);
                    case ":" -> new Token(Tipo.STIPO, ":", linha, col);
                    case "+" -> new Token(Tipo.SMAIS, "+", linha, col);
                    case "-" -> new Token(Tipo.SMENOS, "-", linha, col);
                    case "*" -> new Token(Tipo.SMULTIPLICACAO, "*", linha, col);
                    case "/" -> new Token(Tipo.SDIVISAO, "/", linha, col);
                    case ";" -> new Token(Tipo.SPONTO_E_VIRGULA, ";", linha, col);
                    case "." -> new Token(Tipo.SPONTO, ".", linha, col);
                    case "," -> new Token(Tipo.SVIRGULA, ",", linha, col);
                    case "(" -> new Token(Tipo.SABRE_PARENTESIS, "(", linha, col);
                    case ")" -> new Token(Tipo.SFECHA_PARENTESIS, ")", linha, col);
                    default -> new Token(Tipo.SERRO, lexema.toString(), linha, col);
                };
            } else if (Character.isDigit(ch)) {
                String numero = lerNumero();
                int col = coluna - numero.length();
                return new Token(Tipo.SNUMERO, numero, linha, col);
            } else if (Character.isLetter(ch)) {
                String palavra = lerPalavra();
                int col = coluna - palavra.length();
                return switch (palavra) {
                    case "programa" -> new Token(Tipo.SPROGRAMA, palavra, linha, col);
                    case "inteiro" -> new Token(Tipo.SINTEIRO, palavra, linha, col);
                    case "inicio" -> new Token(Tipo.SINICIO, palavra, linha, col);
                    case "fim" -> new Token(Tipo.SFIM, palavra, linha, col);
                    case "var" -> new Token(Tipo.SVAR, palavra, linha, col);
                    case "escreva" -> new Token(Tipo.SESCREVA, palavra, linha, col);
                    default -> new Token(Tipo.SIDENTIFICADOR, palavra, linha, col);
                };
            }

            lerCaractere();
        }

        lexema.append(ch);
        return new Token(Tipo.SERRO, lexema.toString(), linha, coluna);
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
        while (t.getTipo() != Tipo.SERRO && t.getTipo() != Tipo.SFIM);

        return ts.listarTokens();
    }
}
