package org.acme.resource;

import org.acme.model.Book;
import org.acme.model.Loan;
import org.acme.service.BookService;
import org.acme.service.LoanService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    private BookService bookService;

    @Inject
    private LoanService loanService;

    @GET
    public Response getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return Response.ok(books).build();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") Long id) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            return Response.ok(book.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Book not found\"}")
                    .build();
        }
    }

    @POST
    @Path("/{id}/borrow")
    public Response borrowBook(@PathParam("id") Long bookId, @QueryParam("userId") Long userId) {
        try {
            if (userId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"User ID is required\"}")
                        .build();
            }
            Loan loan = loanService.borrowBook(bookId, userId);
            return Response.ok(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}/return")
    public Response returnBook(@PathParam("id") Long bookId) {
        try {
            // Find the active loan for this book
            List<Loan> activeLoans = loanService.getActiveLoansByBookId(bookId);

            if (activeLoans.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"No active loan found for this book\"}")
                        .build();
            }

            // Return the first active loan (there should only be one)
            Loan returnedLoan = loanService.returnBook(activeLoans.get(0).getId());
            return Response.ok(returnedLoan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/{id}/reserve")
    public Response reserveBook(@PathParam("id") Long bookId, @QueryParam("userId") Long userId) {
        try {
            // This should be handled by ReservationService, but we'll create a simple
            // implementation
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity("{\"error\": \"Reservation functionality not yet implemented\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Transactional
    public Response createBook(Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            return Response.status(Response.Status.CREATED)
                    .entity(createdBook)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateBook(@PathParam("id") Long id, Book book) {
        try {
            Optional<Book> existingBook = bookService.getBookById(id);
            if (existingBook.isPresent()) {
                book.setId(id);
                Book updatedBook = bookService.updateBook(book); // Use updateBook instead of createBook
                return Response.ok(updatedBook).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Book not found\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteBook(@PathParam("id") Long id) {
        try {
            Optional<Book> existingBook = bookService.getBookById(id);
            if (existingBook.isPresent()) {
                bookService.deleteBook(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Book not found\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}/status")
    @Transactional
    public Response updateBookStatus(@PathParam("id") Long id, @QueryParam("status") String status) {
        try {
            Book updatedBook = bookService.updateBookStatus(id, status);
            if (updatedBook != null) {
                return Response.ok(updatedBook).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Book not found\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}