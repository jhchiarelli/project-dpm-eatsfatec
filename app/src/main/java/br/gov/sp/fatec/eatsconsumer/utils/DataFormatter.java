package br.gov.sp.fatec.eatsconsumer.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataFormatter {

    // Método para formatar strings
    public static String formatString(String input) {
        if (input == null || input.isEmpty()) {
            return "N/A";
        }
        return input.trim();
    }

    // Método para formatar datas
    public static String formatDate(Date date) {
        if (date == null) {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    // Método para formatar números (por exemplo, moeda)
    public static String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return currencyFormat.format(amount);
    }

    // Método para formatar números (por exemplo, número decimal)
    public static String formatNumber(double number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(number);
    }

    // Método para formatar números inteiros
    public static String formatInteger(int number) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.getDefault());
        return numberFormat.format(number);
    }
}

