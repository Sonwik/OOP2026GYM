package entities.edu.aitu.oop3.entities;
import java.time.OffsetDateTime;
public class ClassBooking {
    private Long id;
    private Long memberId;
    private Long classId;
    private BookingStatus status;
    private OffsetDateTime bookedAt;

    public ClassBooking(Long id, Long memberId, Long classId, BookingStatus status, OffsetDateTime bookedAt) {
        this.id = id;
        this.memberId = memberId;
        this.classId = classId;
        this.status = status;
        this.bookedAt = bookedAt;
    }

    public ClassBooking(Long memberId, Long classId) {
        this(null, memberId, classId, BookingStatus.BOOKED, null);
    }

    public Long getId() { return id; }
    public Long getMemberId() { return memberId; }
    public Long getClassId() { return classId; }
    public BookingStatus getStatus() { return status; }
    public OffsetDateTime getBookedAt() { return bookedAt; }

    public void setId(Long id) { this.id = id; }
}
