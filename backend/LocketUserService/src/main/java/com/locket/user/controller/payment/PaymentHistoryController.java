package com.locket.user.controller.payment;

import com.locket.user.security.RequiresUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/history")
@RequiredArgsConstructor
@RequiresUser(ownerOnly = true)
public class PaymentHistoryController {

}
