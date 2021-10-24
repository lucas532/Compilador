package com.lucas.compilador.objetos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Token {
    Tipo tipo;
    String lexema;
    int linha;
    int coluna;
}
