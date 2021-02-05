package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/")
@RequiredArgsConstructor
public class BeerOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    @PreAuthorize(
            "hasAuthority('order.read') OR " +
                    "hasAuthority('customer.order.read') AND " +
                    "@beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId)"
    )
    @GetMapping("orders")
    public BeerOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        pageNumber = (pageNumber == null || pageNumber < 0) ? (DEFAULT_PAGE_NUMBER) : (pageNumber);

        pageSize = (pageSize == null || pageSize < 1) ? (DEFAULT_PAGE_SIZE) : (pageSize);

        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PreAuthorize(
            "hasAuthority('order.create') OR " +
                    "hasAuthority('customer.order.create') AND " +
                    "@beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId)"
    )
    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody BeerOrderDto beerOrderDto) {
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @PreAuthorize(
            "hasAuthority('order.read') OR " +
                    "hasAuthority('customer.order.read') AND " +
                    "@beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId)"
    )
    @GetMapping("orders/{orderId}")
    public BeerOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        beerOrderService.pickupOrder(customerId, orderId);
    }
}
