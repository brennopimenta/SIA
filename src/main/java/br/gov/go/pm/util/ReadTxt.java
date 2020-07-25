//package br.gov.go.pm.util;
//
//import br.gov.go.pm.util.jsf.FacesUtil;
//
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//
//public class ReadTxt {
//
//    public ReadTxt() throws IOException {
//    }
//
//    public static void read () {
//
//        Path arquivo = Paths.get("/home/marcos-fc/AmbienteJava/claudio/arquivos_sia", "sigma.txt");
//        Charset charset = Charset.forName("UTF-8");
//
//
//        try {
//            List<String> lines = Files.readAllLines(arquivo, charset);
//            for (String line : lines) {
//                System.out.println(line);
//            }
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void main(String[] args) {
//        read();
//    }
//
//}
