package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.domain.datastorage.EntityTypeEnum;
import com.i4biz.mygarden.domain.datastorage.File;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.expression.ExpressionNodeUtils;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.datastorage.IFileService;
import com.i4biz.mygarden.utils.PageRequestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/data-storage")
public class DataStorageRESTService {
    @Autowired
    IFileService fileService;

    @RequestMapping(value = "/{itemType}/{itemId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void uploadFile(@PathVariable String itemType, @PathVariable Long itemId, @RequestParam("file") MultipartFile file, Principal principal) {
        try {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            Long userId = user.getId();
            String fileName = file.getName();
            String contentType = file.getContentType();
            InputStream data = file.getInputStream();

            File f = new File();

            f.setOwnerId(userId);

            f.setAssociatedEntityType(EntityTypeEnum.valueOf(itemType));
            f.setAssociatedEntityId(itemId);

            f.setMimeType(contentType);
            f.setFileName(fileName);

            fileService.storeFile(f, data);
        } catch (Exception e) {
            throw new RuntimeException("error.file.upload");
        }
    }

    @RequestMapping(value = "/{itemType}/{itemId}/{fileId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateFile(@PathVariable String itemType, @PathVariable Long itemId, @PathVariable Long fileId, @RequestParam("file") MultipartFile file, Principal principal) {
        try {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            Long userId = user.getId();
            String fileName = file.getName();
            String contentType = file.getContentType();
            InputStream data = file.getInputStream();

            File f = fileService.findByIdUserId(fileId, userId);

            f.setAssociatedEntityType(EntityTypeEnum.valueOf(itemType));
            f.setAssociatedEntityId(itemId);

            f.setMimeType(contentType);
            f.setFileName(fileName);

            fileService.storeFile(f, data);
        } catch (Exception e) {
            throw new RuntimeException("error.file.upload");
        }
    }

    @RequestMapping(value = "/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> download(@PathVariable Long fileId, Principal principal, HttpServletResponse response) throws IOException {
        Long userId = principal==null ? null : ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser().getId();
        File file = fileService.findByIdUserId(fileId, userId);
        InputStream is = fileService.dataStream(file);

        CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.DAYS);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.getMimeType()));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + file.getFileName().replace(" ", "_"));

        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .headers(headers)
                .body(IOUtils.toByteArray(is));
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET)
    @ResponseBody
    public List<File> findFiles(Principal principal, HttpServletRequest request) {
        Long userId = principal==null ? null : ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser().getId();

        PageRequest<File> pageRequest = PageRequestUtils
                .buildPageRequest(File.class, request.getParameterMap());

        ExpressionNode root = pageRequest.getConditionTree();

        ExpressionNode security = new ExpressionNode("ownerId", "isNull", null);
        if (userId != null) {
            security = ExpressionNodeUtils.or(
                    security,
                    new ExpressionNode("ownerId", "eq", userId)
            );
        }

        if (root == null) {
            root = security;
        } else {
            root = root.and(security);
        }
        pageRequest.setConditionTree(root);
        return fileService.scroll(pageRequest);
    }

    @RequestMapping(value = "/homePageCarousel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Object[]>> buildHomePageCarousel() {
        CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.DAYS);
        return  ResponseEntity.ok()
                .cacheControl(cacheControl)
                .body(fileService.buildHomePageCarousel());
    }

}
