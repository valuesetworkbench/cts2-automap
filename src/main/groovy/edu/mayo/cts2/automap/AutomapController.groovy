package edu.mayo.cts2.automap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class AutomapController {

    @Autowired
    AutomapService automapService

    @RequestMapping(value = "/map", method = [RequestMethod.GET, RequestMethod.POST])
    def map(String mapversion, Integer distance) {
        automapService.map(mapversion, distance)
    }

}
