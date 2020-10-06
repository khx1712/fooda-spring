package ohlim.fooda.controller;

import ohlim.fooda.domain.Folder;
import ohlim.fooda.dto.FolderDto.*;
import ohlim.fooda.dto.ResponseDto;
import ohlim.fooda.service.FolderNotFoundException;
import ohlim.fooda.service.FolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FolderController {
    private final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    FolderService folderService;

    @PostMapping("/user/folder")
    public ResponseEntity<?> create(
            Authentication authentication,
            @RequestBody FolderInfo resource
            ) throws URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Folder folder = Folder.builder()
                .userName(userDetails.getUsername())
                .name(resource.getName())
                .build();
        Folder saved = folderService.addFolder(folder);
        System.out.println("create folder : " + saved.getId());
        URI location_uri = new URI("/user/folder/" + saved.getId());
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "폴더가 생성되었습니다.");
        meta.put("folderId", saved.getId());
        ResFolderDto<?,?> resFolderDto = ResFolderDto.builder().meta(meta).build();
        return ResponseEntity.created(location_uri).body(resFolderDto);
    }

    @PatchMapping("/user/folder/{folderId}")
    public ResponseEntity<?> update(
            Authentication authentication,
            @PathVariable("folderId") Long id,
            @RequestBody FolderInfo resource
    ) throws URISyntaxException, FolderNotFoundException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Folder updated = folderService.updateFolder(id, userDetails.getUsername(), resource.getName());
        URI location_uri = new URI("/user/folder/" + updated.getId());
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "폴더가 수정되었습니다.");
        meta.put("folderId", updated.getId());
        ResFolderDto<?,?> resFolderDto = ResFolderDto.builder().meta(meta).build();
        return ResponseEntity.created(location_uri).body(resFolderDto);
    }

    @DeleteMapping("/user/folder/{folderId}")
    public ResponseEntity<?> delete(
            Authentication authentication,
            @PathVariable("folderId") Long id
    ) throws FolderNotFoundException, URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        folderService.deleteFolder(id, userDetails.getUsername());
        URI location_uri = new URI("/user/folder/" + id);
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "폴더가 삭제되었습니다.");
        meta.put("folderId", id);
        ResFolderDto<?,?> resFolderDto = ResFolderDto.builder().meta(meta).build();
        return ResponseEntity.created(location_uri).body(resFolderDto);
    }

    @GetMapping("/user/folders")
    public ResponseEntity<?> list(
            Authentication authentication
    ) throws URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<Folder> folders = folderService.getFolders(userDetails.getUsername());
        URI location_uri = new URI("/user/folders");
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "폴더 목록입니다.");
        meta.put("folderCnt", folders.size());
        ResFolderDto<?,?> resFolderDto = ResFolderDto.builder()
                .meta(meta)
                .documents(folders)
                .build();
        return ResponseEntity.created(location_uri).body(resFolderDto);
    }

    @GetMapping("/user/folder/{folderId}")
    public ResponseEntity<?> detail(
            Authentication authentication,
            @PathVariable("folderId") Long id
    ) throws URISyntaxException, FolderNotFoundException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Folder folder = folderService.getFolder(id);
        URI location_uri = new URI("/user/folder/" + folder.getId());
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "폴더 상세입니다.");
        meta.put("folderId", folder.getId());
        ResFolderDto<?,?> resFolderDto = ResFolderDto.builder()
                .meta(meta)
                .documents(folder)
                .build();
        return ResponseEntity.created(location_uri).body(resFolderDto);
    }

    @GetMapping("/user/folders/restaurants")
    public ResponseEntity<?> listIncludeRes(
            Authentication authentication
    ) throws URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<FolderIncludeResInfo> foldersRes = folderService.getFoldersIncludeRes(userDetails.getUsername());
        URI location_uri = new URI("/user/folders");
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "레스토랑을 포함한 폴더 목록입니다.");
        meta.put("folderCnt", foldersRes.size());
        ResFolderDto<?,?> resFolderDto = ResFolderDto.builder()
                .meta(meta)
                .documents(foldersRes)
                .build();
        return ResponseEntity.created(location_uri).body(resFolderDto);
    }

    @GetMapping("/user/folder/{folderId}/restaurants")
    public ResponseEntity<?> detailIncludeRes(
            Authentication authentication,
            @PathVariable("folderId") Long id
    ) throws URISyntaxException, FolderNotFoundException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        FolderIncludeResInfo folderRes = folderService.getFolderIncludeRes(userDetails.getUsername(), id);
        URI location_uri = new URI("/user/folders");
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "레스토랑을 포함한 폴더 상세입니다.");
        meta.put("folderId", folderRes.getId());
        ResFolderDto<?,?> resFolderDto = ResFolderDto.builder()
                .meta(meta)
                .documents(folderRes)
                .build();
        return ResponseEntity.created(location_uri).body(resFolderDto);
    }
}
