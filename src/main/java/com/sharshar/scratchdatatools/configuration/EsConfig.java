package com.sharshar.scratchdatatools.configuration;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;

/**
 * Created by lsharshar on 3/24/2018.
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.sharshar.scratchdatatools.repository")
public class EsConfig {

	@Value("${elasticsearch.host}")
	private String esHost;

	@Value("${elasticsearch.port}")
	private int esPort;

	@Value("${elasticsearch.clustername}")
	private String esClusterName;

	@Bean
	public Client client() throws Exception {
		Settings settings = Settings.builder()
				.put("cluster.name", esClusterName)
				.put("client.transport.sniff", true)
				.build();
		TransportClient client = new PreBuiltTransportClient(settings);
		TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName(esHost), esPort);
		client.addTransportAddress(address);
		return client;
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() throws Exception {
		return new ElasticsearchTemplate(client());
	}
}
