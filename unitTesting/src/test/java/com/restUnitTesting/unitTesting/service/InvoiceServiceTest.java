package com.restUnitTesting.unitTesting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restUnitTesting.unitTesting.model.InvoiceQuery.InvoiceResponse;
import com.restUnitTesting.unitTesting.service.imp.InvoiceServiceImp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResourceAccessException;

import java.io.FileInputStream;
import java.nio.charset.Charset;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(InvoiceServiceImp.class)
@PropertySource("classpath:application-test.properties")
public class InvoiceServiceTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceServiceImp mockService;

    private static final ObjectMapper mapper = new ObjectMapper();

    private InvoiceResponse responseInvoiceResponseEntity;

    @Value("${invoiceInfo.path}")
    private String invoiceUrl;

    @Test
    public void testWhenPhoneNumEnteredAndDataExists_thenReturnHttp200() throws Exception {

        String mockPhoneNum = "534*******";
        final FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:response_http200.json"));
        final String staticResponse = StreamUtils.copyToString(fileInputStream, Charset.defaultCharset());

        InvoiceResponse mockInvoiceResponse = mapper.readValue(staticResponse, InvoiceResponse.class);

        this.mockServer.expect(ExpectedCount.once(), requestTo(invoiceUrl
                + mockPhoneNum))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(mockInvoiceResponse)));

        responseInvoiceResponseEntity = mockService.getInvoiceInfo(mockPhoneNum);
        mockServer.verify();

        Assert.assertEquals("200",
                responseInvoiceResponseEntity.
                        getStatusCode());

    }

    @Test
    public void testWhenPhoneNumEnteredAndNoDataFound_thenReturnHttp204() throws Exception {

        String mockPhoneNum = "542*******";
        final FileInputStream fileInputStream = new FileInputStream(ResourceUtils.getFile("classpath:response_http204.json"));
        final String staticResponse = StreamUtils.copyToString(fileInputStream, Charset.defaultCharset());

        InvoiceResponse mockInvoiceResponse = mapper.readValue(staticResponse, InvoiceResponse.class);

        this.mockServer.expect(ExpectedCount.once(), requestTo(invoiceUrl
                + mockPhoneNum))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(mockInvoiceResponse)));

        responseInvoiceResponseEntity = mockService.getInvoiceInfo(mockPhoneNum);
        mockServer.verify();

        Assert.assertEquals("204", responseInvoiceResponseEntity.
                getStatusCode());
    }


    @Test
    public void testWhenInternalServerError_thenReturnHttp500() throws Exception {

        String mockPhoneNum = "542*******";

        this.mockServer.expect(ExpectedCount.once(), requestTo(invoiceUrl
                + mockPhoneNum))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond((response) -> { throw new RuntimeException(); });

        responseInvoiceResponseEntity = mockService.getInvoiceInfo(mockPhoneNum);
        mockServer.verify();

        Assert.assertEquals("500", responseInvoiceResponseEntity.
                getStatusCode());
    }

}
