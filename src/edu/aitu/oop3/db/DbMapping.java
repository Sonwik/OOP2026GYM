package edu.aitu.oop3.db;

import java.sql.*;
import java.util.*;

public final class DbMapping {
    private static DbMapping INSTANCE;

    private final String membersTable;          // "schema"."table"
    private final String membershipTypesTable;
    private final String classesTable;
    private final String classBookingsTable;

    private final String memberNameCol;
    private final String memberTypeIdCol;
    private final String memberEndDateCol;

    private final String typeNameCol;
    private final String typeDurationDaysCol;
    private final String typePriceCol;

    private final String classTitleCol;
    private final String classStartTimeCol;
    private final String classCapacityCol;

    private final String bookingMemberIdCol;
    private final String bookingClassIdCol;
    private final String bookingStatusCol;

    public static synchronized DbMapping getOrCreate(IDB db) {
        if (INSTANCE == null) {
            INSTANCE = new DbMapping(db);
        }
        return INSTANCE;
    }

    private DbMapping(IDB db) {
        try (Connection con = db.getConnection()) {
            List<TableRef> tables = loadTables(con);

            TableRef members = resolveTable(tables,
                    List.of("members", "member", "gym_members", "club_members"),
                    List.of("member"),
                    List.of("membership") // exclude
            );

            TableRef types = resolveTable(tables,
                    List.of("membership_types", "membership_type", "membershiptypes", "membershiptype", "plans"),
                    List.of("membership", "type"),
                    List.of()
            );

            TableRef classes = resolveTable(tables,
                    List.of("classes", "class", "fitness_classes", "fitness_class", "gym_classes"),
                    List.of("class"),
                    List.of("booking")
            );

            TableRef bookings = resolveTable(tables,
                    List.of("class_bookings", "class_booking", "bookings", "classbooking", "class_book"),
                    List.of("booking"),
                    List.of()
            );

            this.membersTable = q(members);
            this.membershipTypesTable = q(types);
            this.classesTable = q(classes);
            this.classBookingsTable = q(bookings);

            // resolve columns
            Set<String> memberCols = loadColumns(con, members);
            this.memberNameCol = resolveColumn(memberCols, List.of("full_name", "name", "fullname", "member_name"));
            this.memberTypeIdCol = resolveColumn(memberCols, List.of("membership_type_id", "type_id", "membershiptype_id"));
            this.memberEndDateCol = resolveColumn(memberCols, List.of("membership_end_date", "end_date", "expires_at", "membership_end"));

            Set<String> typeCols = loadColumns(con, types);
            this.typeNameCol = resolveColumn(typeCols, List.of("name", "title", "type_name"));
            this.typeDurationDaysCol = resolveColumn(typeCols, List.of("duration_days", "duration", "days", "duration_in_days"));
            this.typePriceCol = resolveColumn(typeCols, List.of("price", "cost", "amount"));

            Set<String> classCols = loadColumns(con, classes);
            this.classTitleCol = resolveColumn(classCols, List.of("title", "name", "class_name"));
            this.classStartTimeCol = resolveColumn(classCols, List.of("start_time", "starts_at", "start", "date_time"));
            this.classCapacityCol = resolveColumn(classCols, List.of("capacity", "max_capacity", "limit"));

            Set<String> bookingCols = loadColumns(con, bookings);
            this.bookingMemberIdCol = resolveColumn(bookingCols, List.of("member_id", "memberid"));
            this.bookingClassIdCol = resolveColumn(bookingCols, List.of("class_id", "classid"));
            this.bookingStatusCol = resolveColumn(bookingCols, List.of("status", "booking_status"));

        } catch (SQLException e) {
            throw new RuntimeException("DbMapping init error: " + e.getMessage(), e);
        }
    }

    // -------- getters for tables ----------
    public String membersTable() { return membersTable; }
    public String membershipTypesTable() { return membershipTypesTable; }
    public String classesTable() { return classesTable; }
    public String classBookingsTable() { return classBookingsTable; }

    // -------- getters for columns ----------
    public String memberNameCol() { return memberNameCol; }
    public String memberTypeIdCol() { return memberTypeIdCol; }
    public String memberEndDateCol() { return memberEndDateCol; }

    public String typeNameCol() { return typeNameCol; }
    public String typeDurationDaysCol() { return typeDurationDaysCol; }
    public String typePriceCol() { return typePriceCol; }

    public String classTitleCol() { return classTitleCol; }
    public String classStartTimeCol() { return classStartTimeCol; }
    public String classCapacityCol() { return classCapacityCol; }

    public String bookingMemberIdCol() { return bookingMemberIdCol; }
    public String bookingClassIdCol() { return bookingClassIdCol; }
    public String bookingStatusCol() { return bookingStatusCol; }
    private static String q(TableRef t) {
        return "\"" + t.schema + "\".\"" + t.name + "\"";
    }

    private static List<TableRef> loadTables(Connection con) throws SQLException {
        String sql = """
                select table_schema, table_name
                from information_schema.tables
                where table_type = 'BASE TABLE'
                  and table_schema not in ('pg_catalog', 'information_schema')
                order by (case when table_schema='public' then 0 else 1 end), table_schema, table_name
                """;
        List<TableRef> out = new ArrayList<>();
        try (PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                out.add(new TableRef(rs.getString("table_schema"), rs.getString("table_name")));
            }
        }
        return out;
    }

    private static Set<String> loadColumns(Connection con, TableRef t) throws SQLException {
        String sql = """
                select column_name
                from information_schema.columns
                where table_schema = ? and table_name = ?
                """;
        Set<String> cols = new HashSet<>();
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, t.schema);
            st.setString(2, t.name);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) cols.add(rs.getString("column_name"));
            }
        }
        return cols;
    }

    private static String resolveColumn(Set<String> cols, List<String> candidatesLower) {
        for (String cand : candidatesLower) {
            for (String c : cols) {
                if (c.equalsIgnoreCase(cand)) return c;
            }
        }
        for (String cand : candidatesLower) {
            for (String c : cols) {
                if (c.toLowerCase().contains(cand.toLowerCase())) return c;
            }
        }
        throw new IllegalStateException("Cannot resolve column. Available columns: " + cols);
    }

    private static TableRef resolveTable(List<TableRef> tables,
                                         List<String> exactCandidates,
                                         List<String> mustContain,
                                         List<String> mustNotContain) {
        for (String exact : exactCandidates) {
            for (TableRef t : tables) {
                if (t.name.equalsIgnoreCase(exact)) return t;
            }
        }

        for (TableRef t : tables) {
            String n = t.name.toLowerCase();

            boolean ok = true;
            for (String k : mustContain) {
                if (!n.contains(k.toLowerCase())) { ok = false; break; }
            }
            if (!ok) continue;

            for (String bad : mustNotContain) {
                if (n.contains(bad.toLowerCase())) { ok = false; break; }
            }
            if (ok) return t;
        }

        throw new IllegalStateException("Cannot resolve table. Existing tables: " + tables);
    }

    private static final class TableRef {
        final String schema;
        final String name;
        TableRef(String schema, String name) { this.schema = schema; this.name = name; }
        @Override public String toString() { return schema + "." + name; }
    }
}