package br.gov.go.pm.security;

import br.gov.go.pm.enuns.Profile;
import br.gov.go.pm.enuns.StatusSituacao;
import br.gov.go.pm.modelo.Grupo;
import br.gov.go.pm.modelo.Usuario;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import java.util.List;

@Component
public class SegurancaDetalhe {

    private final Logger log = Logger.getLogger(this.getClass());

    /**captura o usuário logado*/
    private UsuarioSistema getUsuarioLogado() {
        UsuarioSistema usuario = null;

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken)
                FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();

        if (auth != null && auth.getPrincipal() != null) {
            usuario = (UsuarioSistema) auth.getPrincipal();
        }

        return usuario;
    }

    /**captura o nome do usuário logado*/
    public String getNomeUsuario() {
        String nome = null;

        UsuarioSistema usuarioLogado = getUsuarioLogado();

        if (usuarioLogado != null) {
            nome = usuarioLogado.getUsuario().getNome();
        }
        return nome;
    }

    /**captura o profile (perfil individual) do usuário logado*/
    public String getPefilUsuario() {
        String perfil = null;
        UsuarioSistema usuarioLogado = getUsuarioLogado();

        if (usuarioLogado != null)
            return usuarioLogado.getUsuario().getProfile().name();

        return perfil;
    }

    public Usuario getUsuarioPadrao(){
        UsuarioSistema usuarioLogado = getUsuarioLogado();
        Usuario userPadrao = new Usuario();

        Long codigo = usuarioLogado.getUsuario().getCodigo();
        String cpf = usuarioLogado.getUsuario().getCpf();
        String nome = usuarioLogado.getUsuario().getNome().toUpperCase();
        String graduacao = usuarioLogado.getUsuario().getGraduacao();

        userPadrao.setCodigo(codigo);
        userPadrao.setCpf(cpf);
        userPadrao.setNome(nome);
        userPadrao.setGraduacao(graduacao);

        return userPadrao;
    }

    public Usuario getUsuarioCheck(){
        UsuarioSistema usuarioLogado = getUsuarioLogado();
        Usuario usuario = new Usuario();

        Long codigo = usuarioLogado.getUsuario().getCodigo();
        String cpf = usuarioLogado.getUsuario().getCpf();
        String nome = usuarioLogado.getUsuario().getNome();
        String senhaAutoridade = usuarioLogado.getUsuario().getSenhaAssinatura();
        Profile profile = usuarioLogado.getUsuario().getProfile();
        StatusSituacao status = usuarioLogado.getUsuario().getStatus();
        String token = usuarioLogado.getUsuario().getToken();
        String senha = usuarioLogado.getUsuario().getSenha();
        String graduacao = usuarioLogado.getUsuario().getGraduacao();

        usuario.setCodigo(codigo);
        usuario.setCpf(cpf);
        usuario.setNome(nome);
        usuario.setSenhaAssinatura(senhaAutoridade);
        usuario.setProfile(profile);
        usuario.setStatus(status);
        usuario.setToken(token);
        usuario.setSenha(senha);
        usuario.setGraduacao(graduacao);
        usuario.setGrupos(getGruposUsuarioLogado());

        return usuario;
    }

    public List<Grupo> getGruposUsuarioLogado(){
        UsuarioSistema usuarioLogado = getUsuarioLogado();
        List<Grupo> grupos = usuarioLogado.getUsuario().getGrupos();
        return grupos;
    }


}//end class
