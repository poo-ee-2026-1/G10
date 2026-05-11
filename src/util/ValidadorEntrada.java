package util;

/**
 * Utilitario para validacao de entradas de usuario na interface grafica.
 */
public class ValidadorEntrada {

    private ValidadorEntrada() {
        // Classe utilitaria, nao instanciavel.
    }

    /**
     * Valida se um texto nao e nulo ou vazio.
     */
    public static boolean validarNaoVazio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Normaliza numeros digitados com virgula decimal.
     */
    public static String normalizarNumero(String texto) {
        return texto == null ? "" : texto.trim().replace(",", ".");
    }

    /**
     * Valida se um texto representa um numero positivo.
     */
    public static boolean validarNumeroPositivo(String texto) {
        try {
            double valor = Double.parseDouble(normalizarNumero(texto));
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida se um texto representa um numero nao negativo.
     */
    public static boolean validarNumeroNaoNegativo(String texto) {
        try {
            double valor = Double.parseDouble(normalizarNumero(texto));
            return valor >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida um nome de dispositivo.
     */
    public static boolean validarNomeDispositivo(String nome) {
        if (!validarNaoVazio(nome)) {
            return false;
        }
        return nome.matches("^[\\p{L}0-9 \\-_()]+$");
    }

    /**
     * Valida potencia em Watts.
     */
    public static boolean validarPotencia(String potencia) {
        try {
            double valor = Double.parseDouble(normalizarNumero(potencia));
            return valor >= 0.01 && valor <= 100000;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida tarifa de energia.
     */
    public static boolean validarTarifa(String tarifa) {
        return validarNumeroPositivo(tarifa);
    }

    /**
     * Valida tensao eletrica.
     */
    public static boolean validarTensao(String tensao) {
        try {
            double valor = Double.parseDouble(normalizarNumero(tensao));
            return valor >= 100 && valor <= 500;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida corrente eletrica.
     */
    public static boolean validarCorrente(String corrente) {
        try {
            double valor = Double.parseDouble(normalizarNumero(corrente));
            return valor >= 0.01 && valor <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Obtem a mensagem de erro apropriada para o tipo de validacao.
     */
    public static String obterMensagemErro(String campo, String tipo) {
        switch (tipo) {
            case "vazio":
                return "O campo '" + campo + "' nao pode ser vazio.";
            case "positivo":
                return "O campo '" + campo + "' deve conter um numero maior que zero.";
            case "naoNegativo":
                return "O campo '" + campo + "' deve conter um numero nao negativo.";
            case "numero":
                return "O campo '" + campo + "' deve conter um numero valido.";
            case "nome":
                return "O campo '" + campo + "' contem caracteres invalidos.";
            case "potencia":
                return "A potencia deve ser um valor entre 0,01 W e 100.000 W.";
            case "tensao":
                return "A tensao deve ser um valor entre 100 V e 500 V.";
            case "corrente":
                return "A corrente deve ser um valor entre 0,01 A e 100 A.";
            default:
                return "O campo '" + campo + "' contem um valor invalido.";
        }
    }
}
