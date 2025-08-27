public class User {
    private final String id;
    private final String nome;
    private final String endereco;
    private final String email;

    public User(String id, String nome, String endereco, String email) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.email = email;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getEmail() { return email; }
}
