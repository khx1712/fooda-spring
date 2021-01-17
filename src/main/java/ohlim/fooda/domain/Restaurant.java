package ohlim.fooda.domain;

import javassist.NotFoundException;
import lombok.*;
import ohlim.fooda.dto.restaurant.RestaurantDto;
import ohlim.fooda.service.LocationToGPS;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.json.simple.parser.ParseException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant")
public class Restaurant{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "restaurant_id", unique = true)
    private Long id;

    @Column
    @NotNull(message = "식당의 이름은 필수 값입니다.")
    private String name;

    @Column(name = "phone_number",length = 30)
    private String phoneNumber;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    @NotNull(message = "주소는 필수 값입니다.")
    private String location;

    //Korean : K, Japanese : J, Chinese : C, Western : W, World : O, Buffet : B, Cafe : F, Izakaya : I
    @Column
    private Character category;

    @Column(name = "business_hour")
    private String businessHour;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @CreationTimestamp
    @Column(name = "register_date")
    private Date registerDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    private Date updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private Set<RestImage> restImages = new LinkedHashSet<RestImage>();

    public Set<RestImage> getRestImages(){
        return this.restImages;
    }

    public void setAccount(Account account){
        if(this.account != null) {
            this.account.getRestaurants().remove(this);
        }
        this.account = account;
        account.getRestaurants().add(this);
    }

    public void setFolder(Folder folder){
        if(this.folder != null) {
            this.folder.getRestaurants().remove(this);
        }
        this.folder = folder;
        folder.getRestaurants().add(this);
    }

    public static Restaurant createRestaurant(RestaurantDto restaurantDto, Account account, Folder folder)
            throws NotFoundException, ParseException {
        // TODO: ModelMapper 처리해주기
        Restaurant restaurant = Restaurant.builder()
                .name(restaurantDto.getName())
                .category(restaurantDto.getCategory())
                .businessHour(restaurantDto.getBusinessHour())
                .location(restaurantDto.getLocation())
                .phoneNumber(restaurantDto.getPhoneNumber())
                .latitude(restaurantDto.getLatitude())
                .longitude(restaurantDto.getLongitude())
                .restImages(new LinkedHashSet<>())
                .build();
        restaurant.setAccount(account);
        restaurant.setFolder(folder);
        if(restaurantDto.getLatitude() == null && restaurantDto.getLongitude() == null){
            String locationJson = LocationToGPS.getGPSKakaoApiFromLocation(restaurantDto.getLocation());
            System.out.println(locationJson);
            List<Double> GPS = LocationToGPS.getLatLonFromJsonString(locationJson);
            restaurant.setLatitude(GPS.get(0));
            restaurant.setLongitude(GPS.get(1));
        }else{
            restaurant.setLatitude(restaurantDto.getLatitude());
            restaurant.setLongitude(restaurantDto.getLongitude());
        }
        return restaurant;
    }
}
