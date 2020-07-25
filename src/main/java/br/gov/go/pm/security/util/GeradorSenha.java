package br.gov.go.pm.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorSenha {

    public static String encodeSenha(String senha){
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(senha);
    }

    public static boolean assertSenhaEncode(String senha, String senhaCrip){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(senha, senhaCrip);
    }
}//end class
