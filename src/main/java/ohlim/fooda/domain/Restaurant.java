package ohlim.fooda.domain;

import lombok.*;
import ohlim.fooda.dto.RestaurantDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String userName;

    @Column(nullable=false)
    private Long folderId;

    @Column(nullable=false)
    private String name;

    @Column(length = 30)
    private String phoneNumber;

    @Column
    private Double lat;

    @Column
    private Double lon;

    @Column
    private String location;

    //Korean : K, Japanese : J, Chinese : C, Western : W, World : O, Buffet : B, Cafe : F, Izakaya : I
    @Column
    private Character category;

    @Column
    private String businessHour;

    @CreationTimestamp
    private Date regdate;

    @UpdateTimestamp
    private Date updatedate;

    public void setRestaurantInfo(RestaurantDto.RestaurantInfo restaurantInfo){
        this.folderId = restaurantInfo.getFolderId();
        this.name = restaurantInfo.getName();
        this.phoneNumber = restaurantInfo.getPhoneNumber();
        this.lat = restaurantInfo.getLat();
        this.lon = restaurantInfo.getLon();
        this.location = restaurantInfo.getLocation();
        this.category = restaurantInfo.getCategory();
        this.businessHour = restaurantInfo.getBusinessHour();
    }
}
