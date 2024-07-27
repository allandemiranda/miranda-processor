package lu.forex.system.fx.mappers;

import lu.forex.system.fx.dtos.InitTradeDto;
import lu.forex.system.fx.dtos.TradeDto;
import lu.forex.system.fx.models.Trade;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface TradeMapper {

  TradeDto toDto(Trade trade);

  Trade toEntity(InitTradeDto initTradeDto);

  InitTradeDto toDto1(Trade trade);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Trade partialUpdate(InitTradeDto initTradeDto, @MappingTarget Trade trade);
}