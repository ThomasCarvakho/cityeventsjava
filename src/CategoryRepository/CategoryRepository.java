import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private final FileRepository fileRepo;
    private final List<Category> categories = new ArrayList<>();

    public CategoryRepository(FileRepository fileRepo) {
        this.fileRepo = fileRepo;
    }

    public void add(Category c) { categories.add(c); }

    public List<Category> findAll() { return new ArrayList<>(categories); }

    // ===== NOVO: exporta só a seção de categorias =====
    public List<String> exportSectionLines() {
        List<String> lines = new ArrayList<>();
        lines.add("#CATEGORIES");
        lines.add("id;nome");
        for (Category c : categories) {
            lines.add(c.getId() + ";" + c.getNome());
        }
        lines.add(""); // linha em branco separadora (opcional)
        return lines;
    }

    // ===== NOVO: importa só a seção de categorias =====
    public void importFromLines(List<String> lines) {
        categories.clear();
        boolean inSection = false;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            if (line.startsWith("#")) {
                inSection = line.equalsIgnoreCase("#CATEGORIES");
                continue;
            }
            if (!inSection) continue;
            if (line.equalsIgnoreCase("id;nome")) continue;

            String[] parts = line.split(";", 2);
            if (parts.length == 2) {
                categories.add(new Category(parts[0], parts[1]));
            }
        }
    }

    // Mantém para compatibilidade (não use por enquanto)
    public void saveToFile() {
        fileRepo.writeAll(exportSectionLines());
    }
    public void loadFromFile() {
        importFromLines(fileRepo.readAll());
    }
}
