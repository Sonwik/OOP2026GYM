package services;

public class NotificationService {
    public void notifyMember(long memberId, String message) {

        System.out.println("[NOTIFY] Member=" + memberId + " -> " + message);
    }
}