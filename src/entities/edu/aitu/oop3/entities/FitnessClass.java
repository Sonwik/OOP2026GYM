package entities.edu.aitu.oop3.entities;
import java.time.OffsetDateTime;
public class FitnessClass {
    private Long id;
    private String title;
    private OffsetDateTime startTime;
    private int capacity;

    public FitnessClass(Long id, String title, OffsetDateTime startTime, int capacity) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public OffsetDateTime getStartTime() { return startTime; }
    public int getCapacity() { return capacity; }
}
//