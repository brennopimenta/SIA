package br.gov.go.pm.security;

import br.gov.go.pm.modelo.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UsuarioSistema extends User {

	private static final long serialVersionUID = 1L;

	private Usuario usuario;

	public UsuarioSistema(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
		super(usuario.getCpf(),usuario.getSenha(), authorities);
		this.usuario = usuario;

	}
	/**construtor para as autorizações do spring securty*/
	public Usuario getUsuario() {
		return usuario;
	}

}
