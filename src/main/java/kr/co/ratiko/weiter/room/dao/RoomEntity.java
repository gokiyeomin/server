package kr.co.ratiko.weiter.room.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kr.co.ratiko.weiter.common.persistence.BaseTimeEntity;
import kr.co.ratiko.weiter.room.domain.Gate;
import kr.co.ratiko.weiter.room.domain.TradeStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "room")
public class RoomEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "deposit_won", nullable = false)
    private int depositWon;

    @Column(name = "monthly_rent_won", nullable = false)
    private int monthlyRentWon;

    @Column(name = "maintenance_fee_won", nullable = false)
    private int maintenanceFeeWon;

    @Column(name = "monthly_cost_won", insertable = false, updatable = false)
    private int monthlyCostWon;

    @Column(name = "move_in_date", nullable = false)
    private LocalDate moveInDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gate", nullable = false, length = 30)
    private Gate gate;

    @Column(name = "transfer_benefit", nullable = false)
    private boolean transferBenefit;

    @Column(name = "transfer_benefit_detail", columnDefinition = "TEXT")
    private String transferBenefitDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_status", nullable = false, length = 20)
    private TradeStatus tradeStatus;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "contact_url", nullable = false, length = 500)
    private String contactUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", nullable = false)
    private Map<String, Object> details;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<RoomImageEntity> images = new ArrayList<>();

    protected RoomEntity() {
    }

    public RoomEntity(
            Long id,
            String title,
            int depositWon,
            int monthlyRentWon,
            int maintenanceFeeWon,
            LocalDate moveInDate,
            LocalDate expiryDate,
            Gate gate,
            boolean transferBenefit,
            String transferBenefitDetail,
            TradeStatus tradeStatus,
            String description,
            String contactUrl,
            Map<String, Object> details) {
        this.id = id;
        this.title = title;
        this.depositWon = depositWon;
        this.monthlyRentWon = monthlyRentWon;
        this.maintenanceFeeWon = maintenanceFeeWon;
        this.moveInDate = moveInDate;
        this.expiryDate = expiryDate;
        this.gate = gate;
        this.transferBenefit = transferBenefit;
        this.transferBenefitDetail = transferBenefitDetail;
        this.tradeStatus = tradeStatus;
        this.description = description;
        this.contactUrl = contactUrl;
        this.details = details;
    }

    public void addImage(String url) {
        images.add(new RoomImageEntity(this, url, images.size()));
    }

    public Long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public int depositWon() {
        return depositWon;
    }

    public int monthlyRentWon() {
        return monthlyRentWon;
    }

    public int maintenanceFeeWon() {
        return maintenanceFeeWon;
    }

    public LocalDate moveInDate() {
        return moveInDate;
    }

    public LocalDate expiryDate() {
        return expiryDate;
    }

    public Gate gate() {
        return gate;
    }

    public boolean transferBenefit() {
        return transferBenefit;
    }

    public String transferBenefitDetail() {
        return transferBenefitDetail;
    }

    public TradeStatus tradeStatus() {
        return tradeStatus;
    }

    public String description() {
        return description;
    }

    public String contactUrl() {
        return contactUrl;
    }

    public Map<String, Object> details() {
        return details;
    }

    public List<String> imageUrls() {
        return images.stream().map(RoomImageEntity::url).toList();
    }
}
