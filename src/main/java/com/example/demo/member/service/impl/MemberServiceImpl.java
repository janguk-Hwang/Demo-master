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

    @Override
    public boolean setDbFavoritesURL(String url, String username) {
        Optional<Member> optionalMember = memberRepository.findById(username);

        Member member = optionalMember.get();

        if (member.getFavorites1()==null){
            member.setFavorites1(url);
        } else if (member.getFavorites1()!=null&&member.getFavorites2()==null) {
            member.setFavorites2(url);
        } else if (member.getFavorites2()!=null&&member.getFavorites3()==null) {
            member.setFavorites3(url);
        } else if (member.getFavorites3()!=null&&member.getFavorites4()==null) {
            member.setFavorites4(url);
        } else if (member.getFavorites4()!=null&&member.getFavorites5()==null) {
            member.setFavorites5(url);
        } else if (member.getFavorites5()!=null) {
        }

        memberRepository.save(member);

        return true;
    }

    @Override
    public ArrayList<String> getDbFavriteURL(String username) {
        Optional<Member> optionalMember = memberRepository.findById(username);
        Member member = optionalMember.get();

        ArrayList<String> favoriteURL = new ArrayList<>();

        if(member.getFavorites1()!=null){
            favoriteURL.add(member.getFavorites1());
        }
        if (member.getFavorites2()!=null){
            favoriteURL.add(member.getFavorites2());
        }
        if (member.getFavorites3()!=null) {
            favoriteURL.add(member.getFavorites3());
        }
        if (member.getFavorites4()!=null) {
            favoriteURL.add(member.getFavorites4());
        }
        if (member.getFavorites5()!=null) {
            favoriteURL.add(member.getFavorites5());
        }

        return favoriteURL;
    }


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