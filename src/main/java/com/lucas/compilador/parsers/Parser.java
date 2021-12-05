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
        CheckProgram();
        return ts.listarTokens();
    }

    public void CheckComment() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SABRE_COMENTARIO) {
            buscarProximoToken();
            while (tokenAtual.getTipo() != Tipo.SFECHA_COMENTARIO) {
                buscarProximoToken();
            }
        }
    }

    public void CheckProgram() throws IOException {
        buscarProximoToken();
        if (tokenAtual.getTipo() == Tipo.SPROGRAMA) {
            buscarProximoToken();
            if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
                ts.adicionarToken(i, tokenAtual);
                i++;
                buscarProximoToken();
                if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
                    buscarProximoToken();
                    CheckBlock();
                } else {
                    throw new IOException("Erro sintático");
                }
            } else {
                throw new IOException("Erro sintático");
            }
        } else {
            throw new IOException("Erro sintático");
        }
    }

    public void CheckBlock() throws IOException {
        CheckVarDeclaration();
        CheckCommands();
    }

    public void CheckVarDeclaration() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SVAR) {
            buscarProximoToken();
            while (tokenAtual.getTipo() != Tipo.STIPO) {
                buscarProximoToken();
                if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
                    buscarProximoToken();
                    if (tokenAtual.getTipo() == Tipo.SVIRGULA) {
                        buscarProximoToken();
                    } else {
                        throw new IOException("Erro sintático");
                    }
                } else {
                    throw new IOException("Erro sintático");
                }
            }
            buscarProximoToken();
            CheckType();
        }
    }

    public void CheckType() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SINTEIRO) {
            buscarProximoToken();
        } else if (tokenAtual.getTipo() == Tipo.SBOOLEANO)
            buscarProximoToken();
        else
            throw new IOException("Erro sintático");
    }

    public void CheckCommands() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SINICIO) {
            buscarProximoToken();
            CheckCommand();
            if (tokenAtual.getTipo() == Tipo.SFIM) {
                buscarProximoToken();
                if (tokenAtual.getTipo() != Tipo.SPONTO) {
                    throw new IOException("Erro sintático");
                }
            } else {
                throw new IOException("Erro sintático");
            }
        } else {
            throw new IOException("Erro sintático");
        }
    }

    public void CheckCommand() throws IOException {
        while (tokenAtual.getTipo() != Tipo.SFIM) {
            CheckAttribution();
            if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
                buscarProximoToken();
                CheckCommand();
            } else {
                throw new IOException("Erro sintático");
            }

            CheckWrite();
            if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
                buscarProximoToken();
                CheckCommand();
            } else {
                throw new IOException("Erro sintático");
            }

            if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
                buscarProximoToken();
                CheckCommand();
            } else {
                throw new IOException("Erro sintático");
            }
        }
        if (tokenAtual.getTipo() == Tipo.SPONTO_E_VIRGULA) {
            buscarProximoToken();
        }
    }

    public void CheckWrite() throws IOException {
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

    public void CheckAttribution() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SIDENTIFICADOR) {
            buscarProximoToken();
            if (tokenAtual.getTipo() == Tipo.SATRIBUICAO) {
                buscarProximoToken();
                CheckExpression();
            } else {
                throw new IOException("Erro sintático");
            }
        }
    }

    public void CheckExpression() throws IOException {
        CheckSimpleExpression();
//        CheckRelacionalOperator();
        CheckTerm();
        CheckFactor();
    }

//    private void CheckRelacionalOperator() {
//        if (tokenAtual.getTipo() == Tipo.SIGUAL) {
//            buscarProximoToken();
//        }
//    }

    private void CheckSimpleExpression() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SMAIS || tokenAtual.getTipo() == Tipo.SMENOS) {
            buscarProximoToken();
            CheckTerm();
            if (tokenAtual.getTipo() == Tipo.SMAIS || tokenAtual.getTipo() == Tipo.SMENOS) {
                buscarProximoToken();
                CheckTerm();
            } else {
                throw new IOException("Erro sintático");
            }
        } else {
            buscarProximoToken();
            CheckTerm();
            if (tokenAtual.getTipo() == Tipo.SMAIS || tokenAtual.getTipo() == Tipo.SMENOS) {
                buscarProximoToken();
                CheckTerm();
            } else {
                throw new IOException("Erro sintático");
            }
        }
    }

    private void CheckTerm() throws IOException {
        CheckFactor();
        while (tokenAtual.getTipo() == Tipo.SMULTIPLICACAO || tokenAtual.getTipo() == Tipo.SDIVISAO) {
            buscarProximoToken();
            CheckFactor();
        }
    }

    private void CheckFactor() throws IOException {
        if (tokenAtual.getTipo() == Tipo.SNUMERO || tokenAtual.getTipo() == Tipo.SVAR || tokenAtual.getTipo() == Tipo.SBOOLEANO) {
            buscarProximoToken();
        }
    }
}
