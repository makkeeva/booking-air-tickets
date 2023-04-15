package com.booking.service.ticket;

import com.booking.entity.ChartData;
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
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {
    private TicketRepository ticketRepository;
    private BookingRepository bookingRepository;
    private TicketValidator ticketValidator;
    private BookingValidator bookingValidator;
    public JavaMailSender emailSender;

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
    public ChartData getNumberOfSalesPerLastYear() {
        List<Booking> bookings = bookingRepository.findAll();

        LocalDateTime localDateTime = LocalDateTime.now();
        bookings = bookings.stream().filter(booking ->
                        booking.getTicket().getDepartureTime().after(Timestamp.valueOf(localDateTime.minusYears(1))))
                .collect(Collectors.toList());

        int[] array = new int[12];
        String[] axiosX = new String[12];
        for (int i = 0; i < 12; i++) {
            final int iterator = i;
            array[i] = (int) bookings.stream().filter(booking ->
                    booking.getTicket().getDepartureTime()
                            .after(Timestamp.valueOf(localDateTime.minusYears(1).plusMonths(iterator))) &&
                            booking.getTicket().getDepartureTime()
                                    .before(Timestamp.valueOf(localDateTime.minusYears(1).plusMonths(iterator + 1)))

            ).count();
            axiosX[i] = localDateTime.minusYears(1).plusMonths(iterator).getMonth()
                    .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
        }

        return ChartData.builder().data(array).axisX(axiosX).build();
    }

    @Override
    public void sendEmailBooking(Long bookingId, String email) throws MessagingException {
        this.renderBookingPdf(bookingId);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(email);
        messageHelper.setSubject("Броинрование билета");
        messageHelper.setText("Вы успешно забронировали билет");
        messageHelper.addAttachment("booking.pdf", new File("booking.pdf"));
        emailSender.send(mimeMessage);
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
