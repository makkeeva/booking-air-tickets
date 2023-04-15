package com.booking.service.ticket;

import com.booking.entity.domain.Booking;
import com.booking.entity.domain.Ticket;
import com.booking.entity.domain.User;
import com.booking.exception.NoSuchDataException;
import com.booking.exception.OperationExecutionException;
import com.booking.repository.BookingRepository;
import com.booking.repository.TicketRepository;
import com.booking.utils.ValidationUtil;
import com.booking.validation.BookingValidator;
import com.booking.validation.TicketValidator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TicketValidator ticketValidator;

    @Autowired
    private BookingValidator bookingValidator;

    @Override
    public boolean removeTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchDataException("Данного билета не существует"));

        bookingRepository.findAllByTicket(ticket).forEach(booking -> bookingRepository.delete(booking));
        if (ticketRepository.removeById(ticket.getId()) < 1)
            throw new OperationExecutionException("Билет не удалён");
        return true;
    }

    @Override
    public Booking addNewBooking(Booking booking, User user, Long ticket_id) {
        ValidationUtil.validate(booking, bookingValidator);
        Ticket ticket = ticketRepository.findById(ticket_id)
                .orElseThrow(() -> new NoSuchDataException("Данного билета не существует"));
        booking.setUser(user);
        booking.setTicket(ticket);
        return bookingRepository.save(booking);
    }

    @Override
    public void renderBookingPdf(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchDataException("Данной брони не существует"));

        try {
            File file = new File("booking.pdf");
            PDDocument doc = PDDocument.load(file);

            PDFont pdfFont = PDType0Font.load(doc, new File("times.ttf"));

            if (doc.getNumberOfPages() == 1)
                doc.removePage(0);
            doc.addPage(new PDPage());

            PDPageContentStream contents = new PDPageContentStream(doc, doc.getPage(0), PDPageContentStream.AppendMode.APPEND, true, true);
            contents.setFont(pdfFont, 14);
            formatPdfOutput(contents, booking);
            contents.close();

            doc.save("booking.pdf");
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void formatPdfOutput(PDPageContentStream contents, Booking booking) throws IOException {
        contents.beginText();

        contents.newLineAtOffset(25, 775);
        contents.showText("Бронирование");
        contents.newLineAtOffset(0, -15);
        contents.showText("ID: " + booking.getId());
        contents.newLineAtOffset(0, -15);
        contents.showText("Пользователь: " + booking.getUser().getUsername());
        contents.newLineAtOffset(0, -15);
        contents.showText("Билет: ");
        contents.newLineAtOffset(0, -15);
        contents.showText("    ID: " + booking.getTicket().getId());
        contents.newLineAtOffset(0, -15);
        contents.showText("    Место отправления: " + booking.getTicket().getDeparturePoint());
        contents.newLineAtOffset(0, -15);
        contents.showText("    Место прибытия: " + booking.getTicket().getArrivalPoint());
        contents.newLineAtOffset(0, -15);
        contents.showText("    Цена: " + booking.getTicket().getCost());
        contents.newLineAtOffset(0, -15);
        contents.showText("    Перевозчик: " + booking.getTicket().getAirline());
        contents.newLineAtOffset(0, -15);
        contents.showText("    Время прибытия: " + booking.getTicket().getArrivalTime());
        contents.newLineAtOffset(0, -15);
        contents.showText("Имя: " + booking.getName() + " " + booking.getSurname());
        contents.newLineAtOffset(0, -15);
        contents.showText("Пасспорт ID: " + booking.getPassport());
        contents.newLineAtOffset(0, -15);
        contents.showText("Адрес: " + booking.getAddress());

        contents.endText();
    }

    @Override
    public List<Booking> findAllBooking() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> findAllBookingByUser(User user) {
        return bookingRepository.findAllByUser(user);
    }

    @Override
    public Ticket addNewTicket(Ticket ticket) {
        ValidationUtil.validate(ticket, ticketValidator);
        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }
}
