package kg;

import io.rhiot.cloudplatform.connector.IoTConnector;
import io.rhiot.cloudplatform.runtime.spring.CloudPlatform;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import static io.rhiot.cloudplatform.connector.Header.arguments;

public class Kg extends CloudPlatform {

    @Autowired
    IoTConnector connector;

    @Bean
    RouteBuilder webcam() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("webcam:label?motion=true&format=JPG").
                        to("log:CameraNotification").
                        process(exc -> connector.toBus("camera.process", exc.getIn().getBody(byte[].class),
                                arguments("cameraHall0002", "eu")));
            }
        };
    }

}
