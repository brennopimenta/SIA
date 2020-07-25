package br.gov.go.pm.utilitarios;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class ImageResource {

    public static Image getMakeImage(final InputStream in){
        try {
            return ImageIO.read(in);
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

}
