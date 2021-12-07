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
//        Integer i = 0;
//
//        do {
//            buscarProximoToken();
//            if (tokenAtual == null) {
//                break;
//            }
//            ts.adicionarToken(i, tokenAtual);
//            i++;
//        }
//        while (tokenAtual.getTipo() != Tipo.SERRO);

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
                throw new IOException("identificador esperado");
            }
        } else {
            throw new IOException("declaração de programa esperado");
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
                        throw new IOException(", ou : esperado");
                    } else {
                        buscarProximoToken();
                    }
                } else {
                    throw new IOException("identificador esperado");
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
            throw new IOException("tipo esperado");
    }

    private void checkStatementEnd() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
            buscarProximoToken();
        } else {
            throw new IOException("; esperado");
        }
    }

    private void checkCommandsBlock() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SINICIO) {
            buscarProximoToken();
            checkCommands();
            if (tokenAtual.getTipo() == Tipo.SFIM) {
                buscarProximoToken();
                if (tokenAtual.getTipo() != Tipo.SPONTO) {
                    throw new IOException(". esperado");
                }
            } else {
                throw new IOException("fim esperado");
            }
        } else {
            throw new IOException("inicio esperado");
        }
    }

    private void checkCommands() throws IOException {
        while (tokenAtual.getTipo() != Tipo.SFIM) {
            checkAttribution();
            checkWrite();
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
                checkStatementEnd();
            } else {
                throw new IOException(":= esperado");
            }
        } else {
            throw new IOException("identificador esperado");
        }
    }

    private void checkWrite() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SESCREVA) {
            buscarProximoToken();
            if (tokenAtual.getTipo() == Tipo.SABRE_PARENTESIS) {
                buscarProximoToken();
                if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
                    buscarProximoToken();
                    if (tokenAtual.getTipo() == Tipo.SFECHA_PARENTESIS)
                        buscarProximoToken();
                    else {
                        throw new IOException("Erro sintático");
                    }
                } else {
                    throw new IOException("Erro sintático");
                }
            } else {
                throw new IOException("Erro sintático");
            }
        }
    }

    //Lógica deste método está errada -CONSERTAR
    private void checkExpression() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SABRE_PARENTESIS) {
            buscarProximoToken();
        }
        checkFactor();
        checkOperator();
        if (tokenAtual.getTipo() == Tipo.SFECHA_PARENTESIS) {
            buscarProximoToken();
        }
//        if (tokenAtual.getTipo() == Tipo.SMAIS || tokenAtual.getTipo() == Tipo.SMENOS) {
//            buscarProximoToken();
//            checkTerm();
//        } else {
//            throw new IOException("Erro sintático");
//        }
    }

    private void checkOperator() throws IOException {
        List<Tipo> tiposPermitidos = List.of(Tipo.SMAIS, Tipo.SMENOS, Tipo.SMULTIPLICACAO, Tipo.SDIVISAO);
        while (tiposPermitidos.contains(tokenAtual.getTipo())) {
            buscarProximoToken();
            checkFactor();
        }
        if (tokenAtual.getTipo() != Tipo.SPONTO_E_VIRGULA && tokenAtual.getTipo() != Tipo.SFECHA_PARENTESIS) {
            throw new IOException("operador matemático esperado");
        }
    }

    private void checkFactor() throws IOException {
        List<Tipo> tiposPermitidos = List.of(Tipo.SIDENTIFICADOR, Tipo.SNUMERO, Tipo.SBOOLEANO);
        if (!tiposPermitidos.contains(tokenAtual.getTipo())) {
            throw new IOException("identificador, numero, ou booleano esperado");
        }
        buscarProximoToken();
    }
}
