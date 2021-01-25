package ohlim.fooda.dto.folder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class FolderDto {
    @ApiModelProperty(name = "폴더 이름", required = true)
    private String name;
}
