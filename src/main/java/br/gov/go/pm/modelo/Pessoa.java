package br.gov.go.pm.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(
        name = "PessoaMapper",
        classes = @ConstructorResult(
                targetClass = Pessoa.class,
                columns = {
                        @ColumnResult(name = "cpf_proprietario", type = String.class),
                        @ColumnResult(name = "rg_proprietario", type = String.class),
                        @ColumnResult(name = "nome_proprietario", type = String.class)}))
public class Pessoa {
    private String cpf;
    private String rg;
    private String nome;
}