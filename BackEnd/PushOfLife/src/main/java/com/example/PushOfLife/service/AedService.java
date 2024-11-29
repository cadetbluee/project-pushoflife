package com.example.PushOfLife.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AedService {
    @Value("${API_URL}")
    private String apiUrl;

    @Value("${SERVICE_KEY}")
    private String serviceKey;

    public void fetchAllAedData() {
        int totalCount = 52439;  // 총 데이터 개수
        int itemsPerPage = 100;  // 한 페이지에 나오는 데이터 수
        int totalPages = (int) Math.ceil((double) totalCount / itemsPerPage);  // 총 페이지 수 계산

        // DefaultUriBuilderFactory 설정
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(apiUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(apiUrl)
                .build();

        // CSV 파일 헤더 추가
        try (FileWriter csvWriter = new FileWriter("aed_data.csv", false)) {
            csvWriter.append("rnum,buildAddress,buildPlace,manager,managerTel,mfg,model,org,wgs84Lon,wgs84Lat,zipcode1,zipcode2,clerkTel,monSttTme,monEndTme,tueSttTme,tueEndTme,wedSttTme,wedEndTme,thuSttTme,thuEndTme,friSttTme,friEndTme,satSttTme,satEndTme,sunSttTme,sunEndTme,holSttTme,holEndTme,sunFrtYon,sunScdYon,sunThiYon,sunFurYon\n");
            csvWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 초기화 중 오류가 발생했습니다.", e);
        }

        // 전체 페이지 돌면서 데이터를 수집
        for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
            String url = createUrlForPage(pageNo);
            System.out.println("Requesting URL for page " + pageNo + ": " + url);

            // WebClient에서 수동으로 만든 URI를 사용하여 요청
            String responseXml = webClient.get()
                    .uri(url)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();  // 동기적으로 결과를 기다림

            System.out.println("responseXml for page " + pageNo + ": " + responseXml);

            // 각 페이지의 데이터를 필터링해서 CSV 파일로 저장
            if (responseXml != null && !responseXml.isEmpty()) {
                saveToCsv(responseXml);  // 응답 데이터를 CSV 파일에 저장
            } else {
                System.out.println("No data for page " + pageNo);
            }
        }
    }

    private String createUrlForPage(int pageNo) {
        // UriComponentsBuilder를 사용하여 쿼리 파라미터를 직접 설정
        return UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("serviceKey", serviceKey)  // 서비스 키 추가
                .queryParam("pageNo", pageNo)  // 페이지 번호 추가
                .queryParam("numOfRows", 100)  // 한 페이지에 나오는 데이터 수 설정
                .build(false)  // false로 설정하여 이중 인코딩 방지
                .toUriString();
    }

    // XML 데이터를 파싱하여 CSV 파일로 저장하는 메서드
    private void saveToCsv(String xmlData) {
        try {
            // XML 데이터를 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlData.getBytes()));

            // <item> 태그들을 추출
            NodeList items = doc.getElementsByTagName("item");

            try (FileWriter csvWriter = new FileWriter("aed_data.csv", true)) {
                // 각 <item>을 순회하며 데이터를 CSV 형식으로 저장
                for (int i = 0; i < items.getLength(); i++) {
                    String rnum = getElementValue(items.item(i), "rnum");
                    String buildAddress = getElementValue(items.item(i), "buildAddress");
                    String buildPlace = getElementValue(items.item(i), "buildPlace");
                    String manager = getElementValue(items.item(i), "manager");
                    String managerTel = getElementValue(items.item(i), "managerTel");
                    String mfg = getElementValue(items.item(i), "mfg");
                    String model = getElementValue(items.item(i), "model");
                    String org = getElementValue(items.item(i), "org");
                    String wgs84Lon = getElementValue(items.item(i), "wgs84Lon");
                    String wgs84Lat = getElementValue(items.item(i), "wgs84Lat");
                    String zipcode1 = getElementValue(items.item(i), "zipcode1");
                    String zipcode2 = getElementValue(items.item(i), "zipcode2");
                    String clerkTel = getElementValue(items.item(i), "clerkTel");
                    String monSttTme = getElementValue(items.item(i), "monSttTme");
                    String monEndTme = getElementValue(items.item(i), "monEndTme");
                    String tueSttTme = getElementValue(items.item(i), "tueSttTme");
                    String tueEndTme = getElementValue(items.item(i), "tueEndTme");
                    String wedSttTme = getElementValue(items.item(i), "wedSttTme");
                    String wedEndTme = getElementValue(items.item(i), "wedEndTme");
                    String thuSttTme = getElementValue(items.item(i), "thuSttTme");
                    String thuEndTme = getElementValue(items.item(i), "thuEndTme");
                    String friSttTme = getElementValue(items.item(i), "friSttTme");
                    String friEndTme = getElementValue(items.item(i), "friEndTme");
                    String satSttTme = getElementValue(items.item(i), "satSttTme");
                    String satEndTme = getElementValue(items.item(i), "satEndTme");
                    String sunSttTme = getElementValue(items.item(i), "sunSttTme");
                    String sunEndTme = getElementValue(items.item(i), "sunEndTme");
                    String holSttTme = getElementValue(items.item(i), "holSttTme");
                    String holEndTme = getElementValue(items.item(i), "holEndTme");
                    String sunFrtYon = getElementValue(items.item(i), "sunFrtYon");
                    String sunScdYon = getElementValue(items.item(i), "sunScdYon");
                    String sunThiYon = getElementValue(items.item(i), "sunThiYon");
                    String sunFurYon = getElementValue(items.item(i), "sunFurYon");

                    // CSV 파일에 행 추가 (각 값을 큰따옴표로 감싸기)
                    csvWriter.append("\"").append(rnum).append("\",")
                            .append("\"").append(buildAddress).append("\",")
                            .append("\"").append(buildPlace).append("\",")
                            .append("\"").append(manager).append("\",")
                            .append("\"").append(managerTel).append("\",")
                            .append("\"").append(mfg).append("\",")
                            .append("\"").append(model).append("\",")
                            .append("\"").append(org).append("\",")
                            .append("\"").append(wgs84Lon).append("\",")
                            .append("\"").append(wgs84Lat).append("\",")
                            .append("\"").append(zipcode1).append("\",")
                            .append("\"").append(zipcode2).append("\",")
                            .append("\"").append(clerkTel).append("\",")
                            .append("\"").append(monSttTme).append("\",")
                            .append("\"").append(monEndTme).append("\",")
                            .append("\"").append(tueSttTme).append("\",")
                            .append("\"").append(tueEndTme).append("\",")
                            .append("\"").append(wedSttTme).append("\",")
                            .append("\"").append(wedEndTme).append("\",")
                            .append("\"").append(thuSttTme).append("\",")
                            .append("\"").append(thuEndTme).append("\",")
                            .append("\"").append(friSttTme).append("\",")
                            .append("\"").append(friEndTme).append("\",")
                            .append("\"").append(satSttTme).append("\",")
                            .append("\"").append(satEndTme).append("\",")
                            .append("\"").append(sunSttTme).append("\",")
                            .append("\"").append(sunEndTme).append("\",")
                            .append("\"").append(holSttTme).append("\",")
                            .append("\"").append(holEndTme).append("\",")
                            .append("\"").append(sunFrtYon).append("\",")
                            .append("\"").append(sunScdYon).append("\",")
                            .append("\"").append(sunThiYon).append("\",")
                            .append("\"").append(sunFurYon).append("\"\n");
                }
                csvWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException("CSV 파일 저장 중 오류가 발생했습니다.", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("XML 파싱 중 오류가 발생했습니다.", e);
        }
    }

    // XML Element에서 값을 추출하는 유틸리티 메서드
    private String getElementValue(org.w3c.dom.Node node, String tagName) {
        org.w3c.dom.Element element = (org.w3c.dom.Element) node;
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            return list.item(0).getTextContent();
        } else {
            return "";
        }
    }
}
