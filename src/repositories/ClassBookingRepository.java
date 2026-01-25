package repositories;
import entities.edu.aitu.oop3.entities.ClassBooking;
import java.util.List;
public interface ClassBookingRepository {
    ClassBooking create(ClassBooking booking);
    int countActiveBookingsForClass(long classId);
    List<String> attendanceHistory(long memberId);
}
//