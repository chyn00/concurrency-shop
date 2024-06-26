package com.shop.concurrency.order.service;

import com.shop.concurrency.item.facade.VersionedItemFacade;
import com.shop.concurrency.item.service.ItemAtomicService;
import com.shop.concurrency.item.service.ItemService;
import com.shop.concurrency.item.service.VersionedItemService;
import com.shop.concurrency.member.model.domain.Member;
import com.shop.concurrency.member.model.dto.response.MembersResponse;
import com.shop.concurrency.member.service.MemberService;
import com.shop.concurrency.order.domain.Orders;
import com.shop.concurrency.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberService memberService;
    private final ItemService itemService;
    private final ItemAtomicService itemAtomicService;
    private final VersionedItemService versionedItemService;
    private final OrderRepository orderRepository;
    private final VersionedItemFacade versionedItemFacade;

    public synchronized boolean synchronizedOrderItemsByMember(Long itemId, Long memberId) {

        Member member = memberService.findMember(memberId);

        if(this.saveOrder(member)){
            itemService.decreaseOneItemQuantity(itemId, 1);
            return true;
        }else{
            return false;
        }
    }

    @Transactional
    public boolean transactionalOrderItemsByMember(Long itemId, Long memberId) {

        Member member = memberService.findMember(memberId);

        if(this.saveOrder(member)){
            itemService.decreaseOneItemQuantity(itemId, 1);
            return true;
        }else{
            return false;
        }
    }

    //itemService -> 직접
    @Transactional
    public boolean transactionalOrderItemsUsingAtomicByMember(Long itemId, Long memberId) {

        Member member = memberService.findMember(memberId);

        if(this.saveOrder(member)){
            itemAtomicService.decreaseOneItemAtomicQuantity(itemId);
            return true;
        }else{
            return false;
        }
    }

    @Transactional
    public boolean transactionalOrderItemsUsingPessimisticLock(Long itemId, Long memberId) {

        Member member = memberService.findMember(memberId);

        if(this.saveOrder(member)) {
            itemService.decreaseItemStockWithPessimisticLock(itemId,1);
            return true;
        } else {
            return false;
        }
    }

    public boolean transactionalOrderItemUsingOptimisticLock(Long itemId, Long memberId) throws InterruptedException {

        Member member = memberService.findMember(memberId);

        if(this.saveOrder(member)) {
            versionedItemFacade.decreaseItemStockWithOptimisticLock(itemId, 1);
            return true;
        } else {
            return false;
        }
    }

    public boolean saveOrder(Member member){
        Orders order = Orders.builder().member(member).build();
        orderRepository.saveAndFlush(order);
        return true;
    }

}
