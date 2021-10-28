package org.zerock.guestbook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.web.WebAppConfiguration;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;

import java.util.Optional;
import java.util.stream.IntStream;

@WebAppConfiguration
@SpringBootTest
public class GuestbookRepositoryTests {

    @Autowired // 필요한 의존 객체의 “타입"에 해당하는 빈을 찾아 주입
    private GuestbookRepository guestbookRepository;

    @Test
    public void insertDummies() {
        IntStream.rangeClosed(1, 300).forEach(i -> {
            Guestbook guestbook = Guestbook.builder()
                    .title("Title...." + i)
                    .content("Content...." + i)
                    .writer("user" + (i % 10))
                    .build();
            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    @Test
    public void updateTest() {
        Optional<Guestbook> result = guestbookRepository.findById(297L);

        if(result.isPresent()) {
            Guestbook guestbook = result.get();

            guestbook.changeTitle("Changed Title...");
            guestbook.changeContent("Changed Content...");

            guestbookRepository.save(guestbook);
        }
    }

    @Test
    public void testQuery1() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        // 1) Q도메인 클래스를 가져온다.
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = "1";
        // 2) where문에 들어가는 조건들을 넣어주는 컨테이너 객체 생성
        BooleanBuilder builder = new BooleanBuilder();
        // 3) 필드 값과 같이 결합해서 생성
        BooleanExpression expression = qGuestbook.title.contains(keyword);
        // 4) 만들어진 조건을 where문에 and나 or 같은 키워드와 결합
        builder.and(expression);
        // 5) guestbookRepository에 추가된 QuerydslPredicateExcutor 인터페이스의 findAll() 사용
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }

    @Test
    public void testQuery2() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = "1";

        BooleanBuilder builder = new BooleanBuilder();
        // exTitle like %1%
        BooleanExpression exTitle = qGuestbook.title.contains(keyword);
        // exContent like %1%
        BooleanExpression exContent = qGuestbook.title.contains(keyword);
        // 1) exTitle like %1% or exContent like %1%
        BooleanExpression exAll = exTitle.or(exContent);
        // 2) BooleanBuilder에 추가
        builder.and(exAll);
        // 3) gno가 0보다 크다, 조건 추가
        builder.and(qGuestbook.gno.gt(0L));

        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }
}
