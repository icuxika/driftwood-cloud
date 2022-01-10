package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.entity.Oauth2RegisteredClient;
import com.icuxika.service.Oauth2RegisteredClientService;
import org.springframework.data.domain.Page;
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
    public ApiData<Page<Oauth2RegisteredClient>> getPage() {
        Page<Oauth2RegisteredClient> page = oauth2RegisteredClientService.getPage();
        return ApiData.ok(page);
    }

    @GetMapping("/{id}")
    public void getById(@PathVariable("id") String id) {
    }

    @PostMapping
    public void save(@RequestBody Oauth2RegisteredClient oauth2RegisteredClient) {
    }

    @PutMapping
    public void update(@RequestBody Oauth2RegisteredClient oauth2RegisteredClient) {
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") String id) {
    }
}
