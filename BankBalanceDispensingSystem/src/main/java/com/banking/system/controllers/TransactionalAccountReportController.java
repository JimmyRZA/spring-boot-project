package com.banking.system.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.banking.system.domain.TransactionalAccountReportBean;
import com.banking.system.domain.Types;
import com.banking.system.service.ReportsService;

/**
 * @author Jimmy Mhlanga
 *
 */
@RequestMapping("/reports")
@Controller
public class TransactionalAccountReportController {

	private String applicationName;
	
	@Autowired
	ReportsService  reportsService;
	
	@Autowired
	Types types;

	@Autowired
	TransactionalAccountReportBean transactionalAccountReportBean;
	
	@GetMapping("/trasactionalreport")
	public String getTransactionalReport() {
	    return "transactional-report";
	}
	
	
	@GetMapping("/aggregatereport")
	public String getAggregateReport() {
	    return "aggregate-report";
	}
	
	@GetMapping("/home")
	public String returnBacktoHomePage() {
		return "BankingHomePageLanding";
	}

	
	@PostMapping("/transactional-accounts-report")
	public ResponseEntity<Resource> displayClientsAccounts(Model model, @RequestParam String type) throws IOException {
		
		
		String contentType = "application/octet-stream";

		Resource resource = reportsService.exportAll(type.toUpperCase());

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.contentLength(resource.getFile().length())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resource.getFilename())
				.headers(createAlert(applicationName, "transactions accounts exported successfully", resource.toString()))
				.body(resource);

	}
	
	@PostMapping("/aggregate-financials-report")
	public ResponseEntity<Resource> displayAggregateFinancials(Model model, @RequestParam String type) throws IOException {
		String contentType = "application/octet-stream";;

		Resource resource = reportsService.exportAggregateAll(type.toUpperCase());

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.contentLength(resource.getFile().length())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resource.getFilename())
				.headers(createAlert(applicationName, "Aggregated accounts exported successfully", resource.toString()))
				.body(resource);

	}
	
	
	
	public HttpHeaders createAlert(String applicationName, String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-alert", message);
        headers.add("X-" + applicationName + "-params", param);
        return headers;
    }

}
