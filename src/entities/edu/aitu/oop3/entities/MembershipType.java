package entities.edu.aitu.oop3.entities;
import java.math.BigDecimal;
public class MembershipType {
    private Long id;
    private String name;
    private int durationDays;
    private BigDecimal price;

    public MembershipType(Long id, String name, int durationDays, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.durationDays = durationDays;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getDurationDays() { return durationDays; }
    public BigDecimal getPrice() { return price; }
}
