package com.lucas.compilador.parsers;

import com.lucas.compilador.lexers.Lexer;
import com.lucas.compilador.objetos.TabelaSimbolos;
import com.lucas.compilador.objetos.Tipo;
import com.lucas.compilador.objetos.Token;

import java.io.IOException;
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

    public void buscarProximoToken() throws IOException {
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

    public void checkComment() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SABRE_COMENTARIO) {
            while (tokenAtual.getTipo() != Tipo.SFECHA_COMENTARIO) {
                buscarProximoToken();
            }
            buscarProximoToken();
        }
    }

    public void checkProgram() throws IOException {
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
                throw new IOException("Identificador esperado");
            }
        } else {
            throw new IOException("Declaração de programa esperado");
        }
    }

    public void checkBlock() throws IOException {
        checkVarDeclaration();
        checkCommandsBlock();
    }

    public void checkVarDeclaration() throws IOException {
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
                    throw new IOException("Identificador esperado");
                }
            }

        }
    }

    public void checkType() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SINTEIRO) {
            buscarProximoToken();
        } else if (tokenAtual.getTipo() == Tipo.SBOOLEANO) {
            buscarProximoToken();
        } else
            throw new IOException("Tipo esperado");
    }

    private void checkStatementEnd() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
            buscarProximoToken();
        } else {
            throw new IOException("; esperado");
        }
    }

    public void checkCommandsBlock() throws IOException {
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

    public void checkCommands() throws IOException {
        while (tokenAtual.getTipo() != Tipo.SFIM) {
            checkAttribution();
            if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
                buscarProximoToken();
                checkCommands();
            } else {
                throw new IOException("Erro sintático");
            }

            checkWrite();
            if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
                buscarProximoToken();
                checkCommands();
            } else {
                throw new IOException("Erro sintático");
            }

            if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
                buscarProximoToken();
                checkCommands();
            } else {
                throw new IOException("Erro sintático");
            }
        }
        if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
            buscarProximoToken();
        }
    }

    public void checkAttribution() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
            buscarProximoToken();
            if (tokenAtual.getTipo() == Tipo.SATRIBUICAO) {
                buscarProximoToken();
                checkExpression();
            } else {
                throw new IOException(":= esperado");
            }
        }
    }

    public void checkWrite() throws IOException {
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

    public void checkExpression() throws IOException {
        checkSimpleExpression();
        checkTerm();
        checkFactor();
    }

    //Lógica deste método está errada -CONSERTAR
    private void checkSimpleExpression() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR || tokenAtual.getTipo() == Tipo.SNUMERO) {
            buscarProximoToken();
            checkTerm();
            if (tokenAtual.getTipo() == Tipo.SMAIS || tokenAtual.getTipo() == Tipo.SMENOS) {
                buscarProximoToken();
                checkTerm();
            } else {
                throw new IOException("Erro sintático");
            }
        } else {
            buscarProximoToken();
            checkTerm();
            if (tokenAtual.getTipo() == Tipo.SMAIS || tokenAtual.getTipo() == Tipo.SMENOS) {
                buscarProximoToken();
                checkTerm();
            } else {
                throw new IOException("Erro sintático");
            }
        }
    }

    private void checkTerm() throws IOException {
        checkFactor();
        while (tokenAtual.getTipo() == Tipo.SMULTIPLICACAO || tokenAtual.getTipo() == Tipo.SDIVISAO) {
            buscarProximoToken();
            checkFactor();
        }
    }

    private void checkFactor() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SNUMERO || tokenAtual.getTipo() == Tipo.SVAR || tokenAtual.getTipo() == Tipo.SBOOLEANO) {
            buscarProximoToken();
        }
    }
}
