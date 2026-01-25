package impl;

import edu.aitu.oop3.db.DbMapping;
import edu.aitu.oop3.db.IDB;
import entities.edu.aitu.oop3.entities.Member;
import repositories.MemberRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class MemberRepositoryImpl implements MemberRepository {
    private final IDB db;
    private final DbMapping m;

    public MemberRepositoryImpl(IDB db) {
        this.db = db;
        this.m = DbMapping.getOrCreate(db);
    }

    @Override
    public Optional<Member> findById(long id) {
        String sql = "select id, " + m.memberNameCol() + " as full_name, " +
                m.memberTypeIdCol() + " as membership_type_id, " +
                m.memberEndDateCol() + " as membership_end_date " +
                "from " + m.membersTable() + " where id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                Long typeId = rs.getObject("membership_type_id", Long.class);
                LocalDate endDate = rs.getObject("membership_end_date", LocalDate.class);

                return Optional.of(new Member(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        typeId,
                        endDate
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error MemberRepository.findById: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateMembership(long memberId, long typeId, LocalDate endDate) {
        String sql = "update " + m.membersTable() +
                " set " + m.memberTypeIdCol() + " = ?, " + m.memberEndDateCol() + " = ? where id = ?";

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, typeId);
            st.setObject(2, endDate);
            st.setLong(3, memberId);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("DB error MemberRepository.updateMembership: " + e.getMessage(), e);
        }
    }
}