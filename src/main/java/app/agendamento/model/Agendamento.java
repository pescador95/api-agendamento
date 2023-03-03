package app.agendamento.model;

import java.util.Date;

import javax.persistence.*;

import app.core.model.Organizacao;
import app.core.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "agendamento")
@JsonIgnoreProperties({"usuarioAcao", "isAtivo", "dataAcao", "systemDateDeleted"})
public class Agendamento extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "agendamentoIdSequence", sequenceName = "agendamento_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "agendamentoIdSequence")
    @Id
    public Long id;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "pessoaId")
    public Pessoa pessoaAgendamento;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "userId")
    public Usuario profissionalAgendamento;

    @Column()
    public String statusConsulta;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "organizacaoId")
    public Organizacao organizaoAgendamento;

    @Column()
    public Date dataAgenda;

    @Column()
    public String nomePessoa;

    @Column()
    public String nomeProfissional;

    @Column()
    @JsonIgnore
    public boolean isAtivo;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    public Usuario usuario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    public Usuario usuarioAcao;

    @Column()
    @JsonIgnore

    public Date dataAcao;

    @Column()
    @JsonIgnore

    public Date systemDateDeleted;

    public Agendamento() {

    }

}
