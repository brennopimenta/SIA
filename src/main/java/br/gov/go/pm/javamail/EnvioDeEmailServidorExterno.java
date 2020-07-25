package br.gov.go.pm.javamail;

import br.gov.go.pm.dao.EmailDAO;
import br.gov.go.pm.modelo.EmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class EnvioDeEmailServidorExterno {

    @Autowired
    private EmailDAO dao;

    static String SERVIDOR_SMTP = "";
    static String PORTA_SERVIDOR = "";
    static String EMAIL = "";
    static String SENHA = "";
    static String MENSAGEM = "";

   public void config() {
        EmailConfig config = dao.buscarConfigEnvioEmail();

        SERVIDOR_SMTP = config.getSmtp();
        PORTA_SERVIDOR = config.getPorta();
        EMAIL = config.getEmail();
        SENHA = config.getSenha();
        MENSAGEM = config.getMensagem();

    }

    /**recebe os dados do bean*/
    public void enviar (String EMAIL_DESTINO) throws AddressException, MessagingException {
        config();
        Autenticacao autenticacao = new Autenticacao(EMAIL, SENHA);
        Session session = Session.getDefaultInstance(getPropriedades(), autenticacao);
        session.setDebug(true);
        MimeMessage email = new MimeMessage(session);
        email.setRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_DESTINO)); /*quem recebe*/
        email.setFrom(new InternetAddress(EMAIL));
        email.setSubject("Mensagem de seu registro de arma particular.");
        email.setContent(MENSAGEM, "text/plain; charset=UTF-8"); /*aqui recebe o conteúdo para enviar. pode vir de um form por exemplo*/
        email.setSentDate(new Date());
        Transport envio = session.getTransport("smtp");
        envio.connect(SERVIDOR_SMTP, EMAIL, SENHA);
        email.saveChanges();
        envio.sendMessage(email, email.getAllRecipients());
        envio.close();
        System.out.println("EmailConfig enviado com sucesso!");


    }

    private static Properties getPropriedades() {
        Properties config = new Properties();
        config.setProperty("mail.transport.protocol", "smtp");
        config.setProperty("mail.smtp.starttls.enable", "true");
        config.setProperty("mail.smtp.host", SERVIDOR_SMTP);
        config.setProperty("mail.smtp.auth", "true");
        config.setProperty("mail.smtp.user", EMAIL);
        config.setProperty("mail.debug", "true");
        config.setProperty("mail.smtp.port", PORTA_SERVIDOR);
        config.setProperty("mail.smtp.socketFactory.port", PORTA_SERVIDOR);
        config.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        config.setProperty("mail.smtp.socketFactory.fallback", "false");
        return config;

    }


}//end

/*      SERVIDOR_SMTP = "smtp.gmail.com";
        PORTA_SERVIDOR = "465";
        EMAIL = "bancodedadosdtic@gmail.com";
        SENHA = "pmgo2018";
        MENSAGEM = "Atenção, seu Craf está pronto! Por favor compareça à DECAE na CALTI";
* */
