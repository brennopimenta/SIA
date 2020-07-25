package br.gov.go.pm.service;

import br.gov.go.pm.dao.EmailDAO;
import br.gov.go.pm.modelo.Carga;
import br.gov.go.pm.modelo.EmailConfig;
import br.gov.go.pm.modelo.Sigma;
import br.gov.go.pm.security.SegurancaDetalhe;
import br.gov.go.pm.security.UsuarioSistema;
import br.gov.go.pm.util.exception.NegocioException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmailService {
	private Logger log = Logger.getLogger(getClass());

	@Autowired
	private EmailDAO dao;

	@Autowired
	private SegurancaDetalhe segurancaDetalhe;

	public List<EmailConfig> list() {
		return dao.list();
	}

	@Transactional
	public void salvar(EmailConfig emailConfig) throws NegocioException {
		try {
			emailConfig.setUsuarioSistema(segurancaDetalhe.getUsuarioPadrao());
			dao.salvar(emailConfig);

		}catch (Exception e){
			log.error(e.getMessage(), e.getCause());
			throw new NegocioException(e.getMessage());
		}

	}
}//end class
