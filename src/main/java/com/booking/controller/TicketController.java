package com.booking.controller;

import com.booking.entity.domain.Booking;
import com.booking.entity.domain.Ticket;
import com.booking.entity.domain.User;
import com.booking.exception.ExistingDataException;
import com.booking.exception.IncorrectDataException;
import com.booking.exception.NoSuchDataException;
import com.booking.exception.OperationExecutionException;
import com.booking.service.ticket.TicketService;
import com.booking.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public String showTicketPage(Model model) {
        model.addAttribute("tickets", ticketService.findAllTickets());
        return "ticket/tickets_list";
    }

    @GetMapping("/add")
    public String showAddTicketPage(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("arrivalDateStr", "");
        model.addAttribute("departureDateStr", "");
        return "ticket/add_ticket";
    }

    @PostMapping("/add")
    public String addNewTicket(@ModelAttribute("ticket") Ticket ticket, @ModelAttribute("arrivalDateStr") String arrivalDateStr,
                               @ModelAttribute("departureDateStr") String departureDateStr, Model model) throws ParseException {
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        ticket.setArrivalTime(new Timestamp(format2.parse(arrivalDateStr).getTime()));
        ticket.setDepartureTime(new Timestamp(format2.parse(arrivalDateStr).getTime()));
        try {
            ticketService.addNewTicket(ticket);
        } catch (IncorrectDataException | ExistingDataException e) {
            model.addAttribute("error", e.getMessage());
            return "/ticket/add_ticket";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка добавления");
            return "/ticket/add_ticket";
        }
        return "redirect:/ticket/all";
    }

    @PostMapping("/remove")
    public String removeTicket(@ModelAttribute("ticket_id") Long ticket_id, Model model) {
        try {
            ticketService.removeTicket(ticket_id);
        } catch (IncorrectDataException | ExistingDataException | NoSuchDataException | OperationExecutionException e) {
            model.addAttribute("tickets", ticketService.findAllTickets());
            model.addAttribute("error", e.getMessage());
            return "/ticket/tickets_list";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка удаления");
            model.addAttribute("tickets", ticketService.findAllTickets());
            return "/ticket/tickets_list";
        }
        return "redirect:/ticket/all";
    }

    @GetMapping("/booking/add/**")
    public String showAddBookingPage(Model model) {
        model.addAttribute("booking", new Booking());
        return "booking/booking_add";
    }

    @PostMapping("/booking/add/**")
    public String addBookingToUser(@ModelAttribute("booking") Booking booking, @RequestParam("ticket_id") Long ticketId, Model model) {
        try {
            User user = userService.find(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(() -> new NoSuchDataException("Пользователь не найден"));
            model.addAttribute("booking", ticketService.addNewBooking(booking, user, ticketId));
        } catch (IncorrectDataException | ExistingDataException | NoSuchDataException | OperationExecutionException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("booking", new Booking());
            return "/booking/booking_add";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка добавления");
            model.addAttribute("booking", new Booking());
            return "/booking/booking_add";
        }
        return "booking/booking_list";
    }

    @GetMapping("/booking/all/user")
    public String showBookingPage(Model model) {
        try {
            User user = userService.find(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(() -> new NoSuchDataException("Пользователь не найден"));
            model.addAttribute("booking", ticketService.findAllBookingByUser(user));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "/ticket/tickets_list";
        }
        return "booking/booking_list";
    }
}
