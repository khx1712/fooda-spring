package ohlim.fooda.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "folder_id")
    private Long id;

    @Column(nullable=false, length=30)
    private String name;

    @CreationTimestamp
    @Column(name = "register_date")
    private Date registerDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    private Date updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Account account;

    @OneToMany(mappedBy = "folder")
    private Set<Restaurant> restaurants = new LinkedHashSet<Restaurant>();

    public void setAccount(Account account){
        if(this.account != null) {	// this.user이 null이 아니면 이 folder객체는 user이 있음을 의미
            this.account.getFolders().remove(this);		// 해당 유저의 폴더에서 삭제
        }
        this.account = account;
        account.getFolders().add(this);
    }
}
