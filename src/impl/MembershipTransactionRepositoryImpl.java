package impl;

import edu.aitu.oop3.db.IDB;
import edu.aitu.oop3.db.RowMapper;
import entities.edu.aitu.oop3.entities.MembershipTransaction;
import repositories.MembershipTransactionRepository;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembershipTransactionRepositoryImpl implements MembershipTransactionRepository {

    private final IDB db;

    // таблица стабильная (как в schema.sql)
    private static final String TABLE = "public.membership_transactions";

    public MembershipTransactionRepositoryImpl(IDB db) {
        this.db = db;
    }

    private final RowMapper<MembershipTransaction> mapper = rs -> {
        MembershipTransaction tx = MembershipTransaction.builder()
                .memberId(rs.getLong("member_id"))
                .typeId(rs.getLong("type_id"))
                .startDate(rs.getObject("start_date", java.time.LocalDate.class))
                .endDate(rs.getObject("end_date", java.time.LocalDate.class))
                .basePrice(rs.getBigDecimal("base_price"))
                .finalPrice(rs.getBigDecimal("final_price"))
                .build();

        tx.setId(rs.getLong("id"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) tx.setCreatedAt(ts.toInstant().atOffset(OffsetDateTime.now().getOffset()));
        return tx;
    };

    @Override
    public Optional<MembershipTransaction> findById(Long id) {
        String sql = "select * from " + TABLE + " where id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapper.map(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("DB error MembershipTransactionRepository.findById: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MembershipTransaction> findAll() {
        String sql = "select * from " + TABLE + " order by created_at desc";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            List<MembershipTransaction> list = new ArrayList<>();
            while (rs.next()) list.add(mapper.map(rs));
            return list;

        } catch (Exception e) {
            throw new RuntimeException("DB error MembershipTransactionRepository.findAll: " + e.getMessage(), e);
        }
    }

    @Override
    public MembershipTransaction save(MembershipTransaction tx) {
        String sql = "insert into " + TABLE +
                " (member_id, type_id, start_date, end_date, base_price, final_price) " +
                "values (?, ?, ?, ?, ?, ?) returning id, created_at";

        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, tx.getMemberId());
            st.setLong(2, tx.getTypeId());
            st.setDate(3, Date.valueOf(tx.getStartDate()));
            st.setDate(4, Date.valueOf(tx.getEndDate()));
            st.setBigDecimal(5, tx.getBasePrice());
            st.setBigDecimal(6, tx.getFinalPrice());

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    tx.setId(rs.getLong("id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) tx.setCreatedAt(ts.toInstant().atOffset(OffsetDateTime.now().getOffset()));
                }
            }
            return tx;

        } catch (Exception e) {
            throw new RuntimeException("DB error MembershipTransactionRepository.save: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "delete from " + TABLE + " where id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setLong(1, id);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("DB error MembershipTransactionRepository.deleteById: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MembershipTransaction> findByMemberId(long memberId) {
        String sql = "select * from " + TABLE + " where member_id = ? order by created_at desc";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, memberId);

            List<MembershipTransaction> list = new ArrayList<>();
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) list.add(mapper.map(rs));
            }
            return list;

        } catch (Exception e) {
            throw new RuntimeException("DB error MembershipTransactionRepository.findByMemberId: " + e.getMessage(), e);
        }
    }
}
