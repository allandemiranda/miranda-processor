package lu.forex.system.fx.mappers;

import lu.forex.system.fx.dtos.TickDto;
import lu.forex.system.fx.models.Tick;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface TickMapper {

  Tick toEntity(TickDto tickDto);

  TickDto toDto(Tick tick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Tick partialUpdate(TickDto tickDto, @MappingTarget Tick tick);
}