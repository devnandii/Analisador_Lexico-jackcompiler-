# Analisador Léxico da Linguagem Jack

<p align="center"> <img src="analisador_lexico_animado.gif" alt="Animação demonstrando o funcionamento do analisador léxico" width="800"> </p>

## 1. Integrantes

* **Irlanda Hildeney Oliveira Teixeira**
* **Isaque Mendonça Costa**

**Disciplina:** Compiladores

## 2. Descrição do projeto

Este projeto implementa um analisador léxico para a linguagem Jack, desenvolvido em Java.

O analisador léxico é responsável por ler o código-fonte de um arquivo `.jack`, identificar os elementos que compõem a linguagem e classificá-los em tokens. Ao final do processamento, os tokens identificados são organizados e armazenados em um arquivo XML.

## 3. Funcionalidades

O programa realiza as seguintes operações:

* Identificação de palavras reservadas (*keywords*).
* Identificação de símbolos da linguagem (*symbols*).
* Identificação de constantes inteiras (*integer constants*).
* Identificação de constantes de texto (*string constants*).
* Identificação de nomes e variáveis (*identifiers*).
* Ignora espaços em branco e comentários de linha (`//`) e de bloco (`/* ... */`).
* Geração de um arquivo XML contendo os tokens identificados.
* Verificação de erros léxicos, como números inteiros fora do intervalo permitido e strings ou comentários de bloco não terminados.

## 4. Tecnologias utilizadas

* **Linguagem de programação:** Java.
* **Formato de saída:** XML.


## 5. Estrutura do projeto

```text
Analisador_Lexico-jackcompiler/
├── src/
│   ├── JackTokenizer.java
│   ├── Main.java
│   └── Token.java
├── test/
│   ├── expected_xml/
│   │   ├── MainT.xml
│   │   ├── SquareT.xml
│   │   └── SquareGameT.xml
│   └── jack_files/
│       ├── Main.jack
│       ├── Square.jack
│       └── SquareGame.jack
├── .gitignore
└── README.md
```

## 6. Como compilar

É necessário ter o JDK instalado e configurado no sistema.

Na pasta principal do projeto, execute:

```bash
javac -encoding UTF-8 src/*.java
```


## 7. Como executar

O programa recebe o caminho do arquivo `.jack` como primeiro argumento e, opcionalmente, o caminho do arquivo XML de saída como segundo argumento.

**Exemplo:**

```powershell
java -cp src Main .\test\jack_files\Main.jack .\test\generated\MainT.xml
```

Para executar com outro arquivo:

```powershell
java -cp src Main .\test\jack_files\Square.jack .\test\generated\SquareT.xml
```

```powershell
java -cp src Main .\test\jack_files\SquareGame.jack .\test\generated\SquareGameT.xml
```

Caso o caminho de saída não seja informado, o programa gera automaticamente um arquivo XML com o sufixo `T.xml` no diretório do arquivo de entrada.

## 8. Estratégia de testes

Os testes foram realizados utilizando os arquivos de código-fonte Jack disponibilizados para a atividade e seus respectivos arquivos XML de referência.

Foram executadas as seguintes verificações:

1. Compilação das classes Java.
2. Execução do analisador sobre `Main.jack`, `Square.jack` e `SquareGame.jack`.
3. Comparação dos arquivos XML gerados com os arquivos XML de referência.
4. Teste com um arquivo Jack contendo palavras reservadas, identificadores, símbolos, números inteiros e constantes de texto.
5. Teste de entrada inválida com uma constante inteira fora do intervalo permitido, verificando a emissão de uma mensagem de erro.

Os três arquivos XML gerados foram comparados com os arquivos de referência, sem diferenças identificadas. O teste com o inteiro `32768` também apresentou a mensagem de erro esperada.

## 9. Conclusão

O projeto implementa as funcionalidades de um analisador léxico para a linguagem Jack, identificando e classificando tokens, ignorando espaços e comentários, gerando arquivos XML e sinalizando entradas inválidas. Os testes realizados demonstraram o funcionamento esperado para os arquivos de referência utilizados.
