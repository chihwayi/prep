package zw.mohcc.org.prep.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.mohcc.org.prep.entities.Config;
import zw.mohcc.org.prep.repositories.ConfigRepository;

import java.util.List;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;

    @Autowired
    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public Config save(Config config) {
        return configRepository.save(config);
    }

    public List<Config> getAllConfig() {
        return configRepository.findAll();
    }
}
