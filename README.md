# Compilador A2

## Descrição

Projeto de um compilador para a disciplina de compiladores, o projeto engloba apenas o front-end do compilador, isto é,
até a etapa de geração de código intermediário.

Suas principais classes são o [Lexer](src/main/java/com/lucas/compilador/lexers/Lexer.java), responsável por buscar o próximo token e classificá-lo, a partir de um
arquivo `.txt`, e o [Parser](src/main/java/com/lucas/compilador/parsers/Parser.java), que através dos token obtidos do lexer, realiza a análise sintática do código.

Abaixo, o método do Lexer responsável por buscar o próximo token.

```Java
public Token buscarToken() throws IOException {
    ...
}
```

E a seguir, o método do parser que chama o método acima, do Lexer.

```Java
private void buscarProximoToken() throws IOException {
    tokenAtual = lexer.buscarToken();
}
```

## Considerações
