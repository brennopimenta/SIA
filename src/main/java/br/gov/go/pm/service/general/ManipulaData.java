package br.gov.go.pm.service.general;

import br.gov.go.pm.util.jsf.FacesUtil;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/** PARA SER UTILIZADO na conversão de datas*/

@Service
public class ManipulaData {

    private static Calendar dataPronta;

    /**a data long vai ser convertida em DataLocale*/
    public LocalDate converteLongEmDataLocale(String dataSsp){
        try {
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(dataSsp)), ZoneId.systemDefault());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //uuuu-MM-d"
            String text = date.format(formatter);
            LocalDate data = LocalDate.parse(text,formatter);

            return data;

        }catch (Exception e){
            FacesUtil.addErrorMessage("Erro ao converter Long DataLocale " + e.getMessage());
            return null;

        }
    }

    /**converte uma data passada como long para formato Date */
    public Date converteLongEmDate(Long dataOrigem) {

        Date data = null;
        if (dataOrigem != null) {

            Date dataConvertida = Date.from(Instant.ofEpochMilli(dataOrigem));

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = formatter.format(dataConvertida);

            try {

                data = (Date) formatter.parse(dataFormatada);

            } catch (ParseException e) {
                e.printStackTrace();
                FacesUtil.addErrorMessage("Erro de conversão de Data em Calendar " + e.getMessage());
            }

        }
        return data;

    } //end method





} // end class

/* *************************************************************************

EXEMPLO PARA USAR LocaleDate

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate ld = LocalDate.parse("08/08/2016",formatter);


<p:outputLabel value="Data Plantio: " for="dataPlantio"/>
<p:calendar id="dataPlantio" value="#{talhaoController.talhao.data_plantio}" pattern="dd/MM/yyyy" mask="true" converter="localDateConverter" style="width: 100%;"/>

        Sendo value o atributo do tipo LocalDate situado na Classe Talhao, pattern é a máscara que o campo vai ser preenchido, ou seja, dia/mes/ano, converter="localDateConverter" é o nome do corversor que eu tive que criar para converter do formato de retorno de <p:calendar>, que é do tipo String, para o formato aceito pelo LocalDate.

        Código do conveter:

@FacesConverter(value="localDateConverter")
public class FacesConvertLocalDate implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Locale BRAZIL = new Locale("pt", "BR");
        return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(BRAZIL));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        LocalDate dateValue = (LocalDate) value;

        return dateValue.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    }

}  */
