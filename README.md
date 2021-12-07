# Compilador A2

## Descrição

Projeto de um compilador para a disciplina de compiladores, o projeto engloba apenas o front-end do compilador, isto é,
até a etapa de geração de código intermediário.

Suas principais classes são o [Lexer](src/main/java/com/lucas/compilador/lexers/Lexer.java), responsável por buscar o
próximo token e classificá-lo, a partir de um arquivo `.txt`, e
o [Parser](src/main/java/com/lucas/compilador/parsers/Parser.java), que através dos token obtidos do lexer, realiza a
análise sintática do código.

Abaixo, o método do Lexer responsável por buscar o próximo token.

```Java
public Token buscarToken()throws IOException {
    ...
}
```

E a seguir, o método do parser que chama o método acima, do Lexer.

```Java
private void buscarProximoToken()throws IOException {
    tokenAtual=lexer.buscarToken();
}
```

Por fim, o método `main`, que realiza a chamada do parser passando o nome do arquivo a ser analisado.

```Java
public static void main(String[]args) {
    Parser p=new Parser("/ProgramaTeste.txt");
    Map<Integer, Token> tokens;
    try {
        tokens=p.analise();
        logger.info("Tokens: {}",tokens.size());
        tokens.forEach((id,token)->logger.info(token.toString()));
    } catch(IOException e) {
        logger.error(e.getMessage());
    }
}
```

## Como rodar a aplicação

### Requisitos

- JDK
- Gradle
- Lombok
- Log4J

Basta abrir a aplicação em sua IDE de preferência, buildar o arquivo [build.gradle](build.gradle), e então executar o
método [main](src/main/java/com/lucas/compilador/Main.java).

## Considerações

Infelizmente, não conseguimos implementar a etapa de geração de código intermediário, embora tenhamos compreendido a
teoria, não conseguimos realizar na prática, no entanto, o restante do solicitado foi implementado.
