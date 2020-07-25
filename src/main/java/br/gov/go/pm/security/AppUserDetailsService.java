package br.gov.go.pm.security;

import br.gov.go.pm.dao.UserDAO;
import br.gov.go.pm.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ManagedBean
@ViewScoped
public class AppUserDetailsService implements UserDetailsService  {

	@Autowired
	private UserDAO userDAO;

	@Override
	public UserDetails loadUserByUsername(String cpf) {
		final Optional<Usuario> usuario = userDAO.usuarioAtivoPorCpf(cpf);
			return new UsuarioSistema(usuario.get(), getGrupos(usuario.get()));
	}

	private Collection<? extends GrantedAuthority> getGrupos(Usuario usuario) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		usuario.getGrupos().forEach(g ->  authorities.add(new SimpleGrantedAuthority(g.getNome().toUpperCase())));
		return authorities;
	}


}//end class
