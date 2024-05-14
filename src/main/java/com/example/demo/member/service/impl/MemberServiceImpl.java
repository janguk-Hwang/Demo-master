package com.example.demo.member.service.impl;

import com.example.demo.admin.dto.MemberDto;
import com.example.demo.admin.mapper.MemberMapper;
import com.example.demo.admin.model.MemberParam;
import com.example.demo.components.MailComponents;
import com.example.demo.member.entity.Member;
import com.example.demo.member.exception.MemberNotEmailAuthException;
import com.example.demo.member.model.MemberInput;
import com.example.demo.member.model.ResetPasswordInput;
import com.example.demo.member.repository.MemberRepository;
import com.example.demo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.DemoApplication.post;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;
    private final MemberMapper memberMapper;

    @Override
    public boolean register(MemberInput parameter) {
        Optional<Member> optionalMember = memberRepository.findById(parameter.getUserId());
        if(optionalMember.isPresent()) {
            //해당 Id에 데이터 존재
            return false;
        }

        String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());

        String uuid = UUID.randomUUID().toString();

        Member member = Member.builder()
                .userId(parameter.getUserId())
                .userName(parameter.getUserName())
                .phone(parameter.getPhone())
                .password(encPassword)
                .regDt(LocalDateTime.now())
                .emailAuthYn(false)
                .emailAuthKey(uuid)
                .build();

        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "demo 사이트 가입을 축하드립니다.";
        String text = "<p> demo 사이트 가입을 축하드립니다.</p> <p>아래 링크를 클릭하셔서 가입을 완료하세요.</p>"
                + "<div><a target='_blank' href='http://localhost:8080/member/email-auth?id=" + uuid + "'> 가입완료 </a></div>";

        mailComponents.sendMail(email, subject, text);

        return true;
    }

    @Override
    public boolean emailAuth(String uuid) {

        Optional<Member> optionalMember = memberRepository.findByEmailAuthKey(uuid);//null이 가능한 member안의 인스턴스를 optionalMember객체에연결하고 memberrepository에서 emailauthkey를 찾아서연결
        if (!optionalMember.isPresent()) {
            return false;
        }

        Member member = optionalMember.get();

        if(member.isEmailAuthYn()) {
            return false;
        }

        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean sendResetPassword(ResetPasswordInput parameter) {

        Optional<Member> optionalMember = memberRepository.findByUserIdAndUserName(parameter.getUserId(), parameter.getUserName());
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지않습니다.");
        }

        Member member = optionalMember.get();

        String uuid = UUID.randomUUID().toString();

        member.setResetPasswordKey(uuid);
        member.setResetPasswordLimitDt(LocalDateTime.now().plusDays(1));
        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "[demo] 비밀번호 초기화 메일 입니다.";
        String text = "<p> demo 비밀번호 초기화 메일입니다.</p> <p>아래 링크를 클릭하셔서 비밀번호를 초기화 해주세요.</p>" + "http://localhost:8080/member/reset/password?id=" + uuid
                + "<div><a target='_blank' href='http://localhost:8080/member/reset/password?id=" + uuid + "'> 비밀번호 초기화 링크 </a></div>";
        mailComponents.sendMail(email, subject, text);

        return true;
    }

    @Override
    public boolean resetPassword(String uuid, String password) {
        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지않습니다.");
        }

        Member member = optionalMember.get();

        //초기화 날짜가 유효한지 체크
        if(member.getResetPasswordLimitDt() == null) {
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        if(member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        member.setPassword(encPassword);
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean checkResetPassword(String uuid) {

        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);
        if (!optionalMember.isPresent()) {
            return false;
        }

        Member member = optionalMember.get();

        //초기화 날짜가 유효한지 체크
        if(member.getResetPasswordLimitDt() == null) {
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        if(member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        return true;
    }



    @Override
    public List<MemberDto> list(MemberParam parameter) {

        long totalCount = memberMapper.selectListCount(parameter);

        List<MemberDto> list = memberMapper.selectList(parameter);//memberMapper에 selectList메소드에 MemberParam의 parameter을 넣어서 MemberDto타입(MemberDto안의 모든 인스턴스)의 리스트인 list 객체에 넣는다
        if (!CollectionUtils.isEmpty(list)) {
            int i = 0;
            for(MemberDto x : list) {
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i);
                i++;
            }
        }

        return list;
    }

    @Override
    public MemberDto detail(String userId) {
        Optional<Member> member = memberRepository.findById(userId);

        return null;
    }

    @Override
    public List<String> apiResponseX(String query, String year, String month1, String day1, String year2, String month2, String day2, String timeunit, String coverage, String gender, String[] age) {
        String clientId = "yeRsNjkDl0PmHo3i09r1"; // 애플리케이션 클라이언트 아이디
        String clientSecret = "iqRygcj9AF"; // 애플리케이션 클라이언트 시크릿

        String apiUrl = "https://openapi.naver.com/v1/datalab/search";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        requestHeaders.put("Content-Type", "application/json");

        String agesString = "";
        if (age != null) {
            agesString = "[" + Arrays.stream(age)
                    .map(a -> "\"" + a + "\"")
                    .collect(Collectors.joining(",")) + "]";
        }

        String requestBody = "{\"startDate\":\"" + year + "-" + month1 + "-" + day1 + "\"," +
                "\"endDate\":\"" + year2 + "-" + month2 + "-" + day2 + "\"," +
                "\"timeUnit\":\"" +  timeunit + "\"," +
                "\"keywordGroups\":" + (query.isEmpty() ? "[]" : "[{\"groupName\":\"" + query + "\"," + "\"keywords\":[\"" + query + "\"]}]") + "," +
                "\"device\":\"" + coverage + "\"," +
                "\"ages\":" + agesString + "," +
                "\"gender\":\"" + gender + "\"}";

        String responseBody = post(apiUrl, requestHeaders, requestBody);

        List<String> xAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            if (jsonObject.has("results")) {
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject resultObject = resultsArray.getJSONObject(i);
                    JSONArray dataArray = resultObject.getJSONArray("data");
                    for (int j = 0; j < dataArray.length(); j++) {
                        JSONObject dataObject = dataArray.getJSONObject(j);
                        String period = dataObject.getString("period");
                        String ratio = dataObject.getString("ratio");
                        xAxisData.add(period);
                        seriesData.add(ratio);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return xAxisData;
    }

    @Override
    public List<String> apiResponseY(String query, String year, String month1, String day1, String year2, String month2, String day2, String timeunit, String coverage, String gender, String[] age) {
        String clientId = "yeRsNjkDl0PmHo3i09r1"; // 애플리케이션 클라이언트 아이디
        String clientSecret = "iqRygcj9AF"; // 애플리케이션 클라이언트 시크릿

        String apiUrl = "https://openapi.naver.com/v1/datalab/search";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        requestHeaders.put("Content-Type", "application/json");

        String agesString = "";
        if (age != null) {
            agesString = "[" + Arrays.stream(age)
                    .map(a -> "\"" + a + "\"")
                    .collect(Collectors.joining(",")) + "]";
        }

        String requestBody = "{\"startDate\":\"" + year + "-" + month1 + "-" + day1 + "\"," +
                "\"endDate\":\"" + year2 + "-" + month2 + "-" + day2 + "\"," +
                "\"timeUnit\":\"" +  timeunit + "\"," +
                "\"keywordGroups\":" + (query.isEmpty() ? "[]" : "[{\"groupName\":\"" + query + "\"," + "\"keywords\":[\"" + query + "\"]}]") + "," +
                "\"device\":\"" + coverage + "\"," +
                "\"ages\":" + agesString + "," +
                "\"gender\":\"" + gender + "\"}";

        String responseBody = post(apiUrl, requestHeaders, requestBody);

        List<String> xAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            if (jsonObject.has("results")) {
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject resultObject = resultsArray.getJSONObject(i);
                    JSONArray dataArray = resultObject.getJSONArray("data");
                    for (int j = 0; j < dataArray.length(); j++) {
                        JSONObject dataObject = dataArray.getJSONObject(j);
                        String period = dataObject.getString("period");
                        String ratio = dataObject.getString("ratio");
                        xAxisData.add(period);
                        seriesData.add(ratio);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return seriesData;
    }

//    @Override
//    public boolean setDbFavoritesURL(String url, String username) {
//        Optional<Member> optionalMember = memberRepository.findById(username);
//
//        Member member = optionalMember.get();
//
//
//        if (member.getFavorites1()==null){
//            member.setFavorites1(url);
//        } else if (member.getFavorites1()!=null&&member.getFavorites2()==null) {
//            member.setFavorites2(url);
//        } else if (member.getFavorites2()!=null&&member.getFavorites3()==null) {
//            member.setFavorites3(url);
//        } else if (member.getFavorites3()!=null&&member.getFavorites4()==null) {
//            member.setFavorites4(url);
//        } else if (member.getFavorites4()!=null&&member.getFavorites5()==null) {
//            member.setFavorites5(url);
//        } else if (member.getFavorites5()!=null) {
//            return false;
//        }
//
//        memberRepository.save(member);
//
//        return true;
//    }

//    @Override
//    public ArrayList<String> getDbFavriteURL(String username) {
//        Optional<Member> optionalMember = memberRepository.findById(username);
//        Member member = optionalMember.get();
//
//        ArrayList<String> favoriteURL = new ArrayList<>();
//
//        if(member.getFavorites1()!=null){
//            favoriteURL.add(member.getFavorites1());
//        }
//        if (member.getFavorites2()!=null){
//            favoriteURL.add(member.getFavorites2());
//        }
//        if (member.getFavorites3()!=null) {
//            favoriteURL.add(member.getFavorites3());
//        }
//        if (member.getFavorites4()!=null) {
//            favoriteURL.add(member.getFavorites4());
//        }
//        if (member.getFavorites5()!=null) {
//            favoriteURL.add(member.getFavorites5());
//        }
//
//        return favoriteURL;
//    }

//    @Override
//    public String[] extractUrl(String str) {
//        String[] arr = new String[11];
//
//        String keyword1 = "query1=";
//        String keyword2 = "&query2=";
//        String keyword3 = "&query3=";
//        String keyword4 = "&query4=";
//        String keyword5 = "&query5=";
//        String keyword6 = "&year=";
//        String keyword7 = "&month=";
//        String keyword8 = "&day=";
//        String keyword9 = "&year2=";
//        String keyword10 = "&month2=";
//        String keyword11 = "&day2=";
//        String keyword12 = "&select_day_week_month=";
//
//        String regexPattern1 = keyword1 + "(.*?)" + keyword2;
//        String regexPattern2 = keyword2 + "(.*?)" + keyword3;
//        String regexPattern3 = keyword3 + "(.*?)" + keyword4;
//        String regexPattern4 = keyword4 + "(.*?)" + keyword5;
//        String regexPattern5 = keyword5 + "(.*?)" + keyword6;
//        String regexPattern6 = keyword6 + "(.*?)" + keyword7;
//        String regexPattern7 = keyword7 + "(.*?)" + keyword8;
//        String regexPattern8 = keyword8 + "(.*?)" + keyword9;
//        String regexPattern9 = keyword9 + "(.*?)" + keyword10;
//        String regexPattern10 = keyword10 + "(.*?)" + keyword11;
//        String regexPattern11 = keyword11 + "(.*?)" + keyword12;
//
//
//        Pattern pattern1 = Pattern.compile(regexPattern1);
//        Pattern pattern2 = Pattern.compile(regexPattern2);
//        Pattern pattern3 = Pattern.compile(regexPattern3);
//        Pattern pattern4 = Pattern.compile(regexPattern4);
//        Pattern pattern5 = Pattern.compile(regexPattern5);
//        Pattern pattern6 = Pattern.compile(regexPattern6);
//        Pattern pattern7 = Pattern.compile(regexPattern7);
//        Pattern pattern8 = Pattern.compile(regexPattern8);
//        Pattern pattern9 = Pattern.compile(regexPattern9);
//        Pattern pattern10 = Pattern.compile(regexPattern10);
//        Pattern pattern11 = Pattern.compile(regexPattern11);
//
//        Matcher matcher1 = pattern1.matcher(str);
//        Matcher matcher2 = pattern2.matcher(str);
//        Matcher matcher3 = pattern3.matcher(str);
//        Matcher matcher4 = pattern4.matcher(str);
//        Matcher matcher5 = pattern5.matcher(str);
//        Matcher matcher6 = pattern6.matcher(str);
//        Matcher matcher7 = pattern7.matcher(str);
//        Matcher matcher8 = pattern8.matcher(str);
//        Matcher matcher9 = pattern9.matcher(str);
//        Matcher matcher10 = pattern10.matcher(str);
//        Matcher matcher11 = pattern11.matcher(str);
//
//        while (matcher1.find()) {
//            String extractedContent = matcher1.group(1);
//            arr[0] = extractedContent;
//        }
//        while (matcher2.find()) {
//            String extractedContent = matcher2.group(1);
//            arr[1] = extractedContent;
//        }
//        while (matcher3.find()) {
//            String extractedContent = matcher3.group(1);
//            arr[2] = extractedContent;
//        }
//        while (matcher4.find()) {
//            String extractedContent = matcher4.group(1);
//            arr[3] = extractedContent;
//        }
//        while (matcher5.find()) {
//            String extractedContent = matcher5.group(1);
//            arr[4] = extractedContent;
//        }
//        while (matcher6.find()) {
//            String extractedContent = matcher6.group(1);
//            arr[5] = extractedContent;
//        }
//        while (matcher7.find()) {
//            String extractedContent = matcher7.group(1);
//            arr[6] = extractedContent;
//        }
//        while (matcher8.find()) {
//            String extractedContent = matcher8.group(1);
//            arr[7] = extractedContent;
//        }
//        while (matcher9.find()) {
//            String extractedContent = matcher9.group(1);
//            arr[8] = extractedContent;
//        }
//        while (matcher10.find()) {
//            String extractedContent = matcher10.group(1);
//            arr[9] = extractedContent;
//        }
//        while (matcher11.find()) {
//            String extractedContent = matcher11.group(1);
//            arr[10] = extractedContent;
//        }
//
//        return arr;
//    }

//    @Override
//    public boolean setRemoveUrl(String username) {
//        Optional<Member> optionalMember = memberRepository.findById(username);
//
//        Member member = optionalMember.get();
//
//        member.setFavorites1(null);
//        member.setFavorites2(null);
//        member.setFavorites3(null);
//        member.setFavorites4(null);
//        member.setFavorites5(null);
//
//        memberRepository.save(member);
//
//        return true;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Member> optionalMember = memberRepository.findById(username);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지않습니다.");
        }

        Member member = optionalMember.get();

        if(!member.isEmailAuthYn()) { // 메일 활성화 안되었을 때 오류 처리
            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인해주세요.");
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (member.isAdminYn()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new User(member.getUserId(), member.getPassword(), grantedAuthorities);
    }
}