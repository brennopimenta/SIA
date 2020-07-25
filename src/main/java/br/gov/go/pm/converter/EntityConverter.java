package br.gov.go.pm.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.lang.reflect.Method;
import java.util.Map;

@FacesConverter(value="entityConverter")
public class EntityConverter implements Converter{

    public final Object getAsObject(final FacesContext context, final UIComponent component, final String key) {
        if (key != null && !key.isEmpty()) {
           return getElements(component).get(key);
        }
        return null;
    }

    public final String getAsString(final FacesContext context, final UIComponent component, final Object value) {
        if (value != null && !"".equals(value)) {
            final Class<?> c = value.getClass();
            try {
                final Method m = c.getMethod("getCodigo");
                final String key = String.valueOf(m.invoke(value));
                getElements(component).put(key, value);
                return key;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return String.valueOf(value);
    }

    private Map<String, Object> getElements(final UIComponent component) {
        return component.getAttributes();
    }

}