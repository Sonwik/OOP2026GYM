package impl;

import edu.aitu.oop3.db.DbMapping;
import edu.aitu.oop3.db.IDB;
import entities.edu.aitu.oop3.entities.FitnessClass;
import repositories.FitnessClassRepository;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.Optional;

public class FitnessClassRepositoryImpl implements FitnessClassRepository {
    private final IDB db;
    private final DbMapping m;

    public FitnessClassRepositoryImpl(IDB db) {
        this.db = db;
        this.m = DbMapping.getOrCreate(db);
    }

    @Override
    public Optional<FitnessClass> findById(long id) {
        String sql = "select id, " + m.classTitleCol() + " as title, " +
                m.classStartTimeCol() + " as start_time, " +
                m.classCapacityCol() + " as capacity " +
                "from " + m.classesTable() + " where id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                OffsetDateTime start = rs.getObject("start_time", OffsetDateTime.class);

                return Optional.of(new FitnessClass(
                        rs.getLong("id"),
                        rs.getString("title"),
                        start,
                        rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error FitnessClassRepository.findById: " + e.getMessage(), e);
        }
    }
}