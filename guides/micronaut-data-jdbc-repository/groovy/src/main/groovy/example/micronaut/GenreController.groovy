package example.micronaut

import io.micronaut.scheduling.annotation.ExecuteOn
import javax.validation.Valid
import javax.validation.constraints.NotBlank

import example.micronaut.domain.Genre
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.Status
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpStatus
import io.micronaut.scheduling.TaskExecutors

@ExecuteOn(TaskExecutors.IO) // <1>
@Controller('/genres') // <2>
class GenreController {

    protected final GenreRepository genreRepository

    GenreController(GenreRepository genreRepository) { // <3>
        this.genreRepository = genreRepository
    }

    @Get('/{id}') // <4>
    Optional<Genre> show(Long id) {
        genreRepository
                .findById(id) // <5>
    }

    @Put // <6>
    HttpResponse update(@Body @Valid GenreUpdateCommand command) { // <7>
        genreRepository.update(command.id, command.name)
        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, location(command.id).path) // <8>
    }

    @Get(value = '/list') // <9>
    List<Genre> list(@Valid Pageable pageable) { // <10>
        genreRepository.findAll(pageable).content
    }

    @Post // <11>
    HttpResponse<Genre> save(@Body('name') @NotBlank String name) {
        Genre genre = genreRepository.save(name)

        HttpResponse.created(genre)
                .headers(headers -> headers.location(location(genre)))
    }

    @Post('/ex') // <12>
    HttpResponse<Genre> saveExceptions(@Body @NotBlank String name) {
        try {
            def genre = genreRepository.saveWithException(name)
            return HttpResponse.create(genre)
                    .headers(headers -> headers.location(location(genre)))
        } catch(ex) {
            return HttpResponse.noContent()
        }
    }

    @Delete('/{id}') // <13>
    @Status(HttpStatus.NO_CONTENT)
    void delete(Long id) {
        genreRepository.deleteById(id)
    }

    protected URI location(Long id) { URI.create("/genres/$id") }

    protected URI location(Genre genre) { location(genre.id) }

}

