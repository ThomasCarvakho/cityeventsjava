import java.util.ArrayList;
import java.util.List;

public class ParticipationRepository {
    private final FileRepository fileRepo;
    private final List<Participation> parts = new ArrayList<>();

    public ParticipationRepository(FileRepository fileRepo) {
        this.fileRepo = fileRepo;
    }

    // Upsert: define status para (user,event)
    public void setStatus(String userId, String eventId, ParticipationStatus status) {
        for (Participation p : parts) {
            if (p.getUserId().equals(userId) && p.getEventId().equals(eventId)) {
                p.setStatus(status);
                return;
            }
        }
        parts.add(new Participation(userId, eventId, status));
    }

    public List<String> confirmedEventIds(String userId) {
        List<String> ids = new ArrayList<>();
        for (Participation p : parts) {
            if (p.getUserId().equals(userId) && p.getStatus() == ParticipationStatus.CONFIRMED) {
                ids.add(p.getEventId());
            }
        }
        return ids;
    }

    // ===== persistência: exporta/importa a seção #PARTICIPATIONS =====
    public List<String> exportSectionLines() {
        List<String> lines = new ArrayList<>();
        lines.add("#PARTICIPATIONS");
        lines.add("userId;eventId;status");
        for (Participation p : parts) {
            lines.add(p.getUserId() + ";" + p.getEventId() + ";" + p.getStatus().name());
        }
        lines.add("");
        return lines;
    }

    public void importFromLines(List<String> lines) {
        parts.clear();
        boolean in = false;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            if (line.startsWith("#")) { in = line.equalsIgnoreCase("#PARTICIPATIONS"); continue; }
            if (!in) continue;
            if (line.equalsIgnoreCase("userId;eventId;status")) continue;

            String[] partsArr = line.split(";", 3);
            if (partsArr.length == 3) {
                parts.add(new Participation(
                        partsArr[0],
                        partsArr[1],
                        ParticipationStatus.valueOf(partsArr[2])
                ));
            }
        }
    }
}
