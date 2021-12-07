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

    private void buscarProximoToken() throws IOException {
        tokenAtual = lexer.buscarToken();
    }

    public Map<Integer, Token> analise() throws IOException {
        checkProgram();
        return ts.listarTokens();
    }

    private void checkComment() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SABRE_COMENTARIO) {
            while (tokenAtual.getTipo() != Tipo.SFECHA_COMENTARIO) {
                buscarProximoToken();
            }
            buscarProximoToken();
        }
    }

    private void checkProgram() throws IOException {
        buscarProximoToken();
        if (tokenAtual.getTipo() == Tipo.SPROGRAMA) {
            buscarProximoToken();
            if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
                ts.adicionarToken(i, tokenAtual);
                i++;
                buscarProximoToken();
                checkStatementEnd();
                checkBlock();
            } else {
                throw new IOException(String.format("linha:%d, coluna:%d, identificador esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
            }
        } else {
            throw new IOException(String.format("linha:%d, coluna:%d, declaração de programa esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
        }
    }

    private void checkBlock() throws IOException {
        checkVarDeclaration();
        checkCommandsBlock();
    }

    private void checkVarDeclaration() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SVAR) {
            buscarProximoToken();
            while (true) {
                checkComment();
                if (tokenAtual.getTipo() == Tipo.SINICIO) {
                    break;
                }
                if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
                    buscarProximoToken();
                    if (tokenAtual.getTipo() == Tipo.STIPO) {
                        buscarProximoToken();
                        checkType();
                        checkStatementEnd();
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

    private void checkType() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SINTEIRO) {
            buscarProximoToken();
        } else if (tokenAtual.getTipo() == Tipo.SBOOLEANO) {
            buscarProximoToken();
        } else
            throw new IOException(String.format("linha:%d, coluna:%d, tipo esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
    }

    private void checkStatementEnd() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
            buscarProximoToken();
        } else {
            throw new IOException(String.format("linha:%d, coluna:%d, ; esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
        }
    }

    private void checkCommandsBlock() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SINICIO) {
            buscarProximoToken();
            checkCommands();
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

    private void checkCommands() throws IOException {
        while (tokenAtual.getTipo() != Tipo.SFIM) {
            checkAttribution();
            checkWrite();
            checkStatementEnd();
        }
        if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
            buscarProximoToken();
        }
    }

    private void checkAttribution() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
            buscarProximoToken();
            if (tokenAtual.getTipo() == Tipo.SATRIBUICAO) {
                buscarProximoToken();
                checkExpression();
            } else {
                throw new IOException(String.format("linha:%d, coluna:%d, := esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
            }
        }
    }

    private void checkWrite() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SESCREVA) {
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

    private void checkExpression() throws IOException {
        checkFactor();
        if (tokenAtual.getTipo() != Tipo.SPONTO_E_VIRGULA) {
            checkOperator();
        }
    }

    private void checkOperator() throws IOException {
        List<Tipo> tiposPermitidos = List.of(Tipo.SMAIS, Tipo.SMENOS, Tipo.SMULTIPLICACAO, Tipo.SDIVISAO, Tipo.SABRE_PARENTESIS, Tipo.SFECHA_PARENTESIS);
        if (!tiposPermitidos.contains(tokenAtual.getTipo())) {
            throw new IOException(String.format("linha:%d, coluna:%d, operador esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
        }
        while (tiposPermitidos.contains(tokenAtual.getTipo())) {
            buscarProximoToken();
            checkFactor();
        }
    }

    private void checkFactor() throws IOException {
        List<Tipo> tiposPermitidos = List.of(Tipo.SIDENTIFICADOR, Tipo.SNUMERO, Tipo.SBOOLEANO, Tipo.SABRE_PARENTESIS, Tipo.SFECHA_PARENTESIS);
        if (!tiposPermitidos.contains(tokenAtual.getTipo())) {
            throw new IOException(String.format("linha:%d, coluna:%d, identificador, numero ou booleano esperado", tokenAtual.getLinha(), tokenAtual.getColuna()));
        }
        while (tiposPermitidos.contains(tokenAtual.getTipo())) {
            buscarProximoToken();
        }
    }
}
