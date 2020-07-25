package br.gov.go.pm.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
* Conversor de Maiúscula.
*
*/
public class MinusculaConverter implements Converter {
     public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {

          String atributo = value;
          if (value!= null && !value.equals(""))
               atributo = value.toLowerCase();
 
          return atributo;
     }
 
     public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {

          String atributo= (String) value;
          if (atributo != null && atributo.length() == 11)
               atributo = atributo.toLowerCase();
 
          return atributo;
     }
}