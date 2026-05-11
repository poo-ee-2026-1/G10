package util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe para centralizar tratamento de excecoes e logging de erros.
 */
public class TratadorExcecoes {

    private static final Logger LOGGER = Logger.getLogger(TratadorExcecoes.class.getName());

    private TratadorExcecoes() {
        // Classe utilitaria, nao instanciavel.
    }

    /**
     * Trata excecao generica e retorna mensagem amigavel ao usuario.
     *
     * @param e Excecao capturada
     * @param contexto Contexto onde o erro ocorreu (ex: "adicionar dispositivo")
     * @return Mensagem de erro formatada para o usuario
     */
    public static String tratarExcecao(Exception e, String contexto) {
        String mensagem = "Erro ao " + contexto + ": ";

        if (e instanceof NumberFormatException) {
            mensagem += "Valor numerico invalido.";
            logErro(contexto, "Formato numerico invalido", e);
        } else if (e instanceof IllegalArgumentException) {
            mensagem += e.getMessage();
            logErro(contexto, "Argumento invalido", e);
        } else if (e instanceof NullPointerException) {
            mensagem += "Dados invalidos ou incompletos.";
            logErro(contexto, "Null pointer", e);
        } else {
            mensagem += e.getClass().getSimpleName();
            logErro(contexto, "Erro desconhecido", e);
        }

        return mensagem;
    }

    /**
     * Registra informacoes sobre o erro via java.util.logging.
     */
    public static void logErro(String contexto, String tipo, Exception e) {
        LOGGER.log(Level.WARNING, "Erro em {0} ({1}): {2}",
                new Object[] { contexto, tipo, e.getMessage() });
    }

    /**
     * Validacao com tratamento seguro - retorna valor padrao se falhar.
     *
     * @param valor String a converter
     * @param valorPadrao Valor padrao caso falhe
     * @return Valor convertido ou padrao
     */
    public static double parseDouble(String valor, double valorPadrao) {
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            logErro("parseDouble", "Formato invalido", e);
            return valorPadrao;
        }
    }

    /**
     * Validacao com tratamento seguro para inteiros.
     *
     * @param valor String a converter
     * @param valorPadrao Valor padrao caso falhe
     * @return Valor convertido ou padrao
     */
    public static int parseInt(String valor, int valorPadrao) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            logErro("parseInt", "Formato invalido", e);
            return valorPadrao;
        }
    }
}
