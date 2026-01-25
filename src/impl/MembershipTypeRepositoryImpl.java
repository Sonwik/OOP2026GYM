package impl;

import edu.aitu.oop3.db.DbMapping;
import edu.aitu.oop3.db.IDB;
import entities.edu.aitu.oop3.entities.MembershipType;
import repositories.MembershipTypeRepository;

import java.sql.*;
import java.util.Optional;

public class MembershipTypeRepositoryImpl implements MembershipTypeRepository {
    private final IDB db;
    private final DbMapping m;

    public MembershipTypeRepositoryImpl(IDB db) {
        this.db = db;
        this.m = DbMapping.getOrCreate(db);
    }

    @Override
    public Optional<MembershipType> findById(long id) {
        String sql = "select id, " + m.typeNameCol() + " as name, " +
                m.typeDurationDaysCol() + " as duration_days, " +
                m.typePriceCol() + " as price " +
                "from " + m.membershipTypesTable() + " where id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                return Optional.of(new MembershipType(
                        rs.getLong("Id"),
                        rs.getString("name"),
                        rs.getInt("duration_days"),
                        rs.getBigDecimal("price")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error MembershipTypeRepository.findById: " + e.getMessage(), e);
        }
    }
}