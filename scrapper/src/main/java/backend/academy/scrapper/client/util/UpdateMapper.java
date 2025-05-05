package backend.academy.scrapper.client.util;

import backend.academy.scrapper.client.github.util.GithubDto;
import backend.academy.scrapper.client.stackoverflow.util.StackOverflowDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UpdateMapper {
    // Мы не ожидаем много разных клиентов, пока достаточно одно класса.

    @Mapping(target = "name", source = "title")
    @Mapping(target = "lastTimeUpdated", source = "lastActivityDate")
    UpdateDto toUpdateDto(StackOverflowDto.QuestionDto question);

    default UpdateDto map(StackOverflowDto dto) {
        if (dto.items() == null || dto.items().isEmpty()) return null;
        return toUpdateDto(dto.items().getFirst());
    }

    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastTimeUpdated", source = "updatedAt")
    UpdateDto map(GithubDto dto);
}
