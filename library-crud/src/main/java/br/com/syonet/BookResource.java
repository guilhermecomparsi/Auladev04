package br.com.syonet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.quarkus.panache.common.Page;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/book")
public class BookResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> list(@QueryParam("size") Integer size, 
    @QueryParam("index") Integer index, 
    @QueryParam("title") String title,
    @QueryParam("author") String author, 
    @QueryParam("year") Integer year) {
        if (index == null) index = 0;
        if (size == null) size = 10;

        String query = "from Book where 1=1";
        Map<String, Object> params = new HashMap<>();
        
        if (title != null && !title.isEmpty()) {
            query += " and title like :title";
            params.put("title", "%" + title + "%");
        }
        if (author != null && !author.isEmpty()) {
            query += " and author like :author";
            params.put("author", "%" + author + "%");
        }
        if (year != null) {
            query += " and year = :year";
            params.put("year", year);
        }


        return Book.find(query, params)
                   .page(new Page(index, size))
                   .list();
    }



    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Book create(Book book) {
        book.persist();
        return book;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Book update(@PathParam("id") Long id, Book updatedBook) {
        Optional<Book> optionalBook = Book.findByIdOptional(id);
        if (optionalBook.isEmpty()) {
            throw new WebApplicationException("Book with id " + id + " not found", 404);
        }

        Book book = optionalBook.get();
        book.title = updatedBook.title;
        book.author = updatedBook.author;
        book.year = updatedBook.year;
        book.persist();
        return book;
    }
       @DELETE
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id) {
        Optional<Book> optionalBook = Book.findByIdOptional(id);
        if (optionalBook.isEmpty()) {
            throw new WebApplicationException("Book with id " + id + " not found", 404);
        }

        Book book = optionalBook.get();
        book.delete();  // Deleta o livro encontrado
        return Response.status(204).build();  // Retorna um status 204 No Content
    }
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book getById(@PathParam("id") Long id) {
        Book book = Book.findById(id);
        if (book == null) {
            throw new WebApplicationException("Book with id " + id + " not found", 404);
        }
        return book;
    }
}



    

