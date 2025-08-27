import java.time.LocalDateTime;

public class Event {
    private final String id;
    private final String nome;
    private final String endereco;
    private final String categoryId;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String descricao;

    public Event(String id, String nome, String endereco,
                 String categoryId, LocalDateTime start,
                 LocalDateTime end, String descricao) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.categoryId = categoryId;
        this.start = start;
        this.end = end;
        this.descricao = descricao;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getCategoryId() { return categoryId; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public String getDescricao() { return descricao; }
}
