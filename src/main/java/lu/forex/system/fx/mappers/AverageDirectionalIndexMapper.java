package lu.forex.system.fx.mappers;

import lu.forex.system.fx.dtos.AverageDirectionalIndexDto;
import lu.forex.system.fx.models.AverageDirectionalIndex;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface AverageDirectionalIndexMapper {

  AverageDirectionalIndex toEntity(AverageDirectionalIndexDto averageDirectionalIndexDto);

  AverageDirectionalIndexDto toDto(AverageDirectionalIndex averageDirectionalIndex);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  AverageDirectionalIndex partialUpdate(AverageDirectionalIndexDto averageDirectionalIndexDto,
      @MappingTarget AverageDirectionalIndex averageDirectionalIndex);
}