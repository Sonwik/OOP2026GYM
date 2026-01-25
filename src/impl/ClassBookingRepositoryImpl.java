package impl;

import edu.aitu.oop3.db.DbMapping;
import edu.aitu.oop3.db.IDB;
import entities.edu.aitu.oop3.entities.ClassBooking;
import exceptions.BookingAlreadyExistsException;
import repositories.ClassBookingRepository;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClassBookingRepositoryImpl implements ClassBookingRepository {
    private final IDB db;
    private final DbMapping m;

    public ClassBookingRepositoryImpl(IDB db) {
        this.db = db;
        this.m = DbMapping.getOrCreate(db);
    }

    @Override
    public ClassBooking create(ClassBooking booking) {
        String sql = "insert into " + m.classBookingsTable() +
                " (" + m.bookingMemberIdCol() + ", " + m.bookingClassIdCol() + ", " + m.bookingStatusCol() + ") " +
                "values (?, ?, 'BOOKED') returning id";

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, booking.getMemberId());
            st.setLong(2, booking.getClassId());

            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                booking.setId(rs.getLong("id"));
                return booking;
            }

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new BookingAlreadyExistsException("Booking already exists");
            }
            throw new RuntimeException("DB error ClassBookingRepository.create: " + e.getMessage(), e);
        }
    }

    @Override
    public int countActiveBookingsForClass(long classId) {
        String sql = "select count(*) as c from " + m.classBookingsTable() +
                " where " + m.bookingClassIdCol() + " = ? and " + m.bookingStatusCol() + " = 'BOOKED'";

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, classId);
            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                return rs.getInt("c");
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB error countActiveBookingsForClass: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> attendanceHistory(long memberId) {
        String sql = "select b.id, c." + m.classTitleCol() + " as title, c." + m.classStartTimeCol() + " as start_time, " +
                "b." + m.bookingStatusCol() + " as status " +
                "from " + m.classBookingsTable() + " b " +
                "join " + m.classesTable() + " c on c.id = b." + m.bookingClassIdCol() + " " +
                "where b." + m.bookingMemberIdCol() + " = ? " +
                "order by c." + m.classStartTimeCol() + " desc";

        List<String> out = new ArrayList<>();

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, memberId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    OffsetDateTime start = rs.getObject("start_time", OffsetDateTime.class);
                    out.add("#" + rs.getLong("id") + " | " +
                            rs.getString("title") + " | " +
                            start + " | " +
                            rs.getString("status"));
                }
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB error attendanceHistory: " + e.getMessage(), e);
        }
    }
}