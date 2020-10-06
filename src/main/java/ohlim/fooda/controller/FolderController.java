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
import java.util.Map;

@RestController
public class FolderController {
    private final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    FolderService folderService;

    @PostMapping("/user/folder")
    public ResponseEntity<?> create(
            Authentication authentication,
            @RequestBody String name
            ) throws URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Folder folder = Folder.builder()
                .userName(userDetails.getUsername())
                .name(name)
                .build();
        Folder saved = folderService.addFolder(folder);
        System.out.println("create folder : " + saved.getId());
        URI location_uri = new URI("/user/folder/" + saved.getId());
        Map<String, Object> meta = new HashMap<>();
        meta.put("success", true);
        meta.put("msg", "폴더가 생성되었습니다");
        meta.put("folderId", saved.getId());
        ResFolderDto<?,?> resFolderDto = ResFolderDto.builder().meta(meta).build();
        return ResponseEntity.created(location_uri).body(resFolderDto);
    }

    @PatchMapping("/user/folder/{folderId}")
    public ResponseEntity<?> update(
            Authentication authentication,
            @PathVariable("folderId") Long id,
            @RequestBody String name
    ) throws URISyntaxException, FolderNotFoundException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Folder updated = folderService.updateFolder(id, userDetails.getUsername(), name);
        URI location_uri = new URI("/user/folder/" + updated.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("folderId", updated.getId());
        return ResponseEntity.created(location_uri).body(map);
    }

    @DeleteMapping("/user/folder/{folderId}")
    public ResponseEntity<?> delete(
            Authentication authentication,
            @PathVariable("foderId") Long id
    ) throws FolderNotFoundException, URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        folderService.deleteFolder(id, userDetails.getUsername());
        URI location_uri = new URI("/user/folder/" + id);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("folderId", id);
        return ResponseEntity.created(location_uri).body(map);
    }

}
