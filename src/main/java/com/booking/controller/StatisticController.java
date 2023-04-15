package com.booking.controller;

import com.booking.service.ticket.TicketService;
import com.booking.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class StatisticController {
    private TicketService ticketService;
    private UserService userService;

    @GetMapping("/statistic")
    public String getStatisticData(Model model) {
        model.addAttribute("userChartData", userService.getNumberOfUsersPerLastYear());
        model.addAttribute("ticketChartData", ticketService.getNumberOfSalesPerLastYear());
        return "statistic/statistic";
    }
}
