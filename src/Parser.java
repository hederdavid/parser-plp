//Compilar javac Parser.java
//Executar java Parser programa1.plp
//Executar java Parser programa2.plp
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Objects;

enum Estado {
    Q0,
    Q1,
    Q2,
    Q3,
    Q4,
    Q5,
    Q6,
    Q10,
    Q17,
    Q18,
    Q19,
    Q20,
    Q21,
    Q22,
    Q23,
    Q24,
    Q25,
    Q26,
    Q27,
    Q29,

    // Estados Finais
    INSTR_IF,
    INSTR_ELSE,
    INSTR_WHILE,
    INSTR_PRINT,
    ABR_PAR,
    FECHA_PAR,
    ABRE_CHA,
    FECHA_CHA,
    TEXTO,
    ATRIBUICAO,
    SEPARADOR,
    IDENT,
    NUM_REAL,
    NUM_INT,
    TIPO_INT,
    TIPO_FLOAT,
    OPER_ADICAO,
    OPER_SUBTRACAO,
    OPER_MULTIPLICACAO,
    OPER_DIVISAO,
    OPER_IGUAL,
    OPER_DIFERENTE,
    OPER_MENOR,
    OPER_MAIOR,
    OPER_MENOR_IGUAL,
    OPER_MAIOR_IGUAL,
    INICIO_COMENT,
    ESP
}




public class Parser {
    public static void main(String[] args) {

        StringBuilder entrada = new StringBuilder();

        try {
            FileReader fileReader = new FileReader("src/programa2.plp");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String linha;
            boolean dentroDeBloco = false;  // Para rastrear se estamos dentro de um bloco { }

            while ((linha = bufferedReader.readLine()) != null) {
                linha = linha.trim();

                if (linha.contains("{")) {
                    // Inicia um bloco
                    entrada.append(linha).append(" "); // Não adiciona | ainda
                    dentroDeBloco = true;
                } else if (linha.contains("}")) {
                    // Finaliza um bloco
                    entrada.append(linha); // Adiciona a linha final do bloco
                    entrada.append("\n"); // Agora sim pode adicionar o delimitador
                    dentroDeBloco = false;
                } else if (dentroDeBloco) {
                    // Está dentro de um bloco, junta sem |
                    entrada.append(linha).append(" ");
                } else {
                    // Fora de bloco, adiciona com |
                    entrada.append(linha).append("\n");
                }

                System.out.println(linha);  // Mantém o debug com impressão da linha
            }
            bufferedReader.close();
            System.out.println(entrada.toString());
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }



        Parser p = new Parser();

        boolean resultado = p.processar(String.valueOf(entrada));
        System.out.println("Entrada " + (resultado ? "valida" : "invalida"));
    }

    private List<String> tokenizar(String entrada) {
        List<String> tokens = new ArrayList<>();
        int i = 0;

        while (i < entrada.length()) {
            char charAtual = entrada.charAt(i);

            // Delimitador '\n' separa comandos
            if (charAtual == '\n') {
                tokens.add("\n");
                i++; // Pula o delimitador
                continue;
            }

            if (Character.isWhitespace(charAtual)) {
                i++; // Ignorar espaços em branco
                continue;
            }



            // Comentários de linha única iniciados com #
            if (charAtual == '#') {
                StringBuilder comentario = new StringBuilder();
                tokens.add("#"); // Adicionar o símbolo # como parte do comentário
                i++;

                // Agrupar todo o texto após o # até o próximo delimitador ou fim da linha
                while (i < entrada.length() && entrada.charAt(i) != '|') {
                    comentario.append(entrada.charAt(i));
                    i++;
                }
                tokens.add(comentario.toString().trim()); // Adicionar o comentário como um token
                continue;
            }

            // Identificadores ou palavras-chave (iniciam com letra)
            if (Character.isLetter(charAtual)) {
                StringBuilder token = new StringBuilder();
                while (i < entrada.length() && Character.isLetterOrDigit(entrada.charAt(i))) {
                    token.append(entrada.charAt(i));
                    i++;
                }
                tokens.add(token.toString());

                continue;
            }

            // Numeros
            if (Character.isDigit(charAtual)) {
                StringBuilder numero = new StringBuilder();

                // Constrói o número enquanto for dígito
                while (i < entrada.length() && Character.isDigit(entrada.charAt(i))) {
                    numero.append(entrada.charAt(i));
                    i++;
                }

                // Verifica se o próximo caractere é um ponto para tratar número flutuante
                if (i < entrada.length() && entrada.charAt(i) == '.') {
                    numero.append('.'); // Adiciona o ponto decimal
                    i++;

                    // Adiciona a parte decimal
                    while (i < entrada.length() && Character.isDigit(entrada.charAt(i))) {
                        numero.append(entrada.charAt(i));
                        i++;
                    }
                    tokens.add(numero.toString()); // Numero float
                } else {
                    tokens.add(numero.toString()); // Numero int
                }
                continue;
            }

            // Operadores e símbolos
            else if (charAtual == '=' || charAtual == '+' || charAtual == '-' ||
                    charAtual == '*' || charAtual == '/' || charAtual == '{' ||
                    charAtual == '}' || charAtual == '(' || charAtual == ')' ||
                    charAtual == ',' || charAtual == '<' || charAtual == '>') {

                // Verificar operadores de comparação (==, !=, <=, >=)
                if (charAtual == '=') {
                    if (i + 1 < entrada.length() && entrada.charAt(i + 1) == '=') {
                        tokens.add("==");
                        i += 2; // Avança dois caracteres
                        continue;
                    }
                    tokens.add("=");
                    i++; // Avança um caractere
                    continue;
                } else if (charAtual == '!') {
                    if (i + 1 < entrada.length() && entrada.charAt(i + 1) == '=') {
                        tokens.add("!=");
                        i += 2; // Avança dois caracteres
                        continue;
                    }
                } else if (charAtual == '<') {
                    if (i + 1 < entrada.length() && entrada.charAt(i + 1) == '=') {
                        tokens.add("<=");
                        i += 2; // Avança dois caracteres
                        continue;
                    }
                    tokens.add("<");
                    i++; // Avança um caractere
                    continue;
                } else if (charAtual == '>') {
                    if (i + 1 < entrada.length() && entrada.charAt(i + 1) == '=') {
                        tokens.add(">=");
                        i += 2; // Avança dois caracteres
                        continue;
                    }
                    tokens.add(">");
                    i++; // Avança um caractere
                    continue;
                } else {
                    tokens.add(String.valueOf(charAtual)); // Adiciona o símbolo simples
                    i++; // Avança um caractere
                    continue;
                }
            }

            // Ignorar qualquer outro caractere
            i++;
        }
        System.out.println(tokens);
        return tokens;
    }


    int contadorIdent = 0;

    private Estado transitar(Estado estado, String token) {

        switch (estado) {
            case Q0 -> {
                switch (token.toLowerCase()) {
                    case "int" -> {
                        return Estado.TIPO_INT;
                    }
                    case "float" -> {
                        return Estado.TIPO_FLOAT;
                    }
                    default -> {
                        if (token.matches("^[a-z].*")) { // Verifica se começa com a-z
                            contadorIdent++;
                            return Estado.IDENT;

                        }
                    }
                }
            }

            case TIPO_INT, TIPO_FLOAT -> {
                if (Character.isLetter(token.charAt(0))) {
                    return Estado.IDENT;
                } else {
                    return null;
                }
            }

            case SEPARADOR -> {
                if (Character.isLetter(token.charAt(0))) {
                    return Estado.IDENT;
                } else if (token.equals("\n")) {
                    return Estado.Q0;
                } else {
                    return null;
                }

            }

            case IDENT -> {
                if (contadorIdent == 1 && !token.equals("\n")) {
                    if (!token.equals("=")) {
                        return null;
                    }
                }
                if (token.equals(",")) {
                    return Estado.SEPARADOR;

                } else if (token.equals("=")) {
                    return Estado.ATRIBUICAO;

                } else if (token.equals("\n")) {
                    return Estado.Q0;

                } else {
                    return null;
                }
            }

            case ATRIBUICAO -> {
                if (token.matches("^[0-9]+$")) { // numeros int
                    return Estado.NUM_INT;

                } else if (token.matches("^[0-9]+(\\.[0-9]+)?$")) { // numeros float
                    return Estado.NUM_REAL;
                } else if (token.matches("^[a-z].*")) {
                    return Estado.IDENT;
                }
            }

            case NUM_INT, NUM_REAL -> {
                if (token.equals("\n")) {
                    return Estado.Q0;
                }
            }

            case Q1 -> {
                if (token.equals("=")) {
                return Estado.Q2;
                }
            }

            case Q2 -> {
                if (Character.isDigit(token.charAt(0))) {  // Número
                    return Estado.Q3;
                }
            }

            case Q3 -> {
                if (token.equals(";")) {  // Final de instrução de atribuição
                    return Estado.Q0;
                }

            }

            case Q4 -> {
                if (Character.isLetter(token.charAt(0))) {  // Identificador para print
                    return Estado.Q5;
                }

            }

            case Q5 -> {
                if (token.equals(";")) {  // Final de instrução print
                    return Estado.Q0;
                }
            }

            default -> {
                return null;
            }

        }
        return null;
    }

    private boolean processar(String entrada) {
        Estado estadoAtual = Estado.Q0;			//estado inicial
        Estado[] estadosFinais = {Estado.Q0};	//estados finais

        List<String> tokens = tokenizar(entrada);
        for (String token : tokens) {
            estadoAtual = transitar(estadoAtual, token);
            if (estadoAtual == null)
                return false;
        }

        //se parou num estado final, o código-fonte é bem formado
        for (Estado estadoFinal : estadosFinais) {
            if (estadoAtual == estadoFinal)
                return true;
        }
        return false;
    }
}