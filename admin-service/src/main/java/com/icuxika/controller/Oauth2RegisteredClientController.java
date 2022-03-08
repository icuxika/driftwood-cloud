package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.entity.Oauth2RegisteredClient;
import com.icuxika.service.Oauth2RegisteredClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class Oauth2RegisteredClientController {

    private final Oauth2RegisteredClientService oauth2RegisteredClientService;

    public Oauth2RegisteredClientController(Oauth2RegisteredClientService oauth2RegisteredClientService) {
        this.oauth2RegisteredClientService = oauth2RegisteredClientService;
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/page")
    public ApiData<Page<Oauth2RegisteredClient>> getPage(@PageableDefault(sort = "id") Pageable pageable, Oauth2RegisteredClient oauth2RegisteredClient) {
        Page<Oauth2RegisteredClient> page = oauth2RegisteredClientService.getPage(pageable, oauth2RegisteredClient);
        return ApiData.ok(page);
    }

    @GetMapping("/{id}")
    public ApiData<Oauth2RegisteredClient> getById(@PathVariable("id") String id) {
        Oauth2RegisteredClient oauth2RegisteredClient = oauth2RegisteredClientService.getById(id);
        return ApiData.ok(oauth2RegisteredClient);
    }

    @PostMapping
    public ApiData<Void> save(@RequestBody Oauth2RegisteredClient oauth2RegisteredClient) {
        oauth2RegisteredClientService.save(oauth2RegisteredClient);
        return ApiData.okMsg("新增成功");
    }

    @PutMapping
    public ApiData<Void> update(@RequestBody Oauth2RegisteredClient oauth2RegisteredClient) {
        oauth2RegisteredClientService.update(oauth2RegisteredClient);
        return ApiData.okMsg("更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiData<Void> deleteById(@PathVariable("id") String id) {
        oauth2RegisteredClientService.deleteById(id);
        return ApiData.okMsg("删除成功");
    }
}
