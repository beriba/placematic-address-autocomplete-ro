package pl.placematic.address.autocomplete.ro.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;

@Configuration
@EnableElasticsearchRepositories(basePackages = "pl.placematic.address.autocomplete.ro.elastic.repository")
public class EsConfiguration {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private int port;

    @Value("${elasticsearch.clustername}")
    private String clusterName;

    @Bean
    public Client client() throws Exception {

        Settings esSettings = Settings.builder()
//                .put("cluster.fieldName", clusterName)
                .put("client.transport.ignore_cluster_name", true)
                .put("client.transport.sniff", false)
                .build();

        return new PreBuiltTransportClient(esSettings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new ElasticsearchTemplate(client());
    }

}
