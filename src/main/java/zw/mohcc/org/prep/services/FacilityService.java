package zw.mohcc.org.prep.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.mohcc.org.prep.entities.Facility;
import zw.mohcc.org.prep.repositories.FacilityRepository;

import java.util.List;

@Service
public class FacilityService {
    private final FacilityRepository facilityRepository;

    @Autowired
    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Facility getFacilityBySiteId(String siteCode) {
        return facilityRepository.getFacilitiesBySiteCode(siteCode);
    }
}
