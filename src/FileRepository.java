import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileRepository {
    private final Path path;

    public FileRepository(String filePath) {
        this.path = Paths.get(filePath);
        ensureFile();
    }

    private void ensureFile() {
        try {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro criando arquivo: " + path, e);
        }
    }

    public List<String> readAll() {
        try {
            if (!Files.exists(path)) return new ArrayList<>();
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("Erro lendo: " + path, e);
        }
    }

    public void writeAll(List<String> lines) {
        try {
            Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException("Erro escrevendo: " + path, e);
        }
    }
}
