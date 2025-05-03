package com.example.eip;

import java.io.File;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.DirectChannelSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
public class EipApplication {

    public static void main(String[] args) {
        SpringApplication.run(EipApplication.class, args);
    }

    @Bean
    DirectChannelSpec inbound() {
        return MessageChannels.direct();
    }

    @RestController
    class PurchaseOrderController {
        private final MessageChannel inbound;

        public PurchaseOrderController(@Qualifier("inbound") MessageChannel inbound) {
            this.inbound = inbound;
        }

        @PostMapping("/purchase-orders")
        void postPurchaseOrder(@RequestParam MultipartFile file) throws Exception {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            var content = new String(file.getBytes());
            var build = MessageBuilder.withPayload(content).build();
            inbound.send(build);
        }
    }

    @Bean
    IntegrationFlow fileInboundFlow(
            @Qualifier("inbound") MessageChannel inbound,
            @Value("file://${HOME}/Desktop/MyProject/data/purchase-orders") File files) {
        var filesInboundAdapter = Files.inboundAdapter(files).autoCreateDirectory(true);
        return IntegrationFlow.from(filesInboundAdapter)
                .transform(new FileToStringTransformer())
                .channel(inbound)
                .get();
    }

    @Bean
    IntegrationFlow purchaseOrderIntegrationFlow(@Qualifier("inbound") MessageChannel inbound) {
        return IntegrationFlow.from(inbound)
                .transform(new JsonToObjectTransformer(PurchaseOrder.class))
                .handle(
                        (payload, headers) -> {
                            System.out.println("File content: " + payload);
                            headers.keySet().forEach(System.out::println);
                            return payload;
                        })
                .split(PurchaseOrder.class, PurchaseOrder::lineItems)
                .handle(
                        (payload, headers) -> {
                            System.out.println("Line item: " + payload);
                            headers.keySet().forEach(System.out::println);
                            return payload;
                        })
                .aggregate()
                .handle(
                        (payload, headers) -> {
                            System.out.println("Aggregated line items: " + payload);
                            headers.keySet().forEach(System.out::println);
                            return null;
                        })
                .get();
    }
}

record ShippableLineItem(
        PurchaseOrder order, LineItem original, boolean dosmestic, boolean shipped) {
}

record LineItem(String sku, String productName, int quantity, double unitPrice) {
}

record PurchaseOrder(String orderId, String country, Set<LineItem> lineItems, double total) {
}
