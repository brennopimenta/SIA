package br.gov.go.pm.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormataData {

    public static String dataAbreviadaAnoCompletoComHoras(Date data){
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dataFormatada = fmt.format(data);
        return dataFormatada;
    }

    public static String dataAbreviada(Date data){
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = fmt.format(data);
        return dataFormatada;
    }

}
