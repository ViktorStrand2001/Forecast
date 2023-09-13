package com.viktor.dag1.repositories;

import com.viktor.dag1.models.Forecast;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IForecastRepository extends CrudRepository<Forecast, UUID> {

    @Override
    List<Forecast> findAll();

    List<Forecast> findAllByPredictionDatum(LocalDate predictionDatum);

}
