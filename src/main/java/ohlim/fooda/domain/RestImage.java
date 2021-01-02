package ohlim.fooda.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.net.URL;
import java.util.Date;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private Long restaurantId;

    @Column(nullable=false)
    private String userName;

    @Column(nullable = false) // 실제 디스크에 저장되는 이름
    private String fileSaveName;

    @Column(nullable = false) // 파일의 원래 이름
    private String fileOriginName;

    @Column(nullable = false) // 파일의 저장 경로
    private String filePath;

    @Column(nullable = false) // 파일의 URL
    private String fileUrl;

    @Column(nullable = false) // 파일 확장자
    private String fileExt;

    @Column(nullable = false)
    private  String contentType;

    @CreationTimestamp
    private Date registerDate;
}
