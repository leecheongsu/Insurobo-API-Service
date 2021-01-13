package com.insrb.app.insurance;

import org.json.XML;
import org.springframework.stereotype.Component;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AddressSearch {

    /**
     *
     */
    private static final String APIS_DATA_GO_KR_SERVICE_KEY =
            "uc6vMDUub4APkNN4uC0WT3N19fZaUBPVKIfgiiJmOBXVn+Imupb6vOaFNvI5GYwDAMVdGqvGNbojGRcnH4xV2w==";
    /**
     *
     */
    private static final String JUSO_GO_KR_URL = "http://www.juso.go.kr/addrlink/addrLinkApi.do";
    private static final String CONFIRM_KEY = "U01TX0FVVEgyMDIwMDcwODE3MDExMTEwOTkzNzM=";


    /**
     * Spec : https://www.juso.go.kr/addrlink/devAddrLinkRequestGuide.do?menu=roadApi
     * 
     * @param search
     * @return JSONObject
     */
    public JSONObject getJusoList(String search) {
        HttpResponse<JsonNode> res = Unirest.post(JUSO_GO_KR_URL).field("keyword", search)
                .field("confmKey", CONFIRM_KEY).field("resultType", "json")
                .field("currentPage", "1").field("countPerPage", "1000").field("addressDong", "")
                .field("addressHo", "").asJson();

        JSONObject json = res.getBody().getObject();
        log.info(search, json.toString());
        return json;
    }


    /**
     * Spec: https://www.data.go.kr/data/15044713/openapi.do 
     * TODO: kong.unirest.json.JSONObject가 아닌
     * org.json.JSONObject라 맘에 안듬. 나중에 수정할것. 해결되면 org.json pom에서 제거할 것.
     * 
     * 
     * sigunguCd = juso.admCd 앞 5 자리 bjdongCd = juso.admCd 나머지 5자리 bun = juso.lnbrMnnm ji =
     * juso.lnbrSlno 건물명 = juso.bdNm
     * 
     * @return JSONObject
     */
    public org.json.JSONObject getHouseInfo(String sigunguCd, String bjdongCd, int bun, int ji) {
        String str_bun = String.format("%04d", bun);
        String str_ji = String.format("%04d", ji);

        HttpResponse<String> res =
                Unirest.get("http://apis.data.go.kr/1611000/BldRgstService/getBrTitleInfo")
                        .queryString("ServiceKey", APIS_DATA_GO_KR_SERVICE_KEY).queryString("sigunguCd", sigunguCd)
                        .queryString("bjdongCd", bjdongCd).queryString("bun", str_bun)
                        .queryString("ji", str_ji).queryString("numOfRows", "1000")
                        .queryString("pageNo", "1").asString();
        log.info(res.getBody());
        org.json.JSONObject jObject = XML.toJSONObject(res.getBody());

        return jObject;
    }
}
