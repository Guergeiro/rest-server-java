package users;

import java.io.Serializable;
import java.time.LocalDate;

public class User implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 5084245250595541107L;
  // Attributes
  private String nome;
  private LocalDate data_nascimento;
  private String localidade;

  // Constructor
  public User(String nome, LocalDate data_nascimento, String localidade) {
    this.nome = nome;
    this.data_nascimento = data_nascimento;
    this.localidade = localidade;
  }

  // Get & Set
  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public LocalDate getData_nascimento() {
    return data_nascimento;
  }

  public void setData_nascimento(LocalDate data_nascimento) {
    this.data_nascimento = data_nascimento;
  }

  public String getLocalidade() {
    return localidade;
  }

  public void setLocalidade(String localidade) {
    this.localidade = localidade;
  }

  public String toString() {
    return "\n" + this.nome + "\n" + this.data_nascimento.toString() + "\n" + this.localidade;
  }
}
