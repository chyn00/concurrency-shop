package com.shop.concurrency.utils.init;

import com.shop.concurrency.item.service.ItemAtomicService;
import com.shop.concurrency.item.service.ItemService;
import com.shop.concurrency.item.service.VersionedItemService;
import com.shop.concurrency.member.model.domain.Member;
import com.shop.concurrency.member.service.MemberService;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class InitialDataUtil {

    private final MemberService memberService;
    private final ItemService itemService;
    private final ItemAtomicService itemAtomicService;
    private final VersionedItemService versionedItemService;

    @PostConstruct
    public void createData() {
        Member member = Member.builder().name(Long.toString(Math.round(Math.random()*100000))).build();
        memberService.join(member);
        itemService.makeItem(1000);
        itemAtomicService.makeAtomicItem(new AtomicInteger(1000));
        versionedItemService.makeItem(1000);
        System.out.println("Member Data가 생성되었습니다.");
        System.out.println("Order Data가 생성되었습니다.");
        System.out.println("Item Data가 생성되었습니다.");
        System.out.println("AtomicItem Data가 생성되었습니다.");
        System.out.println("VersionedItem Data가 생성되었습니다.");
    }

}
