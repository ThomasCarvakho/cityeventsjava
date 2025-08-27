import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private final FileRepository fileRepo;
    private final List<Event> events = new ArrayList<>();

    public EventRepository(FileRepository fileRepo) {
        this.fileRepo = fileRepo;
    }

    public void add(Event e) { events.add(e); }

    public List<Event> findAll() { return new ArrayList<>(events); }

    // ===== exporta a seção de eventos =====
    public List<String> exportSectionLines() {
        List<String> lines = new ArrayList<>();
        lines.add("#EVENTS");
        lines.add("id;nome;endereco;categoryId;startIso;endIso;descricao");
        for (Event e : events) {
            lines.add(
                    e.getId() + ";" +
                            e.getNome() + ";" +
                            e.getEndereco() + ";" +
                            e.getCategoryId() + ";" +
                            DateUtil.formatIso(e.getStart()) + ";" +
                            DateUtil.formatIso(e.getEnd()) + ";" +
                            e.getDescricao()
            );
        }
        lines.add("");
        return lines;
    }

    // ===== importa a seção de eventos =====
    public void importFromLines(List<String> lines) {
        events.clear();
        boolean inSection = false;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            if (line.startsWith("#")) {
                inSection = line.equalsIgnoreCase("#EVENTS");
                continue;
            }
            if (!inSection) continue;
            if (line.equalsIgnoreCase("id;nome;endereco;categoryId;startIso;endIso;descricao")) continue;

            String[] parts = line.split(";", 7);
            if (parts.length == 7) {
                events.add(new Event(
                        parts[0], // id
                        parts[1], // nome
                        parts[2], // endereco
                        parts[3], // categoryId
                        DateUtil.parseIso(parts[4]), // start
                        DateUtil.parseIso(parts[5]), // end
                        parts[6]  // descricao
                ));
            }
        }
    }

    // Mantém por compatibilidade (não use por enquanto)
    public void saveToFile() { fileRepo.writeAll(exportSectionLines()); }
    public void loadFromFile() { importFromLines(fileRepo.readAll()); }
}
