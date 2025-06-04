package com.donation.ddb;

import com.donation.ddb.Domain.CampaignSortType;
import com.donation.ddb.Service.CampaignService.CampaignQueryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class DdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(DdbApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner run(ApplicationContext context) {
//		return args -> {
//			CampaignQueryService campaignService = context.getBean(CampaignQueryService.class);
//
//			// 파라미터 값 설정
//			String name = "";
//			CampaignSortType sortType = CampaignSortType.ENDING_SOON;
//			Pageable pageable = PageRequest.of(1, 5); // 페이지 번호와 크기 설정
//
//			// 쿼리 메서드 호출 및 쿼리 문자열과 파라미터 출력
//			System.out.println("Executing findStoresByNameAndScore with parameters:");
//			System.out.println("Name: " + name);
//			System.out.println("SortType: " + sortType);
//
//			campaignService.findAllCampaigns(name, sortType, pageable);
//		};
//	}
}