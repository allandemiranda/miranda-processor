package lu.forex.system.fx.mappers;

import lu.forex.system.fx.dtos.RelativeStrengthIndexDto;
import lu.forex.system.fx.models.RelativeStrengthIndex;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface RelativeStrengthIndexMapper {

  RelativeStrengthIndex toEntity(RelativeStrengthIndexDto relativeStrengthIndexDto);

  RelativeStrengthIndexDto toDto(RelativeStrengthIndex relativeStrengthIndex);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  RelativeStrengthIndex partialUpdate(RelativeStrengthIndexDto relativeStrengthIndexDto,
      @MappingTarget RelativeStrengthIndex relativeStrengthIndex);
}