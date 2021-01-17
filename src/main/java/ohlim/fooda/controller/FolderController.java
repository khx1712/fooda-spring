package ohlim.fooda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ohlim.fooda.dto.SuccessResponse;
import ohlim.fooda.dto.folder.FolderDto;
import ohlim.fooda.dto.folder.FolderDetailDto;
import ohlim.fooda.dto.folder.FolderRestaurantDto;
import ohlim.fooda.service.FolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

@Api(tags = {"Folder API"})
@RestController
public class FolderController {
    private final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    FolderService folderService;

    @ApiOperation(value = "폴더 등록", notes = "새로운 폴더를 등록합니다.")
    @PostMapping("/user/folder")
    public ResponseEntity<?> create(
            Authentication authentication,
            @RequestBody FolderDto resource
            ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long folderId = folderService.addFolder(userDetails.getUsername(), resource);
        return  new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("'"+folderId.toString()+"' 폴더를 추가하였습니다.")
                        // TODO: Dto 처리해줄지 고민해보기
                        .meta( new HashMap<String, Long>(){{
                            put("folderId", folderId);
                        }}).build()
                , HttpStatus.CREATED);
    }

    @ApiOperation(value = "폴더 수정", notes = "폴더 이름을 수정합니다.")
    @PatchMapping("/user/folder/{folderId}")
    public ResponseEntity<?> update(
            Authentication authentication,
            @PathVariable("folderId") Long id,
            @RequestBody FolderDto resource
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        folderService.updateFolder(id, resource);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("'"+id.toString()+"' 폴더를 수정하였습니다.").build()
                , HttpStatus.OK);
    }


    @ApiOperation(value = "폴더 삭제", notes = "해당 id의 폴더를 삭제합니다.")
    @DeleteMapping("/user/folder/{folderId}")
    public ResponseEntity<?> delete(
            Authentication authentication,
            @PathVariable("folderId") Long id
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        folderService.deleteFolder(id, userDetails.getUsername());
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("'"+id.toString()+"' 폴더를 삭제하였습니다.").build()
                , HttpStatus.OK);
    }

    @ApiOperation(value = "식당 상세", notes = "해당 id의 식당 상세정보를 확인합니다.")
    @GetMapping("/user/folder/{folderId}")
    public ResponseEntity<?> detail(
            Authentication authentication,
            @PathVariable("folderId") Long id
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        FolderDetailDto folderDetailDto = folderService.getFolder(id);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("'"+id.toString()+"' 폴더의 상세입니다.")
                        .documents(folderDetailDto).build()
                , HttpStatus.OK);
    }

    @ApiOperation(value = "폴더 상세(식당 포함)", notes = "해당 id의 폴더 상세정보를 확인합니다(식당 포함).")
    @GetMapping("/user/folder/{folderId}/restaurants")
    public ResponseEntity<?> listIncludeRes(
            Authentication authentication,
            @PathVariable("folderId") Long id
    ) throws URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        FolderRestaurantDto folderRestaurantDto = folderService.getFolderIncludeRest(id);
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("식당을 포함한 '"+id.toString()+"' 폴더의 상세입니다.")
                        .documents(folderRestaurantDto).build()
                , HttpStatus.OK);
    }

    @ApiOperation(value = "폴더 목록(유저 이름)", notes = "해당 유저 아이디의 폴더 목록을 제공합니다.")
    @GetMapping("/user/folders")
    public ResponseEntity<?> list(
            Authentication authentication
    ) throws URISyntaxException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<FolderDetailDto> folders = folderService.getFoldersByUserName(userDetails.getUsername());
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message("'"+userDetails.getUsername()+"' 유저의 폴더 목록입니다.")
                        .documents(folders).build()
                , HttpStatus.OK);
    }
}
