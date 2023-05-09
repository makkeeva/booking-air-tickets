package com.booking;

import com.booking.configuration.SecurityConfiguration;
import com.booking.controller.TicketController;
import com.booking.entity.domain.Ticket;
import com.booking.service.ticket.TicketServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TicketController.class)
@Import({ControllerTestConfig.class, SecurityConfiguration.class})
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TicketServiceImpl ticketService;

    @Test
    public void returnTicketListPage() throws Exception {
        mockMvc.perform(get("/ticket/all").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/tickets_list"));
    }

    @Test
    public void returnAddTicketPage() throws Exception {
        mockMvc.perform(get("/ticket/add").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/add_ticket"));
    }

    @Test
    public void returnTicketModelObject_WhenGetAddTicketPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/ticket/add").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/add_ticket"))
                .andReturn();

        ModelAndView mav = result.getModelAndView();
        assertTrue(mav.getModel().get("ticket") != null);
    }

    @Test
    public void returnRedirectToTocketAll_WhenPostRemoveTicketNotFoundId() throws Exception {
        mockMvc.perform(post("/ticket/remove")
                        .with(user("user").password("password"))
                .param("ticket_id", String.valueOf(1L)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ticket/all"));
    }

    @Test
    public void returnArrivalDateStrModel_WhenGetAddTicketPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/ticket/add").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/add_ticket"))
                .andReturn();

        ModelAndView mav = result.getModelAndView();
        assertTrue(mav.getModel().get("arrivalDateStr") != null);
    }

    @Test
    public void returnDepartureDateStrModel_WhenGetAddTicketPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/ticket/add").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/add_ticket"))
                .andReturn();

        ModelAndView mav = result.getModelAndView();
        assertTrue(mav.getModel().get("departureDateStr") != null);
    }

    @Test
    public void returnTicketListPage_WhenAddTicketFormIsEmpty() throws Exception {
        mockMvc.perform(get("/ticket/add").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/add_ticket"));
    }

    @Test
    public void returnTicketListInModel_WhenGetAllTicketsPage() throws Exception {
        List<Ticket> ticketList = Arrays.asList(Ticket.builder().departurePoint("Минск").build());
        when(ticketService.findAllTickets()).thenReturn(ticketList);

        MvcResult result = mockMvc.perform(get("/ticket/all").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/tickets_list"))
                .andReturn();

        ModelAndView mav = result.getModelAndView();
        assertTrue(mav.getModel().get("tickets") != null);
    }

    @Test
    public void returnEmptyTicketListInModel_WhenGetAllTicketsPage() throws Exception {
        List<Ticket> ticketList = Arrays.asList(Ticket.builder().departurePoint("Минск").build());
        when(ticketService.findAllTickets()).thenReturn(ticketList);

        MvcResult result = mockMvc.perform(get("/ticket/all").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/tickets_list"))
                .andReturn();

        ModelAndView mav = result.getModelAndView();
        assertTrue(mav.getModel().get("tickets") != null);
        assertTrue(mav.getModel().get("tickets").equals(new ArrayList<>()));
    }

    @Test
    public void returnAllBookingListPage() throws Exception {
        mockMvc.perform(get("/ticket/booking/all").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/booking_list"));
    }

    @Test
    public void returnBookingAddPage() throws Exception {
        mockMvc.perform(get("/ticket/booking/add").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/booking_add"));
    }

    @Test
    public void returnBookingModelObject_WhenGetBookingAddPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/ticket/booking/add").with(user("user").password("password")))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = result.getModelAndView();
        assertTrue(mav.getModel().get("booking") != null);
    }
}
