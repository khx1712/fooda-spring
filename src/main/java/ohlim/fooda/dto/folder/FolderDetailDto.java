package ohlim.fooda.dto.folder;

import lombok.*;
import ohlim.fooda.domain.Folder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderDetailDto {
    private Long id;
    private String name;

    public static FolderDetailDto createFolderDetailDto(Folder folder){
        // TODO: ModelMapper 처리하기
        return FolderDetailDto.builder()
                .id(folder.getId())
                .name(folder.getName())
                .build();
    }
}