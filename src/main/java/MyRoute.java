import javax.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class MyRoute extends RouteBuilder {
  public void configure() throws Exception {
    from("netty:tcp://0.0.0.0:9999")
        .process(
            new Processor() {
              public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Response");
              }
            });

    from("rest:get:hello/{message}/{ip}")
        .setBody(
            exchange -> {
              return exchange.getIn().getHeader("message");
            })
        .toD("netty:tcp://${header.ip}:9999?disconnect=true")
        .setBody(
            e -> {
              System.out.println("Got response: " + e.getIn().getBody());
              return "Response: " + e.getIn().getBody();
            })
        .end();
  }
}
