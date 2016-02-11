package kg;

import io.rhiot.cloudplatform.connector.IoTConnector;
import io.rhiot.cloudplatform.runtime.spring.CloudPlatform;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.io.File;

import static io.rhiot.cloudplatform.connector.Header.arguments;

public class Kg extends CloudPlatform {

    @Autowired
    IoTConnector connector;

    File localStore = new File("/var/rhiot/gateway/camera");

    @Bean
    RouteBuilder webcam() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                localStore.mkdir();

                from("webcam:label?motion=true&format=JPG").
                        to("log:CameraMotionNotification").
                        to("file://" + localStore.getAbsolutePath());

                        from("file://" + localStore.getAbsolutePath() + "?sortBy=file:modified").
                        onException(Exception.class).maximumRedeliveries(100000).useExponentialBackOff().end().
                        process(exc -> connector.toBus("camera.process", exc.getIn().getBody(byte[].class),
                                arguments("cameraHall0002", "eu")));
            }
        };
    }

}
