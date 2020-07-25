INSERT INTO usuario (cpf, nome, graduacao, profile, senha, status, token) VALUES ('82698708115', 'CLAUDIO MARTINS DAS SILVA', '3º SGT', 'ADM', '', 'ATIVO','');
INSERT INTO usuario (cpf, nome, graduacao, profile, senha, status, token) VALUES ('60138513104', 'MARCIO VICENTE DA SILVA ', 'CEL', 'CMT', '', 'ATIVO','');
INSERT INTO usuario (cpf, nome, graduacao, profile, senha, status, token) VALUES ('39482472187', 'WANDERLEI CARLOS MEDEIROS', 'TEN CEL', 'AUTORIDADE_DELEGADA', '', 'ATIVO','');
INSERT INTO usuario (cpf, nome, graduacao, profile, senha, status, token) VALUES ('85951137187', 'ALEXANDRE GREVY MAGACHO BARCELLOS','CAP', 'SIA', '', 'ATIVO','');
INSERT INTO usuario (cpf, nome, graduacao, profile, senha, status, token) VALUES ('90329147153', 'JEANLOU RIBEIRO DE OLIVEIRA', 'CB', 'SIA', '', 'ATIVO','');
INSERT INTO usuario (cpf, nome, graduacao, profile, senha, status, token) VALUES ('01138277177', 'SÉRGIO SÁVIO BATISTA', 'SD', 'SIA', '', 'ATIVO','');
INSERT INTO usuario (cpf, nome, graduacao, profile, senha, status, token) VALUES ('77644620125', 'FERNANDO DE PINHO ARAÚJO', 'CAP','SIA', '', 'ATIVO','');
INSERT INTO usuario (cpf, nome, graduacao, profile, senha, status, token) VALUES ('02369897155', 'FABRICIO ARAÚJO FARIAS', 'SD', 'SIA', '', 'ATIVO','');

--
--

INSERT INTO grupo (descricao, nome) VALUES ('Administrador do Sistema', 'ADM');
INSERT INTO grupo (descricao, nome) VALUES ('Comandante da Unidade', 'CMT');
INSERT INTO grupo (descricao, nome) VALUES ('Sub cmt ou autoridade delegada para assinatura', 'AUTORIDADE_DELEGADA');
INSERT INTO grupo (descricao, nome) VALUES ('Quem tem a chancela do comandante ', 'CHANCELA');
INSERT INTO grupo (descricao, nome) VALUES ('Chefe ou adm da seção ou departamento', 'CHEFE');
INSERT INTO grupo (descricao, nome ) VALUES ('Responsavel pela gestão do sistema', 'GESTOR');
INSERT INTO grupo (descricao, nome ) VALUES ('Responsavel pela gestão auxiliar do sistema', 'GESTOR_AUXILIAR');
INSERT INTO grupo (descricao, nome ) VALUES ('Auxiliares Gerais', 'AUXILIAR');

INSERT INTO usuario_grupo (usuario_codigo,grupo_codigo ) VALUES (1, 1);
INSERT INTO usuario_grupo (usuario_codigo,grupo_codigo ) VALUES (2, 2);
INSERT INTO usuario_grupo (usuario_codigo,grupo_codigo ) VALUES (3, 3);
INSERT INTO usuario_grupo (usuario_codigo,grupo_codigo ) VALUES (4, 5);
INSERT INTO usuario_grupo (usuario_codigo,grupo_codigo ) VALUES (5, 1);
INSERT INTO usuario_grupo (usuario_codigo,grupo_codigo ) VALUES (6, 7);
INSERT INTO usuario_grupo (usuario_codigo,grupo_codigo ) VALUES (7, 1);
INSERT INTO usuario_grupo (usuario_codigo,grupo_codigo ) VALUES (8, 1);




