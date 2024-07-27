package lu.forex.system.fx.mappers;

import lu.forex.system.fx.dtos.TickDto;
import lu.forex.system.fx.models.Tick;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface TickMapper {

  Tick toEntity(TickDto tickDto);

  TickDto toDto(Tick tick);
}