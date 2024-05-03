package com.example.demo.admin.model;

import lombok.Data;

@Data
public class MemberParam {

    long pageIndex;//페이지번호
    long pageSize;//총페이지수

    String searchType;//찾으려는 타입
    String searchValue;//찾으려는 내용

    String userId;

    public long getPageStart() {
        init();

        return (pageIndex - 1) * pageSize;
    }

    public long getPageEnd() {
        init();
        return pageSize;
    }

    public void init() {
        if (pageIndex < 1) {
            pageIndex = 1;//페이지번호 1로 초기화
        }

        if(pageSize < 1) {
            pageSize = 10;
        }
    }


    public String getQueryString() {
        init();

        StringBuilder sb = new StringBuilder();

        if (searchType != null && searchType.length() > 0) {
            sb.append(String.format("searchType=%s", searchType));
        }

        if (searchValue != null && searchValue.length() > 0) {
            if(sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("searchValue=%s", searchValue));
        }

        return sb.toString();
    }
}
