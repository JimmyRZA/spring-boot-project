package com.banking.system.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.banking.system.dao.AccountTypeRepository;
import com.banking.system.dao.ClientAccountRepository;
import com.banking.system.dao.ClientRepository;
import com.banking.system.domain.Account_Type;
import com.banking.system.domain.AggregateAccountReportBean;
import com.banking.system.domain.Client;
import com.banking.system.domain.Client_Account;
import com.banking.system.domain.SimpleReportExporter;
import com.banking.system.domain.TransactionalAccountReportBean;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRSaver;

@Service
@Transactional
public class ReportsServiceImpl implements ReportsService {

	@Autowired
	ClientAccountRepository clientAccountRepo;

	@Autowired
	ClientRepository clientRepo;

	@Autowired
	AccountTypeRepository accountTypeRepo;

	@Autowired
	TransactionalAccountReportBean transactionalAccountReportBean;

	@Autowired
	AggregateAccountReportBean aggregateAccountReportBean;

	private final Path fileStorageLocation;

	public ReportsServiceImpl() {
		this.fileStorageLocation = Paths.get("../Docs").toAbsolutePath().normalize();
	}

	@Override
	public Resource exportAggregateAll(String type) {
		float aggregatedTotal = (float) 0.00;

		List<AggregateAccountReportBean> aggregateAccountReportList = new ArrayList<AggregateAccountReportBean>();

		try {

			List<Client_Account> accounts = clientAccountRepo.findAll().stream().collect(Collectors.toList());

			for (Client_Account account : accounts) {

				aggregateAccountReportBean = new AggregateAccountReportBean();

				Optional<Client> clientDetails = clientRepo.findById(account.getClientId());

				if (clientDetails.isPresent()) {
					aggregateAccountReportBean.setClientTitle(clientDetails.get().getClientSubTypeCode());
					aggregateAccountReportBean.setClientName(clientDetails.get().getName());
					aggregateAccountReportBean.setClientSurname(clientDetails.get().getSurname());
				}

				List<Client_Account> clientAccountList = clientAccountRepo
						.findAllById((String.valueOf(account.getClientId())));

				for (Client_Account accountByid : clientAccountList) {
					aggregatedTotal += accountByid.getDisplayBalance();
				}

				aggregateAccountReportBean.setTransactionalBalance(aggregatedTotal);
				aggregateAccountReportBean.setNetPosition(aggregatedTotal);
				aggregatedTotal = (float) 0.00;

				aggregateAccountReportList.add(aggregateAccountReportBean);

			}

			File file = ResourceUtils.getFile("classpath:aggregatereport.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRSaver.saveObject(jasperReport, "exampleTransactionReport.jasper");

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(aggregateAccountReportList);
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("reportName", "Aggregate-Accounts-Report");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			SimpleReportExporter simpleReportExporter = new SimpleReportExporter(jasperPrint);
			String fileName = "";
			switch (type) {
			case "PDF":
			case "PRINT":
				fileName = "aggregate-accounts-report.pdf";
				simpleReportExporter.exportToPdf(this.fileStorageLocation + "/" + fileName, "aggregate");
				break;
			case "XLSX":
				fileName = "aggregate-accounts-report.xlsx";
				simpleReportExporter.exportToXlsx(this.fileStorageLocation + "/" + fileName, "aggregate");
				break;
			case "CSV":
				fileName = "aggregate-accounts-report.csv";
				simpleReportExporter.exportToCsv(this.fileStorageLocation + "/" + fileName);
				break;
			default:
				break;
			}
			return loadFileAsResource(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Resource exportAll(String type) {

		List<TransactionalAccountReportBean> transactionalAccountReportList = new ArrayList<TransactionalAccountReportBean>();

		try {

			Comparator<Client_Account> comparator = Comparator.comparing(Client_Account::getDisplayBalance);

			Client_Account maximumBalanceAcc = clientAccountRepo.findAll().stream().max(comparator).get();

			transactionalAccountReportBean = new TransactionalAccountReportBean();

			transactionalAccountReportBean.setClientAccountNumber(maximumBalanceAcc.getClientAccountNumber());
			transactionalAccountReportBean.setClientId(String.valueOf(maximumBalanceAcc.getClientId()));

			Optional<Client> clientDetails = clientRepo.findById(maximumBalanceAcc.getClientId());

			if (clientDetails.isPresent()) {
				transactionalAccountReportBean.setClientSurname(clientDetails.get().getSurname());
			}

			Optional<Account_Type> accoutType = accountTypeRepo.findById(maximumBalanceAcc.getAccountTypeCode());

			if (accoutType.isPresent()) {
				transactionalAccountReportBean.setAccountDescription(accoutType.get().getDescription());
			}

			transactionalAccountReportBean
					.setDisplayBalance("R " + String.valueOf(maximumBalanceAcc.getDisplayBalance()));

			transactionalAccountReportList.add(transactionalAccountReportBean);

			List<Client_Account> otherAccounts = clientAccountRepo.findAll().stream().filter(
					acc -> !acc.getClientAccountNumber().equalsIgnoreCase(maximumBalanceAcc.getClientAccountNumber()))
					.collect(Collectors.toList());

			for (Client_Account account : otherAccounts) {
				transactionalAccountReportBean = new TransactionalAccountReportBean();

				transactionalAccountReportBean.setClientAccountNumber(account.getClientAccountNumber());
				transactionalAccountReportBean.setClientId(String.valueOf(account.getClientId()));

				clientDetails = clientRepo.findById(account.getClientId());

				if (clientDetails.isPresent()) {
					transactionalAccountReportBean.setClientSurname(clientDetails.get().getSurname());
				}

				accoutType = accountTypeRepo.findById(account.getAccountTypeCode());

				if (accoutType.isPresent()) {
					transactionalAccountReportBean.setAccountDescription(accoutType.get().getDescription());
				}

				transactionalAccountReportBean.setDisplayBalance("R " + String.valueOf(account.getDisplayBalance()));

				transactionalAccountReportList.add(transactionalAccountReportBean);

			}

			File file = ResourceUtils.getFile("classpath:example.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
			JRSaver.saveObject(jasperReport, "exampleTransactionReport.jasper");

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(transactionalAccountReportList);
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("reportName", "Trasactional-Accounts-Report");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			SimpleReportExporter simpleReportExporter = new SimpleReportExporter(jasperPrint);
			String fileName = "";
			switch (type) {
			case "PDF":
			case "PRINT":
				fileName = "transactional-accounts-report.pdf";
//			JasperExportManager.exportReportToPdfFile(jasperPrint, this.fileStorageLocation + "/example.pdf");
				simpleReportExporter.exportToPdf(this.fileStorageLocation + "/" + fileName, "trasnactional");
				break;
			case "XLSX":
				fileName = "transactional-accounts-report.xlsx";
				simpleReportExporter.exportToXlsx(this.fileStorageLocation + "/" + fileName, "trasnactional");
				break;
			case "CSV":
				fileName = "transactional-accounts-report.csv";
				simpleReportExporter.exportToCsv(this.fileStorageLocation + "/" + fileName);
				break;
			default:
				break;
			}
			return loadFileAsResource(fileName);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Resource loadFileAsResource(String fileName) throws IOException {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new FileNotFoundException("File not found " + fileName);
		}
	}

}