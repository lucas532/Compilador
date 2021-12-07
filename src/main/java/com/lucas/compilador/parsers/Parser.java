package com.lucas.compilador.parsers;

import com.lucas.compilador.lexers.Lexer;
import com.lucas.compilador.objetos.TabelaSimbolos;
import com.lucas.compilador.objetos.Tipo;
import com.lucas.compilador.objetos.Token;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Parser {
    TabelaSimbolos ts;
    Token tokenAtual;
    Lexer lexer;
    Integer i = 0;

    public Parser(String nomeArquivo) {
        ts = new TabelaSimbolos();
        lexer = new Lexer(nomeArquivo);
    }

    private void inserirTokenNaTabela() {
        ts.adicionarToken(i, tokenAtual);
        i++;
    }

    private void buscarProximoToken() throws IOException {
        tokenAtual = lexer.buscarToken();
    }

    public Map<Integer, Token> analise() throws IOException {
        checarPrograma();
        return ts.listarTokens();
    }

    private void checarComentario() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SABRE_COMENTARIO) {
            while (tokenAtual.getTipo() != Tipo.SFECHA_COMENTARIO) {
                buscarProximoToken();
            }
            buscarProximoToken();
        }
    }

    private void checarPrograma() throws IOException {
        buscarProximoToken();
        if (tokenAtual.getTipo() == Tipo.SPROGRAMA) {
            buscarProximoToken();
            if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
                inserirTokenNaTabela();
                buscarProximoToken();
                checarFimComando();
                checarBloco();
            } else {
                throw new IOException(String.format("linha:%d, coluna:%d, identificador esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
            }
        } else {
            throw new IOException(String.format("linha:%d, coluna:%d, declaração de programa esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
        }
    }

    private void checarBloco() throws IOException {
        checarDeclaracaoVar();
        checarBlocoComandos();
    }

    private void checarDeclaracaoVar() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SVAR) {
            buscarProximoToken();
            while (true) {
                checarComentario();
                if (tokenAtual.getTipo() == Tipo.SINICIO) {
                    break;
                }
                if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
                    inserirTokenNaTabela();
                    buscarProximoToken();
                    if (tokenAtual.getTipo() == Tipo.STIPO) {
                        buscarProximoToken();
                        checarTipo();
                        checarFimComando();
                    } else if (tokenAtual.getTipo() != Tipo.SVIRGULA) {
                        throw new IOException(String.format("linha:%d, coluna:%d, , ou : esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
                    } else {
                        buscarProximoToken();
                    }
                } else {
                    throw new IOException(String.format("linha:%d, coluna:%d, identificador esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
                }
            }
        }
    }

    private void checarTipo() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SINTEIRO) {
            buscarProximoToken();
        } else if (tokenAtual.getTipo() == Tipo.SBOOLEANO) {
            buscarProximoToken();
        } else
            throw new IOException(String.format("linha:%d, coluna:%d, tipo esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
    }

    private void checarFimComando() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
            buscarProximoToken();
        } else {
            throw new IOException(String.format("linha:%d, coluna:%d, ; esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
        }
    }

    private void checarBlocoComandos() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SINICIO) {
            buscarProximoToken();
            checarComandos();
            if (tokenAtual.getTipo() == Tipo.SFIM) {
                buscarProximoToken();
                if (tokenAtual.getTipo() != Tipo.SPONTO) {
                    throw new IOException(String.format("linha:%d, coluna:%d, . esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
                }
            } else {
                throw new IOException(String.format("linha:%d, coluna:%d, fim esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
            }
        } else {
            throw new IOException(String.format("linha:%d, coluna:%d, inicio esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
        }
    }

    private void checarComandos() throws IOException {
        while (tokenAtual.getTipo() != Tipo.SFIM) {
            checarAtribuicao();
            checarEscreva();
            checarFimComando();
        }
        if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
            buscarProximoToken();
        }
    }

    private void checarAtribuicao() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
            buscarProximoToken();
            if (tokenAtual.getTipo() == Tipo.SATRIBUICAO) {
                buscarProximoToken();
                checarExpressao();
            } else {
                throw new IOException(String.format("linha:%d, coluna:%d, := esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
            }
        }
    }

    private void checarEscreva() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SESCREVA) {
            inserirTokenNaTabela();
            buscarProximoToken();
            if (tokenAtual.getTipo() == Tipo.SABRE_PARENTESIS) {
                buscarProximoToken();
                if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR || tokenAtual.getTipo() == Tipo.SNUMERO) {
                    buscarProximoToken();
                    if (tokenAtual.getTipo() == Tipo.SFECHA_PARENTESIS)
                        buscarProximoToken();
                    else {
                        throw new IOException(String.format("linha:%d, coluna:%d, ) esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
                    }
                } else {
                    throw new IOException(String.format("linha:%d, coluna:%d, identificador ou texto esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
                }
            } else {
                throw new IOException(String.format("linha:%d, coluna:%d, ( esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
            }
        }
    }

    private void checarExpressao() throws IOException {
        checarFator();
        if (tokenAtual.getTipo() != Tipo.SPONTO_E_VIRGULA) {
            checarTermo();
        }
    }

    private void checarTermo() throws IOException {
        List<Tipo> tiposPermitidos = List.of(Tipo.SMAIS, Tipo.SMENOS, Tipo.SMULTIPLICACAO, Tipo.SDIVISAO, Tipo.SABRE_PARENTESIS, Tipo.SFECHA_PARENTESIS);
        if (!tiposPermitidos.contains(tokenAtual.getTipo())) {
            throw new IOException(String.format("linha:%d, coluna:%d, operador esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
        }
        while (tiposPermitidos.contains(tokenAtual.getTipo())) {
            buscarProximoToken();
            checarFator();
        }
    }

    private void checarFator() throws IOException {
        List<Tipo> tiposPermitidos = List.of(Tipo.SIDENTIFICADOR, Tipo.SNUMERO, Tipo.SBOOLEANO, Tipo.SABRE_PARENTESIS, Tipo.SFECHA_PARENTESIS);
        if (!tiposPermitidos.contains(tokenAtual.getTipo())) {
            throw new IOException(String.format("linha:%d, coluna:%d, identificador, numero ou booleano esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
        }
        while (tiposPermitidos.contains(tokenAtual.getTipo())) {
            buscarProximoToken();
        }
    }
}
