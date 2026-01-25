package exceptions;

public class MembershipExpiredException extends RuntimeException {
    public MembershipExpiredException(String message) { super(message); }
}//