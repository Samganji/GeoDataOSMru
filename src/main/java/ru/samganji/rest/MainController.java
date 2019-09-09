package ru.samganji.rest;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/")
public class MainController {

    //корневая страница
    @GetMapping
    public String welcome() {
        return "GeoDataOSM Россия. Добро пожаловать!\n" +
                "Для использования наберите:/тип_географического_объекта/имя_объекта.";
    }

    //кэшируемая страничка выдачи ответа в HTML
    @GetMapping("/{type}/{name}")
    @Cacheable(cacheNames = "geodata")
    public String responseData(@PathVariable("type") String type, @PathVariable("name") String name){

            GeoData geoData = new GeoData(type, name);
            String centerCoord = geoData.getCenterPoint();
            String coord = geoData.getCoordinates();
            String objName = geoData.getObjectName();

        return "<html>\n" +
                " <head>\n" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "  <title>"+ objName +"</title>\n" +
                " </head>\n" +
                " <body>\n" +
                "  <h2>Географический центр:</h2>\n" +
                "  <p>"+ centerCoord +"</p>\n" +
                "  <h2>Массив координат:</h2>\n" +
                "  <p>"+ coord +"</p>\n" +
                " </body>\n" +
                "</html>";
    }
}
