package services;

public class NotificationService {
    public void notifyMember(long memberId, String message) {
        // На milestone 1 достаточно консоли
        System.out.println("[NOTIFY] member=" + memberId + " -> " + message);
    }
}