import edu.aitu.oop3.db.DatabaseConnection;
import edu.aitu.oop3.db.DbMapping;
import edu.aitu.oop3.db.IDB;

import exceptions.BookingAlreadyExistsException;
import exceptions.NotFoundException;

import impl.ClassBookingRepositoryImpl;
import impl.FitnessClassRepositoryImpl;
import impl.MemberRepositoryImpl;
import impl.MembershipTypeRepositoryImpl;

import repositories.ClassBookingRepository;
import repositories.FitnessClassRepository;
import repositories.MemberRepository;
import repositories.MembershipTypeRepository;

import services.BookingService;
import services.MembershipService;
import services.NotificationService;

import entities.edu.aitu.oop3.entities.Member;
import entities.edu.aitu.oop3.entities.MembershipType;
import entities.edu.aitu.oop3.entities.FitnessClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {


    private static final boolean USE_COLOR = true;

    private static final String RESET  = USE_COLOR ? "\u001B[0m"  : "";
    private static final String BOLD   = USE_COLOR ? "\u001B[1m"  : "";
    private static final String DIM    = USE_COLOR ? "\u001B[2m"  : "";
    private static final String RED    = USE_COLOR ? "\u001B[31m" : "";
    private static final String GREEN  = USE_COLOR ? "\u001B[32m" : "";
    private static final String YELLOW = USE_COLOR ? "\u001B[33m" : "";
    private static final String BLUE   = USE_COLOR ? "\u001B[34m" : "";
    private static final String CYAN   = USE_COLOR ? "\u001B[36m" : "";

    public static void main(String[] args) {

        banner("🏋️ Fitness Club • Demo Run");

        step(1, "Подключение к Supabase");
        try (Connection connection = DatabaseConnection.getConnection()) {
            ok("Connected successfully!");

            String sql = "SELECT CURRENT_TIMESTAMP";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    info("Database time: " + rs.getTimestamp(1));
                }
            }
        } catch (SQLException e) {
            fail("Ошибка подключения к базе");
            e.printStackTrace();
            return;
        }

        IDB db = DatabaseConnection::getConnection;

        step(2, "Инициализация маппинга таблиц/колонок");
        try {
            DbMapping.getOrCreate(db);
            ok("DbMapping ready ✅");
        } catch (Exception e) {
            fail("DbMapping error (не нашёл таблицы/колонки): " + e.getMessage());
            e.printStackTrace();
            return;
        }

        MemberRepository memberRepo = new MemberRepositoryImpl(db);
        MembershipTypeRepository typeRepo = new MembershipTypeRepositoryImpl(db);
        FitnessClassRepository classRepo = new FitnessClassRepositoryImpl(db);
        ClassBookingRepository bookingRepo = new ClassBookingRepositoryImpl(db);

        MembershipService membershipService = new MembershipService(memberRepo, typeRepo);
        BookingService bookingService = new BookingService(memberRepo, classRepo, bookingRepo);
        NotificationService notificationService = new NotificationService();

        long memberId = 1;
        long typeId = 1;
        long classId = 1;

        step(3, "Загрузка данных (Member / MembershipType / FitnessClass)");
        Member member;
        MembershipType type;
        FitnessClass fc;

        try {
            member = memberRepo.findById(memberId)
                    .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));
            type = typeRepo.findById(typeId)
                    .orElseThrow(() -> new NotFoundException("MembershipType not found: " + typeId));
            fc = classRepo.findById(classId)
                    .orElseThrow(() -> new NotFoundException("Class not found: " + classId));

            ok("Данные найдены ✅");
            line("👤 Member: " + BOLD + member.getFullName() + RESET + " (id=" + memberId + ")");
            line("🏷️ Type: " + BOLD + type.getName() + RESET + " (" + type.getDurationDays() + " days, id=" + typeId + ")");
            line("🧘 Class: " + BOLD + fc.getTitle() + RESET + " (cap=" + fc.getCapacity() + ", id=" + classId + ")");
            line("");

        } catch (RuntimeException e) {
            fail("Нет данных для demo id. " + e.getMessage());
            e.printStackTrace();
            return;
        }
        step(4, "Продление membership");
        try {
            var newEnd = membershipService.buyOrExtend(memberId, typeId);
            ok("Membership extended ✅");
            line("📅 Новый конец: " + BOLD + newEnd + RESET);
        } catch (RuntimeException e) {
            fail("Ошибка при продлении membership: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        step(5, "Бронирование класса");
        try {
            var booking = bookingService.bookClass(memberId, classId);
            ok("Booking created ✅");
            line("🎟️ bookingId=" + booking.getId() + " | member=" + memberId + " | class=" + classId);
            notificationService.notifyMember(memberId, "You booked class " + classId);

        } catch (BookingAlreadyExistsException e) {
            warn("Уже есть бронь (member=" + memberId + ", class=" + classId + ")");
            long altClassId = classId + 1;

            line(DIM + "Пробую другой classId = " + altClassId + RESET);
            try {
                var booking2 = bookingService.bookClass(memberId, altClassId);
                ok("Booking created ✅");
                line("🎟️ bookingId=" + booking2.getId() + " | member=" + memberId + " | class=" + altClassId);
                notificationService.notifyMember(memberId, "You booked class " + altClassId);
            } catch (RuntimeException ex) {
                fail("Не получилось забронировать другой класс: " + ex.getMessage());
                ex.printStackTrace();
            }

        } catch (RuntimeException e) {
            fail("Booking error: " + e.getMessage());
            e.printStackTrace();
        }

        step(6, "Attendance history");
        try {
            var history = bookingRepo.attendanceHistory(memberId);
            if (history.isEmpty()) {
                warn("История пустая");
            } else {
                ok("Найдено записей: " + history.size());

                for (String row : history) {

                    String pretty = prettifyHistoryRow(row);
                    line("📌 " + pretty);
                }
            }
        } catch (RuntimeException e) {
            fail("History error: " + e.getMessage());
            e.printStackTrace();
        }

        banner("✅ Done");
    }

    private static void banner(String title) {
        String line = "══════════════════════════════════════════════════";
        System.out.println(BLUE + line + RESET);
        System.out.println(BLUE + " " + BOLD + title + RESET);
        System.out.println(BLUE + line + RESET);
    }

    private static void step(int n, String text) {
        System.out.println("\n" + CYAN + BOLD + "▶ Step " + n + ":" + RESET + " " + text);
    }

    private static void ok(String msg) {
        System.out.println(GREEN + "✅ " + msg + RESET);
    }

    private static void warn(String msg) {
        System.out.println(YELLOW + "⚠️  " + msg + RESET);
    }

    private static void fail(String msg) {
        System.out.println(RED + "❌ " + msg + RESET);
    }

    private static void info(String msg) {
        System.out.println(CYAN + "ℹ️  " + msg + RESET);
    }

    private static void line(String msg) {
        System.out.println(msg);
    }

    private static String prettifyHistoryRow(String raw) {
        String[] parts = raw.split("\\s*\\|\\s*");
        if (parts.length < 4) return raw;
        String id = parts[0].trim();
        String title = parts[1].trim();
        String time = parts[2].trim();
        String status = parts[3].trim();
        return BOLD + id + RESET + "  🏷️ " + title + "  🕒 " + time + "  ✅ " + status;
    }
}
