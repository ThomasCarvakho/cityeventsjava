public class Participation {
    private final String userId;
    private final String eventId;
    private ParticipationStatus status;

    public Participation(String userId, String eventId, ParticipationStatus status) {
        this.userId = userId;
        this.eventId = eventId;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public ParticipationStatus getStatus() { return status; }
    public void setStatus(ParticipationStatus status) { this.status = status; }
}
