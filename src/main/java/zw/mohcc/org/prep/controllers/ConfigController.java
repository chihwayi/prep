package zw.mohcc.org.prep.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.mohcc.org.prep.entities.Config;
import zw.mohcc.org.prep.services.ConfigService;

import java.util.List;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/current")
    public ResponseEntity<List<Config>> findAll() {
        return ResponseEntity.ok(configService.getAllConfig());
    }

    @PostMapping("/save")
    public ResponseEntity<Config> save(@RequestBody Config config) {
        Config request = new Config();
        request.setSiteCode(config.getSiteCode());
        Config saveConfig = configService.save(request);
        return new ResponseEntity<>(saveConfig, HttpStatus.CREATED);
    }
}
