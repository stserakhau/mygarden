package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.domain.region.RegionView;
import com.i4biz.mygarden.service.IRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/regions")
public class RegionRESTService {
    @Autowired
    private IRegionService regionService;

    @RequestMapping(value = "/countries/", method = RequestMethod.GET)
    @ResponseBody
    public List<RegionView> countries() {
        return regionService.getAllCountries();
    }

    @RequestMapping(value = "/citiesByCountry/", method = RequestMethod.GET)
    @ResponseBody
    public List<RegionView> citiesByCountry(@RequestParam String countryName) {
        return regionService.findCitiesByCountry(countryName);
    }
}
