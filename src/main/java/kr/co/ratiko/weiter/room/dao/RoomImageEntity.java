package kr.co.ratiko.weiter.room.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "room_image")
public class RoomImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    protected RoomImageEntity() {
    }

    RoomImageEntity(RoomEntity room, String url, int displayOrder) {
        this.room = room;
        this.url = url;
        this.displayOrder = displayOrder;
    }

    public String url() {
        return url;
    }
}
