package com.yoshio3.services;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 *
 * @author Yoshio Terada
 * @author Toshiaki Maki
 */
@Component
public class EmotionService {
	private final Logger log = LoggerFactory.getLogger(EmotionService.class);
	private final RestTemplate restTemplate;

	public EmotionService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@HystrixCommand(fallbackMethod = "getEmotionalInfoFallback")
	public EmotionAttributes getEmotionalInfo(String pictURI) {
		log.info("start get emotion => {}", pictURI);
		URI uri = UriComponentsBuilder
				.fromHttpUrl("http://EmotionService/api/emotionservice")
				.queryParam("url", pictURI).build().toUri();
		return restTemplate.getForObject(uri, EmotionAttributes.class);
	}

	public EmotionAttributes getEmotionalInfoFallback(String pictURI) {
		return new EmotionAttributes(-100.0, -100.0, -100.0, -100.0, -100.0, -100.0,
				-100.0, -100.0);
	}

	public void kill() {
		log.info("Kill Emotion API");
		URI uri = UriComponentsBuilder.fromHttpUrl("http://EmotionService/api/kill")
				.build().toUri();
		try {
			restTemplate.getForObject(uri, Void.class);
		}
		catch (RestClientException e) {
			log.info("Ignore exception => message = {}", e.getMessage());
		}
	}
}
